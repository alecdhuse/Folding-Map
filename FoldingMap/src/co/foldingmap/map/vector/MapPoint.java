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
import co.foldingmap.map.Visibility;
import co.foldingmap.map.labeling.PointImage;
import co.foldingmap.map.labeling.PointLabel;
import co.foldingmap.map.themes.ColorStyle;
import co.foldingmap.map.themes.IconStyle;
import co.foldingmap.map.themes.LabelStyle;
import co.foldingmap.xml.XmlOutput;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ByteLookupTable;
import java.awt.image.LookupOp;
import java.util.HashMap;
import javax.swing.ImageIcon;

/**
 *
 * @author Alec
 */
public class MapPoint extends VectorObject {
    protected boolean         pointLeftInit, pointCenterInit, pointRightInit;
    protected BufferedImage   iconImage;
    protected byte            byteLookupTable[];
    protected Ellipse2D       pointLeft, pointCenter, pointRight;
    protected float           iconX1, iconX2, iconX3, iconY;                
    protected IconStyle       pointIcon;
    protected ImageIcon       objectImage;
    protected LabelStyle      labelStyle;
    protected Point2D.Float   p2dLeft, p2dCenter, p2dRight;
    protected Rectangle2D     imageArea;  
    
    protected MapPoint() {
        
    }
    
    /**
     * Constructor
     * 
     * @param name
     * @param objectClass
     * @param description
     * @param coordinate 
     */
    public MapPoint(String name, String objectClass, String description, Coordinate coordinate) {
        try {
            init();
            
            this.objectClass       = objectClass;
            this.objectName        = name;
            this.objectDescription = description;
            
            coordinates.add(coordinate);                        
            loadByteLookupTable();
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in MapPoint Constructor(String, String, String, Coordinate) - " + e);
        }
    }    
           
    public MapPoint(String name, Coordinate c, HashMap<String,String> customFields) {
        try {
            init();
            this.coordinates.add(c);
            
            this.objectName        = name;
            this.customDataFields  = customFields;
            this.objectDescription = "";
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in MapPoint Constructor(String, Coordinate, HashMap) - " + e);
        }
    }    
    
    public MapPoint(String name, String objectClass, String description, CoordinateList<Coordinate> coordinates) {
        try {
            init();
            this.coordinates       = coordinates;
            this.objectClass       = objectClass;
            this.objectName        = name;
            this.objectDescription = description;
            
            loadByteLookupTable();
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in MapPoint Constructor(String, String, String, CoordinateList) - " + e);
        }
    }        
    
    @Override
    public VectorObject copy() {
        MapPoint    newMapPoint = new MapPoint(this.objectName, 
                                               this.objectClass, 
                                               this.objectDescription,
                                               this.coordinates);

        newMapPoint.setCustomDataFields(this.customDataFields);
        newMapPoint.setParentLayer(this.getParentLayer());

        return newMapPoint;
    }

