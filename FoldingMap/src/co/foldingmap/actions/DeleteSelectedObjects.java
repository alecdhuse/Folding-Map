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

import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.Layer;
import co.foldingmap.map.MapObject;
import co.foldingmap.map.MapObjectList;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.VectorObject;

/**
 *
 * @author Alec
 */
public class DeleteSelectedObjects extends Action {
    private DigitalMap               mapData;
    private MapObjectList<MapObject> selectedObjects;
    
    public DeleteSelectedObjects(DigitalMap mapData) {
        this.commandDescription = "Delete";
        this.mapData            = mapData;
        this.selectedObjects    = mapData.getSelectedObjects().clone();
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
        for (Layer l: mapData.getLayers()) {
            if (l instanceof VectorLayer) {
                VectorLayer vl = (VectorLayer) l;
                vl.getObjectList().removeAll(selectedObjects);
            }
        }
        
        mapData.deselectObjects();        
    }

    @Override
    public void undo() {
        VectorLayer parentLayer;
        
        for (MapObject object: selectedObjects) {
            parentLayer = (VectorLayer) object.getParentLayer();
            parentLayer.addObject((VectorObject) object);
        }
        
        mapData.setSelected(selectedObjects);
    }
    
}
