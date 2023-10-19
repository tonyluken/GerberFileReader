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
 * A class to hold the Gerber graphics state
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-
 * specification-revision-2022-02_en.pdf?ac97011bf6bce9aaf0b1aac43d84b05f#page=16">
 * Section 2.7 of the Gerber Layer Format Specification</a>
*/
public class GraphicsState {
    private CoordinateFormat coordinateFormat;
    private Unit unit;
    private Point currentPoint = new Point(0, 0);
    private Point arcRelativeCenterPoint;
    private boolean singleQuadrantArcMode = false;
    private Aperture currentAperture;
    private PlotState plotState = PlotState.Linear;
    private ApertureTransformation apertureTransformation = new ApertureTransformation();
    
    /**
     * 
     * @return the current coordinate format
     * @throws Exception if no coordinate format has been set
     */
    public CoordinateFormat getCoordinateFormat() throws Exception {
        if (coordinateFormat == null) {
            throw new Exception("Coordinate Format Not Set");
        }
        return coordinateFormat;
    }
    
    /**
     * Sets the coordinate format
     * @param coordinateFormat - the format to set
     */
    public void setCoordinateFormat(CoordinateFormat coordinateFormat) {
        this.coordinateFormat = coordinateFormat;
    }
    
    /**
     * 
     * @return the current coordinate units, defaults to inch if no unit has been specified
     */
    public Unit getUnit() {
        if (unit == null) {
            unit = Unit.inch;
        }
        return unit;
    }
    
    /**
     * Sets the coordinate units
     * @param unit - the coordinate units to set
     */
    public void setUnit(Unit unit) {
        this.unit = unit;
    }
    
    /**
     * 
     * @return the current point or null if the current point has not been set
     */
    public Point getCurrentPoint() {
        return currentPoint;
    }
    
    /**
     * Sets the current point
     * @param currentPoint - the current point to set
     */
    public void setCurrentPoint(Point currentPoint) {
        this.currentPoint = currentPoint;
    }
    
    /**
     * 
     * @return the arc center point relative to the current point or null if it has not been set
     */
    public Point getArcRelativeCenterPoint() {
        return arcRelativeCenterPoint;
    }

    /**
     * Sets the arc center point relative to the current point
     * @param arcRelativeCenterPoint - the center point to set
     */
    public void setArcRelativeCenterPoint(Point arcRelativeCenterPoint) {
        this.arcRelativeCenterPoint = arcRelativeCenterPoint;
    }

    /**
     * 
     * @return true if single quadrant arc mode is in effect
     */
    public boolean isSingleQuadrantArcMode() {
        return singleQuadrantArcMode;
    }

    /**
     * Sets single quadrant arc mode to the specified state
     * @param singleQuadrantArcMode - the state to set
     */
    public void setSingleQuadrantArcMode(boolean singleQuadrantArcMode) {
        this.singleQuadrantArcMode = singleQuadrantArcMode;
    }

    /**
     * 
     * @return the current aperture
     * @throws Exception if the current aperture has not been set
     */
    public Aperture getCurrentAperture() throws Exception {
        if (currentAperture == null) {
            throw new Exception("Current aperture has not been set.");
        }
        return currentAperture;
    }
    
    /**
     * Sets the current aperture
     * @param currentAperture - the aperture to set
     */
    public void setCurrentAperture(Aperture currentAperture) {
        this.currentAperture = currentAperture;
    }
    
    /**
     * 
     * @return the current plot state
     * @throws Exception if the plot state has not been set
     */
    public PlotState getPlotState() throws Exception {
        if (plotState == null) {
            throw new Exception("Plot State has not been set.");
        }
        return plotState;
    }
    
    /**
     * Sets the plot state
     * @param plotState - the plot state to set
     */
    public void setPlotState(PlotState plotState) {
        this.plotState = plotState;
    }
    
    /**
     * 
     * @return the current aperture transformation
     * @throws Exception if the aperture transformation has not been set
     */
    public ApertureTransformation getApertureTransformation() throws Exception {
        if (apertureTransformation != null) {
         return apertureTransformation;
        }
        else {
            throw new Exception("Aperture transform has not been set.");
        }
    }
    
    /**
     * Sets the current aperture transformation
     * @param command - one of the Gerber commands: LP - Load Polarity, LM - Load Mirror, 
     * LR - LoadRotation, or LS - Load Scale
     * @throws Exception if the command is not valid
     */
    public void setApertureTransformation(String command) throws Exception {
        apertureTransformation.set(command);
    }
}
