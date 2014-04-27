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

import co.foldingmap.map.MapProjection;
import co.foldingmap.map.MapObjectList;
import co.foldingmap.map.MapObject;
import co.foldingmap.map.MercatorProjection;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.Layer;
import co.foldingmap.map.vector.NetworkLayer;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.NodeMap;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.xml.XmlWriter;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Alec
 */
public class FmXmlExporter {
    
    
    public static void export(DigitalMap mapData, File fileOut) {
        ArrayList<Coordinate>   coordinates;
        Coordinate              c;        
        MapProjection           mapProjection;
        NodeMap                 nodeMap;
        String                  keyString, valueString, viewInfo;
        XmlWriter               kmlWriter;
        
        try {
            kmlWriter     = new XmlWriter(fileOut);        
            mapProjection = mapData.getLastMapView().getMapProjection();
                    
            //write xml header stuff
            kmlWriter.writeText("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");        
            kmlWriter.openTag("fmxml xmlns=\"http://www.foldingmap.co/fmxml/\"");

            kmlWriter.openTag("document");
            kmlWriter.writeTag("name",        mapData.getName());
            kmlWriter.writeTag("description", mapData.getMapDescription());
            kmlWriter.writeTag("view",        mapProjection.getViewInfo());
            
            if (mapProjection instanceof MercatorProjection) {
                kmlWriter.writeTag("projection", "Mercator");
            }
            
            //Get Nodes to write
            coordinates = new ArrayList<Coordinate>();            
            VectorObject vObject;
            
            //Get a list of all the Map Objects
            MapObjectList<MapObject> mapObjects = new MapObjectList<MapObject>();
            
            for (Layer l: mapData.getLayers()) {
                if (l instanceof VectorLayer && !(l instanceof NetworkLayer)) {
                    VectorLayer vl = (VectorLayer) l;
                    mapObjects.addAll(vl.getObjectList());
                }
            }
            
            //Get a list of all the Coordinates
            for (MapObject object: mapObjects) {                
                if (object instanceof VectorObject) {
                    vObject = (VectorObject) object;
                    coordinates.addAll(vObject.getCoordinateList());                                        
                }
            }
            
            //Add coordinates to the NodeMap
            nodeMap = new NodeMap(coordinates.size() + 1);
            
            for (Coordinate cord: coordinates) 
                nodeMap.put(cord);                                            
                     
            //Write Nodes
            kmlWriter.openTag("nodes");                

            for (int i = 1; i <= nodeMap.size(); i++) {
                c = nodeMap.getFromIndex(i);
                
                if (c != null) {
                    keyString   = Long.toString(c.getID());                            
                    valueString = c.toString();

                    kmlWriter.writeTag("node id=\"" + keyString + "\"", valueString);
                }
            }

            kmlWriter.closeTag("nodes");         
            
            //TEMP until CSS is up
            kmlWriter.openTag("mapstyle");
            mapData.getTheme().toXML(kmlWriter);
            kmlWriter.closeTag("mapstyle");
            
            //Write Layers
            kmlWriter.openTag("layers");
            
            for (Layer currentLayer: mapData.getLayers()) 
                currentLayer.toXML(kmlWriter);                        
            
            kmlWriter.closeTag("layers");
            
            kmlWriter.closeTag("document");

            //write xml close
            kmlWriter.closeTag("fmxml");
            
            kmlWriter.closeFile();
        } catch (Exception e) {
            System.err.println("Error in FmXmlExporter.export(DigitalMap, File) - " + e);
        }
    }
}
