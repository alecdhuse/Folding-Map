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

import co.foldingmap.map.themes.PolygonStyle;
import co.foldingmap.map.themes.MapTheme;
import co.foldingmap.map.themes.ThemeConstants;
import co.foldingmap.map.themes.LabelStyle;
import co.foldingmap.map.themes.ColorStyle;
import co.foldingmap.map.themes.OutlineStyle;
import co.foldingmap.Logger;
import co.foldingmap.dataStructures.ListOperations;
import co.foldingmap.dataStructures.StringCountList;
import co.foldingmap.map.MapView;
import co.foldingmap.map.ObjectNotWithinBoundsException;
import co.foldingmap.map.labeling.PolygonLabel;
import co.foldingmap.xml.XmlOutput;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Handles the FmXML object: Polygon.
 * 
 * @author Alec
 */
public class Polygon extends VectorObject {
    private BasicStroke               stroke; 
    private boolean                   pLeftInit, pCenterInit, pRightInit;
    private boolean                   segmentsGenerated;
    private GeneralPath               pLeft, pCenter, pRight;    
    private ArrayList<InnerBoundary>  innerBoundaries;
    private ArrayList<OutlineSegment> outlineSegments;
    
    /**
     * Constructor for Polygon.
     * 
     * @param name
     * @param type
     * @param coordinates 
     */
    public Polygon(String name, String type, CoordinateList coordinates) {
        super();

        commonConstructor(name, type);
        this.coordinates.addAll(coordinates);
    }      
    
    /**
     * Constructor using String Coordinates.
     * 
     * @param name
     * @param type
     * @param lineCoordinates 
     */
    public Polygon(String name, String type, String lineCoordinates) {
        super();
        Coordinate      newCoordinate;
        String          currentCoordinate;
        StringTokenizer st = new StringTokenizer(lineCoordinates);

        commonConstructor(name, type);

        //get coordinates
        while (st.hasMoreTokens())
        {
            currentCoordinate = st.nextToken();
            newCoordinate     = new Coordinate(currentCoordinate);
            coordinates.add(newCoordinate);
        }
    }    
    
