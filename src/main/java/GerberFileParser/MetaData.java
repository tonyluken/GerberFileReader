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
 * A class to hold data about graphics objects that isn't specified by the Gerber Layer Format 
 * Specification but nonetheless could make life much easier for someone trying to process those 
 * objects. This includes information about the centerline of objects created with draws (straight
 * strokes) and arcs; and information about which repeat of Step and Repeat Blocks that created the
 * objects.
 */
public class MetaData {
    private StrokeInfo strokeInfo;
    private RepeatCount repeatCount;
    
    /**
     * Constructs an empty MetaData object
     */
    MetaData() {
        
    }
    
    /**
     * Constructs a MetaData object and populates it with the specified StrokeInfo object
     * @param strokeInfo - the stroke information
     */
    MetaData(StrokeInfo strokeInfo) {
        this.strokeInfo = strokeInfo;
    }
    
    /**
     * Constructs a MetaData object and populates it with the specified StrokeInfo object and 
     * the specified repeat counts
     * @param strokeInfo - the stroke information
     * @param xCount - the x repeat count
     * @param yCount - the y repeat count
     */
    MetaData(StrokeInfo strokeInfo, int xCount, int yCount) {
        this.strokeInfo = strokeInfo;
        repeatCount = new RepeatCount(xCount, yCount);
    }
    
    /**
     * 
     * @return the stroke information
     */
    public StrokeInfo getStrokeInfo() {
        return strokeInfo;
    }
    
    /**
     * Sets the stroke information
     * @param strokeInfo - the stroke information to set
     */
    public void setStrokeInfo(StrokeInfo strokeInfo) {
        this.strokeInfo = strokeInfo;
    }
    
    /**
     * 
     * @return the repeat counts as a string of the form "[xCount,yCount]". If no repeat count was
     * defined, returns an empty string ""
     */
    public String getRepeatId() {
        if (repeatCount == null) {
            return "";
        }
        return repeatCount.getId();
    }
    
    /**
     * 
     * @return the repeat count
     */
    public RepeatCount getRepeatCount() {
        return repeatCount;
    }
    
    /**
     * Sets the repeat count
     * @param xCount - the x repeat count to set
     * @param yCount - the y repeat count to set
     */
    public void setRepeatCount(int xCount, int yCount) {
        repeatCount = new RepeatCount(xCount, yCount);
    } 
}
