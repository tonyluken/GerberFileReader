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
public class ApertureFunction extends StandardAttribute {
    /**
     * The string used in Gerber files as the name for Aperture Function Standard Attributes: 
     * {@value #GERBER_STANDARD_ATTRIBUTE_NAME}
     */
    public static final String GERBER_STANDARD_ATTRIBUTE_NAME = ".AperFunction";

    /**
     * Constructs an empty Aperture Function Standard Attribute, call 
     * {@link StandardAttribute#initialize(Attribute)} to initialize the contents of this Aperture 
     * Function Standard Attribute
     */
    public ApertureFunction() {
        super();
    }
    
    /**
     * Constructs an Aperture Function Standard Attribute from a Gerber TA extended 
     * command
     * 
     * @param cmd the Gerber command
     * 
     * @throws GerberLayerFormatException if the command does not properly define an Aperture 
     * Function Standard Attribute
     */
    public ApertureFunction(String cmd) throws GerberLayerFormatException {
        super(cmd);
        validate();
    }
    
    /**
     * An enumeration of possible aperture functions: {@link #ViaDrill}, {@link #BackDrill}, 
     * {@link #ComponentDrill}, {@link #MechanicalDrill}, {@link #CastellatedDrill}, 
     * {@link #OtherDrill}, {@link #ComponentPad}, {@link #SMDPad}, {@link #BGAPad}, 
     * {@link #ConnectorPad}, {@link #HeatsinkPad}, {@link #ViaPad}, {@link #TestPad}, 
     * {@link #CastellatedPad}, {@link #FiducialPad}, {@link #ThermalReliefPad}, {@link #WasherPad},
     * {@link #AntiPad}, {@link #OtherPad}, {@link #Conductor}, {@link #EtchedComponent},
     * {@link #NonConductor}, {@link #CopperBalancing}, {@link #Border}, {@link #OtherCopper}, 
     * {@link #ComponentMain}, {@link #ComponentOutline}, {@link #ComponentPin}, {@link #Profile}, 
     * {@link #NonMaterial}, {@link #Material}, {@link #Other}, and {@link #Unknown}
     */
    public enum Function {
        /**
         * Identifies via holes, plated holes whose sole function is to connect different layers, 
         * where there is no intention to insert component leads or mechanical objects.
         */
        ViaDrill,
        /**
         * Identifies holes used to remove plating over a sub-span by drilling that sub-span with a 
         * larger diameter.
         */
        BackDrill,
        /**
         * Identifies holes that are used for the attachment and/or electrical connection of 
         * component terminations, including pins and wires, to a printed board.
         */
        ComponentDrill,
        /**
         * Identifies holes with mechanical function (registration, screw, etc.) It applies to drill 
         * holes and rout slots.
         */
        MechanicalDrill,
        /**
         * Identifies plated holes cut through by the board edge; used to join PCBs.
         */
        CastellatedDrill,
        /**
         * Identifies holes with a function not otherwise specified by the Gerber Layer 
         * Format Specification.
         */
        OtherDrill,
        /**
         * Identifies pads associated with component holes. Only used for through-hole components.
         */
        ComponentPad,
        /**
         * Identifies pads belonging to the footprint of an SMD component, whether the corresponding 
         * lead is connected or not. Applies only for the normal electrical pads.
         */
        SMDPad,
        /**
         * Identifies pads belonging to the footprint of a BGA component, whether the corresponding 
         * lead is connected or not. Applies only for the normal electrical pads.
         */
        BGAPad,
        /**
         * Identifies edge connector pads. Only applicable for outer layers.
         */
        ConnectorPad,
        /**
         * Identifies heatsink or thermal pads.
         */
        HeatsinkPad,
        /**
         * Identifies via pads that provides a ring to attach the plating in the barrel. This is 
         * reserved for pads that have no other function than making the connection between layers.
         */
        ViaPad,
        /**
         * Identifies test pads. Only applicable for outer layers.
         */
        TestPad,
        /**
         * Identifies pads on plated holes cut through by the board edge.
         */
        CastellatedPad,
        /**
         * Identifies fiducial pads.
         */
        FiducialPad,
        /**
         * Identifies thermal relief pads electrically connected to the surrounding copper but in a
         * manner that restricts heat flow.
         */
        ThermalReliefPad,
        /**
         * Identifies pads around non-plated holes without electrical function. Typically for 
         * mechanical reenforcement.
         */
        WasherPad,
        /**
         * Identifies pads with clearing polarity (LPC) whose purpose is to create clearance in a 
         * plane. It makes room for a drill pass without connecting to the plane.
         */
        AntiPad,
        /**
         * Identifies pads with a function not otherwise specified in the Gerber Layer Format 
         * Specification
         */
        OtherPad,
        /**
         * Identifies copper whose function is to electrically connect pads or to provide shielding, 
         * typically tracks and copper pours such as power and ground planes.
         */
        Conductor,
        /**
         * Identifies etched components such a embedded inductors, transformers and capacitors. 
         */
        EtchedComponent,
        /**
         * Identifies copper that does not serve as a conductor, i.e., has no electrical function. 
         * Typically text in the PCB such as a part number and version.
         */
        NonConductor,
        /**
         * Identifies copper added to balance copper coverage for the plating process.
         */
        CopperBalancing,
        /**
         * Identifies copper used as the border of a production panel.
         */
        Border,
        /**
         * Identifies copper used for some purpose not otherwise specified in the Gerber Layer Format
         * Specification.
         */
        OtherCopper,
        /**
         * Identifies the the centroid of a component.
         */
        ComponentMain,
        /**
         * Identifies the outline of a component.
         */
        ComponentOutline,
        /**
         * Identifies a pin or lead location of a component.
         */
        ComponentPin,
        /**
         * Identifies the draws and arcs that exactly define the profile or outline of the PCB.
         */
        Profile,
        /**
         * Identifies objects that do not represent physical material but rather drawing elements.
         */
        NonMaterial,
        /**
         * Identifies objects that do represent physical material.
         */
        Material,
        /**
         * Identifies objects with a function that is not otherwise specified in the Gerber Layer
         * Format Specification.
         */
        Other,
        /**
         * This value is only used to indicate the Gerber file contains an invalid or unknown 
         * function.
         */
        Unknown;
        
