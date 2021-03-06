/* 
 * Copyright (C) 2015 Alec Dhuse
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
package co.foldingmap.mapImportExport;

import co.foldingmap.map.vector.LinearRing;
import co.foldingmap.map.vector.MultiGeometry;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.MapPoint;
import co.foldingmap.map.vector.LineString;
import co.foldingmap.map.vector.Polygon;
import co.scarletshark.geojson.JsonCoordinate;
import co.scarletshark.geojson.JsonObject;
import co.scarletshark.geojson.JsonPair;
import co.scarletshark.geojson.JsonValue;
import co.foldingmap.Logger;
import co.foldingmap.dataStructures.PropertyValuePair;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.Layer;
import co.foldingmap.map.MapObject;
import co.foldingmap.map.Visibility;
import co.foldingmap.map.themes.ColorStyle;
import co.foldingmap.map.themes.IconStyle;
import co.foldingmap.map.vector.CoordinateList;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Alec
 */
public class JsonExporter {
    
    /**
     * Returns the VisibilityObject for an MapObject's style, if there is one.
     * 
     * @param mapData
     * @param object
     * @return 
     */
    public static Visibility getObjectStyleVisibility(DigitalMap mapData, MapObject object) {
        ColorStyle cs  = null;
        Visibility vis = null;
        
        if (object instanceof MapPoint) {
            cs = mapData.getTheme().getIconStyle(((VectorObject)object).getObjectClass());
        } else if (object instanceof LineString) {
            cs = mapData.getTheme().getLineStyle(((VectorObject)object).getObjectClass());                            
        } else if (object instanceof Polygon) {
            cs = mapData.getTheme().getPolygonStyle(((VectorObject)object).getObjectClass());                                         
        }  
        
        if (cs != null) vis = cs.getVisibility();
        
        return vis;
    }    
    
    /**
     * Exports an IconStyle to Leaflet JSON format.
     * 
     * @param iconStyle
     * @return 
     */
    public static JsonObject exportIconStyle(IconStyle iconStyle) {
        Double[]   imgDim = new Double[2];
        Double[]   imgLoc = new Double[2];
        Double[]   popLoc = new Double[2];
        
        JsonObject object;

        object    = new JsonObject();
        imgDim[0] = new Double(iconStyle.getObjectImage().getIconWidth());
        imgDim[1] = new Double(iconStyle.getObjectImage().getIconHeight());
        imgLoc[0] = new Double(iconStyle.getObjectImage().getIconWidth()  / 2f);
        imgLoc[1] = new Double(iconStyle.getObjectImage().getIconHeight() / 2f);
        popLoc[0] = new Double(iconStyle.getObjectImage().getIconWidth()  / 2f);
        popLoc[1] = new Double(iconStyle.getObjectImage().getIconHeight() / 2f);
        
        object.addPair(new JsonPair("iconUrl",     iconStyle.getImageFileName()));
        object.addPair(new JsonPair("iconSize",    new JsonValue(imgDim, JsonValue.ARRAY)));
        object.addPair(new JsonPair("iconAnchor",  new JsonValue(imgLoc, JsonValue.ARRAY)));
        object.addPair(new JsonPair("popupAnchor", new JsonValue(popLoc, JsonValue.ARRAY)));
        
        return object;
    }    
    public static JsonObject exportLinearRing(DigitalMap mapData, LinearRing ring) {
        JsonObject geometryObject, jObject, propertiesObject;
        
        jObject          = new JsonObject();
        propertiesObject = new JsonObject();
        geometryObject   = new JsonObject();          
                
        propertiesObject.addPair(new JsonPair("name", ring.getName()));
        
        geometryObject.addPair(new JsonPair("type", "LineString"));
        geometryObject.addPair(getCoordinatePair(ring));        
        
        jObject.addPair(new JsonPair("type",       "Feature"));
        jObject.addPair(new JsonPair("properties", propertiesObject));
        jObject.addPair(new JsonPair("geometry",   geometryObject));          
        
        return jObject;
    }    
    
    public static JsonObject exportLineString(DigitalMap mapData, LineString line) {
        JsonObject geometryObject, jObject, propertiesObject;
        
        jObject          = new JsonObject();
        propertiesObject = new JsonObject();
        geometryObject   = new JsonObject();          
                
        propertiesObject.addPair(new JsonPair("name", line.getName()));
        
        geometryObject.addPair(new JsonPair("type", "LineString"));
        geometryObject.addPair(getCoordinatePair(line));        
        
        jObject.addPair(new JsonPair("type",       "Feature"));
        jObject.addPair(new JsonPair("properties", propertiesObject));
        jObject.addPair(new JsonPair("geometry",   geometryObject));          
        
        return jObject;
    }
    
