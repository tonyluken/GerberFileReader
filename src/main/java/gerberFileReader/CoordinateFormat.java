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
 * A class to hold the Gerber format specification and apply it to parse coordinates.
 * 
 * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2024-05_en.pdf?94b45d8745c1a068fd091f095a26ddeb#page=48">
 * Section 4.2.2 of the Gerber Layer Format Specification</a>
 */
class CoordinateFormat {
    
    private final String formatSpecification;
    
    private final boolean trailingZeroOmission;
    private final int xSignificance;
    private final int xFraction;
    private final int ySignificance;
    private final int yFraction;
    
    /**
     * Constructs a CoordinateFormat object based upon the given Gerber format specification string
     * @param formatSpecification - the format specification string
     * @throws GerberLayerFormatException if the string is invalid or contains an unsupported format
     * 
     * @see <a href="https://www.ucamco.com/files/downloads/file_en/456/gerber-layer-format-specification-revision-2024-05_en.pdf?94b45d8745c1a068fd091f095a26ddeb#page=48">
     * Section 4.2.2 of the Gerber Specification</a>
     */
    public CoordinateFormat(String formatSpecification) throws GerberLayerFormatException {
        // Parse the format statement, such as "FSLAX24Y24"
        if (!(formatSpecification.startsWith("FSLAX") || formatSpecification.startsWith("FSTAX")) 
                || formatSpecification.length() != 10 || formatSpecification.charAt(7) != 'Y') {
            throw new GerberLayerFormatException(
                    "Invalid or Unsupported Format Specification: " + formatSpecification);
        }
        this.formatSpecification = formatSpecification;
        trailingZeroOmission = formatSpecification.charAt(2) == 'T';
        try {
            xSignificance = Integer.parseInt(formatSpecification.substring(5, 6));
            xFraction = Integer.parseInt(formatSpecification.substring(6, 7));
            ySignificance = Integer.parseInt(formatSpecification.substring(8, 9));
            yFraction = Integer.parseInt(formatSpecification.substring(9, 10));
        }
        catch (Exception ex) {
            throw new GerberLayerFormatException(
                    "Invalid Format Specification: " + formatSpecification);
        }
    }

    /**
     * @return the Gerber format specification command
     */
    public String getFormatCommand() {
        return formatSpecification;
    }

    /**
     * Parses a coordinate string and converts it to a numerical value based on this coordinate
     * format.
     *
     * @param numString - the string to parse (must begin with one of the letters 'X', 'Y', 'I', or
     * 'J' and be followed by one or more of the ten numeric characters '0' through '9' with 
     * optional leading sign (+/-)
     * @return the numerical value of the coordinate
     * @throws GerberLayerFormatException if the coordinate is invalid
     */
    public double parseCoordinate(String numString) throws GerberLayerFormatException {
        int significance;
        int fraction;
        if (numString.charAt(0) == 'X' || numString.charAt(0) == 'I') {
            significance = xSignificance;
            fraction = xFraction;
        }
        else if (numString.charAt(0) == 'Y' || numString.charAt(0) == 'J') {
            significance = ySignificance;
            fraction = yFraction;
        }
        else {
            throw new GerberLayerFormatException("Invalid coordinate: " + numString);
        }
        double dVal;
        try {
            dVal = Double.parseDouble(numString.substring(1));
        }
        catch (NumberFormatException ex) {
            throw new GerberLayerFormatException("Invalid coordinate: " + numString);
        }
        if (trailingZeroOmission) {
            int skip = 1;
            if (numString.charAt(1) == '+' || numString.charAt(1) == '-') {
                skip++;
            }
            return dVal * Math.pow(10, (significance - numString.length() + skip));
        }
        dVal /= Math.pow(10, fraction);
        return dVal;
      }

}
