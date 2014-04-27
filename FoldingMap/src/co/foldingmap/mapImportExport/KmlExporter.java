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

import co.foldingmap.map.vector.Region;
import co.foldingmap.map.vector.LinearRing;
import co.foldingmap.map.vector.MultiGeometry;
import co.foldingmap.map.vector.LatLonAltBox;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.CoordinateList;
import co.foldingmap.map.vector.VectorObject;
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
import co.foldingmap.map.themes.StyleMap;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.Layer;
import co.foldingmap.map.raster.ImageOverlay;
import co.foldingmap.xml.XmlOutput;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Alec
 */
public class KmlExporter {
    
    public void exportGroundOverlay(XmlOutput kmlWriter, ImageOverlay groundOverlay) {
        LatLonAltBox bounds = groundOverlay.getBounds();
                
        kmlWriter.openTag("GroundOverlay id=\"" + groundOverlay.getID() + "\"");
        
        exportIcon(kmlWriter, groundOverlay.getMapIcon());
        
        kmlWriter.openTag("LatLonBox");
        
        kmlWriter.writeTag("north",     Float.toString(bounds.getNorth()));
        kmlWriter.writeTag("south",     Float.toString(bounds.getSouth()));
        kmlWriter.writeTag("east",      Float.toString(bounds.getEast()));
        kmlWriter.writeTag("west",      Float.toString(bounds.getWest()));
        kmlWriter.writeTag("rotation",  "0");
        
        kmlWriter.closeTag("LatLonBox");
        
        kmlWriter.closeTag("GroundOverlay");
    }    
    
    /**
     * Writes out the KML for the MapIcon Object.
     * 
     * @param kmlWriter
     * @param mapIcon 
     */
    public void exportIcon(XmlOutput kmlWriter, MapIcon mapIcon) {
        kmlWriter.openTag("Icon id=\"" + mapIcon.getID() + "\"");
        
        kmlWriter.writeTag("href",            mapIcon.getFileAddress());
        kmlWriter.writeTag("refreshMode",     Integer.toString(mapIcon.getRefreshMode()));
        kmlWriter.writeTag("refreshInterval", Float.toString(mapIcon.getRefreshInterval()));
        kmlWriter.writeTag("viewRefreshMode", Integer.toString(mapIcon.getViewRefreshMode()));
        
        kmlWriter.closeTag("Icon");        
    }
    
    /**
     * Writes an IconStyle to KML.
     * 
     * @param kmlWriter 
     */
    public static void exportIconStyle(XmlOutput kmlWriter, IconStyle style) {
        try {
            kmlWriter.openTag ("Style id=\"" + style.getID() + "\"");
            kmlWriter.openTag ("IconStyle");

            kmlWriter.writeTag("color", ColorHelper.getColorHexAlphabetical(style.getFillColor()));

            if (style.getColorMode() == ColorStyle.NORMAL) {
                kmlWriter.writeTag("colorMode", "normal");
            } else if (style.getColorMode() == ColorStyle.RANDOM) {
                kmlWriter.writeTag("colorMode", "random");
            }   

            kmlWriter.writeTag("scale",   Float.toString(style.getScale()));
            kmlWriter.writeTag("heading", Float.toString(style.getHeading()));

            if (style.getImageFile() != null) {
                    kmlWriter.openTag ("Icon");
                    kmlWriter.writeTag("href", style.getImageFile().getName());
                    kmlWriter.closeTag("Icon");
            }

            kmlWriter.closeTag("IconStyle");

            if (style.getLabel() != null)
                exportLabelStyle(kmlWriter, style.getLabel());

            kmlWriter.closeTag("Style");     
        } catch (Exception e) {
            System.err.println("Error in KmlExporter.exportIconStyle(KmlOutpu, IconStyle) - " + e);
        }
    }    
    
    
    /**
     * Writes out KML for this LabelStyle.
     * 
     * @param kmlWriter 
     */
    public static void exportLabelStyle(XmlOutput kmlWriter, LabelStyle style) {
        kmlWriter.openTag ("LabelStyle");
        kmlWriter.writeTag("color", ColorHelper.getColorHexAlphabetical(style.getFillColor()));           
        
        kmlWriter.closeTag("LabelStyle");
    }    
    
