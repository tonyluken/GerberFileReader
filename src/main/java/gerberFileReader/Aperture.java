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

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import gerberFileReader.StrokeInfo;
import gerberFileReader.Utils.ArcParameters;

/**
 * A class to represent a Gerber Aperture
 * 
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-
 * specification-revision-2024-05_en.pdf?94b45d8745c1a068fd091f095a26ddeb#page=49">
 * Section 4.3 of the Gerber Layer Format Specification</a>
 */
class Aperture extends GraphicsStream {
    protected ApertureType type;
    protected String id;
    
    /**
     * Default constructor
     */
    Aperture() {
        
    }
    
    /**
     * Parses the Aperture Definition command (see section 4.3.1) and constructs the new aperture by 
     * finding the requested template name in the aperture template dictionary, instantiating the 
     * template, and attaching all aperture attributes contained in the attribute dictionary.
     * 
     * @param command - an Aperture Definition command, such as: "ADD10C,.025"
     * @param templateDictionary - the template dictionary
     * @param attributeDictionary - the attribute dictionary
     * @throws GerberLayerFormatException if the command passed does not begin with "AD" or if the 
     * aperture id is not valid or if the requested aperture template is not found in the template 
     * dictionary
     */
    Aperture(String command, ApertureTemplateDictionary templateDictionary, 
            AttributeDictionary attributeDictionary) throws GerberLayerFormatException {
        String cmd = command.substring(0, 2);
        if (!cmd.equals("AD")) {
            throw new GerberLayerFormatException(
                    "Aperture must be defined with an AD command but found: " + command);
        }
        int ne = Utils.numberEndIndex(command, 3);
        if (command.charAt(2) != 'D' || ne < 0) {
            throw new GerberLayerFormatException("Aperture id not valid: " + command);
        }
        id = command.substring(3, ne);
        try {
            int idNum = Integer.parseInt(id);
            if (idNum < 10) {
                throw new GerberLayerFormatException("Aperture id not valid: " + command);
            }
        }
        catch (NumberFormatException ex) {
            throw new GerberLayerFormatException("Aperture id not valid: " + command);
        }
        String templateName;
        int te = command.indexOf(",");
        if (te != -1) {
            templateName = command.substring(ne, te);
        }
        else {
            templateName = command.substring(ne);            
        }
        ApertureTemplate template = templateDictionary.get(templateName);
        if (template == null) {
            throw new GerberLayerFormatException("Aperture template name '" + templateName
                    + "' not found in aperture template dictionary");
        }
        type = template.getType();
        List<Double> params = new ArrayList<>();
        if (te != -1) {
            StringTokenizer st = new StringTokenizer(command.substring(te+1), "X");
            while (st.hasMoreTokens()) {
                try {
                    params.add(Double.parseDouble(st.nextToken()));
                }
                catch (NumberFormatException ex) {
                    throw new GerberLayerFormatException("Invalid Aperture parameter value: " + command);
                }
            }
        }
        Aperture aperture = template.instantiate(params, attributeDictionary.getAllOf(AttributeType.APERTURE));
        put(aperture);
    }
    
    /**
     * 
     * @return the type of the aperture
     */
    public ApertureType getType() {
        return type;
    }
    
    /**
     * 
     * @return the id of the aperture
     */
    public String getId() {
        return id;
    }
    
