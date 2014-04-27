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

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author Alec
 */
public class ImageRenderInstruction implements HtmlRenderInstruction {
    protected float     x, y ,w, h;
    protected BufferedImage image;
    
    public ImageRenderInstruction(BufferedImage image, float x, float y) {
        this.image  = image;
        this.x      = x;
        this.y      = y;
        this.h      = image.getHeight();
        this.w      = image.getWidth();
    }    
    
    public ImageRenderInstruction(BufferedImage  image, float x, float y, float h, float w) {
        this.image  = image;
        this.x      = x;
        this.y      = y;
        this.h      = h;
        this.w      = w;
    }
    
    @Override
    public void draw(Graphics2D g2) {        
        BufferedImage img = image.getSubimage(0, 0, (int) w, (int) h);
        g2.drawImage(img, (int) x, (int) y, null);        
    }

    @Override
    public Rectangle2D getBounds(Graphics2D g2) {
        return new Rectangle2D.Float(x, y, w, h);
    }
    
}
