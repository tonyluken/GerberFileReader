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
public class FileGenerationSoftware extends StandardAttribute {
    /**
     * The string used in Gerber files as the name for File Generation Software Standard Attributes: 
     * {@value #GERBER_STANDARD_ATTRIBUTE_NAME}
     */
    public static final String GERBER_STANDARD_ATTRIBUTE_NAME = ".GenerationSoftware";

    /**
     * Constructs an empty File Generation Software Standard Attribute, call 
     * {@link StandardAttribute#initialize(Attribute)} to initialize the contents of this File 
     * Generation Software Standard Attribute.
     */
    public FileGenerationSoftware() {
        super();
    }
    
    /**
     * Constructs a File Generation Software Standard Attribute from a Gerber TF extended command.
     * 
     * @param cmd the Gerber command
     * @throws GerberLayerFormatException if the command does not properly define a File Generation 
     * Software Standard Attribute
     */
    public FileGenerationSoftware(String cmd) throws GerberLayerFormatException {
        super(cmd);
    }

    /**
     * Gets the name of the software vendor.
     * 
     * @return the vendor's name
     * @see #getApplication()
     * @see #getVersion()
     */
    public String getVendor() {
        return getValues().get(0);
    }

    /**
     * Gets the name of the software application.
     * 
     * @return the application's name
     * @see #getVendor()
     * @see #getVersion()
     */
    public String getApplication() {
        return getValues().get(1);
    }

    /**
     * Gets the version of the software application.
     * 
     * @return the version
     * @see #getVendor()
     * @see #getApplication()
     */
    public String getVersion() {
        return getValues().get(2);
    }

    @Override
    public String getGerberStandardAttributeName() {
        return GERBER_STANDARD_ATTRIBUTE_NAME;
    }

    @Override
    public void validate() throws GerberLayerFormatException {
        verifyValueCount(3);
    }
    
}
