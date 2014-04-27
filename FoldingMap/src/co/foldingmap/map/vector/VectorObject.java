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
import co.foldingmap.map.MapObject;
import co.foldingmap.map.Visibility;
import co.foldingmap.map.ObjectNotWithinBoundsException;
import co.foldingmap.map.Layer;
import co.foldingmap.Logger;
import co.foldingmap.dataStructures.PropertyValuePair;
import co.foldingmap.map.themes.ColorStyle;
import co.foldingmap.map.themes.MapTheme;
import co.foldingmap.xml.XmlOutput;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * VectorObject is the Parent Object for all of the different types
 * of possible objects, MapPoint, Polygon, LineString, LinearRing, MultiGeometry 
 * 
 * @author Alec
 */
public abstract class VectorObject implements MapObject {
    public static final byte    CLAMP_TO_GROUND    = 1;
    public static final byte    RELATIVE_TO_GROUND = 2;
    public static final byte    ABSOLUTE           = 3;    
    
    //Listeners
    protected ArrayList<ChangeObjectClassListener> classChangeListeners;
    
    protected BasicStroke                lineStyle;
    protected boolean                    highlighted;
    protected byte                       altitudeMode;
    protected Coordinate                 selectedCoordinate;
    protected HashMap<String, String>    customDataFields;
    protected Layer                      parentLayer;
    protected LatLonAltBox               boundingBox;
    protected long                       reference, timestamp;
    protected String                     objectDescription, objectName, objectClass;
    protected CoordinateList<Coordinate> coordinates;    
    protected Visibility                 visibility;
    
    //abstract methods        
    public abstract void            drawOutline(Graphics2D g2, MapView mapView);
    public abstract VectorObject    fitToBoundry(LatLonAltBox boundry) throws ObjectNotWithinBoundsException;
    public abstract Coordinate      getCoordinateWithinRectangle(Rectangle2D range);    
    public abstract void            toXML(XmlOutput kmlWriter);    
    
    /**
     * Constructor to setup the default object settings, may or may not be 
     * called by children.
     *
     */
    public VectorObject() {
        //Default Settings;
        coordinates             = new CoordinateList();
        customDataFields        = new HashMap<String, String>(); 
        selectedCoordinate      = Coordinate.UNKNOWN_COORDINATE;      
        reference               = 0;
        
        //initiate listener lists
        classChangeListeners = new ArrayList<ChangeObjectClassListener>(3);
    }    
    
    /**
     * Adds a ChageObjectClassListener to this issue.
     * 
     * @param listener 
     */
    public void addChangeObjectClassListener(ChangeObjectClassListener listener) {
        classChangeListeners.add(listener);
    }
    
    /**
     * Adds a custom data field to the object like City_population
     * 
     * @param pvp   The Property Value pair with the info.
     */
    public void addCustomDataField(PropertyValuePair pvp) {
        customDataFields.put(pvp.getProperty(), pvp.getValue());
    }
    
    /**
     * Adds a custom data field to the object like City_population
     *
     * @param   String      The field name.
     * @param   String      The field value.
     */
    public void addCustomDataField(String field, String value) {
        //remove old value
        customDataFields.remove(field);

        //insert new value
        customDataFields.put(field, value);
    }    
    
    /**
     * Adds Custom Data Fields to the object.
     * 
     * @param customDataFields An ArrayList of PropertyValuePairs to set as custom data fields.                           
     */
    public void addCustomDataFields(ArrayList<PropertyValuePair> customDataFields) {        
        for (PropertyValuePair pvp: customDataFields)             
            this.customDataFields.put(pvp.getProperty(), pvp.getValue());        
    }       
    
    /**
     * Adds a coordinate to the end of the coordinate list.
     * 
     * @param c 
     */
    public void appendCoordinate(Coordinate c) {
        coordinates.add(c);
        generateBoundingBox();
    }    
        
