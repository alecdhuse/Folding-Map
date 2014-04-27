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
package co.foldingmap.map.labeling;

import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * This class is used to get Abbreviations for labels.
 * @author Alec
 */
public class LabelAbbreviations {
    protected HashMap<String, String>   abbreviations;
    
    /**
     * Basic constructor.
     * 
     */
    public LabelAbbreviations() {
        abbreviations = new HashMap<String, String>();
        
        loadAbbreviations();
    }    
    
    /**
     * Hard coded label abbreviations.
     */
    private void loadAbbreviations() {
        //Keys must all be lowercase
        abbreviations.put("avenue",    "Ave");
        abbreviations.put("boulevard", "Blvd");
        abbreviations.put("east",      "E");
        abbreviations.put("north",     "N");
        abbreviations.put("northeast", "NE");
        abbreviations.put("northwest", "NW");
        abbreviations.put("south",     "S");
        abbreviations.put("southeast", "SE");
        abbreviations.put("southwest", "SW");
        abbreviations.put("street",    "St");
        abbreviations.put("west",      "W");
    }    
    
    /**
     * Replaces a given label text with any available abbreviations.
     * 
     * @param label
     * @return 
     */
    public String replaceWithAbbreviations(String label) {
        String          abbreviation, token, tokenLowerCase;
        StringBuilder   newLabel;
        StringTokenizer st;
        
        st       = new StringTokenizer(label);
        newLabel = new StringBuilder();
        
        while (st.hasMoreTokens()) {
            token          = st.nextToken();
            tokenLowerCase = token.toLowerCase();            
            abbreviation   = abbreviations.get(tokenLowerCase);
            
            if (abbreviation != null) {
                newLabel.append(abbreviation);
                newLabel.append(" ");
            } else {
                newLabel.append(token);
                newLabel.append(" ");                
            }
        }
        
        return newLabel.toString();        
    }    
}
