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

import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.LatLonAltBox;
import co.foldingmap.map.vector.LineString;
import co.foldingmap.graphicsSupport.Graphics2DTest;
import co.foldingmap.map.MapView;
import co.foldingmap.map.labeling.LabelInstruction;
import co.foldingmap.map.labeling.LineStringLabel;
import co.foldingmap.map.themes.ColorStyle;
import co.foldingmap.map.themes.LabelStyle;
import co.foldingmap.testMapObjects.TestRoadLineString;
import co.foldingmap.testMapObjects.TestRoadLineString2;
import co.foldingmap.xml.XmlBuffer;
import co.foldingmap.xml.XmlOutput;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author Alec
 */
public class LineStringTest {
    public LineString line1, line2;
    public MapView    mapView2_1;
    
    public LineStringTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        line1 = new LineString("Northwest Johnson Street", "Road - City Secondary", TestRoadLineString.getCoordinates());
        line2 = new LineString("Southwest 11th Avenue",    "Road - City Tertiary",  TestRoadLineString2.getCoordinates());
        
        mapView2_1 = TestRoadLineString2.getMapView1();
        
        //Set the Coordinates' on screen locations
        for (Coordinate c: line2.getCoordinateList()) {
            float x = mapView2_1.getX(c, MapView.NO_WRAP);
            float y = mapView2_1.getY(c);
            
            c.setCenterPoint(x, y);
        }         
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of commonConstructor method, of class LineString.
     */
    @Test
    public void testCommonConstructor() {
        System.out.println("commonConstructor");        
        line1.commonConstructor("New Name", "Road");

        assertEquals("New Name", line1.getName());
        assertEquals("Road",     line1.getObjectClass());
    }

    /**
     * Test of copy method, of class LineString.
     */
    @Test
    public void testCopy() {
        System.out.println("copy");
        LineString result = (LineString) line1.copy();
        
        assertEquals(line1.getAllCustomData(),  result.getAllCustomData());
        assertEquals(line1.getAltitudeMode(),   result.getAltitudeMode());
        assertEquals(line1.getBoundingBox(),    result.getBoundingBox());
        assertEquals(line1.getCoordinateList(), result.getCoordinateList());
        assertEquals(line1.getDescription(),    result.getDescription());
        assertEquals(line1.getName(),           result.getName());
        assertEquals(line1.getObjectClass(),    result.getObjectClass());
        assertEquals(line1.getParentLayer(),    result.getParentLayer());
        assertEquals(line1.getReference(),      result.getReference());
        assertEquals(line1.getVisibility(),     result.getVisibility());
        assertEquals(line1.getTimestamp(),      result.getTimestamp());
    }   

    /**
     * Test of convertCoordinatesToLines method, of class LineString.
     */
    @Test
    public void testConvertCoordinatesToLines() {
        System.out.println("convertCoordinatesToLines");

        line1.convertCoordinatesToLines(mapView2_1);
        
        assertEquals(false, line1.lineLeftInit);
        assertEquals(false, line1.lineRightInit);
        
        //TODO: Add more Tests for this.
    }

    /**
     * Test of createLabel method, of class LineString.
     */
    @Test
    public void testCreateLabel() {
        System.out.println("createLabel");
        
        Graphics2D g2         = new Graphics2DTest();
        LabelStyle labelStyle = TestRoadLineString2.getLineStyle1().getLabel();      
        
        ArrayList<LineStringLabel> expResult = TestRoadLineString2.getLabels(g2);
        ArrayList<LineStringLabel> result    = line2.createLabel(g2, mapView2_1, labelStyle);
        LineStringLabel            label     = result.get(1);

        if (result.size() != expResult.size()) {
            fail("The number of labels generated was different then what was expected.");
        } else {
            for (int i = 0; i < result.size(); i++) {
                LineStringLabel label1 = expResult.get(i);
                LineStringLabel label2 = result.get(i);

                if (label1.getLabelInstruction().size() != label2.getLabelInstruction().size()) {
                    fail("The number of label instructions was different then what was expected.");
                } else {
                    for (int j = 0; j < label1.getLabelInstruction().size(); j++) {
                        LabelInstruction instruct1 = label1.getLabelInstruction().get(j);
                        LabelInstruction instruct2 = label2.getLabelInstruction().get(j);
                        
                        assertEquals(instruct1.getAngle(), instruct2.getAngle(), 0.1);
                        assertEquals(instruct1.getX(),     instruct2.getX(),     0.1);
                        assertEquals(instruct1.getY(),     instruct2.getY(),     0.1);
                        assertEquals(instruct1.getText(),  instruct2.getText());
                        
                        assertEquals(instruct1.getRotationFocus(), instruct2.getRotationFocus());                        
                    }
                }
            }
        }
    }

    /**
     * Test of drawObject method, of class LineString.
     */
    @Test
    public void testDrawObject() {
        System.out.println("drawObject");
        Graphics2D g2 = new Graphics2DTest();
        ColorStyle colorStyle = null;
        line2.drawObject(g2, mapView2_1, null);
        
        // TODO write an actual test here
    }

    /**
     * Test of drawOutline method, of class LineString.
     */
    @Test
    public void testDrawOutline() {
        System.out.println("drawOutline");
        Graphics2D g2 = new Graphics2DTest();

        line2.drawOutline(g2, mapView2_1, false);
        
        // TODO write an actual test here
    }

