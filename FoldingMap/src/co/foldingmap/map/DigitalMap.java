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

import co.foldingmap.map.vector.NetworkLayer;
import co.foldingmap.map.vector.LatLonAltBox;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.NodeMap;
import co.foldingmap.map.vector.SearchResultsLayer;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.GUISupport.ProgressIndicator;
import co.foldingmap.GUISupport.Updateable;
import co.foldingmap.actions.Actions;
import co.foldingmap.actions.UpdateObjectOutlines;
import co.foldingmap.map.raster.RasterLayer;
import co.foldingmap.map.themes.MapTheme;
import co.foldingmap.map.themes.MapThemeManager;
import co.foldingmap.map.visualization.TimeSpanControl;
import co.foldingmap.map.visualization.VisualizationLayer;
import co.foldingmap.xml.XmlOutput;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Alec
 */
public class DigitalMap {
    public static final String[] DATUM          = {"WGS86"};
    public static final String[] MAP_PROJECTION = {"Web Mercator"};
    
    private Actions                      actions;
    private ArrayList<Layer>             layers;
    private ArrayList<Updateable>        upateables;
    private Coordinate                   lookAtCoordinate;
    private File                         mapFile;    
    private Layer                        selectedLayer;
    private long                         lastObjectReference;
    private MapObjectList<MapObject>     selectedObjects;
    private MapTheme                     mapTheme;
    private MapThemeManager              mapThemeManager;
    private MapView                      lastMapView;
    private NodeMap                      coordinateSet;
    private SearchResultsLayer           searchResultsLayer;
    private String                       mapDescription, mapName, versionNumber;
    private TimeSpanControl              timeSpanControl; 
    
    public DigitalMap() {
        init();
                
        this.lastMapView         = new MapView(new MercatorProjection());
        this.mapName             = "New Map";   
        this.versionNumber       = "0.1";        
        this.lastObjectReference = 0;
        this.selectedObjects     = new MapObjectList<MapObject>();   
        this.upateables          = new ArrayList<Updateable>();
    }
    
    public DigitalMap(String name, MapProjection projection) {
        init();
        
        this.coordinateSet       = new NodeMap();
        this.mapName             = name;        
        this.lastMapView         = new MapView(projection);
        this.versionNumber       = "0.1";
        this.lastObjectReference = 0;
        this.selectedObjects     = new MapObjectList<MapObject>();
        this.upateables          = new ArrayList<Updateable>();
    }
    
    /**
     * Adds a Coordinate to this map's NodeMap.
     * 
     * @param c 
     */
    public void addCoordinateNode(Coordinate c) {
        this.coordinateSet.put(c);
    }
    
    /**
     * Adds a layer to map and returns the index it was added to.
     * 
     * @param newLayer
     * @return 
     */
    public int addLayer(Layer newLayer) {
        int layerIndex;
        
        if (selectedLayer == null) 
            selectedLayer = newLayer;        
        
        layers.add(newLayer);
        layerIndex = layers.size() - 1;
        newLayer.setParentMap(this);
        
        if (newLayer instanceof VisualizationLayer) {
            VisualizationLayer vl = (VisualizationLayer) newLayer;

            if (vl.hasTimeSeries()) {
                timeSpanControl = vl.getTimeSpanControl();                                
            }
        }        
        
        fireUpdates();
        
        return layerIndex;
    }    
    
    /**
     * Adds a new layer at a given index, or z-order.
     * 
     * @param newLayer
     * @param atIndex
     */
    public void addLayer(Layer newLayer, int atIndex) {        
        try {
            layers.add(atIndex, newLayer);
            newLayer.setParentMap(this);
            
            if (newLayer instanceof VisualizationLayer) {
                VisualizationLayer vl = (VisualizationLayer) newLayer;
                
                if (vl.hasTimeSeries()) 
                    timeSpanControl = vl.getTimeSpanControl();                
            }
        } catch (Exception e) {
            System.err.println("Error in DigitalMap.addLayer(Layer, int) - " + e);
        }
        
        fireUpdates();
    }    
    
