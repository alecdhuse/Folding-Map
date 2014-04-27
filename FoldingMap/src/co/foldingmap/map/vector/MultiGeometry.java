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

import co.foldingmap.map.Layer;
import co.foldingmap.map.MapView;
import co.foldingmap.map.ObjectNotWithinBoundsException;
import co.foldingmap.map.themes.ColorStyle;
import co.foldingmap.xml.XmlOutput;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Alec
 */
public class MultiGeometry extends VectorObject {
    protected VectorObjectList<VectorObject>  mapObjects;
    
    /**
     * Creates a new MultiGeometry with the supplied name and no objects.
     * 
     * @param name 
     */
    public MultiGeometry(String name) {
        this.objectName = name;
        this.mapObjects = new VectorObjectList<VectorObject>();
    }    
    
    /**
     * Creates a new MultiGeometry with the supplied name and objects.
     * 
     * @param name
     * @param mapObjects 
     */
    public MultiGeometry(String name, VectorObjectList<VectorObject> mapObjects) {
        try {
            this.mapObjects = new VectorObjectList<VectorObject>();

            this.mapObjects.addAll(mapObjects.getPolygons());
            this.mapObjects.addAll(mapObjects.getLineStrings());
            this.mapObjects.addAll(mapObjects.getMapPoints());

            this.objectName = name;

            for (int i = 0; i < mapObjects.size(); i++) {
                VectorObject currentObject = mapObjects.get(i);
                currentObject.setParentLayer(this.getParentLayer());
            }
        } catch (Exception e) {
            System.err.println("Error in MultiGeometry.constructor(Stirng, VectorObjectList) - " + e);
        }
    }    
    
    /**
     * Adds an object to this MultiGeometry.
     * 
     * @param newObject 
     */
    public void addObject(VectorObject newObject) {
        this.mapObjects.add(newObject);
        newObject.setParentLayer(this.getParentLayer());
        generateBoundingBox();
    }    
    
    @Override
    public VectorObject copy() {
        VectorObjectList mapObjectsCopy = new VectorObjectList<VectorObject>();

        for (int i = 0; i < mapObjects.size(); i++) {
            VectorObject currentMapObject = mapObjects.get(i);
            mapObjectsCopy.add(currentMapObject.copy());
        }

        return new MultiGeometry(objectName, mapObjectsCopy);
    }

