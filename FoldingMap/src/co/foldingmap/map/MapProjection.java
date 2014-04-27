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
package co.foldingmap.map;

import co.foldingmap.map.vector.Coordinate;

/**
 *
 * @author Alec
 */
public abstract class MapProjection {
    public static final float EARTH_RADIUS =  6378.1f;
    
    protected boolean  displayLeft, displayRight; //used when the map is zoomed out that an object needs te be drawn multiple times.
    protected float    displayHeight, displayWidth;
    protected double   referenceLatitude, referenceLongitude;
    protected float    zoomLevel;    
    
    public abstract double getLatitude(double x, double y);
    public abstract double getLongitude(double x, double y);
    public abstract double getX(Coordinate c);
    public abstract double getX(double lat, double lon);
    public abstract double getY(Coordinate c);
    public abstract double getY(double lat, double lon);
    public abstract String getViewInfo();
    public abstract void   setZoomLevel(float zoomLevel);
    public abstract void   shiftMapReference(double x, double y);
    public abstract void   zoomIn(double x, double y);
    public abstract void   zoomOut(double x, double y);
            
    /*
     * Returns the display height of this projection.
     */
    public float getDisplayHeight() {
        return displayHeight;
    }

    /**
     * Return the display width of this projection.
     * @return 
     */
    public float getDisplayWidth() {
        return displayWidth;
    }   
    
    /**
     * Returns the radius of the planet being used in this projection.
     * 
     * @return 
     */
    public float getPlanetRadius() {
        return EARTH_RADIUS;
    }
    
    /**
     * Returns the Reference Latitude for this projection.
     * This reference is usually where the point (0,0) is.
     * 
     * @return 
     */
    public double getReferenceLatitude() {
        return referenceLatitude;
    }

    /**
     * Returns the Reference Longitude for this projection.
     * This reference is usually where the point (0,0) is.
     * 
     * @return 
     */
    public double getReferenceLongitude() {
        return referenceLongitude;
    }      
    
    /**
     * Returns the Projections current Zoom Level.
     * 
     * @return 
     */
    public float getZoomLevel() {
        return zoomLevel;
    }       
    
    /**
     * Returns if objects are to be drawn a second time on the left side 
     * of the map.
     * 
     * @return 
     */
    public boolean isLeftShown() {
        return displayLeft;
    }
    
    /**
     * Returns if objects are to be drawn a second time on the right side of 
     * the map.
     * 
     * @return 
     */
    public boolean isRightShown() {
        return displayRight;
    }    
    
    public void setDisplaySize(float displayHeight, float displayWidth) {
        this.displayHeight = displayHeight;
        this.displayWidth  = displayWidth;
    }      
    
    /**
     * Sets the map reference, the coordinate in the upper left corner of the screen.
     * 
     * @param c 
     */
    public void setReference(Coordinate c) {
        double x;
        
        this.referenceLatitude     = c.getLatitude();
        this.referenceLongitude    = c.getLongitude();
        
        x = getX(0,-180);
        
        if (x >= 0 && x <= displayWidth) {
            displayLeft = true;
        } else {
            displayLeft = false;
        }
            
        if (x >= 0 && x <= displayWidth) {
            displayRight = true;
        } else {
            displayRight = false;
        }
    }  
    
    /**
     * Sets the map reference, the lat and long in the upper left corner of the screen.
     * 
     * @param latitude
     * @param longitude 
     */
    public void setReference(double latitude, double longitude) {
        double x;
        
        this.referenceLatitude     = latitude;
        this.referenceLongitude    = longitude;
        
        x = getX(0,-180);
        
        if (x >= 0 && x <= displayWidth) {
            displayLeft = true;
        } else {
            displayLeft = false;
        }
            
        if (x >= 0 && x <= displayWidth) {
            displayRight = true;
        } else {
            displayRight = false;
        }
    }     
}
