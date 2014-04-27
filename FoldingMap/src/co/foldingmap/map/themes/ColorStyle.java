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

import co.foldingmap.map.Visibility;
import co.foldingmap.xml.XmlOutput;
import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author Alec
 */
public abstract class ColorStyle extends ThemeConstants implements Comparable {
    public static final int NORMAL = 0;
    public static final int RANDOM = 1;
    
    protected ArrayList<OutlineStyle> outlineStyles;
    protected boolean                 fill, outline;
    protected Color                   fillColor, outlineColor;
    protected Color                   selectedFillColor, selectedOutlineColor;
    protected int                     colorMode;    
    protected LabelStyle              label;
    protected String                  featureType, id;
    protected Visibility              visibility;
    
    //Abstract Methods
    public abstract void toXML(XmlOutput kmlWriter);  
    
    /**
     * Constructor for some default options.
     */
    public ColorStyle() {
        colorMode               = NORMAL;
        fill                    = true;
        fillColor               = new Color(68,  68, 68, 128);
        outline                 = true;
        outlineColor            = new Color(50,  50, 50, 128); 
        outlineStyles           = new ArrayList<OutlineStyle>(1);
        selectedFillColor       = new Color(243, 62, 62, 128);
        selectedOutlineColor    = Color.RED;          
    }
    
    /**
     * Adds and OutlineStyle to be used in this Color Style.
     * 
     * @param outlineStyle 
     */
    public void addOutlineStyle(OutlineStyle outlineStyle) {
        this.outlineStyles.add(outlineStyle);
        this.outline = true;
    }
    
    /**
     * Compare method for use in collections.
     * 
     * @param o
     * @return 
     */
    @Override
    public int compareTo(Object o) {
        ColorStyle cs;

        if (o instanceof ColorStyle) {
            cs = (ColorStyle) o;
            return this.id.compareTo(cs.getID());
        } else {
            return 0;
        }
    }    
    
    /**
     * Return is this ColorStyle is equal to another object.
     * 
     * @param o
     * @return 
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof ColorStyle) {
            ColorStyle cs = (ColorStyle) o;
            return (this.hashCode() == cs.hashCode());
        } else {
            return false;
        }
    }

    /**
     * Generates the hash code for this ColorStyle
     * 
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.fill ? 1 : 0);
        hash = 29 * hash + (this.outline ? 1 : 0);
        hash = 29 * hash + (this.fillColor != null ? this.fillColor.hashCode() : 0);
        hash = 29 * hash + (this.outlineColor != null ? this.outlineColor.hashCode() : 0);
        hash = 29 * hash + (this.selectedFillColor != null ? this.selectedFillColor.hashCode() : 0);
        hash = 29 * hash + (this.selectedOutlineColor != null ? this.selectedOutlineColor.hashCode() : 0);
        hash = 29 * hash + this.colorMode;
        hash = 29 * hash + (this.label != null ? this.label.hashCode() : 0);
        hash = 29 * hash + (this.featureType != null ? this.featureType.hashCode() : 0);
        hash = 29 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
    
    /**
     * Returns the ColorMode for this Style
     * 
     * @return 
     */
    public int getColorMode() {
        return colorMode;
    }
    
    /**
     * Return this feature type of this Style
     * 
     * @return 
     *  A String corresponding to a feature type defined in ThemeConstants.
     */
    public String getFeatureType() {
        return this.featureType;        
    }
    
    /**
     * Returns the fill color for this style.
     * 
     * @return 
     */
    public Color getFillColor() {
        return fillColor;
    }    

    /**
     * Returns the name of this ColorStyle.
     * 
     * @return 
     */
    public String getID() {
        return id;
    }    
    
    /**
     * Returns the label associated with this style.
     * 
     * @return 
     */
    public LabelStyle getLabel() {
        return label;
    }
    
    /**
     * Returns the Outline Color for this Style.
     * 
     * @return 
     */
    public Color getOutlineColor() {
        return this.outlineColor;
    }
    
