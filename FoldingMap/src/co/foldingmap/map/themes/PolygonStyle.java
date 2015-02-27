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

import co.foldingmap.ResourceHelper;
import co.foldingmap.map.NumericValueOutOfRangeException;
import co.foldingmap.xml.XmlOutput;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Class for the FmXML object PolygonStyle.
 * 
 * @author Alec
 */
public class PolygonStyle extends ColorStyle { 
    protected BufferedImage objectImage; 
    protected Color         gradient1, gradient2;
    protected float         lineWidth;     
    protected String        imageFileName;
    
    /**
     * Default Constructor
     */
    public PolygonStyle() {
        super();
        
        this.colorMode          = NORMAL;
        this.featureType        = ThemeConstants.UNKNOWN;
        this.fill               = true;            
        this.fillColor          = new Color(255, 255, 255, 128);
        this.gradient1          = null;
        this.gradient2          = null;
        this.id                 = "Default PolyStyle";
        this.imageFileName      = null;
        this.lineWidth          = 1;
        this.objectImage        = null;
        this.outline            = true;  
        this.outlineStyles      = new ArrayList<OutlineStyle>(1); 
    }
    
    /**
     * Constructor, no outline
     * 
     * @param id
     * @param objectColor 
     */
    public PolygonStyle(String id, Color objectColor) {
        this.colorMode          = NORMAL;        
        this.featureType        = ThemeConstants.UNKNOWN;
        this.fillColor          = objectColor;
        this.fill               = true;
        this.gradient1          = null;
        this.gradient2          = null;        
        this.id                 = id;
        this.imageFileName      = null;
        this.objectImage        = null;
        this.outline            = false;
        this.outlineStyles      = new ArrayList<OutlineStyle>(1);   
    }    
            
    /**
     * Constructor, no outline
     * 
     * @param id
     * @param fillColor
     * @param featureType 
     */
    public PolygonStyle(String id, Color fillColor, String featureType) {
        this.colorMode          = NORMAL;
        this.featureType        = featureType;
        this.fillColor          = fillColor;
        this.fill               = true;
        this.id                 = id;
        this.gradient1          = null;
        this.gradient2          = null;        
        this.imageFileName      = null;        
        this.objectImage        = null;
        this.outline            = false;
        this.outlineStyles      = new ArrayList<OutlineStyle>(1);  
    }    
    
    /**
     * Constructor, with an outline and a given fill option.
     * Fill color is Color.WHITE if fill == true.
     * 
     * @param id
     * @param outlineColor
     * @param fill
     * @param featureType 
     */
    public PolygonStyle(String id, Color outlineColor, String featureType, boolean fill) {
        this.colorMode          = NORMAL;
        this.featureType        = featureType;
        this.fillColor          = Color.WHITE;
        this.fill               = fill;
        this.id                 = id;
        this.gradient1          = null;
        this.gradient2          = null;        
        this.imageFileName      = null;        
        this.objectImage        = null;
        this.outline            = true;
        this.outlineColor       = outlineColor; 
        this.outlineStyles      = new ArrayList<OutlineStyle>(1); 
        
        addOutlineStyle(new OutlineStyle(outlineColor));
    }      
    
    /**
     * Constructor, with outline
     * 
     * @param id
     * @param objectColor 
     * @param outlineColor 
     */
    public PolygonStyle(String id, Color objectColor, Color outlineColor) {
        this.colorMode          = NORMAL;
        this.featureType        = ThemeConstants.UNKNOWN;
        this.fillColor          = objectColor;
        this.fill               = true;
        this.id                 = id;
        this.gradient1          = null;
        this.gradient2          = null;        
        this.imageFileName      = null;        
        this.objectImage        = null;
        this.outline            = true;
        this.outlineColor       = outlineColor;    
        this.outlineStyles      = new ArrayList<OutlineStyle>(1);  
        
        addOutlineStyle(new OutlineStyle(outlineColor));
    }      
    
    /**
     * Constructor, with outline
     * 
     * @param id
     * @param objectColor 
     * @param outlineColor 
     * @param featureType 
     */
    public PolygonStyle(String id, Color objectColor, Color outlineColor, String featureType) {
        this.colorMode          = NORMAL;
        this.featureType        = featureType;
        this.fillColor          = objectColor;
        this.fill               = true;
        this.gradient1          = null;
        this.gradient2          = null;        
        this.id                 = id;
        this.imageFileName      = null;        
        this.objectImage        = null;
        this.outline            = true;
        this.outlineColor       = outlineColor;  
        this.outlineStyles      = new ArrayList<OutlineStyle>(1);  

        outlineStyles.add(new OutlineStyle(outlineColor));
    }     
        
