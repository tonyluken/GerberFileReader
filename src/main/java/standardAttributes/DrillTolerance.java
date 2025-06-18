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
public class DrillTolerance extends StandardAttribute {
    /**
     * The string used in Gerber files as the name for Drill Tolerance Standard Attributes: 
     * {@value #GERBER_STANDARD_ATTRIBUTE_NAME}
     */
    public static final String GERBER_STANDARD_ATTRIBUTE_NAME = ".DrillTolerance";
    
    /**
     * Constructs an empty Drill Tolerance Standard Attribute, call 
     * {@link StandardAttribute#initialize(Attribute)} to initialize the contents of this Drill 
     * Tolerance Standard Attribute.
     */
    public DrillTolerance() {
        super();
    }
    
    /**
     * Constructs a Drill Tolerance Standard Attribute from a Gerber TA extended command.
     * 
     * @param cmd the Gerber command
     * @throws GerberLayerFormatException if the command does not properly define a Drill Tolerance
     * Standard Attribute
     */
    public DrillTolerance(String cmd) throws GerberLayerFormatException {
        super(cmd);
    }
    
    /**
     * Gets the plus tolerance of a drill hole as a positive value.
     * 
     * @return the plus tolerance
     * @see getMinusTolerance()
     */
    public double getPlusTolerance() {
        return Double.parseDouble(getValues().get(0));
    }
    
    /**
     * Gets the minus tolerance of a drill hole as a positive number.
     * 
     * @return the minus tolerance
     * @see getPlusTolerance()
     */
    public double getMinusTolerance() {
        return Double.parseDouble(getValues().get(1));
    }

    @Override
    public String getGerberStandardAttributeName() {
        return GERBER_STANDARD_ATTRIBUTE_NAME;
    }

    @Override
    public void validate() throws GerberLayerFormatException {
        verifyValueCount(2);
        try {
            if (getPlusTolerance() < 0) {
                throw new GerberLayerFormatException(
                        "Invalid plus drill tolerance (must be non-negative): " + toString());
            }
        }
        catch (NumberFormatException ex) {
            throw new GerberLayerFormatException(
                    "Invalid format for plus drill tolerance: " + toString());
        }
        try {
            if (getMinusTolerance() < 0) {
                throw new GerberLayerFormatException(
                        "Invalid minus drill tolerance (must be non-negative): " + toString());
            }
        }
        catch (NumberFormatException ex) {
            throw new GerberLayerFormatException(
                    "Invalid format for minus drill tolerance: " + toString());
        }
    }

}
