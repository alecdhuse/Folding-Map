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

import co.foldingmap.map.MapObject;
import co.foldingmap.map.NumericValueOutOfRangeException;
import java.awt.geom.Point2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.StringTokenizer;

/**
 * The class that represents a Latitude, Longitude, Altitude, Time Coordinate
 * 
 * @author Alec
 */
public class Coordinate {
    protected ArrayList<MapObject>  parentObjects;
    protected byte                  pullCount;
    protected long                  id, timestamp;
    protected float                 altitude, latitude, longitude;    
    protected Point2D.Float         centerPoint, leftPoint, rightPoint;
            
    public static final Coordinate UNKNOWN_COORDINATE = new Coordinate(0, -999, -999);
    public static final float      RAD_CONVERSION     = 0.01745329251994f;    
    public static final float      DEC_CONVERSION     = 57.2957795130823f; 
    
    /**
     * Creates a coordinate from a string formated longitude,latitude,altitude
     * Example: 10.5,4.5,1300
     * 
     * @param coordinates 
     */
    public Coordinate(String coordinate) {
        try {
            StringTokenizer st;

            if (coordinate.indexOf(",") > 0) {
                //read comma delimited coordinate data
                st = new StringTokenizer(coordinate, ",");
            } else {
                //read space delimited coordinate data
                st = new StringTokenizer(coordinate);
            }

            this.setLongitude(Float.parseFloat(st.nextToken()));
            this.setLatitude(Float.parseFloat(st.nextToken()));
            
            if (st.hasMoreTokens())
                altitude = Float.parseFloat(st.nextToken());
            
            if (st.hasMoreTokens()) {
                setTimestamp(st.nextToken());
            } else {
                this.timestamp = System.currentTimeMillis();
            }
            
            this.pullCount =  0;
            this.id        = 0;
        } catch (Exception e) {
            System.err.println("Error in Coordinate(String = " + coordinate + ") - " + e);
        }        
    }    
    
    /**
     * Creates a coordinate from altitude, latitude and longitude values
     * 
     * @param altitude
     * @param latitude
     * @param longitude 
     */
    public Coordinate(float altitude, double latitude, double longitude) {
        this.setLongitude((float) longitude);
        this.setLatitude((float) latitude);
        this.setAltitude((float) altitude);

        this.timestamp           = System.currentTimeMillis();
        this.pullCount           = 0;        
        this.id                  = 0;
    }
    
    /**
     * Creates a coordinate from altitude, latitude and longitude values.
     * Sets the coordinate id to the id value provided.
     * 
     * @param altitude
     * @param latitude
     * @param longitude
     * @param id 
     */
    public Coordinate(float altitude, double latitude, double longitude, long id) {
        this.setLongitude((float) longitude);
        this.setLatitude((float) latitude);
        this.setAltitude((float) altitude);

        this.timestamp           = System.currentTimeMillis();
        this.pullCount           = 0;        
        this.id                  = id;
    }    
    
    /**
     * Creates a coordinate from altitude, latitude and longitude values
     * Does not perform bounds checking.
     * 
     * @param altitude
     * @param latitude
     * @param longitude 
     * @param noChecke 
     */
    public Coordinate(float altitude, double latitude, double longitude, boolean noCheck) {
        this.longitude = (float) longitude;
        this.latitude  = (float) latitude;
        this.altitude  = (float) altitude;

        this.timestamp           = System.currentTimeMillis();
        this.pullCount           = 0;       
        this.id                  = 0;
    }    
    
    /**
     * Creates a coordinate from altitude, latitude, longitude and timestamp values
     * 
     * @param altitude
     * @param latitude
     * @param longitude
     * @param timestamp in the format: yyyy-MM-dd'T'HH:mm:ssZ
     */
    public Coordinate(float altitude, double latitude, double longitude, String timestamp) {
        this.setLongitude((float) longitude);
        this.setLatitude((float) latitude);
        this.setAltitude((float) altitude);
        
        this.pullCount = 0;

        if (timestamp.length() > 0) {
            this.setTimestamp(timestamp);
        } else {
            this.timestamp = System.currentTimeMillis();
        }    
        
        this.id = 0;
    }        
        