        /**
         * Converts a string representation of the Function to the enumerated value of the Function
         * 
         * @param attributeValue the attribute value as a string
         * 
         * @return the Function as an enumeration
         */
        protected static Function fromAttributeValue(String attributeValue) {
            try {
                return Function.valueOf(attributeValue);
            }
            catch (Exception e) {
                return Unknown;
            }
        }
    }
    
    /**
     * Gets the function of the object to which this attribute is attached.
     * 
     * @return the object's function
     * 
     * @see #isViaDrill() 
     * @see #isBackDrill()
     * @see #isComponentDrill()
     * @see #isMechanicalDrill()
     * @see #isCastellatedDrill()
     * @see #isOtherDrill()
     * @see #isComponentPad()
     * @see #isSMDPad()
     * @see #isBGAPad()
     * @see #isConnectorPad()
     * @see #isHeatsinkPad()
     * @see #isViaPad()
     * @see #isTestPad()
     * @see #isCastellatedPad()
     * @see #isFiducialPad()
     * @see #isThermalReliefPad()
     * @see #isWasherPad()
     * @see #isAntiPad()
     * @see #isOtherPad()
     * @see #isConductor()
     * @see #isEtchedComponent()
     * @see #isNonConductor()
     * @see #isCopperBalancing()
     * @see #isProductionPanelBorder()
     * @see #isOtherCopper()
     * @see #isComponentMain()
     * @see #isComponentOutline()
     * @see #isComponentPin()
     * @see #isProfile()
     * @see #isNonMaterial()
     * @see #isMaterial()
     * @see #isOther()
     */
    public Function getFunction() {
        return Function.fromAttributeValue(getValues().get(0));
    }
    
    /**
     * Checks if this attribute is attached to an object that defines a via drill hole.
     * 
     * @return true if the object defines a via drill hole
     * 
     * @see #getFunction()
     * @see #hasViaIPC4761ProtectionLevel()
     * @see #getViaIPC4761ProtectionLevel()
     */
    public boolean isViaDrill() {
        return getFunction().equals(Function.ViaDrill);
    }
    
    /**
     * Checks if this attribute is attached to an object that defines a via drill hole and it 
     * has an IPC-4761 Protection Level specified. If so, the protection level can be obtained by 
     * calling {@link #getViaIPC4761ProtectionLevel()}.
     *  
     * @return true if the via drill hole has an IPC-4761 Protection Level specified
     * @throws UnsupportedOperationException if this attribute doesn't define via drill holes
     * @see #isViaDrill()
     */
    public boolean hasViaIPC4761ProtectionLevel() {
        if (!isViaDrill()) {
            throw new UnsupportedOperationException("Method hasViaIPC4761ProtectionLevel() is not available for " + toString());
        }
        return getValues().size() > 1;
    }
    
    /**
     * An enumeration of IPC-4761 Protection Levels: Ia, Ib, IIa, IIb, IIIa, IIIb, IVa, IVb, V, VI, 
     * VII, None, and Unknown
     */
    public enum ViaIPC4761ProtectionLevel {
        Ia, Ib, IIa, IIb, IIIa, IIIb, IVa, IVb, V, VI, VII, None, Unknown;
        
        protected static ViaIPC4761ProtectionLevel fromAttributeValue(String attributeValue) {
            try {
                return ViaIPC4761ProtectionLevel.valueOf(attributeValue);
            }
            catch (Exception e) {
                return Unknown;
            }
        }
    }
    
