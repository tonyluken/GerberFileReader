/*
 * Copyright (C) 2023 Tony Luken <tonyluken62+gerberfileparser.gmail.com>
 * 
 * This file is part of GerberFileParser.
 * 
 * GerberFileParser is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of 
 * the License, or (at your option) any later version.
 * 
 * GerberFileParser is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with GerberFileParser. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package GerberFileParser;

/**
 * An enumeration of Gerber length units, either inch or millimeter. Also provides conversions from
 * one to the other.
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-
 * specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=46">
 * Section 4.2.1 of the Gerber Format Layer Specification</a>
 */
public enum Unit {
    inch, millimeter;
    
    public static final double MILLIMETERS_PER_INCH = 25.4;
    
    /**
     * Returns the enumeration corresponding to the Gerber MO command
     * 
     * @param modeCommand - the MO command string
     * @return the enumeration
     * @throws Exception if the command is not valid
     */
    public static Unit fromCommand(String modeCommand) throws Exception {
        if (modeCommand.startsWith("MOM")) {
            return millimeter;
        }
        else if (modeCommand.startsWith("MOI")) {
            return inch;
        }
        else {
            throw new Exception("Invalid mode command: " + modeCommand);
        }
    }
    
    /**
     * Converts a value with units of millimeters to a value with units of inches
     * @param millimeters - the value in millimeters
     * @return the value in inches
     */
    public static double toInches(double millimeters) {
        return millimeters / MILLIMETERS_PER_INCH;
    }
    
    /**
     * Converts a value with units of inches to a value with units of millimeters
     * @param inches - the value in inches
     * @return the value in millimeters
     */
    public static double toMillimeters(double inches) {
        return inches * MILLIMETERS_PER_INCH;
    }
}
