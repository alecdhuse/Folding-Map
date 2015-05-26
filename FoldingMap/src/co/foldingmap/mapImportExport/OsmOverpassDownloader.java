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

import co.foldingmap.GUISupport.ProgressBarPanel;
import co.foldingmap.Logger;
import co.foldingmap.MainWindow;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.vector.LatLonBox;
import co.foldingmap.map.vector.NodeMap;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.VectorObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Connector to download OSM XML from http://www.overpass-api.de/
 * 
 * @author Alec
 */
public class OsmOverpassDownloader extends Thread {
    private ArrayList<OsmImportCondition>   importConditions;
    private ArrayList<VectorObject>         coastlines;
    private HttpURLConnection               osmConnection;
    private DigitalMap                      mapData;
    private LatLonBox                       bounds;
    private MainWindow                      mainWindow;
    private NodeMap                         nodeMap;
    private ProgressBarPanel                progressPanel;  
    
    public OsmOverpassDownloader(MainWindow mainWindow, DigitalMap mapData, LatLonBox bounds) {
        this.mainWindow       = mainWindow;
        this.mapData          = mapData;       
        this.bounds           = bounds;
        this.importConditions = new ArrayList<OsmImportCondition>();
        this.nodeMap          = mapData.getCoordinateSet();
        this.coastlines       = new ArrayList<VectorObject>();
                
        if (nodeMap == null) {
            nodeMap = new NodeMap();
            mapData.setCoordinateSet(nodeMap);
        }
    }
    
