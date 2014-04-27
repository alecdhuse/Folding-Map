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

import co.foldingmap.map.vector.MultiGeometry;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.CoordinateList;
import co.foldingmap.map.vector.CoordinateMath;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.LineString;
import co.foldingmap.map.vector.MapPoint;
import co.foldingmap.map.vector.Polygon;
import co.foldingmap.GUISupport.Updateable;
import co.foldingmap.Logger;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.Layer;
import co.foldingmap.map.MapView;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Merges a combination of points and LineStrings into a single LineStirng
 * 
 * @author Alec
 */
public class MergeToLineString extends Action {    
    protected boolean                           allObjectsArePoints;
    protected DigitalMap                        mapData;
    protected VectorObject                      newObject;
    protected VectorObjectList<VectorObject>    objectsToMerge, pointsToMerge;
    protected VectorObjectList<VectorObject>    originalObjects;
    protected MapView                           mapView;
    protected Updateable                        updateable;
    
    public MergeToLineString(DigitalMap mapData, Updateable updateable) {        
        this.mapData            = mapData;
        this.commandDescription = "Merge To LineString";  
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
        boolean                         verticesCross;
        CoordinateList<Coordinate>      mergedCoordinates, userSelection;
        int                             currentObjectNumberOfPoints, greatestNumberOfPoints;  
        VectorObject                    largestMapObject;
        VectorLayer                     parentLayer;        
        
        this.allObjectsArePoints = false;
        this.pointsToMerge       = new VectorObjectList<VectorObject>();     
        this.newObject           = null;        
        
        try {         
            objectsToMerge      = new VectorObjectList<VectorObject>(mapData.getSelectedObjects());
            originalObjects     = (objectsToMerge.getFullCopy());
        
            allObjectsArePoints = (objectsToMerge.getMapPoints().size() == objectsToMerge.size());         
            mapView             = mapData.getLastMapView();
            verticesCross       = true;
            userSelection       = new CoordinateList<Coordinate>();

            if (objectsToMerge.size() > 1) {                
                if (allObjectsArePoints) {

                    for (int i = 0; i < objectsToMerge.size(); i++)
                        userSelection.add(objectsToMerge.get(i).getCoordinateList().get(0));

                    verticesCross = CoordinateMath.testPolygonVertices(constructVertices(userSelection));                    
                } else if (objectsToMerge.getLineStrings().size() == objectsToMerge.size()) {
                    //All Objects are LineStrings
                    newObject = mergeFromLineStrings(objectsToMerge);
                    verticesCross = false;
                    
                    for (int i = 0; i < objectsToMerge.size(); i++) {
                        VectorObject tempMapObject = objectsToMerge.get(i);                        
                        newObject.setCustomDataFields(tempMapObject.getCustomDataFields());
                    }
                }

                //Temp Debug Code
                int i3 = 0;
                for (VectorObject vObj: objectsToMerge) 
                    i3 += vObj.getCoordinateList().size();

                if (newObject.getCoordinateList().size() < i3 - 1) 
                    newObject = mergeFromLineStrings(objectsToMerge);                             
                
                if (verticesCross) {

                    greatestNumberOfPoints = 0;
                    largestMapObject       = objectsToMerge.get(0);

                    //remove old objects and decide which object has the most points, use the info from that object in the new object.
                    for (int i = 0; i < objectsToMerge.size(); i++) {
                        VectorObject currentMapObject = objectsToMerge.get(i);
                        parentLayer = (VectorLayer) currentMapObject.getParentLayer();
                        currentObjectNumberOfPoints = currentMapObject.getCoordinateList().size();

                        if (currentObjectNumberOfPoints > greatestNumberOfPoints) {
                            greatestNumberOfPoints = currentObjectNumberOfPoints;
                            largestMapObject       = currentMapObject;
                        }

                        mapData.getSelectedObjects().remove(currentMapObject);
                        parentLayer.getObjectList().remove(currentMapObject);                      
                    }

                    if (largestMapObject instanceof LineString) {
                        newObject         = (VectorObject) largestMapObject.copy();
                        mergedCoordinates = MergeFunctions.getCoordinatesForMerge(this.objectsToMerge);
                        newObject.setCoordinateList(mergedCoordinates);                 
                    } else {
                        mergedCoordinates = MergeFunctions.getCoordinatesForMerge(this.objectsToMerge);
                        newObject = new LineString(largestMapObject.getName(), "(Unspecified Linestring)", mergedCoordinates);
                        newObject.setDescription(largestMapObject.getDescription());
                        newObject.setCustomDataFields(largestMapObject.getCustomDataFields());
                    }

                    parentLayer   = (VectorLayer) largestMapObject.getParentLayer();
                    parentLayer.addObject(newObject);
                    mapData.setSelected(newObject);
                   
                } else {
                    if (newObject == null) {
                        //use user selecion to merge
                        newObject = new LineString("New Line", "(Unspecified Linestring)", userSelection);
                        newObject.setDescription("");
                        newObject.setCustomDataFields(objectsToMerge.get(0).getCustomDataFields());
                    }
                              
                    parentLayer = (VectorLayer) objectsToMerge.get(0).getParentLayer();                     
                    
                    for (VectorObject vObj: objectsToMerge) {
                        VectorLayer l = (VectorLayer) vObj.getParentLayer();
                        l.removeObject(vObj);
                    }                                                                  
                    
                    parentLayer.addObject(newObject);
                }
            } else if (objectsToMerge.size() == 1) {
                VectorObject currentObject = objectsToMerge.get(0);
                
                if (currentObject instanceof MultiGeometry) {
                    MultiGeometry            currentMulti    = (MultiGeometry) currentObject;
                    MultiGeometry            newMulti        = new MultiGeometry(currentObject.getName());
                    VectorObjectList<VectorObject> componetObjects = currentMulti.getComponentObjects();
                    
                    for (int i = 0; i < componetObjects.size(); i++) {
                        VectorObject currentSubObject = componetObjects.get(i);
                        newObject = new LineString(currentObject.getName(), "(Unspecified Linestring)", currentSubObject.getCoordinateList());
                        newMulti.addObject(newObject);
                    }
                    
                    newObject   = newMulti;
                    parentLayer = (VectorLayer) objectsToMerge.get(0).getParentLayer();
                    parentLayer.addObject(newObject);
                    mapData.setSelected(newObject);
                    parentLayer.getObjectList().removeAll(objectsToMerge);  
                    mapData.getSelectedObjects().removeAll(objectsToMerge);
                } else {                             
                    String newType = objectsToMerge.get(0).getName();
                    
                    if (objectsToMerge.get(0) instanceof Polygon) {
                        Polygon p   = (Polygon) objectsToMerge.get(0);
                        String type = p.getObjectClass();
                        
                        if (type.equals("Lake") || type.equals("Small Island") || type.equals("River") ) {
                            newType = "Coastline";
                        }
                    } else if (currentObject instanceof LineString) {
                        //For LineStrings, LinearRings and other types with LineString Parent.
                        newType = currentObject.getObjectClass();
                    }
                    
                    newObject = new LineString(currentObject.getName(), newType, currentObject.getCoordinateList());                
                    newObject.setCustomDataFields(objectsToMerge.get(0).getCustomDataFields());

                    parentLayer = (VectorLayer) objectsToMerge.get(0).getParentLayer();
                    parentLayer.removeObject(objectsToMerge.get(0));
                            
                    if (parentLayer == null)
                        parentLayer = mapData.getVectorLayer();
                    
                    parentLayer.addObject(newObject);
                }
            } else {
                Logger.log(Logger.WARN, "Error in CommandMergeToLineString.execute() - No objects to merge");
            }                        
            
            //Clear the selected Objects
            mapData.deselectObjects();;
            mapData.setSelected(newObject);

            if (updateable != null)
                updateable.update();       
            
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in CommandMergeToLineString.execute() - " + e);
        }        
    }

