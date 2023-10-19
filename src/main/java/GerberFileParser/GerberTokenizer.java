/*
 * Copyright (C) 2023 Tony Luken <tonyluken62+gerberfileparser.gmail.com>
 * 
 * This file is part of GerberFileParser.
 * 
 * GerberFileParser is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of 
 * the License, or (at your option) any later version.
 * 
 * GerberFileParser is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with GerberFileParser. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package GerberFileParser;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;

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
    
    /**
     * Constructs a tokenizer for Gerber files
     * @param reader - the reader of the Gerber file
     */
    public GerberTokenizer(Reader reader) {
        super(reader);
        this.reader = reader;
        resetSyntax();
        wordChars(0, 0x10FFFF); //makes all UTF-8 characters part of a word except for those below
        ordinaryChar('%');
        ordinaryChar('*');
        ordinaryChar('\r');
        ordinaryChar('\n');
        lowerCaseMode(false);
        eolIsSignificant(false);
    }
    
    /**
     * Gets the next token from the Gerber file
     * 
     * @return either StreamTokenizer.TT_EOF if the end of the file has been reached, the character 
     * value of '%' if a percent character was found, or StreamTokenizer.TT_WORD if a Gerber command
     * was found. In the case of a Gerber command, sval is set to the Gerber command excluding its 
     * terminating '*' character. Note, any leading and trailing white space (including carriage 
     * returns and/or line feeds) are stripped from each line of the file as it is tokenized.
     */
    @Override
    public int nextToken() throws IOException {
        String token = "";
        while(super.nextToken() != TT_EOF) {
            switch (ttype) {
                case TT_WORD:
                    if (tokenStarting) {
                        firstLineno = lineCount;
                        tokenStarting = false;
                    }
                    bytesProcessed += sval.length();
                    //Keep appending to the token until we hit an '*'
                    token += sval.trim();
                    break;
                case '*':
                    //Ok, we've hit an '*' which means we're at the end of a Gerber command, so save
                    //the token to sval and return TT_WORD
                    sval = unescape(token);
                    ttype = TT_WORD;
                    bytesProcessed += 1;
                    tokenStarting = true; //reset for the next token
                    return ttype;
                case '%':
                    //We just return the '%' characters as we hit them so that the parser knows when
                    //a Gerber extended command has started/ended 
                    firstLineno = lineCount;
                    bytesProcessed += 1;
                    return ttype;
                case '\r':
                case '\n':
                    //Carriage returns and line feeds don't have any significance to the Gerber file
                    //syntax. We only keep track of them here so we can count line numbers. This is
                    //mainly for error reporting purposes if something goes wrong during the parsing
                    //process.
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
                    //This should never happen
                    System.out.println("At line " + lineCount + ", found unknown '" + super.ttype + "'");
            }
        };
        if (ttype == TT_EOF && token.length() > 0) {
            sval = unescape(token);
            ttype = TT_WORD;
            tokenStarting = true; //reset for the next token
            return ttype;
        }
        return ttype;
    }
    
    /**
     * Scans a string for unicode escape sequences and replaces them with their equivalent unicode 
     * characters
     * 
     * @param s - a string with possible unicode escape sequences embedded within it
     * @return a copy of the string with all unicode escape sequences replaced by their unicode 
     * characters
     */
    private static String unescape(String s) {
        int i = 0;
        char c;
        int len = s.length();
        StringBuffer sb = new StringBuffer(len);
        while (i<len) {
             c = s.charAt(i++);
             if (c == '\\') {
                  if (i<len) {
                       c = s.charAt(i++);
                       if (c == 'u') {
                          try {
                               c = (char) Integer.parseInt(s.substring(i, i+4), 16);
                               i += 4;
                          }
                          catch (Exception e) {
                              //the escaped unicode character doesn't have the correct form (four 
                              //hexidecimal digits) so just pass it along as a string
                              sb.append('\\');
                          }
                       }
                       else if (c == 'U') {
                          try {
                               c = (char) Integer.parseInt(s.substring(i, i+8), 16);
                               i += 8;
                          }
                          catch (Exception e) {
                              //the escaped unicode character doesn't have the correct form (eight 
                              //hexidecimal digits) so just pass it along as a string
                              sb.append('\\');
                          }
                       }
                       else {
                           //in all other cases just pass the backslash along
                           sb.append('\\');
                       }
                  }
             }
        sb.append(c);
        }
        return sb.toString();
    }
    
    /**
     * 
     * @return the number of file bytes processed so far
     */
    public long getBytesProcessed() {
        return bytesProcessed;
    }
    
    /**
     * Gets the file line number where the current token began
     * 
     * @return the first line number
     */
    public int getFirstLineno() {
        return firstLineno;
    }
    
    /**
     * Gets the file line number where the current token ended
     */
    @Override
    public int lineno() {
        return lineCount;
    }
    
    /**
     * 
     * @return a string description of which file line(s) the current token came from
     */
    public String getLineInfo() {
        if (firstLineno < lineCount) {
            return "lines " + firstLineno + " to " + lineCount;
        }
        else {
            return "line " + lineCount;
        }
    }

    @Override
    public void close() throws Exception {
        reader.close();
    }
}