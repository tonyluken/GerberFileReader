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

import java.util.ArrayList;
import java.util.List;

/**
 * A class to hold Gerber attributes
 * 
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-
 * specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=122">
 * Section 5 of the Gerber Layer Format Specification</a>
 */
public class Attribute {
    private AttributeType type;
    private String name;
    
    private List<String> values = new ArrayList<>();
    
    /**
     * Constructs an attribute from the given Gerber TF, TA, or TO command
     * @param cmd - the Gerber command string
     * @throws Exception if the given command is not a valid TF, TA, or TO command
     * 
     * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-
     * specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=124">
     * Section 5.2 of the Gerber Specification</a>
     * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-
     * specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=124">
     * Section 5.3 of the Gerber Specification</a>
     * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-
     * specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=125">
     * Section 5.4 of the Gerber Specification</a>
     */
    Attribute(String cmd) throws Exception {
        type = AttributeType.from(cmd);
        cmd = cmd.substring(2);
        int idx = cmd.indexOf(',');
        if (idx < 0) {
            idx = cmd.length();
        }
        name = cmd.substring(0, idx);
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

    /**
     * 
     * @return the type of this attribute, either File, Aperture, or Object
     */
    public AttributeType getType() {
        return type;
    }

    /**
     * 
     * @return the name of this attribute
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @return a list of this attribute's values 
     */
    public List<String> getValues() {
        return values;
    }

    @Override
    public String toString() {
        String ret = type + " " + name;
        for (String value : values) {
            ret += ", " + value;
        }
        return ret;
    }
}
