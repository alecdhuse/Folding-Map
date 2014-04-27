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
package co.foldingmap.testFileFormats;

import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.LatLonAltBox;
import co.foldingmap.map.vector.MapIcon;
import co.foldingmap.map.vector.NetworkLayer;
import co.foldingmap.map.vector.LinearRing;
import co.foldingmap.map.vector.Region;
import co.foldingmap.map.vector.InnerBoundary;
import co.foldingmap.map.vector.MapPoint;
import co.foldingmap.map.vector.LevelOfDetail;
import co.foldingmap.map.vector.Polygon;
import co.foldingmap.map.vector.MultiGeometry;
import co.foldingmap.map.vector.LatLonBox;
import co.foldingmap.map.vector.LineString;
import co.foldingmap.map.vector.CoordinateList;
import co.foldingmap.map.raster.ImageOverlay;
import co.foldingmap.map.themes.IconStyle;
import co.foldingmap.map.themes.LabelStyle;
import co.foldingmap.map.themes.LineStyle;
import co.foldingmap.map.themes.PolygonStyle;
import co.foldingmap.testMapObjects.TestRoadLineString;
import co.foldingmap.xml.XMLTag;
import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import org.junit.Ignore;

/**
 *
 * @author Alec
 */
@Ignore
public class KmlTestData {
    
    public static XMLTag getCoordinateTag() {
        XMLTag coordinatesTag;
        
        coordinatesTag = new XMLTag("coordinates", "-122.68031,45.528698,0 -122.68124,45.528683 -122.681305,45.52868 -122.68133,45.528675,0 -122.68142,45.52867,0 -122.68225,45.528656,0 -122.682335,45.528656,0 -122.68236,45.528656,0 -122.68242,45.528656,0 -122.68326,45.52864,0 -122.68335,45.52864,0, -122.68344,45.528637,0, -122.68427,45.528625,0 -122.684364,45.52862,0 -122.684456,45.52862,0 -122.685394,45.528606,0");                
        
        return coordinatesTag;
    }
    
    public static HashMap<String, String> getExtendedDataObject() {
        HashMap<String, String> hashMap;
        
        hashMap = new HashMap<String, String>();
        hashMap.put("holeNumber",   "1");
        hashMap.put("holeYardage",  "234");
        hashMap.put("holePar",      "4");
        
        return hashMap;
    }
    
    public static XMLTag getExtendedDataTag() {
        XMLTag dataTag1, dataTag2, dataTag3, returnTag;
        
        dataTag1  = new XMLTag("Data name=\"holeNumber\"",  new XMLTag("value", "1"));
        dataTag2  = new XMLTag("Data name=\"holeYardage\"", new XMLTag("value", "234"));
        dataTag3  = new XMLTag("Data name=\"holePar\"",     new XMLTag("value", "4"));
        returnTag = new XMLTag("ExtendedData", dataTag1);
        returnTag.addSubtag(dataTag2);
        returnTag.addSubtag(dataTag3);
                
        return returnTag;
    }
    
    public static ImageOverlay getGroundOverlayObject() {
        LatLonBox    bounds      = new LatLonBox(37.83234f, 37.832122f, -122.373033f, -122.373033f);
        MapIcon      mapIcon     = new MapIcon("", "http://www.google.com/intl/en/images/logo.gif");
        ImageOverlay testOverlay = new ImageOverlay("GroundOverlay.kml", mapIcon, bounds);
        
        mapIcon.setRefreshInterval(86400);
        mapIcon.setRefreshMode(MapIcon.ON_INTERVAL);
        
        return testOverlay;
    }
    
