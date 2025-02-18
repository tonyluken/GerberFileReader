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
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import stringMathExpressionEvaluator.InvalidMathExpressionException;
import stringMathExpressionEvaluator.StringMathExpressionEvaluator;

/**
 * A class for defining parameterized aperture templates
 * 
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-
 * specification-revision-2024-05_en.pdf?94b45d8745c1a068fd091f095a26ddeb#page=22">
 * Section 2.10 of the Gerber Layer Format Specification</a>
 */
class ApertureTemplate {
    private final ApertureType type;
    private final String templateName;
    private final List<String> macroCommands;
    
    /**
     * Constructs one of any of the four standard aperture templates or a macro aperture template
     * 
     * @param templateName - the name of the template to construct. If one of "C", "R", "O" or "P" 
     * a standard aperture template is constructed (CIRCLE, RECTANGLE, OBROUND or POLYGON 
     * respectively). Any other name and a macro aperture template is constructed.
     * 
     * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2024-05_en.pdf?94b45d8745c1a068fd091f095a26ddeb#page=51">
     * Section 4.4 of the Gerber Specification</a>
     * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2024-05_en.pdf?94b45d8745c1a068fd091f095a26ddeb#page=57">
     * Section 4.5 of the Gerber Specification</a>
     */
    ApertureTemplate(String templateName) {
        this.templateName = templateName;
        if (templateName == "C") {
            type = ApertureType.CIRCLE;
            macroCommands = null;
        }
        else if (templateName == "R") {
            type = ApertureType.RECTANGLE;
            macroCommands = null;
        }
        else if (templateName == "O") {
            type = ApertureType.OBROUND;
            macroCommands = null;
        }
        else if (templateName == "P") {
            type = ApertureType.POLYGON;
            macroCommands = null;
        }
        else {
            type = ApertureType.MACRO;
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
     * @throws GerberLayerFormatException if this isn't a macro aperture template
     */
    public void addMacroCommand(String macroCommand) throws GerberLayerFormatException {
        if (type == ApertureType.MACRO) {
            validateMacroCommand(macroCommand);
            macroCommands.add(macroCommand);
        }
        else {
            throw new GerberLayerFormatException(
                    "Can't add a command to a standard aperture template");
        }
    }

    /**
     * Creates an aperture based on this template and the given parameters
     * 
     * @param params - the parameter values to use when creating the aperture
     * @param attributes - the aperture attributes to attach to the aperture
     * @return the new aperture
     * @throws GerberLayerFormatException if the incorrect number of parameters is supplied
     */
    public Aperture instantiate(List<Double> params, AttributeDictionary attributes) 
            throws GerberLayerFormatException {
        Aperture ret = new Aperture();
        int nParams = params.size();
        int holeIndex = 0;
        Shape appShape = null;
        switch (type) {
            case CIRCLE:
                if (nParams >= 1 && nParams <= 2) {
                    double dia = params.get(0);
                    appShape = new Ellipse2D.Double(-dia / 2, -dia / 2, dia, dia);
                    holeIndex = params.size() > 1 ? 1 : 0;
                }
                else {
                    throw new GerberLayerFormatException("Incorrect number (" + nParams + 
                            ") of parameters for a standard Circle aperture");
                }
                break;
            case RECTANGLE:
                if (nParams >= 2 && nParams <= 3) {
                    double width = params.get(0);
                    double height = params.get(1);
                    appShape = new Rectangle2D.Double(-width / 2, -height / 2, width, height);
                    holeIndex = params.size() > 2 ? 2 : 0;
                }
                else {
                    throw new GerberLayerFormatException("Incorrect number (" + nParams + 
                            ") of parameters for a standard Rectangle aperture");
                }
                break;
            case OBROUND:
                if (nParams >= 2 && nParams <= 3) {
                    double width = params.get(0);
                    double height = params.get(1);
                    double cornerDia = Math.min(width, height);
                    appShape = new RoundRectangle2D.Double(-width / 2, -height / 2, width, height, 
                            cornerDia, cornerDia);
                    holeIndex = params.size() > 2 ? 2 : 0;
                }
                else {
                    throw new GerberLayerFormatException("Incorrect number (" + nParams + 
                            ") of parameters for a standard Obround aperture");
                }
                break;
            case POLYGON:
                if (nParams >= 2 && nParams <= 4) {
                    double radius = params.get(0) / 2.0;
                    int sides = params.get(1).intValue();
                    if (sides < 3) {
                        throw new GerberLayerFormatException(
                                "Can't have a polygon with fewer than three sides");
                    }
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
                      } 
                      else {
                          path.lineTo(cx, cy);
                      }
                      first = false;
                    }
                    path.closePath();
                    appShape = path;
                    holeIndex = params.size() > 3 ? 3 : 0;
                }
                else {
                    throw new GerberLayerFormatException("Incorrect number of parameters (" + 
                            nParams + ") for a standard Polygon aperture");
                }
                break;
            case MACRO:
                appShape = createMacroAperture(params);
                break;
            default:
                throw new GerberLayerFormatException("Unexpected Aperture Template Type: " + type);
        }
        Area appArea = new Area(appShape);
        if (holeIndex > 0) {
          // Create a round hole in the aperture - note that the hole is transparent - see 
          // section 4.4.6 
          double diam = params.get(holeIndex);
          Shape hole = new Ellipse2D.Double(-diam / 2, -diam / 2, diam, diam);
          appArea.subtract(new Area(hole));
        }
        ret.put(new GraphicalObject(appArea, Polarity.DARK, attributes, 
                new MetaData(new StrokeInfo(new Point2D.Double(0, 0)))));
        return ret;
    }
    
