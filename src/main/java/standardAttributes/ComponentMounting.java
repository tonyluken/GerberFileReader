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
public class ComponentMounting extends StandardAttribute {
    /**
     * The string used in Gerber files as the name for Component Mounting Standard Attributes: 
     * {@value #GERBER_STANDARD_ATTRIBUTE_NAME}
     */
    public static final String GERBER_STANDARD_ATTRIBUTE_NAME = ".CMnt";

    /**
     * Constructs an empty Component Mounting Standard Attribute, call 
     * {@link StandardAttribute#initialize(Attribute)} to initialize the contents of this Component 
     * Mounting Standard Attribute.
     */
    public ComponentMounting() {
        super();
    }
    
    /**
     * Constructs a Component Mounting Standard Attribute from a Gerber TO extended command.
     * 
     * @param cmd the Gerber command
     * @throws GerberLayerFormatException if the command does not properly define a Component 
     * Mounting Standard Attribute
     */
    public ComponentMounting(String cmd) throws GerberLayerFormatException {
        super(cmd);
    }

    /**
     * An enumeration of possible mounting types: {@link #ThroughHole}, {@link #SurfaceMountDevice}, 
     * {@link #PressFit}, {@link #Other}, and {@link #Unknown}
     */
    public enum MountingType {
        /**
         * The component leads are inserted into plated holes and are soldered to make electrical
         * contact with the plated holes
         */
        ThroughHole,
        /**
         * The component leads rest on the surface of copper pads and are soldered to make 
         * electrical contact with the copper pads
         */
        SurfaceMountDevice,
        /**
         * The component leads are mechanically pressed into properly sized plated holes to make
         * electrical contact with the plated holes
         */
        PressFit,
        /**
         * The component is mounted in some other way
         */
        Other,
        /**
         * This value is only used to indicate the Gerber file contains an invalid or unknown type
         */
        Unknown;
        
        public static MountingType fromAttributeValue(String attributeValue) {
            switch (attributeValue) {
                case "TH" :
                    return ThroughHole;
                case "SMD" :
                    return SurfaceMountDevice;
                case "Pressfit" :
                    return PressFit;
                case "Other" :
                    return Other;
                default :
                    return Unknown;
            }
        }
    }
    
    /**
     * Gets the component's mounting type associated with the object to which this attribute
     * is attached.
     * 
     * @return the component's mounting type
     */
    public MountingType getMountingType() {
        return MountingType.fromAttributeValue(getValues().get(0));
    }
    
    /**
     * Checks if the component associated with the object to which this attribute is attached
     * is a through hole component.
     * 
     * @return true if the component is a through hole component
     */
    public boolean isThroughHole() {
        return getMountingType() == MountingType.ThroughHole;
    }
    
    /**
     * Checks if the component associated with the object to which this attribute is attached
     * is a surface mount component. 
     * 
     * @return true if the component is a surface mount component
     */
    public boolean isSurfaceMountDevice() {
        return getMountingType() == MountingType.SurfaceMountDevice;
    }
    
    /**
     * Checks if the component associated with the object to which this attribute is attached
     * is a press fit component, i.e., its leads are pressed into properly sized plated holes to 
     * realize electrical contact.
     * 
     * @return true if the component is press fit
     */
    public boolean isPressFit() {
        return getMountingType() == MountingType.PressFit;
    }
    
    /**
     * Checks if the component associated with the object to which this attribute is attached
     * is something other than a through hole, surface mount, or press fit component.
     * 
     * @return true if the component is something other than a through hole, surface mount, or press 
     * fit component
     */
    public boolean isOther() {
        return getMountingType() == MountingType.Other;
    }

    @Override
    public String getGerberStandardAttributeName() {
        return GERBER_STANDARD_ATTRIBUTE_NAME;
    }

    @Override
    public void validate() throws GerberLayerFormatException {
        verifyValueCount(1);
        if (getMountingType() == MountingType.Unknown) {
            throw new GerberLayerFormatException("Invalid component mounting type: " + toString());
        }
    }
    
    
}