    /**
     * Gets the via's IPC-4761 Protection Level.
     * 
     * @return the IPC-4761 Protection Level if the via drill hole has a protection level 
     * specified; otherwise returns null
     * @throws UnsupportedOperationException if this attribute doesn't define via drill holes
     * @see #isViaDrill()
     * @see #hasViaIPC4761ProtectionLevel()
     */
    public ViaIPC4761ProtectionLevel getViaIPC4761ProtectionLevel() {
        if (!isViaDrill()) {
            throw new UnsupportedOperationException("Method getViaIPC4761ProtectionLevel() is not available for " + toString());
        }
        if (!hasViaIPC4761ProtectionLevel()) {
            return null;
        }
        return ViaIPC4761ProtectionLevel.fromAttributeValue(getValues().get(1));
    }
    
    /**
     * Checks if this attribute is attached to an object that defines a hole to remove 
     * via plating over a sub-span by drilling that sub-span with a larger diameter.
     * 
     * @return true if the hole is intended to remove via plating
     * @see #getFunction()
     */
    public boolean isBackDrill() {
        return getFunction().equals(Function.BackDrill);
    }
    
    /**
     * Checks if this attribute is attached to an object that defines a hole that is used 
     * for the attachment and/or electrical connection of component terminations, including pins and 
     * wires, to a printed board. The attribute applies to drill holes and rout slots.
     * 
     * @return true if the hole is used for the attachment and/or electrical connection of 
     * component terminations
     * @see #getFunction()
     * @see #isComponentDrillPressFit()
     */
    public boolean isComponentDrill() {
        return getFunction().equals(Function.ComponentDrill);
    }
    
    /**
     * Checks if this attribute is attached to an object that defines a hole for a press 
     * fit lead.  Press fit leads are pressed in properly sized plated-through holes to realize 
     * electrical contact.
     * 
     * @return true if the hole is for a press fit lead
     * @see #isComponentDrill()
     */
    public boolean isComponentDrillPressFit() {
        return isComponentDrill() && getValues().size() > 1 && 
                getValues().get(1).equals("PressFit");
    }
    
    /**
     * Checks if this attribute is attached to an object that defines a hole with mechanical 
     * function (registration, screw, etc.) It applies to drill holes and rout slots.
     * 
     * @return true if the hole has a mechanical function
     * @see #getFunction()
     * @see #hasMechanicalDrillPurpose()
     * @see #getMechanicalDrillPurpose()
     */
    public boolean isMechanicalDrill() {
        return getFunction().equals(Function.MechanicalDrill);
    }
    
    /**
     * An enumeration of possible mechanical drill purposes: {@link #Tooling}, {@link #BreakOut}, 
     * {@link #Other}, and {@link #Unknown}
     */
    public enum MechanicalDrillPurpose {
        /**
         * Holes placed on a PCB or a panel of PCBs for registration and hold-down purposes during 
         * the manufacturing process. Also called mounting holes. 
         */
        Tooling, 
        /**
         * Non-plated holes forming a break out tab used in break routing.
         */
        BreakOut, 
        /**
         * Holes for some other purpose.
         */
        Other,
        /**
         * This value is only used to indicate the Gerber file contains an invalid or unknown type
         */
        Unknown;
        
        protected static MechanicalDrillPurpose fromAttributeValue(String attributeValue) {
            try {
                return MechanicalDrillPurpose.valueOf(attributeValue);
            }
            catch (Exception e) {
                return Unknown;
            }
        }
    }
    
    /**
     * Checks if this attribute is attached to an object that defines a hole with mechanical 
     * function and that it has a mechanical drill purpose specified. If so, use 
     * {@link #getMechanicalDrillPurpose()} to get the specified mechanical drill purpose.
     * 
     * @return true if the hole has mechanical function and has a purpose specified
     * @throws UnsupportedOperationException if this attribute doesn't define mechanical drill holes
     * @see #isMechanicalDrill()
     */
    public boolean hasMechanicalDrillPurpose() {
        if (!isMechanicalDrill()) {
            throw new UnsupportedOperationException("Method hasMechanicalDrillPurpose() is not available for " + toString());
        }
        return getValues().size() > 1;
    }
    
    /**
     * Gets the mechanical drill hole's purpose.
     * 
     * @return the purpose of the mechanical drill hole if it has a purpose specified; otherwise 
     * returns null
     * @throws UnsupportedOperationException if this attribute doesn't define mechanical drill holes
     * @see #hasMechanicalDrillPurpose {@link #hasMechanicalDrillPurpose()}
     */
    public MechanicalDrillPurpose getMechanicalDrillPurpose() {
        if (!isMechanicalDrill()) {
            throw new UnsupportedOperationException("Method getMechanicalDrillPurpose() is not available for " + toString());
        }
        if (!hasMechanicalDrillPurpose()) {
            return null;
        }
        return MechanicalDrillPurpose.fromAttributeValue(getValues().get(1));
    }
    
    /**
     * Checks if this attribute is attached to an object that defines a plated hole cut 
     * through by the board edge. Typically used to join PCBs.
     * 
     * @return true if the plated hole is cut through by the board edge
     * @see #getFunction()
     */
    public boolean isCastellatedDrill() {
        return getFunction().equals(Function.CastellatedDrill);
    }
    
