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

import co.foldingmap.Logger;
import co.foldingmap.ResourceHelper;
import co.foldingmap.map.Visibility;
import co.foldingmap.map.vector.LineString;
import co.foldingmap.map.vector.MapPoint;
import co.foldingmap.map.vector.Polygon;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.xml.XmlOutput;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.ImageIcon;

/**
 * Class used for storing information about how the map is styled.
 * 
 * @author Alec
 */
public class MapTheme {    
    protected Color                         backgroundColor, pointColor;
    protected HashMap<String, ColorRamp>    colorRamps;                  
    protected HashMap<String, IconStyle>    iconStyles;
    protected HashMap<String, LineStyle>    lineStyles;
    protected HashMap<String, PolygonStyle> polygonStyles;
    protected HashMap<String, StyleMap>     styleMaps;    
    protected LabelStyle                    noLabel;
    protected Visibility                    lvl1, lvl2, lvl3, lvl4, lvl5;
    protected ResourceHelper                resourceHelper;
    protected String                        themeName;
    
    public static final Font       DEFAULT_FONT  = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    public static final LabelStyle DEFAULT_LABEL = new LabelStyle(Color.WHITE, new Color(68, 68, 68), DEFAULT_FONT);           
    
    public MapTheme(String themeName) {
        this.themeName       = themeName;
        this.backgroundColor = new Color(244, 243, 240);
        
        lvl1  = new Visibility(25, 16);
        lvl2  = new Visibility(25, 12);
        lvl3  = new Visibility(25, 8);
        lvl4  = new Visibility(25, 4);
        lvl5  = new Visibility(25, 2);        
        
        this.colorRamps      = new HashMap<String, ColorRamp>();
        this.iconStyles      = new HashMap<String, IconStyle>();
        this.lineStyles      = new HashMap<String, LineStyle>();
        this.polygonStyles   = new HashMap<String, PolygonStyle>();
        this.styleMaps       = new HashMap<String, StyleMap>();
        
        this.resourceHelper  = ResourceHelper.getInstance();
        
        addStyleElement(new IconStyle(   "(Unspecified Point)",      new Color(68, 68, 68, 128)));
        addStyleElement(new LineStyle(   "(Unspecified Linestring)", new Color(68, 68, 68, 128), 1.0f, LineStyle.SOLID, false));
        addStyleElement(new PolygonStyle("(Unspecified Polygon)",    new Color(188, 190, 178)));        
    }    
    
    /**
     * Adds a ColorRamp to this Theme.
     * 
     * @param colorRamp 
     */
    public final void addColorRamp(ColorRamp colorRamp) {
        colorRamps.put(colorRamp.getID(), colorRamp);
    }        
    
    /**
     * Add a Style Element
     * @param cs 
     */
    public final void addStyleElement(ColorStyle cs) {        
        if (cs instanceof IconStyle) {
            IconStyle is = (IconStyle) cs;
            
            is.setResourceHelper(resourceHelper);
            iconStyles.put(cs.getID(), is);
        } else if (cs instanceof LineStyle) {
            lineStyles.put(cs.getID(), (LineStyle) cs);
        } else if (cs instanceof PolygonStyle) {
            polygonStyles.put(cs.getID(), (PolygonStyle) cs);
        }
    }    
    
    /**
     * Returns an ArrayList of all the ColorRamps in this Theme.
     * 
     * @return 
     */
    public ArrayList<ColorRamp> getAllColorRamps() {
        return new ArrayList<ColorRamp>(colorRamps.values());
    }    
    
    /**
     * Returns all the IconStyles in this MapTheme.
     * 
     * @return 
     */
    public ArrayList<IconStyle> getAllIconStyles() {
        return new ArrayList<IconStyle>(iconStyles.values());
    }       
    
    /**
     * Returns all the LineStyles in this MapTheme.
     * 
     * @return 
     */
    public ArrayList<LineStyle> getAllLineStyles() {
        return new ArrayList<LineStyle>(lineStyles.values());
    }     
    
