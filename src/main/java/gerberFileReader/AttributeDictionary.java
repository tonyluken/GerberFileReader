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

/**
 * A class to hold a mapping of attribute names to their corresponding attributes
 * 
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2024-05_en.pdf?94b45d8745c1a068fd091f095a26ddeb#page=22">
 * Section 2.10 of the Gerber Layer Format Specification</a>
 */
public class AttributeDictionary extends HashMap<String, Attribute>{
    private static final long serialVersionUID = 7235779159160265207L;

    /**
     * Returns a new AttributeDictionary that contains all of the attributes of 
     * the specified type that are currently held in this dictionary
     * 
     * @param type - the type of attributes to get from this dictionary
     * @return the new dictionary
     */
    protected AttributeDictionary getAllOf(AttributeType type) {
        AttributeDictionary ret = new AttributeDictionary();
        for (String key : keySet()) {
            Attribute attribute = get(key);
            if (attribute.getType() == type) {
                ret.put(attribute.getName(), attribute);
            }
        }
        return ret;
    }
    
}