    /**
     * Checks if this attribute is attached to an object that defines a drill hole with a function
     * not otherwise specified by the Gerber Layer Format Specification. If so, use 
     * {@link #getOtherDrillFunction()} to get an informal description of the function of the hole.
     * 
     * @return true if the hole has a function not otherwise specified by the Gerber Layer Format 
     * Specification 
     * @see #getFunction() 
     */
    public boolean isOtherDrill() {
        return getFunction().equals(Function.OtherDrill);
    }
    
    /**
     * Gets an informal description of a hole's function that is not otherwise specified by the 
     * Gerber Layer Format Specification.
     * 
     * @return the informal description 
     * @throws UnsupportedOperationException if this attribute doesn't define an other drill 
     * function
     * @see #isOtherDrill()
     */
    public String getOtherDrillFunction() {
        if (!isOtherDrill()) {
            throw new UnsupportedOperationException("Method getOtherDrillFunction() is not available for " + toString());
        }
        return getValues().get(1);
    }
    
    /**
     * Checks if this attribute is attached to an object that defines a pad associated with 
     * a through-hole component.
     * 
     * @return true if this attribute defines a through-hole component pad
     * @see #getFunction()
     * @see #isSMDPad()
     * @see #isBGAPad()
     * @see #isConnectorPad()
     * @see #isHeatsinkPad()
     * @see #isViaPad()
     * @see #isTestPad()
     * @see #isCastellatedPad()
     * @see #isFiducialPad()
     * @see #isThermalReliefPad()
     * @see #isWasherPad()
     * @see #isAntiPad()
     * @see #isOtherPad()
     */
    public boolean isComponentPad() {
        return getFunction().equals(Function.ComponentPad);
    }
    
    /**
     * Checks if this attribute is attached to an object that defines a pad belonging to the 
     * footprint of an SMD component, whether the corresponding lead is connected or not. Applies 
     * only for the normal electrical pads.
     * 
     * @return true if this attribute defines an SMD pad
     * @see #getFunction()
     * @see #getPadDefinitionMethod()
     * @see #isComponentPad()
     * @see #isBGAPad()
     * @see #isConnectorPad()
     * @see #isHeatsinkPad()
     * @see #isViaPad()
     * @see #isTestPad()
     * @see #isCastellatedPad()
     * @see #isFiducialPad()
     * @see #isThermalReliefPad()
     * @see #isWasherPad()
     * @see #isAntiPad()
     * @see #isOtherPad()
     */
    public boolean isSMDPad() {
        return getFunction().equals(Function.SMDPad);
    }
    
    /**
     * An enumeration of possible SMD and BGA pad definition methods: {@link #CopperDefined}, 
     * {@link #SoldermaskDefined}, and {@link #Unknown}
     */
    public enum PadDefinitionMethod {
        /**
         * The copper pad is completely free of solder mask. The area to be covered by solder paste 
         * is defined by the copper pad. This is by far the most common SMD pad definition method. 
         */
        CopperDefined,
        /**
         * The solder mask overlaps the copper pad. The area to be covered by solder paste is 
         * defined by the solder mask opening and not by the copper pad.
         */
        SoldermaskDefined, 
        /**
         * This value is only used to indicate the Gerber file contains an invalid or unknown type
         */
        Unknown;
        
        protected static PadDefinitionMethod fromAttributeValue(String attributeValue) {
            switch (attributeValue) {
                case "CuDef" :
                    return CopperDefined;
                case "SMDef" :
                    return SoldermaskDefined;
                default :
                    return Unknown;
            }
        }
    }
    
    /**
     * Gets the pad definition method of an SMD or BGA pad.
     * 
     * @return the pad definition method
     * @throws UnsupportedOperationException if this attribute doesn't define an SMD pad or a BGA 
     * pad
     * @see #isSMDPad()
     * @see #isBGAPad()

     */
    public PadDefinitionMethod getPadDefinitionMethod() {
        if (!isSMDPad() && !isBGAPad()) {
            throw new UnsupportedOperationException("Method getPadDefinitionMethod() is not available for " + toString());
        }
        return PadDefinitionMethod.fromAttributeValue(getValues().get(1));
    }

    /**
     * Checks if a pad is {@link #isPadSoldermaskDefined()}.
     * 
     * @return true if the pad is soldermask defined
     * @see getPadDefinitionMethod()
     * @see isPadCopperDefined()
     */
    public boolean isPadSoldermaskDefined() {
        return (isSMDPad() || isBGAPad()) && 
                getPadDefinitionMethod() == PadDefinitionMethod.SoldermaskDefined;
    }
    
    /**
     * Checks if a pad is {@link #isPadCopperDefined()}.
     * 
     * @return true if the pad is copper defined
     * @see getPadDefinitionMethod()
     * @see isPadSoldermaskDefined()
     */
    public boolean isPadCopperDefined() {
        return (isSMDPad() || isBGAPad()) && 
                getPadDefinitionMethod() == PadDefinitionMethod.CopperDefined;
    }
    
