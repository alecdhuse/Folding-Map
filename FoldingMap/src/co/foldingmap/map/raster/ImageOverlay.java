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
package co.foldingmap.map.raster;

import co.foldingmap.map.Overlay;
import co.foldingmap.map.MapView;
import co.foldingmap.map.MapObject;
import co.foldingmap.map.Visibility;
import co.foldingmap.map.Layer;
import co.foldingmap.map.vector.LatLonBox;
import co.foldingmap.map.vector.LatLonAltBox;
import co.foldingmap.map.vector.CoordinateList;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.MapIcon;
import co.foldingmap.map.themes.ColorStyle;
import co.foldingmap.xml.XmlOutput;
import java.awt.*;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import javax.swing.ImageIcon;

/**
 *
 * @author Alec
 */
public class ImageOverlay extends Overlay implements MapObject {
    protected BasicStroke  points, centerStroke, centerOutline;
    protected boolean      highlighted;
    protected Coordinate   center, ne, nw, se, sw;
    protected Coordinate   centerB, neB, nwB, seB, swB;
    protected Coordinate   selectedCoordinate;
    protected double       rotation;
    protected float        centerX, centerY;
    protected Layer        parentLayer;
    protected MapIcon      mapIcon;    
    protected MapView      lastMapView;
    protected int          height, width;
    protected int[]        pointsX, pointsY;
    protected Polygon      screenBounds;
    
