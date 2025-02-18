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
 * An enumeration of Gerber attribute types - FILE, APERTURE, and OBJECT
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2024-05_en.pdf?94b45d8745c1a068fd091f095a26ddeb#page=122">
 * Section 5.1 of the Gerber Specification</a>
 */
public enum AttributeType {
    /**
     * Indicates the attribute is a Gerber File Attribute
     */
    FILE,
    /**
     * Indicates the attribute is a Gerber Aperture Attribute
     */
    APERTURE,
    /**
     * Indicates the attribute is a Gerber Object Attribute
     */
    OBJECT;
    
    /**
     * Returns the attribute type corresponding to the specified Gerber TF, TA, or TO command
     * @param cmd - the Gerber command
     * @return the attribute type
     * @throws GerberLayerFormatException if the command is not a TF, TA, or TO command
     * 
     * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2024-05_en.pdf?94b45d8745c1a068fd091f095a26ddeb#page=125">
     * Section 5.2, 5.3, and 5.4 of the Gerber Layer Format Specification</a>
     */
    protected static AttributeType from(String cmd) throws GerberLayerFormatException {
        switch (cmd.substring(0, 2)) {
            case "TF":
                return FILE;
            case "TA":
                return APERTURE;
            case "TO":
                return OBJECT;
            default :
                throw new GerberLayerFormatException("Unknown attribute type: " + cmd);
        }
    }
}
