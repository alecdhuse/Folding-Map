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
import co.foldingmap.map.vector.VectorObjectList;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author Alec
 */
public class MapUtilities {
    
    /**
     * Returns the the current time in the yyyy-MM-dd'T'HH:mm:ssZ format.
     * 
     * @return 
     */
    public static String getCurrentTimestamp() {
        long             currentTime;
        SimpleDateFormat timestampDateFormat;
                
        currentTime         = Calendar.getInstance().getTimeInMillis();
        timestampDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        timestampDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
                
        return timestampDateFormat.format(currentTime)  + "Z";          
    }
    
    /**
     * Returns a list of all the Object classes used by the given ObjectList.
     * 
     * @param objects
     * @return 
     */
    public static ArrayList<String> getObjectClasses(VectorObjectList<VectorObject> objects) {
        ArrayList<String>  objectClasses;
        String[]           result;
        
        objectClasses = new ArrayList<String>();
                
        for (VectorObject object: objects) {
            if (!objectClasses.contains(object.getObjectClass()))
                objectClasses.add(object.getObjectClass());
        }      
        
        return objectClasses;        
    }
    
    /**
     * Takes a longitude and insures that: -180 >= longitude <= 180
     * 
     * @param longitude
     * @return 
     */
    public static double normalizeLongitude(double longitude) {
        if (longitude > 180) {
            while (longitude > 180)
                longitude = (longitude - 180) + (-180);            
        } else if (longitude < -180) {
            while (longitude < -180)
                longitude = 180 - (longitude + 180);
        }          
        
        return longitude;
    }
    
    /**
     * Sets this Coordinates TimeStamp
     * 
     * @param  newTimestamp as a String in the format: yyyy-MM-dd'T'HH:mm:ssZ
     * 
     * @return Date         
     *         The Date representation of the parameter String.  Returns null 
     *         if the string cannot be parsed.
     */
    public static Date parseTimestamp(String newTimestamp) {
        GregorianCalendar   calendar;
        int                 dateEndIndex;
        int                 year, month,  day;
        int                 hour, minute, second;
        long                timestamp;
        String              date, time;

        try {
            timestamp    = 0;
            dateEndIndex = newTimestamp.indexOf("T");
            date         = newTimestamp.substring(0, dateEndIndex);
            time         = newTimestamp.substring(dateEndIndex + 1, newTimestamp.length() - 1);

            year         = Integer.parseInt(date.substring(0,4));
            month        = Integer.parseInt(date.substring(5,7));
            day          = Integer.parseInt(date.substring(8,10));

            hour         = Integer.parseInt(time.substring(0,2));
            minute       = Integer.parseInt(time.substring(3,5));
            second       = Integer.parseInt(time.substring(6,8));

            /* there is a problem somewhere that causes the month and hour to increment by one,
             * this code combats it untill the reson can be found */
                if (month > 1) {
                    month--;
                } else {
                    month = 12;
                    year--;
                }

                if (hour > 1) {
                    hour--;
                } else {
                    hour = 23;
                    day--;
                }
            //end fix code            
            
            calendar     = new GregorianCalendar();
            
            calendar.setTimeZone(new SimpleTimeZone(0, "Z"));
            calendar.set(year, month, day, hour, minute, second);
           
            timestamp = calendar.getTimeInMillis();
            
            return new Date(timestamp);
        } catch (Exception e) {
            System.out.println("Error in MapUtilities.parseTimestamp(String) - " + e);
            return null;
        }
    }    
}