    /**
     * Checks if this attribute is attached to an object that defines a pad belonging to the 
     * footprint of a BGA component, whether the corresponding lead is connected or not. Applies 
     * only for the normal electrical pads.
     * 
     * @return true if this attribute defines a BGA pad
     * @see #getFunction()
     * @see #getPadDefinitionMethod()
     * @see #isComponentPad()
     * @see #isSMDPad()
     * @see #isConnectorPad()
     * @see #isHeatsinkPad()
     * @see #isViaPad()
     * @see #isTestPad()
     * @see #isCastellatedPad()
     * @see #isFiducialPad()
     * @see #isThermalReliefPad()
     * @see #isWasherPad()
     * @see #isAntiPad()
     * @see #isOtherPad()
     */
    public boolean isBGAPad() {
        return getFunction().equals(Function.BGAPad);
    }
    
    /**
     * Checks if this attribute is attached to an object that defines an edge connector pad. Only 
     * applicable for outer layers.
     * 
     * @return true if this attribute defines an edge connector pad
     * @see #getFunction()
     * @see #isComponentPad()
     * @see #isSMDPad()
     * @see #isBGAPad()
     * @see #isHeatsinkPad()
     * @see #isViaPad()
     * @see #isTestPad()
     * @see #isCastellatedPad()
     * @see #isFiducialPad()
     * @see #isThermalReliefPad()
     * @see #isWasherPad()
     * @see #isAntiPad()
     * @see #isOtherPad()
     */
    public boolean isConnectorPad() {
        return getFunction().equals(Function.ConnectorPad);
    }

    /**
     * Checks if this attribute is attached to an object that defines a heatsink or thermal 
     * pad, typically for SMDs.
     * 
     * @return true if this attribute defines a heatsink pad
     * @see #getFunction()
     * @see #isComponentPad()
     * @see #isSMDPad()
     * @see #isBGAPad()
     * @see #isConnectorPad()
     * @see #isViaPad()
     * @see #isTestPad()
     * @see #isCastellatedPad()
     * @see #isFiducialPad()
     * @see #isThermalReliefPad()
     * @see #isWasherPad()
     * @see #isAntiPad()
     * @see #isOtherPad()
     */
    public boolean isHeatsinkPad() {
        return getFunction().equals(Function.HeatsinkPad);
    }
    
    /**
     * Checks if this attribute is attached to an object that defines a via pad that provides a 
     * ring to attach the plating in a via barrel. This is reserved for pads that have no other 
     * function than making the connection between layers.
     * 
     * @return true if this attribute is attached to a via pad
     * @see #getFunction()
     * @see #isComponentPad()
     * @see #isSMDPad()
     * @see #isBGAPad()
     * @see #isConnectorPad()
     * @see #isHeatsinkPad()
     * @see #isTestPad()
     * @see #isCastellatedPad()
     * @see #isFiducialPad()
     * @see #isThermalReliefPad()
     * @see #isWasherPad()
     * @see #isAntiPad()
     * @see #isOtherPad()
     */
    public boolean isViaPad() {
        return getFunction().equals(Function.ViaPad);
    }
    
    /**
     * Checks if this attribute is attached to an object that defines a test pad. Only applicable 
     * for outer copper layers.
     * 
     * @return true if the attribute defines a test pad
     * @see #getFunction()
     * @see #isComponentPad()
     * @see #isSMDPad()
     * @see #isBGAPad()
     * @see #isConnectorPad()
     * @see #isHeatsinkPad()
     * @see #isViaPad()
     * @see #isCastellatedPad()
     * @see #isFiducialPad()
     * @see #isThermalReliefPad()
     * @see #isWasherPad()
     * @see #isAntiPad()
     * @see #isOtherPad()
     */
    public boolean isTestPad() {
        return getFunction().equals(Function.TestPad);
    }

    /**
     * Checks if this attribute is attached to an object that defines a pad for a plated hole 
     * cut through by the board edge. Typically used to join PCBs.
     * 
     * @return true if this attribute defines a pad for a plated hole cut through by the board edge
     * @see #getFunction()
     * @see #isComponentPad()
     * @see #isSMDPad()
     * @see #isBGAPad()
     * @see #isConnectorPad()
     * @see #isHeatsinkPad()
     * @see #isViaPad()
     * @see #isTestPad()
     * @see #isFiducialPad()
     * @see #isThermalReliefPad()
     * @see #isWasherPad()
     * @see #isAntiPad()
     * @see #isOtherPad()
     */
    public boolean isCastellatedPad() {
        return getFunction().equals(Function.CastellatedPad);
    }
    
    /**
     * Checks if this attribute is attached to an object that defines a fiducial pad. If so, 
     * {@link #getFiducialType()} returns the fiducial's type.
     * 
     * @return true if this attribute defines a fiducial pad.
     * @see #getFunction()
     * @see #isComponentPad()
     * @see #isSMDPad()
     * @see #isBGAPad()
     * @see #isConnectorPad()
     * @see #isHeatsinkPad()
     * @see #isViaPad()
     * @see #isTestPad()
     * @see #isCastellatedPad()
     * @see #isThermalReliefPad()
     * @see #isWasherPad()
     * @see #isAntiPad()
     * @see #isOtherPad()
     */
    public boolean isFiducialPad() {
        return getFunction().equals(Function.FiducialPad);
    }
    
