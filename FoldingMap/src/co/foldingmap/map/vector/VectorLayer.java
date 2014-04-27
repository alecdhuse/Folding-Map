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

import co.foldingmap.map.Overlay;
import co.foldingmap.map.MapView;
import co.foldingmap.map.MapObjectList;
import co.foldingmap.map.MapObject;
import co.foldingmap.map.MapPanel;
import co.foldingmap.map.Layer;
import co.foldingmap.Logger;
import co.foldingmap.map.raster.ImageOverlay;
import co.foldingmap.xml.XmlOutput;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JMenuItem;

/**
 * A Layer that holds VectorObject, which are vector objects.
 * 
 * @author Alec
 */
public class VectorLayer extends Layer {
    protected ArrayList<Overlay>                overlays;
    protected VectorObjectList<VectorObject>    objects;
    protected MapView                           lastMapView;
    protected SimpleDateFormat                  timestampDateFormat;   
    
    /**
     * Constructor for objects of class VectorLayer
     */
    public VectorLayer() {
        this.layerName = "New Vector Layer";
        this.visible   = true;

        this.objects              = new VectorObjectList<VectorObject>();
        this.layerPropertiesPanel = new VecorLayerPropertiesPanel(this);
    }       
    
    /**
     * Constructor for objects of class VectorLayer
     */
    public VectorLayer(String layerName) {
        this.layerName = layerName;
        this.visible   = true;

        this.objects              = new VectorObjectList<VectorObject>();
        //this.selectedObjects      = new VectorObjectList<MapObject>();
        this.layerPropertiesPanel = new VecorLayerPropertiesPanel(this);
    }    
    
    /**
     * Adds a list of map objects to this layer.
     * 
     * @param objectsToAdd 
     */
    public void addAllObjects(VectorObjectList<VectorObject> objectsToAdd) {    
        objects.addAll(objectsToAdd);

        for (int i = 0; i < objectsToAdd.size(); i++) {
            VectorObject currentMapObject = objectsToAdd.get(i);
            currentMapObject.setParentLayer(this);
            
            if (this.parentMap.getTheme() != null)
                currentMapObject.updateOutlines(this.parentMap.getTheme());
            
            //Updates NodeMap for the map
            for (Coordinate c: currentMapObject.getCoordinateList())
                this.parentMap.addCoordinateNode(c);            
            
            //Check to see if the object has a reference, if not give it one.
            if (currentMapObject.getReference() < 1) {
                if (parentMap != null) {
                    currentMapObject.setReference(parentMap.getNewObjectReference());
                } else {
                    System.err.println("Parent Map for layer: " + layerName + " was not set before adding new objects.  No object references will be set");
                }
            }
        }
    }    
    
    /**
     * Adds a VectorObject to this layer at a specific z-position.
     * 
     * @param obj
     * @param posistion 
     */
    public void addObject(VectorObject obj, int posistion) {
        objects.add(posistion, obj);
        obj.setParentLayer(this);       
        obj.updateOutlines(this.parentMap.getTheme());
        
        //Updates NodeMap for the map
        for (Coordinate c: obj.getCoordinateList())
            this.parentMap.addCoordinateNode(c);

        //Check to see if the object has a reference, if not give it one.
        if (obj.getReference() < 1) {
            if (parentMap != null) {
                obj.setReference(parentMap.getNewObjectReference());
            } else {
                System.err.println("Parent Map for layer: " + layerName + " was not set before adding new objects.  No object references will be set");
            }
        }           
    }    
    
    /**
     * Adds a VectorObject to this Layer.
     * 
     * @param obj 
     */
    public void addObject(VectorObject obj) {
        try {
            objects.add(obj);
            obj.setParentLayer(this);
            
            if (parentMap != null) {
                obj.updateOutlines(this.parentMap.getTheme());      
                
                if (parentMap != null)
                    obj.setReference(parentMap.getNewObjectReference());
            } else {
                Logger.log(Logger.ERR, "Parent Map for layer: " + layerName + " was not set before adding new objects.  No object references will be set");
            } 
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in VectorLayer.addObject(VectorObject) - " + e);
        }
    }    
    
    /**
     * Adds an Overlay to this layer.
     * 
     * @param overlay 
     */
    public void addOverlay(Overlay overlay) {
        if (overlay != null) {
            //see if the arraylist has been initialied or not.
            if (overlays == null)
                overlays = new ArrayList<Overlay>();

            overlays.add(overlay);
            
            if (overlay instanceof ImageOverlay)
                ((ImageOverlay) overlay).setParentLayer(this);
        }
    }
    