    /**
     * Validates the macro command
     * @param macroCommand - the command to validate
     * @throws GerberLayerFormatException if the macro command is invalid
     */
    private void validateMacroCommand(String macroCommand) throws GerberLayerFormatException {
        Map<String, String> vars = new LinkedHashMap<>();
        if (macroCommand.startsWith("0 ")) {
            // Skip over a comment within the macro (4.5.4.1)
            return;
        }
        else if (macroCommand.startsWith("$")) {
            // Process equation within the macro
            int idx = macroCommand.indexOf("=");
            if (idx < 0) {
                throw new GerberLayerFormatException(
                        "Invalid use of macro variable: " + macroCommand);
            }
            String putString = vars.putIfAbsent(macroCommand.substring(0, idx),
                    macroCommand.substring(idx + 1));
            if (putString != null && !putString.equals(macroCommand.substring(idx + 1))) {
                throw new GerberLayerFormatException(
                        "Attempt to redefine macro variable " + macroCommand.substring(0, idx));
            }
        }
        else {
            // Process a Macro primitive
            String[] commandParamStrings = macroCommand.split(",");
            List<Double> commandParams = new ArrayList<>();
            // Scan for variable expressions in the macro command's parameters and evaluate them
            for (String mParm : commandParamStrings) {
                commandParams.add(evaluateExpression(mParm, vars));
            }

            int primType;
            try {
                primType = Integer.parseInt(commandParamStrings[0]);
            }
            catch (Exception ex) {
                throw new GerberLayerFormatException("Invalid macro primitive: " + macroCommand);
            }
            
            switch (primType) {
                case 1: // Circle Primitive (4.5.4.2)
                    //Exposure, Diameter, Center X, Center Y[, Rotation]
                    if (commandParams.size() < 5 || commandParams.size() > 6) {
                        throw new GerberLayerFormatException(
                                "Invalid number of parameters for a Circle primitive");
                    }
                    break;
                case 4: // Outline Primitive (4.5.4.5)
                    //Exposure, # vertices, Start X, Start Y, Subsequent points..., Rotation
                    if (commandParams.size() < 8) {
                        throw new GerberLayerFormatException(
                                "Invalid number of parameters for an Outline primitive");
                    }
                    // Number of vertices is one more because it ends with the start point
                    int nVertices = commandParams.get(2).intValue() + 1;
                    if (commandParams.size() != 6 + (nVertices - 1) * 2) {
                        throw new GerberLayerFormatException(
                                "Invalid number of parameters for an Outline primitive");
                    }
                    break;
                case 5: // Polygon Primitive (4.5.4.6)
                    // Exposure, # vertices, Center X, Center Y, Diameter, Rotation
                    if (commandParams.size() != 7) {
                        throw new GerberLayerFormatException(
                                "Invalid number of parameters for a Polygon primitive");
                    }
                    break;
                case 6: // Moire Primitive (4.5.4.7)
                    //Center X, Center Y, Outer Diameter, Ring Thickness, Ring Gap, Max Rings, 
                    //Crosshair Thickness, Crosshair Length, Rotation
                    if (commandParams.size() != 10) {
                        throw new GerberLayerFormatException(
                                "Invalid number of parameters for a Moire primitive");
                    }
                    break;
                case 7: // Thermal Primitive (4.5.4.8)
                    //Center X, Center Y, Outer diameter, Inner diameter, Gap, Rotation
                    if (commandParams.size() != 7) {
                        throw new GerberLayerFormatException(
                                "Invalid number of parameters for a Thermal primitive");
                    }
                    break;
                case 2: // Same as Vector Line (deprecated)
                case 20: // Vector Line Primitive (4.5.4.3)
                    // Exposure, Width, Start X, Start Y, End X, End Y, Rotation
                    if (commandParams.size() != 8) {
                        throw new GerberLayerFormatException(
                                "Invalid number of parameters for a Vector Line primitive");
                    }
                    break;
                case 21: // Center Line Primitive (4.5.4.4)
                    //Exposure, width, height, center X, center Y, rotation
                    if (commandParams.size() != 7) {
                        throw new GerberLayerFormatException(
                                "Invalid number of parameters for a Center Line primitive");
                    }
                    break;
                case 22: // Lower Left Line Primitive (deprecated 8.25)
                    // Exposure, Width, Height, Lower Left X, Lower Left Y, Rotation
                    if (commandParams.size() != 7) {
                        throw new GerberLayerFormatException(
                                "Invalid number of parameters for a Lower Left Line primitive");
                    }
                    break;
                default:
                    throw new GerberLayerFormatException(
                            "Unsupported macro primitive type " + primType);
            }
        }
    }

