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

import co.foldingmap.map.vector.LinearRing;
import co.foldingmap.map.vector.Region;
import co.foldingmap.map.vector.MultiGeometry;
import co.foldingmap.map.vector.LatLonAltBox;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.NodeMap;
import co.foldingmap.map.vector.CoordinateMath;
import co.foldingmap.map.vector.CoordinateList;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;
import co.foldingmap.map.vector.LineString;
import co.foldingmap.map.vector.Polygon;
import co.foldingmap.map.vector.LatLonBox;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.MapPoint;
import co.foldingmap.map.vector.InnerBoundary;
import co.foldingmap.GUISupport.ProgressBarPanel;
import co.foldingmap.GUISupport.ProgressIndicator;
import co.foldingmap.GUISupport.Updateable;
import co.foldingmap.Logger;
import co.foldingmap.ResourceHelper;
import co.foldingmap.dataStructures.PropertyValuePair;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.MapProjection;
import co.foldingmap.xml.XMLTag;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * This class is used to import OpenStreetMap.org XML files.  These files are
 * usually downloaded from the OpenStreetMap.org website via the export option.
 * 
 * @author Alec
 */
public class OsmImporter extends Thread {
    
    private DigitalMap          mapData;
    private File                osmFile;
    private float               minlat, minlon, maxlat, maxlon;
    private ProgressIndicator   progressIndicator;
    private Updateable          updateable;

    public OsmImporter(DigitalMap        mapData, 
                       File              osmFile, 
                       Updateable        updateable, 
                       ProgressIndicator progressIndicator) {
        
        super("OsmImporter");

        this.mapData           = mapData;
        this.osmFile           = osmFile;
        this.updateable        = updateable;
        this.progressIndicator = progressIndicator;
    }    
    
    /**
     * Creates a KML region based on the OSM dataLevel
     * This is experimental and not yet finished.
     * 
     * @param dataLevel
     * @return
     */
    public Region createRegion(int dataLevel) {
        float           maxLevelOfDetailPixels, minLevelOfDetailPixels;
        LatLonAltBox    bounds;
        Region          newRegion;
        String          regionName;

        newRegion = new Region("Default Region");
        
        try {
            bounds      = new LatLonAltBox(minlat, maxlat, maxlon, minlon, 0, 10000000);
            regionName  = "OSM Region For Data Level: " + dataLevel;

            switch (dataLevel) {
                case 1:
                    maxLevelOfDetailPixels = 100000000;
                    minLevelOfDetailPixels = 600;
                    break;
                default:
                    maxLevelOfDetailPixels = 100000000;
                    minLevelOfDetailPixels = 1;
            }

            newRegion = new Region(regionName, bounds, maxLevelOfDetailPixels, minLevelOfDetailPixels);
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in OsmImporter.createRegion(int) - " + e);
        }
        
        return newRegion;
    }    
    
    /**
     * Creates an OsmMember object from the OSM XML member tag.
     * Returns null if a member could not be created.
     * 
     * @param memberXML
     * @return 
     */
    public static OsmMember getOsmMember(String memberXML) {
        int       start, end;
        OsmMember newMember;
        String    ref, role, type;
        
        newMember = null;
        
        try {
            //get member type
            start = memberXML.indexOf("type=") + 6;
            end   = memberXML.indexOf("\"", start);
            type  = memberXML.substring(start, end);

            //get ref ID
            start = memberXML.indexOf("ref=") + 5;
            end   = memberXML.indexOf("\"", start);
            ref   = memberXML.substring(start, end);        

            //get role
            start = memberXML.indexOf("role=") + 6;
            end   = memberXML.indexOf("\"", start);
            role  = memberXML.substring(start, end);              

            newMember = new OsmMember(type, ref, role);
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in OsmImporter.getOsmMember(String) - " + e);
        }
        
        return newMember;
    }
    
    /**
     * Extracts a specific property from an Open Street Map xlm string
     *
     * @param   nodeString    The XML string.
     * @param   nodeProperty  The name of the property being retrieved.
     * @return  String        The property value.
     */
    public static String getOsmNodeProperty(String nodeString, String nodeProperty) {
        int     tabPropertyStart;
        int     tabPropertyEnd   = -1;
        String  propertyValue    = "";

        try {
            tabPropertyStart = nodeString.indexOf(nodeProperty + "=");

            if (tabPropertyStart >= 0) {
                tabPropertyStart += (nodeProperty.length() + 2);
                tabPropertyEnd    = nodeString.indexOf("\"", tabPropertyStart);
            }

            if ((tabPropertyEnd == -1) && (tabPropertyStart >= 0))
                tabPropertyEnd = nodeString.indexOf("'", tabPropertyStart);

            if ((tabPropertyEnd >= 0) && (tabPropertyStart >= 0))
                propertyValue = nodeString.substring(tabPropertyStart, tabPropertyEnd);       
            
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in OsmImporter.getOsmNodeProperty(String, String) - " + e);
        }

        return propertyValue;
    }    
    
    /**
     * Returns a single OsmNode object given the node XML.
     * 
     * @param nodeXML
     * @return 
     */
    public static OsmNode getOsmNode(String nodeXML) {
        ArrayList<PropertyValuePair>    tagInfo;
        Coordinate                      nodeCoordinate;
        int                             offset;
        int                             idStart, latStart, lonStart, tagStart;
        int                             idEnd, latEnd, lonEnd, tagEnd;
        PropertyValuePair               currentTag;
        String                          id, lat, lon, tag;
        
        offset   = 0;
        tagStart = 0;
        tagInfo  = new ArrayList<PropertyValuePair>();
        
        try {                       
            idStart  = nodeXML.indexOf("id=")  + 4;
            idEnd    = nodeXML.indexOf(" ", idStart) - 1;
            id       = nodeXML.substring(idStart, idEnd);

            latStart = nodeXML.indexOf("lat=") + 5;
            latEnd   = nodeXML.indexOf("\"", latStart);
            
            if (latEnd == -1)
                latEnd = nodeXML.indexOf("'", latStart);
            
            lat      = nodeXML.substring(latStart, latEnd);

            lonStart = nodeXML.indexOf("lon=") + 5;
            lonEnd   = nodeXML.indexOf("\"", lonStart);
            
            if (lonEnd == -1)
                lonEnd = nodeXML.indexOf("'", lonStart);            
            
            lon      = nodeXML.substring(lonStart, lonEnd);

            nodeCoordinate = new Coordinate(0, Float.parseFloat(lat), Float.parseFloat(lon), Long.parseLong(id));
            
            nodeCoordinate.setId(Long.parseLong(id));
            
            while (tagStart != -1) {
                tagStart = nodeXML.indexOf("<tag", offset);        
                tagEnd   = nodeXML.indexOf("/>", tagStart) + 2;

                if (tagStart >= 0 && tagEnd > 0) {
                    tag        = nodeXML.substring(tagStart, tagEnd);
                    currentTag = getOsmTag(tag);
                    tagInfo.add(currentTag);
                    
                    //set elevation if available
                    if (currentTag.getProperty().equalsIgnoreCase("ele")) {
                        try {
                            nodeCoordinate.setAltitude(Float.parseFloat(currentTag.getValue()));
                        } catch (Exception e) {
                            Logger.log(Logger.WARN, "Could not read elevation for node id: " + id);
                        }
                    }
                }
                
                offset = tagEnd;
            }

            return new OsmNode(id, nodeCoordinate, tagInfo);
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in OsmImporter.getOsmNode(String) - " + e);
            return null;
        }
    }    
    