    /**
     * Adds one or more Overlays to this layer.
     * 
     * @param overlay 
     */
    public void addOverlays(ArrayList<Overlay> overlays) {
        for (Overlay ol: overlays)
            addOverlay(ol); 
    }    
    
    /**
     * Removes all Overlays from this VectorLayer.
     * 
     */
    public void clearOverlays() {
        if (this.overlays != null)
            this.overlays.clear();
    }
    
    /**
     * Closes this Layer gracefully.
     */
    @Override
    public void closeLayer() {
        objects  = null;
        overlays = null;
    }       
    
    @Override
    public Layer copy() {
        VectorObject                    currentMapObjectCopy;
        VectorObjectList<VectorObject>  objectsCopy;
        VectorLayer                     layerCopy;

        objectsCopy = new VectorObjectList<VectorObject>();

        //create a copy of every object
        for (int i = 0; i < objects.size(); i++) {
            VectorObject currentMapObject = objects.get(i);
            currentMapObjectCopy = (VectorObject) currentMapObject.copy();
            objectsCopy.add(currentMapObjectCopy);
        }

        layerCopy = new VectorLayer(layerName);
        layerCopy.setParentMap(parentMap);
        layerCopy.addAllObjects(objectsCopy);
                
        return layerCopy;
    }

    /**
     * Draws this Layer on the map.
     * 
     * @param g2
     * @param mapView 
     */
    @Override
    public void drawLayer(Graphics2D g2, MapView mapView) {
        LatLonAltBox                      objectBounds, viewBounds;
        LineString                        currentLineString;
        VectorObject                      currentMapObject;
        VectorObjectList<VectorObject>    lineStrings, polygons;
        Polygon                           currentPolygon;
        
        try {
            this.lastMapView = mapView;
            viewBounds       = new LatLonAltBox(mapView.getViewBounds());
            
            if (visible) {
                for (int i = 0; i < objects.size(); i++) {
                    currentMapObject = objects.get(i);   
                    
                    if (currentMapObject instanceof Polygon) {
                        currentMapObject.drawObject(g2, mapView, null);    
                        currentMapObject.drawOutline(g2, mapView);                        
                    } else if (currentMapObject instanceof LineString) {
                        currentMapObject.drawOutline(g2, mapView);
                    }
                }
                
                //draw all the other objects
                for (int i = 0; i < objects.size(); i++) {
                    currentMapObject = objects.get(i);     
                    
                    if (currentMapObject != null) {
                        objectBounds  = currentMapObject.getBoundingBox();

                        float latEast = mapView.getLongitude(mapView.getDisplayWidth(), 0);
                        float diff    = Math.abs(mapView.getLongitude(0, 0)) + latEast;

                        if (mapView.displayAll()) {
                            currentMapObject.drawObject(g2, mapView, null);

                            if (currentMapObject instanceof Polygon) {
                                ((Polygon) currentMapObject).drawOutline(g2, mapView);   
                            } else if (currentMapObject instanceof LineString) {

                            }                        
                        } else {
                            if (viewBounds.overlaps(objectBounds) || diff >= 90) {                         
                                   
                                    
                                if (currentMapObject instanceof Polygon) {
//                                    if (!mapView.isDragging() && !currentMapObject.isHighlighted()) {                                
//                                        currentMapObject.drawOutline(g2, mapView);
//                                    }
                                } else {
                                    currentMapObject.drawObject(g2, mapView, null); 
                                }
                            }
                        }
                    } else { //end null check
                        //clean up nulls
                        objects.remove(i);
                    }
                }                                                     
                
                //draw overlays
                if (overlays != null) {
                    for (Overlay overlay: overlays) {
                        overlay.drawObject(g2, mapView);
                    }
                }                
                    
            }// if visible
        } catch (Exception e) {
            System.err.println("Error in VectorLayer.drawLayer(Graphics2D, MapView) - " + e);
        }
    }

//    /**
//     * Searches in all objects to see if a coordinate with the same 
//     * lat, long and alt exists.  If it does it is returned.
//     * 
//     * @param coordinateToFind
//     * @return 
//     */
//    public Coordinate findCoordinate(Coordinate coordinateToFind) {
//        Coordinate   returnCoordinate = null;
//        LatLonAltBox currentObjectBounds;
//        VectorObject    currentMapObject;
//        
//        for (int i = 0; i < objects.size(); i++) {
//            currentMapObject    = objects.get(i);
//            currentObjectBounds = currentMapObject.getBoundingBox();
//            
//            if (currentObjectBounds.contains(coordinateToFind)) {
//                if (currentMapObject.getCoordinateList().contains(coordinateToFind))
//                    returnCoordinate = coordinateToFind;
//
//                //save cpu exit loop after coordinate has been found.
//                if (returnCoordinate != null)
//                    break;
//            }
//        }
//
//        return returnCoordinate;
//    }    
    
