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

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;

/**
 *
 * @author Alec
 */
public class FontLoader extends Thread {
    private static ArrayList<Font> fontList = new ArrayList();
    
    public FontLoader() {

    }
    
    public static Font[] getFonts() {
        if (fontList.isEmpty()) {
            FontLoader fl = new FontLoader();
            fl.start();
        }
        
        return fontList.toArray(new Font[0]);
    }
    
    @Override
    public void run() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        fontList.clear();
        
        for (Font f: ge.getAllFonts())
            fontList.add(f.deriveFont(12f)); 
    }
}