    /**
     * A convenience class for getting an OutlineStlye by specifying a condition.
     * 
     * @param condition
     * @return 
     */
    public OutlineStyle getOutlineStyleByCondition(String condition) {
        OutlineStyle anyStyle, returnStyle;
        
        anyStyle    = null;
        returnStyle = null;
        
        for (OutlineStyle os: outlineStyles) {
            //Grab the Any condition while we are looking
            if (os.getBorderCondition().equalsIgnoreCase(ThemeConstants.ANY)) 
                anyStyle = os;      
            
            if (os.getBorderCondition().equalsIgnoreCase(condition)) {
                returnStyle = os;
                break;
            }                  
        }
        
        //If the return style is not found, return the ANY style;
        if (returnStyle == null) {
            if (anyStyle != null) {
                return anyStyle;
            } else {
                //No styles at all!  Return a new style, with the fill color.
                return new OutlineStyle(this.fillColor);
            }
        } else {
            return returnStyle;
        }        
    }
    
    /**
     * Returns an ArrayList of OutlineStyles for this Style.
     * 
     * @return 
     */
    public ArrayList<OutlineStyle> getOutlineStyles() {
        return this.outlineStyles;
    }
    
    /**
     * Returns the selected fill color for this style.
     * 
     * @return 
     */
    public Color getSelectedFillColor() {
        return selectedFillColor;
    }
        
    /**
     * Returns the selected Outline Color for this Style.
     * 
     * @return 
     */
    public Color getSelectedOutlineColor() {
        return this.selectedOutlineColor;
    }    
    
    /**
     * Returns the Visibility for this Style.
     * 
     * @return 
     */
    public Visibility getVisibility() {
        return this.visibility;
    }
    
    /**
     * Returns if this Style is filled.
     * 
     * @return 
     */
    public boolean isFilled() {
        return fill;
    }
    
    /**
     * Returns id this Style id outlined.
     * 
     * @return 
     */
    public boolean isOutlined() {
        return outline;
    }    
    
    /**
     * Sets the color mode for this ColorStyle.
     * 
     * @param colorMode 
     */
    public void setColorMode(int colorMode) {
        this.colorMode = colorMode;
    }
    
    /**
     * Sets the feature type of this ColorStyle.
     * @param featureType
     */
    public void setFeatureType(String featureType) {
        this.featureType = featureType;
    }
    
    /**
     * Sets if this Style is filled.
     * 
     * @param fill 
     */
    public void setFill(boolean fill) {
        this.fill = fill;
    }
    
    /**
     * Sets the fill color for this Style.
     * 
     * @param c 
     */
    public void setFillColor(Color c) {
        this.fillColor  = c;
    }
    
    /**
     * Sets the label for this object.
     * @param label
     */
    public void setLabel(LabelStyle label) {
        this.label = label;
    }
    
    /**
     * Sets if this Style is Outlined.
     * 
     * @param outline 
     */
    public void setOutline(boolean outline) {
        this.outline = outline;
    }
    
    /**
     * Sets the Outline Color for this Style.
     * 
     * @param c 
     */
    public void setOutlineColor(Color c) {
        this.outlineColor = c;
    }
    
    /**
     * Sets the fill color for this style when selected.
     * 
     * @param c 
     */
    public void setSelectedFillColor(Color c) {
        this.selectedFillColor = c;
    }    
    
    /**
     * Sets the outline color for this style when selected.
     * 
     * @param c 
     */
    public void setSelectedOutlineColor(Color c) {
        this.selectedOutlineColor = c;
    }
    
    /**
     * Sets the Visibility for this object.
     * 
     * @param visibility 
     */
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }
    
    /**
     * Returns this name of this Style, useful for when these are displayed in 
     * lists and trees.
     * 
     * @return 
     */
    @Override
    public String toString() {
        return this.id;
    }
}
