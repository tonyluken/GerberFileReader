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
 * An enumeration of attribute types - File, Aperture, and Object
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-
 * specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=122">
 * Section 5.1 of the Gerber Specification</a>
 */
public enum AttributeType {
    File, Aperture, Object;
    
    /**
     * Returns the attribute type corresponding to the specified Gerber TF, TA, or TO command
     * @param cmd - the Gerber command
     * @return the attribute type
     * @throws Exception if the command is not a TF, TA, or TO command
     * 
     * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-
     * specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=124">
     * Section 5.2, 5.3, and 5.4 of the Gerber Layer Format Specification</a>
     */
    public static AttributeType from(String cmd) throws Exception {
        switch (cmd.substring(0, 2)) {
            case "TF":
                return File;
            case "TA":
                return Aperture;
            case "TO":
                return Object;
            default :
                throw new Exception("Unknown attribute type: " + cmd);
        }
    }
}
