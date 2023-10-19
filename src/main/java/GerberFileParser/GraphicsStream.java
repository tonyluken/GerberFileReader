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

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * A class to hold a stream of Gerber Graphical Objects. The stream is an ordered list of Graphical
 * Objects that when superimposed on the image plane in the order they occur within the stream, 
 * generate the image described by the Gerber file.
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-
 * specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=19">
 * Section 2.9 of the Gerber Layer Format Specification</a>
 */
public class GraphicsStream {

    private ArrayList<GraphicalObject> stream = new ArrayList<>();
    private Rectangle2D bounds;
    
    /**
     * Appends a Gerber Graphical Object onto the end of this stream
     * @param graphicObject - the Graphical Object to append
     * @return this GraphicsStream
     */
    public GraphicsStream put(GraphicalObject graphicObject) {
        if (bounds != null) {
            bounds.add(graphicObject.getArea().getBounds2D());
        }
        else {
            bounds = graphicObject.getArea().getBounds2D();
        }
        stream.add(graphicObject);
        return this;
    }
    
    /**
     * Appends a Gerber GraphicsStream onto the end of this stream
     * @param graphicsStream - the Graphical Stream to append
     * @return this GraphicsStream
     */
    public GraphicsStream put(GraphicsStream graphicsStream) {
        if (bounds != null) {
            bounds.add(graphicsStream.getBounds());
        }
        else {
            bounds = graphicsStream.getBounds();
        }
        stream.addAll(graphicsStream.stream);
        return this;
    }

    /**
     * 
     * @return the 2D bounding box containing all objects within the stream
     */
    public Rectangle2D getBounds() {
        return bounds;
    }
    
    /**
     * 
     * @return the stream as a list of GraphicalObjects
     */
    public List<GraphicalObject> getStream() {
        return stream;
    }

}
