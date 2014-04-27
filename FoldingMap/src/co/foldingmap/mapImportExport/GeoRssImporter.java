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
import co.foldingmap.map.vector.VectorObjectList;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.MapPoint;
import co.foldingmap.map.vector.LineString;
import co.foldingmap.map.vector.Polygon;
import co.foldingmap.GUISupport.ProgressIndicator;
import co.foldingmap.Logger;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.Layer;
import co.foldingmap.xml.XMLParser;
import co.foldingmap.xml.XMLTag;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Importer for GeoRss Feeds
 * Reference <link>http://georss.org/gml</link>
 * 
 * @author Alec
 */
public class GeoRssImporter implements FormatImporter {
    private VectorObjectList<VectorObject> objects;
    private String                         feedDate, feedDescription, feedLanguage;
    private String                         feedLink, feedTitle;
            
    public GeoRssImporter() {        
        objects = new VectorObjectList<VectorObject>();
    }    
    
    public GeoRssImporter(NodeMap nodeMap, File rssFile) {        
        try {
            objects = new VectorObjectList<VectorObject>();
            parseFile(nodeMap, new XMLParser(new FileReader(rssFile)));
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in GeoRssImporter.constructor - " + e);
        }
    }
    
    public String getFeedDescription() {
        if (feedDescription == null) {
            return "";
        } else {
            return feedDescription;
        }
    }
    
    public String getFeedTitle() {
        if (feedTitle == null) {
            return "RSS";
        } else {
            return feedTitle;
        }
    }
    
    public VectorObjectList<VectorObject> getObjects() {
        return objects;
    }
    
    public static VectorObject getItem(NodeMap nodeMap, XMLTag entryTag) {
        Coordinate                  coordinate;
        CoordinateList<Coordinate>  coordinates;
        int                         subjectIndex;
        String                      coordinateStr, description, lat, lon;
        String                      link, pubDate, title;
        String                      coordinateString, token1, token2;
        StringTokenizer             st;
        VectorObject                object;
        XMLTag                      exteriorTag, interiorTag, linkTag;
        XMLTag                      linearTag, pointTag, polygonTag, whereTag;
        
        object = null;
        
        try {
            title       = entryTag.getSubtagContent("title");
            linkTag     = entryTag.getSubtagsByName("link").get(0);
            description = entryTag.getSubtagContent("description");
            pubDate     = entryTag.getSubtagContent("pubDate");
            
            if (linkTag != null) {
                if (linkTag.containsSubTag("href")) {
                    link = linkTag.getSubtagContent("href");
                } else {
                    link = linkTag.getPropertyValue("href");
                }
            }
            
            if (entryTag.containsSubTag("georss:point")) {
                coordinateStr = entryTag.getSubtagContent("georss:point");
                
                if (coordinateStr.contains(",")) {                
                    st        = new StringTokenizer(coordinateStr, ",");
                } else {
                    st        = new StringTokenizer(coordinateStr);
                }
                
                token1        = st.nextToken();
                token2        = st.nextToken();
                coordinate    = new Coordinate(0, Float.parseFloat(token1), Float.parseFloat(token2));             
                object        = new MapPoint(title, "(Unspecified Point)", description, coordinate);    
                
                nodeMap.put(coordinate);
            } else if (entryTag.containsSubTag("gml:Polygon")) {
                polygonTag    = entryTag.getSubtag("gml:Polygon");
                coordinates   = new CoordinateList<Coordinate>();
                        
                if (polygonTag.containsSubTag("gml:exterior")) {
                    exteriorTag   = polygonTag.getSubtag("gml:exterior");
                    linearTag     = exteriorTag.getSubtag("gml:LinearRing");                    
                    coordinates   = parsePosList(nodeMap, linearTag.getSubtag("gml:posList"));                    
                    object        = new Polygon(title, "(Unspecified Polygon)", coordinates);
                }
                
                if (polygonTag.containsSubTag("gml:interior")) {
                    interiorTag = polygonTag.getSubtag("gml:interior");
                    
                    //TODO add interior
                }
                
            } else if (entryTag.containsSubTag("geo:lat")) {
                lat           = entryTag.getSubtagContent("geo:lat");
                lon           = entryTag.getSubtagContent("geo:long");
                coordinate    = new Coordinate(0, Float.parseFloat(lat), Float.parseFloat(lon));    
                object        = new MapPoint(title, "(Unspecified Point)", description, coordinate);
                nodeMap.put(coordinate);              
            } else if (entryTag.containsSubTag("georss:where")) {                
                whereTag      = entryTag.getSubtag("georss:where");
                
                if (whereTag.containsSubTag("gml:Point")) {
                    pointTag    = whereTag.getSubtag("gml:Point");
                    coordinate  = parseCoordinate(pointTag.getSubtag("gml:pos"));
                    object      = new MapPoint(title, "(Unspecified Point)", description, coordinate);
                    
                    nodeMap.put(coordinate); 
                    object.setDescription(description);
                } else if (whereTag.containsSubTag("gml:LineString")) {
                    linearTag   = whereTag.getSubtag("gml:LineString");
                    coordinates = parsePosList(nodeMap, linearTag.getSubtag("gml:posList"));                  
                    object      = new LineString(title, "(Unspecified Linestring)", coordinates);
                    
                    object.setDescription(description);
                } else if (whereTag.containsSubTag("gml:Polygon")) {
                    polygonTag  = whereTag.getSubtag("gml:Polygon");
                    exteriorTag = polygonTag.getSubtag("gml:exterior");
                    linearTag   = exteriorTag.getSubtag("gml:LinearRing");
                    coordinates = parsePosList(nodeMap, linearTag.getSubtag("gml:posList")); 
                    object      = new Polygon(title, "(Unspecified Polygon)", coordinates);
                    
                    //TODO write exterior 
                    
                    object.setDescription(description);
                }
            } 
            
            if (entryTag.containsSubTag("dc:subject")) { 
                subjectIndex = 0;
                
                for (XMLTag tag: entryTag.getSubtags("dc:subject")) {
                    if (object != null) {
                        object.addCustomDataField("Subject" + subjectIndex, tag.getTagContent());                        
                        subjectIndex++;
                    }                              
                }
            }
            
        } catch (Exception e) {            
            Logger.log(Logger.ERR, "Error in GeoRssImporter.getItem(XMLTag) - " + e);            
        }
        
        return object;
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
        objects = new VectorObjectList<VectorObject>();
        parseFile(nodeMap, new XMLParser(new FileReader(mapFile)));

        layer.setName(getFeedTitle());
        layer.setLayerDescription(getFeedDescription());
        
        if (layer instanceof VectorLayer) {
            VectorLayer vecLayer = (VectorLayer) layer;
            
            vecLayer.getObjectList().clear();

            for (VectorObject object: objects) {            
                //Add Coordinates to the NodeMap
                for (Coordinate c: object.getCoordinateList())
                    nodeMap.put(c);

                vecLayer.addObject(object);
            }         
        } else {
            Logger.log(Logger.ERR, "Error in GeoRssImporter.importToLayer(File, NodeMap, Layer, ProgressIndicator) - Supplied Layer must be a VectorLayer.");
        }
    }

