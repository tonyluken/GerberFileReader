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

import java.awt.geom.Area;

/**
 * A class for representing Gerber graphical objects
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-
 * specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=11">
 * Section 2.3 of the Gerber Layer Format Specification</a>
 */
public class GraphicalObject {
    private Area area;
    private Polarity polarity;
    private AttributeDictionary attributes;
    private MetaData metaData; //not part of the Gerber spec but useful information
    
    /**
     * Constructs a GraphicalObject with the shape, size and position of the given Area. Its 
     * polarity, attributes and metaData are set to the specified values
     * 
     * @param area - the shape, size and position of the GraphicalObject
     * @param polarity - the polarity of the GraphicalObject
     * @param attributes - the attributes to attach to the GraphicalObject
     * @param metaData - the metaData for the GraphicalObject
     */
    GraphicalObject(Area area, Polarity polarity, AttributeDictionary attributes, MetaData metaData) {
        this.area = area;
        this.polarity = polarity;
        this.attributes = new AttributeDictionary();
        this.attributes.putAll(attributes);
        this.metaData = metaData;
    }

    /**
     * 
     * @return the shape, size and position of this GraphicalObject
     */
    public Area getArea() {
        return area;
    }

    /**
     * 
     * @return the polarity of this GraphicalObject
     */
    public Polarity getPolarity() {
        return polarity;
    }
    
    /**
     * 
     * @return the attributes attached to this GraphicalObject
     */
    public AttributeDictionary getAttributes() {
        return attributes;
    }
    
    /**
     * 
     * @return the strokeInfo for this GraphicalObject
     */
    public MetaData getMetaData() {
        return metaData;
    }
    
    /**
     * Reverses the polarity of this GraphicalObject
     * 
     * @param reverse - true reverses the polarity of this GraphicalObject, false leaves the 
     * polarity unchanged
     * @return either a new copy of this GraphicalObject with its polarity reversed if reverse is 
     * true or this GraphicalObject if reverse is false
     */
    public GraphicalObject reversePolarity(boolean reverse) {
        if (reverse) {
            return new GraphicalObject(area, Polarity.reversed(polarity), attributes, metaData);
        }
        else {
            return this;
        }
    }
    
}