    /**
     * An enumeration of possible fiducial types: {@link #Local}, {@link #Global}, {@link Panel}, and
     * {@link #Unknown} 
     */
    public enum FiducialType {
        /**
         * Fiducials used to locate the position of an individual component on a PCB
         */
        Local,
        /**
         * Fiducials used to locate a PCB on the assembly panel
         */
        Global,
        /**
         * Fiducials used to locate an assembly panel on the fabrication panel
         */
        Panel,
        /**
         * This value is only used to indicate the Gerber file contains an invalid or unknown type
         */
        Unknown;
        
        protected static FiducialType fromAttributeValue(String attributeValue) {
            switch (attributeValue) {
                case "Local" :
                    return Local;
                case "Global" :
                    return Global;
                case "Panel" :
                    return Panel;
                default :
                    return Unknown;
            }
        }
    }
    
    /**
     * Gets the fiducial's type.
     * 
     * @return the fiducial's type
     * @throws UnsupportedOperationException if this attribute doesn't define a fiducial pad
     * @see #isFiducialPad()
     */
    public FiducialType getFiducialType() {
        if (!isFiducialPad()) {
            throw new UnsupportedOperationException("Method getFiducialType() is not available for " + toString());
        }
        return FiducialType.fromAttributeValue(getValues().get(1));
    }
    
    /**
     * Checks if this attribute is attached to an object that defines a thermal relief pad, that is,
     * a pad electrically connected to the surrounding copper but in a manner that restricts heat 
     * flow.
     * 
     * @return true if this attribute defines a thermal relief pad
     * @see #getFunction()
     * @see #isComponentPad()
     * @see #isSMDPad()
     * @see #isBGAPad()
     * @see #isConnectorPad()
     * @see #isHeatsinkPad()
     * @see #isViaPad()
     * @see #isTestPad()
     * @see #isCastellatedPad()
     * @see #isFiducialPad()
     * @see #isWasherPad()
     * @see #isAntiPad()
     * @see #isOtherPad()
     */
    public boolean isThermalReliefPad() {
        return getFunction().equals(Function.ThermalReliefPad);
    }
    
    /**
     * Checks if this attribute is attached to an object that defines a pad around a non-plated 
     * hole without electrical function. Typically for mechanical reinforcement, hence the term 
     * washer pad.
     * 
     * @return true if this attribute defines a washer pad
     * @see #getFunction()
     * @see #isComponentPad()
     * @see #isSMDPad()
     * @see #isBGAPad()
     * @see #isConnectorPad()
     * @see #isHeatsinkPad()
     * @see #isViaPad()
     * @see #isTestPad()
     * @see #isCastellatedPad()
     * @see #isFiducialPad()
     * @see #isThermalReliefPad()
     * @see #isAntiPad()
     * @see #isOtherPad()
     */
    public boolean isWasherPad() {
        return getFunction().equals(Function.WasherPad);
    }
    
    /**
     * Checks if this attribute is attached to an object that defines a pad with clearing 
     * polarity (LPC) for the purpose of creating a clearance in a plane. It makes room for a drill 
     * pass without connecting to the plane.
     * 
     * @return true if this attribute defines an anti-pad
     * @see #getFunction()
     * @see #isComponentPad()
     * @see #isSMDPad()
     * @see #isBGAPad()
     * @see #isConnectorPad()
     * @see #isHeatsinkPad()
     * @see #isViaPad()
     * @see #isTestPad()
     * @see #isCastellatedPad()
     * @see #isFiducialPad()
     * @see #isThermalReliefPad()
     * @see #isWasherPad()
     * @see #isOtherPad()
     */
    public boolean isAntiPad() {
        return getFunction().equals(Function.AntiPad);
    }

    /**
     * Checks if this attribute is attached to an object that defines a pad with a function not 
     * otherwise specified by the Gerber Layer Format Specification. If so, 
     * {@link #getOtherPadFunction()} returns an informal description of the function of the pad.
     * 
     * @return returns true if this attribute defines a pad with an otherwise undefined function
     * @see #getFunction()
     * @see #isComponentPad()
     * @see #isSMDPad()
     * @see #isBGAPad()
     * @see #isConnectorPad()
     * @see #isHeatsinkPad()
     * @see #isViaPad()
     * @see #isTestPad()
     * @see #isCastellatedPad()
     * @see #isFiducialPad()
     * @see #isThermalReliefPad()
     * @see #isWasherPad()
     * @see #isAntiPad()
     */
    public boolean isOtherPad() {
        return getFunction().equals(Function.OtherPad);
    }
    
    /**
     * Gets an informal description of the function of the pad.
     * 
     * @return the informal description
     * @throws UnsupportedOperationException if this attribute doesn't define an other function pad
     * @see #isOtherPad()
     */
    public String getOtherPadFunction() {
        if (!isOtherPad()) {
            throw new UnsupportedOperationException("Method getOtherPadFunction() is not available for " + toString());
        }
        return getValues().get(1);
    }
    
