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
 * A class to hold the Gerber graphics state
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-
 * specification-revision-2024-05_en.pdf?94b45d8745c1a068fd091f095a26ddeb#page=17">
 * Section 2.7 of the Gerber Layer Format Specification</a>
*/
class GraphicsState {
    private CoordinateFormat coordinateFormat;
    private Units unit;
    private Point currentPoint = new Point(0, 0);
    private Point arcRelativeCenterPoint;
    private boolean singleQuadrantArcMode = false;
    private Aperture currentAperture;
    private PlotState plotState = PlotState.LINEAR;
    private ApertureTransformation apertureTransformation = new ApertureTransformation();
    
    /**
     * 
     * @return the current coordinate format
     * @throws GerberLayerFormatException if no coordinate format has been set
     */
    public CoordinateFormat getCoordinateFormat() throws GerberLayerFormatException {
        if (coordinateFormat == null) {
            throw new GerberLayerFormatException("Coordinate format not set");
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
     * @return the current coordinate units, defaults to INCHES if no unit has been specified
     */
    public Units getUnit() {
        if (unit == null) {
            unit = Units.INCHES;
        }
        return unit;
    }
    
    /**
     * Sets the coordinate units
     * @param unit - the coordinate units to set
     */
    public void setUnit(Units unit) {
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
     * @throws GerberLayerFormatException if the current aperture has not been set
     */
    public Aperture getCurrentAperture() throws GerberLayerFormatException {
        if (currentAperture == null) {
            throw new GerberLayerFormatException("Current aperture has not been set.");
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
     * @throws GerberLayerFormatException if the plot state has not been set
     */
    public PlotState getPlotState() throws GerberLayerFormatException {
        if (plotState == null) {
            throw new GerberLayerFormatException("Plot State has not been set.");
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
     * @throws GerberLayerFormatException if the aperture transformation has not been set
     */
    public ApertureTransformation getApertureTransformation() throws GerberLayerFormatException {
        if (apertureTransformation != null) {
         return apertureTransformation;
        }
        else {
            throw new GerberLayerFormatException("APERTURE transform has not been set.");
        }
    }
    
    /**
     * Sets the current aperture transformation
     * @param command - one of the Gerber commands: LP - Load Polarity, LM - Load Mirror, 
     * LR - LoadRotation, or LS - Load Scale
     * @throws GerberLayerFormatException if the command is not valid
     */
    public void setApertureTransformation(String command) throws GerberLayerFormatException {
        apertureTransformation.set(command);
    }
}