    @Override
    public void undo() {
        Layer        parentLayer;
        VectorLayer  vectorLayer;

        try {
            //remove the newly created, merged objects
            parentLayer = newObject.getParentLayer();
            
            if (parentLayer instanceof VectorLayer) {
                vectorLayer = (VectorLayer) parentLayer;            
                vectorLayer.getObjectList().remove(newObject);
            }

            //restore the original objects to their original layers
            for (int i = 0; i < originalObjects.size(); i++) {
                VectorObject currentMapObject = (VectorObject) originalObjects.get(i);
                parentLayer = currentMapObject.getParentLayer();
                
                if (parentLayer instanceof VectorLayer) {
                    vectorLayer = (VectorLayer) parentLayer;
                    vectorLayer.addObject(currentMapObject);
                }
            }

            mapData.setSelected(originalObjects);
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in CommandMergeToLineString.undo() - " + e);
        }
    }
    
    private ArrayList<Line2D> constructVertices(CoordinateList<Coordinate> coordinates) {
        ArrayList<Line2D> vertices;
        Point2D           p1, p2;

        vertices = new ArrayList<Line2D>();

        for (int i = 0; i < (coordinates.size() - 1); i += 2) {
            p1 = new Point2D.Double(mapView.getX(coordinates.get(i),   MapView.NO_WRAP), mapView.getY(coordinates.get(i)));
            p2 = new Point2D.Double(mapView.getX(coordinates.get(i+1), MapView.NO_WRAP), mapView.getY(coordinates.get(i+1)));

            vertices.add(new Line2D.Double(p1, p2));
        }

        return vertices;
    }    
    