    /**
     * Checks if this attribute is attached to an object that defines copper whose function is to 
     * electrically connect pads or to provide shielding, typically tracks and copper pours such as 
     * power and ground planes.
     * 
     * @return true if this attribute defines copper whose function is electrical conduction
     * @see #getFunction()
     * @see #isNonConductor()
     */
    public boolean isConductor() {
        return getFunction().equals(Function.Conductor);
    }
    
    /**
     * Checks if this attribute is attached to an object that defines an embedded inductor, 
     * transformer, capacitor, or other component etched into the PCB copper.
     * 
     * @return true if this attribute defines an etched component
     * @see #getFunction()
     */
    public boolean isEtchedComponent() {
        return getFunction().equals(Function.EtchedComponent);
    }
    
    /**
     * Checks if this attribute is attached to an object that defines copper that does not serve as 
     * a conductor and has no electrical function; typically text in the PCB such as a part number 
     * and version.
     * 
     * @return true if the attribute defines copper that has no electrical function
     * @see #getFunction()
     * @see #isConductor()
     */
    public boolean isNonConductor() {
        return getFunction().equals(Function.NonConductor);
    }
    
    /**
     * Checks if this attribute is attached to an object that defines copper whose purpose is 
     * to balance copper coverage for the plating process.
     * 
     * @return true if this attribute defines copper to balance the plating process
     * @see #getFunction()
     */
    public boolean isCopperBalancing() {
        return getFunction().equals(Function.CopperBalancing);
    }
    
    /**
     * Checks if this attribute is attached to an object that defines the copper border of a 
     * production panel.
     * 
     * @return true if this attribute defines the copper border of a production panel
     * @see #getFunction()
     */
    public boolean isProductionPanelBorder() {
        return getFunction().equals(Function.Border);
    }
    
    /**
     * Checks if this attribute is attached to an object that defines copper whose function is not 
     * otherwise specified by the Gerber Layer Format Specification. If so, 
     * {@link #getOtherCopperFunction()} returns an informal description of that copper's other 
     * function.
     * 
     * @return true if this attribute defines copper whose function is not otherwise specified
     * @see #getFunction()
     */
    public boolean isOtherCopper() {
        return getFunction().equals(Function.OtherCopper);
    }
    
    /**
     * Gets an informal description of the function of the copper whose function is not otherwise 
     * specified by the Gerber Layer Format Specification.
     * 
     * @return the informal description
     * @throws UnsupportedOperationException if this attribute doesn't define copper with an other
     * function
     * @see #isOtherCopper()
     */
    public String getOtherCopperFunction() {
        if (!isOtherCopper()) {
            throw new UnsupportedOperationException("Method getOtherCopperFunction() is not available for " + toString());
        }
        return getValues().get(1);
    }
    
    /**
     * Checks if this attribute is attached to an object whose purpose is to identify the centroid 
     * of a component. The object must also have a {@link ComponentReferenceDesignator} Standard 
     * Attribute attached to it to identify the component's reference designator.
     * 
     * @return true if this attribute identifies the centroid of a component
     * @see #getFunction()
     * @see ComponentReferenceDesignator#getReferenceDesignator()
     */
    public boolean isComponentMain() {
        return getFunction().equals(Function.ComponentMain);
    }
    
    /**
     * Checks if this attribute is attached to an object whose purpose is to identify the 
     * outline of a component. If so, {@link #getComponentOutlineType()} returns the outline type.
     * 
     * @return true if this attribute identifies the outline of a component
     * @see #getFunction()
     */
    public boolean isComponentOutline() {
        return getFunction().equals(Function.ComponentOutline);
    }
    
    /**
     * An enumeration of the different possible component outlines: Body, LeadToLead, Footprint,
     * Courtyard, and Unknown. See the Gerber Layer Format Specification, section 5.6.10 for their 
     * definitions.
     */
    public enum ComponentOutlineType {
        Body,
        LeadToLead,
        Footprint,
        Courtyard,
        Unknown;
        
        protected static ComponentOutlineType fromAttributeValue(String attributeValue) {
            switch (attributeValue) {
                case "Body" :
                    return Body;
                case "Lead2Lead" :
                    return LeadToLead;
                case "Footprint" :
                    return Footprint;
                case "Courtyard" :
                    return Courtyard;
                default :
                    return Unknown;
            }
        }
    }
    
    /**
     * Gets the component outline type.
     * 
     * @return the outline type
     * @throws UnsupportedOperationException if this attribute doesn't identify the outline of a
     * component
     * @see #isComponentOutline()
     */
    public ComponentOutlineType getComponentOutlineType() {
        if (!isComponentOutline()) {
            throw new UnsupportedOperationException("Method getComponentOutlineType() is not available for " + toString());
        }
        return ComponentOutlineType.fromAttributeValue(getValues().get(1));
    }
    
