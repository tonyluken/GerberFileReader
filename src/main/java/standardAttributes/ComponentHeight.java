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
public class ComponentHeight extends StandardAttribute {
    /**
     * The string used in Gerber files as the name for Component Height Standard Attributes: 
     * {@value #GERBER_STANDARD_ATTRIBUTE_NAME}
     */
    public static final String GERBER_STANDARD_ATTRIBUTE_NAME = ".CHgt";
    
    /**
     * Constructs an empty Component Height Standard Attribute, call 
     * {@link StandardAttribute#initialize(Attribute)} to initialize the contents of this Component 
     * Height Standard Attribute
     */
    public ComponentHeight() {
        super();
    }
    
    /**
     * Constructs a Component Height Standard Attribute from a Gerber TO extended command
     * @param cmd the Gerber command
     * @throws GerberLayerFormatException if the command does not properly define a Component 
     * Height Standard Attribute
     */
    public ComponentHeight(String cmd) throws GerberLayerFormatException {
        super(cmd);
    }

    /**
     * Gets the height in units of the file of the component associated with the object to which 
     * this attribute is attached.
     * 
     * @return the height
     */
    public double getHeight() {
        return Double.parseDouble(getValues().get(0));
    }

    @Override
    public String getGerberStandardAttributeName() {
        return GERBER_STANDARD_ATTRIBUTE_NAME;
    }

    @Override
    public void validate() throws GerberLayerFormatException {
        verifyValueCount(1);
        try {
            if (getHeight() < 0) {
                throw new GerberLayerFormatException("Component height must be non-negative: " 
                        + toString());
            }
        }
        catch (NumberFormatException ex) {
            throw new GerberLayerFormatException("Invalid format for component height: " 
                    + toString());
        }
    }
}
