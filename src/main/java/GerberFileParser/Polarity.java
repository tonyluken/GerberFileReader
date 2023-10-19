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
 * An enumeration of Gerber Polarities
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-
 * specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=87">
 * Section 4.9.2 of the Gerber Layer Format Specification</a>
 */
public enum Polarity {
    Dark, Clear;
    
    /**
     * Returns a Polarity enumeration based on the Gerber LP (Load Polarity) command string
     * @param command - the Gerber command string
     * @return the enumeration
     * @throws Exception if the command is invalid
     */
    public static Polarity fromCommand(String command) throws Exception {
        if (command.equals("LPD")) {
            return Dark;
        }
        else if (command.equals("LPC")) {
            return Clear;
        }
        else {
            throw new Exception("Unrecognized Polarity Command: " + command);
        }
    }
    
    /**
     * Returns a polarity that is opposite of the given Polarity
     * @param polarity - the Polarity to reverse
     * @return the reversed Polarity
     */
    public static Polarity reversed(Polarity polarity) {
        if (polarity == Dark) {
            return Clear;
        }
        else {
            return Dark;
        }
    }
    
    /**
     * Returns a Polarity that is the reverse of this Polarity if the parameter is true
     * @param b - the parameter
     * @return the Polarity that is the reverse of this Polarity if the parameter is true, otherwise
     * returns this Polarity
     */
    public Polarity reverse(boolean b) {
        if (b) {
            return reversed(this);
        }
        else {
            return this;
        }
    }
}