    /**
     * Writes a LinearRing as KML
     * 
     * @param kmlWriter
     * @param ring 
     */
    public static void exportLinearRing(XmlOutput kmlWriter, LinearRing ring) {
        try {
            kmlWriter.openTag ("Placemark");
            kmlWriter.writeTag("name", ring.getName());
            writeCustomDataFieldsAsXML(kmlWriter, ring);
            
            if (ring.hasDisplayableText(ring.getDescription()) && !ring.getDescription().equalsIgnoreCase("null"))
                kmlWriter.writeTag("description", "<![CDATA[" + ring.getDescription() + "]]>");

            kmlWriter.writeTag("styleUrl", "#" + ring.getObjectClass());

            if (ring.getVisibility() != null) {
                LatLonAltBox bounds = ring.getBoundingBox();
                float max = Region.calculateLodFromTileZoom(ring.getVisibility().getMaxTileZoomLevel(), bounds);
                float min = Region.calculateLodFromTileZoom(ring.getVisibility().getMinTileZoomLevel(), bounds);

                LevelOfDetail lod = new LevelOfDetail(max, min);
                Region        r   = new Region(ring.getName() + "-region", bounds, lod);

                r.toXML(kmlWriter);
            }                          

            kmlWriter.openTag ("LinearRing");
            
            if (ring.getAltitudeMode() == VectorObject.ABSOLUTE) {
                kmlWriter.writeTag("altitudeMode", "absolute");
            } else if (ring.getAltitudeMode() == VectorObject.RELATIVE_TO_GROUND) {
                kmlWriter.writeTag("altitudeMode", "relativeToGround");
            } else {
                kmlWriter.writeTag("altitudeMode", "clampToGround");
            }
                        
            kmlWriter.writeTag("coordinates",  getCoordinateString(ring.getCoordinateList()));
            kmlWriter.closeTag("LineString");

            kmlWriter.closeTag("LinearRing");
            kmlWriter.closeTag("Placemark");
        } catch (Exception e) {
            System.err.println("Error in KmlExporter.exportLinearRing(KmlWriter) Object: " + ring.getName() + " - " + e);
        }
    }         
    
    /**
     * Writes LineString KML to the xmlWriter.
     * 
     * @param kmlWriter
     * @param line 
     */
    public static void exportLineString(XmlOutput kmlWriter, LineString line) {
        try {
            kmlWriter.openTag ("Placemark");

            kmlWriter.writeTag("name", line.getName());
            writeCustomDataFieldsAsXML(kmlWriter, line);
            
            if (line.hasDisplayableText(line.getDescription()) && !line.getDescription().equalsIgnoreCase("null"))
                kmlWriter.writeTag("description", "<![CDATA[" + line.getDescription() + "]]>");

            kmlWriter.writeTag("styleUrl", "#" + line.getObjectClass());

            if (line.getVisibility() != null) {
                LatLonAltBox bounds = line.getBoundingBox();
                float max = Region.calculateLodFromTileZoom(line.getVisibility().getMaxTileZoomLevel(), bounds);
                float min = Region.calculateLodFromTileZoom(line.getVisibility().getMinTileZoomLevel(), bounds);
                
                LevelOfDetail lod = new LevelOfDetail(max, min);
                Region        r   = new Region(line.getName() + "-region", bounds, lod);
                
                r.toXML(kmlWriter);
            }

            kmlWriter.openTag ("LineString");

            if (line.getAltitudeMode() == VectorObject.ABSOLUTE) {
                kmlWriter.writeTag("altitudeMode", "absolute");
            } else if (line.getAltitudeMode() == VectorObject.RELATIVE_TO_GROUND) {
                kmlWriter.writeTag("altitudeMode", "relativeToGround");
            } else {
                kmlWriter.writeTag("altitudeMode", "clampToGround");
            }            
            
            kmlWriter.writeTag("coordinates",  getCoordinateString(line.getCoordinateList()));
            kmlWriter.closeTag("LineString");

            kmlWriter.closeTag("LineString");
            kmlWriter.closeTag("Placemark");
        } catch (Exception e) {
            System.out.println("Error in KmlExporter.exportLineString(KmlWriter, LineString) Object: " + line.getName() + " - " + e);
        }
    }    
    
    /**
     * Writes out a LineStyle to KML.
     * 
     * @param kmlWriter 
     */
    public static void exportLineStyle(XmlOutput kmlWriter, LineStyle style) {
        kmlWriter.openTag ("Style id=\"" + style.getID() + "\"");
        kmlWriter.openTag ("LineStyle");

        kmlWriter.writeTag("color", ColorHelper.getColorHexAlphabetical(style.getFillColor()));        
        kmlWriter.writeTag("width", Float.toString(style.getLineWidth()));

        if (style.isOutlined()) {
            kmlWriter.writeTag("gx:outerColor", ColorHelper.getColorHexAlphabetical(style.getOutlineColor()));
        } 

        kmlWriter.closeTag("LineStyle");
        kmlWriter.closeTag("Style");  
    }  
    
