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
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 *
 * @author Alec
 */
public class PointLabel extends MapLabel {
    public final static int     NORTH   = 1;
    public final static int     EAST    = 3;
    public final static int     SOUTH   = 5;
    public final static int     WEST    = 7;
    
    protected double            theta;        
    protected int               lineX1, lineX2; 
    protected int               lineY1, lineY2;
    protected int               posistion;
    protected int               signifiedX, signifiedY;            
    protected String            labelText;
    protected String            line1, line2;    
    
    /**
     * Constructor for objects of class PointLabel
     *
     * @param   String     labelText    The text of the label.
     * @param   LabelStyle labelStyle   The labelStyle object used to style the text.
     * @param   int        signifiedX   The horizontal location of the object being labeled.
     * @param   int        signifiedY   The vertical location of the object being labeled.
     * @param   double     theta        The angle to draw the label; the angle of rotation is in radians.
     *
     */
    public PointLabel(String labelText, LabelStyle labelStyle, int signifiedX, int signifiedY, double theta) {        
        this.labelFont   = labelStyle.getFont();
        this.labelText   = labelText;
        this.labelStyle  = labelStyle;
        this.posistion   = SOUTH;
        this.signifiedX  = signifiedX;
        this.signifiedY  = signifiedY;
        this.theta       = theta;
    }    
    
    /**
     * Creates the label to be displayed on the map.
     * 
     * @param g2
     * @param posistion 
     */
    private void createLabel(Graphics2D g2, int posistion) {           
        Rectangle2D lineArea1, lineArea2;

        line1  = "";
        line2  = "";
        lineX1 = 0;
        lineX2 = 0;
        lineY1 = 0;
        lineY2 = 0;
        
        ArrayList<String> lines = getLabelLines();
        
        try {
            if (lines.size() > 1) {
                line1 = lines.get(0);
                line2 = lines.get(1);
                lineArea1 = fontMetrics.getStringBounds(line1, g2);
                lineArea2 = fontMetrics.getStringBounds(line2, g2);

                if (posistion == EAST) {
                    lineX1    = (signifiedX + 6);
                    lineX2    = (signifiedX + 6);                                                                    
                } else if (posistion == SOUTH) {
                    lineX1    = (int) (signifiedX - (lineArea1.getWidth() / 2));
                    lineX2    = (int) (signifiedX - (lineArea2.getWidth() / 2));
                    lineY1    = signifiedY;
                    lineY2    = signifiedY + 12;
                }

                labelArea = new Rectangle2D.Double(lineArea1.getX(),
                                                   lineArea1.getY(),
                                                   Math.max(lineArea1.getWidth(), lineArea2.getWidth()),
                                                   lineArea1.getHeight() + lineArea2.getHeight() + 12);                            
            } else {
                if (posistion == EAST) {
                    lineArea1 = fontMetrics.getStringBounds(lines.get(0), g2);
                    labelArea = lineArea1;
                    lineX1    = signifiedX +  6;
                    lineY1    = signifiedY - 14;
                    line1     = labelText;
                } else if (posistion == SOUTH) {
                    lineArea1 = fontMetrics.getStringBounds(lines.get(0), g2);
                    labelArea = lineArea1;
                    lineX1    = (int) (signifiedX - (lineArea1.getWidth() / 2));
                    lineY1    = signifiedY;
                    line1     = labelText;
                }
            }   
        } catch (Exception e) {
            System.err.println("Error in PointLabel.createLabel(g2, int) - " + e);
        }
    }   
    
    /**
     * Draws this TextLabel object.
     *
     * @param   Graphics2D g2           The graphics object used to draw.
     */
    @Override
    public void drawLabel(Graphics2D g2) {
        try {
            if (theta != 0) {
                g2.rotate(theta * -1);
                drawLabel(labelStyle, g2);
                g2.rotate(theta);                    
            } else {                
                drawLabel(labelStyle, g2);
            }      
        } catch (Exception e) {
            System.err.println("Error in PointLabel.drawLabel(Graphics2D) - " + e);
        }
    }

    private void drawLabel(LabelStyle labelStyle, Graphics2D g2) {    
        try {
            fontMetrics = g2.getFontMetrics(labelFont);

            g2.setFont(labelFont);

            createLabel(g2, posistion);

            if (labelStyle.isLabelVisible()) {                
                //draw outline
                g2.setColor(labelStyle.getOutlineColor());
                g2.drawString(line1, (lineX1 - 1), (lineY1 + 0));
                g2.drawString(line1, (lineX1 + 1), (lineY1 - 0));
                g2.drawString(line1, (lineX1 - 0), (lineY1 + 1));
                g2.drawString(line1, (lineX1 + 0), (lineY1 - 1));

                g2.drawString(line2, (lineX2 - 1), (lineY2 + 0));
                g2.drawString(line2, (lineX2 + 1), (lineY2 + 0));
                g2.drawString(line2, (lineX2 - 0), (lineY2 + 1));
                g2.drawString(line2, (lineX2 + 0), (lineY2 - 1));

                //draw center
                g2.setColor(labelStyle.getFillColor());
                g2.drawString(line2, (lineX2 + 0), (lineY2));
                g2.drawString(line1, (lineX1 + 0), (lineY1));
            }
        } catch (Exception e) {
            System.err.println("Error in PointLabel.drawLabel(LabelStyle, Graphics2D, int, int) - " + e);
        }            
    }    
    
