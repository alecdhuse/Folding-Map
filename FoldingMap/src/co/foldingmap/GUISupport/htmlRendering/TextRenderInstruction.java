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
package co.foldingmap.GUISupport.htmlRendering;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Alec
 */
public class TextRenderInstruction implements HtmlRenderInstruction {
    protected Color  color;
    protected float  x, y;
    protected Font   font;
    protected String text;
    
    public TextRenderInstruction(String text, Font font, float x, float y) {
        this.text = text;
        this.font = font;
        this.x    = x;
        this.y    = y;
    }

    @Override
    public void draw(Graphics2D g2) {
        if (font != null)
            g2.setFont(font);
        
        if (color != null)
            g2.setColor(color);
        
        g2.drawString(text, x, y);
    }
    
    /**
     * Returns a Rectangle2D with the bounds for this Instruction
     * 
     * @param g2
     * @return 
     */
    @Override
    public Rectangle2D getBounds(Graphics2D g2) {
        FontMetrics fontMetrics = g2.getFontMetrics();
        Rectangle2D bounds      = fontMetrics.getStringBounds(text, g2);
        
        bounds.setRect(x, y, bounds.getWidth(), bounds.getHeight());
        
        return bounds;
    }
}
