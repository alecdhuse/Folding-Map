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

import co.foldingmap.map.vector.NodeMap;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.LatLonAltBox;
import co.foldingmap.map.vector.LinearRing;
import co.foldingmap.map.vector.Region;
import co.foldingmap.map.vector.InnerBoundary;
import co.foldingmap.map.vector.MapPoint;
import co.foldingmap.map.vector.LevelOfDetail;
import co.foldingmap.map.vector.VectorObjectList;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.Polygon;
import co.foldingmap.map.vector.MultiGeometry;
import co.foldingmap.map.vector.LineString;
import co.foldingmap.map.vector.CoordinateList;
import co.foldingmap.map.themes.LabelStyle;
import co.foldingmap.map.themes.ColorStyle;
import co.foldingmap.map.themes.LineStyle;
import co.foldingmap.map.themes.PolygonStyle;
import co.foldingmap.map.themes.IconStyle;
import co.foldingmap.map.themes.OutlineStyle;
import co.foldingmap.GUISupport.ProgressBarPanel;
import co.foldingmap.GUISupport.ProgressIndicator;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.Layer;
import co.foldingmap.map.Visibility;
import co.foldingmap.map.themes.ColorRamp;
import co.foldingmap.map.tile.TileLayer;
import co.foldingmap.map.vector.NetworkLayer;
import co.foldingmap.map.vector.PhotoPoint;
import co.foldingmap.map.visualization.HeatMap;
import co.foldingmap.testFileFormats.FmXmlTestData;
import co.foldingmap.testMapObjects.FmXmlObjects;
import co.foldingmap.xml.XMLTag;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.*;

/**
 *
 * @author Alec
 */
public class FmXmlImporterTest {
    
