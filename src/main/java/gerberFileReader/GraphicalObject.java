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

import java.awt.geom.Area;

/**
 * A class for representing Gerber graphical objects
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2024-05_en.pdf?94b45d8745c1a068fd091f095a26ddeb#page=12">
 * Section 2.3 of the Gerber Layer Format Specification</a>
 */
public class GraphicalObject {
    private Area area;
    private Polarity polarity;
    private AttributeDictionary attributes;
    private MetaData metaData; //not part of the Gerber spec but useful information
    
    /**
     * Constructs a deep copy of the specified GraphicalObject
     * @param graphicalObject - the GraphicalObject to copy
     */
    public GraphicalObject(GraphicalObject graphicalObject) {
        area = graphicalObject.area;
        polarity = graphicalObject.polarity;
        attributes = graphicalObject.attributes;
        metaData = new MetaData(graphicalObject.metaData);
    }
    
    /**
     * Constructs a GraphicalObject with the shape, size and position of the given Area. Its 
     * polarity, attributes and metaData are set to the specified values
     * 
     * @param area - the shape, size and position of the GraphicalObject
     * @param polarity - the polarity of the GraphicalObject
     * @param attributes - the attributes to attach to the GraphicalObject
     * @param metaData - the metaData for the GraphicalObject
     */
    protected GraphicalObject(Area area, Polarity polarity, AttributeDictionary attributes, 
            MetaData metaData) {
        this.area = area;
        this.polarity = polarity;
        this.attributes = new AttributeDictionary();
        this.attributes.putAll(attributes);
        this.metaData = metaData;
    }

    /**
     * Gets a java.awt.geom.Area object that describes the shape, size and position of this 
     * GraphicalObject
     * @return the shape, size and position of this GraphicalObject
     */
    public Area getArea() {
        return area;
    }

    /**
     * Gets the polarity of this GraphicalObject, either DARK (meaning the object should plot on top
     * of and block any other existing objects beneath it in the image) or CLEAR (meaning the object
     * should clear all other existing objects beneath it in the image)
     * @return the polarity of this GraphicalObject
     */
    public Polarity getPolarity() {
        return polarity;
    }
    
    /**
     * Gets the attributes attached to this GraphicalObject
     * @return the attributes attached to this GraphicalObject
     */
    public AttributeDictionary getAttributes() {
        return attributes;
    }
    
    /**
     * Gets the metadata for this GraphicalObject
     * @return the metadata for this GraphicalObject
     */
    public MetaData getMetaData() {
        return metaData;
    }
    
    /**
     * Reverses the polarity of this GraphicalObject
     * 
     * @param reverse - true reverses the polarity of this GraphicalObject, false leaves the 
     * polarity unchanged
     * @return a new copy of this GraphicalObject with its polarity reversed if reverse is 
     * true
     */
    protected GraphicalObject reversePolarity(boolean reverse) {
        if (reverse) {
            return new GraphicalObject(area, Polarity.reverse(polarity), attributes, metaData);
        }
        else {
            return new GraphicalObject(area, polarity, attributes, metaData);
        }
    }
    
}