    public static XMLTag getGroundOverlayTag() {
        
        XMLTag gOverlay  = new XMLTag("north",     new XMLTag("name", "GroundOverlay.kml"));
        XMLTag icon      = new XMLTag("Icon",      new XMLTag("href", "http://www.google.com/intl/en/images/logo.gif"));
        XMLTag north     = new XMLTag("north",     "37.83234");
        XMLTag south     = new XMLTag("south",     "37.832122");
        XMLTag east      = new XMLTag("east",      "-122.373033");
        XMLTag west      = new XMLTag("west",      "-122.373033");
        XMLTag rotation  = new XMLTag("rotation",  "45");
        XMLTag latLonBox = new XMLTag("LatLonBox", north);
        
        latLonBox.addSubtag(south);
        latLonBox.addSubtag(east);
        latLonBox.addSubtag(west);
        latLonBox.addSubtag(rotation);
        
        icon.addSubtag(new XMLTag("refreshMode",     "onInterval"));
        icon.addSubtag(new XMLTag("refreshInterval", "86400"));
        icon.addSubtag(new XMLTag("viewBoundScale",  "0.75"));
        
        gOverlay.addSubtag(new XMLTag("color",      "7fffffff"));
        gOverlay.addSubtag(new XMLTag("drawOrder",  "1"));
        gOverlay.addSubtag(icon);
        gOverlay.addSubtag(latLonBox);
        
        return gOverlay;
    }
    
    public static MapIcon getIconObject() {
        MapIcon icon = new MapIcon("ID", "http://foldingmap.co/icons/standard_bar-small.png");
        
        icon.setRefreshInterval(86400f);
        icon.setRefreshMode(MapIcon.ON_INTERVAL);
        
        return icon;
    }
    
    public static XMLTag getIconTag() {
        XMLTag returnTag;
        
        returnTag = new XMLTag("Icon id=\"ID\"", new XMLTag("href", "http://foldingmap.co/icons/standard_bar-small.png"));
        returnTag.addSubtag(new XMLTag("refreshMode",     "onInterval"));
        returnTag.addSubtag(new XMLTag("refreshInterval", "86400"));
        
        return returnTag;
    }
    
    
    public static IconStyle getIconStyleObject() {
        IconStyle is = new IconStyle("Style", new Color(204, 0, 0, 255));
        
        is.setImageFileName("http://foldingmap.co/icons/standard_bar-small.png");
        is.setScale(1.399f);
        
        return is;
    }
    
    public static XMLTag getIconStyleTag() {
        XMLTag iconStyleTag, iconTag, returnTag;
                        
        iconTag      = new XMLTag("Icon", new XMLTag("href", "http://foldingmap.co/icons/standard_bar-small.png"));
        iconStyleTag = new XMLTag("IconStyle", new XMLTag("color", "ff0000cc"));
        iconStyleTag.addSubtag(new XMLTag("scale", "1.399"));        
        iconStyleTag.addSubtag(iconTag); 
                
        returnTag = new XMLTag("Style id=\"Style\"", iconStyleTag);
        
        return returnTag;
    }
    
    public static InnerBoundary getInnerBoundartObject() {
        CoordinateList<Coordinate> list = new CoordinateList<Coordinate>();
        
        list.add(new Coordinate(30f, 37.818977f, -122.366212f));
        list.add(new Coordinate(30f, 37.819294f, -122.365424f));
        list.add(new Coordinate(30f, 37.819731f, -122.365704f));
        list.add(new Coordinate(30f, 37.819402f, -122.366488f));
        
        /* FoldingMap objects do not require that closed objects' CoordinateLists 
         * have the first and last coordinates the same.
         */
        
        return new InnerBoundary(list);
    }
    
    public static XMLTag getInnerBoundaryTag() {
        XMLTag coordinatesTag, linearRingTag, returnTag;
        
        coordinatesTag = new XMLTag("coordinates", "-122.366212,37.818977,30 -122.365424,37.819294,30 -122.365704,37.819731,30 -122.366488,37.819402,30 -122.366212,37.818977,30");
        linearRingTag  = new XMLTag("LinearRing", coordinatesTag);
        returnTag      = new XMLTag("innerBoundaryIs", linearRingTag);
        
        return returnTag;
    }
    
    public static LabelStyle getLabelStyleObject() {
        Color fillColor, outlineColor;
        Font  font;
        
        fillColor    = new Color(204, 0, 0, 255);
        outlineColor = new Color(60, 68, 75);
        font         = new Font(Font.SANS_SERIF, Font.BOLD, 18);
        
        return new LabelStyle(fillColor, outlineColor, font);
    }
    
    public static XMLTag getLabelStyleTag() {
        XMLTag returnTag;
        
        returnTag = new XMLTag("LabelStyle", new XMLTag("color", "ff0000cc"));
        returnTag.addSubtag(new XMLTag("colorMode", "random"));
        returnTag.addSubtag(new XMLTag("scale",     "1.5"));
        
        return returnTag;
    }
    
