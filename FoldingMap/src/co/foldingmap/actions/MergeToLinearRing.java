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

import co.foldingmap.map.vector.LinearRing;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.CoordinateList;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.LineString;
import co.foldingmap.GUISupport.Updateable;
import co.foldingmap.Logger;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.MapObject;
import co.foldingmap.map.MapObjectList;

/**
 *
 * @author Alec
 */
public class MergeToLinearRing extends Action {
    protected DigitalMap               currentMap;
    protected Updateable               updateable;
    protected VectorObject             newObject;
    protected MapObjectList<MapObject> objectsToMerge;
    
    public MergeToLinearRing(DigitalMap dMap, Updateable updateable) {        
        this.currentMap         = dMap;
        this.commandDescription = "Merge To LinearRing";    
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
        CoordinateList<Coordinate>  mergeCoordinates;
        String                      name, objectClass, description;
        VectorLayer                 parentLayer;
        VectorObject                object;
        
        try {
            this.objectsToMerge = currentMap.getSelectedObjects().getFullCopy();
            
            //if we are converting a single LineString to a LinearRing use it's properties
            if ((objectsToMerge.size() == 1) && (objectsToMerge.get(0) instanceof LineString)) {
                object           = (VectorObject) objectsToMerge.get(0);
                name             = object.getName();
                objectClass      = object.getObjectClass();
                description      = object.getDescription();
                mergeCoordinates = objectsToMerge.get(0).getCoordinateList();
            } else {
                name             = "New LinearRing";
                objectClass      = "(Unspecified Linestring)";
                description      = "";
                mergeCoordinates =  MergeFunctions.getCoordinatesForMerge(new VectorObjectList<VectorObject>(this.objectsToMerge));
            }
            
            //remove old objects
            for (MapObject obj: objectsToMerge) {
                if (obj instanceof VectorObject) {
                    object      = (VectorObject) obj;                
                    parentLayer = (VectorLayer) object.getParentLayer();
                    parentLayer.removeObject(object);
                }
            }            
            
            parentLayer = (VectorLayer) objectsToMerge.get(0).getParentLayer();
            newObject   = new LinearRing(name, objectClass, mergeCoordinates);
            
            newObject.setDescription(description);
            parentLayer.addObject(newObject);
            
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in CommandMergeToLinearRing.execute: " + e);
        }
        
        this.updateable.update();
    }

    @Override
    public void undo() {
        VectorLayer   parentLayer;

        try {
            parentLayer = (VectorLayer) newObject.getParentLayer();
            parentLayer.removeObject(newObject);

            for (int i = 0; i < objectsToMerge.size(); i++) {
                MapObject currentObject = objectsToMerge.get(i);

                parentLayer = (VectorLayer) currentObject.getParentLayer();
                parentLayer.addObject((VectorObject) currentObject);
                currentObject.setHighlighted(true);
            }

            currentMap.setSelected(objectsToMerge);
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in CommandMergeToLinearRing.unExecute(): " + e);
        }
        
        this.updateable.update();
    }
    
}
