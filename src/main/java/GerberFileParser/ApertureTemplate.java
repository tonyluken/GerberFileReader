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

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A class for defining parameterized aperture templates
 * 
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-
 * specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=21">
 * Section 2.10 of the Gerber Layer Format Specification</a>
 */
public class ApertureTemplate {
    private final ApertureType type;
    private final String templateName;
    private final List<String> macroCommands;
    
    /**
     * Constructs one of any of the four standard aperture templates or a macro aperture template
     * 
     * @param templateName - the name of the template to construct. If one of "C", "R", "O" or "P" 
     * a standard aperture template is constructed (Circle, Rectangle, Obround or Polygon 
     * respectively). Any other name and a macro aperture template is constructed.
     * 
     * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-
     * specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=50">
     * Section 4.4 of the Gerber Specification</a>
     * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-
     * specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=56">
     * Section 4.5 of the Gerber Specification</a>
     */
    ApertureTemplate(String templateName) {
        this.templateName = templateName;
        if (templateName == "C") {
            type = ApertureType.Circle;
            macroCommands = null;
        }
        else if (templateName == "R") {
            type = ApertureType.Rectangle;
            macroCommands = null;
        }
        else if (templateName == "O") {
            type = ApertureType.Obround;
            macroCommands = null;
        }
        else if (templateName == "P") {
            type = ApertureType.Polygon;
            macroCommands = null;
        }
        else {
            type = ApertureType.Macro;
            macroCommands = new ArrayList<>();
        }
    }

    /**
     * 
     * @return the type of this aperture template
     */
    public ApertureType getType() {
        return type;
    }

    /**
     * 
     * @return the name of this aperture template
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * 
     * @return the list of macro commands if this is a macro aperture template, otherwise null
     */
    public List<String> getMacroCommands() {
        return macroCommands;
    }
    
    /**
     * Adds a macro command to this aperture template
     * 
     * @param macroCommand - the macro command to add
     * @throws Exception if this isn't a macro aperture template
     */
    public void addMacroCommand(String macroCommand) throws Exception {
        if (type == ApertureType.Macro) {
            macroCommands.add(macroCommand);
        }
        else {
            throw new Exception("Can't add a command to a standard aperture template");
        }
    }

    /**
     * Creates an aperture based on this template and the given parameters
     * 
     * @param params - the parameter values to use when creating the aperture
     * @param attributes - the aperture attributes to attach to the aperture
     * @return the new aperture
     * @throws Exception if the incorrect number of parameters is supplied
     */
    public GraphicsStream instantiate(List<Double> params, AttributeDictionary attributes) throws Exception {
        GraphicsStream ret = new GraphicsStream();
        int nParams = params.size();
        int holeIndex = 0;
        Shape appShape = null;
        switch (type) {
            case Circle:
                if (nParams >= 1 && nParams <= 2) {
                    double dia = params.get(0);
                    appShape = new Ellipse2D.Double(-dia / 2, -dia / 2, dia, dia);
                    holeIndex = params.size() > 1 ? 1 : 0;
                }
                else {
                    throw new Exception("Incorrect number (" + nParams + ") of parameters for a standard Circle aperture");
                }
                break;
            case Rectangle:
                if (nParams >= 2 && nParams <= 3) {
                    double width = params.get(0);
                    double height = params.get(1);
                    appShape = new Rectangle2D.Double(-width / 2, -height / 2, width, height);
                    holeIndex = params.size() > 2 ? 2 : 0;
                }
                else {
                    throw new Exception("Incorrect number (" + nParams + ") of parameters for a standard Rectangle aperture");
                }
                break;
            case Obround:
                if (nParams >= 2 && nParams <= 3) {
                    double width = params.get(0);
                    double height = params.get(1);
                    double eRadius = Math.min(width, height);
                    appShape = new RoundRectangle2D.Double(-width / 2, -height / 2, width, height, eRadius, eRadius);
                    holeIndex = params.size() > 2 ? 2 : 0;
                }
                else {
                    throw new Exception("Incorrect number (" + nParams + ") of parameters for a standard Obround aperture");
                }
                break;
            case Polygon:
                if (nParams >= 2 && nParams <= 4) {
                    double radius = params.get(0) / 2.0;
                    int sides = params.get(1).intValue();
                    double start = 0;
                    if (params.size() > 2) {
                        //Optional start angle specified
                        start = params.get(2);
                    }
                    Path2D.Double path = new Path2D.Double();
                    boolean first = true;
                    for (int ii = 0; ii < sides; ii++) {
                      double cx = radius * Math.cos(Math.toRadians(start + 360.0 * ii / sides));
                      double cy = radius * Math.sin(Math.toRadians(start + 360.0 * ii / sides));
                      if (first) {
                        path.moveTo(cx, cy);
                      } else {
                        path.lineTo(cx, cy);
                      }
                      first = false;
                    }
                    path.closePath();
                    appShape = path;
                    holeIndex = params.size() > 3 ? 3 : 0;
                }
                else {
                    throw new Exception("Incorrect number (" + nParams + ") of parameters for a standard Polygon aperture");
                }
                break;
            case Macro:
                appShape = createMacroAperture(params);
                break;
            default:
                throw new Exception("Unexpected Aperture Template Type: " + type);
        }
        Area appArea = new Area(appShape);
        if (holeIndex > 0) {
          // Create a round hole in the aperture - note that the hole is transparent - see section 4.4.6 
          double diam = params.get(holeIndex);
          Shape hole = new Ellipse2D.Double(-diam / 2, -diam / 2, diam, diam);
          appArea.subtract(new Area(hole));
        }
        ret.put(new GraphicalObject(appArea, Polarity.Dark, attributes, 
                new MetaData(new StrokeInfo(new Point2D.Double(0, 0)))));
        return ret;
    }
    
