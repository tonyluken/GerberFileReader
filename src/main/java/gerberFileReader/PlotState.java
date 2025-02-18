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
 * An enumeration of plot states, either LINEAR, CLOCKWISE, or COUNTERCLOCKWISE
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2024-05_en.pdf?94b45d8745c1a068fd091f095a26ddeb#page=76">
 * Section 4.7 of the Gerber Layer Format Specification</a>
 */
enum PlotState {
    LINEAR, CLOCKWISE, COUNTERCLOCKWISE;
    
    /**
     * Returns the enumeration for the given G code
     * @param gCode - the G code
     * @return the enumeration
     * @throws GerberLayerFormatException if the G code is not valid
     */
    public static PlotState fromCommand(int gCode) throws GerberLayerFormatException {
        if (gCode == 1) {
            return LINEAR;
        }
        else if (gCode == 2) {
            return CLOCKWISE;
        }
        else if (gCode == 3) {
            return COUNTERCLOCKWISE;
        }
        else {
            throw new GerberLayerFormatException("Unrecognized G code: " + gCode);
        }
    }
}
