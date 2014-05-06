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

import co.foldingmap.map.NumericValueOutOfRangeException;
import co.foldingmap.map.Visibility;
import co.foldingmap.xml.XmlOutput;
import java.awt.Color;

/**
 * Class for the KML object LineStyle.
 * 
 * @author Alec
 */
public class LineStyle extends ColorStyle { 
    protected boolean   scaleWidth;
    protected float     lineWidth;
    protected String    lineStroke;
    
    /**
     * Default constructor
     */
    public LineStyle() {
        super();
        
        this.featureType = ThemeConstants.UNKNOWN;
        this.id          = "Default LineStyle";
        this.lineStroke  = SOLID;
        this.lineWidth   = 1;
        this.scaleWidth  = true;
    }
    
    /**
     * Constructor No Outline
     * 
     * @param id
     * @param c
     * @param width
     * @param outline 
     */
    public LineStyle(String  id, 
                     Color   fillColor, 
                     float   lineWidth, 
                     String  lineStroke,
                     boolean scaleWidth) {
        
        this.id          = id;
        this.featureType = ThemeConstants.UNKNOWN;
        this.fillColor   = fillColor;
        this.lineWidth   = lineWidth;
        this.lineStroke  = lineStroke;
        this.outline     = false;
        this.scaleWidth  = scaleWidth;
    }    
    
    /**
     * Constructor with outline
     * 
     * @param id
     * @param c
     * @param width
     * @param outline 
     */
    public LineStyle(String id, 
                     Color  fillColor, 
                     Color  outlineColor, 
                     float  lineWidth, 
                     String lineStroke) {
        
        this.id           = id;
        this.featureType  = ThemeConstants.UNKNOWN;
        this.fillColor    = fillColor;
        this.lineWidth    = lineWidth;
        this.lineStroke   = lineStroke;
        this.outline      = true;
        this.outlineColor = outlineColor;
        this.scaleWidth   = true;
    }     
    
    /**
     * Constructor with outline
     * 
     * @param id
     * @param c
     * @param width
     * @param outline 
     * @param visibility
     */
    public LineStyle(String     id, 
                     Color      fillColor, 
                     Color      outlineColor, 
                     float      lineWidth, 
                     String     lineStroke,
                     Visibility visibility) {
        
        this.id           = id;
        this.featureType  = ThemeConstants.UNKNOWN;
        this.fillColor    = fillColor;
        this.lineWidth    = lineWidth;
        this.lineStroke   = lineStroke;
        this.outline      = true;
        this.outlineColor = outlineColor;
        this.scaleWidth   = true;
        this.visibility   = visibility;
    }        
    
    /**
     * Constructor with outline and label style
     * 
     * @param id
     * @param fillColor
     * @param outlineColor
     * @param lineWidth
     * @param lineStroke
     * @param labelStyle 
     */
    public LineStyle(String     id, 
                     Color      fillColor, 
                     Color      outlineColor, 
                     float      lineWidth, 
                     String     lineStroke, 
                     LabelStyle labelStyle) {
        
        this.id           = id;
        this.featureType  = ThemeConstants.UNKNOWN;
        this.fillColor    = fillColor;
        this.lineWidth    = lineWidth;
        this.lineStroke   = lineStroke;
        this.outline      = true;
        this.outlineColor = outlineColor;
        this.label        = labelStyle;
        this.scaleWidth   = true;
    }     
    
    /**
     * Constructor with outline, label style and Visibility.
     * 
     * @param id
     * @param fillColor
     * @param outlineColor
     * @param lineWidth
     * @param lineStroke
     * @param labelStyle 
     * @param visibility 
     */
    public LineStyle(String     id, 
                     Color      fillColor, 
                     Color      outlineColor, 
                     float      lineWidth, 
                     String     lineStroke, 
                     LabelStyle labelStyle,
                     Visibility visibility) {
        
        this.id           = id;
        this.featureType  = ThemeConstants.UNKNOWN;
        this.fillColor    = fillColor;
        this.lineWidth    = lineWidth;
        this.lineStroke   = lineStroke;
        this.outline      = true;
        this.outlineColor = outlineColor;
        this.label        = labelStyle;
        this.scaleWidth   = true;
        this.visibility   = visibility;
    }     
    
