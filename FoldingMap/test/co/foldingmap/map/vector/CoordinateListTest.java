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
import co.foldingmap.map.vector.LatLonAltBox;
import co.foldingmap.map.vector.CoordinateList;
import java.util.ArrayList;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author Alec
 */
public class CoordinateListTest {
    
    public CoordinateListTest() {
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
     * Test of add method, of class CoordinateList.
     */
    @Test
    public void testAdd_int_GenericType() {
        Coordinate                 c1, c2, c3;
        CoordinateList<Coordinate> cList;
        int                        location;
        
        location = 1;
        cList    = new CoordinateList<Coordinate>();

        c1 = new Coordinate(300, 5.11f, 10.0f);
        c2 = new Coordinate(  0, 5.45f, 12.0f);
        c3 = new Coordinate(122, 5.01f, 11.1f);

        cList.add(c1);
        cList.add(c2);
        cList.add(location, c3);

        assertEquals(c3, cList.get(1));
    }

    /**
     * Test of add method, of class CoordinateList.
     */
    @Test
    public void testAdd_GenericType() {
        Coordinate                 c1, c2;
        CoordinateList<Coordinate> cList;

        cList = new CoordinateList<Coordinate>();
        c1    = new Coordinate(300, 5.11f, 10.0f);
        c2    = new Coordinate(  0, 5.45f, 12.0f);

        cList.add(c1);
        cList.add(c2);

        assertEquals(c1, cList.get(0));
        assertEquals(c2, cList.get(1));
    }

    /**
     * Test of addAll method, of class CoordinateList.
     */
    @Test
    public void testAddAll() {
        Coordinate                 c1, c2, c3;
        CoordinateList<Coordinate> cList1, cList2;

        cList1 = new CoordinateList<Coordinate>();
        cList2 = new CoordinateList<Coordinate>();
        c1     = new Coordinate(300, 5.11f, 10.0f);
        c2     = new Coordinate(  0, 5.45f, 12.0f);
        c3     = new Coordinate(122, 5.01f, 11.1f);

        cList1.add(c1);
        cList1.add(c2);
        cList2.add(c3);

        cList2.addAll(cList1);

        assertEquals(3, cList2.size());
        assertEquals(cList2.get(0), c3);
        assertEquals(cList2.get(1), c1);
        assertEquals(cList2.get(2), c2);
    }

    /**
     * Test of clone method, of class CoordinateList.
     */
    @Test
    public void testClone() {
        Coordinate                 c1, c2, c3;
        CoordinateList<Coordinate> cList1, cList2;

        cList1 = new CoordinateList<Coordinate>();
        c1     = new Coordinate(300, 5.11f, 10.0f);
        c2     = new Coordinate(  0, 5.45f, 12.0f);
        c3     = new Coordinate(122, 5.01f, 11.1f);

        cList1.add(c1);
        cList1.add(c2);
        cList1.add(c3);

        cList2 = cList1.clone();

        assertEquals(cList1.size(), cList2.size());
        assertEquals(cList1.get(0), cList2.get(0));
        assertEquals(cList1.get(1), cList2.get(1));
        assertEquals(cList1.get(2), cList2.get(2));
    }

    /**
     * Test of contains method, of class CoordinateList.
     */
    @Test
    public void testContains() {
        Coordinate                 c1, c2, c3;
        CoordinateList<Coordinate> cList1;
        
        cList1 = new CoordinateList<Coordinate>();
        c1     = new Coordinate(300, 5.11f, 10.0f);
        c2     = new Coordinate(  0, 5.45f, 12.0f);
        c3     = new Coordinate(122, 5.01f, 11.1f);

        cList1.add(c1);
        cList1.add(c2);
        cList1.add(c3);  
        
        assertEquals(cList1.contains(c1), true);
        assertEquals(cList1.contains(c2), true);
        assertEquals(cList1.contains(c3), true);
    }

    /**
     * Test of forceAdd method, of class CoordinateList.
     */
    @Test
    public void testForceAdd() {
        Coordinate                 c1, c2;
        CoordinateList<Coordinate> cList;

        c1    = new Coordinate(0, 5.12f, 10.5f);
        c2    = new Coordinate(0, 5.12f, 10.5f);
        cList = new CoordinateList<Coordinate>();

        cList.add(c1);
        cList.forceAdd(c2);

        if (cList.size() == 2) {
            assertEquals(c1, cList.get(0));
            assertEquals(c2, cList.get(1));
        } else {
            fail("The second, identical Coordinate was not added.");
        }
    }

    /**
     * Test of get method, of class CoordinateList.
     */
    @Test
    public void testGet_int() {
        Coordinate                 c1;
        CoordinateList<Coordinate> cList;

        cList = new CoordinateList<Coordinate>();
        c1    = new Coordinate(300, 5.11f, 10.0f);

        cList.add(c1);
        assertEquals(c1, cList.get(0));
    }

    /**
     * Test of get method, of class CoordinateList.
     */
    @Test
    public void testGet_LatLonAltBox() {
        Coordinate                 c1, c2, c3, c4;
        CoordinateList<Coordinate> cList1, cList2;
        LatLonAltBox               boundary;
        
        boundary = new LatLonAltBox(6.0f, 4.5f, 13f, 9f, 0, 2000);
        cList1   = new CoordinateList<Coordinate>();
        c1       = new Coordinate(300,  5.11f,   10.0f);
        c2       = new Coordinate(  0,  5.45f,   12.0f);
        c3       = new Coordinate(122,  5.01f,   11.1f);
        c4       = new Coordinate( 70, 44.56f, -123.2f);
        
        cList1.add(c1);
        cList1.add(c2);
        cList1.add(c3);
        cList1.add(c4);
        
        cList2 = cList1.get(boundary);
        
        assertEquals(cList2.contains(c1), true);
        assertEquals(cList2.contains(c2), true);
        assertEquals(cList2.contains(c3), true);        
    }

    /**
     * Test of getArrayList method, of class CoordinateList.
     */
    @Test
    public void testGetArrayList() {
        ArrayList<Coordinate>      aList;
        Coordinate                 c1, c2, c3;
        CoordinateList<Coordinate> cList;

        cList = new CoordinateList<Coordinate>();
        c1    = new Coordinate(300, 5.11f, 10.0f);
        c2    = new Coordinate(  0, 5.45f, 12.0f);
        c3    = new Coordinate(122, 5.01f, 11.1f);

        cList.add(c1);
        cList.add(c2);
        cList.add(c3);

        aList = cList.getArrayList();

        assertEquals(3,  aList.size());
        assertEquals(aList.get(0),  cList.get(0));
        assertEquals(aList.get(1),  cList.get(1));
        assertEquals(aList.get(2),  cList.get(2));
    }

    /**
     * Test of getCoordinatesBetween method, of class CoordinateList.
     */
    @Test
    public void testGetCoordinatesBetween() {
        Coordinate                 c1, c2, c3, c4;
        CoordinateList<Coordinate> cList1, cList2;
        
        cList1   = new CoordinateList<Coordinate>();
        c1       = new Coordinate(300,  5.11f,   10.0f);
        c2       = new Coordinate(  0,  5.45f,   12.0f);
        c3       = new Coordinate(122,  5.01f,   11.1f);
        c4       = new Coordinate( 70, 44.56f, -123.2f);
        
        cList1.add(c1);
        cList1.add(c2);
        cList1.add(c3);
        cList1.add(c4);
        
        cList2 = cList1.getCoordinatesBetween(c1, c3);
        
        assertEquals(cList2.get(0), c1);  
        assertEquals(cList2.get(1), c2); 
        assertEquals(cList2.get(2), c3); 
    }

    /**
     * Test of getCoordinateClosestTo method, of class CoordinateList.
     */
    @Test
    public void testGetCoordinateClosestTo() {
        Coordinate                 c1, c2, c3, c4, c5;
        CoordinateList<Coordinate> cList1;
        
        cList1   = new CoordinateList<Coordinate>();
        c1       = new Coordinate(300,  5.11f,   10.0f);
        c2       = new Coordinate(  0,  5.45f,   12.0f);
        c3       = new Coordinate(122, 10.01f,   14.1f);
        c4       = new Coordinate( 70, 44.56f, -123.2f);
        
        cList1.add(c2);
        cList1.add(c3);
        cList1.add(c4);
        
        c5 = cList1.getCoordinateClosestTo(c1);
        
        assertEquals(c5, c2);  
    }

    /**
     * Test of indexOf method, of class CoordinateList.
     */
    @Test
    public void testIndexOf() {
        Coordinate                 c1, c2, c3, c4;
        CoordinateList<Coordinate> cList1;
        
        cList1   = new CoordinateList<Coordinate>();
        c1       = new Coordinate(300,  5.11f,   10.0f);
        c2       = new Coordinate(  0,  5.45f,   12.0f);
        c3       = new Coordinate(122, 10.01f,   14.1f);
        c4       = new Coordinate( 70, 44.56f, -123.2f);
        
        cList1.add(c1);
        cList1.add(c2);
        cList1.add(c3);
        cList1.add(c4);
        
        assertEquals(cList1.indexOf(c3), 2);  
    }

    /**
     * Test of getEarliestDate method, of class CoordinateList.
     */
    @Test
    public void testGetEarliestDate() {
        Coordinate                 c1, c2, c3, c4;
        CoordinateList<Coordinate> cList1;
        long                       value;
        
        cList1   = new CoordinateList<Coordinate>();
        c1       = new Coordinate(300,  5.11f,   10.0f, "2010-03-10T13:11:11Z");
        c2       = new Coordinate( 10,  5.45f,   12.0f, "2010-03-11T11:23:45Z");
        c3       = new Coordinate(122, 10.01f,   14.1f, "2011-01-12T15:01:55Z");
        c4       = new Coordinate( 70, 44.56f, -123.2f, "2012-04-01T13:25:15Z");
        
        cList1.add(c1);
        cList1.add(c2);
        cList1.add(c3);
        cList1.add(c4);
        
        value = cList1.getEarliestDate();
                
        assertEquals(c1.getDate(), value, 0.0); 
    }

    /**
     * Test of getEasternMostLongitude method, of class CoordinateList.
     */
    @Test
    public void testGetEasternMostLongitude() {
        Coordinate                 c1, c2, c3, c4;
        CoordinateList<Coordinate> cList1;
        double                     value;
        
        cList1   = new CoordinateList<Coordinate>();
        c1       = new Coordinate(300,  5.11f,   10.0f);
        c2       = new Coordinate(  0,  5.45f,   12.0f);
        c3       = new Coordinate(122, 10.01f,   14.1f);
        c4       = new Coordinate( 70, 44.56f, -123.2f);
        
        cList1.add(c1);
        cList1.add(c2);
        cList1.add(c3);
        cList1.add(c4);
        
        value = cList1.getEasternMostLongitude();
                
        assertEquals(14.1, value, 0.00001);  
    }

    /**
     * Test of getLatestDate method, of class CoordinateList.
     */
    @Test
    public void testGetLatestDate() {
        Coordinate                 c1, c2, c3, c4;
        CoordinateList<Coordinate> cList1;
        long                       value;
        
        cList1   = new CoordinateList<Coordinate>();
        c1       = new Coordinate(300,  5.11f,   10.0f, "2010-03-10T13:11:11Z");
        c2       = new Coordinate( 10,  5.45f,   12.0f, "2010-03-11T11:23:45Z");
        c3       = new Coordinate(122, 10.01f,   14.1f, "2011-01-12T15:01:55Z");
        c4       = new Coordinate( 70, 44.56f, -123.2f, "2012-04-01T13:25:15Z");
        
        cList1.add(c1);
        cList1.add(c2);
        cList1.add(c3);
        cList1.add(c4);
        
        value = cList1.getLatestDate();
                
        assertEquals(c4.getDate(), value, 0.0); 
    }

    /**
     * Test of getMaxAltitude method, of class CoordinateList.
     */
    @Test
    public void testGetMaxAltitude() {
        Coordinate                 c1, c2, c3, c4;
        CoordinateList<Coordinate> cList1;
        float                      value;
        
        cList1   = new CoordinateList<Coordinate>();
        c1       = new Coordinate(300,  5.11f,   10.0f);
        c2       = new Coordinate( 10,  5.45f,   12.0f);
        c3       = new Coordinate(122, 10.01f,   14.1f);
        c4       = new Coordinate( 70, 44.56f, -123.2f);
        
        cList1.add(c1);
        cList1.add(c2);
        cList1.add(c3);
        cList1.add(c4);
        
        value = cList1.getMaxAltitude();
                
        assertEquals(300, value, 0.0); 
    }

    /**
     * Test of getMinAltitude method, of class CoordinateList.
     */
    @Test
    public void testGetMinAltitude() {
        Coordinate                 c1, c2, c3, c4;
        CoordinateList<Coordinate> cList1;
        float                      value;
        
        cList1   = new CoordinateList<Coordinate>();
        c1       = new Coordinate(300,  5.11f,   10.0f);
        c2       = new Coordinate( 10,  5.45f,   12.0f);
        c3       = new Coordinate(122, 10.01f,   14.1f);
        c4       = new Coordinate( 70, 44.56f, -123.2f);
        
        cList1.add(c1);
        cList1.add(c2);
        cList1.add(c3);
        cList1.add(c4);
        
        value = cList1.getMinAltitude();
                
        assertEquals(10, value, 0.0); 
    }

    /**
     * Test of getNorthernMostLatitude method, of class CoordinateList.
     */
    @Test
    public void testGetNorthernMostLatitude() {
        Coordinate                 c1, c2, c3, c4;
        CoordinateList<Coordinate> cList1;
        double                     value;
        
        cList1   = new CoordinateList<Coordinate>();
        c1       = new Coordinate(300,  5.11f,   10.0f);
        c2       = new Coordinate(  0,  5.45f,   12.0f);
        c3       = new Coordinate(122, 10.01f,   14.1f);
        c4       = new Coordinate( 70, 44.56f, -123.2f);
        
        cList1.add(c1);
        cList1.add(c2);
        cList1.add(c3);
        cList1.add(c4);
        
        value = cList1.getNorthernMostLatitude();
                
        assertEquals(44.56, value, 0.00001);  
    }

    /**
     * Test of getSouthernMostLatitude method, of class CoordinateList.
     */
    @Test
    public void testGetSouthernMostLatitude() {
        Coordinate                 c1, c2, c3, c4;
        CoordinateList<Coordinate> cList1;
        double                     value;
        
        cList1   = new CoordinateList<Coordinate>();
        c1       = new Coordinate(300,  5.11f,   10.0f);
        c2       = new Coordinate(  0,  5.45f,   12.0f);
        c3       = new Coordinate(122, 10.01f,   14.1f);
        c4       = new Coordinate( 70, 44.56f, -123.2f);
        
        cList1.add(c1);
        cList1.add(c2);
        cList1.add(c3);
        cList1.add(c4);
        
        value = cList1.getSouthernMostLatitude();
                
        assertEquals(5.11, value, 0.00001); 
    }

    /**
     * Test of getWesternMostLongitude method, of class CoordinateList.
     */
    @Test
    public void testGetWesternMostLongitude() {
        Coordinate                 c1, c2, c3, c4;
        CoordinateList<Coordinate> cList1;
        double                     value;
        
        cList1   = new CoordinateList<Coordinate>();
        c1       = new Coordinate(300,  5.11f,   10.0f);
        c2       = new Coordinate(  0,  5.45f,   12.0f);
        c3       = new Coordinate(122, 10.01f,   14.1f);
        c4       = new Coordinate( 70, 44.56f, -123.2f);
        
        cList1.add(c1);
        cList1.add(c2);
        cList1.add(c3);
        cList1.add(c4);
        
        value = cList1.getWesternMostLongitude();
                
        assertEquals(-123.2, value, 0.00001); 
    }

    /**
     * Test of isEndPoint method, of class CoordinateList.
     */
    @Test
    public void testIsEndPoint() {
        Coordinate                 c1, c2, c3;
        CoordinateList<Coordinate> cList;

        cList = new CoordinateList<Coordinate>();
        c1    = new Coordinate(300, 5.11f, 10.0f);
        c2    = new Coordinate(  0, 5.45f, 12.0f);
        c3    = new Coordinate(122, 5.01f, 11.1f);

        cList.add(c1);
        cList.add(c2);
        cList.add(c3);

        assertEquals(true,  cList.isEndPoint(c1));
        assertEquals(true,  cList.isEndPoint(c3));
        assertEquals(false, cList.isEndPoint(c2));
    }

    /**
     * Test of lastCoordinate method, of class CoordinateList.
     */
    @Test
    public void testLastCoordinate() {
        Coordinate                 c1, c2, c3;
        CoordinateList<Coordinate> cList;

        System.out.println("lastCoordinate");

        cList = new CoordinateList<Coordinate>();
        c1    = new Coordinate(300, 5.11f, 10.0f);
        c2    = new Coordinate(  0, 5.45f, 12.0f);
        c3    = new Coordinate(122, 5.01f, 11.1f);

        cList.add(c1);
        cList.add(c2);
        cList.add(c3);

        assertEquals(c3, cList.lastCoordinate());
    }

    /**
     * Test of remove method, of class CoordinateList.
     */
    @Test
    public void testRemove() {
        Coordinate                 c1, c2, c3;
        CoordinateList<Coordinate> cList;

        System.out.println("remove");

        cList = new CoordinateList<Coordinate>();
        c1    = new Coordinate(300, 5.11f, 10.0f);
        c2    = new Coordinate(  0, 5.45f, 12.0f);
        c3    = new Coordinate(122, 5.01f, 11.1f);

        cList.add(c1);
        cList.add(c2);
        cList.add(c3);
        cList.remove(1);

        assertEquals(c1, cList.get(0));
        assertEquals(c3, cList.get(1));
    }

    /**
     * Test of reverse method, of class CoordinateList.
     */
    @Test
    public void testReverse() {
        Coordinate                 c1, c2, c3;
        CoordinateList<Coordinate> cList;

        cList = new CoordinateList<Coordinate>();
        c1    = new Coordinate(300, 5.11f, 10.0f);
        c2    = new Coordinate(  0, 5.45f, 12.0f);
        c3    = new Coordinate(122, 5.01f, 11.1f);

        cList.add(c1);
        cList.add(c2);
        cList.add(c3);
        cList.reverse();

        assertEquals(c3, cList.get(0));
        assertEquals(c2, cList.get(1));
        assertEquals(c1, cList.get(2));
    }

    /**
     * Test of size method, of class CoordinateList.
     */
    @Test
    public void testSize() {
        Coordinate                 c1, c2;
        CoordinateList<Coordinate> cList;

        cList = new CoordinateList<Coordinate>();
        c1    = new Coordinate(300, 5.11f, 10.0f);
        c2    = new Coordinate(  0, 5.45f, 12.0f);

        cList.add(c1);
        assertEquals(1, cList.size());

        cList.add(c2);
        assertEquals(2, cList.size());
    }
}
