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

import java.util.HashMap;
import java.util.Map;

/**
 * A class for storing Gerber apertures so that they may be looked-up by their Id.
 * 
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-
 * specification-revision-2024-05_en.pdf?94b45d8745c1a068fd091f095a26ddeb#page=22">
 * Section 2.10 of the Gerber Layer Format Specification</a>
 */
class ApertureDictionary {
    private Map<String, Aperture> dictionary;
    
    /**
     * Constructs an empty aperture dictionary
     */
    ApertureDictionary() {
        dictionary = new HashMap<>();
    }
    
    /**
     * Gets an aperture from the dictionary given its id
     * @param id - the id of the aperture to get
     * @return the aperture with the given id or null if no aperture with that id exists within the
     * dictionary
     */
    public Aperture get(String id) {
        return dictionary.get(id);
    }
    
    /**
     * Puts an aperture into the dictionary
     * @param id - the id of the aperture
     * @param aperture - the aperture to put into the dictionary
     * @return the previous aperture with this id or null if the id didn't previously exist within
     * the dictionary 
     */
    public Aperture put(String id, Aperture aperture) {
        return dictionary.put(id, aperture);
    }
}
