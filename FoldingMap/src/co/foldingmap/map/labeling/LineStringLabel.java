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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * This class is used to label LineString on the map.
 * 
 * @author Alec
 */
public class LineStringLabel extends MapLabel {
    private ArrayList<LabelInstruction> instructions;
    
    /**
     * Basic constructor for LineStringLabel, sets all options to default.
     */
    public LineStringLabel(Graphics2D g2) {
        this.complete           = false;
        this.fillColor          = new Color(75, 68, 60);
        this.labelFont          = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
        this.fontMetrics        = g2.getFontMetrics(labelFont);
        this.instructions       = new ArrayList<LabelInstruction>();        
    }
    
    /**
     * Creates a new draw instruction and adds it to the list of daw instructions.
     * 
     * @param angle         The angle to draw this potion of the label.
     * @param rotationFocus The focus of the rotation.
     * @param textX         The x position of where to draw the label.
     * @param textY         The y position of where to draw the label.
     * @param text          The text representing this portion of the label.
     */
    public void addLabelInstruction(float angle, Point2D rotationFocus, float textX, float textY, String text) {
        instructions.add(new LabelInstruction(angle, rotationFocus, textX, textY, text));
    }   
    
    /**
     * Draws the label on the given graphics class.
     * 
     * @param g2 
     */
    @Override
    public void drawLabel(Graphics2D g2) {
        AffineTransform at, originalTransform;
        
        originalTransform = g2.getTransform();
        
        g2.setFont(labelFont);             
        
        for (LabelInstruction li: instructions) {
            at = g2.getTransform();
            
            if (li.angle != 0) {
                at.rotate(li.angle, li.rotationFocus.getX(), li.rotationFocus.getY());                       
                g2.setTransform(at);                                           
            }

            //draw outline
            if (this.outlineColor != null) {
                g2.setColor(outlineColor);   
                g2.drawString(li.text, (li.textX - 1), (li.textY + 0));  
                g2.drawString(li.text, (li.textX + 1), (li.textY - 0));  
                g2.drawString(li.text, (li.textX - 0), (li.textY + 1));  
                g2.drawString(li.text, (li.textX + 0), (li.textY - 1));  
            }
            
            g2.setColor(fillColor);   
            g2.drawString(li.text, li.textX, li.textY);                            
            g2.setTransform(originalTransform);                   
        }
    }    
    
    /**
     * Tests to see if this LineStringLabel is equal to another.
     * TODO: Complete
     * 
     * @param lsl
     * @return 
     */
    public boolean equal(LineStringLabel lsl) {
        ArrayList<LabelInstruction> i1, i2;
        boolean                     equal;
        
        equal = false;
        i1    = this.getLabelInstruction();
        i2    = lsl.getLabelInstruction();
        
        if (i1.size() == i2.size()) {
            for (int i = 0; i < i1.size(); i++) {
                if (i1.get(i).equals(i2.get(i))) {
                    equal = true;
                } else {
                    equal = false;
                    break;
                }
            }
        } else {
            equal = false;
        }
        
        return false;
    }
    
    /**
     * Generates a Rectangle2D representing the area of this label.
     * 
     * @param g2 
     */
    public void generateLabelArea(Graphics2D g2) {
        double      x, y, w, h; 
        double      sectionX, sectionY, sectionW, sectionH;
        double      xMod, yMod;
        Rectangle2D sectionBounds, totalBounds;
                
        totalBounds = null;
        fontMetrics = g2.getFontMetrics(labelFont);
        
        //TODO: Finish this method.
        for (LabelInstruction li: instructions) {
            sectionBounds = fontMetrics.getStringBounds(li.text, g2);
            xMod          = (sectionBounds.getHeight() * 0.153);
            yMod          = (sectionBounds.getHeight() / 1.4);
        
            //initial values
            if (totalBounds == null) {
                x = li.textX;
                y = li.textY;
                w = 0;
                h = 0;
                
                totalBounds = new Rectangle2D.Float(0,0,0,0);
            } else {                
                x = totalBounds.getX();
                y = totalBounds.getY();
                w = totalBounds.getWidth();
                h = totalBounds.getHeight();
            }
            
            
            
            if (li.text.length() > 0) {
                if (li.angle == Math.toRadians(0)) {
                    
                    
                    sectionX = sectionBounds.getX() + li.textX;
                    sectionY = sectionBounds.getY() + li.textY;
                    
                    if (sectionX < x)
                        x = sectionX;

                    if (sectionY < y)
                        y = sectionY;                        

                    if (sectionBounds.getWidth() > w)
                        w = sectionBounds.getWidth();                            

                    if (sectionBounds.getHeight() > h)
                        h = sectionBounds.getHeight();                                                                           
                } else if (li.angle < 0 && li.angle > Math.toRadians(-90)) {
                    sectionX = (li.textX - xMod);
                    sectionY = (li.textY - yMod);
                    sectionW = sectionBounds.getWidth()  + xMod;
                    sectionH = sectionBounds.getHeight() + yMod;
                    
                    if (sectionX < totalBounds.getX())
                        x = sectionX;                    
                    
                    if (sectionY < totalBounds.getY())
                        y = sectionY;    
                    
                    if (sectionW > w)
                        w = sectionW;                            

                    if (sectionH > h)
                        h = sectionH;                     
                } else {
                    sectionX = (li.textX - xMod);
                    sectionY = (li.textY - yMod);
                    sectionW = sectionBounds.getWidth()  + xMod;
                    sectionH = sectionBounds.getHeight() + yMod;
                    
                    if (sectionX < totalBounds.getX())
                        x = sectionX;                    
                    
                    if (sectionY < totalBounds.getY())
                        y = sectionY;    
                    
                    if (sectionW > w)
                        w = sectionW;                            

                    if (sectionH > h)
                        h = sectionH;                     

                }

                //set new bounds
                totalBounds.setRect(x, y, w, h);
            }
            
        }
        
        this.labelArea = totalBounds;
    }    
    
    /**
     * Returns a Rectangle2D representing the area of this label.
     * 
     * @return 
     */
    @Override
    public Rectangle2D getLabelArea() {        
        return labelArea;
    }    
    
    /**
     * Returns the ArrayList of LabelInstructions for this Label;
     * 
     * @return 
     */
    public ArrayList<LabelInstruction> getLabelInstruction() {
        return instructions;
    }
    
    /**
     * Returns the number of draw instructions for this label.
     * 
     * @return 
     */
    public int getNumberOfInstructions() {
        return instructions.size();
    }    
    
    /**
     * Returns if the construction of this label is complete.
     * 
     * @return 
     */
    @Override
    public boolean isComplete() {
        return complete;
    }    
    
    /**
     * Returns if this label overlaps another.
     * 
     * @param label
     * @return 
     */
    @Override
    public boolean overlapsLabel(MapLabel label) {
//        return false;
        
        if (labelArea != null) {
            boolean     value    = false;
            Rectangle2D testArea = label.getLabelArea();

            if (labelArea.equals(testArea))
                value = true;

            if (labelArea != null && testArea != null) 
                value = labelArea.intersects(testArea);        

            return value;        
        } else {
            return false;
        }
    }   
    
    /**
     * Sets if the construction of this label is complete, having all the 
     * necessary draw instructions saved.
     * 
     * @param complete 
     */
    public void setComplete(boolean complete) {
        this.complete = complete;
    }    
}