    /**
     * Exports a map to a JsonObject
     * @param mapData
     * @return 
     */
    public static JsonObject exportMap(DigitalMap mapData) {
        ArrayList  mapObjects        = new ArrayList();
        JsonObject featureCollection = new JsonObject();
        
        featureCollection.addPair(new JsonPair("type", "FeatureCollection"));
        
        for (Layer l: mapData.getLayers()) {
            if (l instanceof VectorLayer) {
                VectorLayer vl = (VectorLayer) l;
                
                for (VectorObject object: vl.getObjectList()) 
                    mapObjects.add(exportVectorObject(mapData, object));                                
            }
        }
        
        featureCollection.addPair(new JsonPair("features", mapObjects.toArray(new Object[1])));
                
        return featureCollection;
    }
    
    /**
     * Exports a DigitalMap to GeoJSON written to the given file.
     * 
     * @param mapData
     * @param file 
     */
    public static void exportMap(DigitalMap mapData, File file) {
        JsonObject jsonObject = exportMap(mapData);
        
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            String jsonString = jsonObject.toString();
            writer.write(jsonString);
            writer.close();
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in JsonExporter.exportMap(DigitalMap, File) - " + e);
        }
    }
    
    /**
     * Exports a DigitalMap to GeoJSON written to the given file.
     * 
     * @param mapData
     * @param file 
     */
    public static void exportMapForLeaflet(DigitalMap mapData, File file) {
        HashMap<String, ArrayList> classes = new HashMap<String, ArrayList>();       
        JsonObject jsonObject = exportMap(mapData);
        
        try {
            //Convert Vector Object to JsonObjects sorted by their class.
            for (Layer l: mapData.getLayers()) {
                if (l instanceof VectorLayer) {
                    VectorLayer vl = (VectorLayer) l;

                    for (VectorObject object: vl.getObjectList()) {
                        ArrayList list = classes.get(object.getObjectClass());
                        
                        if (list == null) {
                            list = new ArrayList();
                            list.add(exportVectorObject(mapData, object));
                            classes.put(object.getObjectClass(), list);
                        } else {
                            list.add(exportVectorObject(mapData, object));
                        }
                    }
                              
                }
            }            
            
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            
            Set<String> keySet  = classes.keySet();
            Collection  vals    = classes.values();
            Object[]    lists   = vals.toArray();
            String      keys[]  = keySet.toArray(new String[1]);
            
            for (int i = 0; i < lists.length; i++) {
                ArrayList  list   = (ArrayList) lists[i];
                JsonObject object = new JsonObject();
                
                object.addPair(new JsonPair("type", "FeatureCollection"));                
                object.addPair(new JsonPair("features", list.toArray(new JsonObject[1])));
                
                String text = "var " + keys[i].replace(" ", "") + " = " + object.toString() + ";\n\n";
                writer.write(text);
            }
                                    
            writer.close();
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in JsonExporter.exportMapForLeaflet(DigitalMap, File) - " + e);
        }
    }    
    
    public static JsonObject exportMapPoint(DigitalMap mapData, MapPoint point) {
        JsonObject geometryObject, jObject, propertiesObject;
        String     popupContent;
        Visibility styleVis;
        
        jObject          = new JsonObject();
        propertiesObject = new JsonObject();
        geometryObject   = new JsonObject();        
        popupContent     = "<b>" + point.getName() + "</b>" + "<br>" + point.getPopupDescription();
        styleVis         = getObjectStyleVisibility(mapData, point);
        
        propertiesObject.addPair(new JsonPair("name", point.getName()));
        propertiesObject.addPair(new JsonPair("show_on_map", true));
        
        if (popupContent.length() > 0)
            propertiesObject.addPair(new JsonPair("popupContent", popupContent));                
        
        if (point.getVisibility() != null) {
            if (point.getVisibility().getMaxTileZoomLevel() < 25)
                propertiesObject.addPair(new JsonPair("maxZoom", point.getVisibility().getMaxTileZoomLevel()));
            
            if (point.getVisibility().getMinTileZoomLevel() > 0)
                propertiesObject.addPair(new JsonPair("minZoom", point.getVisibility().getMinTileZoomLevel()));
        } else if (styleVis != null) {
            //Export Style Visibility, if it exists and the point does not have a Visibility
            if (styleVis.getMaxTileZoomLevel() < 25)
                propertiesObject.addPair(new JsonPair("maxZoom", styleVis.getMaxTileZoomLevel()));
            
            if (styleVis.getMinTileZoomLevel() > 0)
                propertiesObject.addPair(new JsonPair("minZoom", styleVis.getMinTileZoomLevel()));            
        }
        
        //Add custome data
        for (PropertyValuePair pvp: point.getAllCustomData()) 
            propertiesObject.addPair(new JsonPair(pvp.getProperty(), pvp.getValue()));       
        
        geometryObject.addPair(new JsonPair("type", "Point"));
        geometryObject.addPair(getCoordinatePair(point));
        
        jObject.addPair(new JsonPair("type",       "Feature"));
        jObject.addPair(new JsonPair("properties", propertiesObject));
        jObject.addPair(new JsonPair("geometry",   geometryObject));        
        
        return jObject;
    }
    
    public static JsonObject exportMultiGeometry(DigitalMap mapData, MultiGeometry multi) {
        JsonObject   geometryObject, jObject, propertiesObject;
        JsonObject[] geometries;
        
        jObject          = new JsonObject();
        propertiesObject = new JsonObject();
        geometryObject   = new JsonObject();          
        geometries       = new JsonObject[multi.getComponentObjects().size()];
        
        propertiesObject.addPair(new JsonPair("name", multi.getName()));        
        geometryObject.addPair(new JsonPair("type", "GeometryCollection"));
        
        for (int i = 0; i < multi.getComponentObjects().size(); i++) {
            VectorObject obj = multi.getComponentObjects().get(i);
            geometries[i] = new JsonObject();
            
            if (obj instanceof MapPoint) {
                geometries[i].addPair(new JsonPair("type", "Point"));
            } else if (obj instanceof LineString) {
                geometries[i].addPair(new JsonPair("type", "LineString"));
            } else if (obj instanceof LinearRing) {
                geometries[i].addPair(new JsonPair("type", "LineString"));
            } else if (obj instanceof Polygon) {
                geometries[i].addPair(new JsonPair("type", "Polygon"));
            }                       
         
            geometries[i].addPair(getCoordinatePair(obj));  
        }              
        
        jObject.addPair(new JsonPair("type",       "Feature"));
        jObject.addPair(new JsonPair("properties", propertiesObject));
        jObject.addPair(new JsonPair("geometries", geometryObject));          
        
        return jObject;
    }      
    
    public static JsonObject exportPolygon(DigitalMap mapData, Polygon poly) {
        JsonObject geometryObject, jObject, propertiesObject;
        
        jObject          = new JsonObject();
        propertiesObject = new JsonObject();
        geometryObject   = new JsonObject();          
                
        propertiesObject.addPair(new JsonPair("name", poly.getName()));
        
        geometryObject.addPair(new JsonPair("type", "Polygon"));
        geometryObject.addPair(getCoordinatePair(poly));        
        
        jObject.addPair(new JsonPair("type",       "Feature"));
        jObject.addPair(new JsonPair("properties", propertiesObject));
        jObject.addPair(new JsonPair("geometry",   geometryObject));          
        
        return jObject;
    }    
    
    public static JsonObject exportVectorObject(DigitalMap mapData, VectorObject obj) {
        JsonObject objectJson = new JsonObject();
        
        if (obj instanceof MapPoint) {
            objectJson = exportMapPoint(mapData, (MapPoint) obj);
        } else if (obj instanceof LineString) {
            objectJson = exportLineString(mapData, (LineString) obj);
        } else if (obj instanceof LinearRing) {
            objectJson = exportLinearRing(mapData, (LinearRing) obj);
        } else if (obj instanceof Polygon) {
            objectJson = exportPolygon(mapData, (Polygon) obj);
        } else if (obj instanceof MultiGeometry) {
            objectJson = exportMultiGeometry(mapData, (MultiGeometry) obj);
        } else {
            
        }
        
        return objectJson;
    }
    
    /**
     * Returns a JsonPair containing all the coordinates for an object.
     * 
     * @param  obj A vector object to extract coordinates from.
     * @return The JsonPair containing all the coordinates.
     */
    public static JsonPair getCoordinatePair(VectorObject obj) {
        CoordinateList<Coordinate> coordinateList  = obj.getCoordinateList();
        JsonCoordinate[]           coordinateArray = new JsonCoordinate[coordinateList.size()];
        
        for (int i = 0; i < coordinateList.size(); i++) {
            Coordinate c = coordinateList.get(i);
            coordinateArray[i] = new JsonCoordinate(c.getLongitude(), c.getLatitude(), c.getAltitude());
        }
        
        return new JsonPair("coordinates", (JsonCoordinate[]) coordinateArray);
    }
}
