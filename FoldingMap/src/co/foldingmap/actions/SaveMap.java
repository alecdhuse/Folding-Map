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

import co.foldingmap.GUISupport.ProgressIndicator;
import co.foldingmap.Logger;
import co.foldingmap.MainWindow;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.mapImportExport.FmXmlExporter;
import co.foldingmap.mapImportExport.KmlExporter;
import co.foldingmap.xml.XmlWriter;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Action for saving the map.
 * 
 * @author Alec
 */
public class SaveMap extends Action {
    private DigitalMap        mapData;
    private ProgressIndicator progressIndicator;
    private MainWindow        mainWindow;
    
    public SaveMap(MainWindow mainWindow, DigitalMap mapData, ProgressIndicator progressIndicator) {
        this.commandDescription = "Save Map";
        this.mainWindow         = mainWindow;
        this.mapData            = mapData;
        this.progressIndicator  = progressIndicator;
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
        File        mapFile;
        XmlWriter   kmlWriter;
        
        progressIndicator.updateProgress("Saving Map", 0);
        
        try {
            mapFile = mapData.getMapFile();

            //file has been saved to a location on the disk already
            if (mapFile != null) {
                if (mapFile.getName().endsWith(".fmxml")) {
                    FmXmlExporter.export(mapData, mapFile);
                } else if (mapFile.getName().endsWith(".gpx")) {
                    //TODO: add code for GXP exporter
                } else if (mapFile.getName().endsWith(".kmz")) {
                    //TODO: Finish writing kmz save
                } else if (mapFile.getName().endsWith(".kml")) {
                    kmlWriter = new XmlWriter(mapFile);
                    
                    KmlExporter.exportMap(kmlWriter, mapData);                                        
                    kmlWriter.closeFile();
                } else {
                    Logger.log(Logger.WARN, "Error in SaveMap - Unkown File Type, saving as FmXML.");
                    FmXmlExporter.export(mapData, mapFile);
                }
            } else {
                //file has not yet been saved, call save as
                SaveMapAs saveAsAction = new SaveMapAs(mainWindow, mapData, progressIndicator);
                saveAsAction.execute();
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            String           date       = dateFormat.format(new Date()) + "\t";
            
            Logger.log(Logger.INFO, "Map Saved");
            progressIndicator.updateProgress("Map Saved at " + date, 100);            
            progressIndicator.finish();
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in SaveMap.execute() - " + e);
        } 
    }

    @Override
    public void undo() {
        //Cannot Undo
    }
    
}
