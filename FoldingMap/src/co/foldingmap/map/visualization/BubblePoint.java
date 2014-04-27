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
package co.foldingmap.map.visualization;

import co.foldingmap.map.vector.LinearRing;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.MapPoint;
import co.foldingmap.map.vector.LineString;
import co.foldingmap.map.vector.Polygon;
import co.foldingmap.Logger;
import co.foldingmap.dataStructures.PropertyValuePair;
import co.foldingmap.map.MapView;
import co.foldingmap.map.themes.ColorRamp;
import co.foldingmap.map.themes.ColorStyle;
import co.foldingmap.map.themes.MapTheme;
import co.foldingmap.xml.XmlOutput;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A visualization that displays a circle with color and/or area dependant on 
 * a dynamic value;
 * 
 * @author Alec
 */
public class BubblePoint extends MapPoint {
    private   boolean       changeSize;
    private   float         minValue, maxValue;
    private   int           maxRadius;
    private   String        colorRampID, colorVariable, sizeVariable;
    private   VectorObject  object;
            
    public BubblePoint(VectorObject object, 
                       String       colorRampID, 
                       String       colorVariable, 
                       String       sizeVariable) {      
        
        this.changeSize    = true;
        this.colorRampID   = colorRampID;
        this.colorVariable = colorVariable;
        this.maxRadius     = 10;
        this.sizeVariable  = sizeVariable;
        this.minValue      = 4;
        this.maxValue      = 10;
        this.object        = object;
        
        if (object instanceof MapPoint) {
            this.coordinates.add(object.getCoordinateList().get(0));
        } else if (object instanceof LinearRing) {
            this.coordinates.add(object.getBoundingBox().getCenter());
        } else if (object instanceof LineString) {
            this.coordinates.add(object.getBoundingBox().getCenter());
        } else if (object instanceof Polygon) {
            this.coordinates.add(object.getBoundingBox().getCenter());
        } else {
            this.coordinates.add(object.getBoundingBox().getCenter());
        }
    }
    
    /**
     * Adds a custom data field to the object like City_population
     * 
     * @param pvp   The Property Value pair with the info.
     */
    @Override
    public void addCustomDataField(PropertyValuePair pvp) {
        object.addCustomDataField(pvp);
    }    
    
    /**
     * Adds a custom data field to the object like City_population
     *
     * @param   String      The field name.
     * @param   String      The field value.
     */
    @Override
    public void addCustomDataField(String field, String value) {
        object.addCustomDataField(field, value);
    }      
    
    /**
     * Adds Custom Data Fields to the object.
     * 
     * @param customDataFields An ArrayList of PropertyValuePairs to set as custom data fields.                           
     */
    @Override
    public void addCustomDataFields(ArrayList<PropertyValuePair> customDataFields) {        
        object.addCustomDataFields(customDataFields);
    }          
    
    /**
     * 
     * @param g2
     * @param mapView
     * @param colorStyle 
     */
    @Override
    public void drawObject(Graphics2D g2, MapView mapView, ColorStyle colorStyle) {
        Color       drawColor, outlineColor;
        float       x, y, radius;;              
        Point2D     pCenter;
        
        pCenter      = getCoordinateList().get(0).getCenterPoint();        
        radius       = getRadius();
        x            = (float) (pCenter.getX() - radius / 2.0);
        y            = (float) (pCenter.getY() - radius / 2.0);
        pointCenter  = new Ellipse2D.Float(x, y, radius, radius); //Center BubblePoint
        
        if (this.highlighted == false) {
            drawColor    = getColor(mapView.getMapTheme());
            outlineColor = drawColor.darker();
        } else {
            drawColor    = getColor(mapView.getMapTheme()).darker();
            outlineColor = new Color(68, 68, 68, 200);         
        }
        
        g2.setColor(outlineColor);
        g2.draw(pointCenter);        
        
        if (mapView.getMapProjection().isLeftShown()) {
            x           = (float) (pCenter.getX() - radius / 2.0);
            y           = (float) (pCenter.getY() - radius / 2.0);
            pointLeft   = new Ellipse2D.Float(x, y, radius, radius);    
            g2.draw(pointLeft);  
        }
        
        if (mapView.getMapProjection().isRightShown()) {
            x           = (float) (pCenter.getX() - radius / 2.0);
            y           = (float) (pCenter.getY() - radius / 2.0);
            pointRight  = new Ellipse2D.Float(x, y, radius, radius);    
            g2.draw(pointRight);  
        }        
        
        g2.setColor(drawColor);
        g2.fill(pointCenter);
        
        if (mapView.getMapProjection().isLeftShown())  g2.fill(pointLeft);
        if (mapView.getMapProjection().isRightShown()) g2.fill(pointRight);
    }    

