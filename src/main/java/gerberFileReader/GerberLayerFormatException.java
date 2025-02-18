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
 * A class to represent various exceptions that can occur during the parsing of invalid Gerber files
 */
public class GerberLayerFormatException extends Exception {
    private static final long serialVersionUID = 4752746107470693024L;

    public GerberLayerFormatException() {
        super();
    }

    public GerberLayerFormatException(String message) {
        super(message);
    }

    public GerberLayerFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public GerberLayerFormatException(Throwable cause) {
        super(cause);
    }

    public GerberLayerFormatException(String message, Throwable cause, boolean enableSuppression, 
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
