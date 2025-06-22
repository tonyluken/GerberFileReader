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
public class FileFunction extends StandardAttribute {
    /**
     * The string used in Gerber files as the name for File Function Standard Attributes: 
     * {@value #GERBER_STANDARD_ATTRIBUTE_NAME}
     */
    public static final String GERBER_STANDARD_ATTRIBUTE_NAME = ".FileFunction";
    
    /**
     * Constructs an empty File Function Standard Attribute, call 
     * {@link StandardAttribute#initialize(Attribute)} to initialize the contents of this File 
     * Function Standard Attribute
     */
    public FileFunction() {
        super();
    }
    
    /**
     * Constructs a File Function Standard Attribute from a Gerber TF extended command
     * @param cmd the Gerber command
     * @throws GerberLayerFormatException if the command does not properly define a File Function 
     * Standard Attribute
     */
    public FileFunction(String cmd) throws GerberLayerFormatException {
        super(cmd);
        validate();
    }

    /**
     * An enumeration of possible file functions: {@link #Copper}, {@link #Plated}, 
     * {@link #NonPlated}, {@link #Profile}, {@link #Soldermask}, {@link #Legend}, 
     * {@link #Component}, {@link #Paste}, {@link #Glue}, {@link #Carbonmask}, {@link #Goldmask}, 
     * {@link #Heatsinkmask}, {@link #Peelablemask}, {@link #Silvermask}, {@link #Tinmask}, 
     * {@link #Depthrout}, {@link #Vcut}, {@link #Viafill}, {@link #Pads}, {@link #Other}, 
     * {@link #Drillmap}, {@link #FabricationDrawing}, {@link #Vcutmap}, {@link #AssemblyDrawing}, 
     * {@link #ArrayDrawing}, {@link #OtherDrawing}, and {@link #Unknown}.
     */
    public enum Function {
        /**
         * Defines the layout of a copper layer of the PCB.
         */
        Copper,
        /**
         * Defines the size and location of plated drill holes and routed slots.
         */
        Plated,
        /**
         * Defines the size and location of non-plated drill holes and routed slots.
         */
        NonPlated,
        /**
         * Defines the shape (AKA outline or edge cuts) of the PCB.
         */
        Profile,
        /**
         * Defines a solder mask for the PCB.
         */
        Soldermask,
        /**
         * Defines a legend or silk screen for the PCB.
         */
        Legend,
        /**
         * Defines the orientation, location, and type of components to install on the PCB
         */
        Component,
        /**
         * Defines where solder paste should be applied to the PCB prior to component assembly.
         */
        Paste,
        /**
         * Defines where glue should be applied to the PCB prior to component assembly.
         */
        Glue,
        /**
         * Defines a carbon mask for the PCB.
         */
        Carbonmask,
        /**
         * Defines a gold mask for the PCB.
         */
        Goldmask,
        /**
         * Defines a heatsink mask for the PCB.
         */
        Heatsinkmask,
        /**
         * Defines a peelable mask for the PCB.
         */
        Peelablemask,
        /**
         * Defines a silver mask for the PCB.
         */
        Silvermask,
        /**
         * Defines a tin mask for the PCB.
         */
        Tinmask,
        /**
         * Defines areas that must be routed to a given depth rather than going through the whole 
         * board.
         */
        Depthrout,
        /**
         * Defines lines to be v-cut or scored.
         */
        Vcut,
        /**
         * Defines the vias that are to be filled.
         */
        Viafill,
        /**
         * Defines the pads (footprints) of the PCB's components.
         */
        Pads,
        /**
         * Defines some function not otherwise specified by the Gerber Layer Format Specification.
         */
        Other,
        /**
         * A drawing with the locations of the drilled holes. It often also contains the hole sizes, 
         * tolerances and plated/non-plated info. 
         */
        Drillmap,
        /**
         * An auxiliary drawing with additional information for the fabrication of the bare PCB: the
         * location of holes and slots, the board outline, sizes and tolerances, layer stack, 
         * material, finish, etc.
         */
        FabricationDrawing,
        /**
         * A drawing with v-cut or scoring information. 
         */
        Vcutmap,
        /**
         * An auxiliary drawing with the locations and reference designators of the components. It 
         * is mainly used in PCB assembly. 
         */
        AssemblyDrawing,
        /**
         * A drawing of the array (biscuit, assembly panel, shipment panel, customer panel). 
         */
        ArrayDrawing,
        /**
         * Any other drawing whose function is not otherwise specified by the Gerber Layer Format 
         * Specification.
         */
        OtherDrawing,
        /**
         * This value is only used to indicate the Gerber file contains an invalid or unknown 
         * function.
         */
        Unknown;

        public static Function fromAttributeValue(String attributeValue) {
            try {
                return valueOf(Function.class, attributeValue);
            }
            catch (IllegalArgumentException e) {
                return Unknown;
            }
        }
    }
    
    /**
     * Gets the file's function.
     * 
     * @return the file's function
     */
    public Function getFunction() {
        return Function.fromAttributeValue(getValues().get(0));
    }
    
    /**
     * Checks if the file describes a copper layer.
     * 
     * @return true if the file describes a copper layer
     * 
     * @see #getFunction()
     * @see #getCopperLayerNumber()
     * @see #getCopperSide()
     * @see #isCopperTop()
     * @see #isCopperInner()
     * @see #isCopperBottom()
     * @see #hasCopperType()
     * @see #getCopperType()
     */
    public boolean isCopperLayer() {
        return getFunction() == Function.Copper;
    }
    
    /**
     * For copper layers, gets the copper layer number within the PCB. For an N layer board, the 
     * layers are numbered 1 to N with 1 being the top layer.
     * 
     * @return the copper layer number
     * @throws UnsupportedOperationException if the file does not describe a copper layer
     * @see #isCopperLayer()
     */
    public int getCopperLayerNumber() {
        if (!isCopperLayer()) {
            throw new UnsupportedOperationException("Method getCopperLayerNumber() is not available for " + toString());
        }
        return Integer.parseInt(getValues().get(1).substring(1)); //Strip the leading 'L'
    }
    
