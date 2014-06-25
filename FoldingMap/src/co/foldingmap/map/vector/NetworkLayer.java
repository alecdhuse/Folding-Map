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

import co.foldingmap.mapImportExport.FmXmlImporter;
import co.foldingmap.mapImportExport.GeoRssImporter;
import co.foldingmap.mapImportExport.KmlImport;
import co.foldingmap.mapImportExport.JsonImporter;
import co.foldingmap.mapImportExport.CsvImporter;
import co.foldingmap.mapImportExport.MapImporter;
import co.foldingmap.mapImportExport.JsImporter;
import co.foldingmap.map.Overlay;
import co.foldingmap.map.MapView;
import co.foldingmap.map.MapObjectList;
import co.foldingmap.map.MapObject;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.Visibility;
import co.foldingmap.map.Layer;
import co.foldingmap.Logger;
import co.foldingmap.ResourceHelper;
import co.foldingmap.map.themes.ColorStyle;
import co.foldingmap.map.tile.TileMath;
import co.foldingmap.xml.XmlOutput;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JMenuItem;

/**
 *
 * @author Alec
 */
public class NetworkLayer extends VectorLayer implements ActionListener {
    public static final int  NEVER       = 0;
    public static final int  ON_CHANGE   = 1;
    public static final int  ON_INTERVAL = 2;
    public static final int  ON_EXPIRE   = 3;
            
    protected ArrayList<Layer>  layers;   
    protected float             refreshInterval; //in seconds
    protected int               refreshMode;
    protected long              lastUpdate;
    protected JMenuItem         menuItemRefresh;
    protected NodeMap           nodeMap;
    protected ResourceHelper    helper;
    protected String            address, defaultPointClass;
    protected VectorLayer       defaultLayer;
    protected Visibility        layerVisibility;
    
