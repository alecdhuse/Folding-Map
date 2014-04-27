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
import co.foldingmap.map.vector.CoordinateList;
import co.foldingmap.map.vector.VectorObjectList;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.MapPoint;
import co.foldingmap.map.vector.LineString;
import co.foldingmap.GUISupport.ProgressIndicator;
import co.foldingmap.map.DigitalMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * This class is used to import GPX files.  
 * These files are a common way of exchanging GPS data between programs.
 * Currently only supports GPX 1.0.
 * 
 * See http://www.topografix.com/gpx.asp for more information.
 * 
 * @author Alec
 */
public class GpxImporter extends Thread {
    
    private DigitalMap        mapData;
    private String            xml;
    private ProgressIndicator progressIndicator;        
    
    public GpxImporter(DigitalMap mapData, File gpxFile, ProgressIndicator progressIndicator) {
        this.mapData            = mapData;
        this.progressIndicator  = progressIndicator;
        this.xml                = readGpxFile(gpxFile);
    }        
    
    /**
     * Returns a coordinate representing a single trkpt tag in a trkseg parent
     * tag.
     * 
     * Tags will have the form:            
     * 
     *      <trkpt lat="4.765787534415722" lon="11.227527866140008">
     *          <ele>482.71923828125</ele>
     *      </trkpt>
     * 
     * @param xml
     * @return 
     */
    public static Coordinate getTrackSegment(String xml) {
        Coordinate          segmentCoordinate;
        float               altitude, latitude, longitude;
        int                 offset, xmlLength;
        int                 propertyStart, propertyEnd, trackpointTagStart, trackpointTagEnd;
        String              propertyText, time, waypointXML;

        offset              = 0;
        segmentCoordinate   = Coordinate.UNKNOWN_COORDINATE;
        xmlLength           = xml.length();

        altitude           = 0;
        latitude           = 0;
        longitude          = 0;
        time               = "";

        try {
            trackpointTagStart = xml.indexOf("<trkpt",   offset);
            trackpointTagEnd   = xml.indexOf("</trkpt>", trackpointTagStart);

            if ((trackpointTagStart >=0) && (trackpointTagStart < trackpointTagEnd)) {
                waypointXML  = xml.substring(trackpointTagStart, trackpointTagEnd);

                propertyStart = waypointXML.indexOf("lat=\"") + 5;
                propertyEnd   = waypointXML.indexOf("\"", propertyStart);
                if ((propertyStart >= 0) && (propertyStart < propertyEnd)) {
                    propertyText  = waypointXML.substring(propertyStart, propertyEnd);
                    latitude      = Float.parseFloat(propertyText);
                }

                propertyStart = waypointXML.indexOf("lon=\"") + 5;
                propertyEnd   = waypointXML.indexOf("\"", propertyStart);
                if ((propertyStart >= 0) && (propertyStart < propertyEnd)) {
                    propertyText  = waypointXML.substring(propertyStart, propertyEnd);
                    longitude     = Float.parseFloat(propertyText);
                }

                propertyStart = waypointXML.indexOf("<ele>") + 5;
                propertyEnd   = waypointXML.indexOf("</ele>", propertyStart);
                if ((propertyStart > 0) && (propertyStart < propertyEnd)) {
                    propertyText = waypointXML.substring(propertyStart, propertyEnd);
                    altitude     = Float.parseFloat(propertyText);
                }

                propertyStart = waypointXML.indexOf("<time>") + 6;
                propertyEnd   = waypointXML.indexOf("</time>", propertyStart);
                if ((propertyStart > 0) && (propertyStart < propertyEnd)) {
                    propertyText  = waypointXML.substring(propertyStart, propertyEnd);
                    time      = propertyText;
                }

                segmentCoordinate = new Coordinate(altitude, latitude, longitude, time);
            }
        } catch (Exception e) {
            System.err.println("Error on GpxImport.getTrackSegment(String) - " + e);
        }

        return segmentCoordinate;        
    }
    
