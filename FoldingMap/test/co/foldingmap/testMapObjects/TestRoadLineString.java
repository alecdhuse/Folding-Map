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
package co.foldingmap.testMapObjects;

import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.CoordinateList;
import co.foldingmap.map.vector.LineString;
import org.junit.Ignore;

/**
 *
 * @author Alec
 */
@Ignore
public class TestRoadLineString {
    
    public static LineString getLineString() {
        CoordinateList<Coordinate> coordinates = getCoordinates();
        
        return new LineString("Northwest Johnson Street", "Road - City Secondary", coordinates);
    }
    
    /**
     * Creates the CoordinateList for the LineString.
     * 
     * @return 
     */
    public static CoordinateList<Coordinate> getCoordinates() {
        CoordinateList<Coordinate> cList = new CoordinateList<Coordinate>();
        
        cList.add(new Coordinate("-122.68031,45.528698,0,2012-09-06T22:41:26Z"));
        cList.add(new Coordinate("-122.68124,45.528683,0,2012-09-06T22:41:30Z"));
        cList.add(new Coordinate("-122.681305,45.52868,0,2012-09-06T22:41:27Z"));
        cList.add(new Coordinate("-122.68133,45.528675,0,2012-09-06T22:41:27Z"));
        cList.add(new Coordinate("-122.68142,45.52867,0,2012-09-06T22:41:26Z"));
        cList.add(new Coordinate("-122.68225,45.528656,0,2012-09-06T22:41:29Z"));
        cList.add(new Coordinate("-122.682335,45.528656,0,2012-09-06T22:41:25Z"));
        cList.add(new Coordinate("-122.68236,45.528656,0,2012-09-06T22:41:32Z"));
        cList.add(new Coordinate("-122.68242,45.528656,0,2012-09-06T22:41:30Z"));
        cList.add(new Coordinate("-122.68326,45.52864,0,2012-09-06T22:41:30Z"));
        cList.add(new Coordinate("-122.68335,45.52864,0,2012-09-06T22:41:26Z"));
        cList.add(new Coordinate("-122.68344,45.528637,0,2012-09-06T22:41:30Z"));
        cList.add(new Coordinate("-122.68427,45.528625,0,2012-09-06T22:41:30Z"));
        cList.add(new Coordinate("-122.684364,45.52862,0,2012-09-06T22:41:25Z"));
        cList.add(new Coordinate("-122.684456,45.52862,0,2012-09-06T22:41:30Z"));
        cList.add(new Coordinate("-122.685394,45.528606,0,2012-09-06T22:41:26Z"));
        
        return cList;
    }
}
