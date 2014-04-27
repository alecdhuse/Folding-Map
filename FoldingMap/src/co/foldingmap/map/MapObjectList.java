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
package co.foldingmap.map;

import co.foldingmap.map.vector.VectorObject;
import java.io.Serializable;
import java.util.*;

/**
 *
 * @author Alec
 */
public class MapObjectList<MapObject> extends    AbstractList<MapObject> 
                                      implements List<MapObject>, 
                                                 Cloneable, 
                                                 Serializable, 
                                                 RandomAccess {
    
    private transient int               firstIndex;
    private transient int               lastIndex;
    private transient MapObject[]       array;     
    
    public MapObjectList() {
        this(10);
    }
    
    /**
     * Constructs a new instance of MapObjectList with the specified capacity.
     * 
     * @param capacity
     *            the initial capacity of this MapObjectList.
     */
    public MapObjectList(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException();
        }

        firstIndex = lastIndex = 0;
        array      = newElementArray(capacity);    
    }     
    
    /**
     * Creates a new MapObjectList from another collection of MapObjects.
     * 
     * @param collection 
     */
    public MapObjectList(Collection<? extends MapObject> collection) {
        this(collection.size());
        
        Object[] dumpArray = collection.toArray();

        if (dumpArray.length != 0) {

            if (dumpArray.length > array.length - lastIndex) {
                growAtEnd(dumpArray.length);
            }

            System.arraycopy(dumpArray, 0, this.array, lastIndex, dumpArray.length);
            lastIndex += dumpArray.length;
            modCount++;
        }
    }    
    
    /**
     * Returns if all the MapObjects are instances of VectorObjects
     * 
     * @return 
     */
    public boolean areAllVectorObjects() {
       boolean  result = false;
       
       for (int i = firstIndex; i < lastIndex; i++) {
           if (array[i] instanceof VectorObject) {
               result = true;
           } else {
               result = false;
               break;
           }
       }
       
       return result;
    }
    
    /**
     * Inserts the specified object into this {@code MapObjectList} at the 
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
    public void add(int location, MapObject object) {
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
     * Adds the specified object at the end of this MapObjectList unless an
     * instance already exists in the list.
     *
     * @param object
     *          The MapObject to add.
     * @return 
     *          If the object was added or not.
     */
    @Override
    public boolean add(MapObject newObject) {
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
            System.err.println("Error in MapObjectList.add() - " + e);
            return false;
        }
    }    
    
    /**
     * Adds the objects in the specified collection to this 
     * {@code MapObjectList}.
     *
     * @param collection
     *            the collection of objects.
     * @return {@code true} if this {@code MapObjectList} is modified, 
     *          {@code false} otherwise.
    */
    @Override
    public boolean addAll(Collection<? extends MapObject> collection) {
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
     * Returns a new {@code MapObjectList} with the same elements, the same 
     * size and the same capacity as this {@code MapObjectList}.
     *
     * @return a shallow copy of this {@code MapObjectList}
     * @see java.lang.Cloneable
    */
    @Override
    @SuppressWarnings("unchecked")
    public MapObjectList clone() {
        try {
            MapObjectList<MapObject> newList = (MapObjectList<MapObject>) super.clone();
            newList.array = array.clone();
            return newList;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
    
    /**
     * Returns if the supplied MapObject already exists in the list.
     * 
     * @param object
     *      The MapObject to check.
     * 
     * @return If the MapObject exist in the list or not.
     */
    public boolean contains(co.foldingmap.map.MapObject object) {
        boolean                   result = false;
        co.foldingmap.map.MapObject  currentObject;
                
        for (int i = firstIndex; i < lastIndex; i++) {
            currentObject = (co.foldingmap.map.MapObject) array[i];
            
            if (currentObject == object) {
                result = true;
                break;
            }                
        }
        
        return result;
    }         
    
    /**
     * Adds the specified object at the end of this MapObjectList even if an
     * instance already exists in the list.
     *
     * @param object
     *            the MapObject to add.
     * @return always true
     */
    public boolean forceAdd(MapObject object) {
        try {
            if (lastIndex == array.length) {
                growAtEnd(1);
            }

            array[lastIndex++] = object;
            modCount++;          
            
            return true;
        } catch (Exception e) {
            System.err.println("Error in MapObjectList.forceAdd(Coordinate) - " + e);
            return false;
        }
    }    
    
    /**
     * Returns the MapObject at the given list location.
     * 
     * @param location
     * @return 
     */
    @Override
    public MapObject get(int location) {
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
    public MapObject[] getArray() {        
        return array;
    }
    
    /**
     * Returns a new MspObjectList with copies of each object in the original
     * list.
     * 
     * @return 
     */
    public MapObjectList<co.foldingmap.map.MapObject> getFullCopy() {
        co.foldingmap.map.MapObject                 object;
        MapObjectList<co.foldingmap.map.MapObject>  copy;
        
        copy = new MapObjectList<co.foldingmap.map.MapObject>();
        
        for (int i = firstIndex; i < lastIndex; i++) {
            object = (co.foldingmap.map.MapObject) array[i]; 
            copy.add((co.foldingmap.map.MapObject) object.copy());
        }
        
        return copy;
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

            MapObject[] newArray = newElementArray(size + increment);

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
            
            MapObject[] newArray = newElementArray(size + increment);

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

        MapObject[] newArray = newElementArray(size + increment);
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
    
    @SuppressWarnings("unchecked")
    private MapObject[] newElementArray(int size) {
        return (MapObject[]) new Object[size];
    }  
    
    /**
     * Returns the index in the list of a given MapObject.
     * 
     * @param object
     * @return 
     */
    public int indexOf(co.foldingmap.map.MapObject object) {
        co.foldingmap.map.MapObject  currentObject;
        int                       index = -1;
        
        for (int i = firstIndex; i < lastIndex; i++) {
            currentObject = (co.foldingmap.map.MapObject) array[i];
            
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
    public MapObject lastElement() {
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
    public MapObject remove(int location) {
        MapObject   result;
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
     * Removes a MapObject from this List.
     * 
     * @param object
     * @return If the object was removed or not.
     */
    @Override
    public boolean remove(Object object) {
        boolean removed;
        
        removed = super.remove(object);
        
        return removed;
    }    
    
    /**
     * Returns the number of elements in this MapObjectList.
     *
     * @return the number of elements in this MapObjectList.
    */
    @Override
    public int size() {
        return lastIndex - firstIndex;
    }        
}
