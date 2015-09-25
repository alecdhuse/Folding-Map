/* 
 * Copyright (C) 2015 Alec Dhuse
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
import co.foldingmap.map.vector.MapPoint;
import co.foldingmap.map.vector.LineString;
import co.foldingmap.map.vector.Polygon;
import co.foldingmap.GUISupport.Updateable;
import co.foldingmap.Logger;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.Layer;
import java.util.ArrayList;

/**
 * Takes a list of MapObjects and creates a new object form one or more parts
 * of the given list.
 * 
 * @author Alec
 */
public class TraceMerge extends Action {
    private final ArrayList<Updateable>             updateables;    
    private final CoordinateList<Coordinate>        coordinatesToMerge;
    private DigitalMap                              mapData;
    private VectorObject                            newObject;
    private final VectorObjectList<VectorObject>    objectsToMerge;
    
    public TraceMerge(DigitalMap mapData, 
                      VectorObjectList<VectorObject> objectsToMerge, 
                      CoordinateList<Coordinate>     coordinatesToMerge,
                      ArrayList<Updateable>          updateables) {
        
        this.commandDescription  = "Trace Merge";
        this.coordinatesToMerge  = coordinatesToMerge.clone();
        this.mapData             = mapData;
        this.objectsToMerge      = objectsToMerge;
        this.updateables         = updateables;
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
        VectorLayer  parentLayer;
        
        try {
            newObject   = null;
            parentLayer = null;
            
            if (coordinatesToMerge.size() == 1) {
                //create point
                newObject = new MapPoint("New Point", "(Unspecified Point)", "", coordinatesToMerge.get(0));
            } else if (coordinatesToMerge.size() > 1) {
                if (coordinatesToMerge.lastCoordinate().equals(coordinatesToMerge.get(0))) {
                    //make a Polygon
                    coordinatesToMerge.remove(coordinatesToMerge.lastCoordinate());
                    newObject = new co.foldingmap.map.vector.Polygon("New Polygon", "(Unspecified Polygon)", coordinatesToMerge);
                } else {
                    //make a LineString
                    newObject = new co.foldingmap.map.vector.LineString("New LineString", "(Unspecified Linestring)", coordinatesToMerge);
                }
            } 
            
            if (newObject != null) {
                for (Coordinate c: coordinatesToMerge) {
                    c.addParent(newObject);           
                    c.incrementPullCount();
                }

                /*
                 * Run a check to see if all the objects points, if they are
                 * remove the points.
                 */
                if (objectsToMerge.getMapPoints().size() == objectsToMerge.size()) {
                    for (VectorObject vo: objectsToMerge) {
                        parentLayer = (VectorLayer) vo.getParentLayer();
                        parentLayer.removeObject(vo);                        
                        vo.getCoordinateList().removeParentObject(vo);
                    }
                }
                    
                if (mapData.getSelectedLayer() instanceof VectorLayer) {
                    parentLayer = (VectorLayer) mapData.getSelectedLayer();
                } else {
                    for (Layer l: mapData.getLayers()) {
                        if (l instanceof VectorLayer) 
                            parentLayer = (VectorLayer) l;     
                            break;
                    }
                }

                if (parentLayer == null) {
                    parentLayer = new VectorLayer("New Layer");
                    mapData.addLayer(parentLayer);
                }            
            
                parentLayer.addObject(newObject);
                newObject.setHighlighted(true);
                mapData.getSelectedObjects().add(newObject);
            }    
            
            for (Updateable u: updateables)
                u.update();
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in TraceMerge.execute() - " + e);
        }        
    }

    private void generalMerge() {
        VectorObject               currentObject;
        VectorLayer             parentLayer;
        
        //If objects are MapPoints, remove them
        for (int i = 0; i < objectsToMerge.size(); i++) {
            currentObject = objectsToMerge.get(i);

            if (currentObject instanceof MapPoint) {
                parentLayer = (VectorLayer) currentObject.getParentLayer();
                parentLayer.removeObject(currentObject);
            }
        } // end for loop

        if (coordinatesToMerge.get(0) == coordinatesToMerge.lastCoordinate()) {
            //polygon
            coordinatesToMerge.remove(coordinatesToMerge.size() - 1);
            newObject = new Polygon("New Polgon", "(Unspecified Polygon)", coordinatesToMerge);
        } else {
            if (objectsToMerge.get(0) == objectsToMerge.lastElement() && objectsToMerge.get(0) instanceof LineString) {
                /** The first and last object is the same LineString
                 *  Create a Polygon using the coordinates in between the 
                 *  the two coordinates selected.
                 */
                LineString tempLine = (LineString) objectsToMerge.get(0);                
                CoordinateList<Coordinate> tempLineCoordinatesToUse = 
                        tempLine.getCoordinateList().getCoordinatesBetween(
                            coordinatesToMerge.get(0), coordinatesToMerge.lastCoordinate());
                
                //remove the first and last coordinate
                coordinatesToMerge.remove(coordinatesToMerge.get(0));
                coordinatesToMerge.remove(coordinatesToMerge.lastCoordinate());
                
                //add in the coordinates from the LineStirng
                tempLineCoordinatesToUse.reverse();
                coordinatesToMerge.addAll(tempLineCoordinatesToUse);
                
                //create the polygon
                newObject = new Polygon("New Polgon", "(Unspecified Polygon)", coordinatesToMerge);
            } else {
                //lineString
                newObject = new LineString("New Line", "(Unspecified Linestring)", coordinatesToMerge);        
            }
        }
        
        //Get the parent layer of the first object, that is where the new object will go.
        parentLayer = (VectorLayer) objectsToMerge.get(0).getParentLayer();        
        parentLayer.addObject(newObject);
        mapData.deselectObjects();
        mapData.setSelected(newObject);
    }