    public static void exportMap(XmlOutput kmlWriter, DigitalMap mapData) {
        double   mapLatitude, mapLongitude, mapZoomLevel;

        try {
            mapLatitude  = mapData.getLastMapView().getMapProjection().getReferenceLatitude()  ;
            mapLongitude = mapData.getLastMapView().getMapProjection().getReferenceLongitude() ;
            mapZoomLevel = mapData.getLastMapView().getZoomLevel();

            kmlWriter.writeTextLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            kmlWriter.openTag("kml xmlns=\"http://www.opengis.net/kml/2.2\"");
            kmlWriter.openTag("Document");

            kmlWriter.writeTag("name", mapData.getName());
            kmlWriter.writeTag("open", "1");

            kmlWriter.openTag ("LookAt");
            kmlWriter.writeTag("longitude",  Double.toString(mapLongitude));
            kmlWriter.writeTag("latitude",   Double.toString(mapLatitude));            
            kmlWriter.closeTag("LookAt");

            //export theme
            exportTheme(kmlWriter, mapData.getTheme());
            
            //layer objects
            for (Layer currentLayer: mapData.getLayers()) {
                if (currentLayer instanceof VectorLayer) {
                    exportVectorLayer(kmlWriter, (VectorLayer) currentLayer);
                } else {
                    currentLayer.toXML(kmlWriter);
                }
            }

            kmlWriter.closeTag("Document");
            kmlWriter.closeTag("kml");
        } catch (Exception e) {
            System.err.println("Error in KmlExporter.exportMap(KmlWritter, MapData) - " + e);
        }
    }        
    
    /**
     * Writes KML for a given VectorObject.
     * 
     * @param kmlWriter
     * @param object 
     */
    public static void exportMapObject(XmlOutput kmlWriter, VectorObject object) {
        try {
            if (object instanceof LinearRing) {
                exportLinearRing(kmlWriter, (LinearRing) object);                
            } else if (object instanceof LineString) {
                exportLineString(kmlWriter, (LineString) object);
            } else if (object instanceof MapPoint) {
                exportMapPoint(kmlWriter, (MapPoint) object);
            } else if (object instanceof MultiGeometry) {
                getMultiGeometryXML(kmlWriter, (MultiGeometry) object);
            } else if (object instanceof Polygon) {
                exportPolygon(kmlWriter, (Polygon) object);
            } else {
                object.toXML(kmlWriter);
            }
        } catch (Exception e) {
            System.err.println("Error in KmlExport.getMapObjectXML(KmlOutput, MapObject) - " + e);
        }
    }    
    
    /**
     * Writes a MapPoint to KML
     * 
     * @param kmlWriter
     * @param point 
     */
    public static void exportMapPoint(XmlOutput kmlWriter, MapPoint point) {
        kmlWriter.openTag ("Placemark");

        kmlWriter.writeTag("name", point.getName());
        writeCustomDataFieldsAsXML(kmlWriter, point);
        
        if (point.hasDisplayableText(point.getDescription()) && !point.getDescription().equalsIgnoreCase("null"))
            kmlWriter.writeTag("description", "<![CDATA[" + point.getDescription() + "]]>");

        kmlWriter.writeTag("styleUrl", point.getObjectClass());

        if (point.getVisibility() != null) {
            LatLonAltBox bounds = point.getBoundingBox();
            float max = Region.calculateLodFromTileZoom(point.getVisibility().getMaxTileZoomLevel(), bounds);
            float min = Region.calculateLodFromTileZoom(point.getVisibility().getMinTileZoomLevel(), bounds);

            LevelOfDetail lod = new LevelOfDetail(max, min);
            Region        r   = new Region(point.getName() + "-region", bounds, lod);

            r.toXML(kmlWriter);
        }        

        kmlWriter.openTag ("Point");
        kmlWriter.writeTag("coordinates",  getCoordinateString(point.getCoordinateList()));

        kmlWriter.closeTag("Point");
        kmlWriter.closeTag("Placemark");
    }      
    