    /**
     * For copper layers, gets the the PCB side of the copper layer.
     * 
     * @return the side
     * @throws UnsupportedOperationException if the file does not describe a copper layer
     * @see #isCopperLayer()
     */
    public BoardSide getCopperSide() {
        if (!isCopperLayer()) {
            throw new UnsupportedOperationException("Method getCopperSide() is not available for " + toString());
        }
        return BoardSide.fromAttributeValue(getValues().get(2));
    }
    
    /**
     * Checks if the file describes the top copper layer.
     * 
     * @return true if the file describes the top copper layer
     * @see #isCopperLayer()
     * @see #getCopperSide()
     * @see #isCopperInner()
     * @see #isCopperBottom()
     */
    public boolean isCopperTop() {
        return isCopperLayer() && getCopperSide() == BoardSide.Top;
    }
    
    /**
     * Checks if the file describes an inner copper layer.
     * 
     * @return true if the file describes an inner copper layer
     * @see #isCopperLayer()
     * @see #getCopperSide()
     * @see #isCopperTop()
     * @see #isCopperBottom()
     */
    public boolean isCopperInner() {
        return isCopperLayer() && getCopperSide() == BoardSide.Inner;
    }
    
    /**
     * Checks if the file describes the bottom copper layer.
     * 
     * @return true if the file describes the bottom copper layer
     * @see #isCopperLayer()
     * @see #getCopperSide()
     * @see #isCopperTop()
     * @see #isCopperInner()
     */
    public boolean isCopperBottom() {
        return isCopperLayer() && getCopperSide() == BoardSide.Bottom;
    }
    
    /**
     * For copper layers, checks if a copper type is specified. If so, {@link #getCopperType()} 
     * returns the copper type.
     * 
     * @return true if a copper type is specified
     * @throws UnsupportedOperationException if the file does not describe a copper layer
     * @see #isCopperLayer()
     */
    public boolean hasCopperType() {
        if (!isCopperLayer()) {
            throw new UnsupportedOperationException("Method hasCopperType() is not available for " + toString());
        }
        return getValues().size() > 3;
    }
    
    /**
     * For copper layers, gets the copper type.
     * 
     * @return the copper type if a copper type is specified, otherwise returns null 
     * @throws UnsupportedOperationException if the file does not describe a copper layer
     * @see #isCopperLayer()
     * @see #hasCopperType()
     */
    public CopperType getCopperType() {
        if (!isCopperLayer()) {
            throw new UnsupportedOperationException("Method getCopperType() is not available for " + toString());
        }
        if (hasCopperType()) {
            return CopperType.fromAttributeValue(getValues().get(3));
        }
        return null;
    }
    
    /**
     * Checks if the file describes plated holes and routed slots in the PCB.
     * 
     * @return true if the file describes plated holes and routed slots
     * @see #getFunction()
     * @see #isNonPlatedHoles()
     * @see #getHoleFromLayerNumber()
     * @see #getHoleToLayerNumber()
     * @see #getHoleType()
     * @see #hasHoleFabricationMethod()
     * @see #getHoleFabricationMethod()
     */
    public boolean isPlatedHoles() {
        return getFunction() == Function.Plated;
    }
    
    /**
     * Checks if the file describes non-plated holes and routed slots in the PCB.
     * 
     * @return true if the file describes the non-plated holes and routed slots
     * @see #getFunction()
     * @see #isPlatedHoles()
     * @see #getHoleFromLayerNumber()
     * @see #getHoleToLayerNumber()
     * @see #getHoleType()
     * @see #hasHoleFabricationMethod()
     * @see #getHoleFabricationMethod()
     */
    public boolean isNonPlatedHoles() {
        return getFunction() == Function.NonPlated;
    }
    
    /**
     * Gets the drill/rout span from layer number for the holes and routed slots.
     * 
     * @return the from layer number
     * @throws UnsupportedOperationException if the file does not describe holes and routed slots.
     * @see #getHoleToLayerNumber()
     * @see #isPlatedHoles()
     * @see #isNonPlatedHoles()
     */
    public int getHoleFromLayerNumber() {
        if (!isPlatedHoles() && !isNonPlatedHoles()) {
            throw new UnsupportedOperationException("Method getHoleFromLayerNumber() is not available for " + toString());
        }
        return Integer.parseInt(getValues().get(1));
    }
    
    /**
     * Gets the drill/rout span to layer number for the holes and routed slots.
     * 
     * @return the to layer number
     * @throws UnsupportedOperationException if the file does not describe holes and routed slots.
     * @see #getHoleFromLayerNumber()
     * @see #isPlatedHoles()
     * @see #isNonPlatedHoles()
     */
    public int getHoleToLayerNumber() {
        if (!isPlatedHoles() && !isNonPlatedHoles()) {
            throw new UnsupportedOperationException("Method getHoleToLayerNumber() is not available for " + toString());
        }
        return Integer.parseInt(getValues().get(2));
    }
    
    /**
     * Gets the hole type defined by the file.
     * 
     * @return the hole type
     * @throws UnsupportedOperationException if the file does not describe holes and routed slots.
     * @see #isPlatedHoles()
     * @see #isNonPlatedHoles()
     */
    public HoleType getHoleType() {
        if (!isPlatedHoles() && !isNonPlatedHoles()) {
            throw new UnsupportedOperationException("Method getHoleType() is not available for " + toString());
        }
        return HoleType.fromAttributeValue(getValues().get(3));
    }
    
    /**
     * Checks if a hole fabrication method is specified by the file. If so, 
     * {@link #getHoleFabricationMethod()} returns the hole fabrication method.
     * 
     * @return true if a hole fabrication method is specified
     * @throws UnsupportedOperationException if the file does not describe holes and routed slots.
     * @see #isPlatedHoles()
     * @see #isNonPlatedHoles()
     */
    public boolean hasHoleFabricationMethod() {
        if (!isPlatedHoles() && !isNonPlatedHoles()) {
            throw new UnsupportedOperationException("Method hasHoleFabricationMethod() is not available for " + toString());
        }
        return getValues().size() > 4;
    }
    
