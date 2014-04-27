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

import co.foldingmap.GUISupport.Updateable;
import co.foldingmap.Logger;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.Layer;
import co.foldingmap.map.MapObject;
import co.foldingmap.map.MapObjectList;
import co.foldingmap.map.vector.MultiGeometry;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;
import java.util.ArrayList;

/**
 *
 * @author Alec
 */
public class MergeToMultiGeometry extends Action {
    protected ArrayList<VectorLayer>         parentLayers;
    protected DigitalMap                     mapData;
    protected Updateable                     updateable;
    protected MultiGeometry                  newObject;
    protected VectorObjectList<VectorObject> objectsToMerge;
    
    public MergeToMultiGeometry(DigitalMap mapData, Updateable updateable) {
        this.commandDescription = "Merge To Multigeometry"; 
        this.mapData            = mapData;
        this.updateable         = updateable;
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
        MapObject                mapObject;
        MapObjectList<MapObject> selectedObjects = mapData.getSelectedObjects();
        parentLayers             = new ArrayList<VectorLayer>();
        VectorLayer              vectorLayer;        
        
        objectsToMerge = new VectorObjectList<VectorObject>();
        
        //Check to see if all selected objects are VectorObjects.
        for (int i = 0; i < selectedObjects.size(); i++) {
            mapObject = selectedObjects.get(i);
            
            if (mapObject instanceof VectorObject) {
                objectsToMerge.add((VectorObject) mapObject);
            } else {
                Logger.log(Logger.WARN, "Error in MergeToMultiGeometry.execute() - Can't merge non VectorObjects.");
            }
        }                
        
        //remove old objects from their parent layers
         for (VectorObject vo: objectsToMerge) {
            if (vo.getParentLayer() instanceof VectorLayer) {
                vectorLayer = (VectorLayer) vo.getParentLayer();
                vectorLayer.removeObject(vo);  
                parentLayers.add(vectorLayer);                              
            } else {
                //Default here, if there is a problem
                for (Layer l: mapData.getLayers()) {
                    if (l instanceof VectorLayer) {
                        vectorLayer = (VectorLayer) l;
                        vectorLayer.removeObject(vo);
                    }
                }
            }
        }
        
        newObject = new MultiGeometry("New MultiGeometry", objectsToMerge);         
         
        //Add the new object to the selected Layer, if it is a VectorLayer.
        if (mapData.getSelectedLayer() instanceof VectorLayer) {
            vectorLayer = (VectorLayer) mapData.getSelectedLayer();
            vectorLayer.addObject(newObject);
        } else {
            //Selected Layer is not a VectorLayer, find the first VectorLayer 
            //and add it to that
            for (Layer l: mapData.getLayers()) {
                if (l instanceof VectorLayer) {
                    vectorLayer = (VectorLayer) l;
                    vectorLayer.addObject(newObject);
                }
            }
        }
        
        this.updateable.update();
    }

    @Override
    public void undo() {
        VectorLayer     vectorLayer;    
        VectorObject    vo;
        
        //Add the componet objects back to their parent Layers.
        for (int i = 0; i < newObject.getComponentObjects().size(); i++) {
            vo = newObject.getComponentObject(i);
            vectorLayer = parentLayers.get(i);
            vectorLayer.addObject(vo);
        }
        
        //Remove the new MultiGeometry from it's parent Layer.
        vectorLayer = (VectorLayer) newObject.getParentLayer();
        vectorLayer.removeObject(newObject);
        
        this.updateable.update();
    }
    
}