    public VectorObjectList<VectorObject> mergeConnectedLineStrings(VectorObjectList<VectorObject> objectsToMerge) {
        CoordinateList<Coordinate>  compareCoordinates, currentCoordinates, newLineStringCoordinates;
        Coordinate                  compareFirstCoordinate, compareLastCoordinate, currentFirstCoordinate, currentLastCoordinate;
        LineString                  compareLineString, currentLineStirng, newLineString;
        VectorObjectList<VectorObject>    updatedObjects;

        updatedObjects = new VectorObjectList<VectorObject>();
        updatedObjects.addAll(objectsToMerge);

        try {
            for (int i = 0; i < objectsToMerge.size(); i++) {
                currentLineStirng      = (LineString) objectsToMerge.get(i);
                currentFirstCoordinate = currentLineStirng.getCoordinateList().get(0);
                currentLastCoordinate  = currentLineStirng.getCoordinateList().lastCoordinate();

                if (updatedObjects.size() > 1) {
                    for (int j = 0; j < objectsToMerge.size(); j++) {
                        compareLineString      = (LineString) objectsToMerge.get(j);
                        compareFirstCoordinate = compareLineString.getCoordinateList().get(0);
                        compareLastCoordinate  = compareLineString.getCoordinateList().lastCoordinate();

                        if (currentLineStirng != compareLineString) {
                            if (currentFirstCoordinate.equals(compareFirstCoordinate)) {
                                compareCoordinates       = compareLineString.getCoordinateList();
                                currentCoordinates       = currentLineStirng.getCoordinateList();
                                newLineStringCoordinates = new CoordinateList<Coordinate>();

                                newLineStringCoordinates.addAll(compareCoordinates.getReverse());
                                newLineStringCoordinates.addAll(currentCoordinates);
                                updatedObjects.remove(compareLineString);
                                updatedObjects.remove(currentLineStirng);
                                newLineString = new LineString((currentLineStirng.getName() + "-" + compareLineString.getName()), currentLineStirng.getObjectClass(), newLineStringCoordinates);
                                updatedObjects.add(newLineString);
                                break;
                            } else if (currentFirstCoordinate.equals(compareLastCoordinate)) {
                                compareCoordinates       = compareLineString.getCoordinateList();
                                currentCoordinates       = currentLineStirng.getCoordinateList();
                                newLineStringCoordinates = new CoordinateList<Coordinate>();

                                newLineStringCoordinates.addAll(compareCoordinates);
                                newLineStringCoordinates.addAll(currentCoordinates);
                                updatedObjects.remove(compareLineString);
                                updatedObjects.remove(currentLineStirng);
                                newLineString = new LineString((currentLineStirng.getName() + "-" + compareLineString.getName()), currentLineStirng.getObjectClass(), newLineStringCoordinates);
                                updatedObjects.add(newLineString);
                                break;
                            } else if (currentLastCoordinate.equals(compareFirstCoordinate)) {
                                compareCoordinates       = compareLineString.getCoordinateList();
                                currentCoordinates       = currentLineStirng.getCoordinateList();
                                newLineStringCoordinates = new CoordinateList<Coordinate>();

                                newLineStringCoordinates.addAll(currentCoordinates);
                                newLineStringCoordinates.addAll(compareCoordinates);
                                updatedObjects.remove(compareLineString);
                                updatedObjects.remove(currentLineStirng);
                                newLineString = new LineString((currentLineStirng.getName() + "-" + compareLineString.getName()), currentLineStirng.getObjectClass(), newLineStringCoordinates);
                                updatedObjects.add(newLineString);
                                break;
                            } else if (currentLastCoordinate.equals(compareLastCoordinate)) {
                                compareCoordinates       = compareLineString.getCoordinateList();
                                currentCoordinates       = currentLineStirng.getCoordinateList();
                                newLineStringCoordinates = new CoordinateList<Coordinate>();

                                newLineStringCoordinates.addAll(currentCoordinates);
                                newLineStringCoordinates.addAll(compareCoordinates.getReverse());
                                updatedObjects.remove(compareLineString);
                                updatedObjects.remove(currentLineStirng);
                                newLineString = new LineString((currentLineStirng.getName() + "-" + compareLineString.getName()), currentLineStirng.getObjectClass(), newLineStringCoordinates);
                                updatedObjects.add(newLineString);
                                break;
                            }
                        }

                    } // end for j loop
                }//end size check
            } // end for i loop
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in CommanMergeToLineString.mergeConnectedLineStrings(MapObjectList) - " + e);
        }

        return updatedObjects;
    }
    
