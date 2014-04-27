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
package co.foldingmap.map.labeling;

import co.foldingmap.map.themes.LabelStyle;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * This is the parent class for the MapLabeling classes.
 * 
 * @author Alec
 */
public abstract class MapLabel {
    protected boolean     complete;
    protected Color       outlineColor, fillColor;
    protected Font        labelFont;
    protected FontMetrics fontMetrics;
    protected LabelStyle  labelStyle;
    protected Rectangle2D labelArea;
    
    public abstract void        drawLabel(Graphics2D g2);
    public abstract Rectangle2D getLabelArea();
    public abstract boolean     overlapsLabel(MapLabel label);    
    
    /**
     * Returns the fill color for this Label.
     * 
     * @return 
     */
    public Color getFillColor() {
        return fillColor;
    }
    
    /**
     * Returns the Font being used for this label.
     * 
     * @return 
     */
    public Font getFont() {
        return labelFont;
    }
    
    /**
     * Return the length of a a perspective label.
     * 
     * @param g2
     * @param labelText
     * @return 
     */
    public float getLabelLength(Graphics2D g2, String labelText) {
        Rectangle2D lineArea;
        
        lineArea = fontMetrics.getStringBounds(labelText, g2);
        
        return (float) lineArea.getWidth();
    }
    
    /**
     * Returns the LabelStyle used to draw this Label.
     * 
     * @return 
     */
    public LabelStyle getLabelStyle() {
        return labelStyle;
    }
    
    /**
     * Returns the Outline Color for this Label.
     * 
     * @return 
     */
    public Color getOutlineColor() {
        return this.outlineColor;
    }
    
    /**
     * Returns if the label construction has been completed.
     * 
     * @return 
     */
    public boolean isComplete() {
        return complete;
    }
    
    /**
     * Sets the fill color for this Label.
     * 
     * @param c 
     */
    public void setFillColor(Color c) {
        this.fillColor  = c;
    }
    
    /**
     * Set the Font to be used by this Label.
     * 
     * @param labelFont 
     */
    public void setFont(Font labelFont) {
        this.labelFont  = labelFont;
    }
    
    /**
     * Sets the LabelStyle to be used when drawing this label.
     * 
     * @param labelStyle 
     */
    public void setLabelStyle(LabelStyle labelStyle) {
        this.labelStyle = labelStyle;
    }
    
    /**
     * Sets the Outline Color for this Label.
     * 
     * @param c 
     */
    public void setOutlineColor(Color c) {
        this.outlineColor = c;
    }    
}
