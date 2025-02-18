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
 * A class to hold the repeat count for objects created by Gerber Step and Repeat Blocks. Ordinarily
 * there is no way to distinguish the Gerber objects created by a Gerber Step and Repeat Block (other
 * than by their coordinates). This repeat count is stored as part of the object's metaData in order 
 * to provide a simple means to disambiguate the objects. 
 *
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2024-05_en.pdf?94b45d8745c1a068fd091f095a26ddeb#page=116">
 * Section 4.12 of the Gerber Layer Format Specification</a>
 */
public final class RepeatCount {
    private int xCount;
    private int yCount;
    
    /**
     * Constructs a RepeatCount object with the specified counts
     * @param xCount - the count in the x direction
     * @param yCount - the count in the y direction
     */
    protected RepeatCount(int xCount, int yCount) {
        this.xCount = xCount;
        this.yCount = yCount;
    }
    
    /**
     * Gets a string representation of the repeat count in the form "[xCount,yCount]"
     * @return the string
     */
    public String getId() {
        return String.format("[%d,%d]", xCount, yCount);
    }

    /**
     * Gets the X direct repeat count
     * @return the repeat count
     */
    public int getXCount() {
        return xCount;
    }

    /**
     * Gets the Y direct repeat count
     * @return the repeat count
     */
    public int getYCount() {
        return yCount;
    }
}
