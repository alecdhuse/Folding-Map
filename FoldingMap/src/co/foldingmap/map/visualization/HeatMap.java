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
package co.foldingmap.map.visualization;

import co.foldingmap.map.MapView;
import co.foldingmap.map.MapObjectList;
import co.foldingmap.map.MapObject;
import co.foldingmap.map.MapPanel;
import co.foldingmap.map.Layer;
import co.foldingmap.map.vector.MultiGeometry;
import co.foldingmap.map.vector.LatLonAltBox;
import co.foldingmap.map.vector.VectorObjectList;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.MapPoint;
import co.foldingmap.map.vector.LineString;
import co.foldingmap.map.vector.Polygon;
import co.foldingmap.map.themes.PolygonStyle;
import co.foldingmap.map.themes.ColorRamp;
import co.foldingmap.map.themes.ColorHelper;
import co.foldingmap.map.themes.ColorStyle;
import co.foldingmap.map.themes.IconStyle;
import co.foldingmap.map.themes.LineStyle;
import co.foldingmap.Logger;
import co.foldingmap.xml.XmlOutput;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.JMenuItem;

/**
 *
 * @author Alec
 */
public class HeatMap extends VisualizationLayer {
    protected ArrayList<String>                 objectRefs;
    protected HeatMapKey                        heatMapKey;
    protected int                               position;
    protected VectorObjectList<VectorObject>    objects;    
    protected MapView                           lastMapView;
    protected String                            colorRampID;
    protected String[]                          variables;    
    
    /**
     * Constructor for the HeatMap Layer.
     * 
     * @param layerName
     *      The name of the layer.
     * 
     * @param objects   
     *      The List of Object to be used when creating the HeatMap, these 
     *      should be present in other layers.
     * 
     * @param hashMap
     *      The HashMap containing the value to color mappings used in this 
     *      HeatMap.
     * 
     * @param variables 
     *      An array of variables used to create this HeatMap.  Multiple 
     *      variables indicates a Time Series.  A single variable indicates 
     *      a static HeatMap.
     * 
     * @param displayInterval
     *      The time in milliseconds to display each HeatMap in the time 
     *      series before transitioning to the next one.
     */
    public HeatMap(String layerName, 
                   VectorObjectList<VectorObject> objects, 
                   String     colorRampID, 
                   String[]   variables,
                   int        displayInterval,
                   HeatMapKey heatMapKey) {
        
        this.displayInterval = displayInterval;
        this.colorRampID     = colorRampID;
        this.objects         = objects;
        this.variables       = variables;
        this.layerName       = layerName;
        this.visible         = true;
        this.position        = 0;
        this.heatMapKey      = heatMapKey;
        
        if (heatMapKey.getPositionReference() == HeatMapKey.NONE) {
            this.showKey = false;
        } else {          
            this.showKey    = true;                
        }
        
        if (this.hasTimeSeries())
            timeControl = new TimeSpanControl(this);
    }
    
    /**
     * Constructor for the HeatMap Layer.
     * 
     * @param layerName
     *      The name of the layer.
     * 
     * @param objects   
     *      The List of Object to be used when creating the HeatMap, these 
     *      should be present in other layers.
     * 
     * @param hashMap
     *      The HashMap containing the value to color mappings used in this 
     *      HeatMap.
     * 
     * @param variables 
     *      An array of variables used to create this HeatMap.  Multiple 
     *      variables indicates a Time Series.  A single variable indicates 
     *      a static HeatMap.
     * 
     * @param displayInterval
     *      The time in milliseconds to display each HeatMap in the time 
     *      series before transitioning to the next one.
     */
    public HeatMap(String layerName, 
                   ArrayList<String> objectRefs, 
                   String            colorRampID, 
                   String[]          variables,
                   int               displayInterval,
                   HeatMapKey        heatMapKey) {
        
        this.displayInterval = displayInterval;
        this.colorRampID     = colorRampID;
        this.objectRefs      = objectRefs;
        this.objects         = null;
        this.variables       = variables;
        this.layerName       = layerName;
        this.visible         = true;
        this.position        = 0;
        this.heatMapKey      = heatMapKey;
        
        if (heatMapKey.getPositionReference() == HeatMapKey.NONE) {
            this.showKey = false;
        } else {          
            this.showKey    = true;                
        }     
        
        if (this.hasTimeSeries())
            timeControl = new TimeSpanControl(this);
    }   
    
