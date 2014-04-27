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

import java.util.ArrayList;

/**
 * Manages all the map themes available for use.
 * 
 * @author Alec
 */
public class MapThemeManager {
    ArrayList<MapTheme>    mapThemes;
    
    /**
     * Basic constructor, adds the hard coded themes in.
     * 
     */
    public MapThemeManager() {
        mapThemes = new ArrayList<MapTheme>();
        
        mapThemes.add(new Climbing());
        mapThemes.add(new Night());
        mapThemes.add(new Taxi());
        mapThemes.add(new Toner());
        mapThemes.add(new Trail());
        mapThemes.add(new Web());        
    }
    
    /**
     * Adds a theme to this manager.
     * 
     * @param newTheme 
     */
    public void addTheme(MapTheme newTheme) {
        mapThemes.add(newTheme);
    }

    /**
     * Returns all the themes in an ArrayList.
     * 
     * @return 
     */
    public ArrayList<MapTheme> getAllThemes() {
        return mapThemes;
    }
    
    /**
     * Returns a Theme with the given name;
     * Null if theme does not exist.
     * 
     * @param themeName
     * @return 
     */
    public MapTheme getTheme(String themeName) {
        MapTheme    returnTheme = null;
        
        for (MapTheme theme: mapThemes) {
            if (theme.getName().equalsIgnoreCase(themeName)) 
                returnTheme = theme;            
        }//end for loop
        
        return returnTheme;
    }    
}
