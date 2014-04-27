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
import co.foldingmap.map.vector.VectorObjectList;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.LevelOfDetail;
import co.foldingmap.map.vector.LineString;
import co.foldingmap.map.vector.Polygon;
import co.foldingmap.map.vector.PhotoPoint;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.MapPoint;
import co.foldingmap.map.vector.InnerBoundary;
import co.foldingmap.map.tile.TileServerTileSource;
import co.foldingmap.map.tile.TileMath;
import co.foldingmap.map.tile.TileSource;
import co.foldingmap.map.tile.DirectoriesTileSource;
import co.foldingmap.map.tile.TileLayer;
import co.foldingmap.map.tile.MbTileSource;
import co.foldingmap.map.themes.PolygonStyle;
import co.foldingmap.map.themes.ColorRamp;
import co.foldingmap.map.themes.ColorHelper;
import co.foldingmap.map.themes.ThemeConstants;
import co.foldingmap.map.themes.MapTheme;
import co.foldingmap.map.themes.LabelStyle;
import co.foldingmap.map.themes.ColorStyle;
import co.foldingmap.map.themes.OutlineStyle;
import co.foldingmap.map.themes.IconStyle;
import co.foldingmap.map.themes.LineStyle;
import co.foldingmap.GUISupport.ProgressBarPanel;
import co.foldingmap.GUISupport.ProgressIndicator;
import co.foldingmap.Logger;
import co.foldingmap.map.visualization.HeatMap;
import co.foldingmap.map.visualization.HeatMapKey;
import co.foldingmap.xml.XMLParser;
import co.foldingmap.xml.XMLTag;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Class for loading FmXml files
 * 
 * @author Alec
 */
public class FmXmlImporter implements FormatImporter {
    
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
    
    /**
     * Reads a ColorRamp FmXml tag and creates a ColorRamp object from it.
     * 
     * @param id
     * @param colorRampTag
     * @return 
     */
    public static ColorRamp getColorRamp(String id, XMLTag colorRampTag) {
        ColorRamp   colorRamp = new ColorRamp(id);
        
        for (XMLTag tag: colorRampTag.getSubtags()) {
            if (tag.getTagName().equalsIgnoreCase("Default")) {
                String defaultColor = colorRampTag.getSubtagContent("Default");
                colorRamp.setDefaultColor(ColorHelper.parseHexStandard(defaultColor));                
            } else {
                colorRamp.addEntry(tag.getTagValue(), ColorHelper.parseHexStandard(tag.getTagContent()));
            }
        }
        
        return colorRamp;
    }
    
    /**
     * Creates a CoordinateList from data in the given NodeMap and NodeIDs from the coordinate String.
     * 
     * @param coordinateSet
     * @param coordinateString
     * @return 
     */
    public static CoordinateList<Coordinate> getCoordinateList(NodeMap coordinateSet,
                                                               String  coordinateString) {
        Coordinate                  coordinate;
        CoordinateList<Coordinate>  coordinates;
        long                        nodeId;
        String                      coordinateGroupString;
        StringTokenizer             coordinateGroupTokenizer;
        
        try {            
            coordinateGroupTokenizer = new StringTokenizer(coordinateString);
            coordinates              = new CoordinateList<Coordinate>(coordinateGroupTokenizer.countTokens());            
            
            while (coordinateGroupTokenizer.hasMoreTokens()) {            
                coordinateGroupString = coordinateGroupTokenizer.nextToken();                                
                
                if (!coordinateGroupString.contains(",")) {                    
                    //Single element, should be a reference to a node id
                    nodeId     = Long.parseLong(coordinateGroupString);                        
                    coordinate = coordinateSet.get(nodeId);  
                    
                    if (coordinate != null) {                        
                        coordinates.add(coordinate); 
                    } else {
                        System.err.println("Error node id: " + nodeId + " not found");
                    }
                } else {                                               
                    //Two elements, should be lon, lat
                    //Three elements should be lon, lat, alt
                    //Four elements, should be lon, lat, alt, timestmap                   
                    coordinate = new Coordinate(coordinateGroupString);   
                    
                    if (coordinate != null) {
                        //check to see if the coordinate already exists in the map
                        nodeId = coordinateSet.findKey(coordinate);
                        //nodeId = getKeyByValue(coordinateSet, coordinate);

                        if (nodeId > 0) {
                            //the coordinate already exists
                            coordinate = coordinateSet.get(nodeId);
                            
                            if (coordinate != null) {
                                coordinate.setShared(true);
                                coordinates.add(coordinate);                            
                            } else {
                                System.err.println("Error node id: " + nodeId + "not found");
                            }                            
                        } else {
                            //generate new ID
                            nodeId = coordinateSet.size() - 1;

                            //Make sure the new ID does not already exist
                            while (coordinateSet.get(nodeId) != null)
                                nodeId++;

                            coordinateSet.put(nodeId, coordinate);
                            coordinates.add(coordinate);                         
                        }                                                       
                        
                        
                    } else {
                        System.err.println("Error in FmXmlImporter - null coordinate");
                    } // null coordinate check
                } 
            }
            
            //System.err.println(coordinates.size());
            
            return coordinates;
        } catch (Exception e) {
            System.err.println("Error in FmXmlImporter.getCoordinateList(NodeMap, String - " + e);
            return new CoordinateList<Coordinate>(1);
        }               
    }
    
   /**
     * Reads gxTags to create a HashMap of customDataFields.
     * 
     * @param gxTags
     * @return 
     */
    public static HashMap<String,String> getCustomDataFields(XMLTag dataTag) {
        ArrayList<XMLTag>       dataSubTabs;
        HashMap<String,String>  customDataFields;
        String                  key, value, tagName;
        
        dataSubTabs      = dataTag.getSubtags();
        customDataFields = new HashMap<String,String>();
        
        try {
            for (int i = 0; i < dataSubTabs.size(); i++) {
                tagName = dataSubTabs.get(i).getTagName();
                        
                if (tagName.length() > 3 && tagName.substring(0,4).equalsIgnoreCase("pair")) {
                    key   = dataSubTabs.get(i).getTagValue();
                    value = dataSubTabs.get(i).getTagContent();                                       
                } else {                
                    //for legacy compatibility
                    key   = dataSubTabs.get(i).getTagContent();
                    value = dataSubTabs.get(i+1).getTagContent();
                    i++;
                }
                
                customDataFields.put(key, value);
            }
            
        } catch (Exception e) {
            System.err.println("Error in FmXmlImporter.getCustomDataFields(ArrayList) - " + e);            
        }   
        
        return customDataFields;
    }    
    