    public FmXmlImporterTest() {
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
     * Test of getCoordinateList method, of class FmXmlImporter.
     */
    @Test
    public void testGetCoordinateList() {
        System.out.println("getCoordinateList");
        
        NodeMap coordinateSet    = new NodeMap();
        String  coordinateString = "-122.67275,45.522644,0,2013-03-17T16:44:12Z -122.67277,45.515152,0,2012-09-06T22:41:24Z -122.669945,45.520393,0,2012-09-06T22:41:24Z";
        CoordinateList<Coordinate> expResult = new CoordinateList<Coordinate>();
        
        expResult.add(new Coordinate("-122.67275,45.522644,0,2013-03-17T16:44:12Z"));
        expResult.add(new Coordinate("-122.67277,45.515152,0,2012-09-06T22:41:24Z"));
        expResult.add(new Coordinate("-122.669945,45.520393,0,2012-09-06T22:41:24Z"));
        
        CoordinateList result = FmXmlImporter.getCoordinateList(coordinateSet, coordinateString);
        assertEquals(expResult, result);
    }

    /**
     * Test of getCustomDataFields method, of class FmXmlImporter.
     */
    @Test
    public void testGetCustomDataFields() {
        System.out.println("getCustomDataFields");
        
        String tagText = "<pair key=\"highway\">residential</pair><pair key=\"speed\">80</pair>";
        XMLTag dataTag = new XMLTag("data" , tagText);        
        
        HashMap<String, String> result = FmXmlImporter.getCustomDataFields(dataTag);

        assertEquals(result.get("highway"), "residential");
        assertEquals(result.get("speed"),   "80");
    }

    /**
     * Test of getHeatMapLayer method, of class FmXmlImporter.
     */
    @Test
    public void testGetHeatMapLayer() {
        System.out.println("getHeatMapLayer");
        DigitalMap openedMap = null;
        XMLTag layerTag = null;
        HeatMap expResult = null;
        HeatMap result = FmXmlImporter.getHeatMapLayer(openedMap, layerTag);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getIconStyle method, of class FmXmlImporter.
     */
    @Test
    public void testGetIconStyle() {
        System.out.println("getIconStyle");
        XMLTag styleTag = null;
        IconStyle expResult = null;
        IconStyle result = FmXmlImporter.getIconStyle(styleTag);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getInnerBoundary method, of class FmXmlImporter.
     */
    @Test
    public void testGetInnerBoundary() {
        System.out.println("getInnerBoundary");
        VectorLayer layer = null;
        XMLTag ibTag = null;
        InnerBoundary expResult = null;
        //InnerBoundary result = FmXmlImporter.getInnerBoundary(layer, ibTag);
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLabelStyle method, of class FmXmlImporter.
     */
    @Test
    public void testGetLabelStyle() {
        System.out.println("getLabelStyle");
        XMLTag labelTag = null;
        LabelStyle expResult = null;
        LabelStyle result = FmXmlImporter.getLabelStyle(labelTag);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLatLonAltBox method, of class FmXmlImporter.
     */
    @Test
    public void testGetLatLonAltBox() {
        System.out.println("getLatLonAltBox");
        XMLTag tag = null;
        LatLonAltBox expResult = null;
        LatLonAltBox result = FmXmlImporter.getLatLonAltBox(tag);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLevelOfDetail method, of class FmXmlImporter.
     */
    @Test
    public void testGetLevelOfDetail() {
        System.out.println("getLevelOfDetail");
        XMLTag lodTag = null;
        LevelOfDetail expResult = null;
        LevelOfDetail result = FmXmlImporter.getLevelOfDetail(lodTag);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLinearRing method, of class FmXmlImporter.
     */
    @Test
    public void testGetLinearRing() {
        System.out.println("getLinearRing");
        VectorLayer layer = null;
        XMLTag placemarkTag = null;
        NodeMap coordinateSet = null;
        LinearRing expResult = null;
        LinearRing result = FmXmlImporter.getLinearRing(layer, placemarkTag, coordinateSet);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLineString method, of class FmXmlImporter.
     */
    @Test
    public void testGetLineString() {
        System.out.println("getLineString");
        VectorLayer layer = null;
        XMLTag placemarkTag = null;
        NodeMap coordinateSet = null;
        LineString expResult = null;
        LineString result = FmXmlImporter.getLineString(layer, placemarkTag, coordinateSet);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLineStyle method, of class FmXmlImporter.
     */
    @Test
    public void testGetLineStyle() {
        System.out.println("getLineStyle");
        
        XMLTag    styleTag  = FmXmlTestData.getLineStyleTag();
        LineStyle expResult = FmXmlTestData.getLineStyleObject();
        LineStyle result    = FmXmlImporter.getLineStyle(styleTag);
        
        assertEquals(expResult, result);
    }

    /**
     * Test of getMapPoint method, of class FmXmlImporter.
     */
    @Test
    public void testGetMapPoint() {
        System.out.println("getMapPoint");
        VectorLayer layer = null;
        XMLTag placemarkTag = null;
        NodeMap coordinateSet = null;
        MapPoint expResult = null;
        MapPoint result = FmXmlImporter.getMapPoint(layer, placemarkTag, coordinateSet);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMultiGeometry method, of class FmXmlImporter.
     */
    @Test
    public void testGetMultiGeometry() {
        System.out.println("getMultiGeometry");
        VectorLayer layer = null;
        XMLTag placemarkTag = null;
        NodeMap coordinateSet = null;
        MultiGeometry expResult = null;
        MultiGeometry result = FmXmlImporter.getMultiGeometry(layer, placemarkTag, coordinateSet);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getObjectsFromPlaceMarks method, of class FmXmlImporter.
     */
    @Test
    public void testGetObjectsFromPlaceMarks() {
        System.out.println("getObjectsFromPlaceMarks");
        VectorLayer layer = null;
        ArrayList<XMLTag> placeMarkTags = null;
        NodeMap coordinateSet = null;
        VectorObjectList expResult = null;
        VectorObjectList result = FmXmlImporter.getObjectsFromPlaceMarks(layer, placeMarkTags, coordinateSet);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getOutlineStyle method, of class FmXmlImporter.
     */
    @Test
    public void testGetOutlineStyle() {
        System.out.println("getOutlineStyle");
        XMLTag outlineStyleTag = null;
        OutlineStyle expResult = null;
        OutlineStyle result = FmXmlImporter.getOutlineStyle(outlineStyleTag);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPolygon method, of class FmXmlImporter.
     */
    @Test
    public void testGetPolygon() {
        System.out.println("getPolygon");
        VectorLayer layer = null;
        XMLTag placemarkTag = null;
        NodeMap coordinateSet = null;
        Polygon expResult = null;
        Polygon result = FmXmlImporter.getPolygon(layer, placemarkTag, coordinateSet);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPolygonStyle method, of class FmXmlImporter.
     */
    @Test
    public void testGetPolygonStyle() {
        System.out.println("getPolygonStyle");
        XMLTag styleTag = null;
        PolygonStyle expResult = null;
        PolygonStyle result = FmXmlImporter.getPolygonStyle(styleTag);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRegion method, of class FmXmlImporter.
     */
    @Test
    public void testGetRegion() {
        System.out.println("getRegion");
        XMLTag regionTag = null;
        Region expResult = null;
        Region result = FmXmlImporter.getRegion(regionTag);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getStyle method, of class FmXmlImporter.
     */
    @Test
    public void testGetStyle() {
        System.out.println("getStyle");
        XMLTag styleTag = null;
        ColorStyle expResult = null;
        ColorStyle result = FmXmlImporter.getStyle(styleTag);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getVectorLayer method, of class FmXmlImporter.
     */
    @Test
    public void testGetVectorLayer() {
        System.out.println("getVectorLayer");
        DigitalMap openedMap = null;
        XMLTag layerTag = null;
        NodeMap coordinateSet = null;
        VectorLayer expResult = null;
        VectorLayer result = FmXmlImporter.getVectorLayer(openedMap, layerTag, coordinateSet);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of loadObject method, of class FmXmlImporter.
     */
    @Test
    public void testLoadObject() {
        System.out.println("loadObject");
        VectorLayer layer = null;
        XMLTag objectTag = null;
        NodeMap coordinateSet = null;
        VectorObject expResult = null;
        VectorObject result = FmXmlImporter.loadObject(layer, objectTag, coordinateSet);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of openFile method, of class FmXmlImporter.
     */
    @Test
    public void testImportAsMap() {
        System.out.println("openFile");
        ProgressBarPanel progressBarPanel = null;
        File mapFile = null;
        
        DigitalMap expResult = null;
        //DigitalMap result = FmXmlImporter.importAsMap(mapFile, progressBarPanel);
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of parseNodes method, of class FmXmlImporter.
     */
    @Test
    public void testParseNodes() {
        System.out.println("parseNodes");
        
        NodeMap coordinateSet = new NodeMap(5);
        ArrayList<XMLTag> nodeTags = FmXmlObjects.getNodesTag();
        
        coordinateSet.put(39649625, new Coordinate("-123.094894,44.06016,0,2014-04-30T04:16:59Z"));
        coordinateSet.put(39649628, new Coordinate("-123.095375,44.06009,0,2014-04-30T04:16:59Z"));
        coordinateSet.put(39649631, new Coordinate("-123.09568,44.060005,0,2014-04-30T04:16:59Z"));
        
        FmXmlImporter.parseNodes(coordinateSet, nodeTags);
    }

    /**
     * Test of removeCDataTag method, of class FmXmlImporter.
     */
    @Test
    public void testRemoveCDataTag() {
        System.out.println("removeCDataTag");
        String text = "";
        String expResult = "";
        String result = FmXmlImporter.removeCDataTag(text);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of convertRegionToVisibility method, of class FmXmlImporter.
     */
    @Test
    public void testConvertRegionToVisibility() {
        System.out.println("convertRegionToVisibility");
        Region r = null;
        Visibility expResult = null;
        Visibility result = FmXmlImporter.convertRegionToVisibility(r);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getColorRamp method, of class FmXmlImporter.
     */
    @Test
    public void testGetColorRamp() {
        System.out.println("getColorRamp");
        String id = "";
        XMLTag colorRampTag = null;
        ColorRamp expResult = null;
        ColorRamp result = FmXmlImporter.getColorRamp(id, colorRampTag);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNetworkLayer method, of class FmXmlImporter.
     */
    @Test
    public void testGetNetworkLayer() {
        System.out.println("getNetworkLayer");
        XMLTag layerTag = null;
        NetworkLayer expResult = null;
        NetworkLayer result = FmXmlImporter.getNetworkLayer(layerTag);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPhotoPoint method, of class FmXmlImporter.
     */
    @Test
    public void testGetPhotoPoint() {
        System.out.println("getPhotoPoint");
        VectorLayer layer = null;
        XMLTag placemarkTag = null;
        NodeMap coordinateSet = null;
        PhotoPoint expResult = null;
        PhotoPoint result = FmXmlImporter.getPhotoPoint(layer, placemarkTag, coordinateSet);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTileLayer method, of class FmXmlImporter.
     */
    @Test
    public void testGetTileLayer() {
        System.out.println("getTileLayer");
        XMLTag layerTag = null;
        TileLayer expResult = null;
        TileLayer result = FmXmlImporter.getTileLayer(layerTag);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getVisibility method, of class FmXmlImporter.
     */
    @Test
    public void testGetVisibility() {
        System.out.println("getVisibility");
                        
        XMLTag     visTag    = FmXmlObjects.getTestVisibilityTag();
        Visibility expResult = FmXmlObjects.getVisibilityObject();
        Visibility result    = FmXmlImporter.getVisibility(visTag);
        
        assertEquals(expResult, result);
    }

    /**
     * Test of openFile method, of class FmXmlImporter.
     */
    @Test
    public void testOpenFile() {
        System.out.println("openFile");
        File mapFile = null;
        ProgressIndicator progressIndicator = null;
        DigitalMap expResult = null;
        DigitalMap result = FmXmlImporter.openFile(mapFile, progressIndicator);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of importToLayer method, of class FmXmlImporter.
     */
    @Test
    public void testImportToLayer() throws Exception {
        System.out.println("importToLayer");
        File mapFile = null;
        NodeMap nodeMap = null;
        Layer layer = null;
        ProgressIndicator progressIndicator = null;
        FmXmlImporter instance = new FmXmlImporter();
        instance.importToLayer(mapFile, nodeMap, layer, progressIndicator);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