    /**
     * Creates the Rectangle2D that represents this Label's area.
     * Used to help prevent overlapping of labels.
     * 
     * @param g2
     * @param labelPosistion 
     */
    public void generateLabelArea(Graphics2D g2, int labelPosistion) {
        ArrayList<String>   labelLines;
        Rectangle2D         lineArea1, lineArea2;
        
        try {
            labelLines  = getLabelLines();
            posistion   = labelPosistion;
            fontMetrics = g2.getFontMetrics(labelFont);

            if (labelLines.size() > 1) {
                line1 = labelLines.get(0);
                line2 = labelLines.get(1);
                lineArea1 = fontMetrics.getStringBounds(line1, g2);
                lineArea2 = fontMetrics.getStringBounds(line2, g2);

                if (labelPosistion == EAST) {
                    lineX1    = (signifiedX + 6);
                    lineX2    = (signifiedX + 6);   
                    lineY1    = signifiedY - 14;
                } else if (labelPosistion == SOUTH) {
                    lineX1    = (int) (signifiedX - (lineArea1.getWidth() / 2));
                    lineX2    = (int) (signifiedX - (lineArea2.getWidth() / 2));
                    lineY1    = signifiedY;
                    lineY2    = signifiedY + 12;
                }

                labelArea = new Rectangle2D.Double(lineArea1.getX(),
                                                   lineArea1.getY(),
                                                   Math.max(lineArea1.getWidth(), lineArea2.getWidth()),
                                                   lineArea1.getHeight() + lineArea2.getHeight() + 12);                            
            } else {
                if (labelPosistion == EAST) {
                    lineArea1 = fontMetrics.getStringBounds(labelLines.get(0), g2);                
                    lineX1    = signifiedX +  0;
                    lineY1    = signifiedY - 10;
                    line1     = labelText;
                    labelArea = new Rectangle2D.Double(lineX1, lineY1, lineArea1.getWidth() + 10, lineArea1.getHeight() + 10);
                } else if (labelPosistion == SOUTH) {
                    lineArea1 = fontMetrics.getStringBounds(labelLines.get(0), g2);
                    lineX1    = (int) (signifiedX - (lineArea1.getWidth() / 2) - 5);
                    lineY1    = signifiedY - 5;
                    line1     = labelText;
                    labelArea = new Rectangle2D.Double(lineX1, lineY1, lineArea1.getWidth() + 10, lineArea1.getHeight() + 10);
                }
            }        
        } catch (Exception e) {
            System.err.println("Error in PointLabel.generateLabelArea() - " + e);
        }
    }
    
    /**
     * Returns the Rectangle2D representing the area of this label.
     * 
     * @return 
     */
    @Override
    public Rectangle2D getLabelArea() {
        return labelArea;
    }

    /**
     * Returns the label text broken up into lines to be displayed.
     * 
     * @return 
     */
    private ArrayList<String> getLabelLines() {
        ArrayList<String> lines = new ArrayList<String>();
        String            subText;
                
        try {
            if (labelText.length() > 12) {
                for (int i = labelText.length(); i > 0; i--) {
                    subText = labelText.substring(i - 1, i);

                    if (subText.equals(" ") || subText.equals("-")) {
                        lines.add(labelText.substring(0, i));
                        lines.add(labelText.substring(i));
                    }
                }

                if (lines.isEmpty()) {
                    lines.add(labelText);
                }
            } else {
                lines.add(labelText);
            }         
        } catch (Exception e) {
            System.err.println("Error in PointLabel.getLabelLines() - " + e);
        }
        
        return lines;
    }    
    
    /**
     * Creates a point that indicates the on screen location that the first line
     * of this label will be drawn.
     * 
     * @return 
     */
    public Point2D.Float getLine1StartPoint() {
        return new Point2D.Float(lineX1, lineY1);
    }
    
    /**
     * Creates a point that indicates the on screen location that the second 
     * line of this label will be drawn.
     * 
     * @return 
     */
    public Point2D.Float getLine2StartPoint() {
        return new Point2D.Float(lineX2, lineY2);
    }    
    
    /**
     * Returns a String for the first line of this label
     * @return 
     */
    public String getLine1Text() {
        return line1;
    }
    
    /**
     * Returns a String for the first line of this label
     * @return 
     */
    public String getLine2Text() {
        return line1;
    }    
    
    /**
     * Returns if this label overlaps another label.
     * 
     * @param otherLabel
     * @return 
     */
    @Override
    public boolean overlapsLabel(MapLabel otherLabel) {
        boolean     value    = false;
        Rectangle2D testArea = otherLabel.getLabelArea();
        
        if (labelArea.equals(testArea))
            value = true;
        
        if (labelArea != null && testArea != null) 
            value = labelArea.intersects(testArea);        
        
        return value;
    }  
    
}