    /**
     * Returns a HeatMap form fmXml
     * 
     * @param openedMap
     * @param layerTag
     * @return 
     */
    public static HeatMap getHeatMapLayer(DigitalMap openedMap, XMLTag layerTag) {
        try {
            ArrayList<Color>        colors;
            ArrayList<String>       objectRefs, variables;
            HeatMap                 heatMap;
            HeatMapKey              heatMapKey;
            int                     positionRef;
            String                  layerDescription, layerName;
            String                  style, orientation, position;
            String[]                temp;
            StringTokenizer         st;
            XMLTag                  fields, descriptionTag, keyTag, mappings;
            XMLTag                  objects, orientationTag;
                    
            objectRefs       = new ArrayList<String>();
            variables        = new ArrayList<String>();
            descriptionTag   = layerTag.getSubtag("Description");
            layerName        = layerTag.getSubtagContent("Name");
            fields           = layerTag.getSubtag("fields");
            mappings         = layerTag.getSubtag("mappings");
            layerDescription = removeCDataTag(layerTag.getSubtagContent("Description"));
            objects          = layerTag.getSubtag("objects");            
            keyTag           = layerTag.getSubtag("key");
            style            = layerTag.getSubtagContent("Style");
            
            colors      = new ArrayList<Color>();
            positionRef = -1;
            
            if (keyTag != null) {
                position       = keyTag.getSubtagContent("Position");
                orientationTag = keyTag.getSubtag("Orientation");
                
                if (position.equalsIgnoreCase("None")) {
                    positionRef = HeatMapKey.NONE;
                } else if (position.equalsIgnoreCase("Bottom-Left")) {
                    positionRef = HeatMapKey.BOTTOM_LEFT;
                } else if (position.equalsIgnoreCase("Bottom-Right")) {
                    positionRef = HeatMapKey.BOTTOM_RIGHT;
                } else if (position.equalsIgnoreCase("Top-Left")) {
                    positionRef = HeatMapKey.TOP_LEFT;
                } else if (position.equalsIgnoreCase("Top-Right")) {  
                    positionRef = HeatMapKey.TOP_RIGHT;
                } 
                
                for (XMLTag tag: keyTag.getSubtags("color")) {
                    colors.add(ColorHelper.parseHexStandard(tag.getTagContent()));
                }
                
                if (orientationTag != null) {
                    orientation = orientationTag.getTagContent();
                } else {
                    orientation = HeatMapKey.HORIZONTAL;
                }
            } else {
                orientation = HeatMapKey.HORIZONTAL;
            }                        
            
            //parse Variables/Fields
            st = new StringTokenizer(fields.getTagContent(), ",");
            
            while(st.hasMoreTokens())
                variables.add(st.nextToken().replace(",", ""));                                   
            
            //description uses HTML
            if (layerDescription.equals("") && (descriptionTag != null))
                layerDescription = descriptionTag.getSubtagsAsString();

            st = new StringTokenizer(objects.getTagContent(), ",");

            while (st.hasMoreTokens())
                objectRefs.add(st.nextToken().replace(",", ""));                

            temp       = new String[variables.size()];    
            heatMapKey = new HeatMapKey(new ArrayList<String>(), colors, positionRef, orientation);
            heatMap    = new HeatMap(layerName, objectRefs, style, variables.toArray(temp), 500, heatMapKey);
            heatMap.setLayerDescription(layerDescription);
            
            return heatMap;
        } catch (Exception e) {
            System.err.println("Error in FmXmlImport.getHeatMapLayer(XMLTag) - " + e);
            return null;
        }           
    }
    
    /**
     * Returns an IconStyle given it's XML code.
     * 
     * @param styleTag
     * @return 
     */
    public static IconStyle getIconStyle(XMLTag styleTag) {
        try {
            boolean         hasHeading, hasIcon, hasLabel, hasLOD, hasOutline, hasScale, hasVis;
            boolean         outline;
            Color           fillColor, outlineColor;
            float           heading, scale;
            IconStyle       newIconStyle;
            int             colorMode;
            LabelStyle      labelStyle;
            String          colorModeString, icon, styleID, styleTagName;
            XMLTag          iconTag, iconStyleTag;

            styleTagName    = styleTag.getTagName();
            styleID         = styleTagName.substring(styleTagName.indexOf("id=\"") + 4,  styleTagName.length() - 1);
            iconStyleTag    = styleTag.getSubtag("IconStyle");  
            fillColor       = ColorHelper.parseHexStandard(iconStyleTag.getSubtagContent("color"));
            colorModeString = iconStyleTag.getSubtagContent("colorMode");
            hasScale        = iconStyleTag.containsSubTag("scale");
            hasHeading      = iconStyleTag.containsSubTag("heading");
            hasOutline      = iconStyleTag.containsSubTag("outline");
            hasIcon         = iconStyleTag.containsSubTag("Icon");
            hasLabel        = iconStyleTag.containsSubTag("LabelStyle");
            hasVis          = iconStyleTag.containsSubTag("Visibility");                                                                     

            if (colorModeString.equalsIgnoreCase("normal")) {
                colorMode = ColorStyle.NORMAL;
            } else {
                colorMode = ColorStyle.RANDOM;
            }
            
            newIconStyle = new IconStyle(styleID, fillColor);

            if (iconStyleTag.containsSubTag("Visibility")) {
                Visibility vis = getVisibility(iconStyleTag.getSubtag("Visibility"));
                newIconStyle.setVisibility(vis);
            }            
            
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
                    outlineColor = ColorHelper.parseHexStandard(iconStyleTag.getSubtagContent("outlineColor"));
                    newIconStyle.setOutlineColor(outlineColor);
                }                
            }                        
            
            newIconStyle.setColorMode(colorMode);

            if (hasIcon) {
                iconTag = iconStyleTag.getSubtag("Icon");
                icon = iconTag.getSubtagContent("href");
                newIconStyle.setImageFileName(icon);
            }

            if (hasVis) {
                Visibility vis = getVisibility(iconStyleTag.getSubtag("Visibility"));
                newIconStyle.setVisibility(vis);
            }

            if (hasLabel) {
                labelStyle = getLabelStyle(iconStyleTag.getSubtag("LabelStyle"));
                newIconStyle.setLabel(labelStyle);
            }

