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

import co.foldingmap.map.MapView;
import co.foldingmap.map.Overlay;
import co.foldingmap.xml.XmlOutput;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Alec
 */
public class HeatMapKey extends Overlay {
    public static final int NONE         = 0;
    public static final int TOP_LEFT     = 1;
    public static final int TOP_RIGHT    = 2;
    public static final int BOTTOM_LEFT  = 3;
    public static final int BOTTOM_RIGHT = 4;
    
    public static final String VERTICAL   = "Vertical";
    public static final String HORIZONTAL = "Horizontal";
    
    protected ArrayList<Color>  colors;
    protected ArrayList<String> labels;
    protected boolean           horizontal;
    protected Color             outlineColor, fillColor;
    protected double            maxLabelWidth;
    protected FontMetrics       fontMetrics;
    protected int               positionReference;
    protected RoundRectangle2D  outline;
    
    public HeatMapKey(ArrayList<String> labels, 
                      ArrayList<Color>  colors, 
                      int               keyPosition, 
                      String            orientation) {
        
        this.labels            = labels;
        this.colors            = colors;       
        this.maxLabelWidth     = -1;
        this.positionReference = keyPosition;
        
        if (orientation.equalsIgnoreCase(VERTICAL)) {
            this.horizontal    = false;   
            
            //If the key is vertical we should have high values on top
            Collections.reverse(colors);   
            Collections.reverse(labels); 
        } else {
            this.horizontal    = true;  
        }        

        outline      = new RoundRectangle2D.Float(20, 20, 266, 40, 10, 10);
        fillColor    = new Color(68,68,68, 120);
        outlineColor = new Color(255,255,255, 77);        
    }
    
    public HeatMapKey(ArrayList<String> labels, 
                      ArrayList<Color>  colors, 
                      int               keyPosition, 
                      boolean           horizontal) {
        
        this.labels            = labels;
        this.colors            = colors;      
        this.maxLabelWidth     = -1;
        this.positionReference = keyPosition;
        this.horizontal        = horizontal;          
        
        outline      = new RoundRectangle2D.Float(20, 20, 266, 40, 10, 10);
        fillColor    = new Color(68,68,68, 120);
        outlineColor = new Color(255,255,255, 77);   
        
       //If the key is vertical we should have high values on top
        if (!horizontal) {
            Collections.reverse(colors);   
            Collections.reverse(labels); 
        }
    }    
    
