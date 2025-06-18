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

import java.math.BigInteger;

import gerberFileReader.GerberLayerFormatException;

/**
 * A class to represent the Gerber Standard Attribute {@value #GERBER_STANDARD_ATTRIBUTE_NAME}
 */
public class FileMD5 extends StandardAttribute {
    /**
     * The string used in Gerber files as the name for File MD5 Standard Attributes: 
     * {@value #GERBER_STANDARD_ATTRIBUTE_NAME}
     */
    public static final String GERBER_STANDARD_ATTRIBUTE_NAME = ".MD5";

    /**
     * Constructs an empty File MD5 Standard Attribute, call 
     * {@link StandardAttribute#initialize(Attribute)} to initialize the contents of this File 
     * MD5 Standard Attribute.
     */
    public FileMD5() {
        super();
    }
    
    /**
     * Constructs a File MD5 Standard Attribute from a Gerber TF extended command.
     * 
     * @param cmd the Gerber command
     * @throws GerberLayerFormatException if the command does not properly define a File MD5 
     * Standard Attribute
     */
    public FileMD5(String cmd) throws GerberLayerFormatException {
        super(cmd);
    }

    /**
     * Gets the file's expected MD5 signature as a string.
     * 
     * @return the MD5 signature
     */
    public String getSignature() {
        return getValues().get(0);
    }

    @Override
    public String getGerberStandardAttributeName() {
        return GERBER_STANDARD_ATTRIBUTE_NAME;
    }

    @Override
    public void validate() throws GerberLayerFormatException {
        verifyValueCount(1);
        try {
            new BigInteger(getSignature(), 16);
        }
        catch (NumberFormatException ex) {
            throw new GerberLayerFormatException("Invalid format for MD5 signature: " + toString());
        }
        if (getSignature().length() != 32) {
            throw new GerberLayerFormatException(
                    "MD5 signature is required to have 32 hex characters: " + toString());
        }
    }
}
