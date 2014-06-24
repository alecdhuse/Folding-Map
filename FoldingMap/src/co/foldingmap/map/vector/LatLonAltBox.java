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

import co.foldingmap.xml.XmlOutput;

/**
 * This is a bounding box implementing the KML/FmXml object of the same name.
 * 
 * @author Alec
 */
public class LatLonAltBox extends LatLonBox {
    public static final int CLAMP_TO_GROUND    = 0;
    public static final int RELATIVE_TO_GROUND = 1;
    public static final int ABSOLUTE           = 2;
    
    private float minAltitude, maxAltitude;
    private int   altitudeMode;
 
    /**
     * Constructor to convert LatLonBox to LatLonAltBox.
     * 
     * @param latLonBox 
     */
    public LatLonAltBox(LatLonBox latLonBox) {
        try {
            this.north = latLonBox.getNorth();
            this.south = latLonBox.getSouth();
            this.east  = latLonBox.getEast();
            this.west  = latLonBox.getWest();

            this.minAltitude  = -1;
            this.maxAltitude  = -1;
            this.altitudeMode = ABSOLUTE;        
        } catch (Exception e) {
            System.err.println("Error in LatLonAltBoc constructor(LatLonBox) - " + e);
        }            
    }
    
    /**
     * Constructor for objects of class LatLonAltBox using string parameters.
     */
    public LatLonAltBox(String north, String south, String east, String west) {
        
        try {
            setNorth(Float.parseFloat(north));
            setSouth(Float.parseFloat(south));
            setEast (Float.parseFloat(east));
            setWest (Float.parseFloat(west));

            this.minAltitude  = 0;
            this.maxAltitude  = 0;
            this.altitudeMode = ABSOLUTE;
        } catch (Exception e) {
            System.err.println("Error in LatLonAltBox constructor(String, String, String, String) - " + e);
        }
    }    
    
    /**
     * Constructor for objects of class LatLonAltBox using floats.
     */
    public LatLonAltBox(float north, float south, float east, float west, float minAltitude, float maxAltitude) {
        
        setNorth(north);
        setSouth(south);
        setEast(east);
        setWest(west);

        this.minAltitude  = minAltitude;
        this.maxAltitude  = maxAltitude;
        this.altitudeMode = ABSOLUTE;
    }    
    
    /**
     * Creates a new LatLonAltBox by combining the the areas of two LatLonAltBoxes.
     * 
     * @param box1
     * @param box2
     * @return 
     */
    public static LatLonAltBox combine(LatLonAltBox box1, LatLonAltBox box2) {
        float north, south, east, west, maxAlt, minAlt;
        
        north  = Math.max(box1.getNorth(), box2.getNorth());
        south  = Math.min(box1.getSouth(), box2.getSouth());
        east   = Math.max(box1.getEast(),  box2.getEast());
        west   = Math.min(box1.getWest(),  box2.getWest());
        
        maxAlt = Math.max(box1.getMaxAltitude(), box2.getMaxAltitude());
        minAlt = Math.min(box1.getMinAltitude(), box2.getMinAltitude());                
        
        return new LatLonAltBox(north, south, east, west, minAlt, maxAlt);
    }    
                
    /**
     * Returns if this LatLonAltBox is equal to another.
     * 
     * @param box
     * @return 
     */
    @Override
    public boolean equals(Object object) {
        LatLonAltBox box;
        
        if (!(object instanceof LatLonAltBox)) {
            return false;
        } else {
            box = (LatLonAltBox) object;
            if (this.altitudeMode == box.getAltitudeMode() &&
                this.east         == box.getEast() &&
                this.maxAltitude  == box.getMaxAltitude() &&
                this.minAltitude  == box.getMinAltitude() &&
                this.north        == box.getNorth() &&
                this.south        == box.getSouth() &&
                this.west         == box.getWest()) {

                return true;
            } else {
                return false;
            }    
        }
    }
    
    /**
     * Returns the Altitude Mode for this LatLonAltBox.
     * 
     * @return 
     */
    public int getAltitudeMode() {
        return this.altitudeMode;
    }
    
    /**
     * Returns a Coordinate representing the center of this LatLonAltBox.
     * 
     * @return 
     */
    @Override
    public Coordinate getCenter() {
        Coordinate center;
        float      centerAlt, centerLat, centerLon;
                     
        try {
            centerAlt = minAltitude + ((maxAltitude - minAltitude) / 2.0f);
            centerLat = south       + (getHeight() / 2.0f);
            centerLon = west        + (getWidth()  / 2.0f); 
            center    = new Coordinate(centerAlt, centerLat, centerLon);  
        } catch (Exception e) {
            System.err.println("Error in LatLonAltBox.getCenter() - " + e);
            center = Coordinate.UNKNOWN_COORDINATE;
        }
        
        return center;
    }    
    
    /**
     * Creates and returns two LatLonAltBoxes split horizontally.
     * 
     * @return 
     */    
    public LatLonAltBox[] getHalfs() {
        Coordinate      center;
        LatLonAltBox[]  halfs;
        
        center   = this.getCenter();
        halfs    = new LatLonAltBox[2];
        halfs[0] = new LatLonAltBox(north, (float) center.getLatitude(), east, west, 0, 0);
        halfs[1] = new LatLonAltBox((float) center.getLatitude(), south, east, west, 0, 0);
                
        return halfs;
    }          
    
