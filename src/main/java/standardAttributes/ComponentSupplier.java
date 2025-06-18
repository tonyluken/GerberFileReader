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

import java.util.ArrayList;
import java.util.List;

import gerberFileReader.GerberLayerFormatException;

/**
 * A class to represent the Gerber Standard Attribute {@value #GERBER_STANDARD_ATTRIBUTE_NAME}
 */
public class ComponentSupplier extends StandardAttribute {
    /**
     * The string used in Gerber files as the name for Component Supplier Standard Attributes: 
     * {@value #GERBER_STANDARD_ATTRIBUTE_NAME}
     */
    public static final String GERBER_STANDARD_ATTRIBUTE_NAME = ".CSup";

    /**
     * Constructs an empty Component Supplier Standard Attribute, call 
     * {@link StandardAttribute#initialize(Attribute)} to initialize the contents of this Component 
     * Supplier Standard Attribute.
     */
    public ComponentSupplier() {
        super();
    }
    
    /**
     * Constructs a Component Supplier Standard Attribute from a Gerber TO extended 
     * command.
     * 
     * @param cmd the Gerber command
     * @throws GerberLayerFormatException if the command does not properly define a Component 
     * Supplier Standard Attribute
     */
    public ComponentSupplier(String cmd) throws GerberLayerFormatException {
        super(cmd);
    }

    /**
     * Gets the number of suppliers for the component associated with the object to which this
     * attribute is attached.
     * 
     * @return the number of suppliers
     */
    public int getNumberOfSuppliers() {
        return getValues().size() / 2;
    }
    
    /**
     * Gets a list of supplier names for the component associated with the object to which this
     * attribute is attached. The names are returned in the same order as the part numbers returned 
     * by {@link #getSupplierPartNumbers()}.
     * 
     * @return a list of supplier names
     */
    public List<String> getSupplierNames() {
        List<String> supplierNames = new ArrayList<>();
        for (int i=0; i<getNumberOfSuppliers(); i++) {
            supplierNames.add(getValues().get(2*i));
        }
        return supplierNames;
    }
    
    /**
     * Gets a list of supplier part numbers for the component associated with the object to which
     * this attribute is attached. The part numbers are returned in the same order as the supplier 
     * names returned by {@link #getSupplierNames()}.
     * 
     * @return a list of supplier part numbers
     */
    public List<String> getSupplierPartNumbers() {
        List<String> supplierPartNumbers = new ArrayList<>();
        for (int i=0; i<getNumberOfSuppliers(); i++) {
            supplierPartNumbers.add(getValues().get(2*i+1));
        }
        return supplierPartNumbers;
    }

    @Override
    public String getGerberStandardAttributeName() {
        return GERBER_STANDARD_ATTRIBUTE_NAME;
    }

    @Override
    public void validate() throws GerberLayerFormatException {
        verifyValueCount(2, Integer.MAX_VALUE);
        if (getValues().size() % 2 != 0) {
            throw new GerberLayerFormatException("Supplier names and part numbers must be in pairs: "
                    + toString());
        }
    }
}
