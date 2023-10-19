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

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utilities class
 */
public class Utils {
    
    /**
     * Finds the index of the first character in a string that is not part of a valid Gerber number
     * @param cmd - the string to search
     * @param pstart - the index of the character where the search is to begin
     * @return the index of the first non-numeric character or -1 if pstart is beyond the end of
     * the string or the character at pstart is not part of a valid gerber number
     */
    public static int numberEndIndex(String cmd, int pstart) {
        char cc = cmd.charAt(pstart);
        if (pstart >= cmd.length() || (!Character.isDigit(cc) && cc != '-' && cc != '+' && cc != '.')) {
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
     * @param graphicsState - the current GraphicsState (starting point, center, and direction of
     * the arc)
     * @param newPoint - the end point of the arc
     * @return the arc parameters
     * @throws Exception if the arc starting or center points are not set or if the plot state is
     * linear
     */
    public static ArcParameters computeArcParameters(GraphicsState graphicsState, Point newPoint) 
            throws Exception {
        ArcParameters arcParameters = new ArcParameters();
        Point currentPoint = graphicsState.getCurrentPoint();
        if (currentPoint == null) {
            throw new Exception("Arc starting Point not set");
        }
        Point arcRelCenter = graphicsState.getArcRelativeCenterPoint();
        if (arcRelCenter == null) {
            throw new Exception("Arc center Point not set");
        }
        
        //Calculate the radius of the arc
        arcParameters.radius = Math.hypot(arcRelCenter.x, arcRelCenter.y);
        
        ArcParameters bestArcParams = null;
        double bestDeviation = Double.POSITIVE_INFINITY;
        
        //In the event of being in single quadrant mode, we need to try four different centers to
        //see which one is the correct center
        for (int i=0; i<4; i++) {
            //Calculate the center of the arc
            arcParameters.center = currentPoint.add(arcRelCenter);
    
            double checkRadius = Math.hypot(arcParameters.center.x - newPoint.x, 
                    arcParameters.center.y - newPoint.y);
            
            //Calculate the angle of the starting point (negate the Y component because Java's 
            //positive Y is down)
            arcParameters.startAngleDeg = Math.toDegrees(
                    Math.atan2(-(currentPoint.y - arcParameters.center.y),
                    currentPoint.x - arcParameters.center.x));
            if (arcParameters.startAngleDeg < 0) {
                arcParameters.startAngleDeg += 360;
            }
            //Calculate the angle of the ending point (again, negate the Y component because Java's 
            //positive Y is down)
            double stop = Math.toDegrees(Math.atan2(-(newPoint.y - arcParameters.center.y),
                    newPoint.x - arcParameters.center.x));
            if (stop < 0) {
                stop += 360;
            }
            arcParameters.extentAngleDeg = stop - arcParameters.startAngleDeg;
            
            //Correct the extent for clockwise or counterclockwise arcs (this looks backwards
            //because in Java, positive angles are measured clockwise). Note that in single quadrant
            //mode, zero angle extent means no arc at all versus multi-quadrant mode where that 
            //means a full 360 degree arc.
            if (graphicsState.getPlotState() == PlotState.CounterClockwise) {
                if ((!graphicsState.isSingleQuadrantArcMode() && arcParameters.extentAngleDeg >= 0) ||
                        (graphicsState.isSingleQuadrantArcMode() && arcParameters.extentAngleDeg > 0)) {
                    arcParameters.extentAngleDeg -= 360;
                }
            }
            else if (graphicsState.getPlotState() == PlotState.Clockwise) {
                if ((!graphicsState.isSingleQuadrantArcMode() && arcParameters.extentAngleDeg <= 0) ||
                        (graphicsState.isSingleQuadrantArcMode() && arcParameters.extentAngleDeg < 0)) {
                    arcParameters.extentAngleDeg += 360;
                }
            }
            else {
                throw new Exception("Invalid PlotState for an arc");
            }
            
            if (graphicsState.isSingleQuadrantArcMode()) {
                //Keep the one that is no more than 90 degrees in extent and has the least radial
                //deviation
                if (Math.abs(arcParameters.extentAngleDeg) <= 90) {
                    double deviation = Math.abs(arcParameters.radius - checkRadius);
                    if (bestArcParams == null || deviation < bestDeviation) {
                        bestArcParams = new ArcParameters(arcParameters);
                        bestDeviation = deviation;
                    }
                }
                if (i == 3) {
                    //We've tried all four possible centers so return the best one
                    if (bestArcParams == null) {
                        throw new Exception("Invalid arc");
                    }
                    arcParameters = bestArcParams;
                }
                else {
                    //Retry with the next possible center: (+,+) => (+,-) => (-,+) => (-,-)
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
        //adjusting the center point so that it is exactly the same distance from both points. We 
        //use the average distance from the specified center point to the start and end points as 
        //the radius of the arc. This makes the arc actually start and end at the desired points.
        if (currentPoint.x != newPoint.x || currentPoint.y != newPoint.y) {
            //Calculate the arc radius as the average of the two distances
            double r = (Math.hypot(currentPoint.x-arcParameters.center.x, currentPoint.y-arcParameters.center.y) +
                    Math.hypot(newPoint.x-arcParameters.center.x, newPoint.y-arcParameters.center.y))/2;
            arcParameters.radius = r;
            
            //Now calculate the coordinates of center of the circle with the new radius that passes 
            //exactly through the start and end points. There will be two possibilities.
            double a = currentPoint.x;
            double a2 = a*a;
            double a3 = a2*a;
            double a4 = a2*a2;
            double b = currentPoint.y;
            double b2 = b*b;
            double b3 = b2*b;
            double b4 = b2*b2;
            double c = newPoint.x;
            double c2 = c*c;
            double c3 = c2*c;
            double c4 = c2*c2;
            double d = newPoint.y;
            double d2 = d*d;
            double d3 = d2*d;
            double d4 = d2*d2;
            double r2 = r*r;
            double srt = Math.sqrt(-a4 - 2*a2*b2 - b4 + 4*a*c3 - c4 + 4*b*d3 - d4 - 2*(3*a2 + b2)*c2
                    - 2*(a2 + 3*b2 - 2*a*c + c2)*d2 + 4*(a2 + b2 - 2*a*c + c2 - 2*b*d + d2)*r2 
                    + 4*(a3 + a*b2)*c + 4*(a2*b + b3 - 2*a*b*c + b*c2)*d);
            double denom = a2 + b2 - 2*a*c + c2 - 2*b*d + d2;
            
            //One possible center
            double x1 = 0.5*(a3 + a*b2 - a*c2 + c3 + (a + c)*d2 - (a2 - b2)*c - 2*(a*b + b*c)*d 
                    + srt*(b - d))/denom;
            double y1 = 0.5*(a2*b + b3 - 2*a*b*c + b*c2 - b*d2 + d3 + (a2 - b2 - 2*a*c + c2)*d 
                    - srt*(a - c))/denom;
            
            //The other possible center
            double x2 = 0.5*(a3 + a*b2 - a*c2 + c3 + (a + c)*d2 - (a2 - b2)*c - 2*(a*b + b*c)*d 
                    - srt*(b - d))/denom;
            double y2 = 0.5*(a2*b + b3 - 2*a*b*c + b*c2 - b*d2 + d3 + (a2 - b2 - 2*a*c + c2)*d 
                    + srt*(a - c))/denom;
            
            //Pick the one that is closest to the original center
            if (Math.hypot(x1-arcParameters.center.x, y1-arcParameters.center.y) <= 
                    Math.hypot(x2-arcParameters.center.x, y2-arcParameters.center.y)) {
                arcParameters.center.x = x1;
                arcParameters.center.y = y1;
            }
            else {
                arcParameters.center.x = x2;
                arcParameters.center.y = y2;
            }
            
            //Calculate the angle of the starting point (negate the Y component because Java's 
            //positive Y is down)
            arcParameters.startAngleDeg = Math.toDegrees(
                    Math.atan2(-(currentPoint.y - arcParameters.center.y),
                    currentPoint.x - arcParameters.center.x));
            if (arcParameters.startAngleDeg < 0) {
                arcParameters.startAngleDeg += 360;
            }
            
            //Calculate the angle of the ending point (again, negate the Y component because Java's 
            //positive Y is down)
            double stop = Math.toDegrees(Math.atan2(-(newPoint.y - arcParameters.center.y),
                    newPoint.x - arcParameters.center.x));
            if (stop < 0) {
                stop += 360;
            }
            arcParameters.extentAngleDeg = stop - arcParameters.startAngleDeg;
            
            //Correct the extent for clockwise or counterclockwise arcs (this looks backwards
            //because in Java, positive angles are measured clockwise). Note that in single quadrant
            //mode, zero angle extent means no arc at all versus multi-quadrant mode where that 
            //means a full 360 degree arc.
            if (graphicsState.getPlotState() == PlotState.CounterClockwise) {
                if ((!graphicsState.isSingleQuadrantArcMode() && arcParameters.extentAngleDeg >= 0) ||
                        (graphicsState.isSingleQuadrantArcMode() && arcParameters.extentAngleDeg > 0)) {
                    arcParameters.extentAngleDeg -= 360;
                }
            }
            else if (graphicsState.getPlotState() == PlotState.Clockwise) {
                if ((!graphicsState.isSingleQuadrantArcMode() && arcParameters.extentAngleDeg <= 0) ||
                        (graphicsState.isSingleQuadrantArcMode() && arcParameters.extentAngleDeg < 0)) {
                    arcParameters.extentAngleDeg += 360;
                }
            }
        }
        
        return arcParameters;
    }
    
    /**
     * Reverses the specified path so that its iterator traverses the path in the opposite direction
     * @param path - the path to reverse
     * @return the reversed path
     */
    public static Path2D reversePath(Path2D path) {
        List<Integer> segTypes = new ArrayList<>();
        List<Point2D> points = new ArrayList<>();
        PathIterator pathIter = path.getPathIterator(null);
        double[] coords = new double[6];
        while (!pathIter.isDone()) {
            int segType = pathIter.currentSegment(coords);
            segTypes.add(segType);
            switch (segType) {
                case PathIterator.SEG_MOVETO:
                    points.add(new Point2D.Double(coords[0], coords[1]));
                    break;
                case PathIterator.SEG_LINETO:
                    points.add(new Point2D.Double(coords[0], coords[1]));
                    break;
                case PathIterator.SEG_QUADTO:
                    points.add(new Point2D.Double(coords[0], coords[1]));
                    points.add(new Point2D.Double(coords[2], coords[3]));
                    break;
                case PathIterator.SEG_CUBICTO:
                    points.add(new Point2D.Double(coords[0], coords[1]));
                    points.add(new Point2D.Double(coords[2], coords[3]));
                    points.add(new Point2D.Double(coords[4], coords[5]));
                    break;
                case PathIterator.SEG_CLOSE:
                    break;
            }
            pathIter.next();
        }
        Collections.reverse(segTypes);
        Collections.reverse(points);
        Path2D reversedPath = new Path2D.Double(path.getWindingRule());
        Point2D pt1 = null;
        Point2D pt2;
        Point2D pt3;
        Point2D currentPoint = null;
        Point2D startingPoint = null;
        boolean starting = true;
        int idx = 0;
        for (int segType : segTypes) {
            if (starting) {
                currentPoint = points.get(idx);
                idx++;
                reversedPath.moveTo(currentPoint.getX(), currentPoint.getY());
                startingPoint = currentPoint;
                starting = false;
            }
            switch (segType) {
                case PathIterator.SEG_MOVETO:
                    if (idx < points.size()) {
                        pt1 = points.get(idx);
                        idx++;
                        reversedPath.moveTo(pt1.getX(), pt1.getY());
                        startingPoint = pt1;
                    }
                    break;
                case PathIterator.SEG_LINETO:
                    if (idx < points.size()) {
                        pt1 = points.get(idx);
                        idx++;
                    }
                    else {
                        pt1 = startingPoint;
                    }
                    reversedPath.lineTo(pt1.getX(), pt1.getY());
                    break;
                case PathIterator.SEG_QUADTO:
                    pt1 = points.get(idx);
                    idx++;
                    if (idx < points.size()) {
                        pt2 = points.get(idx);
                        idx++;
                    }
                    else {
                        pt2 = startingPoint;
                    }
                    reversedPath.quadTo(pt1.getX(), pt1.getY(), pt2.getX(), pt2.getY());
                    break;
                case PathIterator.SEG_CUBICTO:
                    pt1 = points.get(idx);
                    idx++;
                    pt2 = points.get(idx);
                    idx++;
                    if (idx < points.size()) {
                        pt3 = points.get(idx);
                        idx++;
                    }
                    else {
                        pt3 = startingPoint;
                    }
                    reversedPath.curveTo(pt1.getX(), pt1.getY(), pt2.getX(), pt2.getY(), pt3.getX(), pt3.getY());
                    break;
                case PathIterator.SEG_CLOSE:
                    pt1 = points.get(idx);
                    idx++;
                    reversedPath.lineTo(pt1.getX(), pt1.getY());
                    startingPoint = currentPoint;
                    break;
            }
            currentPoint = pt1;
        }
        return reversedPath;
    }
    
    /**
     * Evaluates a math expression in string form to obtain its numerical value
     * @see <a href="https://stackoverflow.com/questions/3422673/how-to-evaluate-a-math-expression-
     * given-in-string-form">StackOverflow how-to-evaluate-a-math-expression-given-in-string-form</a>
     * @param str - the math expression as a string
     * @return - the numerical value of the expression
     */
    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;
            
            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }
            
            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }
            
            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }
            
            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)` | number
            //        | functionName `(` expression `)` | functionName factor
            //        | factor `^` factor
            
            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }
            
            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }
            
            double parseFactor() {
                if (eat('+')) return +parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus
                
                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    if (!eat(')')) throw new RuntimeException("Missing ')'");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    if (eat('(')) {
                        x = parseExpression();
                        if (!eat(')')) throw new RuntimeException("Missing ')' after argument to " + func);
                    } else {
                        x = parseFactor();
                    }
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }
                
                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation
                
                return x;
            }
        }.parse();
    }

}
