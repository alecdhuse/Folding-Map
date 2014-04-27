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
import co.foldingmap.map.MapObject;
import java.io.Serializable;
import java.util.*;

/**
 * This is a list object with use specific to the Coordinate class.
 * It includes useful functions for dealing with Coordinates. 
 * This class is for use in MapObjects for storing the objects Coordinates.
 * 
 * @author Alec
 */
public class CoordinateList<Coordinate> extends    AbstractList<Coordinate>
                                        implements List<Coordinate>, 
                                                   Cloneable, 
                                                   Serializable, 
                                                   RandomAccess {

    private           MapObject    parentObject;
    private transient int          firstIndex;
    private transient int          lastIndex;
    private transient Coordinate[] array;
    
    /**
     * Constructs a new instance of CoordinateList with ten capacity.
     */
    public CoordinateList() {
        this(10);
    }   
    
    /**
     * Constructs a new instance of CoordinateList with the specified capacity.
     * 
     * @param capacity
     *            the initial capacity of this CoordinateList.
     */
    public CoordinateList(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException();
        }

        firstIndex = lastIndex = 0;
        array = newElementArray(capacity);
    }    
    
    /**
     * Constructs a new instance given an ArrayList<Coordinate>
     * 
     * @param coordinates 
     */
    public CoordinateList(ArrayList<Coordinate> coordinates) {
        this.array = (Coordinate[]) coordinates.toArray();
        firstIndex = 0;
        lastIndex  = array.length;
    }    
    
    /**
     * Constructs a new instance given an Array of Coordinates.
     * 
     * @param coordinates 
     */
    public CoordinateList(Coordinate[] coordinates) {
        Object[] tempArray = new Object[coordinates.length];
        this.array = (Coordinate[]) tempArray;
        
        System.arraycopy(coordinates, 0, this.array, 0, coordinates.length);

        firstIndex = 0;
        lastIndex  = coordinates.length;
    }        
    
    /**
     * Inserts the specified object into this {@code CoordinateList} at the 
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
    public void add(int location, Coordinate object) {
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

        modCount++;
    }
    
    /**
     * Adds the specified object at the end of this CoordinateList unless an
     * instance already exists in the list.
     *
     * @param object
     *          The Coordinate to add.
     * @return 
     *          If the object was added or not.
     */
    @Override
    public boolean add(Coordinate newCoordinate) {
        try {
            boolean instanceFound = false;            

            if (firstIndex != lastIndex) {
                //check to see if the coordinate exists already
                for (int i = firstIndex; i < lastIndex; i++) {
                    if (array[i].equals(newCoordinate)) {
                        instanceFound = true;
                        break;
                    }
                } //end for loop
            }

            if (instanceFound == false) {
                forceAdd(newCoordinate);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error in CoordinateList.add() - " + e);
            return false;
        }
    }    
    
    /**
     * Adds the objects in the specified collection to this 
     * {@code CoordinateList}.
     *
     * @param collection
     *            the collection of objects.
     * @return {@code true} if this {@code CoordinateList} is modified, 
     *          {@code false} otherwise.
    */
    @Override
    public boolean addAll(Collection<? extends Coordinate> collection) {
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

        return true;
    }    
    
    /**
     * Returns a new {@code CoordinateList} with the same elements, the same 
     * size and the same capacity as this {@code CoordinateList}.
     *
     * @return a shallow copy of this {@code CoordinateList}
     * @see java.lang.Cloneable
    */
    @Override
    @SuppressWarnings("unchecked")
    public CoordinateList clone() {
        try {
            CoordinateList<Coordinate> newList = (CoordinateList<Coordinate>) super.clone();
            newList.array = array.clone();
            return newList;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    } 
    
    /**
     * Returns if the supplied coordinate already exists in the list.
     * 
     * @param c
     *      The Coordinate to check.
     * 
     * @return If the Coordinate exist in the list or not.
     */
    public boolean contains(co.foldingmap.map.vector.Coordinate c) {
        boolean                     result = false;
        co.foldingmap.map.vector.Coordinate  currentCoordinate;
                
        for (int i = firstIndex; i < lastIndex; i++) {
            currentCoordinate = (co.foldingmap.map.vector.Coordinate) array[i];
            
            if (currentCoordinate == c) {
                result = true;
                break;
            }                
        }
        
        return result;
    }    
    
    /**
     * Returns if this Coordinate list has the same coordinates as another list.
     * 
     * @param o
     * @return 
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof CoordinateList) {
            CoordinateList cl = (CoordinateList) o;
            
            return (this.hashCode() == cl.hashCode());
        } else {
            return false;
        }
    }

    /**
     * Hash code generation for this CoordinateList.
     * 
     * @return 
     */
    @Override
    public int hashCode() {
        Coordinate  c;
        int         hash = 7;
        
        for (int i = firstIndex; i < lastIndex; i++) {
            c = array[i];                  
            hash = 22 * hash + (c != null ? c.hashCode() : 0);
        }
                
        return hash;
    }
    
    /**
     * Adds the specified object at the end of this CoordinateList even if an
     * instance already exists in the list.
     *
     * @param newCoordinate
     *            the Coordinate to add.
     * @return always true
     */
    public boolean forceAdd(Coordinate newCoordinate) {
        try {
            if (lastIndex == array.length) {
                growAtEnd(1);
            }

            array[lastIndex++] = newCoordinate;
            modCount++;

            return true;
        } catch (Exception e) {
            System.err.println("Error in CoordinateList.forceAdd(Coordinate) - " + e);
            return false;
        }
    }    
    
    /**
     * Returns the Coordinate at the given list location.
     * 
     * @param location
     * @return 
     */
    @Override
    public Coordinate get(int location) {
        if (0 <= location && location < (lastIndex - firstIndex)) {
            return array[firstIndex + location];
        }

        throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(location)
                                          + " List Size: " + Integer.valueOf(lastIndex - firstIndex));
    }    
    
    /**
     * Returns all coordinates in this list that are within the LatLonAltBox.
     * Altitude is ignored.
     * 
     * @param bounds
     * @return 
     */
    public CoordinateList<Coordinate> get(LatLonAltBox bounds) {
        CoordinateList                    coordinates;
        co.foldingmap.map.vector.Coordinate  currentCoordinate;
        
        coordinates = new CoordinateList();
        
        for (int i = firstIndex; i < lastIndex; i++) {
            currentCoordinate = (co.foldingmap.map.vector.Coordinate) array[i];
            
            if (bounds.contains(currentCoordinate))
               coordinates.add(currentCoordinate);
        }        
        
        return coordinates;
    }    
    
    /**
     * Returns a float array containing the altitudes of each coordinate.  
     * Indexes of the float array are the same as this CoordinateList.
     * 
     * @return 
     */
    public float[] getAltitudes() {
        co.foldingmap.map.vector.Coordinate c;
        float[] altitudes; 
                
        altitudes = new float[this.size()];
        
        for (int i = 0; i < this.size(); i++) {
            c = (co.foldingmap.map.vector.Coordinate) this.get(i);
            altitudes[i] = c.getAltitude();
        }
        
        return altitudes;
    }
    
    /**
     * Returns this CoordinateList as an ArrayList
     * 
     * @return 
     */
    public ArrayList<Coordinate> getArrayList() {
        ArrayList<Coordinate> arrayList;

        arrayList = new ArrayList<Coordinate>(this);

        return arrayList;
    }  
        
    /**
     * Returns a list of coordinates between two coordinates that exist in this
     * CoordinateList.  The returned list will incluse the start and end
     * Coordinate Parameters. 
     * 
     * @param c1    Start Coordinate
     * @param c2    End Coordinate
     * @return 
     */
    public CoordinateList<co.foldingmap.map.vector.Coordinate> getCoordinatesBetween(co.foldingmap.map.vector.Coordinate c1, co.foldingmap.map.vector.Coordinate c2) {
        CoordinateList<co.foldingmap.map.vector.Coordinate>  results;
        int                                         c1Index, c2Index;
        co.foldingmap.map.vector.Coordinate         currentCoordinate;
        
        results     = new CoordinateList<co.foldingmap.map.vector.Coordinate>();
        c1Index     = indexOf(c1);
        c2Index     = indexOf(c2);
        
        if (c1Index < c2Index) {
            for (int i = c1Index; i <= c2Index; i++) {
                currentCoordinate = (co.foldingmap.map.vector.Coordinate) array[i];
                results.add(currentCoordinate);
            }
        } else {
            for (int i = c2Index; i >= c1Index; i--) {
                currentCoordinate = (co.foldingmap.map.vector.Coordinate) array[i];
                results.add(currentCoordinate);
            }
        }
        
        return results;
    }    
    
    /**
     * Returns the Coordinate closest to the coordinate provided.
     * 
     * @param  c The Coordinate to find the closest.
     * @return The Coordinate in this list closest to the Coordinate provided.
     */
    public co.foldingmap.map.vector.Coordinate getCoordinateClosestTo(co.foldingmap.map.vector.Coordinate c) {
        co.foldingmap.map.vector.Coordinate closestCoordinate;
        co.foldingmap.map.vector.Coordinate currentCoordinate;
        double                              closestDistance,   currentDistance;
        Object                              currentObject;
        
        closestCoordinate = co.foldingmap.map.vector.Coordinate.UNKNOWN_COORDINATE;
        closestDistance   = Double.MAX_VALUE;

        try {
            for (int i = firstIndex; i < lastIndex; i++) {
                currentObject     = array[i];
                currentCoordinate = (co.foldingmap.map.vector.Coordinate) currentObject;
                
                if (currentCoordinate != null) {
                    currentDistance   = CoordinateMath.getDistance(c, currentCoordinate);

                    if (currentDistance < closestDistance) {
                        closestDistance   = currentDistance;
                        closestCoordinate = currentCoordinate;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error in CoordinateList.getCoordinateClosestTo(Coordinate) - " + e);
        }

        return closestCoordinate;
    }    
    
    /**
     * Returns the index in the list of a given Coordinate.
     * 
     * @param c
     * @return 
     */
    public int indexOf(co.foldingmap.map.vector.Coordinate c) {
        co.foldingmap.map.vector.Coordinate  currentCoordinate;
        int                         index = -1;
        
        for (int i = firstIndex; i < lastIndex; i++) {
            currentCoordinate = (co.foldingmap.map.vector.Coordinate) array[i];
            
            if (currentCoordinate == c) {
                index = i;
                break;
            }
        }
        
        return index;
    }    
    
    /**
     * Returns this List's Coordinates as a String of the form:
     * longitude,latitude,altitude longitude,latitude,altitude
     * 
     * @return 
     */
    public String getCoordinateString() {
        co.foldingmap.map.vector.Coordinate  currentCoordinate;
        StringBuilder                     coord;

        coord = new StringBuilder();
        
        try {
            for (int i = firstIndex; i < lastIndex; i++) {
                currentCoordinate = (co.foldingmap.map.vector.Coordinate) array[i];
                
                if (currentCoordinate.getID() == 0) {
                    coord.append(currentCoordinate.toString());                    
                } else {
                    coord.append(currentCoordinate.getID());
                }
                
                if (i < (lastIndex - 1))
                    coord.append(" ");
            }
        } catch (Exception e) {
            System.err.println("Error in CoordinateList.getCoordinateString() - " + e);
        }

        return coord.toString();
    }     
    
    /**
     * Returns the Earliest date of a Coordinate in this CoordinateList.
     * @return 
     */
    public long getEarliestDate() {
        long                                earliest;
        co.foldingmap.map.vector.Coordinate currentCoordinate;

        earliest = Long.MAX_VALUE;

        for (int i = firstIndex; i < lastIndex; i++) {
            currentCoordinate = (co.foldingmap.map.vector.Coordinate) array[i];

            if (currentCoordinate.getDate() < earliest) 
                earliest = currentCoordinate.getDate();
        }

        return earliest;
    }    
    
    /**
     * Returns the Eastern most coordinate in this CoordinateList.
     * @return 
     */
    public double getEasternMostLongitude() {
        double                           longitude;
        co.foldingmap.map.vector.Coordinate currentCoordinate;
        
        longitude = -180f;
        
        for (int i = firstIndex; i < lastIndex; i++) {
            currentCoordinate = (co.foldingmap.map.vector.Coordinate) array[i];

            if (currentCoordinate != null) {
                if (currentCoordinate.getLongitude() > longitude)
                    longitude = currentCoordinate.getLongitude();
            }
        }

        return longitude;
    }    
    
    /**
     * Returns the latest date of a Coordinate in this CoordinateList.
     * 
     * @return 
     */
    public long getLatestDate() {
        long                                latest;
        co.foldingmap.map.vector.Coordinate currentCoordinate;

        latest = getEarliestDate();

        for (int i = firstIndex; i < lastIndex; i++) {
            currentCoordinate = (co.foldingmap.map.vector.Coordinate) array[i];

            if (currentCoordinate.getDate() > latest)
                latest = currentCoordinate.getDate();
        }

        return latest;
    }    
    
    /**
     * Returns the Maximum Altitude of any Coordinate in this list.
     * 
     * @return 
     */
    public float getMaxAltitude() {
        float                               minAltitude;
        co.foldingmap.map.vector.Coordinate currentCoordinate;

        minAltitude = Float.MIN_VALUE;
        
        for (int i = firstIndex; i <lastIndex; i++) {
            currentCoordinate = (co.foldingmap.map.vector.Coordinate) array[i];

            if (currentCoordinate != null) {
                if (currentCoordinate.getAltitude() > minAltitude)
                    minAltitude = currentCoordinate.getAltitude();
            }
        }

        return minAltitude;
    }    
    
    /**
     * Returns the Minimum Altitude of any Coordinate in this list.
     * 
     * @return 
     */
    public float getMinAltitude() {
        float                               minAltitude;
        co.foldingmap.map.vector.Coordinate currentCoordinate;

        minAltitude = Float.MAX_VALUE;
        
        for (int i = firstIndex; i < lastIndex; i++) {
            currentCoordinate = (co.foldingmap.map.vector.Coordinate) array[i];

            if (currentCoordinate != null) {
                if (currentCoordinate.getAltitude() < minAltitude)
                    minAltitude = currentCoordinate.getAltitude();
            }
        }

        return minAltitude;
    }    
    
    /**
     * Returns the Northern most Latitude of any coordinate in this list.
     * @return 
     */
    public double getNorthernMostLatitude() {
        double                           latitude;
        co.foldingmap.map.vector.Coordinate currentCoordinate;

        latitude = -90f;
        
        for (int i = firstIndex; i < lastIndex; i++) {
            currentCoordinate = (co.foldingmap.map.vector.Coordinate) array[i];

            if (currentCoordinate != null) {
                if (currentCoordinate.getLatitude() > latitude)
                    latitude = currentCoordinate.getLatitude();
            }
        }
        
        return latitude;
    }
        
    /**
     *  Returns an array containing the same coordinates as this CoordinateList,
     *  but in reverse order of the coordinates in this list.
     * 
     * @return 
     */
    public CoordinateList<Coordinate> getReverse() {
        Coordinate[] newArray      = newElementArray(this.size());
        int          newArrayIndex = 0;
        
        try {
            for (int i = (lastIndex - 1); i >= firstIndex; i--) {
                newArray[newArrayIndex] = array[i];
                newArrayIndex++;
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in CoordinateList.getReverse() - " + e);
        }

        return new CoordinateList(newArray);
    }      
    
    /**
     * Calculates the length of segments for Coordinates in this list.
     * 
     * @return 
     */
    public ArrayList<Float> getSegmentLengths() {
       ArrayList<Float>                    lengths;
       Float                               length;
       co.foldingmap.map.vector.Coordinate coordinate1;
       co.foldingmap.map.vector.Coordinate coordinate2;
       
       lengths = new ArrayList<Float>();
       
       for (int i = (firstIndex + 1); i < lastIndex; i++) {
           coordinate1 = (co.foldingmap.map.vector.Coordinate) array[i];
           coordinate2 = (co.foldingmap.map.vector.Coordinate) array[i-1];           
           length      = new Float(CoordinateMath.getDistance(coordinate1, coordinate2));
           
           lengths.add(length);
       }
       
       return lengths;
    }
    
    
    /**
     * Returns the Southern most Latitude of any Coordinate in this list
     * @return 
     */
    public double getSouthernMostLatitude() {
        double                           latitude;
        co.foldingmap.map.vector.Coordinate currentCoordinate;

        latitude = 90;
        
        for (int i = firstIndex; i < lastIndex; i++) {
            currentCoordinate = (co.foldingmap.map.vector.Coordinate) array[i];

            if (currentCoordinate != null) {
                if (currentCoordinate.getLatitude() < latitude)
                    latitude = currentCoordinate.getLatitude();
            }
        }

        return latitude;
    }    
    
    /**
     * Returns the Western most Longitude of any Coordinate in this list.
     * 
     * @return 
     */
    public double getWesternMostLongitude() {
        double                           longitude;
        co.foldingmap.map.vector.Coordinate currentCoordinate;

        longitude = 180f;
        
        for (int i = firstIndex; i < lastIndex; i++) {
            currentCoordinate = (co.foldingmap.map.vector.Coordinate) array[i];

            if (currentCoordinate != null) {
                if (currentCoordinate.getLongitude() < longitude)
                    longitude = currentCoordinate.getLongitude();
            }
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

            Coordinate[] newArray = newElementArray(size + increment);

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
            
            Coordinate[] newArray = newElementArray(size + increment);

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

        Coordinate[] newArray = newElementArray(size + increment);
        int newFirst = increment - required;

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
     * Returns if this CoordinateList contains and Coordinates that are shared
     * with other objects.
     * 
     * @return 
     */
    public boolean hasSharedCoordinates() {
        boolean                          hasShared;
        co.foldingmap.map.vector.Coordinate currentCoordinate;
        
        hasShared = false;
        
        for (int i = firstIndex; i < lastIndex; i++) {
            currentCoordinate = (co.foldingmap.map.vector.Coordinate) array[i];
            
            if (currentCoordinate.isShared()) {
                hasShared = true;
                break;
            }
        }
        
        return hasShared;
    }
    
    /**
     * Checks to see if the supplied coordinate is at the beginning or end of
     * this coordinate list.
     *
     * @param c
     *          The Coordinate to test.
     * @return  If the coordinate is an endpoint.
     */
    public boolean  isEndPoint(Coordinate c) {
        if (c.equals(array[firstIndex])) {
            return true;
        } else if (c.equals(array[lastIndex - 1])) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the last Coordinate in this list
     * 
     * @return 
     */
    public Coordinate lastCoordinate() {
        return array[lastIndex - 1];
    }    
    
    @SuppressWarnings("unchecked")
    private Coordinate[] newElementArray(int size) {
        return (Coordinate[]) new Object[size];
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
    public Coordinate remove(int location) {
        Coordinate  result;
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

        modCount++;
        return result;
    }
    
    /**
     * Removes the given MapObject parent, from the Coordinates in this 
     * CoordinateList.  Not all coordinateLists have to have a parent.
     * 
     * @param parent 
     */
    public void removeParentObject(MapObject parent) {
        co.foldingmap.map.vector.Coordinate currentCoordinate;
        
        if (this.parentObject == parent)
            this.parentObject = null;
        
        //update the parent for all Coordinates containd in this list.
        for (int i = firstIndex; i < lastIndex; i++) {
            currentCoordinate = (co.foldingmap.map.vector.Coordinate) array[i];
            currentCoordinate.removeParent(parent);
        }
    }    
    
    /**
     *  Reverses the order of the coordinates in this list.
     */
    public void reverse() {
        Coordinate[] newArray      = newElementArray(array.length);
        int                          newArrayIndex = firstIndex;

        for (int i = (lastIndex - 1); i >= firstIndex; i--) {
            newArray[newArrayIndex] = array[i];
            newArrayIndex++;
        }

        array = newArray;
    }    
    
    /**
     * Sets the MapObject that is the parent, the object using this 
     * CoordinateList.  Not all coordinateLists have to have a parent.
     * 
     * @param parent 
     */
    public void setParentObject(MapObject parent) {
        co.foldingmap.map.vector.Coordinate currentCoordinate;
        
        this.parentObject = parent;
        
        //update the parent for all Coordinates containd in this list.
        for (int i = firstIndex; i < lastIndex; i++) {
            currentCoordinate = (co.foldingmap.map.vector.Coordinate) array[i];
            currentCoordinate.addParent(parent);
        }
    }
    
    /**
     * Returns the number of elements in this CoordinateList.
     *
     * @return the number of elements in this CoordinateList.
    */
    @Override
    public int size() {
        return lastIndex - firstIndex;
    }    
}

