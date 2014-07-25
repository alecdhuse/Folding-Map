/*
 * Copyright (C) 2014 dhusea
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

package co.foldingmap.mapImportExport.css;

import co.foldingmap.ResourceHelper;
import co.foldingmap.dataStructures.SmartTokenizer;
import java.io.File;

/**
 * Basic class for parsing CSS files, can be extended for specific implementations.
 * 
 * @author Alec Dhuse
 */
public class CssParser {    
    
    public static void parseCssFile(String filePath) {
        File cssFile = ResourceHelper.getInstance().getFile(filePath);
        parseCssFile(cssFile);
    }
    
    public static void parseCssFile(File file) {
        String css = ResourceHelper.getTextFromFile(file);
        SmartTokenizer tokenizer = new SmartTokenizer(css);
        
        
    }    
}
