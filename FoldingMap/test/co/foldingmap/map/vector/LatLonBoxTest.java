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

package co.foldingmap.map.vector;

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
public class LatLonBoxTest {
    
    public LatLonBoxTest() {
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
     * Test of contains method, of class LatLonBox.
     */
    @Test
    public void testContains() {
        System.out.println("contains");

        LatLonBox instance = new LatLonBox(10, 1, 10, 1);
        assertEquals(true, instance.contains(new Coordinate(0, 5, 5)));
        assertEquals(false, instance.contains(new Coordinate(0, 15, 5)));
        assertEquals(false, instance.contains(new Coordinate(0, 5, 15)));
    }

    /**
     * Test of equals method, of class LatLonBox.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        
        Object obj1 = new LatLonBox(10, 1, 10, 1);
        Object obj2 = new LatLonBox(15, 1, 10, 1);
        LatLonBox instance = new LatLonBox(10, 1, 10, 1);

        assertEquals(true, instance.equals(obj1));
        assertEquals(false, instance.equals(obj2));
    }

    /**
     * Test of getCenter method, of class LatLonBox.
     */
    @Test
    public void testGetCenter() {
        System.out.println("getCenter");
        
        LatLonBox  instance  = new LatLonBox(10, 0, 10, 0);
        Coordinate expResult = new Coordinate(0, 5, 5);
        Coordinate result = instance.getCenter();
        assertEquals(expResult, result);
    }

    /**
     * Test of getHeight method, of class LatLonBox.
     */
    @Test
    public void testGetHeight() {
        System.out.println("getHeight");
        
        LatLonBox instance = new LatLonBox(10, 0, 10, 0);
        assertEquals(10f, instance.getHeight(), 0.0);
    }

    /**
     * Test of getNorth method, of class LatLonBox.
     */
    @Test
    public void testGetNorth() {
        System.out.println("getNorth");
        
        LatLonBox instance = new LatLonBox(10, 0, 10, 0);
        assertEquals(10f, instance.getNorth(), 0.0);
    }

    /**
     * Test of getSouth method, of class LatLonBox.
     */
    @Test
    public void testGetSouth() {
        System.out.println("getSouth");
        
        LatLonBox instance = new LatLonBox(10, -10, 10, 0);
        assertEquals(-10, instance.getSouth(), 0.0);
    }

    /**
     * Test of getEast method, of class LatLonBox.
     */
    @Test
    public void testGetEast() {
        System.out.println("getEast");
        
        LatLonBox instance = new LatLonBox(10, -10, 10, 0);
        assertEquals(10f, instance.getEast(), 0.0);
    }

    /**
     * Test of getWest method, of class LatLonBox.
     */
    @Test
    public void testGetWest() {
        System.out.println("getWest");
        
        LatLonBox instance = new LatLonBox(10, -10, 10, -20);
        assertEquals(-20f, instance.getWest(), 0.0);
    }

    /**
     * Test of getWidth method, of class LatLonBox.
     */
    @Test
    public void testGetWidth() {
        System.out.println("getWidth");
        
        LatLonBox instance = new LatLonBox(10, -10, 10, -20);
        assertEquals(30f, instance.getWidth(), 0.0);
    }

    /**
     * Test of overlaps method, of class LatLonBox.
     */
    @Test
    public void testOverlaps() {
        System.out.println("overlaps");
        
        LatLonBox instance = new LatLonBox(10, -10, 10, -20);
        assertEquals(true, instance.overlaps(new LatLonBox(5, -10, 10, -20)));
    }

    /**
     * Test of setNorth method, of class LatLonBox.
     */
    @Test
    public void testSetNorth() {
        System.out.println("setNorth");
        
        LatLonBox instance = new LatLonBox(10, -10, 10, -20);
        instance.setNorth(45);
        assertEquals(45f, instance.getNorth(), 0.0);
    }

    /**
     * Test of setSouth method, of class LatLonBox.
     */
    @Test
    public void testSetSouth() {
        System.out.println("setSouth");

        LatLonBox instance = new LatLonBox(10, -10, 10, -20);
        instance.setSouth(-40);
        assertEquals(-40f, instance.getSouth(), 0.0);
    }

    /**
     * Test of setEast method, of class LatLonBox.
     */
    @Test
    public void testSetEast() {
        System.out.println("setEast");

        LatLonBox instance = new LatLonBox(10, -10, 10, -20);
        instance.setEast(30);
        assertEquals(30f, instance.getEast(), 0.0);
    }

    /**
     * Test of setWest method, of class LatLonBox.
     */
    @Test
    public void testSetWest() {
        System.out.println("setWest");

        LatLonBox instance = new LatLonBox(10, -10, 10, -20);
        instance.setWest(-40);
        assertEquals(-40f, instance.getWest(), 0.0);
    }

    /**
     * Test of toXML method, of class LatLonBox.
     */
    @Test
    public void testToXML() {
        System.out.println("toXML");
        
        String expResult = "<LatLonBox>\n\t<north>10.0</north>\n\t<south>-10.0</south>\n\t<east>10.0</east>\n\t<west>-20.0</west>\n</LatLonBox>";        
        XmlOutput xmlWriter = new XmlBuffer();
        LatLonBox instance  = new LatLonBox(10, -10, 10, -20);
        instance.toXML(xmlWriter);
        assertEquals(expResult, xmlWriter.toString());
    }
    
}