    /**
     * Outputs a PolygonStyle as KML.
     * 
     * @param kmlWriter 
     */
    public static void exportPolyStyle(XmlOutput kmlWriter, PolygonStyle style) {
        kmlWriter.openTag ("Style id=\"" + style.getID() + "\"");
        kmlWriter.openTag ("PolyStyle");

        kmlWriter.writeTag("color", ColorHelper.getColorHexAlphabetical(style.getFillColor()));  

        if (style.getColorMode() == ColorStyle.NORMAL) {
            kmlWriter.writeTag("colorMode", "normal");
        } else if (style.getColorMode() == ColorStyle.RANDOM) {
            kmlWriter.writeTag("colorMode", "random");
        }

        if (style.isFilled()) {
            kmlWriter.writeTag("fill", "1");
        } else {
            kmlWriter.writeTag("fill", "0");
        }

        if (style.isOutlined()) {
            kmlWriter.writeTag("outline", "1");
            //No polygon outling color suported in KML
            //kmlWriter.writeTag("gx:outlineColor", ColorHelper.getColorHexAlphabetical(style.getOutlineColor()));
        } else {
            kmlWriter.writeTag("outline", "0");
        }

        kmlWriter.closeTag("PolyStyle");
        kmlWriter.closeTag("Style");
    }       
    
    public static void exportVectorLayer(XmlOutput kmlWriter, VectorLayer layer) {
        try {
            kmlWriter.openTag ("Folder");

            kmlWriter.writeTag("name", layer.getName());

            if (layer.getDescription() != null) {                
                if (!layer.getDescription() .equals("") && !layer.getDescription() .equalsIgnoreCase("null"))
                    kmlWriter.writeTag("description", layer.getDescription());
            }

            if (layer.hasTimeSpan()) {
                kmlWriter.openTag ("TimeSpan");
                    kmlWriter.writeTag("begin", layer.getTimeSpanBeginString());
                    kmlWriter.writeTag("end",   layer.getTimeSpanEndString());
                kmlWriter.closeTag("TimeSpan");

            }

            //get xml for each object
            for (int i = 0; i < layer.getObjectList().size(); i++) {
                VectorObject object = layer.getObjectList().get(i);                
                exportMapObject(kmlWriter, object);    
            }

            kmlWriter.closeTag("Folder");
        } catch (Exception e) {
            System.err.println("Error in KmlExporter.exportVectorLayer(KmlWriter, VectorLayer) - " + e);
        }
    }    
    
    /**
     * Returns a Coordinate String in the KML format.
     * 
     * @param coordinateString
     * @return 
     */
    public static String getCoordinateString(CoordinateList<Coordinate> coordinateString) {
        String          cString;
        StringBuilder   coord;

        coord = new StringBuilder();
        
        try {
            for (Coordinate currentCoordinate: coordinateString) {
                coord.append(currentCoordinate.getLongitude());
                coord.append(",");
                coord.append(currentCoordinate.getLatitude());
                coord.append(",");
                coord.append(currentCoordinate.getAltitude());
                coord.append(" ");
            }
        } catch (Exception e) {
            System.err.println("Error in CoordinateList.getCoordinateString() - " + e);
        }

        cString = coord.toString();
        
        //remove last space
        return cString.substring(0, coord.length() - 1);
    }                 
                 
    /**
     * Writes MultiGeometry as KML
     * 
     * @param kmlWriter
     * @param mg 
     */
    public static void getMultiGeometryXML(XmlOutput kmlWriter, MultiGeometry mg) {
        try {
            kmlWriter.openTag ("Placemark");

            kmlWriter.writeTag("name", mg.getName());
            writeCustomDataFieldsAsXML(kmlWriter, mg);
            
            if (MultiGeometry.hasDisplayableText(mg.getDescription()) && !mg.getDescription().equalsIgnoreCase("null"))
                kmlWriter.writeTag("description", "<![CDATA[" + mg.getDescription() + "]]>");

            kmlWriter.openTag ("MultiGeometry id=\"" + mg.getName() + "\"");

            for (int i = 0; i < mg.getComponentObjects().size(); i++) {
                VectorObject currentMapObject = mg.getComponentObjects().get(i);
                currentMapObject.toXML(kmlWriter);
            }

            writeCustomDataFieldsAsXML(kmlWriter, mg);  
            
            kmlWriter.closeTag("MultiGeometry");
            kmlWriter.closeTag("Placemark");
        } catch (Exception e) {
            System.err.println("Error in MultiGeometry.toXML(KmlWriter) Object: " + mg.getName() + " - " + e);
        }
    }    
    