    /**
     * Loads Open Street Map nodes into a HashMap.  Node IDs are used as keys,
     * while a Coordinate Object is used as the value.
     *
     * @param nodeXML
     * @param progressPanel Panel to update progress, can be null.
     * @return  HashMap     The HashMap containing the loaded nodes.
     */
    public static HashMap<String, OsmNode> getOsmNodes(String nodeXML, ProgressBarPanel progressPanel) {
        double                          percentDone;
        HashMap<String, OsmNode>        coordinateNodes;
        int                             nodeStart, nodeEnd, nodeTagEnd, offset;       
        OsmNode                         newNode;
        String                          nodeText;
        
        //initilize object variables
        offset          = 0;
        coordinateNodes = new HashMap<String, OsmNode>(10000);

        try {
            while (offset < nodeXML.length()) {
                percentDone   = (((double) offset) / ((double) nodeXML.length())) * 100;
                nodeStart     = nodeXML.indexOf("<node id=", offset);
                nodeEnd       = nodeXML.indexOf("/>",        nodeStart);
                nodeTagEnd    = nodeXML.indexOf("</node>",   nodeStart) + 7;

                if (percentDone > 0 && progressPanel != null)
                    progressPanel.updateProgress("Loading OSM Nodes", (int) (percentDone - 21));

                if ((nodeStart >= 0) && (nodeEnd > nodeStart)) {
                    nodeText = nodeXML.substring(nodeStart, nodeTagEnd);
                } else {
                    //no more node tags
                    nodeText = null;
                    offset   = nodeXML.length();
                }
                
                newNode = getOsmNode(nodeText);
                offset  = nodeTagEnd;

                if (newNode != null) {
                    coordinateNodes.put(newNode.getNodeID(), newNode);                
                }
            } //end while parse statment
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in OsmImporter.getOsmNodes(String, ProgressBarPanel) - " + e);
        }

        return coordinateNodes;
    }
 
    /**
     * Creates a MapPoint from an OsmNode.
     * 
     * @param node
     * @param nodeMap
     * @return 
     */
    public static MapPoint getOsmPoint(OsmNode node, NodeMap nodeMap) {
        ArrayList<PropertyValuePair> nodeTags;
        Coordinate                   pointCoordinate;
        HashMap<String,String>       customFields;
        long                         nodeID;
        MapPoint                     newPoint;
        String                       name, key, value;
        
        try {
            customFields    = new HashMap<String,String>();
            name            = node.getNodeID();
            nodeTags        = node.getNodeTags();
            nodeID          = Long.parseLong(node.getNodeID());
            pointCoordinate = nodeMap.get(nodeID);
                    
            if (pointCoordinate == null) {
                Logger.log(Logger.ERR, "Could not retrieve node ID: " + node.getNodeID() + " from Node Map.");
                pointCoordinate = node.getNodeCoordinate();
            }
            
            //Replace HTML safe text with normal text.
            name = XMLTag.convertSafeText(name);
            
            //add a tag to say it came from OSM
            key   = "DataSource";
            value = "OpenStreetMap.org";
            customFields.put(key, value);

            for (PropertyValuePair currentPair: nodeTags) {
                if (currentPair.getProperty().equalsIgnoreCase("Name")) {
                    name  = currentPair.getValue();
                } else {
                    key   = currentPair.getProperty();
                    value = currentPair.getValue();
                    customFields.put(key, value);                  
                }
            }

            newPoint = new MapPoint(name, pointCoordinate, customFields);

            //apply theme
            if (customFields.containsKey("aeroway")) {
                value = customFields.get("aeroway");
                
                if (value.equalsIgnoreCase("aerodrome")) {
                    newPoint.setClass("Airport");
                } else if (value.equalsIgnoreCase("aeroway")) {
                    newPoint.setClass("Airport");
                }
            } else if (customFields.containsKey("amenity")) {
                value = customFields.get("amenity");

                if (value.equalsIgnoreCase("arts_centre")) {
                    newPoint.setClass("Art Gallery");
                } else if (value.equalsIgnoreCase("bank")) {
                    newPoint.setClass("Bank");
                } else if (value.equalsIgnoreCase("bar")) {
                    newPoint.setClass("Bar");       
                } else if (value.equalsIgnoreCase("bus_station")) {
                    newPoint.setClass("Bus Station");                         
                } else if (value.equalsIgnoreCase("cafe")) {
                    newPoint.setClass("Cafe");  
                } else if (value.equalsIgnoreCase("cinema")) {
                    newPoint.setClass("Cinema"); 
                } else if (value.equalsIgnoreCase("courthouse")) {
                    newPoint.setClass("Courthouse");
                } else if (value.equalsIgnoreCase("fast_food")) {
                    newPoint.setClass("Restaurant - Fast Food");    
                } else if (value.equalsIgnoreCase("fire_station")) {
                    newPoint.setClass("Fire Station"); 
                } else if (value.equalsIgnoreCase("fuel")) {
                    newPoint.setClass("Gas Station");
                } else if (value.equalsIgnoreCase("grave_yard")) {
                    newPoint.setClass("Gas Station");
                } else if (value.equalsIgnoreCase("hospital")) {                    
                    newPoint.setClass("Hospital");
                } else if (value.equalsIgnoreCase("library")) {
                    newPoint.setClass("Library");
                } else if (value.equalsIgnoreCase("place_of_worship")) {
                    if (customFields.containsKey("religion")) {
                        value = customFields.get("religion");  
                        
                        if (value.equalsIgnoreCase("christian")) {
                            newPoint.setClass("Place Of Worship - Christian");
                        } else if (value.equalsIgnoreCase("hindu")) {
                            newPoint.setClass("Place Of Worship - Hindu");
                        } else if (value.equalsIgnoreCase("islam")) {
                            newPoint.setClass("Place Of Worship - Islam");
                        } else if (value.equalsIgnoreCase("jewish")) {
                            newPoint.setClass("Place Of Worship - Jewish");
                        } else {
                            newPoint.setClass("Place Of Worship");
                        }
                    }
                } else if (value.equalsIgnoreCase("pharmacy")) {
                    newPoint.setClass("Pharmacy");                       
                } else if (value.equalsIgnoreCase("police")) {
                    newPoint.setClass("Police Station");     
                } else if (value.equalsIgnoreCase("post_office")) {
                    newPoint.setClass("Post Office");                    
                } else if (value.equalsIgnoreCase("pub")) {
                    newPoint.setClass("Bar");                    
                } else if (value.equalsIgnoreCase("restaurant")) {
                    newPoint.setClass("Restaurant");
                } else if (value.equalsIgnoreCase("school")) {
                    newPoint.setClass("School");
                } else if (value.equalsIgnoreCase("university")) {
                    newPoint.setClass("University");                    
                } else {
                    newPoint.setClass("Amenity");
                }
            } else if (customFields.containsKey("building")) {  
                value = customFields.get("building");
                
                if (value.equalsIgnoreCase("yes")) {
                    newPoint.setClass("Building");                      
                }
            } else if (customFields.containsKey("bus")) {   
                value = customFields.get("bus");
                
                if (value.equalsIgnoreCase("yes")) 
                    newPoint.setClass("Bus Station"); 
            } else if (customFields.containsKey("landuse")) { 
                value = customFields.get("landuse");
                
                if (value.equalsIgnoreCase("mine")) {
                    newPoint.setClass("Mine");              
                }
            } else if (customFields.containsKey("leisure")) {    
                value = customFields.get("leisure");
                
                if (value.equalsIgnoreCase("park")) {
                    newPoint.setClass("Park");     
                } else if (value.equalsIgnoreCase("playground")) {
                    newPoint.setClass("Park");    
                }
            } else if (customFields.containsKey("natural")) {
                value = customFields.get("natural");
                
                if (value.equalsIgnoreCase("peak")) {
                    newPoint.setClass("Mountain Peak");
                }
            } else if (customFields.containsKey("highway")) {
                value = customFields.get("highway");
                
                if (value.equalsIgnoreCase("bus_stop")) {
                    newPoint.setClass("Bus Station");
                }                
            } else if (customFields.containsKey("historic")) {
                value = customFields.get("historic");
                
                if (value.equalsIgnoreCase("memorial")) {
                    newPoint.setClass("Memorial");
                }      
            } else if (customFields.containsKey("man_made")) {    
                value = customFields.get("man_made");
                
                if (value.equalsIgnoreCase("tower")) {
                    newPoint.setClass("Antenna");
                }
            } else if (customFields.containsKey("parking")) {
                value = customFields.get("parking");
                
                if (value.equalsIgnoreCase("multi-storey")) {
                    newPoint.setClass("Parking Garage");
                } else {
                    newPoint.setClass("Parking");
                }                
            } else if (customFields.containsKey("place")) {
                value = customFields.get("place");
                
                if (value.equalsIgnoreCase("city")) {
                    newPoint.setClass("City");
                } else if (value.equalsIgnoreCase("hamlet")) {
                    newPoint.setClass("Place - Village");
                } else if (value.equalsIgnoreCase("suburb")) {
                    newPoint.setClass("Place - Suburb");
                } else if (value.equalsIgnoreCase("town")) {
                    newPoint.setClass("Place - Town");
                } else if (value.equalsIgnoreCase("village")) {
                    newPoint.setClass("Place - Village");
                }                
            } else if (customFields.containsKey("railway")) {    
                value = customFields.get("railway");
                
                if (value.equalsIgnoreCase("stop")) {
                    newPoint.setClass("Railway Stop");
                } else if (value.equalsIgnoreCase("tram_stop")) {
                    newPoint.setClass("Railway Stop");
                }
            } else if (customFields.containsKey("shop")) {
                value = customFields.get("shop");
                
                if (value.equalsIgnoreCase("supermarket")) {
                    newPoint.setClass("Super Market");
                } else {
                    newPoint.setClass("Shop");
                }  
            } else if (customFields.containsKey("tourism")) {
                if (value.equalsIgnoreCase("hotel")) {
                    newPoint.setClass("Hotel");
                } else {                
                    newPoint.setClass("Tourist Attraction");
                }
            }
            
            return newPoint;
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in OsmImporter.getOsmPoint(OsmNode) - " + e);
        }
        
        return null;
    }    
    
