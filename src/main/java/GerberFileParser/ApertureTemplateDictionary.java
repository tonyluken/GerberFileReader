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

import java.util.HashMap;

/**
 * A class for storing aperture templates so that they may be looked-up by name
 * 
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-
 * specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=21">
 * Section 2.10 of the Gerber Layer Format Specification</a>
 */
public class ApertureTemplateDictionary extends HashMap<String, ApertureTemplate> {
    private static final long serialVersionUID = 5811999852645242226L;

    /**
     * Constructs an aperture template dictionary pre-populated with the four standard aperture 
     * templates (Circle, Rectangle, Obround and Polygon)
     */
    ApertureTemplateDictionary() {
        put("C", new ApertureTemplate("C")); //Circle
        put("R", new ApertureTemplate("R")); //Rectangle
        put("O", new ApertureTemplate("O")); //Obround
        put("P", new ApertureTemplate("P")); //Polygon
    }
    
}
