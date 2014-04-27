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
import co.foldingmap.map.MercatorProjection;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.Visibility;
import co.foldingmap.map.Layer;
import co.foldingmap.map.vector.LinearRing;
import co.foldingmap.map.vector.Region;
import co.foldingmap.map.vector.NetworkLayer;
import co.foldingmap.map.vector.MultiGeometry;
import co.foldingmap.map.vector.LatLonAltBox;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.NodeMap;
import co.foldingmap.map.vector.CoordinateList;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;
import co.foldingmap.map.vector.LevelOfDetail;
import co.foldingmap.map.vector.LineString;
import co.foldingmap.map.vector.MapIcon;
import co.foldingmap.map.vector.Polygon;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.MapPoint;
import co.foldingmap.map.vector.InnerBoundary;
import co.foldingmap.map.themes.PolygonStyle;
import co.foldingmap.map.themes.ColorHelper;
import co.foldingmap.map.themes.MapTheme;
import co.foldingmap.map.themes.ColorStyle;
import co.foldingmap.map.themes.LabelStyle;
import co.foldingmap.map.themes.IconStyle;
import co.foldingmap.map.themes.LineStyle;
import co.foldingmap.GUISupport.ProgressBarPanel;
import co.foldingmap.GUISupport.ProgressIndicator;
import co.foldingmap.Logger;
import co.foldingmap.map.raster.ImageOverlay;
import co.foldingmap.map.tile.TileMath;
import co.foldingmap.xml.XMLParser;
import co.foldingmap.xml.XMLTag;
import java.awt.Color;
import java.awt.Font;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Class for loading KML files.
 * 
 * @author Alec
 */
public class KmlImport implements FormatImporter {
    
    public KmlImport() {
        
    }
    
    /**
     * Creates a new Visibility object from a Region.
     * If the region does not contain a LevelOfDetail then a Visibility object with max = 25 and 
     * min = 0 is created.
     * 
     * @param r
     * @return 
     */
    public static Visibility convertRegionToVisibility(Region r) {
        LevelOfDetail lod = r.getLevelOfDetail();
        
        if (lod != null) {
            float max = r.calculateVectorZoomLevelForLOD(lod.getMaxLodPixels());
            float min = r.calculateVectorZoomLevelForLOD(lod.getMinLodPixels());

            return new Visibility(TileMath.getTileMapZoom(max), TileMath.getTileMapZoom(min));
        } else {
            return new Visibility(25, 0);
        }
    }        
    
    public static CoordinateList<Coordinate> getCoordinateList(NodeMap coordinateSet,
                                                               String  coordinateString) {
        Coordinate                  coordinate, existingCoordinate;
        CoordinateList<Coordinate>  coordinates;
        long                        nodeId;
        String                      coordinateGroupString;
        StringTokenizer             coordinateGroupTokenizer;
        
        try {            
            coordinateGroupTokenizer = new StringTokenizer(coordinateString);
            coordinates              = new CoordinateList<Coordinate>(coordinateGroupTokenizer.countTokens());            
            
            while (coordinateGroupTokenizer.hasMoreTokens()) {            
                coordinateGroupString = coordinateGroupTokenizer.nextToken();                                                                                                     
                coordinate            = new Coordinate(coordinateGroupString);   

                if (coordinate != null) {
                    //check to see if the coordinate already exists in the map
                    nodeId = coordinateSet.findKey(coordinate);

                    if (nodeId > 0) {
                        //the coordinate already exists
                        existingCoordinate = coordinateSet.get(nodeId);

                        if (existingCoordinate != null) {                                
                            coordinates.add(existingCoordinate);                            
                        } else {
                            Logger.log(Logger.ERR, "Error node id: " + nodeId + "not found");
                        }                            
                    } else {
                        coordinateSet.put(coordinate);
                        coordinates.add(coordinate);                       
                    }                                                       
                } else {
                    Logger.log(Logger.ERR, "Error in KmlImport.getCoordinateList(NodeMap, String) - Error paring Coordinates.");
                } // null coordinate check
            }
            
            return coordinates;
        } catch (Exception e) {
            System.err.println("Error in KmlImporter.getCoordinateList(NodeMap, String - " + e);
            return new CoordinateList<Coordinate>(1);
        }               
    }    
    
    /**
     * Reads gxTags to create a HashMap of customDataFields.
     * This is the old way of reading in custom data in TrucGIS.
     * 
     * @param gxTags
     * @return 
     */
    public static HashMap<String,String> getCustomDataFields(ArrayList<XMLTag> gxTags) {
        HashMap<String,String>  customDataFields;
        String                  key, value;
        
        customDataFields = new HashMap<String,String>();
        
        try {
            for (XMLTag currentGXTag: gxTags) {
                key     = currentGXTag.getTagName().substring(3);
                value   = currentGXTag.getTagContent();

                if ( (!currentGXTag.getTagName().equalsIgnoreCase("gx:Timestamps")) &&
                        (!currentGXTag.getTagName().equalsIgnoreCase("gx:LineStroke")) &&
                        (!currentGXTag.getTagName().equalsIgnoreCase("gx:locked"))     &&
                        (!currentGXTag.getTagName().equalsIgnoreCase("gx:Track"))      &&
                        (!currentGXTag.getTagName().equalsIgnoreCase("gx:outlineColor")) ) {
                        customDataFields.put(key, value);                                 
                }
            } //end for loop        
        } catch (Exception e) {
            System.err.println("Error in KmlImport.getCustomDataFields(ArrayList) - " + e);
        }
                                        
        return customDataFields;
    }
    
    /**
     * Reads the ExtendedData tag to read in object data
     * 
     * @param extendedDataTag
     * @return 
     */
    public static HashMap<String, String> getExtendedData(XMLTag extendedDataTag) {
        HashMap<String,String>  customDataFields;
        String                  key, value, tagName;
        
        customDataFields = new HashMap<String,String>();
        
        try {
            for (XMLTag subtag: extendedDataTag.getSubtags()) {
                key     = "";
                value   = "";
                tagName = subtag.getTagName();
                
                if (tagName.substring(0,4).equalsIgnoreCase("Data")) {
                    key   = subtag.getTagValue();
                    value = subtag.getSubtagContent("value");
                    
                    customDataFields.put(key, value); 
                }
            }
        } catch (Exception e) {
            System.err.println("Error in KmlImport. getExtendedData(XMLTag) - " + e);
        }    
        
        return customDataFields;
    }
    
    /**
     * Loads a ColorStyle from a given style XMLTag
     */
    public static ColorStyle getStyle(XMLTag styleTag) {
        ColorStyle  style       = null;
        LabelStyle  labelStyle;
        XMLTag      labelTag;
        
        try {
            if (styleTag.containsSubTag("IconStyle")) {
                style = getIconStyle(styleTag);
            } else if (styleTag.containsSubTag("LineStyle")) {
                style = getLineStyle(styleTag);
            } else if (styleTag.containsSubTag("PolyStyle")) {
                style = getPolygonStyle(styleTag);
            }

            if (styleTag.containsSubTag("labelStyle")) {
                labelTag   = styleTag.getSubtag("labelStyle");
                labelStyle = getLabelStyle(labelTag);

                if (labelStyle != null)
                    style.setLabel(labelStyle);
            }        

            return style;
        } catch (Exception e) {
            System.err.println("Error in KmlImport.getStyle(XMLTag) - " + e);
            return null;
        }
    }
    
