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

import java.util.List;

import gerberFileReader.GerberLayerFormatException;

/**
 * A class to represent the Gerber Standard Attribute {@value #GERBER_STANDARD_ATTRIBUTE_NAME}
 */
public class Net extends StandardAttribute {
    /**
     * The string used in Gerber files as the name for Net Standard Attributes: 
     * {@value #GERBER_STANDARD_ATTRIBUTE_NAME}
     */
    public static final String GERBER_STANDARD_ATTRIBUTE_NAME = ".N";

    /**
     * Constructs an empty Net Standard Attribute, call 
     * {@link StandardAttribute#initialize(Attribute)} to initialize the contents of this Net
     * Standard Attribute.
     */
    public Net() {
        super();
    }
    
    /**
     * Constructs a Net Standard Attribute from a Gerber TO extended command.
     * 
     * @param cmd the Gerber command
     * @throws GerberLayerFormatException if the command does not properly define a Net Standard 
     * Attribute
     */
    public Net(String cmd) throws GerberLayerFormatException {
        super(cmd);
    }

    /**
     * Gets the first name (it may have more than one) of the net associated with the object to 
     * which this attribute is attached.
     * 
     * @return the name
     * 
     * @see #getNetNameCount()
     * @see #getNetNames()
     */
    public String getNetName() {
        return getValues().get(0);
    }
    
    /**
     * Gets the number of net names associated with the object to which this attribute is 
     * attached.
     * 
     * @return the number of net names
     */
    public int getNetNameCount() {
        return getValues().size();
    }
    
    /**
     * Gets a list of all net names associated with the object to which this attribute is 
     * attached.
     * 
     * @return the list of net names
     */
    public List<String> getNetNames() {
        return getValues();
    }

    /**
     * Checks if the net name is empty, that is, this attribute is attached to an object not 
     * connected to a net, such as tooling holes, text, logos, pads for component leads not 
     * connected to the component circuitry, etc.
     * 
     * @return true if the net name is empty
     */
    public boolean isEmpty() {
        return getValues().get(0).isEmpty();
    }
    
    /**
     * Checks if the net name is "N/C" which is reserved for single pad nets that are not 
     * connected electrically to any other pads. This is an alternative to giving each such net 
     * a unique name.
     * 
     * @return true if the net name is "N/C"
     */
    public boolean isNotConnected() {
        return getValues().get(0).equals("N/C");
    }
    
    @Override
    public String getGerberStandardAttributeName() {
        return GERBER_STANDARD_ATTRIBUTE_NAME;
    }

    @Override
    public void validate() throws GerberLayerFormatException {
        verifyValueCount(1, Integer.MAX_VALUE);
    }
}
