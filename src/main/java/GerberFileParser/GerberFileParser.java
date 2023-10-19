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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.SwingWorker;

/**
 * A class to parse Gerber text files into Java objects suitable for further processing. Separate
 * methods are provided to parse just the file header as well as the entire file. The methods for 
 * parsing the entire file perform the majority of the processing on a background thread and can
 * notify the caller of success or failure via call-backs. 
 */
public class GerberFileParser {

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
    private Block currentApertureBlock;
    private Region region;
    private boolean extendedCommand;
    private GraphicsStream imageGraphicsStream;
    private AttributeDictionary fileAttributeDictionary;
    private AttributeDictionary attributeDictionary;
    private String lastOp = "";

    /**
     * Constructs a GerberFileParser for the specified file
     * @param file - the file to parse
     * @throws Exception if the file doesn't exist or can't be opened for reading
     */
    public GerberFileParser(File file) throws Exception {
        if (file == null || !file.canRead()) {
            throw new Exception("File doesn't exist or can't be opened for reading");
        }
        gerberFile = file;
    }
    
    /**
     * Parses the Gerber header contents to extract the file attributes of the specified file. Runs
     * on the current thread. When it returns, the contents of the file attributes are available via
     * the getFileAttributes method.
     * 
     * @throws Exception if an error occurs either during the opening of the file or if the contents
     * of the file are invalid
     */
    public void parseFileHeaderOnly() throws Exception {
        headerOnly = true;
        parseGerberFile(null);
        setDone(false);
    }
    
    /**
     * Parses the Gerber file on a background thread to extract its file attributes and 
     * generate the graphics stream described by the file
     * 
     * @param showProgress - (optional, set to null if not used) a call-back routine that gets
     * called periodically on the event dispatch thread to show progress as the file is parsed. The 
     * call-back routine should have one double parameter which will vary from 0 to 1 as the file is
     * parsed. Typically used to update a progress bar or percent complete display.
     * @param runOnSuccess - (optional, set to null if not used) a call-back routine that gets 
     * called when the file has been successfully parsed
     * @param runOnFailure - (optional, set to null if not used) a call-back routine that gets 
     * called if parsing of the file has failed for some reason. The call-back routine should have
     * one parameter of type Exception which is used to pass back the exception that caused the 
     * failure.
     */
    public void parseFileInBackground(Consumer<Double> showProgress,
            Runnable runOnSuccess, Consumer<Exception> runOnFailure) {
        parseFileInBackground(showProgress,
                runOnSuccess, runOnFailure, false);
    }
    
