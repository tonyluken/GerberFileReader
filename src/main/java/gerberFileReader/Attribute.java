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

import java.util.ArrayList;
import java.util.List;

/**
 * A class to hold Gerber attributes
 * 
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2024-05_en.pdf?94b45d8745c1a068fd091f095a26ddeb#page=122">
 * Section 5 of the Gerber Layer Format Specification</a>
 */
public class Attribute {
    protected AttributeType type;
    protected String name;
    
    protected List<String> values = new ArrayList<>();
    
    /**
     * Constructs an attribute from the given Gerber TF, TA, or TO command
     * @param cmd - the Gerber command string
     * @throws GerberLayerFormatException if the given command is not a valid TF, TA, or TO command
     * 
     * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2024-05_en.pdf?94b45d8745c1a068fd091f095a26ddeb#page=125">
     * Section 5.2 of the Gerber Specification</a>
     * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2024-05_en.pdf?94b45d8745c1a068fd091f095a26ddeb#page=125">
     * Section 5.3 of the Gerber Specification</a>
     * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2024-05_en.pdf?94b45d8745c1a068fd091f095a26ddeb#page=126">
     * Section 5.4 of the Gerber Specification</a>
     */
    protected Attribute(String cmd) throws GerberLayerFormatException {
        type = AttributeType.from(cmd);
        cmd = cmd.substring(2);
        int idx = cmd.indexOf(',');
        if (idx < 0) {
            idx = cmd.length();
        }
        name = cmd.substring(0, idx);
        if (name.equals("")) {
            throw new GerberLayerFormatException("Attribute must have a valid name.");
        }
        if (idx < cmd.length()) {
            cmd = cmd.substring(idx+1);
        }
        else {
            cmd = "";
        }
        while (cmd.length() > 0) {
            idx = cmd.indexOf(',');
            if (idx < 0) {
                idx = cmd.length();
            }
            values.add(cmd.substring(0, idx));
            if (idx < cmd.length()) {
                cmd = cmd.substring(idx+1);
            }
            else {
                cmd = "";
            }
        }
    }

    public Attribute() {
    }

    /**
     * Gets the type of this attribute, either FILE, APERTURE, or OBJECT
     * @return the type of this attribute
     */
    public AttributeType getType() {
        return type;
    }

    /**
     * Gets the name of this attribute
     * @return the name of this attribute
     */
    public String getName() {
        return name;
    }

    /**
     * Gets this attribute's value(s)
     * @return a list of this attribute's value(s)
     */
    public List<String> getValues() {
        return values;
    }

    /**
     * Returns this attribute as a string with the format (type=attribute type, name=attribute name, 
     * values={list of values})
     */
    @Override
    public String toString() {
        String ret = "(type=" + type + ", name=" + name;
        ret += ", values={";
        boolean starting = true;
        for (String value : values) {
            if (!starting) {
                ret += ", ";
            }
            starting = false;
            ret += value;
        }
        return ret + "})";
    }
    

}
