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

import java.awt.geom.Point2D;

/**
 * A class for holding and operating on 2D coordinates
 */
@SuppressWarnings("serial")
class Point extends Point2D.Double {
    
    /**
     * Constructs a copy of the specified Point
     * @param point - the Point to copy
     */
    public Point(Point point) {
        super(point.getX(), point.getY());
    }

    /**
     * Constructs a Point with the given coordinates
     * @param x - the X coordinate
     * @param y - the Y coordinate
     */
    public Point(double x, double y) {
        super(x, y);
    }

    /**
     * Sets the X coordinate of this Point to the specified value
     * @param x - the value to set
     */
    public void setX(double x) {
        setLocation(x, getY());
    }
    
    /**
     * Sets the Y coordinate of this Point to the specified value
     * @param y - the value to set
     */
    public void setY(double y) {
        setLocation(getX(), y);
    }
    
    /**
     * Returns a new Point whose coordinates are the sum of this Point's coordinates and that of 
     * the specified Point
     * @param point - the Point to add to this Point
     * @return the new Point
     */
    public Point add(Point point) {
        return new Point(x+point.x, y+point.y);
    }
    
    /**
     * Returns a new Point whose coordinates are the difference of this Point's coordinates and that
     * of the specified Point
     * @param point - the Point to subtract from this Point
     * @return the new Point
     */
    public Point subtract(Point point) {
        return new Point(x-point.x, y-point.y);
    }
}
