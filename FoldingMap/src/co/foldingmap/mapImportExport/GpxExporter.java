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

import co.foldingmap.map.vector.LatLonAltBox;
import co.foldingmap.map.vector.CoordinateList;
import co.foldingmap.map.vector.VectorObjectList;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.MapPoint;
import co.foldingmap.map.vector.LineString;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.MapUtilities;
import co.foldingmap.xml.XmlWriter;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.SimpleTimeZone;

/**
 *
 * @author Alec
 */
public class GpxExporter {
    
    /**
     * Returns this coordinates timestamp.
     * This function is used rather than the one in Coordinate because stupid
     * Garmin Basecamp will not except timestamp with hours 1-24.
     * 
     * @param c The coordinate to get the timestamp from.
     * @return  The timestamp in yyyy-mm-ddThh:mm:ssZ format
     */
    public static String getTimestamp(Coordinate c) {             
        SimpleDateFormat timestampDateFormat;
                
        timestampDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        timestampDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
                
        return timestampDateFormat.format(c.getDate())  + "Z";       
    }    
    
    public static void export(DigitalMap mapData, File fileOut) {
        float                    maxlat, maxlon, minlat, minlon;
        XmlWriter                writer;
        LatLonAltBox             bounds;
        VectorObjectList<VectorObject> lines, objects, points;
        StringBuilder            gpxTag, sb;
        
        writer = new XmlWriter(fileOut);
        gpxTag = new StringBuilder();
        sb     = new StringBuilder();
        
        //get map bounds info
        bounds = mapData.getBoundary();
        maxlat = bounds.getNorth();
        maxlon = bounds.getEast();
        minlat = bounds.getSouth();
        minlon = bounds.getWest();
        
        sb.append("\t\t");
        sb.append("<bounds maxlat=\"");
        sb.append(maxlat);
        sb.append("\" maxlon=\"");
        sb.append(maxlon);
        sb.append("\" minlat=\"");
        sb.append(minlat);
        sb.append("\" minlon=\"");
        sb.append(minlon);
        sb.append("\"></bounds>");
        sb.append("\n");        
        
        writer.writeText("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
        writer.writeText("<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" creator=\"Folding Map\" version=\"1.1\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">\n");
        writer.increaseIndent();                        
        
        //write meta data
        writer.openTag("metadata");
        writer.openTag("link href=\"http://foldingmap.co\"");
        writer.writeTag("text", "Folding Map");
        writer.closeTag("link");
        writer.writeTag("time", MapUtilities.getCurrentTimestamp());
        writer.writeText(sb.toString());
        writer.closeTag("metadata");
        
        //export Objects
        objects = new VectorObjectList<VectorObject>(mapData.getAllMapObjects());               
        points  = objects.getMapPoints();
        lines   = objects.getLineStrings();
        
        //export MapPoints
        for (VectorObject obj: points) 
            exportMapPoint(writer, (MapPoint) obj);        
        
        //export LineStrings
        for (VectorObject obj: lines) 
            exportLineString(writer, (LineString) obj);             
        
        writer.decreaseIndent();
        writer.writeText("</gpx>");
        writer.closeFile();
    }
    
    /**
     * Exports LineStrings to a GPX track
     * 
     * @param writer    The XML writer
     * @param line      The LineString to export.
     */
    public static void exportLineString(XmlWriter writer, LineString line) {
        CoordinateList<Coordinate>  cList;
        StringBuilder               sb;
        
        try {
            cList = line.getCoordinateList();

            writer.openTag("trk");
            writer.writeTag("name", line.getDescription());
            writer.openTag("trkseg");

            for (Coordinate c: cList) {
                sb = new StringBuilder();

                sb.append("trkpt lat=\"");
                sb.append(c.getLatitude());
                sb.append("\" lon=\"");
                sb.append(c.getLongitude());
                sb.append("\"");    

                writer.openTag(sb.toString());
                writer.writeTag("ele",  Float.toString(c.getAltitude()));
                writer.writeTag("time", getTimestamp(c));
                writer.closeTag("trkpt");
            }

            writer.closeTag("trkseg");
            writer.closeTag("trk");
        } catch (Exception e) {
            System.err.println("Error in GpxExporter.exportLineString(KmlWriter, LineString) - " + e);
        }
    }    
    
    public static void exportMapPoint(XmlWriter writer, MapPoint point) {

        Coordinate          c         = point.getCoordinateList().get(0);
        StringBuilder       sb        = new StringBuilder();
        GpxGarminExtensions garminExt = new GpxGarminExtensions();
        
        sb.append("wpt lat=\"");
        sb.append(c.getLatitude());
        sb.append("\" lon=\"");
        sb.append(c.getLongitude());
        sb.append("\"");
        
        writer.openTag(sb.toString());
        writer.writeTag("ele",  Float.toString(c.getAltitude()));
        writer.writeTag("time", getTimestamp(c));
        writer.writeTag("name", point.getName());
        writer.writeTag("cmt",  point.getDescription());
        writer.writeTag("desc", point.getDescription());
        writer.writeTag("sym",  garminExt.getSymbolFromClass(point.getObjectClass()));        
        writer.closeTag("wpt");
    }
    

}
