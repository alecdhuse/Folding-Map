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
package co.foldingmap.GUISupport.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * RangeSlider is a double value slider.  It allows the user to set a low and
 * a high value for a range.
 * 
 * It's visual representation is close to: --[3]----------[13]----
 * 
 * @author Alec
 */
public class RangeSlider extends JComponent implements MouseListener, MouseMotionListener {
    protected ArrayList<ChangeListener> changeListeners;
    protected boolean                   enabled, lowKnobClicked, highKnobClicked;
    protected Color                     barFillColor, barOutlineColor, labelColor, knobFill, knobOutline;
    protected RoundRectangle2D          highKnob, lowKnob, sliderBar, sliderFill;
    protected int                       insetLeft, insetRight;
    protected int                       maxValue, minValue;
    protected int                       lowValue, highValue;
    protected int                       width, height;
    protected int                       xLow, xHigh, boxY;
    protected int                       knobStops, stopPositionDiff; 
    protected int                       knobWidth;
    protected int                       clickedX;
    
    /**
     * Constructor specifying the minimum and maximum values for this slider.
     * 
     * @param minValue
     * @param maxValue 
     */
    public RangeSlider(int minValue, int maxValue) {
        this.insetLeft   = 10;
        this.insetRight  = 10;
        this.maxValue    = maxValue;
        this.minValue    = minValue;
        this.lowValue    = minValue;
        this.highValue   = maxValue;
        this.enabled     = true;
        
        this.barFillColor    = new Color(112, 192, 208); 
        this.barOutlineColor = new Color(168, 168, 168); 
        this.labelColor      = Color.WHITE;
        this.knobFill        = new Color(110, 110, 110);  
        this.knobOutline     = new Color( 81,  81,  81);  
        this.knobWidth       = 20;
        this.changeListeners = new ArrayList<ChangeListener>();
                
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }    
    
    /**
     * Adds a ChangeListener to this component. 
     * 
     * @param cl 
     */
    public void addChangeListener(ChangeListener cl) {
        changeListeners.add(cl);
    }    
    
    /**
     * Draws the knobs for the slider.
     * 
     * @param g2 
     */
    protected void drawKnobs(Graphics2D g2) { 
        int    lowLabelStart, highLabelStart;
        String lowLabel, highLabel;
        
        //ensure if at maximim that the max knob is at maximum
        if (highValue == maxValue)
            xHigh = insetLeft + width - insetRight - 8;
        
        highKnob = new RoundRectangle2D.Float(xHigh, boxY, knobWidth, height, 3, 3);
        lowKnob  = new RoundRectangle2D.Float(xLow,  boxY, knobWidth, height, 3, 3);
        
        g2.setColor(knobOutline);
        g2.draw(highKnob);
        
        //If buttons overlap only draw one
        if (lowValue != highValue)
            g2.draw(lowKnob);
        
        g2.setColor(knobFill);
        g2.fill(highKnob);
        
        //If buttons overlap only draw one
        if (lowValue != highValue)
            g2.fill(lowKnob);     
        
        //draw label
        if (enabled) {
            g2.setColor(labelColor);
        } else {
            g2.setColor(Color.LIGHT_GRAY);
        }
        
        lowLabel  = Integer.toString(lowValue);
        highLabel = Integer.toString(highValue);
        
        if (lowLabel.length() == 1) {
            lowLabelStart = xLow  + 6;
        } else if (lowLabel.length() == 2) {
            lowLabelStart = xLow  +  2;
        } else {
            lowLabelStart = xLow;
        }
        
        if (highLabel.length() == 1) {
            highLabelStart = xHigh  + 6;
        } else if (highLabel.length() == 2) {
            highLabelStart = xHigh  +  2;
        } else {
            highLabelStart = xHigh;
        }       
                
        g2.drawString(highLabel, highLabelStart, boxY + 13);
        
        if (lowValue != highValue)
            g2.drawString(lowLabel,  lowLabelStart,  boxY + 13);
    }    
    
    /**
     * Draws the slider bar for this slider.
     * 
     * @param g2 
     */
    protected void drawSliderBar(Graphics2D g2) {                               
        int siliderFillWidth, x;
                
        x = insetLeft;
        siliderFillWidth = (xHigh - xLow - 10);
                
        //ensure if at maximim that the slider fill is at maximum
        if (highValue == maxValue && (width - xLow > 0))
            siliderFillWidth = width - xLow;        
        
        sliderBar  = new RoundRectangle2D.Float(x, boxY, width, height, 3, 3);
        sliderFill = new RoundRectangle2D.Float(xLow + 10, boxY, siliderFillWidth, height, 3, 3);
                
        if (enabled) {
            g2.setColor(barFillColor);
        } else {
            g2.setColor(Color.GRAY);
        }
        
        g2.fill(sliderFill);   
        
        g2.setColor(barOutlineColor);
        g2.draw(sliderBar);                
    }    
    
    /**
     * A state change has occurred, notify the change listeners.
     */
    protected void fireStateChanged() {
        ChangeEvent ce = new ChangeEvent(this);
        
        for (ChangeListener l: changeListeners) {
            l.stateChanged(ce);
        }
    }    
    
    /**
     * Return the high value of the range.
     * 
     * @return 
     */
    public int getHighValue() {
        return highValue;
    }
    
