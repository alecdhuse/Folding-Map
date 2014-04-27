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

import java.awt.Color;

/**
 *
 * @author Alec
 */
public class Toner extends MapTheme {
    
    public Toner() {
        super("Toner");
        
        backgroundColor  = Color.WHITE; 
                
        //Labels
        LabelStyle primaryHighwayLabel   = new LabelStyle(Color.WHITE,           new Color(68, 68, 68, 128));
        LabelStyle secondaryHighwayLabel = new LabelStyle(Color.WHITE,           new Color(68, 68, 68, 128));
        LabelStyle cityStreetLabel       = new LabelStyle(new Color(75, 68, 60), Color.white);
         
        //IconStyle
        iconStyles.putAll(DefaultIconSet.getSmallSet(resourceHelper));        
        addStyleElement(new IconStyle("(Unspecified Point)",           new Color(68, 68, 68, 128)));
        addStyleElement(new IconStyle("Amenity",                       new Color(68, 68, 68, 128),  noLabel,        lvl1));
        addStyleElement(new IconStyle("Building",                      new Color(68, 68, 68, 128),  noLabel,        lvl1));
        addStyleElement(new IconStyle("Place - City",                  Color.BLACK,                 DEFAULT_LABEL,  lvl5));
        addStyleElement(new IconStyle("Place - Town",                  Color.BLACK,                 DEFAULT_LABEL,  lvl2));
        addStyleElement(new IconStyle("Place - Suburb",                Color.BLACK,                 DEFAULT_LABEL,  lvl2));
        addStyleElement(new IconStyle("Place - Village",               Color.BLACK,                 DEFAULT_LABEL,  lvl2));
        addStyleElement(new IconStyle("Labeled Point",                 Color.BLACK,                 DEFAULT_LABEL,  null));
        addStyleElement(new IconStyle("NGO Office",                    Color.BLACK,                 noLabel,        lvl1));
        addStyleElement(new IconStyle("Phone",                         Color.BLACK,                 noLabel,        lvl1));
        addStyleElement(new IconStyle("Public Toilets",                Color.BLACK));
        addStyleElement(new IconStyle("Tourist Attraction",            new Color(153, 179, 204),    noLabel,        lvl1));
                
        //LineStyles                              
        addStyleElement(new LineStyle("(Unspecified Linestring)",        Color.BLACK,     1.0f, LineStyle.SOLID,  false));
        addStyleElement(new LineStyle("Border - Country Border",         Color.DARK_GRAY, 1.2f, LineStyle.SOLID,  false));
        addStyleElement(new LineStyle("Border - Inter-Country",          Color.DARK_GRAY, 1.2f, LineStyle.DASHED, false));
        addStyleElement(new LineStyle("Coastline",                       Color.GRAY,      1.2f, LineStyle.SOLID,  false));
        addStyleElement(new LineStyle("Ferry Line",                      Color.WHITE,              Color.BLACK, 1.5f, LineStyle.DASHED));
        addStyleElement(new LineStyle("Hiking Trail",                    Color.BLACK,              Color.GRAY,  0.4f, LineStyle.DASHED));
        addStyleElement(new LineStyle("Path - Steps",                    Color.BLACK,              Color.GRAY,  0.4f, LineStyle.SOLID));
        addStyleElement(new LineStyle("Pier",                            Color.WHITE,              Color.BLACK, 0.8f, LineStyle.SOLID));
        addStyleElement(new LineStyle("Rail Line",                       Color.WHITE,              Color.BLACK, 1.2f, LineStyle.SOLID_DASHED, null, null));
        addStyleElement(new LineStyle("Road - Unclassified",             new Color(231, 231, 231), Color.GRAY,  1.5f, LineStyle.SOLID));
        addStyleElement(new LineStyle("Road - City Primary",             new Color(231, 231, 231), Color.GRAY,  1.5f, LineStyle.SOLID,        cityStreetLabel));
        addStyleElement(new LineStyle("Road - City Secondary",           new Color(231, 231, 231), Color.GRAY,  1.3f, LineStyle.SOLID,        cityStreetLabel));
        addStyleElement(new LineStyle("Road - City Tertiary",            new Color(231, 231, 231), Color.GRAY,  0.5f, LineStyle.SOLID,        cityStreetLabel));
        addStyleElement(new LineStyle("Road - Motorway",                 Color.BLACK,              Color.GRAY,  2.0f, LineStyle.SOLID,        primaryHighwayLabel));
        addStyleElement(new LineStyle("Road - Motorway Link",            Color.BLACK,              Color.GRAY,  1.5f, LineStyle.SOLID));        
        addStyleElement(new LineStyle("Road - Primary Highway",          Color.BLACK,              Color.GRAY,  2.0f, LineStyle.SOLID,        primaryHighwayLabel));
        addStyleElement(new LineStyle("Road - Primary Highway Link",     Color.BLACK,              Color.GRAY,  1.5f, LineStyle.SOLID));
        addStyleElement(new LineStyle("Road - Secondary Highway",        Color.BLACK,              Color.GRAY,  1.8f, LineStyle.SOLID,        secondaryHighwayLabel));
        addStyleElement(new LineStyle("Rock Face",                       Color.GRAY,      1.5f, LineStyle.DASHED, true));
        addStyleElement(new LineStyle("Territorial Boundary",            Color.GRAY,      1.5f, LineStyle.DASHED, false));            
        addStyleElement(new LineStyle("Water Way - Intermittent Stream", Color.GRAY,      1.2f, LineStyle.DASHED, false));
        addStyleElement(new LineStyle("Water Way - River",               Color.GRAY,      3.0f, LineStyle.SOLID,  true));
        addStyleElement(new LineStyle("Water Way - Stream",              Color.GRAY,      1.2f, LineStyle.SOLID,  false));   
        
        //polystyle
        addStyleElement(new PolygonStyle("(Unspecified Polygon)", Color.WHITE, Color.BLACK));
        addStyleElement(new PolygonStyle("Agricultural Plot",     new Color(190, 190, 190), Color.BLACK, ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Building",              Color.WHITE, Color.BLACK));
        addStyleElement(new PolygonStyle("Commercial Area",       Color.WHITE, Color.BLACK, ThemeConstants.LAND));  
        addStyleElement(new PolygonStyle("Country - Filled",      Color.WHITE, new Color(0, 0, 0, 128), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Default Backdrop",      Color.GRAY));
        addStyleElement(new PolygonStyle("Forest",                new Color(190, 190, 190), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Grass Field",           new Color(190, 190, 190), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Industrial Area",       Color.WHITE, Color.BLACK, ThemeConstants.LAND));  
        addStyleElement(new PolygonStyle("Island",                Color.WHITE, Color.BLACK, ThemeConstants.LAND));          
        addStyleElement(new PolygonStyle("Lake",                  Color.DARK_GRAY,          ThemeConstants.WATER));
        addStyleElement(new PolygonStyle("Market",                Color.WHITE, Color.BLACK, ThemeConstants.LAND));    
        addStyleElement(new PolygonStyle("Ocean",                 Color.DARK_GRAY,          ThemeConstants.WATER));
        addStyleElement(new PolygonStyle("Park",                  Color.WHITE,              new Color(190, 190, 190), ThemeConstants.LAND, "toner_park-fill.png"));
        addStyleElement(new PolygonStyle("Parking Lot",           Color.WHITE, Color.BLACK, ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Protected Area",        new Color(190, 190, 190), ThemeConstants.LAND)); 
        addStyleElement(new PolygonStyle("Residential Area",      Color.WHITE, Color.BLACK, ThemeConstants.LAND));  
        addStyleElement(new PolygonStyle("River",                 Color.GRAY, ThemeConstants.WATER));
        addStyleElement(new PolygonStyle("School",                Color.WHITE, Color.BLACK, ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Small Island",          Color.WHITE, new Color(0, 0, 0, 128), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Stadium",               Color.WHITE, Color.BLACK, ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("University",            Color.WHITE, Color.BLACK, ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Wetland",               new Color(190, 190, 190), ThemeConstants.LAND));    
              
    }
}