    /**
     * Returns all the Coordinates in MapObjects in this Layer.
     * @return 
     */
    public CoordinateList<Coordinate> getAllLayerCoordinates() {
        CoordinateList<Coordinate>  allCoordinates = new CoordinateList<Coordinate>();

        try {
            for (int i = 0; i < objects.size(); i++) {
                VectorObject currentMapObject = objects.get(i);
                allCoordinates.addAll(currentMapObject.getCoordinateList());
            }
        } catch (Exception e) {
            System.err.println("Error VectorLayer.getAllMapCoordinates() " + e);
        }

        return allCoordinates;
    }    
    
    /**
     * Returns all Objects within a given range.
     * 
     * @param range
     * @return 
     */
    public VectorObjectList<VectorObject> getAllObjectsWithinRange(LatLonAltBox range) {
        return objects.getAllWithinRange(range);
    }      
    
//    /**
//     * Gets the regions for all objects in this layer.
//     * Used mainly in exporting
//     *
//     * @return ArrayList<Region>    All Regions used in this layer
//     */
//    public ArrayList<Region> getAllRegions() {
//        ArrayList<Region>   regions;
//
//        regions = new ArrayList<Region>();
//
//        for (int i = 0; i < objects.size(); i++) {
//            VectorObject currentObject = objects.get(i);
//
//            if (currentObject.getRegion() != null)
//                regions.add(currentObject.getRegion());
//        }
//
//        return regions;
//    }    
    
    /**
     * Returns the boundary that contains all the object in this layer.
     * 
     * @return 
     */
    @Override
    public LatLonAltBox getBoundary() {
        return this.objects.getBoundary();
    }
    
    /**
     * Returns the center Longitude of all object in this Layer.
     * 
     * @return 
     */
    @Override
    public float getCenterLongitude() {
        float center, delta;

        delta  = objects.getEasternMostLongitude() - objects.getWesternMostLongitude();
        center = objects.getWesternMostLongitude() + (float) (delta / 2.0);

        return center;
    }

    /**
     * Gets the center Latitude of all the Objects in this Layer.
     * 
     * @return 
     */
    @Override
    public float getCenterLatitude() {
        float center, delta;

        delta  = objects.getNorthernMostLatitude() - objects.getSouthernMostLatitude();
        center = objects.getSouthernMostLatitude() + (float) (delta / 2.0);
        
        return center;
    }
    
    /**
     * Returns JMenuItems that should be used in the context menu for this Layer
     * 
     * @return 
     */
    @Override
    public JMenuItem[] getContextMenuItems() {        
        return new JMenuItem[0];
    }
    
    /**
     * Returns all the values for a specified custom field name of objects
     * in this layer.
     *
     * @param   String             The field name for the associated value.
     * @return  ArrayList<String>  The value for the passed in fieldName.
     */
    public ArrayList<String> getCustomDataFieldValue(String fieldName) {        
        return objects.getCustomDataFieldValue(fieldName);
    }    
    
    /**
     * Returns the EarliestDate used by any Coordinate in this Layer.
     * 
     * @return 
     */
    public Date getEarliestDate() {
        Date      current, earliest;
        VectorObject currentObject;

        earliest = new Date();

        for (int i = 0; i < objects.size(); i++) {
            currentObject = objects.get(i);
            current       = new Date(currentObject.getCoordinateList().getEarliestDate());

            if (current.before(earliest))
                earliest = current;
        }
        
        return earliest;
    }
    
