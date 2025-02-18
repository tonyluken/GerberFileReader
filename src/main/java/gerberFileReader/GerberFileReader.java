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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.SwingWorker;

/**
 * A class to read and parse Gerber files into Java objects suitable for further processing. 
 */
public class GerberFileReader {

    private SwingWorker<Void, Double> backgroundWorker;
    private File gerberFile;
    private boolean headerOnly = false;
    private GraphicsState graphicState;
    private ApertureTemplateDictionary apertureTemplateDictionary;
    private ApertureDictionary apertureDictionary;
    private ApertureTemplate macro;
    private List<Block> apertureBlocks;
    private boolean done = false;
    private boolean error = false;
    private Exception lastError;
    private Block currentApertureBlock;
    private Region region;
    private boolean extendedCommand;
    private GraphicsStream graphicsStream;
    private AttributeDictionary fileAttributeDictionary;
    private AttributeDictionary attributeDictionary;
    private String lastOp = "";

    /**
     * Constructs a GerberFileReader for the specified file. It attempts to open the file and read
     * the file header. When it returns, the file's attributes are available via the {@link 
     * #getFileAttributes()} method and the coordinate units are available via the {@link #getUnits()} 
     * method.
     * @param file - the file to read
     * @throws GerberLayerFormatException if the file can't be opened for reading or is not a valid 
     * Gerber file
     */
    public GerberFileReader(File file) throws GerberLayerFormatException {
        if (file == null || !file.canRead()) {
            throw new GerberLayerFormatException(
                    "File doesn't exist or can't be opened for reading");
        }
        gerberFile = file;
        headerOnly = true;
        parseGerberFile(null);
        setDone(false);
    }
    
    /**
     * Parses the Gerber file on the current thread. When it returns, the graphics stream is 
     * available via the {@link #getGraphicsStream()} method.
     * @throws GerberLayerFormatException if an error occurs during the parsing of the file
     */
    public void parseFile() throws GerberLayerFormatException {
        headerOnly = false;
        parseGerberFile(null);
    }
    
    /**
     * Parses the Gerber file on a background thread to generate its graphics stream.
     * 
     * @param showProgress - (optional, set to null if not used) a call-back routine that gets
     * called periodically on the event dispatch thread to show progress as the file is parsed. The 
     * call-back routine should have one parameter of type double which will vary from 0 to 1 as the
     * file is parsed. Typically used to update a progress bar or percent complete display.
     * @param runOnSuccess - (optional, set to null if not used) a call-back routine that gets 
     * called when the file has been successfully parsed
     * @param runOnFailure - (optional, set to null if not used) a call-back routine that gets 
     * called if parsing of the file has failed for some reason. The call-back routine should have
     * one parameter of type Exception which is used to pass back the exception that caused the 
     * failure.
     */
    public void parseFileInBackground(Consumer<Double> showProgress,
            Runnable runOnSuccess, Consumer<Exception> runOnFailure) {
        this.headerOnly = false;
        
        backgroundWorker = new SwingWorker<Void, Double>() {

            @Override
            protected Void doInBackground() throws GerberLayerFormatException {
                parseGerberFile((Double progress) -> publish(progress));
                return null;
            }
            
            @Override
            protected void process(List<Double> chunksOfStatus) {
                if (showProgress != null) {
                    showProgress.accept(chunksOfStatus.get(chunksOfStatus.size()-1));
                }
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    if (runOnSuccess != null) {
                        runOnSuccess.run();
                    }
                }
                catch (Exception ex) {
                    if (runOnFailure != null) {
                        runOnFailure.accept(ex);
                    }
                    else {
                        ex.printStackTrace();
                    }
                }
            }
        };
          