    /**
     * Test of drawPoints method, of class LineString.
     */
    @Test
    public void testDrawPoints() {
        System.out.println("drawPoints");
        Graphics2D g2 = new Graphics2DTest();

        line2.drawPoints(g2, mapView2_1);
        
        // TODO write an actual test here
    }

    /**
     * Test of equals method, of class LineString.
     */
    @Test
    public void testEquals() {
        LineString lineCopy;
        
        System.out.println("equals");
        
        lineCopy = (LineString) line1.copy();
            
        //Test Some basics
        assertEquals(true,  line1.equals(lineCopy));
        assertEquals(false, line1.equals(line2));
        
        //Test Name
        lineCopy = (LineString) line1.copy();
        lineCopy.setName("New Name");
        assertEquals(false, line1.equals(lineCopy));
        
        //Test Class
        lineCopy = (LineString) line1.copy();
        lineCopy.setClass("New Class");
        assertEquals(false, line1.equals(lineCopy));       
        
        //Test Description
        lineCopy = (LineString) line1.copy();
        lineCopy.setDescription("New Desc");
        assertEquals(false, line1.equals(lineCopy));     
        
        //Test AltitudeMode
        lineCopy = (LineString) line1.copy();
        lineCopy.setAltitudeMode(VectorObject.RELATIVE_TO_GROUND);
        assertEquals(false, line1.equals(lineCopy));         
    }

    /**
     * Test of firstCoordinate method, of class LineString.
     */
    @Test
    public void testFirstCoordinate() {
        System.out.println("firstCoordinate");
        LineString instance  = line1;
        Coordinate expResult = line1.getCoordinateList().get(0);
        Coordinate result    = instance.firstCoordinate();
        
        assertEquals(expResult, result);
        assertEquals(false,     instance.firstCoordinate().equals(line1.getCoordinateList().get(1)));
    }

    /**
     * Test of fitToBoundry method, of class LineString.
     */
    @Test
    public void testFitToBoundry() throws Exception {
        System.out.println("fitToBoundry");
        LatLonAltBox boundry = line1.getBoundingBox();

        VectorObject result    = line1.fitToBoundry(boundry);
        assertEquals(line1, result);
    }

    /**
     * Test of getCoordinateWithinRectangle method, of class LineString.
     */
    @Test
    public void testGetCoordinateWithinRectangle() {
        System.out.println("getCoordinateWithinRectangle");
        Rectangle2D range1 = new Rectangle2D.Float(400, 100, 300, 300);
        
        Coordinate expResult = line2.getCoordinateList().get(7);
        Coordinate result    = line2.getCoordinateWithinRectangle(range1);
        assertEquals(expResult, result);

    }

    /**
     * Test of getSegmentLengths method, of class LineString.
     */
    @Test
    public void testGetSegmentLengths() {
        System.out.println("getSegmentLengths");
        LineString instance = new LineString();
        ArrayList expResult = null;
        float[] result = instance.getSegmentLengths();

        //TODO: Write an actual test here.
    }

    /**
     * Test of getWidthModifier method, of class LineString.
     */
    @Test
    public void testGetWidthModifier() {
        System.out.println("getWidthModifier");

        float expResult = 8.046282f;
        float result = line2.getWidthModifier(mapView2_1);
        assertEquals(expResult, result, 0.0001);
    }

    /**
     * Test of isEndPoint method, of class LineString.
     */
    @Test
    public void testIsEndPoint() {
        System.out.println("isEndPoint");
        Coordinate c1 = line1.getCoordinateList().lastCoordinate();
        Coordinate c2 = line1.getCoordinateList().get(0);
        Coordinate c3 = line1.getCoordinateList().get(1);
        
        assertEquals(true,  line1.isEndPoint(c1));
        assertEquals(true,  line1.isEndPoint(c2));
        assertEquals(false, line1.isEndPoint(c3));
    }

    /**
     * Test of isObjectWithinRectangle method, of class LineString.
     */
    @Test
    public void testIsObjectWithinRectangle() {
        System.out.println("isObjectWithinRectangle");
        Rectangle2D range1 = new Rectangle2D.Float(  0,   0,  10,  10);
        Rectangle2D range2 = new Rectangle2D.Float(400, 100, 300, 300);
        
        line2.drawObject(new Graphics2DTest(), mapView2_1, null);
        assertEquals(false, line2.isObjectWithinRectangle(range1));
        assertEquals(true,  line2.isObjectWithinRectangle(range2));
    }

    /**
     * Test of lastCoordinate method, of class LineString.
     */
    @Test
    public void testLastCoordinate() {
        System.out.println("lastCoordinate");
        Coordinate expResult = line1.getCoordinateList().lastCoordinate();
        Coordinate result    = line1.lastCoordinate();
        
        assertEquals(expResult, result);
        assertEquals(false,     line1.firstCoordinate().equals(line1.getCoordinateList().get(1)));
    }

    /**
     * Test of toXML method, of class LineString.
     */
    @Test
    public void testToXML() {
        System.out.println("toXML");
        XmlOutput kmlWriter = new XmlBuffer();

        line1.toXML(kmlWriter);
        
        // TODO write an actual test here
    }
}