    /**
     * Creates a ImageOverlay object from the ImageOverlay XML.
     * 
     * @param tag
     * @return 
     */
    public static ImageOverlay getGroundOverlay(XMLTag tag) {
        ImageOverlay   groundOverlay;
        LatLonAltBox    bounds;
        MapIcon         mapIcon;
        String          id;
        XMLTag          iconTag, latLonAltTag;
        
        id            = tag.getTagValue();
        iconTag       = tag.getSubtag("Icon");
        latLonAltTag  = tag.getSubtag("LatLonBox"); 
        mapIcon       = getIcon(iconTag);
        bounds        = getLatLonAltBox(latLonAltTag);
        
        if (id == null)
            id = "Ground Overlay";
        
        groundOverlay = new ImageOverlay(id, mapIcon, bounds);
        
        return groundOverlay;
    }
    
    /**
     * Loads the Icon XML object into a MapIcon object.
     * 
     * @param tag
     * @return 
     */
    public static MapIcon getIcon(XMLTag tag) {
        boolean     hasRefreshInterval, hasRefreshMode, hasViewRefreshMode;
        Float       refreshInterval; //in seconds                 
        MapIcon     mapIcon;
        String      address, id;
        String      refreshMode, viewRefreshMode;      
        
        id      = tag.getTagValue();
        address = tag.getSubtagContent("href");
        mapIcon = new MapIcon(id, address);
        
        hasRefreshInterval  = tag.containsSubTag("refreshInterval");
        hasRefreshMode      = tag.containsSubTag("refreshMode");
        hasViewRefreshMode  = tag.containsSubTag("viewRefreshMode");
        
        if (hasRefreshInterval) {
            refreshInterval = Float.parseFloat(tag.getSubtagContent("refreshInterval"));
            mapIcon.setRefreshInterval(refreshInterval);
        }
        
        if (hasRefreshMode) {
            refreshMode = tag.getSubtagContent("refreshMode");
            
            if (refreshMode.equalsIgnoreCase("onChange")) {
                mapIcon.setRefreshMode(MapIcon.ON_CHANGE);
            } else if (refreshMode.equalsIgnoreCase("onInterval")) {
                mapIcon.setRefreshMode(MapIcon.ON_INTERVAL);
            } else if (refreshMode.equalsIgnoreCase("onExpire")) {
                mapIcon.setRefreshMode(MapIcon.ON_EXPIRE);
            }                       
        }
        
        if (hasViewRefreshMode) {
            viewRefreshMode = tag.getSubtagContent("viewRefreshMode");
            
            if (viewRefreshMode.equalsIgnoreCase("never")) {
                mapIcon.setViewRefreshMode(MapIcon.NEVER);
            } else if (viewRefreshMode.equalsIgnoreCase("onStop")) {
                mapIcon.setViewRefreshMode(MapIcon.ON_STOP);
            } else if (viewRefreshMode.equalsIgnoreCase("onRequest")) {
                mapIcon.setViewRefreshMode(MapIcon.ON_REQUEST);
            } else if (viewRefreshMode.equalsIgnoreCase("onRegion")) {
                mapIcon.setViewRefreshMode(MapIcon.ON_REGION);
            }                        
        }
            
        return mapIcon;
    }
    
    /**
     * Returns an IconStyle given it's KML code.
     * 
     * @param styleTag
     * @return 
     */
    public static IconStyle getIconStyle(XMLTag styleTag) {
        try {
            boolean         hasHeading, hasIcon, hasLabel, hasOutline, hasScale;
            boolean         outline;
            Color           fillColor, outlineColor;
            float           heading, scale;
            IconStyle       newIconStyle;
            int             colorMode;
            LabelStyle      labelStyle;
            String          colorModeString, icon, styleID, styleTagName;
            XMLTag          iconTag, iconStyleTag;

            styleTagName    = styleTag.getTagName();            
            styleID         = styleTag.getTagValue();
            iconStyleTag    = styleTag.getSubtag("IconStyle");  
            fillColor       = ColorHelper.parseHexAlphabetical(iconStyleTag.getSubtagContent("color"));
            colorModeString = iconStyleTag.getSubtagContent("colorMode");
            hasScale        = iconStyleTag.containsSubTag("scale");
            hasHeading      = iconStyleTag.containsSubTag("heading");
            hasOutline      = iconStyleTag.containsSubTag("outline");
            hasIcon         = iconStyleTag.containsSubTag("Icon");
            hasLabel        = iconStyleTag.containsSubTag("LabelStyle");                                                                    

            if (colorModeString.equalsIgnoreCase("normal")) {
                colorMode = ColorStyle.NORMAL;
            } else {
                colorMode = ColorStyle.RANDOM;
            }
            
            newIconStyle = new IconStyle(styleID, fillColor);

            if (hasScale) {
                scale = Float.parseFloat(iconStyleTag.getSubtagContent("scale"));     
                newIconStyle.setScale(scale);
            }            
            
            if (hasHeading) { 
                heading = Float.parseFloat(iconStyleTag.getSubtagContent("heading"));
                newIconStyle.setHeading(heading);  
            }             
            
            if (hasOutline) {
                outline = Boolean.parseBoolean(iconStyleTag.getSubtagContent("outline"));
                
                if (outline) {
                    outlineColor = ColorHelper.parseHexAlphabetical(iconStyleTag.getSubtagContent("gx:outlineColor"));
                    newIconStyle.setOutlineColor(outlineColor);
                }                
            }                        
            
            newIconStyle.setColorMode(colorMode);

            if (hasIcon) {
                iconTag = iconStyleTag.getSubtag("Icon");
                icon = iconTag.getSubtagContent("href");
                newIconStyle.setImageFileName(icon);
            }

            if (hasLabel) {
                labelStyle = getLabelStyle(iconStyleTag.getSubtag("LabelStyle"));
                
                if (labelStyle != null)
                    newIconStyle.setLabel(labelStyle);
            }

            return newIconStyle;           
        } catch (Exception e) {
            System.err.println("Error in KmlImport.getIconStyle(XMLTag) - " + e);
            return null;
        }            
    }
    
    /**
     * Creates an InnerBoundary when given an InnerBoundary KML tag.
     * 
     * @param layer
     * @param ibTag
     * @return 
     */
    public static InnerBoundary getInnerBoundary(NodeMap nodeMap, VectorLayer layer, XMLTag ibTag) {
        CoordinateList<Coordinate>  coordinates;
        InnerBoundary               ib;
        String                      coordinateString;
        XMLTag                      linearRingTag;
        
        linearRingTag    = ibTag.getSubtag("LinearRing");
        coordinateString = linearRingTag.getSubtagContent("coordinates");
        coordinates      = getCoordinateList(nodeMap, coordinateString);
        ib               = new InnerBoundary(coordinates);
        
        return ib;
    }
    