    /**
     * Adds a reference for an object that uses this Coordinate.  This will 
     * provide a quick way for the program to check which object are using this
     * coordinate.
     * 
     * @param parent 
     */
    public void addParent(MapObject parent) {
        if (parentObjects == null) {
            //Average parent use is 3
            parentObjects = new ArrayList<MapObject>(3); 
        }
        
        parentObjects.add(parent);               
    }
    
    /**
     * Creates a new coordinate that is a copy of this one.
     * 
     * @return The new copy Coordinate
     */
    public Coordinate copy() {
        Coordinate  newCopy;

        newCopy = new Coordinate(this.altitude, this.latitude, this.longitude, getTimestamp());
        newCopy.setPullCount(pullCount);

        return newCopy;        
    }
    
    /**
     * Test to see if this coordinate equals another coordinate.  
     * It Compares altitude, latitude and longitude; it does not check the timestamp.
     * @param coordinateToCompare 
     * @return 
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Coordinate) {
            Coordinate c = (Coordinate) o;

            if (this.getAltitude()  == c.getAltitude() &&
                this.getLatitude()  == c.getLatitude() &&
                this.getLongitude() == c.getLongitude()) {
                
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }                      
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Float.floatToIntBits(this.altitude);
        hash = 85 * hash + Float.floatToIntBits(this.latitude);
        hash = 32 * hash + Float.floatToIntBits(this.longitude);
        
        return hash;
    }
    
    /**
     * Returns this coordinates Altitude.
     * 
     * @return 
     */
    public float getAltitude() {    
        return altitude;
    }

    /**
     * Creates a Point2D with x and y equals to longitude and latitude respectively.
     * 
     * @return 
     */
    public Point2D getAsPoint2D() {
         return new Point2D.Float(this.longitude, this.latitude);
    }

    /**
     * Returns the center on screen point for this Coordinate.
     */
    public Point2D.Float getCenterPoint() {
        if (centerPoint == null) 
            parentObjects.get(0).getParentLayer().getParentMap().addCoordinateNode(this);
        
        
        return centerPoint;
    }
    
    /**
     * Returns the left on screen point for this Coordinate.
     */
    public Point2D.Float getLeftPoint() {
        return leftPoint;
    }    
    
    /**
     * Returns the right on screen point for this Coordinate.
     */
    public Point2D.Float getRightPoint() {
        return rightPoint;
    }    
    
    /**
     * Returns the timestamp as milliseconds since the epoch.
     * 
     * @return 
     */
    public long getDate() {
        return timestamp;
    }

    /**
     * Returns the ID of this coordinate, if no ID is set 0 is returned.
     * 
     * @return 
     */
    public long getID() {
        return this.id;
    }
    
    /**
     * Returns the Coordinates Latitude.
     * 
     * @return The Latitude in Decimal.
     */
    public double getLatitude() {    
        return latitude;
    }

    /**
     * Calculates the Decimal Latitude given a Radian Latitude.
     * 
     * @param latitude  The Latitude in Radians.
     * @return          The Latitude in Decimal.
     */
    public static double getLatitudeInDecimal(double latitude) {
        return (float) DEC_CONVERSION * latitude;
    }
    
    /**
     * Calculates the Radian Latitude given a Decimal Latitude.
     * 
     * @param latitude  The Latitude in Decimal.
     * @return          The Latitude in Radians.
     */
    public static double getLatitudeInRadians(double latitude) {
        return (float) RAD_CONVERSION * latitude;
    }
    
    /**
     * Returns the Latitude in Radians of this Coordinate.
     * 
     * @return The Latitude in Radians.
     */
    public double getLatitudeInRadians() {    
        return getLatitudeInRadians(this.latitude);
    }

    /**
     * Returns the Coordinates Longitude.
     * 
     * @return The Longitude in Decimal
     */
    public double getLongitude() {    
        return longitude;
    }
    
    /**
     * Calculates a Decimal Longitude given a Longitude in Radians.
     * 
     * @param longitude The Longitude in Radians
     * @return          The Longitude in Decimal
     */
    public static double getLongitudeInDecimal(double longitude) {                
        return (float) DEC_CONVERSION * longitude;
    }
    