    /**
     * Returns an ArrayList of at the custom data associated with this object.
     * 
     * @return 
     */
    @Override
    public ArrayList<PropertyValuePair> getAllCustomData() {
        return object.getAllCustomData();
    }      
    
    /**
     * Gets all the custom data field names associated with this object.
     *
     * @return  Vector<String>  A Vector containing all of the Custom Field Names.
     */
    @Override
    public ArrayList<String> getAllCustomDataFields() {
        return object.getAllCustomDataFields();
    }     
    
    /**
     * Returns the Color from the ColorRamp to be used for this BubblePoint.
     * 
     * @param mapTheme
     * @return 
     */
    public Color getColor(MapTheme mapTheme) {
        Color     color;
        ColorRamp colorRamp;
        String    value;
        
        colorRamp = mapTheme.getColorRamp(colorRampID);
        
        if (colorRamp != null) {
            value = object.getCustomDataFieldValue(colorVariable);
            color = colorRamp.getColor(value);
        } else {
            color = Color.RED;
        }
        
        return color;
    }       
    
    /**
     * Returns a HashMap of all the CustomeDataFields
     *
     * @return  HashMap<String, String>   The custom data fields.
     */
    @Override
    public HashMap<String, String> getCustomDataFields() {
        return object.getCustomDataFields();
    }    
    
    /**
     * Returns the value for a specified custom field name
     *
     * @param   String  The field name for the associated value.
     * @return  String  The value for the passed in fieldName.
     */
    @Override
    public String getCustomDataFieldValue(String fieldName) {
        return object.getCustomDataFieldValue(fieldName);
    }         
    
    /**
     * Returns this Objects Name.
     * 
     * @return 
     */
    @Override
    public String getName() {    
        return object.getName();
    }       
    
    /**
     * Returns the current radius of this bubble.
     * 
     * @return 
     */
    public float getRadius() {
        float  radius, range, value;
        
        try {
            if (changeSize) {                    
                value   = Float.parseFloat(object.getCustomDataFieldValue(sizeVariable));
                range   = maxValue - minValue;
                radius  = (value / range) * maxRadius;      
                radius += 2;
                //TODO: This should probably have a Square Root in it somwhere
            } else {
                radius = 4;
            }

            return radius;
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in BubblePoint.getRadius() - " + e);
            return 1;
        }
    }
    
    /**
     * Removes a custom data field from this object.
     * 
     * @param field 
     */
    @Override
    public void removeCustomDataField(String field) {
        object.removeCustomDataField(field);
    }    
    
    /**
     * Sets the variable to be used when deciding the color of the bubble.
     * 
     * @param colorVariable 
     */
    public void setColorVariable(String colorVariable) {
        this.colorVariable = colorVariable;
    }
    
    /**
     * Adds a custom data field to the object like City_population
     *
     * @param   String      The field name.
     * @param   String      The field value.
     */
    @Override
    public void setCustomDataField(String field, String value) {
        object.setCustomDataField(field, value);
    }     
    
    /**
     * Sets the Custom Data Field HashMap for this VectorObject.
     * 
     * @param customDataFields 
     */
    @Override
    public void setCustomDataFields(HashMap<String, String> customDataFields) {
        object.setCustomDataFields(customDataFields);
    }    
    
    /**
     * Sets the variable to be used when adjusting the size of the bubble.
     * 
     * @param sizeVariable 
     */
    public void setSizeVariable(String sizeVariable) {
        this.sizeVariable = sizeVariable;
    }
    
    @Override
    public void toXML(XmlOutput kmlWriter) {
            
    }
}
