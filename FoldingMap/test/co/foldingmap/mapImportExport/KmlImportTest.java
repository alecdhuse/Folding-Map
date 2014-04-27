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
package co.foldingmap.mapImportExport;

import co.foldingmap.GUISupport.ProgressBarPanel;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.raster.ImageOverlay;
import co.foldingmap.map.themes.ColorStyle;
import co.foldingmap.map.themes.IconStyle;
import co.foldingmap.map.themes.LabelStyle;
import co.foldingmap.map.themes.LineStyle;
import co.foldingmap.map.themes.PolygonStyle;
import co.foldingmap.map.vector.CoordinateList;
import co.foldingmap.map.vector.InnerBoundary;
import co.foldingmap.map.vector.LatLonAltBox;
import co.foldingmap.map.vector.LevelOfDetail;
import co.foldingmap.map.vector.LineString;
import co.foldingmap.map.vector.LinearRing;
import co.foldingmap.map.vector.MapIcon;
import co.foldingmap.map.vector.MapPoint;
import co.foldingmap.map.vector.MultiGeometry;
import co.foldingmap.map.vector.NetworkLayer;
import co.foldingmap.map.vector.NodeMap;
import co.foldingmap.map.vector.Polygon;
import co.foldingmap.map.vector.Region;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;
import co.foldingmap.testFileFormats.KmlTestData;
import co.foldingmap.testMapObjects.TestRoadLineString;
import co.foldingmap.xml.XMLTag;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 * @author Alec
 */
public class KmlImportTest {
    
