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
public class Trail extends MapTheme {
    protected LabelStyle waterFeatureLabel;
    
    public Trail() {
        super("Trail");
        
        PolygonStyle buildPolyStyle;
        
        this.backgroundColor   = new Color(244, 243, 240);              
        this.waterFeatureLabel = new LabelStyle(new Color(94, 155, 203), new Color(94, 155, 203, 0));
        
        //IconStyle
        addStyleElement(new IconStyle("(Unspecified Point)",           new Color(68, 68, 68, 128)));
        addStyleElement(new IconStyle("Airport",                       "trail_airport.png",        lvl2));
        addStyleElement(new IconStyle("Amenity",                       new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Antenna",                       new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Art Gallery",                   new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Bank",                          "dark_bank.png",            lvl1));
        addStyleElement(new IconStyle("Bar",                           new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Bridge",                        new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Building",                      new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Bus Station",                   "trail_bus.png",            lvl1));
        addStyleElement(new IconStyle("Cafe",                          new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Camp Site",                     "trail_campsite.png",       lvl1));
        addStyleElement(new IconStyle("Cemetery",                      new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Clinic",                        "trail_clinic.png",         lvl2));
        addStyleElement(new IconStyle("Cinema",                        "trail_theater.png",        lvl1));
        addStyleElement(new IconStyle("Place - City",                  Color.BLACK, DEFAULT_LABEL, lvl5));
        addStyleElement(new IconStyle("Place - Town",                  Color.BLACK, DEFAULT_LABEL, lvl2));
        addStyleElement(new IconStyle("Place - Suburb",                Color.BLACK, DEFAULT_LABEL, lvl2));
        addStyleElement(new IconStyle("Place - Village",               Color.BLACK, DEFAULT_LABEL, lvl2));
        addStyleElement(new IconStyle("Courthouse",                    new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Cyber Café",                    new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Ferry",                         new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Fire Station",                  new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Forest",                        new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Gas Station",                   "trail_gas-station.png",    lvl1));
        addStyleElement(new IconStyle("Golf",                          "trail_golf.png",             lvl1));
        addStyleElement(new IconStyle("Harbor",                        "trail_harbor.png",           lvl1));
        addStyleElement(new IconStyle("Heliport",                      new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Hill",                          "web_triangle-solid.png",   lvl3));
        addStyleElement(new IconStyle("Hospital",                      "trail_hospital.png",       lvl2));
        addStyleElement(new IconStyle("Hotel",                         "trail_hotel.png",          lvl1));
        addStyleElement(new IconStyle("Industrial",                    new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Labeled Point",                 Color.BLACK, DEFAULT_LABEL,  null));
        addStyleElement(new IconStyle("Library",                       new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Lookout",                       new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Marker",                        "dark_found-marker.png",    null));
        addStyleElement(new IconStyle("Memorial",                      new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Mine",                          Color.BLACK, DEFAULT_LABEL,  lvl1));
        addStyleElement(new IconStyle("Minefield",                     new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Moto Taxis",                    new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Mountain Peak",                 "web_triangle-solid.png",   null));
        addStyleElement(new IconStyle("NGO Office",                    Color.BLACK, null, lvl1));
        addStyleElement(new IconStyle("Park",                          new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Parking",                       "trail_parking.png",        lvl1));
        addStyleElement(new IconStyle("Parking Garage",                new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Pharmacy",                      new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Phone",                         Color.BLACK, null, lvl1));
        addStyleElement(new IconStyle("Place Of Worship",              Color.BLACK, null, lvl1));
        addStyleElement(new IconStyle("Place Of Worship - Christian",  new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Place Of Worship - Islam",      new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Place Of Worship - Jewish",     new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Police Station",                new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Post Office",                   "trail_post.png",                 lvl1));
        addStyleElement(new IconStyle("Public Toilets",                Color.BLACK));
        addStyleElement(new IconStyle("Railway Stop",                  new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Restaurant",                    "trail_restaurant.png",         lvl1));
        addStyleElement(new IconStyle("Restaurant - Fast Food",        "trail_fast-food.png",          lvl1));
        addStyleElement(new IconStyle("Roadblock",                     new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("School",                        new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Shop",                          "trail_store.png",                lvl1));
        addStyleElement(new IconStyle("Super Market",                  new Color(68, 68, 68, 128), null, lvl1));
        addStyleElement(new IconStyle("Swimming",                      "trail_swimming.png",           lvl1));
        addStyleElement(new IconStyle("Tourist Attraction",            new Color(153, 179, 204), null, lvl1));
        addStyleElement(new IconStyle("Tree",                          "dark_tree.png",                null));
        addStyleElement(new IconStyle("University",                    new Color(68, 68,  68,  128), null, lvl1));
        addStyleElement(new IconStyle("Warehouse",                     new Color(68, 68,  68,  128), null, lvl1));
        addStyleElement(new IconStyle("Water Feature Label",           new Color(94, 155, 203, 0), waterFeatureLabel, lvl1));          
        
        //LineStyles
        addStyleElement(new LineStyle("(Unspecified Linestring)",        new Color(68, 68, 68, 128),    1.0f, LineStyle.SOLID,  false));
        addStyleElement(new LineStyle("Border - Country Border",         new Color(180, 174, 174),      1.2f, LineStyle.SOLID,  false));
        addStyleElement(new LineStyle("Border - Inter-Country",          new Color(180, 174, 174),      1.2f, LineStyle.DASHED, false));
        addStyleElement(new LineStyle("Coastline",                       new Color(234, 200, 117),      1.2f, LineStyle.SOLID,  false));
        addStyleElement(new LineStyle("Ferry Line",                      new Color(110, 113, 248),      1.5f, LineStyle.DASHED, false));
        addStyleElement(new LineStyle("Hiking Trail",                    Color.BLACK,                   0.8f, LineStyle.DASHED, false));
        addStyleElement(new LineStyle("Path - Running",                  Color.WHITE,                   new Color(68, 68, 68, 128), 0.4f, LineStyle.SOLID));
        addStyleElement(new LineStyle("Path - Steps",                    new Color(231, 230, 225),      new Color(68, 68, 68, 128), 0.4f, LineStyle.SOLID));
        addStyleElement(new LineStyle("Pier",                            new Color(244, 243, 240),      new Color(231, 230, 225),   0.8f, LineStyle.SOLID));
        addStyleElement(new LineStyle("Rail Line",                       Color.WHITE,                   new Color(212, 208, 204),   1.2f, LineStyle.SOLID_DASHED));
        addStyleElement(new LineStyle("Rail - Platform",                 new Color(231, 230, 225),      0.5f, LineStyle.SOLID, false));
        addStyleElement(new LineStyle("Rail - Tram",                     Color.WHITE,                   new Color(212, 208, 204),   0.4f, LineStyle.SOLID_DASHED));
        addStyleElement(new LineStyle("Road - Unclassified",             Color.WHITE,                   new Color(68, 68, 68, 128), 0.9f, LineStyle.SOLID));
        addStyleElement(new LineStyle("Road - City Primary",             Color.WHITE,                   new Color(68, 68, 68, 128), 1.5f, LineStyle.SOLID));
        addStyleElement(new LineStyle("Road - City Secondary",           Color.WHITE,                   new Color(68, 68, 68, 128), 1.5f, LineStyle.SOLID));
        addStyleElement(new LineStyle("Road - City Tertiary",            Color.WHITE,                   new Color(68, 68, 68, 128), 1.0f, LineStyle.SOLID));
        addStyleElement(new LineStyle("Road - Motorway",                 new Color(255, 195,  69),      new Color(217, 142,  15),   2.5f, LineStyle.SOLID));
        addStyleElement(new LineStyle("Road - Motorway Link",            new Color(255, 195,  69),      new Color(217, 142,  15),   1.5f, LineStyle.SOLID));        
        addStyleElement(new LineStyle("Road - Primary Highway",          new Color(255, 195,  69),      new Color(217, 142,  15),   2.5f, LineStyle.SOLID));
        addStyleElement(new LineStyle("Road - Primary Highway Link",     new Color(255, 195,  69),      new Color(217, 142,  15),   1.5f, LineStyle.SOLID));
        addStyleElement(new LineStyle("Road - Secondary Highway",        Color.WHITE,                   Color.BLACK,                1.8f, LineStyle.SOLID));
        addStyleElement(new LineStyle("Rock Face",                       new Color(119,  44,   0),      1.5f, LineStyle.DASHED, true));
        addStyleElement(new LineStyle("Route",                           new Color(105, 168, 238, 128), 1.0f, LineStyle.SOLID,  true));
        addStyleElement(new LineStyle("Track",                           new Color(68, 68, 68, 128),    1.0f, LineStyle.SOLID,  true));
        addStyleElement(new LineStyle("Territorial Boundary",            new Color(161, 132, 179),      1.5f, LineStyle.DASHED, false));            
        addStyleElement(new LineStyle("Water Way - Intermittent Stream", new Color( 94, 155, 203),      1.2f, LineStyle.DASHED, false));
        addStyleElement(new LineStyle("Water Way - River",               new Color( 94, 155, 203),      3.0f, LineStyle.SOLID,  true));
        addStyleElement(new LineStyle("Water Way - Stream",              new Color( 94, 155, 203),      1.2f, LineStyle.SOLID,  false));        
        
        //polystyle
        addStyleElement(new PolygonStyle("(Unspecified Polygon)", new Color(188, 190, 178), new Color(68,68,68,180)));
        addStyleElement(new PolygonStyle("Agricultural Plot",     new Color(172, 194, 160), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Building",              new Color(231, 230, 225), new Color(212, 208, 204)));
        addStyleElement(new PolygonStyle("Commercial Area",       new Color(222, 208, 213), ThemeConstants.LAND));  
        //addStyleElement(new PolygonStyle("Country - Filled",      new Color(250, 240, 220), new Color(200, 200, 200, 128), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Default Backdrop",      new Color(166, 174, 183, 128)));
        addStyleElement(new PolygonStyle("Forest",                new Color(135, 162, 121), new Color( 68,  68,  68, 180), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Grass Field",           new Color(190, 217, 166), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Industrial Area",       new Color(222, 208, 213), ThemeConstants.LAND));  
        addStyleElement(new PolygonStyle("Island",                new Color(250, 240, 220), ThemeConstants.LAND));       
        addStyleElement(new PolygonStyle("Lake",                  new Color(197, 227, 244), new Color( 94, 155, 203), ThemeConstants.WATER));
        addStyleElement(new PolygonStyle("Market",                new Color(  0, 153, 141), ThemeConstants.LAND)); 
        //addStyleElement(new PolygonStyle("Ocean",                 new Color(197, 227, 244), new Color( 94, 155, 203), ThemeConstants.WATER));
        addStyleElement(new PolygonStyle("Park",                  new Color(201, 223, 175), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Parking Lot",           new Color(212, 208, 204), new Color( 68,  68,  68, 128), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Protected Area",        new Color(201, 223, 175), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Residential Area",      new Color(220, 220, 220), new Color(68, 68, 68, 128), ThemeConstants.LAND));  
        addStyleElement(new PolygonStyle("River",                 new Color(197, 227, 244), new Color( 94, 155, 203), ThemeConstants.WATER));
        addStyleElement(new PolygonStyle("School",                new Color(232, 221, 189), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Small Island",          new Color(250, 240, 220), new Color(250, 240, 220, 140), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Sports Field",          new Color(201, 223, 175), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Stadium",               new Color(170, 204, 130), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("University",            new Color(232, 221, 189), ThemeConstants.LAND));
        addStyleElement(new PolygonStyle("Wetland",               new Color(132, 220,  79), ThemeConstants.LAND));             
        
        buildPolyStyle = new PolygonStyle("Country - Filled", new Color(250, 240, 220), ThemeConstants.LAND);
        buildPolyStyle.addOutlineStyle(new OutlineStyle(new Color(200, 200, 200, 128), ThemeConstants.LAND));
        buildPolyStyle.addOutlineStyle(new OutlineStyle(new Color(200, 200, 200, 10), ThemeConstants.ANY));
        addStyleElement(buildPolyStyle);
        
        buildPolyStyle = new PolygonStyle("Ocean", new Color(197, 227, 244), ThemeConstants.WATER);
        buildPolyStyle.addOutlineStyle(new OutlineStyle(new Color(197, 227, 244), ThemeConstants.WATER));
        buildPolyStyle.addOutlineStyle(new OutlineStyle(new Color(197, 227, 244), ThemeConstants.NONE));
        buildPolyStyle.addOutlineStyle(new OutlineStyle(new Color( 94, 155, 203), ThemeConstants.ANY));
        addStyleElement(buildPolyStyle);
 
    }
}