    /**
     * Gets the hole fabrication method.
     * 
     * @return the hole fabrication method if one is specified, null otherwise
     * @throws UnsupportedOperationException if the file does not describe holes and routed slots.
     * 
     * @see #hasHoleFabricationMethod()
     * @see #isPlatedHoles()
     * @see #isNonPlatedHoles()
     */
    public HoleFabricationMethod getHoleFabricationMethod() {
        if (!isPlatedHoles() && !isNonPlatedHoles()) {
            throw new UnsupportedOperationException("Method getHoleFabricationMethod() is not available for " + toString());
        }
        if (hasHoleFabricationMethod()) {
            return HoleFabricationMethod.fromAttributeValue(getValues().get(4));
        }
        return null;
    }
    
    /**
     * Checks if the file defines the profile (outline or edge cuts) of the PCB.
     * 
     * @return true if the file defines the profile
     * 
     * @see #getFunction()
     * @see #isProfileEdgePlated()
     * @see #isProfileNonEdgePlated()
     */
    public boolean isProfile() {
        return getFunction() == Function.Profile;
    }
    
    /**
     * An enumeration of PCB profile edge treatments: {@link #Plated}, {@link #NonPlated}, and 
     * {@link #Unknown}.
     */
    public enum ProfileEdgeTreatment {
        /**
         * Indicated the PCB edges are to be copper plated
         */
        Plated,
        /**
         * Indicated the PCB edges are to not be copper plated
         */
        NonPlated,
        /**
         * This value is only used to indicate the Gerber file contains an invalid or unknown 
         * value.
         */
        Unknown;
        
        public static ProfileEdgeTreatment fromAttributeValue(String attributeValue) {
            switch (attributeValue) {
                case "P" :
                    return Plated;
                case "NP" :
                    return NonPlated;
                default :
                    return Unknown;
            }
        }
    }
    
    /**
     * Gets the profile's edge treatment.
     * 
     * @return the edge treatment
     * @throws UnsupportedOperationException if the file doesn't defines the profile
     * @see #isProfile()
     * @see #isProfileEdgePlated()
     * @see #isProfileNonEdgePlated()
     */
    public ProfileEdgeTreatment getProfileEdgeTreatment() {
        if (!isProfile()) {
            throw new UnsupportedOperationException("Method getProfileEdgeTreatment() is not available for " + toString());
        }
        return ProfileEdgeTreatment.fromAttributeValue(getValues().get(1));
    }
    
    /**
     * For files defining the profile, checks if the profile is to be edge plated.
     * 
     * @return true if the file defines the profile and its edges are to be plated
     * 
     * @see #isProfile()
     * @see #getProfileEdgeTreatment()
     * @see #isProfileNonEdgePlated()
     */
    public boolean isProfileEdgePlated() {
        return isProfile() && getProfileEdgeTreatment() == ProfileEdgeTreatment.Plated;
    }
    
    /**
     * For files defining the profile, checks if the profile is to not be edge plated.
     * 
     * @return true if the file defines the profile and its edges are not to be plated
     * 
     * @see #isProfile()
     * @see #getProfileEdgeTreatment()
     * @see #isProfileEdgePlated()
     */
    public boolean isProfileNonEdgePlated() {
        return isProfile() && getProfileEdgeTreatment() == ProfileEdgeTreatment.NonPlated;
    }
    
    /**
     * Checks if the file defines one of the known PCB mask types.
     * 
     * @return true if the file defines one of the known mask types
     * 
     * @see #getFunction()
     * @see #getMaskType()
     * @see #isSolderMask()
     * @see #getMaskSide()
     * @see #isMaskTop()
     * @see #isMaskBottom()
     * @see #hasMaskIndex()
     * @see #getMaskIndex()
     */
    public boolean isMask() {
        if (getValues().size() < 1) {
            return false;
        }
        return MaskType.fromAttributeValue(getValues().get(0)) != MaskType.Unknown;
    }
    
    /**
     * Gets the PCB mask type defined by the file
     * 
     * @return the mask type
     * @throws UnsupportedOperationException if the file does not define a mask
     * @see #isMask()
     * @see #isSolderMask()
     */
    public MaskType getMaskType() {
        if (!isMask()) {
            throw new UnsupportedOperationException("Method getMaskType() is not available for " + toString());
        }
        return MaskType.fromAttributeValue(getValues().get(0));
    }
    
    /**
     * Checks if the file defines a solder mask for the PCB.
     * 
     * @return true if the file defines a solder mask
     * @see #getFunction()
     * @see #isMask()
     * @see #getMaskType()
     */
    public boolean isSolderMask() {
        return getFunction() == Function.Soldermask;
    }
    
    /**
     * Gets the side of the PCB on which the mask is to be applied.
     * 
     * @return the side
     * @throws UnsupportedOperationException if the file does not describe a mask
     * @see #isMask()
     * @see #isMaskTop()
     * @see #isMaskBottom()
     */
    public BoardSide getMaskSide() {
        if (!isMask()) {
            throw new UnsupportedOperationException("Mehtod getMaskSide() is not available for " + toString());
        }
        return BoardSide.fromAttributeValue(getValues().get(1));
    }
    
    /**
     * Checks if the file defines a mask for the top side of the PCB.
     * 
     * @return true if the file defines a mask for the top side
     * @see #isMask()
     * @see #getMaskSide()
     * @see #isMaskBottom()
     */
    public boolean isMaskTop() {
        return isMask() && getMaskSide() == BoardSide.Top;
    }
    
    /**
     * Checks if the file defines a mask for the bottom side of the PCB.
     * 
     * @return true if the file defines a mask for the bottom side
     * @see #isMask()
     * @see #getMaskSide()
     * @see #isMaskTop()
     */
    public boolean isMaskBottom() {
        return isMask() && getMaskSide() == BoardSide.Bottom;
    }
    
    /**
     * Checks if the file has an optional index that indicates the order in which the mask is 
     * to be applied to the PCB. If so, {@link #getMaskIndex()} returns the value of the index.
     * 
     * @return true if the file has the optional index
     * @throws UnsupportedOperationException if the file does not describe a mask
     * @see #isMask()
     * @see #getMaskIndex()
     */
    public boolean hasMaskIndex() {
        if (!isMask()) {
            throw new UnsupportedOperationException("Method hasMaskIndex() is not available for " + toString());
        }
        return getValues().size() > 2;
    }
    
