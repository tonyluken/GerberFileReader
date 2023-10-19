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
 * A Class to hold the repeat count for objects created during Step and Repeat Blocks
 *
 */
public final class RepeatCount {
    private int xCount;
    private int yCount;
    
    /**
     * Constructs a RepeatCount object with the specified counts
     * @param xCount - the count in the x direction
     * @param yCount - the count in the y direction
     */
    RepeatCount(int xCount, int yCount) {
        this.xCount = xCount;
        this.yCount = yCount;
    }
    
    /**
     * 
     * @return a string representation of the repeat count in the form "[xCount,yCount]"
     */
    public String getId() {
        return String.format("[%d,%d]", xCount, yCount);
    }

    /**
     * 
     * @return the repeat count in the x direction
     */
    public int getxCount() {
        return xCount;
    }

    /**
     * 
     * @return the repeat count in the y direction
     */
    public int getyCount() {
        return yCount;
    }
}
