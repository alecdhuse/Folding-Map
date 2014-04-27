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
import co.foldingmap.map.labeling.LabelManager;
import co.foldingmap.map.themes.MapTheme;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.LatLonBox;
import co.foldingmap.map.vector.NodeMap;
import java.util.ArrayList;

/**
 * Bundles various aspects of how the map is viewed into one class
 * 
 * @author Alec
 */
public class MapView {
    //Values for Map wraping for Longitude < -180 and > 180.
    public static final int   NO_WRAP      =  0;
    public static final int   WRAP_LEFT    = -1;    
    public static final int   WRAP_RIGHT   =  1;
    
    private ArrayList<Coordinate>   viewPortCoordinates;
    private boolean                 displayAll;  //Insure that the whole map is drawn, does not affect labeling    
    private boolean                 dragging, showPointsInMapObject;  
    private float                   mouseLatitude, mouseLongitude;
    private LabelManager            labelManager;
    private MapProjection           projection;
    private MapTheme                mapTheme;    
    private NodeMap                 nodeMap;
    
    /**
     * Constructor for objects of class MapView.
     * Uses the MercatorProjection as the default Projection.
     */
    public MapView() {    
        this.displayAll            = false;
        this.dragging              = false;
        this.showPointsInMapObject = false;
        this.projection            = new MercatorProjection();
        this.labelManager          = new LabelManager();        
    }
    
    public MapView(MapProjection projection) { 
        this.displayAll            = false;
        this.dragging              = false;       
        this.labelManager          = new LabelManager();  
        this.projection            = projection;
        this.showPointsInMapObject = false;
    }
    
    /**
     * Returns if the Map should show object component points.
     * 
     * @return 
     */
    public boolean arePointsShown() {
        return showPointsInMapObject;
    } 
    
    /**
     * Creates a copy of this MapView.
     * 
     * @return 
     */
    @Override
    public MapView clone() {
        MapView mapViewCopy = new MapView(projection);
        
        mapViewCopy.setShowPoints(showPointsInMapObject);
        mapViewCopy.setLabelManager(this.labelManager);
        mapViewCopy.setMapTheme(this.mapTheme);
        
        return mapViewCopy;
    }    
    
    /**
     * Returns if all the MapObjects are to be drawn.  Normal drawing only 
     * draws objects within the View Port.  But when outputting images this 
     * may need to be true.
     * 
     * @return 
     */
    public boolean displayAll() {
        return displayAll;
    }    
    
    /**
     * Returns the Height of the View Port.
     * 
     * @return 
     */
    public float getDisplayHeight() {
        return projection.getDisplayHeight();
    }  
    
    /**
     * Returns the Width of the View Port.
     * 
     * @return 
     */
    public float getDisplayWidth() {
        return projection.getDisplayWidth();
    }   
    
    /**
     * Returns the Maps Label Manager.
     * 
     * @return 
     */
    public LabelManager getLabelManager() {
        return this.labelManager;
    }    
    
    /**
     * Returns a Coordinate of the last click location of the mouse.
     * 
     * @return 
     */
    public Coordinate getLastMouseClickCoordinate() {
        return new Coordinate(0, mouseLatitude, mouseLongitude);
    }
        
    /**
     * Translates a screen point (x, y) to a Latitude.
     * 
     * @param x
     * @param y
     * @return 
     */
    public float getLatitude(float x, float y) {
        return (float) projection.getLatitude(x, y);
    }

    /**
     * Translates a screen point (x, y) to a Longitude.
     * 
     * @param x
     * @param y
     * @return 
     */
    public float getLongitude(float x, float y) {
        return (float) projection.getLongitude(x, y);
    }    
         
    /**
     * Returns the Projection being used by the Map.
     * 
     * @return 
     */
    public MapProjection getMapProjection() {
        return projection;
    }    
    
    /**
     * Returns the Map's Theme
     * 
     * @return 
     */
    public MapTheme getMapTheme() {
        return mapTheme;
    }       
    
    /**
     * Returns the View Port dimensions as a LatLonAltBox.
     * If any problems occur a view bounds for the while globe is returned.
     * 
     * @return 
     */
    public LatLonBox getViewBounds() {
        float        north, south, east, west;
        LatLonBox    viewBounds;

        try {
            north = this.getLatitude(0, 0);
            south = this.getLatitude(0, this.getDisplayHeight());
            east  = this.getLongitude(this.getDisplayWidth(), 0);
            west  = this.getLongitude(0, 0);

            while (east > 180)
                east = -180 + ((180 - east) * -1);        

            while (west < -180)
                west = 180 - Math.abs(west + 180);                

            viewBounds = new LatLonBox(north, south, east, west);

            return viewBounds;
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in MapView.getViewBounds() - " + e);
            return new LatLonBox(90, -90, 180, -180);
        }
    }   
    