    /**
     * Returns this Coordinates Longitude in Radians.
     * 
     * @return The Longitude in Radians
     */
    public double getLongitudeInRadians() {    
        return getLongitudeInRadians(this.longitude);
    }    
    
    /**
     * Calculates a Radian Longitude given a Longitude in Decimal.
     * 
     * @param longitude The Longitude in Decimal
     * @return          The Longitude in Radians
     */
    public static double getLongitudeInRadians(double longitude) {    
        return (float) RAD_CONVERSION * longitude;
    }         
    
    /**
     * Returns a list of all parent VectorObject for this coordinate.
     * 
     * @return 
     */
    public ArrayList<VectorObject> getParentVectorObjects() {
        ArrayList<VectorObject> parents = new ArrayList<VectorObject>();
        
        try {
            if (parentObjects != null) {
                for (MapObject object: parentObjects) {
                    if (object instanceof VectorObject) 
                        parents.add((VectorObject) object);                
                }
            }
        } catch (Exception e) {
            System.out.println("Error in Coordinate.getParentVectorObjects() - " + e);
        }
        
        return parents;
    }

    /**
     * Returns this coordinates timestamp.
     * 
     * @return The timestamp in yyyy-mm-ddThh:mm:ssZ format
     */
    public String getTimestamp() {             
        SimpleDateFormat timestampDateFormat;
                
        timestampDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss");
        timestampDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
                
        return timestampDateFormat.format(timestamp)  + "Z";       
    }
        
    /**
     * Returns the timestamp value as a long data type.
     * 
     * @return 
     */
    public long getTimestampValue() {
        return this.timestamp;
    }
    
    
    /**
     * Increments the pull count, the number of times this coordinate has been
     * pulled from a NodeMap.
     */
    public void incrementPullCount() {
        this.pullCount++;
    }
    
    /**
     * Returns if this coordinate is east of another.
     * 
     * @param c
     * @return
     */
    public boolean isEastOf(Coordinate c) {
        boolean eastOf = false;
        float   diff;
        
        if (longitude >= c.getLongitude())
            eastOf = true;

        if (longitude > 0 && c.getLongitude() < 0) {
            diff = (float) ((180 - longitude) + Math.abs(-180 - c.getLongitude()));
            
            if (diff < 90) {
                eastOf = true;
            }
        }
            
        
        return eastOf;
    }

    /**
     * Returns whether or not a given Latitude is valid.
     * 
     * @param latitude as a String.
     * @return 
     */
    public static boolean isLatitudeValid(String latitude) {
        boolean valid = false;

        try {
            valid = isLatitudeValid(Float.parseFloat(latitude));            
        } catch (Exception e) {

        }

        return valid;
    }

    /**
     * Returns whether or not a given Latitude is valid.
     * 
     * @param latitude as a float
     * @return 
     */
    public static boolean isLatitudeValid(double latitude) {
        boolean valid = false;

        if ((latitude <= 90) && (latitude >= -90))
            valid = true;

        return valid;
    }

    /**
     * Returns whether or not a given Longitude is valid.
     * 
     * @param Longitude as a String
     * @return 
     */
    public static boolean isLongitudeValid(String Longitude) {
        boolean valid = false;

        try {
            valid = isLongitudeValid(Float.parseFloat(Longitude));
        } catch (Exception e) {

        }

        return valid;
    }

    /**
     * Returns whether or not a given Longitude is valid.
     * 
     * @param longitude as a float
     * @return 
     */
    public static boolean isLongitudeValid(double longitude) {
        boolean valid = false;

        if ((longitude <= 180) && (longitude >= -180))
            valid = true;

        return valid;
    }

    /**
     * Returns if this coordinate is north of another.
     * 
     * @param c
     * @return
     */
    public boolean isNorthOf(Coordinate c) {
        boolean northOf = false;

        if (latitude >= c.getLatitude()) {
            northOf = true;
        } 
        
        return northOf;
    }

    /** 
     * Returns if this coordinate is shared between objects.
     * Sharing is detected by the pull count.  The pull count is the number of
     * times an object has been pulled from the NodeMap.
     * 
     * @return 
     */
    public boolean isShared() {
        if (pullCount > 1) {
            return true;
        } else {
            if (parentObjects != null) {
                if (parentObjects.size() > 1) {
                    return true;
                } else {
                    return false;
                }
            } else {            
                return false;
            }
        }
    }