    /**
     * Gets the value of the optional mask index that indicates the order in which the mask is to be 
     * applied to the PCB. Masks with lower indices are applied before ones with higher indices.
     *
     * @return the value of the mask index if the file has one, otherwise returns -1
     * @throws UnsupportedOperationException if the file does not describe a mask.
     * @see #isMask()
     * @see #hasMaskIndex()
     */
    public int getMaskIndex() {
        if (!isMask()) {
            throw new UnsupportedOperationException("Method getMaskIndex() is not available for " + toString());
        }
        if (hasMaskIndex()) {
            return Integer.parseInt(getValues().get(2));
        }
        return -1;
    }
    
    /**
     * Checks if the file defines a legend (also known as a silkscreen) for the PCB.
     * 
     * @return true if the file defines a legend
     * @see #getFunction()
     * @see #getLegendSide()
     * @see #isLegendTop()
     * @see #isLegendBottom()
     * @see #hasLegendIndex()
     * @see #getLegendIndex() 
     */
    public boolean isLegend() {
        return getFunction() == Function.Legend;
    }
    
    /**
     * Gets the side of the PCB on which the legend is to be applied.
     * 
     * @return the side
     * @throws UnsupportedOperationException if the file does not describe a legend
     * @see #isLegend()
     * @see #isLegendTop()
     * @see #isLegendBottom()
     */
    public BoardSide getLegendSide() {
        if (!isLegend()) {
            throw new UnsupportedOperationException("Method getLegendSide() is not available for " + toString());
        }
        return BoardSide.fromAttributeValue(getValues().get(1));
    }
    
    /**
     * Checks if the file defines a legend for the top side of the PCB.
     * 
     * @return true if the file defines a legend for the top side of the PCB.
     * @see #isLegend()
     * @see #getLegendSide()
     * @see #isLegendBottom()
     */
    public boolean isLegendTop() {
        return isLegend() && getLegendSide() == BoardSide.Top;
    }
    
    /**
     * Checks if the file defines a legend for the bottom side of the PCB.
     * 
     * @return true if the file defines a legend for the bottom side of the PCB.
     * @see #isLegend()
     * @see #getLegendSide()
     * @see #isLegendTop()
     */
    public boolean isLegendBottom() {
        return isLegend() && getLegendSide() == BoardSide.Bottom;
    }
    
    /**
     * Checks if the file has the optional index that defines the order in which the legend 
     * is to be applied to the PCB. If so, {@link #getLegendIndex()} returns the value of the index.
     * 
     * @return true if the file has the optional index
     * @throws UnsupportedOperationException if the file does not describe a legend
     * @see #isLegend()
     * @see #getLegendIndex()
     */
    public boolean hasLegendIndex() {
        if (!isLegend()) {
            throw new UnsupportedOperationException("Method hasLegendIndex() is not available for " + toString());
        }
        return getValues().size() > 2;
    }
    
    /**
     * Gets the value of the legend index that defines the order in which the legend is to be 
     * applied to the PCB. Legends with lower indices are applied to the PCB before legends with
     * higher indices.
     * 
     * @return the value of the legend index if one is specified, otherwise returns -1
     * @throws UnsupportedOperationException if the file does not describe a legend
     * @see #isLegend()
     * @see #hasLegendIndex()
     */
    public int getLegendIndex() {
        if (!isLegend()) {
            throw new UnsupportedOperationException("Method getLegendIndex() is not available for " + toString());
        }
        if (hasLegendIndex()) {
            return Integer.parseInt(getValues().get(2));
        }
        return -1;
    }
    
    /**
     * Checks if the file describes components, their location, and orientation on the 
     * PCB.
     * 
     * @return true if the file describes components
     * @see #getFunction()
     * @see #getComponentSide()
     * @see #isComponentSideTop()
     * @see #isComponentSideBottom()
     * @see #getComponentLayerNumber()
     */
    public boolean isComponent() {
        return getFunction() == Function.Component;
    }
    
    /**
     * Gets the side of the PCB on which the components are to be installed.
     * 
     * @return the side
     * @throws UnsupportedOperationException if the file does not describe components.
     * @see #isComponent()
     * @see #isComponentSideTop()
     * @see #isComponentSideBottom()
     */
    public BoardSide getComponentSide() {
        if (!isComponent()) {
            throw new UnsupportedOperationException("Method getComponentSide() is not available for " + toString());
        }
        return BoardSide.fromAttributeValue(getValues().get(2));
    }
    
    /**
     * Checks if the file describes components for the top side of the PCB.
     * 
     * @return true if the file describes components and they are to be installed on the top side
     * @see #isComponent()
     * @see #getComponentSide()
     * @see #isComponentSideBottom()
     */
    public boolean isComponentSideTop() {
        return isComponent() && getComponentSide() == BoardSide.Top;
    }
    
    /**
     * Checks if the file describes components for the bottom side of the PCB.
     * 
     * @return true if the file describes components and they are to be installed on the bottom side
     * @see #isComponent()
     * @see #getComponentSide()
     * @see #isComponentSideTop()
     */
    public boolean isComponentSideBottom() {
        return isComponent() && getComponentSide() == BoardSide.Bottom;
    }
    
    /**
     * Gets the layer number (where 1 is the top copper layer) of the PCB where the components
     * reside. Note, this information is somewhat redundant with the information returned by 
     * {@link #getComponentSide()}. However, for embedded components, the layer number is important.
     * 
     * @return the layer number
     * @throws UnsupportedOperationException if the file does not describe components
     * @see #isComponent()
     */
    public int getComponentLayerNumber() {
        if (!isComponent()) {
            throw new UnsupportedOperationException("Method getComponentLayerNumber() is not available for " + toString());
        }
        return Integer.parseInt(getValues().get(1).substring(1)); //Strip leading 'L'
    }
    
    /**
     * Checks if the file describes where solder paste should be applied to the PCB.
     * 
     * @return true if the file describes where solder paste should be applied
     * @see #getFunction()
     * @see #getPasteSide()
     * @see #isPasteTop()
     * @see #isPasteBottom()
     */
    public boolean isPaste() {
        return getFunction() == Function.Paste;
    }
    