    /**
     * Adds an Updateable object to this DigitalMap.
     * 
     * @param u 
     */
    public void addUpdateable(Updateable u) {
        this.upateables.add(u);
    }
    
    /**
     * Calculates the screen points of all Coordinates.
     * This is called once every time the map is drawn.
     * 
     * @param mapView 
     */
    public void calculateCoordinateLocations(MapView mapView) {           
        float   y;

        //Convert all the coordinates once, to save cpu
        if (coordinateSet != null) {
            for (Coordinate c: coordinateSet.getAllCoordinates()) {
                if (c != null) {
                    y = mapView.getY(c);
                    c.setCenterPoint(mapView.getX(c, MapView.NO_WRAP), y);

                    if (mapView.getMapProjection().isLeftShown()) 
                        c.setLeftPoint(mapView.getX(c, MapView.WRAP_LEFT), y);                

                    if (mapView.getMapProjection().isRightShown()) 
                        c.setRightPoint(mapView.getX(c, MapView.WRAP_RIGHT), y);                
                }
            }  
        }
    }
    
    /**
     * Returns if this map contains a Raster Layer;
     * 
     * @return 
     */
    public boolean containsRasterLayer() {
        boolean result = false;
        
        for (Layer l: layers) {
            if (l instanceof RasterLayer) {
                result = true;
                break;
            }
                
        }
        
        return result;
    }
    
    /**
     * Cleanly close the map.
     * 
     */
    public void closeMap() {
        //TODO: Check to see if map need saving
                
        for (Layer l: layers)
            l.closeLayer();
            
        layers.clear();
        
        coordinateSet = null;        
        actions       = null;
        
        System.gc();
    }
    
    /**
     * Deselects all objects in the map
     */
    public void deselectObjects() {
        for (MapObject object: this.selectedObjects) {
            object.setHighlighted(false);
            object.setSelectedCoordinate(Coordinate.UNKNOWN_COORDINATE);
        }
        
        selectedObjects.clear();
    }    
    
    //draws the map by calling the drawObject method of each object in the map
    public void drawMap(Graphics2D g2, MapView mapView) {         
        this.lastMapView = mapView;

        mapView.setMapTheme(mapTheme);
        mapView.getLabelManager().clear();

        calculateCoordinateLocations(mapView);

        //draw each layer, in reverse order
        for (int l = layers.size() - 1; l >= 0; l--) {
            Layer currentLayer = layers.get(l);
            currentLayer.drawLayer(g2, mapView);
        }

        if (searchResultsLayer != null)
            searchResultsLayer.drawLayer(g2, mapView);             

        //draw selected object points
        if (mapView.arePointsShown()) {
            for (MapObject object: selectedObjects) {
                if (object.isHighlighted() && object.getParentLayer().isVisible() == true) 
                    object.drawPoints(g2, mapView);
            }
        }

        //draw labels
        mapView.getLabelManager().drawLabels(g2);      
    }    
    
    /**
     * Executes the update() method on all the updateable objects added to this DigitalMap.
     */
    protected void fireUpdates() {
        for (Updateable u: this.upateables)
            u.update();
    }
    
    /**
     * Returns the actions manager used with this map.
     * 
     * @return 
     */
    public Actions getActions() {
        return actions;
    }
    
    /**
     * Gets all the custom data field names associated with this object.
     *
     * @return  Vector<String>  A Vector containing all of the Custom Field Names.
     */
    public ArrayList<String> getAllCustomDataFields() {
        boolean            stringFound;
        ArrayList<String>  allFields, objectFields;
        VectorLayer        currentVectorLayer;

        allFields  = new ArrayList<String>();

        for (Layer currentLayer: layers) {
            if (currentLayer instanceof VectorLayer) {
                currentVectorLayer = (VectorLayer) currentLayer;
                objectFields       = currentVectorLayer.getObjectList().getAllCustomDataFields();

                for (String currentField: objectFields) {
                    stringFound  = false;

                    for (String currentAllField: allFields) {
                        if (currentField.equals(currentAllField))
                            stringFound = true;
                    }

                    if (!stringFound)
                        allFields.add(currentField);
                } //end fields for loop
            } //end Vector Layer Check
        } // end objects for loop

        return allFields;
    }    
    
