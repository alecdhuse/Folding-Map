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
package co.foldingmap.map.vector;

import co.foldingmap.map.MapUtilities;
import co.foldingmap.map.NumericValueOutOfRangeException;
import co.foldingmap.xml.XmlOutput;

/**
 *
 * @author Alec
 */
public class LatLonBox {
    
    protected float north, south, east, west;
    
    public LatLonBox() {
        north = 0;
        south = 0;
        east  = 0;
        west  = 0;
    }
    
    /**
     * Constructor for objects of class LatLonAltBox using floats.
     * 
     * @param north North bounds of the box.
     * @param south South bounds of the box.
     * @param east  East bounds of the box.
     * @param west  West bounds of the box.
     */
    public LatLonBox(float north, float south, float east, float west) {        
        setNorth(north);
        setSouth(south);
        setEast(east);
        setWest(west);
    }     
    
    /**
     * Returns if this LatLonBox contains a given Coordinate.
     * 
     * @param c
     * @return 
     */
    public boolean contains(Coordinate c) {
        boolean     returnValue = false;
        Coordinate  max, min;
        float       diff;
        
        try {
            max = new Coordinate(0, south, east);
            min = new Coordinate(0, north, west);

            if (west > 0 && east < 0) {
                diff = (180 - west) + Math.abs(180 + east);
            } else {
                diff = east - west;
            }

            if (c.isEastOf(min)  &&
                c.isWestOf(max, diff)  &&
                c.isNorthOf(max) &&
                c.isSouthOf(min)) {

                returnValue = true;
            }
        } catch (Exception e) {
            System.err.println("Error in LatLonBox.contains(Coordinate) - " + e);
        }
        
        return returnValue;
    }   
    
    /**
     * Returns if this LatLonBox equals another object.
     * 
     * @param obj
     * @return 
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LatLonBox) {
            LatLonBox box = (LatLonBox) obj;
            
            return (this.hashCode() == box.hashCode());
        } else {
            return false;
        }
    }

    /**
     * Returns hash code for this object.
     * 
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Float.floatToIntBits(this.north);
        hash = 52 * hash + Float.floatToIntBits(this.south);
        hash = 51 * hash + Float.floatToIntBits(this.east);
        hash = 50 * hash + Float.floatToIntBits(this.west);
        
        return hash;
    }
    
    /**
     * Returns a Coordinate representing the center of this LatLonBox.
     * 
     * @return 
     */
    public Coordinate getCenter() {
        Coordinate center;
        float      centerLat, centerLon;
                     
        try {
            centerLat = south       + (getHeight() / 2.0f);
            centerLon = west        + (getWidth()  / 2.0f); 
            center    = new Coordinate(0, centerLat, centerLon);  
        } catch (Exception e) {
            System.err.println("Error in LatLonBox.getCenter() - " + e);
            center = Coordinate.UNKNOWN_COORDINATE;
        }
        
        return center;
    }      
    
    /**
     * Returns the height in Latitude degrees of this LatLonAltBox.
     * 
     * @return 
     */
    public float getHeight() {
        float height = -1;

        if ((this.north > 0) && (this.south >= 0)) {
            height = north - south;
        } else if ((this.north > 0) && (this.south < 0)) {
            height = north + Math.abs(south);
        } else if ((this.north < 0) && (this.south < 0)) {
            height = Math.abs(south) - Math.abs(north);
        }

        return height;
    }      
    
    /**
     * Returns this LatLonAltBox's North boundary Latitude.
     * @return 
     */
    public float getNorth() {
        return north;
    }   
    
    /**
     * Returns this LatLonAltBox's South Latitude.
     * 
     * @return 
     */
    public float getSouth() {
        return south;
    }
    
    /**
     * Returns this LatLonAltBox's East Longitude value.
     * 
     * @return 
     */
    public float getEast() {
        return east;
    }
    
    /**
     * Returns this LatLonAltBox's West Longitude value.
     * @return 
     */
    public float getWest() {
        return west;
    }
    
    /**
     * Returns the width in Longitude Degrees of this LatLonAltBox.
     * 
     * @return 
     */
    public float getWidth() {
        float width = -1;

        if ((this.west >= 0) && (this.east >= 0)) {
            width = east - west;
        } else if ((this.west < 0) && (this.east > 0)) {
            width = Math.abs(west) + east;
        } else if ((this.west > 0) && (this.east < 0)) {
            width = (180 - west) + (180 - Math.abs(east));
        } else if ((this.west < 0) && (this.east < 0)) {
            width = Math.abs(east - west);
        }

        return width;
    }
    