    /**
     * Constructor for 2D ImageOverlay.
     * 
     * @param id
     * @param mapIcon
     * @param bounds 
     */
    public ImageOverlay(String id, MapIcon mapIcon, LatLonBox bounds) {
        this.overlayID  = id;
        this.mapIcon    = mapIcon;
        this.rotation   = 0;
        
        this.pointsX    = new int[4];
        this.pointsY    = new int[4];        
        
        points          = new BasicStroke(1,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        centerStroke    = new BasicStroke(3,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        centerOutline   = new BasicStroke(5,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);        
        
        setBoundary(new LatLonAltBox(bounds));
    }    
    
    /**
     * Constructor for 3D ImageOverlay.
     * 
     * @param id
     * @param mapIcon
     * @param bounds 
     */
    public ImageOverlay(String id, MapIcon mapIcon, LatLonAltBox bounds) {
        this.overlayID  = id;
        this.mapIcon    = mapIcon;
        this.rotation   = 0;
        
        this.pointsX    = new int[4];
        this.pointsY    = new int[4];        
        
        points          = new BasicStroke(1,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        centerStroke    = new BasicStroke(3,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        centerOutline   = new BasicStroke(5,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);        
        
        setBoundary(bounds);
    }
    
    /**
     * Converts the objects latitude and longitude coordinates screen x,y points.
     * 
     * @param mapView 
     */
    protected void convertCoordinatesToLines(MapView mapView) {      
        float heightWidthRatio;
        float latDiff, lonDiff;
        float tempHeight, tempWidth;
        int   x, y;
        
        if (!center.equals(centerB)) {
            
        }
        
        if (!nw.equals(nwB)) {
            x = (int) mapView.getX(nwB, MapView.NO_WRAP);
            y = (int) mapView.getY(nwB);
            tempHeight = (pointsY[3] - y) * mapIcon.getHeightWidthRatio();
            tempWidth  = (pointsX[1] - x) / mapIcon.getHeightWidthRatio();
            heightWidthRatio = tempHeight / tempWidth;
            
            
            nwB = nw.copy();
            ne.setLatitude((float) nw.getLatitude());
            sw.setLongitude((float) nw.getLongitude());
            center  = getCenter();
            centerB = center.copy();
        }        
        
        if (!ne.equals(neB)) {
            neB = ne.copy();
            nw.setLatitude((float) ne.getLatitude());
            se.setLongitude((float) ne.getLongitude());
            center  = getCenter();
            centerB = center.copy();
        }         
        
        if (!se.equals(seB)) {
            seB = se.copy();
            sw.setLatitude((float) se.getLatitude());
            ne.setLongitude((float) se.getLongitude());    
            center  = getCenter();
            centerB = center.copy();
        }         
        
        if (!sw.equals(swB)) {
            swB = sw.copy();
            se.setLatitude((float) sw.getLatitude());
            nw.setLongitude((float) sw.getLongitude());    
            center  = getCenter();
            centerB = center.copy();
        }         
        
        //accomidate for changes in the center coordinate.
        if (!center.equals(centerB)) {
            latDiff = (float) (centerB.getLatitude()  - center.getLatitude());
            lonDiff = (float) (centerB.getLongitude() - center.getLongitude());
            
            nw.setLatitude((float) nw.getLatitude() - latDiff);
            ne.setLatitude((float) ne.getLatitude() - latDiff);
            se.setLatitude((float) se.getLatitude() - latDiff);
            sw.setLatitude((float) sw.getLatitude() - latDiff);
            
            nw.setLongitude((float) nw.getLongitude() - lonDiff);
            ne.setLongitude((float) ne.getLongitude() - lonDiff);
            se.setLongitude((float) se.getLongitude() - lonDiff);
            sw.setLongitude((float) sw.getLongitude() - lonDiff);   
            
            centerB = center.copy();
            swB = sw.copy();
            seB = se.copy();
            neB = ne.copy();
            nwB = nw.copy();
        }
                        
        pointsX[0] = (int) mapView.getX(nw, MapView.NO_WRAP);
        pointsX[1] = (int) mapView.getX(ne, MapView.NO_WRAP);
        pointsX[2] = (int) mapView.getX(se, MapView.NO_WRAP);
        pointsX[3] = (int) mapView.getX(sw, MapView.NO_WRAP);
        centerX    = mapView.getX(center,   MapView.NO_WRAP);
                
        pointsY[0] = (int) mapView.getY(nw);
        pointsY[1] = (int) mapView.getY(nw);
        pointsY[2] = (int) mapView.getY(se);
        pointsY[3] = (int) mapView.getY(sw);           
        centerY    = mapView.getY(center);    
        
        height = Math.abs(pointsY[3] - pointsY[0]);
        width  = Math.abs(pointsX[1] - pointsX[0]);
        
        if (width > 0) {
            heightWidthRatio = (height / (float) width);
        } else {
            heightWidthRatio = 0;
        }
        
        //make sure to keep the images height-width ratio
        if (heightWidthRatio != mapIcon.getHeightWidthRatio()) {

        }        
    }    
    
    /**
     * Returns a generic MapObject copy of this ImageOverlay.
     * 
     * @return 
     */
    @Override
    public MapObject copy() {
        MapObject    object;
                
        object = new ImageOverlay(overlayID, mapIcon.copy(), getBounds());
        
        return object;
    }    
    
    /**
     * Returns a copy of this ImageOverlay
     * 
     * @return 
     */
    @Override
    public Overlay copyOverlay() {
        ImageOverlay groundOverlay;
        
        groundOverlay = new ImageOverlay(overlayID, mapIcon.copy(), getBounds());
        
        return groundOverlay;
    }

    /**
     * Draws this object.
     * 
     * @param g2
     * @param mapView 
     */
    @Override
    public void drawObject(Graphics2D g2, MapView mapView) {
        lastMapView = mapView;
        drawObject(g2, mapView, null);
    }    
    
    /**
     * Draws this overlay object on the given Graphics class using the 
     * given MapView.
     * 
     * @param g2
     * @param mapView 
     */
    @Override
    public void drawObject(Graphics2D g2, MapView mapView, ColorStyle colorStyle) {        
        ImageIcon     imageIcon;                   
        
        lastMapView = mapView;
        
        if (rotation != 0) {
            
        }            
        
        convertCoordinatesToLines(mapView);
        screenBounds = new Polygon(pointsX, pointsY, 4);
        imageIcon    = mapIcon.getImageIcon();
        
        if (imageIcon != null)
            g2.drawImage(imageIcon.getImage(), pointsX[0], pointsY[1], width, height, imageIcon.getImageObserver());
    }

    /**
     * Draws the control points for this ImageOverlay.
     * 
     * @param g2
     * @param mapView 
     */
    @Override
    public void drawPoints(Graphics2D g2, MapView mapView) {
        Ellipse2D   centerPoint;
        Rectangle2D rect1, rect2, rect3, rect4;
        
        convertCoordinatesToLines(mapView);        
        
        rect1 = new Rectangle2D.Double((pointsX[0] - 0), (pointsY[0] - 0), 4, 4);
        rect2 = new Rectangle2D.Double((pointsX[1] - 4), (pointsY[1] - 0), 4, 4);
        rect3 = new Rectangle2D.Double((pointsX[2] - 4), (pointsY[2] - 4), 4, 4);
        rect4 = new Rectangle2D.Double((pointsX[3] - 0), (pointsY[3] - 4), 4, 4);
        
        centerPoint = new Ellipse2D.Float(centerX - 10, centerY - 10, 20, 20);
        g2.setStroke(points);
        
        //draw outlines
        g2.setColor(Color.BLACK);
        g2.draw(rect1);
        g2.draw(rect2);
        g2.draw(rect3);
        g2.draw(rect4);
        g2.draw(centerPoint);
        
        if (selectedCoordinate.equals(nw)) {
            g2.fill(rect2);
            g2.fill(rect3);
            g2.fill(rect4);
            
            g2.setColor(Color.WHITE);
            g2.fill(rect1);
        } else if (selectedCoordinate.equals(ne)) {
            g2.fill(rect1);
            g2.fill(rect3);
            g2.fill(rect4);
            
            g2.setColor(Color.WHITE);
            g2.fill(rect2);            
        } else if (selectedCoordinate.equals(se)) {
            g2.fill(rect1);
            g2.fill(rect2);
            g2.fill(rect4);
            
            g2.setColor(Color.WHITE);
            g2.fill(rect3);        
        } else if (selectedCoordinate.equals(sw)) {
            g2.fill(rect1);
            g2.fill(rect2);
            g2.fill(rect3);
            
            g2.setColor(Color.WHITE);
            g2.fill(rect4);   
        } else if (selectedCoordinate.equals(center)) {
            g2.fill(rect1);
            g2.fill(rect2);
            g2.fill(rect3);        
            g2.fill(rect4);   
            
            g2.setStroke(centerOutline);
            g2.setColor(new Color(255,255,255,128));
            g2.draw(centerPoint); 
            
            g2.setStroke(centerStroke);
            g2.setColor(new Color(68,68,68,128));
            g2.draw(centerPoint);             
        }
        
    }
    
    /**
     * Returns if this ImageOverlay is equal to another object.
     * 
     * @param o
     * @return 
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof ImageOverlay) {
            ImageOverlay io = (ImageOverlay) o;
            
            return (this.hashCode() == io.hashCode());
        } else {
            return false;
        }
    }

    /**
     * Generates a hashcode for this ImageOverlay.
     * 
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.ne != null ? this.ne.hashCode() : 0);
        hash = 89 * hash + (this.nw != null ? this.nw.hashCode() : 0);
        hash = 89 * hash + (this.se != null ? this.se.hashCode() : 0);
        hash = 89 * hash + (this.sw != null ? this.sw.hashCode() : 0);
        hash = 89 * hash + (this.mapIcon != null ? this.mapIcon.hashCode() : 0);
        return hash;
    }
    
    /**
     * Returns the boundary for this ImagEOverlay.
     * 
     * @return 
     */
    public LatLonAltBox getBounds() {
        return new LatLonAltBox((float) nw.getLatitude(), (float) sw.getLatitude(), (float) ne.getLongitude(), (float) sw.getLongitude(), -1, -1);
    }
    
    /**
     * Creates a CoordinateList with the four corners of this object as the Coordinates.
     * 
     * @return 
     */
    @Override
    public CoordinateList<Coordinate> getCoordinateList() {
        CoordinateList<Coordinate>  cList = new CoordinateList<Coordinate>();
        
        cList.add(nw);
        cList.add(ne);
        cList.add(se);
        cList.add(sw);
        cList.add(center);
        
        return cList;
    }    
        
    /**
     * Returns the center of this ImageOverlay as a Coordinate.
     * 
     * @return 
     */
    protected Coordinate getCenter() {
        Coordinate c;
        float      centerLat, centerLon;
                     
        try {
            centerLat = (float) sw.getLatitude()  + (getHeight() / 2.0f);
            centerLon = (float) sw.getLongitude() + (getWidth()  / 2.0f); 
            return new Coordinate(0, centerLat, centerLon);  
        } catch (Exception e) {
            System.err.println("Error in ImageOverlay.getCenter() - " + e);
            return Coordinate.UNKNOWN_COORDINATE;
        }
    }    
    
    /**
     * Returns the Description of this object.  For Image Overlay a blank string is always returned.
     * @return 
     */
    @Override
    public String getDescription() {
        return "";
    }        
    
    /**
     * Returns the height in Latitude degrees of this Overlay.
     * 
     * @return 
     */
    private float getHeight() {
        if ((nw.getLatitude() > 0) && (sw.getLatitude() >= 0)) {
            return (float) nw.getLatitude() - (float) sw.getLatitude();
        } else if ((nw.getLatitude() > 0) && (sw.getLatitude() < 0)) {
            return (float) nw.getLatitude() + Math.abs((float) sw.getLatitude());
        } else if ((nw.getLatitude() < 0) && (sw.getLatitude() < 0)) {
            return Math.abs((float) sw.getLatitude()) - Math.abs((float) nw.getLatitude());
        } else {
            return 0;
        }
    }     
    
    /**
     * Returns the MapIcon being used by this ImageOverlay.
     * 
     * @return 
     */
    public MapIcon getMapIcon() {
        return this.mapIcon;
    }
    
    /**
     * Returns the name/ID for htis ImageOverlay.
     * 
     * @return 
     */
    @Override
    public String getName() {
        return this.overlayID;
    }
           
    /**
     * Returns the parent Layer that contains this ImageOverlay.
     * 
     * @return 
     */
    @Override
    public final Layer getParentLayer() {
        return parentLayer;
    }

    /**
     * Returns which Coordinate is selected.
     * 
     * @return 
     */
    @Override
    public Coordinate getSelectedCoordinate() {
        return this.selectedCoordinate;
    }     
    
    /**
     * Returns the Visibility for this object.
     * 
     * @return 
     */
    @Override
    public Visibility  getVisibility() {
        return null;
        //TODO implement Visibility in ImageOverlay.
    }
    
    
    /**
     * Returns the width in Longitude Degrees of this Overlay.
     * 
     * @return 
     */
    public float getWidth() {
        if ((nw.getLongitude() >= 0) && (ne.getLongitude() >= 0)) {
            return (float) ne.getLongitude() - (float) nw.getLongitude();
        } else if ((nw.getLongitude() < 0) && (ne.getLongitude() > 0)) {
            return Math.abs((float) nw.getLongitude()) + (float) ne.getLongitude();
        } else if ((nw.getLongitude() > 0) && (ne.getLongitude() < 0)) {
            return (180 - (float) nw.getLongitude()) + (180 - Math.abs((float) ne.getLongitude()));
        } else if ((nw.getLongitude() < 0) && (ne.getLongitude() < 0)) {
            return Math.abs((float) ne.getLongitude() - (float) nw.getLongitude());
        } else {
            return 0;
        }
    }    
    
    /**
     * Returns if this object is Highlighted.
     * 
     * @return 
     */
    @Override
    public boolean isHighlighted() {
        return highlighted;
    }    
    
    /**
     * Returns if this object is contained by, or overlaps another range.
     * 
     * @param range
     * @return 
     */
    @Override
    public boolean isObjectWithinRectangle(Rectangle2D range) {
        if (screenBounds != null) {
            return screenBounds.contains(range);
        } else {
            return false;
        }
    }
    
    /**
     * Sets the boundary of this ImageOverlay.
     * 
     * @param bounds 
     */
    public final void setBoundary(LatLonAltBox bounds) {
        if (bounds != null) {
            center  = bounds.getCenter();
            nw      = bounds.getNorthWestCoordinate();
            ne      = bounds.getNorthEastCoordinate();
            sw      = bounds.getSouthWestCoordinate();
            se      = bounds.getSouthEastCoordinate();
            
            //set backups to detect coordinate change
            centerB = center.copy();
            nwB     = nw.copy();
            neB     = ne.copy();
            swB     = sw.copy();
            seB     = se.copy();         
        }
    }
    
    /**
     * Sets the description for this object.  However for ImageOverlay this is ignored.
     * 
     * @param description 
     */
    @Override
    public void setDescription(String description) {        
    }    
    
    public void setDimension(Dimension d) {
        if (lastMapView != null) {
            
        } else {
            System.err.println("Error in ImageOverlay.setDimension(Dimension) - MapView not set, cannot change dimensions.");            
        }
    }
    
    /**
     * Sets if this object is highlighted or not.
     * 
     * @param h 
     */
    @Override
    public void setHighlighted(boolean h) {
        this.highlighted = h;
    }    
    
    /**
     * Sets the last MapView used to draw this object.
     * 
     * @param mapView 
     */
    public void setLastMapView(MapView mapView) {
        this.lastMapView = mapView;
    }
    
    /**
     * Sets the Icon to use when drawing this object.
     * 
     * @param mapIcon 
     */
    public void setMapIcon(MapIcon mapIcon) {
        this.mapIcon = mapIcon;
    }
    
    /**
     * Sets the Name/ID of this object.
     * 
     * @param name 
     */
    @Override
    public void setName(String name) {
        this.overlayID = name;
    }    
    
    /**
     * Sets the ParentLayer containing this object.
     * 
     * @param parentLayer 
     */
    @Override
    public void setParentLayer(Layer parentLayer) {
        this.parentLayer = parentLayer;        
    }         
        
    /**
     * Sets the selected Coordinate of this Object.
     * 
     * @param selectedCoordinate 
     */
    @Override
    public void setSelectedCoordinate(Coordinate selectedCoordinate) {
        this.selectedCoordinate = selectedCoordinate;
    }    
    
    /**
     * Sets the visibility for this ImageOverlay.
     * 
     * @param v 
     */
    public void setVisibility(Visibility v) {
        //TODO implement this.
    }
    
    /**
     * Exports this ImageOverlay to FmXML.
     * 
     * @param kmlWriter 
     */
    @Override
    public void toXML(XmlOutput xmlWriter) {
        LatLonAltBox bounds = getBounds();
        
        xmlWriter.openTag("GroundOverlay id=\"" + overlayID + "\"");
        
        mapIcon.toXML(xmlWriter);
        
        xmlWriter.openTag("LatLonBox");
        
        xmlWriter.writeTag("north",     Float.toString(bounds.getNorth()));
        xmlWriter.writeTag("south",     Float.toString(bounds.getSouth()));
        xmlWriter.writeTag("east",      Float.toString(bounds.getEast()));
        xmlWriter.writeTag("west",      Float.toString(bounds.getWest()));
        xmlWriter.writeTag("rotation",  "0");
        
        xmlWriter.closeTag("LatLonBox");
        
        xmlWriter.closeTag("GroundOverlay");
    }

    /**
     * Unselects the selected Coordinate.
     * 
     */
    @Override
    public void unselectCoordinate() {
        this.selectedCoordinate = Coordinate.UNKNOWN_COORDINATE;
    }
    
}
