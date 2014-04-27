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

import co.foldingmap.Logger;
import co.foldingmap.map.MapView;
import co.foldingmap.map.ObjectNotWithinBoundsException;
import co.foldingmap.map.labeling.LabelAbbreviations;
import co.foldingmap.map.labeling.LineStringLabel;
import co.foldingmap.map.themes.ColorStyle;
import co.foldingmap.map.themes.LabelStyle;
import co.foldingmap.map.themes.LineStyle;
import co.foldingmap.map.themes.MapTheme;
import co.foldingmap.xml.XmlOutput;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Handles LineString objects, drawing and operation for modifying.
 * 
 * @author Alec Dhuse
 */
public class LineString extends VectorObject {    
    protected boolean                    lineLeftInit, lineCenterInit, lineRightInit;    
    protected GeneralPath                lineLeft, lineCenter, lineRight;
    
    /**
     * Constructor for super class.
     * 
     */
    public LineString() {
        super();                
    }
    
    /**
     * Constructor for objects of class LineString
     * 
     * @param name
     * @param lineClass
     * @param lineCoordinates
     */
    public LineString(String name, String lineClass, String lineCoordinates) {
        super();
        Coordinate      newCoordinate;
        String          currentCoordinateString;
        StringTokenizer st = new StringTokenizer(lineCoordinates);

        commonConstructor(name, lineClass);

        //get coordinates
        while (st.hasMoreTokens()) {
            currentCoordinateString = st.nextToken();
            newCoordinate           = new Coordinate(currentCoordinateString);
            coordinates.add(newCoordinate);
        }
    }
    
    /**
     * Constructor for objects of class LineString
     * 
     * @param name
     * @param type
     * @param lineCoordinates 
     */
    public LineString(String name, String type, CoordinateList lineCoordinates) {
        super();

        commonConstructor(name, type);
        this.coordinates.addAll(lineCoordinates);        
    }     
    
    /**
     * Constructor elements from multiple constructors in one place.
     * 
     * @param name
     * @param lineClass 
     */
    protected final void commonConstructor(String name, String lineClass) {
        try {
            this.altitudeMode       = CLAMP_TO_GROUND;
            this.coordinates        = new CoordinateList<Coordinate>();
            this.objectName         = name;
            this.objectClass        = lineClass;
            this.objectDescription  = "";            
        } catch (Exception e) {
            System.err.println("Error in LineString.commonConstructor(String, String) - " + e);
        }          
    }      
    
    /**
     * Creates a copy of this LineString.
     * 
     * @return 
     */
    @Override
    public VectorObject copy() {
        LineString newCopy;

        newCopy = new LineString(this.objectName, this.getObjectClass(), this.getCoordinateList());

        newCopy.setDescription(this.getDescription());
        newCopy.setParentLayer(this.getParentLayer());
        newCopy.setCustomDataFields(this.customDataFields);
        newCopy.setAltitudeMode(this.altitudeMode);
                
        return newCopy;
    }