    /**
     * Creates a Java AWT Shape by evaluating the macro commands 
     * @param params - the list of parameters for this macro aperture template
     * @return the Shape
     * @throws Exception if an error occurs during evaluation of the macro commands
     */
    private Shape createMacroAperture(List<Double> params) throws Exception {
        Area retShape = new Area();
        Shape appShape = null;
        Map<String, String> vars = new LinkedHashMap<>();
        for (int i = 1; i <= params.size(); i++) {
            vars.put("$" + Integer.toString(i), Double.toString(params.get(i - 1)));
        }
        for (String macroCommand : macroCommands) {
            if (macroCommand.startsWith("0 ")) {
                // Skip over a comment within the macro (4.5.4.1)
            }
            else if (macroCommand.startsWith("$")) {
                // Process equation within the macro
                int idx = macroCommand.indexOf("=");
                String putString = vars.putIfAbsent(macroCommand.substring(0, idx),
                        macroCommand.substring(idx + 1));
                if (putString != null && !putString.equals(macroCommand.substring(idx + 1))) {
                    throw new Exception(
                            "Attempt to redefine macro variable " + macroCommand.substring(0, idx));
                }
            }
            else {
                // Process one of the primitives defined in the Macro
                String[] commandParamStrings = macroCommand.split(",");
                List<Double> commandParams = new ArrayList<>();
                // Scan for variable expressions in the macro command's parameters and evaluate them
                for (String mParm : commandParamStrings) {
                    commandParams.add(evaluateExpression(mParm, vars));
                }

                int primType = Integer.parseInt(commandParamStrings[0]);
                double exposure = 1.0;

                switch (primType) {
                    case 1: // Circle Primitive (4.5.4.2)
                        //Exposure, Diameter, Center X, Center Y[, Rotation]
                        if (commandParams.size() < 5 || commandParams.size() > 6) {
                            throw new Exception("Invalid number of parameters for a Circle primitive");
                        }
                        exposure = commandParams.get(1);
                        double aDia = commandParams.get(2);
                        double aX = commandParams.get(3);
                        double aY = commandParams.get(4);
                        double rot = 0;
                        if (commandParams.size() > 5) {
                            // Optional rotation is specified and rotates about the macro center 
                            // not the center of the circle
                            rot = commandParams.get(5);
                        }
                        Shape circle =
                                new Ellipse2D.Double(aX - aDia / 2, aY - aDia / 2, aDia, aDia);
                        AffineTransform at = new AffineTransform();
                        at.rotate(Math.toRadians(rot));
                        appShape = at.createTransformedShape(circle);
                        break;
                    case 4: // Outline Primitive (4.5.4.5)
                        //Exposure, # vertices, Start X, Start Y, Subsequent points..., Rotation
                        if (commandParams.size() < 8) {
                            throw new Exception("Invalid number of parameters for an Outline primitive");
                        }
                        exposure = commandParams.get(1);
                        int nVertices = commandParams.get(2).intValue() + 1; // Ends with the start point
                        if (commandParams.size() != 6 + (nVertices - 1) * 2) {
                            throw new Exception("Invalid number of parameters for an Outline primitive");
                        }
                        Path2D.Double outline = new Path2D.Double();
                        for (int ii = 0; ii < nVertices; ii++) {
                            if (ii == 0) {
                                outline.moveTo(commandParams.get(2 * ii + 3),
                                        commandParams.get(2 * ii + 4));
                            }
                            else {
                                outline.lineTo(commandParams.get(2 * ii + 3),
                                        commandParams.get(2 * ii + 4));
                            }
                        }
                        outline.closePath();
                        rot = commandParams.get(5 + (nVertices - 1) * 2);
                        at = new AffineTransform();
                        at.rotate(Math.toRadians(rot));
                        appShape = at.createTransformedShape(outline);
                        break;
                    case 5: // Polygon Primitive (4.5.4.6)
                        // Exposure, # vertices, Center X, Center Y, Diameter, Rotation
                        if (commandParams.size() != 7) {
                            throw new Exception("Invalid number of parameters for a Polygon primitive");
                        }
                        exposure = commandParams.get(1);
                        double sides = commandParams.get(2);
                        double cX = commandParams.get(3);
                        double cY = commandParams.get(4);
                        double radius = commandParams.get(5) / 2.0;
                        rot = commandParams.get(6);
                        Path2D.Double path = new Path2D.Double();
                        for (int ii = 0; ii < sides; ii++) {
                            double cx = cX + radius * Math.cos(Math.toRadians(360.0 * ii / sides));
                            double cy = cY + radius * Math.sin(Math.toRadians(360.0 * ii / sides));
                            if (ii == 0) {
                                path.moveTo(cx, cy);
                            }
                            else {
                                path.lineTo(cx, cy);
                            }
                        }
                        path.closePath();
                        at = new AffineTransform();
                        at.rotate(Math.toRadians(rot));
                        appShape = at.createTransformedShape(path);
                        break;
                    case 6: // Moire Primitive (4.5.4.7)
                        if (commandParams.size() != 10) {
                            throw new Exception("Invalid number of parameters for a Moire primitive");
                        }
                        exposure = 1;
                        cX = commandParams.get(1);
                        cY = commandParams.get(2);
                        double outDia = commandParams.get(3);
                        double ringThickness = commandParams.get(4);
                        double ringGap = commandParams.get(5);
                        int maxRings = commandParams.get(6)
                                                    .intValue();
                        double crosshairThickness = commandParams.get(7);
                        double crosshairLength = commandParams.get(8);
                        rot = commandParams.get(9);
                        Area moire = new Area();
                        int ringCount = 0;
                        while (outDia > 0 && ringCount < maxRings) {
                            moire.add(new Area(new Ellipse2D.Double(cX - outDia / 2,
                                    cY - outDia / 2, outDia, outDia)));
                            outDia -= 2 * ringThickness;
                            if (outDia > 0) {
                                moire.subtract(new Area(new Ellipse2D.Double(cX - outDia / 2,
                                        cY - outDia / 2, outDia, outDia)));
                            }
                            outDia -= 2 * ringGap;
                            ringCount++;
                        }
                        BasicStroke s1 = new BasicStroke((float) crosshairThickness,
                                BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
                        moire.add(new Area(s1.createStrokedShape(new Line2D.Double(cX,
                                cY - crosshairLength / 2, cX, cY + crosshairLength / 2))));
                        moire.add(new Area(s1.createStrokedShape(new Line2D.Double(
                                cX - crosshairLength / 2, cY, cX + crosshairLength / 2, cY))));
                        at = new AffineTransform();
                        at.rotate(Math.toRadians(rot));
                        appShape = at.createTransformedShape(moire);
                        break;
                    case 7: // Thermal Primitive (4.5.4.8)
                        //Center X, Center Y, Outer diameter, Inner diameter, Gap, Rotation
                        if (commandParams.size() != 7) {
                            throw new Exception("Invalid number of parameters for a Thermal primitive");
                        }
                        exposure = 1;
                        cX = commandParams.get(1);
                        cY = commandParams.get(2);
                        outDia = commandParams.get(3);
                        double inDia = commandParams.get(4);
                        double gap = commandParams.get(5);
                        rot = commandParams.get(6);
                        Area thermal = new Area(new Ellipse2D.Double(cX - outDia / 2,
                                cY - outDia / 2, outDia, outDia));
                        thermal.subtract(new Area(new Ellipse2D.Double(cX - inDia / 2,
                                cY - inDia / 2, inDia, inDia)));
                        s1 = new BasicStroke((float) gap, BasicStroke.CAP_BUTT,
                                BasicStroke.JOIN_ROUND);
                        thermal.subtract(new Area(s1.createStrokedShape(
                                new Line2D.Double(cX, cY - outDia, cX, cY + outDia))));
                        thermal.subtract(new Area(s1.createStrokedShape(
                                new Line2D.Double(cX - outDia, cY, cX + outDia, cY))));
                        at = new AffineTransform();
                        at.rotate(Math.toRadians(rot));
                        appShape = at.createTransformedShape(thermal);
                        break;
                    case 2: // Same as Vector Line (deprecated)
                    case 20: // Vector Line Primitive (4.5.4.3)
                        // Exposure, Width, Start X, Start Y, End X, End Y, Rotation
                        if (commandParams.size() != 8) {
                            throw new Exception("Invalid number of parameters for a Vector Line primitive");
                        }
                        exposure = commandParams.get(1);
                        double width = commandParams.get(2);
                        aX = commandParams.get(3);
                        aY = commandParams.get(4);
                        double bX = commandParams.get(5);
                        double bY = commandParams.get(6);
                        rot = commandParams.get(7);
                        s1 = new BasicStroke((float) width, BasicStroke.CAP_BUTT,
                                BasicStroke.JOIN_ROUND);
                        Shape vline = s1.createStrokedShape(new Line2D.Double(aX, aY, bX, bY));
                        at = new AffineTransform();
                        at.rotate(Math.toRadians(rot));
                        appShape = at.createTransformedShape(vline);
                        break;
                    case 21: // Center Line Primitive (4.5.4.4)
                        if (commandParams.size() != 7) {
                            throw new Exception("Invalid number of parameters for a Center Line primitive");
                        }
                        exposure = commandParams.get(1);
                        double aWid = commandParams.get(2);
                        double aHyt = commandParams.get(3);
                        aX = commandParams.get(4);
                        aY = commandParams.get(5);
                        rot = commandParams.get(6);
                        s1 = new BasicStroke((float) aHyt, BasicStroke.CAP_BUTT,
                                BasicStroke.JOIN_ROUND);
                        Shape cline = s1.createStrokedShape(new Line2D.Double(aX - aWid/2, aY, 
                                aX + aWid/2, aY));
                        at = new AffineTransform();
                        at.rotate(Math.toRadians(rot));
                        appShape = at.createTransformedShape(cline);
                        break;
                    case 22: // Lower Left Line Primitive (deprecated 8.25)
                        // Exposure, Width, Height, Lower Left X, Lower Left Y, Rotation
                        if (commandParams.size() != 7) {
                            throw new Exception("Invalid number of parameters for a Lower Left Line primitive");
                        }
                        exposure = commandParams.get(1);
                        width = commandParams.get(2);
                        double height = commandParams.get(3);
                        aX = commandParams.get(4);
                        aY = commandParams.get(5);
                        rot = commandParams.get(6);
                        s1 = new BasicStroke((float) height, BasicStroke.CAP_BUTT,
                                BasicStroke.JOIN_ROUND);
                        Shape llline = s1.createStrokedShape(new Line2D.Double(aX, aY+height/2, 
                                aX+width, aY+height/2));
                        at = new AffineTransform();
                        at.rotate(Math.toRadians(rot));
                        appShape = at.createTransformedShape(llline);
                        break;
                    default:
                        throw new Exception("Unsupported macro primitive type " + primType);
                }

                if (exposure > 0) {
                    retShape.add(new Area(appShape));
                }
                else {
                    retShape.subtract(new Area(appShape));
                }
            }
        }
        return retShape;
    }

    /**
     * Evaluates a string that represents a mathematical expression as defined in section 4.5.4
     * 
     * @param strExp - the expression to evaluate
     * @param vars - a mapping of variable names to values, if strExp contains a variable and that
     * variable exists in vars, its value is substituted into the expression before it is evaluated
     * @return the numeric value of the expression
     */
    private double evaluateExpression(String strExp, Map<String, String> vars) {
        //First sort the known variables from longest length to shortest length
        String[] varArray = {""};
        varArray = vars.keySet().toArray(varArray);
        Arrays.sort(varArray, new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                if (o1.length() > o2.length()) {
                    return -1;
                }
                else if (o1.length() < o2.length()) {
                    return +1;
                }
                return 0;
            }});
        
