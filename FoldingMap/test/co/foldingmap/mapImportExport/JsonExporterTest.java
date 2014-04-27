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

import co.foldingmap.map.vector.Polygon;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.MultiGeometry;
import co.foldingmap.map.vector.LinearRing;
import co.foldingmap.map.vector.LineString;
import co.foldingmap.map.vector.MapPoint;
import co.scarletshark.geojson.JsonCoordinate;
import co.scarletshark.geojson.JsonObject;
import co.scarletshark.geojson.JsonPair;
import co.scarletshark.geojson.JsonValue;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.themes.IconStyle;
import co.foldingmap.map.themes.Web;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.*;

/**
 *
 * @author Alec
 */
public class JsonExporterTest {
    
    public JsonExporterTest() {
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

    @Ignore
    public static JsonObject getPoint() {
        JsonObject geometryObject, jObject, propertiesObject;
        
        jObject          = new JsonObject();
        propertiesObject = new JsonObject();
        geometryObject   = new JsonObject();
                
        propertiesObject.addPair(new JsonPair("name",         "Coors Field"));
        propertiesObject.addPair(new JsonPair("show_on_map",  true));
        propertiesObject.addPair(new JsonPair("popupContent", "This is where the Rockies play!"));                
        
        //Get Coordinate
        Coordinate       c    = new Coordinate(0, 39.75621f, -104.99404f);
        JsonCoordinate   jc   = new JsonCoordinate(c.getLongitude(), c.getLatitude(), c.getAltitude());
        JsonCoordinate[] cArr = new JsonCoordinate[1];
        
        cArr[0] = jc;
        geometryObject.addPair(new JsonPair("type",        "Point"));
        geometryObject.addPair(new JsonPair("coordinates", jc));
        
        jObject.addPair(new JsonPair("type",       "Feature"));
        jObject.addPair(new JsonPair("properties", propertiesObject));
        jObject.addPair(new JsonPair("geometry",   geometryObject));        
        
        return jObject;
    }    
    
    /**
     * Test of exportLinearRing method, of class JsonExporter.
     */
    @Test
    public void testExportLinearRing() {
        System.out.println("exportLinearRing");
        LinearRing ring = null;
        JsonExporter instance = new JsonExporter();
        JsonObject expResult = null;
        JsonObject result = instance.exportLinearRing(ring);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of exportLineString method, of class JsonExporter.
     */
    @Test
    public void testExportLineString() {
        System.out.println("exportLineString");
        LineString line = null;
        JsonExporter instance = new JsonExporter();
        JsonObject expResult = null;
        JsonObject result = instance.exportLineString(line);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of exportMapPoint method, of class JsonExporter.
     */
    @Test
    public void testExportMapPoint() {
        System.out.println("exportMapPoint");
        
        DigitalMap   mapData   = new DigitalMap();
        mapData.setTheme(new Web(), null, null);
        
        String       desc      = "This is where the Rockies play!";
        Coordinate   c         = new Coordinate(0, 39.75621f, -104.99404f);
        MapPoint     point     = new MapPoint("Coors Field", "Stadium", desc, c);                
        JsonObject   expResult = getPoint();        
        JsonObject   result    = JsonExporter.exportMapPoint(mapData, point);
        
        assertEquals(expResult, result);
    }

    /**
     * Test of exportMultiGeometry method, of class JsonExporter.
     */
    @Test
    public void testExportMultiGeometry() {
        System.out.println("exportMultiGeometry");
        MultiGeometry multi = null;
        JsonExporter instance = new JsonExporter();
        JsonObject expResult = null;
        JsonObject result = instance.exportMultiGeometry(multi);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of exportPolygon method, of class JsonExporter.
     */
    @Test
    public void testExportPolygon() {
        System.out.println("exportPolygon");
        Polygon poly = null;
        JsonExporter instance = new JsonExporter();
        JsonObject expResult = null;
        JsonObject result = instance.exportPolygon(poly);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of exportIconStyle method, of class JsonExporter.
     */
    @Test
    public void testExportIconStyle() {
        System.out.println("exportIconStyle");
        JsonObject expResult;
        
        Double[]   imgDim = new Double[2];
        Double[]   imgLoc = new Double[2];
        Double[]   popLoc = new Double[2];

        expResult = new JsonObject();
        imgDim[0] = new Double(12);
        imgDim[1] = new Double(12);
        imgLoc[0] = new Double(6);
        imgLoc[1] = new Double(6);
        popLoc[0] = new Double(6);
        popLoc[1] = new Double(6);
        
        expResult.addPair(new JsonPair("iconUrl",     "standard_library-small.png"));
        expResult.addPair(new JsonPair("iconSize",    new JsonValue(imgDim, JsonValue.ARRAY)));
        expResult.addPair(new JsonPair("iconAnchor",  new JsonValue(imgLoc, JsonValue.ARRAY)));
        expResult.addPair(new JsonPair("popupAnchor", new JsonValue(popLoc, JsonValue.ARRAY)));        
        
        Web        webTheme  = new Web();
        IconStyle  iconStyle = webTheme.getIconStyle("Library");
        JsonObject result    = JsonExporter.exportIconStyle(iconStyle);
        
        assertEquals(expResult, result);
    }
}