    /**
     * Gets the side of the PCB to which solder paste should be applied.
     * 
     * @return the side
     * @throws UnsupportedOperationException if the file doesn't describe where solder paste should
     * be applied
     * @see #isPaste()
     */
    public BoardSide getPasteSide() {
        if (!isPaste()) {
            throw new UnsupportedOperationException("Method getPasteSide() is not available for " + toString());
        }
        return BoardSide.fromAttributeValue(getValues().get(1));
    }
    
    /**
     * Checks if the file describes solder paste for the top side of the PCB.
     * 
     * @return true if the file describes solder paste for the top side
     * @see #isPaste()
     * @see #getPasteSide()
     * @see #isPasteBottom()
     */
    public boolean isPasteTop() {
        return isPaste() && getPasteSide() == BoardSide.Top;
    }
    
    /**
     * Checks if the file describes solder paste for the bottom side of the PCB.
     * 
     * @return true if the file describes solder paste for the bottom side
     * @see #isPaste()
     * @see #getPasteSide()
     * @see #isPasteBottom()
     */
    public boolean isPasteBottom() {
        return isPaste() && getPasteSide() == BoardSide.Bottom;
    }
    
    /**
     * Checks if the file describes where glue should be applied to the PCB.
     * 
     * @return true if the file describes where glue should be applied
     * @see #getFunction()
     * @see #getGlueSide()
     * @see #isGlueTop()
     * @see #isGlueBottom()
     */
    public boolean isGlue() {
        return getFunction() == Function.Glue;
    }
    
    /**
     * Gets the side of the PCB on which glue is to be applied.
     * 
     * @return the side
     * @throws UnsupportedOperationException if the file doesn't describe where glue should be 
     * applied to the PCB
     * @see #isGlue()
     * @see #isGlueTop()
     * @see #isGlueBottom()
     */
    public BoardSide getGlueSide() {
        if (!isGlue()) {
            throw new UnsupportedOperationException("Method getGlueSide() is not available for " + toString());
        }
        return BoardSide.fromAttributeValue(getValues().get(1));
    }
    
    /**
     * Checks if the file describes where glue should be applied to the top of the PCB.
     * 
     * @return true if the file describes where glue should be applied to the top
     * @see #isGlue()
     * @see #getGlueSide()
     * @see #isGlueBottom()
     */
    public boolean isGlueTop() {
        return isGlue() && getGlueSide() == BoardSide.Top;
    }
    
    /**
     * Checks if the file describes where glue should be applied to the bottom of the PCB.
     * 
     * @return true if the file describes where glue should be applied to the bottom
     * @see #isGlue()
     * @see #getGlueSide()
     * @see #isGlueTop()
     */
    public boolean isGlueBottom() {
        return isGlue() && getGlueSide() == BoardSide.Bottom;
    }
    
    /**
     * Checks if the file describes areas of the PCB that are to be routed to a specific depth (not
     * all the way through the board).
     * 
     * @return true if the file describes areas of the PCB that are to be routed to a specific depth
     * @see #getFunction()
     * @see #getDepthRoutSide()
     * @see #isDepthRoutTop()
     * @see #isDepthRoutBottom()
     */
    public boolean isDepthRout() {
        return getFunction() == Function.Depthrout;
    }
    
    /**
     * Gets the side of the PCB to be routed to a specific depth.
     * 
     * @return the side
     * @throws UnsupportedOperationException if the file doesn't describe where the PCB is to be 
     * routed to a specific depth
     * @see #isDepthRout()
     * @see #isDepthRoutTop()
     * @see #isDepthRoutBottom()
     */
    public BoardSide getDepthRoutSide() {
        if (!isDepthRout()) {
            throw new UnsupportedOperationException("Method getDepthRoutSide() is not available for " + toString());
        }
        return BoardSide.fromAttributeValue(getValues().get(1));
    }
    
    /**
     * Checks if the file describes where the top side of the PCB is to be routed to a specific 
     * depth.
     * 
     * @return true if the file describes where the top side of the PCB is to be routed to a 
     * specific depth
     * @see #isDepthRout()
     * @see #getDepthRoutSide()
     * @see #isDepthRoutBottom()
     */
    public boolean isDepthRoutTop() {
        return isDepthRout() && getDepthRoutSide() == BoardSide.Top;
    }
    
    /**
     * Checks if the file describes where the bottom side of the PCB is to be routed to a specific 
     * depth.
     * 
     * @return true if the file describes where the bottom side of the PCB is to be routed to a 
     * specific depth
     * @see #isDepthRout()
     * @see #getDepthRoutSide()
     * @see #isDepthRoutTop()
     */
    public boolean isDepthRoutBottom() {
        return isDepthRout() && getDepthRoutSide() == BoardSide.Bottom;
    }
    
    /**
     * Checks if the file describes v-cuts (also known as scoring).
     * 
     * @return true if the file describes v-cuts
     * @see #getFunction()
     * @see #getVCutSide()
     * @see #isVCutTop()
     * @see #isVCutBottom()
     * @see #isVCutBoth()
     */
    public boolean isVCut() {
        return getFunction() == Function.Vcut;
    }
    
    /**
     * Gets the side of the PCB that is to be v-cut or scored.
     * 
     * @return the side
     * @throws UnsupportedOperationException if the file doesn't describe v-cuts 
     * @see #isVCut()
     * @see #isVCutTop()
     * @see #isVCutBottom()
     * @see #isVCutBoth()
     */
    public BoardSide getVCutSide() {
        if (!isVCut()) {
            throw new UnsupportedOperationException("Method getVCutSide() is not available for " + toString());
        }
        if (getValues().size() > 1) {
            return BoardSide.fromAttributeValue(getValues().get(1));
        }
        return BoardSide.Both;
    }
    
    /**
     * Checks if the file describes where the top of the PCB should be v-cut
     * 
     * @return true if the file describes where the top of the PCB should be v-cut
     * @see #isVCut()
     * @see #getVCutSide()
     * @see #isVCutBottom()
     * @see #isVCutBoth()
     */
    public boolean isVCutTop() {
        return isVCut() && getVCutSide() == BoardSide.Top;
    }
    
