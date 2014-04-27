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
package co.foldingmap.actions;

import co.foldingmap.mapImportExport.OsmImporter;
import co.foldingmap.mapImportExport.FmXmlImporter;
import co.foldingmap.mapImportExport.GpxImporter;
import co.foldingmap.mapImportExport.KmlImport;
import co.foldingmap.mapImportExport.MapImporter;
import co.foldingmap.GUISupport.FileExtensionFilter;
import co.foldingmap.GUISupport.ProgressIndicator;
import co.foldingmap.GUISupport.Updateable;
import co.foldingmap.Logger;
import co.foldingmap.MainWindow;
import co.foldingmap.ResourceHelper;
import co.foldingmap.UserConfig;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.MapAccepter;
import co.foldingmap.map.MercatorProjection;
import co.foldingmap.map.themes.Web;
import co.foldingmap.map.tile.MbTileSource;
import co.foldingmap.map.tile.TileLayer;
import co.foldingmap.map.tile.TileMath;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.LatLonAltBox;
import java.awt.FileDialog;
import java.io.File;
import javax.swing.JFrame;

/**
 *
 * @author Alec
 */
public class OpenMap extends Action {
    private Actions     actions;
    private DigitalMap  openedMap;
    private MainWindow  mainWindow;    
        
    public OpenMap(Actions actions, MainWindow mainWindow) {
        this.actions            = actions;
        this.mainWindow         = mainWindow;
        this.commandDescription = "Open Map";
    }
    
    /**
     * Returns if this Action can be undone.
     * 
     * @return 
     */
    @Override
    public boolean canUndo() {
        return false;
    }      
    
    @Override
    public void execute() {
        try {                       
            String  fileName = promptUserForMapFile(mainWindow, mainWindow.getUserConfig());            
            File    mapFile  = new File(fileName);
            
            if (fileName != null) {      
                //Clear out old data
                ResourceHelper.clearResourcePaths();                 

                MapImporter.importMap(mapFile, mainWindow, mainWindow, mainWindow.getProgressBarPanel());
                
                mainWindow.setMap(mainWindow.getMapPanel().getMap());
                mainWindow.updateExtendedOptions();
                mainWindow.repaint();
                mainWindow.getMapPanel().getMap().setActions(actions); 
            }            
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in OpenMap.execute() - " + e);
        }
    }

    public DigitalMap getMap() {
        return openedMap;
    }
    
    public static DigitalMap openMap(String            fileName, 
                                     MapAccepter       mapAccepter, 
                                     Updateable        updateable,
                                     ProgressIndicator progressIndicator) {
        
        DigitalMap  newMap;
        int         extensionStart;
        String      fileExtension;
        
        newMap = new DigitalMap();
        
        try {
            if (fileName != null) {  
                extensionStart = fileName.lastIndexOf(".");
                fileExtension  = fileName.substring(extensionStart + 1);                        
                                                                
                if (progressIndicator != null) progressIndicator.reset();
                System.gc();
                
                if (fileExtension.equalsIgnoreCase("fmxml")) {
                    File mapFile = new File(fileName);
                    MapImporter importer = new MapImporter(new FmXmlImporter(), mapFile, mapAccepter, progressIndicator);
                    importer.start();
                } else if (fileExtension.equalsIgnoreCase("gpx")) {
                    newMap.setMapFile(new File(fileName));
                    GpxImporter gpxImporter;                    
                    gpxImporter = new GpxImporter(newMap, new File(fileName), progressIndicator);                    
                    gpxImporter.start();                     
                } else if (fileExtension.equalsIgnoreCase("kml")) {
                    KmlImport kmlImporter = new KmlImport();
                    newMap = kmlImporter.importAsMap(new File(fileName), progressIndicator);
                } else if (fileExtension.equalsIgnoreCase("kmz")) {
                    newMap = KmlImport.openKMZ(progressIndicator, new File(fileName));
                } else if (fileExtension.equalsIgnoreCase("mbtiles")) {
                    MercatorProjection proj;
                    MbTileSource       mbTiles   = new MbTileSource(fileName);                    
                    TileLayer          tileLayer = new TileLayer(mbTiles);
                    LatLonAltBox       bounds    = mbTiles.getBoundingBox();           
                    float              zoomLevel = TileMath.getVectorMapZoom(mbTiles.getMinZoom());
                    
                    if (bounds != null) {
                        proj = new MercatorProjection(bounds.getNorth(), bounds.getWest(), zoomLevel);
                    } else {
                        proj = new MercatorProjection();
                    }
                    
                    proj.setZoomLevel(TileMath.getVectorMapZoom(2));                    
                    newMap = new DigitalMap(mbTiles.getName(), proj);
                    
                    //Set map description from the layer description.
                    newMap.setMapDescription(tileLayer.getDescription());
                                                           
                    if (bounds.getNorth() >= 90) {
                        newMap.setLookAtCoordinate(new Coordinate(0, 85.0511f, bounds.getWest()));  
                    } else {
                        newMap.setLookAtCoordinate(new Coordinate(0, bounds.getNorth(), bounds.getWest()));  
                    }     
                                                                                      
                    newMap.addLayer(tileLayer);
                    
                } else if (fileExtension.equalsIgnoreCase("osm")) {
                    OsmImporter osmImporter;
                    
                    newMap = new DigitalMap("Open Street Map", new MercatorProjection());
                    newMap.setTheme(new Web(), updateable, progressIndicator);
                    
                    osmImporter = new OsmImporter(newMap, new File(fileName), updateable, progressIndicator);
                    
                    osmImporter.start();                             
                } else if (fileExtension.equalsIgnoreCase("shp")) {  
                   //TODO: add ability to Open Shape files
                } else {
                    newMap = null;
                }                                   
            }    
        
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in OpenMap.openMap(String, MapAccepter, Updateable, ProgressIndicator) - " + e);
        }
        
        return newMap;
    }
    
    /**
     * Opens up a File Dialog to prompt the user to select a map file to open.
     * 
     * @param parentWindow  
     * @param userConfig    User config option, can be null.
     * @return              A string with the path and file name to open.
     */
    public static String promptUserForMapFile(JFrame parentWindow, UserConfig userConfig) {
        FileExtensionFilter     fileExtensionFilter;
        FileDialog              fileDialog;
        String                  fileName;
        
        fileName = null;
        
        try {
            fileDialog          = new FileDialog(parentWindow);
            fileExtensionFilter = new FileExtensionFilter();
            
            if (userConfig != null)
                fileDialog.setDirectory(userConfig.getWorkingDIR());
            
            //Add acceptable file extentions
            fileExtensionFilter.addExtension("fmxml");
            fileExtensionFilter.addExtension("gpx");
            fileExtensionFilter.addExtension("kml");            
            fileExtensionFilter.addExtension("kmz"); 
            fileExtensionFilter.addExtension("mbtiles");
            fileExtensionFilter.addExtension("osm");
            fileExtensionFilter.addExtension("shp");
            fileDialog.setFilenameFilter(fileExtensionFilter);
            
            fileDialog.setVisible(true);
                        
            if (fileDialog.getFile() != null) 
                fileName = fileDialog.getDirectory() + fileDialog.getFile();                       
               
            if (userConfig != null) {
                userConfig.setWorkingDIR(fileDialog.getDirectory());
                userConfig.addRecentFile(fileName);
            }
            
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in OpenMap.promptUserForMapFile()");            
        }
        
        return fileName;
    }
    
    @Override
    public void undo() {
        //Cannot undo
    }
    
}
