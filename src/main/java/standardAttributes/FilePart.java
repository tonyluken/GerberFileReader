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
public class FilePart extends StandardAttribute {
    /**
     * The string used in Gerber files as the name for File Part Standard Attributes: 
     * {@value #GERBER_STANDARD_ATTRIBUTE_NAME}
     */
    public static final String GERBER_STANDARD_ATTRIBUTE_NAME = ".Part";

    /**
     * Constructs an empty File Part Standard Attribute, call 
     * {@link StandardAttribute#initialize(Attribute)} to initialize the contents of this File 
     * Part Standard Attribute.
     */
    public FilePart() {
        super();
    }
    
    /**
     * Constructs a File Part Standard Attribute from a Gerber TF extended command.
     * 
     * @param cmd the Gerber command
     * @throws GerberLayerFormatException if the command does not properly define a File Part
     * Standard Attribute
     */
    public FilePart(String cmd) throws GerberLayerFormatException {
        super(cmd);
    }

    /**
     * An enumeration of possible file part types: {@link #Single}, {@link #Array}, 
     * {@link #FabricationPanel}, {@link #Coupon}, {@link #Other}, and {@link #Unknown}
     */
    public enum FilePartType {
        /**
         * The Gerber file represents a single PCB
         */
        Single,
        /**
         * The Gerber file represents an array of PCBs - also known as a customer panel, assembly 
         * panel, shipping panel, or biscuit
         */
        Array,
        /**
         * The Gerber file represents a fabrication panel - also known as a working panel or 
         * production panel
         */
        FabricationPanel,
        /**
         * The Gerber file represents a test coupon
         */
        Coupon,
        /**
         * The Gerber file represents a part that is not otherwise specified by the Gerber Layer 
         * Format Specification
         */
        Other,
        /**
         * This value is only used to indicate the Gerber file contains an invalid or unknown type
         */
        Unknown;
        
        protected static FilePartType fromAttributeValue(String attributeValue) {
            try {
                return FilePartType.valueOf(attributeValue);
            }
            catch (Exception e) {
                return Unknown;
            }
        }
    }
    
    /**
     * Gets the part type that the Gerber file represents.
     * 
     * @return the part type
     * @see #isSinglePCB()
     * @see #isArray()
     * @see #isFabricationPanel()
     * @see #isTestCoupon()
     * @see #isOther()
     */
    public FilePartType getPartType() {
        return FilePartType.fromAttributeValue(getValues().get(0));
    }
    
    /**
     * Checks if the Gerber file represents a single PCB.
     * 
     * @return true if the Gerber file represents a single PCB
     * @see #getPartType()
     * @see #isArray()
     * @see #isFabricationPanel()
     * @see #isTestCoupon()
     * @see #isOther()
     */
    public boolean isSinglePCB() {
        return getPartType() == FilePartType.Single;
    }
    
    /**
     * Checks if the Gerber file represents an array of PCBs - also known as a customer panel, 
     * assembly panel, shipping panel, or biscuit.
     * 
     * @return true if the Gerber file represents an array of PCBs
     * @see #getPartType()
     * @see #isSinglePCB()
     * @see #isFabricationPanel()
     * @see #isTestCoupon()
     * @see #isOther()
     */
    public boolean isArray() {
        return getPartType() == FilePartType.Array;
    }
    
    /**
     * Checks if the Gerber file represents a fabrication panel - also known as a working panel or 
     * production panel.
     * 
     * @return true if the Gerber file represents a fabrication panel
     * @see #getPartType()
     * @see #isSinglePCB()
     * @see #isArray()
     * @see #isTestCoupon()
     * @see #isOther()
     */
    public boolean isFabricationPanel() {
        return getPartType() == FilePartType.FabricationPanel;
    }
    
    /**
     * Checks if the Gerber file represents a test coupon.
     *  
     * @return true if the Gerber file represents a test coupon
     * @see #getPartType()
     * @see #isSinglePCB()
     * @see #isArray()
     * @see #isFabricationPanel()
     * @see #isOther()
     */
    public boolean isTestCoupon() {
        return getPartType() == FilePartType.Coupon;
    }
    
    /**
     * Checks if the Gerber file represents a part that is not otherwise specified by the 
     * Gerber Layer Format Specification. If so, {@link #getOtherDescription()} returns an informal
     * description of the part.
     * 
     * @return true if the Gerber file represents a part that is not otherwise specified by the 
     * Gerber Layer Format Specification
     * @see #getPartType()
     * @see #isSinglePCB()
     * @see #isArray()
     * @see #isFabricationPanel()
     * @see #isTestCoupon()
     */
    public boolean isOther() {
        return getPartType() == FilePartType.Other;
    }
    
    /**
     * Gets an informal description of the part that is not otherwise specified by the Gerber
     * Layer Format Specification.
     * 
     * @return the informal description
     * @throws UnsupportedOperationException if the Gerber file does not represents a part that is 
     * not otherwise specified by the Gerber Layer Format Specification
     * @see #isOther {@link #isOther()}
     */
    public String getOtherDescription() {
        if (!isOther()) {
            throw new UnsupportedOperationException("Method getOtherDescription() is not supported for: " 
                    + toString());
        }
        return getValues().get(1);
    }

    @Override
    public String getGerberStandardAttributeName() {
        return GERBER_STANDARD_ATTRIBUTE_NAME;
    }

    @Override
    public void validate() throws GerberLayerFormatException {
        verifyValueCount(1, 2);
        if (getPartType() == FilePartType.Unknown) {
            throw new GerberLayerFormatException("Invalid File Part value: " + toString());
        }
        if (isOther()) {
            verifyValueCount(2);
        }
    }
}
