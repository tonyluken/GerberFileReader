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
 * Enumeration of aperture types. Note that per the Gerber spec, STEP_AND_REPEAT is not considered 
 * an aperture but it in included here because we treat it basically the same as a block aperture.
 * 
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2024-05_en.pdf?94b45d8745c1a068fd091f095a26ddeb#page=51">
 * Section 4.4 of the Gerber Layer Format Specification</a>
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2024-05_en.pdf?94b45d8745c1a068fd091f095a26ddeb#page=57">
 * Section 4.5 of the Gerber Layer Format Specification</a>
 */
enum ApertureType {
    /**
     * A standard Gerber aperture consisting of a circular area
     */
    CIRCLE,
    /**
     * A standard Gerber aperture consisting of a rectangular area
     */
    RECTANGLE,
    /**
     * A standard Gerber aperture consisting of a rectangular area with two semi-circular end caps
     */
    OBROUND,
    /**
     * A standard Gerber regular aperture consisting of a regular polygon
     */
    POLYGON,
    /**
     * A Gerber Macro defined aperture
     */
    MACRO,
    /**
     * A Gerber Block aperture
     */
    BLOCK,
    /**
     * A Gerber Step and Repeat Block aperture
     */
    STEP_AND_REPEAT;
}