        //Now substitute the variable values into the expression until the expression no longer changes.
        //We start with the longest variable names first so that shorter ones don't overwrite longer
        //ones, for example, $14 needs to be substituted before $1.
        String strExpPrev = "";
        while (strExp.contains("$") && !strExp.equals(strExpPrev)) {
            strExpPrev = strExp;
            for (String var : varArray) {
                if (strExp.contains(var)) {
                    String repString = "(" + vars.get(var) + ")";
                    strExp = strExp.replaceAll("\\" + var, 
                            java.util.regex.Matcher.quoteReplacement(repString));
                }
                if (!strExp.contains("$")) {
                    break;
                }
            }
        }
        
        //Any remaining variables are undefined and take on the value of 0, see section 4.5.4.3
        while (strExp.contains("$")) {
            int idxs = strExp.indexOf("$");
            int idxe = idxs+1;
            while (idxe < strExp.length() && ("0123456789".contains(strExp.subSequence(idxe, idxe+1)))) {
                idxe++;
            }
            String var = strExp.substring(idxs, idxe);
            strExp = strExp.replaceAll("\\"+var, java.util.regex.Matcher.quoteReplacement("0"));
        }
        
        //Replace the gerber multiplication operator 'x' with the more usual multiplication operator '*'
        strExp = strExp.replaceAll("x", java.util.regex.Matcher.quoteReplacement("*"));
        
        //Now evaluate the mathematical expression
        return Utils.eval(strExp);
    }

}