    @Override
    public Overlay copyOverlay() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawObject(Graphics2D g2, MapView mapView) {
        int         x, y;
        float       height, width;
        float       labelHeight, labelWidth;
        float       labelY;        
        Rectangle2D colorRect;
        String      currentLabel;
        
        fontMetrics = g2.getFontMetrics();
        
        //Get the max label width as it may affect the outline size
        if (maxLabelWidth == -1) {
            maxLabelWidth = fontMetrics.getStringBounds(getLongestLabel(), g2).getWidth();
        }
        
        if (positionReference == TOP_LEFT || positionReference == BOTTOM_LEFT) {
            x = 10;
        } else if (positionReference == TOP_RIGHT || positionReference == BOTTOM_RIGHT) {
            if (horizontal) {
                x = (int) mapView.getDisplayWidth() - 276;
            } else {
                x = (int) mapView.getDisplayWidth() - 55;
            }
        } else {
            x = 0;
        }
        
        if (positionReference == TOP_LEFT || positionReference == TOP_RIGHT) {
            y = 10;
        } else if (positionReference == BOTTOM_LEFT || positionReference == BOTTOM_RIGHT) {
            if (horizontal) {
                y = (int) mapView.getDisplayHeight() - 105;
            } else {
                y = (int) mapView.getDisplayHeight() - 310;
            }
        } else {
            y = 0;
        }        
        
        if (horizontal) {
            height = 8;
            width  = 256f / colors.size();   
            outline.setRoundRect(x, y, 266, 40, 10, 10);
        } else {
            height = 256f / colors.size();      
            width  = 8;
            outline.setRoundRect(x, y, (23 + maxLabelWidth), 266, 10, 10);
        }        
                        
        colorRect = new Rectangle2D.Float(x, 40, width, 5);
        
        g2.setColor(outlineColor);
        g2.draw(outline);
        
        g2.setColor(fillColor);
        g2.fill(outline);
                        
        if (horizontal) {
            x = (int) (outline.getX() + 5);
            y = (int) (outline.getY() + 25);
        } else {
            y = (int) (outline.getY() + 5);
            
            if (positionReference == TOP_LEFT || positionReference == BOTTOM_LEFT) {
                //Left Side
                x = (int) (outline.getX() + 5);                
            } else {
                //Right Side
                x = (int) (mapView.getDisplayWidth() - 18);   
            }
        }        
        
        for (Color c: colors) {
            colorRect.setRect(x, y, width, height);
            g2.setColor(c);
            g2.fill(colorRect);
            
            if (horizontal) {
                //increment x by the color box width
                x += width;
            } else {
                //increment y by the color box height
                y += height;
            }
            
        }
        
        if (labels != null) {
            g2.setColor(Color.WHITE);
            
            if (labels.size() > 0) {                                                 
                labelWidth  = (float) fontMetrics.getStringBounds(labels.get(labels.size() - 1), g2).getWidth();
                labelHeight = (float) fontMetrics.getStringBounds(labels.get(labels.size() - 1), g2).getHeight();
                                
                if (horizontal) {
                    g2.drawString(labels.get(labels.size() - 1), (float) (x - labelWidth), (float) (outline.getY() + 15));
                    
                    x = (int) (outline.getX() + 5); 
                    y = (int) (outline.getY() + 15);
                    
                    g2.drawString(labels.get(0), x, y);  
                } else {
                    if (positionReference == TOP_LEFT || positionReference == BOTTOM_LEFT) {
                        x = (int) (outline.getX() + outline.getWidth()   - 30);
                    } else {
                        x = (int) (outline.getX() + 4);
                    }
                    
                    if (labels.size() > 3) {
                        y = (int) (outline.getY() + 15);
                        
                        for (int i = 0; i < labels.size(); i++) {
                            currentLabel = labels.get(i);
                            labelY = (((height /2f) - (labelHeight / 2f)));
                            g2.drawString(currentLabel, (x + width + 1), (y + (height * i) + labelY));
                        }
                    } else {
                        y = (int) (outline.getY() + outline.getHeight()) - 8;
                        g2.drawString(labels.get(labels.size() - 1), x, y);

                        y = (int) (outline.getY() + 15);
                        g2.drawString(labels.get(0), x, y);                         
                    }
 
                }
              
            }
        }
    }

    public ArrayList<Color> getColors() {
        return this.colors;
    }
    
    private String getLongestLabel() {
        String longestLabel = "";
        
        for (String s: this.labels) {
            if (s.length() > longestLabel.length()) {
                longestLabel = s;
            }
        }
        
        return longestLabel;
    }
    
    public int getPositionReference() {
        return positionReference;
    }
    
    public boolean hasHorizontalOrientation() {
        return horizontal;
    }
    
    @Override
    public boolean isObjectWithinRectangle(Rectangle2D range) {
        return outline.intersects(range);
    }

    public void setFillColor(Color c) {
        this.fillColor = c;
    }    
    
    public void setHorizontal(boolean horizontal) {
        if (horizontal != this.horizontal) {
            //If the key is vertical we should have high values on top
            Collections.reverse(colors);   
            Collections.reverse(labels);             
        }
        
        this.horizontal = horizontal;
    }
    
    public void setOutlineColor(Color c) {
        this.outlineColor = c;
    }
    
    public void setPositionReference(int ref) {
        positionReference = ref;
    }        
    
    @Override
    public void toXML(XmlOutput kmlWriter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
