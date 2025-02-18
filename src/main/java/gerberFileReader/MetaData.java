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
 * A class to hold data about graphics objects that isn't specified by the Gerber Layer Format 
 * Specification but nonetheless could make life much easier for someone trying to process those 
 * objects. This includes information about the centerline of objects created with draws (straight
 * strokes) and arcs; and information about which repeat of Step and Repeat Blocks that created the
 * objects (useful to disambiguate net names and reference designators).
 */
public class MetaData {
    private StrokeInfo strokeInfo;
    private RepeatCount repeatCount;
    private String blockId = "";
    
    /**
     * Constructs an empty MetaData object
     */
    protected MetaData() {
        
    }
    
    /**
     * Constructs a deep copy of the specified MetaData object
     * @param metaData - the MetaData object to copy
     */
    public MetaData(MetaData metaData) {
        strokeInfo = metaData.strokeInfo;
        repeatCount = metaData.repeatCount;
        blockId = new String(metaData.blockId);
    }

    /**
     * Constructs a MetaData object and populates it with the specified StrokeInfo object
     * @param strokeInfo - the stroke information
     */
    protected MetaData(StrokeInfo strokeInfo) {
        this.strokeInfo = strokeInfo;
    }
    
    /**
     * Constructs a MetaData object and populates it with the specified StrokeInfo object and 
     * the specified repeat counts
     * @param strokeInfo - the stroke information
     * @param xCount - the x repeat count
     * @param yCount - the y repeat count
     */
    protected MetaData(StrokeInfo strokeInfo, int xCount, int yCount) {
        this.strokeInfo = strokeInfo;
        repeatCount = new RepeatCount(xCount, yCount);
    }
    
    /**
     * Gets the stroke information for this object
     * @return the stroke information
     */
    public StrokeInfo getStrokeInfo() {
        return strokeInfo;
    }
    
    /**
     * Sets the stroke information
     * @param strokeInfo - the stroke information to set
     */
    protected void setStrokeInfo(StrokeInfo strokeInfo) {
        this.strokeInfo = strokeInfo;
    }
    
    /**
     * Gets the repeat count for objects that were created during a Step and Repeat command. This
     * can be used to uniquely identify objects that would otherwise be identical.
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
     * Gets the repeat count for objects that were created during a Step and Repeat command. This
     * can be used to uniquely identify objects that would otherwise be identical.
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
    protected void setRepeatCount(int xCount, int yCount) {
        repeatCount = new RepeatCount(xCount, yCount);
    }
    
    /**
     * Gets the block Id for objects that were created by flashing Block Apertures. This
     * can be used to uniquely identify objects that would otherwise be identical.
     * @return the block Id of the object. If the object was not created by flashing a Block
     * Aperture, returns an empty string "".
     */
    public String getBlockId() {
        return blockId;
    }
    
    /**
     * Prepends the specified block Id to the currently defined block Id
     * @param blockId - the block Id to prepend
     */
    protected void prependBlockId(int blockId) {
        this.blockId = "B" + String.valueOf(blockId) + this.blockId;
    }
    
    /**
     * Returns a string representation of this MetaData object
     */
    public String toString() {
        return "strokeInfo = " + strokeInfo + ", repeatId = '" + getRepeatId() + "', blockId = '" + blockId + "'";
    }
}
