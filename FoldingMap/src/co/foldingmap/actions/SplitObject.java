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

import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.CoordinateList;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.LineString;
import co.foldingmap.Logger;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.Layer;
import java.util.HashMap;

/**
 * Splits an object at a given point
 * 
 * @author Alec
 */
public class SplitObject extends Action {
    private Coordinate          coordinateToSplitAt;
    private DigitalMap          currentMap;
    private VectorObject        objectToSplit, newObject1, newObject2;
    
    /**
     * Constructor for objects of class CommandSplitObject
     * 
     * @param dMap
     * @param objectToSplit
     * @param coordinateToSplitAt 
     */
    public SplitObject(DigitalMap dMap, VectorObject objectToSplit, Coordinate coordinateToSplitAt) {
        this.commandDescription  = "Split Object";
        this.coordinateToSplitAt = coordinateToSplitAt;
        this.currentMap          = dMap;
        this.objectToSplit       = objectToSplit;
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
        boolean                     matchFound;
        Coordinate                  currentCoordinate;
        CoordinateList<Coordinate>  parentObjectCoordinates, newObjectCoordinates1, newObjectCoordinates2;
        VectorLayer                 parentLayer;
            
        try {
            matchFound              = false;
            parentObjectCoordinates = this.objectToSplit.getCoordinateList();
            newObjectCoordinates1   = new CoordinateList<Coordinate>();
            newObjectCoordinates2   = new CoordinateList<Coordinate>();

            /** 
            * Processes point by point, add it to the first new object until 
            * the split point is found then add to the the second object                  
            */        
            for (int i = 0; i < parentObjectCoordinates.size(); i++) {
                currentCoordinate = parentObjectCoordinates.get(i);

                if (!matchFound) {
                    newObjectCoordinates1.add(currentCoordinate);
                } else {
                    newObjectCoordinates2.add(currentCoordinate);
                }

                if (currentCoordinate.equals(this.coordinateToSplitAt) && !matchFound) {
                    matchFound = true;
                    currentCoordinate.setShared(true);
                    newObjectCoordinates2.add(currentCoordinate);
                }
            }

            if (matchFound) {
                if (objectToSplit instanceof LineString) {
                    newObject1 = new LineString((objectToSplit.getName()), objectToSplit.getObjectClass(), newObjectCoordinates1);
                    newObject2 = new LineString((objectToSplit.getName()), objectToSplit.getObjectClass(), newObjectCoordinates2);

                    if (objectToSplit.getParentLayer() instanceof VectorLayer) {
                        parentLayer = (VectorLayer) objectToSplit.getParentLayer();

                        //Preserve visibility with split objects
                        if (objectToSplit.getVisibility() != null) {
                            newObject1.setVisibility(objectToSplit.getVisibility().clone());
                            newObject2.setVisibility(objectToSplit.getVisibility().clone());
                        }
                        
                        parentLayer.removeObject(objectToSplit);
                        parentLayer.addObject(newObject1);
                        parentLayer.addObject(newObject2);
                    } else {
                        //Parent layer not set, search for it                    
                        for (Layer currentLayer: currentMap.getLayers()) {
                            if (currentLayer instanceof VectorLayer) {
                                VectorLayer vl = (VectorLayer) currentLayer;

                                if (vl.getObjectList().contains(objectToSplit)) {
                                    vl.removeObject(objectToSplit);
                                    vl.addObject(newObject1);
                                    vl.addObject(newObject2);                                
                                }
                            }
                        }

                    }

                }
                
                currentMap.deselectObjects();
            } // end if matchFound
            
            //copy over custom data fields
            if (newObject1 != null && newObject2 != null) {
                newObject1.setCustomDataFields((HashMap<String, String>) objectToSplit.getCustomDataFields().clone());
                newObject2.setCustomDataFields((HashMap<String, String>) objectToSplit.getCustomDataFields().clone());
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in SplitObject.execute() - " + e);
        }
    }

    @Override
    public void undo() {
        VectorLayer  parentLayer;

        try {
            parentLayer = (VectorLayer) newObject1.getParentLayer();
            parentLayer.removeObject(newObject1);

            parentLayer = (VectorLayer) newObject2.getParentLayer();
            parentLayer.removeObject(newObject2);

            parentLayer = (VectorLayer) objectToSplit.getParentLayer();
            parentLayer.addObject(objectToSplit);
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error SplitObject.undo() - " + e);
        }
    }
    
}
