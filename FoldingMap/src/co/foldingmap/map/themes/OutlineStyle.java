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
package co.foldingmap.map.themes;

import co.foldingmap.xml.XmlOutput;
import java.awt.Color;

/**
 *
 * @author Alec
 */
public class OutlineStyle extends ThemeConstants {
    
    protected Color  color, selectedColor;
    protected float  width;
    
    /* Conditions on when to draw this boarder.  
     * Example: 
     *  borderCondition = Land
     *  borderCondition = Water
     *  borderCondition = Country - Filled 
     *  borderCondition = Any     
     */      
    protected String borderCondition;
    
    protected String strokeStyle;
    
    /**
     * The default constructor, creates an outline style with these conditions:
     * 
     * Border Condition: ANY
     * Color:            Black
     * Selected Color:   Red
     * Stroke Style      Solid
     * Width             1
     */
    public OutlineStyle() {
        this.borderCondition = ThemeConstants.ANY;
        this.color           = Color.BLACK;
        this.selectedColor   = Color.RED;
        this.strokeStyle     = ThemeConstants.SOLID;
        this.width           = 1f;
    }
    
    /**
     * Creates an OutlineStyle with the given color and these conditions:
     * 
     * Border Condition: ANY
     * Selected Color:   Red
     * Stroke Style      Solid
     * Width             1
     */
    public OutlineStyle(Color outlineColor) {
        this.borderCondition = ThemeConstants.ANY;
        this.color           = outlineColor;
        this.selectedColor   = Color.RED;
        this.strokeStyle     = ThemeConstants.SOLID;
        this.width           = 1f;
    }    
    
    /**
     * Creates an OutlineStyle with the given color and border condition.
     * 
     * Other default parameters:
     *  Selected Color:   Red
     *  Stroke Style      Solid
     *  Width             1
     * 
     * @param outlineColor
     * @param borderCondition 
     */
    public OutlineStyle(Color outlineColor, String borderCondition) {
        this.borderCondition = borderCondition;
        this.color           = outlineColor;
        this.selectedColor   = Color.RED;
        this.strokeStyle     = ThemeConstants.SOLID;
        this.width           = 1f;
    }
    
    /**
     * Returns uf this OutlineStyle is equal to another object.
     * 
     * @param o
     * @return 
     */
    @Override
    public boolean equals(Object o) {
        boolean isEqual = false;
        
        if (o instanceof OutlineStyle) {
            OutlineStyle os = (OutlineStyle) o;
            
            if (os.getBorderCondition().equals(this.getBorderCondition()) &&
                os.getColor().equals(this.getColor()) &&
                os.getSelectedColor().equals(this.getSelectedColor()) &&
                os.getStrokeStyle().equals(this.getStrokeStyle()) &&
                os.getWidth() == this.getWidth()) {
                
                isEqual = true;
            }
        }
        
        return isEqual;
    }
    
    /**
     * Returns the border condition for this OutlineStyle.
     * 
     * @return 
     */
    public String getBorderCondition() {
        return borderCondition;
    }
    
    /**
     * Returns the Color used for this OutlineStyle.
     * 
     * @return 
     */
    public Color getColor() {
        return this.color;
    }
    
    /**
     * Returns the Color to be used for this style when selected.
     * 
     * @return 
     */
    public Color getSelectedColor() {
        return this.selectedColor;
    }
    
    /**
     * Returns the Stroke Style to be used by this OutlineStyle.
     * 
     * @return 
     */
    public String getStrokeStyle() {
        return this.strokeStyle;
    }
    
    /**
     * Returns the width of this OutlineStyle.
     * 
     * @return 
     */
    public float getWidth() {
        return this.width;
    }
    
    /**
     * Sets the condition on when to use this outline style.
     * The condition is the type of object (Class or Feature Type) the object
     * being styled by this style is bordering.
     * 
     * Any string is valid, but it should be either an Object Class from the
     * current theme or a a Feature Type from the ThemeConstants class.
     * 
     * The default is ThemeConstants.ANY where any bordering object will
     * satisfy the condition.
     * 
     * If there are more than one OutlineStyle for an object the conditions will
     * be processed in order, so any should be the last condition.
     * 
     * @param condition 
     */
    public void setBorderCondition(String condition) {
        this.borderCondition = condition;
    }
   
    /**
     * Sets the Color to be used for this OutlineStyle.
     * 
     * @param c 
     */
    public void setColor(Color c) {
        this.color = c;
    }
    
    /**
     * Sets the Color to be used when the object being styled by this class
     * is selected.
     * 
     * @param c 
     */
    public void setSelectedColor(Color c) {
        this.selectedColor = c;
    }
    
    /**
     * Sets the stroke style for this class.
     * Styles can be found in the ThemeConstants class. 
     * 
     * For Example:
     *      ThemeConstants.SOLID
     *      ThemeConstants.DASHED
     * 
     * @param style 
     */
    public void setStrokeStyle(String style) {
        this.strokeStyle = style;
    }
    
    /**
     * Sets the line for this OutlineStyle.
     * 
     * @param width 
     */
    public void setWidth(float width) {
        this.width = width;
    }
    
    /**
     * Returns the border condition for this OutlineStyle.
     * 
     * @return 
     */
    @Override
    public String toString() {
        return this.borderCondition;
    }
    
    /**
     * Writes out this OutlineStyle to FmXML
     * 
     * @param kmlWriter 
     */
    public void toXML(XmlOutput kmlWriter) {
        kmlWriter.openTag ("outlineStyle");
        
        if (this.borderCondition != null)
            kmlWriter.writeTag("borderCondition", borderCondition);        
        
        if (this.color != null)
            kmlWriter.writeTag("color", ColorHelper.getColorHexStandard(color));
        
        if (this.selectedColor != null)
            kmlWriter.writeTag("selectedColor", ColorHelper.getColorHexStandard(selectedColor));        
        
        if (this.strokeStyle != null)
            kmlWriter.writeTag("strokeStyle", strokeStyle);             
        
        if (this.width > 0)
            kmlWriter.writeTag("width", Float.toString(width));          
        
        kmlWriter.closeTag("outlineStyle");
    }
 }

