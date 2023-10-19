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
 * A class for holding an aperture template macro
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-
 * specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=56">
 * Section 4.5 of the Gerber Layer Format Specification</a>
 */
public class ApertureMacro {
    private String name;
    private List<String> commands = new ArrayList<>();
    
    /**
     * Constructs an aperture template macro with the name given by the AM command
     * @param command - the AM command
     */
    ApertureMacro(String command) throws Exception {
        if (command.startsWith("AM") && command.length() > 2) {
            name = command.substring(2);
        }
        else {
            throw new Exception("Macro command must begin with 'AM' and be followed by a valid name.");
        }
    }
    
    /**
     * 
     * @return the name of the aperture template macro
     */
    public String getName() {
        return name;
    }
    
    /**
     * Appends a macro command to this macro
     * @param command - the command to append
     */
    public void addCommand(String command) {
        commands.add(command);
    }
    
    /**
     * 
     * @return the list of commands for this macro
     */
    public List<String> getCommands() {
        return commands;
    }
}