    /**
     * Returns if this coordinate is South of another.
     *
     * @param c
     * @return
     */
    public boolean isSouthOf(Coordinate c) {
        boolean southOf = false;

        if (latitude <= c.getLatitude()) {
            southOf = true;
        }

        return southOf;
    }

    /**
     * Returns if this coordinate is west of another.
     * 
     * @param c The coordinate to compare.
     * @param maxDifference The maximum longitude to detect being west of.
     * @return If this coordinate is west of the given coordinate with the
     *          maximum longitude difference.
     */
    public boolean isWestOf(Coordinate c, float maxDifference) {
        boolean westOf = false;
        float   diff;
        
        if (longitude <= c.getLongitude())
            westOf = true;

        if (longitude > 0 && c.getLongitude() < 0) {
            diff = (float) ((180 - longitude) + Math.abs(180 + c.getLongitude()));
            
            if (diff < maxDifference) {
                westOf = true;
            }
        }
        
        return westOf;
    }

    /**
     * Returns a new Coordinate the given Distance in KM and an Azimuth in Decimal degrees.
     * 
     * @param distance  Distance in KM.
     * @param azimuth   Degree in decimal 0-360.
     * @return 
     */
    public final Coordinate reckonCoordinate(double distance, double azimuth) {
        float  earthRadius = 6371.0f; //In KM
        double latitude1, longitude1, latitude2, longitude2;

        //convert input to radians
        azimuth    = Math.toRadians(azimuth);
        distance   = distance / earthRadius;
        latitude1  = getLatitudeInRadians();
        longitude1 = getLongitudeInRadians();

        // Taken from "Map Projections - A Working Manual", page 31, equation 5-5 and 5-6.
        latitude2 = (float) (Math.asin(Math.sin(latitude1) * Math.cos(distance) + Math.cos(latitude1) * Math.sin(distance) * Math.cos(azimuth)));

        longitude2 = (float) (longitude1 + Math.atan2(
                        Math.sin(distance)  * Math.sin(azimuth),
                        Math.cos(latitude1) * Math.cos(distance) - Math.sin(latitude1) * 
                        Math.sin(distance)  * Math.cos(azimuth)));

        //convert back to degrees
        latitude2  = (float) (DEC_CONVERSION * latitude2);
        longitude2 = (float) (DEC_CONVERSION * longitude2);

        return new Coordinate(altitude, latitude2, longitude2, false);
    }

    /**
     * Removes a MapObject as a Parent object of this Coordinate.
     * 
     * @param  parent
     * @return True if the object was removed, false if it was not found.
     */
    public final boolean removeParent(MapObject parent) {
        if (parentObjects != null) {
            return parentObjects.remove(parent);
        } else {
            return false;
        }
    }
    
    /**
     * Sets this Coordinates Altitude
     * 
     * @param newAltitude 
     */
    public final void setAltitude(float newAltitude) {
        altitude = newAltitude;
    }

    /**
     * Sets the center on screen point of this Coordinate.
     * 
     * @param x
     * @param y
     */
    public final void setCenterPoint(float x, float y) {
        if (this.centerPoint != null) {
            this.centerPoint.setLocation(x, y);
        } else {
            this.centerPoint = new Point2D.Float(x, y);
        }
    }
    
    /**
     * Sets the left on screen point of this Coordinate.
     * 
     * @param x
     * @param y
     */
    public final void setLeftPoint(float x, float y) {
        if (this.leftPoint != null) {
            this.leftPoint.setLocation(x, y);
        } else {
            this.leftPoint = new Point2D.Float(x, y);
        }        
    }
    
    /**
     * Sets the right on screen point of this Coordinate.
     * 
     * @param x
     * @param y
     */
    public final void setRightPoint(float x, float y) {
        if (this.rightPoint != null) {
            this.rightPoint.setLocation(x, y);
        } else {
            this.rightPoint = new Point2D.Float(x, y);
        }        
    }    
    
    /**
     * Sets the Id of this coordinate.  The Id is a reference that can be used
     * to identify a coordinate by a single long value instead of lon, lat, alt.
     * 
     * @param id 
     */
    public final void setId(long id) {
        this.id = id;
    }
    
