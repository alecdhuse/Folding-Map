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
package co.foldingmap.mapImportExport;

import java.util.HashMap;

/**
 *
 * @author Alec
 */
public class GpxGarminExtensions {
    private HashMap<String, String> objectClassToSymbolMap, symbolToObjectClassMap;
    
    public GpxGarminExtensions() {
        initHashMaps();
    }
    
    /**
     * Adds a mapping between Garmin Symbols and FoldingMap ObjectClasses.
     * 
     * @param symbol
     * @param objectClass 
     */
    private void addMapping(String symbol, String objectClass) {
        objectClassToSymbolMap.put(objectClass, symbol);
        symbolToObjectClassMap.put(symbol,      objectClass);        
    }
    
    /**
     * Converts a Garmin symbol into a FoldingMap object class.
     * 
     * @param symbol
     * @return 
     */
    public String getClassFromSymbol(String symbol) {
        if (symbolToObjectClassMap == null)
            initHashMaps();
        
        String object = symbolToObjectClassMap.get(symbol);
        
        if (object == null)
            object = "(Unspecified Point)";
        
        return object;
    }
        
    /**
     * Converts a FoldoingMap object class to a Garmin Symbol.
     * 
     * @param objectClass
     * @return 
     */
    public String getSymbolFromClass(String objectClass) {
        if (objectClassToSymbolMap == null)
            initHashMaps();        
        
        String symbol = objectClassToSymbolMap.get(objectClass);
        
        if (symbol == null)
            symbol = "Waypoint";    
        
        return symbol;
    }
            
    private void initHashMaps() {
        objectClassToSymbolMap = new HashMap<String, String>();
        symbolToObjectClassMap = new HashMap<String, String>();
        
        addMapping("Airport",               "Airport");
        addMapping("Bank",                  "Bank");
        addMapping("Bar",                   "Bar");
        addMapping("Bridge",                "Bridge");
        addMapping("Building",              "Building");
        addMapping("Campground",            "Camp Site");
        addMapping("Cemetery",              "Cemetery");
        addMapping("Church",                "Place Of Worship");
        addMapping("Circle with X",         "Roadblock");
        addMapping("City (Capitol)",        "Place - Capitol");
        addMapping("City (Large)",          "Place - City");
        addMapping("City (Medium)",         "Place - Town");
        addMapping("City (Small)",          "Place - Village");
        addMapping("Convenience Store",     "Grocery");
        addMapping("Dam",                   "Dam");
        addMapping("Danger Area",           "Dangerous Area");
        addMapping("Drinking Water",        "Drinking Water");
        addMapping("Fast Food",             "Restaurant - Fast Food");
        addMapping("Forest",                "Forest");
        addMapping("Gas Station",           "Gas Station");
        addMapping("Golf Course",           "Golf");
        addMapping("Ground Transportation", "Bus Station");        
        addMapping("Heliport",              "Heliport");
        addMapping("Information",           "Information");
        addMapping("Library",               "Library");
        addMapping("Lodging",               "Hotel");
        addMapping("Marina",                "Harbor");
        addMapping("Medical Facility",      "Clinic");
        addMapping("Mine",                  "Mine");
        addMapping("Movie Theater",         "Cinema");
        addMapping("Museum",                "Museum");
        addMapping("Park",                  "Park");
        addMapping("Parking Area",          "Parking");
        addMapping("Pharmacy",              "Pharmacy");
        addMapping("Picnic Area",           "Picnic Area");
        addMapping("Pin, Red",              "Marker");
        addMapping("Police Station",        "Police Station");
        addMapping("Post Office",           "Post Office");
        addMapping("Restaurant",            "Restaurant");
        addMapping("Restroom",              "Public Toilets");
        addMapping("Scenic Area",           "Lookout");
        addMapping("School",                "School");
        addMapping("Shipwreck",             "Shipwreck");
        addMapping("Shopping Center",       "Super Market");
        addMapping("Skull and Crossbones",  "Dangerous Area");
        addMapping("Summit",                "Mountain Peak");
        addMapping("Swimming Area",         "Swimming");
        addMapping("Tall Tower",            "Antenna");
        addMapping("Telephone",             "Phone");
        addMapping("Toll Booth",            "Toll Booth");
        addMapping("Trail Head",            "Trail Head");
        addMapping("Waypoint",              "(Unspecified Point)");
        addMapping("Zoo",                   "Zoo");                

        objectClassToSymbolMap.put("Cafe",                         "Restaurant");
        objectClassToSymbolMap.put("Hill",                         "Summit");
        objectClassToSymbolMap.put("Embassy",                      "Flag");
        objectClassToSymbolMap.put("Minefield",                    "Skull and Crossbones");
        objectClassToSymbolMap.put("Parking Garage",               "Parking Area");
        objectClassToSymbolMap.put("Place Of Worship - Christian", "Church");
        objectClassToSymbolMap.put("University",                   "School");        
    }
}