    /**
     * Download data from the Overpass API
     * 
     * @param importLayer 
     */
    public void downloadData(VectorLayer importLayer) {
        boolean                         nodeStarted, wayStarted, relationStarted;
        BufferedReader                  br;
        HashMap<String, OsmNode>        nodes;
        HashMap<String, VectorObject>   objectIDs;
        int                             idEnd, idStart, linesRead;
        VectorObject                    currentObject;
        OsmNode                         osmNode;
        OsmRelation                     osmRelation;
        String                          address, apiURL, bounding, importType, line, tags;
        String                          north, south, east, west;
        StringBuffer                    nodeXML, wayXML;
        URL                             url;
                
        north      = Float.toString(bounds.getNorth());
        south      = Float.toString(bounds.getSouth());
        east       = Float.toString(bounds.getEast());
        west       = Float.toString(bounds.getWest());        
        
        apiURL     = "http://www.overpass-api.de/api/xapi?";        
        bounding   = "[bbox=" + west + "," + south + "," + east + "," + north + "]";                
           
        if (importConditions.isEmpty()) 
            importConditions.add(new OsmImportCondition("Any", "Any", "Any"));
        
        for (OsmImportCondition condition: importConditions) {
            
            //Set the condition object type
            if (condition.getObjectType().equalsIgnoreCase("Any")) {
                importType = "*";
            } else if (condition.getObjectType().equalsIgnoreCase("Nodes")) {
                importType = "node";                    
            } else if (condition.getObjectType().equalsIgnoreCase("Ways")) {
                importType = "way";
            } else if (condition.getObjectType().equalsIgnoreCase("Areas")) {
                importType = "area";       
            } else if (condition.getObjectType().equalsIgnoreCase("Relations")) {
                importType = "relation";                    
            } else {
                importType = condition.getObjectType();
            }
            
            //create tags
            if (!condition.getKey().equalsIgnoreCase("Any")) {
                if (condition.getValue().equalsIgnoreCase("Any")) {
                    tags = "[" + condition.getKey() + "=*]";
                } else if (condition.getValue().equalsIgnoreCase("Any")) {
                    tags = "[" + condition.getKey() + "=*]";
                } else {
                    tags = "[" + condition.getKey() + "=" + condition.getValue() + "]";
                }
            } else {
                tags = "";
            }
            
            //Create the address
            address = apiURL + importType + bounding + tags;
            
            try {
                line            = "";
                linesRead       = 0;
                url             = new URL(address);            
                osmConnection   = (HttpURLConnection) url.openConnection();
                nodes           = new HashMap<String, OsmNode>();
                objectIDs       = new HashMap<String, VectorObject>();
                osmRelation     = null;
                nodeXML         = new StringBuffer();
                wayXML          = new StringBuffer();
                nodeStarted     = false; 
                wayStarted      = false;
                relationStarted = false;

                osmConnection.setReadTimeout(180*1000);
                osmConnection.connect();

                br = new BufferedReader(new InputStreamReader(osmConnection.getInputStream()));

                while (!line.contains("</osm>")) {
                    while (br.ready()) {                
                        line = br.readLine().trim();
                        linesRead++;
                        progressPanel.updateProgress("Downloading... " + Integer.toString(linesRead), 25);

                        if (line.startsWith("<node") && !line.endsWith("/>")) {
                            nodeXML     = new StringBuffer();
                            nodeStarted = true;
                            nodeXML.append(line);
                            nodeXML.append("\n");
                        } else if (line.startsWith("<node") && line.endsWith("/>")) {
                            osmNode = OsmImporter.getOsmNode(line);
                            nodes.put(osmNode.getNodeID(), osmNode);   
                            nodeMap.put(Long.parseLong(osmNode.getNodeID()), osmNode.getNodeCoordinate());
                        } else if (line.startsWith("</node>")) {
                            nodeXML.append(line);
                            osmNode     = OsmImporter.getOsmNode(nodeXML.toString());  
                            nodeStarted = false;

                            nodes.put(osmNode.getNodeID(), osmNode); 
                            nodeMap.put(Long.parseLong(osmNode.getNodeID()), osmNode.getNodeCoordinate());
                            
                            if (osmNode.hasNameTag()) {
                                currentObject = OsmImporter.getOsmPoint(osmNode, nodeMap);
                                importLayer.addObject(currentObject);  
                                objectIDs.put(currentObject.getCustomDataFieldValue("OsmID"), currentObject);
                                mainWindow.repaint();
                            }
                        } else if (line.startsWith("<way ")) {
                            wayXML     = new StringBuffer();
                            wayStarted = true;

                            wayXML.append(line);
                            wayXML.append("\n");     
                        } else if (line.startsWith("</way>")) {
                            wayXML.append(line);   
                            currentObject = OsmImporter.getOsmWay(wayXML.toString(), nodeMap);             
                            
                            if (currentObject != null) {
                                if (currentObject.getObjectClass().equalsIgnoreCase("Coastline")) {
                                    coastlines.add(currentObject);
                                }

                                objectIDs.put(currentObject.getCustomDataFieldValue("OsmID"), currentObject);                                                        
                                importLayer.addObject(currentObject);      
                            } else {
                                Logger.log(Logger.ERR, "Could Not Covert OSM Way - " + wayXML.toString());                            
                            }
                            
                            mainWindow.repaint();
                            wayStarted = false; 
                        } else if (line.startsWith("<relation ")) {
                            relationStarted = true;
                            idStart         = line.indexOf("id=") + 4;
                            idEnd           = line.indexOf("\"", idStart);
                            osmRelation     = new OsmRelation(Long.parseLong(line.substring(idStart, idEnd)));                                                                   
                        } else if (line.startsWith("</relation>")) {                         
                            relationStarted = false;
                            OsmImporter.processRelation(objectIDs, importLayer, osmRelation);
                        } else {
                            if (nodeStarted) {
                                nodeXML.append(line);
                                nodeXML.append("\n");      
                            } else if (wayStarted) {
                                wayXML.append(line);
                                wayXML.append("\n");     
                            } else if (relationStarted) {
                                if (line.startsWith("<member ")) {
                                    osmRelation.addMember(OsmImporter.getOsmMember(line));
                                } else if (line.startsWith("<tag ")) {
                                    osmRelation.addProperty(OsmImporter.getOsmTag(line));                                    
                                }
                            }
                        }

                    } //end of osm loop                                
                }

                //OsmImporter.mergeCoastlines(importLayer, coastlines); //removed, causing issue with relations.
            } catch (Exception e) {
                System.err.println("Error in OsmOverpassDownloader.downloadData() - " + e);  
                progressPanel.setError(e.toString());
                //TODO: Put a popup if there is an error
            }         
        }
    }    
    
    @Override
    public void run() {
        VectorLayer         importLayer;
                
        progressPanel = mainWindow.getProgressBarPanel();
        importLayer   = new VectorLayer("OSM Import");  
        
        progressPanel.updateProgress("Contacting OSM Server", 10);     
        progressPanel.setVisible(true);
        mapData.addLayer(importLayer, 0);
        mainWindow.updateLayersTree();
        downloadData(importLayer);                                                            
                
        importLayer.getObjectList().sortByLayer();    
        
        progressPanel.updateProgress("OSM Import Complete", 100);  
        progressPanel.finish();       
    }    
    
    /**
     * Sets the import conditions for this download
     * @param importConditions 
     */
    public void setImportConditions(ArrayList<OsmImportCondition> importConditions) {
        this.importConditions = importConditions;
    }
}
