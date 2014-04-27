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

import co.foldingmap.Logger;
import co.foldingmap.map.tile.TileMath;
import co.foldingmap.map.vector.Coordinate;

/**
 * Enables maps to be rendered using the MercatorProjection.
 * Reference: http://en.wikipedia.org/wiki/Mercator_projection
 * 
 * @author Alec
 */
public class MercatorProjection extends MapProjection {
    
    /**
     * Constructor with default options.
     */
    public MercatorProjection() {
        this.referenceLatitude     = 83;
        this.referenceLongitude    = -180;      
        this.zoomLevel             = 0.022f;
    }

    /**
     * Constructor with default options.
     * 
     * @param refLat
     * @param refLon
     * @param zoom
     */
    public MercatorProjection(double refLat, double refLon, float zoom) {
        if (refLat <= 90 && refLat >= -90) {
            this.referenceLatitude = refLat;
        } else {
            this.referenceLatitude = 85;
            Logger.log(Logger.ERR, "Error in MercatorProjection Constructor Latitude out of range: " + refLat);
        }
        
        if (refLon <= 180 && refLon >= -180) {
            this.referenceLongitude = refLon;      
        } else {
            this.referenceLongitude = -180;
            Logger.log(Logger.ERR, "Error in MercatorProjection Constructor Longitude out of range: " + refLon);            
        }
        
        if (getX(new Coordinate(0,0,-180)) >= 0) {
            displayLeft = true;
        } else {
            displayLeft = false;
        }
            
        if (getX(new Coordinate(0,0,-180)) <= displayWidth) {
            displayRight = true;
        } else {
            displayRight = false;
        }        
        
        this.zoomLevel = zoom;
    }    
    
    /**
     * Returns the Latitude for a given x,y on the screen.
     * Adjusts for reference.
     * 
     * @param x
     * @param y
     * @return 
     */
    @Override
    public double getLatitude(double x, double y) {        
        double  yRef     = getY(referenceLatitude);
        double  yAdjust  = ((y / zoomLevel));
        double  yCorrect = yAdjust - yRef;
        double  latitude   = getLatitude(yCorrect) * -1;

        return latitude;
    }

    /**
     * Gets a latitude from a y value.
     * Does not adjust for reference.
     * 
     * @param y
     * @return 
     */
    private double getLatitude(double y) {
        double latitude;

        double p1  = Math.pow(Math.E, (y / EARTH_RADIUS));        
        double p2  = (2 * Math.atan(p1));    
        double lat = (p2 - (Math.PI / 2));
        
        latitude = Coordinate.getLatitudeInDecimal(lat);
        
        return latitude;
    }      
    
    /**
     * Gets a Longitude for a given x,y on the screen.
     * Adjusts for reference.
     * 
     * @param x
     * @param y
     * @return 
     */
    @Override
    public double getLongitude(double x, double y) {
        double  xCorrect;
        double  longitude, lon, ref;

        xCorrect = x / zoomLevel;
        
        ref = Coordinate.getLongitudeInRadians(referenceLongitude);
        lon = (xCorrect / EARTH_RADIUS) + ref;
        
        longitude = Coordinate.getLongitudeInDecimal(lon);
        
        return longitude;
    }

    /**
     * Returns a string containing the view info in the form:
     * longitude,latitude,zoom
     * 
     * @return 
     */
    @Override
    public String getViewInfo() {
        String x, y, z;
        
        x = Double.toString(getReferenceLongitude());
        y = Double.toString(MapUtilities.normalizeLongitude(getReferenceLatitude()));
        z = Double.toString(TileMath.getTileMapZoom(zoomLevel));                
        
        return x + "," + y + "," + z;
    }    
    
    /**
     * Returns the x position on the screen that corresponds to the given 
     * longitude.  
     * 
     * @param  c
     * @return The x coordinate on the screen the longitude corresponds to.
     */
    @Override
    public final double getX(Coordinate c) {
        double x, ref, lon, mod;

        ref = Coordinate.getLongitudeInRadians(referenceLongitude);
        lon = c.getLongitudeInRadians();
        mod = (lon - ref);
        x   = mod * EARTH_RADIUS;

        return x * zoomLevel;     
    }

    /**
     * Returns the x position on the screen that corresponds to the given 
     * longitude.  
     * 
     * @param  latitude
     * @param  longitude 
     * @return The x coordinate on the screen the longitude corresponds to.
     */
    @Override
    public final double getX(double latitude, double longitude) {
        double x, ref, lon, mod;

        ref = Coordinate.getLongitudeInRadians(referenceLongitude);
        lon = Coordinate.getLongitudeInRadians(longitude);
        mod = (lon - ref);
        x   = mod * EARTH_RADIUS;

        return x * zoomLevel;     
    }    
    