    /**
     * Sets this Coordinates Latitude
     * 
     * @param newLatitude
     * @throws NumericValueOutOfRangeException 
     */
    public final void setLatitude(double newLatitude) throws NumericValueOutOfRangeException {
        if (((newLatitude <= 90) && (newLatitude >= -90)) || newLatitude == -999) {
            this.latitude = (float) newLatitude;
        } else {
            throw (new NumericValueOutOfRangeException(newLatitude));
        }
    }

    /**
     * Sets this Coordinates Longitude
     * 
     * @param newLongitude
     * @throws NumericValueOutOfRangeException 
     */
    public final void setLongitude(double newLongitude) throws NumericValueOutOfRangeException {
        //if (((newLongitude <= 180) && (newLongitude >= -180)) || newLongitude == -999) {
            this.longitude = (float) newLongitude;
        //} else {
        //    throw (new NumericValueOutOfRangeException(newLongitude));
        //}
    }

    /** 
     * Sets the number of times this Coordinate has been pulled from a NodeMap.
     * 
     * @param count 
     */
    public void setPullCount(byte count) {
        this.pullCount = count;
    }

    public void setShared(boolean shared) {
        if (shared) {
            if (pullCount > 1) {
                pullCount++;
            } else {
                pullCount = 2;
            }
        } else {
            pullCount = 1;
        }
    }
    
    /**
     * Sets this Coordinates TimeStamp
     * 
     * @param newTimestamp as a String in the format: yyyy-MM-dd'T'HH:mm:ssZ
     */
    public final void setTimestamp(String newTimestamp) {
        GregorianCalendar   calendar;
        int                 dateEndIndex;
        int                 year, month,  day;
        int                 hour, minute, second;
        String              date, time, timeZone;

        try {
            timestamp    = 0;
            dateEndIndex = newTimestamp.indexOf("T");
            date         = newTimestamp.substring(0, dateEndIndex);
            time         = newTimestamp.substring(dateEndIndex + 1, newTimestamp.length() - 1);

            year         = Integer.parseInt(date.substring(0,4));
            month        = Integer.parseInt(date.substring(5,7));
            day          = Integer.parseInt(date.substring(8,10));

            hour         = Integer.parseInt(time.substring(0,2));
            minute       = Integer.parseInt(time.substring(3,5));
            second       = Integer.parseInt(time.substring(6,8));
            timeZone     = newTimestamp.substring(newTimestamp.length() - 1);
                    
            /* there is a problem somewhere that causes the month to increment by one,
             * this code combats it until the reson can be found */
                if (month > 1) {
                    month--;
                } else {
                    month = 12;
                    year--;
                }
            //end fix code            
            
            calendar = new GregorianCalendar();
            
            calendar.setTimeZone(new SimpleTimeZone(0, timeZone));
            calendar.set(year, month, day, hour, minute, second);
           
            timestamp = calendar.getTimeInMillis();
        } catch (Exception e) {
            System.out.println("Error in Coordinate.setTimestamp - " + e);
        }
    }

    /**
     * Returns this Coordinate as a String in the format longitude,latitude,altitude
     * @return 
     */
    @Override
    public String toString() {
        String lon, lat, alt, out;
        
        lon = Float.toString(longitude);
        lat = Float.toString(latitude);
        alt = Float.toString(altitude);
        
        if (lon.endsWith(".0")) 
            lon = lon.substring(0, lon.length() - 2);        
        
        if (lat.endsWith(".0")) 
            lat = lat.substring(0, lat.length() - 2);              
        
        if (alt.endsWith(".0")) 
            alt = alt.substring(0, alt.length() - 2);             
        
        if (this.timestamp > 0) {
            out = lon + "," +lat + "," + alt + "," + getTimestamp();
        } else {
            out = lon + "," +lat + "," + alt;
        }
        
        return out;
    }
    
    /**
     * Updates this coordinate with new values.
     * 
     * @param altitude
     * @param latitude
     * @param longitude 
     */
    public void update(float altitude, double latitude, double longitude, long timestamp) {
        this.setLongitude((float) longitude);
        this.setLatitude((float) latitude);
        this.setAltitude(altitude);
        this.timestamp = timestamp;
    }    
}