    /**
     * Converts the objects latitude and longitude coordinates screen x,y points.
     * 
     * @param mapView 
     */
    protected void convertCoordinatesToLines(MapView mapView) {
        Coordinate     currentCoordinate;
        int            loopStart;
        Point2D.Float  tempPoint;
        Point2D[]      points       = null;
        
        try {
            
            this.lineCenter     = new GeneralPath();            
            
            lineLeftInit        = false;
            lineCenterInit      = false;
            lineRightInit       = false;
            loopStart           = 0;            

            if (coordinates.size() > 0) {
                //prime the loop
                currentCoordinate = (Coordinate) coordinates.get(loopStart);
                tempPoint         = currentCoordinate.getCenterPoint();
                loopStart++;
                
                lineCenter.moveTo(tempPoint.getX(), tempPoint.getY());
                lineCenterInit = true; 
               
                //is left wrapping needed
                if (mapView.getMapProjection().isLeftShown()) { 
                    tempPoint    = currentCoordinate.getLeftPoint(); 
                    lineLeft     = new GeneralPath();
                    lineLeftInit = true;
                    
                    lineLeft.moveTo(tempPoint.getX(), tempPoint.getY());                  
                }     
                
                //right wrapping is needed
                if (mapView.getMapProjection().isRightShown()) {
                    tempPoint     = currentCoordinate.getRightPoint();
                    lineRight     = new GeneralPath();
                    lineRightInit = true;   
                    
                    lineRight.moveTo(tempPoint.getX(), tempPoint.getY());                                
                }                   
            }

            //Check to see if we are in the modify mode
            if (!mapView.arePointsShown()) {
                //Test for Ramer–Douglas–Peucker algorithm
                points = new Point2D[coordinates.size()];

                for (int i = 0; i < coordinates.size(); i++) 
                    points[i] = coordinates.get(i).centerPoint;                        

//                points = CoordinateMath.douglasPeuckerLine(points, 2);
            }
            
            //create the line
            for (int i = loopStart; i < coordinates.size(); i++) {
                currentCoordinate = coordinates.get(i);     

                if (lineCenterInit) {
                    if (!mapView.arePointsShown()) {
                        //Normal View Mode
                        if (i == (coordinates.size() - 1)) {
                            tempPoint = currentCoordinate.getCenterPoint();
                            lineCenter.lineTo(tempPoint.getX(), tempPoint.getY());                        
                        } else {
                            if (i < points.length)
                                lineCenter.lineTo(points[i].getX(), points[i].getY());
                        }
                    } else {         
                        //Modify Mode, show all points
                        tempPoint = currentCoordinate.getCenterPoint();
                        lineCenter.lineTo(tempPoint.getX(), tempPoint.getY());
                    }
                }
                
                if (lineLeftInit) {
                    tempPoint = currentCoordinate.getLeftPoint();
                    lineLeft.lineTo(tempPoint.getX(), tempPoint.getY());
                }
                
                if (lineRightInit) {
                    tempPoint = currentCoordinate.getRightPoint();
                    lineRight.lineTo(tempPoint.getX(), tempPoint.getY());   
                }                

            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in LineString.convertCoordinatesToLines(MapView) - " + e);
        }
    }
    
    /**
     * Creates a label for this LineStirng.
     * 
     * @param g2
     * @param mapView
     * @return 
     */
    public ArrayList<LineStringLabel> createLabel(Graphics2D g2, 
                                                  MapView    mapView,
                                                  LabelStyle labelStyle) {
        
        ArrayList<Float>            lengths;
        ArrayList<LineStringLabel>  labels;
        boolean                     createLabel;
        double                      slope;
        int                         offset;
        float                       angle;
        float                       centeredX, yOffset; 
        float                       labelLength, labelPartLength, segmentLengths;
        float                       modifiedWidth, widthModifier; 
        LabelAbbreviations          labelAbbreviations;  
        LineStringLabel             lineLabel;
        LineStyle                   lineStringStyle;
        Point2D.Float               p1, p2;        
        String                      nameAbbr;
        StringBuffer                namePart;
        
        labels = new ArrayList<LineStringLabel>();
        
        try {           
            labelAbbreviations  = new LabelAbbreviations();
            lengths             = getSegmentLengths();            
            lineLabel           = new LineStringLabel(g2);
            nameAbbr            = labelAbbreviations.replaceWithAbbreviations(objectName).trim();          
            segmentLengths      = 0;
            labelLength         = lineLabel.getLabelLength(g2, nameAbbr);
            lineStringStyle     = (LineStyle) mapView.getMapTheme().getLineStyle(this.getObjectClass());

            if (lineStringStyle == null)
                lineStringStyle = (LineStyle) mapView.getMapTheme().getLineStyle("(Unspecified Linestring)");        

            widthModifier       = getWidthModifier(mapView);
            modifiedWidth       = (lineStringStyle.getLineWidth() * widthModifier);
            yOffset             = (modifiedWidth <= 12 ? (modifiedWidth / 3.0f) : 4);

            for (int i = 1; i < lengths.size(); i++) {
                Float f         = lengths.get(i);
                segmentLengths += f.floatValue();

                if (labels.size() > 0) {
                    /* Only draw the label if there has been enough space 
                    * since the last place the label was drawn */
                    if ((segmentLengths > labelLength * 2) && segmentLengths > 15) {
                        createLabel = true;
                    } else {
                        createLabel = false;
                    }
                } else {
                    //No previous labels drawn, draw one now.
                    createLabel = true;
                }

                if (createLabel) {
                    lineLabel = new LineStringLabel(g2);
                    
                    if ((f.floatValue() >= labelLength) && !coordinates.get(i).isShared()) {                        
                        p1        = this.coordinates.get(i).centerPoint;
                        p2        = this.coordinates.get(i+1).centerPoint;      
                        angle     = (float) Math.atan2((p2.getY() - p1.getY()) , (p2.getX() - p1.getX()));  //convert slope to an angle  
                        
                        //Check to see if label will be drawn upside-down
                        if ((angle > 1.5707f && angle < 4.712f) || 
                            (angle < -1.5707f && angle > -4.712f)) {
                            
                            p1    = this.coordinates.get(i+1).centerPoint;
                            p2    = this.coordinates.get(i).centerPoint;                                
                            angle = (float) Math.atan2((p2.getY() - p1.getY()) , (p2.getX() - p1.getX())); 
                        }
                        
                        centeredX = (float) ((p1.getX() + (f.floatValue() / 2.0f)) - (labelLength / 2.0f));                        
                        lineLabel.addLabelInstruction(angle, p1, centeredX, ((float) p1.getY()) + yOffset, nameAbbr);
                        lineLabel.setComplete(true);                                                                           
                        segmentLengths = 0;

                        //Set Label Style
                        if (labelStyle != null) {
                            lineLabel.setOutlineColor(labelStyle.getOutlineColor());
                            lineLabel.setFillColor(labelStyle.getFillColor());
                            lineLabel.setFont(labelStyle.getFont());                            
                        }
                        
                        //TODO: Figure out why this is needed and make it so it is not, to make drawing faster
                        if (!labels.contains(lineLabel))
                            labels.add(lineLabel);
                    } else {
                        //Break up the label

                    }
                }
            }                                                  
            
        } catch (Exception e) {
            System.err.println("Error in LineString.createLabel(Graphics2D, MapView) - " + e);
        }    
        
        return labels;
    }
    
    
    /**
     * Method that will draw this object.
     * 
     * @param g2
     * @param mapView 
     * @param colorStyle
     */
    @Override
    public void drawObject(Graphics2D g2, MapView mapView, ColorStyle colorStyle) {
        boolean     drawObject;
        float       width, widthModifier;
        LineStyle   lineStringStyle;
        String      lineStroke;
        
        drawObject    = true;
        
        try {
            drawObject = this.isVisible(mapView);

            if (drawObject) {                
                //style
                if (colorStyle == null) {
                    lineStringStyle = (LineStyle) mapView.getMapTheme().getLineStyle(getObjectClass());
                } else {
                    lineStringStyle = (LineStyle) colorStyle;
                }
                
                if (lineStringStyle == null) 
                    lineStringStyle = (LineStyle) mapView.getMapTheme().getLineStyle("(Unspecified Linestring)");                                
                
                convertCoordinatesToLines(mapView);                
                                     
                //Check to see if width is Modified based on zoom.
                if (lineStringStyle.scaleWidth()) {
                    widthModifier = getWidthModifier(mapView);                                    
                    width         = lineStringStyle.getLineWidth() * widthModifier;
                } else {
                    width         = lineStringStyle.getLineWidth();
                }
                
                if (width > 28) width = 28;                             
                if (width < lineStringStyle.getLineWidth()) lineStringStyle.getLineWidth();
                
                if (highlighted) {
                    g2.setColor(lineStringStyle.getSelectedFillColor());
                } else {
                    g2.setColor(lineStringStyle.getFillColor());
                }
                                             
                lineStroke = lineStringStyle.getLineStroke();
                
                if (lineStroke.equals(LineStyle.SOLID_DASHED) || lineStroke.equals(LineStyle.IN_DASH)) {                    
                    //draw the solid part first
                    lineStyle = new BasicStroke(width,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                    g2.setStroke(lineStyle);
                    
                    if (lineLeftInit)
                        g2.draw(lineLeft); 
                        
                    if (lineCenterInit)
                        g2.draw(lineCenter);                    
                                               
                    if (lineRightInit)
                        g2.draw(lineRight); 
                    
                    //draw the dashed part next          
                    if (lineStroke.equals(LineStyle.IN_DASH)) {
                        lineStyle = new BasicStroke(lineStringStyle.getLineWidth(),  BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 8.0f, LineStyle.IN_DASHED_STYLE, (10.0f));
                    } else {
                        lineStyle = new BasicStroke(width,  BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, LineStyle.SOLID_DASHED_STYLE, (10.0f)); 
                    }
                    
                    g2.setStroke(lineStyle);
                    g2.setColor(lineStringStyle.getOutlineColor());

                    if (lineLeftInit)
                        g2.draw(lineLeft); 
                        
                    if (lineCenterInit)
                        g2.draw(lineCenter);                    
                                               
                    if (lineRightInit)
                        g2.draw(lineRight);                     
                } else {                    
                    if (lineStringStyle.getLineStroke() != null) {
                        if (lineStroke.equals(LineStyle.SOLID)) {
                           lineStyle = new BasicStroke(width,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                        } else if (lineStroke.equals(LineStyle.DASHED)) {
                           lineStyle = new BasicStroke(width,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, LineStyle.DASHED_STYLE, 0.0f);                     
                        } else if (lineStroke.equals(LineStyle.DOTTED)) {
                           lineStyle = MapTheme.getStroke(LineStyle.DOTTED, lineStringStyle.getLineWidth());                
                        } else if (lineStroke.equals(LineStyle.DASH_DOT)) {
                           lineStyle = new BasicStroke(width,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, LineStyle.DASHED_STYLE, 0.0f);
                        }
                    } else {
                        lineStyle = new BasicStroke(width,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                    }
                    
                    g2.setStroke(lineStyle);     
                    
                    if (lineLeftInit)
                        g2.draw(lineLeft); 
                        
                    if (lineCenterInit)
                        g2.draw(lineCenter);                    
                                               
                    if (lineRightInit)
                        g2.draw(lineRight); 
                }

                //create label
                if (lineStringStyle.getLabel() != null)
                    mapView.getLabelManager().addLabels(g2, createLabel(g2, mapView, lineStringStyle.getLabel()));                                       
                
            } //end if draw object
        } catch (Exception e) {
            System.err.println("Error in LineString.drawObject(Graphics2D, MapView) " + e + " - " + this.objectName);
        }
    }// end drawObject

    /**
     * Draws the outline for this LineString.
     * 
     * @param g2
     * @param mapView 
     */
    @Override
    public void drawOutline(Graphics2D g2, MapView mapView) {
        BasicStroke lineOutlineStroke;
        boolean     drawObject, outline;
        float       width, widthModifier;
        LineStyle   lineStringStyle;
        
        try {
            lineStringStyle = (LineStyle) mapView.getMapTheme().getLineStyle(this.getObjectClass());
            
            if (lineStringStyle != null) {
                outline    = lineStringStyle.isOutlined();
                drawObject = true;

                if (outline) {
                    if (lineStringStyle.scaleWidth()) {
                        widthModifier   = getWidthModifier(mapView);        
                        width           = (lineStringStyle.getLineWidth() * widthModifier) + 1.2f;            
                    } else {
                        width           = lineStringStyle.getLineWidth();
                    }
                    
                    if (width > 28) width = 28;
                    if (width < lineStringStyle.getLineWidth()) lineStringStyle.getLineWidth();
                    
                    drawObject = this.isVisible(mapView);      

                   if (lineStringStyle.getLineStroke() != null) {
                        if (lineStringStyle.getLineStroke().equals(LineStyle.SOLID)) {
                           lineOutlineStroke = new BasicStroke(width,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                        } else if (lineStringStyle.getLineStroke().equals(LineStyle.DASHED)) {
                           lineOutlineStroke = new BasicStroke(width,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, LineStyle.DASHED_STYLE, 0.0f);
                        } else if (lineStringStyle.getLineStroke().equalsIgnoreCase(LineStyle.DOTTED)) {
                            lineOutlineStroke = MapTheme.getStroke(LineStyle.DOTTED, lineStringStyle.getLineWidth());
                        } else if (lineStringStyle.getLineStroke().equals(LineStyle.DASH_DOT)) {
                            lineOutlineStroke = new BasicStroke(width,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, LineStyle.DASHED_STYLE, 0.0f);
                        } else {
                            lineOutlineStroke = new BasicStroke((width),  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                        }
                    } else {
                        lineOutlineStroke = new BasicStroke((width),  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                    } 

                    g2.setStroke(lineOutlineStroke);
                    g2.setColor(lineStringStyle.getOutlineColor());

                    if (drawObject) {
                        convertCoordinatesToLines(mapView);
                        
                        if (lineLeftInit)
                            g2.draw(lineLeft); 

                        if (lineCenterInit)
                            g2.draw(lineCenter);                    

                        if (lineRightInit)
                            g2.draw(lineRight);           
                    }
                } // end style != null check
            }
        } catch (Exception e) {
            System.err.println("Error in LineString.drawOutline(Graphics2D, MapView) - " + e);
        }
    }
    
    /**
     * Draws the points that make up this object.
     * 
     * @param g2
     * @param mapView 
     */
    @Override
    public void drawPoints(Graphics2D g2, MapView mapView) {
        Shape       leftShape, centerShape, rightShape;
        
        leftShape  = null;
        rightShape = null;         
        
        if (selectedCoordinate == null) 
            selectedCoordinate = getCoordinateList().getCoordinateClosestTo(mapView.getLastMouseClickCoordinate());

        //prevent null pointer exceptions
        if (selectedCoordinate  == null)
            selectedCoordinate = Coordinate.UNKNOWN_COORDINATE;

        //change the draw style
        g2.setStroke(new BasicStroke(2f,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));                    

        for (int i = 0; i < coordinates.size(); i++) {
            centerShape = getPointShape(coordinates.get(i), MapView.NO_WRAP);

            g2.setColor(mapView.getMapTheme().getPointColor());            
            g2.draw(centerShape); 
            
            if (mapView.getMapProjection().isLeftShown()) {
                leftShape = getPointShape(coordinates.get(i), MapView.WRAP_LEFT);
                g2.draw(leftShape);    
            }
            
            if (mapView.getMapProjection().isRightShown()) {
                rightShape = getPointShape(coordinates.get(i), MapView.WRAP_RIGHT);
                g2.draw(rightShape);                  
            }            
            
            if (selectedCoordinate.equals(coordinates.get(i)))
                g2.setColor(Color.WHITE);                        

            g2.fill(centerShape);     
            
            if (mapView.getMapProjection().isRightShown())
                 g2.fill(rightShape);  
            
            if (mapView.getMapProjection().isLeftShown())                    
                g2.fill(leftShape); 
        }        
    }    
    
//    /**
//     * Returns whether or not this LineString is the same as another by comparing their coordinates.
//     * Other information is not compared.
//     *
//     * @param  lineStringToCompare   The LineString being compared to this LineString.
//     * @return If the two LineStrings are equal.
//     */
//    @Override
//    public boolean equals(Object o) {
//        LineString lineStringToCompare;
//        
//        if (o instanceof LineString) {  
//            lineStringToCompare = (LineString) o;
//            
//            return (this.hashCode() == lineStringToCompare.hashCode());
//        } else {
//            return false;
//        }
//    }    
    
    /**
     * Returns the first Coordinate in this line's CoordinateList.
     * 
     * @return 
     */
    public Coordinate firstCoordinate() {
        return coordinates.get(0);
    }    
    
    /**
     * Returns this object sided to a given boundary.
     * 
     * @param boundry
     * @return
     * @throws ObjectNotWithinBoundsException 
     */
    @Override
    public VectorObject fitToBoundry(LatLonAltBox boundry) throws ObjectNotWithinBoundsException {
        Coordinate  northEastCoordinate, northWestCoordinate, southEastCoordinate, southWestCoordinate;
        Coordinate  cutOffCoordinate;
        LineString  fittedObject;

        fittedObject= this;
        fittedObject.getCoordinateList().clear();

        cutOffCoordinate    = Coordinate.UNKNOWN_COORDINATE;
        northEastCoordinate = boundry.getNorthEastCoordinate();
        northWestCoordinate = boundry.getNorthWestCoordinate();
        southEastCoordinate = boundry.getSouthEastCoordinate();
        southWestCoordinate = boundry.getSouthWestCoordinate();

        for (int i = 0; i < this.coordinates.size(); i++) {
            Coordinate currentCoordinate = this.coordinates.get(i);
            if (boundry.contains(currentCoordinate)) {
                if (boundry.contains(this.coordinates.get(i-1))) {
                    fittedObject.appendCoordinate(currentCoordinate);
                } else {
                    if (currentCoordinate.isNorthOf(northWestCoordinate)) {
                        cutOffCoordinate = Intersection.getIntersection(coordinates.get(i-1), currentCoordinate, northWestCoordinate, northEastCoordinate);
                    } else if (currentCoordinate.isSouthOf(southEastCoordinate)) {
                        cutOffCoordinate = Intersection.getIntersection(coordinates.get(i-1), currentCoordinate, southWestCoordinate, southEastCoordinate);
                    } else if (currentCoordinate.isWestOf(northWestCoordinate, 90)) {
                        cutOffCoordinate = Intersection.getIntersection(coordinates.get(i-1), currentCoordinate, northWestCoordinate, southWestCoordinate);
                    } else if (currentCoordinate.isEastOf(southEastCoordinate)) {
                        cutOffCoordinate = Intersection.getIntersection(coordinates.get(i-1), currentCoordinate, northEastCoordinate, southEastCoordinate);
                    }

                    fittedObject.appendCoordinate(cutOffCoordinate);
                }
            } else {
                //coordinate is not within the area
                if (boundry.contains(this.coordinates.get(i-1))) {
                    //last object was in the boundry
                    if (currentCoordinate.isNorthOf(northWestCoordinate)) {
                        cutOffCoordinate = Intersection.getIntersection(coordinates.get(i-1), currentCoordinate, northWestCoordinate, northEastCoordinate);
                    } else if (currentCoordinate.isSouthOf(southEastCoordinate)) {
                        cutOffCoordinate = Intersection.getIntersection(coordinates.get(i-1), currentCoordinate, southWestCoordinate, southEastCoordinate);
                    } else if (currentCoordinate.isWestOf(northWestCoordinate, 90)) {
                        cutOffCoordinate = Intersection.getIntersection(coordinates.get(i-1), currentCoordinate, northWestCoordinate, southWestCoordinate);
                    } else if (currentCoordinate.isEastOf(southEastCoordinate)) {
                        cutOffCoordinate = Intersection.getIntersection(coordinates.get(i-1), currentCoordinate, northEastCoordinate, southEastCoordinate);
                    }

                    fittedObject.appendCoordinate(cutOffCoordinate);
                }
            }
        } //end for loop

        fittedObject = this;

        return fittedObject;
    }        
    
    /**
     * Returns a Coordinate within a given range.     
     * 
     * @param range
     * @return 
     */
    @Override
    public Coordinate getCoordinateWithinRectangle(Rectangle2D range) {
        Coordinate returnCoordinate = null;
        Point2D    currentPoint;

        for (int i = 0; i < coordinates.size(); i++) {
            currentPoint = this.coordinates.get(i).getCenterPoint();

            if (range.contains(currentPoint.getX(), currentPoint.getY())) {
                returnCoordinate = coordinates.get(i);
                //break; //Why was this removed?
            }
        }

        return returnCoordinate;
    }
    
    /**
     * Calculates the length of segments for Coordinates in this list.
     * 
     * @return 
     */
    public ArrayList<Float> getSegmentLengths() {
       ArrayList<Float>                 lengths;
       Float                            distance, length;
       Point2D                          p1, p2;
       
       lengths = new ArrayList<Float>();
              
       for (int i = 1; i < coordinates.size(); i++) {
           p1 = this.coordinates.get(i-1).getCenterPoint();
           p2 = this.coordinates.get(i).getCenterPoint();

           distance = (float) Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2));
           lengths.add(new Float(distance));
       }
       
       
       return lengths;
    }    
    
    /**
     * Gets the line width modifier to know the width when the map is zoomed in.
     * 
     * @param  mapView
     * @return the current width modifier.
     */
    protected float getWidthModifier(MapView mapView) {
        float widthModifier;
        
        if (mapView.getZoomLevel() < 100) {
            widthModifier = 1f;
        } else {
             widthModifier = (float) (mapView.getZoomLevel() / 100.0);
        }    
        
        return widthModifier;
    }    
    
    /**
     * Checks to see if the supplied coordinate is at the beginning or end of
     * this LineString.
     *
     * @param c
     *          The Coordinate to test.
     * @return  If the coordinate is an endpoint.
     */
    public boolean  isEndPoint(Coordinate c) {
        return coordinates.isEndPoint(c);
    }    
    
    @Override
    public boolean isObjectWithinRectangle(Rectangle2D range) {
        boolean returnValue;
        Line2D  testLine;
        Point2D lastPoint, lastPointLeft, lastPointRight;
        
        //init
        returnValue      = false;
        lastPoint        = null;
        lastPointLeft    = null;
        lastPointRight   = null;                    
        
        try {
            if ((lineCenter != null))  {
                for (Coordinate c: this.coordinates) {
                    
                    //Check to see if the line's points itersect the range
                    if ((c.getCenterPoint() != null) && range.contains(c.getCenterPoint())) {
                        returnValue = true;  
                        break;                
                    }

                    if (lineLeftInit) {
                        if (range.contains(c.getLeftPoint())) {                            
                            returnValue = true;  
                            break;                  
                        }
                    }

                    if (lineRightInit) {
                        if (range.contains(c.getRightPoint())) {
                            returnValue = true;  
                            break;                  
                        }
                    } 

                    /** Construct a line from the current point and 
                     * the last to check if the range intersects it. */
                    if (c.getCenterPoint() != null && lastPoint != null) {
                        testLine = new Line2D.Float(lastPoint, c.getCenterPoint());

                        if (testLine.intersects(range)) {
                            returnValue = true;
                            break;
                        }       
                    }

                    if (lineLeftInit) {
                        if (c.getLeftPoint() != null && lastPointLeft != null) {
                            testLine = new Line2D.Float(lastPointLeft, c.getLeftPoint());

                            if (testLine.intersects(range)) {
                                returnValue = true;
                                break;
                            }      
                        }
                    }

                    if (lineRightInit) {
                        if (c.getRightPoint() != null && lastPointRight != null) {
                            testLine = new Line2D.Float(lastPointRight, c.getRightPoint());

                            if (testLine.intersects(range)) {
                                returnValue = true;
                                break;
                            }    
                        }
                    }                   

                    lastPoint      = c.getCenterPoint();
                    lastPointLeft  = c.getLeftPoint();
                    lastPointRight = c.getRightPoint();

                } 
            }     
        } catch (Exception e) {
            System.err.println("Error in LineString.isObjectWithinRectangle(Rectangle2D) - " + e);
//            isObjectWithinRectangle(range);
        }
        
        return returnValue;
    }

    /**
     * Returns the last Coordinate in this LineString's Coordinate List.
     * 
     * @return 
     */
    public Coordinate lastCoordinate() {
        return coordinates.lastCoordinate();
    }  
    
    @Override
    public void toXML(XmlOutput kmlWriter) {
        try {
            kmlWriter.openTag ("LineString id=\"" + getObjectClass() + "\"");
            kmlWriter.writeTag("name", getName());
            
            if (hasDisplayableText(getDescription()) && !getDescription().equalsIgnoreCase("null"))
                kmlWriter.writeTag("description", "<![CDATA[" + getDescription() + "]]>");            
            
            kmlWriter.writeTag("Ref", Long.toString(getReference()));
            kmlWriter.writeTag("coordinates",  getCoordinateString());

            if (visibility != null)
                visibility.toXML(kmlWriter);            

            writeCustomDataFieldsAsXML(kmlWriter);

            kmlWriter.closeTag("LineString");
        } catch (Exception e) {
            System.out.println("Error in LineString.toXML(KmlWriter) Object: " + this.objectName + " - " + e);
        }
    }
    
}