    /**
     * A Constructor for shared init elements between constructors.
     * 
     * @param name
     * @param objectClass
     */
    public final void commonConstructor(String name, String objectClass) {
        try {
            this.coordinates        = new CoordinateList<Coordinate>();
            this.innerBoundaries    = new ArrayList<InnerBoundary>(1);
            this.objectName         = name;
            this.objectClass        = objectClass;  
            this.pLeftInit          = false;
            this.pCenterInit        = false;
            this.pRightInit         = false;
            this.pLeft              = new GeneralPath();
            this.pRight             = new GeneralPath();
            this.pCenter            = new GeneralPath();    
            this.segmentsGenerated  = false;
            this.stroke             = new BasicStroke(1f,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in Polygon.commonConstructor(String, String) - " + e);
        }
    }    
    
    /**
     * Adds an Inner Boundary to this polygon.
     * 
     * @param ib 
     */
    public void addInnerBoundary(InnerBoundary ib) {
        this.innerBoundaries.add(ib);
    }
    
    /**
     * Adds an OutlineSegment to this Polygon.  
     * This should be called directly rather than adding to the outlineSegments
     * ArrayList directly.
     * 
     * @param segment 
     */
    public void addOutlineSegment(OutlineSegment segment) {
        try {
            if (outlineSegments.size() > 0 && segment.getColor() != null) {
                OutlineSegment lastSegment = outlineSegments.get(outlineSegments.size() - 1);

                //Check to see if the outline styles are the same.
                if (segment.getColor().equals(lastSegment.getColor()) &&
                    segment.getStrokeType() == lastSegment.getStrokeType()) {

                    //If they are the same combine segments
                    lastSegment.appendSegment(segment);
                } else {
                    //If they are not the same then add a new segment.
                    outlineSegments.add(segment);
                }
            } else {
                //No segment to check against
                outlineSegments.add(segment);
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in Polygon.addOutlineSegment(OutlineSegment) - " + e);
        }
    }
    
    public void convertCoordinatesToLines(MapView mapView) {          
        Coordinate     currentCoordinate;
        float          x1, x2, x3, y1;
        Point2D.Float  tempPoint;        
            
        //clear out general path
        pCenter.reset();
        
        pLeftInit     = false;
        pCenterInit   = false; 
        pRightInit    = false;
        
        try {
            //prime the loop
            currentCoordinate = (Coordinate) coordinates.get(0);
            pCenterInit       = true; 
            tempPoint         = currentCoordinate.getCenterPoint();
            
            pCenter.moveTo(tempPoint.getX(), tempPoint.getY());
            
            //is left wrapping needed
            if (mapView.getMapProjection().isLeftShown()) {    
                tempPoint  = currentCoordinate.getLeftPoint();
                pLeftInit  = true;  
                
                pLeft.reset();                
                pLeft.moveTo(tempPoint.getX(), tempPoint.getY());                     
            }

            //right wrapping is needed
            if (mapView.getMapProjection().isRightShown()) {
                tempPoint   = currentCoordinate.getRightPoint();
                pRightInit  = true;      
                
                pRight.reset();            
                pRight.moveTo(tempPoint.getX(), tempPoint.getY());   
            }                                 

            for (int i = 1; i < coordinates.size(); i++) {
                currentCoordinate = (Coordinate) coordinates.get(i);
                tempPoint         = currentCoordinate.getCenterPoint();

                pCenter.lineTo(tempPoint.getX(), tempPoint.getY());
                
                if (pLeftInit) {
                    tempPoint  = currentCoordinate.getLeftPoint();
                    pLeft.lineTo(tempPoint.getX(), tempPoint.getY());                  
                }
                         
                if (pRightInit) {
                    tempPoint  = currentCoordinate.getRightPoint();
                    pRight.lineTo(tempPoint.getX(), tempPoint.getY());
                }
                                
            }
  
            if (pLeftInit)   pLeft.closePath();         
            if (pCenterInit) pCenter.closePath(); 
            if (pRightInit)  pRight.closePath();           
            
            if (this.innerBoundaries.size() > 0) {
                
                if (pLeftInit)   pLeft.setWindingRule(  GeneralPath.WIND_EVEN_ODD);        
                if (pCenterInit) pCenter.setWindingRule(GeneralPath.WIND_EVEN_ODD);
                if (pRightInit)  pRight.setWindingRule( GeneralPath.WIND_EVEN_ODD);             
                
                for (InnerBoundary ib: innerBoundaries) {
                    currentCoordinate = ib.getCoordinateList().get(0);
                                
                    y1 = mapView.getY(currentCoordinate);        
                                                                                    
                    if (pLeftInit) {
                        x2 = mapView.getX(currentCoordinate, MapView.WRAP_LEFT);
                        pLeft.moveTo(x2, y1);
                    }   
                    
                    if (pCenterInit) {
                        x1 = mapView.getX(currentCoordinate, MapView.NO_WRAP);
                        pCenter.moveTo(x1, y1);
                    }
                    
                    if (pRightInit)  {
                        x3 = mapView.getX(currentCoordinate, MapView.WRAP_RIGHT);
                        pRight.moveTo(x3, y1);
                    }
                    
                    for (int i = 1; i < coordinates.size(); i++) {
                        currentCoordinate = (Coordinate) coordinates.get(i);     
                        y1 = mapView.getY(currentCoordinate); 
                        
                        if (pLeftInit) {
                            x2 = mapView.getX(currentCoordinate, MapView.WRAP_LEFT);
                            pLeft.lineTo(x2, y1);
                        }   

                        if (pCenterInit) {
                            x1 = mapView.getX(currentCoordinate, MapView.NO_WRAP);
                            pCenter.lineTo(x1, y1);
                        }

                        if (pRightInit)  {
                            x3 = mapView.getX(currentCoordinate, MapView.WRAP_RIGHT);
                            pRight.lineTo(x3, y1);
                        }                        
                    }         
                    
                    if (pLeftInit)   pLeft.closePath();         
                    if (pCenterInit) pCenter.closePath(); 
                    if (pRightInit)  pRight.closePath();                     
                }
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in Polygon.convertCoordinatesToLines(MapView) - " + e);
        }
    }    
    
    @Override
    public VectorObject copy() {
        Polygon newCopy = new Polygon(this.objectName, this.getObjectClass(), this.getCoordinateList());

        newCopy.setDescription(this.getDescription());
        newCopy.setParentLayer(this.getParentLayer());
        newCopy.setCustomDataFields(this.customDataFields);

        return newCopy;
    }

    /**
     * Creates a Label for this Polygon.
     * 
     * @param g2
     * @param mapView 
     */
    public void createLabel(Graphics2D g2, MapView mapView) {
        Coordinate  center      = this.boundingBox.getCenter();        
        FontMetrics fontMetrics = g2.getFontMetrics();
        
        int labelWidth = fontMetrics.stringWidth(objectName);
        
        float x = mapView.getX(center, MapView.NO_WRAP);
        float y = mapView.getY(center);
        
        x = x - (labelWidth / 2f);
        
        mapView.getLabelManager().addLabel(g2, new PolygonLabel(objectName, new LabelStyle(Color.WHITE), (int) x, (int) y));
    }
    
    /**
     * Draws this Polygon on the map.
     * 
     * @param g2
     * @param mapView 
     * @param colorStyle
     */
    @Override
    public void drawObject(Graphics2D g2, MapView mapView, ColorStyle colorStyle) {        
        boolean       drawObject, fillPoly;
        PolygonStyle  polygonStyle;    
        
        try {                                      
            //check to see if we need to draw the object
            if (mapView.displayAll()) {
                drawObject = true;
            } else {
                drawObject = this.isVisible(mapView);
            }

            if (drawObject) {                     
                convertCoordinatesToLines(mapView);

                if (colorStyle == null) {
                    polygonStyle = (PolygonStyle) mapView.getMapTheme().getStyle(this, mapView.getZoomLevel());

                    if (polygonStyle == null) 
                        polygonStyle = mapView.getMapTheme().getPolygonStyle("(Unspecified Polygon)");                
                } else {
                    polygonStyle = (PolygonStyle) colorStyle;
                }                
                
                fillPoly = polygonStyle.isFilled();
                g2.setStroke(this.stroke);                  
                
                if (highlighted) {
                    g2.setColor(polygonStyle.getSelectedFillColor());
                    fillPoly = true; //Force fill is selected
                } else {
                    g2.setColor(polygonStyle.getFillColor());
                }

                if (fillPoly) {
                    Paint cPaint = g2.getPaint();
                    
                    if (polygonStyle.isGradientFilled() && !highlighted) {
                        Rectangle2D   bounds = pCenter.getBounds2D();
                        float         x1     = (float) bounds.getMinX();
                        float         x2     = (float) bounds.getMaxX();
                        float         y1     = (float) 0;
                        float         y2     = (float) 20;                        
                        GradientPaint gp     = new GradientPaint(x1, y1, polygonStyle.getGradient1(), x2, y2, polygonStyle.getGradient2());
                        
                        g2.setPaint(gp);
                        g2.fill(pCenter);
                        g2.setPaint(cPaint);
                    } else {
                        if (pLeftInit)   g2.fill(pLeft);                
                        if (pCenterInit) g2.fill(pCenter);
                        if (pRightInit)  g2.fill(pRight);  

                        if (polygonStyle.isImagedFilled()) {                            
                            BufferedImage bi     = polygonStyle.getObjectImage();
                            Rectangle2D   bounds = new Rectangle2D.Float(0, 0, bi.getWidth(), bi.getHeight());
                            TexturePaint  tp     = new TexturePaint(bi, bounds);

                            g2.setPaint(tp);

                            if (pLeftInit)   g2.fill(pLeft);                
                            if (pCenterInit) g2.fill(pCenter);
                            if (pRightInit)  g2.fill(pRight);  

                            g2.setPaint(cPaint);
                        } else {
                            //if not outlined, draw the outline with the polygon's color
                            if (!polygonStyle.isOutlined()) {                    
                                if (pLeftInit)   g2.draw(pLeft);                       
                                if (pCenterInit) g2.draw(pCenter);  
                                if (pRightInit)  g2.draw(pRight); 
                            }                             
                        }//Image Filled Check
                    } // Gradient Check
                } // Filled Check   
                
//                createLabel(g2, mapView);
            } //end drawObject check                        
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in Polygon.drawObjet(Graphics2D, MapView) " + this.objectName + " - " + e);
        }
    }

    /**
     * Draws the outline of this polygon.
     * 
     * @param g2
     * @param mapView 
     * @param inMultiGeometry 
     */
    @Override
    public void drawOutline(Graphics2D g2, MapView mapView, boolean inMultiGeometry) {       
        boolean     drawObject;
        ColorStyle  polygonStyle;
         
        try {
            //update segment outlines, if needed
            if (!segmentsGenerated) {
                this.updateOutlines(mapView.getMapTheme());                
            }
            
            polygonStyle = mapView.getMapTheme().getPolygonStyle(this.getObjectClass());            

            if (polygonStyle != null) {  
                drawObject = this.isVisible(mapView);        

                if (drawObject && polygonStyle.isOutlined()) {                    
                    if (highlighted) {
                        g2.setColor(polygonStyle.getSelectedOutlineColor());
                    } else {
                        g2.setColor(polygonStyle.getOutlineColor());
                    }                    

                    for (OutlineSegment seg: this.outlineSegments) {
                        if (outlineSegments.size() == 1)
                            seg.closeSegment();
                                
                        seg.draw(g2, mapView);
                    }
                }
            } 
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in Polygon.drawOutline(Graphics2D, MapView) - " + e);
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

        //change the draw style
        g2.setStroke(new BasicStroke(2f,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));                    
        
        for (Coordinate c: this.coordinates) {            
            centerShape = getPointShape(c, MapView.NO_WRAP);
            
            g2.setColor(mapView.getMapTheme().getPointColor());              
            g2.draw(centerShape);     
            
            if (mapView.getMapProjection().isLeftShown()) {
                leftShape = getPointShape(c, MapView.WRAP_LEFT);
                g2.draw(leftShape);    
            }
            
            if (mapView.getMapProjection().isRightShown()) {
                rightShape = getPointShape(c, MapView.WRAP_RIGHT);
                g2.draw(rightShape);                  
            }            
            
            if (selectedCoordinate.equals(c))
                g2.setColor(Color.WHITE);                        

            g2.fill(centerShape);     
            
            if (mapView.getMapProjection().isRightShown())
                 g2.fill(rightShape);  
            
            if (mapView.getMapProjection().isLeftShown())                    
                g2.fill(leftShape); 
        }           
    }
    
    /**
     * Fits this object to a given boundary.
     * 
     * @param boundry
     * @return
     * @throws ObjectNotWithinBoundsException 
     */
    @Override
    public VectorObject fitToBoundry(LatLonAltBox boundry) throws ObjectNotWithinBoundsException {
        Coordinate  northEastCoordinate, northWestCoordinate, southEastCoordinate, southWestCoordinate;
        Coordinate  cutOffCoordinate;
        Polygon     fittedObject;

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
     * Gets a coordinate with in a given Rectangle2D range.
     * 
     * @param range
     * @return 
     */
    @Override
    public Coordinate getCoordinateWithinRectangle(Rectangle2D range) {
        Coordinate returnCoordinate = null;

        for (Coordinate c: this.coordinates) {
            if (range.contains(c.getCenterPoint())) {
                returnCoordinate =  c;
                break;                
            }
            
            if ((c.getLeftPoint() != null) && range.contains(c.getLeftPoint())) {
                returnCoordinate =  c;
                break;                  
            }
            
            if ((c.getRightPoint() != null) && range.contains(c.getRightPoint())) {
                returnCoordinate =  c;
                break;                  
            }                                   
        }

        return returnCoordinate;
    }

    /**
     * Returns the inner boundaries of this polygon.
     * 
     * @return 
     */
    public ArrayList<InnerBoundary> getInnerBoundaries() {
        return innerBoundaries;
    }
            
    
    /**
     * Specifies if the selected range is within this polygon.
     * 
     * TODO: If the polygon is filing the screen don't select it.
     * 
     * @param range
     * @return 
     */
    @Override
    public boolean isObjectWithinRectangle(Rectangle2D range) {
        boolean returnValue = false;
        
        try {
            if (pLeft != null) {
                if (pLeft.contains(range))
                   returnValue = true;
            }
            
            if (pCenter != null) {
                if (pCenter.contains(range))
                   returnValue = true;                
            }
            
            if (pRight != null) {
                if (pRight.contains(range))
                   returnValue = true;                
            }            
            
            //TODO: this is slow and needs to be refactored
            if (!returnValue) {
                for (Coordinate c: this.coordinates) {
                    if (range.contains(c.getCenterPoint())) {
                        returnValue = true;  
                        break;                
                    }

                    if ((c.getLeftPoint() != null) && range.contains(c.getLeftPoint())) {
                        returnValue = true;  
                        break;                  
                    }

                    if ((c.getRightPoint() != null) && range.contains(c.getRightPoint())) {
                        returnValue = true;  
                        break;                  
                    }           
                }                
            }

        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in Polygon.isObjectWithinRectangle(Rectangle2D) - " + e);
            isObjectWithinRectangle(range);
        }

        return returnValue;
    }

    @Override
    public String toString() {
       return  "Polygon - " + objectName + " - " + objectClass;
    }
    
    /**
     * Writes out this object in XML form so it can be copied or written to a 
     * file.
     * 
     * @param xmlWriter 
     */
    @Override
    public void toXML(XmlOutput xmlWriter) {
        try {
            xmlWriter.openTag ("Polygon class=\"" + getObjectClass() + "\" id=\"" + getName() + "\"");

            if (this.visibility != null)
                visibility.toXML(xmlWriter);            
            
            if (hasDisplayableText(getDescription()) && !getDescription().equalsIgnoreCase("null"))
                xmlWriter.writeTag("description", "<![CDATA[" + getDescription() + "]]>");    
            
            xmlWriter.writeTag("Ref", Long.toString(getReference()));
            xmlWriter.openTag ("outerBoundary");
            xmlWriter.writeTag("coordinates",  getCoordinateString());
            xmlWriter.closeTag("outerBoundary");
            
            for (InnerBoundary ib: innerBoundaries)
                ib.toXML(xmlWriter);

            //Timestanps are now in nodes and this isn't needed - 2014-07-16 ASD            
//            if (parentLayer.hasTimeSpan()) {
//                xmlWriter.openTag ("gx:Timestamps");
//                    for (Coordinate currentCoordinate: coordinates)
//                        xmlWriter.writeText(currentCoordinate.getTimestamp() + " ");
//                xmlWriter.closeTag("gx:Timestamps");
//            }

             writeCustomDataFieldsAsXML(xmlWriter);

            xmlWriter.closeTag("Polygon");
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in Polygon.toXML(KmlWriter) Object: " + this.objectName + " - " + e);
        }
    }
    
    private void buildSegments(MapTheme theme) {
        ArrayList<Coordinate>     segCoordinates;
        ArrayList<VectorObject>   commonObjects;
        ArrayList<VectorObject>   currentParentObjects, lastParentObjects;
        boolean                   addSegment, dateline, divideSegment;
        Color                     outlineColor, ColorToUse;
        Coordinate                c;
        double                    longitudeFirst, longitudeLast;
        OutlineSegment            currentOutlineSegment;
        PolygonStyle              polyStyle;
        VectorObject              gulfedObject;
        
        try {
            //init
            ColorToUse            = null;
            dateline              = false;
            lastParentObjects     = null;
            currentParentObjects  = null;            
            currentOutlineSegment = new OutlineSegment();
            outlineSegments       = new ArrayList<OutlineSegment>();
            polyStyle             = theme.getPolygonStyle(this.getObjectClass());
            outlineColor          = polyStyle.getOutlineColor();

            for (int i = 0; i < coordinates.size(); i++) {
                addSegment    = true;
                divideSegment = false;
                gulfedObject  = null;
                c = coordinates.get(i);
                
                currentParentObjects = c.getParentVectorObjects();
                
                currentOutlineSegment.addCoordinate(c);

                if (lastParentObjects == null) {
                    lastParentObjects = currentParentObjects;
                } else {
                    if (ListOperations.listsContainSameObjects(currentParentObjects, lastParentObjects)) {                    
                        lastParentObjects = currentParentObjects;

                        //gulf detection
                        if (currentOutlineSegment.size() == 2) {
                            Coordinate c1, c2;
                            int lastIndex, thisIndex;

                            c1 = currentOutlineSegment.getCoordinateList().get(0);
                            c2 = currentOutlineSegment.getCoordinateList().get(1);        
                            commonObjects = ListOperations.getCommonObjects(c1.getParentVectorObjects(), c2.getParentVectorObjects());

                            for (VectorObject vecObj: commonObjects) {
                                thisIndex = vecObj.getCoordinateList().indexOf(c1);
                                lastIndex = vecObj.getCoordinateList().indexOf(c2);

                                if (lastIndex > 0 && thisIndex > 0) {
                                    if (Math.abs(lastIndex - thisIndex) > 1) {
                                        //is a gulf
                                        divideSegment = true;    
                                        gulfedObject  = vecObj;
                                        break;                                    
                                    }
                                }
                            }                        
                        }
                        
                        //International Dateline Detection
                        if (divideSegment == false) {
                            if (c.getLongitude() == 180 || c.getLongitude() == -180) {
                                divideSegment = true;
                                dateline      = true;
                            }           
                        }
                    } else {          
                        divideSegment = true;
                    }
                }
                
                if (divideSegment == true) {
                    if (currentOutlineSegment.getCoordinateList().size() > 1) {
                        //Lists have diffent parent objects, split into a new outline segment.

                        commonObjects  = ListOperations.getCommonObjects(currentParentObjects, lastParentObjects);
                        segCoordinates = currentOutlineSegment.getCoordinateList();
                        longitudeFirst = segCoordinates.get(0).getLongitude();
                        longitudeLast  = segCoordinates.get(segCoordinates.size() - 1).getLongitude();

                        //If this segment bridges a gulf, remove the gulfed object
                        if (gulfedObject != null) commonObjects.remove(gulfedObject);
                        
                        //Check to see if the segment lies on the IDL
                        if (dateline == true && 
                        (longitudeFirst == 180 || longitudeFirst == -180) &&
                        (longitudeLast  == 180 || longitudeLast  == -180)) {

                            ColorToUse = polyStyle.getOutlineStyleByCondition("None").getColor();
                            currentOutlineSegment.setColor(ColorToUse);
                        } else if (commonObjects.size() == 2) {
                            commonObjects.remove(this);             

                            if (commonObjects.size() > 0)
                                ColorToUse = getBorderConditionColor(theme, polyStyle, commonObjects.get(0));

                            if (ColorToUse != null) {
                                currentOutlineSegment.setColor(ColorToUse); 
                            } else {
                                addSegment = false;
                            }
                        } else if (commonObjects.size() < 2) {                               
                            if (currentParentObjects.size() == 2) {
                                if (currentParentObjects.get(0) == this) {
                                    ColorToUse = getBorderConditionColor(theme, polyStyle, currentParentObjects.get(1));
                                } else {
                                    ColorToUse = getBorderConditionColor(theme, polyStyle, currentParentObjects.get(0));
                                }                                               

                                currentOutlineSegment.setColor(ColorToUse);
                            } else {                           
                                lastParentObjects.removeAll(currentParentObjects);

                                if (lastParentObjects.size() > 0) {                                      
                                    ColorToUse = getBorderConditionColor(theme, polyStyle, lastParentObjects.get(0));
                                    currentOutlineSegment.setColor(ColorToUse); 
                                } 
                            }         
                        } else {
                            //More than 2 common object, common with gulfs
                            System.out.println("--> More than 2 common Objects");
                            currentOutlineSegment.setColor(Color.RED);  
                        }

                        if (addSegment == true) addOutlineSegment(currentOutlineSegment);
                        currentOutlineSegment = new OutlineSegment();
                        currentOutlineSegment.addCoordinate(c);

                        lastParentObjects = currentParentObjects;                        
                    }                    
                } // If for deviding segment                 
            }
        
            //Last Segment
            if (currentOutlineSegment.getColor() == null) {    
                commonObjects  = ListOperations.getCommonObjects(currentParentObjects, lastParentObjects);

                if (commonObjects.size() == 2) {
                //Check to see if the segment lies on the IDL
                    //test for implementing OutlineStyles
                    commonObjects.remove(this);                            
                    ColorToUse = getBorderConditionColor(theme, polyStyle, commonObjects.get(0));

                    if (ColorToUse != null) {
                        currentOutlineSegment.setColor(ColorToUse); 
                    }             
                } else if (commonObjects.size() == 1) {
                    //no border check for ANY or NONE
                    for (OutlineStyle os: polyStyle.getOutlineStyles()) {
                        if (os != null) {

                            if (os.getBorderCondition().equalsIgnoreCase(ThemeConstants.NONE) || 
                                os.getBorderCondition().equalsIgnoreCase(ThemeConstants.ANY)) {

                                //Matching condition found
                                if (os.getColor() != null) {
                                    currentOutlineSegment.setColor(os.getColor()); 
                                    break;
                                } 
                            }
                        } //end null check
                    }                                         
                } else {                    
                    StringCountList countList = new StringCountList();
                    for (VectorObject commObj: commonObjects)
                        countList.addIncrement(commObj.getObjectClass());
                    
                    if (countList.getMostOccurringString("").equalsIgnoreCase("Ocean")) {
                        currentOutlineSegment.setColor(polyStyle.getFillColor());
                    } else {
                        currentOutlineSegment.setColor(outlineColor); 
                    }                                               
                }               
            }
            
            addOutlineSegment(currentOutlineSegment);       
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in Polygon.buildSegments(MapView) - " + e);
        }        
    }
    
    /**
     * Updates how outlines are drawn.  A polygon may have OutlineSegments with
     * different characteristics.  I.e. different color, depending on certain
     * conditions like what other objects the outline will be shared with.
     */
    @Override
    public void updateOutlines(MapTheme theme) {        
        ArrayList<OutlineStyle> outlineStyles;
        boolean                 buildSegments;
        PolygonStyle            polyStyle;
        try {
            polyStyle = theme.getPolygonStyle(objectClass);
            
            if (polyStyle != null) {
                outlineStyles = theme.getPolygonStyle(objectClass).getOutlineStyles();

                //Decide if the outline segments need to be built
                if (outlineStyles.size() > 1) {        
                    buildSegments = true;
                } else if (outlineStyles.size() == 1) {     
                    if (!outlineStyles.get(0).getBorderCondition().equalsIgnoreCase(OutlineStyle.ANY)) {
                        buildSegments = true;
                    } else {
                        buildSegments = false;
                    }
                } else {
                    buildSegments = false;
                }

                if (buildSegments) {
                    buildSegments(theme);
                } else {
                    //This Polygon has no shared segments 
                    OutlineSegment newSegment = new OutlineSegment(1);            
                    OutlineStyle   styleToUse;
                    PolygonStyle   style;

                    //Create the OutlineSegment ArrayList with a size of 1, because there is only one outline.
                    this.outlineSegments = new ArrayList<OutlineSegment>(1);

                    //add all the coordinates from this Polygon into a new segment.
                    for (Coordinate c: this.coordinates) 
                        newSegment.addCoordinate(c);

                    newSegment.closeSegment();
                    style = theme.getPolygonStyle(objectClass);

                    if (style != null) {
                        //First check to see if there is a 'None' style
                        styleToUse = ListOperations.getOutlineStyleFromCondition(style.getOutlineStyles(), "None");

                        //If no 'None' style, look for an 'Any'
                        if (styleToUse == null) 
                            styleToUse = ListOperations.getOutlineStyleFromCondition(style.getOutlineStyles(), "Any");

                        if (styleToUse != null) {
                            newSegment.setColor(styleToUse.getColor());
                        } else {
                            //no outline style, do not outline
                        }

                        addOutlineSegment(newSegment);  
                    } else {
                        Logger.log(Logger.WARN, "Polygon.updateOutlines(MapTheme) Style: " + objectClass + " Not found in this theme."); 
                    }
                }
            }
            
            this.segmentsGenerated = true;
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in Polygon.updateOutlines(MapTheme) - " + e);
        }
    }
    
    /**
     * Returns the Color to be used for the border from the given polyStyle and
     * the given borderingObject.
     * 
     * @param theme
     * @param polyStyle
     * @param borderingObject
     * @return 
     */
    public static Color getBorderConditionColor(MapTheme theme, PolygonStyle polyStyle, VectorObject borderingObject) {
        ColorStyle borderingStyle;
        
        for (OutlineStyle os: polyStyle.getOutlineStyles()) {
            if (os != null) {
                borderingStyle  = theme.getStyle(borderingObject, 0);

                if (os.getBorderCondition().equalsIgnoreCase(borderingStyle.getFeatureType())  || 
                    os.getBorderCondition().equalsIgnoreCase(borderingObject.getObjectClass()) ||
                    os.getBorderCondition().equalsIgnoreCase(ThemeConstants.ANY)) {

                    //Matching condition found
                    if (os.getColor() != null) {
                        return os.getColor(); 
                    } 
                }
            } //end null check
        }        
        
        return null;
    }
}

