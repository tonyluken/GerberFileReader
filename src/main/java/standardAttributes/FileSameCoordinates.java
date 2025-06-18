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
public class FileSameCoordinates extends StandardAttribute {
    /**
     * The string used in Gerber files as the name for File Same Coordinates Standard Attributes: 
     * {@value #GERBER_STANDARD_ATTRIBUTE_NAME}
     */
    public static final String GERBER_STANDARD_ATTRIBUTE_NAME = ".SameCoordinates";

    /**
     * Constructs an empty File Same Coordinate Standard Attribute, call 
     * {@link StandardAttribute#initialize(Attribute)} to initialize the contents of this File 
     * Same Coordinate Standard Attribute.
     */
    public FileSameCoordinates() {
        super();
    }
    
    /**
     * Constructs a File Same Coordinate Standard Attribute from a Gerber TF extended command.
     * 
     * @param cmd the Gerber command
     * @throws GerberLayerFormatException if the command does not properly define a File Same 
     * Coordinate Standard Attribute
     */
    public FileSameCoordinates(String cmd) throws GerberLayerFormatException {
        super(cmd);
    }
    
    /**
     * Gets the optional identifier as a string.
     * 
     * @return the optional identifier as a string if one is present, otherwise returns an empty 
     * string
     */
    public String getIdentifier() {
        if (getValues().size() > 0) {
            return getValues().get(0);
        }
        return "";
    }

    @Override
    public String getGerberStandardAttributeName() {
        return GERBER_STANDARD_ATTRIBUTE_NAME;
    }

    @Override
    public void validate() throws GerberLayerFormatException {
        verifyValueCount(0, 1);
    }
}