    /**
     * Returns all the MapObjects in each layer of this Map.
     * 
     * @return 
     */
    public MapObjectList<MapObject> getAllMapObjects() {
        MapObjectList<MapObject>    objects;
        VectorLayer                 vl;
        
        objects = new MapObjectList<MapObject>();
        
        for (Layer l: layers) {
            if (l instanceof VectorLayer) {
                vl = (VectorLayer) l;
                objects.addAll(vl.getObjectList());
            }
        }
        
        return objects;
    }
    
    /**
     * Returns the boundary for this map.
     * 
     * @return 
     */
    public LatLonAltBox getBoundary() {
        LatLonAltBox    bounds, layerBounds;
        Layer           layer0;
        
        layer0 = layers.get(0);
        bounds = layer0.getBoundary();
        
        for (Layer l: this.layers) {
            layerBounds = l.getBoundary();
            bounds      = LatLonAltBox.combine(bounds, layerBounds);
        }
        
        return bounds;
    }    
    
    /**
     * Returns the maps CoordinateSet, null if it has not been set.
     * 
     * @return 
     */
    public NodeMap getCoordinateSet() {
        return this.coordinateSet;
    }
    
    /**
     * Returns all the values for a specified custom field name of objects
     * in the map.
     *
     * @param   String             The field name for the associated value.
     * @return  ArrayList<String>  The value for the passed in fieldName.
     */
    public ArrayList<String> getCustomDataFieldValue(String fieldName) {
        ArrayList<String>   values;
        VectorLayer         vl;
        
        values = new ArrayList<String>();
        
        for (Layer l: this.layers) {
            if (l instanceof VectorLayer) {
                vl = (VectorLayer) l;
                values.addAll(vl.getCustomDataFieldValue(fieldName));
            }
        }
        
        return values;
    }    
    
    /**
     * Returns the last MapView used by this map.
     * 
     * @return 
     */
    public MapView getLastMapView() {
        return lastMapView;
    }
    
    /**
     * Returns the Layer at the given index in this Map's Layer List.
     * 
     * @param layerNumber
     * @return 
     */
    public Layer getLayer(int layerNumber) {
        return layers.get(layerNumber);
    }    
    
    /**
     * Returns an ArrayList of all the Layers in this Map.
     * 
     * @return 
     */
    public ArrayList<Layer> getLayers() {
        return layers;
    }    
    
    /**
     * Gets the Coordinate used to set the view port position when 
     * the map is opened.
     * 
     * @return 
     */
    public Coordinate getLookAtCoordinate() {
        if (lookAtCoordinate == null) 
            lookAtCoordinate = new Coordinate(0,0,0);
        
        return lookAtCoordinate;
    }
    
    /**
     * Returns a VectorObject from any layer with the given reference.
     * Returns null if the object reference cannot be found.
     * 
     * @param ref
     * @return 
     */
    public VectorObject getMapObjectFromReference(long ref) {
        VectorObject   object = null;
        VectorLayer vl;
        
        for (Layer l: layers) {
            if (l instanceof VectorLayer) {
                vl = (VectorLayer) l;
                object = vl.getMapObjectFromReference(ref);
                
                if (object != null)
                    break;
            }
        }
        
        return object;
    }
    
    /**
     * Returns this Map's description.
     * 
     * @return 
     */
    public String getMapDescription() {
        if (mapDescription == null) {
            return "";
        } else {
            return this.mapDescription;
        }
    }    
    
    /**
     * Returns the file holding this map's data.
     */
    public File getMapFile() {
        return mapFile;
    }
    
    /**
     * Returns the theme manager for this map.
     * 
     * @return 
     */
    public MapThemeManager getMapThemeManager() {
        return mapThemeManager;
    }
    