    /**
     * 
     * @param g2
     * @param mapView
     * @param colorStyle 
     */
    @Override
    public void drawObject(Graphics2D g2, MapView mapView, ColorStyle colorStyle) {
        boolean       drawObject;        
        
        try {
            if (mapView.displayAll()) {
                drawObject = true;
            } else {
                drawObject = this.isVisible(mapView);          
            }
            
            if (drawObject) {
                //Figure out which IconStyle to use
                if (colorStyle == null) {
                    pointIcon = (IconStyle) mapView.getMapTheme().getStyle(this);
                    if (pointIcon == null) pointIcon = new IconStyle();                
                } else {
                    pointIcon = (IconStyle) colorStyle;
                }            

                //Check to see if the Icon Style is imposing a Visibility
                Visibility vis = pointIcon.getVisibility();
                
                if (vis != null && this.visibility == null) {
                    if (vis.isVisible(mapView.getZoomLevel()) == false) 
                        return;                    
                }                                
                
                //initialize
                pointLeftInit   = false;
                pointCenterInit = false;
                pointRightInit  = false;
                        
                p2dLeft     = null;
                p2dRight    = null;
                
                lineStyle   = new BasicStroke(1,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);                                                
                labelStyle  = pointIcon.getLabel();
                g2.setStroke(lineStyle);            
                
                //is left wrapping needed
                if (mapView.getMapProjection().isLeftShown()) {   
                    p2dLeft       = coordinates.get(0).getLeftPoint();                    
                    pointLeft     = new Ellipse2D.Double( (p2dLeft.x - 2), (p2dLeft.y - 2), 4, 4 );
                    pointLeftInit = true;
                }                  
                
                //right wrapping is needed
                if (mapView.getMapProjection().isRightShown()) {  
                    p2dRight       = coordinates.get(0).getRightPoint();   
                    pointRight     = new Ellipse2D.Double( (p2dRight.x - 2), (p2dRight.y - 2), 4, 4 );
                    pointRightInit = true;
                } 
                
                //center               
                if (coordinates.get(0).getCenterPoint() != null) {
                    p2dCenter = coordinates.get(0).getCenterPoint();
                
                    pointCenter     = new Ellipse2D.Double( (p2dCenter.x - 2), (p2dCenter.y - 2), 4, 4 );
                    pointCenterInit = true;      
                } else {
                    pointCenterInit = false; 
                    Logger.log(Logger.ERR, "MapPoint.drawObject(Graphics2D, MapView) - Coordinate Seems to be Missing from Node Map, adding it.");
                    parentLayer.getParentMap().addCoordinateNode(coordinates.get(0));
                }                
                    
                if (labelStyle != null) {
                    if (pointCenterInit) 
                        mapView.getLabelManager().addLabel(g2, new PointLabel(objectName, labelStyle, (int) p2dCenter.x, (int) (p2dCenter.y + 15), 0));
                     
                    if (pointLeftInit) 
                        mapView.getLabelManager().addLabel(g2, new PointLabel(objectName, labelStyle, (int) p2dLeft.x, (int) (p2dLeft.y + 15), 0));  
                    
                    if (pointRightInit)    
                        mapView.getLabelManager().addLabel(g2, new PointLabel(objectName, labelStyle, (int) p2dRight.x, (int) (p2dRight.y + 15), 0));  
                }//end isLabelVisible
                
                if (pointIcon.getObjectImage() != null) {
                        objectImage = pointIcon.getObjectImage();

                    if (pointCenterInit)
                        iconX1 = p2dCenter.x - (objectImage.getIconWidth() / 2.0f);                        

                    if (pointLeftInit)
                        iconX2 = p2dLeft.x - (objectImage.getIconWidth() / 2.0f); 

                    if (pointRightInit)
                        iconX3 = p2dRight.x - (objectImage.getIconWidth() / 2.0f); 

                    //adjust the marker so that the bottom is the point.
                    if (pointIcon.getID().equalsIgnoreCase("Marker")) {
                        iconY  = p2dCenter.y - (objectImage.getIconHeight());
                    } else {
                        iconY  = p2dCenter.y - (objectImage.getIconHeight() / 2.0f);
                    }                 

                    imageArea = new Rectangle2D.Float(iconX1, iconY, objectImage.getIconWidth(), objectImage.getIconHeight());

                    if (highlighted) {
                        //temportay code  for selection, may replace later
                        ByteLookupTable blut = new ByteLookupTable(0, byteLookupTable);
                        LookupOp        lop  = new LookupOp(blut, null);
                        iconImage            = new BufferedImage(objectImage.getImage().getWidth(null), objectImage.getImage().getHeight(null), BufferedImage.TYPE_4BYTE_ABGR );
                        Graphics2D      g    = (Graphics2D) iconImage.getGraphics();

                        g.drawImage(objectImage.getImage(), 0, 0, null);
                        
                        if (drawObject) {
                            if (pointCenterInit)
                                mapView.getLabelManager().addLabel(g2, new PointImage(iconImage, lop, iconX1, iconY));              

                            if (pointLeftInit)
                                mapView.getLabelManager().addLabel(g2, new PointImage(iconImage, lop, iconX2, iconY));

                            if (pointRightInit)
                                mapView.getLabelManager().addLabel(g2, new PointImage(iconImage, lop, iconX3, iconY));   
                        }
                    } else {
                        if (drawObject) {
                            if (pointCenterInit)
                                mapView.getLabelManager().addLabel(g2, new PointImage(objectImage, iconX1, iconY));                  

                            if (pointLeftInit)
                                mapView.getLabelManager().addLabel(g2, new PointImage(objectImage, iconX2, iconY));

                            if (pointRightInit)
                                mapView.getLabelManager().addLabel(g2, new PointImage(objectImage, iconX3, iconY));    
                        }
                    }//end if highlited                                                
                } else {
                    //Draw an elipse there is no image
                    if (highlighted) {
                        g2.setColor(pointIcon.getSelectedOutlineColor());
                    } else {
                        g2.setColor(pointIcon.getOutlineColor());
                    }  

                    if (pointIcon.isOutlined()) {
                        if (pointCenterInit)
                            g2.draw(pointCenter);        

                        if (pointLeftInit)
                            g2.draw(pointLeft);  

                        if (pointRightInit)                            
                            g2.draw(pointRight);  
                    }      

                    if (highlighted) {
                        g2.setColor(pointIcon.getSelectedFillColor());
                    } else {
                        g2.setColor(pointIcon.getFillColor());
                    }       

                    if (pointCenterInit)
                        g2.fill(pointCenter);        

                    if (pointLeftInit)
                        g2.fill(pointLeft);  

                    if (pointRightInit)                            
                        g2.fill(pointRight);                         
                }                
            } 
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in MapPoint.drawObject(Graphics2D, MapView) - " + e);
        }
    }

