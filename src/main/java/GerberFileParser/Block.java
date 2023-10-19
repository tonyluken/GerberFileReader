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
 * A class to represent a Gerber Aperture Block
 * 
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-
 * specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=14">
 * Section 2.4 of the Gerber Layer Format Specification</a>
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-
 * specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=111">
 * Section 4.11 of the Gerber Layer Format Specification</a>
 */
public class Block extends Aperture {
    protected AttributeDictionary apertureAttributes;
    
    /**
     * Constructs an empty aperture block
     */
    Block() {
        
    }
    
    /**
     * Parses an opening Aperture Block command and constructs an empty aperture block with the Id
     * specified by the command. The graphical objects created during the subsequent command stream
     * (up to the matching closing Aperture Block command) should be added to the block using its 
     * put method. 
     * 
     * @param command - an opening Aperture Block command, such as: "ABD12"
     * @throws Exception if the command passed does not begin with "AB" or if the aperture id is not 
     * valid
     * 
     * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-
     * specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=111">
     * Section 4.11 of the Gerber Specification</a>
     */
    Block(String command, AttributeDictionary attributeDictionary) throws Exception {
        String cmd = command.substring(0, 2);
        if (!cmd.equals("AB")) {
            throw new Exception("Aperture block must be defined with an AB command but found: " + command);
        }
        type = ApertureType.Block;
        int ne = Utils.numberEndIndex(command, 3);
        if (command.charAt(2) != 'D' || ne < 0) {
            throw new Exception("Aperture id not valid: " + command);
        }
        id = command.substring(3, ne);
        apertureAttributes = attributeDictionary.getAllOf(AttributeType.Aperture);
    }

    /**
     * Closes the block definition
     */
    public void close() {
        for (GraphicalObject go : getStream()) {
            go.getAttributes().putAll(apertureAttributes);
        }
    }
}
