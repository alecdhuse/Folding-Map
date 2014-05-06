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

package co.foldingmap.map;

import co.foldingmap.xml.XmlBuffer;
import co.foldingmap.xml.XmlOutput;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dhusea
 */
public class VisibilityTest {
    
    public VisibilityTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of clone method, of class Visibility.
     */
    @Test
    public void testClone() {
        System.out.println("clone");
        Visibility instance  = new Visibility(20, 10);
        Visibility expResult = new Visibility(20, 10);
        Visibility result    = instance.clone();
        assertEquals(expResult, result);
    }

    /**
     * Test of equals method, of class Visibility.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        
        Visibility test1    = new Visibility(19, 10);
        Visibility test2    = new Visibility(18, 12);
        Visibility instance = new Visibility(19, 10);

        assertEquals(true, instance.equals(test1));
        assertEquals(false, instance.equals(test2));
    }

    /**
     * Test of hashCode method, of class Visibility.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        
        Visibility instance = new Visibility(20, 1);
        
        int expResult = -1147132561;
        int result    = instance.hashCode();
        
        assertEquals(expResult, result);
    }

    /**
     * Test of getMaxTileZoomLevel method, of class Visibility.
     */
    @Test
    public void testGetMaxTileZoomLevel() {
        System.out.println("getMaxTileZoomLevel");
        
        Visibility instance  = new Visibility(18, 10);
        float      expResult = 18F;
        float      result    = instance.getMaxTileZoomLevel();
        
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getMinTileZoomLevel method, of class Visibility.
     */
    @Test
    public void testGetMinTileZoomLevel() {
        System.out.println("getMinTileZoomLevel");

        Visibility instance  = new Visibility(18, 10);
        float      expResult = 10F;
        float      result    = instance.getMinTileZoomLevel();
        
        assertEquals(expResult, result, 0.0);        
    }

    /**
     * Test of isVisible method, of class Visibility.
     */
    @Test
    public void testIsVisible() {
        System.out.println("isVisible");
        
        Visibility test1  = new Visibility(19, 10);
        Visibility test2  = new Visibility(16, 10);
        
        assertEquals(true, test1.isVisible(2000F));
        assertEquals(false, test2.isVisible(2000F));
    }

    /**
     * Test of setMaxTileZoomLevel method, of class Visibility.
     */
    @Test
    public void testSetMaxTileZoomLevel() {
        System.out.println("setMaxTileZoomLevel");
        
        Visibility instance = new Visibility(10, 9);
        instance.setMaxTileZoomLevel(15);
        
        assertEquals(15, instance.getMaxTileZoomLevel(), 0.0);
    }

    /**
     * Test of setMinTileZoomLevel method, of class Visibility.
     */
    @Test
    public void testSetMinTileZoomLevel() {
        System.out.println("setMinTileZoomLevel");
        
        Visibility instance = new Visibility(10, 9);
        instance.setMinTileZoomLevel(5);
        
        assertEquals(5, instance.getMinTileZoomLevel(), 0.0);
    }

    /**
     * Test of toXML method, of class Visibility.
     */
    @Test
    public void testToXML() {
        System.out.println("toXML");
        
        XmlOutput  xmlWriter = new XmlBuffer();
        Visibility instance  = new Visibility(18, 10);              
        String     expresult = "<Visibility>\n\t<maxTileZoom>18.0</maxTileZoom>\n\t<minTileZoom>10.0</minTileZoom>\n</Visibility>";
        
        instance.toXML(xmlWriter);  
        String result = xmlWriter.toString();
        assertEquals(expresult, result);
    }
    
}
