/*
 * Copyright (C) 2025 Tony Luken <tonyluken62+gerberfilereader.gmail.com>
 * 
 * This file is part of GerberFileReader.
 * 
 * GerberFileReader is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of 
 * the License, or (at your option) any later version.
 * 
 * GerberFileReader is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with GerberFileReader. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package gerberFileReader;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A class for tokenizing Gerber files into Gerber commands
 */
class GerberTokenizer extends StreamTokenizer implements AutoCloseable {

    private int firstLineno = 1;
    private long bytesProcessed = 0;
    private boolean starting = true;
    private int eol;
    private int lineCount = 1;
    private boolean tokenStarting = true;
    private Reader reader;
    private int lastChar = '*';
    private String md5String = "";
    private boolean md5InProgress = true;
    private MessageDigest messageDigest;
    private boolean attributeValue = false;

    /**
     * Constructs a tokenizer for Gerber files
     * 
     * @param reader the reader of the Gerber file
     */
    public GerberTokenizer(Reader reader) {
        super(reader);
        this.reader = reader;
        resetSyntax();
        wordChars(0, 0x10FFFF); // makes all UTF-8 characters part of a word except for those below
        ordinaryChar('%');
        ordinaryChar('*');
        ordinaryChar(',');
        ordinaryChar('\r');
        ordinaryChar('\n');
        lowerCaseMode(false);
        eolIsSignificant(false);
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            // This really should never happen but we catch it here just in case
            e.printStackTrace();
        }
        md5InProgress = messageDigest != null;
    }

    /**
     * Gets the next token from the Gerber file.
     *  
     * @return either StreamTokenizer.TT_EOF if the end of the file has been reached, the character
     *         value of '%' if a percent character was found, or StreamTokenizer.TT_WORD if a Gerber
     *         command was found. In the case of a Gerber command, sval is set to the Gerber command
     *         excluding its terminating '*' character. Note, carriage returns and line feeds are 
     *         stripped from each line as it is tokenized. In addition, any leading and trailing 
     *         white spaces are stripped from each line unless the spaces are part of an attribute 
     *         value.
     */
    @Override
    public int nextToken() throws IOException {
        String token = "";
        while (super.nextToken() != TT_EOF) {
            switch (ttype) {
                case TT_WORD:
                    if (tokenStarting) {
                        firstLineno = lineCount;
                        tokenStarting = false;
                    }
                    bytesProcessed += sval.length();
                    md5String += sval;
                    // Keep appending to the token until we hit an '*'
                    if (!attributeValue) {
                        sval = sval.trim();
                    }
                    token += sval;
                    if (token.length() > 0) {
                        lastChar = token.charAt(token.length() - 1);
                    }
                    break;
                case '*':
                    // Ok, we've hit an '*' which means we're at the end of a Gerber command, so
                    // save the token to sval and return TT_WORD
                    sval = unescape(token);
                    ttype = TT_WORD;
                    bytesProcessed += 1;
                    md5String += "*";
                    tokenStarting = true; // reset for the next token
                    lastChar = '*';
                    if (md5InProgress) {
                        //When computing the file's MD5 signature, we want to skip everything after 
                        //a %TF.MD5,...*% extended command or a closing M02* command (whichever
                        //comes first
                        if (md5String.contains("TF.MD5")) {
                            int idx1 = md5String.indexOf("G04");
                            int idx2 = md5String.indexOf("#@!");
                            int idx3 = md5String.indexOf("TF.MD5");
                            int idx4 = md5String.indexOf("%TF.MD5");
                            if (idx4 >= 0) {
                                //We have a TF extended command for a .MD5 Standard Attribute so
                                md5String = md5String.substring(0, idx4);
                                md5InProgress = false;
                            }
                            else if (idx1 >= 0 && idx2 > idx1 && idx3 > idx2) {
                                //We have a standard comment for a .MD5 Standard Attribute
                                md5String = md5String.substring(0, idx1);
                                md5InProgress = false;
                            }
                        }
                        else if (md5String.contains("M02*") || md5String.contains("M2*") || 
                                md5String.contains("M00*") || md5String.contains("M0*")) {
                            //We're at the end of the file
                            int idx = md5String.lastIndexOf("M");
                            md5String = md5String.substring(0, idx);
                            md5InProgress = false;
                        }
                        messageDigest.update(md5String.getBytes(StandardCharsets.UTF_8));
                        md5String = "";
                    }
                    attributeValue = false;
                    return ttype;
                case '%':
                    // We just return the '%' characters as we hit them so that the parser knows
                    // when a Gerber extended command has started/ended
                    firstLineno = lineCount;
                    bytesProcessed += 1;
                    md5String += "%";
                    if (lastChar != '*') {
                        // We should throw an exception here since we can't have a % without a
                        // preceding *. However, since the interface for nextToken doesn't allow for
                        // that, we just return a '*' so that the parser can detect it and
                        // throw the exception.
                        ttype = '*';
                    }
                    return ttype;
                case ',':
                    if (token.startsWith("TF") || token.startsWith("TA")
                            || token.startsWith("TO")) {
                        // Attribute values are more lenient as they can contain leading and 
                        // trailing spaces as part of the value. So, here we set a flag to ensure we
                        // don't trim them away when processing an attribute value.
                        attributeValue = true;
                    }
                    bytesProcessed += 1;
                    md5String += ",";
                    token += (char) ttype;
                    lastChar = ',';
                    break;
                case '\r':
                case '\n':
                    // Carriage returns and line feeds don't have any significance to the Gerber
                    // file syntax. Here we keep track of them so we can count line numbers. This is
                    // mainly for error reporting purposes if something goes wrong during the 
                    // parsing process.
                    if (starting) {
                        eol = ttype;
                        starting = false;
                    }
                    if (ttype == eol) {
                        lineCount++;
                    }
                    bytesProcessed += 1;
                    break;
                default:
                    // This should never happen since we've already accounted for all possibilities
                    System.out.println("At line " + lineCount + ", found unknown character value "
                            + super.ttype);
            }
        }
        if (ttype == TT_EOF && token.length() > 0) {
            // This shouldn't happen unless the file doesn't have a proper ending M02* command
            sval = unescape(token);
            ttype = TT_WORD;
            tokenStarting = true; // reset for the next token
            if (md5InProgress) {
                messageDigest.update(md5String.getBytes(StandardCharsets.UTF_8));
                md5String = "";
                md5InProgress = false;
            }
            return ttype;
        }
        return ttype;
    }

    /**
     * Scans a string for unicode escape sequences and replaces them with their equivalent unicode
     * characters
     * 
     * @param string a string with possible unicode escape sequences embedded within it
     * @return a copy of the string with all unicode escape sequences replaced by their unicode
     *         characters
     */
    private static String unescape(String string) {
        int i = 0;
        char c;
        int len = string.length();
        StringBuffer sb = new StringBuffer(len);
        while (i < len) {
            c = string.charAt(i++);
            if (c == '\\') {
                if (i < len) {
                    c = string.charAt(i++);
                    if (c == 'u') {
                        try {
                            c = (char) Integer.parseInt(string.substring(i, i + 4), 16);
                            i += 4;
                        }
                        catch (NumberFormatException e) {
                            // the escaped unicode character doesn't have the correct form (four
                            // hexidecimal digits) so just pass it along as a string
                            sb.append('\\');
                        }
                    }
                    else if (c == 'U') {
                        try {
                            c = (char) Integer.parseInt(string.substring(i, i + 8), 16);
                            i += 8;
                        }
                        catch (NumberFormatException e) {
                            // the escaped unicode character doesn't have the correct form (eight
                            // hexidecimal digits) so just pass it along as a string
                            sb.append('\\');
                        }
                    }
                    else {
                        // in all other cases just pass the backslash along
                        sb.append('\\');
                    }
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * Gets the number of file bytes processed so far.
     * 
     * @return the number of bytes
     */
    public long getBytesProcessed() {
        return bytesProcessed;
    }

    /**
     * Gets the file line number where the current token began.
     * 
     * @return the line number
     */
    public int getFirstLineno() {
        return firstLineno;
    }

    /**
     * Gets the file line number where the current token ended.
     * 
     * @return the line number
     */
    @Override
    public int lineno() {
        return lineCount;
    }

    /**
     * Gets a string description of which file line(s) correspond to the current token.
     * 
     * @return the description
     */
    public String getLineInfo() {
        if (firstLineno < lineCount) {
            return "lines " + firstLineno + " to " + lineCount;
        }
        else {
            return "line " + lineCount;
        }
    }
    
    /**
     * Gets the MD5 signature computed over all characters excluding carriage returns and line feeds
     * and ending with the last character before a "%TF.MD5,...*%" or "M02* command, whichever comes 
     * first.
     * 
     * @return the MD5 signature as 32 hexadecimal characters 
     */
    public String getMD5Signature() {
        if (messageDigest != null) {
            return String.format("%032x", new BigInteger(1, messageDigest.digest()));
        }
        else {
            return null;
        }
    }

    @Override
    public void close() throws Exception {
        reader.close();
    }
}
