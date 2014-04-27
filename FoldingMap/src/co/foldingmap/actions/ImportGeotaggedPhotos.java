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
import co.foldingmap.GUISupport.ProgressBarPanel;
import co.foldingmap.Logger;
import co.foldingmap.MainWindow;
import co.foldingmap.UserConfig;
import co.foldingmap.imaging.GeoTag;
import co.foldingmap.imaging.JpegGeoTagReader;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.PhotoPoint;
import co.foldingmap.map.vector.VectorLayer;
import java.awt.FileDialog;
import java.io.File;

/**
 *
 * @author Alec
 */
public class ImportGeotaggedPhotos extends Action {
    private DigitalMap       mapData;
    private MainWindow       mainWindow;
    
    public ImportGeotaggedPhotos(MainWindow mainWindow, DigitalMap mapData) { 
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
        Coordinate               coordinate;
        File                     photoFile;
        FileDialog               fileDialog;
        FileExtensionFilter      fileExtensionFilter;
        GeoTag                   geoTag;
        JpegGeoTagReader         jpegGeoTagReader;
        PhotoPoint               photoPoint;
        ProgressBarPanel         progressPanel;
        String                   fileName, photoName;
        UserConfig               userConfig;
        VectorLayer              parentLayer;
        
        try {
            fileDialog    = new FileDialog(mainWindow);
            progressPanel = mainWindow.getProgressBarPanel();
            userConfig    = mainWindow.getUserConfig();
            
            fileDialog.setDirectory(userConfig.getImportDIR());
            //fileDialog.setMultipleMode(true);
            
            fileExtensionFilter = new FileExtensionFilter();
            fileExtensionFilter.addExtension("jpg");
            fileExtensionFilter.addExtension("jpeg");
            fileExtensionFilter.acceptDirectories(true);
            fileDialog.setFilenameFilter(fileExtensionFilter);     
                    
            fileDialog.setVisible(true);                        
            fileName  = fileDialog.getDirectory() + fileDialog.getFile(); 
            
            if (!fileName.endsWith("null")) {
                progressPanel.setVisible(true);     
                parentLayer      = mapData.getVectorLayer();
                jpegGeoTagReader = new JpegGeoTagReader();
                
                //TODO: loop for multiple files       
                photoFile  = new File(fileName);
                geoTag     = jpegGeoTagReader.readMetadata(photoFile);                
                photoName  = fileDialog.getFile().substring(0, fileDialog.getFile().lastIndexOf("."));
                coordinate = new Coordinate((float) geoTag.getAltitude(), (float) geoTag.getLatitude(), (float) geoTag.getLongitude(), geoTag.getDateInMillis());                        
                photoPoint = new PhotoPoint(photoName, coordinate, photoFile.getPath());
                
                mapData.getCoordinateSet().put(coordinate);
                coordinate.addParent(photoPoint);
                parentLayer.addObject(photoPoint);
            }
            
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in ImportGeotaggedPhotos.execute() - " + e);
        }
    }

    @Override
    public void undo() {
        //no undo
    }
    
}
