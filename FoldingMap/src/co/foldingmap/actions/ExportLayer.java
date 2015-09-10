/* 
 * Copyright (C) 2015 Alec Dhuse
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
import co.foldingmap.GUISupport.ProgressBarPanel;
import co.foldingmap.Logger;
import co.foldingmap.MainWindow;
import co.foldingmap.UserConfig;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.Layer;
import co.foldingmap.map.MapProjection;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.NodeMap;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.mapImportExport.FmXmlExporter;
import co.foldingmap.mapImportExport.GpxExporter;
import co.foldingmap.mapImportExport.JsonExporter;
import co.foldingmap.mapImportExport.KmlExporter;
import co.foldingmap.xml.XmlWriter;
import java.awt.FileDialog;
import java.io.File;

/**
 *
 * @author Alec
 */
public class ExportLayer extends Action {
    private final Layer         layer;
    private final MainWindow    mainWindow;
    private final MapProjection projection;
    
    public ExportLayer(MainWindow mainWindow, MapProjection projection, Layer layer) {
        this.mainWindow  = mainWindow;
        this.projection  = projection;
        this.layer       = layer;
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
    
    public static void export(MainWindow mainWindow, MapProjection projection, Layer layer) {
        ExportLayer el = new ExportLayer(mainWindow, projection, layer);
        el.execute();
    }
    
    private int getCoordinateCount(VectorLayer vectorLayer) {        
        int count = 0;
        
        for (VectorObject object: vectorLayer.getObjectList()) {
            if (object != null) {
                if (object.getCoordinateList() != null)
                    count += object.getCoordinateList().size();
            }
        }
        
        return count;
    }
    
    @Override
    public void execute() {
        DigitalMap          newMap;
        int                 count, numberOfCoordinates;
        NodeMap             nodeMap;
        ProgressBarPanel    progressBarPanel;
        String              fileExt, fileName;
        VectorLayer         vectorLayer;
        XmlWriter           xmlWriter;
        
        //init       
        count    = 0;
        fileName = getFile();
        fileExt  = fileName.substring(fileName.lastIndexOf(".") + 1);
        newMap   = new DigitalMap(layer.getName(), projection);
        nodeMap  = new NodeMap();
        
        progressBarPanel = mainWindow.getProgressBarPanel();
        
        if (fileExt.equalsIgnoreCase("fmxml")) {    
            progressBarPanel.updateProgress("Indexing Nodes", 15);
            progressBarPanel.setVisible(true);
            
            if (layer instanceof VectorLayer) {
                vectorLayer = (VectorLayer) layer;
                numberOfCoordinates = getCoordinateCount(vectorLayer);

                nodeMap = new NodeMap(numberOfCoordinates);

                for (VectorObject object: vectorLayer.getObjectList()) {
                    //add nodes in to new map                
                    for (Coordinate c: object.getCoordinateList()) {
                        if (c.getID() != 0) {
                            nodeMap.put(c.getID(), c);                
                        } else {
                            count++;
                            c.setId(count);
                            nodeMap.put(count, c);
                        }
                    }
                }
            } 
        }
        
        newMap.addLayer(layer);
        newMap.setNodeMap(nodeMap);
        
        progressBarPanel.updateProgress("Exporting File", 30);
        
        if (fileExt.equalsIgnoreCase("fmxml")) {
            FmXmlExporter.export(newMap, new File(fileName));
        } else if (fileExt.equalsIgnoreCase("gpx")) {
            GpxExporter.export(newMap, new File(fileName));
        } else if (fileExt.equalsIgnoreCase("js")) {
            JsonExporter.exportMapForLeaflet(newMap, new File(fileName));
        } else if (fileExt.equalsIgnoreCase("json")) {
            JsonExporter.exportMap(newMap, new File(fileName));
        } else if (fileExt.equalsIgnoreCase("kml")) {
            xmlWriter = new XmlWriter(new File(fileName));
            KmlExporter.exportMap(xmlWriter, newMap); 
            xmlWriter.closeFile();                      
        } else if (fileExt.equalsIgnoreCase("kmz")) {                
            //TODO: Finish writing kmz save
        }
        
        progressBarPanel.finish();
    }

    private String getFile() {
        FileExtensionFilter     fileExtensionFilter;
        FileDialog              fileDialog;
        UserConfig              userConfig;
        
        try {
            fileDialog          = new FileDialog(mainWindow);
            fileExtensionFilter = new FileExtensionFilter();
            userConfig          = mainWindow.getUserConfig();  
            
            if (userConfig != null) fileDialog.setDirectory(userConfig.getWorkingDIR());               
            
            fileExtensionFilter.addExtension("fmxml");
            fileExtensionFilter.addExtension("gpx");
            fileExtensionFilter.addExtension("js");
            fileExtensionFilter.addExtension("json");
            fileExtensionFilter.addExtension("kml");            
            fileExtensionFilter.addExtension("kmz"); 
            
            fileDialog.setFilenameFilter(fileExtensionFilter);            
            fileDialog.setMode(FileDialog.SAVE);                     
            fileDialog.setVisible(true);
            
            if (fileDialog.getFile() != null) {
                return fileDialog.getDirectory() + fileDialog.getFile();
            } else {
                return null;
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in ExportLayer.getFile()");
            return null;
        }
    }
    
    @Override
    public void undo() {
        //no undo
    }
    
}