    /**
     * Plots either a linear or a circular stroke (depending on the current graphic state) using 
     * this aperture as a pen. The resulting graphic object has the aperture's attributes attached 
     * to it as well as any object attributes that are currently in the attribute dictionary.
     * 
     * @param graphicState - the current graphic state (see section 2.6)
     * @param newPoint - the end point of the stroke
     * @param attributeDictionary - the attribute dictionary
     * @return a GraphicStream containing the GraphicObject generated by the stroke
     * @throws GerberLayerFormatException if an aperture other than a Circle is used to stroke an 
     * arc or if an aperture other than a Circle or a Rectangle is used to stroke a linear segment.
     */
    public GraphicsStream plot(GraphicsState graphicState, Point newPoint, 
            AttributeDictionary attributeDictionary) throws GerberLayerFormatException {
        AttributeDictionary objAtt = new AttributeDictionary();
        for (GraphicalObject go : getStream()) {
            objAtt.putAll(go.getAttributes());
        }
        objAtt.putAll(attributeDictionary.getAllOf(AttributeType.OBJECT));

        Rectangle2D bounds = getStream().get(0).getArea().getBounds2D();
        Point currentPoint = graphicState.getCurrentPoint();
        GraphicsStream ret = new GraphicsStream();
        if (graphicState.getPlotState() == PlotState.LINEAR) {
            Shape stroke = new Line2D.Double(currentPoint.x, currentPoint.y, 
                    newPoint.x, newPoint.y);
            MetaData metaData = new MetaData(new StrokeInfo(new Path2D.Double(stroke)));
            if (getType() == ApertureType.CIRCLE) {
                double diam = Math.max(bounds.getWidth() * graphicState.getApertureTransformation()
                        .getScale(), 1e-7);
                BasicStroke s1 = new BasicStroke((float) diam, BasicStroke.CAP_ROUND, 
                        BasicStroke.JOIN_ROUND);
                Shape shape = s1.createStrokedShape(stroke);
                ret.put(new GraphicalObject(new Area(shape), graphicState
                        .getApertureTransformation().getPolarity(), objAtt, metaData));
            }
            else if (getType() == ApertureType.RECTANGLE) { //Deprecated, but we handle it
                double width = bounds.getWidth();
                double height = bounds.getHeight();
                //Compute the corners of the rectangle
                double[] cornerPoints = new double[] {
                        -width/2, -height/2,
                        -width/2,  height/2, 
                         width/2,  height/2, 
                         width/2, -height/2};
                //Compute the corners of the rectangle at the current point (the start)
                double[] cornerPointsStart = new double[8];
                AffineTransform atStart = new AffineTransform();
                atStart.translate(currentPoint.x, currentPoint.y);
                atStart.concatenate(graphicState.getApertureTransformation().getApertureTransform());
                atStart.transform(cornerPoints, 0, cornerPointsStart, 0, 4);
                //Compute the corners of the rectangle at the new point (the end)
                double[] cornerPointsEnd = new double[8];
                AffineTransform atEnd = new AffineTransform();
                atEnd.translate(newPoint.x, newPoint.y);
                atEnd.concatenate(graphicState.getApertureTransformation().getApertureTransform());
                atEnd.transform(cornerPoints, 0, cornerPointsEnd, 0, 4);
                //Equation of the line through the current and new points is a*x + b*y + c = 0 where:
                double a = currentPoint.y - newPoint.y;
                double b = newPoint.x - currentPoint.x;
                double c = newPoint.y*(currentPoint.x - newPoint.x) - 
                        newPoint.x*(currentPoint.y - newPoint.y);
                //Figure out which corners of the rectangle are farthest from the line, these set 
                //the side of the stroke.
                double corners0And2Distance = Math.abs(a*cornerPointsStart[0] + b*cornerPointsStart[1] + c);
                double corners1And3Distance = Math.abs(a*cornerPointsStart[2] + b*cornerPointsStart[3] + c);
                Path2D.Double path = new Path2D.Double();
                if (corners0And2Distance >= corners1And3Distance) {
                    //0 and 2 are the farthest from the line
                    //Now figure out which of the other corners is farthest from the opposite end
                    if (Math.hypot(cornerPointsStart[2]-newPoint.x, cornerPointsStart[3]-newPoint.y) >= 
                            Math.hypot(cornerPointsStart[6]-newPoint.x, cornerPointsStart[7]-newPoint.y)) {
                        //s1 is the farthest from the other end, so we go s1, s0, e0, e3, e2, s2, s1
                        path.moveTo(cornerPointsStart[2], cornerPointsStart[3]);
                        path.lineTo(cornerPointsStart[0], cornerPointsStart[1]);
                        path.lineTo(cornerPointsEnd[0], cornerPointsEnd[1]);
                        path.lineTo(cornerPointsEnd[6], cornerPointsEnd[7]);
                        path.lineTo(cornerPointsEnd[4], cornerPointsEnd[5]);
                        path.lineTo(cornerPointsStart[4], cornerPointsStart[5]);
                    }
                    else {
                        //s3 is the farthest from the other end, so we go s3, s0, e0, e1, e2, s2, s3 
                        path.moveTo(cornerPointsStart[6], cornerPointsStart[7]);
                        path.lineTo(cornerPointsStart[0], cornerPointsStart[1]);
                        path.lineTo(cornerPointsEnd[0], cornerPointsEnd[1]);
                        path.lineTo(cornerPointsEnd[2], cornerPointsEnd[3]);
                        path.lineTo(cornerPointsEnd[4], cornerPointsEnd[5]);
                        path.lineTo(cornerPointsStart[4], cornerPointsStart[5]);
                    }
                }
                else {
                    //1 and 3 are the farthest from the line
                    //Now figure out which of the other corners is farthest from the opposite end
                    if (Math.hypot(cornerPointsStart[0]-newPoint.x, cornerPointsStart[1]-newPoint.y) >= 
                            Math.hypot(cornerPointsStart[4]-newPoint.x, cornerPointsStart[5]-newPoint.y)) {
                        //s0 is the farthest from the other end, so we go s0, s1, e1, e2, e3, s3, s0
                        path.moveTo(cornerPointsStart[0], cornerPointsStart[1]);
                        path.lineTo(cornerPointsStart[2], cornerPointsStart[3]);
                        path.lineTo(cornerPointsEnd[2], cornerPointsEnd[3]);
                        path.lineTo(cornerPointsEnd[4], cornerPointsEnd[5]);
                        path.lineTo(cornerPointsEnd[6], cornerPointsEnd[7]);
                        path.lineTo(cornerPointsStart[6], cornerPointsStart[7]);
                    }
                    else {
                        //s2 is the farthest from the other end, so we go s2, s1, e1, e0, e3, s3, s2
                        path.moveTo(cornerPointsStart[4], cornerPointsStart[5]);
                        path.lineTo(cornerPointsStart[2], cornerPointsStart[3]);
                        path.lineTo(cornerPointsEnd[2], cornerPointsEnd[3]);
                        path.lineTo(cornerPointsEnd[0], cornerPointsEnd[1]);
                        path.lineTo(cornerPointsEnd[6], cornerPointsEnd[7]);
                        path.lineTo(cornerPointsStart[6], cornerPointsStart[7]);
                    }
                }
                path.closePath();
                ret.put(new GraphicalObject(new Area(path), 
                        graphicState.getApertureTransformation().getPolarity(), objAtt, metaData));
                }
            else {
                throw new GerberLayerFormatException("Plotting lines with " + getType() + 
                        " apertures is not supported");
            }
        }
        else {
            if (getType() != ApertureType.CIRCLE) {
                throw new GerberLayerFormatException("Plotting arcs with " + getType() + 
                        " apertures is not supported");
            }
            
            ArcParameters arcParams = Utils.computeArcParameters(graphicState, newPoint);
            if (arcParams.extentAngleDeg == 0) {
                return flash(graphicState, newPoint, attributeDictionary);
            }
            Shape stroke = new Arc2D.Double(arcParams.center.x - arcParams.radius,
                    arcParams.center.y - arcParams.radius, 
                    2*arcParams.radius, 2*arcParams.radius, arcParams.startAngleDeg, 
                    arcParams.extentAngleDeg, Arc2D.OPEN);
            MetaData metaData = new MetaData(new StrokeInfo(new Path2D.Double(stroke)));
            // Stroke the arc
            double diam = bounds.getWidth() * graphicState.getApertureTransformation().getScale();
            BasicStroke s1 = new BasicStroke((float) diam, BasicStroke.CAP_ROUND, 
                    BasicStroke.JOIN_ROUND);
            Shape shape = s1.createStrokedShape(stroke);
            ret.put(new GraphicalObject(new Area(shape), 
                    graphicState.getApertureTransformation().getPolarity(), objAtt, metaData));
        }

        return ret;
    }
    
    /**
     * Generates one or more graphical objects by applying the aperture transformations that are 
     * currently in effect to a copy of this aperture. The resulting graphical objects are 
     * translated so that the point corresponding to the aperture's center is located at the 
     * flashPoint. The aperture's attributes as well as any object attributes that are currently in 
     * the attribute dictionary are attached to the new graphical objects.
     * 
     * @param graphicState - the current graphic state (see section 2.6)
     * @param flashPoint - the location of the flash
     * @param attributeDictionary - the attribute dictionary
     * @return a GraphicStream containing the GraphicalObjects generated by the flash
     * @throws GerberLayerFormatException if the aperture transformation has not been set
     */
    public GraphicsStream flash(GraphicsState graphicState, Point flashPoint, 
            AttributeDictionary attributeDictionary) throws GerberLayerFormatException {
        return graphicState.getApertureTransformation()
                .apply(flashPoint, this, attributeDictionary);
    }
}