    /**
     * Returns if this LatLonBox overlaps another LatLonAltBox.
     * @param box
     * @return 
     */
    public boolean overlaps(LatLonBox box) {
        boolean     overlap = false;
                
        Coordinate  northWest, northEast, southWest, southEast;
        Coordinate  boxNorthWest, boxNorthEast, boxSouthWest, boxSouthEast;

        boolean     northSouth = false;
        boolean     eastWest   = false;
        
        northWest    = new Coordinate(0, north, west);
        northEast    = new Coordinate(0, north, east);
        southWest    = new Coordinate(0, south, west);
        southEast    = new Coordinate(0, south, east);

        boxNorthWest = new Coordinate(0, box.getNorth(), box.getWest());
        boxNorthEast = new Coordinate(0, box.getNorth(), box.getEast());
        boxSouthWest = new Coordinate(0, box.getSouth(), box.getWest());
        boxSouthEast = new Coordinate(0, box.getSouth(), box.getEast());
        
        if (northWest.isSouthOf(boxNorthWest) && northWest.isNorthOf(boxSouthWest)) {
            northSouth = true;
        } else if (southWest.isSouthOf(boxNorthWest) && southWest.isNorthOf(boxSouthWest)) {
            northSouth = true;
        } else if (northWest.isNorthOf(boxNorthWest) && southWest.isSouthOf(boxSouthWest)) {
            northSouth = true;
        } else if (boxNorthWest.isNorthOf(northWest) && boxSouthWest.isSouthOf(southWest)) {   
            northSouth = true;
        }

        if (northWest.isEastOf(boxNorthWest) && northWest.isWestOf(boxSouthEast, 90)) {
            eastWest = true;
        } else if (northWest.isWestOf(boxNorthWest, 90) && northWest.isEastOf(boxSouthWest)) {
            eastWest = true;
        } else if (southEast.isEastOf(boxNorthWest) && southEast.isWestOf(boxSouthEast, 90)) {
            eastWest = true;
        } else if (northWest.isWestOf(boxNorthWest, 90) && northEast.isEastOf(boxSouthEast)) {            
            eastWest = true;
        }       
        
        if (eastWest && northSouth)
            overlap = true;

        return overlap;
    }       
    
    /**
     * Sets this LatLonAltBox's north value.
     * 
     * @param north
     *      A Latitude between -90 and 90 inclusive.
     * 
     * @throws NumericValueOutOfRangeException 
     */
    public final void setNorth(float north) throws NumericValueOutOfRangeException {
        if ((north <= 90) && (north >= -90)) {
            this.north = north;
        } else {
            throw (new NumericValueOutOfRangeException(north));
        }
    }
    
    /**
     * Sets this LatLonAltBox's south value.
     * 
     * @param south
     *      A Latitude between -90 and 90 inclusive.
     * @throws NumericValueOutOfRangeException 
     */
    public final void setSouth(float south) throws NumericValueOutOfRangeException {
        if ((south <= 90) && (south >= -90)) {
            this.south = south;
        } else {
            throw (new NumericValueOutOfRangeException(south));
        }
    }
    
    /**
     * Sets this LatLonAltBox's east value.
     * 
     * @param east
     *      A Longitude between -180 and 180 inclusive.
     */
    public final void setEast(float east){
        this.east = (float) MapUtilities.normalizeLongitude(east);
    }
    
    /**
     * Sets this LatLonAltBox's west value.
     * 
     * @param west
     *      A Longitude between -180 and 180 inclusive.
     */
    public final void setWest(float west) {
        this.west =  (float) MapUtilities.normalizeLongitude(west);
    } 
    
    /**
     * Returns a String Representation of this Object.
     * 
     * @return 
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append( "North: ");
        sb.append(Double.toString(north));
        sb.append(" South: ");
        sb.append(Double.toString(south));
        sb.append(" East: ");
        sb.append(Double.toString(east));
        sb.append(" West: ");
        sb.append(Double.toString(west));

        return sb.toString();
    }      
    
    /**
     * Writes the LatLonAltBox to fmXML.
     * 
     * @param xmlWriter 
     */
    public void toXML(XmlOutput xmlWriter) {
        xmlWriter.openTag ("LatLonBox");

        xmlWriter.writeTag("north", Double.toString(north));
        xmlWriter.writeTag("south", Double.toString(south));
        xmlWriter.writeTag("east",  Double.toString(east));
        xmlWriter.writeTag("west",  Double.toString(west));

        xmlWriter.closeTag("LatLonBox");
    }       
}
