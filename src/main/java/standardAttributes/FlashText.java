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

package standardAttributes;

import gerberFileReader.GerberLayerFormatException;

/**
 * A class to represent the Gerber Standard Attribute {@value #GERBER_STANDARD_ATTRIBUTE_NAME}
 */
public class FlashText extends StandardAttribute {
    /**
     * The string used in Gerber files as the name for Flash Text Standard Attributes: 
     * {@value #GERBER_STANDARD_ATTRIBUTE_NAME}
     */
    public static final String GERBER_STANDARD_ATTRIBUTE_NAME = ".FlashText";

    /**
     * Constructs an empty Flash Text Standard Attribute, call 
     * {@link StandardAttribute#initialize(Attribute)} to initialize the contents of this Flash Text
     * Standard Attribute.
     */
    public FlashText() {
        super();
    }
    
    /**
     * Constructs a Flash Text Standard Attribute from a Gerber TA extended command.
     * 
     * @param cmd the Gerber command
     * @throws GerberLayerFormatException if the command does not properly define a Flash Text
     * Standard Attribute
     */
    public FlashText(String cmd) throws GerberLayerFormatException {
        super(cmd);
    }
    
    /**
     * Gets the text represented by the object to which this attribute is attached.
     * 
     * @return the text
     */
    public String getText() {
        return getValues().get(0);
    }
    
    /**
     * An enumeration of the possible ways text may be represented: {@link #Barcode}, 
     * {@link #Characters}, or {@link #Unknown}
     */
    public enum TextRepresentation {
        /**
         * Indicates the text is represented by a barcode
         */
        Barcode,
        /**
         * Indicates the text is represented by human readable characters
         */
        Characters,
        /**
         * This value is only used to indicate the Gerber file contains an invalid or unknown type
         */
        Unknown;
        
        public static TextRepresentation fromAttributeValue(String attributeValue) {
            switch (attributeValue) {
                case "B" :
                    return Barcode;
                case "C" :
                    return Characters;
                default :
                    return Unknown;
            }
        }
    }
    
    /**
     * Gets how the text is represented.
     * 
     * @return how the text is represented
     * @see #isBarcode()
     * @see #isCharacters()
     */
    public TextRepresentation getTextRepresentation() {
        return TextRepresentation.fromAttributeValue(getValues().get(1));
    }
    
    /**
     * Checks if the text is represented by a machine readable barcode.
     * 
     * @return true if the text is represented by a machine readable barcode
     * @see #getTextRepresentation()
     * @see #isCharacters()
     */
    public boolean isBarcode() {
        return getTextRepresentation() == TextRepresentation.Barcode;
    }

    /**
     * Checks if the text is represented by human readable characters.
     * 
     * @return true if the text is represented by human readable characters
     * @see #getTextRepresentation()
     * @see #isBarcode()
     */
    public boolean isCharacters() {
        return getTextRepresentation() == TextRepresentation.Characters;
    }
    
    /**
     * Checks if the text is specified to be readable, that is, non-mirrored. Since the 
     * readable/mirrored field is optional, this method returns false if the field is not specified.
     * 
     * @return true if the text is non-mirrored
     * @see #isTextMirrored()
     */
    public boolean isTextReadable() {
        return getValues().size() > 2 && getValues().get(2).equals("R");
    }

    /**
     * Checks if the text is specified to be mirrored left-to-right. Since the readable/mirrored 
     * field is optional, this method returns false if the field is not specified.
     * 
     * @return true if the text is mirrored left-to-right
     * @see #isTextReadable()
     */
    public boolean isTextMirrored() {
        return getValues().size() > 2 && getValues().get(2).equals("M");
    }
    
    /**
     * Gets the font name.
     * 
     * @return the font name or an empty string if the font name is not specified
     * @see #getFontSize()
     */
    public String getFontName() {
        if (getValues().size() > 3) {
            return getValues().get(3);
        }
        return "";
    }
    
    /**
     * Gets the font size as a string.
     * 
     * @return the font size or an empty string if the font size is not specified
     * @see #getFontName()
     */
    public String getFontSize() {
        if (getValues().size() > 4) {
            return getValues().get(4);
        }
        return "";
    }

    /**
     * Gets any extra information about the text.
     * 
     * @return the extra information or an empty string in no extra information is specified
     */
    public String getComment() {
        if (getValues().size() > 5) {
            return getValues().get(5);
        }
        return "";
    }

    @Override
    public String getGerberStandardAttributeName() {
        return GERBER_STANDARD_ATTRIBUTE_NAME;
    }

    @Override
    public void validate() throws GerberLayerFormatException {
        verifyValueCount(2, 6);
        if (getTextRepresentation() == TextRepresentation.Unknown) {
            throw new GerberLayerFormatException("Invalid Barcode/Characters flag: " + toString());
        }
        if (getValues().size() > 2 && getValues().get(2).length() > 0  && 
                !getValues().get(2).matches("[MR]")) {
            throw new GerberLayerFormatException("Invalid Readable/Mirrored flag: " + toString());
        }
    }
}
