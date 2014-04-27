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

import co.foldingmap.map.vector.LatLonBox;
import co.foldingmap.map.vector.NodeMap;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.MapPoint;
import co.foldingmap.map.vector.LineString;
import co.foldingmap.map.vector.Polygon;
import co.foldingmap.Logger;
import co.foldingmap.dataStructures.PropertyValuePair;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.MapObject;
import co.foldingmap.xml.XMLTag;
import co.foldingmap.xml.XmlOutput;
import co.foldingmap.xml.XmlWriter;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Alec
 */
public class OsmExporter {
    private DigitalMap mapData;
    
    public OsmExporter(DigitalMap mapData) {
        this.mapData = mapData;
    }
    
    public void export(File osmFile) {
        ArrayList<MapPoint>     points;
        ArrayList<VectorObject> objects;
        XmlWriter               xmlOut;
        
        objects = new ArrayList<VectorObject>();
        points  = new ArrayList<MapPoint>();
        xmlOut  = new XmlWriter(osmFile);
        
        //Get a list of all the MapPoints and other objects
        for (MapObject object: mapData.getAllMapObjects()) {
            if (object instanceof MapPoint) {
                points.add((MapPoint) object);          
            } else if (object instanceof VectorObject) {
                objects.add((VectorObject) object);
            }
        }        
        
        //write header info
        xmlOut.writeText("<?xml version='1.0' encoding='UTF-8'?>\n");
        xmlOut.writeText("<osm version='0.6' generator='FoldingMap'>\n");
        xmlOut.increaseIndent();
        
        //write bounds
        xmlOut.writeText(getOsmBoundsString(mapData.getBoundary()));
        
        //Export Nodes
        exportNodes(xmlOut, points);
        
        //Export Ways
        exportWays(xmlOut, objects);
                
        //Export Relations
        
        xmlOut.decreaseIndent();
        xmlOut.writeText("</osm>\n");
        
        xmlOut.closeFile();
    }    
    
    private void exportNodes(XmlOutput xmlOut, ArrayList<MapPoint> points) {
        Coordinate          coordinate;
        MapPoint            point;
        NodeMap             nodeMap;
        
        nodeMap = mapData.getCoordinateSet();        
        
        for (int i = 0; i < nodeMap.size(); i++) {
            coordinate = nodeMap.getFromIndex(i);            
            point      = pointUsingCoordinate(points, coordinate);
            
            if (coordinate.getID() > 0) {
                xmlOut.writeText(getNodeOpenTag(coordinate));
                xmlOut.increaseIndent();

                xmlOut.writeText(xmlOut.getIndent());
                xmlOut.writeText(getTagString("ele", coordinate.getAltitude()));

                if (point != null) {
                    xmlOut.writeText(getTagString("name", XMLTag.getSafeText(point.getName()) ));

                    //Style 
                    xmlOut.writeText(xmlOut.getIndent());
                    xmlOut.writeText(getStyleTags(point));                    
                    
                    for (PropertyValuePair pvp: point.getAllCustomData()) {
                        xmlOut.writeText(xmlOut.getIndent());                    
                        xmlOut.writeText(getTagString(pvp.getProperty(), pvp.getValue()));                
                    }
                }

                xmlOut.decreaseIndent();
                xmlOut.writeText("</node>\n");
            }
        }
    }
    
    private void exportWays(XmlOutput xmlOut, ArrayList<VectorObject> objects) {
        long   wayID, wayCounter;
        String value;
        
        wayCounter = 1;
        
        for (VectorObject object: objects) {
            //If the object has an OSM field use that for the way ID, if not increment from 1.
            value = object.getCustomDataFieldValue("OsmID");
            
            if (value != null) {
                wayID = Long.parseLong(value);
            } else {
                wayID = wayCounter;
                wayCounter++;
            }
            
            xmlOut.writeText(getWayTagOpen(wayID, object.getTimestamp()));
            xmlOut.increaseIndent();
            
            //output node IDs used in this way
            for (Coordinate c: object.getCoordinateList()) {
                xmlOut.writeText(xmlOut.getIndent());
                xmlOut.writeText("<nd ref='-" + c.getID() + "' />\n");    
            }
            
            //If polygon close way
            if (object instanceof Polygon) {
                xmlOut.writeText(xmlOut.getIndent());
                xmlOut.writeText("<nd ref='-" + object.getCoordinateList().get(0).getID() + "' />\n");    
            }
            
            //name
            xmlOut.writeText(getTagString("name", XMLTag.getSafeText(object.getName())));
            
            //Style 
            xmlOut.writeText(xmlOut.getIndent());
            xmlOut.writeText(getStyleTags(object));
            
            //output properties
            for (PropertyValuePair pvp: object.getAllCustomData()) {
                xmlOut.writeText(xmlOut.getIndent());            
                xmlOut.writeText(getTagString(pvp.getProperty(), pvp.getValue()));               
            }
            
            xmlOut.decreaseIndent();
            xmlOut.writeText("</way>\n");            
        }
    }
    