    /**
     * Draws the outline for this Point.
     * Not used for MapPoint.
     * 
     * @param g2
     * @param mapView 
     * @param inMultiGeometry 
     */
    @Override
    public void drawOutline(Graphics2D g2, MapView mapView, boolean inMultiGeometry) {
        
    }    
    
    /**
     * Draws the points that make up this object.
     * 
     * @param g2
     * @param mapView 
     */
    @Override
    public void drawPoints(Graphics2D g2, MapView mapView) {
        //do nothing for MapPoint
    }    
    
    /**
     * If the object is within the boundary it will be returned.  If not throw 
     * an error.
     * 
     * @param boundry
     * @return
     * @throws ObjectNotWithinBoundsException 
     */
    @Override
    public VectorObject fitToBoundry(LatLonAltBox boundry) throws ObjectNotWithinBoundsException {
        MapPoint  fittedObject;

        if (boundry.contains(this.coordinates.get(0))) {
            fittedObject = this;
        } else {
            throw new ObjectNotWithinBoundsException(this, boundry);
        }

        return fittedObject;
    }    
    
    /**
     * Creates a box that gives the bounds of the coordinate of this object.
     */
    @Override
    public void generateBoundingBox() {
        Coordinate c = coordinates.get(0);

        this.boundingBox = new LatLonAltBox((float) (c.getLatitude()  - 0.0001),
                                            (float) (c.getLatitude()  + 0.0001),
                                            (float) (c.getLongitude() + 0.0001),
                                            (float) (c.getLongitude() - 0.0001),
                                            (float) (c.getAltitude()),
                                            (float) (c.getAltitude()));        
    }
    
    /**
     * If the Screen Point is within the range return it, as its Coordinate.
     * If it is not within the range, return null.
     * 
     * @param range
     * @return 
     */
    @Override
    public Coordinate getCoordinateWithinRectangle(Rectangle2D range) {
        Coordinate returnCoordinate = null;
        Point2D    screenPoint      = this.coordinates.get(0).getCenterPoint();
        
        if (range.contains(screenPoint.getX(), screenPoint.getY())) 
            returnCoordinate = coordinates.get(0);        

        return returnCoordinate;
    }

    /**
     * Used in place of calling super().  This is mainly to initiate the 
     * CoordinateList to a single Coordinate to save mem.
     * 
     */
    protected final void init() {
        //Default Settings;
        coordinates             = new CoordinateList(1);
        customDataFields        = new HashMap<String, String>(); 
        selectedCoordinate      = Coordinate.UNKNOWN_COORDINATE;      
        reference               = 0;        
    }
    
    /**
     * Used to detect if an object is to be selected.
     * If all or part of the object is within the range this method will 
     * return true.
     * 
     * @param range
     * @return 
     */
    @Override
    public boolean isObjectWithinRectangle(Rectangle2D range) {
        boolean     returnVal = false;

        try {
            
            if (pointLeftInit) {
                if (pointLeft.intersects(range)) 
                    returnVal = true;                
            }
                        
            if (pointCenterInit) {
                if (pointCenter.intersects(range)) 
                    returnVal = true;                
            }
                
            if (pointRightInit) {
                if (pointRight.intersects(range)) 
                    returnVal = true;                
            }            
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in MapPoint.isObjectWithinRectangle(Rectangle2D range) - " + e);
        }

        return returnVal;
    }

    /**
     * Loads the byte lookup table, 
     * this is for when points with icons are selected.
     */
    private void loadByteLookupTable() {
        try {            
            this.byteLookupTable    = new byte[256];

            //setup the seletion lookup table
            for (int j = 0; j < 127; j++)
                byteLookupTable[j] = (byte)(128-j);

            for (int j = 128; j < 256; j++)
                byteLookupTable[j] = (byte) ((128-j) * -1);

        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in MapPoint.loadByteLookupTable() - " + e);
        }
    }     
    
    /**
     * Sets the MapPoint as Highlighted and selects it's one and only coordinate
     * as the selected coordinate;
     *
     * @param highlighted If the MapPoint is highlighted or not.
     */
    @Override
    public void setHighlighted(boolean highlighted) {
        try {
            this.highlighted        = highlighted;
            this.selectedCoordinate = this.coordinates.get(0);
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in MapPoint.setHighlighter(boolean) - " + e);
        }
    }    
    
    @Override
    public void toXML(XmlOutput xmlWriter) {        
        xmlWriter.openTag ("Point class=\"" + getObjectClass() + "\" id=\"" + getName() + "\"");
        xmlWriter.writeTag("Ref", Long.toString(getReference()));
        xmlWriter.writeTag("coordinates", getCoordinateString());

        if (hasDisplayableText(getDescription()) && !getDescription().equalsIgnoreCase("null"))
            xmlWriter.writeTag("description", "<![CDATA[" + getDescription() + "]]>");

        if (visibility != null)
            visibility.toXML(xmlWriter);                

        writeCustomDataFieldsAsXML(xmlWriter);

        xmlWriter.closeTag("Point");
    }
    
}
