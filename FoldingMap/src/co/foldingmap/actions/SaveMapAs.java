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

import co.foldingmap.GUISupport.FileExtensionFilter;
import co.foldingmap.GUISupport.ProgressIndicator;
import co.foldingmap.Logger;
import co.foldingmap.MainWindow;
import co.foldingmap.UserConfig;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.mapImportExport.FmXmlExporter;
import co.foldingmap.mapImportExport.GpxExporter;
import co.foldingmap.mapImportExport.KmlExporter;
import co.foldingmap.mapImportExport.OsmExporter;
import co.foldingmap.xml.XmlWriter;
import java.awt.FileDialog;
import java.io.File;

/**
 *
 * @author Alec
 */
public class SaveMapAs extends Action {
    private DigitalMap        mapData;
    private MainWindow        mainWindow;
    private ProgressIndicator progressIndicator;
    
    public SaveMapAs(MainWindow mainWindow, DigitalMap mapData, ProgressIndicator progressIndicator) {
        this.canUndo            = false;
        this.commandDescription = "Save Map As";
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
        try {
            File                    mapFile;
            FileExtensionFilter     fileExtensionFilter;
            FileDialog              fileDialog;
            int                     extensionStart;
            XmlWriter               kmlWriter;
            String                  fileExtension, fileName;
            UserConfig              userConfig;
            
            fileDialog          = new FileDialog(mainWindow);
            fileExtensionFilter = new FileExtensionFilter();
            userConfig          = mainWindow.getUserConfig();   
            
            if (userConfig != null)
                fileDialog.setDirectory(userConfig.getWorkingDIR());            
            
            fileExtensionFilter.addExtension("fmxml");
            fileExtensionFilter.addExtension("gpx");
            fileExtensionFilter.addExtension("kml");            
            fileExtensionFilter.addExtension("kmz"); 
            fileExtensionFilter.addExtension("osm"); 
            fileDialog.setFilenameFilter(fileExtensionFilter);            
            fileDialog.setMode(FileDialog.SAVE);
            
            fileDialog.setVisible(true);
            
            if (fileDialog.getFile() != null) {
                extensionStart = fileDialog.getFile().lastIndexOf(".");
                fileExtension  = fileDialog.getFile().substring(extensionStart + 1);
                fileName       = fileDialog.getDirectory() + fileDialog.getFile();
                
                if (extensionStart == -1) {
                    fileName = fileName + ".fmxml";
                    fileExtension = "fmxml";
                }
                
                mapFile = new File(fileName);
                
                if (fileExtension.equalsIgnoreCase("fmxml")) {                    
                    FmXmlExporter.export(mapData, mapFile);
                    mapData.setMapFile(mapFile);
                } else if (fileExtension.equalsIgnoreCase("gpx")) {
                    GpxExporter.export(mapData, new File(fileName));
                } else if (fileExtension.equalsIgnoreCase("kml")) {
                    kmlWriter = new XmlWriter(new File(fileName));
                    KmlExporter.exportMap(kmlWriter, mapData); 
                    kmlWriter.closeFile();                      
                } else if (fileExtension.equalsIgnoreCase("kmz")) {                
                    //TODO: Finish writing kmz save
                } else if (fileExtension.equalsIgnoreCase("osm")) {
                    OsmExporter osmExporter = new OsmExporter(mapData);
                    osmExporter.export(new File(fileName));
                }
                
                mapData.setMapFile(new File(fileDialog.getFile()));
            }
            
            progressIndicator.updateProgress("Map Saved", 100);
            progressIndicator.finish();            
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in SaveMapAs.execute() - " + e);
        }
    }

    @Override
    public void undo() {
        //No undo for this action.
    }
    
}
