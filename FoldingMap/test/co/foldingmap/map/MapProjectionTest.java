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

import co.foldingmap.map.MapProjection;
import co.foldingmap.map.vector.Coordinate;
import static org.junit.Assert.assertEquals;
import org.junit.*;

/**
 *
 * @author Alec
 */
public class MapProjectionTest {
    
    public MapProjectionTest() {
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
     * Test of getDisplayHeight method, of class MapProjection.
     */
    @Test
    public void testGetDisplayHeight() {
        MapProjection instance = new MapProjectionTest.MapProjectionImpl();
        instance.setDisplaySize(1000f, 1000f);
        
        float expResult = 1000.0f;
        float result    = instance.getDisplayHeight();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getDisplayWidth method, of class MapProjection.
     */
    @Test
    public void testGetDisplayWidth() {
        MapProjection instance = new MapProjectionTest.MapProjectionImpl();
        instance.setDisplaySize(1000f, 1000f);
        
        float expResult = 1000f;
        float result    = instance.getDisplayWidth();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getReferenceLatitude method, of class MapProjection.
     */
    @Test
    public void testGetReferenceLatitude() {
        Coordinate    c        = new Coordinate(0, 5.14f, 10.54f);
        MapProjection instance = new MapProjectionTest.MapProjectionImpl();
        instance.setReference(c);
        
        float  expResult = 5.14f;
        double result    = instance.getReferenceLatitude();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getReferenceLongitude method, of class MapProjection.
     */
    @Test
    public void testGetReferenceLongitude() {
        Coordinate    c        = new Coordinate(0, 5.14f, 10.54f);
        MapProjection instance = new MapProjectionTest.MapProjectionImpl();
        instance.setReference(c);
        
        float  expResult = 10.54f;
        double result    = instance.getReferenceLongitude();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getZoomLevel method, of class MapProjection.
     */
    @Test
    public void testGetZoomLevel() {
        MapProjection instance = new MapProjectionTest.MapProjectionImpl();
        
        instance.setZoomLevel(1000);
        
        float expResult = 1000;
        float result    = instance.getZoomLevel();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of setDisplaySize method, of class MapProjection.
     */
    @Test
    public void testSetDisplaySize() {
        float displayHeight = 1000.0f;
        float displayWidth  = 1000.0f;
        
        MapProjection instance = new MapProjectionTest.MapProjectionImpl();
        instance.setDisplaySize(displayHeight, displayWidth);
    }

    /**
     * Test of setReference method, of class MapProjection.
     */
    @Test
    public void testSetReference() {
        Coordinate  c = new Coordinate(0, 5.14f, 10.54f);

        MapProjection instance = new MapProjectionTest.MapProjectionImpl();
        instance.setReference(c);
    }

    public class MapProjectionImpl extends MapProjection {

        public float getLatitude(float x, float y) {
            return 0.0F;
        }

        public float getLongitude(float x, float y) {
            return 0.0F;
        }

        @Override
        public double getX(Coordinate c) {
            return 0.0F;
        }

        @Override
        public double getY(Coordinate c) {
            return 0.0F;
        }

        @Override
        public void setZoomLevel(float zoomLevel) {
            this.zoomLevel = zoomLevel;
        }

        @Override
        public void shiftMapReference(double x, double y) {
        }

        @Override
        public void zoomIn(double x, double y) {
        }

        @Override
        public void zoomOut(double x, double y) {
        }

        @Override
        public String getViewInfo() {
            return "";
        }

        @Override
        public double getLatitude(double x, double y) {
            return 0.0;
        }

        @Override
        public double getLongitude(double x, double y) {
            return 0.0;
        }

        @Override
        public double getX(double lat, double lon) {
            return 0.0;
        }

        @Override
        public double getY(double lat, double lon) {
            return 0.0;
        }
    }
}
