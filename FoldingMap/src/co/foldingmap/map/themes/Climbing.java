/* 
 * Copyright (C) 2015 Alec Dhuse
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

import co.foldingmap.map.Visibility;
import java.awt.Color;

/**
 *
 * @author Alec
 */
public class Climbing extends MapTheme {
    
    public Climbing() {
        super("Climbing");        
                
        backgroundColor  = new Color(244, 243, 240);  
        
        LabelStyle cityRoadStandard = new LabelStyle(new Color(75, 68, 60), Color.white);
        LabelStyle primaryHighway   = new LabelStyle(new Color(75, 68, 60), new Color(255, 195,  69));
        LabelStyle secondaryHighway = new LabelStyle(new Color(75, 68, 60), new Color(255, 254, 121));
                
        //IconStyle
        iconStyles.putAll(DefaultIconSet.getSmallSet(resourceHelper));        
        addStyleElement(new IconStyle("(Unspecified Point)",           new Color( 68,  68,  68, 128)));
        addStyleElement(new IconStyle("Amenity",                       new Color( 68,  68,  68, 128), noLabel,        lvl1));
        addStyleElement(new IconStyle("Boulder Problem",               new Color(  0, 255,   0, 204), DEFAULT_LABEL,  lvl2));
        addStyleElement(new IconStyle("Building",                      new Color( 68,  68,  68, 128), noLabel,        lvl1));
        addStyleElement(new IconStyle("Climbing Area",                 new Color(255, 120,   0, 204), DEFAULT_LABEL,  lvl2));
        addStyleElement(new IconStyle("Place - City",                  Color.BLACK,                   DEFAULT_LABEL,  lvl5));
        addStyleElement(new IconStyle("Place - Town",                  Color.BLACK,                   DEFAULT_LABEL,  lvl2));
        addStyleElement(new IconStyle("Place - Suburb",                Color.BLACK,                   DEFAULT_LABEL,  lvl2));
        addStyleElement(new IconStyle("Place - Village",               Color.BLACK,                   DEFAULT_LABEL,  lvl2));
        addStyleElement(new IconStyle("Labeled Point",                 Color.BLACK,                   DEFAULT_LABEL,  null));
        addStyleElement(new IconStyle("Mixed Route",                   new Color(128,   0, 128, 204), DEFAULT_LABEL,  lvl2));
        addStyleElement(new IconStyle("NGO Office",                    Color.BLACK,                   noLabel,        lvl1));
        addStyleElement(new IconStyle("Phone",                         Color.BLACK,                   noLabel,        lvl1));
        addStyleElement(new IconStyle("Point",                         new Color(243, 62, 62, 128)));
        addStyleElement(new IconStyle("Public Toilets",                Color.BLACK));
        addStyleElement(new IconStyle("Sport Route",                   new Color(  0,   0, 255, 204), DEFAULT_LABEL,  lvl2));
        addStyleElement(new IconStyle("Tourist Attraction",            new Color(153, 179, 204),      noLabel,        lvl1));
        addStyleElement(new IconStyle("Trad Route",                    new Color(255,   0,   0, 204), DEFAULT_LABEL,  lvl2));
        
        //LineStyles
        addStyleElement(new LineStyle("(Unspecified Linestring)",        new Color(68, 68, 68, 128),    1.0f, LineStyle.SOLID,  false));        
        addStyleElement(new LineStyle("Aeroway",                         new Color(211, 202, 189),      1.5f, LineStyle.SOLID,  false));        
        addStyleElement(new LineStyle("Border - Country Border",         new Color(180, 174, 174),      1.2f, LineStyle.SOLID,  false));
        addStyleElement(new LineStyle("Border - Inter-Country",          new Color(180, 174, 174),      1.2f, LineStyle.DASHED, false));
        addStyleElement(new LineStyle("Coastline",                       new Color(234, 200, 117),      1.2f, LineStyle.SOLID,  false));
        addStyleElement(new LineStyle("Ferry Line",                      new Color(110, 113, 248),      1.5f, LineStyle.DASHED, false));
        addStyleElement(new LineStyle("Hiking Trail",                    Color.WHITE,                   new Color(68, 68, 68, 100), 1.0f, LineStyle.IN_DASH, cityRoadStandard));
        addStyleElement(new LineStyle("Path - Bikeway",                  Color.WHITE,                   new Color(68, 68, 68, 128), 0.4f, LineStyle.SOLID));
        addStyleElement(new LineStyle("Path - Footway",                  Color.WHITE,                   new Color(68, 68, 68, 128), 0.4f, LineStyle.SOLID));
        addStyleElement(new LineStyle("Path - Running",                  Color.WHITE,                   new Color(68, 68, 68, 128), 0.4f, LineStyle.SOLID));
        addStyleElement(new LineStyle("Path - Steps",                    new Color(231, 230, 225),      new Color(68, 68, 68, 128), 0.4f, LineStyle.SOLID));
        addStyleElement(new LineStyle("Pier",                            new Color(244, 243, 240),      new Color(231, 230, 225),   0.8f, LineStyle.SOLID));
        addStyleElement(new LineStyle("Power Line",                      new Color( 99,  28,  28),      1.0f, LineStyle.DOTTED, false));
        addStyleElement(new LineStyle("Rail Line",                       Color.WHITE,                   new Color(212, 208, 204),   1.2f, LineStyle.SOLID_DASHED));
        addStyleElement(new LineStyle("Rail - Platform",                 new Color(231, 230, 225),      0.5f, LineStyle.SOLID, true));
        addStyleElement(new LineStyle("Rail - Tram",                     Color.WHITE,                   new Color(212, 208, 204),   0.2f, LineStyle.SOLID_DASHED));
        addStyleElement(new LineStyle("Road - Unclassified",             Color.WHITE,                   new Color(68, 68, 68, 128), 0.9f, LineStyle.SOLID, cityRoadStandard));
        addStyleElement(new LineStyle("Road - City Primary",             Color.WHITE,                   new Color(68, 68, 68, 128), 1.5f, LineStyle.SOLID, cityRoadStandard));
        addStyleElement(new LineStyle("Road - City Secondary",           Color.WHITE,                   new Color(68, 68, 68, 128), 1.5f, LineStyle.SOLID, cityRoadStandard, new Visibility(23, 13)));
        addStyleElement(new LineStyle("Road - City Tertiary",            Color.WHITE,                   new Color(68, 68, 68, 128), 1.0f, LineStyle.SOLID, cityRoadStandard, new Visibility(23, 14)));
        addStyleElement(new LineStyle("Road - Motorway",                 new Color(255, 195,  69),      new Color(217, 142,  15),   2.5f, LineStyle.SOLID)); 
        addStyleElement(new LineStyle("Road - Motorway Link",            new Color(255, 195,  69),      new Color(217, 142,  15),   1.5f, LineStyle.SOLID, new Visibility(23, 13)));       
        addStyleElement(new LineStyle("Road - Primary Highway",          new Color(255, 195,  69),      new Color(217, 142,  15),   2.5f, LineStyle.SOLID, primaryHighway));
        addStyleElement(new LineStyle("Road - Primary Highway Link",     new Color(255, 195,  69),      new Color(217, 142,  15),   1.5f, LineStyle.SOLID, new Visibility(23, 13)));
        addStyleElement(new LineStyle("Road - Secondary Highway",        new Color(255, 254, 121),      new Color(226, 213, 162),   1.8f, LineStyle.SOLID, secondaryHighway));
        addStyleElement(new LineStyle("Road - Secondary Highway Link",   new Color(255, 254, 121),      new Color(226, 213, 162),   1.0f, LineStyle.SOLID, secondaryHighway));
        addStyleElement(new LineStyle("Road - Track",                    Color.WHITE,                  new Color(128, 128, 128, 128), 1.0f, LineStyle.SOLID, cityRoadStandard, new Visibility(23, 14)));
        addStyleElement(new LineStyle("Rock Face",                       new Color(119,  44,   0),      1.5f, LineStyle.DASHED, false));
        addStyleElement(new LineStyle("Route",                           new Color(105, 168, 238, 128), 1.0f, LineStyle.SOLID,  true));
        addStyleElement(new LineStyle("Track",                           new Color(68, 68, 68, 128),    1.0f, LineStyle.SOLID,  true));
        addStyleElement(new LineStyle("Trail - Improved",                Color.WHITE,                   new Color(68, 68, 68, 100), 0.6f, LineStyle.IN_DASH, cityRoadStandard));
        addStyleElement(new LineStyle("Trail - Unimproved",              Color.WHITE,                   new Color(68, 68, 68, 100), 0.4f, LineStyle.IN_DASH, cityRoadStandard));
        addStyleElement(new LineStyle("Trail - Water Crossing",          new Color(161, 132, 179),      new Color(68, 68, 68, 100), 0.6f, LineStyle.IN_DASH, cityRoadStandard));
        addStyleElement(new LineStyle("Territorial Boundary",            new Color(161, 132, 179),      1.5f, LineStyle.DASHED, false));            
        addStyleElement(new LineStyle("Water Way - Intermittent Stream", new Color(165, 191, 221),      1.0f, LineStyle.DASHED, false));
        addStyleElement(new LineStyle("Water Way - River",               new Color(165, 191, 221),      3.0f, LineStyle.SOLID,  true));
        addStyleElement(new LineStyle("Water Way - Stream",              new Color(165, 191, 221),      1.0f, LineStyle.SOLID,  false));        
        
        //polystyle
        addStyleElement(new PolygonStyle("(Unspecified Polygon)", new Color(188, 190, 178), new Color(68,68,68,180)));
        addStyleElement(new PolygonStyle("Airport",               new Color(223, 219, 212), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Agricultural Plot",     new Color(172, 194, 160), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Beach",                 new Color(230, 210, 151), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Building",              new Color(231, 230, 225), new Color(230, 230, 200)));
        addStyleElement(new PolygonStyle("Commercial Area",       new Color(222, 208, 213), ThemeConstants.LAND));  
        addStyleElement(new PolygonStyle("Country - Filled",      new Color(239, 237, 230), new Color(200, 200, 200, 128), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Default Backdrop",      new Color(166, 174, 183, 128)));
        addStyleElement(new PolygonStyle("Forest",                new Color(135, 162, 121), new Color( 68,  68,  68, 128), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Grass Field",           new Color(190, 217, 166), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Industrial Area",       new Color(222, 208, 213), ThemeConstants.LAND));  
        addStyleElement(new PolygonStyle("Island",                new Color(239, 237, 230), new Color(239, 237, 230, 140), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Lake",                  new Color(165, 191, 221), ThemeConstants.WATER));
        addStyleElement(new PolygonStyle("Land Cover - Heath",    new Color(239, 218, 194), null, ThemeConstants.LAND, "standard_heath-fill.png")); 
        addStyleElement(new PolygonStyle("Market",                new Color(216, 237, 235), ThemeConstants.LAND)); 
        addStyleElement(new PolygonStyle("Ocean",                 new Color(165, 191, 221), ThemeConstants.WATER));
        addStyleElement(new PolygonStyle("Park",                  new Color(201, 223, 175), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Parking Lot",           new Color(212, 208, 204), new Color(68, 68, 68, 120),    ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Protected Area",        new Color(201, 223, 175), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Reef",                  new Color(203, 216, 230), new Color(183, 203, 228, 200))); 
        addStyleElement(new PolygonStyle("Residential Area",      new Color(220, 220, 220), new Color( 68,  68,  68, 128), ThemeConstants.LAND));  
        addStyleElement(new PolygonStyle("River",                 new Color(165, 191, 221), ThemeConstants.WATER));
        addStyleElement(new PolygonStyle("Rock - Basalt",         new Color( 56,  54,  45), new Color( 68, 68, 68,180), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Rock - Granite",        new Color(188, 190, 178), new Color(100,100,100,180), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Rock - Lava Tuft",      new Color(175, 166, 136), new Color(85,68,0,180), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Rock - Limestone",      new Color(200, 213, 213), new Color(174,185,185,180), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Rock - Sandstone",      new Color(203, 183, 156), new Color(173,153,126,180), ThemeConstants.LAND));        
        addStyleElement(new PolygonStyle("Rock - Sandstone Red",  new Color(196, 147, 126), new Color(143,72,92), ThemeConstants.LAND)); 
        addStyleElement(new PolygonStyle("Rock - Sandstone White",new Color(255, 249, 232), new Color(200,200,200), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("School",                new Color(232, 221, 189), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Small Island",          new Color(239, 237, 230), new Color(239, 237, 230,  90), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Snow Cover",            new Color(255, 255, 255), new Color(255, 255, 255, 140), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Sports Field",          new Color(201, 223, 175), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Stadium",               new Color(170, 204, 130), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("University",            new Color(232, 221, 189), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Wet Land",              new Color(132, 220,  79), ThemeConstants.LAND));    
        
        PolygonStyle sandStoneCavePS = new PolygonStyle("Rock - Sandstone Cave", new Color(203, 183, 156, 180), ThemeConstants.LAND);
        sandStoneCavePS.addOutlineStyle(new OutlineStyle(new Color(203, 183, 156),   ThemeConstants.LAND));   
        sandStoneCavePS.addOutlineStyle(new OutlineStyle(new Color(173,153,126,180), ThemeConstants.ANY));       
        addStyleElement(sandStoneCavePS);
        
        PolygonStyle waterWadi = new PolygonStyle("Water - Wadi", new Color(230, 230, 230), null, ThemeConstants.WATER, "standard_wadi-fill.png");
        sandStoneCavePS.addOutlineStyle(new OutlineStyle(new Color(230, 230, 230), ThemeConstants.WATER));   
        sandStoneCavePS.addOutlineStyle(new OutlineStyle(new Color(165, 191, 221), ThemeConstants.ANY));       
        addStyleElement(waterWadi);        
    }
            
}
