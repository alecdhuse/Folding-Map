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
import co.foldingmap.map.MapObject;
import co.foldingmap.map.MapObjectList;
import co.foldingmap.map.vector.MultiGeometry;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;

/**
 * Unmerge actions, performs different operations depending on what is being unmerged.
 * 
 * @author Alec
 */
public class Unmerge extends Action {
    private DigitalMap                      mapData;
    private MapObjectList<MapObject>        selectedObjects;
    private VectorObjectList<VectorObject>  unmergedObjects;

    public Unmerge(DigitalMap mapData) {
        this.mapData         = mapData;
        this.selectedObjects = mapData.getSelectedObjects();
        this.unmergedObjects = new VectorObjectList<VectorObject>(); 
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
        
        for (MapObject object: selectedObjects) {
            
            if (object instanceof MultiGeometry) {
                unmergeMultiGeometry((MultiGeometry) object);
            }
        }
    }

    @Override
    public void undo() {
        
    }
    
    /**
     * Takes the component objects of the given MultiGeometry removes them and
     * adds them to the same layer as the MultiGeometry. 
     * 
     * @param multi 
     */
    public void unmergeMultiGeometry(MultiGeometry multi) {
        VectorLayer  parentLayer;
        VectorObject object;
        
        parentLayer = (VectorLayer) multi.getParentLayer();
        parentLayer.removeObject(multi);        
        
        for (int i = 0; i < multi.getComponentObjects().size(); i++) {
            object = multi.getComponentObjects().get(i);
            parentLayer.addObject(object);
            unmergedObjects.add(object);
        }       
    }
}