    /**
     * Returns if this LineStyle is equal to a given Object.
     * 
     * @param o
     * @return 
     */
    @Override
    public boolean equals(Object o) {
        boolean isEqual = false;
        
        if (o instanceof LineStyle) {
            LineStyle ls = (LineStyle) o;           
            isEqual = (this.hashCode() == ls.hashCode());
        }
        
        return isEqual;        
    }

    /**
     * Generate the hash code for this object.
     * 
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Float.floatToIntBits(this.lineWidth);
        hash = 17 * hash + (this.lineStroke != null ? this.lineStroke.hashCode() : 0);
        hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 17 * hash + (this.getFeatureType() != null ? this.getFeatureType().hashCode() : 0);
        hash = 17 * hash + (this.getLabel() != null ? this.getLabel().hashCode() : 0);
        hash = 17 * hash + (this.getOutlineColor() != null ? this.getOutlineColor().hashCode() : 0);
        hash = 17 * hash + (this.getFillColor() != null ? this.getFillColor().hashCode() : 0);
        hash = 17 * hash + (this.getLineStroke() != null ? this.getLineStroke().hashCode() : 0);
        
        return hash;
    }
    
    /**
     * Returns the Line Stroke, or how the line is drawn for this LineStyle.
     * 
     * @return 
     */
    public String getLineStroke() {
        return lineStroke;
    }    
    
    /**
     * Returns the line width for this LineStyle.
     * 
     * @return 
     */
    public float getLineWidth() {
        return lineWidth;
    }    
    
    /**
     * Returns if the width should be scaled when zooming.
     * 
     * @return 
     */
    public boolean scaleWidth() {
        return this.scaleWidth;
    }
    
    /**
     * Sets the LineStroke for this LineStyle.
     * 
     * @param lineStroke 
     */
    public void setLineStroke(String lineStroke) {
        this.lineStroke = lineStroke;
    }
    
    /**
     * Sets the LineWidth for this LineStyle.
     * 
     * @param lineWidth 
     *          For width > 0
     */
    public void setLineWidth(float lineWidth) {
        if (lineWidth > 0) {
            this.lineWidth = lineWidth;
        } else {
            throw new NumericValueOutOfRangeException(lineWidth);
        }
    }
    
    /**
     * Writes out this LineStyle to KML.
     * 
     * @param xmlWriter
     */
    @Override
    public void toXML(XmlOutput xmlWriter) {
        xmlWriter.openTag ("Style id=\"" + id + "\"");
        xmlWriter.openTag ("LineStyle");
              
        xmlWriter.writeTag("width",             Float.toString(lineWidth));
        xmlWriter.writeTag("color",             ColorHelper.getColorHexStandard(fillColor));  
        xmlWriter.writeTag("selectedFillColor", ColorHelper.getColorHexStandard(selectedFillColor));  
        
        if (outline) {
            xmlWriter.writeTag("outline",      "1");
            xmlWriter.writeTag("outlineColor", ColorHelper.getColorHexStandard(getOutlineColor()));
            
            if (selectedOutlineColor != null)
                xmlWriter.writeTag("selectedOutlineColor", ColorHelper.getColorHexStandard(selectedOutlineColor));            
        } else {
            xmlWriter.writeTag("outline", "0");
        }

        if (!this.scaleWidth)
            xmlWriter.writeTag("scaleWidth", Boolean.toString(this.scaleWidth));
        
        if (!this.lineStroke.equals(SOLID))
            xmlWriter.writeTag("lineStroke", this.lineStroke);

        if (this.getVisibility() != null) {
            this.getVisibility().toXML(xmlWriter);
        }
        
        xmlWriter.closeTag("LineStyle");
        
        if (this.getLabel() != null) 
            getLabel().toXML(xmlWriter);        
        
        xmlWriter.closeTag("Style");  
    }    
}
