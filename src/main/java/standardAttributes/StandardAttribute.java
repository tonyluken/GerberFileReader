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

import gerberFileReader.Attribute;
import gerberFileReader.GerberLayerFormatException;

/**
 * An abstract class to represent Gerber Standard Attributes
 */
public abstract class StandardAttribute extends Attribute {
    /**
     * The string used in Gerber files as the Standard Attribute's name. All subclasses of 
     * StandardAttribute must override this with the proper value. Failure to do so will result in
     * an IllegalStateException to be thrown when the constructor is called.
     */
    public static final String GERBER_STANDARD_ATTRIBUTE_NAME = "";
    
    /**
     * Constructs a Gerber Standard Attribute from a Gerber command
     * @param cmd the Gerber command
     * @throws GerberLayerFormatException if the command doesn't properly represent a Gerber 
     * Attribute
     */
    protected StandardAttribute(String cmd) throws GerberLayerFormatException {
        super(cmd);
        if (getGerberStandardAttributeName().equals(StandardAttribute.GERBER_STANDARD_ATTRIBUTE_NAME)) {
            throw new IllegalStateException("Subclasses of StandardAttribute must provide a unique value for GERBER_STANDARD_ATTRIBUTE_NAME");
        }
        initialize(this);
    }
    
    /**
     * Constructs and empty Gerber Standard Attribute. Call {@link #initialize(Attribute)} to 
     * initialize the contents with another Attribute.
     */
    protected StandardAttribute() {
        super();
        if (getGerberStandardAttributeName().equals(StandardAttribute.GERBER_STANDARD_ATTRIBUTE_NAME)) {
            throw new IllegalStateException("Subclasses of StandardAttribute must provide a unique value for GERBER_STANDARD_ATTRIBUTE_NAME");
        }
    }
    
    /**
     * Initializes and validates the standard attribute using the contents of the specified 
     * attribute.
     * 
     * @param attribute the attribute whose contents are used to initialize the standard attribute
     * @throws UnsupportedOperationException if the name of the specified attribute does not match
     * the name of the standard attribute.
     * @throws GerberLayerFormatException if the contents of the specified attribute are not valid
     * per the Gerber Layer Format Specification.
     */
    public void initialize(Attribute attribute) throws GerberLayerFormatException {
        if (!getGerberStandardAttributeName().equals(attribute.getName())) {
            throw new UnsupportedOperationException("Can not initialize standard attribute \"" + 
                    getGerberStandardAttributeName() + "\" with contents of " + attribute.toString());
        }
        name = attribute.getName();
        type = attribute.getType();
        values = attribute.getValues();
        validate();
    }

    /**
     * Gets the standard attribute's name as specified in the Gerber Layer Format Specification, 
     * should always return a string beginning with a "." character.
     * 
     * @return the standard attribute's name
     */
    public abstract String getGerberStandardAttributeName();
    
    /**
     * Validates the Standard Attribute to ensure all values are of proper type and range.
     * @throws GerberLayerFormatException if the contents of the specified attribute are not valid
     * per the Gerber Layer Format Specification.
     */
    public abstract void validate() throws GerberLayerFormatException;

    /**
     * Verifies the number of values this attribute has is exactly the expected count 
     * @param expectedCount the expected number of values
     * @throws GerberLayerFormatException if the number of values is not correct
     */
    protected void verifyValueCount(int expectedCount) throws GerberLayerFormatException {
        int count = getValues().size();
        if (count != expectedCount) {
            throw new GerberLayerFormatException("Invalid number of values for Standard Attribute, "
                    + "expected " + expectedCount + ", but found " + count + ": " + toString());
        }
    }
    
    /**
     * Verifies the number of values this attribute has is within the expected range
     * @param minCount the minimum number of expected values
     * @param maxCount the maximum number of expected values
     * @throws GerberLayerFormatException if the number of values is not correct
     */
    protected void verifyValueCount(int minCount, int maxCount) throws GerberLayerFormatException {
        int count = getValues().size();
        if (count < minCount || count > maxCount) {
            throw new GerberLayerFormatException("Invalid number of values for Standard Attribute, "
                    + "expected " + minCount + " to " + maxCount + ", but found " + count + ": " 
                    + toString());
        }
    }
    

}
