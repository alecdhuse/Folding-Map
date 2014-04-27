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

import co.foldingmap.map.MapView;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;

/**
 *
 * @author Alec
 */
public class OutlineSegment {    
    protected BasicStroke               stroke;
    protected boolean                   closeSegment;
    protected byte                      strokeType;
    protected Color                     color;
    protected ArrayList<Coordinate>     coordinates;
    protected GeneralPath               left, center, right;
    
    public OutlineSegment() {
        coordinates  = new ArrayList<Coordinate>();
        strokeType   = 1;
        stroke       = new BasicStroke(1f,  BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND);
        closeSegment = false;
    }
    
    public OutlineSegment(int numberOfSegments) {
        coordinates  = new ArrayList<Coordinate>(numberOfSegments);
        strokeType   = 1;
        stroke       = new BasicStroke(1f,  BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND);
        closeSegment = false;
    }    
    
    /**
     * Adds a Coordinate to this segment.
     * 
     * @param c 
     */
    public void addCoordinate(Coordinate c) {
        coordinates.add(c);
    }
    
    /**
     * Append another segment to the end of this one.
     *  
    * @param segment 
     */
    public void appendSegment(OutlineSegment segment) {
        coordinates.addAll(segment.getCoordinateList());
    }
    
    /**
     * Closes this segment so that it is drawn as a polygon.
     */
    public void closeSegment() {
        this.closeSegment = true;
    }
    
    /**
     * Draws this outline segment.
     * 
     * @param g2
     * @param mapView 
     */
    public void draw(Graphics2D g2, MapView mapView) {     
        g2.setStroke(stroke);
        g2.setColor(color);
        generatePaths(mapView);
        g2.draw(center);                
        
        if (mapView.getMapProjection().isLeftShown())
            g2.draw(left);
        
        if (mapView.getMapProjection().isRightShown())
            g2.draw(right);    
    }
    
    /**
     * Generates the GeneralPath shapes user to draw this OutlineSegment.
     * 
     * @param mapView 
     */
    private void generatePaths(MapView mapView) {
        Coordinate  c;
        
        center = new GeneralPath();
        
        //Create the GeneralPath for the Center outline
        for (int i = 0; i < coordinates.size(); i++) {
            c = coordinates.get(i);

            if (i == 0) {                
                center.moveTo(c.getCenterPoint().x, c.getCenterPoint().y);
            } else {
                center.lineTo(c.getCenterPoint().x, c.getCenterPoint().y);
            }
        }        
        
        if (mapView.getMapProjection().isLeftShown()) {  
            left = new GeneralPath();
            
            //Create the GeneralPath for the Center outline
            for (int i = 0; i < coordinates.size(); i++) {
                c = coordinates.get(i);

                if (i == 0) {                
                    left.moveTo(c.getLeftPoint().x, c.getLeftPoint().y);
                } else {
                    left.lineTo(c.getLeftPoint().x, c.getLeftPoint().y);
                }
            }              
        }
        
        if (mapView.getMapProjection().isRightShown()) {  
            right = new GeneralPath();
            
            //Create the GeneralPath for the Center outline
            for (int i = 0; i < coordinates.size(); i++) {
                c = coordinates.get(i);

                if (i == 0) {                
                    right.moveTo(c.getRightPoint().x, c.getRightPoint().y);
                } else {
                    right.lineTo(c.getRightPoint().x, c.getRightPoint().y);
                }
            }              
        }    
        
        if (closeSegment) {
            center.closePath();
            
            if (mapView.getMapProjection().isRightShown())
                right.closePath();
            
            if (mapView.getMapProjection().isLeftShown())
                left.closePath();            
        }
    }
    
    /**
     * Returns the Color for this OutlineSegment.
     * 
     * @return 
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * Returns the CoordinateList for this Segment.
     * 
     * @return 
     */
    public ArrayList<Coordinate> getCoordinateList() {
        return coordinates;
    }
    
    /**
     * Returns the last coordinate for this OutlineSegment.
     * 
     * @return 
     */
    public Coordinate lastCoordinate() {
        return coordinates.get(coordinates.size() - 1);
    }
    
    /**
     * Removes and returns the last Coordinate in the segment.
     * 
     * @return 
     */
    public Coordinate removeLastCoordinate() {
        return coordinates.remove(coordinates.size() - 1);
    }
    
    /**
     * Sets the Color for this Segment.
     * 
     * @param color 
     */
    public void setColor(Color color) {
        this.color = color;
    }
    
    /**
     * Returns the stroke type for this outline.
     * @return 
     */
    public byte getStrokeType() {
        return this.strokeType;
    }
    
    /**
     * Returns the number of coordinates in this outline segment.
     * 
     * @return 
     */
    public int size() {
        return coordinates.size();
    }
}
