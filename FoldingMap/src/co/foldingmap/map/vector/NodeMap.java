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
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Used to replace HashMap when keeping track of nodes, as HashMap is too slow.
 * This implementation is 1:1
 * 
 * @author Alec
 */
public class NodeMap {
    private int          lastIndex; //should always be and empty index
    private long[]       keys;
    private Coordinate[] values;
    
    public NodeMap() {
        init(1000);
        lastIndex = 1;
    }
    
    public NodeMap(int size) {
        lastIndex = 1;
        
        if (size > 0) {
            init(size);
        } else {
            init(1000);
        }
    }   
    
    /**
     * Returns the Key assigned to a given value;
     * 
     * @param value
     * @return Returns -1 if no coordinate is found.
     */
    public long findKey(Coordinate value) {
        Coordinate c;
        
        try {
            for (int i = 0; i < lastIndex; i++) {
                c = values[i];

                if (c != null) {
                    if (c.equals(value)) 
                        return keys[i];        
                } else {
                    return -1;
                }
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in NodeMap.findKey(Coordinate) - " + e);
        }
        
        return 0;
    }
    
    /**
     * Returns a coordinate with a given Key.
     * 
     * @param key
     * @return 
     */
    public Coordinate get(long key) {
        Coordinate  c;
        int         index;
        
        try {
            index = Arrays.binarySearch(keys, 0, lastIndex, key);

            if (index >= 0) { 
                c = values[index];
                c.incrementPullCount();
                return c;
            } else {
                index = Math.abs(index);
                
                if (index < lastIndex) {
                    if (keys[index] == key) {
                        c = values[index];
                        c.incrementPullCount();
                        return c;                    
                    } else {
                        for (int i = lastIndex - 1; i > 0; i--) {
                            if (keys[i] == key) {
                                c = values[i];
                                c.incrementPullCount();
                                return c;                                    
                            }
                        }
                        
                        return null;
                    }
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in NodeMap.get(long) - " + e);
            return null;
        }
    }
    
    /**
     * Returns an Array of all the Coordinates in this NodeMap.
     * 
     * @return 
     */
    public Coordinate[] getAllCoordinates() {
        return values;
    }
    
    /**
     * Returns a LatLonBox that bounds all the coordinates in this NodeMap.
     * 
     * @return 
     */
    public LatLonBox getBounds() {
        double north = -90;
        double south = 90;
        double east  = -180;
        double west  = 180;
        
        for (int i = 0; i < lastIndex; i++) {
            Coordinate c = values[i];
            
            if (c.getLatitude() >= -90 && c.getLatitude() <= 90) {
                if (c.getLatitude()  > north) north = c.getLatitude();
                if (c.getLatitude()  < south) south = c.getLatitude();
            }
            
            if (c.getLongitude() >= -180 && c.getLongitude() <= 180) {
                if (c.getLongitude() < west)  west  = c.getLongitude();
                if (c.getLongitude() > east)  east  = c.getLongitude();
            }
        }
        
        return new LatLonBox((float) north, (float) south, (float) east, (float) west);
    }
    
    /**
     * Returns an ArrayList<Coordinate> of the Coordinates contained in the 
     * given LatLonBox boundary.
     * 
     * @param boundary
     * @return 
     */
    public ArrayList<Coordinate> getCoordinatesWithinBoundary(LatLonBox boundary) {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        Coordinate            c;
        
        for (int i = 0; i < lastIndex; i++) {
            c = values[i];        
            
            if (boundary.contains(c))
                coordinates.add(c);
        }
        
        return coordinates;
    }
    
    /**
     * Returns the coordinate at a given Index;
     * 
     * @param index
     * @return 
     */
    public Coordinate getFromIndex(int index) {
        Coordinate c = values[index];
        
        if (c != null)
            c.incrementPullCount();
        
        return c;
    }
    
    /**
     * Returns the Key at a given Index
     * 
     * @param index
     * @return 
     */
    public long getKeyFromIndex(int index) {
        return keys[index];
    }
    
    /**
     * Returns the array index for a given key.
     * 
     * @param key
     * @return 
     */
    public int getKeyIndex(long key) {
        return Arrays.binarySearch(keys, 0, lastIndex, key);
    }
                
    /**
     * Returns the Value at a given index
     * 
     * @param index
     * @return 
     */
    public Coordinate getValueFromIndex(int index) {
        Coordinate c = values[index];
        
        c.incrementPullCount();
        return c;
    }
    
    private void growAtEnd(int required) {
        long[]       newKeys   = new long[required];
        Coordinate[] newValues = new Coordinate[required];
        
        System.arraycopy(keys,   0, newKeys,   0, lastIndex);
        System.arraycopy(values, 0, newValues, 0, lastIndex);
        
        this.keys   = newKeys;
        this.values = newValues;       
    }
    
    private void growForInsert(int location, int required) {
        long[]       newKeys   = new long[required];
        Coordinate[] newValues = new Coordinate[required];
         
        System.arraycopy(keys,   0, newKeys,   0, location);
        System.arraycopy(values, 0, newValues, 0, location);
        
        System.arraycopy(keys,   location, newKeys,   location + 1, lastIndex - location);
        System.arraycopy(values, location, newValues, location + 1, lastIndex - location);     
        
        keys   = newKeys;
        values = newValues;        
    }
    
    private void init(int size) {
        lastIndex = 1;
        keys      = new long[size];
        values    = new Coordinate[size];      
        
        keys[0]   = 0;
        values[0] = Coordinate.UNKNOWN_COORDINATE;
    }
    
    private void insert(int index, long key, Coordinate value) {
        if (lastIndex + 1 < keys.length) {
            long[]       newKeys   = new long[keys.length];
            Coordinate[] newValues = new Coordinate[values.length];
        
            System.arraycopy(keys,   0, newKeys,   0, index);
            System.arraycopy(values, 0, newValues, 0, index);            
            
            System.arraycopy(keys,   index, newKeys,   index + 1, lastIndex - index);
            System.arraycopy(values, index, newValues, index + 1, lastIndex - index);            
                     
            keys   = newKeys;
            values = newValues;
            
            keys[index]   = key;
            values[index] = value;    
            lastIndex++;
        } else {
            growForInsert(index, lastIndex + 50);
            keys[index]   = key;
            values[index] = value;
            lastIndex++;
        }
    }
    
    /**
     * Returns if the Node Map is empty or not;
     * 
     * @return 
     */
    public boolean isEmpty() {
        if (lastIndex == 1) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Adds a coordinate to this Map.  A key is generated from the last index.
     * 
     * @param value 
     */
    public void put(Coordinate value) {
        if (value != null) {
            if (value.getID() > 0) {
                put(value.getID(),  value);
            } else {                
                put((keys[lastIndex - 1] + 1), value);            
            }    
        } else {
            Logger.log(Logger.ERR, "NodeMap.put(Coordinate) - Null Coordinate");
        }
    }    
    
    /**
     * Adds a coordinate with a given key to this Map.
     * 
     * @param key
     * @param value 
     */
    public void put(long key, Coordinate value) {
        boolean keyInserted;
        int     currentIndex;
        long    currentKey;

        try {
            keyInserted = false;
            value.setId(key);
            
            if (lastIndex == 1) {
                keys[lastIndex]   = key;
                values[lastIndex] = value;
                lastIndex++;
            } else {
                currentKey = keys[lastIndex - 1];

                //Check to see if we can just add to the end, which should be the case most of the time.
                if (key > currentKey) {                
                    keys[lastIndex]   = key;
                    values[lastIndex] = value;                
                    
                    if ((lastIndex + 1) < keys.length) {
                        //no need to grow array, just increment last index
                        lastIndex++;
                    } else {
                        lastIndex++;
                        growAtEnd(lastIndex + 500);                                                
                    }
                } else {                    
                    currentIndex = Arrays.binarySearch(keys, 0, lastIndex, key);                                            
    
                    if (currentIndex > 0) {
                        currentKey   = keys[currentIndex];

                        if (currentKey != key) {
                            insert(currentIndex, key, value);
                        } else {
                            //Key already exists                            
                            if (values[currentIndex].equals(value)) {
                                //coordinates are the same, do nothing
                            } else {
                                //overwrite/update Coordinate
                                Logger.log(Logger.INFO, "NodeMap - Node Updated, old: " + values[currentIndex] + " new: " + value);      
                                
                                //Put the new values in the old Coordinate
                                values[currentIndex].update((float) value.getAltitude(), (float) value.getLatitude(), (float) value.getLongitude(), value.getTimestampValue());
                                
                                //Upate the reference of the new Coordinate to the now update 'old' Coordinate.
                                value = values[currentIndex];
                            }
                        }
                    } else if (currentIndex < 0) {
                        //Key not present insert it.
                        currentIndex = Math.abs(currentIndex) - 1;
                        
                        if (keys[currentIndex - 1] < key) {                        
                            //we found the right place for it.
                            insert(currentIndex, key, value); 
                        } else {
                            //we didn't find the right place for it, search.
                            for (int i = lastIndex - 1; i >= 0; i--) {
                                if (i == 0) {
                                    insert(i+1, key, value);
                                    keyInserted = true;
                                    break;                                   
                                } else if (key > keys[i]) { 
                                    insert(i+1, key, value);   
                                    keyInserted = true;
                                    break;
                                }
                            }
                            
                            if (keyInserted == false)
                                Logger.log(Logger.ERR, "Error in NodeMap.put(long, Coordinate) - Insert Error Key: " + key);                                                                       
                        }                                                                             
                    } else {
                        //something went wrong
                        Logger.log(Logger.ERR, "Error in NodeMap.put(long, Coordinate) - Insert Error Key: " + key);
                    }                                           
                }
            }
            
            if (keys.length == lastIndex) 
                growAtEnd(lastIndex + 500);
            
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in NodeMap.put(long, Coordinate) - " + e + " (" + key + " , " + value.toString() + ")");
        }
    }
    
    /**
     * Returns the number of elements in this map.
     * 
     * @return 
     */
    public int size() {
        return lastIndex - 1;
    }
}