    /**
     * Closes this Layer gracefully.
     */
    @Override
    public void closeLayer() {
        
    }
    
    @Override
    public Layer copy() {
        return new HeatMap(layerName, objects, colorRampID, variables, displayInterval, heatMapKey);
    }

    @Override
    public void drawLayer(Graphics2D g2, MapView mapView) {
        Color       color;
        ColorRamp   colorRamp;
        ColorStyle  newStyle;
        int         time;
        LineStyle   lineStyle;
        String      id, value;
        
        try {
            colorRamp = mapView.getMapTheme().getColorRamp(colorRampID);
            
            if (objects == null) {
                //objects not loaded load from object refs
                objects = new VectorObjectList<VectorObject>();
                
                for (String ref: objectRefs) {
                    VectorObject obj = parentMap.getMapObjectFromReference(Long.parseLong(ref));
                    objects.add(obj);
                }
            }
            
            if (this.hasTimeSeries()) {
                time = timeControl.getPosition();
            } else {
                time = 0;
            }
            
            lastMapView = mapView;
            
            if (visible) {
                for (VectorObject object: objects) {
                    value    = object.getCustomDataFieldValue(variables[time]);
                    color    = colorRamp.getColor(value);
                    newStyle = null;

                    if (object instanceof MapPoint) {   
                        id        = "HeatMapPoint-" + value;
                        newStyle  = new IconStyle(id, color);
                    } else if (object instanceof LineString) {  
                        id        = "HeatMapLine-" + value;                    
                        lineStyle = (LineStyle) mapView.getMapTheme().getLineStyle(object.getObjectClass());
                        newStyle  = new LineStyle(id, color, lineStyle.getOutlineColor(), lineStyle.getLineWidth(), lineStyle.getLineStroke());
                    } else if (object instanceof Polygon) {
                        id        = "HeatMapPoly-" + value;
                        newStyle  = new PolygonStyle(id, color, color);
                    } else if (object instanceof MultiGeometry) {

                    }      

                    if (newStyle != null && value != null)
                        object.drawObject(g2, mapView, newStyle);
                }
                
                if (showKey) {
                    //Draw HeatMap key
                    heatMapKey.drawObject(g2, mapView);
                }
            }//end visibility check
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in HeatMap.drawLayer(Graphics2D, MapView) - " + e);
        }
    }

    @Override
    public LatLonAltBox getBoundary() {
        return objects.getBoundary();
    }

    @Override
    public float getCenterLongitude() {
        float center, delta;

        delta  = objects.getEasternMostLongitude() - objects.getWesternMostLongitude();
        center = objects.getWesternMostLongitude() + (float) (delta / 2.0);

        return center;
    }

    @Override
    public float getCenterLatitude() {
        float center, delta;

        delta  = objects.getNorthernMostLatitude() - objects.getSouthernMostLatitude();
        center = objects.getSouthernMostLatitude() + (float) (delta / 2.0);
        
        return center;
    }

