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

import co.foldingmap.map.MapProjection;
import co.foldingmap.map.MapView;
import co.foldingmap.map.labeling.LineStringLabel;
import co.foldingmap.map.themes.LineStyle;
import co.foldingmap.map.themes.MapTheme;
import co.foldingmap.map.themes.Web;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.CoordinateList;
import co.foldingmap.map.vector.LineString;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import org.junit.Ignore;

/**
 *
 * @author Alec
 */
@Ignore
public class TestRoadLineString2 {
    
    public static LineString getLineString() {        
        return new LineString("Southwest 11th Avenue", "Road - City Tertiary", getCoordinates());
    }    
    
    /**
     * Creates the CoordinateList for the LineString.
     * 
     * @return 
     */
    public static CoordinateList<Coordinate> getCoordinates() {
        CoordinateList<Coordinate> cList = new CoordinateList<Coordinate>();
        
        cList.add(new Coordinate("-122.68212,45.52296,0,2012-09-06T22:41:25Z"));
        cList.add(new Coordinate("-122.68212,45.522884,0,2012-09-06T22:41:29Z"));
        cList.add(new Coordinate("-122.682106,45.52237,0,2012-09-06T22:41:30Z"));
        cList.add(new Coordinate("-122.6821,45.522312,0,2012-09-06T22:41:25Z"));
        cList.add(new Coordinate("-122.68209,45.522224,0,2012-09-06T22:41:27Z"));
        cList.add(new Coordinate("-122.68238,45.521675,0,2012-09-06T22:41:30Z"));
        cList.add(new Coordinate("-122.68241,45.521618,0,2012-09-06T22:41:25Z"));
        cList.add(new Coordinate("-122.68244,45.521553,0,2012-09-06T22:41:30Z"));
        cList.add(new Coordinate("-122.68274,45.521015,0,2012-09-06T22:41:30Z"));
        cList.add(new Coordinate("-122.68277,45.52095,0,2012-09-06T22:41:25Z"));
        cList.add(new Coordinate("-122.6828,45.52089,0,2012-09-06T22:41:30Z"));
        cList.add(new Coordinate("-122.6831,45.520344,0,2012-09-06T22:41:30Z"));
        cList.add(new Coordinate("-122.68312,45.5203,0,2012-09-06T22:41:25Z"));
        cList.add(new Coordinate("-122.68314,45.520267,0,2012-09-06T22:41:32Z"));
        cList.add(new Coordinate("-122.68317,45.52023,0,2012-09-06T22:41:27Z"));
        cList.add(new Coordinate("-122.68318,45.52019,0,2012-09-06T22:41:30Z"));
        cList.add(new Coordinate("-122.68344,45.51971,0,2012-09-06T22:41:30Z"));
        cList.add(new Coordinate("-122.683464,45.51967,0,2012-09-06T22:41:27Z"));
        cList.add(new Coordinate("-122.68348,45.51963,0,2012-09-06T22:41:26Z"));
        cList.add(new Coordinate("-122.6835,45.519596,0,2012-09-06T22:41:25Z"));
        cList.add(new Coordinate("-122.683525,45.519558,0,2012-09-06T22:41:30Z"));
        cList.add(new Coordinate("-122.683815,45.519012,0,2012-09-06T22:41:30Z"));
        cList.add(new Coordinate("-122.68385,45.51895,0,2012-09-06T22:41:25Z"));
        cList.add(new Coordinate("-122.68388,45.51889,0,2012-09-06T22:41:30Z"));
        cList.add(new Coordinate("-122.68417,45.518345,0,2012-09-06T22:41:30Z"));
        cList.add(new Coordinate("-122.68422,45.518288,0,2012-09-06T22:41:25Z"));     
        
        return cList;
    }    
    
    public static ArrayList<LineStringLabel> getLabels(Graphics2D g2) {
        ArrayList<LineStringLabel> labels = new ArrayList<LineStringLabel>();
        LineStringLabel            label;
        
        label  = new LineStringLabel(g2);
        label.addLabelInstruction(1.550215244293213f, new Point2D.Float(466.73965f, 198.40685f), 466.9454650878906f, 201.08894f, "SW 11th Ave");
        labels.add(label);
        
        label  = new LineStringLabel(g2);
        label.addLabelInstruction(-1.2186891f, new Point2D.Float(411.38693f, 437.67374f),   415.07360f, 440.35583f,  "SW 11th Ave");
        labels.add(label);
        
        label  = new LineStringLabel(g2);
        label.addLabelInstruction(-1.2150155f, new Point2D.Float(315.0322f, 693.83466f),  319.33175f, 696.51672f,  "SW 11th Ave");        
        labels.add(label);
        
        return labels;
    }
    
    public static LineStyle getLineStyle1() {
        MapTheme webTheme = new Web();
        return webTheme.getLineStyle("Road - City Tertiary");
    }
    
    /**
     * Returns a MapView that will show this line.
     * This MapView is used to test if a line label is placed correctly.
     * 
     * @return 
     */
    public static MapView getMapView1() {
        MapProjection mapProjection;
        MapView       mapView;
        
        mapView = new MapView();
        mapView.setDisplayAll(false);
        mapView.setDragging(false);
        mapView.setMapTheme(new Web());
              
        mapProjection = mapView.getMapProjection();
        mapProjection.setDisplaySize(663, 1015);
        mapProjection.setReference(new Coordinate(0.0f, 45.52444f, -122.68733f));
        mapProjection.setZoomLevel(804.6282f);
        
        return mapView;
    }
}
