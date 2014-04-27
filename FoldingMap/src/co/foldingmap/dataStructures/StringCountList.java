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
package co.foldingmap.dataStructures;

import co.foldingmap.Logger;

/**
 *
 * @author Alec
 */
public class StringCountList {
    private int         lastIndex; //should always be empty
    private int[]       counts; 
    private String[]    values;
    
    public StringCountList() {
        init(4);
    }
    
    /**
     * Adds a String with a a count of 1.
     * 
     * @param key
     * @param value 
     */
    private void add(String s) {            
        try {            
            counts[lastIndex] = 1;
            values[lastIndex] = s;
            lastIndex++;
            
            if (lastIndex >= values.length) {
                this.growAtEnd(values.length + 2);
            }
            
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in StringCountList.add(String) - " + e);
        }
    }    
    
    /**
     * If the list contains the String the count is incremented.  If the list
     * does not contain the String then it is added with an initial count of 1.
     * 
     * @param s 
     */
    public void addIncrement(String s) {
        int index = indexOf(s);
        
        if (index >= 0) {
            //value exists, increment count
            counts[index]++;
        } else {
            //add the value, with init count = 1
            add(s);
        }
    }
    
    /**
     * Returns the index of a given String in this list.
     * Returns -1 if the String is not fount.
     * 
     * @param s
     * @return 
     */
    public int indexOf(String s) {
        try {
            for (int i = 0; i < lastIndex; i++) {
                if (values[i].equals(s)) {
                    return i;
                }
            }
        } catch (Exception e) {
            
        }
        
        //Not Found
        return -1;
    }
    
    /**
     * Returns the sum of all the counts.
     * 
     * @return 
     */
    public int getCountTotal() {
        int countTotal = 0;
        
        for (int i = 0; i < this.lastIndex; i++) {
            countTotal += counts[i];
        }
        
        return countTotal;
    }
    
    /**
     * Returns the count of the highest occurrence.
     * 
     * @return 
     */
    public int getHighestCount() {
        int     highCount   = -1;
        
        for (int i = 0; i < this.lastIndex; i++) {
            if (counts[i] > highCount) {
                highCount = counts[i];
            }
        }
        
        return highCount;
    }
    
    /**
     * Returns the String with the highest count.
     * If there is a tie, a blank String is returned.
     * 
     * @param ignoreString A String to ignore when finding the highest.
     * @return 
     */
    public String getMostOccurringString(String ignoreString) {
        boolean tie         = false;
        int     highCount   = -1;
        String  highValue   = "";
        
        for (int i = 0; i < this.lastIndex; i++) {
            if (!values[i].equals(ignoreString)) {
                if (counts[i] > highCount) {
                    highCount = counts[i];
                    highValue = values[i];
                    tie       = false;
                } else if (counts[i] == highCount) {
                    //tie = true;
                }
            }
        }
        
        if (tie) {
            return "";
        } else {
            return highValue;
        }
    }
    
    /**
     * Returns the amount the highest occurrence is different from the second 
     * highest.
     * 
     * @return 
     */
    public int getLeadDifference() {
        String[] topTwo = getTopTwo();
        int      count0, count1;
        int      index0, index1;
        
        index0 = indexOf(topTwo[0]);
        index1 = indexOf(topTwo[1]);
        
        if (index0 >= 0) {
            count0 = counts[index0];
        } else {
            count0 = 0;
        }
        
        if (index1 >= 0)  {
            count1 = counts[index1];
        } else {
            count1 = 0;
        }
        
        return count0 - count1;       
    }
    
    /**
     * Returns the top two highest counts, with the highest being first.
     * 
     * @return 
     */
    public String[] getTopTwo() {
        String[]    topTwo = new String[2];
        
        topTwo[0] = getMostOccurringString("");
        topTwo[1] = getMostOccurringString(topTwo[0]);
        
        return topTwo;
    }
    
    private void growAtEnd(int required) {
        int[]    newCounts = new int[required];
        String[] newValues = new String[required];
        
        System.arraycopy(counts, 0, newCounts, 0, lastIndex);
        System.arraycopy(values, 0, newValues, 0, lastIndex);
        
        this.counts = newCounts;
        this.values = newValues;       
    }    
    
    private void growForInsert(int location, int required) {
        int[]    newCounts = new int[required];
        String[] newValues = new String[required];
        
        System.arraycopy(counts, 0, newCounts, 0, location - 1);
        System.arraycopy(values, 0, newValues, 0, location - 1);
        
        System.arraycopy(counts, location, newCounts, location + 1, lastIndex);
        System.arraycopy(values, location, newValues, location + 1, lastIndex);         
    }    
    
    private void init(int size) {
        lastIndex = 0;
        counts    = new int[size];
        values    = new String[size];      
    }    
}
