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

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * A class to hold a stream of Gerber Graphical Objects. The stream is an ordered list of Graphical
 * Objects that when superimposed on the image plane in the order they occur within the stream, 
 * generate the image described by the Gerber file.
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2024-05_en.pdf?94b45d8745c1a068fd091f095a26ddeb#page=20">
 * Section 2.9 of the Gerber Layer Format Specification</a>
 */
public class GraphicsStream {

    private ArrayList<GraphicalObject> stream = new ArrayList<>();
    private Rectangle2D bounds;
    
    /**
     * Constructs an empty GraphicsStream
     */
    public GraphicsStream() {
    }
    
    /**
     * Constructs a deep copy of the specified GraphicsStream
     * @param graphicsStream - the GraphicsStream to copy
     */
    public GraphicsStream(GraphicsStream graphicsStream) {
        for (GraphicalObject go : graphicsStream.getStream()) {
            this.put(new GraphicalObject(go));
        }
    }

    /**
     * Appends a Gerber Graphical Object onto the end of this stream
     * @param graphicObject - the Graphical Object to append
     * @return this GraphicsStream
     */
    protected GraphicsStream put(GraphicalObject graphicObject) {
        Rectangle2D gob = graphicObject.getArea().getBounds2D();
        if (gob.getWidth() > 0 || gob.getHeight() > 0) {
            if (bounds != null) {
                bounds.add(gob);
            }
            else {
                bounds = gob;
            }
        }
        stream.add(graphicObject);
        return this;
    }
    
    /**
     * Appends a Gerber GraphicsStream onto the end of this stream
     * @param graphicsStream - the Graphical Stream to append
     * @return this GraphicsStream
     */
    protected GraphicsStream put(GraphicsStream graphicsStream) {
        Rectangle2D gsb = graphicsStream.getBounds();
        if (gsb != null && (gsb.getWidth() > 0 || gsb.getHeight() > 0)) {
            if (bounds != null) {
                bounds.add(gsb);
            }
            else {
                bounds = gsb;
            }
        }
        stream.addAll(graphicsStream.stream);
        return this;
    }

    /**
     * Gets the 2D bounding box containing all objects within the stream
     * @return the 2D bounding box containing all objects within the stream
     */
    public Rectangle2D getBounds() {
        return bounds;
    }
    
    /**
     * Gets the graphics stream as a list of GraphicalObjects
     * @return the graphics stream as a list of GraphicalObjects
     */
    public List<GraphicalObject> getStream() {
        return stream;
    }

}
