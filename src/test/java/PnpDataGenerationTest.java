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

import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import gerberFileReader.AttributeDictionary;
import gerberFileReader.GerberFileReader;
import gerberFileReader.GraphicalObject;

public class PnpDataGenerationTest {
    Exception savedError;

    /**
     * This test extracts the Pick-and-Place data from the Gerber files and compares it with that
     * exported directly by KiCad into .pos files.
     * 
     * @throws Exception if there is a problem reading the files or the pick-and-place data 
     * miscompares.
     */
    @Test
    public void testPnpDataGeneration() throws Exception {
        File testFileDir = new File(ClassLoader.getSystemResource("gerberFiles").getPath());
        File expectedDir = new File(ClassLoader.getSystemResource("expectedResults").getPath());
        
        File[] gerberFiles = {
                new File(testFileDir.getPath() + File.separator + "sampleBoardDesign" + 
                        File.separator + "IR2IP-pnp_top.gbr"),
                new File(testFileDir.getPath() + File.separator + "sampleBoardDesign" + 
                        File.separator + "IR2IP-pnp_bottom.gbr")
        };
        
        //The expected pick-and-place data was generated and exported by KiCad into .pos files
        File[] expectedFiles = {
                new File(expectedDir.getPath() + File.separator + "sampleBoardDesign" + 
                        File.separator + "IR2IP-top.pos" ),
                new File(expectedDir.getPath() + File.separator + "sampleBoardDesign" + 
                        File.separator + "IR2IP-bottom.pos" )
        };
        
        System.out.println("Extracting PnP data from Gerber files...");
        List<String> gerberPnpData = readGerberPnpData(gerberFiles);
        System.out.println("Completed extraction of PnP data from Gerber files.");
        
        System.out.println("Reading expected PnP data from .pos files...");
        List<String> expectedPnpData = readExpectedPnpData(expectedFiles);
        System.out.println("Completed reading expected PnP data from .pos files.");
        
        if (gerberPnpData.size() != expectedPnpData.size()) {
            throw new Exception("Gerber PnP data size is not the same size as the expected PnP data.");
        }
        
        System.out.println("Comparing PnP data from Gerber files to expected PnP data from .pos files...");
        int idx = 0;
        for (String item : gerberPnpData) {
            if (!item.equals(expectedPnpData.get(idx))) {
                throw new Exception("PnP data miscompare:  " + item + "  <->  " + expectedPnpData.get(idx));
            }
            idx++;
        }
        
        System.out.println("PnP data (" + gerberPnpData.size() + " components) compared successfully!");
    }
    
    private List<String> readGerberPnpData(File[] gerberFiles) throws Exception {
        List<String> ret = new ArrayList<>();
        for (File gerberFile : gerberFiles) {
            GerberFileReader parser = new GerberFileReader(gerberFile);
            parser.parseFile();
            
//            if (parser.isError()) {
//                throw new Exception(savedError.getMessage());
//            }
            
            AttributeDictionary fileAttributes = parser.getFileAttributes();
            if (!fileAttributes.get(".FileFunction").getValues().get(0).equals("Component")) {
                continue;
            }
            String side = fileAttributes.get(".FileFunction").getValues().get(2).toLowerCase();
            for (GraphicalObject go : parser.getGraphicsStream().getStream()) {
                AttributeDictionary goAttributes = go.getAttributes();
                if (!goAttributes.get(".AperFunction").getValues().get(0).equals("ComponentMain")) {
                    continue;
                }
                Rectangle2D bounds = go.getArea().getBounds2D();
                
                //When KiCad exports to the .pos file, it replaces space characters with
                //underscores. However, when it exports to Gerber files, it keeps the spaces and
                //encloses the string in double quotes. So here we remove the quotes and change all
                //spaces to underscores.
                String refDes = goAttributes.get(".C").getValues().get(0).replaceAll("[\"]", "").replaceAll("[ ]", "_");
                String value = goAttributes.get(".CVal").getValues().get(0).replaceAll("[\"]", "").replaceAll("[ ]", "_");
                String pkg = goAttributes.get(".CFtp").getValues().get(0).replaceAll("[\"]", "").replaceAll("[ ]", "_");
                
                double xCoord = bounds.getCenterX();
                double yCoord = bounds.getCenterY();
                double rot = Double.parseDouble(goAttributes.get(".CRot").getValues().get(0));
                
                //Gerber bottom side rotation angles are defined 180 degrees differently than KiCad's
                if (side.equals("bot")) {
                    rot += 180;
                }
                rot = normalizeAngle(rot);
                
                String item = String.format("%s,%s,%s,%.4f,%.4f,%.4f,%s", refDes, value, pkg, xCoord, yCoord, rot, side);
                
                if (ret.contains(item)) {
                    continue;
                }
                
                ret.add(item);
            }
        }
        Collections.sort(ret);
        return ret;
    }
    
    private List<String> readExpectedPnpData(File[] expectedFiles) throws FileNotFoundException, IOException {
        List<String> ret = new ArrayList<>();
        
        for (File expectedFile : expectedFiles) {
            try (BufferedReader reader = new BufferedReader(new FileReader(expectedFile))) {
                String line = reader.readLine();
                while (line != null) {
                    if (line.startsWith("#")) {
                        //Skip comment lines
                        line = reader.readLine();
                        continue;
                    }
                    int idx = line.indexOf(" ");
                    String refDes = line.substring(0, idx);
                    line = line.substring(idx).strip();
                    
                    idx = line.indexOf(" ");
                    String value = line.substring(0, idx);
                    line = line.substring(idx).strip();
                    
                    idx = line.indexOf(" ");
                    String pkg = line.substring(0, idx);
                    line = line.substring(idx).strip();
                    
                    idx = line.indexOf(" ");
                    double xCoord = Double.parseDouble(line.substring(0, idx));
                    line = line.substring(idx).strip();
                    
                    idx = line.indexOf(" ");
                    double yCoord = Double.parseDouble(line.substring(0, idx));
                    line = line.substring(idx).strip();
                    
                    idx = line.indexOf(" ");
                    double rot = normalizeAngle(Double.parseDouble(line.substring(0, idx)));
                    line = line.substring(idx).strip();

                    String side = line.substring(0, 3);

                    //For bottom side components, KiCad exports the negated x coordinate to the
                    //.pos file so we need to negate it back
                    if (side.equals("bot")) {
                        xCoord *= -1;
                    }
                    
                    ret.add(String.format("%s,%s,%s,%.4f,%.4f,%.4f,%s", refDes, value, pkg, xCoord, yCoord, rot, side));
                    
                    line = reader.readLine();
                }
            }
        }
        Collections.sort(ret);
        return ret;
    }
    
    /**
     * Normalizes angles to be in the range (-180,+180] degrees. Also converts -0.0 to 0.0.
     * @param angleDeg - the angle in degrees to be normalized
     * @return the normalized angle in degrees
     */
    private double normalizeAngle(double angleDeg) {
        while (angleDeg <= -180) {
            angleDeg += 360;
        }
        while (angleDeg > 180) {
            angleDeg -= 360;
        }
        if (angleDeg == -0.0) {
            angleDeg = 0;
        }
        return angleDeg;
    }
}
