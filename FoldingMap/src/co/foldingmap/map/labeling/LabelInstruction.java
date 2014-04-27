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

import java.awt.geom.Point2D;

/**
 * This class represents the instructions on how to draw the label.
 * Each instruction is comprised of an angle to draw a string, the focus of
 * rotation, an x and y to draw the text and a string representing all or part
 * of the label.
 * 
 * @author Alec
 */
public class LabelInstruction {
    float   angle;          //Angle in Radians
    float   textX, textY;
    Point2D rotationFocus;
    String  text;
    
    /**
     * Constructor for LabelInstruction
     * 
     * @param angle         The angle to draw this potion of the label.
     * @param rotationFocus The focus of the rotation.
     * @param textX         The x position of where to draw the label.
     * @param textY         The y position of where to draw the label.
     * @param text          The text representing this portion of the label.
     */
    public LabelInstruction(float angle, Point2D rotationFocus, float textX, float textY, String text) {
        this.angle          = angle;
        this.rotationFocus  = rotationFocus;
        this.textX          = textX;
        this.textY          = textY;
        this.text           = text;
        
        //TODO: Create a box that acts as the bounds for this instruction.
        if (angle > 0) {
            
        } else {
            
        }
    }
    
    /**
     * Returns if the LabelInstruction is equal to another.
     * 
     * @param li
     * @return 
     */
    public boolean equals(LabelInstruction li) {
        if (li.angle == angle &&
            li.rotationFocus.equals(rotationFocus) &&    
            li.textX == textX &&
            li.textY == textY &&    
            li.text.equals(text)) {
            
            return true;
        } else {
            return false;
        }
    }    
    
    /**
     * Returns the angle in radians of this instruction.
     * @return 
     */
    public float getAngle() {
        return this.angle;
    }
    
    /**
     * Returns the Point2D of the rotational focus of this instruction.
     * 
     * @return 
     */
    public Point2D getRotationFocus() {
        return this.rotationFocus;
    }
    
    /**
     * Returns the text for this instruction.
     * 
     * @return 
     */
    public String getText() {
        return this.text;
    }
    
    /**
     * Returns the x position of this label.
     * 
     * @return 
     */
    public float getX() {
        return this.textX;
    }
    
    /**
     * Returns the y position of this label.
     * 
     * @return 
     */
    public float getY() {
        return this.textY;
    }    
}