    /**
     * Returns the low value of the range.
     * 
     * @return 
     */
    public int getLowValue() {
        return lowValue;
    }    
    
    /**
     * Returns the minimum size for this component.
     * 
     * @return 
     */
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(100, 18);
    }            
    
    /**
     * Handles object behavior for when it is clicked.
     * 
     * @param me 
     */
    @Override
    public void mouseClicked(MouseEvent me) {
        if (this.enabled) {
            int changed, changeValue, diff;
            int lowDiff  = me.getX() - xLow;
            int highDiff = xHigh - me.getX();

            if (sliderBar.contains(me.getX(), me.getY()) && enabled) {
                if (lowDiff < highDiff) {
                    diff        = clickedX - xLow;
                    changed     = (diff / stopPositionDiff);
                    changeValue = (lowValue + changed);

                    if (changed != 0 && changeValue <= highValue && changeValue >= minValue) {              
                        lowValue = lowValue + changed;          
                        fireStateChanged();
                    }
                } else if (highDiff < lowDiff) {
                    diff        = clickedX - xHigh;
                    changed     = (diff / stopPositionDiff);
                    changeValue = (highValue + changed);

                    if (changed != 0 && changeValue >= lowValue && changeValue <= maxValue) {
                        highValue = highValue + changed;
                        fireStateChanged();
                    }
                }
            }
        } //end enable check
        
        this.repaint();
    }

    /**
     * Handles the dragging behavior for this component.
     * 
     * @param me 
     */
    @Override
    public void mouseDragged(MouseEvent me) {    
        if (this.enabled) {
            int diff    = me.getX() - clickedX;
            int changed = (diff / stopPositionDiff);
            int changeValue;

            if (lowKnobClicked && highKnobClicked) {
                //buttons overlap
                if (changed < 0) {
                    //move low knob
                    changeValue     = (lowValue + changed);
                    highKnobClicked = false;

                    if (changeValue >= minValue) {                
                        lowValue = lowValue + changed;
                        clickedX = me.getX();
                        fireStateChanged();
                    }                
                } else if (changed > 0) {
                    //move high knob
                    changeValue    = (highValue + changed);
                    lowKnobClicked = false;

                    if (changeValue <= maxValue) {
                        highValue = highValue + changed;
                        clickedX  = me.getX();
                        fireStateChanged();
                    }                  
                }
            } else if (lowKnobClicked) {    
                changeValue = (lowValue + changed);

                if (changed != 0 && changeValue <= highValue && changeValue >= minValue) {                
                    lowValue = lowValue + changed;
                    clickedX = me.getX();
                    fireStateChanged();
                }
            } else if (highKnobClicked) {
                changeValue = (highValue + changed);

                if (changed != 0 && changeValue >= lowValue && changeValue <= maxValue) {
                    highValue = highValue + changed;
                    clickedX  = me.getX();
                    fireStateChanged();
                }            
            }                
        } //end enable check
        
        this.repaint();
    }    
    
    @Override
    public void mouseEntered(MouseEvent me) {}

    @Override
    public void mouseExited(MouseEvent me) {}    
    
    @Override
    public void mouseMoved(MouseEvent me) {}    
    
    /**
     * Handles the mouse pressed behavior for this component.
     * 
     * @param me 
     */
    @Override
    public void mousePressed(MouseEvent me) {
        clickedX = me.getX();
        
        if (lowKnob.contains(me.getX(), me.getY()) && enabled) {
            lowKnobClicked  = true; 
        }
        
        if (highKnob.contains(me.getX(), me.getY()) && enabled) {
            highKnobClicked = true;
        }        
    }

    /**
     * Handles the Mouse Released behavior for this component.
     * 
     * @param me 
     */
    @Override
    public void mouseReleased(MouseEvent me) {
        lowKnobClicked  = false; 
        highKnobClicked = false;
    }
    
    /**
     * Paints this component.
     * 
     * @param g 
     */
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        
        width  = (getWidth()  < getMaximumSize().width  ? getWidth() :  getMaximumSize().width);
        height = (getHeight() < getMaximumSize().height ? getHeight() : getMaximumSize().height);                
        width  = width  - (insetLeft + insetRight);          
        
        knobStops        = maxValue - minValue;
        stopPositionDiff = width / knobStops;
        boxY             = (height / 2) - 8;
        xLow             = (insetLeft) + (stopPositionDiff * lowValue);
        xHigh            = (insetLeft) + (stopPositionDiff * highValue) - 10;        
        height           = 16;              
        
        drawSliderBar(g2);
        drawKnobs(g2);
    }           
    
    /**
     * Sets if this component is enable or disabled.
     * 
     * @param enabled 
     */
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * Set the high value for the range.
     * 
     * @param highValue 
     */
    public void setHighValue(int highValue) {
        if (highValue <= maxValue && highValue  >= minValue)
            this.highValue = highValue;
        
        this.repaint();
    }
    
    /**
     * Set the low value for the range.
     * 
     * @param lowValue 
     */
    public void setLowValue(int lowValue) {
        if (lowValue >= minValue && lowValue <= maxValue)
            this.lowValue = lowValue;
        
        this.repaint();
    }  
    
    /**
     * Sets both the high and the low values for this Range Slider.
     * 
     * @param lowValue
     * @param highValue 
     */
    public void setValues(int lowValue, int highValue) {
        setLowValue(lowValue);
        setHighValue(highValue);
    }
}