    /**
     * Parses the  Gerber file on a background thread to extract its file attributes and 
     * generate the graphics stream. Can also be used to only parse the file header.
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
     * @param headerOnly - a flag, that if set to true, results in only the file header being parsed
     */
    public void parseFileInBackground(Consumer<Double> showProgress,
            Runnable runOnSuccess, Consumer<Exception> runOnFailure, boolean headerOnly) {
        this.headerOnly = headerOnly;
        
        backgroundWorker = new SwingWorker<Void, Double>() {

            @Override
            protected Void doInBackground() throws Exception {
                parseGerberFile((Double progress) -> publish(progress));
                return null;
            }
            
            @Override
            protected void process(List<Double> chunksOfStatus) {
                if (showProgress != null) {
                    for (Double d : chunksOfStatus) {
                        showProgress.accept(d);
                    }
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
     * Cancels any parsing that is currently occurring on the background task
     */
    public void cancel() {
        if (backgroundWorker != null) {
            System.out.println("Cancelling...");
            backgroundWorker.cancel(true);
        }
    }
    
    /**
     * Sets the done flag
     * @param done - the state to set the done flag
     */
    protected synchronized void setDone(boolean done) {
        this.done = done;
    }
    
    /**
     * 
     * @return true if parsing of the Gerber file has completed, false otherwise
     */
    public synchronized boolean isDone() {
        return done;
    }

    /**
     * Sets the error flag
     * @param error - the state to set the error flag
     */
    protected synchronized void setError(boolean error) {
        this.error = error;
    }
    
    /**
     * 
     * @return true if parsing of the Gerber file has failed
     */
    public synchronized boolean isError() {
        return error;
    }

    /**
     * 
     * @return true if the file units are millimeters, false otherwise (only valid after the file 
     * header has been parsed)
     */
    public boolean isMillimeters() {
        return graphicState.getUnit() == Unit.millimeter;
    }

    /**
     * 
     * @return true if the file units are inches, false otherwise (only valid after the file 
     * header has been parsed)
     */
    public boolean isInches() {
        return graphicState.getUnit() == Unit.inch;
    }

    /**
     * 
     * @return the Gerber file
     */
    public File getGerberFile() {
        return gerberFile;
    }

    /**
     * Gets the stream of GraphicObjects described by the Gerber file. 
     * @return the stream of GraphicObjects or null if the file has not been parsed
     */
    public GraphicsStream getImageGraphicStream() {
        return imageGraphicsStream;
    }

    /**
     * Gets the Gerber file attributes 
     * @return an AttributeDictionary holding the file attributes or null if the file has not been
     * parsed
     */
    public AttributeDictionary getFileAttributes() {
        return fileAttributeDictionary;
    }

    /**
     * Initializes the state of the parser
     */
    private void resetState() {
        graphicState = new GraphicsState();
        imageGraphicsStream = new GraphicsStream();
        
        apertureTemplateDictionary = new ApertureTemplateDictionary();
        
        apertureDictionary = new ApertureDictionary();
        apertureBlocks = new ArrayList<>();
        macro = null;
        currentApertureBlock = null;
        setDone(false);
        setError(false);
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
     * @throws Exception if the parsing failed for some reason
     */
    private void parseGerberFile(Consumer<Double> showProgress) throws Exception {
        resetState();
        String lineInfo = "unknown line";
        try (GerberTokenizer tokenizer = new GerberTokenizer(new BufferedReader(new FileReader(gerberFile)))) {
            long totalBytes = gerberFile.length();
            while (tokenizer.nextToken() != StreamTokenizer.TT_EOF && !isDone()) {
                if (Thread.interrupted()) {
                    return;
                }
                if (showProgress != null) {
                    showProgress.accept(((double) tokenizer.getBytesProcessed()) / totalBytes);
                }
    
                lineInfo = tokenizer.getLineInfo();
                
                if (tokenizer.ttype == '%') {
                    extendedCommand = !extendedCommand;
                    if (!extendedCommand) {
                        //We just finished an extended command
                        if (macro != null) {
                            //We just finished an aperture macro definition so add it to the aperture
                            //template dictionary
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
        }
        catch (Exception ex) {
            setError(true);
            throw new Exception("Error in " + gerberFile.getName() + " at " + lineInfo + ": " + ex.getMessage());
        }
    }
    
    /**
     * Processes the Gerber word commands
     * @param cmd - the word command to process excluding the terminating '*' character
     * @throws Exception if the word command is invalid or unrecognized
     */
    private void processWordCommand(String cmd) throws Exception {
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
                    cmd = cmd.substring(idx);
                    continue;
                case 'G':
                    idx = Utils.numberEndIndex(cmd, 1);
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
                            addToGraphicsStream(region.getGraphicStream(graphicState, attributeDictionary));
                            region = null;
                            continue;
                        case 54: // G54 - Select aperture (deprecated)
                        case 55: // G55 - Prepare for flash  (deprecated)
                            //Neither of these have any effect so we'll just ignore them
                            continue;
                        case 70: // G70 (deprecated but we handle it)
                            graphicState.setUnit(Unit.inch);
                            continue;
                        case 71: // G71 (deprecated but we handle it)
                            graphicState.setUnit(Unit.millimeter);
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
                            throw new Exception("Unrecognized or unsupported Gcode: " + nd);
                    }
                case 'X': // Set X position.
                    idx = Utils.numberEndIndex(cmd, 1);
                    newPoint.setX(graphicState.getCoordinateFormat()
                                              .parseCoordinate(cmd.substring(0, idx)));
                    cmd = cmd.substring(idx);
                    if (cmd.length() == 0) {
                        if (lastOp.equals("D01")) {
                            cmd = "D01";
                        }
                        else {
                            throw new Exception("Invalid implicit operation code: " + lastOp);
                        }
                    }
                    continue;
                case 'Y': // Set Y position.
                    idx = Utils.numberEndIndex(cmd, 1);
                    newPoint.setY(graphicState.getCoordinateFormat()
                                              .parseCoordinate(cmd.substring(0, idx)));
                    cmd = cmd.substring(idx);
                    if (cmd.length() == 0) {
                        if (lastOp.equals("D01")) {
                            cmd = "D01";
                        }
                        else {
                            throw new Exception("Invalid implicit operation code: " + lastOp);
                        }
                    }
                    continue;
                case 'I': // Set the relative X center of the circle
                    idx = Utils.numberEndIndex(cmd, 1);
                    Point centerX = graphicState.getArcRelativeCenterPoint();
                    if (centerX == null) {
                        centerX = new Point(0, 0);
                        graphicState.setArcRelativeCenterPoint(centerX);
                    }
                    centerX.setX(graphicState.getCoordinateFormat()
                                       .parseCoordinate(cmd.substring(0, idx)));
                    cmd = cmd.substring(idx);
                    continue;
                case 'J': // Set the relative Y center of the circle
                    idx = Utils.numberEndIndex(cmd, 1);
                    Point centerY = graphicState.getArcRelativeCenterPoint();
                    if (centerY == null) {
                        centerY = new Point(0, 0);
                        graphicState.setArcRelativeCenterPoint(centerY);
                    }
                    centerY.setY(graphicState.getCoordinateFormat()
                                       .parseCoordinate(cmd.substring(0, idx)));
                    cmd = cmd.substring(idx);
                    continue;
                case 'D': // Operation code
                    if (headerOnly) {
                        setDone(true);
                    }
                    idx = Utils.numberEndIndex(cmd, 1);
                    id = cmd.substring(1, idx);
                    nd = Integer.parseInt(id);
                    lastOp = "D" + id;
                    cmd = cmd.substring(idx);
                    if (nd >= 10) { // Select aperture
                        Aperture aperture = apertureDictionary.get(id);
                        if (aperture == null) {
                            throw new Exception(
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
                            // Nothing else needs to be done as the current point gets updated at the end
                        }
                    }
                    else if (nd == 3) { // D03: Flash
                        addToGraphicsStream(graphicState.getCurrentAperture()
                                .flash(graphicState, newPoint, attributeDictionary));
                    }
                    continue;
                case 'M':
                    idx = Utils.numberEndIndex(cmd, 1);
                    id = cmd.substring(1, idx);
                    nd = Integer.parseInt(id);
                    if ((nd == 0 || nd == 2) && currentApertureBlock != null && 
                            currentApertureBlock.getType() == ApertureType.StepAndRepeat) {
                        //Handle deprecated case of a Step and Repeat block being closed by the end
                        //of the file
                        currentApertureBlock.close();
                        StepAndRepeatBlock temp = (StepAndRepeatBlock) currentApertureBlock;
                        currentApertureBlock = null;
                        addToGraphicsStream(temp.getGraphicStream());
                    }
                    setDone(nd == 0 || nd == 2);
                    cmd = cmd.substring(idx);
                    continue;
                default:
                    throw new Exception("Unknown command: " + cmd);
            }
        }
        graphicState.setCurrentPoint(newPoint);
    }

    /**
     * Processes the Gerber extended commands
     * @param cmd - the extended command to process excluding its enclosing '%' characters as well
     * as the terminating '*' character
     * @throws Exception if the extended command is invalid or unsupported
     */
    private void processExtendedCommand(String cmd) throws Exception {
        //If a macro definition has been started, just add the commands to it 
        if (macro != null) {
            macro.addMacroCommand(cmd);
            return;
        }

        // Otherwise, process the command
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
          case "LP": //Load polarity
          case "LM": //Load mirror
          case "LR": //Load rotation
          case "LS": //Load scale
              graphicState.setApertureTransformation(cmd);
              break;
          case "MO": //Set unit mode (inches or millimeters)
              graphicState.setUnit(Unit.fromCommand(cmd));
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
          case "SR": // Step repeat block
              if (cmd.equals("SRX1Y1I0J0") && currentApertureBlock == null) {
                  //Deprecated, doesn't do anything so just ignore it
                  cmd = "";
                  break;
              }
              if (cmd.length() > 2 && !cmd.equals("SRX1Y1I0J0")) {
                  //Starting a step repeat block definition
                  //<SR command> = SR[X<Repeats>Y<Repeats>I<Distance>J<Distance>]*
                  if (currentApertureBlock == null) {
                      //Standard way to start a Step and Repeat block
                      currentApertureBlock = new StepAndRepeatBlock(cmd, attributeDictionary);
                  }
                  else if (currentApertureBlock.getType() == ApertureType.StepAndRepeat) {
                      //Implicitly ending a step repeat block and starting a new one (deprecated)
                      currentApertureBlock.close();
                      StepAndRepeatBlock temp = (StepAndRepeatBlock) currentApertureBlock;
                      currentApertureBlock = new StepAndRepeatBlock(cmd, attributeDictionary);
                      addToGraphicsStream(temp.getGraphicStream());
                  }
                  else {
                      throw new Exception("Invalid nesting of a Step and Repeat Block within another block");
                  }
              }
              else {
                  if (currentApertureBlock != null && 
                          currentApertureBlock.getType() == ApertureType.StepAndRepeat) {
                      //Standard way to end a Step and Repeat block but also handles the deprecated
                      //ending with SRX1Y1I0J0
                      currentApertureBlock.close();
                      StepAndRepeatBlock temp = (StepAndRepeatBlock) currentApertureBlock;
                      currentApertureBlock = null;
                      addToGraphicsStream(temp.getGraphicStream());
                  }
                  else {
                      throw new Exception("Invalid ending for a Step and Repeat Block");
                  }
              }
              break;
          case "IN":      // Deprecated command (ignored)
          case "LN":      // Deprecated command (ignored)
              break;
          case "AS":      // Deprecated command (unsupported if it doesn't confirm the default)
              if (!cmd.equals("ASAXBY")) {
                  throw new Exception("Unsupported deprecated extended command: " + cmd);
              }
              break;
          case "IP":      // Deprecated command (unsupported if it doesn't confirm the default)
              if (!cmd.equals("IPPOS")) {
                  throw new Exception("Unsupported deprecated extended command: " + cmd);
              }
              break;
          case "IR":      // Deprecated command (unsupported if it doesn't confirm the default)
              if (!cmd.equals("IR0")) {
                  throw new Exception("Unsupported deprecated extended command: " + cmd);
              }
              break;
          case "MI":      // Deprecated command (unsupported if it doesn't confirm the default)
              if (!cmd.equals("MIA0B0")) {
                  throw new Exception("Unsupported deprecated extended command: " + cmd);
              }
          case "OF":      // Deprecated command (unsupported if it doesn't confirm the default)
              if (!cmd.equals("OFA0B0")) {
                  throw new Exception("Unsupported deprecated extended command: " + cmd);
              }
              break;
          case "SF":      // Deprecated command (unsupported if it doesn't confirm the default)
              if (!cmd.equals("SFA1B1")) {
                  throw new Exception("Unsupported deprecated extended command: " + cmd);
              }
              break;
          default:
              throw new Exception("Unknown extended command: " + cmd);
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
            imageGraphicsStream.put(graphicsStream);
        }
    }
}
