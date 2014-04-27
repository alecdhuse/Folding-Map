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
public class Night extends MapTheme {
    public Night() {
        super("Night");
        
        PolygonStyle buildPolyStyle;
                
        backgroundColor  = new Color(0, 0, 0);  
        pointColor       = new Color(255,255,255);
        
        LabelStyle cityRoadStandard = new LabelStyle(new Color( 0,  0,  0), Color.white);
        LabelStyle primaryHighway   = new LabelStyle(new Color( 0,  0,  0), new Color(255, 255, 255));
        LabelStyle secondaryHighway = new LabelStyle(new Color( 0,  0,  0), new Color(255, 255, 255));
                
        //IconStyle
        iconStyles.putAll(DefaultIconSet.getSmallSet(resourceHelper));        
        addStyleElement(new IconStyle("(Unspecified Point)",           new Color(185, 210, 255, 200)));
        addStyleElement(new IconStyle("Amenity",                       new Color( 68,  68,  68, 128),   noLabel,        lvl1));
        addStyleElement(new IconStyle("Building",                      new Color( 68,  68,  68, 128),   noLabel,        lvl1));
        addStyleElement(new IconStyle("Place - City",                  Color.BLACK,                     DEFAULT_LABEL,  lvl5));
        addStyleElement(new IconStyle("Place - Town",                  Color.BLACK,                     DEFAULT_LABEL,  lvl2));
        addStyleElement(new IconStyle("Place - Suburb",                Color.BLACK,                     DEFAULT_LABEL,  lvl2));
        addStyleElement(new IconStyle("Place - Village",               Color.BLACK,                     DEFAULT_LABEL,  lvl2));
        addStyleElement(new IconStyle("Labeled Point",                 Color.BLACK,                     DEFAULT_LABEL,  null));
        addStyleElement(new IconStyle("NGO Office",                    Color.BLACK,                     noLabel,        lvl1));
        addStyleElement(new IconStyle("Phone",                         Color.BLACK,                     noLabel,        lvl1));
        addStyleElement(new IconStyle("Point",                         new Color(243, 62, 62, 128)));
        addStyleElement(new IconStyle("Public Toilets",                Color.BLACK));
        addStyleElement(new IconStyle("Tourist Attraction",            new Color(153, 179, 204),        noLabel,        lvl1));
        
        //LineStyles
        addStyleElement(new LineStyle("(Unspecified Linestring)",        new Color(185, 210, 255, 128), 1.0f, LineStyle.SOLID,  false));
        addStyleElement(new LineStyle("Border - Country Border",         new Color(180, 174, 174),      1.2f, LineStyle.SOLID,  false));
        addStyleElement(new LineStyle("Border - Inter-Country",          new Color(180, 174, 174),      1.2f, LineStyle.DASHED, false));
        addStyleElement(new LineStyle("Coastline",                       new Color(234, 200, 117),      1.2f, LineStyle.SOLID,  false));
        addStyleElement(new LineStyle("Ferry Line",                      new Color(110, 113, 248),      1.5f, LineStyle.DASHED, false));
        addStyleElement(new LineStyle("Hiking Trail",                    Color.WHITE,                   new Color(68, 68, 68, 100), 1.0f, LineStyle.IN_DASH));
        addStyleElement(new LineStyle("Path - Bikeway",                  Color.WHITE,                   new Color(68, 68, 68, 128), 0.4f, LineStyle.SOLID));
        addStyleElement(new LineStyle("Path - Footway",                  Color.WHITE,                   new Color(68, 68, 68, 128), 0.4f, LineStyle.SOLID));
        addStyleElement(new LineStyle("Path - Running",                  Color.WHITE,                   new Color(68, 68, 68, 128), 0.4f, LineStyle.SOLID));
        addStyleElement(new LineStyle("Path - Steps",                    new Color(231, 230, 225),      new Color(68, 68, 68, 128), 0.4f, LineStyle.SOLID));
        addStyleElement(new LineStyle("Pier",                            new Color(244, 243, 240),      2.0f, LineStyle.SOLID, false));
        addStyleElement(new LineStyle("Power Line",                      new Color( 99,  28,  28),      1.0f, LineStyle.DOTTED, false));
        addStyleElement(new LineStyle("Rail Line",                       Color.WHITE,                   new Color(212, 208, 204),   1.2f, LineStyle.SOLID_DASHED));
        addStyleElement(new LineStyle("Rail - Platform",                 new Color(231, 230, 225),      0.5f, LineStyle.SOLID, true));
        addStyleElement(new LineStyle("Rail - Tram",                     Color.WHITE,                   new Color(212, 208, 204),   0.2f, LineStyle.SOLID_DASHED));
        addStyleElement(new LineStyle("Road - Unclassified",             Color.WHITE,                   new Color(68, 68, 68, 128), 0.9f, LineStyle.SOLID, cityRoadStandard));
        addStyleElement(new LineStyle("Road - City Primary",             Color.WHITE,                   new Color(68, 68, 68, 128), 1.5f, LineStyle.SOLID, cityRoadStandard));
        addStyleElement(new LineStyle("Road - City Secondary",           Color.WHITE,                   new Color(68, 68, 68, 128), 1.5f, LineStyle.SOLID, cityRoadStandard));
        addStyleElement(new LineStyle("Road - City Tertiary",            Color.WHITE,                   new Color(68, 68, 68, 128), 1.0f, LineStyle.SOLID, cityRoadStandard));
        addStyleElement(new LineStyle("Road - Motorway",                 new Color(173,   0,   0),      new Color(255, 255, 255),   2.5f, LineStyle.SOLID, primaryHighway));
        addStyleElement(new LineStyle("Road - Motorway Link",            new Color(173,   0,   0),      new Color(255, 255, 255),   1.5f, LineStyle.SOLID, primaryHighway));        
        addStyleElement(new LineStyle("Road - Primary Highway",          new Color(173,   0,   0),      new Color(255, 255, 255),   2.5f, LineStyle.SOLID, primaryHighway));
        addStyleElement(new LineStyle("Road - Primary Highway Link",     new Color(173,   0,   0),      new Color(255, 255, 255),   1.5f, LineStyle.SOLID, primaryHighway));
        addStyleElement(new LineStyle("Road - Secondary Highway",        new Color(255, 255, 255),      new Color(  4,  28,   5),   1.8f, LineStyle.SOLID, secondaryHighway));
        addStyleElement(new LineStyle("Road - Secondary Highway Link",   new Color(255, 255, 255),      new Color(  4,  28,   5),   1.0f, LineStyle.SOLID, secondaryHighway));
        addStyleElement(new LineStyle("Rock Face",                       new Color(119,  44,   0),      1.5f, LineStyle.DASHED, true));
        addStyleElement(new LineStyle("Route",                           new Color(105, 168, 238, 128), 1.0f, LineStyle.SOLID,  true));
        addStyleElement(new LineStyle("Track",                           new Color(68, 68, 68, 128),    1.0f, LineStyle.SOLID,  true));
        addStyleElement(new LineStyle("Territorial Boundary",            new Color(161, 132, 179),      1.5f, LineStyle.DASHED, false));            
        addStyleElement(new LineStyle("Water Way - Intermittent Stream", new Color( 51, 197, 255),      1.0f, LineStyle.DASHED, false));
        addStyleElement(new LineStyle("Water Way - River",               new Color( 51, 197, 255),      3.0f, LineStyle.SOLID,  true));
        addStyleElement(new LineStyle("Water Way - Stream",              new Color( 51, 197, 255),      1.0f, LineStyle.SOLID,  false));        
        
        //polystyle
        addStyleElement(new PolygonStyle("(Unspecified Polygon)", new Color(188, 190, 178), new Color(68,68,68,180)));
        addStyleElement(new PolygonStyle("Agricultural Plot",     new Color(172, 194, 160), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Beach",                 new Color(230, 210, 151), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Building",              new Color(  4,  28,   5), new Color(212, 208, 204)));
        addStyleElement(new PolygonStyle("Commercial Area",       new Color( 11,  35,  45), ThemeConstants.LAND));  
        addStyleElement(new PolygonStyle("Country - Filled",      new Color(  0,   0,   0), new Color( 22,  90, 127, 128), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Default Backdrop",      new Color(166, 174, 183, 128)));
        addStyleElement(new PolygonStyle("Forest",                new Color(135, 162, 121), new Color( 68,  68,  68, 180), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Grass Field",           new Color( 91, 210,  81), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Industrial Area",       new Color(222, 208, 213), ThemeConstants.LAND));  
        addStyleElement(new PolygonStyle("Island",                new Color(  0,   0,   0), new Color(  0,   0,   0, 140), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Lake",                  new Color( 51, 197, 255), ThemeConstants.WATER));
        addStyleElement(new PolygonStyle("Market",                new Color(  0, 153, 141), ThemeConstants.LAND)); 
        addStyleElement(new PolygonStyle("Ocean",                 new Color( 51, 197, 255), ThemeConstants.WATER));
        addStyleElement(new PolygonStyle("Park",                  new Color( 91, 210,  81), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Parking Lot",           new Color( 68,  68,  68), new Color(68, 68, 68, 120),    ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Protected Area",        new Color(201, 223, 175), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Reef",                  new Color(203, 216, 230), new Color(183, 203, 228, 200))); 
        addStyleElement(new PolygonStyle("Residential Area",      new Color(220, 220, 220), new Color( 68,  68,  68, 128), ThemeConstants.LAND));         
        addStyleElement(new PolygonStyle("River",                 new Color( 51, 197, 255), ThemeConstants.WATER));
        addStyleElement(new PolygonStyle("School",                new Color( 22,  90, 127), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Small Island",          new Color(  0,   0,   0), new Color(  0,   0,   0, 140), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Snow Cover",            new Color(255, 255, 255), new Color(255, 255, 255, 140), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Sports Field",          new Color( 91, 210,  81), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Stadium",               new Color( 91, 210,  81), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("University",            new Color( 22,  90, 127), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Wet Land",              new Color(132, 220,  79), ThemeConstants.LAND));        
    }
}
