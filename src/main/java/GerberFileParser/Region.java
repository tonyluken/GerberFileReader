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

import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Path2D;

import GerberFileParser.Utils.ArcParameters;

/**
 * A class to hold a Gerber Region
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-
 * specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=90">
 * Section 4.10 of the Gerber Layer Format Specification</a>
 */
public class Region {
    
    private Path2D path = new Path2D.Double();
    private AttributeDictionary attributes;
    
    /*
     * Constructs an empty region with all Aperture Attributes in the given AttributeDictionary
     * attached to it.
     */
    Region(AttributeDictionary attributeDictionary) {
        attributes = new AttributeDictionary();
        attributes.putAll(attributeDictionary.getAllOf(AttributeType.Aperture));
    }
    
    /**
     * Appends a line or arc to this region's contour depending on the graphics state
     * @param graphicsState - the GraphicsState that controls whether a line or an arc is appended
     * @param newPoint - the end point of the line or arc
     * @throws Exception if the GraphicsState has not been set
     */
    public void plot(GraphicsState graphicsState, Point newPoint) throws Exception {
        if(graphicsState.getPlotState() == PlotState.Linear) { //line
            path.lineTo(newPoint.getX(), newPoint.getY());
        }
        else { //arc
            ArcParameters arcParams = Utils.computeArcParameters(graphicsState, newPoint);
 
            // Generate the arc and append it to the region
            Arc2D.Double curve = new Arc2D.Double(arcParams.center.x - arcParams.radius, 
                    arcParams.center.y - arcParams.radius, 
                    2*arcParams.radius, 2*arcParams.radius, arcParams.startAngleDeg, 
                    arcParams.extentAngleDeg, Arc2D.OPEN);
            path.append(curve.getPathIterator(new AffineTransform()), true);
        }
    }
    
    /**
     * Closes this Region's current contour (if one exists) and starts a new contour at the 
     * specified location 
     * @param point - the starting Point of the new contour
     */
    public void moveTo(Point point) {
        if (path.getCurrentPoint() != null) {
            path.closePath();
        }
        path.moveTo(point.getX(), point.getY());
    }
    
    /**
     * Creates a GraphicalStream containing a GraphicalObject that is represented by this Region
     * @param graphicState - the GraphicsState that controls the Polarity of the Region
     * @param attributeDictionary - all Object type attributes in the dictionary are attached to the
     * GraphicalObject
     * @return the GraphicalStream
     * @throws Exception if the Polarity of the ApertureTransformation has not been set 
     */
    public GraphicsStream getGraphicStream(GraphicsState graphicState, AttributeDictionary attributeDictionary) throws Exception {
        attributes.putAll(attributeDictionary.getAllOf(AttributeType.Object));
        return new GraphicsStream().
                put(new GraphicalObject(new Area(path), graphicState.getApertureTransformation().getPolarity(), attributes,
                        new MetaData(new StrokeInfo(path))));
    }
    
}
