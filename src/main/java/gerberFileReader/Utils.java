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
 * Utilities class
 */
class Utils {
    
    /**
     * Finds the index of the first character in a string that is not part of a valid Gerber number
     * @param cmd - the string to search
     * @param pstart - the index of the character where the search is to begin
     * @return the index of the first non-numeric character or -1 if pstart is beyond the end of
     * the string or the character at pstart is not part of a valid gerber number
     */
    public static int numberEndIndex(String cmd, int pstart) {
        if (pstart >= cmd.length()) {
            return -1;
        }
        char cc = cmd.charAt(pstart);
        if (!Character.isDigit(cc) && cc != '-' && cc != '+' && cc != '.') {
            return -1;
        }
        boolean foundPoint = cc == '.';
        int p;
        for (p = pstart+1; p < cmd.length(); p++) {
            cc = cmd.charAt(p);
            if ((!Character.isDigit(cc) && cc != '.') || (foundPoint && cc == '.')) {
                break;
            }
            foundPoint = foundPoint || (cc == '.');
        }
        return p;
    }
    
    /**
     * A class to hold Java AWT Geometry arc parameters 
     */
    public static class ArcParameters {
        public Point center;
        public double radius;
        public double startAngleDeg;
        public double extentAngleDeg;
        
        /**
         * Default constructor
         */
        ArcParameters() {
        }
        
        /**
         * Construct a deep copy of the specified ArcParameters object
         * @param arcParameters - the object to copy
         */
        ArcParameters(ArcParameters arcParameters) {
            this.center = new Point(arcParameters.center);
            this.radius = arcParameters.radius;
            this.startAngleDeg = arcParameters.startAngleDeg;
            this.extentAngleDeg = arcParameters.extentAngleDeg;
        }
    }
    