    @Override
    public DigitalMap importAsMap(File mapFile, ProgressIndicator progressIndicator) throws IOException {
        DigitalMap  mapData;
        VectorLayer layer;
        
        mapData = new DigitalMap();
        layer   = new VectorLayer();
        
        mapData.addLayer(layer);
        
        importToLayer(mapFile, mapData.getCoordinateSet(), layer, progressIndicator);
        
        return mapData;
    }    
    
    private static Coordinate parseCoordinate(XMLTag tag) {
        Coordinate      coordinate;
        String          token1, token2;
        StringTokenizer st;
        
        st = new StringTokenizer(tag.getTagContent());
        
        if (st.countTokens() >= 2) {
            token1     = st.nextToken(); //lat
            token2     = st.nextToken(); //lon
            coordinate = new Coordinate(0, Float.parseFloat(token1), Float.parseFloat(token2));
        } else {
            coordinate = null;
        }       
        
        return coordinate;
    }
    
    private void parseFile(NodeMap nodeMap, XMLParser parser) {
        ArrayList<XMLTag>   itemTags;
        VectorObject        object;
        XMLTag              channelTag, rssTag, tag;
        
        tag      =   parser.parseDocument();
        itemTags   = tag.getSubtagsByName("feed");
        channelTag = null;
        
        if (tag.containsSubTag("feed") || (itemTags.size() > 0)) {
            channelTag = itemTags.get(0);
        } else if (tag.containsSubTag("rss")) {
            rssTag          = tag.getSubtag("rss");
            channelTag      = rssTag.getSubtag("channel");
        }
        
        if (channelTag != null) {
            feedTitle       = channelTag.getSubtagContent("title");
            feedLink        = channelTag.getSubtagContent("link");
            feedDescription = channelTag.getSubtagContent("description");
            feedLanguage    = channelTag.getSubtagContent("language");
            feedDate        = channelTag.getSubtagContent("pubDate");
            itemTags        = channelTag.getSubtagsByName("item");
            
            if (itemTags.isEmpty())
                itemTags = channelTag.getSubtagsByName("entry");
                
            //read objects
            for (XMLTag itemTag: itemTags) {
                object = getItem(nodeMap, itemTag);
                
                if (object != null)
                    objects.add(getItem(nodeMap, itemTag));
            }

        }
    }
    
    /**
     * Returns a Coordinate list from the GeoRSS posList XML tag.
     * 
     * Example:
     *   <gml:posList>45.256 -110.45 46.46 -109.48 43.84 -109.86</gml:posList>
     * 
     * @param tag
     * @return 
     */
    private static CoordinateList<Coordinate> parsePosList(NodeMap nodeMap, XMLTag tag) {
        Coordinate                 c;
        CoordinateList<Coordinate> list;
        String                     token1, token2;
        StringTokenizer            st;
        
        list = new CoordinateList<Coordinate>();
        st   = new StringTokenizer(tag.getTagContent());
        
        while (st.countTokens() >= 2) {
            token1 = st.nextToken(); //lat
            token2 = st.nextToken(); //lon
            c      = new Coordinate(0, Float.parseFloat(token1), Float.parseFloat(token2));
            
            list.add(c);
            nodeMap.put(c);
        }
        
        return list;
    }
}
