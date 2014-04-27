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
package co.foldingmap.map.vector;

import co.foldingmap.map.vector.Coordinate;
import java.awt.geom.Point2D;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import static org.junit.Assert.assertEquals;
import org.junit.*;

/**
 *
 * @author Alec
 */
public class CoordinateTest {
    
    public CoordinateTest() {
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
     * Test of copy method, of class Coordinate.
     */
    @Test
    public void testCopy() {
        Coordinate instance  = new Coordinate(1000f, 5.14f, 10.52f);
        Coordinate result    = instance.copy();
        assertEquals(instance.getAltitude(),  result.getAltitude(),  0);
        assertEquals(instance.getLatitude(),  result.getLatitude(),  0);
        assertEquals(instance.getLongitude(), result.getLongitude(), 0);
    }

    /**
     * Test of equals method, of class Coordinate.
     */
    @Test
    public void testEquals() {
        Coordinate coordinateToCompair = new Coordinate(1000f, 5.14f, 10.52f);
        Coordinate instanceFalse       = new Coordinate(2000f, 5.11f, 10.52f);
        Coordinate instanceTrue        = new Coordinate(1000f, 5.14f, 10.52f);
        boolean    resultFalse         = instanceFalse.equals(coordinateToCompair);
        boolean    resultTrue          = instanceTrue.equals(coordinateToCompair);
        assertEquals(false, resultFalse);
        assertEquals(true,  resultTrue);
    }

    /**
     * Test of getAltitude method, of class Coordinate.
     */
    @Test
    public void testGetAltitude() {
        Coordinate instance = new Coordinate(1000f, 5.14f, 10.52f);
        double expResult    = 1000.0;
        double result       = instance.getAltitude();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getAsPoint2D method, of class Coordinate.
     */
    @Test
    public void testGetAsPoint2D() {
        Coordinate instance = new Coordinate(1000f, 5.14f, 10.52f);
        Point2D expResult   = new Point2D.Float(10.52f, 5.14f);
        Point2D result      = instance.getAsPoint2D();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDate method, of class Coordinate.
     */
    @Test
    public void testGetDate() {
        Coordinate        instance  = new Coordinate(10f, 5.14f, 10.52f, "2010-03-27T14:31:00Z");
        
        GregorianCalendar calendar  = new GregorianCalendar();
            
        calendar.setTimeZone(new SimpleTimeZone(0, "Z"));
        calendar.set(2010, 2, 27, 14, 31, 0);  
        
        long              expResult = calendar.getTimeInMillis();        
        long              result    = instance.getDate();
        
        assertEquals(expResult, result, 10);
    }

    /**
     * Test of getLatitude method, of class Coordinate.
     */
    @Test
    public void testGetLatitude() {
        Coordinate  instance    = new Coordinate(1000f, 5.14f, 10.52f);
        float       expResult   = 5.14f;
        double      result      = instance.getLatitude();
                
        assertEquals(expResult, result, 0.001);
    }

    /**
     * Test of getLatitudeInDecimal method, of class Coordinate.
     */
    @Test
    public void testGetLatitudeInDecimal() {
        float  latitude  = 0.089f;
        float  expResult = 5.099324703216553f;
        double result    = Coordinate.getLatitudeInDecimal(latitude);
        
        assertEquals(expResult, result, 0.0001);
    }

    /**
     * Test of getLatitudeInRadians method, of class Coordinate.
     */
    @Test
    public void testGetLatitudeInRadians_float() {
        float  latitude  = 5.14f;
        float  expResult = 0.08970992355249f;
        double result = Coordinate.getLatitudeInRadians(latitude);
        
        assertEquals(expResult, result, 0.0000001);
    }

    /**
     * Test of getLatitudeInRadians method, of class Coordinate.
     */
    @Test
    public void testGetLatitudeInRadians_0args() {        
        Coordinate  instance  = new Coordinate(1000f, 5.14f, 10.52f);
        float       expResult = 0.08970992355249f;
        double      result   = instance.getLatitudeInRadians();
        
        assertEquals(expResult, result, 0.000001);
    }

    /**
     * Test of getLongitude method, of class Coordinate.
     */
    @Test
    public void testGetLongitude() {
        Coordinate  instance  = new Coordinate(1000f, 5.14f, 10.52f);
        float       expResult = 10.52f;
        double      result    = instance.getLongitude();
        
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getLongitudeInDecimal method, of class Coordinate.
     */
    @Test
    public void testGetLongitudeInDecimal() {
        float  latitude  = 0.089f;
        float  expResult = 5.099324524980204f;
        double result    = Coordinate.getLatitudeInDecimal(latitude);
        
        assertEquals(expResult, result, 0.000001);
    }

    /**
     * Test of getLongitudeInRadians method, of class Coordinate.
     */
    @Test
    public void testGetLongitudeInRadians_float() {
        Coordinate instance = new Coordinate(1000f, 5.14f, 10.52f);
        float  expResult    = 10.52f;
        double result       = instance.getLongitude();
        assertEquals(expResult, result, 0.000001);
    }

    /**
     * Test of getLongitudeInRadians method, of class Coordinate.
     */
    @Test
    public void testGetLongitudeInRadians_0args() {
        Coordinate instance = new Coordinate(1000f, 5.14f, 10.52f);
        float  expResult    = 0.18360865116119385f;
        double result       = instance.getLongitudeInRadians();
        assertEquals(expResult, result, 0.0000001);
    }

    /**
     * Test of getTimestamp method, of class Coordinate.
     */
    @Test
    public void testGetTimestamp() {
        System.out.println("getTimestamp");
        Coordinate instance = new Coordinate(1000f, 5.14f, 10.52f, "2010-09-15T16:08:04Z");
        String expResult    = "2010-09-15T16:08:04Z";
        String result       = instance.getTimestamp();
        assertEquals(expResult, result);
    }

    /**
     * Test of isEastOf method, of class Coordinate.
     */
    @Test
    public void testIsEastOf() {
        System.out.println("isEastOf");
        Coordinate c             = new Coordinate(1000f, 5.14f, 10.52f);
        Coordinate instanceFalse = new Coordinate(1000f, 5.14f, 10.00f);
        Coordinate instanceTrue  = new Coordinate(1000f, 5.14f, 11.00f);
        boolean    resultFalse   = instanceFalse.isEastOf(c);
        boolean    resultTrue    = instanceTrue.isEastOf(c);
        assertEquals(false, resultFalse);
        assertEquals(true,  resultTrue);
    }

    /**
     * Test of isLatitudeValid method, of class Coordinate.
     */
    @Test
    public void testIsLatitudeValid_String() {
        System.out.println("isLatitudeValid_String");
        boolean resultFalse1 = Coordinate.isLatitudeValid("91");
        boolean resultFalse2 = Coordinate.isLatitudeValid("-91");
        boolean resultTrue   = Coordinate.isLatitudeValid("5.14");
        assertEquals(false, resultFalse1);
        assertEquals(false, resultFalse2);
        assertEquals(true,  resultTrue);
    }

    /**
     * Test of isLatitudeValid method, of class Coordinate.
     */
    @Test
    public void testIsLatitudeValid_float() {
        boolean resultFalse1 = Coordinate.isLatitudeValid(91);
        boolean resultFalse2 = Coordinate.isLatitudeValid(-91);
        boolean resultTrue   = Coordinate.isLatitudeValid(5.14f);
        assertEquals(false, resultFalse1);
        assertEquals(false, resultFalse2);
        assertEquals(true,  resultTrue);
    }

    /**
     * Test of isLongitudeValid method, of class Coordinate.
     */
    @Test
    public void testIsLongitudeValid_String() {
        boolean resultFalse1 = Coordinate.isLongitudeValid("181");
        boolean resultFalse2 = Coordinate.isLongitudeValid("-181");
        boolean resultTrue   = Coordinate.isLongitudeValid("10.52");
        assertEquals(false, resultFalse1);
        assertEquals(false, resultFalse2);
        assertEquals(true,  resultTrue);
    }

    /**
     * Test of isLongitudeValid method, of class Coordinate.
     */
    @Test
    public void testIsLongitudeValid_float() {
        boolean resultFalse1 = Coordinate.isLongitudeValid(181);
        boolean resultFalse2 = Coordinate.isLongitudeValid(-181);
        boolean resultTrue   = Coordinate.isLongitudeValid(10.52f);
        
        assertEquals(false, resultFalse1);
        assertEquals(false, resultFalse2);
        assertEquals(true,  resultTrue);
    }

    /**
     * Test of isNorthOf method, of class Coordinate.
     */
    @Test
    public void testIsNorthOf() {
        Coordinate c             = new Coordinate(1000f, 5.14f, 10.52f);
        Coordinate instanceFalse = new Coordinate(1000f, 3.14f, 10.52f);
        Coordinate instanceTrue  = new Coordinate(1000f, 6.14f, 10.52f);
        boolean    resultFalse   = instanceFalse.isNorthOf(c);
        boolean    resultTrue    = instanceTrue.isNorthOf(c);
        assertEquals(false, resultFalse);
        assertEquals(true , resultTrue);
    }

    /**
     * Test of isShared method, of class Coordinate.
     */
    @Test
    public void testIsShared() {
        Coordinate instanceFalse = new Coordinate(1000f, 5.14f, 10.52f);
        Coordinate instanceTrue  = new Coordinate(1000f, 5.14f, 10.52f);

        instanceFalse.setShared(false);
        instanceTrue.setShared(true);

        boolean resultFalse = instanceFalse.isShared();
        boolean resultTrue  = instanceTrue.isShared();

        assertEquals(false, resultFalse);
        assertEquals(true,  resultTrue);
    }

    /**
     * Test of isSouthOf method, of class Coordinate.
     */
    @Test
    public void testIsSouthOf() {
        Coordinate c             = new Coordinate(1000f,  5.14f, 10.52f);
        Coordinate instanceFalse = new Coordinate(1000f,  6.14f, 10.52f);
        Coordinate instanceTrue1 = new Coordinate(1000f,  1.14f, 10.52f);
        Coordinate instanceTrue2 = new Coordinate(1000f, -3.14f, 10.52f);
        boolean    resultFalse   = instanceFalse.isSouthOf(c);
        boolean    resultTrue1   = instanceTrue1.isSouthOf(c);
        boolean    resultTrue2   = instanceTrue2.isSouthOf(c);

        assertEquals(false, resultFalse);
        assertEquals(true,  resultTrue1);
        assertEquals(true,  resultTrue2);
    }

    /**
     * Test of isWestOf method, of class Coordinate.
     */
    @Test
    public void testIsWestOf() {
        Coordinate c             = new Coordinate(1000f, 5.14f, 10.52f);
        Coordinate instanceFalse = new Coordinate(1000f, 5.14f, 15.52f);
        Coordinate instanceTrue1 = new Coordinate(1000f, 5.14f,  9.52f);
        Coordinate instanceTrue2 = new Coordinate(1000f, 5.14f, -9.52f);
        boolean    resultFalse   = instanceFalse.isWestOf(c, 90);
        boolean    resultTrue1   = instanceTrue1.isWestOf(c, 90);
        boolean    resultTrue2   = instanceTrue2.isWestOf(c, 90);

        assertEquals(false, resultFalse);
        assertEquals(true,  resultTrue1);
        assertEquals(true,  resultTrue2);
    }

    /**
     * Test of reckonCoordinate method, of class Coordinate.
     */
    @Test
    public void testReckonCoordinate() {
        double distance      =  2.0;
        double azimuth       = 45.0;
        Coordinate instance  = new Coordinate(1000f, 5.14f, 10.52f);
        Coordinate expResult = new Coordinate(1000f, 5.152718201039167f, 10.532771f);
        Coordinate result    = instance.reckonCoordinate(distance, azimuth);
        assertEquals(true,   expResult.equals(result));
    }

    /**
     * Test of setAltitude method, of class Coordinate.
     */
    @Test
    public void testSetAltitude() {
        float      newAltitude  = 1000.0f;
        Coordinate instance     = new Coordinate(1000f, 5.14f, 10.52f);
        
        instance.setAltitude(newAltitude);
        assertEquals(newAltitude, instance.getAltitude(), 0);
    }

    /**
     * Test of setLatitude method, of class Coordinate.
     */
    @Test
    public void testSetLatitude() {
        float       newLatitude  = 6.1f;
        Coordinate  instance     = new Coordinate(1000f, 5.14f, 10.52f);
        
        instance.setLatitude(newLatitude);
        assertEquals(newLatitude, instance.getLatitude(), 0);
    }

    /**
     * Test of setLongitude method, of class Coordinate.
     */
    @Test
    public void testSetLongitude() {
        float       newLongitude = 11.1f;
        Coordinate  instance = new Coordinate(1000f, 5.14f, 10.52f);
        
        instance.setLongitude(newLongitude);
        assertEquals(newLongitude, instance.getLongitude(), 0);
    }

    /**
     * Test of setShared method, of class Coordinate.
     */
    @Test
    public void testSetShared() {
        boolean s           = true;
        Coordinate instance = new Coordinate(1000f, 5.14f, 10.52f);
        instance.setShared(s);
        assertEquals(s, instance.isShared());
    }

    /**
     * Test of setTimestamp method, of class Coordinate.
     */
    @Test
    public void testSetTimestamp() {
        String newTimestamp = "2010-11-15T16:08:04Z";
        Coordinate instance = new Coordinate(1000f, 5.14f, 10.52f, "2010-11-15T16:08:04Z");
        instance.setTimestamp(newTimestamp);
        assertEquals(newTimestamp, instance.getTimestamp());
    }

    /**
     * Test of toString method, of class Coordinate.
     */
    @Test
    public void testToString() {
        Coordinate instance = new Coordinate(1000f, 5.14f, 10.52f, "2010-09-15T16:08:04Z");
        String expResult    = "10.52,5.14,1000,2010-09-15T16:08:04Z";
        String result       = instance.toString();
        assertEquals(expResult, result);
    }
}
