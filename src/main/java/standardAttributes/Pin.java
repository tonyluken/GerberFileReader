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
public class Pin extends StandardAttribute {
    /**
     * The string used in Gerber files as the name for Pin Standard Attributes: 
     * {@value #GERBER_STANDARD_ATTRIBUTE_NAME}
     */
    public static final String GERBER_STANDARD_ATTRIBUTE_NAME = ".P";

    /**
     * Constructs an empty Pin Standard Attribute, call 
     * {@link StandardAttribute#initialize(Attribute)} to initialize the contents of this Pin
     * Standard Attribute.
     */
    public Pin() {
        super();
    }
    
    /**
     * Constructs a Pin Standard Attribute from a Gerber TO extended command.
     * 
     * @param cmd the Gerber command
     * @throws GerberLayerFormatException if the command does not properly define a Pin Standard 
     * Attribute
     */
    public Pin(String cmd) throws GerberLayerFormatException {
        super(cmd);
    }

    /**
     * Gets the component reference designator associated with the object to which this 
     * attribute is attached.
     * 
     * @return the reference designator
     */
    public String getReferenceDesignator() {
        return getValues().get(0);
    }
    
    /**
     * Gets the pin number of the component associated with the object to which this 
     * attribute is attached. Note that the Gerber Layer Format Specification always requires a pin
     * number but some ECAD programs (KiCad) don't always write a pin number when one isn't defined
     * for a footprint (many of the predefined fiducials). So here we'll just return an empty string 
     * if a pin number is not specified.
     * 
     * @return the pin number
     */
    public String getNumber() {
        if (getValues().size() > 1) {
            return getValues().get(1);
        }
        return "";
    }
    
    /**
     * Gets the pin function of the component associated with the object to which this 
     * attribute is attached.
     * 
     * @return the pin function or an empty string if no function is specified
     */
    public String getFunction() {
        if (getValues().size() > 2) {
            return getValues().get(2);
        }
        return "";
    }

    @Override
    public String getGerberStandardAttributeName() {
        return GERBER_STANDARD_ATTRIBUTE_NAME;
    }

    @Override
    public void validate() throws GerberLayerFormatException {
        //The Gerber spec says there should be 2 or 3 values but KiCad doesn't write a placeholder 
        //for a pin number if a pin number is not defined on a footprint's pad, e.g., fiducials. So 
        //for now we won't throw an exception if the pin number is not specified.
        verifyValueCount(1, 3);
    }
}