    public LineString mergeFromLineStrings(VectorObjectList<VectorObject> objectsToMerge) {
        LineString                       currentLineStirng;
        VectorObjectList<VectorObject>   updatedObjects;

        updatedObjects = new VectorObjectList<VectorObject>();
        updatedObjects.addAll(objectsToMerge);

        try {
            while (updatedObjects.size() > 1) {
                currentLineStirng = (LineString) updatedObjects.remove(0);                
                updatedObjects    = mergeLineStringToBestMatch(currentLineStirng, updatedObjects);
            }


        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in CommanMergeToLineString.mergeFromLineStrings(MapObjectList) - " + e);
        }

        return (LineString) updatedObjects.get(0);
    }
    
    //will merge a LineString to the object closest to one of it's endpoints
    public static VectorObjectList<VectorObject> mergeLineStringToBestMatch(LineString lineStringToMerge, VectorObjectList<VectorObject> objects) {
        CoordinateList<Coordinate>      lineStringCoordinates, newlineStringCoordinates;
        Coordinate                      lineStringFirstCoordinate, lineStringLastCoordinate;
        double                          closestDistance, currentDistance;
        LineString                      newLineStirng;
        String                          objectClass;
        VectorObject                    bestMatch, currentObject;
        VectorObjectList<VectorObject>  updatedObjects;

        bestMatch                 = null;
        closestDistance           = Float.MAX_VALUE;
        lineStringCoordinates     = lineStringToMerge.getCoordinateList();
        lineStringFirstCoordinate = lineStringToMerge.getCoordinateList().get(0);
        lineStringLastCoordinate  = lineStringToMerge.getCoordinateList().lastCoordinate();
        newLineStirng             = null;
        updatedObjects            = new VectorObjectList<VectorObject>();                
        updatedObjects.addAll(objects);

        for (int i = 0; i < objects.size(); i++) {
            currentObject = objects.get(i);
            objectClass   = (lineStringToMerge.getObjectClass().length() == 0 ? currentObject.getObjectClass() : lineStringToMerge.getObjectClass());
            objectClass   = (objectClass == null ? objectClass = "(Unspecified Linestring)" : objectClass);
            
            if (currentObject instanceof MapPoint) {
                currentDistance = CoordinateMath.getDistance(currentObject.getCoordinateList().get(0), lineStringFirstCoordinate);
                if (currentDistance < closestDistance) {
                    closestDistance = currentDistance;
                    bestMatch       = currentObject;

                    newlineStringCoordinates = new CoordinateList<Coordinate>();
                    newlineStringCoordinates.add(currentObject.getCoordinateList().get(0));
                    newlineStringCoordinates.addAll(lineStringCoordinates);
                    newLineStirng = new LineString(lineStringToMerge.getName(), objectClass, newlineStringCoordinates);
                }

                currentDistance = CoordinateMath.getDistance(currentObject.getCoordinateList().get(0), lineStringLastCoordinate);
                if (currentDistance < closestDistance) {
                    closestDistance = currentDistance;
                    bestMatch       = currentObject;

                    newlineStringCoordinates = new CoordinateList<Coordinate>();
                    newlineStringCoordinates.addAll(lineStringCoordinates);
                    newlineStringCoordinates.add(currentObject.getCoordinateList().get(0));
                    newLineStirng = new LineString(lineStringToMerge.getName(), objectClass, newlineStringCoordinates);
                }
            } else if (currentObject instanceof LineString) {
                CoordinateList<Coordinate> compareCoordinates = currentObject.getCoordinateList();
                Coordinate                 compareFirst       = currentObject.getCoordinateList().get(0);
                Coordinate                 compareLast        = currentObject.getCoordinateList().lastCoordinate();

                currentDistance = CoordinateMath.getDistance(compareFirst, lineStringFirstCoordinate);
                if (currentDistance < closestDistance) {
                    closestDistance = currentDistance;
                    bestMatch       = currentObject;
                    newlineStringCoordinates = new CoordinateList<Coordinate>();

                    newlineStringCoordinates.addAll(compareCoordinates.getReverse());
                    newlineStringCoordinates.addAll(lineStringCoordinates);
                    newLineStirng = new LineString(lineStringToMerge.getName(), objectClass, newlineStringCoordinates);
                }

                currentDistance = CoordinateMath.getDistance(compareLast, lineStringFirstCoordinate);
                if (currentDistance < closestDistance) {
                    closestDistance          = currentDistance;
                    bestMatch                = currentObject;
                    newlineStringCoordinates = new CoordinateList<Coordinate>();

                    newlineStringCoordinates.addAll(compareCoordinates);
                    newlineStringCoordinates.addAll(lineStringCoordinates);
                    newLineStirng = new LineString(lineStringToMerge.getName(), objectClass, newlineStringCoordinates);
                }

                currentDistance = CoordinateMath.getDistance(compareFirst, lineStringLastCoordinate);
                if (currentDistance < closestDistance) {
                    closestDistance          = currentDistance;
                    bestMatch                = currentObject;
                    newlineStringCoordinates = new CoordinateList<Coordinate>();

                    newlineStringCoordinates.addAll(lineStringCoordinates);
                    newlineStringCoordinates.addAll(compareCoordinates);
                                        
                    newLineStirng = new LineString(lineStringToMerge.getName(), objectClass, newlineStringCoordinates);
                }

                currentDistance = CoordinateMath.getDistance(compareLast, lineStringLastCoordinate);
                if (currentDistance < closestDistance) {
                    closestDistance          = currentDistance;
                    bestMatch                = currentObject;
                    newlineStringCoordinates = new CoordinateList<Coordinate>();
                 
                    if (CoordinateMath.getDistance(lineStringCoordinates.lastCoordinate(), compareCoordinates.get(0)) == currentDistance) {
                        newlineStringCoordinates.addAll(lineStringCoordinates);
                        newlineStringCoordinates.addAll(compareCoordinates);
                    } else {
                        newlineStringCoordinates.addAll(lineStringCoordinates);
                        newlineStringCoordinates.addAll(compareCoordinates.getReverse());
                    }


                    newLineStirng = new LineString(lineStringToMerge.getName(), objectClass, newlineStringCoordinates);
                }
            } else {
                //Polygon or LineString
            }

        } // end for loop

        if ((bestMatch != null) && (newLineStirng != null)) {
            updatedObjects.remove(bestMatch);
            updatedObjects.add(newLineStirng);
        } else {
            updatedObjects.add(lineStringToMerge);
        }


        return updatedObjects;
    }
    
}