    /**
     * Reads in the KML for LabelStyle and returns an object.
     * 
     * @param labelTag
     * @return 
     */
    public static LabelStyle getLabelStyle(XMLTag labelTag) {
        Color       fillColor, outlineColor;
        Font        labelFont;
        float       scale;
        int         fontSize;
        LabelStyle  labelStyle;
        String      scaleString;
        
        try {
            fillColor    = ColorHelper.parseHexAlphabetical(labelTag.getSubtagContent("color"));
            outlineColor = new Color(60, 68, 75);
            fontSize     = 12;

            if (labelTag.containsSubTag("scale")) {
                scaleString = labelTag.getSubtagContent("scale");
                scale       = Float.parseFloat(scaleString);
                fontSize    = (int) (fontSize * scale);
            }            
            
            if (fontSize >= 1) {            
                labelFont  = new Font(Font.SANS_SERIF, Font.BOLD,    fontSize);
                labelStyle = new LabelStyle(fillColor, outlineColor, labelFont);            
                
                return labelStyle;
            } else {
                Logger.log(Logger.WARN, "LabelStyle has font size of zero, disregarding style." );
                return null;
            }
            
            
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in KmlImport.getLabelStyle(XMLTag) - " + e);
            return null;
        }
    }
    
    /**
     * Reads in XML for LevelOfDetail and returns a LevelOfDetail object.
     * 
     * @param styleTag
     * @return 
     */
    public static LevelOfDetail getLevelOfDetail(XMLTag lodTag) {
        float           minLodPixels, maxLodPixels, minFadeExtent, maxFadeExtent;
        LevelOfDetail   lod;
                
        minLodPixels  = Float.parseFloat(lodTag.getSubtagContent("minLodPixels"));
        maxLodPixels  = Float.parseFloat(lodTag.getSubtagContent("maxLodPixels"));
        minFadeExtent = Float.parseFloat(lodTag.getSubtagContent("minFadeExtent"));
        maxFadeExtent = Float.parseFloat(lodTag.getSubtagContent("maxFadeExtent"));       
        
        lod = new LevelOfDetail(maxLodPixels, minLodPixels, maxFadeExtent, minFadeExtent);
        
        return lod;
    }
    
    /**
     * Creates a LatLonAltBox object from the XML LatLonAltBox.
     * 
     * @param tag
     * @return 
     */
    public static LatLonAltBox getLatLonAltBox(XMLTag tag) {
        boolean         hasMaxAltitude, hasMinAltitude;
        float           north, south, east, west, minAlt, maxAlt;
        LatLonAltBox    latLonAltBox;
        
        hasMaxAltitude  = tag.containsSubTag("maxAltitude");
        hasMinAltitude  = tag.containsSubTag("minAltitude");        
        
        north   = Float.parseFloat(tag.getSubtagContent("north"));
        south   = Float.parseFloat(tag.getSubtagContent("south"));
        east    = Float.parseFloat(tag.getSubtagContent("east"));
        west    = Float.parseFloat(tag.getSubtagContent("west"));

        if (hasMinAltitude) {
            minAlt = Float.parseFloat(tag.getSubtagContent("minAltitude"));
        } else {
            minAlt = -1;
        }

        if (hasMaxAltitude) {
            maxAlt = Float.parseFloat(tag.getSubtagContent("maxAltitude"));
        } else {
            maxAlt = -1;
        }            

        latLonAltBox = new LatLonAltBox(north, south, east, west,minAlt, maxAlt);        
        
        return latLonAltBox;
    }
            
    /**
     * Reads in KML for a LinearRing and returns a LinearRing object.
     * 
     * @param layer
     * @param placemarkTag
     * @return 
     */
    public static LinearRing getLinearRing(NodeMap nodeMap, VectorLayer layer, XMLTag placemarkTag) {
        try {
            ArrayList<XMLTag>           gxTags;
            boolean                     hasExtendedData, hasRegion, hasTimestamps;
            CoordinateList<Coordinate>  coordinates;
            HashMap<String,String>      customDataFields;
            LinearRing                  newRing;
            String                      altitudeMode, coordinateString, description;
            String                      objectName, styleUrl, timestamps;
            XMLTag                      extendedData, ringTag;

            objectName       = placemarkTag.getSubtagContent("name");
            description      = removeCDataTag(placemarkTag.getSubtagContent("description"));
            styleUrl         = placemarkTag.getSubtagContent("styleUrl");
            hasExtendedData  = placemarkTag.containsSubTag("ExtendedData");
            hasRegion        = placemarkTag.containsSubTag("Region");
            ringTag          = placemarkTag.getSubtagStartsWith("LinearRing");
            altitudeMode     = ringTag.getSubtagContent("altitudeMode");
            coordinateString = ringTag.getSubtagContent("coordinates");
            coordinates      = getCoordinateList(nodeMap, coordinateString);
            hasTimestamps    = ringTag.containsSubTag("gx:Timestamps");
            gxTags           = ringTag.getGxSubtags();
             
            //Check for alternative format.
            if (styleUrl.equals("")) 
                styleUrl = ringTag.getTagValue();             

            if (styleUrl.startsWith("#"))
                styleUrl = styleUrl.substring(1);
                
            newRing = new LinearRing(objectName, styleUrl, coordinates);            
            newRing.setDescription(description);
            
            if (altitudeMode.equalsIgnoreCase("clampToGround")) {
                newRing.setAltitudeMode(VectorObject.CLAMP_TO_GROUND);
            } else if (altitudeMode.equalsIgnoreCase("relativeToGround")) {
                newRing.setAltitudeMode(VectorObject.RELATIVE_TO_GROUND);
            } else {
                newRing.setAltitudeMode(VectorObject.ABSOLUTE);
            } 
            
            if (hasExtendedData) {
                extendedData     = placemarkTag.getSubtag("ExtendedData");
                customDataFields = getExtendedData(extendedData);
                newRing.setCustomDataFields(customDataFields);
            } else {
                //use old extended data method for compatibility
                customDataFields = getCustomDataFields(gxTags);
                newRing.setCustomDataFields(customDataFields);
            }
            
            if (hasRegion) {
                Region     region = loadRegion(placemarkTag.getSubtag("Region"));
                Visibility vis    = convertRegionToVisibility(region);                
                newRing.setVisibility(vis);
            }

            if (hasTimestamps) {
                timestamps = ringTag.getSubtagContent("gx:Timestamps");
                newRing.setTimestamps(timestamps);
            }        

            return newRing;
        } catch (Exception e) {
            System.err.println("Error in KmlImport.getLinearRing(VectorLayer, XMLTag) - " + e);
            return null;
        }
    }
    
