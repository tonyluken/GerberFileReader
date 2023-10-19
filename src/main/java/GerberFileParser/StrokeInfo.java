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
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

/**
 * A class to hold Java AWT Geometry stroke information for Gerber GraphicalObjects. This is not 
 * part of the Gerber spec but is used to attach useful information that is not easy to recover from
 * the graphical object itself. As an example, in section 6.5 of the Gerber spec, a board profile is
 * defined as,  "...a cleanly connected closed path made of draws/arcs (D01) obeying the same rules
 * as for contour segments (see 4.10.3). In case the path uses a non-zero aperture the center line 
 * represents the profile." Recovering the center line of an arbitrary closed path when it is stroked
 * with a non-zero width pen is not trivial task. The StrokeInfo class is used to hold the center 
 * line so that it is directly available.
 */
public class StrokeInfo {
    public Path2D path;
    public Point2D point;
    
    /**
     * Constructs an empty StrokeInfo object
     */
    StrokeInfo() {
        
    }
    
    /**
     * Constructs a StrokeInfo object for the given path
     * @param path - the path
     */
    StrokeInfo(Path2D path) {
        this.path = path;
    }
    
    /**
     * Constructs a StrokeInfo object for the given point
     * @param point - the point
     */
    StrokeInfo(Point2D point) {
        this.point = point;
    }
    
    /**
     * Creates a new StrokeInfo object by applying the specifed AffineTransform to this StrokeInfo
     * @param at - the AffineTransform to apply
     * @return the new StrokeInfo
     */
    public StrokeInfo transform(AffineTransform at) {
        StrokeInfo strokeInfo = new StrokeInfo();
        if (path != null) {
            strokeInfo.path = new Path2D.Double(path);
            strokeInfo.path.transform(at);
        }
        if (point != null) {
            strokeInfo.point = new Point2D.Double(point.getX(), point.getY());
            at.transform(strokeInfo.point, strokeInfo.point);
        }
        return strokeInfo;
    }
}