    /**
     * Gets the Latest Date used by any Coordinate in this Layer.
     * 
     * @return 
     */
    public Date getLatestDate() {
        Date      current, latest;
        VectorObject currentObject;

        latest = new Date(objects.get(0).getCoordinateList().getEarliestDate());

        for (int i = 0; i < objects.size(); i++) {
            currentObject = objects.get(i);
            current       = new Date(currentObject.getCoordinateList().getEarliestDate());

            if (current.after(latest))
                latest = current;
        }
        
        return latest;
    }    
    
    /**
     * Returns a VectorObject from with the given reference.
     * Returns null if the object reference cannot be found.
     * 
     * @param ref
     * @return 
     */
    public VectorObject getMapObjectFromReference(long ref) {
        VectorObject   object = null;

        for (VectorObject obj: objects) {
            if (ref == obj.getReference()) {
                object = obj;
                break;
            }
        }
        
        return object;
    }    
    
    /**
     * Returns the maximum value for a numeric data field of all object in this layer.
     *
     * @param   String  The field name to find the maximum.
     * @return  double  The maximum value for the supplied field.
     */
    public double getMaximumFieldValue(String fieldName) {
        double valueMax;

        valueMax = getMaximumFieldValue(fieldName, objects);

        return valueMax;
    }

    /**
     * Returns the maximum value for a numeric data field of all object in this layer.
     *
     * @param   String              The field name to find the maximum.
     * @param   MapObjectCollection The Vector of MapObjects to Search.
     * @return  double              The maximum value for the supplied field.
     */
    public static double getMaximumFieldValue(String fieldName, VectorObjectList<VectorObject> mapObjectsToSearch) {
        double  fieldValue, valueMax;
        String  fieldStringValue;

        valueMax = Double.MIN_VALUE;

        for (int i = 0; i < mapObjectsToSearch.size(); i++) {
             VectorObject currentObject = mapObjectsToSearch.get(i);
             fieldStringValue = currentObject.getCustomDataFieldValue(fieldName);

             try {
                    if (fieldStringValue != null) {
                        fieldValue = Double.parseDouble(fieldStringValue);

                        try {
                            if (fieldValue > valueMax)
                                valueMax = fieldValue;
                        } catch (NumberFormatException nfe) {
                            //not a numeric value
                        }//end catch
                    } //end value null check
            } catch (Exception e) {

            }
        }//end for loop

        return valueMax;
    }

    /**
     * Returns the minimum value for a numeric data field of all object in this layer.
     *
     * @param   String  The field name to find the minimum.
     * @return  double  The minimum value for the supplied field.
     */
    public double getMinimumFieldValue(String fieldName) {
        double  valueMin;

        valueMin = getMinimumFieldValue(fieldName, objects);

        return valueMin;
    }

    /**
     * Returns the minimum value for a numeric data field of all object in this layer.
     *
     * @param   String              The field name to find the minimum.
     * @param   Vector<MapObject>   The Vector of MapObjects to Search.
     * @return  double              The minimum value for the supplied field.
     */
    public static double getMinimumFieldValue(String fieldName, VectorObjectList<VectorObject> mapObjectsToSearch) {
        double  fieldValue, valueMin;
        String  fieldStringValue;

        valueMin = Double.MAX_VALUE;

        for (VectorObject currentObject: mapObjectsToSearch) {
            fieldStringValue = currentObject.getCustomDataFieldValue(fieldName);

            try {
                if (fieldStringValue != null) {

                    try {
                        fieldValue = Double.parseDouble(currentObject.getCustomDataFieldValue(fieldName));

                        if (fieldValue < valueMin)
                            valueMin = fieldValue;
                    } catch (NumberFormatException nfe) {
                        //not a numeric value
                    }//end catch
                } //end if (fieldStringValue != null)
            } catch (Exception e) {
                
            }

        } //end for loop

        return valueMin;
    }    
    
    /**
     * Returns a list of all the objects in this layer.
     * 
     * @return 
     */
    public VectorObjectList<VectorObject> getObjectList() {
        return objects;
    }    
    
    /**
     * Returns any overlays used in this VectorLayer.
     * 
     * @return 
     */
    public ArrayList<Overlay> getOverlays() {
        if (this.overlays != null) {
            return this.overlays;
        } else {
            return new ArrayList<Overlay>();
        }
    }
    
    /**
     * Returns the Time Span Begin as a String in the format: yyyy-MM-dd'T'HH:mm:ss
     * 
     * @return 
     */    
    public String getTimeSpanBeginString() {
        this.timestampDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        if (timeSpanBegin == null) {
            return null;
        } else {
            return timestampDateFormat.format(timeSpanBegin)  + "Z";
        }
    }

