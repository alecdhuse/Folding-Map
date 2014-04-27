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
package co.foldingmap.testFileFormats;

import co.foldingmap.map.themes.LineStyle;
import co.foldingmap.xml.XMLTag;
import java.awt.Color;
import org.junit.Ignore;

/**
 *
 * @author Alec Dhuse
 */
@Ignore
public class FmXmlTestData {
    
    public static LineStyle getLineStyleObject() {        
        LineStyle testStyle;
                
        testStyle = new LineStyle("linestyle", new Color(255, 69, 195), 2.5f, LineStyle.SOLID, true);
        testStyle.setOutlineColor(new Color(255, 15, 142, 217));
        testStyle.setOutline(true);
        testStyle.setSelectedOutlineColor(Color.RED);
        testStyle.setSelectedFillColor(new Color(128, 62, 62, 243));
        return testStyle;
    }    
    
    public static XMLTag getLineStyleTag() {
        XMLTag lineStyle, returnTag;        
        
        lineStyle = new XMLTag("LineStyle", new XMLTag("color", "ff45c3ff"));
        lineStyle.addSubtag(new XMLTag("width",                 "2.5")); 
        lineStyle.addSubtag(new XMLTag("selectedFillColor",     "803e3ef3")); 
        lineStyle.addSubtag(new XMLTag("outline",               "1")); 
        lineStyle.addSubtag(new XMLTag("outlineColor",          "ff0f8ed9")); 
        lineStyle.addSubtag(new XMLTag("selectedOutlineColor",  "ff0000ff")); 
        
        returnTag = new XMLTag("Style id=\"linestyle\"", lineStyle);
        
        return returnTag;
    }    
}
