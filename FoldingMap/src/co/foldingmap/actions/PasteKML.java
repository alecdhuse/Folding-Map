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

import co.foldingmap.Logger;
import co.foldingmap.MainWindow;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.themes.ColorStyle;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;
import co.foldingmap.mapImportExport.KmlImport;
import co.foldingmap.xml.XMLParser;
import co.foldingmap.xml.XMLTag;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.StringReader;
import java.util.ArrayList;

/**
 *
 * @author Alec
 */
public class PasteKML extends Action {
    private boolean                         layerAdded;
    private DigitalMap                      mapData;
    private MainWindow                      mainWindow;
    private VectorObjectList<VectorObject>  pasteObjects; 
    private VectorLayer                     currentLayer;    
    
    public PasteKML(MainWindow mainWindow, DigitalMap mapData) {
        this.commandDescription = "Paste KML";
        this.layerAdded         = false;
        this.mainWindow         = mainWindow;
        this.mapData            = mapData;
    }
    
    /**
     * Returns if this Action can be undone.
     * 
     * @return 
     */
    @Override
    public boolean canUndo() {
        return true;
    }      
    
    @Override
    public void execute() {
        ArrayList<XMLTag>   tags;       
        ColorStyle          cs;
        String              clipboardText, layerName;
        Transferable        clipboardTransferable;        
        XMLParser           xmlParser;
        XMLTag              documentTag;        
        
        try {
            clipboardTransferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            clipboardText         = (String) clipboardTransferable.getTransferData(DataFlavor.stringFlavor);            
            xmlParser             = new XMLParser(new StringReader(clipboardText));
            documentTag           = xmlParser.parseDocument();
            tags                  = documentTag.getTags("Folder");

            if (tags.size() > 0) {
                layerName    = tags.get(0).getSubtagContent("Name");
                tags         = tags.get(0).getTags("Placemark");
                currentLayer = new VectorLayer(layerName);
                pasteObjects = KmlImport.getObjectsFromPlaceMarks(mapData.getCoordinateSet(), currentLayer, tags);
                currentLayer.addAllObjects(pasteObjects);                                
                mapData.addLayer(currentLayer);
                layerAdded = true;
                mainWindow.updateLayersTree();
                
                for (VectorObject vo: pasteObjects) {
                    mapData.setSelected(vo);     
                    
                    //Add Coordinates to the DigitalMaps's NodeMap
                    for (Coordinate c: vo.getCoordinateList()) 
                        mapData.addCoordinateNode(c);                    
                }                              
            } else {
                tags = documentTag.getTags("Placemark");
                
                //add styles                
                ArrayList<XMLTag> styleTags = documentTag.getTags("Style"); 
                
                for (XMLTag currentTag: styleTags) {
                    cs = KmlImport.getStyle(currentTag);
                    mapData.getTheme().addStyleElement(cs);
                }
                
                mainWindow.updateObjectDetailsToolBar();
                
                pasteObjects = KmlImport.getObjectsFromPlaceMarks(mapData.getCoordinateSet(), (VectorLayer) mapData.getSelectedLayer(), tags);
                currentLayer = (VectorLayer) mapData.getSelectedLayer();
                currentLayer.addAllObjects(pasteObjects);    
                                
                mainWindow.updateLayersTree();                
            }
            
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in PasteKML.execute() - " + e);
        }
    }

    @Override
    public void undo() {
        if (layerAdded) {
            mapData.removeLayer(currentLayer);
        } else {
            currentLayer.getObjectList().removeAll(pasteObjects);
        }
    }
    
}
