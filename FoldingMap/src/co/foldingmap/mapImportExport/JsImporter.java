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

import java.util.ArrayList;

/**
 * JavaScript Importer, only used for variable parsing at the moment.
 * 
 * @author Alec
 */
public class JsImporter {
    
    public static String[] getVariableText(String jsText) {
        ArrayList<String> variableText = new ArrayList<String>();
        int               currentIndex = 0;
        int               varEnd, equalsIndex, varStart;
        String            varText;
        
        while (currentIndex < jsText.length()) {
            varStart     = jsText.indexOf("var", currentIndex);
            
            if (varStart < 0) break;                
            
            equalsIndex  = jsText.indexOf("=",   varStart);
            varEnd       = jsText.indexOf("};",   equalsIndex);                        
            varText      = jsText.substring(equalsIndex + 1, varEnd + 1);
            currentIndex = varEnd + 1;
            
            variableText.add(varText.trim());
        }
        
        return variableText.toArray(new String[variableText.size()]);
    }
}