    /**
     * Returns all the PolygonStyles in this MapTheme.
     * 
     * @return 
     */
    public ArrayList<PolygonStyle> getAllPolygonStyles() {
        return new ArrayList<PolygonStyle>(polygonStyles.values());
    }       
    
    /**
     * Returns all styles for this theme, in an ArrayList.
     * 
     * @return 
     */
    public ArrayList<ColorStyle> getAllStyles() {
        ArrayList<ColorStyle> styles = new ArrayList<ColorStyle>();
        
        styles.addAll(iconStyles.values());
        styles.addAll(lineStyles.values());
        styles.addAll(polygonStyles.values());
        
        return styles;
    }
    
    /**
     * Returns all the StyleMaps in this Theme.
     * 
     * @return 
     */
    public ArrayList<StyleMap> getAllStyleMaps() {
        if (styleMaps.values() != null) {
            return new ArrayList<StyleMap>(styleMaps.values());
        } else {
            return new ArrayList<StyleMap>();
        }
    }     
    
    /**
     * Returns the background style of this Theme.
     * 
     * @return 
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }    
    
    /**
     * Returns the ColorRamp with the given is.  If no ColorRamp exists with 
     * that id in this theme, null is returned.
     * 
     * @param id
     * @return 
     */
    public ColorRamp getColorRamp(String id) {
        return colorRamps.get(id);
    }   
    
    /**
     * Returns an IconStyle with a given Name.  Returns null if that name 
     * doesn't exist in the HashMap.
     * 
     * @param elementName
     * @return 
     */
    public IconStyle getIconStyle(String elementName) {
        IconStyle   returnIconStyle;

        returnIconStyle = iconStyles.get(elementName);

        return returnIconStyle;
    }         
    
    /**
     * Returns an ImageIcon of a given image file located in this program's 
     * .jar file.
     * 
     * @param fileName
     * @return 
     */
    public ImageIcon getImageFromResourceMap(String fileName) {
        ImageIcon imageIcon;

        try {
            imageIcon = resourceHelper.getImage(fileName);
        } catch (Exception e) {
            imageIcon = null;
            Logger.log(Logger.ERR, "Error in MapTheme.getImageFromResourceMap(String) - " + e);
        }
        
        return imageIcon;
    }      
    
    /**
     * Returns a LineStyle with a given name.  Returns null if that LineStyle 
     * does not exist in the HashMap.
     * 
     * @param elementName
     * @return 
     */
    public LineStyle getLineStyle(String elementName) {
        LineStyle returnLineStyle;

        returnLineStyle = lineStyles.get(elementName);

        return returnLineStyle;
    }         
    
    /**
     * Returns the name of this theme.
     * 
     * @return 
     */
    public String getName() {
        return this.themeName;
    }
    
    /**
     * Returns the color that should be used in Edit Mode for object points.
     * The default is black.
     * 
     * @return 
     */
    public Color getPointColor() {
        if (pointColor == null) {
            return Color.BLACK;
        } else {
            return pointColor;
        }
    }
    
    /**
     * Returns a PolygonStyle with a given name.  Returns null if that 
     * PolygonStyle does not exist in the HashMap.
     * 
     * @param elementName
     * @return 
     */
    public PolygonStyle getPolygonStyle(String elementName) {
        PolygonStyle    returnPolygonStyle;

        returnPolygonStyle = polygonStyles.get(elementName);

        return returnPolygonStyle;
    }         
    
