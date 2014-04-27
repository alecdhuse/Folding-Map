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

import co.foldingmap.Logger;
import co.foldingmap.map.vector.LatLonAltBox;
import co.foldingmap.xml.XmlOutput;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import javax.swing.JMenuItem;

/**
 * This is the abstract parent class for all the Map Layers.
 * 
 * @author Alec
 */
public abstract class Layer {
    protected boolean                   locked, visible;
    protected Date                      timeSpanBegin, timeSpanEnd;
    protected DigitalMap                parentMap;
    protected LayerPropertiesPanel      layerPropertiesPanel;
    protected String                    layerDescription, layerName;

    public abstract void                closeLayer();
    public abstract Layer               copy();
    public abstract void                drawLayer(Graphics2D g2, MapView mView);
    public abstract LatLonAltBox        getBoundary();
    public abstract float               getCenterLongitude();
    public abstract float               getCenterLatitude();
    public abstract JMenuItem[]         getContextMenuItems();
    public abstract MapObjectList       selectObjects(Rectangle2D range);
    public abstract void                toXML(XmlOutput kmlWriter);        
    
    /**
     * Returns the description for this Layer.
     * 
     * @return 
     */
    public String getDescription() {
        return layerDescription;
    }    
    
    /**
     * Returns the LayerPropertiesPanel for this Layer.
     * 
     * @return 
     */
    public LayerPropertiesPanel getLayerPropertiesPanel() {
        return layerPropertiesPanel;
    }    
    
    /**
     * Returns the name for this Layer.
     * 
     * @return 
     */
    public String getName() {
        return layerName;
    }    
    
    /**
     * Returns the map that holds this layer.
     * 
     * @return 
     */
    public DigitalMap getParentMap() {
        return parentMap;
    }
    
    /**
     * Returns the starting date for the Layer Time-span.
     * 
     * @return 
     */
    public Date getTimeSpanBegin() {
        return timeSpanBegin;
    }  
    
    /**
     * Returns the ending date for the Ayer Time-span.
     * @return 
     */
    public Date getTimeSpanEnd() {
        return timeSpanEnd;
    }    
    
    /**
     * Returns is this Layer is using a time span.
     * 
     * @return 
     */
    public boolean hasTimeSpan() {
        if (timeSpanBegin == null || timeSpanEnd == null) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Returns if this layer is locked, meaning it cannot be edited.  
     * @return 
     */
    public boolean isLocked() {
        return locked;
    }    
    
    /**
     * Returns if this layer is visible when drawing the map.
     * 
     * @return 
     */
    public boolean isVisible() {
        return visible;
    }    
    
    /**
     * Parses a String date in the format of yyyy-MM-dd'T'HH:mm:ssZ to a date object.
     * 
     * @param newTimestamp
     * @return 
     */
    public static Date parseTimestamp(String newTimestamp) {
        Date                returnDate;
        GregorianCalendar   calendar;
        int                 dateEndIndex;
        int                 year, month,  day;
        int                 hour, minute, second;
        String              date, time;

        returnDate   = new Date(0);

        try {
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

            calendar   = new GregorianCalendar(year, month, day, hour, minute, second);
            calendar.setTimeZone(new SimpleTimeZone(0, "Z"));
            returnDate = calendar.getTime();
        } catch (Exception e) {
            Logger.log(Logger.ERR,"Error in Layer.parseTimestamp - " + e);
        }

        return returnDate;
    } 
    
    /**
     * Sets the description for this Layer.
     * 
     * @param layerDescription 
     */
    public void setLayerDescription(String layerDescription) {
        this.layerDescription = layerDescription;
    }    
    
    /**
     * Sets the LayerPropertiesPanel for this layer.
     * 
     * @param lpPanel 
     */    
    public void setLayerPropertiesPanel(LayerPropertiesPanel lpPanel) {
        layerPropertiesPanel = lpPanel;
    }    
    
    /**
     * Sets if this layer is locked, if it can be edited.
     * 
     * @param locked 
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }    
    
    /**
     * Sets the name for this Layer.
     * 
     * @param name 
     */
    public void setName(String name) {
        this.layerName = name;
    }    

    /**
     * Sets the Map that this layer is contained in.
     * 
     * @param parentMap 
     */
    public void setParentMap(DigitalMap parentMap) {
        this.parentMap = parentMap;
    }
    
    /**
     * Sets the starting time for the Layer Time-span.
     * 
     * @param timestamp 
     */
    public void setTimeSpanBegin(Date timestamp) {
        timeSpanBegin = (timestamp);
    }    
    
    /**
     * Sets the starting time for the Layer Time-span.
     * 
     * @param timestamp 
     */    
    public void setTimeSpanBegin(String timestamp) {
        timeSpanBegin = parseTimestamp(timestamp);
    }    
    
    /**
     * Sets the ending time for the Layer Time-span.
     * 
     * @param timestamp 
     */      
    public void setTimeSpanEnd(Date timestamp) {
        timeSpanEnd = (timestamp);
    }

    /**
     * Sets the ending time for the Layer Time-span.
     * 
     * @param timestamp 
     */     
    public void setTimeSpanEnd(String timestamp) {
        timeSpanEnd = parseTimestamp(timestamp);
    }

    /**
     * Sets if this Layer is drawn when drawing the map.
     * 
     * @param visible 
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Returns the Layer name.  Mainly used for the layers side menu.
     * 
     * @return 
     */
    @Override
    public String toString() {
        return layerName;
    }    
}
