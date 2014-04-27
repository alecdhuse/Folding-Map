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
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;

/**
 * The Action for changing an object's Z Order, within a layer.
 * 
 * @author Alec
 */
public class ChangeObjectZOrder extends Action {
    public static final int   TO_FRONT    = 0;
    public static final int   TO_BACK     = Integer.MAX_VALUE;
    public static final int   FORWARD     = -1;
    public static final int   BACKWARD    = 1;
    
    private int                      operation;
    private int[]                    originalZOrders;
    private VectorObjectList<VectorObject> objectsToChangeZOrder;
    
    public ChangeObjectZOrder(DigitalMap mapData, int operation) {
        VectorObject   currentMapObject;
        VectorLayer parentLayer;

        this.commandDescription    = "Change Object Z Order";
        this.objectsToChangeZOrder = new VectorObjectList<VectorObject>(mapData.getSelectedObjects());
        this.operation             = operation;
        this.originalZOrders       = new int[objectsToChangeZOrder.size()];

        //copy all of the original Z orders into an array, so the action can be undone.
        for (int i = 0; i < objectsToChangeZOrder.size(); i++) {
            currentMapObject   = objectsToChangeZOrder.get(i);
            parentLayer        = (VectorLayer) currentMapObject.getParentLayer();
            originalZOrders[i] = parentLayer.getObjectList().indexOf(currentMapObject);            
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
        int         currentObjectZOrder, numberOfObjectsInLayer;
        VectorLayer parentLayer;

        for (int i = 0; i < objectsToChangeZOrder.size(); i++) {
            VectorObject currentMapObject = objectsToChangeZOrder.get(i);
            parentLayer                = (VectorLayer) currentMapObject.getParentLayer();
            currentObjectZOrder        = parentLayer.getObjectList().indexOf(currentMapObject);
            numberOfObjectsInLayer     = parentLayer.getObjectList().size();

            if (this.operation == TO_BACK) {
                try {
                    this.commandDescription = "Move Object To Back";
                    parentLayer.setObjectZOrder(currentMapObject, 0);
                } catch (Exception e) {
                    Logger.log(Logger.ERR, "Error in ChangeObjectZOrder.execute(Move Object To Back) - " + e);
                }
            } else if (this.operation == TO_FRONT) {
                try {
                    this.commandDescription = "Move Object To Front";
                    parentLayer.setObjectZOrder(currentMapObject, numberOfObjectsInLayer - 1);
                } catch (Exception e) {
                    Logger.log(Logger.ERR, "Error in ChangeObjectZOrder.execute(Move Object To Front) - " + e);
                }
            } else if (this.operation == FORWARD) {
                try {
                    this.commandDescription = "Move Object Forward";
                    parentLayer.setObjectZOrder(currentMapObject, (currentObjectZOrder - 1));
                } catch (Exception e) {
                    Logger.log(Logger.ERR, "Error in ChangeObjectZOrder.execute(Move Object Forward) - " + e);
                }
            } else if (this.operation == BACKWARD) {
                try {
                    this.commandDescription = "Move Object Backward";
                    parentLayer.setObjectZOrder(currentMapObject, (currentObjectZOrder + 1));
                } catch (Exception e) {
                    Logger.log(Logger.ERR, "Error in ChangeObjectZOrder.execute(Move Object Backward) - " + e);
                }
            }
        }
    }

    @Override
    public void undo() {
        VectorObject   currentMapObject;
        VectorLayer parentLayer;

        for (int i = 0; i < objectsToChangeZOrder.size(); i++) {
            currentMapObject   = objectsToChangeZOrder.get(i);
            parentLayer        = (VectorLayer) currentMapObject.getParentLayer();

            parentLayer.setObjectZOrder(currentMapObject, originalZOrders[i]);
        }
    }
    
}
