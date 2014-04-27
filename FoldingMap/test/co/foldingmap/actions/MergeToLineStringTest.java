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
package co.foldingmap.actions;

import co.foldingmap.actions.MergeToLineString;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.CoordinateList;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.testMapObjects.MergeTestObjects;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.*;

/**
 *
 * @author Alec
 */
public class MergeToLineStringTest {
    
    public MergeToLineStringTest() {
    }

    /**
     * Test of canUndo method, of class MergeToLineString.
     */
    @Test
    public void testCanUndo() {
        System.out.println("canUndo");
        MergeToLineString instance = new MergeToLineString(null, null);
        
        assertEquals(true, instance.canUndo());        
    }

    /**
     * Test of execute method, of class MergeToLineString.
     */
    @Test
    public void testExecute() {
        System.out.println("execute");
        
        DigitalMap        mapData  = MergeTestObjects.getMap();
        MergeToLineString instance = new MergeToLineString(mapData, null);
        instance.execute();
        
        VectorLayer  layer  = (VectorLayer) mapData.getLayer(0);
        
        if (layer.getObjectList().size() == 1) {
            VectorObject object = layer.getObjectList().get(0);
            CoordinateList<Coordinate> cList = MergeTestObjects.getLine1Coordinates();
            cList.addAll(MergeTestObjects.getLine2Coordinates().getReverse());
            
            if (object.getCoordinateList().size() == cList.size()) {
                for (int i = 0; i < cList.size(); i++) {                    
                    if (!cList.get(i).equals(object.getCoordinateList().get(i)))
                        fail("MergeToLineStringTest -> New object coordinates are incorrect.");
                }
            } else {
                fail("MergeToLineStringTest -> New object coordinates size is incorrect.");
            }
        } else {
            fail("MergeToLineStringTest -> Layer has the wrong number of objects, it should be 1.");
        }
        
        
        // TODO review the generated test code and remove the default call to fail.
        
    }

    /**
     * Test of undo method, of class MergeToLineString.
     */
    @Test
    public void testUndo() {
        System.out.println("undo");

        DigitalMap        mapData  = MergeTestObjects.getMap();
        MergeToLineString instance = new MergeToLineString(mapData, null);
        
        instance.execute();
        instance.undo();
        
        VectorLayer  layer  = (VectorLayer) mapData.getLayer(0);
        
        if (!layer.getObjectList().get(0).equals(MergeTestObjects.getLine1())) 
            fail("MergeToLineStringTest.undo() -> Line1 is incorect.");
        
        if (!layer.getObjectList().get(1).equals(MergeTestObjects.getLine2())) 
            fail("MergeToLineStringTest.undo() -> Line2 is incorect.");        
    }

}