    /**
     * Writes Polygon as KML.
     * 
     * @param kmlWriter
     * @param poly 
     */
    public static void exportPolygon(XmlOutput kmlWriter, Polygon poly) {
        try {
            kmlWriter.openTag ("Placemark");

            kmlWriter.writeTag("name", poly.getName());

            if (poly.hasDisplayableText(poly.getDescription()) && !poly.getDescription().equalsIgnoreCase("null"))
                kmlWriter.writeTag("description", "<![CDATA[" + poly.getDescription() + "]]>");

            kmlWriter.writeTag("styleUrl", "#" + poly.getObjectClass());

            if (poly.getVisibility() != null) {
                LatLonAltBox bounds = poly.getBoundingBox();
                float max = Region.calculateLodFromTileZoom(poly.getVisibility().getMaxTileZoomLevel(), bounds);
                float min = Region.calculateLodFromTileZoom(poly.getVisibility().getMinTileZoomLevel(), bounds);

                LevelOfDetail lod = new LevelOfDetail(max, min);
                Region        r   = new Region(poly.getName() + "-region", bounds, lod);

                r.toXML(kmlWriter);
            }              

            writeCustomDataFieldsAsXML(kmlWriter, poly);            
            
            kmlWriter.openTag ("Polygon");

            kmlWriter.writeTag("extrude",     "0");
            kmlWriter.writeTag("tessellate",  "0");
            kmlWriter.writeTag("altitudeMode", "clampToGround");

            kmlWriter.openTag ("outerBoundaryIs");
            kmlWriter.openTag ("LinearRing");
            kmlWriter.writeTag("coordinates",  getCoordinateString(poly.getCoordinateList()));
            kmlWriter.closeTag("LinearRing");
            kmlWriter.closeTag("outerBoundaryIs");

            for (InnerBoundary ib: poly.getInnerBoundaries())
                ib.toXML(kmlWriter);                             

            kmlWriter.closeTag("Polygon");
            kmlWriter.closeTag("Placemark");
        } catch (Exception e) {
            System.err.println("Error in KmlExporter.exportPolygon(KmlWriter, Polygon) Object: " + poly.getName() + " - " + e);
        }
    }    
    
    /**
     * Writes out KML for a given MapTheme
     * 
     * @param kmlWriter
     * @param theme 
     */
    public static void exportTheme(XmlOutput kmlWriter, MapTheme theme) {
        ArrayList<IconStyle>    iconStylesVector;
        ArrayList<LineStyle>    lineStylesVector;
        ArrayList<PolygonStyle> polygonStylesVector;
        ArrayList<StyleMap>     styleMapsVector;

        try {
            iconStylesVector    = new ArrayList<IconStyle>    (theme.getAllIconStyles());
            lineStylesVector    = new ArrayList<LineStyle>    (theme.getAllLineStyles());
            polygonStylesVector = new ArrayList<PolygonStyle> (theme.getAllPolygonStyles());
            styleMapsVector     = new ArrayList<StyleMap>     (theme.getAllStyleMaps());

            for (IconStyle currentIconStyle: iconStylesVector)
                exportIconStyle(kmlWriter, currentIconStyle);

            for (LineStyle currentStyle: lineStylesVector)
                exportLineStyle(kmlWriter, currentStyle);

            for (PolygonStyle currentStyle: polygonStylesVector)
                exportPolyStyle(kmlWriter, currentStyle);

            for (StyleMap currentStyle: styleMapsVector)
                currentStyle.toXML(kmlWriter);
        } catch (Exception e) {
            System.err.println("Error in KmlExporter.exportTheme(KmlOutput, MapTheme) - " + e);
        }        
    }
    
    /**
     * Writes all of the custom data fields as XML to the KMLWriter
     *
     * @param kmlWriter The writer to write CML to.
     */
    public static void writeCustomDataFieldsAsXML(XmlOutput kmlBuffer, VectorObject object) {
        Set             set = object.getCustomDataFields().entrySet();
        Iterator        it  = set.iterator();

        if (set.size() > 0) {        
            kmlBuffer.openTag("ExtendedData");
        
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                kmlBuffer.openTag ("Data name=\"" + entry.getKey() + "\"");
                kmlBuffer.writeTag("value", (String) entry.getValue());            
                kmlBuffer.closeTag("Data");
            }

            kmlBuffer.closeTag("ExtendedData");
        }
    }      
}