    /**
     * Checks if this attribute is attached to an object that identifies a component pin 
     * location.
     * 
     * @return true if this attribute identifies a component pin location
     * @see #getFunction()
     */
    public boolean isComponentPin() {
        return getFunction().equals(Function.ComponentPin);
    }
    
    /**
     * Checks if this attribute is attached to an object that identifies the PCB's profile or
     * outline.
     * 
     * @return true if this attribute identifies the PCB's profile
     * @see #getFunction()
     */
    public boolean isProfile() {
        return getFunction().equals(Function.Profile);
    }
    
    /**
     * Checks if this attribute is attached to an object that does not represent physical material 
     * but rather drawing elements.
     * 
     * @return true if this attribute does not represent physical material
     * @see #getFunction()
     */
    public boolean isNonMaterial() {
        return getFunction().equals(Function.NonMaterial);
    }
    
    /**
     * Checks if this attribute is attached to an object that represents physical material.
     * 
     * @return true if this attribute represents physical material.
     * @see #getFunction()
     */
    public boolean isMaterial() {
        return getFunction().equals(Function.Material);
    }
    
    /**
     * Checks if this attribute is attached to an object that has some function not otherwise 
     * specified in the Gerber Layer Format Specification. If so, {@link #getOtherFunction()} 
     * returns an informal description of that function.
     * 
     * @return true if this attribute defines some function not otherwise specified
     * @see #getFunction()
     */
    public boolean isOther() {
        return getFunction().equals(Function.Other);
    }
    
    /**
     * Gets an informal description of the object's function.
     * 
     * @return the informal description
     * @throws UnsupportedOperationException if this attribute doesn't define an other function
     * @see #isOther {@link #isOther()}
     */
    public String getOtherFunction() {
        if (!isOther()) {
            throw new UnsupportedOperationException("Method getOtherFunction() is not available for " + toString());
        }
        return getValues().get(1);
    }

    @Override
    public String getGerberStandardAttributeName() {
        return GERBER_STANDARD_ATTRIBUTE_NAME;
    }

    @Override
    public void validate() throws GerberLayerFormatException {
        try {
            if (getValues().size() == 0) {
                throw new Exception("No aperture function specified: " + toString());
            }
            switch (getFunction()) {
                case AntiPad:
                case CastellatedPad:
                case ComponentPad:
                case ConnectorPad:
                case HeatsinkPad:
                case TestPad:
                case ThermalReliefPad:
                case ViaPad:
                case WasherPad:
                    verifyValueCount(1);
                    break;

                case FiducialPad:
                    verifyValueCount(2);
                    if (getFiducialType() == FiducialType.Unknown) {
                        throw new Exception("Invalid fiducial type: " + toString());
                    }
                    break;
                    
                case OtherPad:
                    verifyValueCount(2);
                    break;
                    
                case BGAPad:
                case SMDPad:
                    verifyValueCount(2);
                    if (getPadDefinitionMethod() == PadDefinitionMethod.Unknown) {
                        throw new Exception("Invalid pad definition method: " + toString());
                    }
                    break;
                    
                case BackDrill:
                case CastellatedDrill:
                    verifyValueCount(1);
                    break;
                    
                case ComponentDrill:
                    verifyValueCount(1, 2);
                    if (getValues().size() == 2 && !isComponentDrillPressFit()) {
                        throw new Exception("Invalid component drill modifier: " + toString());
                    }
                    break;
                case MechanicalDrill:
                    verifyValueCount(1, 2);
                    if (this.hasMechanicalDrillPurpose() && this.getMechanicalDrillPurpose() == MechanicalDrillPurpose.Unknown) {
                        throw new Exception("Invalid mechanical drill purpose: " + toString());
                    }
                    break;
                case OtherDrill:
                    verifyValueCount(1, 2);
                    break;
                case ViaDrill:
                    verifyValueCount(1, 2);
                    if (this.hasViaIPC4761ProtectionLevel() && this.getViaIPC4761ProtectionLevel() == ViaIPC4761ProtectionLevel.Unknown) {
                        throw new Exception("Invalid via drill protection level: " + toString());
                    }
                    break;
                    
                case Border:
                case ComponentMain:
                case ComponentPin:
                case Conductor:
                case CopperBalancing:
                case EtchedComponent:
                case Material:
                case NonConductor:
                case NonMaterial:
                case Profile:
                    verifyValueCount(1);
                    break;
                    
                case ComponentOutline:
                    verifyValueCount(2);
                    if (this.getComponentOutlineType() == ComponentOutlineType.Unknown) {
                        throw new Exception("Invalid component outline type: " + toString());
                    }
                    break;
                    
                case Other:
                case OtherCopper:
                    verifyValueCount(2);
                    break;
                    
                case Unknown:
                    verifyValueCount(1, Integer.MAX_VALUE);
                    throw new Exception("Unknown aperture function: " + toString());
                default:
                    throw new Exception("Problem validating: " + toString());
            }
        }
        catch (Exception e) {
            throw new GerberLayerFormatException(e.getMessage());
        }
    }
}