    /**
     * Checks if the file describes where the bottom of the PCB should be v-cut
     * 
     * @return true if the file describes where the bottom of the PCB should be v-cut
     * @see #isVCut()
     * @see #getVCutSide()
     * @see #isVCutTop()
     * @see #isVCutBoth()
     */
    public boolean isVCutBottom() {
        return isVCut() && getVCutSide() == BoardSide.Bottom;
    }
    
    /**
     * Checks if the file describes where the PCB should be v-cut identically on its top and bottom.
     * 
     * @return true if the file describes where both sides of the PCB should be v-cut
     * @see #isVCut()
     * @see #getVCutSide()
     * @see #isVCutTop()
     * @see #isVCutBottom()
     */
    public boolean isVCutBoth() {
        return isVCut() && getVCutSide() == BoardSide.Both;
    }
    
    /**
     * Check if the file describes vias that are to be filled.
     * 
     * @return true if the file describes vias that are to be filled
     * @see #getFunction()
     */
    public boolean isViaFill() {
        return getFunction() == Function.Viafill;
    }
    
    /**
     * Checks if file describes pads.
     * 
     * @return true if the file describes pads
     * @see #getFunction()
     * @see #getPadsSide()
     * @see #isPadsTop()
     * @see #isPadsBottom()
     */
    public boolean isPads() {
        return getFunction() == Function.Pads;
    }
    
    /**
     * Gets the side of the PCB on which the pads belong.
     * 
     * @return the side
     * @throws UnsupportedOperationException if the file doesn't describe pads.
     * @see #isPads()
     * @see #isPadsTop()
     * @see #isPadsBottom()
     */
    public BoardSide getPadsSide() {
        if (!isPads()) {
            throw new UnsupportedOperationException("Method getPadsSide() is not available for " + toString());
        }
        return BoardSide.fromAttributeValue(getValues().get(1));
    }
    
    /**
     * Checks if the file describes the pads on the top of the PCB.
     * 
     * @return true if the file describes pads on the top of the PCB
     * @see #isPads()
     * @see #getPadsSide()
     * @see #isPadsBottom() 
     */
    public boolean isPadsTop() {
        return isPads() && getPadsSide() == BoardSide.Top;
    }
    
    /**
     * Checks if the file describes the pads on the bottom of the PCB.
     * 
     * @return true if the file describes pads on the bottom of the PCB
     * @see #isPads()
     * @see #getPadsSide()
     * @see #isPadsTop() 
     */
    public boolean isPadsBottom() {
        return isPads() && getPadsSide() == BoardSide.Bottom;
    }
    
    /**
     * Checks if the file has some function that is not otherwise specified by the Gerber
     * Layer Format Specification. If so, {@link #getOtherDescription()} returns an informal 
     * description of the file's function.
     * 
     * @return true if the file has some function that is not otherwise specified by the Gerber
     * Layer Format Specification
     * @see #getFunction()
     */
    public boolean isOther() {
        return getFunction() == Function.Other;
    }
    
    /**
     * Gets an informal description of the file's other function.
     * 
     * @return an informal description of the file's function
     * @throws UnsupportedOperationException if the file doesn't have a function that is not 
     * otherwise specified by the Gerber Layer Format Specification
     * @see #isOther()
     */
    public String getOtherDescription() {
        if (!isOther()) {
            throw new UnsupportedOperationException("Method getOtherDescription() is not available for " + toString());
        }
        return getValues().get(1);
    }
    
    /**
     * Checks if the file describes a drawing with the locations of drilled holes. It often also
     * contains the hole sizes, tolerances and plated/non-plated info.
     * 
     * @return true if the file describes a drawing with the locations of drilled holes
     * @see #getFunction()
     */
    public boolean isDrillMapDrawing() {
        return getFunction() == Function.Drillmap;
    }
    
    /**
     * Checks if the file describes a fabrication drawing which is an auxiliary drawing with 
     * additional information for the fabrication of the bare PCB: the location of holes and slots, 
     * the board outline, sizes and  tolerances, layer stack, material, finish, etc.
     * 
     * @return true if the file describes an fabrication drawing
     * @see #getFunction()
     */
    public boolean isFabricationDrawing() {
        return getFunction() == Function.FabricationDrawing;
    }
    
    /**
     * Checks if the file describes a drawing with v-cut or scoring information.
     * 
     * @return true if the file describes a drawing with v-cut or scoring information 
     * @see #getFunction()
     */
    public boolean isVCutMapDrawing() {
        return getFunction() == Function.Vcutmap;
    }
    
    /**
     * Checks if the file describes an assembly drawing which is an auxiliary drawing with the 
     * locations and reference designators of the components. It is mainly used in PCB assembly.
     * 
     * @return true if the file describes an assembly drawing
     * @see #getFunction()
     * @see #getAssemblyDrawingSide()
     * @see #isAssemblyDrawingTop()
     * @see #isAssemblyDrawingBottom()
     */
    public boolean isAssemblyDrawing() {
        return getFunction() == Function.AssemblyDrawing;
    }
    
    /**
     * Gets the side of the PCB described by the assembly drawing.
     * 
     * @return the side
     * @throws UnsupportedOperationException if the file doesn't describe an assembly drawing
     * @see #isAssemblyDrawing()
     * @see #isAssemblyDrawingTop()
     * @see #isAssemblyDrawingBottom()
     */
    public BoardSide getAssemblyDrawingSide() {
        if (!isAssemblyDrawing()) {
            throw new UnsupportedOperationException("Method getAssemblyDrawingSide() is not available for " + toString());
        }
        return BoardSide.fromAttributeValue(getValues().get(1));
    }
    
    /**
     * Checks if the file describes an assembly drawing for the top side of the PCB.
     * 
     * @return true if the file describes an assembly drawing for the top side of the PCB
     * @see #isAssemblyDrawing()
     * @see #getAssemblyDrawingSide()
     * @see #isAssemblyDrawingBottom()
     */
    public boolean isAssemblyDrawingTop() {
        return isAssemblyDrawing() && getAssemblyDrawingSide() == BoardSide.Top;
    }
    
