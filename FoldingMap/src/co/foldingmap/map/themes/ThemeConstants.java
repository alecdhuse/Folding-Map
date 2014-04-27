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

/**
 *
 * @author Alec
 */
public abstract class ThemeConstants {
    
    //Line Stroke Styles
    public static final String   SOLID         = "Solid";
    public static final String   SOLID_DASHED  = "Solid-Dashed";
    public static final String   DASHED        = "Dashed";
    public static final String   DOTTED        = "Dotted";
    public static final String   DASH_DOT      = "Dash-Dotted";  
    public static final String   IN_DASH       = "In-Dash";  
    public static final String[] STROKE_STYLES = {"Solid", "Solid-Dashed", "Dashed", "Dotted", "Dash-Dotted"};
    
    public static final float    DASHED_STYLE[]        = { 6.0f };
    public static final float    IN_DASHED_STYLE[]     = { 6.0f };
    public static final float    SOLID_DASHED_STYLE[]  = {10.0f, 16.0f };       
    
    //Class Feature Types
    public static final String   ANY           = "Any";
    public static final String   LAND          = "Land";
    public static final String   NONE          = "None";
    public static final String   ROAD          = "Road";
    public static final String   WATER         = "Water"; 
    public static final String   UNKNOWN       = "Unknown";    
}
