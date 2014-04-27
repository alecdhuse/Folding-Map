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

import co.foldingmap.ClipboardOperations;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.Layer;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;

/**
 * Copies selected objects to the clipboard then deletes them from the map.
 * 
 * @author Alec
 */
public class CutSelectedObjects extends Action {
    private DigitalMap               mapData;
    private VectorObjectList<VectorObject> objects;
    
    public CutSelectedObjects(DigitalMap mapData) {
        this.mapData    = mapData;
        this.objects    = new VectorObjectList<VectorObject>(mapData.getSelectedObjects());
        this.commandDescription = "Cut Objects";
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
        ClipboardOperations.copyMapObjects(mapData, objects);
        
        for (VectorObject object: objects) {
            Layer layer = object.getParentLayer();
                  
            if (layer instanceof VectorLayer) {
                VectorLayer vl = (VectorLayer) layer;
                vl.removeObject(object);
            }
        }
    }

    @Override
    public void undo() {

        for (VectorObject object: objects) {
            Layer layer = object.getParentLayer();
                  
            if (layer instanceof VectorLayer) {
                VectorLayer vl = (VectorLayer) layer;
                vl.addObject(object);
            }
        }        
    }
    
}
