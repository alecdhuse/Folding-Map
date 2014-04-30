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
import co.foldingmap.map.vector.Polygon;
import co.foldingmap.GUISupport.Updateable;
import co.foldingmap.Logger;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.MapObject;
import co.foldingmap.map.MapObjectList;
import co.foldingmap.map.MapView;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Merges one or more objects into a single Polygon.
 * 
 * @author Alec
 */
public class MergeToPolygon extends Action {    
    private DigitalMap               mapData;
    private VectorObject                newObject;
    private MapObjectList<MapObject> objectsToMerge;
    private MapView                  mapView;
    private Updateable               updateable;
    
    public MergeToPolygon(DigitalMap mapData, Updateable updateable) {
        try {            
            this.mapData            = mapData;
            this.commandDescription = "Merge To Polygon";
            this.updateable         = updateable;
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in MergeToPolygon.Constructor(DigitalMap) - " + e);
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
        ArrayList<CoordinateList<Coordinate>> polygons, validPolygons;
        CoordinateList<Coordinate>            coordinates, userSelection;
        boolean                               verticesCross;
        int                                   index;
        LineString                            lineString;
        String                                newName, newType;
        VectorLayer                           parentLayer;
        VectorObjectList<VectorObject>        vectorObjects;
        
        objectsToMerge     = mapData.getSelectedObjects().clone();
        mapView            = mapData.getLastMapView();
        vectorObjects      = new VectorObjectList<VectorObject>(objectsToMerge);
        index              = -1;
        
        try {
            parentLayer    = (VectorLayer) objectsToMerge.get(0).getParentLayer();
            coordinates    = MergeFunctions.getCoordinatesForMerge(vectorObjects);
            validPolygons  = new ArrayList<CoordinateList<Coordinate>>();
            newType        = "(Unspecified Polygon)";
            newName        = "New Polygon";            
            
            if (coordinates.size() > 2) {
                //try to use the selection order made by the user
                userSelection = new CoordinateList<Coordinate>();

                if (vectorObjects.getMapPoints().size() == objectsToMerge.size()) {
                    for (int i = 0; i < objectsToMerge.size(); i++) 
                        userSelection.add(objectsToMerge.get(i).getCoordinateList().get(0));

                    verticesCross = testPolygonCoordinates(userSelection);
                } else if (vectorObjects.getLineStrings().size() == objectsToMerge.size()) {                    
                    VectorObjectList<VectorObject> c = vectorObjects.getFullCopy();
                    c.remove(0);
                    VectorObjectList<VectorObject> line = MergeToLineString.mergeLineStringToBestMatch((LineString) objectsToMerge.get(0), c);                                   
                    verticesCross = false;
                    userSelection.addAll(line.get(0).getCoordinateList());
                    newName = line.get(0).getName();
                } else {
                    for (int i = 0; i < objectsToMerge.size(); i++) {
                        userSelection.addAll(objectsToMerge.get(i).getCoordinateList());
                    }
                    
                    if (userSelection.size() < 25) {
                        verticesCross = testPolygonCoordinates(userSelection);
                    } else {
                        //too many points to try and check
                        verticesCross = false;
                    }
                }

                if (verticesCross == false) {
                    newObject = new Polygon(newName, newType, userSelection);
                } else {
                    //create polygon vertices
                    polygons = getNextCoordinate(coordinates, 0);

                    for (CoordinateList<Coordinate> possiblePolygon: polygons) {
                        verticesCross = testPolygonCoordinates(possiblePolygon);

                        if (verticesCross == false) {
                            validPolygons.add(possiblePolygon);
                        }
                    }

                    //just select the first one for now
                    newObject = new Polygon(newName, newType, validPolygons.get(0));
                }                
                
                for (VectorObject oldObject: vectorObjects) {
                    parentLayer = (VectorLayer) oldObject.getParentLayer();                    
                    index       = parentLayer.getObjectList().getIndexOf(oldObject);
                    
                    parentLayer.getObjectList().remove(oldObject);                   
                }

                if (index >= 0) {
                    if (index < parentLayer.getObjectList().size()) {
                        parentLayer.addObject(newObject, index);
                    } else {
                        parentLayer.addObject(newObject);
                    }
                } else {
                    parentLayer.addObject(newObject);
                }
                
                if (objectsToMerge.size() == 1) {
                    VectorObject mObject = (VectorObject) objectsToMerge.get(0);
                    
                    if (mObject instanceof LineString) {
                        lineString = (LineString) mObject;
                        
                        if (lineString.getObjectClass().equals("Coastline")) {
                            newObject.setClass("Lake");      
                        } else if (lineString.getObjectClass().equals("Water Way - Stream")) {
                            newObject.setClass("Lake");       
                        } else if (lineString.getObjectClass().equals("Water Way - River")) {
                            newObject.setClass("Lake");                              
                        }
                    }
                    
                    newObject.setName(mObject.getName());
                    newObject.setCustomDataFields(mObject.getCustomDataFields());
                }                
                
            } //end size check
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in MergeToPolygon.execute() - " + e);
        }        
        
        newObject.updateOutlines(mapData.getTheme());
        
        mapData.deselectObjects();
        mapData.setSelected(newObject);
        
        if (updateable != null)
            updateable.update();        
    }
    
    @Override
    public void undo() {
        VectorLayer parentLayer;
        try {
            parentLayer = (VectorLayer) newObject.getParentLayer();
            parentLayer.getObjectList().remove(newObject);
            
            for (MapObject oldObject: objectsToMerge) {
                parentLayer = (VectorLayer) oldObject.getParentLayer();
                parentLayer.addObject((VectorObject) oldObject);
            }            

            mapData.setSelected(objectsToMerge);
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in CommandMergeToPolygon.execute: " + e);
        }
    }
    
    private ArrayList<CoordinateList<Coordinate>> getNextCoordinate(CoordinateList<Coordinate> coordinates, int startIndex) {
        ArrayList<CoordinateList<Coordinate>> returnResult, recursiveResult;
        CoordinateList<Coordinate>            coordinatesCopy, newCoordinates, newCoordinatesCopy;

        newCoordinates = new CoordinateList<Coordinate>();
        returnResult   = new ArrayList<CoordinateList<Coordinate>>();

        if (startIndex < coordinates.size()) {
            newCoordinates.add(coordinates.remove(startIndex));

            if (coordinates.size() > 1) {
                for (int i = 0; i < coordinates.size(); i++) {
                    coordinatesCopy = (CoordinateList<Coordinate>) coordinates.clone();
                    recursiveResult = getNextCoordinate(coordinatesCopy, i);

                    for (CoordinateList<Coordinate> listResult: recursiveResult) {
                        newCoordinatesCopy = (CoordinateList<Coordinate>) newCoordinates.clone();
                        newCoordinatesCopy.addAll(listResult);
                        returnResult.add(newCoordinatesCopy);
                    }
                }
            } else {
                newCoordinates.add(coordinates.remove(0));
                returnResult.add(newCoordinates);
            }
        }

        return returnResult;
    }    
    
    /**
     * Tests to see if vertices cross
     * @param polygonCoordinates
     * @return 
     */
    private boolean testPolygonCoordinates(CoordinateList<Coordinate> polygonCoordinates) {
        ArrayList<Line2D> vertices;
        boolean           verticesCross;
        Coordinate        c1, c2;
        Point2D           p1, p2;

        if (polygonCoordinates.size() > 2) {
            vertices = new ArrayList<Line2D>(0);

            for (int i = 0; i < polygonCoordinates.size(); i++) {
                c1 = polygonCoordinates.get(i);

                if ((i + 1) < polygonCoordinates.size()) {
                    c2 = polygonCoordinates.get(i+1);
                } else {
                    c2 = polygonCoordinates.get(0);
                }

                p1 = new Point2D.Double(mapView.getX(c1, MapView.NO_WRAP), mapView.getY(c1));
                p2 = new Point2D.Double(mapView.getX(c2, MapView.NO_WRAP), mapView.getY(c2));

                vertices.add(new Line2D.Double(p1, p2));
            }

            verticesCross = CoordinateMath.testPolygonVertices(vertices);
        } else {
            verticesCross = true;
        }

        return verticesCross;
    }    
}
