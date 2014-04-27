/* 
 * Copyright (C) 2014 Alec Dhuse
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package co.foldingmap.data;

import co.foldingmap.Logger;
import co.foldingmap.map.vector.Coordinate;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author Alec
 */
public class DataCell {
    protected String    cellValue;
    
    public DataCell() {
        cellValue = "";
    }
    
    public DataCell(String cellValue) {        
        this.cellValue = cellValue.trim().replaceAll("[\\u00A0]", "");
    }    
    
    /**
     * Returns if this cell contains information that could be a latitude
     * or longitude.
     * 
     * @return 
     */
    public boolean containsCoordinateInformation() {
        return containsCoordinateInformation(cellValue);
    }    
    
    /**
     * Returns if this the given value contains information that could 
     * be a latitude or longitude.
     * 
     * @return 
     */
    public boolean containsCoordinateInformation(String value) {
        char    firstCharacter;
        boolean containsCoordinate;
        float   f;
        String  tempValue;
        
        containsCoordinate = false;
        tempValue          = value;
        
        if (tempValue.length() > 0) {
            firstCharacter = tempValue.charAt(0);

            //test to see if straing starts with a character that denotes a coordinate.
            switch (firstCharacter) {
                case 'N':
                case 'n':
                case 'S':
                case 's':
                case 'E':
                case 'e':
                case 'W':
                case 'w':
                case '+':
                case '-':
                    tempValue = tempValue.substring(1);
                    break;
            }

            try {
                f = Float.parseFloat(tempValue);
                
                if ((f >= -180.0f) && (f <= 180.0f)) {
                    if (tempValue.indexOf(".") > 0) {
                        containsCoordinate = true;
                    } else {
                        containsCoordinate = false;
                    }
                } else {
                    containsCoordinate = false;
                }                
            } catch (Exception e) {
                containsCoordinate = false;
            }

        } //end length check

//        if (!containsCoordinate) {
//            if (getCoordinate() != null)
//                containsCoordinate = true;
//        }
        
        return  containsCoordinate;
    }
    
    /**
     * Checks to see if this DataCell equals another.
     * 
     * @param dataCell
     * @return 
     */
    public boolean equals(DataCell dataCell) {
        return this.cellValue.equals(dataCell.toString());
    }
    
    /**
     * Attempts to return a Coordinate from the cellValue.
     * Returns null if unsuccessful. 
     * 
     * @return 
     */
    public Coordinate getCoordinate() {
        ArrayList<String>   tokens;
        Coordinate          c;
        float               number;
        int                 coordinateInfo;
        String              alt, lat, lon;
        
        alt            = "";
        c              = null;
        coordinateInfo = 0;
        lat            = "";
        lon            = "";
        tokens         = new ArrayList<String>();
        
        try {
            if (cellValue.contains(",")) 
                tokens = getTokens(",");                
                        
            if (tokens.isEmpty()) 
                tokens = getTokens(" ");            
            
            if (tokens.size() >= 1) {
                //Check to see if two tokens could contain coordinate Information
                for (String s: tokens) {
                    if (containsCoordinateInformation(s)) {
                        coordinateInfo++;
                        number = getFloatValue(s);
                        
                        //Check to see is a hemisphere indecator exists.
                        if (s.contains("n") || s.contains("N") || s.contains("s") || s.contains("S")) {
                            lat = s;
                        } else if (s.contains("e") || s.contains("E") || s.contains("w") || s.contains("W")) {
                            lon = s;
                        }
                        
                        if (number != Float.NaN) {
                            /*
                            * Check to see if the current token is a number
                            * between 90 and 108 or -90 and -180 thus indicating
                            * it is a longitude
                            */
                            if (number > 90 && number <= 180) {
                                lon = s;
                            } else if (number < -90 && number >= -180) {
                                lon = s;
                            } else if (number >= 0 && number <= 90) {
                                if (lon.equals("")) {
                                    lon = s;
                                } else {
                                    lat = s;
                                }
                            } else if (number <= 0 && number >= -90) {
                                if (lon.equals("")) {
                                    lon = s;
                                } else {
                                    lat = s;
                                }
                            } else {
                                alt = s;
                            }                           
                        } //end number check                                               
                    } //end coordinate info check
                } //end for loop
                
                if (!alt.equals("") && lat.equals("") && lon.equals("")) {
                    try {
                        c = new Coordinate(Float.parseFloat(alt), Float.parseFloat(lat), Float.parseFloat(lon));
                    } catch (Exception e) {
                        
                    }
                }
            }
        } catch( Exception e) {
            Logger.log(Logger.ERR, "Error in DataCell.getCoordinate() - " + e);
        }
        
        return c;
    }
    
    /**
     * Attempts to return a float value for the cellValue;
     * Returns Float.NaN if unsuccessful. 
     * 
     * @return 
     */
    public float getFloatValue() {
        return getFloatValue(cellValue);
    }
    
    /**
     * Attempts to return a float value for the given value;
     * Returns Float.NaN if unsuccessful. 
     * 
     * @return 
     */
    public float getFloatValue(String value) {
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            return Float.NaN;
        }
    }    
        
    /**
     * Returns the callValue as an ArrayList of tokens delimited by the 
     * given delimiter.
     * 
     * @param delimiter
     * @return 
     */
    public ArrayList<String> getTokens(String delimiter) {
        ArrayList<String> tokens = new ArrayList<String>();
        StringTokenizer   st     = new StringTokenizer(this.cellValue, delimiter);
        
        while (st.hasMoreTokens()) {
            tokens.add(st.nextToken());
        }
        
        return tokens;
    }
        
    /**
     * Splits the input cell text into tokens based on whitespace.
     * 
     * @param  cellText
     * @return An ArrayList with all tokens
     */
    public ArrayList<String> splitCellText() {
        ArrayList       tokens = new ArrayList<String>();
        StringTokenizer st     = new StringTokenizer(cellValue, " ,");

        while (st.hasMoreTokens()) {
            tokens.add(st.nextToken());
        }

        return tokens;
    }    
    
    /**
     * Returns the String value of this Data Cell.
     * 
     * @return 
     */
    @Override
    public String toString() {
        return cellValue;
    }    
    
    /**
     * Returns a trimmed version of the CellValue, one devoid of leading and
     * trailing whitespace.
     * 
     * @return 
     */
    public String trim() {
        return cellValue.trim().replaceAll("[\\u00A0]", "");
    }
}