    public static LinearRing getLinearRingObject() {
        CoordinateList<Coordinate> coordinates = TestRoadLineString.getCoordinates();
        
        return new LinearRing("Northwest Johnson Street", "Road - City Secondary", coordinates);        
    }
    
    public static XMLTag getLinearRingTag() {
        XMLTag LineStringTag, returnTag;
            
        LineStringTag  = new XMLTag("LinearRing id=\"Road - City Secondary\"", new XMLTag("gx:altitudeOffset", "0"));
        returnTag      = new XMLTag("PlaceMark", new XMLTag("name", "Northwest Johnson Street"));
                
        LineStringTag.addSubtag(new XMLTag("extrude",    "0"));
        LineStringTag.addSubtag(new XMLTag("tessellate", "0"));
        LineStringTag.addSubtag(new XMLTag("altitudeMode", "clampToGround"));
        LineStringTag.addSubtag(new XMLTag("gx:drawOrder", "0"));                
        LineStringTag.addSubtag(getCoordinateTag());
        returnTag.addSubtag(LineStringTag);
                
        return returnTag;
    }            
    
    public static XMLTag getLineStringTag() {
        XMLTag LineStringTag, returnTag;
            
        LineStringTag  = new XMLTag("LineString id=\"Road - City Secondary\"", new XMLTag("gx:altitudeOffset", "0"));
        returnTag      = new XMLTag("PlaceMark", new XMLTag("name", "Northwest Johnson Street"));
                
        LineStringTag.addSubtag(new XMLTag("extrude",    "0"));
        LineStringTag.addSubtag(new XMLTag("tessellate", "0"));
        LineStringTag.addSubtag(new XMLTag("altitudeMode", "clampToGround"));
        LineStringTag.addSubtag(new XMLTag("gx:drawOrder", "0"));                
        LineStringTag.addSubtag(getCoordinateTag());
        returnTag.addSubtag(LineStringTag);
                
        return returnTag;
    }
    
    public static LevelOfDetail getLodObject() {
        return new LevelOfDetail(-1, 256);
    }
    
    public static XMLTag getLodTag() {
        XMLTag returnTag;
        
        returnTag = new XMLTag("Lod", new XMLTag("minLodPixels", "256"));
        returnTag.addSubtag(new XMLTag("maxLodPixels", "-1"));
        returnTag.addSubtag(new XMLTag("minFadeExtent", "0"));
        returnTag.addSubtag(new XMLTag("maxFadeExtent", "0"));
        
        return returnTag;
    }
    
    public static LatLonAltBox getLatLonAltBoxObject() {
        return new LatLonAltBox(43.374f, 42.983f, -0.335f, -1.423f, 0, 0);
    }
    
    public static XMLTag getLatLonAltBoxTag() {
        XMLTag returnTag;
        
        returnTag = new XMLTag("LatLonAltBox", new XMLTag("north", "43.374"));
        returnTag.addSubtag(new XMLTag("south", "42.983"));
        returnTag.addSubtag(new XMLTag("east",  "-0.335"));
        returnTag.addSubtag(new XMLTag("west",  "-1.423"));
        returnTag.addSubtag(new XMLTag("minAltitude", "0"));
        returnTag.addSubtag(new XMLTag("maxAltitude", "0"));
        
        return returnTag;
    }
    
    public static LineStyle getLineStyleObject() {        
        return new LineStyle("linestyle", new Color(204, 0, 0, 255), 4, LineStyle.SOLID, false);
    }
    
    public static XMLTag getLineStyleTag() {
        XMLTag lineStyle, returnTag;        
        
        lineStyle = new XMLTag("LineStyle", new XMLTag("color", "ff0000cc"));
        lineStyle.addSubtag(new XMLTag("width", "4")); 
        lineStyle.addSubtag(new XMLTag("gx:labelVisibility", "1")); 
        
        returnTag = new XMLTag("Style id=\"linestyle\"", lineStyle);
        
        return returnTag;
    }
    