            return newIconStyle;           
        } catch (Exception e) {
            System.err.println("Error in FmXmlImport.getIconStyle(XMLTag) - " + e);
            return null;
        }            
    }    
    
    /**
     * Creates an InnerBoundary when given an InnerBoundary FmXml tag.
     * 
     * @param layer   - Layer to add object to.
     * @param nodeMap - The NodeMap which this object references coordinates.
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
        int         fontIntStyle, fontSize;
        LabelStyle  labelStyle;
        String      fontFamily, fontStyle;
        XMLTag      fontTag;
        
        fillColor    = ColorHelper.parseHexStandard(labelTag.getSubtagContent("color"));
        outlineColor = ColorHelper.parseHexStandard(labelTag.getSubtagContent("outlineColor"));
        fontTag      = labelTag.getSubtag("font");
        fontFamily   = fontTag.getSubtagContent("family");
        fontStyle    = fontTag.getSubtagContent("style");
        fontSize     = Integer.parseInt(fontTag.getSubtagContent("size"));
        
        if (fontStyle.equalsIgnoreCase("Bold")) {
            fontIntStyle = Font.BOLD;
        } else if (fontStyle.equalsIgnoreCase("Italic")) {
            fontIntStyle = Font.ITALIC;
        } else if (fontStyle.equalsIgnoreCase("Plain")) {    
            fontIntStyle = Font.PLAIN;
        } else {
            fontIntStyle = Font.PLAIN;
        }
        
        labelFont  = new Font(fontFamily, fontIntStyle, fontSize);
        labelStyle = new LabelStyle(fillColor, outlineColor, labelFont);
                
        return labelStyle;
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
     * Reads in XML for LevelOfDetail and returns a LevelOfDetail object.
     * 
     * @param lodTag - FmXml tag containing the LEvelOfDetail tag.
     * @return 
     */
    public static LevelOfDetail getLevelOfDetail(XMLTag lodTag) {        
        float           minLodPixels, maxLodPixels;
        float           minFadeExtent, maxFadeExtent;
        LevelOfDetail   lod;
                
        if (lodTag.containsSubTag("minLodPixels")) {
            minLodPixels = Float.parseFloat(lodTag.getSubtagContent("minLodPixels"));
        } else {
            minLodPixels = -1;
        }
        
        if (lodTag.containsSubTag("maxLodPixels")) {
            maxLodPixels = Float.parseFloat(lodTag.getSubtagContent("maxLodPixels"));
        } else {
            maxLodPixels = -1;
        }
        
        if (lodTag.containsSubTag("minFadeExtent")) {
            minFadeExtent = Float.parseFloat(lodTag.getSubtagContent("minFadeExtent"));
        } else {
            minFadeExtent = -1;
        }
        
        if (lodTag.containsSubTag("maxFadeExtent")) {
            maxFadeExtent = Float.parseFloat(lodTag.getSubtagContent("maxFadeExtent"));       
        } else {
            maxFadeExtent = -1;
        }
        
        lod = new LevelOfDetail(maxLodPixels, minLodPixels, maxFadeExtent, minFadeExtent);
        
        return lod;
    }    
    
    /**
     * Reads in FmXml for a LinearRing and returns a LinearRing object.
     * 
     * @param layer         - Layer to add object to.
     * @param placemarkTag  - Tag containing the FmXML tag to be imported.
     * @param coordinateSet - The NodeMap which this object references coordinates.
     * @return 
     */
    public static LinearRing getLinearRing(VectorLayer layer, 
                                           XMLTag placemarkTag,
                                           NodeMap coordinateSet) {
        try {
            boolean                     hasData, hasRef, hasRegion, hasTimestamps;
            CoordinateList<Coordinate>  coordinates;
            HashMap<String,String>      customDataFields;
            LinearRing                  newRing;            
            String                      coordinateString, description;
            String                      objectName, styleUrl, timestamps;
            XMLTag                      dataTag, ringTag;

            objectName       = placemarkTag.getSubtagContent("name");
            description      = removeCDataTag(placemarkTag.getSubtagContent("description"));
            styleUrl         = placemarkTag.getTagValue();
            hasData          = placemarkTag.containsSubTag("data");
            hasRef           = placemarkTag.containsSubTag("Ref");  
            hasRegion        = placemarkTag.containsSubTag("Region");
            coordinateString = placemarkTag.getSubtagContent("coordinates");
            coordinates      = getCoordinateList(coordinateSet, coordinateString);
            hasTimestamps    = placemarkTag.containsSubTag("gx:Timestamps");

            if (styleUrl.startsWith("#"))
                styleUrl = styleUrl.substring(1);
                
            newRing = new LinearRing(objectName, styleUrl, coordinates);

            newRing.setDescription(description);

            if (hasRef) 
                newRing.setReference(Long.parseLong(placemarkTag.getSubtagContent("Ref")));              
            
            if (hasRegion) {
                Region     region = getRegion(placemarkTag.getSubtag("Region"));
                Visibility vis    = convertRegionToVisibility(region);
                newRing.setVisibility(vis);
            }

            if (placemarkTag.containsSubTag("Visibility")) {
                Visibility vis = getVisibility(placemarkTag.getSubtag("Visibility"));
                newRing.setVisibility(vis);
            }            

            if (hasTimestamps) {
                timestamps = placemarkTag.getSubtagContent("gx:Timestamps");
                newRing.setTimestamps(timestamps);
            }        

            if (hasData) {
                dataTag = placemarkTag.getSubtag("data");
                customDataFields = getCustomDataFields(dataTag); 
                newRing.setCustomDataFields(customDataFields);
            }
            
            //Add the new object as a Parent for the Coordinates it uses.
            coordinates.setParentObject(newRing);
            
            return newRing;
        } catch (Exception e) {
            System.err.println("Error in FmXmlImporter.getLinearRing(VectorLayer, XMLTag) - " + e);
            return null;
        }
    }    
    
    /**
     * Reads FmXml for LineString and returns a LineString Object.
     * 
     * @param layer         - Layer to add object to.
     * @param placemarkTag  - Tag containing the FmXML tag to be imported.
     * @param coordinateSet - The NodeMap which this object references coordinates.
     * @return 
     */
    public static LineString getLineString(VectorLayer layer, 
                                           XMLTag placemarkTag,
                                           NodeMap coordinateSet) {
        try {
            boolean                     hasData, hasRef, hasRegion, hasTimestamps;
            CoordinateList<Coordinate>  coordinates;
            HashMap<String,String>      customDataFields;
            LineString                  newLine;
            String                      coordinateString, description;
            String                      objectName, styleUrl, timestamps;
            XMLTag                      dataTag, linestringTag;

            objectName       = placemarkTag.getSubtagContent("name");
            description      = removeCDataTag(placemarkTag.getSubtagContent("description"));
            styleUrl         = placemarkTag.getTagValue();
            hasData          = placemarkTag.containsSubTag("data");
            hasRef           = placemarkTag.containsSubTag("Ref");  
            hasRegion        = placemarkTag.containsSubTag("Region");
            coordinateString = placemarkTag.getSubtagContent("coordinates");
            coordinates      = getCoordinateList(coordinateSet, coordinateString);
            hasTimestamps    = placemarkTag.containsSubTag("gx:Timestamps");

            if (styleUrl.startsWith("#"))
                styleUrl = styleUrl.substring(1);            
            
            newLine = new LineString(objectName, styleUrl, coordinates);

            newLine.setDescription(description);

            if (hasRef) 
                newLine.setReference(Long.parseLong(placemarkTag.getSubtagContent("Ref")));              
            
            if (hasRegion) {
                Region     region = getRegion(placemarkTag.getSubtag("Region"));
                Visibility vis    = convertRegionToVisibility(region);
                newLine.setVisibility(vis);
            }

            if (placemarkTag.containsSubTag("Visibility")) {
                Visibility vis = getVisibility(placemarkTag.getSubtag("Visibility"));
                newLine.setVisibility(vis);
            }            
            
            if (hasTimestamps) {
                timestamps = placemarkTag.getSubtagContent("gx:Timestamps");
                newLine.setTimestamps(timestamps);
            }        

            if (hasData) {
                dataTag = placemarkTag.getSubtag("data");
                customDataFields = getCustomDataFields(dataTag); 
                newLine.setCustomDataFields(customDataFields);
            }            
            
            //Add the new object as a Parent for the Coordinates it uses.
            coordinates.setParentObject(newLine);            
            
            if (coordinates.size() > 0) {                    
                return newLine;
            } else {                
                Logger.log(Logger.ERR, "Error in FmXmlImporter.getLineString(VectorLayer, XMLTag) - No Coordinate Data For Object: " + objectName);
                return null;
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in FmXmlImporter.getLineString(VectorLayer, XMLTag) - " + e);
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
            boolean     hasStroke, scaleWidth;
            Color       fillColor, outlineColor, selectedFillColor, selectedOutlineColor;
            float       lineWidth;
            int         outline;
            LineStyle   lineStyle;
            String      lineStroke, styleID, styleTagName;
            XMLTag      lineStyleTag;

            styleTagName = styleTag.getTagName();
            styleID      = styleTagName.substring(styleTagName.indexOf("id=\"") + 4,  styleTagName.length() - 1);
            lineStyleTag = styleTag.getSubtag("LineStyle");
            fillColor    = ColorHelper.parseHexStandard(lineStyleTag.getSubtagContent("color"));
            lineWidth    = Float.parseFloat(lineStyleTag.getSubtagContent("width"));
            outline      = Integer.parseInt(lineStyleTag.getSubtagContent("outline"));        
            hasStroke    = lineStyleTag.containsSubTag("lineStroke");

            if (hasStroke) {
                lineStroke = lineStyleTag.getSubtagContent("lineStroke");
            } else {
                lineStroke = LineStyle.SOLID;
            }            
                        
            if (lineStyleTag.containsSubTag("scaleWidth")) {
                scaleWidth = Boolean.parseBoolean(lineStyleTag.getSubtagContent("scaleWidth"));
            } else {
                scaleWidth = true;
            }
            
            lineStyle = new LineStyle(styleID, fillColor, lineWidth, lineStroke, scaleWidth); 

            if (lineStyleTag.containsSubTag("selectedFillColor")) {
                selectedFillColor = ColorHelper.parseHexStandard(lineStyleTag.getSubtagContent("selectedFillColor"));
                lineStyle.setSelectedFillColor(selectedFillColor);
            }            
            
            if (outline == 1) {
                if (lineStyleTag.containsSubTag("gx:outlineColor")) {
                    outlineColor = ColorHelper.parseHexStandard(lineStyleTag.getSubtagContent("gx:outlineColor"));
                } else if (lineStyleTag.containsSubTag("outlineColor")) {
                    outlineColor = ColorHelper.parseHexStandard(lineStyleTag.getSubtagContent("outlineColor"));
                } else {
                    outlineColor = new Color(68, 68, 68, 128);
                }
                
                if (lineStyleTag.containsSubTag("selectedOutlineColor")) {
                    selectedOutlineColor = ColorHelper.parseHexStandard(lineStyleTag.getSubtagContent("selectedOutlineColor"));
                    lineStyle.setSelectedOutlineColor(selectedOutlineColor);
                }
                
                lineStyle.setOutlineColor(outlineColor);
                lineStyle.setOutline(true);
            } else {
                lineStyle.setOutline(false);
            }
            
            return lineStyle;
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in FmXmlImport.getLineStyle(XMLTag) - " + e);
            return null;
        }
    } 
    
    /**
     * Reads in FmXml for MapPoint and returns a MapPoint object.
     * 
     * @param layer         - Layer to add object to.
     * @param placemarkTag  - Tag containing the FmXML tag to be imported.
     * @param coordinateSet - The NodeMap which this object references coordinates.
     * @return 
     */
    public static MapPoint getMapPoint(VectorLayer layer, 
                                       XMLTag placemarkTag,
                                       NodeMap coordinateSet) {
        try {
            boolean                     hasData, hasRef, hasRegion, hasTimestamps;
            CoordinateList<Coordinate>  coordinates;
            HashMap<String,String>      customDataFields;
            MapPoint                    newPoint;
            String                      coordinateString, description;
            String                      objectName, styleUrl, timestamps;
            XMLTag                      dataTag, pointTag;

            objectName       = placemarkTag.getSubtagContent("name");
            description      = removeCDataTag(placemarkTag.getSubtagContent("description"));
            styleUrl         = placemarkTag.getTagValue();
            hasData          = placemarkTag.containsSubTag("data");
            hasRef           = placemarkTag.containsSubTag("Ref");  
            hasRegion        = placemarkTag.containsSubTag("Region");     
            coordinateString = placemarkTag.getSubtagContent("coordinates");
            coordinates      = getCoordinateList(coordinateSet, coordinateString);
            hasTimestamps    = placemarkTag.containsSubTag("gx:Timestamps");            

            if (styleUrl.startsWith("#"))
                styleUrl = styleUrl.substring(1);            
            
            newPoint = new MapPoint(objectName, styleUrl, description, coordinates);

            if (hasRegion) {
                Region     region = getRegion(placemarkTag.getSubtag("Region"));
                Visibility vis = convertRegionToVisibility(region);
                newPoint.setVisibility(vis);
            }

            if (placemarkTag.containsSubTag("Visibility")) {
                Visibility vis = getVisibility(placemarkTag.getSubtag("Visibility"));
                newPoint.setVisibility(vis);
            }            
            
            if (hasRef) 
                newPoint.setReference(Long.parseLong(placemarkTag.getSubtagContent("Ref")));              
            
            if (hasTimestamps) {
                timestamps = placemarkTag.getSubtagContent("gx:Timestamps");
                newPoint.setTimestamps(timestamps);
            }

            if (hasData) {
                dataTag = placemarkTag.getSubtag("data");
                customDataFields = getCustomDataFields(dataTag); 
                newPoint.setCustomDataFields(customDataFields);
            }            
            
            //Add the new object as a Parent for the Coordinates it uses.
            coordinates.setParentObject(newPoint);            
            
            if (coordinates.size() > 0) {
                return newPoint;
            } else {
                Logger.log(Logger.ERR, "Error in FmXmlImporter.getMapPoint(VectorLayer, XMLTag) - No Coordinate Data For Object: " + objectName);
                return null;
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in FmXmlImporter.getMapPoint(VectorLayer, XMLTag) - " + e);
            return null;
        }
    }    
    
    /**
     * Reads in FmXml for MultiGeometry and returns a MultiGeometry object.
     * 
     * @param layer
     * @param multiGeoTag
     * @param coordinateSet
     * @param placemarkTag
     * @return 
     */
    public static MultiGeometry getMultiGeometry(VectorLayer layer, 
                                                 XMLTag  multiGeoTag,
                                                 NodeMap coordinateSet) {
        
        ArrayList<XMLTag>        objectTags;
        boolean                  hasData, hasRef;
        HashMap<String,String>   customDataFields;
        VectorObjectList<VectorObject> objects;
        MultiGeometry            newMulti;
        String                   description, objectName;
        XMLTag                   dataTag;
        
        try {
            hasData          = multiGeoTag.containsSubTag("data");
            hasRef           = multiGeoTag.containsSubTag("Ref");  
            objectName       = multiGeoTag.getSubtagContent("name");
            description      = removeCDataTag(multiGeoTag.getSubtagContent("description"));   
            objectTags       = multiGeoTag.getSubtag("elements").getSubtags();            
            objects          = getObjectsFromPlaceMarks(layer, objectTags, coordinateSet);
            
            newMulti = new MultiGeometry(objectName, objects);
            
            if (description != null)
                newMulti.setDescription(description);
                
            if (multiGeoTag.containsSubTag("Visibility")) {
                Visibility vis = getVisibility(multiGeoTag.getSubtag("Visibility"));
                newMulti.setVisibility(vis);
            }            
            
            if (hasData) {
                dataTag = multiGeoTag.getSubtag("data");
                customDataFields = getCustomDataFields(dataTag); 
                newMulti.setCustomDataFields(customDataFields);
            }            
            
            return newMulti;
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in FmXmlImport.getMultiGeometry(VectorLayer, XMLTag) - " + e);
            return null;
        }        
    }    
        
    public static NetworkLayer getNetworkLayer(XMLTag layerTag) {
        try {
            boolean      hasAddress, hasPointClass, hasRefreshInterval;
            boolean      hasRefreshMode, hasVisibility;
            int          refMode;
            NetworkLayer networkLayer;
            String       address, description, layerName, refInterval;
            String       pointClass;
            XMLTag       descriptionTag;
            
            hasAddress          = layerTag.containsSubTag("href");
            hasPointClass       = layerTag.containsSubTag("PointClass");
            hasRefreshInterval  = layerTag.containsSubTag("RefreshInterval");
            hasRefreshMode      = layerTag.containsSubTag("RefreshMode");
            hasVisibility       = layerTag.containsSubTag("Visibility");
            descriptionTag      = layerTag.getSubtag("Description");
            layerName           = layerTag.getSubtagContent("Name"); 
            
            if (hasAddress) {
                address = layerTag.getSubtagContent("href");
            } else {
                address = "";
            }
            
            networkLayer = new NetworkLayer(layerName, address);
            
            if (hasPointClass) {
                pointClass  = layerTag.getSubtagContent("PointClass");
                networkLayer.setDefaultPointClass(pointClass);
            }
            
            if (hasRefreshInterval) {
                refInterval = layerTag.getSubtagContent("RefreshInterval");
                networkLayer.setRefreshInterval(Float.parseFloat(refInterval));
            }
            
            if (hasRefreshMode) {
                networkLayer.setRefreshMode(layerTag.getSubtagContent("RefreshInterval"));
            }
            
            if (hasVisibility) {                
                Visibility vis = FmXmlImporter.getVisibility(layerTag.getSubtag("Visibility"));
                networkLayer.setVisibility(vis.getMinTileZoomLevel(), vis.getMaxTileZoomLevel());
            }
            
            if (descriptionTag != null) {
                description = removeCDataTag(descriptionTag.getTagContent());
                networkLayer.setLayerDescription(description);
            }
            
            return networkLayer;
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in FmXml.getNetworkLayer(XMLTag) - " + e);
            return null;
        }
        
        
    }
    
    /**
     * Creates MapObjects from FmXML code.
     *
     * @param  layer
     * @param  coordinateSet - The NodeMap which this object references coordinates.
     * @param  placeMarkTags - An ArrayList containing PlaceMark XMLTags
     * @return A collection of the new MapObjects created.
     */
    public static VectorObjectList<VectorObject> getObjectsFromPlaceMarks(VectorLayer layer, 
                                                                    ArrayList<XMLTag> placeMarkTags,
                                                                    NodeMap coordinateSet) {
        try {
            VectorObjectList<VectorObject>  newObjects;

            newObjects   = new VectorObjectList<VectorObject>();
        
            for (XMLTag currentPlaceMarkTag: placeMarkTags) {
                newObjects.add(loadObject(layer, currentPlaceMarkTag, coordinateSet));
            }
            
            return newObjects;
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in FmXmlImpotrer.getObjectsFromPlaceMarks(VectorLayer, ArrayList<XMLTag>) - " + e);
            return null;
        }        
    }    
    
    /**
     * Creates an OutlineStyle object from FmXML.
     * 
     * @param outlineStyleTag - The XML tag for OutlineStyle.
     * @return 
     */
    public static OutlineStyle getOutlineStyle(XMLTag outlineStyleTag) {
        OutlineStyle    outlineStyle;
        String          tagText;
        
        //Create a default outline style
        outlineStyle = new OutlineStyle();
        
        try {
            //Change the properties of the default style to match the XML code.
            if (outlineStyleTag.containsSubTag("borderCondition"))
                outlineStyle.setBorderCondition(outlineStyleTag.getSubtagContent("borderCondition"));

            if (outlineStyleTag.containsSubTag("color")) {
                tagText = outlineStyleTag.getSubtagContent("color");
                outlineStyle.setColor(ColorHelper.parseHexStandard(tagText));            
            }

            if (outlineStyleTag.containsSubTag("selectedColor")) {
                tagText = outlineStyleTag.getSubtagContent("selectedColor");
                outlineStyle.setSelectedColor(ColorHelper.parseHexStandard(tagText));            
            }

            if (outlineStyleTag.containsSubTag("strokeStyle"))
                outlineStyle.setStrokeStyle(outlineStyleTag.getSubtagContent("strokeStyle"));

            if (outlineStyleTag.containsSubTag("width")) {
                tagText = outlineStyleTag.getSubtagContent("width");
                outlineStyle.setWidth(Float.parseFloat(tagText));       
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in FmXmlImpotrer.getOutlineStyle(XMLTag) - " + e);
        }
        
        return outlineStyle;
    }
    
    /**
     * Reads in FmXml for PhotoPoint and returns a PhotoPoint object.
     * 
     * @param layer
     * @param placemarkTag
     * @param coordinateSet
     * @return 
     */
    public static PhotoPoint getPhotoPoint(VectorLayer layer, 
                                          XMLTag  placemarkTag,
                                          NodeMap coordinateSet) {
        try {
            boolean                     hasData, hasRef, hasRegion, hasTimestamps;
            CoordinateList<Coordinate>  coordinates;
            HashMap<String,String>      customDataFields;
            PhotoPoint                  newPoint;
            String                      coordinateString, description;
            String                      fileName, objectName, styleUrl;
            XMLTag                      dataTag, iconTag;

            objectName       = placemarkTag.getSubtagContent("name");
            description      = removeCDataTag(placemarkTag.getSubtagContent("description"));
            styleUrl         = placemarkTag.getTagValue();
            hasData          = placemarkTag.containsSubTag("data");
            hasRef           = placemarkTag.containsSubTag("Ref");  
            hasRegion        = placemarkTag.containsSubTag("Region");   
            iconTag          = placemarkTag.getSubtag("Icon");
            coordinateString = placemarkTag.getSubtagContent("coordinates");
            coordinates      = getCoordinateList(coordinateSet, coordinateString);
            hasTimestamps    = placemarkTag.containsSubTag("gx:Timestamps");            
            fileName         = iconTag.getSubtagContent("href");
                    
            if (styleUrl.startsWith("#"))
                styleUrl = styleUrl.substring(1);            
            
            newPoint = new PhotoPoint(objectName, coordinates.get(0), fileName);
            
            newPoint.setClass(styleUrl);
            newPoint.setDescription(description);
            
            if (hasRegion) {
                Region     region = getRegion(placemarkTag.getSubtag("Region"));
                Visibility vis = convertRegionToVisibility(region);
                newPoint.setVisibility(vis);
            }

            if (hasRef) 
                newPoint.setReference(Long.parseLong(placemarkTag.getSubtagContent("Ref")));              
            
            if (hasData) {
                dataTag = placemarkTag.getSubtag("data");
                customDataFields = getCustomDataFields(dataTag); 
                newPoint.setCustomDataFields(customDataFields);
            }            
            
            //Add the new object as a Parent for the Coordinates it uses.
            coordinates.setParentObject(newPoint);            
            
            if (coordinates.size() > 0) {
                return newPoint;
            } else {
                Logger.log(Logger.ERR, "Error in FmXmlImporter.getPhotoPoint(VectorLayer, XMLTag) - No Coordinate Data For Object: " + objectName);
                return null;
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in FmXmlImporter.getPhotoPoint(VectorLayer, XMLTag) - " + e);
            return null;
        }
    }       
    
    /**
     * Reads in FmXml for a Polygon and returns a Polygon Object.
     * 
     * @param layer
     * @param placemarkTag
     * @param coordinateSet
     * @return 
     */
    public static Polygon getPolygon(VectorLayer layer, 
                                     XMLTag placemarkTag,
                                     NodeMap coordinateSet) {
        try {
            ArrayList<XMLTag>           innerBoundaryTags;
            boolean                     hasData, hasRef, hasRegion, hasTimestamps;
            CoordinateList<Coordinate>  coordinates;
            HashMap<String,String>      customDataFields;
            Polygon                     newPolygon;
            String                      coordinateString, description;
            String                      objectName, styleUrl, timestamps;
            XMLTag                      dataTag, lineStringTag, outerBoundaryTag, polygonTag;
            
            objectName        = placemarkTag.getSubtagContent("name");
            description       = removeCDataTag(placemarkTag.getSubtagContent("description"));
            styleUrl          = placemarkTag.getTagValue();
            hasData           = placemarkTag.containsSubTag("data");
            hasRegion         = placemarkTag.containsSubTag("Region");
            hasRef            = placemarkTag.containsSubTag("Ref");                         
            innerBoundaryTags = placemarkTag.getSubtags("innerBoundaryIs");                                    
            hasTimestamps     = placemarkTag.containsSubTag("gx:Timestamps");
                        
            if (styleUrl.startsWith("#"))
                styleUrl = styleUrl.substring(1);            
            
            if (placemarkTag.containsSubTag("outerBoundaryIs")) {
                //Old legacy tag setup
                outerBoundaryTag = placemarkTag.getSubtag("outerBoundaryIs"); 
                lineStringTag    = outerBoundaryTag.getSubtag("LinearRing");
                coordinateString = lineStringTag.getSubtagContent("coordinates");
                coordinates      = getCoordinateList(coordinateSet, coordinateString);
            } else if (placemarkTag.containsSubTag("outerBoundary")) {
                //Net tag setup
                outerBoundaryTag = placemarkTag.getSubtag("outerBoundary"); 
                coordinateString = outerBoundaryTag.getSubtagContent("coordinates");
                coordinates      = getCoordinateList(coordinateSet, coordinateString);
            } else {
                coordinates = null;
            }
            
            if (coordinates != null) {
                newPolygon = new Polygon(objectName, styleUrl, coordinates);            
                newPolygon.setDescription(description);
            
                if (hasRef) 
                    newPolygon.setReference(Long.parseLong(placemarkTag.getSubtagContent("Ref")));                        

                if (hasRegion) {
                    Region     region = getRegion(placemarkTag.getSubtag("Region"));
                    Visibility vis = convertRegionToVisibility(region);
                    newPolygon.setVisibility(vis);
                }

                if (placemarkTag.containsSubTag("Visibility")) {
                    Visibility vis = getVisibility(placemarkTag.getSubtag("Visibility"));
                    newPolygon.setVisibility(vis);
                }
                
                if (hasTimestamps) {
                    timestamps = placemarkTag.getSubtagContent("gx:Timestamps");
                    newPolygon.setTimestamps(timestamps);
                }            

                if (hasData) {
                    dataTag = placemarkTag.getSubtag("data");
                    customDataFields = getCustomDataFields(dataTag); 
                    newPolygon.setCustomDataFields(customDataFields);
                }            

                for(XMLTag ibTag: innerBoundaryTags) 
                    newPolygon.addInnerBoundary(getInnerBoundary(coordinateSet, layer, ibTag));

                //Add the new object as a Parent for the Coordinates it uses.
                coordinates.setParentObject(newPolygon);
                
                return newPolygon;
            } else {
                return null;
            }                        
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in FmXmlImporter.getPolygon(VectorLayer, XMLTag) - " + e);
            return null;
        }        
    }    
    
    /**
     * Reads PolygonStyle from XML and returns a PolygonStyle object.
     * 
     * @param styleTag
     * @return 
     */
    public static PolygonStyle getPolygonStyle(XMLTag styleTag) {
        try {
            Color        fillColor;
            int          colorMode, fill;
            OutlineStyle outlineStyle;
            PolygonStyle polygonStyle;
            String       colorModeString, featureType, styleID, styleTagName;
            XMLTag       outlineTag, polygonStyleTag;

            styleTagName    = styleTag.getTagName();
            styleID         = styleTagName.substring(styleTagName.indexOf("id=\"") + 4,  styleTagName.length() - 1);
            polygonStyleTag = styleTag.getSubtag("PolyStyle");
            fillColor       = ColorHelper.parseHexStandard(polygonStyleTag.getSubtagContent("color"));
            colorModeString = polygonStyleTag.getSubtagContent("colorMode");
            fill            = Integer.parseInt(polygonStyleTag.getSubtagContent("fill"));
            outlineTag      = polygonStyleTag.getSubtag("outlines");
            
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
            
            //Load Icon, if there is one.
            if (polygonStyleTag.containsSubTag("Icon")) {
                XMLTag  iconTag = polygonStyleTag.getSubtag("Icon");
                String  href    = iconTag.getSubtagContent("href");
                
                polygonStyle.setImageFileName(href);
            }            
            
            if (polygonStyleTag.containsSubTag("featureType")) {
                featureType = polygonStyleTag.getSubtagContent("featureType");
            } else {
                featureType = ThemeConstants.UNKNOWN;
            }
            
            polygonStyle.setFeatureType(featureType);
            
            //Read in OutlineStyles
            if (outlineTag != null) {
                if (outlineTag.containsSubTag("outlineStyle")) {
                    ArrayList<XMLTag> outlineStyleTags;

                    outlineStyleTags = outlineTag.getSubtags("outlineStyle");

                    for (XMLTag tag: outlineStyleTags) {
                        outlineStyle = getOutlineStyle(tag);
                        polygonStyle.addOutlineStyle(outlineStyle);
                    }

                    polygonStyle.setOutline(true);
                }
            } else {
                polygonStyle.setOutline(false);
            }
            
            return polygonStyle;
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in FmXmlImport.getPolygonStyle(XMLTag) - " + e);
            return null;
        }
    }    
    
    /**
     * Creates a Region object from FmXml code.
     * 
     * @param regionTag
     * @return 
     */
    public static Region getRegion(XMLTag regionTag) {
        try {
            boolean         hasMaxAltitude, hasMinAltitude;
            float           north, south, east, west, minAlt, maxAlt;
            float           minLod, maxLod;
            LatLonAltBox    latLonAltBox;
            LevelOfDetail   lod;
            Region          newRegion;
            String          minLodPixels, maxLodPixels, regionName;
            XMLTag          latLonAltBoxTag, lodTag;

            regionName      = regionTag.getSubtagContent("Name");
            latLonAltBoxTag = regionTag.getSubtag("LatLonAltBox");
            lodTag          = regionTag.getSubtag("Lod");
            lod             = getLevelOfDetail(lodTag);

            hasMaxAltitude  = latLonAltBoxTag.containsSubTag("maxAltitude");
            hasMinAltitude  = latLonAltBoxTag.containsSubTag("minAltitude");            

            north   = Float.parseFloat(latLonAltBoxTag.getSubtagContent("north"));
            south   = Float.parseFloat(latLonAltBoxTag.getSubtagContent("south"));
            east    = Float.parseFloat(latLonAltBoxTag.getSubtagContent("east"));
            west    = Float.parseFloat(latLonAltBoxTag.getSubtagContent("west"));
            
            if (hasMinAltitude) {
                minAlt = Float.parseFloat(latLonAltBoxTag.getSubtagContent("minAltitude"));
            } else {
                minAlt = -1;
            }
            
            if (hasMaxAltitude) {
                maxAlt = Float.parseFloat(latLonAltBoxTag.getSubtagContent("maxAltitude"));
            } else {
                maxAlt = -1;
            }

            latLonAltBox = new LatLonAltBox(north, south, east, west, minAlt, maxAlt);
            newRegion    = new Region(regionName, latLonAltBox, lod);

            return newRegion;
            
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in FmXmlImporter.getRegion(XMLTag) - " + e);
            return null;
        }        
    }    
    
    /**
     * Loads a ColorStyle from a given style XMLTag
     * 
     * @param styleTag
     * @return 
     */
    public static ColorStyle getStyle(XMLTag styleTag) {
        ColorStyle  style      = null;
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

                style.setLabel(labelStyle);
            }

            return style;
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in FmXmlImporter.getStyle(XmlTag) - " + e);
            
            return null;
        }
    }    
    
    /**
     * Creates a TileLayer object from the FmXML for TileLayer.
     * 
     * @param layerTag
     * @return 
     */
    public static TileLayer getTileLayer(XMLTag layerTag) {                
        try {
            boolean     visible;
            String      extension, layerDescription, layerName, tileLocation;
            TileSource  tileSource;
            XMLTag      descriptionTag, tileSourceTag;
            
            descriptionTag   = layerTag.getSubtag("Description");
            layerName        = layerTag.getSubtagContent("Name");    
            tileSourceTag    = layerTag.getSubtag("TileSource");
            tileLocation     = tileSourceTag.getSubtagContent("href");
            extension        = tileLocation.substring(tileLocation.lastIndexOf("."));
                    
            if (descriptionTag != null) {
                layerDescription = removeCDataTag(layerTag.getSubtagContent("Description"));
            } else {
                layerDescription = "";
            }
            
            if (layerTag.containsSubTag("visible")) {
                visible = Boolean.parseBoolean(layerTag.getSubtagContent("visible"));
            } else {
                visible = true;
            }
            
            if (extension.equalsIgnoreCase(".MbTiles")) {
                tileSource = new MbTileSource(tileLocation);
            } else if (tileLocation.toLowerCase().startsWith("http")) {
                tileSource = new TileServerTileSource(tileLocation, layerName);
            } else {
                tileSource = new DirectoriesTileSource(tileLocation);
            }
            
            return new TileLayer(layerName, layerDescription, visible, tileSource);
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in FmXmlImporter.getTileLayer(XmlTag) - " + e);
            return null;
        }
    }
    
    /**
     * Loads a layer from a file.
     * 
     * @param openedMap
     * @param layerTag
     * @param coordinateSet
     * @return 
     */
    public static VectorLayer getVectorLayer(DigitalMap openedMap, 
                                             XMLTag layerTag,
                                             NodeMap coordinateSet) {
        try {
            ArrayList<XMLTag>           objects, tags, subTags, subSubTags;
            boolean                     layerLocked;
            VectorObject                newObject;
            String                      layerDescription, layerName, timeSpanBegin, timeSpanEnd;
            VectorLayer                 newLayer;
            XMLTag                      tempTag, descriptionTag, timeSpanTag;

            descriptionTag   = layerTag.getSubtag("Description");
            layerName        = layerTag.getSubtagContent("Name");
            layerDescription = removeCDataTag(layerTag.getSubtagContent("Description"));
            layerLocked      = Boolean.parseBoolean(layerTag.getSubtagContent("locked"));
            tags             = layerTag.getTags("Placemark");
            objects          = layerTag.getTagSubtags("objects");
            timeSpanTag      = layerTag.getSubtag("TimeSpan");
            newLayer         = new VectorLayer(layerName);    //create new VectorLayer

            newLayer.setParentMap(openedMap);
            
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

            for (XMLTag currentTag: objects) {
                newObject = loadObject(newLayer, currentTag, coordinateSet);

                if (newObject != null)
                    newLayer.addObject(newObject);
            }

            return newLayer;
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in FmXmlImport.getVectorLayer(DigitalMap, XMLTag, NodeMap) - " + e);
            return null;
        }        
    }    
    
    /**
     * Parses the FmXml for Visibility and returns a Visibility object.
     * 
     * @param visTag
     * @return 
     */
    public static Visibility getVisibility(XMLTag visTag) {
        float   max, min;
        
        boolean hasMax = visTag.containsSubTag("maxTileZoom");
        boolean hasMin = visTag.containsSubTag("minTileZoom");
        
        if (hasMax) {
            max = Float.parseFloat(visTag.getSubtagContent("maxTileZoom"));
        } else {
            max = 25;
        }
        
        if (hasMin) {
            min = Float.parseFloat(visTag.getSubtagContent("minTileZoom"));
        } else {
            min = 0;
        }
        
        return new Visibility(max, min);
    }
    
    /**
     * Reads in a FmXml PlaceMark and returns a VectorObject representing it.
     * 
     * @param layer
     * @param objectTag
     * @param coordinateSet
     * @return 
     */
    public static VectorObject loadObject(VectorLayer layer, 
                                          XMLTag objectTag,
                                          NodeMap coordinateSet) {
        
        VectorObject   newObject;               
        
        try {
            if (objectTag.nameStartsWith("MultiGeometry")) {
                newObject = getMultiGeometry(layer, objectTag, coordinateSet);
            } else if (objectTag.nameStartsWith("PhotoPoint")) {
                newObject = getPhotoPoint(layer, objectTag, coordinateSet);
            } else if (objectTag.nameStartsWith("Polygon")) {
                newObject = getPolygon(layer, objectTag, coordinateSet);
            } else if (objectTag.nameStartsWith("Point")) {
                newObject = getMapPoint(layer, objectTag, coordinateSet);
            } else if (objectTag.nameStartsWith("LineString")) { 
                newObject = getLineString(layer, objectTag, coordinateSet);
            } else if (objectTag.nameStartsWith("LinearRing")) {      
                newObject = getLinearRing(layer, objectTag, coordinateSet);
            } else {
                newObject = null;
            }

            return newObject;
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in FmXmlImporter.loadObject(VectorLayer, XmlTag, NodeMap) - " + e);
            return null;
        }
    }    
    
    /**
     * Static shortcut to load a map.
     * 
     * @param mapFile
     * @param progressIndicator
     * @return 
     */
    public static DigitalMap openFile(File mapFile, ProgressIndicator progressIndicator) {
        FmXmlImporter importer = new FmXmlImporter();
        return importer.importAsMap(mapFile, progressIndicator);
    }
    
    /**
     * Loads a FmXml file
     * 
     * @param mapFile
     * @param progressIndicator
     * @return 
     */
    @Override
    public DigitalMap importAsMap(File mapFile, ProgressIndicator progressIndicator) {
        ArrayList<XMLTag>           nodes, styleTags;
        ColorStyle                  currentStyle;
        DigitalMap                  mainMap;
        float                       alt, lat, lon;
        NodeMap                     coordinateSet;
        Layer                       currentLayer;
        MapProjection               mapProjection;
        MapTheme                    mapTheme;
        String                      currentTagName, mapDescription, mapName;
        String                      projection, viewInfo;
        StringTokenizer             st;
        XMLParser                   mapXMLParser;
        XMLTag                      documentTag, layers, mapStyle;
        
        mainMap = new DigitalMap();
        mainMap.setMapFile(mapFile);
                
        if (progressIndicator == null)
            progressIndicator = new ProgressBarPanel();
        
        try {       
            progressIndicator.setVisible(true);
            progressIndicator.updateProgress("Reading Map Information", 5);
            
            mapXMLParser   = new XMLParser(new FileReader(mapFile));
            documentTag    = mapXMLParser.parseDocument();
            
            mapName        = documentTag.getSubtagContent("name");
            mapDescription = documentTag.getSubtagContent("description");
            viewInfo       = documentTag.getSubtagContent("view");
            layers         = documentTag.getSubtag("layers");
            st             = new StringTokenizer(viewInfo, ",");
            lon            = Float.parseFloat(st.nextToken());
            lat            = Float.parseFloat(st.nextToken());
            alt            = TileMath.getVectorMapZoom((int) Float.parseFloat(st.nextToken())); 
            projection     = documentTag.getSubtagContent("projection");
            nodes          = documentTag.getTagSubtags("nodes");
            
            if (nodes.size() > 0) {
                coordinateSet = new NodeMap((int) (nodes.size() * 1.5f));
            } else {
                coordinateSet = new NodeMap(1000);
            }
            
            if (projection.equalsIgnoreCase("Mercator")) {                
                mapProjection = new MercatorProjection(lat, lon, alt);
            } else {
                //default to Mercator
                mapProjection = new MercatorProjection(lat, lon, alt);
            }
            
            mainMap = new DigitalMap(mapName, mapProjection); 
            
            if (nodes.size() > 0) {
                //has node tags
                progressIndicator.updateProgress("Reading Nodes", 10);
                parseNodes(coordinateSet, nodes);
                mainMap.setCoordinateSet(coordinateSet);
            } else {
                //does not have node tags, do nothing
            }
            
            //read in styles
            progressIndicator.updateProgress("Reading Styles", 35);
            mapTheme  = new MapTheme("Map File Theme");
            mainMap.setTheme(mapTheme, null, progressIndicator);
            mainMap.getMapThemeManager().addTheme(mapTheme);
            mapStyle  = documentTag.getSubtag("mapstyle");
            styleTags = mapStyle.getTags("Style");

            for (XMLTag currentTag: styleTags) {
                currentTagName = currentTag.getTagName();
                if (currentTagName.startsWith("Style")) {
                    if (currentTag.getSubtag("BackColor") != null) {
                        Color backColor = ColorHelper.parseHexStandard(currentTag.getSubtagContent("BackColor"));
                        mapTheme.setBackgroundColor(backColor);                                                
                    } else if (currentTag.containsSubTag("ColorRamp")) {
                        ColorRamp colorRamp;
                        colorRamp = getColorRamp(currentTag.getTagValue(), currentTag.getSubtag("ColorRamp")); 
                        mapTheme.addColorRamp(colorRamp);
                    } else {      
                        currentStyle = getStyle(currentTag);
                        
                        if (currentStyle != null)
                            mapTheme.addStyleElement(currentStyle);
                    }
                }
            }            
            
            //read in layers   
            progressIndicator.updateProgress("Reading Layers", 75);
            for (XMLTag layerTag: layers.getSubtags()) {
                currentLayer = null;
                
                if (layerTag.getTagName().equalsIgnoreCase("vectorlayer")) {
                    currentLayer = getVectorLayer(mainMap, layerTag, coordinateSet);
                } else if (layerTag.getTagName().equalsIgnoreCase("heatmap")) {
                    currentLayer = getHeatMapLayer(mainMap, layerTag);    
                } else if (layerTag.getTagName().equalsIgnoreCase("networklayer")) {
                    currentLayer = getNetworkLayer(layerTag);
                } else if (layerTag.getTagName().equalsIgnoreCase("tilelayer")) {
                    currentLayer = getTileLayer(layerTag);
                }
                
                if (currentLayer != null)
                    mainMap.addLayer(currentLayer);
            }
            
            if (!coordinateSet.isEmpty())
                mainMap.setCoordinateSet(coordinateSet);
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in FmXmlImporter.openFile(File) - " + e);
        }
        
        mainMap.setMapFile(mapFile);
        progressIndicator.updateProgress("Map Loaded", 98);
        progressIndicator.finish();             
        
        return mainMap;
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
        throw new UnsupportedOperationException("Not supported yet.");
    }    
    
    /**
     * Parses Node tags and places them in the coordinateSet HashMap
     * 
     * @param coordinateSet
     * @param nodeTags 
     */
    public static void parseNodes(NodeMap coordinateSet,
                                  ArrayList<XMLTag> nodeTags) {
        Coordinate coordinate;
        long       nodeId;
        
        try {
            for (XMLTag tag: nodeTags) {
                nodeId     = Long.parseLong(tag.getTagValue());
                coordinate = new Coordinate(tag.getTagContent());

                if (nodeId > 0 && coordinate != null) {
                    //coordinate.setId(nodeId);
                    coordinateSet.put(nodeId, coordinate);                
                }
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in FmXmlImporter.parseNodes(HashMap, ArrayList) - " + e);
        }
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
}
