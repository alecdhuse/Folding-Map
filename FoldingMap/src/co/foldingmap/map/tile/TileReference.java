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
package co.foldingmap.map.tile;

import co.foldingmap.map.vector.Coordinate;

/**
 * A convenience class for keeping an x, y, zoom triple information.
 * 
 * @author Alec
 */
public class TileReference {
    private int x, y, zoom;
    
    public TileReference(int x, int y, int zoom) {
        this.x    = x;
        this.y    = y;
        this.zoom = zoom;
    }
    
    /**
     * Creates a Coordinate Object to represent the north-west corner of this tile.
     * 
     * @return 
     */
    public Coordinate getCoordinate() {
        Coordinate upperLeft;
        
        upperLeft = new Coordinate(0, getLatitude(), getLongitude());
        
        return upperLeft;
    }    
    
    /**
     * Returns the Latitude for the northern boundary of the tile.
     * 
     * @return 
     */
    public float getLatitude() {
        double latDeg, latRad, n;
        
        n = Math.pow(2, zoom);
        latRad = Math.atan(Math.sinh(Math.PI * (1 - 2 * y / n)));
        latDeg = latRad * 180.0 / Math.PI;   
        
        return (float) latDeg;
    }
        
    /**
     * Returns the Longitude for the western boundary of the tile.
     * 
     * @return 
     */
    public float getLongitude() {
        double lonDeg, n;
        
        n = Math.pow(2, zoom);
        lonDeg = x / n * 360.0 - 180.0;
        
        return (float) lonDeg;
    }
    
    /**
     * Returns the tile reference for the given offsets.
     * 
     * @return 
     */
    public TileReference getTileOffset(int xOffset, int yOffset) {
        int newX, newY;
        
        newX = x + xOffset;
        newY = y + yOffset;
        
        // If x is a negative number wrap
        if (newX < 0) newX  = (int) (Math.pow(2, zoom) - 1);
        
        //If y is negative, set it to 0
        if (newY < 0) newY = 0;
        
        return new TileReference(newX, newY, zoom);
    }   
    
    /**
     * Returns the TileReference that corresponds to the Latitude and Longitude
     * given.
     * 
     * @param latitude
     * @param longitude
     * @param zoomLevel Tile Zoom Level
     * @return 
     */
    public static TileReference getTileReference(double latitude, double longitude, int zoomLevel) {
        int             n, xTile, yTile;
        double          latRadians;
        TileReference   tileRef;
        
        latRadians = Coordinate.getLatitudeInRadians(latitude);
        
        n     = (int) Math.pow(2, zoomLevel);
        xTile = (int) (((longitude + 180.0) / 360.0) * n);    
        
        if (latitude >= 85.0511f) {
            yTile = 0;
        } else {
            yTile = (int) Math.abs(Math.floor( (1 - Math.log(Math.tan(latRadians) + 1 / Math.cos(latRadians)) / Math.PI) / 2 * (1<<zoomLevel) ) );
        }
        
        tileRef = new TileReference(xTile, yTile, zoomLevel);
        
        return tileRef;
    }    
        
    public int getX() {
        return this.x;        
    }
    
    public int getY() {
        return this.y;
    }
    
    /**
     * Returns the zoom value of this TileReference.
     * 
     * @return 
     */
    public int getZoom() {
        return this.zoom;
    }
    
    /**
     * Increments the X value of this Tile Reference.
     */
    public void incrementX() {
        //Check to see if incrementing X would put it beyond the number of X tiles.
        if ((x + 1) < Math.pow(2, zoom)) {
            this.x++;
        }
    }    
    
    /**
     * Increments the Y value of this Tile Reference.
     */
    public void incrementY() {
        //Check to see if incrementing Y would put it beyond the number of y tiles.
        if ((y + 1) < Math.pow(2, zoom)) {
            this.y++;
        }
    }
    
    @Override
    public String toString() {
        return Integer.toString(zoom) + "," + Integer.toString(x) + "," + Integer.toString(y);
    }
}
