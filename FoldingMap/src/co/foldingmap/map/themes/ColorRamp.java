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
package co.foldingmap.map.themes;

import co.foldingmap.xml.XmlOutput;
import java.awt.Color;
import java.util.*;

/**
 *
 * @author Alec
 */
public class ColorRamp {
    protected Color                  defaultColor;
    protected HashMap<String, Color> colorMap;
    protected String                 id;
    
    public ColorRamp(String id) {
        this.id = id;        
        init(40);
    }
    
    public ColorRamp(String id, int size) {
        this.id = id;        
        init(size);
    }    
    
    /**
     * Adds an entry for this ColorRamp.
     * 
     * @param key   The key for the provided Color.
     * @param value The Color for for this Key.
     */
    public void addEntry(String key, Color value) {
        colorMap.put(key, value);
    }
    
    /**
     * Returns the Color mapped to the provided key.  If no color is present for
     * that key then the default color is returned.
     * 
     * @param key
     * @return 
     */
    public Color getColor(String key) {
        Color returnColor;
        
        returnColor = colorMap.get(key);
        
        if (returnColor != null) {
            return returnColor;
        } else {
            return defaultColor;
        }
    }
    
    /**
     * Returns a Collection of all the Colors.
     * @return 
     */
    public Collection<Color> getColors() {
        return colorMap.values();
    }
        
    
    /**
     * Returns the ID for this ColorRamp.
     * 
     * @return 
     */
    public String getID() {
        return this.id;
    }
    
    /**
     * Returns a Set of all the Keys.
     * 
     * @return 
     */
    public Set<String> getKeySet() {
        return colorMap.keySet();
    }
    
    private void init(int size) {
        colorMap     = new HashMap<String, Color>(size);
        defaultColor = new Color (68, 68, 68);
    }
    
    /**
     * Sets the default Color to be returned if there is no Color for a given
     * Key.
     * 
     * @param defaultColor 
     */
    public void setDefaultColor(Color defaultColor) {
        this.defaultColor = defaultColor;
    }
    
    /**
     * Writes this ColorRamp to FmXML.
     * 
     * @param kmlWriter 
     */
    public void toXML(XmlOutput xmlWriter) {    
        Iterator        it;
        Set             set;  
        
        set = colorMap.entrySet();
        it  = set.iterator();        
        
        xmlWriter.openTag("Style id=\"" + id + "\"");
        xmlWriter.openTag("ColorRamp");
        
        //write default Color
        xmlWriter.writeTag("Default", ColorHelper.getColorHexStandard(defaultColor));
        
        //Write all other colors
        while (it.hasNext()) {
          Map.Entry entry = (Map.Entry) it.next();          
          xmlWriter.writePairTag((String) entry.getKey(), ColorHelper.getColorHexStandard((Color) entry.getValue()));
        }          
        
        xmlWriter.closeTag("ColorRamp");
        xmlWriter.closeTag("Style");
    }
}