    /**
     * Returns all coordinates within this view port.
     * 
     * @return 
     */
    public ArrayList<Coordinate> getViewPortCoordinates() {
        return viewPortCoordinates;
    }
    
    /**
     * Translates a Coordinate (Altitude, Latitude, Longitude) to a 
     * screen x value.
     * 
     * @param c
     * @param wrap
     * @return 
     */
    public float getX(Coordinate c, int wrap) {   
        Coordinate m1, m2;  //Meridians 
        double     idl, x;
        double     r, d, n;
        
        m1 = new Coordinate(0, 0, -180);
        m2 = new Coordinate(0, 0,  180);
        
        //non-modified value
        x = projection.getX(c);
                        
        switch (wrap) {
            case NO_WRAP:

                break;
            
            case WRAP_LEFT:
                idl = projection.getX(m1);
                r   = projection.getX(m2);
                d   = x - r;
                n   = d + idl;                    
                x   = n;
                
                break;
            
            case WRAP_RIGHT:
                idl = projection.getX(m1);
                r   = projection.getX(m2);                
                d   = x - idl;
                n   = d + r;
                x   = n;
                
                break;
        }
                
        return (float) (x);
    }      
    
    /**
     * Translates a Coordinate (Altitude, Latitude, Longitude) to a 
     * screen y value.
     * 
     * @param c
     * @return 
     */
    public float getY(Coordinate c) {
        return (float) projection.getY(c);
    } 
    
    /**
     * Returns the current zoom level being used to display the map.
     * Values are dependant on the projection being used.
     * 
     * @return 
     */
    public float getZoomLevel() {
        return projection.getZoomLevel();
    }      
    
    /**
     * Returns if the map is being dragged.  This is to allow different levels
     * of rendering quality or object displaying while dragging the map.
     * 
     * @return 
     */
    public boolean isDragging() {
        return dragging;
    }         
    
    /**
     * Shifts the map reference of the projection.
     * This is used to construct the view port.
     * 
     * @param x
     * @param y 
     */    
    public void shiftMapReference(double x, double y) {
        this.projection.shiftMapReference(x, y);
        this.update();
    }
    
    /**
     * Set if all objects are to be drawn, not just those within the View Port.
     * 
     * @param displayAll 
     */
    public void setDisplayAll(boolean displayAll) {
        this.displayAll = displayAll;
    }
           
    /**
     * Set if the map is being dragged.
     * 
     * @param dragging 
     */
    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }   
    
    /**
     * Sets the LabelManager to be used when rendering this map.
     * 
     * @param labelManager 
     */
    public void setLabelManager(LabelManager labelManager) {
        this.labelManager = labelManager;
    }
    
    /**
     * Set the location on the map and screen of the last mouse click.
     * 
     * @param mouseX
     * @param mouseY 
     */
    public void setLastMouseClickPosition(float mouseX, float mouseY) {
        this.mouseLatitude  = getLatitude(mouseX,  mouseY);
        this.mouseLongitude = getLongitude(mouseX, mouseY);
    }    
    
    /**
     * Sets the MapProjection to be used when rendering this map.
     * 
     * @param projection 
     */
    public void setMapProjection(MapProjection projection) {
        this.projection = projection;
    }    
    
    /**
     * Set the theme to use drawing the map.
     * 
     * @param theme 
     */
    public void setMapTheme(MapTheme theme) {
        this.mapTheme = theme;
    }    
        
    /**
     * Sets the Node map for this MapView
     * 
     * @param nodeMap 
     */
    public void setNodeMap(NodeMap nodeMap) {
        this.nodeMap = nodeMap;
    }
    
    
    /**
     * Set if MapObjects component points are to be drawn.
     * 
     * @param showPoints 
     */
    public void setShowPoints(boolean showPoints) {
        this.showPointsInMapObject = showPoints;
    } 
    
    /**
     * Updates the view point coordinates and maybe some other stuff later.
     */
    public void update() {
        viewPortCoordinates = nodeMap.getCoordinatesWithinBoundary(getViewBounds());
    }
}
