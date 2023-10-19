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

/**
 * A class to hold a Gerber Step and Repeat Block
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-
 * specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=116">
 * Section 4.12 of the Gerber Layer Format Specification</a>
 */
public class StepAndRepeatBlock extends Block {
    private int           countX = -1;
    private int           countY = -1;
    private double        stepX = Double.NaN;
    private double        stepY = Double.NaN;

    StepAndRepeatBlock(String command, AttributeDictionary attributeDictionary) throws Exception {
        String cmd = command;
        type = ApertureType.StepAndRepeat;
        id = "StepAndRepeat";
        if (!command.substring(0, 2).equals("SR")) {
            throw new Exception("Step and Repeat block must be defined with an SR command but found: " + cmd);
        }
        command = command.substring(2);
        while (command.length() > 0) {
            int nl = Utils.numberEndIndex(command, 1);
            if (nl < 1) {
                throw new Exception("Invalid Step and Repeat block command: " + cmd);
            }
            switch (command.substring(0, 1)) {
                case "X":
                    countX = Integer.parseInt(command.substring(1, nl));
                    break;
                case "Y":
                    countY = Integer.parseInt(command.substring(1, nl));
                    break;
                case "I":
                    stepX = Double.parseDouble(command.substring(1, nl));
                    break;
                case "J":
                    stepY = Double.parseDouble(command.substring(1, nl));
                    break;
                default :
                    throw new Exception("Invalid Step and Repeat block command: " + cmd);
            }
            command = command.substring(nl);
        }
        if (countX < 1 || countY < 1 || !Double.isFinite(stepX) || !Double.isFinite(stepY) ||
                (countX > 1 && stepX == 0) || (countY > 1 && stepY == 0)) {
            throw new Exception("Invalid Step and Repeat block command: " + cmd);
        }
        apertureAttributes = attributeDictionary.getAllOf(AttributeType.Aperture);
    }

    /**
     * 
     * @return the number of repeats in the X direction
     */
    public int getCountX() {
        return countX;
    }

    /**
     * 
     * @return the number of repeats in the Y direction
     */
    public int getCountY() {
        return countY;
    }

    /**
     * 
     * @return the distance to step in the X direction for each repeat
     */
    public double getStepX() {
        return stepX;
    }

    /**
     * 
     * @return the distance to step in the Y direction for each repeat
     */
    public double getStepY() {
        return stepY;
    }
    
    /**
     * Replicates and steps the block defined for this StepAndRepeatBlock to form the array of
     * GraphicalObjects. Per the Gerber specification, the block is replicated in the Y direction
     * first and then in the X direction.
     * 
     * @return the GraphicsStream produced by the this StepAndRepeatBlock
     */
    public GraphicsStream getGraphicStream() {
        GraphicsStream ret = new GraphicsStream();
        for (int i=0; i<countX; i++) {
            for (int j=0; j<countY; j++) {
                ApertureTransformation at = new ApertureTransformation();
                Point step = new Point(i*stepX, j*stepY);
                for (GraphicalObject go : getStream()) {
                    go.getMetaData().setRepeatCount(i, j);
                    ret.put(at.apply(step, go, new AttributeDictionary()));
                }
            }
        }
        return ret;
    }
}
