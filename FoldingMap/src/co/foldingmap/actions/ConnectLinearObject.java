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
import co.foldingmap.map.vector.CoordinateMath;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.LineString;
import co.foldingmap.Logger;
import co.foldingmap.map.DigitalMap;

/**
 * Connects two linear objects.  The first selected object is extended to the 
 * second object.
 * 
 * @author Alec
 */
public class ConnectLinearObject extends Action {
    private DigitalMap      mapdata;
    private LineString      lineToExtend, oldLinetoExtend;  
    private VectorObject    objectToConectTo;
    private VectorLayer     originalParentLayer;
    
    public ConnectLinearObject(DigitalMap mapData) {
        VectorObjectList<VectorObject> selected = new VectorObjectList<VectorObject>(mapData.getSelectedObjects());
        
        this.mapdata = mapData;
        this.commandDescription = "Connect Linear Object";        
        
        if (selected.get(0) instanceof LineString) {
            this.lineToExtend     = (LineString) selected.get(0);
            this.objectToConectTo = selected.get(1);
        } else if (selected.get(1) instanceof LineString) {
            this.lineToExtend     = (LineString) selected.get(1);
            this.objectToConectTo = selected.get(0);
        } else {
            this.lineToExtend     = (LineString) selected.get(0);
            this.objectToConectTo = selected.get(1);
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
        Coordinate                  extendFrom, extendTo;
        CoordinateList<Coordinate>  lineToExtendCoordinates, objectToJoinCoordinates;
        float                       closestDistance, currentDistance;
        
        try {
            lineToExtendCoordinates = lineToExtend.getCoordinateList();
            objectToJoinCoordinates = objectToConectTo.getCoordinateList();
            
            //create a copy of the line to manipulate so this action can be undone.
            oldLinetoExtend     = (LineString) lineToExtend.copy();
            originalParentLayer = (VectorLayer) oldLinetoExtend.getParentLayer();
            
            if (!lineToExtendCoordinates.equals(Coordinate.UNKNOWN_COORDINATE) && 
                !objectToJoinCoordinates.equals(Coordinate.UNKNOWN_COORDINATE)) {
                
                //check to see if the user has already selected two coorinates to join
                if ((lineToExtend.getSelectedCoordinate() != null) && (objectToConectTo.getSelectedCoordinate() != null)) {
                    extendFrom = lineToExtend.getSelectedCoordinate();
                    extendTo   = objectToConectTo.getSelectedCoordinate();

                    if (extendFrom == lineToExtendCoordinates.get(0)) {
                        lineToExtend.prependCoordinate(extendTo);
                        extendTo.setShared(true);
                    } else if (extendFrom == lineToExtendCoordinates.lastCoordinate()) {
                        lineToExtend.appendCoordinate(extendTo);
                        extendTo.setShared(true);
                    } else if (extendTo == objectToJoinCoordinates.get(0)) {  
                        if (objectToConectTo instanceof LineString) {
                            VectorObject swap = objectToConectTo;
                            objectToConectTo  = lineToExtend;
                            lineToExtend      = (LineString) swap;
                        }

                        lineToExtend.prependCoordinate(extendFrom);
                        extendFrom.setShared(true);
                    } else if (extendTo == objectToJoinCoordinates.lastCoordinate()) {
                        if (objectToConectTo instanceof LineString) {
                            VectorObject swap   = objectToConectTo;
                            objectToConectTo = lineToExtend;
                            lineToExtend     = (LineString) swap;
                        }

                        lineToExtend.appendCoordinate(extendFrom);
                        extendFrom.setShared(true);
                    }
                } else {
                    /* The user did not select which points to use for the 
                    * connect action, try to determine what points to use.
                    */

                    /* Find which side of the LineToExtend is closest to the 
                    * LineToJoin, that endpoint will be extended.
                    */
                    Coordinate end1  = lineToExtendCoordinates.get(0);
                    Coordinate end2  = lineToExtendCoordinates.lastCoordinate();                
                    Coordinate join1 = objectToJoinCoordinates.getCoordinateClosestTo(end1);
                    Coordinate join2 = objectToJoinCoordinates.getCoordinateClosestTo(end2);                
                    float      dist1 = CoordinateMath.getDistance(end1, join1);
                    float      dist2 = CoordinateMath.getDistance(end2, join2); 

                    if (dist1 < dist2) {
                        extendFrom = end1;
                    } else {
                        extendFrom = end2;
                    }

                    //Find the coodinate closest to the coodinate to extend from.                
                    closestDistance = Float.MAX_VALUE;
                    extendTo        = objectToJoinCoordinates.get(0);

                    for (Coordinate c: objectToJoinCoordinates) {
                        currentDistance = CoordinateMath.getDistance(c, extendFrom);

                        if (currentDistance < closestDistance) {
                            closestDistance = currentDistance;
                            extendTo        = c;
                        }
                    }

                    if (dist1 < dist2) {
                        lineToExtend.appendCoordinate(extendTo);
                        extendTo.setShared(true);
                    } else {
                        lineToExtend.prependCoordinate(extendTo);
                        extendTo.setShared(true);
                    }                
                }
            }
            
            lineToExtend.generateBoundingBox();            
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in ConnectLinearObject.execute() - " + e);
        }
    }

    @Override
    public void undo() {
        VectorLayer parentLayer;

        parentLayer = (VectorLayer) lineToExtend.getParentLayer();        
        
        parentLayer.removeObject(lineToExtend);                
        originalParentLayer.addObject(oldLinetoExtend);
    }
    
}