    public static MultiGeometry getMultiGeometryObject() {
        MultiGeometry multi = new MultiGeometry("test");
        
        multi.addObject(getPointObject());
        multi.addObject(TestRoadLineString.getLineString());
        multi.addObject(getLinearRingObject());
        multi.addObject(getPolygonObject());
        
        return multi;
    }
    
    public static XMLTag getMultiGeometryTag() {
        XMLTag placemarkTag = new XMLTag("MultiGeometry", new XMLTag("name", "test"));
        XMLTag multiTag     = new XMLTag("MultiGeometry", getPointTag());
        
        multiTag.addSubtag(getLineStringTag());
        multiTag.addSubtag(getLinearRingTag());
        multiTag.addSubtag(getPolygonTag());
        placemarkTag.addSubtag(multiTag);
                
        return placemarkTag;
    }
    
    public static NetworkLayer getNetworkLinkObject() {
        NetworkLayer testLayer = new NetworkLayer("Network Link", "http://flash3.ess.washington.edu/lightning_src.kmz");
        
        testLayer.setLayerDescription("Test Network Link");
        
        return testLayer;
    }
        
    public static XMLTag getNetworkLinkTag() {
        XMLTag networkLinkTag = new XMLTag("NetworkLink", new XMLTag("name", "Network Link"));
        XMLTag linkTag        = new XMLTag("Link", new XMLTag("href", "http://flash3.ess.washington.edu/lightning_src.kmz"));
        
        networkLinkTag.addSubtag(new XMLTag("visibility", "0"));
        networkLinkTag.addSubtag(new XMLTag("open", "0"));
        networkLinkTag.addSubtag(new XMLTag("description", "Test Network Link"));
        networkLinkTag.addSubtag(new XMLTag("refreshVisibility", "0"));
        networkLinkTag.addSubtag(new XMLTag("flyToView", "0"));
        networkLinkTag.addSubtag(linkTag);
                
        return networkLinkTag;
    }
    
    public static MapPoint getPointObject() {
        Coordinate c = new Coordinate(0, -43.60505741890396f, 170.1435558771009f);
        
        return new MapPoint("Pin on a mountaintop", "pushpin", "", c);
    }
    
    public static XMLTag getPointTag() {
        XMLTag coordinateTag, nameTag, pointTag, returnTag, styleTag;
                
        coordinateTag = new XMLTag("coordinates", "170.1435558771009,-43.60505741890396,0");
        pointTag      = new XMLTag("Point", coordinateTag);
        styleTag      = new XMLTag("styleUrl", "#pushpin");
        nameTag       = new XMLTag("name", "Pin on a mountaintop");
        returnTag     = new XMLTag("Placemark id=\"mountainpin1\"", nameTag);
        returnTag.addSubtag(styleTag);
        returnTag.addSubtag(pointTag);
        
        return returnTag;
    }
    
    public static Polygon getPolygonObject() {
        CoordinateList<Coordinate> list = new CoordinateList<Coordinate>();
        
        list.add(new Coordinate("-122.365662,37.826988,0"));
        list.add(new Coordinate("-122.365202,37.826302,0"));
        list.add(new Coordinate("-122.364581,37.82655,0"));
        list.add(new Coordinate("-122.365038,37.827237,0"));
        list.add(new Coordinate("-122.365662,37.826988,0"));
        
        return new Polygon("Polygon", "Polygon", list);
    }
    
    public static PolygonStyle getPolygonStyleObject() {
        PolygonStyle style;
        
        style = new PolygonStyle("Park", new Color(255, 255, 255, 255));
        style.setOutline(true);
        
        return style;
    }
    
    public static XMLTag getPolygonTag() {
        XMLTag coordinateTag, linearRingTag, nameTag, outerBoundTag, polyTag, returnTag;
        
        coordinateTag = new XMLTag("coordinates",     "-122.365662,37.826988,0 -122.365202,37.826302,0 -122.364581,37.82655,0 -122.365038,37.827237,0 -122.365662,37.826988,0");
        linearRingTag = new XMLTag("LinearRing",      coordinateTag);
        outerBoundTag = new XMLTag("outerBoundaryIs", linearRingTag);
        polyTag       = new XMLTag("Polygon",         outerBoundTag);
        nameTag       = new XMLTag("name",            "Polygon");
        returnTag     = new XMLTag("Placemark id=\"Polygon\"", nameTag);
        returnTag.addSubtag(polyTag);
        
        return returnTag;
    }
    
