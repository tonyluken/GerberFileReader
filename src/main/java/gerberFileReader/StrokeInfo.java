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

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

/**
 * A class to hold Java AWT Geometry stroke information for Gerber GraphicalObjects. This is not 
 * part of the Gerber spec but is used to attach useful information that is not easy to recover from
 * the graphical object itself. As an example, in section 6.5 of the Gerber spec, a board profile is
 * defined as,  "...a cleanly connected closed path made of draws/arcs (D01) obeying the same rules
 * as for contour segments (see 4.10.3). In case the path uses a non-zero aperture the center line 
 * represents the profile." However, recovering the center line of an arbitrary path when it is 
 * stroked with a non-zero width aperture is generally not a trivial task. The StrokeInfo class is 
 * used to hold the center line so that it is directly available.
 */
public class StrokeInfo {
    private Path2D path;
    
    /**
     * Constructs an empty StrokeInfo object
     */
    protected StrokeInfo() {
        
    }
    
    /**
     * Constructs a StrokeInfo object for the given path
     * @param path - the path
     */
    protected StrokeInfo(Path2D path) {
        this.path = path;
    }
    
    /**
     * Constructs a StrokeInfo object for the given point
     * @param point - the point
     */
    protected StrokeInfo(Point2D point) {
        path = new Path2D.Double();
        path.moveTo(point.getX(), point.getY());
    }
    
    /**
     * Creates a new StrokeInfo object by applying the specified AffineTransform to this StrokeInfo
     * @param at - the AffineTransform to apply
     * @return the new StrokeInfo
     */
    protected StrokeInfo transform(AffineTransform at) {
        StrokeInfo strokeInfo = new StrokeInfo();
        if (path != null) {
            strokeInfo.path = new Path2D.Double(path);
            strokeInfo.path.transform(at);
        }
        return strokeInfo;
    }
    
    /**
     * Gets the path held by this StrokeInfo object. For paths that were created by Gerber draws (either 
     * straight lines or arcs), the path represents the center line of the draw. For Regions, the
     * path is the contour that defines the region. For flashes, the path consists of a single
     * point centered at the flash point.
     * @return the path
     */
    public Path2D getPath() {
        return path;
    }
}
