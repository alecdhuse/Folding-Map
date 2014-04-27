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
import co.foldingmap.mapImportExport.JsonImporter;
import co.foldingmap.mapImportExport.CsvImporter;
import co.foldingmap.GUISupport.FileExtensionFilter;
import co.foldingmap.GUISupport.ProgressBarPanel;
import co.foldingmap.Logger;
import co.foldingmap.MainWindow;
import co.foldingmap.UserConfig;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.Layer;
import co.foldingmap.map.themes.ColorStyle;
import co.foldingmap.map.tile.MbTileSource;
import co.foldingmap.map.tile.TileLayer;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.NodeMap;
import java.awt.FileDialog;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Alec
 */
public class ImportMap extends Action {
    private ArrayList<Layer> newLayers;
    private DigitalMap       mapData;
    private MainWindow       mainWindow;
        
    public ImportMap(MainWindow mainWindow, DigitalMap mapData) {
        this.mainWindow = mainWindow;
        this.mapData    = mapData;
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
        DigitalMap               importedMap;
        FileDialog               fileDialog;
        FileExtensionFilter      fileExtensionFilter;
        ProgressBarPanel         progressPanel;
        String                   fileExtension, fileName;       
        UserConfig               userConfig;
        
        try {    
            userConfig    = mainWindow.getUserConfig();
            progressPanel = mainWindow.getProgressBarPanel();

            fileDialog    = new FileDialog(mainWindow);
            fileDialog.setDirectory(userConfig.getImportDIR());
            
            fileExtensionFilter = new FileExtensionFilter();
            fileExtensionFilter.addExtension("csv");
            fileExtensionFilter.addExtension("kml");
            fileExtensionFilter.addExtension("kmz");
            fileExtensionFilter.addExtension("fmxml");
            fileExtensionFilter.addExtension("geojson");
            fileExtensionFilter.addExtension("gpx");
            fileExtensionFilter.addExtension("mbtiles");
            fileExtensionFilter.addExtension("osm");
            //fileExtensionFilter.addExtension("shp");
            fileDialog.setFilenameFilter(fileExtensionFilter);
            
            fileDialog.setVisible(true);
            fileName = fileDialog.getDirectory() + fileDialog.getFile();                       

            if (!fileName.endsWith("null")) {
                progressPanel.setVisible(true);                
                fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);

                if (fileExtension.equalsIgnoreCase("fmxml")) {
                    DigitalMap openedMap ;
                    
                    openedMap = FmXmlImporter.openFile(new File(fileName), progressPanel);                    
                                                            
                    NodeMap currentNodeMap = mapData.getCoordinateSet();
                    
                    long currentNodeId = currentNodeMap.getKeyFromIndex(currentNodeMap.size() - 1);
                    
                    for (Coordinate c: openedMap.getCoordinateSet().getAllCoordinates()) {
                        if (c != null) {
                            currentNodeId++; 
                            c.setId(currentNodeId);
                            currentNodeMap.put(currentNodeId, c);   
                        }
                    }
                    
                    for (Layer l: openedMap.getLayers())
                        mapData.addLayer(l, 0);
                } else if (fileExtension.equalsIgnoreCase("csv")) {
                    CsvImporter csvImporter;
                    csvImporter = new CsvImporter(new File(fileName), mapData, progressPanel);
                    csvImporter.start();
                } else if (fileExtension.equalsIgnoreCase("geojson")) {
                    JsonImporter.importGeoJSON(new File(fileName), mapData);
                } else if (fileExtension.equalsIgnoreCase("gpx")) {
                    GpxImporter gpxImporter;                    
                    gpxImporter = new GpxImporter(mapData, new File(fileName), progressPanel);                    
                    gpxImporter.start();
                } else if (fileExtension.equalsIgnoreCase("kml")) {
                    importedMap = KmlImport.openKML(progressPanel, new File(fileName), mapData.getCoordinateSet());
                    newLayers   = importedMap.getLayers();
                    
                    for (Layer l: newLayers) 
                        mapData.addLayer(l, 0);
                        
                    //Transfer Styles
                    ArrayList<ColorStyle> styles = importedMap.getTheme().getAllStyles();
                    
                    for (ColorStyle cs: styles) 
                        mapData.getTheme().addStyleElement(cs);
                                        
                    progressPanel.finish();
                } else if (fileExtension.equalsIgnoreCase("kmz")) {   
                    importedMap = KmlImport.openKMZ(progressPanel, new File(fileName));
                    newLayers   = importedMap.getLayers();
                    
                    for (Layer l: newLayers) 
                        mapData.addLayer(l, 0);
                        
                    //Transfer Styles
                    ArrayList<ColorStyle> styles = importedMap.getTheme().getAllStyles();
                    
                    for (ColorStyle cs: styles) 
                        mapData.getTheme().addStyleElement(cs);                    
                    
                    progressPanel.finish();
                } else if (fileExtension.equalsIgnoreCase("mbtiles")) {  
                    MbTileSource       mbTiles   = new MbTileSource(fileName);                    
                    TileLayer          tileLayer = new TileLayer(mbTiles);
                                                             
                    mapData.addLayer(tileLayer, 0);                    
                } else if (fileExtension.equalsIgnoreCase("osm")) {                                        
                    OsmImporter osmImporter = new OsmImporter(mapData, new File(fileName), mainWindow, progressPanel);
                    osmImporter.start();                    
                } else if (fileExtension.equalsIgnoreCase("shp")) {
                    //TODO: Link to Shapefile Importer
                    Logger.log(Logger.ERR, "Error in ImportMap.execute() - Shapefile Import not supported at this time.");
                }

                userConfig.setImportDIR(fileDialog.getDirectory());
                mainWindow.updateLayersTree();
            } //end file dialog ok check
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in ImportMap.execute() - " + e);
        }
        
        mainWindow.update();
    }

    @Override
    public void undo() {
        for (Layer l: newLayers) 
            mapData.removeLayer(l);
    }
    
    
    
}