    public static XMLTag getPolyStyleTag() {
        XMLTag returnTag;
        
        returnTag = new XMLTag("PolyStyle id=\"Park\"", new XMLTag("color", "ffffffff"));
        returnTag.addSubtag(new XMLTag("colorMode", "normal"));
        returnTag.addSubtag(new XMLTag("fill",      "1"));
        returnTag.addSubtag(new XMLTag("outline",   "1"));
        
        return returnTag;
    }
    
    public static Region getRegionObject() {
        return new Region("Test Region", getLatLonAltBoxObject(), getLodObject());
    }
    
    public static XMLTag getRegionTag() {
        XMLTag returnTag;
        
        returnTag = new XMLTag("Region id=\"Test Region\"", getLatLonAltBoxTag());
        returnTag.addSubtag(getLodTag());
        
        return returnTag;
    }
    
    public static LineString getTrackObject() {
        CoordinateList<Coordinate> cList = new CoordinateList<Coordinate>();
        
        cList.add(new Coordinate("-122.207881,37.371915,156.000000,2010-05-28T02:02:09Z"));
        cList.add(new Coordinate("-122.205712,37.373288,152.000000,2010-05-28T02:02:35Z"));
        cList.add(new Coordinate("-122.204678,37.373939,147.000000,2010-05-28T02:02:44Z"));
        cList.add(new Coordinate("-122.203572,37.374630,142.199997,2010-05-28T02:02:53Z"));
        cList.add(new Coordinate("-122.203451,37.374706,141.800003,2010-05-28T02:02:54Z"));
        cList.add(new Coordinate("-122.203329,37.374780,141.199997,2010-05-28T02:02:55Z"));
        cList.add(new Coordinate("-122.203207,37.374857,140.199997,2010-05-28T02:02:56Z"));
        
        return new LineString("", "", cList);
    }
    
    public static XMLTag getTrackTag() {        
        
        XMLTag whenTag0  = new XMLTag("when", "2010-05-28T02:02:09Z");
        XMLTag whenTag1  = new XMLTag("when", "2010-05-28T02:02:35Z");
        XMLTag whenTag2  = new XMLTag("when", "2010-05-28T02:02:44Z");
        XMLTag whenTag3  = new XMLTag("when", "2010-05-28T02:02:53Z");
        XMLTag whenTag4  = new XMLTag("when", "2010-05-28T02:02:54Z");
        XMLTag whenTag5  = new XMLTag("when", "2010-05-28T02:02:55Z");
        XMLTag whenTag6  = new XMLTag("when", "2010-05-28T02:02:56Z");
        
        XMLTag coordTag0 = new XMLTag("gx:coord", "-122.207881 37.371915 156.000000");
        XMLTag coordTag1 = new XMLTag("gx:coord", "-122.205712 37.373288 152.000000");
        XMLTag coordTag2 = new XMLTag("gx:coord", "-122.204678 37.373939 147.000000");
        XMLTag coordTag3 = new XMLTag("gx:coord", "-122.203572 37.374630 142.199997");
        XMLTag coordTag4 = new XMLTag("gx:coord", "-122.203451 37.374706 141.800003");
        XMLTag coordTag5 = new XMLTag("gx:coord", "-122.203329 37.374780 141.199997");
        XMLTag coordTag6 = new XMLTag("gx:coord", "-122.203207 37.374857 140.199997");
        
        XMLTag trackTag  = new XMLTag("gx:Track", whenTag0);
        
        trackTag.addSubtag(whenTag1);
        trackTag.addSubtag(whenTag2);
        trackTag.addSubtag(whenTag3);
        trackTag.addSubtag(whenTag4);
        trackTag.addSubtag(whenTag5);
        trackTag.addSubtag(whenTag6);
        
        trackTag.addSubtag(coordTag0);
        trackTag.addSubtag(coordTag1);
        trackTag.addSubtag(coordTag2);
        trackTag.addSubtag(coordTag3);
        trackTag.addSubtag(coordTag4);
        trackTag.addSubtag(coordTag5);
        trackTag.addSubtag(coordTag6);        
        
        return new XMLTag("Placemark", trackTag);
    }
}