    /**
     * Returns all the Coordinates in a trkseg tag as a CoordinateList.
     * 
     * @param xml
     * @return 
     */
    public static CoordinateList<Coordinate> getTrackSegments(String xml) {
        CoordinateList<Coordinate>  coordinates;
        Coordinate                  currentTrackPoint;
        int                         offset, xmlLength;
        int                         trackPointStart, trackPointEnd;
        String                      trackPointXML;

        coordinates = new CoordinateList<Coordinate>();
        offset      = 0;
        xmlLength   = xml.length();
        
        try {
            while (offset < xmlLength) {
                trackPointStart = xml.indexOf("<trkpt",   offset);
                trackPointEnd   = xml.indexOf("</trkpt>", trackPointStart) + 8;

                if ((trackPointStart >= 0) && (trackPointStart < trackPointEnd)) {
                    trackPointXML     = xml.substring(trackPointStart, trackPointEnd);
                    currentTrackPoint = getTrackSegment(trackPointXML);
                    offset            = trackPointEnd;

                    if (!currentTrackPoint.equals(Coordinate.UNKNOWN_COORDINATE))
                        coordinates.add(currentTrackPoint);
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error in GpxImporter.getTrackSegment(String) - " + e);
        }

        return coordinates ;        
    }
    
    private void importGpx() {
        VectorObjectList<VectorObject> waypoints, tracks;
        VectorLayer              importLayer;
        
        progressIndicator.updateProgress("Loading Waypoints", 1);
        waypoints = parseWaypoints(xml, progressIndicator);

        progressIndicator.updateProgress("Loading Tracks", 50);
        tracks    = parseTracks(xml);

        importLayer = new VectorLayer("GPX Import");
        importLayer.setParentMap(mapData);
        importLayer.addAllObjects(tracks);
        importLayer.addAllObjects(waypoints);        
        mapData.addLayer(importLayer);
        
        progressIndicator.updateProgress("Finishing Import", 99);
        progressIndicator.finish();        
    }
    
    /**
     * Parses the WayPoints from the GPX file and returns a list of MapPoints.
     * 
     * @param xml
     * @param progressPanel
     *          ProgressPanel to update parse progress, can be null.
     * @return 
     */
    public static VectorObjectList<VectorObject> parseWaypoints(String xml, ProgressIndicator progressIndicator) {
        Coordinate                  newCoordinate;
        float                       altitude, latitude, longitude, percentDone;
        int                         offset, xmlLength;
        int                         propertyStart, propertyEnd, waypointTagStart, waypointTagEnd;
        VectorObjectList<VectorObject>    waypoints;
        MapPoint                    newPoint;
        String                      description, name, propertyText, objectClass;
        String                      symbol, time, waypointXML;
        
        offset    = 0;
        waypoints = new VectorObjectList<VectorObject>();
        xmlLength = xml.length();

        try {
            while (offset < xmlLength) {
                altitude         = 0;
                description      = "";
                latitude         = 0;
                longitude        = 0;
                name             = "New WayPoint";
                objectClass      = "(Unspecified Point)";
                symbol           = "";
                time             = null;
                percentDone      = (((float) offset) / ((float) xmlLength)) * 100;
                waypointTagStart = xml.indexOf("<wpt",   offset);
                waypointTagEnd   = xml.indexOf("</wpt>", waypointTagStart);

                if (percentDone > 0 && progressIndicator != null)
                    progressIndicator.updateProgress("Loading GPX Waypoints", (int) (percentDone - 51));

                if ((waypointTagStart >= 0) && (waypointTagStart < waypointTagEnd)) {
                    waypointXML  = xml.substring(waypointTagStart, waypointTagEnd);

                    propertyStart = waypointXML.indexOf("lat=\"") + 5;
                    propertyEnd   = waypointXML.indexOf("\"", propertyStart);
                    if ((propertyStart >= 0) && (propertyStart < propertyEnd)) {
                        propertyText  = waypointXML.substring(propertyStart, propertyEnd);
                        latitude      = Float.parseFloat(propertyText);
                        offset        = propertyEnd + 1;
                    }

                    propertyStart = waypointXML.indexOf("lon=\"") + 5;
                    propertyEnd   = waypointXML.indexOf("\"", propertyStart);
                    if ((propertyStart >= 0) && (propertyStart < propertyEnd)) {
                        propertyText  = waypointXML.substring(propertyStart, propertyEnd);
                        longitude     = Float.parseFloat(propertyText);
                        offset        = propertyEnd + 1;
                    }

                    propertyStart = waypointXML.indexOf("<ele>") + 5;
                    propertyEnd   = waypointXML.indexOf("</ele>", propertyStart);
                    if ((propertyStart >= 0) && (propertyStart < propertyEnd)) {
                        propertyText = waypointXML.substring(propertyStart, propertyEnd);
                        altitude     = Float.parseFloat(propertyText);
                        offset       = propertyEnd + 6;
                    }

                    propertyStart = waypointXML.indexOf("<name>") + 6;
                    propertyEnd   = waypointXML.indexOf("</name>", propertyStart);
                    if ((propertyStart >= 0) && (propertyStart < propertyEnd)) {
                        propertyText  = waypointXML.substring(propertyStart, propertyEnd);
                        name          = propertyText;
                        offset        = propertyEnd + 7;
                    }

                    propertyStart = waypointXML.indexOf("<desc>") + 6;
                    propertyEnd   = waypointXML.indexOf("</desc>", propertyStart);
                    if ((propertyStart >= 0) && (propertyStart < propertyEnd)) {
                        propertyText  = waypointXML.substring(propertyStart, propertyEnd);
                        description   = propertyText;
                        offset        = propertyEnd + 7;
                    }

                    propertyStart = waypointXML.indexOf("<time>") + 6;
                    propertyEnd   = waypointXML.indexOf("</time>", propertyStart);
                    if ((propertyStart >= 0) && (propertyStart < propertyEnd)) {
                        propertyText  = waypointXML.substring(propertyStart, propertyEnd);
                        time          = propertyText;
                        offset        = propertyEnd + 7;
                    }                    
                    
                    propertyStart = waypointXML.indexOf("<sym>") + 5;
                    propertyEnd   = waypointXML.indexOf("</sym>", propertyStart);
                    if ((propertyStart >= 0) && (propertyStart < propertyEnd)) {
                        propertyText  = waypointXML.substring(propertyStart, propertyEnd);
                        symbol        = propertyText; 
                        offset        = propertyEnd + 7;
                    }                     
                    
                    GpxGarminExtensions garminExt = new GpxGarminExtensions();
                    objectClass = garminExt.getClassFromSymbol(symbol);
                                        
                    if (time != null) {
                        newCoordinate = new Coordinate(altitude, latitude, longitude, time);
                    } else {
                        newCoordinate = new Coordinate(altitude, latitude, longitude);
                    }
                    
                    newPoint = new MapPoint(name, objectClass, description, newCoordinate);
                    offset   = waypointTagEnd + 6;

                    waypoints.add(newPoint);
                } else {
                    break;
                }
            } //end while loop
        } catch (Exception e) {
            System.err.println("Error in GpxImporter.parseWaypoints(String) offset: " + offset + " Error: " + e);
        }

        return waypoints;        
    }
    
    public static VectorObjectList<VectorObject> parseTracks(String xml) {
        CoordinateList<Coordinate>  coordinates;
        Coordinate                  trackSegmentCoordinate;
        int                         offset, xmlLength;
        int                         propertyStart, propertyEnd, trackTagStart, trackTagEnd;
        int                         trackSegmentTagStart, trackSegmentTagEnd;
        LineString                  newLineString;
        VectorObjectList<VectorObject>    tracks;
        String                      name, propertyText, trackSegmentXML, trackXML;

        name        = "New Line";
        offset      = 0;
        tracks      = new VectorObjectList<VectorObject>();
        xmlLength   = xml.length();

        try {
            while (offset < xmlLength) {
                trackTagStart = xml.indexOf("<trk>",   offset);
                trackTagEnd   = xml.indexOf("</trk>", trackTagStart);

                if ((trackTagStart >= 0) && (trackTagStart < trackTagEnd)) {
                    trackXML      = xml.substring(trackTagStart, trackTagEnd);

                    propertyStart = trackXML.indexOf("<name>") + 6;
                    propertyEnd   = trackXML.indexOf("</name>", propertyStart);
                    if ((propertyStart > 0) && (propertyStart < propertyEnd)) {
                        propertyText  = trackXML.substring(propertyStart, propertyEnd);
                        name          = propertyText;
                        offset        = propertyEnd + 7;
                    }

                    while (offset < xmlLength) {
                        trackSegmentTagStart = xml.indexOf("<trkseg>",  offset);
                        trackSegmentTagEnd   = xml.indexOf("</trkseg>", trackSegmentTagStart);


                        if ((trackSegmentTagStart >= 0) && (trackSegmentTagStart < trackSegmentTagEnd)) {
                            offset                = trackSegmentTagEnd + 9;
                            trackSegmentTagStart += 8;
                            trackSegmentXML       = xml.substring(trackSegmentTagStart, trackSegmentTagEnd);
                            coordinates           = getTrackSegments(trackSegmentXML);

                            if (coordinates.size() > 0) {
                                newLineString = new LineString(name, "(Unspecified Linestring)", coordinates);
                                tracks.add(newLineString);
                            }
                        } else {
                            break;
                        }
                    }
                } else {
                    break;
                }
            } //end while loop
        } catch (Exception e) {
            System.out.println("Error in GpxImporter.parseTracks(String) - " + e);
        }
        
        return tracks;        
    }
    
    private static String readGpxFile(File gpxFile) {
        BufferedReader  br;
        StringBuilder   sb = new StringBuilder();
        
        try {
            br = new BufferedReader(new FileReader(gpxFile));
            
            while (br.ready()) 
                sb.append(br.readLine().trim());
            
        } catch (Exception e) {
            System.err.println("Error in GpxImporter.readGpxFile(File) - " + e);
        }
        
        return sb.toString();
    }
    
    @Override
    public void run() {
        importGpx();
    }    
}
