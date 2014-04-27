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
package co.foldingmap.map.vector;

import co.foldingmap.Logger;
import co.foldingmap.map.MapObjectList;
import java.io.Serializable;
import java.util.*;

/**
 * This class is used to keep a collection of VectorObject and provide efficient
 * methods to store and retrieve them.
 * 
 * @author alecdhuse
 */
public class VectorObjectList<VectorObject> extends AbstractList<VectorObject>
                                            implements List<VectorObject>, 
                                                       Cloneable, 
                                                       Serializable, 
                                                       RandomAccess {
 
    private transient int               firstIndex;
    private transient int               lastIndex;
    private transient VectorObject[]    array;    
          
    protected ArrayList<LineString>     lineStrings;
    protected ArrayList<MapPoint>       points;
    protected ArrayList<MultiGeometry>  multiGeometries;
    protected ArrayList<Polygon>        polygons;
    
    /**
     * Constructs a new instance of VectorObjectList with the an initial capacity
     * of 10.
     * 
     */
    public VectorObjectList() {
        this(10);
    }
    
    /**
     * Constructs a new instance of VectorObjectList with the specified capacity.
     * 
     * @param capacity
     *            the initial capacity of this VectorObjectList.
     */
    public VectorObjectList(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException();
        }

        firstIndex = lastIndex = 0;
        array = newElementArray(capacity);
        
        lineStrings     = new ArrayList<LineString>();
        multiGeometries  = new ArrayList<MultiGeometry>();
        points          = new ArrayList<MapPoint>();
        polygons        = new ArrayList<Polygon>();        
    } 
    
    /**
     * Creates a new VectorObjectList from another collection of VectorObjects.
     * 
     * @param collection 
     */
    public VectorObjectList(Collection<? extends VectorObject> collection) {
        this(collection.size());
        
        Object[] dumpArray = collection.toArray();

        if (dumpArray.length != 0) {

            if (dumpArray.length > array.length - lastIndex) {
                growAtEnd(dumpArray.length);
            }

            System.arraycopy(dumpArray, 0, this.array, lastIndex, dumpArray.length);
            lastIndex += dumpArray.length;
            modCount++;

            for (int i = 0; i < collection.size(); i++) {
                VectorObject object = (VectorObject) dumpArray[i];

                if (object instanceof MapPoint) {
                    points.add((MapPoint) object);
                } else if (object instanceof LineString) {
                    lineStrings.add((LineString) object);
                } else if (object instanceof Polygon) {
                    polygons.add((Polygon) object);
                } else if (object instanceof MultiGeometry) {    
                    multiGeometries.add((MultiGeometry) object);
                }            
            }
        }
    }    
    
    
    public VectorObjectList(MapObjectList list) {
        this(list.size());
        
        Object[] dumpArray = list.toArray();

        if (dumpArray.length != 0) {

            if (dumpArray.length > array.length - lastIndex) {
                growAtEnd(dumpArray.length);
            }

            System.arraycopy(dumpArray, 0, this.array, lastIndex, dumpArray.length);
            lastIndex += dumpArray.length;
            modCount++;

            for (int i = 0; i < list.size(); i++) {
                VectorObject object = (VectorObject) dumpArray[i];

                if (object instanceof MapPoint) {
                    points.add((MapPoint) object);
                } else if (object instanceof LineString) {
                    lineStrings.add((LineString) object);
                } else if (object instanceof Polygon) {
                    polygons.add((Polygon) object);
                } else if (object instanceof MultiGeometry) {    
                    multiGeometries.add((MultiGeometry) object);
                }            
            }
        }        
    }
    
    /**
     * Inserts the specified object into this {@code VectorObjectList} at the 
     * specified location. The object is inserted before any previous element 
     * at the specified location. If the location is equal to the size of this
     * {@code ArrayList}, the object is added at the end.
     * 
     * @param location
     *            the index at which to insert the object.
     * @param object
     *            the object to add.
     * @throws IndexOutOfBoundsException
     *             when {@code location < 0 || > size()}
    */
    @Override
    public void add(int location, VectorObject object) {
        int size = lastIndex - firstIndex;

        if (0 < location && location < size) {
            if (firstIndex == 0 && lastIndex == array.length) {
                growForInsert(location, 1);
            } else if ((location < size / 2 && firstIndex > 0) || lastIndex == array.length) {
                System.arraycopy(array, firstIndex, array, --firstIndex, location);
            } else {
                int index = location + firstIndex;
                System.arraycopy(array, index, array, index + 1, size - location);
                lastIndex++;
            }

            array[location + firstIndex] = object;
        } else if (location == 0) {
            if (firstIndex == 0) {
                growAtFront(1);
            }

            array[--firstIndex] = object;
        } else if (location == size) {
            if (lastIndex == array.length) {
                growAtEnd(1);
            }

            array[lastIndex++] = object;
        } else {
            throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(location) + " Size: " + Integer.valueOf(lastIndex - firstIndex));
        }

        if (object instanceof MapPoint) {
            points.add((MapPoint) object);
        } else if (object instanceof LineString) {
            lineStrings.add((LineString) object);
        } else if (object instanceof Polygon) {
            polygons.add((Polygon) object);
        } else if (object instanceof MultiGeometry) {    
            multiGeometries.add((MultiGeometry) object);
        } 
        
        modCount++;
    }    
    
    /**
     * Adds the specified object at the end of this VectorObjectList unless an
     * instance already exists in the list.
     *
     * @param object
     *          The VectorObject to add.
     * @return 
     *          If the object was added or not.
     */
    @Override
    public boolean add(VectorObject newObject) {
        try {
            boolean instanceFound = false;            

            if (firstIndex != lastIndex) {
                //check to see if the coordinate exists already
                for (int i = firstIndex; i < lastIndex; i++) {
                    if (array[i].equals(newObject)) {
                        instanceFound = true;
                        break;
                    }
                } //end for loop
            }

            if (instanceFound == false) {
                forceAdd(newObject);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error in VectorObjectList.add() - " + e);
            return false;
        }
    }     
    
    /**
     * Adds the objects in the specified collection to this 
     * {@code VectorObjectList}.
     *
     * @param collection
     *            the collection of objects.
     * @return {@code true} if this {@code VectorObjectList} is modified, 
     *          {@code false} otherwise.
    */
    @Override
    public boolean addAll(Collection<? extends VectorObject> collection) {
        Object[] dumpArray = collection.toArray();

        if (dumpArray.length == 0) {
            return false;
        }

        if (dumpArray.length > array.length - lastIndex) {
            growAtEnd(dumpArray.length);
        }

        System.arraycopy(dumpArray, 0, this.array, lastIndex, dumpArray.length);
        lastIndex += dumpArray.length;
        modCount++;

        for (int i = 0; i < collection.size(); i++) {
            VectorObject object = (VectorObject) dumpArray[i];
            
            if (object instanceof MapPoint) {
                points.add((MapPoint) object);
            } else if (object instanceof LineString) {
                lineStrings.add((LineString) object);
            } else if (object instanceof Polygon) {
                polygons.add((Polygon) object);
            } else if (object instanceof MultiGeometry) {    
                multiGeometries.add((MultiGeometry) object);
            }           
        }
        
        return true;
    }        
    
    /**
     * Returns a new {@code VectorObjectList} with the same elements, the same 
     * size and the same capacity as this {@code VectorObjectList}.
     *
     * @return a shallow copy of this {@code VectorObjectList}
     * @see java.lang.Cloneable
    */
    @Override
    @SuppressWarnings("unchecked")
    public VectorObjectList clone() {
        try {
            VectorObjectList<VectorObject> newList = (VectorObjectList<VectorObject>) super.clone();
            newList.array = array.clone();
            return newList;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }    
    
    /**
     * Returns if the supplied VectorObject already exists in the list.
     * 
     * @param object
     *      The VectorObject to check.
     * 
     * @return If the VectorObject exist in the list or not.
     */
    public boolean contains(co.foldingmap.map.vector.VectorObject object) {
        boolean                             result = false;
        co.foldingmap.map.vector.VectorObject  currentObject;
                
        for (int i = firstIndex; i < lastIndex; i++) {
            currentObject = (co.foldingmap.map.vector.VectorObject) array[i];
            
            if (currentObject == object) {
                result = true;
                break;
            }                
        }
        
        return result;
    }   
    
    /**
     * Returns if this object is equal to another.
     * 
     * @param obj
     * @return 
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (obj instanceof VectorObjectList) {
            VectorObjectList vol = (VectorObjectList) obj;
            
            return (this.hashCode() == vol.hashCode());
        } else {
            return false;
        }
    }    
    
    /**
     * Removes objects that are not within the boundary and shapes objects that
     * are to be completely within it.
     * 
     * @param boundry
     * @return 
     */
    public VectorObjectList<co.foldingmap.map.vector.VectorObject> fitObjectsToBoundary(LatLonBox boundary) {
        boolean                                         includeObject, objectChanged;
        boolean                                         northEast, northWest, southEast, southWest;
        Coordinate                                      min, max, newCoordinate;
        Coordinate                                      c1, c2, c3;
        CoordinateList<Coordinate>                      objectCoordinates, newCoordinates;
        co.foldingmap.map.vector.VectorObject                 currentObject;
        VectorObjectList<co.foldingmap.map.vector.VectorObject>  fittedObjects;

        try {
            fittedObjects = new VectorObjectList<co.foldingmap.map.vector.VectorObject>();
            objectChanged = false;
            includeObject = false;
            
            min = new Coordinate(0, boundary.getNorth(), boundary.getWest());
            max = new Coordinate(0, boundary.getSouth(), boundary.getEast());
            
            for (int i = firstIndex; i < lastIndex; i++) {
                objectChanged     = false;
                includeObject     = false; 
                northEast         = false;
                northWest         = false;
                southEast         = false;
                southWest         = false;
                currentObject     = (co.foldingmap.map.vector.VectorObject) array[i];
                objectCoordinates = currentObject.getCoordinateList();
                newCoordinates    = new CoordinateList<Coordinate>();
                
                for (Coordinate c: objectCoordinates) {
                    if (boundary.contains(c)) {
                        newCoordinates.add(c);
                        includeObject = true;
                    } else {
                        objectChanged = true;
                        
                        if ((c.isSouthOf(min)) && (c.isNorthOf(max))) {
                            if ((c.isEastOf(max)) && c.isEastOf(min)) {
                                //Create a new coordinate from the old with the longitude set to the East boundary.
                                newCoordinate = new Coordinate(c.getAltitude(), c.getLatitude(), boundary.getEast());
                                
                                if (boundary.contains(newCoordinate)) {
                                    newCoordinates.add(newCoordinate); 
                                    
                                    if (newCoordinates.size() > 2) {
                                        c1 = newCoordinates.get(newCoordinates.size() - 2);
                                        c2 = newCoordinates.get(newCoordinates.size() - 3);
                                        
                                        if ((c1.longitude == c2.longitude) && (c2.longitude == newCoordinate.longitude)) {
                                            //The last three corrdinates lay on the same longitude remove the center one
                                            if (! c2.isShared()) newCoordinates.remove(c1);
                                        }
                                    }
                                }
                            } else if (c.isWestOf(min, 90) && c.isWestOf(max, 90)) {
                                //Create a new coordinate from the old with the longitude set to the West boundary.
                                newCoordinate = new Coordinate(c.getAltitude(), c.getLatitude(), boundary.getWest());
                                
                                if (boundary.contains(newCoordinate)) {
                                    newCoordinates.add(newCoordinate); 
                                    
                                    if (newCoordinates.size() > 2) {
                                        c1 = newCoordinates.get(newCoordinates.size() - 2);
                                        c2 = newCoordinates.get(newCoordinates.size() - 3);
                                        
                                    if ((c1.longitude == c2.longitude) && (c2.longitude == newCoordinate.longitude)) {
                                            //The last three corrdinates lay on the same longitude remove the center one
                                            if (! c2.isShared()) newCoordinates.remove(c1);
                                        }
                                    }
                                }
                            }
                        } else if ((c.isEastOf(min)) && (c.isEastOf(max))) {
                            if ((c.isSouthOf(max)) && (c.isSouthOf(min))) {
                                //Create a new coordinate from the old with the latitude set to the South boundary.
                                newCoordinate = new Coordinate(c.getAltitude(), (boundary.getSouth()), c.getLongitude());
                                
                                if (boundary.contains(newCoordinate)) {
                                    newCoordinates.add(newCoordinate); 
                                    
                                    if (newCoordinates.size() > 2) {
                                        c1 = newCoordinates.get(newCoordinates.size() - 2);
                                        c2 = newCoordinates.get(newCoordinates.size() - 3);
                                        
                                        if ((c1.latitude == c2.latitude) && (c2.latitude == newCoordinate.latitude)) {
                                            //The last three corrdinates lay on the same latitude remove the center one
                                            if (! c2.isShared()) newCoordinates.remove(c1);
                                        }
                                    }
                                }
                            } else if ((c.isNorthOf(min)) && (c.isNorthOf(max))) {
                                //Create a new coordinate from the old with the latitude set to the North boundary.
                                newCoordinate = new Coordinate(c.getAltitude(), boundary.getNorth(), c.getLongitude());
                                
                                if (boundary.contains(newCoordinate)) {
                                    newCoordinates.add(newCoordinate); 
                                    
                                    if (newCoordinates.size() > 2) {
                                        c1 = newCoordinates.get(newCoordinates.size() - 2);
                                        c2 = newCoordinates.get(newCoordinates.size() - 3);
                                        
                                        if ((c1.latitude == c2.latitude) && (c2.latitude == newCoordinate.latitude)) {
                                            //The last three corrdinates lay on the same latitude remove the center one
                                            if (! c2.isShared()) newCoordinates.remove(c1);
                                        }
                                    }
                                }
                            }
                        } else if ((c.isEastOf(min)) && (c.isWestOf(max, 90))) {   
                            if (c.isSouthOf(max)) {
                                //Create a new coordinate from the old with the latitude set to the South boundary.
                                newCoordinate = new Coordinate(c.getAltitude(), boundary.getSouth(), c.getLongitude());
                                                         
                                if (boundary.contains(newCoordinate)) {
                                    newCoordinates.add(newCoordinate); 
                                    
                                    if (newCoordinates.size() > 2) {
                                        c1 = newCoordinates.get(newCoordinates.size() - 2);
                                        c2 = newCoordinates.get(newCoordinates.size() - 3);
                                        
                                        if ((c1.latitude == c2.latitude) && (c2.latitude == newCoordinate.latitude)) {
                                            //The last three corrdinates lay on the same latitude remove the center one
                                            if (! c2.isShared()) newCoordinates.remove(c1);
                                        }
                                    }
                                } 
                            } else if (c.isNorthOf(min)) {
                                //Create a new coordinate from the old with the latitude set to the North boundary.
                                newCoordinate = new Coordinate(c.getAltitude(), boundary.getNorth(), c.getLongitude());
                                
                                if (boundary.contains(newCoordinate)) {
                                    newCoordinates.add(newCoordinate); 
                                    
                                    if (newCoordinates.size() > 2) {
                                        c1 = newCoordinates.get(newCoordinates.size() - 2);
                                        c2 = newCoordinates.get(newCoordinates.size() - 3);
                                        
                                        if ((c1.latitude == c2.latitude) && (c2.latitude == newCoordinate.latitude)) {
                                            //The last three corrdinates lay on the same longitude remove the center one
                                            if (! c2.isShared()) newCoordinates.remove(c1);
                                        }
                                    }
                                }
                            }                                                     
                        }
                        
                        //Ensures a point at the South-East edge of the crop
                        if (!southEast && includeObject) {
                            if (c.isSouthOf(max) && c.isEastOf(max)) {
                                southEast     = true;
                                newCoordinate = new Coordinate(c.getAltitude(), (boundary.getSouth()), boundary.getEast());
                                newCoordinates.add(newCoordinate);
                            }
                        }                        
                        
                        //Ensures a point at the South-West edge of the crop
                        if (!southWest && includeObject) {
                            if (c.isSouthOf(max) && c.isWestOf(min, 90)) {
                                southWest     = true;
                                newCoordinate = new Coordinate(c.getAltitude(), (boundary.getSouth()), boundary.getWest());
                                newCoordinates.add(newCoordinate);
                            }
                        }
                        
                        //Ensures a point at the North-East edge of the crop
                        if (!northEast && includeObject) {
                            if (c.isNorthOf(max) && c.isEastOf(max)) {
                                northEast     = true;
                                newCoordinate = new Coordinate(c.getAltitude(), boundary.getNorth(), boundary.getEast());
                                
                                if (currentObject.boundingBox.contains(newCoordinate))
                                    newCoordinates.add(newCoordinate);
                            }
                        }                          
                        
                        if (!northWest && includeObject) {
                            if (c.isNorthOf(min) && c.isWestOf(min, 90)) {
                                northWest     = true;
                                newCoordinate = new Coordinate(c.getAltitude(), boundary.getNorth(), boundary.getWest());
                                newCoordinates.add(newCoordinate);
                            }
                        }                                                                             
                    }
                }

                if (includeObject) {
                    if (!objectChanged) {
                        fittedObjects.add(currentObject);
                    } else {
                        if (newCoordinates.size() > 0) {
                            co.foldingmap.map.vector.VectorObject newObject = (co.foldingmap.map.vector.VectorObject) currentObject.copy();
                            newObject.setCoordinateList(newCoordinates);
                            fittedObjects.add(newObject);
                        } 
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error in VectorObjectCollection.fitObjectsToBoundary(LatLonAltBox) " + e);
            fittedObjects = null;
        }
        
        return fittedObjects;
    }    
    
    /**
     * Adds the specified object at the end of this VectorObjectList even if an
     * instance already exists in the list.
     *
     * @param object
     *            the VectorObject to add.
     * @return always true
     */
    public boolean forceAdd(VectorObject object) {
        try {
            if (lastIndex == array.length) {
                growAtEnd(3);
            }

            array[lastIndex++] = object;
            modCount++;

            if (object instanceof MapPoint) {
                points.add((MapPoint) object);
            } else if (object instanceof LineString) {
                lineStrings.add((LineString) object);
            } else if (object instanceof Polygon) {
                polygons.add((Polygon) object);
            } else if (object instanceof MultiGeometry) {    
                multiGeometries.add((MultiGeometry) object);
            }            
            
            return true;
        } catch (Exception e) {
            System.err.println("Error in VectorObject.forceAdd(Coordinate) - " + e);
            return false;
        }
    }     
    
    /**
     * Returns the VectorObject at the given list location.
     * 
     * @param location
     * @return 
     */
    @Override
    public VectorObject get(int location) {
        if (0 <= location && location < (lastIndex - firstIndex)) {
            return array[firstIndex + location];
        }

        throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(location)
                                          + " List Size: " + Integer.valueOf(lastIndex - firstIndex));
    }    
        
    /**
     * Returns the array for this List.
     * 
     * @return 
     */
    public VectorObject[] getArray() {        
        return array;
    }
    
    /**
     * Gets all the custom data field names associated with this object.
     *
     * @return  Vector<String>  A Vector containing all of the Custom Field Names.
     */
    public ArrayList<String> getAllCustomDataFields() {        
        ArrayList<String>                   allFields, objectFields;
        boolean                             stringFound;
        co.foldingmap.map.vector.VectorObject  currentObject;
        
        allFields  = new ArrayList<String>();

        for (int i = firstIndex; i < lastIndex; i++) {
            currentObject = (co.foldingmap.map.vector.VectorObject) array[i];
            objectFields  = currentObject.getAllCustomDataFields();

            for (String currentField: objectFields) {
                stringFound  = false;

                for (String currentAllField: allFields) {
                    if (currentField.equals(currentAllField))
                        stringFound = true;
                }

                if (!stringFound)
                    allFields.add(currentField);
            } //end fields for loop
        } // end objects for loop

        return allFields;
    }    
    
    /**
     * Returns all objects within a given Range.
     * 
     * @param range
     * @return 
     */
    public VectorObjectList<VectorObject> getAllWithinRange(LatLonAltBox range) {
        LatLonAltBox                        currentRange;
        co.foldingmap.map.vector.VectorObject     currentObject;
        VectorObjectList<VectorObject>            objectsInRange;

        objectsInRange = new VectorObjectList<VectorObject>();

        for (int i = firstIndex; i < lastIndex; i++) {
            currentObject = (co.foldingmap.map.vector.VectorObject) array[i];
            currentRange  = currentObject.getBoundingBox();
            
            if (currentRange.overlaps(range))
                objectsInRange.add((VectorObject) currentObject);
        }

        return (objectsInRange);
    }    
    
    /**
     * Returns a LatLonAltBox in which all the objects in this list reside.
     * It this list is Empty then a LatLonAltBox with all dimentions equal
     * to zero is returned.
     * 
     * @return 
     */
    public LatLonAltBox getBoundary() {
        co.foldingmap.map.vector.VectorObject  currentObject;
        LatLonAltBox                        objectRange, range;
        
        try {
            //set the range to the first object's boundary box.
            currentObject = (co.foldingmap.map.vector.VectorObject) array[firstIndex];  

            if (currentObject != null) {
                range = currentObject.getBoundingBox();

                for (int i = (firstIndex + 1); i < lastIndex; i++) {
                    currentObject = (co.foldingmap.map.vector.VectorObject) array[i];    
                    
                    if (currentObject != null) {
                        objectRange = currentObject.getBoundingBox();
                        range       = LatLonAltBox.combine(range, objectRange);
                    }
                }
            } else {
                range =  new LatLonAltBox(0,0,0,0,0,0);
            }

            return range;
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in getBoundary() - " + e);
            return new LatLonAltBox(0,0,0,0,-1,-1);
        }
    }
    
    /**
     * Returns an organized list of of coordinates generated from the objects in
     * this VectorObjectList.
     * 
     * @return 
     */
    public CoordinateList<Coordinate> getCoordinatesForMerge() {
        CoordinateList<Coordinate>                              tempCoordinates, compareCoordinateList, currentCoordinateList;
        VectorObjectList<co.foldingmap.map.vector.VectorObject> baseSet;
        Coordinate                                              firstCurrentCoordinate, lastCurrentCoordinate;
        Coordinate                                              firstCompareCoordinate, lastCompareCoordinate;
                float                                           closest, bestTest, test1, test2, test3, test4;
        co.foldingmap.map.vector.VectorObject currentObject;
        co.foldingmap.map.vector.VectorObject                   compareObject, bestMatchObject;
        String                                                  bestMatchType;

        currentCoordinateList = new CoordinateList<Coordinate>();

        try {
            baseSet               = this.clone();
            bestMatchObject       = (co.foldingmap.map.vector.VectorObject) baseSet.get(0);
            bestMatchType         = "";
            compareCoordinateList = new CoordinateList<Coordinate>();
            currentObject         = (co.foldingmap.map.vector.VectorObject) baseSet.remove(0);
            currentCoordinateList = currentObject.getCoordinateList();

            while (baseSet.size() > 0) {
                firstCurrentCoordinate  = currentCoordinateList.get(0);
                lastCurrentCoordinate   = currentCoordinateList.get(currentCoordinateList.size() - 1);
                closest = Float.MAX_VALUE;

                for (int j = (baseSet.size() - 1); j >= 0; j--) {
                    compareObject = (co.foldingmap.map.vector.VectorObject) baseSet.get(j);

                    if (!currentObject.equals(compareObject)) {
                        compareCoordinateList   = compareObject.getCoordinateList();
                        firstCompareCoordinate  = compareCoordinateList.get(0);
                        lastCompareCoordinate   = compareCoordinateList.lastCoordinate();

                        test1 = CoordinateMath.getDistance(firstCurrentCoordinate, firstCompareCoordinate);
                        test2 = CoordinateMath.getDistance(firstCurrentCoordinate, lastCompareCoordinate);
                        test3 = CoordinateMath.getDistance(lastCurrentCoordinate,  firstCompareCoordinate);
                        test4 = CoordinateMath.getDistance(lastCurrentCoordinate,  lastCompareCoordinate);

                        bestTest = Math.min(Math.min(Math.min(test1, test2), test3), test4);

                        if (bestTest < closest) {                        
                            closest         = bestTest;
                            bestMatchObject = compareObject;

                            if (test1 == bestTest) {
                                bestMatchType = "first-first";
                            } else if (test2 == bestTest) {
                                bestMatchType = "first-last";
                            } else if (test3 == bestTest) {
                                bestMatchType = "last-first";
                            } else if (test4 == bestTest) {
                                bestMatchType = "last-last";
                            }
                        } //end  if (bestTest < closest)
                    }//end (currentObject != compareObject)
                } //end for j loop

                tempCoordinates = new CoordinateList<Coordinate>();

                if (bestMatchType.equals("first-first")) {
                    compareCoordinateList.reverse();
                    tempCoordinates.addAll(compareCoordinateList);
                    tempCoordinates.addAll(currentCoordinateList);
                } else if (bestMatchType.equals("first-last")) {
                    tempCoordinates.addAll(compareCoordinateList);
                    tempCoordinates.addAll(currentCoordinateList);
                } else if (bestMatchType.equals("last-first")) {
                    tempCoordinates.addAll(currentCoordinateList);
                    tempCoordinates.addAll(compareCoordinateList);
                } else if (bestMatchType.equals("last-last")) {
                    tempCoordinates.addAll(currentCoordinateList);
                    compareCoordinateList.reverse();
                    tempCoordinates.addAll(compareCoordinateList);
                }

                currentCoordinateList = tempCoordinates;
                baseSet.remove(bestMatchObject);
            } //end while loop
        } catch (Exception e) {
            System.err.println("Error in VectorObjectList.getCoordinatesForMerge(): " + e);
        }

        return currentCoordinateList;
    }    
    
    /**
     * Returns all the values for a specified custom field name of objects
     * in this layer.
     *
     * @param   String             The field name for the associated value.
     * @return  ArrayList<String>  The value for the passed in fieldName.
     */
    public ArrayList<String> getCustomDataFieldValue(String fieldName) {
        ArrayList<String>               values;
        co.foldingmap.map.vector.VectorObject object;
        String                          currentValue;
        
        values = new ArrayList<String>();
        
        for (int i = firstIndex; i < lastIndex; i++) {
            object       = (co.foldingmap.map.vector.VectorObject) array[i];        
            currentValue = object.getCustomDataFieldValue(fieldName);
            
            if (currentValue != null && !values.contains(currentValue)) {
                values.add(currentValue);
            }
        }
        
        return values;
    }      
    
    /**
     * Returns the Eastern Most Longitude of all the objects in this list.
     * 
     * @return 
     */
    public float getEasternMostLongitude() {
        float                               compaireLongitude;
        float                               longitude = -180f;
        co.foldingmap.map.vector.VectorObject  currentObject;
        
        for (int i = firstIndex; i < lastIndex; i++) {
            currentObject     = (co.foldingmap.map.vector.VectorObject) array[i];
            compaireLongitude = (float) currentObject.getCoordinateList().getEasternMostLongitude();

            if (compaireLongitude > longitude)
                longitude = compaireLongitude;
        }

        return longitude;
    }    
    
    /**
     * Returns a new MspObjectList with copies of each object in the original
     * list.
     * 
     * @return 
     */
    public VectorObjectList<co.foldingmap.map.vector.VectorObject> getFullCopy() {
        co.foldingmap.map.vector.VectorObject                 object;
        VectorObjectList<co.foldingmap.map.vector.VectorObject>  copy;
        
        copy = new VectorObjectList<co.foldingmap.map.vector.VectorObject>();
        
        for (int i = firstIndex; i < lastIndex; i++) {
            object = (co.foldingmap.map.vector.VectorObject) array[i]; 
            copy.add((co.foldingmap.map.vector.VectorObject) object.copy());
        }
        
        return copy;
    }
    
    /**
     * Returns the Index of a given object.  
     * Returns -1 if the object cannot be found.
     * 
     * @param object
     * @return 
     */
    public int getIndexOf(co.foldingmap.map.vector.VectorObject object) {
        int                                result = -1;
        co.foldingmap.map.vector.VectorObject  currentObject;
                
        for (int i = firstIndex; i < lastIndex; i++) {
            currentObject = (co.foldingmap.map.vector.VectorObject) array[i];
            
            if (currentObject == object) {
                result = i;
                break;
            }                
        }
        
        return result;        
    }
    
    /**
     * Returns only the LineStrings in this List.
     * 
     * @return 
     */
    public VectorObjectList<co.foldingmap.map.vector.VectorObject> getLineStrings() {
        return new VectorObjectList(this.lineStrings);
    }    

    /**
     * Returns only the MapPoints in this List.
     * 
     * @return 
     */
    public VectorObjectList<co.foldingmap.map.vector.VectorObject> getMapPoints() {
        return new VectorObjectList(this.points);
    }      
    
    /**
     * Returns only the MultiGeometries in this list.
     * 
     * @return 
     */
    public VectorObjectList<co.foldingmap.map.vector.VectorObject> getMultiGeometries() {
        return new VectorObjectList(multiGeometries);
    }
    
    /**
     * Returns the Northern Most Latitude of any Object in this List.
     * 
     * @return 
     */
    public float getNorthernMostLatitude() {
        float                               compaireLatitude;
        float                               latitude = -90;
        co.foldingmap.map.vector.VectorObject  currentObject;
        
        for (int i = firstIndex; i < lastIndex; i++) {
            currentObject    = (co.foldingmap.map.vector.VectorObject) array[i];
            compaireLatitude = (float) currentObject.getCoordinateList().getNorthernMostLatitude();

            if (compaireLatitude > latitude)
                latitude = compaireLatitude;
        }

        return latitude;
    }    
    
    /**
     * Returns the VectorObject closes to the given Coordinate, null is the 
     * List is empty.
     * 
     * @param c
     * @return 
     */
    public co.foldingmap.map.vector.VectorObject getObjectClosestTo(Coordinate c) {
        CoordinateList<Coordinate>            currentObjectCoordinates;
        float                                 currentDistance, closestDistance;
        co.foldingmap.map.vector.VectorObject currentObject;
        co.foldingmap.map.vector.VectorObject objectToReturn;

        objectToReturn  = null;
        closestDistance = Float.MAX_VALUE;

        try {
            for (int i = firstIndex; i < lastIndex; i++) {
                currentObject = (co.foldingmap.map.vector.VectorObject) array[i];
                if (currentObject != null) {
                    currentObjectCoordinates = currentObject.getCoordinateList();

                    for (Coordinate currentCoordinate: currentObjectCoordinates) {
                        currentDistance = CoordinateMath.getDistance(currentCoordinate, c);
                        if (currentDistance < closestDistance) {
                            closestDistance = currentDistance;
                            objectToReturn  = currentObject;
                        }
                    } // end coordinate loop
                } //end null check
            } // end object loop
        } catch (Exception e) {
            System.err.println("Error in VectorObjectCollection.getObjectClosestTo.(Coordinate) - " + e);
        }

        return objectToReturn;
    }    
    
    /**
     * Returns only the polygons in this List.
     * 
     * @return 
     */
    public VectorObjectList<co.foldingmap.map.vector.VectorObject> getPolygons() {
        return new VectorObjectList(this.polygons);
    }
    
    /**
     * Returns the Southern most Latitude of an object in this list.
     * 
     * @return 
     */
    public float getSouthernMostLatitude() {
        float                               compaireLatitude;
        float                               latitude = 90;
        co.foldingmap.map.vector.VectorObject  currentObject;
        
        for (int i = firstIndex; i < lastIndex; i++) {
            currentObject    = (co.foldingmap.map.vector.VectorObject) array[i];
            compaireLatitude = (float) currentObject.getCoordinateList().getSouthernMostLatitude();
            
            if (compaireLatitude < latitude)
                latitude = compaireLatitude;
        }

        return latitude;
    }    
    
    /**
     * Returns the Western most Longitude of any object in this List.
     * 
     * @return 
     */
    public float getWesternMostLongitude() {
        float                               compaireLongitude;
        float                               longitude = 180;
        co.foldingmap.map.vector.VectorObject  currentObject;
        
        for (int i = firstIndex; i < lastIndex; i++) {
            currentObject     = (co.foldingmap.map.vector.VectorObject) array[i];
            compaireLongitude = (float) currentObject.getCoordinateList().getWesternMostLongitude();

            if (compaireLongitude < longitude)
                longitude = compaireLongitude;
        }

        return longitude;
    }    
    
    private void growAtEnd(int required) {
        int size = lastIndex - firstIndex;

        if (firstIndex >= required - (array.length - lastIndex)) {
            int newLast = lastIndex - firstIndex;

            if (size > 0) {
                System.arraycopy(array, firstIndex, array, 0, size);
                int start = newLast < firstIndex ? firstIndex : newLast;
                Arrays.fill(array, start, array.length, null);
            }

            firstIndex = 0;
            lastIndex  = newLast;
        } else {
            int increment = size / 2;

            if (required > increment)
                increment = required;

            if (increment < 12)
                increment = 12;

            VectorObject[] newArray = newElementArray(size + increment);

            if (size > 0) {
                System.arraycopy(array, firstIndex, newArray, 0, size);
                firstIndex = 0;
                lastIndex = size;
            }

            array = newArray;
        }
    }
    
    private void growAtFront(int required) {
        int size = lastIndex - firstIndex;

        if (array.length - lastIndex + firstIndex >= required) {
            int newFirst = array.length - size;

            if (size > 0) {
                System.arraycopy(array, firstIndex, array, newFirst, size);
                int length = firstIndex + size > newFirst ? newFirst : firstIndex + size;
                Arrays.fill(array, firstIndex, length, null);
            }

            firstIndex = newFirst;
            lastIndex = array.length;
        } else {
            int increment = size / 2;

            if (required > increment) {
                increment = required;
            }

            if (increment < 12) {
                increment = 12;
            }
            
            VectorObject[] newArray = newElementArray(size + increment);

            if (size > 0) {
                System.arraycopy(array, firstIndex, newArray, newArray.length - size, size);
            }

            firstIndex = newArray.length - size;
            lastIndex = newArray.length;
            array = newArray;
        }
    }    
    
    private void growForInsert(int location, int required) {
        int size = lastIndex - firstIndex;
        int increment = size / 2;

        if (required > increment) {
            increment = required;
        }

        if (increment < 12) {
            increment = 12;
        }

        VectorObject[] newArray = newElementArray(size + increment);
        int         newFirst = increment - required;

        // Copy elements after location to the new array skipping inserted elements
        System.arraycopy(array, location + firstIndex, newArray, newFirst
            + location + required, size - location);

        // Copy elements before location to the new array from firstIndex
        System.arraycopy(array, firstIndex, newArray, newFirst, location);
        firstIndex = newFirst;
        lastIndex = size + increment;

        array = newArray;
    }
    
    /**
     * Creates a hash code for this Object.
     * 
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 3;
        co.foldingmap.map.vector.VectorObject currentObject;
        
        for (int i = firstIndex; i < lastIndex; i++) {
            currentObject = (co.foldingmap.map.vector.VectorObject) array[i];        
            hash += (currentObject != null ? currentObject.hashCode() : 0);
        }
        
        return hash;
    }    
    
    @SuppressWarnings("unchecked")
    private VectorObject[] newElementArray(int size) {
        return (VectorObject[]) new Object[size];
    }
    
    /**
     * Returns the index in the list of a given VectorObject.
     * 
     * @param object
     * @return 
     */
    public int indexOf(co.foldingmap.map.vector.VectorObject object) {
        co.foldingmap.map.vector.VectorObject  currentObject;
        int                         index = -1;
        
        for (int i = firstIndex; i < lastIndex; i++) {
            currentObject = (co.foldingmap.map.vector.VectorObject) array[i];
            
            if (currentObject == object) {
                index = i;
                break;
            }
        }
        
        return index;
    }     
    
    /**
     * Returns the last element in this list;
     * @return 
     */
    public VectorObject lastElement() {
        return array[lastIndex - 1];
    }    
    
    /**
     * Removes the object at the specified location from this list.
     *
     * @param location
     *            the index of the object to remove.
     * @return the removed object.
     * @throws IndexOutOfBoundsException
     *             when {@code location < 0 || >= size()}
     */
    @Override
    public VectorObject remove(int location) {
        VectorObject   result;
        int         size = lastIndex - firstIndex;

        if (0 <= location && location < size) {
            if (location == size - 1) {
                result = array[--lastIndex];
                array[lastIndex] = null;
            } else if (location == 0) {
                result = array[firstIndex];
                array[firstIndex++] = null;
            } else {
                int elementIndex = firstIndex + location;
                result = array[elementIndex];

                if (location < size / 2) {
                    System.arraycopy(array, firstIndex, array, firstIndex + 1, location);
                    array[firstIndex++] = null;
                } else {
                    System.arraycopy(array, elementIndex + 1, array, elementIndex, size - location - 1);
                    array[--lastIndex] = null;
                }
            }

            if (firstIndex == lastIndex) {
                firstIndex = lastIndex = 0;
            }
        } else {
            throw new IndexOutOfBoundsException(Integer.valueOf(location) + " List size: " + Integer.valueOf(lastIndex - firstIndex));
        }         
        
        if (result instanceof MapPoint) {
            points.remove((MapPoint) result);
        } else if (result instanceof LineString) {
            lineStrings.remove((LineString) result);
        } else if (result instanceof Polygon) {
            polygons.remove((Polygon) result);
        } else if (result instanceof MultiGeometry) {
            multiGeometries.remove((MultiGeometry) result);
        }         
        
        modCount++;
        return result;
    }    
    
    /**
     * Removes a VectorObject from this List.
     * 
     * @param object
     * @return If the object was removed or not.
     */
    @Override
    public boolean remove(Object object) {
        boolean removed;
        
        removed = super.remove(object);
        
        if (removed) {
            if (object instanceof MapPoint) {
                points.remove((MapPoint) object);
            } else if (object instanceof LineString) {
                lineStrings.remove((LineString) object);
            } else if (object instanceof Polygon) {
                polygons.remove((Polygon) object);
            } else if (object instanceof MultiGeometry) {
                multiGeometries.remove((MultiGeometry) object);
            }         
        }
        
        return removed;
    }
    
    /**
     * Returns the number of elements in this VectorObjectList.
     *
     * @return the number of elements in this VectorObjectList.
    */
    @Override
    public int size() {
        return lastIndex - firstIndex;
    }     
    
    public void sortByLayer() {
        VectorObjectList<co.foldingmap.map.vector.VectorObject>  sortedObjects;
        VectorObjectList<co.foldingmap.map.vector.VectorObject>  lineStrings, multis, points, polygons; 
        VectorObjectList<co.foldingmap.map.vector.VectorObject>  cityRoads, others, primaryHighways, reefs, secondaryHighways;
        VectorObjectList<co.foldingmap.map.vector.VectorObject>  buildings, otherPolys, polyAreas, tramWays;
        String                    lineType;
        
        buildings         = new VectorObjectList<co.foldingmap.map.vector.VectorObject>();
        cityRoads         = new VectorObjectList<co.foldingmap.map.vector.VectorObject>();
        others            = new VectorObjectList<co.foldingmap.map.vector.VectorObject>();
        otherPolys        = new VectorObjectList<co.foldingmap.map.vector.VectorObject>();   
        polyAreas         = new VectorObjectList<co.foldingmap.map.vector.VectorObject>();   
        primaryHighways   = new VectorObjectList<co.foldingmap.map.vector.VectorObject>();
        secondaryHighways = new VectorObjectList<co.foldingmap.map.vector.VectorObject>();
        sortedObjects     = new VectorObjectList<co.foldingmap.map.vector.VectorObject>();
        tramWays          = new VectorObjectList<co.foldingmap.map.vector.VectorObject>();
        reefs             = new VectorObjectList<co.foldingmap.map.vector.VectorObject>();
                
        lineStrings = getLineStrings();
        multis      = getMultiGeometries();
        points      = getMapPoints();
        polygons    = getPolygons();
        
        sortedObjects.addAll(multis);        
        this.removeAll(multis);
        
        //sort out different Polygons
        for (int i = 0; i < polygons.size(); i++) {
            Polygon currentPoly = (Polygon) polygons.get(i);
            
            if (currentPoly.getObjectClass().equalsIgnoreCase("Building")) {
                buildings.add(currentPoly);
                this.remove(currentPoly);
            } else if (currentPoly.getObjectClass().equalsIgnoreCase("Commercial Area")  ||
                       currentPoly.getObjectClass().equalsIgnoreCase("Industrial Area")  ||
                       currentPoly.getObjectClass().equalsIgnoreCase("Protected Area")   ||
                       currentPoly.getObjectClass().equalsIgnoreCase("Residential Area") ||
                       currentPoly.getObjectClass().equalsIgnoreCase("University")       ||
                       currentPoly.getObjectClass().equalsIgnoreCase("Wetland")) {
                polyAreas.add(currentPoly);
                this.remove(currentPoly);
            } else if (currentPoly.getObjectClass().equalsIgnoreCase("Ocean")) {                
                sortedObjects.add(currentPoly);
                this.remove(currentPoly);
            } else if (currentPoly.getObjectClass().equalsIgnoreCase("Reef")) {
                reefs.add(currentPoly);
                this.remove(currentPoly);
            } else {
                otherPolys.add(currentPoly);
                this.remove(currentPoly);
            }
        }
        
        sortedObjects.addAll(reefs);
        sortedObjects.addAll(polyAreas);
        sortedObjects.addAll(otherPolys);
        sortedObjects.addAll(buildings);
        
        //sort out different LineStyles
        for (int i = 0; i < lineStrings.size(); i++) {
            LineString currentLine = (LineString) lineStrings.get(i);
            lineType = currentLine.getObjectClass();
            
            if (lineType.equals("Road - City Primary") ||
                lineType.equals("Road - City Secondary") ||
                lineType.equals("Road - City Tertiary")) {
                
                cityRoads.add((co.foldingmap.map.vector.VectorObject) currentLine);
                this.remove(currentLine);
            } else if (lineType.equals("Road - Primary Highway") ||
                       lineType.equals("Road - Primary Highway Link")) {
                primaryHighways.add((co.foldingmap.map.vector.VectorObject) currentLine);
                this.remove(currentLine);
            } else if (lineType.equals("Road - Secondary Highway")) {
                secondaryHighways.add((co.foldingmap.map.vector.VectorObject) currentLine);
                this.remove(currentLine);
            } else if (lineType.equals("Rail - Tram")) {
                tramWays.add((co.foldingmap.map.vector.VectorObject) currentLine);
                this.remove(currentLine);
            } else {
                others.add((co.foldingmap.map.vector.VectorObject) currentLine);
                this.remove(currentLine);
            }
        }
        
        sortedObjects.addAll(others);
        sortedObjects.addAll(cityRoads);
        sortedObjects.addAll(secondaryHighways);
        sortedObjects.addAll(tramWays);
        sortedObjects.addAll(primaryHighways);
        sortedObjects.addAll(points);
        
        //copy anythign left in the array
        VectorObject[] theRestObjects = this.array.clone();
        
        //clear the array and add the newly sorted objects
        this.clear();
        for (co.foldingmap.map.vector.VectorObject vo: sortedObjects)
            this.add((VectorObject) vo);        
        
        //add anything the was left behind
        for (VectorObject vo: theRestObjects)
            this.add(vo);
    }    
    
    /**
     * Sorts objects so that object indexes are in the same order as their
     * east to west sorted VectorObjects.
     * 
     * @return 
     */    
    public VectorObjectList<co.foldingmap.map.vector.VectorObject> sortEastToWest() {
        double                                                  bestMatchValue;
        LatLonAltBox                                            currentObjectBounds;
        co.foldingmap.map.vector.VectorObject                   bestMatchObject;
        co.foldingmap.map.vector.VectorObject                   currentObject;
        VectorObjectList<co.foldingmap.map.vector.VectorObject> objectsCopy, sortedCollection;

        objectsCopy      = new VectorObjectList<co.foldingmap.map.vector.VectorObject>();
        sortedCollection = new VectorObjectList<co.foldingmap.map.vector.VectorObject>();

        try {
            for (int i = firstIndex; i < lastIndex; i++) {
                currentObject = (co.foldingmap.map.vector.VectorObject) array[i];
                objectsCopy.add((co.foldingmap.map.vector.VectorObject) currentObject.copy());
            }

            while (objectsCopy.size() > 0) {
                bestMatchObject  = null;
                bestMatchValue   = 0;

                for (co.foldingmap.map.vector.VectorObject object: objectsCopy) {
                    currentObjectBounds = object.getBoundingBox();

                    if (currentObjectBounds.getEast() > bestMatchValue) {
                        bestMatchValue  = currentObjectBounds.getEast();
                        bestMatchObject = object;
                    }
                }

                if (bestMatchObject != null) {
                    objectsCopy.remove(bestMatchObject);
                    sortedCollection.add(bestMatchObject);
                } else {
                    //something went wrong
                    sortedCollection.addAll(objectsCopy);
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error in VectorObjectCollection.sortEastToWest() - " + e);
        }

        return sortedCollection;
    }

    /**
     * Sorts objects so that object indexes are in the same order as their
     * north to south sorted VectorObjects.
     * 
     * @return 
     */
    public VectorObjectList<co.foldingmap.map.vector.VectorObject> sortNorthToSouth() {
        double                                                bestMatchValue;
        LatLonAltBox                                          currentObjectBounds;
        co.foldingmap.map.vector.VectorObject bestMatchObject;
        co.foldingmap.map.vector.VectorObject currentObject;
        VectorObjectList<co.foldingmap.map.vector.VectorObject>  objectsCopy, sortedCollection;

        objectsCopy      = new VectorObjectList<co.foldingmap.map.vector.VectorObject>();
        sortedCollection = new VectorObjectList<co.foldingmap.map.vector.VectorObject>();

        try {
            for (int i = firstIndex; i < lastIndex; i++) {
                currentObject = (co.foldingmap.map.vector.VectorObject) array[i];
                objectsCopy.add(currentObject);
            }
            
            while (objectsCopy.size() > 0) {
                bestMatchObject  = null;
                bestMatchValue   = -99;

                for (co.foldingmap.map.vector.VectorObject object: objectsCopy) {
                    currentObjectBounds = object.getBoundingBox();

                    if (currentObjectBounds.getNorth() > bestMatchValue) {
                        bestMatchValue  = currentObjectBounds.getNorth();
                        bestMatchObject = object;
                    }
                }

                if (bestMatchObject != null) {
                    objectsCopy.remove(bestMatchObject);
                    sortedCollection.add(bestMatchObject);
                } else {
                    //something went wrong
                    sortedCollection.addAll(objectsCopy);
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error in VectorObjectCollection.sortNorthToSouth() - " + e);
        }

        return sortedCollection;
    }
    
}
