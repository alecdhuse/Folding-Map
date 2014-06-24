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
import co.foldingmap.map.vector.LatLonBox;
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
        LatLonBox               bounds;
        MapProjection           mapProjection;
        NodeMap                 nodeMap;
        String                  keyString, valueString, viewInfo;
        XmlWriter               xmlWriter;
        
        try {
            xmlWriter     = new XmlWriter(fileOut);        
            mapProjection = mapData.getLastMapView().getMapProjection();
            bounds        = mapData.getCoordinateSet().getBounds();
            
            //write xml header stuff
            xmlWriter.writeText("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");        
            xmlWriter.openTag("fmxml xmlns=\"http://www.foldingmap.co/fmxml/\"");

            xmlWriter.openTag("document");
            xmlWriter.writeTag("name",        mapData.getName());
            xmlWriter.writeTag("description", mapData.getMapDescription());            
            xmlWriter.writeTag("view",        mapProjection.getViewInfo());
            
            //Write out map bounds, used in linked/network files.
            xmlWriter.openTag("bounds");
            bounds.toXML(xmlWriter);
            xmlWriter.closeTag("bounds");
                        
            if (mapProjection instanceof MercatorProjection) {
                xmlWriter.writeTag("projection", "Mercator");
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
            xmlWriter.openTag("nodes");                

            for (int i = 1; i <= nodeMap.size(); i++) {
                c = nodeMap.getFromIndex(i);
                
                if (c != null) {
                    keyString   = Long.toString(c.getID());                            
                    valueString = c.toString();

                    xmlWriter.writeTag("node id=\"" + keyString + "\"", valueString);
                }
            }

            xmlWriter.closeTag("nodes");         
            
            //TEMP until CSS is up
            xmlWriter.openTag("mapstyle");
            mapData.getTheme().toXML(xmlWriter);
            xmlWriter.closeTag("mapstyle");
            
            //Write Layers
            xmlWriter.openTag("layers");
            
            for (Layer currentLayer: mapData.getLayers()) 
                currentLayer.toXML(xmlWriter);                        
            
            xmlWriter.closeTag("layers");
            
            xmlWriter.closeTag("document");

            //write xml close
            xmlWriter.closeTag("fmxml");
            
            xmlWriter.closeFile();
        } catch (Exception e) {
            System.err.println("Error in FmXmlExporter.export(DigitalMap, File) - " + e);
        }
    }
}
