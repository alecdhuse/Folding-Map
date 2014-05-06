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
package co.foldingmap.xml;

import co.foldingmap.xml.XmlBuffer;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Alec
 */
public class XmlBufferTest {
    
    public XmlBufferTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of closeTag method, of class XmlBuffer.
     */
    @Test
    public void testCloseTag() {
        String tag         = "color";
        String expResult   = "<color>\n</color>";
        XmlBuffer instance = new XmlBuffer();
        
        instance.openTag(tag);
        instance.closeTag(tag);
        
        String kml = instance.toString();
        
        assertEquals(expResult.equals(kml), true);
    }

    /**
     * Test of openTag method, of class XmlBuffer.
     */
    @Test
    public void testOpenTag() {
        String tag         = "color";
        String expResult   = "<color>\n</color>";
        XmlBuffer instance = new XmlBuffer();
        
        instance.openTag(tag);
        instance.closeTag(tag);
        
        String kml = instance.toString();
        
        assertEquals(expResult.equals(kml), true);
    }

    /**
     * Test of writeTag method, of class XmlBuffer.
     */
    @Test
    public void testWriteTag() {
        String    tagName  = "color";
        String    content  = "ff000000";
        String expResult   = "<color>ff000000</color>";
        XmlBuffer instance = new XmlBuffer();
        
        instance.writeTag(tagName, content);
        String kml = instance.toString();
        
        assertEquals(expResult.equals(kml), true);
    }

    /**
     * Test of writeText method, of class XmlBuffer.
     */
    @Test
    public void testWriteText() {
        String    text     = "text";
        XmlBuffer instance = new XmlBuffer();
        instance.writeText(text);
        
        String kml = instance.toString();
        
        assertEquals(text.equals(kml), true);
    }

    /**
     * Test of writeTextLine method, of class XmlBuffer.
     */
    @Test
    public void testWriteTextLine() {
        String    expResult = "text";
        String    text      = "text";
        XmlBuffer instance  = new XmlBuffer();
        instance.writeTextLine(text);
        
        String kml = instance.toString();
        
        assertEquals(expResult.equals(kml), true);        
    }

    /**
     * Test of toString method, of class XmlBuffer.
     */
    @Test
    public void testToString() {
        String    tagName  = "color";
        String    content  = "ff000000";
        String expResult   = "<color>ff000000</color>";
        XmlBuffer instance = new XmlBuffer();
        
        instance.writeTag(tagName, content);
        String kml = instance.toString();
        
        assertEquals(expResult.equals(kml), true);
    }
}
