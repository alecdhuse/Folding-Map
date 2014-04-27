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

import co.foldingmap.dataStructures.PropertyValuePair;
import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author Alec
 */
public class OsmTags {
    ArrayList<PropertyValuePair>    nodes, ways, areas, relations;

    public OsmTags() {
        nodes     = new ArrayList<PropertyValuePair>();
        ways      = new ArrayList<PropertyValuePair>();
        areas     = new ArrayList<PropertyValuePair>();
        relations = new ArrayList<PropertyValuePair>();
                
        loadTags();
    }
    
    public ArrayList<String> getKeys(String objectType, String keyTextPart) {
        ArrayList<PropertyValuePair>    pairs;
        ArrayList<String>               keys;
        
        keys     = new ArrayList<String>();
             
        if (objectType.equalsIgnoreCase("node")) {
            pairs = nodes;
        } else if (objectType.equalsIgnoreCase("way")) {
            pairs = ways;
        } else if (objectType.equalsIgnoreCase("area")) {
            pairs = areas;
        } else if (objectType.equalsIgnoreCase("relation")) {
            pairs = relations;
        } else {
            pairs = new ArrayList<PropertyValuePair>();
            pairs.addAll(nodes);
            pairs.addAll(ways);
            pairs.addAll(areas);
            pairs.addAll(relations);
        }       
        
        for (PropertyValuePair pair: pairs) {
            if (pair.getProperty().toLowerCase().startsWith(keyTextPart.toLowerCase())) {
                if (!keys.contains(pair.getProperty().toLowerCase()))
                    keys.add(pair.getProperty().toLowerCase());            
            }
        }
        
        return keys;
    }
    
    public ArrayList<String> getValues(String objectType, String keyText, String valueTextPart) {
        ArrayList<PropertyValuePair>    keys, pairs;
        ArrayList<String>               values;
        
        keys   = new ArrayList<PropertyValuePair>();
        values = new ArrayList<String>();
        
        if (objectType.equalsIgnoreCase("node")) {
            pairs = nodes;
        } else if (objectType.equalsIgnoreCase("way")) {
            pairs = ways;
        } else if (objectType.equalsIgnoreCase("area")) {
            pairs = areas;
        } else if (objectType.equalsIgnoreCase("relation")) {
            pairs = relations;
        } else {
            pairs = new ArrayList<PropertyValuePair>();
            pairs.addAll(nodes);
            pairs.addAll(ways);
            pairs.addAll(areas);
            pairs.addAll(relations);
        }          
        
        for (PropertyValuePair pair: pairs) {
            if (pair.getProperty().equalsIgnoreCase(keyText)) {
                if (!keys.contains(pair))
                    keys.add(pair);
            }            
        }
        
        for (PropertyValuePair pair: keys) {
            if (pair.getValue().toLowerCase().startsWith(valueTextPart.toLowerCase())) 
                values.add(pair.getValue().toLowerCase());            
        }        
        
        return values;
    }    
    
    private void loadTags() {
        BufferedReader      br;
        DataInputStream     in;
        InputStream         is;
        PropertyValuePair   newPair;
        String              filePath, line, token;
        String              key, value, type;
        StringTokenizer     st;
        
        try {
            filePath = "resources" + File.separator + "OsmTags.csv";
            is       = getClass().getResourceAsStream(filePath);
            in       = new DataInputStream(is);
            br       = new BufferedReader(new InputStreamReader(in));                                     
            
            while ((line = br.readLine()) != null)   {
                st    = new StringTokenizer(line, ",");
                key   = st.nextToken();
                value = st.nextToken();
                type  = st.nextToken();
                
                newPair = new PropertyValuePair(key, value);
                type    = type.replace("\"", "");
                st      = new StringTokenizer(type, ",");
                
                while (st.hasMoreTokens()) {
                    token = st.nextToken();
                    if (token.equalsIgnoreCase("node")) {
                        nodes.add(newPair);
                    } else if (token.equalsIgnoreCase("way")) {
                        ways.add(newPair);
                    } else if (token.equalsIgnoreCase("area")) {
                        areas.add(newPair);
                    } else if (token.equalsIgnoreCase("relation")) {
                        relations.add(newPair);
                    }
                }
            }            
        } catch (Exception e) {
            System.err.println("Error in OsmTags.loadTags() - " + e);
        }
    }
    
}