    /**
     * Returns the maximum value for a numeric data field of all objects in this map.
     *
     * @param   String  The field name to find the maximum.
     * @return  double  The maximum value for the supplied field.
     */
    public double getMaximumFieldValue(String fieldName) {
        double      fieldValue, valueMax;
        VectorLayer currentVectorLayer;

        valueMax = Double.MIN_VALUE;

        for (Layer currentLayer: layers) {
            if (currentLayer instanceof VectorLayer) {
                currentVectorLayer = (VectorLayer) currentLayer;

                fieldValue = currentVectorLayer.getMaximumFieldValue(fieldName);

                if (fieldValue > valueMax)
                    valueMax = fieldValue;
            }
        }

        return valueMax;
    }

    /**
     * Returns the minimum value for a numeric data field of all objects in this map.
     *
     * @param   String  The field name to find the minimum.
     * @return  double  The minimum value for the supplied field.
     */
    public double getMinimumFieldValue(String fieldName) {
        double      fieldValue, valueMin;
        VectorLayer currentVectorLayer;

        valueMin = Double.MAX_VALUE;

        for (Layer currentLayer: layers) {
            if (currentLayer instanceof VectorLayer) {
                currentVectorLayer = (VectorLayer) currentLayer;

                fieldValue = currentVectorLayer.getMinimumFieldValue(fieldName);

                if (fieldValue < valueMin)
                    valueMin = fieldValue;
            }
        }

        return valueMin;
    }    
    
    /**
     * Returns this Map's name.
     * 
     * @return 
     */
    public String getName() {
        return mapName;
    }    
    
    /**
     * Returns a new reference to use with an object.  This will be unique for 
     * the whole map;
     * 
     * @return 
     */
    public long getNewObjectReference() {
        return lastObjectReference++;        
    }
    
    /**
     * Returns the MapOBject closes to the given Coordinate.
     * 
     * @param c
     * @return 
     */
    public VectorObject getObjectClosestTo(Coordinate c) {
        VectorObject                 closetsObject;
        VectorObjectList<VectorObject>  closestInEachLayer;
        VectorLayer               currentVectorLayer;

        closestInEachLayer = new VectorObjectList<VectorObject>();

        try {
            //get the closest in each layer
            for (Layer currentLayer: layers) {
                if (currentLayer instanceof VectorLayer) {
                    currentVectorLayer = (VectorLayer) currentLayer;
                    closetsObject      = currentVectorLayer.getObjectList().getObjectClosestTo(c);
                    closestInEachLayer.add(closetsObject);
                } //end vector layer check
            } //end layers loop
        } catch (Exception e) {
            System.err.println("Error in DigitalMap.getObjectClosestTo.(Coordinate) - " + e);
        }

        return closestInEachLayer.getObjectClosestTo(c);
    }    
    
    /**
     * Returns the currently selected Layer.
     * 
     * @return 
     */
    public Layer getSelectedLayer() {
        if (selectedLayer == null) 
            selectedLayer = this.getVectorLayer();
                    
        return selectedLayer;
    }    
    
    /**
     * Returns the Selected objects from all layers.
     * 
     * @return 
     */
    public MapObjectList<MapObject> getSelectedObjects() {
        return selectedObjects;
    }    
    
    /**
     * Returns the Theme being used by this map.
     * 
     * @return 
     */
    public MapTheme getTheme() {
        if (mapTheme == null) 
            mapTheme = new MapTheme("Default Theme");
        
        return mapTheme;
    }
    
    /**
     * Returns the TimeSpanControl, if it exists, otherwise returns null.
     * 
     * @return 
     */
    public TimeSpanControl getTimeSpanControl() {
        return timeSpanControl;
    }    
    
    /**
     * Returns the first VectorLayer in this map.  
     * If there are no VectorLayers a new one is added.
     * 
     * @return 
     */
    public VectorLayer getVectorLayer() {
        VectorLayer vectorLayer = null;
        
        for (Layer l: this.getLayers()) {
            if (l instanceof NetworkLayer) {
                
            } else if (l instanceof VectorLayer) {
                vectorLayer = (VectorLayer) l;
                break;
            }
        }
        
        if (vectorLayer == null) {
            vectorLayer = new VectorLayer("New Layer");
            this.addLayer(selectedLayer);
        }
        
        return vectorLayer;
    }
    
