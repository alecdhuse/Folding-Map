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
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;
import co.foldingmap.mapImportExport.GpxImporter;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

/**
 * Pastes the KML equivalent of the GPX on the clipboard to the selected layer.
 * 
 * @author Alec
 */
public class PasteGPX extends Action {
    private DigitalMap                     mapData;
    private VectorObjectList<VectorObject> pasteObjects; 
    private VectorLayer                    currentLayer;   
    
    public PasteGPX(DigitalMap mapData) {
        this.commandDescription = "Paste GPX";
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
        String              clipboardText;
        Transferable        clipboardTransferable;    
        VectorLayer         selectedLayer;
        
        try {
            clipboardTransferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            clipboardText         = (String) clipboardTransferable.getTransferData(DataFlavor.stringFlavor);
            pasteObjects          = new VectorObjectList<VectorObject>();
                    
            pasteObjects.addAll(GpxImporter.parseTracks(clipboardText));
            pasteObjects.addAll(GpxImporter.parseWaypoints(clipboardText, null));
            
            if (mapData.getSelectedLayer() instanceof VectorLayer) {
                selectedLayer = (VectorLayer) mapData.getSelectedLayer();
                selectedLayer.addAllObjects(pasteObjects);
                
                //Add Coordinates to the DigitalMaps's NodeMap
                for (VectorObject obj: pasteObjects) {
                    for (Coordinate c: obj.getCoordinateList()) {
                        mapData.addCoordinateNode(c);
                    }
                }
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in PasteGPX.execute() - " + e);
        }
    }

    @Override
    public void undo() {
        VectorLayer         parentLayer;
        
        for (VectorObject object: pasteObjects) {
            if (object.getParentLayer() instanceof VectorLayer) {
                parentLayer = (VectorLayer) object.getParentLayer();
                parentLayer.removeObject(object);
            }
        }
    }
    
}