    /**
     * Checks if the file describes an assembly drawing for the bottom side of the PCB.
     * 
     * @return true if the file describes an assembly drawing for the bottom side of the PCB
     * @see #isAssemblyDrawing()
     * @see #getAssemblyDrawingSide()
     * @see #isAssemblyDrawingTop()
     */
    public boolean isAssemblyDrawingBottom() {
        return isAssemblyDrawing() && getAssemblyDrawingSide() == BoardSide.Bottom;
    }
    
    /**
     * Checks if the file describes a drawing of an array (also known as a biscuit, assembly panel, 
     * shipment panel, or customer panel).
     * 
     * @return true if the file describes a drawing of an array
     * @see #getFunction()
     */
    public boolean isArrayDrawing() {
        return getFunction() == Function.ArrayDrawing;
    }
    
    /**
     * Checks if the file describes a drawing whose function is not otherwise specified by the 
     * Gerber Layer Format Specification. If so, {@link #getOtherDrawingDescription()} returns an 
     * informal description of the drawing's function.
     * 
     * @return true if the file describes a drawing whose function is not otherwise specified by the 
     * Gerber Layer Format Specification
     * @see #getFunction()
     */
    public boolean isOtherDrawing() {
        return getFunction() == Function.OtherDrawing;
    }
    
    /**
     * Gets the informal description of the other drawing's function.
     * 
     * @return the informal description
     * @throws UnsupportedOperationException if the file doesn't describe a drawing whose function 
     * is not otherwise specified by the Gerber Layer Format Specification
     * @see #isOtherDrawing()
     */
    public String getOtherDrawingDescription() {
        if (!isOtherDrawing()) {
            throw new UnsupportedOperationException("Method getOtherDrawingDescription() is not available for " + toString());
        }
        return getValues().get(1);
    }
    
    /**
     * An enumeration of possible board sides: {@link #Top}, {@link #Inner}, {@link #Bottom}, 
     * {@link #Both}, and {@link #Unknown}.
     */
    public enum BoardSide {
        /**
         * The top side of the PCB. If the PCB holds through-hole components, this is the side on
         * which they get installed.
         */
        Top,
        /**
         * Any inner layer of the PCB.
         */
        Inner,
        /**
         * The bottom side of the PCB. The side opposite from the Top side.
         */
        Bottom,
        /**
         * Both top and bottom sides. Only used for v-cuts when a side is not specified.
         */
        Both,
        /**
         * This value is only used to indicate the Gerber file contains an invalid or unknown 
         * value.
         */
        Unknown;
        
        public static BoardSide fromAttributeValue(String attributeValue) {
            switch (attributeValue) {
                case "Top":
                    return Top;
                case "Inr":
                    return Inner;
                case "Bot":
                    return Bottom;
                default :
                    return Unknown;
            }
        }
    }

    /**
     * An enumeration of possible copper layer types: {@link #Plane}, {@link #Signal}, 
     * {@link #Mixed}, {@link #Hatched}, and {@link #Unknown}.
     */
    public enum CopperType {
        /**
         * Solid copper typically power and ground planes.
         */
        Plane,
        /**
         * Copper etched to create signal conducting paths.
         */
        Signal,
        /**
         * A combination of solid copper planes in some areas and signal conduction paths in others.
         */
        Mixed,
        /**
         * A copper latticework that functions as a power or ground plane but provides regular 
         * openings to improve adhesion between PCB layers. Often used in flexible PCBs.
         */
        Hatched, 
        /**
         * This value is only used to indicate the Gerber file contains an invalid or unknown 
         * value.
         */
        Unknown;
        
        public static CopperType fromAttributeValue(String attributeValue) {
            try {
                return valueOf(CopperType.class, attributeValue);
            }
            catch (IllegalArgumentException e) {
                return Unknown;
            }
        }
    }

    /**
     * An enumeration of possible hole types: {@link #PTH}, {@link #NPTH}, {@link #Blind}, 
     * {@link #Buried}, and {@link #Unknown}.
     */
    public enum HoleType {
        /**
         * Plated through hole
         */
        PTH,
        /**
         * Non-plated through hole
         */
        NPTH,
        /**
         * A hole from an outer layer of the PCB to an inner layer of the PCB (not passing entirely 
         * through the PCB)
         */
        Blind, 
        /**
         * A hole only between inner layers of the PCB
         */
        Buried, 
        /**
         * This value is only used to indicate the Gerber file contains an invalid or unknown 
         * value.
         */
        Unknown;
        
        public static HoleType fromAttributeValue(String attributeValue) {
            try {
                return valueOf(HoleType.class, attributeValue);
            }
            catch (IllegalArgumentException e) {
                return Unknown;
            }
        }
    }

    /**
     * An enumeration of known hole fabrication methods: {@link #Drill}, {@link #Rout}, 
     * {@link #Mixed}, and {@link #Unknown}.
     */
    public enum HoleFabricationMethod {
        /**
         * Holes are formed by drilling
         */
        Drill,
        /**
         * Holes are formed by routing
         */
        Rout,
        /**
         * Holes are formed by a combination of drilling and routing
         */
        Mixed, 
        /**
         * This value is only used to indicate the Gerber file contains an invalid or unknown 
         * value.
         */
        Unknown;
        
        public static HoleFabricationMethod fromAttributeValue(String attributeValue) {
            try {
                return valueOf(HoleFabricationMethod.class, attributeValue);
            }
            catch (IllegalArgumentException e) {
                return Unknown;
            }
        }
    }

    /**
     * An enumeration of known mask types: {@link #Soldermask}, {@link #Carbonmask}, 
     * {@link #Goldmask}, {@link #Heatsinkmask}, {@link #Peelablemask}, {@link #Silvermask}, 
     * {@link #Tinmask}, and {@link #Unknown}.
     */
    public enum MaskType {
        /**
         * A polymer coating designed to prevent solder from adhering to specific areas of the PCB
         */
        Soldermask,
        /**
         * A conductive paste used for specific purposes, often applied to pads for keypads, 
         * jumpers, or other similar applications. 
         */
        Carbonmask,
        /**
         * A gold plating process to enhance electrical conductivity and corrosion resistance. 
         */
        Goldmask,
        /**
         * A thermally conductive paste used to improve heat transfer. 
         */
        Heatsinkmask,
        /**
         * A temporary mask that is applied and then removed at a certain stage of the PCB assembly
         * process.
         */
        Peelablemask,
        /**
         * A silver plating process to enhance electrical conductivity and corrosion resistance. 
         */
        Silvermask, 
        /**
         * A tin plating process to enhance electrical conductivity and corrosion resistance. 
         */
        Tinmask, 
        /**
         * This value is only used to indicate the Gerber file contains an invalid or unknown 
         * value.
         */
        Unknown;
        