    /**
     * Draws all objects in this MultiGeometry.
     * 
     * @param g2
     * @param mapView 
     * @param colorStyle
     */
    @Override
    public void drawObject(Graphics2D g2, MapView mapView, ColorStyle colorStyle) {
        boolean drawObject;
            
        try {
            //check to see if we need to draw the object
            if (mapView.displayAll()) {
                drawObject = true;
            } else {
                drawObject = this.isVisible(mapView);
            }

            if (drawObject) {                                      
                for (int i = 0; i < mapObjects.size(); i++) {
                    LineString currentLineString;
                    VectorObject  currentMapObject = mapObjects.get(i);
                    Polygon    currentPolygon;

                    if (currentMapObject != null) {
                        if (currentMapObject instanceof LineString) {
                            currentLineString = (LineString) currentMapObject;
                            currentLineString.drawOutline(g2, mapView);
                        } else if (currentMapObject instanceof Polygon) {
                            currentPolygon = (Polygon) currentMapObject;
                            currentPolygon.drawOutline(g2, mapView);
                        }

                        currentMapObject.drawObject(g2, mapView, colorStyle);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error in MultiGeometry.drawObject(Graphics2D, MapView) - " + e);
        }
    }

    /**
     * Draws the outline for this LineString.
     * 
     * @param g2
     * @param mapView 
     */
    @Override
    public void drawOutline(Graphics2D g2, MapView mapView) {
        
        for (VectorObject obj: this.mapObjects) {
            obj.drawOutline(g2, mapView);
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
        for (VectorObject object: mapObjects) {
            object.drawPoints(g2, mapView);
        }
    }    
    
    /**
     * Returns if this MultiGeometry is equal to another object.
     * 
     * @param o
     * @return 
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof MultiGeometry) {
            MultiGeometry mg = (MultiGeometry) o;
            
            return (this.hashCode() == mg.hashCode());
        } else {
            return false;
        }
    }

    /**
     * Creates a hask code for this Object.
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 3;
        
        for (VectorObject object: this.mapObjects) 
            hash += (object != null ? object.hashCode() : 0);
        
        return hash;
    }
    
    /**
     * Fits all objects in this MultiGeometry to a boundary.
     * 
     * @param boundry
     * @return
     * @throws ObjectNotWithinBoundsException 
     */
    @Override
    public VectorObject fitToBoundry(LatLonAltBox boundry) throws ObjectNotWithinBoundsException {
        VectorObjectList<VectorObject> fittedObjects;
        MultiGeometry            fittedObject;

        fittedObject  = new MultiGeometry(this.getName());
        fittedObjects = new VectorObjectList<VectorObject>();
        
        for (int i = 0; i < mapObjects.size(); i++) {
            VectorObject currentMapObject = mapObjects.get(i);
            fittedObjects.add(currentMapObject.fitToBoundry(boundry));
        }

        mapObjects = fittedObjects;

        return fittedObject;
    }    
    
    /**
     * Creates a box that gives the bounds of the coordinates of this object.
     */
    @Override
    public void generateBoundingBox() {
        float           north, south, east, west, minAltitude, maxAltitude;
        LatLonAltBox    currentBox;

        north       = -90;
        south       =  90;
        east        = -180;
        west        =  180;
        minAltitude =  Float.MAX_VALUE;
        maxAltitude =  Float.MIN_VALUE;

        for (int i = 0; i < mapObjects.size(); i++) {
            VectorObject currentObject = mapObjects.get(i);
             currentBox = currentObject.getBoundingBox();

             if (currentBox.getNorth() > north)
                 north = currentBox.getNorth();

             if (currentBox.getSouth() < south)
                 south = currentBox.getSouth();

             if (currentBox.getEast() > east)
                 east = currentBox.getEast();

             if (currentBox.getWest() < west)
                 west = currentBox.getWest();

             if (currentBox.getMinAltitude() < minAltitude)
                 minAltitude = currentBox.getMinAltitude();

             if (currentBox.getMaxAltitude() > maxAltitude)
                 maxAltitude = currentBox.getMaxAltitude();
        }

        this.boundingBox = new LatLonAltBox(north, south, east, west, minAltitude, maxAltitude);
    }    
    
    /**
     * Returns a component object at the given index.
     * 
     * @param i
     * @return 
     */
    public VectorObject getComponentObject(int i) {
        return mapObjects.get(i);
    }    
    
    /**
     * Returns all the Component objects.
     *  
     * @return 
     */
    public VectorObjectList<VectorObject> getComponentObjects() {
        return mapObjects;
    }    
    
    /**
     * Returns all the coordinates of all contained objects.
     * 
     * @return 
     */
    @Override
    public CoordinateList<Coordinate> getCoordinateList() {
        CoordinateList<Coordinate>  allCoordinates;
        
        allCoordinates = new CoordinateList<Coordinate>();
        
        for (VectorObject object: mapObjects) {
            allCoordinates.addAll(object.getCoordinateList());
        }
        
        return allCoordinates;
    }
    
    /**
     * Returns a coordinate within the range.
     * 
     * @param range
     * @return 
     */
    @Override
    public Coordinate getCoordinateWithinRectangle(Rectangle2D range) {
        Coordinate returnCoordinate = null;

        for (int i = 0; i < mapObjects.size(); i++) {
            VectorObject currentMapObject = mapObjects.get(i);
            returnCoordinate = currentMapObject.getCoordinateWithinRectangle(range);
        }

        return returnCoordinate;
    }
    
    /**
     * Returns if this object only contains one Polygon and one MapPoint.
     * 
     * @return 
     */
    public boolean isIconPolygon() {
        boolean hasMapPoint, hasPolygon, result;

        hasMapPoint = false; 
        hasPolygon  = false;
        result      = false;

        if (mapObjects.size() == 2) {
            if ((mapObjects.get(0) instanceof MapPoint) || (mapObjects.get(1) instanceof MapPoint))
                hasMapPoint = true;

            if ((mapObjects.get(0) instanceof Polygon)  || (mapObjects.get(1) instanceof Polygon))
                hasPolygon = true;

            if (hasPolygon && hasMapPoint)
                result = true;
        }

        return result;
    }    
    
    /**
     * Returns if the object is within a given range.
     * 
     * @param range
     * @return 
     */
    @Override
    public boolean isObjectWithinRectangle(Rectangle2D range) {
        boolean result = false;

        for (int i = 0; i < mapObjects.size(); i++) {
            VectorObject currentMapObject = mapObjects.get(i);
            result = currentMapObject.isObjectWithinRectangle(range);

            if (result == true)
                break;
        }

        return result;
    }

    /**
     * Sets all Component objects as highlighted.
     * 
     * @param h 
     */
    @Override
    public void setHighlighted(boolean h) {
        for (int i = 0; i < mapObjects.size(); i++) {
            VectorObject currentMapObject = mapObjects.get(i);
            currentMapObject.setHighlighted(h);
        }
    }    
    
    /**
     * Sets the name for all the Component map objects.
     * 
     * @param name 
     */
    @Override
    public void setName(String name) {
        super.setName(name);

        for (int i = 0; i < mapObjects.size(); i++) {
            VectorObject currentMapObject = mapObjects.get(i);
            currentMapObject.setName(name);
        }
    }    
    
    /**
     * Sets this Object's Parent Layer.  This is usually called when adding a
     * VectorObject to a Layer Object.
     * 
     * @param parentLayer 
     */
    @Override
    public void setParentLayer(Layer parentLayer) {
        this.parentLayer = parentLayer;
        
        //Set the parent layer for all sub object to the same parent layer.
        for (VectorObject vo: this.mapObjects) {
            vo.setParentLayer(parentLayer);
        }
    }        
    
    /**
     * Writes this object to XML.
     * 
     * @param kmlWriter 
     */
    @Override
    public void toXML(XmlOutput xmlWriter) {
        try {

            xmlWriter.openTag ("MultiGeometry id=\"" + getName() + "\"");
            xmlWriter.writeTag("Ref",  Long.toString(getReference()));            
            xmlWriter.writeTag("Name", this.objectName);  
            
            if (this.visibility != null)
                visibility.toXML(xmlWriter);             
            
            if (hasDisplayableText(getDescription()) && !getDescription().equalsIgnoreCase("null"))
                xmlWriter.writeTag("description", "<![CDATA[" + getDescription() + "]]>");            
            
            xmlWriter.openTag("elements");
            
            for (int i = 0; i < mapObjects.size(); i++) {
                VectorObject currentMapObject = mapObjects.get(i);
                currentMapObject.toXML(xmlWriter);
            }

            xmlWriter.closeTag("elements");
            
            writeCustomDataFieldsAsXML(xmlWriter);
            
            xmlWriter.closeTag("MultiGeometry");
        } catch (Exception e) {
            System.err.println("Error in MultiGeometry.toXML(KmlWriter) Object: " + this.objectName + " - " + e);
        }
    }

    
}
