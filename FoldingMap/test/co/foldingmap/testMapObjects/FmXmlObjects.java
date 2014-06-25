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

package co.foldingmap.testMapObjects;

import co.foldingmap.map.Visibility;
import co.foldingmap.map.vector.LatLonBox;
import co.foldingmap.xml.XMLTag;
import java.util.ArrayList;
import org.junit.Ignore;

/**
 *
 * @author dhusea
 */
@Ignore
public class FmXmlObjects {
    
    /**
     * Returns the test object for LatLonBox.
     * 
     * @return 
     */
    public static LatLonBox getLatLonBoxObject() {
        return new LatLonBox(10, -10, 10, -10);
    }
    
    /**
     * Returns the XmlTag for LatLonBox.
     * 
     * @return 
     */
    public static XMLTag getLatLonBoxTag() {
        XMLTag boxTag = new XMLTag("LatLonBox", new XMLTag("north", "10"));
        boxTag.addSubtag(new XMLTag("south", "-10"));
        boxTag.addSubtag(new XMLTag("east",   "10"));
        boxTag.addSubtag(new XMLTag("west", "-10"));
        
        return boxTag;
    }    
    
    /**
     * Test XMLTag object for Visibility. 
     * 
     * @return 
     */
    public static XMLTag getTestVisibilityTag() {
        XMLTag visibilityTag = new XMLTag("Visibility", new XMLTag("maxTileZoom", "18.0"));
        visibilityTag.addSubtag(new XMLTag("minTileZoom", "10.0"));
        
        return visibilityTag;
    }
    
    /**
     * Test Visibility Object
     * 
     * @return 
     */
    public static Visibility getVisibilityObject() {
        return new Visibility(18, 10);
    }
    
    public static ArrayList<XMLTag> getNodesTag() {     
        ArrayList<XMLTag> tags = new ArrayList<XMLTag>();
                
        XMLTag nodeTag1 = new XMLTag("node", "-123.094894,44.06016,0,2014-04-30T04:16:59Z");
        XMLTag nodeTag2 = new XMLTag("node", "-123.095375,44.06009,0,2014-04-30T04:16:59Z");
        XMLTag nodeTag3 = new XMLTag("node", "-123.09568,44.060005,0,2014-04-30T04:16:59Z");
        
        nodeTag1.addProperty("id", "39649625");
        nodeTag2.addProperty("id", "39649628");
        nodeTag3.addProperty("id", "39649631");
        
        tags.add(nodeTag1);
        tags.add(nodeTag2);
        tags.add(nodeTag3);
        
        return tags;
    }
}