    /**
     * Creates a Java AWT Shape by evaluating the macro commands 
     * @param params - the list of parameters for this macro aperture template
     * @return the Shape
     * @throws GerberLayerFormatException if an error occurs during evaluation of the macro commands
     */
    private Shape createMacroAperture(List<Double> params) throws GerberLayerFormatException {
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
                    throw new GerberLayerFormatException(
                            "Attempt to redefine macro variable " + macroCommand.substring(0, idx));
                }
            }
            else {
                // Process a Macro primitive
                String[] commandParamStrings = macroCommand.split(",");
                List<Double> commandParams = new ArrayList<>();
                // Scan for variable expressions in the macro command's parameters and evaluate them
                for (String mParm : commandParamStrings) {
                    commandParams.add(evaluateExpression(mParm, vars));
                }

                int primType;
                try {
                    primType = Integer.parseInt(commandParamStrings[0]);
                }
                catch (Exception ex) {
                    throw new GerberLayerFormatException("Invalid macro primitive.");
                }
                double exposure = 1.0;
                double rot = 0;
                
                switch (primType) {
                    case 1: // Circle Primitive (4.5.4.2)
                        //Exposure, Diameter, Center X, Center Y[, Rotation]
                        if (commandParams.size() < 5 || commandParams.size() > 6) {
                            throw new GerberLayerFormatException(
                                    "Invalid number of parameters for a Circle primitive");
                        }
                        exposure = commandParams.get(1);
                        double aDia = commandParams.get(2);
                        double aX = commandParams.get(3);
                        double aY = commandParams.get(4);
                        
                        if (commandParams.size() > 5) {
                            // Optional rotation is specified and rotates about the macro center 
                            // not the center of the circle
                            rot = commandParams.get(5);
                        }
                        appShape = new Ellipse2D.Double(aX - aDia / 2, aY - aDia / 2, aDia, aDia);
                        break;
                    case 4: // Outline Primitive (4.5.4.5)
                        //Exposure, # vertices, Start X, Start Y, Subsequent points..., Rotation
                        if (commandParams.size() < 8) {
                            throw new GerberLayerFormatException(
                                    "Invalid number of parameters for an Outline primitive");
                        }
                        exposure = commandParams.get(1);
                        // Number of vertices is one more because it ends with the start point
                        int nVertices = commandParams.get(2).intValue() + 1;
                        if (commandParams.size() != 6 + (nVertices - 1) * 2) {
                            throw new GerberLayerFormatException(
                                    "Invalid number of parameters for an Outline primitive");
                        }
                        Path2D.Double outline = new Path2D.Double();
                        outline.moveTo(commandParams.get(3), commandParams.get(4));
                        for (int ii = 1; ii < nVertices; ii++) {
                            outline.lineTo(commandParams.get(2*ii + 3), commandParams.get(2*ii + 4));
                        }
                        outline.closePath();
                        rot = commandParams.get(5 + (nVertices - 1) * 2);
                        appShape = outline;
                        break;
                    case 5: // Polygon Primitive (4.5.4.6)
                        // Exposure, # vertices, Center X, Center Y, Diameter, Rotation
                        if (commandParams.size() != 7) {
                            throw new GerberLayerFormatException(
                                    "Invalid number of parameters for a Polygon primitive");
                        }
                        exposure = commandParams.get(1);
                        double sides = commandParams.get(2).intValue();
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
                        appShape = path;
                        break;
                    case 6: // Moire Primitive (4.5.4.7)
                        //Center X, Center Y, Outer Diameter, Ring Thickness, Ring Gap, Max Rings, 
                        //Crosshair Thickness, Crosshair Length, Rotation
                        if (commandParams.size() != 10) {
                            throw new GerberLayerFormatException(
                                    "Invalid number of parameters for a Moire primitive");
                        }
                        exposure = 1;
                        cX = commandParams.get(1);
                        cY = commandParams.get(2);
                        double outDia = commandParams.get(3);
                        double ringThickness = commandParams.get(4);
                        double ringGap = commandParams.get(5);
                        int maxRings = commandParams.get(6).intValue();
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
                        appShape = moire;
                        break;
                    case 7: // Thermal Primitive (4.5.4.8)
                        //Center X, Center Y, Outer diameter, Inner diameter, Gap, Rotation
                        if (commandParams.size() != 7) {
                            throw new GerberLayerFormatException(
                                    "Invalid number of parameters for a Thermal primitive");
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
                        appShape = thermal;
                        break;
                    case 2: // Same as Vector Line (deprecated)
                    case 20: // Vector Line Primitive (4.5.4.3)
                        // Exposure, Width, Start X, Start Y, End X, End Y, Rotation
                        if (commandParams.size() != 8) {
                            throw new GerberLayerFormatException(
                                    "Invalid number of parameters for a Vector Line primitive");
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
                        appShape = s1.createStrokedShape(new Line2D.Double(aX, aY, bX, bY));
                        break;
                    case 21: // Center Line Primitive (4.5.4.4)
                        //Exposure, width, height, center X, center Y, rotation
                        if (commandParams.size() != 7) {
                            throw new GerberLayerFormatException(
                                    "Invalid number of parameters for a Center Line primitive");
                        }
                        exposure = commandParams.get(1);
                        double aWid = commandParams.get(2);
                        double aHyt = commandParams.get(3);
                        aX = commandParams.get(4);
                        aY = commandParams.get(5);
                        rot = commandParams.get(6);
                        s1 = new BasicStroke((float) aHyt, BasicStroke.CAP_BUTT,
                                BasicStroke.JOIN_ROUND);
                        appShape = s1.createStrokedShape(new Line2D.Double(aX - aWid/2, aY, 
                                aX + aWid/2, aY));
                        break;
                    case 22: // Lower Left Line Primitive (deprecated 8.25)
                        // Exposure, Width, Height, Lower Left X, Lower Left Y, Rotation
                        if (commandParams.size() != 7) {
                            throw new GerberLayerFormatException(
                                    "Invalid number of parameters for a Lower Left Line primitive");
                        }
                        exposure = commandParams.get(1);
                        width = commandParams.get(2);
                        double height = commandParams.get(3);
                        aX = commandParams.get(4);
                        aY = commandParams.get(5);
                        rot = commandParams.get(6);
                        s1 = new BasicStroke((float) height, BasicStroke.CAP_BUTT,
                                BasicStroke.JOIN_ROUND);
                        appShape = s1.createStrokedShape(new Line2D.Double(aX, aY+height/2, 
                                aX+width, aY+height/2));
                        break;
                    default:
                        throw new GerberLayerFormatException(
                                "Unsupported macro primitive type " + primType);
                }

                //Apply the rotation - rotation is about the Macro's origin and not the primitive's
                //center
                AffineTransform at = new AffineTransform();
                at.rotate(Math.toRadians(rot));
                appShape = at.createTransformedShape(appShape);

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
     * @throws GerberLayerFormatException if the string expression is invalid for some reason
     */
    private double evaluateExpression(String strExp, Map<String, String> vars) 
            throws GerberLayerFormatException {
        //Substitute the variable values into the expression until the expression no longer changes.
        String strExpPrev = "";
        while (strExp.contains("$") && !strExp.equals(strExpPrev)) {
            strExpPrev = strExp;
            for (String var : vars.keySet()) {
                int idxs = strExp.indexOf(var);
                //Check if we have a possible match
                if (idxs >= 0) {
                    int idxe = idxs + var.length();
                    //It's a real match if it occurred at the end of the string or if the next 
                    //character is not a digit (we don't want $2 to match with $20)
                    if (idxe == strExp.length() || !Character.isDigit(strExp.charAt(idxe))) {
                        //Make the substitution
                        strExp = strExp.substring(0, idxs) + "(" + vars.get(var) + ")" + 
                                strExp.substring(idxe, strExp.length());
                    }
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
            while (idxe < strExp.length() && Character.isDigit(strExp.charAt(idxe))) {
                idxe++;
            }
            if (idxe == idxs+1) {
                throw new GerberLayerFormatException("Invalid variable name in: " + strExp);
            }
            strExp = strExp.substring(0, idxs) + "0" + strExp.substring(idxe);
        }
        
        //Replace the gerber multiplication operator 'x' with the more usual multiplication 
        //operator '*'
        strExp = strExp.replaceAll("x", java.util.regex.Matcher.quoteReplacement("*"));
        
        //Now evaluate the mathematical expression
        double value;
        try {
            value = new StringMathExpressionEvaluator().evaluate(strExp);
        }
        catch (InvalidMathExpressionException ex) {
            throw new GerberLayerFormatException(ex.getMessage());
        }
        return value;
    }

}