    /**
     * Creates a basic stroke with the given width and stroke style.
     * 
     * @param stroke    A String from the ThemeConstancts class for the stroke style to use.
     * @param width     Width of stroke to draw.
     * @return 
     */
    public static BasicStroke getStroke(String stroke, float width) {
        BasicStroke newStroke;
        
        if (stroke.equalsIgnoreCase(ThemeConstants.DASHED)) {    
            newStroke = new BasicStroke(width,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, LineStyle.DASHED_STYLE, 0.0f);        
        } else if (stroke.equalsIgnoreCase(ThemeConstants.DOTTED)) {
            newStroke = new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 2f, new float[] {1, 4}, 0f);        
        } else if (stroke.equalsIgnoreCase(ThemeConstants.SOLID)) {
            newStroke = new BasicStroke(width,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        } else {
            newStroke = new BasicStroke(width,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        }
        
        return newStroke;
    }
    
    /**
     * Returns and Style with a given name;
     * 
     * @param elementName
     * @return 
     * 
     * @deprecated Use getStyle(VectorObject) instead.
     */
    public ColorStyle getStyle(String elementName) {
        ColorStyle style;
        
        style = this.getIconStyle(elementName);
        
        if (style == null)
            style = this.getLineStyle(elementName);
        
        if (style == null)
            style = this.getPolygonStyle(elementName);
        
        return style;
    }
    
    /**
     * Returns the ColorStyle that best fits the given VectorObject.
     * 
     * @param vectorObject The VectorObject we are seeking the style for.
     * @param zoom The zoom level to get the style for.
     * @return The ColorStyle that best fits the given VectorObject, null if none can be found.
     */
    public ColorStyle getStyle(VectorObject vectorObject, float zoom) {
        ColorStyle style = null;
        
        // TODO: add other options such as looking for styles based on the Key-Value property pairs of the object.
        
        if (vectorObject instanceof LineString) {
            style = this.getLineStyle(vectorObject.getObjectClass());
        } else if (vectorObject instanceof MapPoint) {
            style = this.getIconStyle(vectorObject.getObjectClass());
        } else if (vectorObject instanceof Polygon) {  
            style = this.getPolygonStyle(vectorObject.getObjectClass());
        }
        
        return style;
    }
   
    /**
     * Returns a StyleMap with with a given name.  Will Return null if the 
     * given name does not exist in the HashMap.
     * 
     * @param elementName
     * @return 
     */
    public StyleMap getStyleMap(String elementName) {
        return styleMaps.get(elementName);
    }   
    
    /**
     * Removes a ColorRamp from this Theme.
     * 
     * @param colorRamp 
     */
    public void removeColorRamp(ColorRamp colorRamp) {
        colorRamps.remove(colorRamp.getID());
    }
    
    /**
     * Sets this Theme's background color.
     * 
     * @param c 
     */
    public void setBackgroundColor(Color c) {
        this.backgroundColor = c;
    }    
    
    /**
     * Returns the name of this Theme.
     * 
     * @return 
     */
    @Override
    public String toString() {
        return themeName;
    }    
    
    public void toXML(XmlOutput xmlWriter) {
        ArrayList<ColorRamp>    colorRampList;
        ArrayList<IconStyle>    iconStylesList;
        ArrayList<LineStyle>    lineStylesList;
        ArrayList<PolygonStyle> polygonStylesList;
        ArrayList<StyleMap>     styleMapsList;

        try {
            colorRampList       = new ArrayList<ColorRamp>    (getAllColorRamps());
            iconStylesList      = new ArrayList<IconStyle>    (getAllIconStyles());
            lineStylesList      = new ArrayList<LineStyle>    (getAllLineStyles());
            polygonStylesList   = new ArrayList<PolygonStyle> (getAllPolygonStyles());
            styleMapsList       = new ArrayList<StyleMap>     (getAllStyleMaps());

            xmlWriter.openTag("Style");
            xmlWriter.writeTag("BackColor", ColorHelper.getColorHexStandard(backgroundColor));
            xmlWriter.closeTag("Style");
                                    
            for (IconStyle currentIconStyle: iconStylesList)
                currentIconStyle.toXML(xmlWriter);

            for (LineStyle currentStyle: lineStylesList)
                currentStyle.toXML(xmlWriter);

            for (PolygonStyle currentStyleMap: polygonStylesList)
                currentStyleMap.toXML(xmlWriter);

            for (StyleMap currentStyle: styleMapsList)
                currentStyle.toXML(xmlWriter);
            
            for (ColorRamp currentRamp: colorRampList)
                currentRamp.toXML(xmlWriter);      
            
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in MapTheme.toXML(XmlOutput) - " + e);
        }
    }    
}
