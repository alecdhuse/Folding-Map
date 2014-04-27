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
package co.foldingmap.testUtills;

import co.foldingmap.GUISupport.ProgressBarPanel;
import co.foldingmap.actions.OpenMap;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.Layer;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.CoordinateList;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.VectorObject;
import org.junit.Ignore;

/**
 *
 * @author Alec
 */
@Ignore
public class TestObjectCreator {
    
    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        
        String     fileName = "/Users/Alec/Documents/Scarlet Shark/Folding Map/test cases/LineString Merge Fail.kml";
        DigitalMap mapData  = OpenMap.openMap(fileName, null, null, new ProgressBarPanel());
        
        for (Layer l: mapData.getLayers()) {
            if (l instanceof VectorLayer) {
                VectorLayer vl = (VectorLayer) l;
                
                for (VectorObject obj: vl.getObjectList()) 
                    sb.append(getTestObjectCode(obj));                
            }
        }
        
        System.out.println(sb.toString());
    }
    
    public static String getCoodinateListCode(String name, CoordinateList<Coordinate> cList) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("\tpublic static CoordinateList<Coordinate> get");
        sb.append("name");
        sb.append("Coordinates() {\n");
        sb.append("\t\tCoordinateList<Coordinate> cList = new CoordinateList<Coordinate>();");
        sb.append("\n\n");
        
        for (Coordinate c: cList) {                         
            sb.append("\t\tcList.add(new Coordinate(\"");
            sb.append(c.toString());
            sb.append("\"));\n");
        }
        
        sb.append("\n");
        sb.append("\t\treturn cList;\n");
        sb.append("\t}\n");
        
        return sb.toString();
    }
    
    public static String getTestObjectCode(VectorObject obj) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("\tpublic static VectorObject get");
        sb.append(obj.getName());
        sb.append("() {\n");
        sb.append("\t\tVectorObject obj;\n");
        sb.append("\t\tCoordinateList<Coordinate> cList = get");
        sb.append(obj.getName());
        sb.append("Coordinates();\n\n");
        
        sb.append("\t}\n");
        
        sb.append(getCoodinateListCode(obj.getName(), obj.getCoordinateList()));
        
        return sb.toString();
    }
}