    /**
     * Computes Java AWT Geometry arc parameters
     * 
     * @param graphicsState - the current GraphicsState (starting point, relative center, and 
     * direction of the arc)
     * @param endPoint - the end point of the arc
     * @return the arc parameters
     * @throws GerberLayerFormatException if the arc starting or center points are not set or if the
     * plot state is linear
     */
    public static ArcParameters computeArcParameters(GraphicsState graphicsState, Point endPoint) 
            throws GerberLayerFormatException {
        ArcParameters arcParams = new ArcParameters();
        Point crntPoint = graphicsState.getCurrentPoint();
        if (crntPoint == null) {
            throw new GerberLayerFormatException("Arc starting point not set");
        }
        Point arcRelCenter = graphicsState.getArcRelativeCenterPoint();
        if (arcRelCenter == null) {
            throw new GerberLayerFormatException("Arc center point not set");
        }
        
        //Calculate the radius of the arc
        arcParams.radius = Math.hypot(arcRelCenter.x, arcRelCenter.y);
        
        ArcParameters bestArcParams = null;
        double bestDeviation = Double.POSITIVE_INFINITY;
        
        //In the event of being in single quadrant mode, we need to try four different centers to
        //see which one is the correct center
        for (int i=0; i<4; i++) {
            //Calculate the absolute center of the arc. Note that this calculation is limited to the
            //resolution of the Gerber file. Later we will recalculate a high-resolution center. 
            arcParams.center = crntPoint.add(arcRelCenter);
    
            double checkRadius = Math.hypot(arcParams.center.x - endPoint.x, 
                    arcParams.center.y - endPoint.y);
            
            //Calculate the angle of the starting point (negate the Y component because Java's 
            //positive Y is down)
            arcParams.startAngleDeg = Math.toDegrees(
                    Math.atan2(-(crntPoint.y - arcParams.center.y),
                    crntPoint.x - arcParams.center.x));
            if (arcParams.startAngleDeg < 0) {
                arcParams.startAngleDeg += 360;
            }
            //Calculate the angle of the ending point (again, negate the Y component because Java's 
            //positive Y is down)
            double stop = Math.toDegrees(Math.atan2(-(endPoint.y - arcParams.center.y),
                    endPoint.x - arcParams.center.x));
            if (stop < 0) {
                stop += 360;
            }
            arcParams.extentAngleDeg = stop - arcParams.startAngleDeg;
            
            //Correct the extent for clockwise or counterclockwise arcs (this looks backwards
            //because in Java, positive angles are measured clockwise). Note that in single quadrant
            //mode, zero angle extent means no arc at all versus multi-quadrant mode where that 
            //means a full 360 degree arc.
            if (graphicsState.getPlotState() == PlotState.COUNTERCLOCKWISE) {
                if ((!graphicsState.isSingleQuadrantArcMode() && arcParams.extentAngleDeg >= 0) ||
                        (graphicsState.isSingleQuadrantArcMode() && arcParams.extentAngleDeg > 0)) {
                    arcParams.extentAngleDeg -= 360;
                }
            }
            else if (graphicsState.getPlotState() == PlotState.CLOCKWISE) {
                if ((!graphicsState.isSingleQuadrantArcMode() && arcParams.extentAngleDeg <= 0) ||
                        (graphicsState.isSingleQuadrantArcMode() && arcParams.extentAngleDeg < 0)) {
                    arcParams.extentAngleDeg += 360;
                }
            }
            else {
                throw new GerberLayerFormatException("Invalid PlotState for an arc");
            }
            
            if (graphicsState.isSingleQuadrantArcMode()) {
                //Keep the one that is no more than 90 degrees in extent and has the least radial
                //deviation
                if (Math.abs(arcParams.extentAngleDeg) <= 90) {
                    double deviation = Math.abs(arcParams.radius - checkRadius);
                    if (bestArcParams == null || deviation < bestDeviation) {
                        bestArcParams = new ArcParameters(arcParams);
                        bestDeviation = deviation;
                    }
                }
                if (i == 3) {
                    //We've tried all four possible centers so return the best one
                    if (bestArcParams == null) {
                        throw new GerberLayerFormatException("Invalid arc");
                    }
                    arcParams = bestArcParams;
                }
                else {
                    //Retry with the next possible center by changing the signs in the following
                    //order: (+,+) => (+,-) => (-,+) => (-,-)
                    if (arcRelCenter.y <= 0) {
                        arcRelCenter.setX(-arcRelCenter.x);
                    }
                    arcRelCenter.setY(-arcRelCenter.y);
                }
            }
            else {
                //Since we're not in single quadrant mode, we're done at this point
                break;
            }
        }
        
        //If the start and end points are different, we need to recalculate everything after 
        //adjusting the center point (since the specified center point is represented in a limited 
        //resolution form) so that it is exactly the same distance from both points. We use the 
        //average distance from the specified center point to the start and end points as the true
        //radius of the arc. This makes the arc actually start and end exactly on the desired 
        //points.
        if (crntPoint.x != endPoint.x || crntPoint.y != endPoint.y) {
            //Calculate the arc radius as the average of the two distances
            double r = (Math.hypot(crntPoint.x-arcParams.center.x, crntPoint.y-arcParams.center.y) +
                    Math.hypot(endPoint.x-arcParams.center.x, endPoint.y-arcParams.center.y))/2;
            arcParams.radius = r;
            
            //Now calculate the high resolution coordinates of the center of the circle with the new
            //radius that passes exactly through the start and end points. There will be two 
            //possibilities.
            double a = crntPoint.x;
            double a2 = a*a;
            double a3 = a2*a;
            double a4 = a2*a2;
            double b = crntPoint.y;
            double b2 = b*b;
            double b3 = b2*b;
            double b4 = b2*b2;
            double c = endPoint.x;
            double c2 = c*c;
            double c3 = c2*c;
            double c4 = c2*c2;
            double d = endPoint.y;
            double d2 = d*d;
            double d3 = d2*d;
            double d4 = d2*d2;
            double r2 = r*r;
            double srt = Math.sqrt(-a4 - 2*a2*b2 - b4 + 4*a*c3 - c4 + 4*b*d3 - d4 - 2*(3*a2 + b2)*c2
                    - 2*(a2 + 3*b2 - 2*a*c + c2)*d2 + 4*(a2 + b2 - 2*a*c + c2 - 2*b*d + d2)*r2 
                    + 4*(a3 + a*b2)*c + 4*(a2*b + b3 - 2*a*b*c + b*c2)*d);
            double denom = a2 + b2 - 2*a*c + c2 - 2*b*d + d2;
            
            //One possible center is given by
            double x1 = 0.5*(a3 + a*b2 - a*c2 + c3 + (a + c)*d2 - (a2 - b2)*c - 2*(a*b + b*c)*d 
                    + srt*(b - d))/denom;
            double y1 = 0.5*(a2*b + b3 - 2*a*b*c + b*c2 - b*d2 + d3 + (a2 - b2 - 2*a*c + c2)*d 
                    - srt*(a - c))/denom;
            
            //The other possible center is given by
            double x2 = 0.5*(a3 + a*b2 - a*c2 + c3 + (a + c)*d2 - (a2 - b2)*c - 2*(a*b + b*c)*d 
                    - srt*(b - d))/denom;
            double y2 = 0.5*(a2*b + b3 - 2*a*b*c + b*c2 - b*d2 + d3 + (a2 - b2 - 2*a*c + c2)*d 
                    + srt*(a - c))/denom;
            
            //Pick the one that is closest to the original center
            if (Math.hypot(x1-arcParams.center.x, y1-arcParams.center.y) <= 
                    Math.hypot(x2-arcParams.center.x, y2-arcParams.center.y)) {
                arcParams.center.x = x1;
                arcParams.center.y = y1;
            }
            else {
                arcParams.center.x = x2;
                arcParams.center.y = y2;
            }
            
            //Calculate the angle of the starting point (negate the Y component because Java's 
            //positive Y is down)
            arcParams.startAngleDeg = Math.toDegrees(
                    Math.atan2(-(crntPoint.y - arcParams.center.y),
                    crntPoint.x - arcParams.center.x));
            if (arcParams.startAngleDeg < 0) {
                arcParams.startAngleDeg += 360;
            }
            
            //Calculate the angle of the ending point (again, negate the Y component because Java's 
            //positive Y is down)
            double stop = Math.toDegrees(Math.atan2(-(endPoint.y - arcParams.center.y),
                    endPoint.x - arcParams.center.x));
            if (stop < 0) {
                stop += 360;
            }
            arcParams.extentAngleDeg = stop - arcParams.startAngleDeg;
            
            //Correct the extent for clockwise or counterclockwise arcs (this looks backwards
            //because in Java, positive angles are measured clockwise). Note that in single quadrant
            //mode, zero angle extent means no arc at all versus multi-quadrant mode where that 
            //means a full 360 degree arc.
            if (graphicsState.getPlotState() == PlotState.COUNTERCLOCKWISE) {
                if ((!graphicsState.isSingleQuadrantArcMode() && arcParams.extentAngleDeg >= 0) ||
                        (graphicsState.isSingleQuadrantArcMode() && arcParams.extentAngleDeg > 0)) {
                    arcParams.extentAngleDeg -= 360;
                }
            }
            else if (graphicsState.getPlotState() == PlotState.CLOCKWISE) {
                if ((!graphicsState.isSingleQuadrantArcMode() && arcParams.extentAngleDeg <= 0) ||
                        (graphicsState.isSingleQuadrantArcMode() && arcParams.extentAngleDeg < 0)) {
                    arcParams.extentAngleDeg += 360;
                }
            }
        }
        
        return arcParams;
    }
        
}