    /**
     * Reads KML for LineString and returns a LineString Object.
     * 
     * @param lineStringTag
     * @return 
     */
    public static LineString getLineString(NodeMap nodeMap, VectorLayer layer, XMLTag placemarkTag) {
        try {
            ArrayList<XMLTag>           gxTags;
            boolean                     hasExtendedData, hasRegion, hasTimestamps;
            CoordinateList<Coordinate>  coordinates;
            HashMap<String,String>      customDataFields;
            LineString                  newLine;
            String                      altitudeMode, coordinateString, description;
            String                      objectName, styleUrl, timestamps;
            XMLTag                      extendedData, linestringTag;

            objectName       = placemarkTag.getSubtagContent("name");
            description      = removeCDataTag(placemarkTag.getSubtagContent("description"));
            styleUrl         = placemarkTag.getSubtagContent("styleUrl");
            hasExtendedData  = placemarkTag.containsSubTag("ExtendedData");
            hasRegion        = placemarkTag.containsSubTag("Region");
            linestringTag    = placemarkTag.getSubtagStartsWith("LineString");
            altitudeMode     = linestringTag.getSubtagContent("altitudeMode");
            coordinateString = linestringTag.getSubtagContent("coordinates");
            coordinates      = getCoordinateList(nodeMap, coordinateString);
            hasTimestamps    = linestringTag.containsSubTag("gx:Timestamps");
            gxTags           = linestringTag.getGxSubtags();

            //Check for alternative format.
            if (styleUrl.equals("")) 
                styleUrl = linestringTag.getTagValue();            
            
            if (styleUrl.startsWith("#"))
                styleUrl = styleUrl.substring(1);            
            
            newLine = new LineString(objectName, styleUrl, coordinates);

            newLine.setDescription(description);

            if (altitudeMode.equalsIgnoreCase("clampToGround")) {
                newLine.setAltitudeMode(VectorObject.CLAMP_TO_GROUND);
            } else if (altitudeMode.equalsIgnoreCase("relativeToGround")) {
                newLine.setAltitudeMode(VectorObject.RELATIVE_TO_GROUND);
            } else {
                newLine.setAltitudeMode(VectorObject.ABSOLUTE);
            }             

            if (hasExtendedData) {
                extendedData     = placemarkTag.getSubtag("ExtendedData");
                customDataFields = getExtendedData(extendedData);
                newLine.setCustomDataFields(customDataFields);
            } else {
                //use old extended data method for compatibility
                customDataFields = getCustomDataFields(gxTags);
                newLine.setCustomDataFields(customDataFields);
            }            
            
            if (hasRegion) {
                Region     region = loadRegion(placemarkTag.getSubtag("Region"));
                Visibility vis    = convertRegionToVisibility(region);   
                newLine.setVisibility(vis);
            }

            if (hasTimestamps) {
                timestamps = linestringTag.getSubtagContent("gx:Timestamps");
                newLine.setTimestamps(timestamps);
            }        

            if (coordinates.size() > 0) {            
                return newLine;
            } else {                
                System.err.println("Error in KmlImport.getLineString(VectorLayer, XMLTag) - No Coordinate Data For Object: " + objectName);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error in KmlImport.getLineString(VectorLayer, XMLTag) - " + e);
            return null;
        }
    }
    
    /**
     * Reads in LineStyle XML and returns a LineStyle Object.
     * 
     * @param styleTag
     * @return 
     */
    public static LineStyle getLineStyle(XMLTag styleTag) {
        try {
            boolean     hasStroke;
            Color       fillColor, outlineColor;
            float       lineWidth;
            int         outline;
            LineStyle   lineStyle;
            String      lineStroke, styleID, styleTagName;
            XMLTag      lineStyleTag;

            styleTagName = styleTag.getTagName();
            styleID      = styleTagName.substring(styleTagName.indexOf("id=\"") + 4,  styleTagName.length() - 1);
            lineStyleTag = styleTag.getSubtag("LineStyle");
            fillColor    = ColorHelper.parseHexAlphabetical(lineStyleTag.getSubtagContent("color"));
            lineWidth    = Float.parseFloat(lineStyleTag.getSubtagContent("width"));                          
            hasStroke    = lineStyleTag.containsSubTag("gx:LineStroke");

            if (lineStyleTag.containsSubTag("outline")) {
                outline = Integer.parseInt(lineStyleTag.getSubtagContent("outline"));
            } else {
                outline = 0;
            }
            
            if (hasStroke) {
                lineStroke = lineStyleTag.getSubtagContent("gx:LineStroke");
            } else {
                lineStroke = LineStyle.SOLID;
            }            
            
            lineStyle = new LineStyle(styleID, fillColor, lineWidth, lineStroke, true); 

            if (outline == 1) {
                outlineColor = ColorHelper.parseHexAlphabetical(lineStyleTag.getSubtagContent("gx:outerColor"));
                lineStyle.setOutlineColor(outlineColor);
                lineStyle.setOutline(true);
            } else {
                lineStyle.setOutline(false);
            }
            
            return lineStyle;
        } catch (Exception e) {
            System.err.println("Error in KmlImport.getLineStyle(XMLTag) - " + e);
            return null;
        }
    }
    
    /**
     * Reads in KML for MapPoint and returns a MapPoint object.
     * 
     * @param pointTag
     * @return 
     */
    public static MapPoint getMapPoint(NodeMap nodeMap, VectorLayer layer, XMLTag placemarkTag) {
        try {
            ArrayList<XMLTag>           gxTags;
            boolean                     hasExtendedData, hasRegion, hasTimestamps;
            CoordinateList<Coordinate>  coordinates;
            HashMap<String,String>      customDataFields;
            MapPoint                    newPoint;
            String                      coordinateString, description;
            String                      objectName, styleUrl, timestamps;
            XMLTag                      extendedData, pointTag;

            objectName       = placemarkTag.getSubtagContent("name");
            description      = removeCDataTag(placemarkTag.getSubtagContent("description"));
            styleUrl         = placemarkTag.getSubtagContent("styleUrl");
            hasExtendedData  = placemarkTag.containsSubTag("ExtendedData");
            hasRegion        = placemarkTag.containsSubTag("Region");
            pointTag         = placemarkTag.getSubtagStartsWith("Point");     
            coordinateString = pointTag.getSubtagContent("coordinates");
            coordinates      = getCoordinateList(nodeMap, coordinateString);
            hasTimestamps    = pointTag.containsSubTag("gx:Timestamps");
            gxTags           = pointTag.getGxSubtags();              

            if (styleUrl.startsWith("#"))
                styleUrl = styleUrl.substring(1);            
            
            newPoint = new MapPoint(objectName, styleUrl, description, coordinates);

            if (hasExtendedData) {
                extendedData     = placemarkTag.getSubtag("ExtendedData");
                customDataFields = getExtendedData(extendedData);
                newPoint.setCustomDataFields(customDataFields);
            } else {
                //use old extended data method for compatibility
                customDataFields = getCustomDataFields(gxTags);
                newPoint.setCustomDataFields(customDataFields);
            }             
            
            if (hasRegion) {
                Region     region = loadRegion(placemarkTag.getSubtag("Region"));
                Visibility vis    = convertRegionToVisibility(region);   
                newPoint.setVisibility(vis);
            }

            if (hasTimestamps) {
                timestamps = pointTag.getSubtagContent("gx:Timestamps");
                newPoint.setTimestamps(timestamps);
            }

            if (coordinates.size() > 0) {
                return newPoint;
            } else {
                System.err.println("Error in KmlImport.getMapPoint(VectorLayer, XMLTag) - No Coordinate Data For Object: " + objectName);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error in KmlImport.getMapPoint(VectorLayer, XMLTag) - " + e);
            return null;
        }
    }
    
    /**
     * Reads in KML for MultiGeometry and returns a MultiGeometry object.
     * 
     * @param layer
     * @param placemarkTag
     * @return 
     */
    public static MultiGeometry getMultiGeometry(NodeMap nodeMap, VectorLayer layer, XMLTag placemarkTag) {
        ArrayList<XMLTag>               gxTags;
        boolean                         hasExtendedData;
        HashMap<String,String>          customDataFields;
        VectorObjectList<VectorObject>  objects;
        MultiGeometry                   newMulti;
        String                          description, objectName;
        XMLTag                          extendedData, multiTag, placemarkNew;
        
        try {
            objectName       = placemarkTag.getSubtagContent("name");
            hasExtendedData  = placemarkTag.containsSubTag("ExtendedData");
            description      = removeCDataTag(placemarkTag.getSubtagContent("description"));
            multiTag         = placemarkTag.getSubtagStartsWith("MultiGeometry");            
            objects          = new VectorObjectList<VectorObject>();
            gxTags           = multiTag.getGxSubtags();
            
            //Load Objects
            if (multiTag.containsSubTag("PlaceMark")) {
                for (XMLTag tag: multiTag.getSubtagsByName("PlaceMark")) 
                    objects.add(loadPlacemark(nodeMap, layer, tag));                               
            } else {
                for (XMLTag objectTag: multiTag.getSubtags()) {
                    placemarkNew = new XMLTag("PlaceMark", objectTag);
                    objects.add(loadPlacemark(nodeMap, layer, placemarkNew));            
                }
            }                     
            
            newMulti = new MultiGeometry(objectName, objects);
            
            if (hasExtendedData) {
                extendedData     = placemarkTag.getSubtag("ExtendedData");
                customDataFields = getExtendedData(extendedData);
                newMulti.setCustomDataFields(customDataFields);
            } else {
                //use old extended data method for compatibility
                customDataFields = getCustomDataFields(gxTags);
                newMulti.setCustomDataFields(customDataFields);
            }             
            
            return newMulti;
        } catch (Exception e) {
            System.err.println("Error in KmlImport.getMultiGeometry(VectorLayer, XMLTag) - " + e);
            return null;
        }        
    }
    
    /**
     * Creates a NetworkLayer object from the a NetworkLayer Xml tag.
     * 
     * @param mapData
     * @param tag
     * @return 
     */
    public static NetworkLayer getNetworkLink(DigitalMap mapData, XMLTag tag) {
        boolean         hasDescription, hasName, hasRegion;
        boolean         hasRefreshInterval, hasRefreshMode;
        NetworkLayer    networkLink;
        String          address, description, name;
        String          refreshInterval, refreshMode;
        XMLTag          linkTag;
            
        try {
            hasDescription  = tag.containsSubTag("description");
            hasName         = tag.containsSubTag("Name");
            hasRegion       = tag.containsSubTag("Region");
            linkTag         = tag.getSubtag("Link");    
            
            if (linkTag == null) linkTag = tag.getSubtag("Url");
            
            address            = linkTag.getSubtagContent("href");
            hasRefreshInterval = linkTag.containsSubTag("refreshInterval");
            hasRefreshMode     = linkTag.containsSubTag("refreshMode");
            
            if (hasName) {
                name = tag.getSubtagContent("name");            
            } else {
                name = "Network Link";
            }

            networkLink = new NetworkLayer(name, address);

            if (mapData != null)
                networkLink.setParentMap(mapData);
                
            if (hasDescription) {
                description = tag.getSubtagContent("description");
                networkLink.setLayerDescription(description);
            }

            if (hasRegion) {
                Region     region = loadRegion(tag.getSubtag("Region"));
                Visibility vis    = convertRegionToVisibility(region);                   
                networkLink.setRegion(region);
            }

            if (hasRefreshInterval) {
                refreshInterval = linkTag.getSubtagContent("refreshInterval");
                networkLink.setRefreshInterval(Float.parseFloat(refreshInterval));
            }
            
            if (hasRefreshMode) {
                refreshMode = linkTag.getSubtagContent("refreshMode");
                networkLink.setRefreshMode(refreshMode);
                
                if (refreshMode.equalsIgnoreCase("onChange")) {
                    networkLink.setRefreshMode(NetworkLayer.ON_CHANGE);
                } else if (refreshMode.equalsIgnoreCase("onInterval")) {
                    networkLink.setRefreshMode(NetworkLayer.ON_INTERVAL);                     
                } else if (refreshMode.equalsIgnoreCase("onExpire")) {
                    networkLink.setRefreshMode(NetworkLayer.ON_EXPIRE);
                }
            }
            
            return networkLink;
        } catch (Exception e) {
            System.err.println("Error in KmlImport.getNetworkLink(XmlTag) - " + e);
            return null;
        }
    }
    
    /**
     * Creates MapObjects from KML code.
     *
     * @param  mapData       - The Digital Map the will contain these objects
     * @param  placeMarkTags - An ArrayList containing PlaceMark XMLTags
     * @return A collection of the new MapObjects created.
     */
    public static VectorObjectList<VectorObject> getObjectsFromPlaceMarks(NodeMap nodeMap, VectorLayer layer, ArrayList<XMLTag> placeMarkTags) {
        try {
            VectorObjectList<VectorObject>  newObjects;

            newObjects   = new VectorObjectList<VectorObject>();
        
            for (XMLTag currentPlaceMarkTag: placeMarkTags) {
                newObjects.add(loadPlacemark(nodeMap, layer, currentPlaceMarkTag));
            }
            
            return newObjects;
        } catch (Exception e) {
            System.err.println("Error in KmlImport.getObjectsFromPlaceMarks(VectorLayer, ArrayList<XMLTag>) - " + e);
            return null;
        }        
    }
    
    /**
     * Reads in KML for a Polygon and returns a Polygon Object.
     * 
     * @param layer
     * @param placemarkTag
     * @return 
     */
    public static Polygon getPolygon(NodeMap nodeMap, VectorLayer layer, XMLTag placemarkTag) {
        try {
            ArrayList<XMLTag>           gxTags, innerBoundaryTags;
            boolean                     hasExtendedData, hasRegion, hasTimestamps;
            CoordinateList<Coordinate>  coordinates;
            HashMap<String,String>      customDataFields;
            Polygon                     newPolygon;
            String                      coordinateString, description;
            String                      objectName, styleUrl, timestamps;
            XMLTag                      extendedData, lineStringTag, outerBoundaryTag, polygonTag;
            
            objectName        = placemarkTag.getSubtagContent("name");
            description       = removeCDataTag(placemarkTag.getSubtagContent("description"));
            styleUrl          = placemarkTag.getSubtagContent("styleUrl");
            hasExtendedData   = placemarkTag.containsSubTag("ExtendedData");
            hasRegion         = placemarkTag.containsSubTag("Region");
            polygonTag        = placemarkTag.getSubtagStartsWith("Polygon");
            outerBoundaryTag  = polygonTag.getSubtag("outerBoundaryIs");            
            innerBoundaryTags = polygonTag.getSubtags("innerBoundaryIs");
            lineStringTag     = outerBoundaryTag.getSubtag("LinearRing");
            coordinateString  = lineStringTag.getSubtagContent("coordinates");
            coordinates       = getCoordinateList(nodeMap, coordinateString);
            hasTimestamps     = polygonTag.containsSubTag("gx:Timestamps");
            gxTags            = polygonTag.getGxSubtags();
            
            if (styleUrl.equals(""))
                styleUrl = placemarkTag.getTagValue();
                
            if (styleUrl.startsWith("#"))
                styleUrl = styleUrl.substring(1);            
            
            newPolygon = new Polygon(objectName, styleUrl, coordinates);

            if (hasExtendedData) {
                extendedData     = placemarkTag.getSubtag("ExtendedData");
                customDataFields = getExtendedData(extendedData);
                newPolygon.setCustomDataFields(customDataFields);
            } else {
                //use old extended data method for compatibility
                customDataFields = getCustomDataFields(gxTags);
                newPolygon.setCustomDataFields(customDataFields);
            }             
            
            if (hasRegion) {
                Region     region = loadRegion(placemarkTag.getSubtag("Region"));
                Visibility vis    = convertRegionToVisibility(region);                  
                newPolygon.setVisibility(vis);
            }

            if (hasTimestamps) {
                timestamps = polygonTag.getSubtagContent("gx:Timestamps");
                newPolygon.setTimestamps(timestamps);
            }            
            
            for(XMLTag ibTag: innerBoundaryTags) 
                newPolygon.addInnerBoundary(getInnerBoundary(nodeMap, layer, ibTag));
            
            
            return newPolygon;
        } catch (Exception e) {
            System.err.println("Error in KmlImport.getPolygon(VectorLayer, XMLTag) - " + e);
            return null;
        }        
    }
    
    /**
     * Reads PolygonStyle from KML and returns a PolygonStyle object.
     * 
     * @param styleTag
     * @return 
     */
    public static PolygonStyle getPolygonStyle(XMLTag styleTag) {
        try {
            Color        fillColor, outlineColor;
            int          colorMode, fill, outline;
            PolygonStyle polygonStyle;
            String       colorModeString, styleID, styleTagName;
            XMLTag       polygonStyleTag;

            styleTagName    = styleTag.getTagName();
            styleID         = styleTagName.substring(styleTagName.indexOf("id=\"") + 4,  styleTagName.length() - 1);
            
            if (!styleTag.containsSubTag("PolyStyle")) {
                polygonStyleTag = styleTag;
            } else {
                polygonStyleTag = styleTag.getSubtag("PolyStyle");
            }
            
            fillColor       = ColorHelper.parseHexAlphabetical(polygonStyleTag.getSubtagContent("color"));
            colorModeString = polygonStyleTag.getSubtagContent("colorMode");
            fill            = Integer.parseInt(polygonStyleTag.getSubtagContent("fill"));
            outline         = Integer.parseInt(polygonStyleTag.getSubtagContent("outline"));

            if (colorModeString.equalsIgnoreCase("normal")) {
                colorMode = ColorStyle.NORMAL;
            } else {
                colorMode = ColorStyle.RANDOM;
            }            
            
            polygonStyle = new PolygonStyle(styleID, fillColor);

            polygonStyle.setColorMode(colorMode);
            
            if (fill == 1) {
                polygonStyle.setFill(true);
            } else {
                polygonStyle.setFill(false);
            }
                    
            if (outline == 1) {
                if (polygonStyleTag.containsSubTag("gx:outlineColor")) {
                    outlineColor = ColorHelper.parseHexAlphabetical(polygonStyleTag.getSubtagContent("gx:outlineColor"));
                    polygonStyle.setOutlineColor(outlineColor);
                }
                
                polygonStyle.setOutline(true);
            } else {
                polygonStyle.setOutline(false);
            }

            return polygonStyle;
        } catch (Exception e) {
            System.err.println("Error in KmlImport.getPolygonStyle(XMLTag) - " + e);
            return null;
        }
    }
    
    /**
     * Reads in a gx:Track tag and converts it to a LineString
     * 
     * @param layer
     * @param placemarkTag
     * @return 
     */
    public static LineString getTrack(NodeMap nodeMap, VectorLayer layer, XMLTag placemarkTag) {
        ArrayList<XMLTag>           times, coordinateTags;
        boolean                     hasExtendedData;
        Coordinate                  newCoordinate;
        CoordinateList<Coordinate>  coordinates;
        HashMap<String,String>      customDataFields;
        LineString                  newLine;
        String                      objectName, styleUrl;
        XMLTag                      extendedData, trackTag;
        
        try {
            objectName       = placemarkTag.getSubtagContent("name");
            styleUrl         = placemarkTag.getSubtagContent("styleUrl");
            trackTag         = placemarkTag.getSubtag("gx:Track");
            times            = trackTag.getSubtagsByName("when");
            coordinateTags   = trackTag.getSubtagsByName("gx:coord");
            coordinates      = new CoordinateList<Coordinate>();
            hasExtendedData  = placemarkTag.containsSubTag("ExtendedData");
            
            if (objectName == null)
                objectName = "";    
                
            for (int i = 0; i < coordinateTags.size(); i++) {
                XMLTag currentTag = coordinateTags.get(i);
                newCoordinate     = new Coordinate(currentTag.getTagContent());
                newCoordinate.setTimestamp(times.get(i).getTagContent());   
                coordinates.add(newCoordinate);
                nodeMap.put(newCoordinate);
            }
            
            newLine = new LineString(objectName, styleUrl, coordinates);
            
            if (hasExtendedData) {
                extendedData     = placemarkTag.getSubtag("ExtendedData");
                customDataFields = getExtendedData(extendedData);
                newLine.setCustomDataFields(customDataFields);
            }      
            
            return newLine;
        } catch (Exception e) {
            System.err.println("Error in KmlImport.getTrack(VectorLayer, XMLTag) - " + e);
            return null;
        }                
    }
    
    /**
     * Loads a layer from a file.
     * 
     * @param openedMap
     * @param folderTag
     * @return 
     */
    public static void loadLayer(DigitalMap openedMap, NodeMap nodeMap, XMLTag folderTag) {
        try {
            ArrayList<XMLTag>           tags;
            boolean                     hasGroundOverlay, layerLocked;
            VectorObject                newObject;
            String                      layerDescription, layerName, timeSpanBegin, timeSpanEnd;
            VectorLayer                 newLayer;
            XMLTag                      descriptionTag, timeSpanTag;

            descriptionTag   = folderTag.getSubtag("Description");
            layerName        = folderTag.getSubtagContent("Name");
            layerDescription = removeCDataTag(folderTag.getSubtagContent("Description"));
            layerLocked      = Boolean.parseBoolean(folderTag.getSubtagContent("gx:locked"));
            tags             = folderTag.getTags("Placemark");
            timeSpanTag      = folderTag.getSubtag("TimeSpan");
            hasGroundOverlay = folderTag.containsSubTag("GroundOverlay");
            newLayer         = new VectorLayer(layerName);    //create new VectorLayer
            
            openedMap.addLayer(newLayer);
            
            //description uses HTML
            if (layerDescription.equals("") && (descriptionTag != null))
                layerDescription = descriptionTag.getSubtagsAsString();

            //load timespan
            if (timeSpanTag != null) {
                timeSpanBegin = timeSpanTag.getSubtagContent("begin");
                timeSpanEnd   = timeSpanTag.getSubtagContent("end");
                newLayer.setTimeSpanBegin(timeSpanBegin);
                newLayer.setTimeSpanEnd(timeSpanEnd);
            }

            newLayer.setLocked(layerLocked);
            newLayer.setLayerDescription(layerDescription);

            for (XMLTag currentTag: tags) {
                if (currentTag.getTagName().equalsIgnoreCase("Placemark")) {
                    newObject = loadPlacemark(nodeMap, newLayer, currentTag);

                    if (newObject != null)
                        newLayer.addObject(newObject);
                } //end placemark tag check
            }

            if (hasGroundOverlay) {
                for (XMLTag groundOverlayTag: folderTag.getSubtags("GroundOverlay")) 
                    newLayer.addOverlay(getGroundOverlay(groundOverlayTag));                
            }
            
        } catch (Exception e) {
            System.err.println("Error in KmlImport.loadLayer(XMLTag) - " + e);
        }        
    }
    
    /**
     * Reads in a KML PlaceMark and returns a VectorObject representing it.
     * 
     * @param layer
     * @param placemarkTag
     * @return 
     */
    public static VectorObject loadPlacemark(NodeMap nodeMap, VectorLayer layer, XMLTag placemarkTag) {
        VectorObject   newObject;
        
        if (placemarkTag.getSubtagStartsWith("gx:Track") != null) {
            newObject = getTrack(nodeMap, layer, placemarkTag);
        } else if (placemarkTag.getSubtagStartsWith("MultiGeometry") != null) {
            newObject = getMultiGeometry(nodeMap, layer, placemarkTag);
        } else if (placemarkTag.getSubtagStartsWith("Polygon") != null) {
            newObject = getPolygon(nodeMap, layer, placemarkTag);
        } else if (placemarkTag.getSubtagStartsWith("Point") != null) {
            newObject = getMapPoint(nodeMap, layer, placemarkTag);
        } else if (placemarkTag.getSubtagStartsWith("LineString") != null) { 
            newObject = getLineString(nodeMap, layer, placemarkTag);
        } else if (placemarkTag.getSubtagStartsWith("LinearRing") != null) {      
            newObject = getLinearRing(nodeMap, layer, placemarkTag);
        } else {
            newObject = null;
        }
        
        return newObject;
    }
    
    /**
     * Creates a Region object from KML code.
     * 
     * @param regionTag
     * @return 
     */
    public static Region loadRegion(XMLTag regionTag) {
        try {
            LatLonAltBox    latLonAltBox;
            LevelOfDetail   lod;
            Region          newRegion;
            String          regionName;
            XMLTag          latLonAltBoxTag, lodTag;

            regionName      = regionTag.getTagValue();
            latLonAltBoxTag = regionTag.getSubtag("LatLonAltBox");
            lodTag          = regionTag.getSubtag("Lod");
            lod             = getLevelOfDetail(lodTag);            

            latLonAltBox = getLatLonAltBox(latLonAltBoxTag);
            newRegion    = new Region(regionName, latLonAltBox, lod);

            return newRegion;
            
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in KmlImport.loadRegion(XMLTag) - " + e);
            return null;
        }        
    }
    
    /**
     * Opens a KML file and returns a DigitalMap.
     * 
     * @param fileKML
     * @return 
     */
    @Override
    public DigitalMap importAsMap(File mapFile, ProgressIndicator progressIndicator) throws IOException {
        DigitalMap newMap;
        String     fileExtension, fileName;
        
        fileName      = mapFile.getName();
        fileExtension = fileName.substring(fileName.length() - 3);
                
        if (fileExtension.equalsIgnoreCase("kml")) {
            newMap = openKML(progressIndicator, mapFile, new NodeMap(2000));
        } else if (fileExtension.equalsIgnoreCase("kmz")) {            
            newMap = openKMZ(progressIndicator, mapFile);
        } else {
            throw new IOException("KMLImporter: Unsupported File Type.");
        }
        
        return newMap;
    }    
    

    /**
     * Opens a KML file and returns a DigitalMap.
     * 
     * @param progressBarPanel
     * @param fileKML
     * @param nodeMap
     * @return 
     */
    public static DigitalMap openKML(ProgressIndicator progressIndicator, File fileKML, NodeMap nodeMap) {
        boolean                 hasNetworkLink, zoomAltitude;
        ArrayList<XMLTag>       networkLinks, tags;
        ColorStyle              currentStyle;
        Coordinate              lookAtCoordinate;
        DigitalMap              openedMap;
        float                   mapLatitude, mapLongitude, zoomLevel;
        MapProjection           projection;
        MapTheme                mapTheme;
        String                  mapName;
        String                  currentTagName;        
        VectorLayer             currentLayer;
        XMLParser               mapXMLParser;
        XMLTag                  documentTag;
                
        zoomLevel    = 0.2f;
        zoomAltitude = false;
        currentLayer = new VectorLayer("New Layer");
        
        if (progressIndicator == null) 
            progressIndicator = new ProgressBarPanel();        
        
        //read in xml file
        try {
            mapXMLParser = new XMLParser(new FileReader(fileKML));
            documentTag  = mapXMLParser.parseDocument();

            //get map data
            mapName             = documentTag.getSubtagContent("Name");
            tags                = documentTag.getTagSubtags("LookAt");
            mapLatitude         = 0;
            mapLongitude        = 0;

            for (XMLTag currentSubtag: tags) {
                progressIndicator.updateProgress("Reading Header", 10);
                
                if (currentSubtag.getTagName().equalsIgnoreCase("longitude")) {
                    mapLongitude = Float.parseFloat(currentSubtag.getTagContent());
                } else if (currentSubtag.getTagName().equalsIgnoreCase("latitude")) {
                    mapLatitude  = Float.parseFloat(currentSubtag.getTagContent());
                } else if (currentSubtag.getTagName().equalsIgnoreCase("altitude")) {
                    //TODO: write code to convert from eye at to the projection zoom.
                    zoomAltitude = true;
                } else if (currentSubtag.getTagName().equalsIgnoreCase("gx:Zoomlevel")) {
                    zoomLevel = Float.parseFloat(currentSubtag.getTagContent());                    
                    zoomAltitude = false;
                }
            }
            
            hasNetworkLink   = documentTag.containsSubTag("NetworkLink");
            lookAtCoordinate = new Coordinate(0, mapLatitude, mapLongitude);
            projection       = new MercatorProjection(mapLatitude, mapLongitude, zoomLevel);
            openedMap        = new DigitalMap(mapName, projection);    
            
            openedMap.setLookAtCoordinate(lookAtCoordinate);
            openedMap.setMapFile(fileKML);
            
            //load styles
            mapTheme = new MapTheme("Map File Theme");
            tags     = documentTag.getTags("Style");

            for (XMLTag currentTag: tags) {
                progressIndicator.updateProgress("Reading Styles", 25);
                
                currentTagName = currentTag.getTagName();
                if (currentTagName.startsWith("Style")) {
                    if (currentTag.getSubtag("BackColor") != null) {
                        Color backColor = ColorHelper.parseHexAlphabetical(currentTag.getSubtagContent("BackColor"));
                        mapTheme.setBackgroundColor(backColor);   
                    } else if (currentTag.getSubtag("BackColor") != null) {    
                        Color backColor = ColorHelper.parseHexAlphabetical("fff0f3f4");
                        mapTheme.setBackgroundColor(backColor);                           
                    } else {           
                        currentStyle = getStyle(currentTag);
                        
                        if (currentStyle != null)
                            mapTheme.addStyleElement(currentStyle);
                    }
                }
            }

            openedMap.setTheme(mapTheme, null, progressIndicator);
            openedMap.getMapThemeManager().addTheme(mapTheme);
            
            //load layers or folders
            //TODO: currently dose not load nested layer, fix later!
            tags = documentTag.getTags("Folder");
            
            progressIndicator.updateProgress("Reading Layers", 50);
            
            //Load placemarks
            if (tags.size() > 0) {
                for (XMLTag currentTag: tags) 
                    loadLayer(openedMap, nodeMap, currentTag);                

            } else {
                //no folders / layers exist
                tags         = documentTag.getTags("Placemark");
                currentLayer = new VectorLayer("Default");

                for (XMLTag currentTag: tags) {
                    currentLayer.addObject(loadPlacemark(nodeMap, currentLayer, currentTag));
                }

                openedMap.addLayer(currentLayer);
            }                        
            
            //read NetworkLinks if there are any
            if (hasNetworkLink) {
                networkLinks = documentTag.getSubtagsByName("NetworkLink");
                
                for (XMLTag networkLink: networkLinks) {
                    NetworkLayer newNetLayer = getNetworkLink(openedMap, networkLink);
                    
                    if (newNetLayer != null) {
                        openedMap.addLayer(newNetLayer);
                    } else {
                        Logger.log(Logger.ERR, "Could Not open Network Link");
                    }
                }
            }
            
            LatLonAltBox bounds = openedMap.getBoundary();
            
            //If view port posistion could not be read use NW corner.
            if (mapLatitude == 0 && mapLongitude == 0 && currentLayer != null) 
                projection.setReference(bounds.getNorthWestCoordinate());         
                          
            float southY = (float) projection.getY(bounds.getSouthWestCoordinate());
            
            if (southY < 1)
                projection.setZoomLevel(southY * 600);
            
            openedMap.setNodeMap(nodeMap);
            progressIndicator.finish();
            
            return openedMap;
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in KmlImport.openKML(File) - " + e);
            return null;
        }                
    }
    
    /**
     * Opens a KMZ file.
     * 
     * @param fileKMZ
     * @return 
     */
    public static DigitalMap openKMZ(ProgressIndicator progressIndicator, File fileKMZ) {
        DigitalMap newMap;
        
        newMap = openKMZ(progressIndicator, fileKMZ, new NodeMap(2000));
        
        return newMap;
    }
        
    /**
     * Opens a KMZ file.
     * 
     * @param fileKMZ
     * @return 
     */
    public static DigitalMap openKMZ(ProgressIndicator progressIndicator, File fileKMZ, NodeMap nodeMap) {        
        ArrayList<File>  archiveFiles;
        boolean          hasDocKML;
        byte[]           buf;
        DigitalMap       mainMap;
        File             currentFile;
        FileInputStream  fis;
        FileOutputStream fos;
        long             length;
        String           entryName;
        ZipInputStream   zis;
        ZipEntry         currentEntry;

        archiveFiles = new ArrayList<File>();
        mainMap      = new DigitalMap();
        hasDocKML    = false;
        
        try {
            fis = new FileInputStream(fileKMZ);
            zis = new ZipInputStream(new BufferedInputStream(fis));

            while (zis.available() > 0) {
                currentEntry = zis.getNextEntry();       
                
                if (currentEntry != null) {
                    currentFile  = File.createTempFile(currentEntry.getName(), "");

                    currentFile.deleteOnExit();
                    archiveFiles.add(currentFile);

                    fos = new FileOutputStream(currentFile);
                    buf = new byte[1024];

                    while((length = zis.read(buf)) >= 0)
                        fos.write(buf, 0, (int) length);

                    fos.close();

                    entryName = currentEntry.getName();
                     
                    if (entryName.equalsIgnoreCase("doc.kml")) {
                        mainMap   = openKML(progressIndicator, currentFile, nodeMap);
                        hasDocKML = true;
                    } else if (entryName.substring(entryName.length() - 3).equalsIgnoreCase("kml") && hasDocKML == false) {
                        mainMap = openKML(progressIndicator, currentFile, nodeMap);
                    } else {
                        //System.out.println((currentEntry.getName()));
                    }
                }
            }
            
            fis.close();
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in KmlImport.openKMZ - " + e);
        }
        
        return mainMap;
    }
    
    /**
     * Removes the CDataTag from the passed text.
     * 
     * @param text
     * @return 
     */
    public static String removeCDataTag(String text) {
        String cleanText;
        
        cleanText = text.replace("<![CDATA[", "");
        cleanText = cleanText.replace("]]>", "");
        
        return cleanText;
    }

    /**
     * Imports objects from a given KML file and adds objects from the map to
     * the given VectorLayer.  Any Network layers in the file will be added
     * directly to the given DigitalMap.
     * 
     * @param mapFile           The file containing the map to import.
     * @param nodeMap           The NodeMap to add new Coordinates to.
     * @param layer             The Layer to add imported objects to.
     * @param progressIndicator Optional, to display the progress of the import.
     */   
    @Override
    public void importToLayer(File mapFile, NodeMap nodeMap, Layer layer, ProgressIndicator progressIndicator) throws IOException {
        DigitalMap newMap;
        String     fileExtension, fileName;

        fileName      = mapFile.getName();
        fileExtension = fileName.substring(fileName.length() - 3);
                
        if (fileExtension.equalsIgnoreCase("kml")) {
            newMap = openKML(progressIndicator, mapFile, nodeMap);
        } else if (fileExtension.equalsIgnoreCase("kmz")) {
            newMap = openKMZ(progressIndicator, mapFile, nodeMap);
        } else {
            throw new IOException("KMLImporter: Unsupported File Type.");
        }            
    
        if (layer instanceof VectorLayer) {
            VectorLayer vecLayer = (VectorLayer) layer;
            
            try {      
                /** Clear Overlays and Objects, this is useful because we might be
                 *  importing to a NetworkLayer and the items need to be cleared.
                 *  If we are importing to another layer type, it should be empty.
                 */
                vecLayer.clearOverlays();
                vecLayer.getObjectList().clear();

                //add any overlays and objects
                for (Layer l: newMap.getLayers()) {
                    if (l instanceof NetworkLayer) {
                        if (layer instanceof NetworkLayer) {
                            vecLayer.addOverlays(((VectorLayer) l).getOverlays()); 
                            vecLayer.addAllObjects(((VectorLayer) l).getObjectList());                         
                        } else {
                            //Add any NetworkLayers to the parent map
                            //assume it has already been added // mapData.addLayer(layer); 
                        }
                    } else if (l instanceof VectorLayer) {
                        vecLayer.addOverlays(((VectorLayer) l).getOverlays()); 
                        vecLayer.addAllObjects(((VectorLayer) l).getObjectList());                     
                    }
                }                

                //Transfer Styles
                ArrayList<ColorStyle> styles = newMap.getTheme().getAllStyles();

                for (ColorStyle cs: styles) 
                    layer.getParentMap().getTheme().addStyleElement(cs);    

            } catch (Exception e) {
                Logger.log(Logger.ERR, "Error in KmlImport.importToLayer(File, NodeMap, VectorLayer, ProgressBarPanel) - " + e);
            }        
        } else {
            Logger.log(Logger.ERR, "Error in KmlImporter.importToLayer(File, NodeMap, Layer, ProgressIndicator) - Supplied Layer must be a VectorLayer.");
        }
    }
}
