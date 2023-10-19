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
import java.awt.geom.Area;

/**
 * A class to hold geometric transformations of an aperture
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-
 * specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=85">
 * Section 4.9 of the Gerber Layer Format Specification</a>
 */
public class ApertureTransformation {

    private Polarity polarity = Polarity.Dark;
    private AffineTransform apertureTransform = new AffineTransform();
    private double mirrorX = +1; //+1 = no mirror, -1 = mirror
    private double mirrorY = +1;
    private double scale = 1;
    private double rotation = 0;
    
    /**
     * Sets the state of the aperture transformations
     * 
     * @param command - one of the Gerber commands: LP - Load Polarity, LM - Load Mirror, LR - Load 
     * Rotation, or LS - Load Scale
     * @throws Exception if the given command is not valid
     */
    public void set(String command) throws Exception {
        switch (command.substring(0, 2)) {
            case "LP": // Load polarity
                polarity = Polarity.fromCommand(command);
                break;
            case "LM": // Load mirror
                if (command.charAt(2) == 'N') {
                    mirrorX = 1;
                    mirrorY = 1;
                }
                else if (command.charAt(2) == 'Y') {
                    mirrorY = -1;
                    // Handle LMYX even though it's not part of the spec
                    if (command.length() > 3 && command.charAt(3) == 'X') {
                        mirrorX = -1;
                    }
                    else {
                        mirrorX = 1;
                    }
                }
                else if (command.charAt(2) == 'X') {
                    mirrorX = -1;
                    if (command.length() > 3 && command.charAt(3) == 'Y') {
                        mirrorY = -1;
                    }
                    else {
                        mirrorY = 1;
                    }
                }
                else {
                    throw new Exception("Unrecognized Aperture Transformation Command: " + command);
                }
                computeTransform();
                break;
            case "LR": // Load rotation
                rotation = Double.parseDouble(command.substring(2));
                computeTransform();
                break;
            case "LS": // Load scale
                scale = Double.parseDouble(command.substring(2));
                computeTransform();
                break;
            default :
                throw new Exception("Unrecognized Aperture Transformation Command: " + command);
        }
    }

    /**
     * 
     * @return the current polarity state
     */
    public Polarity getPolarity() {
        return polarity;
    }

    /**
     * 
     * @return the current scaling, mirroring, and rotation states combined as a single Affine 
     * transform
     */
    public AffineTransform getApertureTransform() {
        return apertureTransform;
    }

    /**
     * 
     * @return the current rotation state (in degrees)
     */
    public double getRotation() {
        return rotation;
    }
    
    /**
     * 
     * @return the current scaling state
     */
    public double getScale() {
        return scale;
    }
    
    /**
     * 
     * @return true if the current mirror state is set to mirror in the X direction, false otherwise
     */
    public boolean isMirrorX() {
        return mirrorX == -1;
    }
    
    /**
     * 
     * @return true if the current mirror state is set to mirror in the Y direction, false otherwise
     */
    public boolean isMirrorY() {
        return mirrorY == -1;
    }
    
    /**
     * Creates a new GraphicObject by applying this ApertureTransformation to the specified 
     * GraphicObject. A copy of the specified object is mirrored, scaled, rotated (in that order), 
     * and its polarity is reversed if the current polarity state is Clear. Finally, the copy is 
     * translated by the specified amount. All attributes attached to the original object are copied
     * to the new object and any object attributes in the specified attribute dictionary are 
     * attached to the new object as well.
     * 
     * @param translation - the amount to translate the transformed object
     * @param graphicObject - the object to copy and transform
     * @param attributes - the attribute dictionary whose object attributes are to be attached to
     * the new object
     * @return the new GraphicsObject
     */
    public GraphicalObject apply(Point translation, GraphicalObject graphicObject, AttributeDictionary attributes) {
        AffineTransform at = new AffineTransform();
        at.translate(translation.getX(), translation.getY());
        at.concatenate(apertureTransform);
        AttributeDictionary goAtt = new AttributeDictionary();
        goAtt.putAll(graphicObject.getAttributes());
        goAtt.putAll(attributes.getAllOf(AttributeType.Object));
        MetaData metaData = graphicObject.getMetaData();
        metaData.setStrokeInfo(metaData.getStrokeInfo().transform(at));
        return new GraphicalObject(new Area(at.createTransformedShape(graphicObject.getArea())), 
                graphicObject.getPolarity().reverse(polarity == Polarity.Clear), goAtt, metaData);
    }
    
    /**
     * Creates a new GraphicStream of by applying this ApertureTransformation to the specified 
     * Aperture. A copy of the aperture is created and all objects of that copy are mirrored, 
     * scaled, rotated (in that order), and have their polarity reversed if the current polarity 
     * state is Clear. Finally, the copies are translated by the specified amount. All attributes 
     * attached to the original aperture are copied to the new objects and any object attributes in
     * the specified attribute dictionary are attached to each new object as well.
     * 
     * @param translation - the amount to translate each transformed object
     * @param aperture - the aperture to copy and transform
     * @param attributes - the attribute dictionary whose object attributes are to be attached to
     * the new objects
     * @return the new GraphicStream containing all of the created objects
     */
    public GraphicsStream apply(Point translation, Aperture aperture, AttributeDictionary attributes) {
        AffineTransform at = new AffineTransform();
        at.translate(translation.getX(), translation.getY());
        at.concatenate(apertureTransform);
        GraphicsStream ret = new GraphicsStream();
        for (GraphicalObject graphicObject : aperture.getStream()) {
            AttributeDictionary goAtt = new AttributeDictionary();
            goAtt.putAll(graphicObject.getAttributes());
            goAtt.putAll(attributes.getAllOf(AttributeType.Object));
            MetaData metaData = graphicObject.getMetaData();
            metaData.setStrokeInfo(metaData.getStrokeInfo().transform(at));
            ret.put(new GraphicalObject(new Area(at.createTransformedShape(graphicObject.getArea())), 
                graphicObject.getPolarity().reverse(polarity == Polarity.Clear), goAtt, metaData));
        }
        return ret;
    }
    
    /**
     * Computes and sets the Affine transform that represents the mirroring, scaling, and rotation 
     * states of this aperture transformation
     */
    private void computeTransform() {
        //Per 4.9.3, mirroring is performed before rotation so it must be concatenated last so it
        //will be applied first
        apertureTransform = new AffineTransform();
        apertureTransform.rotate(Math.toRadians(rotation));
        apertureTransform.scale(mirrorX*scale, mirrorY*scale);
    }
        
}
