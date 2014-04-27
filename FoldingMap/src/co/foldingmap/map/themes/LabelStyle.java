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
import java.awt.Font;

/**
 *
 * @author Alec
 */
public class LabelStyle {
    protected boolean visible;
    protected Color   fillColor, outlineColor;
    protected Font    labelFont;
    
    /**
     * Constructor for objects of class LabelStyle
     */
    public LabelStyle(Color fillColor) {
        this.fillColor     = fillColor;
        this.labelFont     = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
        this.outlineColor  = new Color(75, 68, 60);
        this.visible       = true;
    }    
    
    /**
     * Constructor for objects of class LabelStyle
     */
    public LabelStyle(Color fillColor, Color outlineColor) {
        this.fillColor    = fillColor;
        this.labelFont    = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
        this.outlineColor = outlineColor;
        this.visible      = true;
    }    
    
    /**
     * Full Constructor for objects of class LabelStyle
     */
    public LabelStyle(Color fillColor, Color outlineColor, Font font) {
        this.fillColor    = fillColor;
        this.labelFont    = font;
        this.outlineColor = outlineColor;
        this.visible      = true;
    }     
    
    /**
     * Returns if this LabelStyle equals another object.
     * 
     * @param o
     * @return 
     */
    @Override
    public boolean equals(Object o) {
        boolean isEqual = false;
        
        if (o instanceof LabelStyle) {
            LabelStyle ls = (LabelStyle) o;
            
//            if (ls.getFillColor().equals(this.fillColor) && 
//                ls.getFont().equals(this.labelFont) &&
//                ls.getOutlineColor().equals(this.outlineColor)) {
//                
//                isEqual = true;
//            }
            
            isEqual = (this.hashCode() == ls.hashCode());
        }
        
        return isEqual;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.fillColor != null ? this.fillColor.hashCode() : 0);
        hash = 11 * hash + (this.outlineColor != null ? this.outlineColor.hashCode() : 0);
        hash = 11 * hash + (this.labelFont != null ? this.labelFont.hashCode() : 0);
        
        return hash;
    }
    
    /**
     * Returns the fill color of this label
     * 
     * @return 
     */
    public Color getFillColor() {
        return fillColor;
    }
    
    /**
     * Returns the font used by this label.
     * 
     * @return 
     */
    public Font getFont() {
        return labelFont;
    }    
    
    /**
     * Returns the outline color used by this label.
     * @return 
     */
    public Color getOutlineColor() {
        return outlineColor;
    }
    
    /**
     * Returns if this label is visible
     * 
     * @return 
     */
    public boolean isLabelVisible() {
        return this.visible;
    }    
    
    /**
     * Sets the fill color to be used by this label.
     * 
     */
    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }
    
    /**
     * Sets the font to be used by this label.
     * 
     * @param labelFont 
     */
    public void setFont(Font labelFont) {
        this.labelFont = labelFont;
    }    
    
    /**
     * Sets the outline color to be used by this Label.
     * 
     * @param outlineColor 
     */
    public void setOutlineColor(Color outlineColor) {
        this.outlineColor = outlineColor;
    }
    
    /**
     * Sets weather this label is visible.
     * 
     * @param visible 
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }    
    
    /**
     * Writes out KML for this LabelStyle.
     * 
     * @param kmlWriter 
     */
    public void toXML(XmlOutput kmlWriter) {
        kmlWriter.openTag ("labelStyle");
        kmlWriter.writeTag("color",        ColorHelper.getColorHexStandard(fillColor));
        kmlWriter.writeTag("outlineColor", ColorHelper.getColorHexStandard(outlineColor));
        
        kmlWriter.openTag ("font");
        kmlWriter.writeTag("family", labelFont.getFamily());
        
        if (labelFont.isBold())
            kmlWriter.writeTag("style", "Bold");
        
        if (labelFont.isItalic())
            kmlWriter.writeTag("style", "Italic");
        
        if (labelFont.isPlain())
            kmlWriter.writeTag("style", "Plain");        
        
        kmlWriter.writeTag("size",   Integer.toString(labelFont.getSize()));
        kmlWriter.closeTag("font");
        
        kmlWriter.closeTag("labelStyle");
    }
}
