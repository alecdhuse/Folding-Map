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
package co.foldingmap.mapImportExport;

import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.NodeMap;
import co.foldingmap.map.vector.CoordinateList;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.LineString;
import co.foldingmap.map.vector.MapPoint;
import co.foldingmap.map.vector.Polygon;
import co.scarletshark.geojson.*;
import co.foldingmap.GUISupport.ProgressIndicator;
import co.foldingmap.Logger;
import co.foldingmap.dataStructures.PropertyValuePair;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.Layer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Alec
 */
public class JsonImporter implements FormatImporter {
    
    public JsonImporter() {
        
    }
    
    /**
     * Converts and array of JsonCoordinates to Coordinates used by map package.
     * 
     * @param nodeMap
     * @param jCoordinates
     * @return 
     */
    public static CoordinateList<Coordinate> convertGeoJsonCoordinates(NodeMap nodeMap, JsonCoordinate[] jCoordinates) {
        Coordinate                 coordinate;
        CoordinateList<Coordinate> cList      = new CoordinateList<Coordinate>();
        
        for (JsonCoordinate jCoordinate: jCoordinates) {
            coordinate = new Coordinate((float) jCoordinate.getAltitude(), (float) jCoordinate.getLatitude(), (float) jCoordinate.getLongitude());
            cList.add(coordinate);
            nodeMap.put(coordinate);
        }
        
        return cList;
    }
    
    /**
     * Converts a GeoJsonObject to a VectorObject.
     * GeoJsonObjects are created from the JsonParse class.
     * 
     * @param mapData
     * @param geoObject
     * @return 
     */
    public static VectorObject convertGeoJsonObject(NodeMap nodeMap, GeoJsonObject geoObject) {
        CoordinateList<Coordinate>  cList;
        VectorObject                returnObject;
        
        cList = convertGeoJsonCoordinates(nodeMap, geoObject.getCoordinates());
        
        if (geoObject instanceof JsonLineString) {            
            returnObject = new LineString("JSON LineString", "(Unspecified Linestring)", cList);
        } else if (geoObject instanceof JsonPoint) {
            returnObject = new MapPoint("JSON Point", "(Unspecified Point)", "", cList);
        } else if (geoObject instanceof JsonPolygon) {
            returnObject = new Polygon("JSON Polygon", "(Unspecified Polygon)", cList); 
        } else {
            returnObject = null;
        }
        
        return returnObject;
    }
    
    /**
     * Converts a JsonPair with a valueType of STRING to a PropertyValuePair.
     * If valueType is not a STRING, then a null is returned.
     * 
     * @param pair
     * @return 
     */
    public static PropertyValuePair convertJsonPair(JsonPair pair) {
        PropertyValuePair newPair;
        
        if (pair.getValue().getValueType().equals(JsonValue.STRING)) {
            newPair = new PropertyValuePair(pair.getName(), pair.getValueAsString());
        } else if (pair.getValue().getValueType().equals(JsonValue.BOOLEAN)) {
            newPair = new PropertyValuePair(pair.getName(), pair.getValue().getValue().toString());
        } else if (pair.getValue().getValueType().equals(JsonValue.NUMBER)) {
            newPair = new PropertyValuePair(pair.getName(), pair.getValue().getValue().toString());            
        } else if (pair.getValue().getValueType().equals(JsonValue.NULL)) {
            newPair = new PropertyValuePair(pair.getName(), "null");              
        } else {
            newPair = null;
        }
        
        return newPair;
    }
    
    /**
     * Creates a new VectorLayer in the given DigitalMap with the imported 
     * GeoJSON data.
     * 
     * @param file      The file containing the GeoJSON data.
     * @param mapData   The Map to import the data to.
     */
    public static void importGeoJSON(File file, DigitalMap mapData) {
        VectorLayer layer;

        try {                      
            layer = new VectorLayer("JSON Import");
            
            mapData.addLayer(layer, 0);
            importGeoJSON(file, mapData.getCoordinateSet(), layer);
            
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in JsonImporter.importGeoJSON(File, DigitalMap) - " + e);
        }
    }
    