    /**
     * Returns this LatLonAltBox's MaxAltitude.
     * 
     * @return 
     */
    public float getMaxAltitude() {
        return maxAltitude;
    }

    /**
     * Returns this LatLonAltBox's MinAltitude.
     * 
     * @return 
     */    
    public float getMinAltitude() {
        return minAltitude;
    } 
    
    /**
     * Returns a Coordinate representing this LatLonAltBox's North-East or
     * Top-Right Latitude and Longitude.
     * 
     * @return 
     */
    public Coordinate getNorthEastCoordinate() {
        return new Coordinate(minAltitude, north, east);
    }    
    
    /**
     * Returns a Coordinate representing this LatLonAltBox's North-West or
     * Top-Left Latitude and Longitude.
     * 
     * @return 
     */
    public Coordinate getNorthWestCoordinate() {
        return new Coordinate(minAltitude, north, west);
    }    
    
    /**
     * Creates four new LatLonAltBoxes each representing a quad of the original
     * LatLonAltBox.  The Result is an array with the following quads:
     * 
     *      [0][1]
     *      [2][3]
     * 
     * @return 
     */    
    public LatLonAltBox[] getQuads() {
        Coordinate      center;
        LatLonAltBox[]  quads;
        
        center   = this.getCenter();
        quads    = new LatLonAltBox[4];
        quads[0] = new LatLonAltBox(north, (float) center.getLatitude(), (float) center.getLongitude(), west, 0, 0);
        quads[1] = new LatLonAltBox(north, (float) center.getLatitude(), east, (float) center.getLongitude(), 0, 0);
        quads[2] = new LatLonAltBox((float) center.getLatitude(), south, (float) center.getLongitude(), west, 0, 0);
        quads[3] = new LatLonAltBox((float) center.getLatitude(), south, east, (float) center.getLongitude(), 0, 0);
                
        return quads;
    }            
    
    /**
     * Generates and returns a Coordinate representing the South-East or
     * Bottom-Right Latitude and Longitude.
     * 
     * @return 
     */
    public Coordinate getSouthEastCoordinate() {
        return new Coordinate(minAltitude, south, east);
    }

    /**
     * Generates and returns a Coordinate representing the South-West or
     * Bottom-Left Latitude and Longitude.
     * 
     * @return 
     */    
    public Coordinate getSouthWestCoordinate() {
        return new Coordinate(minAltitude, south, west);
    }        
    
    /**
     * Returns if this LatLonAltBox is contained by another LatLonAltBox.
     * 
     * @param box
     * @return 
     */
    public boolean isContained(LatLonAltBox box) {
        boolean      withinViewPort;
        
        withinViewPort = false;
        
        if ((north < box.getNorth()) && (north > box.getSouth())) {
            if ((west > box.getWest()) && (west < box.getEast())) {
                withinViewPort = true;
            } else if ((east > box.getWest()) && (east < box.getEast())) {
                withinViewPort = true;
            }               
        } else if ((south > box.getSouth()) && (south > box.getNorth())) {
            if ((west > box.getWest()) && (west < box.getEast())) {
                withinViewPort = true;
            } else if ((east > box.getWest()) && (east < box.getEast())) {
                withinViewPort = true;
            }              
        }
        
        return withinViewPort;
    }     
    
    /**
     * Returns if this LatLonAltBoc overlaps another LatLonAltBox.
     * @param box
     * @return 
     */
    public boolean overlaps(LatLonAltBox box) {
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
     * Sets this LatLonAltBox's MaxAltitude value.
     * 
     * @param maxAltitude 
     */
    public void setMaxAltitude(float maxAltitude) {
        this.maxAltitude = maxAltitude;
    }

    /**
     * Sets this LatLonAltBox's MinAltitude value.
     * 
     * @param minAltitude 
     */    
    public void setMinAltitude(float minAltitude) {
        this.minAltitude = minAltitude;
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
        xmlWriter.openTag ("LatLonAltBox");

        xmlWriter.writeTag("north",       Double.toString(north));
        xmlWriter.writeTag("south",       Double.toString(south));
        xmlWriter.writeTag("east",        Double.toString(east));
        xmlWriter.writeTag("west",        Double.toString(west));
        xmlWriter.writeTag("minAltitude", Double.toString(minAltitude));
        xmlWriter.writeTag("maxAltitude", Double.toString(maxAltitude));

        switch (altitudeMode) {
            case CLAMP_TO_GROUND:
                xmlWriter.writeTag("altitudeMode", "clampToGround");
                break;
            case RELATIVE_TO_GROUND:
                xmlWriter.writeTag("altitudeMode", "relativeToGround");
                break;
            case ABSOLUTE:
                xmlWriter.writeTag("altitudeMode", "absolute");
                break;
        }

        xmlWriter.closeTag("LatLonAltBox");
    }    
}