    /**
     * Creates a PropertyValuePair from an OSM XML String of a tag.
     * 
     * @param tagXMLString
     * @return
     * @throws ParseException 
     */
    public static PropertyValuePair getOsmTag(String tagXMLString) throws ParseException {
        int               keyStart, keyEnd, valueStart, valueEnd;
        PropertyValuePair returnData;
        String            property, value;

        returnData = null;

        try {
            //find the key aka property
            keyStart = tagXMLString.indexOf("k=", 0) + 3;
            keyEnd   = tagXMLString.indexOf("\"", keyStart);

            if (keyEnd < 0)
                keyEnd = tagXMLString.indexOf("'", keyStart);

            property = tagXMLString.substring(keyStart, keyEnd);

            //find the value
            valueStart = tagXMLString.indexOf("v=", 0) + 3;
            valueEnd   = tagXMLString.indexOf("\"", valueStart);

            if (valueEnd < 0)
                valueEnd = tagXMLString.indexOf("'", valueStart);

            value = tagXMLString.substring(valueStart, valueEnd);

            returnData = new PropertyValuePair(property, value);
        } catch (Exception e) {
            throw new ParseException("Can not parse OSM Tag", 0);
        }

        return returnData;
    }    
    
    /**
     * Returns a list of PropertyValuePairs created from an OSM XML String
     * of tags.
     * 
     * @param tagsXMLString
     * @return 
     */
    public static ArrayList<PropertyValuePair> getOsmTags(String tagsXMLString) {
        ArrayList<PropertyValuePair>    dataPairs;
        int                             offset, tagStart, tagEnd;
        PropertyValuePair               currentPropertyValuePair;
        String                          tagXML;

        dataPairs = new ArrayList<PropertyValuePair>();
        offset    = 0;

        while (offset < tagsXMLString.length()) {
            tagStart = tagsXMLString.indexOf("<tag",  offset);
            tagEnd   = tagsXMLString.indexOf("/>",    tagStart) + 2;

            if (tagStart >= 0) {
                tagXML   = tagsXMLString.substring(tagStart, tagEnd);

                try {
                    currentPropertyValuePair = getOsmTag(tagXML);
                    dataPairs.add(currentPropertyValuePair);
                } catch (ParseException pe) {
                    Logger.log(Logger.ERR, "Could not parse line: " + tagXML);
                }

                offset = tagEnd;
            } else {
                break;
            }
        }

        return dataPairs;
    }
    