        backgroundWorker.execute();
        
    }
    
    /**
     * Cancels any parsing that is currently occurring on the background task.
     */
    public void cancel() {
        if (backgroundWorker != null) {
            System.out.println("Cancelling...");
            backgroundWorker.cancel(true);
        }
    }
    
    /**
     * Sets the done flag.
     * @param done - the state to set the done flag
     */
    protected synchronized void setDone(boolean done) {
        this.done = done;
    }
    
    /**
     * Checks to see if parsing of the Gerber file has completed.
     * @return true if parsing of the Gerber file has completed, false otherwise
     */
    public synchronized boolean isDone() {
        return done;
    }

    /**
     * Sets the error flag.
     * @param error - the state to set the error flag
     */
    protected synchronized void setError(boolean error) {
        this.error = error;
    }
    
    /**
     * Sets the state of lastError .
     * @param lastError - the value to set
     */
    protected synchronized void setLastError(Exception lastError) {
        this.lastError = lastError;
    }
    
    /**
     * Checks to see if the parsing of the Gerber file has failed.
     * @return true if parsing of the Gerber file has failed
     */
    public synchronized boolean isError() {
        return error;
    }

    /**
     * Gets the last exception that occurred during the parsing of the Gerber file. The contents of
     * the exception message may be examined to determine the exact cause of the exception.
     * @return the last exception that occurred or null if no exception occurred
     */
    public synchronized Exception getLastError() {
        return lastError;
    }

    /**
     * Gets the coordinate units specified by the Gerber file - either inches or millimeters.
     * @return the units specified by the Gerber file
     */
    public Units getUnits() {
        return graphicState.getUnit();
    }
    
    /**
     * Gets the Gerber file.
     * @return the Gerber file
     */
    public File getGerberFile() {
        return gerberFile;
    }

    /**
     * Gets the stream of GraphicObjects described by the Gerber file. 
     * @return the stream of GraphicObjects or an empty stream if the file has not been parsed
     */
    public GraphicsStream getGraphicsStream() {
        return graphicsStream;
    }

    /**
     * Gets the Gerber file attributes. An empty dictionary is returned if the Gerber file does not
     * have any file attributes.
     * @return an AttributeDictionary holding the file attributes
     */
    public AttributeDictionary getFileAttributes() {
        return fileAttributeDictionary;
    }

    /**
     * Initializes the state of the parser
     */
    private void resetState() {
        graphicState = new GraphicsState();
        graphicsStream = new GraphicsStream();
        
        apertureTemplateDictionary = new ApertureTemplateDictionary();
        
        apertureDictionary = new ApertureDictionary();
        apertureBlocks = new ArrayList<>();
        macro = null;
        currentApertureBlock = null;
        setDone(false);
        setError(false);
        setLastError(null);
        region = null;
        extendedCommand = false;
        
        fileAttributeDictionary = new AttributeDictionary();
        attributeDictionary = new AttributeDictionary();
        
        lastOp = "";
      }

    /**
     * Performs the actual work of parsing the Gerber file
     * @param gerberFile - the gerber file to parse
     * @param showProgress - (optional, set to null if not used) a call-back routine that gets
     * called periodically to show progress as the file is parsed. The call-back routine should have
     * one parameter of type double which will vary from 0 to 1 as the file is parsed. Typically 
     * used to update a progress bar or percent complete display.
     * @throws GerberLayerFormatException if the parsing failed for some reason
     */
    private void parseGerberFile(Consumer<Double> showProgress) throws GerberLayerFormatException {
        resetState();
        String lineInfo = "unknown line";
        try (GerberTokenizer tokenizer = new GerberTokenizer(new BufferedReader(
                new FileReader(gerberFile)))) {
            long totalBytes = gerberFile.length();
            while (tokenizer.nextToken() != StreamTokenizer.TT_EOF && !isDone()) {
                if (Thread.interrupted()) {
                    return;
                }
                if (showProgress != null) {
                    showProgress.accept(((double) tokenizer.getBytesProcessed()) / totalBytes);
                }
    
                lineInfo = tokenizer.getLineInfo();
                
                if (tokenizer.ttype == '*') {
                    throw new GerberLayerFormatException(
                            "Syntax error, probably missing * to end previous command.");
                }
                
                if (tokenizer.ttype == '%') {
                    extendedCommand = !extendedCommand;
                    if (!extendedCommand) {
                        //We just finished an extended command
                        if (macro != null) {
                            //We just finished an aperture macro definition so add it to the 
                            //aperture template dictionary
                            apertureTemplateDictionary.put(macro.getTemplateName(), macro);
                            macro = null;
                        }
                    }
                    continue;
                }
                else if (tokenizer.ttype == StreamTokenizer.TT_WORD) {
                    String gerberCommand = tokenizer.sval;
        
                    if (extendedCommand) {
                        // Handle extended command
                        processExtendedCommand(gerberCommand);
                    }
                    else {
                        // Handle word command
                        processWordCommand(gerberCommand);
                    }
                }
            }
            if (!isDone()) {
                throw new GerberLayerFormatException(
                        "FILE appears to be truncated (does not end with a M02* command).");
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            setError(true);
            setLastError(ex);
            throw new GerberLayerFormatException("Error in " + gerberFile.getName() + " at " + 
                    lineInfo + ": " + ex.getMessage());
        }
    }
    
    /**
     * Processes the Gerber word commands
     * @param cmd - the word command to process excluding the terminating '*' character
     * @throws GerberLayerFormatException if the word command is invalid or unrecognized
     */
    private void processWordCommand(String cmd) throws GerberLayerFormatException {
        //Allow for modal coordinates - if a coordinate is not completely specified, the 
        //unspecified portion is taken from the prior point
        Point newPoint;
        if (graphicState.getCurrentPoint() != null) {
            newPoint = new Point(graphicState.getCurrentPoint());
        }
        else {
            newPoint = new Point(Double.NaN, Double.NaN);
        }
        
        while (cmd.length() > 0) {
            switch (cmd.charAt(0)) {
                case 'N':
                    // Line number (ignored)
                    int idx = Utils.numberEndIndex(cmd, 1);
                    if (idx < 0) {
                        throw new GerberLayerFormatException("Invalid line number: " + cmd);
                    }
                    cmd = cmd.substring(idx);
                    continue;
                case 'G':
                    idx = Utils.numberEndIndex(cmd, 1);
                    if (idx < 0) {
                        throw new GerberLayerFormatException("Invalid G code: " + cmd);
                    }
                    String id = cmd.substring(1, idx);
                    int nd = Integer.parseInt(id);
                    cmd = cmd.substring(idx);
                    switch (nd) {
                        case 1: // G01 - linear plot mode
                        case 2: // G02 - clockwise arc mode
                        case 3: // G03 - counter-clockwise arc mode
                            graphicState.setPlotState(PlotState.fromCommand(nd));
                            continue;
                        case 4: // G04 - Comment (ignored)
                            cmd = "";
                            continue;
                        case 36: // G36 - Start new region
                            region = new Region(attributeDictionary);
                            continue;
                        case 37: // G37 - End region
                            addToGraphicsStream(region.getGraphicsStream(graphicState, 
                                    attributeDictionary));
                            region = null;
                            continue;
                        case 54: // G54 - Select aperture (deprecated)
                        case 55: // G55 - Prepare for flash  (deprecated)
                            //Neither of these have any effect so we'll just ignore them
                            continue;
                        case 70: // G70 (deprecated but we handle it)
                            graphicState.setUnit(Units.INCHES);
                            continue;
                        case 71: // G71 (deprecated but we handle it)
                            graphicState.setUnit(Units.MILLIMETERS);
                            continue;
                        case 74: // G74 - Set single-quadrant arc mode (deprecated but we handle it)
                            graphicState.setSingleQuadrantArcMode(true);
                            continue;
                        case 75: // G75 - Set multi-quadrant arc mode
                            graphicState.setSingleQuadrantArcMode(false); 
                            continue;
                        case 90: // G90 - Set absolute coordinate mode  (deprecated)
                            //Absolute coordinate mode is the only mode that is supported so we'll
                            //just ignore this command. Note that G91 - incremental mode is not
                            //supported and will cause an exception
                            continue;
                        default:
                            throw new GerberLayerFormatException(
                                    "Unrecognized or unsupported Gcode: " + nd);
                    }
                case 'X': // Set X position.
                    idx = Utils.numberEndIndex(cmd, 1);
                    if (idx < 0) {
                        throw new GerberLayerFormatException(
                                "Invalid X coordinate: " + cmd);
                    }
                    newPoint.setX(graphicState.getCoordinateFormat()
                                              .parseCoordinate(cmd.substring(0, idx)));
                    cmd = cmd.substring(idx);
                    if (cmd.length() == 0) { //Handle deprecated implicit operation code
                        if (lastOp.equals("D01")) {
                            cmd = "D01";
                        }
                        else {
                            throw new GerberLayerFormatException(
                                    "Invalid implicit operation code: " + lastOp);
                        }
                    }
                    continue;
                case 'Y': // Set Y position.
                    idx = Utils.numberEndIndex(cmd, 1);
                    if (idx < 0) {
                        throw new GerberLayerFormatException("Invalid Y coordinate: " + cmd);
                    }
                    newPoint.setY(graphicState.getCoordinateFormat()
                                              .parseCoordinate(cmd.substring(0, idx)));
                    cmd = cmd.substring(idx);
                    if (cmd.length() == 0) { //Handle deprecated implicit operation code
                        if (lastOp.equals("D01")) {
                            cmd = "D01";
                        }
                        else {
                            throw new GerberLayerFormatException(
                                    "Invalid implicit operation code: " + lastOp);
                        }
                    }
                    continue;
                case 'I': // Set the relative X center of the circle
                    idx = Utils.numberEndIndex(cmd, 1);
                    if (idx < 0) {
                        throw new GerberLayerFormatException("Invalid I coordinate: " + cmd);
                    }
                    Point centerX = graphicState.getArcRelativeCenterPoint();
                    if (centerX == null) {
                        centerX = new Point(0, 0);
                        graphicState.setArcRelativeCenterPoint(centerX);
                    }
                    centerX.setX(graphicState.getCoordinateFormat()
                                       .parseCoordinate(cmd.substring(0, idx)));
                    cmd = cmd.substring(idx);
                    if (cmd.length() == 0) { //Handle deprecated implicit operation code
                        if (lastOp.equals("D01")) {
                            cmd = "D01";
                        }
                        else {
                            throw new GerberLayerFormatException(
                                    "Invalid implicit operation code: " + lastOp);
                        }
                    }
                    continue;
                case 'J': // Set the relative Y center of the circle
                    idx = Utils.numberEndIndex(cmd, 1);
                    if (idx < 0) {
                        throw new GerberLayerFormatException("Invalid J coordinate: " + cmd);
                    }
                    Point centerY = graphicState.getArcRelativeCenterPoint();
                    if (centerY == null) {
                        centerY = new Point(0, 0);
                        graphicState.setArcRelativeCenterPoint(centerY);
                    }
                    centerY.setY(graphicState.getCoordinateFormat()
                                       .parseCoordinate(cmd.substring(0, idx)));
                    cmd = cmd.substring(idx);
                    if (cmd.length() == 0) { //Handle deprecated implicit operation code
                        if (lastOp.equals("D01")) {
                            cmd = "D01";
                        }
                        else {
                            throw new GerberLayerFormatException(
                                    "Invalid implicit operation code: " + lastOp);
                        }
                    }
                    continue;
                case 'D': // Operation code
                    if (headerOnly) {
                        setDone(true);
                        cmd = "";
                        continue;
                    }
                    idx = Utils.numberEndIndex(cmd, 1);
                    if (idx < 0) {
                        throw new GerberLayerFormatException("Invalid operation: " + cmd);
                    }
                    id = cmd.substring(1, idx);
                    nd = Integer.parseInt(id);
                    lastOp = "D" + id;
                    cmd = cmd.substring(idx);
                    if (nd >= 10) { // Select aperture
                        Aperture aperture = apertureDictionary.get(id);
                        if (aperture == null) {
                            throw new GerberLayerFormatException(
                                    "Selected aperture " + nd + 
                                    " but it was not found in the aperture dictionary");
                        }
                        graphicState.setCurrentAperture(aperture);
                    }
                    else if (nd == 1) { // D01: Plot operation
                        if (region != null) {
                            region.plot(graphicState, newPoint);
                        }
                        else {
                            addToGraphicsStream(graphicState.getCurrentAperture()
                                    .plot(graphicState, newPoint, attributeDictionary));
                        }
                    }
                    else if (nd == 2) { // D02: Move operation
                        if (region != null) {
                            region.moveTo(newPoint);
                        }
                        else {
                            // Nothing else needs to be done as the current point gets updated at 
                            // the end
                        }
                    }
                    else if (nd == 3) { // D03: Flash
                        addToGraphicsStream(graphicState.getCurrentAperture()
                                .flash(graphicState, newPoint, attributeDictionary));
                    }
                    else {
                        throw new GerberLayerFormatException("Invalid operation code: D" + id);
                    }
                    continue;
                case 'M':
                    idx = Utils.numberEndIndex(cmd, 1);
                    if (idx < 0) {
                        throw new GerberLayerFormatException("Invalid M code: " + cmd);
                    }
                    id = cmd.substring(1, idx);
                    nd = Integer.parseInt(id);
                    if (nd == 0 || nd == 2) {
                        if (currentApertureBlock != null && 
                                currentApertureBlock.getType() == ApertureType.STEP_AND_REPEAT) {
                            //Handle deprecated case of a Step and Repeat block being closed by the 
                            //end of the file
                            currentApertureBlock.close();
                            StepAndRepeatBlock temp = (StepAndRepeatBlock) currentApertureBlock;
                            currentApertureBlock = null;
                            addToGraphicsStream(temp.getGraphicStream());
                        }
                        setDone(true);
                    }
                    else if (nd == 1) {
                        //Does nothing, ignored
                    }
                    else {
                        throw new GerberLayerFormatException("Invalid M code: M" + id);
                    }
                    cmd = cmd.substring(idx);
                    continue;
                default:
                    throw new GerberLayerFormatException("Unknown command: " + cmd);
            }
        }
        graphicState.setCurrentPoint(newPoint);
    }

    /**
     * Processes the Gerber extended commands
     * @param cmd - the extended command to process excluding its enclosing '%' characters as well
     * as the terminating '*' character
     * @throws GerberLayerFormatException if the extended command is invalid or unsupported
     */
    private void processExtendedCommand(String cmd) throws GerberLayerFormatException {
        //If a macro definition has been started, just add the commands to it 
        if (macro != null) {
            macro.addMacroCommand(cmd);
            return;
        }
        
        // Otherwise, process the command
        if (cmd.length() < 2) {
            throw new GerberLayerFormatException("Invalid extended command: " + cmd);
        }
        switch (cmd.substring(0, 2)) {
          case "AB":
            // Aperture block
            if (cmd.length() > 2) {
                //Starting an aperture block definition
                if (currentApertureBlock != null) {
                    //Must be starting a nested block so save the current one before starting
                    //the new one
                    apertureBlocks.add(currentApertureBlock);
                }
                //Create the new aperture block
                currentApertureBlock = new Block(cmd, attributeDictionary);
                
            }
            else {
                //Finishing an aperture block
                currentApertureBlock.close();
                
                //Add the block to the dictionary
                apertureDictionary.put(currentApertureBlock.getId(), currentApertureBlock);
                
                //Check to see if the just finished block was nested within another, if so, continue
                //with the enclosing block
                if (apertureBlocks.size() != 0) {
                    currentApertureBlock = apertureBlocks.remove(apertureBlocks.size()-1);
                }
                else {
                    currentApertureBlock = null;
                }
            }
            break;
          case "AD":
            //Aperture definition
            Aperture app = new Aperture(cmd, apertureTemplateDictionary, attributeDictionary);
            apertureDictionary.put(app.getId(), app);
            break;
          case "AM": //Aperture Macro
            macro = new ApertureTemplate(cmd.substring(2));
            break;
          case "FS": //Format Specification
              graphicState.setCoordinateFormat(new CoordinateFormat(cmd));
              break;
          case "LP": //Load Polarity
          case "LM": //Load Mirror
          case "LR": //Load Rotation
          case "LS": //Load Scale
              graphicState.setApertureTransformation(cmd);
              break;
          case "MO": //Set unit mode (inches or millimeters)
              graphicState.setUnit(Units.fromCommand(cmd));
              break;
          case "TF": //File Attributes
              Attribute att = new Attribute(cmd);
              fileAttributeDictionary.put(att.getName(), att);
              break;
          case "TA": //Aperture Attributes
          case "TO": //Object Attributes
              att = new Attribute(cmd);
              attributeDictionary.put(att.getName(), att);
              break;
          case "TD": //Delete Attributes
              cmd = cmd.substring(2);
              if (cmd.length() == 0) {
                  //remove all attributes
                  attributeDictionary.clear();
              }
              else {
                  attributeDictionary.remove(cmd);
              }
              break;
          case "SR": // Step and Repeat Block
              if (cmd.equals("SRX1Y1I0J0") && currentApertureBlock == null) {
                  //Deprecated, doesn't do anything so just ignore it
                  cmd = "";
                  break;
              }
              if (cmd.length() > 2 && !cmd.equals("SRX1Y1I0J0")) {
                  //Starting a Step and Repeat Block definition
                  //<SR command> = SR[X<Repeats>Y<Repeats>I<Distance>J<Distance>]*
                  if (currentApertureBlock == null) {
                      //Standard way to start a Step and Repeat block
                      currentApertureBlock = new StepAndRepeatBlock(cmd, attributeDictionary);
                  }
                  else if (currentApertureBlock.getType() == ApertureType.STEP_AND_REPEAT) {
                      //Implicitly ending a Step and Repeat Block and starting a new one (deprecated)
                      currentApertureBlock.close();
                      StepAndRepeatBlock temp = (StepAndRepeatBlock) currentApertureBlock;
                      currentApertureBlock = new StepAndRepeatBlock(cmd, attributeDictionary);
                      addToGraphicsStream(temp.getGraphicStream());
                  }
                  else {
                      throw new GerberLayerFormatException(
                              "Invalid nesting of a Step and Repeat Block within another block");
                  }
              }
              else {
                  if (currentApertureBlock != null && 
                          currentApertureBlock.getType() == ApertureType.STEP_AND_REPEAT) {
                      //Standard way to end a Step and Repeat Block but also handles the deprecated
                      //ending with SRX1Y1I0J0
                      currentApertureBlock.close();
                      StepAndRepeatBlock temp = (StepAndRepeatBlock) currentApertureBlock;
                      currentApertureBlock = null;
                      addToGraphicsStream(temp.getGraphicStream());
                  }
                  else {
                      throw new GerberLayerFormatException(
                              "Invalid ending for a Step and Repeat Block");
                  }
              }
              break;
          case "IN":      // Deprecated command (ignored)
          case "LN":      // Deprecated command (ignored)
              break;
          case "AS":      // Deprecated command (unsupported if it doesn't confirm the default)
              if (!cmd.equals("ASAXBY")) {
                  throw new GerberLayerFormatException(
                          "Unsupported deprecated extended command: " + cmd);
              }
              break;
          case "IP":      // Deprecated command (unsupported if it doesn't confirm the default)
              if (!cmd.equals("IPPOS")) {
                  throw new GerberLayerFormatException(
                          "Unsupported deprecated extended command: " + cmd);
              }
              break;
          case "IR":      // Deprecated command (unsupported if it doesn't confirm the default)
              if (!cmd.equals("IR0")) {
                  throw new GerberLayerFormatException(
                          "Unsupported deprecated extended command: " + cmd);
              }
              break;
          case "MI":      // Deprecated command (unsupported if it doesn't confirm the default)
              if (!cmd.equals("MIA0B0")) {
                  throw new GerberLayerFormatException(
                          "Unsupported deprecated extended command: " + cmd);
              }
              break;
          case "OF":      // Deprecated command (unsupported if it doesn't confirm the default)
              if (!cmd.equals("OFA0B0")) {
                  throw new GerberLayerFormatException(
                          "Unsupported deprecated extended command: " + cmd);
              }
              break;
          case "SF":      // Deprecated command (unsupported if it doesn't confirm the default)
              if (!cmd.equals("SFA1B1")) {
                  throw new GerberLayerFormatException(
                          "Unsupported deprecated extended command: " + cmd);
              }
              break;
          default:
              throw new GerberLayerFormatException("Unknown extended command: " + cmd);
        }
    }
    
    /**
     * Appends the specified GraphicStream either to the end of the current aperture block if one
     * is in progress or to the end of the output image graphics stream
     * @param graphicsStream - the graphics stream to append
     */
    private void addToGraphicsStream(GraphicsStream graphicsStream) {
        if (currentApertureBlock != null) {
            currentApertureBlock.put(graphicsStream);
        }
        else {
            this.graphicsStream.put(graphicsStream);
        }
    }
}