    /**
     * Constructor used for Image fill.
     * 
     * @param id            This style's unique ID.
     * @param fillColor     The Fill color for a Polygon.
     * @param outlineColor  The Color to use when outlining a Polygon.
     * @param featureType   What type of feature does this represent.  Ex: Land, Water, Swamp. etc.
     * @param imageURI      The Images location on disk or web address.
     */
    public PolygonStyle(String  id, 
                        Color   fillColor, 
                        Color   outlineColor, 
                        String  featureType, 
                        String  imageURI) {
        
        ResourceHelper helper = ResourceHelper.getInstance();
        
        this.colorMode          = NORMAL;
        this.featureType        = featureType;
        this.fillColor          = fillColor;
        this.fill               = true;
        this.gradient1          = null;
        this.gradient2          = null;        
        this.id                 = id;
        this.imageFileName      = imageURI;                
        this.objectImage        = helper.getBufferedImage(imageURI);
        this.outline            = true;
        this.outlineColor       = outlineColor;  
        this.outlineStyles      = new ArrayList<OutlineStyle>(1);  
        
        if (outlineColor != null) {
            outlineStyles.add(new OutlineStyle(outlineColor));
        }
    }        
    
    public PolygonStyle(String  id, 
                        Color   outlineColor, 
                        String  featureType, 
                        Color   gradient1,
                        Color   gradient2) {        
        
        this.colorMode          = NORMAL;
        this.featureType        = featureType;
        this.fillColor          = gradient1;
        this.fill               = true;
        this.gradient1          = gradient1;
        this.gradient2          = gradient2;        
        this.id                 = id;
        this.imageFileName      = null;                
        this.objectImage        = null;
        this.outline            = false;
        this.outlineColor       = outlineColor;  
        this.outlineStyles      = new ArrayList<OutlineStyle>(1);    
        
        addOutlineStyle(new OutlineStyle(outlineColor));
    }    
    
    /**
     * Returns the line width for this LineStyle.
     * 
     * @return 
     * @deprecated 
     */
    public float getLineWidth() {
        return lineWidth;
    }     
    
    /**
     * Returns if this PolygonStyle is GradientFilled.
     * 
     * @return 
     */
    public boolean isGradientFilled() {
        if (gradient1 == null || gradient2 == null) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Returns if this PolygonStyle is filled with an image.
     * 
     * @return 
     */
    public boolean isImagedFilled() {
        if (imageFileName == null || imageFileName.equals("")) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Returns the first gradient color.
     * 
     * @return 
     */
    public Color getGradient1() {
        return this.gradient1;
    }
    
    /**
     * Returns the second gradient color.
     * 
     * @return 
     */
    public Color getGradient2() {
        return this.gradient2;
    }    
    
    /**
     * Returns a BufferedImage of the image to be used to fill a polygon using htis style.
     * 
     * @return 
     */
    public BufferedImage getObjectImage() {
        return objectImage;
    }
    
    /**
     * Sets the image file name for this PolygonStyle.
     * 
     * @param fileName 
     */
    public void setImageFileName(String fileName) {
        ResourceHelper helper = ResourceHelper.getInstance();
        
        this.imageFileName = fileName;
        this.objectImage   = helper.getBufferedImage(fileName);
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
     * Outputs this PolygonStyle as XML.
     * 
     * @param xmlWriter 
     */
    @Override
    public void toXML(XmlOutput xmlWriter) {
        xmlWriter.openTag ("Style id=\"" + id + "\"");
        xmlWriter.openTag ("PolyStyle");

        xmlWriter.writeTag("featureType", this.featureType); 
        xmlWriter.writeTag("color",       ColorHelper.getColorHexStandard(fillColor));  

        if (colorMode == NORMAL) {
            xmlWriter.writeTag("colorMode", "normal");
        } else if (colorMode == RANDOM) {
            xmlWriter.writeTag("colorMode", "random");
        }

        if (fill) {
            xmlWriter.writeTag("fill", "1");
        } else {
            xmlWriter.writeTag("fill", "0");
        }

        if (imageFileName != null && !imageFileName.equals("")) {
            xmlWriter.openTag("Icon");
            xmlWriter.writeTag("href", imageFileName);
            xmlWriter.closeTag("Icon");
        }
        
        if (this.outlineStyles.size() > 0) {
            xmlWriter.openTag("outlines");

            for (OutlineStyle outlineStyle: this.outlineStyles) 
                outlineStyle.toXML(xmlWriter);                

            xmlWriter.closeTag("outlines");
        }
        
        if (this.getVisibility() != null) {
            this.getVisibility().toXML(xmlWriter);
        }        
        
        xmlWriter.closeTag("PolyStyle");
        xmlWriter.closeTag("Style");
    }    
}