    /**
     * Returns the version number for this map as a String.
     * 
     * @return 
     */
    public String getVersionNumber() {
        return versionNumber;
    }

    /**
     * Highlights objects within a given range.
     * 
     * @param range
     * @param controlPressed 
     */
    public void highlightObject(Rectangle2D range, boolean controlPressed) {
        try {
            MapObjectList<MapObject> newlySelectedObjects;            

            newlySelectedObjects = (selectObjects(range));
            
            if (controlPressed) {
                //Handle the control select
                
                //If objects are already selected unselect them.
                selectedObjects = getSelectedObjects();
                
                for (int i = 0; i < newlySelectedObjects.size(); i++) {
                    MapObject currentObject = newlySelectedObjects.get(i);
                    
                    if (selectedObjects.contains(currentObject)) {
                        selectedObjects.remove(currentObject);
                        currentObject.setHighlighted(false);
                    } else {
                         selectedObjects.add(currentObject);
                         currentObject.setHighlighted(true);
                    }
                }
            } else {
                deselectObjects();
                selectedObjects = newlySelectedObjects;

                for (int i = 0; i < selectedObjects.size(); i++) {
                    MapObject currentObject = selectedObjects.get(i);
                    currentObject.setHighlighted(true);
                }
            }//End if controlPressed

            setSelected(selectedObjects);
        } catch (Exception e) {
            System.err.println("Error in DigitalMap.highlightObject.(Rectangle2D, boolean) " + e);
        }
    }    
    
    /**
     * Initialize object for this digital map.
     */
    private void init() {
        coordinateSet   = new NodeMap();
        layers          = new ArrayList<Layer>();
        mapThemeManager = new MapThemeManager();
        mapTheme        = new MapTheme("Default Theme");      
    }
    
    /**
     * Removes all layers from this map.
     */
    public void removeAllLayers() {
        this.layers.clear();
        
        fireUpdates();
    }
    
    /**
     * Removes a given layer from the map.
     * 
     * @param l 
     */
    public void removeLayer(Layer l) {
        this.layers.remove(l);
        
        if (l instanceof VisualizationLayer) {
                VisualizationLayer vl = (VisualizationLayer) l;
                
            if (vl.hasTimeSeries()) {
                timeSpanControl = null;  
            }
        }    
        
        fireUpdates();
    }
    
    /**
     * Selects all objects in the given range and returns those selected
     * MapObjects in a list.
     * 
     * @param range
     * @return 
     */
    public MapObjectList<MapObject> selectObjects(Rectangle2D range) {
        MapObjectList<MapObject> selectedObjectsFromLayer, selectedObjects;

        selectedObjects = new MapObjectList<MapObject>();

        //if there is a serch in progress try to select the results first
        if (searchResultsLayer != null)  {
            selectedObjectsFromLayer = this.searchResultsLayer.selectObjects(range);

            if (selectedObjects.isEmpty()) {
                selectedObjects.addAll(selectedObjectsFromLayer);
            } else if ((range.getWidth() != MapPanel.SINGLE_CLICK_WIDTH)) {
                selectedObjects.addAll(selectedObjectsFromLayer);
            }
        }

        for (Layer currentLayer: layers) {
            if (currentLayer.isVisible() && !currentLayer.isLocked()) {
                selectedObjectsFromLayer = currentLayer.selectObjects(range);

                if (selectedObjects.isEmpty()) {
                    selectedObjects.addAll(selectedObjectsFromLayer);
                } else if ((range.getWidth() != MapPanel.SINGLE_CLICK_WIDTH)) {
                    //Check to see if we are doing a mass select or not                    
                    selectedObjects.addAll(selectedObjectsFromLayer);
                }
            }//end visible and locked check
        }

        return selectedObjects;
    }    
    
    /**
     * Sets the Action manager to be used with this map.
     * 
     */
    public void setActions(Actions actions) {
        this.actions = actions;
    }
    
    /**
     * Sets the CoordinateSet for this map
     * 
     * @param coordinateSet 
     */
    public void setCoordinateSet(NodeMap coordinateSet) {
        this.coordinateSet = coordinateSet;
    }

