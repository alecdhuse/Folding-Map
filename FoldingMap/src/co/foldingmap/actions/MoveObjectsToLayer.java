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
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.NetworkLayer;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;

/**
 *
 * @author Alec
 */
public class MoveObjectsToLayer extends Action {
    protected VectorObjectList<VectorObject>  objects;
    protected VectorLayer[]                   originalLayers;
    protected VectorLayer                     newLayer;
            
    /**
     * Moves all objects in the given MapObjectLaist to a Layer with a given 
     * name.
     * 
     * @param mapData
     * @param layerName
     * @param objects 
     */
    public MoveObjectsToLayer(DigitalMap mapData, String layerName, VectorObjectList<VectorObject> objects) {
        this.commandDescription = "Move Objects to Layer";
        this.originalLayers     = new VectorLayer[objects.size()];
        this.objects            = objects;
        
        //find the layer to move objects to by name
        for (Layer l: mapData.getLayers()) {
            if (l.getName().equals(layerName)) {
                newLayer = (VectorLayer) l;
                break;
            }
        }
        
        //set the original layers to allow for undo later
        for (int i = 0; i < objects.size(); i++) {
            originalLayers[i] = (VectorLayer) objects.get(i).getParentLayer();
        }
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
        VectorLayer parentLayer;
        
        for (VectorObject object: objects) {
            parentLayer = (VectorLayer) object.getParentLayer();
            parentLayer.removeObject(object);
            newLayer.addObject(object);
            
            //If the parent layer is a NetworkLayer add coordinates to the main map NodeMap.
            if (parentLayer instanceof NetworkLayer) {
                for (Coordinate c: object.getCoordinateList()) 
                    newLayer.getParentMap().addCoordinateNode(c);                
            }
        }
    }

    @Override
    public void undo() {
        VectorObject   object;
        VectorLayer parentLayer;
        
        for (int i = 0; i < objects.size(); i++) {
            object      = objects.get(i);
            parentLayer = (VectorLayer) object.getParentLayer();
            
            parentLayer.removeObject(object);
            originalLayers[i].addObject(object);
        }
    }
    
}