    /**
     * Imports GeoJSON into a given VectorLayer.
     * 
     * @param file          The file containing the GeoJSON data.
     * @param nodeMap       The Map to import the data to.
     * @param importLayer   The Layer to add the GeoJSON imported objects to.
     */
     public static void importGeoJSON(File file, NodeMap nodeMap, VectorLayer importLayer) {
        ArrayList<PropertyValuePair> pvpPairs;
        JsonObject                   currentObject, propertiesObject, rootObject;
        JsonPair                     featurePair;
        String                       name;
        VectorObject                 vObject;
        
        try {            
            rootObject  = JsonParser.parseFile(file);
            featurePair = rootObject.getPairByName("features");                       
            
            //mapData.addLayer(importLayer, 0);
            
            if (featurePair != null) {
                for (Object obj: featurePair.getValueAsArray()) {
                    currentObject = (JsonObject) obj;
                    name          = null;
                    pvpPairs      = new ArrayList<PropertyValuePair>();                    
                    vObject       = null;
                    
                    for (JsonPair jPair: currentObject.getPairs()) {
                        if (jPair.getName().equalsIgnoreCase("id") && name == null) {
                            name = jPair.getValueAsString();                      
                        } else if (jPair.getName().equalsIgnoreCase("type")) {
                            
                        } else if (jPair.getName().equalsIgnoreCase("properties")) {
                            propertiesObject = jPair.getValueAsObject();
                            
                            for (JsonPair propertiesPair: propertiesObject.getPairs()) {
                                PropertyValuePair pvp = convertJsonPair(propertiesPair);
                                
                                if (pvp != null) { 
                                    if (pvp.getProperty().equalsIgnoreCase("name")) {
                                        name = pvp.getValue();
                                    } else {
                                        pvpPairs.add(pvp);
                                    }
                                }
                            }
                            
                        } else if (jPair.getName().equalsIgnoreCase("geometry")) {
                            GeoJsonObject geoObject = (GeoJsonObject) jPair.getValue().getValue();
                            
                            if (geoObject != null)
                               vObject = convertGeoJsonObject(nodeMap, geoObject);
                        }
                    }
                    
                    if (vObject != null) {
                        vObject.addCustomDataFields(pvpPairs);
                        importLayer.addObject(vObject);
                        
                        if (name != null) 
                            vObject.setName(name);
                    }
                }
            } else {
                Logger.log(Logger.ERR, "Error in JsonImporter.importGeoJSON(File, NodeMap, VectorLayer) - Couldn't find Feature Pair.");
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in JsonImporter.importGeoJSON(File, NodeMap, VectorLayer) - " + e);
        }         
     }

    /**
     * Imports objects from a given map file and adds objects from the map to
     * the given VectorLayer.
     * 
     * @param mapFile           The file containing the map to import.
     * @param nodeMap           The NodeMap to add new Coordinates to.
     * @param layer             The Layer to add imported objects to.
     * @param progressIndicator Optional, to display the progress of the import.
     */     
    @Override
    public void importToLayer(File mapFile, NodeMap nodeMap, Layer layer, ProgressIndicator progressIndicator) throws IOException {
        if (layer instanceof VectorLayer) {
            VectorLayer vecLayer = (VectorLayer) layer;
            vecLayer.getObjectList().clear();        
            importGeoJSON(mapFile, nodeMap, vecLayer);
        } else {
            Logger.log(Logger.ERR, "Error in JsonImporterImporter.importToLayer(File, NodeMap, Layer, ProgressIndicator) - Supplied Layer must be a VectorLayer.");
        }
    }

    @Override
    public DigitalMap importAsMap(File mapFile, ProgressIndicator progressIndicator) throws IOException {
        DigitalMap  mapData;
        VectorLayer newLayer;
        
        mapData  = new DigitalMap();
        newLayer = new VectorLayer("GeoJSON Layer");
        
        mapData.addLayer(newLayer);
        importGeoJSON(mapFile, mapData.getCoordinateSet(), newLayer);
        
        return mapData;
    }
}