    /**
     * Returns the hashMap holding the value to color mappings.
     * 
     * @return 
     */
    public String getColorRampID() {
        return colorRampID;
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
     * Returns HeatMapKey for this HeatMap
     * 
     * @return 
     */
    public HeatMapKey getHeatMapKey() {
        return heatMapKey;
    }
    
    /**
     * Returns the MapObjects used to create the HeatMap
     * 
     * @return 
     */
    public VectorObjectList<VectorObject> getMapObjects() {
        return objects;
    }
    
    /**
     * Returns the number of elements in the variables string array. This will
     * determine how many positions there are in the time series.
     * 
     * @return 
     */
    @Override
    public int getNumberOfSeries() {
        return variables.length;
    }
    
    /**
     * Returns the VectorObject variables used to create this heatMap.
     * 
     * @return 
     */
    public String[] getVariables() {
        return variables;
    }
    
    @Override
    public final boolean hasTimeSeries() {
        if (variables.length > 1) {
            return true;
        } else {
            return false;
        }
    }    
    
    @Override
    public MapObjectList selectObjects(Rectangle2D range) {
        VectorObject                currentObject;
        MapObjectList<MapObject>    newlySelectedObjects;

        newlySelectedObjects = new MapObjectList<MapObject>();
        
        try {
            //search backwards to get objects with higher z-oreder first
            for (int i = (objects.size() - 1); i >= 0; i--) {
                currentObject = objects.get(i);

                if (currentObject.isObjectWithinRectangle(range)) {
                    if (currentObject.isVisible(lastMapView))
                        newlySelectedObjects.add(currentObject);

                    //code for a single mouse click
                    //selection only returns the object closes to the top
                    if ((range.getWidth() == MapPanel.SINGLE_CLICK_WIDTH) && (newlySelectedObjects.size() > 0))
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error in HeatMap.selectObjects(Rectangle2D) - " + e);
        }

        return (newlySelectedObjects);
    }
    
    /**
     * Sets the MapObjects to be used in this HeatMap.
     * 
     * @param objects
     */
    public void setMapObjects(VectorObjectList<VectorObject> objects) {
        this.objects = objects;
    }
    
    /**
     * Sets the HashMap to be used by this HeatMap.
     * 
     * @param hashMap 
     */
    public void setColorRampID(String colorRampID) {
        this.colorRampID = colorRampID;
    }
    
    /**
     * Sets the variables to be used by this HeatMap.
     * 
     * @param variables 
     */
    public void setVariables(String[] variables) {
        this.variables = variables;
    }
    
    @Override
    public void toXML(XmlOutput kmlWriter) {
        StringBuilder   fields, objectRefs;
        
        objectRefs  = new StringBuilder();
        fields      = new StringBuilder();

        kmlWriter.openTag("heatmap");
        
        kmlWriter.writeTag("name",        layerName);
        kmlWriter.writeTag("description", layerDescription);
        
        //variables
        for (String f: variables) {
            fields.append(f);
            fields.append(",");
        }
        
        kmlWriter.openTag("key");
        
        if (heatMapKey.getPositionReference() == HeatMapKey.NONE) {
            kmlWriter.writeTag("Position", "None");
        } else if (heatMapKey.getPositionReference() == HeatMapKey.BOTTOM_LEFT) {
            kmlWriter.writeTag("Position", "Bottom-Left");
        } else if (heatMapKey.getPositionReference() == HeatMapKey.BOTTOM_RIGHT) {    
            kmlWriter.writeTag("Position", "Bottom-Right");
        } else if (heatMapKey.getPositionReference() == HeatMapKey.TOP_LEFT) {
            kmlWriter.writeTag("Position", "Top-Left");
        } else if (heatMapKey.getPositionReference() == HeatMapKey.TOP_RIGHT) {
            kmlWriter.writeTag("Position", "Top-Right");
        }
        
        if (heatMapKey.hasHorizontalOrientation()) {
            kmlWriter.writeTag("Orientation", "Horizontal");
        } else {
            kmlWriter.writeTag("Orientation", "Vertical");
        }
        
        for (Color c: heatMapKey.getColors()) 
            kmlWriter.writeTag("color", ColorHelper.getColorHexStandard(c));        
        
        kmlWriter.closeTag("key");
        
        fields.deleteCharAt(fields.lastIndexOf(","));
        kmlWriter.writeTag("fields", fields.toString());
        
        //colorRamp
        kmlWriter.writeTag("Style", colorRampID);

        //objects           
        for (VectorObject object: objects) {
            objectRefs.append(object.getReference());
            objectRefs.append(",");                        
        }
        
        objectRefs.deleteCharAt(objectRefs.lastIndexOf(","));
        kmlWriter.writeTag("objects", objectRefs.toString());
        
        kmlWriter.closeTag("heatmap");
    }
    
}