    /**
     * Returns a string representing the open of the Node tag.
     * 
     * @param c
     * @return 
     */
    private String getNodeOpenTag(Coordinate c) {
        StringBuilder sb;
        
        sb = new StringBuilder();
        
        try {
            sb.append("<node id='-");                        
            sb.append(c.getID());
            sb.append("' timestamp='");
            sb.append(c.getTimestamp());
            sb.append("' visible='true' ");
            sb.append("lat='");
            sb.append(c.getLatitude());
            sb.append("' lon='");
            sb.append(c.getLongitude());
            sb.append("' version='1'");
            sb.append(">\n");
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in OsmExporter.getNodeOpenTag(Coordinate) - " + e);
        }
        
        return sb.toString();
    }    
    
    private String getOsmBoundsString(LatLonBox bounds) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("<bounds minlat='");
        sb.append(bounds.getSouth());
        sb.append("' minlon='");
        sb.append(bounds.getWest());
        sb.append("' maxlat='");
        sb.append(bounds.getNorth());
        sb.append("' maxlon='");
        sb.append(bounds.getEast());
        sb.append("' />\n");
        
        return sb.toString();
    }    
    
    private String getStyleTags(VectorObject object) {
        String        objectClass;
        StringBuilder sb;
        
        objectClass = object.getObjectClass();
        sb          = new StringBuilder();
        
        if (object instanceof MapPoint) {
            if (objectClass.equalsIgnoreCase("Airport")) {
                sb.append(getTagString("aeroway", "aerodrome"));
            } else if (objectClass.equalsIgnoreCase("Amenity")) {    
                sb.append(getTagString("amenity", "yes"));
            } else if (objectClass.equalsIgnoreCase("Antenna")) {
                sb.append(getTagString("man_made", "antenna"));
            } else if (objectClass.equalsIgnoreCase("Art Gallery")) {
                sb.append(getTagString("amenity", "arts_centre"));
            } else if (objectClass.equalsIgnoreCase("Bank")) {
                sb.append(getTagString("amenity", "bank"));
            } else if (objectClass.equalsIgnoreCase("Bar")) {
                sb.append(getTagString("amenity", "bar"));
            } else if (objectClass.equalsIgnoreCase("Bridge")) {
                sb.append(getTagString("bridge", "yes"));
            } else if (objectClass.equalsIgnoreCase("Building")) {
                sb.append(getTagString("building", "yes"));
            } else if (objectClass.equalsIgnoreCase("Bus Station")) {
                sb.append(getTagString("amenity", "bus_station"));
            } else if (objectClass.equalsIgnoreCase("Cafe")) {
                sb.append(getTagString("amenity", "cafe"));
            } else if (objectClass.equalsIgnoreCase("Camp Site")) {
                sb.append(getTagString("tourism", "camp_site"));
            } else if (objectClass.equalsIgnoreCase("Cemetery")) {
                sb.append(getTagString("amenity", "grave_yard"));
            } else if (objectClass.equalsIgnoreCase("Clinic")) {
                sb.append(getTagString("amenity", "Clinic"));
            } else if (objectClass.equalsIgnoreCase("Cinema")) {
                sb.append(getTagString("amenity", "cinema"));
            } else if (objectClass.equalsIgnoreCase("City")) {
                sb.append(getTagString("place", "city"));
            } else if (objectClass.equalsIgnoreCase("Place - Town")) {  
                sb.append(getTagString("place", "town"));
            } else if (objectClass.equalsIgnoreCase("Place - Suburb")) { 
                sb.append(getTagString("place", "suburb"));
            } else if (objectClass.equalsIgnoreCase("Place - Village")) {   
                sb.append(getTagString("place", "village"));
            } else if (objectClass.equalsIgnoreCase("Courthouse")) {
                sb.append(getTagString("amenity", "courthouse"));
            } else if (objectClass.equalsIgnoreCase("Cyber Café")) {
                sb.append(getTagString("internet_access", "terminal"));
            } else if (objectClass.equalsIgnoreCase("Dangerous Area")) {
                sb.append(getTagString("", ""));
            } else if (objectClass.equalsIgnoreCase("Embassy")) {
                sb.append(getTagString("amenity", "embassy"));
            } else if (objectClass.equalsIgnoreCase("Ferry")) {
                sb.append(getTagString("amenity", "ferry_terminal"));
            } else if (objectClass.equalsIgnoreCase("Fire Station")) {
                sb.append(getTagString("amenity", "fire_station"));
            } else if (objectClass.equalsIgnoreCase("Forest")) {
                sb.append(getTagString("landuse", "forest"));
            } else if (objectClass.equalsIgnoreCase("Football")) {  
                sb.append(getTagString("", ""));
            } else if (objectClass.equalsIgnoreCase("Garden")) { 
                sb.append(getTagString("leisure", "garden"));
            } else if (objectClass.equalsIgnoreCase("Gas Station")) {
                sb.append(getTagString("", ""));
            } else if (objectClass.equalsIgnoreCase("Golf")) {
                sb.append(getTagString("leisure", "golf_course"));
            } else if (objectClass.equalsIgnoreCase("Grocery")) {
                sb.append(getTagString("", ""));
            } else if (objectClass.equalsIgnoreCase("Harbor")) {
                sb.append(getTagString("", ""));
            } else if (objectClass.equalsIgnoreCase("Heliport")) {  
                sb.append(getTagString("aeroway", "helipad"));
            } else if (objectClass.equalsIgnoreCase("Hill")) { 
                sb.append(getTagString("", ""));
            } else if (objectClass.equalsIgnoreCase("Hospital")) {
                sb.append(getTagString("amenity", "hospital"));
            } else if (objectClass.equalsIgnoreCase("Hotel")) {
                sb.append(getTagString("tourism", "hotel"));
            } else if (objectClass.equalsIgnoreCase("Industrial")) {
                sb.append(getTagString("", ""));
            } else if (objectClass.equalsIgnoreCase("Library")) {
                sb.append(getTagString("amenity", "library"));
            } else if (objectClass.equalsIgnoreCase("Lookout")) { 
                sb.append(getTagString("tourism", "viewpoint"));
            } else if (objectClass.equalsIgnoreCase("Marker")) { 
                sb.append(getTagString("", ""));
            } else if (objectClass.equalsIgnoreCase("Memorial")) {
                sb.append(getTagString("", ""));
            } else if (objectClass.equalsIgnoreCase("Mine")) {
                sb.append(getTagString("", ""));
            } else if (objectClass.equalsIgnoreCase("Minefield")) {
                sb.append(getTagString("", ""));
            } else if (objectClass.equalsIgnoreCase("Moto Taxi")) {
                sb.append(getTagString("amenity",    "taxi"));
                sb.append(getTagString("motorcycle", "yes"));
            } else if (objectClass.equalsIgnoreCase("Mountain Peak")) {
                sb.append(getTagString("natural", "peak"));
            } else if (objectClass.equalsIgnoreCase("NGO Office")) { 
                sb.append(getTagString("office", "ngo"));
            } else if (objectClass.equalsIgnoreCase("Park")) { 
                sb.append(getTagString("leisure", "park"));
            } else if (objectClass.equalsIgnoreCase("Parking")) { 
                sb.append(getTagString("amenity", "parking"));
            } else if (objectClass.equalsIgnoreCase("Parking Garage")) { 
                sb.append(getTagString("amenity", "parking"));
            } else if (objectClass.equalsIgnoreCase("Pharmacy")) {
                sb.append(getTagString("amenity", "pharmacy"));
            } else if (objectClass.equalsIgnoreCase("Phone")) {   
                sb.append(getTagString("amenity", "telephone"));
            } else if (objectClass.equalsIgnoreCase("Place Of Worship")) {
                sb.append(getTagString("amenity", "place_of_worship"));
            } else if (objectClass.equalsIgnoreCase("Place Of Worship - Christian")) {
                sb.append(getTagString("amenity", "place_of_worship"));
                sb.append(getTagString("religion", "christian"));
            } else if (objectClass.equalsIgnoreCase("Place Of Worship - Hindu")) {
                sb.append(getTagString("amenity", "place_of_worship"));
                sb.append(getTagString("religion", "hindu"));
            } else if (objectClass.equalsIgnoreCase("Place Of Worship - Islam")) {
                sb.append(getTagString("amenity", "place_of_worship"));
                sb.append(getTagString("religion", "muslim"));
            } else if (objectClass.equalsIgnoreCase("Place Of Worship - Jewish")) {
                sb.append(getTagString("amenity", "place_of_worship"));
                sb.append(getTagString("religion", "jewish"));
            } else if (objectClass.equalsIgnoreCase("Police Station")) {  
                sb.append(getTagString("amenity", "police"));
            } else if (objectClass.equalsIgnoreCase("Prison")) { 
                sb.append(getTagString("amenity", "prison"));
            } else if (objectClass.equalsIgnoreCase("Post Office")) {
                sb.append(getTagString("amenity", "post_office"));
            } else if (objectClass.equalsIgnoreCase("Public Toilets")) {  
                sb.append(getTagString("amenity", "toilets"));
            } else if (objectClass.equalsIgnoreCase("Railway Stop")) {
                sb.append(getTagString("", ""));
            } else if (objectClass.equalsIgnoreCase("Restaurant")) {
                sb.append(getTagString("amenity", "restaurant"));
            } else if (objectClass.equalsIgnoreCase("Restaurant - Fast Food")) {
                sb.append(getTagString("amenity", "fast_food"));
            } else if (objectClass.equalsIgnoreCase("Roadblock")) {  
                sb.append(getTagString("barrier", "highway"));
            } else if (objectClass.equalsIgnoreCase("School")) {  
                sb.append(getTagString("amenity", "school"));
            } else if (objectClass.equalsIgnoreCase("Shop")) {
                sb.append(getTagString("shop", "yes"));
            } else if (objectClass.equalsIgnoreCase("Super Market")) {
                sb.append(getTagString("shop", "supermarket"));
            } else if (objectClass.equalsIgnoreCase("Swimming")) { 
                sb.append(getTagString("", ""));
            } else if (objectClass.equalsIgnoreCase("Tree")) {
                sb.append(getTagString("natural", "tree"));
            } else if (objectClass.equalsIgnoreCase("Tourist Attraction")) {
                sb.append(getTagString("tourism", "attraction"));
            } else if (objectClass.equalsIgnoreCase("University")) { 
                sb.append(getTagString("amenity", "university"));
            } else if (objectClass.equalsIgnoreCase("Warehouse")) { 
                sb.append(getTagString("building", "warehouse"));
            } else if (objectClass.equalsIgnoreCase("Zoo")) {
                sb.append(getTagString("tourism", "zoo"));
            }
        } //end MapPoint Check
        
        if (object instanceof LineString) {
            if (objectClass.equalsIgnoreCase("Lake")  ||
                objectClass.equalsIgnoreCase("Ocean") ||
                objectClass.equalsIgnoreCase("River")) {

                sb.append(getTagString("natural", "water"));
            } else if (objectClass.equalsIgnoreCase("Agricultural Plot")) {   
                sb.append(getTagString("landuse", "farm"));
            } else if (objectClass.equalsIgnoreCase("Beach")) { 
                sb.append(getTagString("natural", "beach"));
            } else if (objectClass.equalsIgnoreCase("Building")) { 
                sb.append(getTagString("building", "yes"));
            } else if (objectClass.equalsIgnoreCase("Commercial Area")) { 
                sb.append(getTagString("landuse", "commercial"));
            } else if (objectClass.equalsIgnoreCase("Country - Filled")) { 
                sb.append(getTagString("boundary", "administrative"));
            } else if (objectClass.equalsIgnoreCase("Forest")) { 
                sb.append(getTagString("landuse", "forest"));
            } else if (objectClass.equalsIgnoreCase("Grass Field")) { 
                sb.append(getTagString("landuse", "grass"));
            } else if (objectClass.equalsIgnoreCase("Industrial Area")) { 
                sb.append(getTagString("landuse", "industrial"));
            } else if (objectClass.equalsIgnoreCase("Island")) {    
                sb.append(getTagString("place", "island"));
            } else if (objectClass.equalsIgnoreCase("Market")) {  
                sb.append(getTagString("amenity", "marketplace"));
            } else if (objectClass.equalsIgnoreCase("Park")) {                        
                sb.append(getTagString("leisure", "park"));            
            } else if (objectClass.equalsIgnoreCase("Parking Lot")) {  
                sb.append(getTagString("amenity", "parking"));
            } else if (objectClass.equalsIgnoreCase("Protected Area")) {  
                sb.append(getTagString("boundary", "protected_area"));
            } else if (objectClass.equalsIgnoreCase("Reef")) {  
                sb.append(getTagString("natural", "reef"));
            } else if (objectClass.equalsIgnoreCase("Residential Area")) { 
                sb.append(getTagString("landuse", "residential"));
            } else if (objectClass.equalsIgnoreCase("School")) {  
                sb.append(getTagString("amenity", "school"));
            } else if (objectClass.equalsIgnoreCase("Small Island")) {  
                sb.append(getTagString("place", "island"));
            } else if (objectClass.equalsIgnoreCase("Sports Field")) { 
                sb.append(getTagString("leisure", "pitch"));
            } else if (objectClass.equalsIgnoreCase("Stadium")) {   
                sb.append(getTagString("leisure", "stadium"));
            } else if (objectClass.equalsIgnoreCase("University")) {  
                sb.append(getTagString("amenity", "university"));
            } else if (objectClass.equalsIgnoreCase("Wetland")) {   
                sb.append(getTagString("natural", "wetland"));
            }        
        } //end LineString check
        
        if (object instanceof Polygon) {
            if (objectClass.equalsIgnoreCase("Border - Country Border")) {

            } else if (objectClass.equalsIgnoreCase("Border - Inter-Country")) {    
            } else if (objectClass.equalsIgnoreCase("Coastline")) { 
                sb.append(getTagString("natural", "coastline"));
            } else if (objectClass.equalsIgnoreCase("Ferry Line")) { 
                sb.append(getTagString("route", "ferry"));
            } else if (objectClass.equalsIgnoreCase("Rail Line")) { 
            } else if (objectClass.equalsIgnoreCase("Rail - Platform")) { 
            } else if (objectClass.equalsIgnoreCase("Rail - Tram")) {    

            } else if (objectClass.equalsIgnoreCase("Path - Bikeway")) {
                sb.append(getTagString("highway", "cycleway"));
            } else if (objectClass.equalsIgnoreCase("Hiking Trail")) {
                sb.append(getTagString("highway", "footway"));
            } else if (objectClass.equalsIgnoreCase("Road - Primary Highway")) {
                sb.append(getTagString("highway", "motorway"));          
            } else if (objectClass.equalsIgnoreCase("Road - Primary Highway Link")) {
                sb.append(getTagString("highway", "motorway_link"));          
            } else if (objectClass.equalsIgnoreCase("Road - Primary Highway")) {
                sb.append(getTagString("highway", "primary"));    
            } else if (objectClass.equalsIgnoreCase("Road - Primary Highway Link")) {
                sb.append(getTagString("highway", "primary_link"));      
            } else if (objectClass.equalsIgnoreCase("Road - City Secondary")) {
                sb.append(getTagString("highway", "residential"));       
            } else if (objectClass.equalsIgnoreCase("Road - Secondary Highway")) {
                sb.append(getTagString("highway", "secondary"));        
            } else if (objectClass.equalsIgnoreCase("Road - Secondary Highway Link")) {
                sb.append(getTagString("highway", "secondary_link"));      
            } else if (objectClass.equalsIgnoreCase("Road - City Tertiary")) {
                sb.append(getTagString("highway", "tertiary"));       
            } else if (objectClass.equalsIgnoreCase("Path - Steps")) {
                sb.append(getTagString("highway", "steps"));   
            } else if (objectClass.equalsIgnoreCase("Road - Unclassified")) {
                sb.append(getTagString("highway", "unclassified"));  
            } else if (objectClass.equalsIgnoreCase("Water Way - Intermittent Stream")) { 
                sb.append(getTagString("waterway",     "stream"));
                sb.append(getTagString("intermittent", "yes"));
            } else if (objectClass.equalsIgnoreCase("Water Way - River")) { 
                sb.append(getTagString("waterway", "river"));
            } else if (objectClass.equalsIgnoreCase("Water Way - Stream")) {              
                sb.append(getTagString("waterway", "stream"));
            }
        } //end polygon check
        
        return sb.toString();
    }
    
    private String getTagString(String key, float value) {
        StringBuilder sb = new StringBuilder();

        sb.append("<tag k='");
        sb.append(key);
        sb.append("' v='");
        sb.append(value);
        sb.append("' />\n");
        
        return sb.toString();
    }    
    
    private String getTagString(String key, String value) {
        StringBuilder sb = new StringBuilder();

        sb.append("<tag k='");
        sb.append(key);
        sb.append("' v='");
        sb.append(value);
        sb.append("' />\n");
        
        return sb.toString();
    }
    
    private String getWayTagOpen(long id, String timeStamp) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("<way id='-");
        sb.append(id);
        sb.append("' timestamp='");
        sb.append(timeStamp);
        sb.append("' version='1'");        
        sb.append(">\n");
        
        return sb.toString();
    }
    
    /**
     * Returns the Coordinate from the provided ArrayList using the given
     * Coordinate.  Returns null is none of the points are using the Coordinate.
     * 
     * @param points
     * @param c
     * @return 
     */
    private MapPoint pointUsingCoordinate(ArrayList<MapPoint> points, Coordinate c) {
        MapPoint point = null;
        
        for (MapPoint p: points) {
            if (p.getCoordinateList().get(0).equals(c)) {
                point = p;
                break;
            }
        }
        
        return point;
    }
    
}