    /**
     * Returns if this Vector Object is Equal to another object.
     * 
     * @param o
     * @return 
     */
    @Override
    public boolean equals(Object o) {
        try {
            if (o instanceof VectorObject) {
                VectorObject vo = (VectorObject) o;
                
                if (this.getClass() == vo.getClass()) {
                    return (this.hashCode() == vo.hashCode());
                } else { 
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in VectorObject.equals(Object) - " + e);
            return false;
        }
    }

    /**
     * Generates hash code for this object.
     * 
     * @return 
     */    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 2  * hash + this.altitudeMode;
//        hash = 89 * hash + (this.customDataFields != null ? this.customDataFields.hashCode() : 0);
        hash = 4  * hash + (this.objectDescription != null ? this.objectDescription.hashCode() : 0);
        hash = 6  * hash + (this.objectName != null ? this.objectName.hashCode() : 0);
        hash = 8  * hash + (this.objectClass != null ? this.objectClass.hashCode() : 0);
        hash = 10 * hash + (this.coordinates != null ? this.coordinates.hashCode() : 0);
        
        return hash;
    }
    
    /**
     * Creates a box that gives the bounds of the coordinates of this object. 
     */
    public void generateBoundingBox() {
        float north         =  -90;
        float south         =   90;
        float east          = -180;
        float west          =  180;
        float minAltitude   =    0;
        float maxAltitude   =    0;
                 
        for (Coordinate c: coordinates) {
            //East
            if (c.getLongitude() > east)
                east = (float) c.getLongitude();                        
            
            //North
            if (c.getLatitude() > north)
                north = (float) c.getLatitude(); 
            
            //south
            if (c.getLatitude() < south)
                south = (float) c.getLatitude();  
            
            //west
            if (c.getLongitude() < west)
                west = (float) c.getLongitude();
            
            //Min Alt
            if (c.getAltitude() < minAltitude)
                minAltitude = c.getAltitude();   
            
            //Max Alt
            if (c.getAltitude() > minAltitude)
                minAltitude = c.getAltitude();            
        }        
        
        this.boundingBox = new LatLonAltBox(north, south, east, west, minAltitude, maxAltitude);
    }    
    
    /**
     * Returns an ArrayList of at the custom data associated with this object.
     * 
     * @return 
     */
    public ArrayList<PropertyValuePair> getAllCustomData() {
        ArrayList<PropertyValuePair> dataPairs;
        Iterator                     it;
        PropertyValuePair            currentDataPair;
        Set                          set;

        dataPairs = new ArrayList<PropertyValuePair>();
        set       = customDataFields.entrySet();
        it        = set.iterator();

        while (it.hasNext()) {
          Map.Entry entry = (Map.Entry) it.next();
          currentDataPair = new PropertyValuePair((String) entry.getKey(), (String) entry.getValue());
          
          dataPairs.add(currentDataPair);
        }

        return dataPairs;
    }    
    
    /**
     * Gets all the custom data field names associated with this object.
     *
     * @return  Vector<String>  A Vector containing all of the Custom Field Names.
     */
    public ArrayList<String> getAllCustomDataFields() {
        Set                set     = customDataFields.entrySet();
        Iterator           it      = set.iterator();
        ArrayList<String>  fields  = new ArrayList<String>();

        while (it.hasNext()) {
          Map.Entry entry = (Map.Entry) it.next();
          fields.add((String) entry.getKey());
        }

        return fields;
    }

    /**
     * Returns the altitudeMode for this object.
     * Possible values are CLAMP_TO_GROUND, RELATIVE_TO_GROUND, ABSOLUTE
     * 
     * @return 
     */
    public byte getAltitudeMode() {
        return altitudeMode;
    }    
    
    /**
     * Returns this objects bounding box.
     * 
     * @return Bounding box as a LAtLonAltBox
     */
    public LatLonAltBox getBoundingBox() {
        if (this.boundingBox == null)
            generateBoundingBox();
        
        return this.boundingBox;
    }            
    
    /**
     * Returns the Coordinates with screen points within the given Rectangle2D.
     * 
     * @param range
     * @return 
     */
    public CoordinateList<Coordinate> getCoordinatesInRange(Rectangle2D range) {
        CoordinateList<Coordinate> returnCoordates;
        
        returnCoordates = new CoordinateList<Coordinate>();
        
        for (Coordinate c: this.coordinates) {            
            if (range.contains(c.getCenterPoint()) ||
                range.contains(c.getLeftPoint())   ||
                range.contains(c.getRightPoint())) {
                
                returnCoordates.add(c);       
            } 
        }
        
        return returnCoordates;
    }    
    
    /**
     * Returns this VectorObject's CoordinateList
     * 
     * @return 
     */
    @Override
    public CoordinateList<Coordinate> getCoordinateList() {
        return coordinates;
    }    
    
    /**
     * Returns this VectorObject's Coordinates as a String of the form:
     * longitude,latitude,altitude longitude,latitude,altitude
     * 
     * @return 
     */
    public String getCoordinateString() {
        return coordinates.getCoordinateString();
    }    
    
    /**
     * Returns the value for a specified custom field name
     *
     * @param   String  The field name for the associated value.
     * @return  String  The value for the passed in fieldName.
     */
    public String getCustomDataFieldValue(String fieldName) {
        String value;

        if (fieldName.equalsIgnoreCase("Altitude")) {
            value = Double.toString(coordinates.get(0).getAltitude());
        } else {
            value = customDataFields.get(fieldName);
        }

        return value;
    }    
    
    /**
     * Returns a HashMap of all the CustomeDataFields
     *
     * @return  HashMap<String, String>   The custom data fields.
     */
    public HashMap<String, String> getCustomDataFields() {
        return customDataFields;
    }
    
    /**
     * Returns this VectorObject's description.
     * 
     * @return 
     */
    @Override
    public String getDescription() {
        if (objectDescription != null) {
            if (objectDescription.equalsIgnoreCase("null")) {
                return "";
            } else {
                return objectDescription;
            }
        } else {
            return "";
        }
    }    
    
    /**
     * Returns the distance from the closest coordinate in the object to the 
     * one provided.
     * 
     * @param  c The Coordinate to compare. 
     * @return   The distance from the closest Coordinate and the compare Coordinate.
     */
    public double getDistanceFrom(Coordinate c) {
        Coordinate  closestCoordinate;
        double      distance;

        distance = Double.MAX_VALUE;

        try {
            closestCoordinate = coordinates.getCoordinateClosestTo(c);
            distance = CoordinateMath.getDistance(closestCoordinate, c);
        } catch (Exception e) {
            System.err.println("Error in MapObject.getDistanceFrom(Coordinate) - " + e);
        }

        return distance;
    }    
    
    /**
     * Returns all of this VectorObject's Coordinates as MapPoints.
     * Used in Merging and un-merging operations.
     * 
     * @return 
     */
    public VectorObjectList getMapPoints() {    
        VectorObjectList       mapPoints;
        Coordinate          currentCoordinate;
        MapPoint            currentMapPoint;

        mapPoints = new VectorObjectList();
        
        for (int i = 0; i < coordinates.size(); i++) {        
            currentCoordinate = coordinates.get(i);
            currentMapPoint   = new MapPoint((objectName + " " + i), "(Unspecified Point)", "", currentCoordinate);
            mapPoints.add(currentMapPoint);
        }

        return mapPoints;
    }    
    
    /**
     * Returns this Objects Class.
     * Example: Highway, Cafe, Lake
     * 
     * @return 
     */
    public String getObjectClass() {
        return this.objectClass;
    }    
    
    /**
     * Returns this Objects Parent Layer
     * 
     * @return 
     */
    @Override
    public Layer getParentLayer() {
        return parentLayer;
    }    
    
    /**
     * Returns the shape to be used for drawing component coordinates in 
     * Vector Objects.  
     * 
     * @param c     The component Coordinate
     * @param wrap  The map wrap to be used, from MapView Class
     * @return 
     */
    public static Shape getPointShape(Coordinate c, int wrap) {
        Shape pointShape = null;
        
        try {
            if (wrap == MapView.WRAP_LEFT) {
                pointShape = new Rectangle2D.Double((c.leftPoint.getX() - 1), (c.leftPoint.getY() - 1), 2, 2 );
            } else if (wrap == MapView.WRAP_RIGHT) {    
                pointShape = new Rectangle2D.Double((c.rightPoint.getX() - 1), (c.rightPoint.getY() - 1), 2, 2 );
            } else {
                //default to center point
                pointShape = new Rectangle2D.Double((c.centerPoint.getX() - 1), (c.centerPoint.getY() - 1), 2, 2 );
            }
        } catch (Exception e) {
            System.err.println("Error in VectorObject.getPointShape(Coordiante, int) - " + e);
        }
        
        return pointShape;
    }
    
    /**
     * Returns the object's description with any variables evaluated, this is used for showing 
     * popup content.
     * 
     * @return 
     */
    public String getPopupDescription() {
        //TODO: Add code to replace variables

        String  desc;
        String  replace = "\\" + "\"";
        
        if (objectDescription != null) {
            //Escape quotes and remove newlines and tabs
            desc = objectDescription.replace("\"", replace);        
            desc = desc.replace("\n", "");
            desc = desc.replace("\t", "");
        } else {
            desc = "";
        }
        
        return desc;
    }
    
    /**
     * Returns the object reference for this object.
     * @return 
     */
    public long getReference() {
        return this.reference;
    }      
    
    /**
     * Returns this Objects Name.
     * 
     * @return 
     */
    @Override
    public String getName() {    
        if (objectName != null) {
            return objectName;
        } else {
            return "";
        }
    }    
    
    /**
     * Returns which coordinate, if any is selected within this map object.
     * If no object is selected a null is returned.
     *
     * @return Coordinate  The selected coordinate within this object.
     */
    @Override
    public Coordinate getSelectedCoordinate() {
        return selectedCoordinate;
    }    
    
    /**
     * Returns this objects timestamp.
     * 
     * @return The timestamp in yyyy-mm-ddThh:mm:ssZ format
     */
    public String getTimestamp() {             
        SimpleDateFormat timestampDateFormat;
                
        timestampDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss");
        timestampDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
                
        return timestampDateFormat.format(timestamp)  + "Z";       
    }    
    
    /**
     * Returns this Objects Visibility if it has one.
     * 
     * @return 
     */
    public Visibility getVisibility() {        
        return visibility;
    }      
    
    /**
     * Tests to see if the given text has displayable text or just HTML
     * 
     * @param text
     * @return 
     */
    public static boolean hasDisplayableText(String text) {
        boolean returnValue = true;

        if (text.trim().equals("")) {
            returnValue = false;
        } else if (text.equalsIgnoreCase("<body></body>")) {
            returnValue = false;
        } else if (text.equalsIgnoreCase("<body><p style=\"margin-top: 0\"></p style=\"margin-top: 0\"></body>")) {
            returnValue = false;
        }

        return returnValue;
    }    
    
    /**
     * Returns if this MapObjet is highlighted, i.e. Selected.
     * 
     * @return 
     */
    @Override
    public boolean isHighlighted() {
        return this.highlighted;
    }    
    
    /**
     * Returns if this object should be visible on the map with the current MapView.
     * 
     * @param mapView
     * @return 
     */
    public boolean isVisible(MapView mapView) {
        boolean visable = true;

        if (this.visibility != null) {
            visable = this.visibility.isVisible(mapView.getZoomLevel());        
        } else {
            //check for style visability
            ColorStyle cs = mapView.getMapTheme().getStyle(this.objectClass);
            
            if (cs != null) {
                Visibility v  = cs.getVisibility();

                if (v != null) {
                    return cs.getVisibility().isVisible(mapView.getZoomLevel());
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }

        return visable;
    }
    
    /**
     * Adds a coordinate to the beginning of the coordinate list
     * 
     * @param c 
     */
    public void prependCoordinate(Coordinate c) {
        coordinates.add(0, c);
        generateBoundingBox();
    }      
    
    /**
     * Removes a custom data field from this object.
     * 
     * @param field 
     */
    public void removeCustomDataField(String field) {
        this.customDataFields.remove(field);
    }
    
    /**
     * Sets the altitudeMode for this Object.
     * 
     * @param altitudeMode 
     */
    public void setAltitudeMode(byte altitudeMode) {
        this.altitudeMode = altitudeMode;
    }    
    
    /**
     * Sets the class associated with this object
     */
    public void setClass(String objectClass) {
        String oldClass = this.objectClass;
        
        this.objectClass = objectClass;
        
        //Call Listeners, if there are any
        if (classChangeListeners != null) {
            for (ChangeObjectClassListener listener: classChangeListeners) 
                listener.objectClassChanged(this, oldClass, objectClass);            
        }
    }    
    
    /**
     * Sets this Objects CoordinateList.  The Coordinate list is what keeps 
     * track of the objects Latitude, Longitude and Altitude Coordinates.
     * 
     * @param newCoordinateList 
     */
    public void setCoordinateList(CoordinateList<Coordinate> newCoordinateList) {
        this.coordinates = newCoordinateList;
        generateBoundingBox();
    }    
        
    /**
     * Adds a custom data field to the object like City_population
     *
     * @param   String      The field name.
     * @param   String      The field value.
     */
    public void setCustomDataField(String field, String value) {
        if (!field.equalsIgnoreCase("Name")) {
            //remove old value
            customDataFields.remove(field);

            //insert new value
            customDataFields.put(field, value);
        } else {
            this.setName(value);
        }
    }    
    
    /**
     * Sets the Custom Data Field HashMap for this VectorObject.
     * 
     * @param customDataFields 
     */
    public void setCustomDataFields(HashMap<String, String> customDataFields) {
        this.customDataFields = customDataFields;
    }
 
    /**
     * Sets this VectorObject's description
     * 
     * @param description 
     */
    @Override
    public void setDescription(String description) {
        if (description.startsWith("<![CDATA[") ) {
            //removes the cdata tag
            this.objectDescription = description.substring(9, (description.length() - 3));
        } else {
            this.objectDescription = description;
        }
    }    
    
    /**
     * Sets if this VectorObject is highlighted i.e. selected.
     * 
     * @param h 
     */
    @Override
    public void setHighlighted(boolean h) {
        this.highlighted = h;
    }    
    
    /**
     * Sets this VectorObject's name.
     * 
     * @param name 
     */
    @Override
    public void setName(String name) {
        this.objectName = name;
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
    }    
    
    /**
     * Sets the long value that can be used to reference an object.
     * 
     * @param reference 
     */
    public void setReference(long reference) {
        this.reference = reference;
    }      
    
    /**
     * Sets the selected coordinate within the VectorObject
     *
     */
    @Override
    public void setSelectedCoordinate(Coordinate selectedCoordinate) {
        if (coordinates.contains(selectedCoordinate)) {
            this.selectedCoordinate = selectedCoordinate;
        } else {
            this.selectedCoordinate = coordinates.getCoordinateClosestTo(selectedCoordinate);
        }
    }    
    
    /**
     * Sets the Timestamps for all of the coordinates in this object
     *
     * @param timestamps    A string of space delimited time stamps in the form: yyyy-MM-dd'T'HH:mm:ss
     */
    public void setTimestamps(String timestamps) {
        StringTokenizer timestampTokenizer;

        timestampTokenizer = new StringTokenizer(timestamps);

        for (Coordinate currentCoordinate: coordinates) {
            if (timestampTokenizer.hasMoreTokens()) {
                currentCoordinate.setTimestamp(timestampTokenizer.nextToken());
            } else {
                //timestamp coordinate missmatch
                System.err.println("Error in MapObject.setTimestamps(String) - Coordinate - Timestamp Missmatch");
            }
        }
    }    
    
    /**
     * Sets a Region for this VectorObject.
     * 
     * @param region 
     */
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;;
    }      
    
    /**
     * Un-selects the selected coordinate from this VectorObject
     */
    @Override
    public void unselectCoordinate() {
        selectedCoordinate = null;
    }        
    
    /**
     * Updates the drawing of outlines, not used in all objects.
     * 
     * @param theme 
     */
    public void updateOutlines(MapTheme theme) {       
        
    }
    
    /**
     * Writes all of the custom data fields as XML to the KMLWriter
     *
     * @param kmlWriter The writer to write xml to.
     */
    public void writeCustomDataFieldsAsXML(XmlOutput xmlBuffer) {
        Set             set = customDataFields.entrySet();
        Iterator        it  = set.iterator();

        if (it.hasNext()) {        
            xmlBuffer.openTag("data");

            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                xmlBuffer.writePairTag((String)entry.getKey(), (String) entry.getValue());
            }

            xmlBuffer.closeTag("data");
        }
    }    
}