        public static MaskType fromAttributeValue(String attributeValue) {
            try {
                return valueOf(MaskType.class, attributeValue);
            }
            catch (IllegalArgumentException e) {
                return Unknown;
            }
        }
    }
    
    @Override
    public String getGerberStandardAttributeName() {
        return GERBER_STANDARD_ATTRIBUTE_NAME;
    }
    
    @Override
    public void validate() throws GerberLayerFormatException {
        try {
            if (getValues().size() == 0) {
                throw new Exception("No file function specified: " + toString());
            }
            switch (getFunction()) {
                case Copper:
                    verifyValueCount(3, 4);
                    try {
                        if (getCopperLayerNumber() < 1) {
                            throw new Exception("Invalid layer number: " + toString());
                        };
                    }
                    catch (NumberFormatException ex) {
                        throw new Exception("Invalid format for layer number: " + toString());
                    }
                    if (getCopperSide() == BoardSide.Unknown) {
                        throw new Exception("Invalid side specified: " + toString());
                    }
                    if (hasCopperType() && getCopperType() == CopperType.Unknown) {
                        throw new Exception("Invalid type specified: " + toString());
                    }
                    break;
                case AssemblyDrawing:
                    verifyValueCount(2);
                    if (getAssemblyDrawingSide() == BoardSide.Unknown) {
                        throw new Exception("Invalid side specified: " + toString());
                    }
                    break;
                case Component:
                    verifyValueCount(3);
                    try {
                        if (getComponentLayerNumber() < 1) {
                            throw new Exception("Invalid layer number: " + toString());
                        }
                    }
                    catch (NumberFormatException ex) {
                        throw new Exception("Invalid format for layer number: " + toString());
                    }
                    if (getComponentSide() == BoardSide.Unknown) {
                        throw new Exception("Invalid side specified: " + toString());
                    }
                    break;
                case Depthrout:
                    verifyValueCount(2);
                    if (getDepthRoutSide() == BoardSide.Unknown) {
                        throw new Exception("Invalid side specified: " + toString());
                    }
                    break;
                case Glue:
                    verifyValueCount(2);
                    if (getGlueSide() == BoardSide.Unknown) {
                        throw new Exception("Invalid side specified: " + toString());
                    }
                    break;
                case Legend:
                    verifyValueCount(2, 3);
                    if (getLegendSide() == BoardSide.Unknown) {
                        throw new Exception("Invalid side specified: " + toString());
                    }
                    if (hasLegendIndex()) {
                        try {
                            if (getLegendIndex() < 1) {
                                throw new Exception("Invalid legend index: " + toString());
                            }
                        }
                        catch (NumberFormatException ex) {
                            throw new Exception("Invalid format for legend index: " + toString());
                        }
                    }
                    break;
                case NonPlated:
                case Plated:
                    verifyValueCount(4, 5);
                    try {
                        if (getHoleFromLayerNumber() < 1) {
                            throw new Exception("Invalid from layer number: " + toString());
                        }
                    }
                    catch (NumberFormatException ex) {
                        throw new Exception("Invalid format for from layer number: " + toString());
                    }
                    try {
                        if (getHoleToLayerNumber() < 1) {
                            throw new Exception("Invalid to layer number: " + toString());
                        }
                    }
                    catch (NumberFormatException ex) {
                        throw new Exception("Invalid format for to layer number: " + toString());
                    }
                    if (getHoleType() == HoleType.Unknown) {
                        throw new Exception("Invalid hole type specified: " + toString());
                    }
                    if (hasHoleFabricationMethod() && 
                            getHoleFabricationMethod() == HoleFabricationMethod.Unknown) {
                        throw new Exception("Invalid hole fabrication method specified: " + 
                            toString());
                    }
                    break;
                case Other:
                    verifyValueCount(2);
                    break;
                case OtherDrawing:
                    verifyValueCount(2);
                    break;
                case Pads:
                    verifyValueCount(2);
                    if (getPadsSide() == BoardSide.Unknown) {
                        throw new Exception("Invalid side: " + toString());
                    }
                    break;
                case Paste:
                    verifyValueCount(2);
                    if (getPasteSide() == BoardSide.Unknown) {
                        throw new Exception("Invalid side: " + toString());
                    }
                    break;
                case Profile:
                    verifyValueCount(2);
                    if (this.getProfileEdgeTreatment() == ProfileEdgeTreatment.Unknown) {
                        throw new Exception("Invalid profile edge treatment specified: " + 
                                toString());
                    }
                    break;
                case Carbonmask:
                case Goldmask:
                case Heatsinkmask:
                case Peelablemask:
                case Silvermask:
                case Soldermask:
                case Tinmask:
                    verifyValueCount(2, 3);
                    if (getMaskSide() == BoardSide.Unknown) {
                        throw new Exception("Invalid side for mask layer: " + toString());
                    }
                    if (hasMaskIndex()) {
                        try {
                            if (getMaskIndex() < 1) {
                                throw new Exception("Invalid mask index number: " + toString());
                            }
                        }
                        catch (NumberFormatException ex) {
                            throw new Exception("Invalid format for mask index number: " + toString());
                        }
                    }
                    break;
                case Vcut:
                    verifyValueCount(2);
                    if (getVCutSide() == BoardSide.Unknown) {
                        throw new Exception("Invalid side: " + toString());
                    }
                    break;
                case ArrayDrawing:
                case Drillmap:
                case FabricationDrawing:
                case Vcutmap:
                case Viafill:
                    verifyValueCount(1);
                    break;
                case Unknown:
                    verifyValueCount(1, Integer.MAX_VALUE);
                    throw new Exception("Unknown file function: " + toString());
                default:
                    throw new Exception("Problem validating: " + toString());
            }
        }
        catch (Exception e) {
            throw new GerberLayerFormatException(e.getMessage());
        }
    }
    
}
