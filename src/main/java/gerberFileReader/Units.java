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

package gerberFileReader;

/**
 * An enumeration of Gerber coordinate units, either INCHES or MILLIMETERS. Also provides 
 * conversions from one to the other.
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2024-05_en.pdf?94b45d8745c1a068fd091f095a26ddeb#page=47">
 * Section 4.2.1 of the Gerber Format Layer Specification</a>
 */
public enum Units {
    /**
     * The imperial length unit equivalent to 25.4 millimeters
     */
    INCHES,
    /**
     * The SI length unit equivalent to 0.001 meters
     */
    MILLIMETERS;
    
    public static final double MILLIMETERS_PER_INCH = 25.4;
    
    /**
     * Returns the enumeration corresponding to the Gerber MO command
     * 
     * @param modeCommand - the MO command string
     * @return the enumeration
     * @throws GerberLayerFormatException if the command is not valid
     */
    protected static Units fromCommand(String modeCommand) throws GerberLayerFormatException {
        if (modeCommand.startsWith("MOM")) {
            return MILLIMETERS;
        }
        else if (modeCommand.startsWith("MOI")) {
            return INCHES;
        }
        else {
            throw new GerberLayerFormatException("Invalid mode command: " + modeCommand);
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
