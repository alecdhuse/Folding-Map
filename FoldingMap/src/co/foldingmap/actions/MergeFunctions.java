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
import co.foldingmap.map.vector.VectorObjectList;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.MapPoint;
import co.foldingmap.Logger;

/**
 *
 * @author Alec
 */
public class MergeFunctions {
    
    public static CoordinateList<Coordinate> getCoordinatesForMerge(VectorObjectList<VectorObject> objectsToMerge) {
        CoordinateList<Coordinate>  coordinates;
        boolean                     allObjectsArePoints = true;
        VectorObject                currentMapObject;

        try {
            if (objectsToMerge.getMapPoints().size() == objectsToMerge.size()) {
                coordinates = getCoordinatesForMergeFromPoints (objectsToMerge);
            } else {
                coordinates = objectsToMerge.getCoordinatesForMerge();
            }
        } catch (Exception e) {
            coordinates = new CoordinateList<Coordinate>();
            Logger.log(Logger.ERR, "Error in MergeToLineString.getCoordinatesForMerge(MapObjectList): " + e);
        }

        //check for duplicute coordinates;
        for (int i = 0; i < coordinates.size(); i++) {
            for (int j = 0; j < coordinates.size(); j++) {
                if (i != j) {
                    if (coordinates.get(i) == coordinates.get(j))
                        coordinates.remove(j);
                }
            } //j loop
        }// i loop

        return coordinates;
    }    

    /**
     *
     */
    public static CoordinateList<Coordinate> getCoordinatesForMergeFromPoints(VectorObjectList<VectorObject> pointsToMerge) {
        boolean                     pointsAreHorizontal;
        Coordinate                  closestCoordinate, currentCoordinate;
        double                      closestDistance, currentDistance;
        double                      latitudeDifference, longitudeDifference;
        double                      easternMostLongitude, northernMostLatitude, southernMostLatitude, westernMostLongitude;
        CoordinateList<Coordinate>  allCoordinates, returnCoordinates;

        allCoordinates    = new CoordinateList<Coordinate>();
        returnCoordinates = new CoordinateList<Coordinate>();

        try {
            //determine if the coordinates are mostly horizontal or vertical
            easternMostLongitude = pointsToMerge.getEasternMostLongitude();
            northernMostLatitude = pointsToMerge.getNorthernMostLatitude();
            southernMostLatitude = pointsToMerge.getSouthernMostLatitude();
            westernMostLongitude = pointsToMerge.getWesternMostLongitude();
            latitudeDifference   = Math.abs(northernMostLatitude - southernMostLatitude);
            longitudeDifference  = Math.abs(easternMostLongitude - westernMostLongitude);

            if (longitudeDifference > latitudeDifference) {
                pointsAreHorizontal = true;
                pointsToMerge = pointsToMerge.sortEastToWest();
            } else {
                pointsAreHorizontal = false;
                pointsToMerge = pointsToMerge.sortNorthToSouth();
            }

            //get all coodinates to be merged
            for (int i = 0; i < pointsToMerge.size(); i++) {
                MapPoint currentPoint = (MapPoint) pointsToMerge.get(i);
                allCoordinates.add(currentPoint.getCoordinateList().get(0));
            }

            if (allCoordinates.size() > 0) {
                currentCoordinate = allCoordinates.remove(0);
                returnCoordinates.add(currentCoordinate);

                while (allCoordinates.size() > 0) {
                    closestCoordinate = null;
                    closestDistance   = Float.MAX_VALUE;

                    for (Coordinate compareCoordinate: allCoordinates) {
                        currentDistance = CoordinateMath.getDistance(currentCoordinate, compareCoordinate);

                        if (currentDistance < closestDistance) {
                            closestCoordinate = compareCoordinate;
                            closestDistance   = currentDistance;
                        }
                    }

                    if (closestCoordinate != null) {
                        allCoordinates.remove(closestCoordinate);
                        returnCoordinates.add(closestCoordinate);
                        currentCoordinate = closestCoordinate;
                    }
                }  //end while
            } //end allCoordinates size check
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in MergeToLineString.getCoordinatesForMergeFromPoints(MapObjectList) - " + e);
        }

        return returnCoordinates;
    }        
}