    public KmlImportTest() {
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
     * Test of getCoordinateList method, of class KmlImport.
     */
    @Test
    public void testGetCoordinateList() {
        System.out.println("getCoordinateList");
        
        NodeMap        coordinateSet    = new NodeMap();
        String         coordinateString = KmlTestData.getCoordinateTag().getTagContent();
        CoordinateList expResult        = TestRoadLineString.getCoordinates();
        CoordinateList result           = KmlImport.getCoordinateList(coordinateSet, coordinateString);
        
        assertEquals(true, expResult.equals(result));
    }        

    /**
     * Test of getExtendedData method, of class KmlImport.
     */
    @Test
    public void testGetExtendedData() {
        
        System.out.println("getExtendedData");

        HashMap expResult = KmlTestData.getExtendedDataObject();
        HashMap result    = KmlImport.getExtendedData(KmlTestData.getExtendedDataTag());
        
        assertEquals(true, expResult.equals(result));

    }

    /**
     * Test of getStyle method, of class KmlImport.
     */
    @Test
    public void testGetStyle() {
        ColorStyle expResult, result;
        
        System.out.println("getStyle");   
        
        expResult = KmlTestData.getLineStyleObject();        
        result    = KmlImport.getStyle(KmlTestData.getLineStyleTag());                
        assertEquals(true, expResult.equals(result));
        
        XMLTag style = new XMLTag("Style id=\"Park\"", KmlTestData.getPolyStyleTag());
        expResult = KmlTestData.getPolygonStyleObject();        
        result    = KmlImport.getStyle(style);                
        assertEquals(true, expResult.equals(result));        
    }

    /**
     * Test of getIconStyle method, of class KmlImport.
     */
    @Test
    public void testGetIconStyle() {
        System.out.println("getIconStyle");

        IconStyle expResult = KmlTestData.getIconStyleObject();
        IconStyle result    = KmlImport.getIconStyle(KmlTestData.getIconStyleTag());
        
        assertEquals(expResult, result);
    }

    /**
     * Test of getInnerBoundary method, of class KmlImport.
     */
    @Test
    public void testGetInnerBoundary() {
        System.out.println("getInnerBoundary");
        
        NodeMap       nodeMap   = new NodeMap();
        VectorLayer   layer     = new VectorLayer("Test");
        InnerBoundary expResult = KmlTestData.getInnerBoundartObject();
        InnerBoundary result    = KmlImport.getInnerBoundary(nodeMap, layer, KmlTestData.getInnerBoundaryTag());
        
        assertEquals(true, expResult.equals(result));
    }

    /**
     * Test of getLabelStyle method, of class KmlImport.
     */
    @Test
    public void testGetLabelStyle() {
        System.out.println("getLabelStyle");

        LabelStyle expResult = KmlTestData.getLabelStyleObject();
        LabelStyle result    = KmlImport.getLabelStyle(KmlTestData.getLabelStyleTag());
        
        assertEquals(expResult, result);
    }

    /**
     * Test of getLevelOfDetail method, of class KmlImport.
     */
    @Test
    public void testGetLevelOfDetail() {
        System.out.println("getLevelOfDetail");
        LevelOfDetail expResult = KmlTestData.getLodObject();
        LevelOfDetail result    = KmlImport.getLevelOfDetail(KmlTestData.getLodTag());
        
        assertEquals(expResult, result);
    }

    /**
     * Test of getLinearRing method, of class KmlImport.
     */
    @Test
    public void testGetLinearRing() {
        System.out.println("getLinearRing");
        
        NodeMap     nodeMap = new NodeMap();
        VectorLayer layer   = new VectorLayer("Test Layer");
        
        LinearRing expResult = KmlTestData.getLinearRingObject();
        LinearRing result    = KmlImport.getLinearRing(nodeMap, layer, KmlTestData.getLinearRingTag());
        
        assertEquals(expResult, result);
    }

    /**
     * Test of getLineString method, of class KmlImport.
     */
    @Test
    public void testGetLineString() {
        System.out.println("getLineString");
        
        NodeMap     nodeMap = new NodeMap();
        VectorLayer layer   = new VectorLayer("Test Layer");
        
        LineString expResult = TestRoadLineString.getLineString();
        LineString result    = KmlImport.getLineString(nodeMap, layer, KmlTestData.getLineStringTag());
        
        assertEquals(true, expResult.equals(result));
    }

    /**
     * Test of getLineStyle method, of class KmlImport.
     */
    @Test
    public void testGetLineStyle() {
        System.out.println("getLineStyle");

        LineStyle expResult = KmlTestData.getLineStyleObject();
        LineStyle result    = KmlImport.getLineStyle(KmlTestData.getLineStyleTag());
        
        assertEquals(true, expResult.equals(result));
    }

    /**
     * Test of getMapPoint method, of class KmlImport.
     */
    @Test
    public void testGetMapPoint() {
        System.out.println("getMapPoint");
        
        NodeMap     nodeMap      = new NodeMap();
        VectorLayer layer        = new VectorLayer("Test Layer");
        XMLTag      placemarkTag = KmlTestData.getPointTag();
        MapPoint    expResult    = KmlTestData.getPointObject();        
        MapPoint    result       = KmlImport.getMapPoint(nodeMap, layer, placemarkTag);
        
        assertEquals(true, expResult.equals(result));
    }

    /**
     * Test of getMultiGeometry method, of class KmlImport.
     */
    @Test
    public void testGetMultiGeometry() {
        System.out.println("getMultiGeometry");
        
        VectorLayer layer       = new VectorLayer("Layer");
        XMLTag placemarkTag     = KmlTestData.getMultiGeometryTag();
        MultiGeometry expResult = KmlTestData.getMultiGeometryObject();        
        MultiGeometry result    = KmlImport.getMultiGeometry(new NodeMap(), layer, placemarkTag);
        
        assertEquals(expResult, result);
    }

    /**
     * Test of getObjectsFromPlaceMarks method, of class KmlImport.
     */
    @Test
    public void testGetObjectsFromPlaceMarks() {
        System.out.println("getObjectsFromPlaceMarks");
        
        NodeMap           nodeMap       = new NodeMap();
        VectorLayer       layer         = new VectorLayer("Layer");
        ArrayList<XMLTag> placeMarkTags = new ArrayList<XMLTag>();
        VectorObjectList  expResult     = new VectorObjectList<VectorObject>();
        
        placeMarkTags.add(KmlTestData.getPointTag());
        placeMarkTags.add(KmlTestData.getLineStringTag());
        placeMarkTags.add(KmlTestData.getLinearRingTag());
        placeMarkTags.add(KmlTestData.getPolygonTag());
        
        expResult.add(KmlTestData.getPointObject());
        expResult.add(TestRoadLineString.getLineString());
        expResult.add(KmlTestData.getLinearRingObject());
        expResult.add(KmlTestData.getPolygonObject());
        
        VectorObjectList result = KmlImport.getObjectsFromPlaceMarks(nodeMap, layer, placeMarkTags);
        
        assertEquals(expResult, result);
    }

    /**
     * Test of getPolygon method, of class KmlImport.
     */
    @Test
    public void testGetPolygon() {
        System.out.println("getPolygon");
        
        NodeMap     nodeMap      = new NodeMap();
        VectorLayer layer        = new VectorLayer("Test Layer");
        XMLTag      placemarkTag = KmlTestData.getPolygonTag();
        Polygon     expResult    = KmlTestData.getPolygonObject();
        Polygon     result       = KmlImport.getPolygon(nodeMap, layer, placemarkTag);
        
        assertEquals(true, expResult.equals(result));
    }

    /**
     * Test of getPolygonStyle method, of class KmlImport.
     */
    @Test
    public void testGetPolygonStyle() {
        System.out.println("getPolygonStyle");

        PolygonStyle expResult = KmlTestData.getPolygonStyleObject();
        PolygonStyle result    = KmlImport.getPolygonStyle(KmlTestData.getPolyStyleTag());
        
        assertEquals(true, expResult.equals(result));
    }

    /**
     * Test of getTrack method, of class KmlImport.
     */
    @Test
    public void testGetTrack() {
        System.out.println("getTrack");
        
        VectorLayer layer     = new VectorLayer("Layer");
        LineString  expResult = KmlTestData.getTrackObject();
        LineString  result    = KmlImport.getTrack(new NodeMap(), layer, KmlTestData.getTrackTag());
        
        assertEquals(expResult, result);
    }

    /**
     * Test of loadLayer method, of class KmlImport.
     */
    @Test
    public void testLoadLayer() {
        System.out.println("loadLayer");
        DigitalMap openedMap = null;
        XMLTag folderTag = null;
        //KmlImport.loadLayer(openedMap, folderTag);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of loadPlacemark method, of class KmlImport.
     */
    @Test
    public void testLoadPlacemark() {
        System.out.println("loadPlacemark");
        
        NodeMap      nodeMap    = new NodeMap();
        VectorLayer  layer      = new VectorLayer("Layer");
               
        VectorObject expResult0 = KmlTestData.getPointObject();
        VectorObject expResult1 = TestRoadLineString.getLineString();
        VectorObject expResult2 = KmlTestData.getLinearRingObject();
        VectorObject expResult3 = KmlTestData.getPolygonObject();
        
        VectorObject result0 = KmlImport.loadPlacemark(nodeMap, layer, KmlTestData.getPointTag());
        VectorObject result1 = KmlImport.loadPlacemark(nodeMap, layer, KmlTestData.getLineStringTag());
        VectorObject result2 = KmlImport.loadPlacemark(nodeMap, layer, KmlTestData.getLinearRingTag());
        VectorObject result3 = KmlImport.loadPlacemark(nodeMap, layer, KmlTestData.getPolygonTag());
        
        assertEquals(expResult0, result0);
        assertEquals(expResult1, result1);
        assertEquals(expResult2, result2);
        assertEquals(expResult3, result3);
    }

    /**
     * Test of loadRegion method, of class KmlImport.
     */
    @Test
    public void testLoadRegion() {
        System.out.println("loadRegion");
        
        Region expResult = KmlTestData.getRegionObject();
        Region result    = KmlImport.loadRegion(KmlTestData.getRegionTag());
        
        assertEquals(expResult, result);
    }

    /**
     * Test of openKMZ method, of class KmlImport.
     */
    @Test
    public void testOpenKMZ() {
        System.out.println("openKMZ");
        File fileKMZ = null;
        DigitalMap expResult = null;
        DigitalMap result = KmlImport.openKMZ(null, fileKMZ);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeCDataTag method, of class KmlImport.
     */
    @Test
    public void testRemoveCDataTag() {
        System.out.println("removeCDataTag");
        
        String text      = "<![CDATA[<b>Trail Head Name</b>]]>";
        String expResult = "<b>Trail Head Name</b>";
        String result    = KmlImport.removeCDataTag(text);
        
        assertEquals(expResult, result);
    }

    /**
     * Test of getGroundOverlay method, of class KmlImport.
     */
    @Test
    public void testGetGroundOverlay() {
        System.out.println("getGroundOverlay");

        ImageOverlay expResult = KmlTestData.getGroundOverlayObject();
        ImageOverlay result    = KmlImport.getGroundOverlay(KmlTestData.getGroundOverlayTag());
        
        assertEquals(expResult, result);
    }

    /**
     * Test of getIcon method, of class KmlImport.
     */
    @Test
    public void testGetIcon() {
        System.out.println("getIcon");
        
        MapIcon expResult = KmlTestData.getIconObject();
        MapIcon result = KmlImport.getIcon(KmlTestData.getIconTag());
        
        assertEquals(expResult, result);
    }

    /**
     * Test of getLatLonAltBox method, of class KmlImport.
     */
    @Test
    public void testGetLatLonAltBox() {
        System.out.println("getLatLonAltBox");

        LatLonAltBox expResult = KmlTestData.getLatLonAltBoxObject();
        LatLonAltBox result    = KmlImport.getLatLonAltBox(KmlTestData.getLatLonAltBoxTag());
        
        assertEquals(expResult, result);
    }

    /**
     * Test of getNetworkLink method, of class KmlImport.
     */
    @Test
    public void testGetNetworkLink() {
        System.out.println("getNetworkLink");
        
        DigitalMap   mapData     = new DigitalMap();
        XMLTag       netLinkTag  = KmlTestData.getNetworkLinkTag();
        NetworkLayer expResult   = KmlTestData.getNetworkLinkObject();
        NetworkLayer result      = KmlImport.getNetworkLink(mapData, netLinkTag);
        
        assertEquals(expResult, result);
    }

    /**
     * Test of importAsMap method, of class KmlImport.
     */
    @Test
    public void testImportAsMap() throws Exception {
        System.out.println("importAsMap");
        File mapFile = null;
        ProgressBarPanel progressBarPanel = null;
        KmlImport instance = new KmlImport();
        DigitalMap expResult = null;
        DigitalMap result = instance.importAsMap(mapFile, progressBarPanel);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of openKMZ method, of class KmlImport.
     */
    @Test
    public void testOpenKMZ_ProgressBarPanel_File() {
        System.out.println("openKMZ");
        ProgressBarPanel progressBarPanel = null;
        File fileKMZ = null;
        DigitalMap expResult = null;
        DigitalMap result = KmlImport.openKMZ(progressBarPanel, fileKMZ);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of openKMZ method, of class KmlImport.
     */
    @Test
    public void testOpenKMZ_3args() {
        System.out.println("openKMZ");

        File fileKMZ = null;
        NodeMap nodeMap = null;
        DigitalMap expResult = null;
        DigitalMap result = KmlImport.openKMZ(null, fileKMZ, nodeMap);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of importToVectorLayer method, of class KmlImport.
     */
    @Test
    public void testImportToLayer() throws Exception {
        System.out.println("importToVectorLayer");
        File mapFile = null;
        NodeMap nodeMap = new NodeMap();
        VectorLayer layer = new VectorLayer("Test Layer");

        KmlImport instance = new KmlImport();
        instance.importToLayer(mapFile, nodeMap, layer, null);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