    public NetworkLayer(String layerName, String address) {
        this.defaultPointClass = "Point";        
        this.helper            = ResourceHelper.getInstance();
        this.lastUpdate        = 0;
        this.layerName         = layerName;
        this.layers            = new ArrayList<Layer>();
        this.address           = address;
        this.nodeMap           = new NodeMap();
        this.refreshInterval   = 300;
        this.layerVisibility   = new Visibility();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == menuItemRefresh) {
            updateData();
        }
    }     
    
    /**
     * Adds a list of map objects to this layer.
     * 
     * @param objectsToAdd 
     */    
    @Override
    public void addAllObjects(VectorObjectList<VectorObject> objectsToAdd) {
        getDefaultLayer().addAllObjects(objectsToAdd);
        Logger.log(Logger.WARN, "Objects added to a NetworkLayer will be deleted the next time the layer is reloaded.");
    }
    
    /**
     * Adds a VectorObject to this Layer.
     * 
     * @param obj 
     */    
    @Override
    public void addObject(VectorObject obj) {
        getDefaultLayer().addObject(obj);
        Logger.log(Logger.WARN, "Objects added to a NetworkLayer will be deleted the next time the layer is reloaded.");
    }
    
    /**
     * Adds a VectorObject to this layer at a specific z-position.
     * 
     * @param obj
     * @param posistion 
     */
    @Override
    public void addObject(VectorObject obj, int posistion) {
        getDefaultLayer().addObject(obj, posistion);
        Logger.log(Logger.WARN, "Objects added to a NetworkLayer will be deleted the next time the layer is reloaded.");        
    }    
    
    /**
     * Adds an Overlay to this layer.
     * 
     * @param overlay 
     */
    @Override
    public void addOverlay(Overlay overlay) {
        getDefaultLayer().addOverlay(overlay);
    }    
    
    /**
     * Adds one or more Overlays to this layer.
     * 
     * @param overlays
     */
    @Override
    public void addOverlays(ArrayList<Overlay> overlays) {
        getDefaultLayer().addAllObjects(objects);
    }           
    
    /**
     * Calculates the screen points of all Coordinates in this Layer.
     * This is called once every time the Layer is drawn.
     * 
     * @param mapView 
     */
    public void calculateCoordinateLocations(MapView mapView) {           
        float   y;

        //Convert all the coordinates once, to save cpu
        if (nodeMap != null) {
            for (Coordinate c: nodeMap.getAllCoordinates()) {
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
     * Removes all Overlays from this VectorLayer.
     * 
     */
    @Override
    public void clearOverlays() {
        //TODO: Make this work for every layer.
        getDefaultLayer().clearOverlays();
    }    
    
    /**
     * Closes this Layer gracefully.
     */
    @Override
    public void closeLayer() {
        
    }    
    
    @Override
    public Layer copy() {
        NetworkLayer layerCopy = new NetworkLayer(this.layerName, this.address);
        
        layerCopy.setDefaultPointClass(defaultPointClass);
        layerCopy.setLayerDescription(layerDescription);
        layerCopy.setLocked(locked);
        layerCopy.setParentMap(parentMap);
        layerCopy.setRefreshInterval(refreshInterval);
        layerCopy.setRefreshMode(refreshMode);
        layerCopy.setVisibility(layerVisibility.getMinTileZoomLevel(), layerVisibility.getMaxTileZoomLevel());
        
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
        boolean drawLayer;
        
        try {
            calculateCoordinateLocations(mapView);
            
            //See if this layer is visible and if we need to draw this layer.
            if (this.visible == true) {
                drawLayer = layerVisibility.isVisible(mapView.getZoomLevel());      
            } else {
                drawLayer = false;
            }

            if (drawLayer) {
                //check to see if the map needs updating 
                if ((lastUpdate + (refreshInterval * 1000)) < System.currentTimeMillis())
                    updateData();

                for (int i = layers.size() - 1; i >= 0; i--) {
                    Layer l = this.layers.get(i);
                    l.drawLayer(g2, mapView);
                }

            }
        } catch (Exception e) {
            System.err.println("Error in NetworkLayer.drawLayer(Graphics2D, MapView) - " + e);
        }
    }    
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NetworkLayer) {
            return (this.hashCode() == obj.hashCode());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        
        hash = 53 * hash + Float.floatToIntBits(this.refreshInterval);
        hash = 53 * hash + this.refreshMode;
        hash = 53 * hash + (this.address   != null ? this.address.hashCode()   : 0);
        hash = 53 * hash + (this.layerName != null ? this.layerName.hashCode() : 0);
        hash = 53 * hash + (this.layerDescription != null ? this.layerDescription.hashCode() : 0);
        hash = 53 * hash + (this.layerVisibility  != null ? this.layerVisibility.hashCode()  : 0);
        
        return hash;
    }
    
    /**
     * Returns the address that this NetworkLayer references.
     * 
     * @return 
     */
    public String getAddress() {
        return address;
    }
    
    /**
     * Returns all the Coordinates in MapObjects in this Layer.
     * @return 
     */
    @Override
    public CoordinateList<Coordinate> getAllLayerCoordinates() {
        CoordinateList<Coordinate> coords = new CoordinateList<Coordinate>();
        
        for (Layer l: this.layers) {
            if (l instanceof VectorLayer) {
                VectorLayer vl = (VectorLayer) l;
                coords.addAll(vl.getAllLayerCoordinates());
            }
        }
        
        return coords;
    }    
    
    /**
     * Returns all Objects within a given range.
     * 
     * @param range
     * @return 
     */
    @Override
    public VectorObjectList<VectorObject> getAllObjectsWithinRange(LatLonAltBox range) {
        VectorObjectList<VectorObject> objectsInRange = new VectorObjectList<VectorObject>();
        
        for (Layer l: this.layers) {
            if (l instanceof VectorLayer) {
                VectorLayer vl = (VectorLayer) l;
                objectsInRange.addAll(vl.getAllObjectsWithinRange(range));
            }
        }
        
        return objectsInRange;
    }    
    
    /**
     * Returns the boundary that contains all the object in this layer.
     * 
     * @return 
     */
    @Override
    public LatLonAltBox getBoundary() {
        LatLonAltBox boundary = null;
        
        for (Layer l: this.layers) {
            if (l instanceof VectorLayer) {
                VectorLayer vl = (VectorLayer) l;
                
                if (boundary == null) {
                    boundary = vl.getBoundary();
                } else {
                    boundary = LatLonAltBox.combine(boundary, vl.getBoundary());
                }
            }
        }
        
        return getDefaultLayer().getBoundary();
    }    
    
    /**
     * Returns the center Longitude of all object in this Layer.
     * 
     * @return 
     */
    @Override
    public float getCenterLongitude() {
        return (float) getBoundary().getCenter().getLongitude();
    }    
    
    /**
     * Gets the center Latitude of all the Objects in this Layer.
     * 
     * @return 
     */
    @Override
    public float getCenterLatitude() {
        return (float) getBoundary().getCenter().getLatitude();
    }    
    
    /**
     * Returns JMenuItems that should be used in the context menu for this Layer
     * 
     * @return 
     */
    @Override
    public JMenuItem[] getContextMenuItems() {           
        JMenuItem[] menuItems;
        
        menuItems       = new JMenuItem[1];
        menuItemRefresh = new JMenuItem("Refresh");
        menuItems[0]    = menuItemRefresh;
        
        menuItemRefresh.addActionListener(this);
        
        return menuItems;
    }    
    
    /**
     * Returns all the values for a specified custom field name of objects
     * in this layer.
     *
     * @param   fieldName          The field name for the associated value.
     * @return  ArrayList<String>  The value for the passed in fieldName.
     */
    @Override
    public ArrayList<String> getCustomDataFieldValue(String fieldName) {  
        ArrayList<String> values = new ArrayList<String>();
        
        for (Layer l: this.layers) {
            if (l instanceof VectorLayer) {
                VectorLayer vl = (VectorLayer) l;
                values.addAll(vl.getCustomDataFieldValue(fieldName));
            }
        }
        
        return values;
    }    
    
    /**
     * Method to get the default layer.  
     * This method creates the default layer if it does not exists.
     * 
     * @return 
     */
    private VectorLayer getDefaultLayer() {
        if (defaultLayer == null) {
            this.defaultLayer = new VectorLayer("Default Layer");
            this.defaultLayer.setParentMap(parentMap);
            this.layers.add(defaultLayer);            
        } 
        
        return defaultLayer;
    }
    
    /**
     * Returns the EarliestDate used by any Coordinate in this Layer.
     * 
     * @return 
     */
    @Override
    public Date getEarliestDate() {
        Date returnDate = null;
        
        for (Layer l: this.layers) {
            if (l instanceof VectorLayer) {
                VectorLayer vl = (VectorLayer) l;
                
                if (returnDate == null) {
                    returnDate = vl.getEarliestDate();
                } else {
                    if (returnDate.after(vl.getEarliestDate()))
                        returnDate = vl.getEarliestDate();
                }
            }
        }
        
        return returnDate;
    }    
    
    /**
     * Gets the Latest Date used by any Coordinate in this Layer.
     * 
     * @return 
     */
    @Override
    public Date getLatestDate() {
        Date returnDate = null;
        
        for (Layer l: this.layers) {
            if (l instanceof VectorLayer) {
                VectorLayer vl = (VectorLayer) l;
                
                if (returnDate == null) {
                    returnDate = vl.getLatestDate();
                } else {
                    if (returnDate.before(vl.getLatestDate()))
                            returnDate = vl.getLatestDate();
                }                
            }
        }        
        
        return returnDate;
    }    
    
    /**
     * Returns a VectorObject from with the given reference.
     * Returns null if the object reference cannot be found.
     * 
     * @param ref
     * @return 
     */
    @Override
    public VectorObject getMapObjectFromReference(long ref) {
        VectorObject returnObject = null;
        
        for (Layer l: this.layers) {
            if (l instanceof VectorLayer) {
                VectorLayer vl = (VectorLayer) l;
                returnObject = vl.getMapObjectFromReference(ref);
                
                if (returnObject != null)
                    break;
            }
        }
        
        return returnObject;
    }    
    
    /**
     * Returns the maximum value for a numeric data field of all object in this layer.
     *
     * @param   fieldName The field name to find the maximum.
     * @return  double    The maximum value for the supplied field.
     */
    @Override
    public double getMaximumFieldValue(String fieldName) {
        double currentVal, maxVal = Double.MIN_VALUE;
        
        for (Layer l: this.layers) {
            if (l instanceof VectorLayer) {
                VectorLayer vl = (VectorLayer) l;
                currentVal = vl.getMaximumFieldValue(fieldName);
                
                if (currentVal > maxVal) 
                    maxVal = currentVal;
            }
        }
        
        return maxVal;        
    }    
             
    /**
     * Returns the minimum value for a numeric data field of all object in this layer.
     *
     * @param   fieldName The field name to find the minimum.
     * @return  double    The minimum value for the supplied field.
     */
    @Override
    public double getMinimumFieldValue(String fieldName) {
        double currentVal, minVal = Double.MAX_VALUE;
        
        for (Layer l: this.layers) {
            if (l instanceof VectorLayer) {
                VectorLayer vl = (VectorLayer) l;
                currentVal = vl.getMinimumFieldValue(fieldName);
                
                if (currentVal < minVal) 
                    minVal = currentVal;
            }
        }
        
        return minVal;
    }    
    
    /**
     * Returns a list of all the objects in this layer.
     * 
     * @return 
     */
    @Override
    public VectorObjectList<VectorObject> getObjectList() {
        VectorObjectList<VectorObject> objList = new VectorObjectList<VectorObject>();
        
        for (Layer l: this.layers) {
            if (l instanceof VectorLayer) {
                VectorLayer vl = (VectorLayer) l;
                objList.addAll(vl.getObjectList());
            }
        }
        
        return objList;
    }    
    
    /**
     * Returns any overlays used in this VectorLayer.
     * 
     * @return 
     */
    @Override
    public ArrayList<Overlay> getOverlays() {
        ArrayList<Overlay> overlays = new ArrayList<Overlay>();
        
        for (Layer l: this.layers) {
            if (l instanceof VectorLayer) {
                VectorLayer vl = (VectorLayer) l;
                overlays.addAll(vl.getOverlays());
            }
        }
        
        return overlays;
    }
    
    /**
     * Returns the Refresh Interval for this Network Layer.
     * 
     * @return 
     */
    public float getRefreshInterval() {
        return refreshInterval;
    }
    
    /**
     * Creates a Region object with this layer's bounds and a LOD based off of the the Visibility.
     * @return 
     */
    public Region getRegion() {
        LatLonAltBox bounds = this.getBoundary();
        
        float  minLOD = Region.calculateLodFromTileZoom(layerVisibility.getMinTileZoomLevel(), bounds);
        float  maxLOD = Region.calculateLodFromTileZoom(layerVisibility.getMaxTileZoomLevel(), bounds);        
        String regionName = this.getName() + " Region";
        
        return new Region(regionName, bounds, maxLOD, minLOD);
    }
    
    /**
     * Returns the Time Span Begin as a String in the format: yyyy-MM-dd'T'HH:mm:ss
     * 
     * @return 
     */    
    @Override
    public String getTimeSpanBeginString() {
        //TODO: Make this work for all layers
        return super.getTimeSpanBeginString();
    }    
    
    /**
     * Returns the Time Span End as a String in the format: yyyy-MM-dd'T'HH:mm:ss
     * 
     * @return 
     */
    @Override
    public String getTimeSpanEndString() {
        //TODO: Make this work for all layers
        return super.getTimeSpanEndString();
    }    
    
    /**
     * Returns the Visibility object used for this NetworkLayer.
     * 
     * @return 
     */
    public Visibility getVisibility() {
        return this.layerVisibility;
    }
    
    /**
     * Imports a FmXML file into this NetworkLayer
     * 
     * @param file 
     */
    private void importFmXML(File file) {
        try {
            DigitalMap tempMap = FmXmlImporter.openFile(file, null);

            if (nodeMap.isEmpty()) {
                nodeMap = (tempMap.getCoordinateSet());
            } else {            
                //copy coordinates into the parent map.
                for (Coordinate c: tempMap.getCoordinateSet().getAllCoordinates()) {
                    if (c != null) {
                        c.setId(0); //Wipe node ID
                        this.nodeMap.put(c);
                    }
                }
            }

            //Copy all layers into this NetworkLayer.
            for (Layer l: tempMap.getLayers()) 
                this.layers.add(l);

            //Copy Styles over
            for (ColorStyle cs: tempMap.getTheme().getAllStyles()) 
                this.parentMap.getTheme().addStyleElement(cs);
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in NetworkLayer.importFmXML(File) - " + e);
        }
    }
    
    /**
     * Imports a JavaScript file into this NetworkLayer.
     * 
     * @param jsFile 
     */
    private void importJS(File jsFile) {
        try {
            String[]   vars    = JsImporter.getVariableText(ResourceHelper.getTextFromFile(jsFile));                            
            String     tmpStr  = vars[0];
            File       tmpFile = File.createTempFile("jsTemp", "js");
            FileWriter fw      = new FileWriter(tmpFile);
            
            jsFile.deleteOnExit();
            
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(tmpStr);
            
            importJSON(jsFile);            
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in NetworkLayer.importJS(File) - " + e);
        }
    }
    
    /**
     * Imports GeoJSON into this Network Layer.
     * 
     * @param jsonFile 
     */
    private void importJSON(File jsonFile) {
        defaultLayer = new VectorLayer("Json Layer");
        defaultLayer.setParentMap(parentMap);
        layers.add(defaultLayer);
        
        JsonImporter json     = new JsonImporter();
        MapImporter  importer = new MapImporter(json, jsonFile, nodeMap, this, null);
        importer.start();                 
    }
    
    /**
     * Removes a given mapObject from the layer.  It does not change the 
     * parentLayer field of that object.
     * 
     * @param object The object to remove.  Only removes it from this instance,
     *               not from the linked file.
     */
    @Override
    public void removeObject(VectorObject object) {
        for (Layer l: this.layers) {
            if (l instanceof VectorLayer) {
                VectorLayer vl = (VectorLayer) l;
                vl.removeObject(object);
            }
        }
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
        MapObjectList<MapObject> objectList = new MapObjectList<MapObject>();
        
        for (Layer l: this.layers) 
            objectList.addAll(l.selectObjects(range));        
        
        return objectList;
    }    
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    /**
     * Sets the Default class for newly created MapPoints.  
     * The default is Point.
     * 
     * @param newClass 
     */
    public void setDefaultPointClass(String newClass) {
        this.defaultPointClass = newClass;
    }    
    
    /**
     * Sets the list of objects in this Layer, all previous objects will be 
     * removed.
     * 
     * @param objects 
     */
    @Override
    public void setObjectList(VectorObjectList<VectorObject> objects) {
        getDefaultLayer().selectObjects(null);
    }    
    
    /**
     * Changes a specific VectorObject's Z order to the one specified
     *
     * @param   mapObject      mapObject, the object to change the Z order of.
     * @param   zOrder         zOrder, the new Z position for the object.
     * @return  boolean        if the object was found and moved.
     */
    @Override
    public boolean setObjectZOrder(VectorObject mapObject, int zOrder) {
        for (Layer l: this.layers) {
            if (l instanceof VectorLayer) {
                VectorLayer vl = (VectorLayer) l;
                
                if (vl.objects.contains(mapObject)) {
                    vl.setObjectZOrder(mapObject, zOrder);
                    break;
                }
            }
        }
        
        return getDefaultLayer().setObjectZOrder(mapObject, zOrder);
    }    
    
    /**
     * Sets the interval, in seconds that this layer should be refreshed.
     * 
     * @param refreshInterval 
     */
    public void setRefreshInterval(float refreshInterval) {
        this.refreshInterval = refreshInterval;
    }
    
    /**
     * Sets the refresh mode for this NetworkLayer with an int.
     * See static integer for this class for values.
     * 
     * @param refreshMode 
     */
    public void setRefreshMode(int refreshMode) {
        this.refreshMode = refreshMode;
    }
    
    /**
     * Sets the refresh mode for this NetworkLayer with a String.
     * Possible values are: never, onChange, onExpire, onInterval.
     * 
     * @param refreshMode 
     */
    public void setRefreshMode(String refreshMode) {
        if (refreshMode.equalsIgnoreCase("never")) {
            this.refreshMode = NetworkLayer.NEVER;
        } else if (refreshMode.equalsIgnoreCase("onChange")) {
            this.refreshMode = NetworkLayer.ON_CHANGE;
        } else if (refreshMode.equalsIgnoreCase("onExpire")) {
            this.refreshMode = NetworkLayer.ON_EXPIRE;
        } else if (refreshMode.equalsIgnoreCase("onInterval")) {            
            this.refreshMode = NetworkLayer.ON_INTERVAL;
        }
    }
    
    /**
     * Takes a Region with a defined LevelOfDetail and uses it to set the visibility values for 
     * this network layer.
     * 
     * @param region 
     */
    public void setRegion(Region region) {    
        if (region.getLevelOfDetail() != null) {
            //Convert region to Vector Zoom Levels
            LevelOfDetail regionLOD = region.getLevelOfDetail();
            double minVectorZoom = region.calculateVectorZoomLevelForLOD(regionLOD.getMinLodPixels());
            double maxVectorZoom = region.calculateVectorZoomLevelForLOD(regionLOD.getMaxLodPixels());

            //Set the visibility values
            layerVisibility.setMinTileZoomLevel(TileMath.getTileMapZoom((float) minVectorZoom));
            layerVisibility.setMaxTileZoomLevel(TileMath.getTileMapZoom((float) maxVectorZoom));
        } else {
            Logger.log(Logger.ERR, "Error in NetworkLayer.setRegion(Region) - Supplied Region does not contain a LevelOfDetail.");
        }
    }
    
    /**
     * Sets the minimum and maximum visibility for this NetworkLayer.
     * 
     * @param minZoom
     * @param maxZoom
     */
    public void setVisibility(float minZoom, float maxZoom) {
        layerVisibility.setMinTileZoomLevel(minZoom);
        layerVisibility.setMaxTileZoomLevel(maxZoom);
    }
    
    public void updateData() {   
        File            tempFile;        
        int             fileNameStart;
        
        lastUpdate = System.currentTimeMillis(); 
        
        try {
            if (!address.equals("")) {
                tempFile = ResourceHelper.downloadFile(address);                                  
                
                if (tempFile != null) {     
                    if (tempFile.getName().endsWith("csv") || address.endsWith("csv")) {   
                        this.getObjectList().clear(); //Remove all current objects
                        
                        CsvImporter csv = new CsvImporter(tempFile, this.parentMap, this);    
                        csv.setDefaultPointClass(defaultPointClass);
                        csv.start();    
                    } else if (tempFile.getName().endsWith("fmxml")) {
                        importFmXML(tempFile);                        
                    } else if (tempFile.getName().endsWith("geojson")) {
                        importJSON(tempFile);      
                    } else if (tempFile.getName().endsWith("js")) { 
                        //javascript       
                        importJS(tempFile);
                    } else if (tempFile.getName().endsWith("kml")) {                                                        
                        KmlImport   kmlImporter = new KmlImport();
                        MapImporter importer    = new MapImporter(kmlImporter, tempFile, nodeMap, this, null);
                        importer.start();
                    } else if (tempFile.getName().endsWith("kmz")) {
                        fileNameStart = address.lastIndexOf("/");
                        
                        if (fileNameStart >= 0)
                            ResourceHelper.addFilePath(address.substring(0, fileNameStart + 1));
                     
                        KmlImport   kmlImporter = new KmlImport();
                        MapImporter importer    = new MapImporter(kmlImporter, tempFile, nodeMap, this, null);
                        importer.start();                        
                    } else if (tempFile.getName().endsWith("rss")) {
                        GeoRssImporter rss   = new GeoRssImporter();
                        MapImporter importer = new MapImporter(rss, tempFile, nodeMap, this, null);
                        importer.start();                                                
                    } else {
                        Logger.log(Logger.ERR, "Error in NetworkLayer.updateData() - Unsupported Data Type"); 
                    }
                }
            }                   
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in NetworkLayer.updateData() - " + e);
        }
    }    
 
    /**
     * Writes this Layer as KML.
     * 
     * @param xmlWriter 
     */
    @Override
    public void toXML(XmlOutput xmlWriter) {
        try {
            xmlWriter.openTag("NetworkLayer");

            xmlWriter.writeTag("Name",            layerName);
            xmlWriter.writeTag("href",            address);                                                      
            xmlWriter.writeTag("PointClass",      defaultPointClass);
            xmlWriter.writeTag("RefreshInterval", Float.toString(refreshInterval));
            
            if (refreshMode == NetworkLayer.NEVER) {
                xmlWriter.writeTag("RefreshMode", "never");
            } else if (refreshMode == NetworkLayer.ON_CHANGE) {
                xmlWriter.writeTag("RefreshMode", "onChange");
            } else if (refreshMode == NetworkLayer.ON_EXPIRE) {
                xmlWriter.writeTag("RefreshMode", "onExpire");
            } else if (refreshMode == NetworkLayer.ON_INTERVAL) {
                xmlWriter.writeTag("RefreshMode", "onInterval");
            }   
            
            layerVisibility.toXML(xmlWriter);
            
            if (layerDescription != null) {                
                if (!layerDescription.equals("") && !layerDescription.equalsIgnoreCase("null"))
                    xmlWriter.writeTag("description", layerDescription);
            }        
        
            xmlWriter.closeTag("NetworkLayer");
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in NetworkLayer.toXML(XmlWriter) - " + e);
        }
    }   
}