    /**
     * Creates a LineString or Polygon from an OSM XML String way.
     * 
     * @param wayXML
     * @param nodeMap
     * @return 
     */
    public static VectorObject getOsmWay(String wayXML, NodeMap nodeMap) {        
        boolean                    isPolygon, isRing;
        Coordinate                 tempCoordinate;
        CoordinateList<Coordinate> objectCoordinates;
        HashMap<String, String>    customDataFields;
        int                        dataLevel, offset, propertyTagXmlEnd, tabPropertyStart, tabPropertyEnd;
        VectorObject               newMapObject;
        OsmNode                    tempOsmNode;
        PropertyValuePair          property;
        Region                     objectRegion;
        String                     nodeID, wayHighway, wayID, wayName, waySurface, wayTimestamp, wayTrackType;
        String                     currentTag, polygonType, propertyTagXML, value, wayType, naturalType;
        StringBuilder              objectDescription;
        
        //initilize
        customDataFields  = new HashMap<String, String>();
        isPolygon         = false;
        isRing            = false;
        newMapObject      = null;
        wayName           = "";
        waySurface        = "";
        wayTrackType      = "";
        objectCoordinates = new CoordinateList<Coordinate>();
        objectRegion      = null;
        offset            = 0;
        polygonType       = "(Unspecified Polygon)";
        objectDescription = new StringBuilder();
        wayType           = "(Unspecified Linestring)";

        //load values
        wayID        = getOsmNodeProperty(wayXML, "id");          //way id
        wayTimestamp = getOsmNodeProperty(wayXML, "timestamp");   //timestamp
        offset       = wayXML.indexOf(">", offset) + 2;

        //add a tag to say it came from OSM
        customDataFields.put("DataSource", "OpenStreetMap.org");
        
        //add a tag for the OSM Way ID
        customDataFields.put("OsmID", wayID);
        
        try {
            while (offset < wayXML.length()) {
                //read way properties and componet nodes
                tabPropertyStart = wayXML.indexOf("<", offset);
                tabPropertyEnd   = wayXML.indexOf("=", tabPropertyStart);

                if ( (tabPropertyStart >= 0) && (tabPropertyEnd >= 0) ) {
                    currentTag = wayXML.substring( (tabPropertyStart + 1), (tabPropertyEnd));

                    if (currentTag.equalsIgnoreCase("nd ref")) {
                        //componet node
                        tabPropertyStart = wayXML.indexOf("=", tabPropertyStart);
                        tabPropertyEnd   = wayXML.indexOf("/", tabPropertyStart);
                        nodeID           = wayXML.substring( (tabPropertyStart + 2), (tabPropertyEnd - 1));
                        
                        if (nodeID.endsWith("'")) 
                            nodeID = nodeID.substring(0, nodeID.length() - 1);                        

                        tempCoordinate = nodeMap.get(Long.parseLong(nodeID));
                        
                        if (tempCoordinate == null) {
                            //cannot find node
                            Logger.log(Logger.ERR, "OsmImporter.getOsmWay() - Can't find Node: " + nodeID);
                        } else {
                            //Add coordinate to vector to eventualy put in new VectorObject, force add to make sure loops are closed.
                            objectCoordinates.forceAdd(tempCoordinate); 
                        }
                    } else if (currentTag.equalsIgnoreCase("tag k")) {
                        //property
                        propertyTagXmlEnd = tabPropertyEnd   = wayXML.indexOf("/>",  tabPropertyStart) + 2;
                        propertyTagXML    = wayXML.substring((tabPropertyStart - 1), propertyTagXmlEnd);
                        property          = getOsmTag(propertyTagXML);

                        //add the tag to the custom field properties
                        customDataFields.put(property.getProperty(), property.getValue());                         
                        
                        if (property.getProperty().equalsIgnoreCase("name")) {
                            wayName = XMLTag.convertSafeText(property.getValue());
                        } else if (property.getProperty().equalsIgnoreCase("admin_level")) {
                            wayType = "Territorial Boundary";
                        } else if (property.getProperty().equalsIgnoreCase("amenity")) { 
                            if (property.getValue().equalsIgnoreCase("fast_food")) {
                                polygonType = "Building"; 
                                isPolygon   = true;                                
                            } else if (property.getValue().equalsIgnoreCase("fountain")) {
                                polygonType = "Lake"; 
                                isPolygon   = true;
                            } else if (property.getValue().equalsIgnoreCase("marketplace")) {
                                polygonType = "Market"; 
                                isPolygon   = true;                                
                            } else if (property.getValue().equalsIgnoreCase("parking")) {
                                polygonType = "Parking Lot"; 
                                isPolygon   = true;
                            } else if (property.getValue().equalsIgnoreCase("place_of_worship")) {
                                polygonType = "Building"; 
                                isPolygon   = true;    
                            } else if (property.getValue().equalsIgnoreCase("post_office")) {
                                polygonType = "Building"; 
                                isPolygon   = true;         
                            } else if (property.getValue().equalsIgnoreCase("pub")) {
                                polygonType = "Building"; 
                                isPolygon   = true;        
                            } else if (property.getValue().equalsIgnoreCase("restaurant")) {
                                polygonType = "Building"; 
                                isPolygon   = true;                                  
                            } else if (property.getValue().equalsIgnoreCase("school")) {
                                polygonType = "School"; 
                                isPolygon   = true;      
                            } else if (property.getValue().equalsIgnoreCase("toilets")) {
                                polygonType = "Building"; 
                                isPolygon   = true;                                    
                            } else if (property.getValue().equalsIgnoreCase("university")) {
                                polygonType = "University";
                                isPolygon   = true;
                            }
                        } else if (property.getProperty().equalsIgnoreCase("aeroway")) {
                            if (property.getValue().equalsIgnoreCase("helipad")) {
                                polygonType = "Parking Lot"; 
                                isPolygon   = true;
                            }
                        } else if (property.getProperty().equalsIgnoreCase("area")) {
                            if (property.getValue().equalsIgnoreCase("yes")) {
                                isPolygon   = true;
                            }                            
                        } else if (property.getProperty().equalsIgnoreCase("border_type")) {
                            if (property.getValue().equalsIgnoreCase("territorial")) {
                                wayName = "Border";    
                            }
                        } else if (property.getProperty().equalsIgnoreCase("boundary")) {
                            wayType = "Territorial Boundary";
                            
                            if (property.getValue().equalsIgnoreCase("administrative")) {
                                wayType = "Border - Inter-Country";
                            } else if (property.getValue().equalsIgnoreCase("town")) {
                                polygonType = "Country - Filled";
                            }    
                        } else if (property.getProperty().equalsIgnoreCase("building")) {
                            if (property.getValue().equalsIgnoreCase("yes")) {
                                polygonType = "Building";                                
                            } else {
                                polygonType = "Building";  
                            }
                            
                            isPolygon   = true;
                        } else if (property.getProperty().equalsIgnoreCase("DataLevel")) {
                            dataLevel   = Integer.parseInt(property.getValue());
                        } else if (property.getProperty().equalsIgnoreCase("footway")) {
                            wayType = "Path - Footway";
                        } else if (property.getProperty().equalsIgnoreCase("highway")) {
                            wayHighway  = property.getValue();

                            //parse diferent types of roads
                            if (wayHighway.equalsIgnoreCase("bus_stop")) {

                            } else if (wayHighway.equalsIgnoreCase("construction")) {      
                                
                            } else if (wayHighway.equalsIgnoreCase("cycleway")) {    
                                wayType = "Path - Bikeway";
                            } else if (wayHighway.equalsIgnoreCase("footway")) {
                                wayType = "Hiking Trail";
                            } else if (wayHighway.equalsIgnoreCase("living_street")) {
                                wayType = "Road - City Tertiary";
                            } else if (wayHighway.equalsIgnoreCase("mini_roundabout")) {
                                
                            } else if (wayHighway.equalsIgnoreCase("motorway")) {
                                wayType = "Road - Motorway";
                            } else if (wayHighway.equalsIgnoreCase("motorway_link")) {
                                wayType = "Road - Motorway Link";  
                            } else if (wayHighway.equalsIgnoreCase("path")) {
                                wayType = "Hiking Trail";
                            } else if (wayHighway.equalsIgnoreCase("pedestrian")) {
                                wayType = "Hiking Trail";
                            } else if (wayHighway.equalsIgnoreCase("primary")) {
                                wayType = "Road - Primary Highway";
                            } else if (wayHighway.equalsIgnoreCase("primary_link")) {
                                wayType = "Road - Primary Highway Link";                              
                            } else if (wayHighway.equalsIgnoreCase("residential")) {
                                wayType = "Road - City Secondary";
                            } else if (wayHighway.equalsIgnoreCase("road")) {
                                wayType = "Road - City Secondary";
                            } else if (wayHighway.equalsIgnoreCase("secondary")) {
                                wayType = "Road - Secondary Highway";
                            } else if (wayHighway.equalsIgnoreCase("secondary_link")) {
                                wayType = "Road - Secondary Highway Link";                                
                            } else if (wayHighway.equalsIgnoreCase("service")) {
                                wayType = "Road - City Tertiary";
                            } else if (wayHighway.equalsIgnoreCase("steps")) {
                                wayType = "Path - Steps";                                
                            } else if (wayHighway.equalsIgnoreCase("tertiary")) {
                                wayType = "Road - City Tertiary";
                            } else if (wayHighway.equalsIgnoreCase("tertiary_link")) {
                                wayType = "Road - City Tertiary";                                
                            } else if (wayHighway.equalsIgnoreCase("track")) {
                                wayType = "Road - Track";
                            } else if (wayHighway.equalsIgnoreCase("trunk")) {
                                wayType = "Road - Secondary Highway";
                            } else if (wayHighway.equalsIgnoreCase("trunk_link")) {
                                wayType = "Road - Secondary Highway Link";                                
                            } else if (wayHighway.equalsIgnoreCase("unclassified")) {
                                wayType = "Road - Unclassified";
                            }
                        } else if (property.getProperty().equalsIgnoreCase("landuse")) {
                            if (property.getValue().equalsIgnoreCase("commercial")) {
                                polygonType = "Commercial Area";     
                            } else if (property.getValue().equalsIgnoreCase("gated_community")) {
                                polygonType = "Residential Area";
                            } else if (property.getValue().equalsIgnoreCase("grass")) {
                                polygonType = "Grass Field";
                            } else if (property.getValue().equalsIgnoreCase("farm")) {
                                polygonType = "Agricultural Plot";
                            } else if (property.getValue().equalsIgnoreCase("farmland")) {
                                polygonType = "Agricultural Plot";                                
                            } else if (property.getValue().equalsIgnoreCase("field")) {
                                polygonType = "Agricultural Plot";
                            } else if (property.getValue().equalsIgnoreCase("forest")) {
                                polygonType = "Forest";
                            } else if (property.getValue().equalsIgnoreCase("industrial")) {
                                polygonType = "Industrial Area";
                            } else if (property.getValue().equalsIgnoreCase("meadow")) {
                                polygonType = "Grass Field";
                            } else if (property.getValue().equalsIgnoreCase("recreation_ground")) {
                                polygonType = "Grass Field";
                            } else if (property.getValue().equalsIgnoreCase("reservoir")) {
                                polygonType = "Lake";
                                wayType     = "Coastline";
                            } else if (property.getValue().equalsIgnoreCase("residential")) {
                                polygonType = "Residential Area";
                            }  
                            
                            isPolygon = true;
                        } else if (property.getProperty().equalsIgnoreCase("leisure")) {
                            if (property.getValue().equalsIgnoreCase("common")) {
                                polygonType = "Park";
                                isPolygon   = true;                                                              
                            } else if (property.getValue().equalsIgnoreCase("garden")) {
                                polygonType = "Grass Field";
                                isPolygon   = true;                                
                            } else if (property.getValue().equalsIgnoreCase("golf_course")) {
                                polygonType = "Grass Field";
                                isPolygon   = true;
                            } else if (property.getValue().equalsIgnoreCase("marina")) {   
                                polygonType = "Lake";
                                isPolygon   = true;                                
                            } else if (property.getValue().equalsIgnoreCase("park")) {
                                polygonType = "Park";
                                isPolygon   = true;  
                            } else if (property.getValue().equalsIgnoreCase("pitch")) {
                                polygonType = "Sports Field";
                                isPolygon   = true;  
                            } else if (property.getValue().equalsIgnoreCase("playground")) {
                                polygonType = "Park";
                                isPolygon   = true;                                           
                            } else if (property.getValue().equalsIgnoreCase("recreation_ground")) {
                                polygonType = "Sports Field";
                                isPolygon   = true;  
                            } else if (property.getValue().equalsIgnoreCase("shop")) {  
                                polygonType = "Building";
                                isPolygon   = true;                                  
                            } else if (property.getValue().equalsIgnoreCase("stadium")) {  
                                polygonType = "Stadium";
                                isPolygon   = true;  
                            } else if (property.getValue().equalsIgnoreCase("swimming_pool")) {   
                                polygonType = "Lake";
                                isPolygon   = true; 
                            } else if (property.getValue().equalsIgnoreCase("track")) {   
                                wayType     = "Path - Running";
                                isPolygon   = false; 
                                isRing      = true;
                            }            
                        } else if (property.getProperty().equalsIgnoreCase("man_made")) {
                            if (property.getValue().equalsIgnoreCase("pier")) {
                                wayType = "Pier";
                            } else if (property.getValue().equalsIgnoreCase("water_tower")) {
                                isPolygon   = true; 
                                polygonType = "Building";                             
                            } else if (property.getValue().equalsIgnoreCase("wastewater_plant")) {
                                isPolygon   = true; 
                                polygonType = "Industrial Area";
                            }
                        } else if (property.getProperty().equalsIgnoreCase("MP_TYPE")) {
                            if (property.getValue().equalsIgnoreCase("0x00")) {
                            } else if (property.getValue().equalsIgnoreCase("0x02")) {
                                isPolygon   = false;
                                wayType     = "Road - Unclassified";
                            } else if (property.getValue().equalsIgnoreCase("0x3C")) {
                                polygonType = "Lake";
                                isPolygon   = true;
                            } else if (property.getValue().equalsIgnoreCase("0x41")) {
                                polygonType = "Lake";
                                isPolygon   = true;
                            } else if (property.getValue().equalsIgnoreCase("0x45")) {
                                polygonType = "Lake";
                                isPolygon   = true;
                            } else if (property.getValue().equalsIgnoreCase("0x50")) {
                                polygonType = "Forest";
                                isPolygon   = true;
                            }
                        } else if (property.getProperty().equalsIgnoreCase("natural")) {
                            naturalType      = property.getValue();

                            if (naturalType.equalsIgnoreCase("beach")) {
                                polygonType = "Beach";
                                isPolygon   = true;    
                            } else if (naturalType.equalsIgnoreCase("coastline")) {
                                wayType     = "Coastline";
                                polygonType = "Island";
                                
                                //If coastline end points are close together, assume an island
                                if (CoordinateMath.getDistance(objectCoordinates.get(0), objectCoordinates.lastCoordinate()) < 200)
                                    isPolygon   = true;    
                            } if (naturalType.equalsIgnoreCase("reef")) {    
                                polygonType = "Reef";   
                                isPolygon   = true;  
                            } if (naturalType.equalsIgnoreCase("water")) {
                                wayType     = "Water Way - River";
                                polygonType = "Lake";     
                            } if (naturalType.equalsIgnoreCase("wood")) {
                                polygonType = "Forest";    
                                isPolygon   = true;
                            }
                        } else if (property.getProperty().equalsIgnoreCase("parking")) {
                            polygonType = "Parking Lot"; 
                            isPolygon   = true;
                        } else if (property.getProperty().equalsIgnoreCase("place")) {
                            value = property.getValue();
                            
                            if (value.equalsIgnoreCase("island")) {
                                polygonType = "Small Island"; 
                                isPolygon   = true;                                
                            }
                        } else if (property.getProperty().equalsIgnoreCase("power")) {
                            value = property.getValue();
                            
                            if (value.equalsIgnoreCase("line")) {
                                wayType     = "Power Line"; 
                                isPolygon   = false;                                
                            } else if (value.equalsIgnoreCase("sub_station")) {
                                wayType     = "Industrial Area"; 
                                isPolygon   = true;      
                            }
                        } else if (property.getProperty().equalsIgnoreCase("railway")) {
                            value = property.getValue();
                            
                            if (value.equalsIgnoreCase("light_rail")) {
                                wayType = "Rail - Tram";
                            } else if (value.equalsIgnoreCase("platform")) {
                                wayType = "Rail - Platform";
                            } else if (value.equalsIgnoreCase("tram")) {
                                wayType = "Rail - Tram";
                            } else {
                                wayType = "Rail Line";
                            }
                        } else if (property.getProperty().equalsIgnoreCase("ref")) {
  
                        } else if (property.getProperty().equalsIgnoreCase("route")) {  
                            if (property.getValue().equalsIgnoreCase("ferry")) {
                                wayType = "Ferry Line";
                            }
                        } else if (property.getProperty().equalsIgnoreCase("shop")) {
                            polygonType = "Building";           
                            isPolygon   = true;             
                        } else if (property.getProperty().equalsIgnoreCase("source")) {
                            
                        } else if (property.getProperty().equalsIgnoreCase("sport")) {
                            polygonType = "Stadium";           
                            isPolygon   = true;
                        } else if (property.getProperty().equalsIgnoreCase("surface")) {
                            waySurface       = property.getValue();
                        } else if (property.getProperty().equalsIgnoreCase("tracktype")) {
                            wayTrackType     = property.getValue();
                        } else if (property.getProperty().equalsIgnoreCase("waterway")) {
                            wayType          = property.getValue();

                            if (wayType.equalsIgnoreCase("river")) {
                                wayType     = "Water Way - River";
                                polygonType = "River";
                            } else if(wayType.equalsIgnoreCase("riverbank")) {
                                polygonType = "River";
                                isPolygon   = true;
                            } else if(wayType.equalsIgnoreCase("stream")) {
                                wayType     = "Water Way - Stream";
                                isPolygon   = false;
                            } else {
                                wayType     = "Water Way - River";
                                polygonType = "Lake";
                            }
                        } else if (property.getProperty().equalsIgnoreCase("width")) {
                            
                        } else {
                            
                        } //end tag type if

                    } //end way tag type if

                    offset = tabPropertyEnd + 1;
                } else { //end prop start and end check
                    //break loop
                    offset = wayXML.length();
                }
            } //end while loop

            //add newly constructed object
            if (objectCoordinates.size() > 1) {
                if (objectCoordinates.get(0) == objectCoordinates.get(objectCoordinates.size() - 1)) {
                    //closed object, if not a road or Boundary create polygon
                    if ((wayType.indexOf("Road") >= 0) || wayType.indexOf("Boundary") >= 0 || wayType.indexOf("Border") >= 0) {
                        newMapObject = new LinearRing(wayName, wayType, objectCoordinates);
                    } else {
                        objectCoordinates.remove(0); //Polygon Objects do not contain the same start and end coordinate, but OSM does, remove to prevent conflict
                        newMapObject = new Polygon(wayName, polygonType, objectCoordinates);
                    }
                } else if (isRing) {
                    newMapObject = new LinearRing(wayName, wayType, objectCoordinates);
                } else if (isPolygon) {
                    newMapObject = new Polygon(wayName, polygonType, objectCoordinates);
                } else {
                    //unclosed object create linestring
                    newMapObject = new LineString(wayName, wayType, objectCoordinates);
                }
                
                newMapObject.setCustomDataFields(customDataFields);
                newMapObject.setReference(Long.parseLong(wayID));                
                newMapObject.setDescription(objectDescription.toString());
            } else  if (objectCoordinates.size() == 1) {
                //create a point
                Logger.log(Logger.ERR, "Way has only one point");
                newMapObject = new MapPoint(wayName, polygonType, objectDescription.toString(), objectCoordinates.get(0));
            } else {
                Logger.log(Logger.ERR, "No Nodes found for OSM Way - " + wayXML);
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in OSMImporter.getOsmWay(String, NodeMap) - " + e);
        }

        return newMapObject;
    }
    
    /**
     * Creates MultiGeometry from OSM relations.
     * TODO: Needs work on the merging of LineString
     * 
     * @param xml
     * @param ways 
     */
    public static VectorObject getRelation(VectorLayer importLayer, String xml, HashMap<String, VectorObject> objects) {
        boolean                     mergeToPolygon;
        CoordinateList<Coordinate>  newCoordinates;
        HashMap<String, String>     customDataFields;
        int                         memberStart, memberEnd;                     
        int                         tagStart, tagEnd;
        int                         offset;
        VectorObject                   currentObject;
        MultiGeometry               relation;
        PropertyValuePair           tag;
        String                      memberText, memberType, refID, tagText;
        String                      polygonType, lineType;
        
        relation = new MultiGeometry("New Relation");
            
        try {                  
            offset            = 0;            
            tagStart          = xml.indexOf("<tag");
            customDataFields  = new HashMap<String, String>();
            polygonType       = "";
            lineType          = "";
            mergeToPolygon    = false;
            
            //Get relation members
            while (offset < tagStart) {
                memberStart = xml.indexOf("<member ", offset);
                memberEnd   = xml.indexOf("/>",       memberStart) + 2;

                if (memberStart >= 0 && memberEnd > 0) {
                    memberText = xml.substring(memberStart, memberEnd);
                    offset     = memberEnd;
                    refID      = getRelationMemberProperty(memberText, "ref");
                    memberType = getRelationMemberProperty(memberText, "type");
                    
                    currentObject = objects.get(refID);
                    
                    if (currentObject != null) {
                        relation.addObject(currentObject);
                        
                        if (!(currentObject instanceof MultiGeometry))
                            importLayer.removeObject(currentObject);
                    }
                } else {
                    offset = tagStart;
                }
            }//member loop end

            //get relation properties
            while (offset < xml.length()) {
                tagStart = xml.indexOf("<tag ", offset);
                tagEnd   = xml.indexOf("/>",    tagStart) + 2;     

                if (tagStart >= 0 && tagEnd > 0) {
                    tagText = xml.substring(tagStart, tagEnd);
                    offset  = tagEnd;
                    tag     = getOsmTag(tagText);
                    
                    customDataFields.put(tag.getProperty(), tag.getValue());
                    
                    if (tag.getProperty().equalsIgnoreCase("name")) {
                        relation.setName(tag.getValue());
                    } else if (tag.getProperty().equalsIgnoreCase("landuse")) {
                        if (tag.getValue().equalsIgnoreCase("reservoir")) {
                            polygonType = "River";
                            lineType    = "Water Way - River";     
                            mergeToPolygon = true;
                        }
                    } else if (tag.getProperty().equalsIgnoreCase("natural")) {
                        if (tag.getValue().equalsIgnoreCase("water")) {
                            polygonType = "River";
                            lineType    = "Water Way - River";
                        }             
                    } else if (tag.getProperty().equalsIgnoreCase("place")) {
                        if (tag.getValue().equalsIgnoreCase("island")) {
                            mergeToPolygon = true;
                             polygonType   = "Small Island";
                        }
                    } else if (tag.getProperty().equalsIgnoreCase("type")) {
                        if (tag.getValue().equalsIgnoreCase("multipolygon")) { 
                            mergeToPolygon = true;
                        } else if (tag.getValue().equalsIgnoreCase("watershed")) {
                            polygonType = "River";
                            lineType    = "Water Way - River";
                        }
                    }
                } else {
                    offset = xml.length();
                }
            }

            //assign typs to objects
            VectorObjectList<VectorObject> componetObjects = relation.getComponentObjects();
            newCoordinates = new CoordinateList<Coordinate>();
            
            for (int i = 0; i < componetObjects.size(); i++) {
                VectorObject o = componetObjects.get(i);
                                
                if (o instanceof Polygon && !polygonType.equals("")) {
                    o.setClass(polygonType);
                } else if (o instanceof LineString && !lineType.equals("")) {
                    o.setClass(lineType);
                }
            }
            
            //merge lineStrings into a single lineString or Polygon
            CoordinateList<Coordinate>  newObjectCoordinates;
            VectorObjectList<VectorObject>    mergedObjects;
            
            newObjectCoordinates = null;
            mergedObjects        = new VectorObjectList<VectorObject>();
            
            for (VectorObject object: componetObjects) {
                if (object instanceof LineString) {
                    if (newObjectCoordinates != null) {
                        if (newObjectCoordinates.lastCoordinate() == object.getCoordinateList().get(0)) {
                            newObjectCoordinates.addAll(object.getCoordinateList());
                            mergedObjects.add(object);                        
                        } else if (newObjectCoordinates.lastCoordinate() == object.getCoordinateList().lastCoordinate()) {                            
                            CoordinateList<Coordinate> reversed = object.getCoordinateList();
                            reversed.reverse();
                            newObjectCoordinates.addAll(reversed);
                            mergedObjects.add(object);                            
                        }
                    } else {
                        //not initilized, use firt objec's CoordinateList
                        newObjectCoordinates = object.getCoordinateList();
                        mergedObjects.add(object);
                    }
                }
            }
            
            if (mergedObjects.size() == componetObjects.size()) {
                //All componet Objects are mergable, create a new object to replace
                if (mergeToPolygon) {
                    Polygon newPoly = new Polygon(relation.getName(), polygonType, newObjectCoordinates);
                    newPoly.setCustomDataFields(customDataFields);     
                    
                    return newPoly;
                } else {
                    LineString newLine = new LineString(relation.getName(), lineType, newObjectCoordinates);
                    newLine.setCustomDataFields(customDataFields);

                    return newLine;
                }                
            } else {
                //only merge some of the componets
                //removed the object we are merging
                for (VectorObject mergedObject: mergedObjects) 
                    relation.getComponentObjects().remove(mergedObject);
                    
                LineString newLine = new LineString(mergedObjects.get(0).getName(), lineType, newObjectCoordinates);
                relation.addObject(newLine);
            }
                                   
            relation.setCustomDataFields(customDataFields);
            
            //PropertyValuePair getOsmTag
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in OsmImporter.getRelation(String, HashMap) - " + e);
        }
        
        return relation;
    }    
    
    /**
     * Return the property from an xml string of the form:
     *  <member type="way" ref="32122400" role="outer"/>
     * 
     * @param xml
     * @param property
     * @return 
     */
    private static String getRelationMemberProperty(String xml, String property) {
        int     refStart, refEnd;
        String  value;
        
        value    = "";
        refStart = xml.indexOf(property) + 2 + property.length();                
        refEnd   = xml.indexOf("\"", refStart);
        
        if (refStart > 0 && refEnd > 0)
            value = xml.substring(refStart, refEnd);
        
        return value;
    }    
    
    /**
     * Imports an OpenStreeMap.org map
     */
    private VectorObjectList<VectorObject> importOSM(String xml, ProgressBarPanel progressPanel) {
        ArrayList<OsmNode>              nodes;
        HashMap<String, OsmNode>        coordinateNodes;
        int                             nodeEnd, nodeStart, offset;
        int                             boundsStart, boundsEnd;
        VectorObject                       newMapObject;
        String                          mapBoundsXML, nodeTags, wayTag;
        VectorObjectList<VectorObject>        mapObjects;

        mapObjects      = new VectorObjectList<VectorObject>();
        offset          = 0;

        try {
            //read header info
            boundsStart  = xml.indexOf("<bounds ");
            boundsEnd    = xml.indexOf("/>", boundsStart) + 2;
            
            if (boundsStart >= 0 && boundsEnd >= 0) {
                mapBoundsXML = xml.substring(boundsStart, boundsEnd);
                parseMapBounds(mapBoundsXML);
            }

            //locate node sarts and end
            int nodesStart    = xml.indexOf("<node");
            int nodesEnd      = xml.lastIndexOf("</node>") + 7;
            int lastNodeStart = xml.lastIndexOf("<node")   + 1;

            if (lastNodeStart > nodesEnd) 
                nodesEnd      = xml.indexOf("/>", lastNodeStart) + 2;
            
            if (progressPanel != null)
                progressPanel.updateProgress("Loading OSM Nodes", 1);

            if ((nodesStart >= 0) && (nodesStart < nodesEnd)) {
                nodeTags        = xml.substring(nodesStart, nodesEnd);
                coordinateNodes = getOsmNodes(nodeTags, progressPanel);
                xml             = xml.substring(nodesStart - 1); //trim to alow larger files

                //load points
                if (progressPanel != null)
                    progressPanel.updateProgress("Loading OSM Points", 85);
                
                nodes = new ArrayList<OsmNode>(coordinateNodes.values());
                
                for (OsmNode currentNode: nodes) {
                    if (currentNode.hasNameTag()) {
                        mapObjects.add(getOsmPoint(currentNode, mapData.getCoordinateSet()));
                    }
                }

                //load ways
                if (progressPanel != null)
                    progressPanel.updateProgress("Loading OSM Ways", 90);

                while (offset < xml.length()) {
                    nodeStart = xml.indexOf("<way id=", offset);
                    nodeEnd   = xml.indexOf("</way>",   nodeStart);

                    if ((nodeStart >= 0) && (nodeEnd >= 0)) {
                        wayTag       = xml.substring(nodeStart, nodeEnd);
                        newMapObject = getOsmWay(wayTag, mapData.getCoordinateSet());

                        if (newMapObject != null)
                            mapObjects.add(newMapObject);

                        offset = nodeEnd + 6;
                    } else {
                        offset = xml.length();
                    }
                } // while loop
            } //end node start / end posistion check

            if (progressPanel != null)
                progressPanel.updateProgress("Finishing Import", 99);
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error on OsmImporter.importOSM(String, ProgressBarPanel) - " + e);
        }

        return mapObjects;
    }
 
    /**
     * Tries to merge connected coastlines together.
     * 
     * @param importLayer
     * @param coastlines 
     */
    public static void mergeCoastlines(VectorLayer importLayer, ArrayList<VectorObject> coastlines) {
        ArrayList<VectorObject>     objectMerged, objectsToRemove;
        Coordinate                  mainStart, mainEnd;
        Coordinate                  matchStart, matchEnd;
        CoordinateList<Coordinate>  cList;
        float                       maxDistance, distance;
        Polygon                     newPoly;        
        VectorObject                mainObject, matchingObject;
                     
        objectMerged    = new ArrayList<VectorObject>();
        objectsToRemove = new ArrayList<VectorObject>();
        
        for (int i = 0; i < coastlines.size(); i++) {
            mainObject = coastlines.get(i);

            for (int j = 0; j < coastlines.size(); j++) {
                matchingObject = coastlines.get(j);
                
                if (mainObject != matchingObject &&
                    !objectsToRemove.contains(mainObject) &&
                    !objectsToRemove.contains(matchingObject)) {
                    
                    mainStart  = mainObject.getCoordinateList().get(0);
                    mainEnd    = mainObject.getCoordinateList().lastCoordinate();
                    matchStart = matchingObject.getCoordinateList().get(0);
                    matchEnd   = matchingObject.getCoordinateList().lastCoordinate();    
                    
                    if (mainEnd.equals(matchStart)) {
                        objectsToRemove.add(matchingObject);
                        cList = matchingObject.getCoordinateList();
                        cList.remove(matchStart); //remove redundant coordinate
                        mainObject.getCoordinateList().addAll(cList);
                    }
                    
                    if (mainStart.equals(matchEnd)) {
                        objectsToRemove.add(matchingObject);
                        cList = matchingObject.getCoordinateList().clone();
                        cList.remove(matchEnd); //remove redundant coordinate
                        cList.addAll(mainObject.getCoordinateList());
                        mainObject.setCoordinateList(cList);
                    }
                    
                    if (mainStart.equals(matchStart)) {
                        System.out.println("MainStart - MatchStart");
                    }                    
                    
                    if (mainEnd.equals(matchEnd)) {
                        objectsToRemove.add(matchingObject);
                        cList = matchingObject.getCoordinateList().clone();
                        cList.remove(matchEnd); //remove redundant coordinate
                        cList.reverse();
                        mainObject.getCoordinateList().addAll(cList);                        
                    }                    
                }
            }
            
            objectMerged.add(mainObject);
        }
        
        //remove object that have been added to other objects.
        for (VectorObject object: objectsToRemove) {
            importLayer.removeObject(object);
            coastlines.remove(object);
        }
        
        //check the distance between the first and last coordinate in the list
        for (VectorObject object: coastlines) {
            cList       = object.getCoordinateList();
            distance    = CoordinateMath.getDistance(cList.get(0), cList.lastCoordinate());
            maxDistance = CoordinateMath.getMaxDistances(cList) * 2;
            
            if (distance < maxDistance) {
                //the distance is within an acceptable margin merge to a polygon
                newPoly = new Polygon(object.getName(), "Small Island", object.getCoordinateList());
                newPoly.addCustomDataFields(object.getAllCustomData());
                importLayer.addObject(newPoly);
                importLayer.removeObject(object);
            }
        }
        
        //sort imported objects
        importLayer.getObjectList().sortByLayer();
    }
    
    /**
     * Processes OSM relations and transforms already loaded object accordingly.
     * OSM relation build polygons, specify restrictions and add other info.
     * 
     * @param objects
     * @param relation 
     */
    public static void processRelation(HashMap<String, VectorObject> objects, 
                                       VectorLayer                   importLayer,
                                       OsmRelation                   relation) {
        
        CoordinateList<Coordinate>  coordinates;
        InnerBoundary               innerBoundary;
        int                         outerRolesCount;
        long[]                      ids;        
        Polygon                     polygon;
        String                      objectClass;
        VectorObject                object;
        
        objectClass = "";
        
        try {
            if (relation.getType().equalsIgnoreCase("boundary")) {
                
            } else if (relation.getType().equalsIgnoreCase("multipolygon")) {
                outerRolesCount = relation.countOuterRoles();
                
                if (outerRolesCount == 1) {
                    object  = objects.get(Long.toString(relation.getOuterRoles()[0]));

                    if (object instanceof Polygon) {
                        polygon = (Polygon) object;
                    } else {
                        //object is not polygon, convert it to one
                        polygon = new Polygon(object.getName(), object.getObjectClass(), object.getCoordinateList());
                        polygon.setDescription(object.getDescription());
                        polygon.setCustomDataFields(object.getCustomDataFields());
                    }

    //                //add internal polygon boundaries
    //                ids = relation.getInnerRoles();
    //                
    //                for(long id: ids) {
    //                    object        = objects.get(Long.toString(id));
    //                    coordinates   = object.getCoordinateList();
    //                    innerBoundary = new InnerBoundary(coordinates);
    //                    polygon.addInnerBoundary(innerBoundary);
    //                    importLayer.removeObject(object);
    //                }
                } else if (outerRolesCount > 1) {
                    ids    = relation.getOuterRoles();
                    object = null;
                    
                    for (int i = 0; i < outerRolesCount; i++) {
                        object = objects.get(Long.toString(ids[i]));
                        
                        if (object != null) {
                            objectClass = object.getObjectClass();
                            break;
                        }
                    }
                    
                    if (object != null) {
                        coordinates = new CoordinateList<Coordinate>();

                        for(long id: ids) {
                            object = objects.get(Long.toString(id));     

                            if (object != null) {
                                coordinates.addAll(object.getCoordinateList());
                                importLayer.removeObject(object);                   
                            }
                        }

                        polygon = new Polygon(relation.getPropertyValue("name"), objectClass, coordinates);
                        
                        polygon.addCustomDataFields(relation.getProperties());
                        importLayer.addObject(polygon);
                    }
                }
            } else if (relation.getType().equalsIgnoreCase("restriction")) {

            } else if (relation.getType().equalsIgnoreCase("route")) {
                String name = relation.getPropertyValue("name");
                MultiGeometry mg = new MultiGeometry(name);
                ArrayList<OsmMember> members = relation.getMembers();
                
                for (OsmMember om: members) {                    
                    VectorObject vObj = objects.get(Long.toString(om.refID));
                    
                    if (vObj != null) {
                        importLayer.removeObject(vObj);
                        mg.addObject(vObj);                        
                    } else {
                        Logger.log(Logger.ERR, "Error in OsmImporter.processRelation(HashMap, VectorLayer, OsmRelation) - Null object member id: " + om.refID);
                    }
                }
                
                importLayer.addObject(mg);
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in OsmImporter.processRelation(HashMap, VectorLayer, OsmRelation) - " + e);
        }
    }
    
    /**
     * Sets the map bounds for the map being imported.
     * 
     * @param mapBoundsXML
     */
    public void parseMapBounds(String mapBoundsXML) {
        int     boundsPropStart, boundsPropEnd;
        String  mapBoundsProp;

        try {
            boundsPropStart  = mapBoundsXML.indexOf("minlat=") + 8;
            boundsPropEnd    = mapBoundsXML.indexOf("\"", boundsPropStart);
            if (boundsPropEnd < 0) {
                boundsPropEnd    = mapBoundsXML.indexOf("'", boundsPropStart);
            }
            mapBoundsProp    = mapBoundsXML.substring(boundsPropStart, boundsPropEnd);
            minlat           = Float.parseFloat(mapBoundsProp);

            boundsPropStart  = mapBoundsXML.indexOf("minlon=") + 8;
            boundsPropEnd    = mapBoundsXML.indexOf("\"", boundsPropStart);
            if (boundsPropEnd < 0) {
                boundsPropEnd    = mapBoundsXML.indexOf("'", boundsPropStart);
            }
            mapBoundsProp    = mapBoundsXML.substring(boundsPropStart, boundsPropEnd);
            minlon           = Float.parseFloat(mapBoundsProp);

            boundsPropStart  = mapBoundsXML.indexOf("maxlat=") + 8;
            boundsPropEnd    = mapBoundsXML.indexOf("\"", boundsPropStart);
            if (boundsPropEnd < 0) {
                boundsPropEnd    = mapBoundsXML.indexOf("'", boundsPropStart);
            }
            mapBoundsProp    = mapBoundsXML.substring(boundsPropStart, boundsPropEnd);
            maxlat           = Float.parseFloat(mapBoundsProp);

            boundsPropStart  = mapBoundsXML.indexOf("maxlon=") + 8;
            boundsPropEnd    = mapBoundsXML.indexOf("\"", boundsPropStart);
            if (boundsPropEnd < 0) {
                boundsPropEnd    = mapBoundsXML.indexOf("'", boundsPropStart);
            }
            mapBoundsProp    = mapBoundsXML.substring(boundsPropStart, boundsPropEnd);
            maxlon           = Float.parseFloat(mapBoundsProp);
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in OSMImporter.parseMapBounds(String) - " + e);
        }
    }    
    
    @Override
    public void run() {
        try {
            ArrayList<VectorObject>         coastlines;
            boolean                         nodesCompleted, nodeStarted;
            boolean                         waysCompleted, wayStarted, relationStarted;
            BufferedReader                  br;
            float                           lat, lon, radius, zoom;
            HashMap<String, VectorObject>   objectIDs;
            LatLonAltBox                    bounds;
            VectorObject                    currentObject;
            MapProjection                   mapProjection;
            OsmNode                         osmNode;            
            String                          line;
            StringBuffer                    nodeXML, wayXML, relationXML;
            VectorLayer                     newLayer;
            
            progressIndicator.updateProgress("Opening OSM File", 1);
            
            br              = new BufferedReader(new FileReader(osmFile));
            coastlines      = new ArrayList<VectorObject>();
            line            = "";
            newLayer        = new VectorLayer("OSM Import");
            nodeXML         = new StringBuffer();
            objectIDs       = new HashMap<String, VectorObject>(10000);
            relationXML     = new StringBuffer();
            wayXML          = new StringBuffer();   
            nodesCompleted  = false;
            nodeStarted     = false; 
            waysCompleted   = false;
            wayStarted      = false;
            relationStarted = false;                        
            
            newLayer.setParentMap(mapData);
            
            while (!line.contains("</osm>")) {
                while (br.ready()) {                
                    line = br.readLine().trim();
                    
                    if (nodesCompleted == false) {
                        if (line.startsWith("<node") && !line.endsWith("/>")) {
                            nodeXML     = new StringBuffer();
                            nodeStarted = true;
                            nodeXML.append(line);
                            nodeXML.append("\n");
                        } else if (line.startsWith("<node") && line.endsWith("/>")) {
                            osmNode = OsmImporter.getOsmNode(line);

                            if (osmNode != null) {                    
                                mapData.getCoordinateSet().put(osmNode.getNodeCoordinate());
                            }
                        } else if (line.startsWith("</node>")) {
                            nodeXML.append(line);
                            osmNode     = OsmImporter.getOsmNode(nodeXML.toString());  
                            nodeStarted = false;

                            if (osmNode != null) {
                                mapData.getCoordinateSet().put(osmNode.getNodeCoordinate());

                                if (osmNode.hasNameTag()) {
                                    currentObject = OsmImporter.getOsmPoint(osmNode, mapData.getCoordinateSet());

                                    if (osmNode != null) {
                                        newLayer.addObject(currentObject);  
                                        objectIDs.put(currentObject.getCustomDataFieldValue("OsmID"), currentObject);
                                        if (updateable != null) updateable.update();
                                    }
                                }
                            }
                        }
                    }
                    
                    if (waysCompleted == false && nodeStarted == false) {
                        if (line.startsWith("<way ")) {
                            nodesCompleted = true;
                            wayXML         = new StringBuffer();
                            wayStarted     = true;

                            wayXML.append(line);
                            wayXML.append("\n");     
                        } else if (line.startsWith("</way>")) {
                            wayXML.append(line);   
                            currentObject = OsmImporter.getOsmWay(wayXML.toString(), mapData.getCoordinateSet());

                            if (currentObject != null) {
                                objectIDs.put(currentObject.getCustomDataFieldValue("OsmID"), currentObject);
                                newLayer.addObject(currentObject);

                                if (updateable != null) updateable.update();
                                wayStarted = false; 

                                if (currentObject.getObjectClass().equalsIgnoreCase("Coastline")) 
                                    coastlines.add(currentObject);
                            } else {
                                Logger.log(Logger.ERR, "Could Not Covert OSM Way - " + wayXML.toString());
                            }         
                        }
                    }
                    
                    if (nodesCompleted == true) {
                        if (line.startsWith("<relation ")) {
                            waysCompleted   = true;
                            relationXML     = new StringBuffer();
                            relationStarted = true;
                            relationXML.append(line);
                            relationXML.append("\n");                        
                        } else if (line.startsWith("</relation>")) {
                            relationStarted = false;
                            relationXML.append(line);                

                            //This needs more work before use
                            OsmImporter.processRelation(objectIDs, newLayer, OsmRelation.parseRelation(relationXML.toString()));
                        } else {
                            if (nodeStarted) {
                                nodeXML.append(line);
                                nodeXML.append("\n");      
                            } else if (wayStarted) {
                                wayXML.append(line);
                                wayXML.append("\n");     
                            } else if (relationStarted) {
                                relationXML.append(line);
                                relationXML.append("\n");
                            }
                        }
                    }
                } //end of osm loop                                
            }
           
            progressIndicator.updateProgress("Cleaning up", 95);
            OsmImporter.mergeCoastlines(newLayer, coastlines);
            
            mapData.addLayer(newLayer, 0);
            
            //set view            
            bounds        = mapData.getBoundary();
            mapProjection = mapData.getLastMapView().getMapProjection();
            lon           = bounds.getWest()  + (bounds.getWidth() / 4f);
            lat           = bounds.getNorth() - (bounds.getHeight() / 4f);
            
            mapData.setLookAtCoordinate(new Coordinate(0, lat, lon)); 
            
            float southY = (float) mapProjection.getY(bounds.getSouthWestCoordinate());
            
            if (southY < 1) {
                mapProjection.setZoomLevel(southY * 600);            
            } else {
                mapProjection.setZoomLevel(southY * 6); 
            }                   
            
            if (progressIndicator != null) {
                progressIndicator.updateProgress("Done", 100);
                progressIndicator.finish();
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in OsmImporter.run() - " + e);
        }
    }    
    
    /**
     * Searches the OSM database for a boundary matching the query String.
     * 
     * @param query 
     */
    public static BoundsSearchResult[] search(String query) {
        ArrayList<BoundsSearchResult> searchResults;
        BoundsSearchResult            searchResult;
        StringBuilder                 sb = new StringBuilder();
        
        sb.append("http://nominatim.openstreetmap.org/search/");
        
        query = query.replaceAll(" ",  "%20");
        query = query.replaceAll("\t", "%20");
        
        sb.append(query);
        sb.append("?format=xml");
        sb.append("&polygon=1");
        
        String result = ResourceHelper.downloadString(sb.toString());
        
        //Interpret results        
        int             offset = 0;
        int             bboxStartIndex, bboxEndIndex, placeIndex;
        int             nameStartIndex, nameEndIndex;
        float           north, south, east, west;
        LatLonBox       bounds;
        String          bbox, name;
        StringTokenizer st;
        
        searchResults = new ArrayList<BoundsSearchResult>();
        placeIndex    = result.indexOf("<place", offset);
        offset        = placeIndex;
        
        while (placeIndex > 0 && offset < result.length()) {                                    
            bboxStartIndex = result.indexOf("boundingbox=", offset) + 13;
            bboxEndIndex   = result.indexOf("\"", bboxStartIndex);
            bbox           = result.substring(bboxStartIndex, bboxEndIndex);
            offset         = bboxEndIndex;
            nameStartIndex = result.indexOf("display_name=", offset) + 14;
            nameEndIndex   = result.indexOf("'", nameStartIndex);
            offset         = nameEndIndex;
            name           = result.substring(nameStartIndex, nameEndIndex);
            placeIndex     = result.indexOf("<place", offset);
            offset         = placeIndex;
            
            st           = new StringTokenizer(bbox, ",");
            south        = Float.parseFloat(st.nextToken());
            north        = Float.parseFloat(st.nextToken());
            west         = Float.parseFloat(st.nextToken());
            east         = Float.parseFloat(st.nextToken());
            bounds       = new LatLonBox(north, south, east, west);            
            searchResult = new BoundsSearchResult(name, bounds);
            
            if (!searchResults.contains(searchResult))
                searchResults.add(searchResult);                        
        }
        
        return searchResults.toArray(new BoundsSearchResult[1]);
    }
}
