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
import co.foldingmap.GUISupport.ProgressIndicator;
import co.foldingmap.GUISupport.Updateable;
import co.foldingmap.Logger;
import co.foldingmap.ResourceHelper;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.MapAccepter;
import co.foldingmap.map.MercatorProjection;
import co.foldingmap.map.themes.Web;
import co.foldingmap.map.vector.NodeMap;
import co.foldingmap.map.vector.VectorLayer;
import java.io.File;
import java.io.IOException;

/**
 * A threaded map loader.
 * 
 * @author Alec
 */
public class MapImporter extends Thread {
    private static final int LOAD_LAYER = 1;
    private static final int LOAD_Map   = 2;
    
    private DigitalMap        mapData;
    private File              mapFile;
    private FormatImporter    formatImporter;
    private int               loadType;
    private MapAccepter       mapAccepter;
    private NodeMap           nodeMap;
    private ProgressIndicator progressIndicator;
    private VectorLayer       layer;    
    
    /**
     * Constructor for Loading to a Layer.
     * 
     * @param formatImporter
     * @param mapFile
     * @param nodeMap
     * @param layer
     * @param progressIndicator 
     */
    public MapImporter(FormatImporter    formatImporter, 
                       File              mapFile, 
                       NodeMap           nodeMap, 
                       VectorLayer       layer, 
                       ProgressIndicator progressIndicator) {
        
        this.loadType          = LOAD_LAYER;
        this.formatImporter    = formatImporter;
        this.mapFile           = mapFile;
        this.nodeMap           = nodeMap;
        this.layer             = layer;
        this.progressIndicator = progressIndicator;        
    }
    
    /**
     * Constructor for Loading to a MapPanel.
     * 
     * @param formatImporter
     * @param mapFile
     * @param mapAccepter
     * @param progressIndicator 
     */
    public MapImporter(FormatImporter    formatImporter, 
                       File              mapFile, 
                       MapAccepter       mapAccepter, 
                       ProgressIndicator progressIndicator) {   
        
        this.loadType          = LOAD_Map;
        this.formatImporter    = formatImporter;
        this.mapFile           = mapFile;
        this.mapAccepter       = mapAccepter;
        this.progressIndicator = progressIndicator;         
    } 
    
    /**
     * Static method to open a map file.  The opened map is set to the given MapAccepter.
     * 
     * @param mapFile
     * @param mapAccepter
     * @param updateable
     * @param progressIndicator
     * @throws IOException 
     */
    public static void importMap(File              mapFile, 
                                  MapAccepter       mapAccepter, 
                                  Updateable        updateable,
                                  ProgressIndicator progressIndicator) throws IOException {
                           
        int extentionStart = mapFile.getName().lastIndexOf(".") + 1;                
                    
        //Add path to ResourceHelper
        ResourceHelper.addFilePath(mapFile.getParent());        
        
        if (extentionStart > 0) {
            String extention = mapFile.getName().substring(extentionStart);
        
            if (extention.equalsIgnoreCase("fmxml")) {
                MapImporter importer = new MapImporter(new FmXmlImporter(), mapFile, mapAccepter, progressIndicator);
                importer.start();               
            } else if (extention.equalsIgnoreCase("gpx")) {
                DigitalMap newMap = new DigitalMap();
                mapAccepter.setMap(newMap);
                GpxImporter gpxImporter;                    
                gpxImporter = new GpxImporter(newMap, mapFile, progressIndicator);                    
                gpxImporter.start();                 
            } else if (extention.equalsIgnoreCase("kml")) {
                KmlImport kmlImporter = new KmlImport();
                DigitalMap newMap = kmlImporter.importAsMap(mapFile, progressIndicator); 
                mapAccepter.setMap(newMap);
            } else if (extention.equalsIgnoreCase("kmz")) {    
                DigitalMap newMap = KmlImport.openKMZ(progressIndicator, mapFile);
                mapAccepter.setMap(newMap);
            } else if (extention.equalsIgnoreCase("mbtiles")) {
                MapImporter importer = new MapImporter(new MbTilesImporter(), mapFile, mapAccepter, progressIndicator);
                importer.start();                    
            } else if (extention.equalsIgnoreCase("osm")) {
                OsmImporter osmImporter;

                DigitalMap newMap = new DigitalMap("Open Street Map", new MercatorProjection());
                newMap.setTheme(new Web(), updateable, progressIndicator);

                osmImporter = new OsmImporter(newMap, mapFile, updateable, progressIndicator);
                osmImporter.start();                 
            }
            
            if (updateable != null)
                updateable.update();
        } else {
            //try to figure out the file type.
            //TODO: write code to try and determin map format.
        }
    }        
    
    @Override
    public void run() {
        try {            
            if (loadType == LOAD_LAYER) {
                formatImporter.importToLayer(mapFile, nodeMap, layer, (ProgressBarPanel) progressIndicator);
            } else if (loadType == LOAD_Map) {
                mapData = formatImporter.importAsMap(mapFile, (ProgressBarPanel) progressIndicator);
                mapAccepter.setMap(mapData);
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in MapImporter.run() - " + e);
        }
    }        
}
