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

import co.foldingmap.map.vector.CoordinateList;
import co.foldingmap.map.vector.CoordinateMath;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.LineString;
import co.foldingmap.map.vector.Polygon;
import co.foldingmap.Logger;
import co.foldingmap.map.DigitalMap;

/**
 *
 * @author Alec
 */
public class AddPointToObject extends Action {

    private Coordinate                  coordinateToAdd;
    private DigitalMap                  mapData;
    private VectorObject                mapObject;
    private CoordinateList<Coordinate>  objectCoordinates, originalCoordinates;       
    
    public AddPointToObject(DigitalMap mapData, Coordinate coordinateToAdd) {
        this.commandDescription  = "Add Point To Object";
        this.coordinateToAdd     = coordinateToAdd;
        this.mapData             = mapData;
        this.mapObject           = (VectorObject) mapData.getSelectedObjects().get(0);
        this.objectCoordinates   = mapObject.getCoordinateList();
        this.originalCoordinates = objectCoordinates.clone();
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
        Coordinate  closestCoordinate, currentCoordinate, coordinateAfterClosest, coordinateBeforeClosest;
        float       altitudeDifference, closestDistance, currentDistance;
        float       originalPointAfterDistance, originalPointBeforeDistance, pointAfterClosestDistance, pointBeforeClosestDistance;
        int         closestCoordinateIndex;

        closestDistance         = Float.MAX_VALUE;
        closestCoordinateIndex  = 0;
        closestCoordinate       = objectCoordinates.get(0);

        try {
            if (mapObject instanceof LineString || mapObject instanceof Polygon) {

                for (int i = 0; i < objectCoordinates.size(); i++) {
                    currentCoordinate = objectCoordinates.get(i);
                    currentDistance   = CoordinateMath.getDistance(currentCoordinate, coordinateToAdd);

                    if (currentDistance < closestDistance) {
                        closestCoordinateIndex = i;
                        closestDistance        = currentDistance;
                        closestCoordinate      = currentCoordinate;
                    }
                }

                //get the distances of the line segments if the new point were inserted before or after the closest point
                if (closestCoordinateIndex >= (objectCoordinates.size() - 1)) {
                    //last index in the list, use first point
                    coordinateAfterClosest     = objectCoordinates.get(0);
                    coordinateBeforeClosest    = objectCoordinates.get(closestCoordinateIndex - 1);
                } else if (closestCoordinateIndex == 0) {
                    //first index use last index
                    coordinateAfterClosest     = objectCoordinates.get(closestCoordinateIndex + 1);
                    coordinateBeforeClosest    = objectCoordinates.get((objectCoordinates.size() - 1));
                } else {
                    coordinateAfterClosest     = objectCoordinates.get(closestCoordinateIndex + 1);
                    coordinateBeforeClosest    = objectCoordinates.get(closestCoordinateIndex - 1);
                }

                pointAfterClosestDistance  = CoordinateMath.getDistance(coordinateToAdd, coordinateAfterClosest);
                pointBeforeClosestDistance = CoordinateMath.getDistance(coordinateToAdd, coordinateBeforeClosest);

                //get the length of the line segment before the new point would be inserted
                originalPointAfterDistance  = CoordinateMath.getDistance(closestCoordinate, coordinateAfterClosest);
                originalPointBeforeDistance = CoordinateMath.getDistance(closestCoordinate, coordinateBeforeClosest);

                //decide where in the LineString sequence the point should be placed; before or after the closest point
                if (pointAfterClosestDistance < originalPointAfterDistance) {
                    //insert point after the closest point
                    objectCoordinates.add((closestCoordinateIndex + 1), coordinateToAdd);
                    altitudeDifference = Math.abs(coordinateAfterClosest.getAltitude() - closestCoordinate.getAltitude());

                    if (coordinateAfterClosest.getAltitude() < closestCoordinate.getAltitude()) {
                        coordinateToAdd.setAltitude(coordinateAfterClosest.getAltitude() + (altitudeDifference / 2));
                    } else {
                        coordinateToAdd.setAltitude(closestCoordinate.getAltitude() + (altitudeDifference / 2));
                    }

                } else if (pointBeforeClosestDistance < originalPointBeforeDistance) {
                    //insert point before the closest point
                    objectCoordinates.add((closestCoordinateIndex), coordinateToAdd);
                    altitudeDifference = Math.abs(coordinateBeforeClosest.getAltitude() - closestCoordinate.getAltitude());

                    if (coordinateBeforeClosest.getAltitude() < closestCoordinate.getAltitude()) {
                        coordinateToAdd.setAltitude(coordinateBeforeClosest.getAltitude() + (altitudeDifference / 2));
                    } else {
                        coordinateToAdd.setAltitude(closestCoordinate.getAltitude() + (altitudeDifference / 2));
                    }
                }

                mapObject.setCoordinateList(objectCoordinates);
                mapObject.generateBoundingBox();
            }
            
            this.mapData.addCoordinateNode(coordinateToAdd);      
            
            //if the object is a polygon, update the outlines
            if (mapObject instanceof Polygon)
                ((Polygon) mapObject).updateOutlines(mapData.getTheme());
            
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in CommandAddPointToObject.execute() " + e);
        }
    }

    @Override
    public void undo() {
        try {
            mapObject.setCoordinateList(originalCoordinates);
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in CommandAddPointToObject.undo(): " + e);
        }
    }
    
}
