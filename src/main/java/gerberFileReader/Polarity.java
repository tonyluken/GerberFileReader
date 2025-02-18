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
 * An enumeration of Gerber Polarities, either DARK meaning opaque or CLEAR meaning transparent
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2024-05_en.pdf?94b45d8745c1a068fd091f095a26ddeb#page=87">
 * Section 4.9.2 of the Gerber Layer Format Specification</a>
 */
public enum Polarity {
    /**
     * Indicates the object is opaque and will cover any objects beneath it
     */
    DARK,
    /**
     * Indicates the object is transparent and will erase any objects beneath it 
     */
    CLEAR;
    
    /**
     * Returns a Polarity enumeration based on the Gerber LP (Load Polarity) command string
     * @param command - the Gerber command string
     * @return the enumeration
     * @throws GerberLayerFormatException if the command is invalid
     */
    protected static Polarity fromCommand(String command) throws GerberLayerFormatException {
        if (command.equals("LPD")) {
            return DARK;
        }
        else if (command.equals("LPC")) {
            return CLEAR;
        }
        else {
            throw new GerberLayerFormatException("Unrecognized Polarity Command: " + command);
        }
    }
    
    /**
     * Returns a polarity that is opposite of the given Polarity
     * @param polarity - the Polarity to reverse
     * @return the reversed Polarity
     */
    protected static Polarity reverse(Polarity polarity) {
        if (polarity == DARK) {
            return CLEAR;
        }
        else {
            return DARK;
        }
    }
    
    /**
     * Returns a Polarity that is the reverse of this Polarity if the parameter is true
     * @param b - the parameter
     * @return the Polarity that is the reverse of this Polarity if the parameter is true, otherwise
     * returns this Polarity
     */
    protected Polarity reverse(boolean b) {
        if (b) {
            return reverse(this);
        }
        else {
            return this;
        }
    }
}
