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

import co.foldingmap.ResourceHelper;
import co.foldingmap.map.Visibility;
import java.util.HashMap;

/**
 *
 * @author Alec
 */
public class DefaultIconSet {
    
    public static void addStyleElement(HashMap<String, IconStyle> map, 
                                       ResourceHelper             resourceHelper,
                                       IconStyle                  style) {        
            
            style.setResourceHelper(resourceHelper);
            map.put(style.getID(), style);
    }
    
    public static HashMap<String, IconStyle> getSmallSet(ResourceHelper resourceHelper) {
        HashMap<String, IconStyle>  icons;
        Visibility                  lvl1, lvl2, lvl3, lvl4, lvl5;
        
        //init
        icons = new HashMap<String, IconStyle>();
        lvl1  = new Visibility(25, 16);
        lvl2  = new Visibility(25, 12);
        lvl3  = new Visibility(25, 8);
        lvl4  = new Visibility(25, 4);
        lvl5  = new Visibility(25, 2);
        
        addStyleElement(icons, resourceHelper, new IconStyle("Airport",                       "standard_airport-small.png",              lvl2));
        addStyleElement(icons, resourceHelper, new IconStyle("Antenna",                       "standard_antenna-small.png",              null));
        addStyleElement(icons, resourceHelper, new IconStyle("Art Gallery",                   "standard_art-gallery-small.png",          lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Bank",                          "dark_bank.png",                           lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Bar",                           "standard_bar-small.png",                  lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Bridge",                        "standard_bridge-small.png",               lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Bus Station",                   "standard_bus-small.png",                  lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Cafe",                          "standard_cafe-small.png",                 lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Camp Site",                     "standard_campsite-small.png",             lvl2));
        addStyleElement(icons, resourceHelper, new IconStyle("Cemetery",                      "standard_cemetery-small.png",             lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Clinic",                        "standard_hospital-small.png",             lvl2));
        addStyleElement(icons, resourceHelper, new IconStyle("Cinema",                        "standard_cinema-small.png",               lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Courthouse",                    "standard_courthouse-small.png",           lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Cyber Café",                    "standard_internet_cafe-small.png",        null));
        addStyleElement(icons, resourceHelper, new IconStyle("Dam",                           "standard_dam-small.png",                  lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Dangerous Area",                "standard_dangerous-area-small.png",       null));
        addStyleElement(icons, resourceHelper, new IconStyle("Embassy",                       "standard_embassy-small.png",              lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Ferry",                         "standard_ferry-small.png",                lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Fire Station",                  "standard_fire-station-small.png",         lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Forest",                        "standard_forest-small.png",               lvl2));
        addStyleElement(icons, resourceHelper, new IconStyle("Football",                      "standard_football-small.png",             lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Garden",                        "standard_garden-small.png",               lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Gas Station",                   "standard_fuel-small.png",                 lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Golf",                          "standard_golf-small.png",                 lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Grocery",                       "standard_grocery-small.png",              lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Harbor",                        "standard_harbor-small.png",               lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Heliport",                      "standard_heliport-small.png",             lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Hill",                          "standard_triangle-solid-small.png",       lvl3));
        addStyleElement(icons, resourceHelper, new IconStyle("Hospital",                      "standard_hospital-small.png",             lvl2));
        addStyleElement(icons, resourceHelper, new IconStyle("Hotel",                         "standard_lodging-small.png",              lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Industrial",                    "standard_industrial-small.png",           lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Library",                       "standard_library-small.png",              lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Lookout",                       "standard_lookout-small.png",              null));
        addStyleElement(icons, resourceHelper, new IconStyle("Marker",                        "standard_marker-small.png",               null));
        addStyleElement(icons, resourceHelper, new IconStyle("Memorial",                      "standard_monument-small.png",             lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Mine",                          "standard_mine-small.png",                 lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Minefield",                     "standard_minefield-small.png",            lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Moto Taxi",                     "standard_mototaxi-small.png",             null));
        addStyleElement(icons, resourceHelper, new IconStyle("Mountain Peak",                 "standard_triangle-solid-small.png",       null));
        addStyleElement(icons, resourceHelper, new IconStyle("Park",                          "standard_park-small.png",                 lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Parking",                       "standard_parking-small.png",              lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Parking Garage",                "standard_parking-garage-small.png",       lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Pharmacy",                      "standard_pharmacy-small.png",             null));     
        addStyleElement(icons, resourceHelper, new IconStyle("Photo",                         "standard_photo-small.png",                null));    
        addStyleElement(icons, resourceHelper, new IconStyle("Place Of Worship",              "standard_place-of-worship-small.png",     lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Place Of Worship - Christian",  "standard_religious-christian-small.png",  lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Place Of Worship - Hindu",      "standard_religious-hindu-small.png",      lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Place Of Worship - Islam",      "standard_religious-islam-small.png",      lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Place Of Worship - Jewish",     "standard_religious-jewish-small.png",     lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Police Station",                "standard_police-small.png",               null));
        addStyleElement(icons, resourceHelper, new IconStyle("Prison",                        "standard_prison-small.png",               lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Post Office",                   "standard_post-small.png",                 lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Railway Stop",                  "standard_rail-small.png",                 lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Restaurant",                    "standard_restaurant-small.png",           lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Restaurant - Fast Food",        "standard_fast-food-small.png",            lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Roadblock",                     "standard_roadblock-small.png",            lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("School",                        "standard_school-small.png",               lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Shop",                          "standard_shop-small.png",                 lvl1));        
        addStyleElement(icons, resourceHelper, new IconStyle("Super Market",                  "standard_shop-small.png",                 lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Swimming",                      "standard_swimming-small.png",             lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Tree",                          "standard_tree-small.png",                 null));
        addStyleElement(icons, resourceHelper, new IconStyle("University",                    "standard_college-small.png",              null));
        addStyleElement(icons, resourceHelper, new IconStyle("Warehouse",                     "standard_warehouse-small.png",            lvl1));
        addStyleElement(icons, resourceHelper, new IconStyle("Zoo",                           "standard_zoo-small.png",                  lvl1));        
        
        return icons;
    }
}
