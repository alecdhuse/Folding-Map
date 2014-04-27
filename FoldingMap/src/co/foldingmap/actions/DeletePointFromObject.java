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
import co.foldingmap.map.vector.CoordinateList;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.VectorObject;

/**
 * Removes the selected point from a selected object.
 * 
 * @author Alec
 */
public class DeletePointFromObject extends Action {
    private Coordinate                  coordinateToRemove;
    private DigitalMap                  mapData;
    private VectorObject                objectToRemovePoint, originalObjectToRemovePoint;
    private CoordinateList<Coordinate>  objectCoordinates;
    
    public DeletePointFromObject(DigitalMap mapData) {
        this.commandDescription          = "Remove Point From Object";
        this.mapData                     = mapData;
        this.objectToRemovePoint         = (VectorObject) mapData.getSelectedObjects().get(0);
        this.originalObjectToRemovePoint = (VectorObject) objectToRemovePoint.copy();
        this.coordinateToRemove          = objectToRemovePoint.getSelectedCoordinate();
        this.objectCoordinates           = objectToRemovePoint.getCoordinateList();
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
        try {
            if (objectCoordinates.size() > 1) {
                objectCoordinates.remove(coordinateToRemove);                
                objectToRemovePoint.updateOutlines(mapData.getTheme());
                objectToRemovePoint.generateBoundingBox();
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in CommandDeletePointFromObject.execute() - " + e);
        }
    }

    @Override
    public void undo() {
        try {
            VectorLayer     parentLayer;

            if (objectToRemovePoint.getParentLayer() instanceof VectorLayer) {
                parentLayer = (VectorLayer) objectToRemovePoint.getParentLayer();
                parentLayer.removeObject(objectToRemovePoint);
                parentLayer.addObject(originalObjectToRemovePoint);
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in CommandDeletePointFromObject.undo() - " + e);
        }
    }
    
}
