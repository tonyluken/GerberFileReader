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
 * An enumeration of plot states, either Linear, Clockwise, or CounterClockwise
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-
 * specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=76">
 * Section 4.7 of the Gerber Layer Format Specification</a>
 */
public enum PlotState {
    Linear, Clockwise, CounterClockwise;
    
    /**
     * Returns the enumeration for the given Gcode
     * @param gCode - the Gcode
     * @return the enumeration
     * @throws Exception if the gCode is not valid
     */
    public static PlotState fromCommand(int gCode) throws Exception {
        if (gCode == 1) {
            return Linear;
        }
        else if (gCode == 2) {
            return Clockwise;
        }
        else if (gCode == 3) {
            return CounterClockwise;
        }
        else {
            throw new Exception("Unrecognized G code: " + gCode);
        }
    }
}