    /**
     * Sets the description for this map
     * @param mapDescription 
     */
    public void setMapDescription(String mapDescription) {
        this.mapDescription = mapDescription;
    }
        
    /**
     * Sets the LookAtCoordinate for this map.
     * The LookAtCoordinate is the part of the map displayed when it is loaded.
     * 
     * @param c 
     */
    public void setLookAtCoordinate(Coordinate c) {
        this.lookAtCoordinate = c;        
        this.lastMapView.getMapProjection().setReference(c);
    }
    
    /**
     * Sets the file this map was loaded from.
     * 
     * @param mapFile 
     */
    public void setMapFile(File mapFile) {
        this.mapFile = mapFile;
    }
    
    /**
     * Sets the name for this Map.
     * 
     * @param newName 
     */
    public void setName(String newName) {
        this.mapName = newName;
    }
    
    /**
     * Sets the node map to be used by this map.
     * 
     * @param newNodeMap 
     */
    public void setNodeMap(NodeMap newNodeMap) {
        this.coordinateSet = newNodeMap;
    }
    
    /**
     * Selects the object given.
     * 
     * @param objectToSelect 
     */
    public void setSelected(MapObject objectToSelect) {
        objectToSelect.setHighlighted(true);
        selectedObjects.add(objectToSelect);
    }    
    
    /**
     * Highlights and adds the provided list of MapObjects to the selected objects.
     * 
     * @param objectsToSelect 
     */
    public void setSelected(MapObjectList<MapObject> objectsToSelect) {        
        for (MapObject object: objectsToSelect) 
            setSelected(object);        
    }    
    
    /**
     * Highlights and adds the provided list of VectorObjects to the selected objects.
     * 
     * @param objectsToSelect 
     */
    public void setSelected(VectorObjectList<VectorObject> objectsToSelect) {        
        for (VectorObject object: objectsToSelect) 
            setSelected(object);        
    }      
    
    /**
     * Sets the selected layer for this map.
     * 
     * @param newSelectedLayer 
     */
    public void setSelectedLayer(Layer newSelectedLayer) {
        selectedLayer = newSelectedLayer;
    }    
    
    /**
     * Sets the theme associated with this map.
     * 
     * @param mapTheme 
     */
    public void setTheme(MapTheme          mapTheme, 
                         Updateable        updateable,
                         ProgressIndicator progressIndicator) {
        
        this.mapTheme = mapTheme;
        
        UpdateObjectOutlines uoo = new UpdateObjectOutlines(mapTheme, this.layers, updateable, progressIndicator);
        uoo.start();
    }
    
    public void toXML(XmlOutput kmlWriter) {
        double   mapLatitude, mapLongitude, mapZoomLevel;

        try {
            mapLatitude  = lastMapView.getMapProjection().getReferenceLatitude()  ;
            mapLongitude = lastMapView.getMapProjection().getReferenceLongitude() ;
            mapZoomLevel = lastMapView.getZoomLevel();

            kmlWriter.writeTextLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            kmlWriter.openTag("kml xmlns=\"http://www.opengis.net/kml/2.2\"");
            kmlWriter.openTag("Document");

            kmlWriter.writeTag("name", mapName);
            kmlWriter.writeTag("open", "1");

            kmlWriter.openTag ("LookAt");
            kmlWriter.writeTag("longitude",    Double.toString(mapLongitude));
            kmlWriter.writeTag("latitude",     Double.toString(mapLatitude));
            kmlWriter.writeTag("gx:Zoomlevel", Double.toString(mapZoomLevel));
            //kmlWriter.writeTag("altitude",  Double.toString(mapZoomLevel));   //removed until it can be made compatible
            kmlWriter.closeTag("LookAt");

            mapTheme.toXML(kmlWriter);

            //layer objects
            for (Layer currentLayer: layers) {
                currentLayer.toXML(kmlWriter);
            }

            kmlWriter.closeTag("Document");
            kmlWriter.closeTag("kml");
        } catch (Exception e) {
            System.err.println("Error in DigitalMap.toXML(KmlWritter) - " + e);
        }
    }    
}