    /**
     * Returns the y position on the screen that corresponds to the given latitude.
     * Adjusts for reference.
     * 
     * @param  c
     * @return The y position on the screen that corresponds to the given latitude.
     */
    @Override
    public final double getY(Coordinate c) {
        //Adjust max coordinate so map is more uniform. 
        if (c.getLatitude() == -90.0f)
            c.setLatitude(-89.99f);
        
        double yGiv = getY(c.getLatitude());
        double yRef = getY(referenceLatitude);
        double y    = (yRef - yGiv);                

        return (y) * zoomLevel;
    }

    /**
     * Returns the y position on the screen that corresponds to the given latitude.
     * Does not adjusts for reference.
     * 
     * @param latitude
     * @return 
     */
    private float getY(double latitude) {
        double lat = Coordinate.getLatitudeInRadians(latitude);        
        double tan = (float) Math.tan((Math.PI / 4.0f) + (lat / 2.0f));
        double log = (float) Math.log(tan);
        double y   = log * EARTH_RADIUS;
        
        return (float) y;        
    }    
    
    /**
     * Returns the y position on the screen that corresponds to the given latitude.
     * Adjusts for reference.
     * 
     * @param  latitude
     * @param  longitude 
     * @return The y position on the screen that corresponds to the given latitude.
     */
    @Override
    public final double getY(double latitude, double longitude) {
        //Adjust max coordinate so map is more uniform. 
        if (latitude == -90.0f)
            latitude = (-89.99f);
        
        double yGiv = getY(latitude);
        double yRef = getY(referenceLatitude);
        double y    = (yRef - yGiv);                

        return (y) * zoomLevel;
    }    
    
    /**
     * Sets the center of the viewable projection to a given screen coordinate.
     * 
     * @param x
     * @param y 
     */
    private void setCenter(float x, float y) {
        double xLon, yLat;
        double centerLon, centerLat;
        double difLon, difLat;
        
        xLon = getLongitude(x, y);
        yLat = getLatitude(x, y);
        
        centerLon = getLongitude(displayHeight / 2.0f, displayWidth / 2.0f);
        centerLat = getLatitude(displayHeight / 2.0f, displayWidth / 2.0f);   
        
        difLon = (xLon - centerLon) * 0.15f;
        difLat = (yLat - centerLat) * 0.15f;
        
        referenceLatitude  += difLat;
        referenceLongitude += difLon;
    }    
    
    /**
     * Sets the zoom level to be used in this projection.
     * 
     * @param zoomLevel 
     */
    @Override
    public void setZoomLevel(float zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    /**
     * Shifts the map reference of the projection.
     * This is used to construct the view port.
     * 
     * @param x
     * @param y 
     */
    @Override
    public void shiftMapReference(double x, double y) {
        double  idlX, newLatitude, newLongitude;

        x *= -1;
        y *= -1;
        
        newLongitude = getLongitude(x, y);
        newLatitude  = getLatitude(x, y);

        if (newLatitude > 90)
            newLatitude = 90;
        
        if (newLatitude < -90)
            newLatitude = -90;
        
        referenceLatitude  = newLatitude;
        referenceLongitude = newLongitude;
        
        idlX = getX(new Coordinate(0,0,-180));        
        
        if (idlX >= 0 && idlX <= displayWidth) {
            displayLeft = true;
        } else {
            displayLeft = false;
        }
        
        if (getLongitude(displayWidth, 0) >= 180) {
        //if (idlX >= 0 && idlX <= displayWidth) {
            displayRight = true;
        } else {
            displayRight = false;
        }        
    }

    /**
     * Zoom in the view port.
     * 
     * @param x
     * @param y 
     */
    @Override
    public void zoomIn(double x, double y) {
        float tileZoom = TileMath.getTileMapZoom(zoomLevel) + 0.5f;
        float newZoom  = TileMath.getVectorMapZoom(tileZoom);
        float scale    = newZoom / zoomLevel;
        
        zoomLevel     = newZoom;
        float centerX = getDisplayWidth() / 2.0f;
        float centerY = getDisplayHeight() / 2.0f;        
        float deltaX  = centerX - (centerX * scale);   
        float deltaY  = centerY - (centerY * scale);   
        shiftMapReference(deltaX, deltaY);         
    }

    /**
     * Zoom out the view port.
     * 
     * @param x
     * @param y 
     */
    @Override
    public void zoomOut(double x, double y) {
        float tileZoom = TileMath.getTileMapZoom(zoomLevel) - 0.5f;
        float newZoom  = TileMath.getVectorMapZoom(tileZoom);
        float scale    = newZoom / zoomLevel;
        
        zoomLevel     = newZoom;
        float centerX = getDisplayWidth() / 2.0f;
        float centerY = getDisplayHeight() / 2.0f;        
        float deltaX  = centerX - (centerX * scale);   
        float deltaY  = centerY - (centerY * scale);   
        shiftMapReference(deltaX, deltaY); 
    }
}