    private void lineStringMerge() {
        CoordinateList  line1Coordinates, line2Coordinates, newLineCoordinates;
        double          firstFirst, firstLast;
        double          lastFirst,  lastLast;
        LineString      line1, line2;
        VectorObject    currentObject;
        VectorLayer     parentLayer;

        line1 = (LineString) objectsToMerge.get(0);
        line2 = (LineString) objectsToMerge.get(1);
        line1Coordinates   = line1.getCoordinateList();
        line2Coordinates   = line2.getCoordinateList();
        newLineCoordinates = new CoordinateList();
        
        if ((line1.isEndPoint(coordinatesToMerge.get(0)) && line2.isEndPoint(coordinatesToMerge.get(1))) ||
            (line1.isEndPoint(coordinatesToMerge.get(1)) && line2.isEndPoint(coordinatesToMerge.get(0)))) {
            
            firstFirst = CoordinateMath.getDistance(line1.firstCoordinate(), line2.firstCoordinate());
            firstLast  = CoordinateMath.getDistance(line1.firstCoordinate(), line2.lastCoordinate());
            lastFirst  = CoordinateMath.getDistance(line1.lastCoordinate(),  line2.firstCoordinate());
            lastLast   = CoordinateMath.getDistance(line1.lastCoordinate(),  line2.lastCoordinate());

            if ((firstFirst < firstLast) && (firstFirst < lastFirst) && (firstFirst < lastLast)) {
                newLineCoordinates = line2Coordinates.clone();
                newLineCoordinates.reverse();
                newLineCoordinates.addAll(line1Coordinates);
            } else if ((firstLast < firstFirst) && (firstLast < lastFirst) && (firstLast < lastLast)) {
                newLineCoordinates = line2Coordinates.clone();
                newLineCoordinates.addAll(line1Coordinates);
            } else if ((lastFirst < firstFirst) && (lastFirst < firstLast) && (lastFirst < lastLast)) {
                newLineCoordinates = line1Coordinates.clone();
                newLineCoordinates.addAll(line2Coordinates);
            } else if ((lastLast < firstFirst) && (lastLast < firstLast) && (lastLast < lastFirst)) {
                newLineCoordinates = line1Coordinates.clone();
                newLineCoordinates.reverse();
                newLineCoordinates.addAll(line2Coordinates);
            }

            for (int i = 0; i < objectsToMerge.size(); i++) {
                currentObject = objectsToMerge.get(i);
                parentLayer   = (VectorLayer) currentObject.getParentLayer();

                parentLayer.removeObject(currentObject);               
            }

            currentObject = line1.copy();
            parentLayer   = (VectorLayer) line1.getParentLayer();

            currentObject.setCoordinateList(newLineCoordinates);
            parentLayer.addObject(currentObject);
        } else {
            //connect aka join
            if (line1.getCoordinateList().lastCoordinate().equals(coordinatesToMerge.get(1))) {
                //the coordinate to extend from is the last coordinate, just add on
                line1.getCoordinateList().add(coordinatesToMerge.get(1));
            } else {
                //coordinate to extend from is first, we must prepend
                line1.prependCoordinate(coordinatesToMerge.get(1));
            }
        }
    }    
    
    @Override
    public void undo() {
        VectorObject       currentObject;
        VectorLayer     parentLayer;

        parentLayer = (VectorLayer) newObject.getParentLayer();
        parentLayer.removeObject(newObject);

        for (int i = 0; i < objectsToMerge.size(); i++) {
            currentObject = objectsToMerge.get(i);

            if (currentObject instanceof MapPoint) {
                parentLayer = (VectorLayer) currentObject.getParentLayer();
                parentLayer.addObject(currentObject);
            }
        } 

        parentLayer = (VectorLayer) newObject.getParentLayer();
        parentLayer.removeObject(newObject);

        for (Updateable u: updateables)
            u.update();        
    }
    
}