    /**
     * Returns the Time Span End as a String in the format: yyyy-MM-dd'T'HH:mm:ss
     * 
     * @return 
     */
    public String getTimeSpanEndString() {
        this.timestampDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        if (timeSpanEnd == null) {
            return null;
        } else {
            return timestampDateFormat.format(timeSpanEnd)  + "Z";
        }
    }    
    
    /**
     * Removes a given mapObject from the layer.  It does not change the 
     * parentLayer field of that object.
     * 
     */
    public void removeObject(VectorObject object) {
        objects.remove(object);
    }
    
    /**
     * Selects all object within the given rectangle, unless user clicks with
     * a single mouse click.  The single mouse click has a specific selection 
     * rectangle with hight and width of 8.
     * 
     * @param  range
     * @return The collection of objects selected given range
     */
    @Override
    public MapObjectList<MapObject> selectObjects(Rectangle2D range) {
        VectorObject                currentObject;
        MapObjectList<MapObject>    newlySelectedObjects;

        newlySelectedObjects = new MapObjectList<MapObject>();
                        
        try {
            //search backwards to get objects with higher z-order first
            for (int i = (objects.size() - 1); i >= 0; i--) {
                currentObject = (VectorObject) objects.get(i);
                
                if (currentObject != null) {                
                    if (currentObject.isObjectWithinRectangle(range)) {
                        if (currentObject.isVisible(lastMapView))
                            newlySelectedObjects.add(currentObject);

                        //code for a single mouse click
                        //selection only returns the object closes to the top
                        if ((range.getWidth() == MapPanel.SINGLE_CLICK_WIDTH) && (newlySelectedObjects.size() > 0))
                            break;
                    }
                } else {//end null check
                    //clean up any nulls
                    objects.remove(i);
                }
            }
        } catch (Exception e) {
            System.err.println("Error in VectorLayer.selectObjects(Rectangle2D) - " + e);
            
        }

        return (newlySelectedObjects);
    } // end selectObjects

    /**
     * Sets the list of objects in this Layer, all previous objects will be 
     * removed.
     * 
     * @param objects 
     */
    public void setObjectList(VectorObjectList<VectorObject> objects) {
        for (VectorObject object: objects)
            object.setParentLayer(this);
            
        this.objects = objects;
        //this.selectedObjects.clear();                      
    }
    
    /**
     * Changes a specific VectorObject's Z order to the one specified
     *
     * @param   VectorObject   mapObject, the object to change the Z order of.
     * @param   int            zOrder, the new Z position for the object.
     * @return  boolean        if the object was found and moved.
     */
    public boolean setObjectZOrder(VectorObject mapObject, int zOrder) {
        boolean success;

        success = objects.remove(mapObject);

        if (success) {
            if (zOrder > (objects.size() - 1)) {
                objects.add(mapObject);
            } else {
                objects.add(zOrder, mapObject);
            }
        }

        return success;
    }           
    
    /**
     * Writes this Layer as FmXml.
     * 
     * @param kmlWriter 
     */
    @Override
    public void toXML(XmlOutput kmlWriter) {
        try {
            kmlWriter.openTag ("VectorLayer");

            kmlWriter.writeTag("name", layerName);

            if (layerDescription != null) {                
                if (!layerDescription.equals("") && !layerDescription.equalsIgnoreCase("null"))
                    kmlWriter.writeTag("description", layerDescription);
            }

            if (hasTimeSpan()) {
                kmlWriter.openTag ("TimeSpan");
                    kmlWriter.writeTag("begin", getTimeSpanBeginString());
                    kmlWriter.writeTag("end",   getTimeSpanEndString());
                kmlWriter.closeTag("TimeSpan");

                kmlWriter.writeTag("locked", Boolean.toString(isLocked()));
            }

            kmlWriter.openTag("objects");
            
            //get xml for each object
            for (int i = 0; i < objects.size(); i++)
                objects.get(i).toXML(kmlWriter);

            kmlWriter.closeTag("objects");
            
            kmlWriter.closeTag("VectorLayer");
        } catch (Exception e) {
            System.err.println("Error in VectorLayer.toXML(XmlWriter) - " + e);
        }
    }    

}
