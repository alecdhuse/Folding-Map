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

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import javax.swing.ImageIcon;

/**
 * Class for containing and drawing an Image at a given screen location.
 * Has methods for detecting overlap with other labels and images.
 * 
 * @author Alec
 */
public class PointImage extends MapLabel {
    protected BufferedImage image;
    protected float         x, y;
    protected ImageIcon     imageIcon;
    protected LookupOp      lookup;
    
    /**
     * Constructor using a BufferedImage
     * 
     * @param image
     * @param x
     * @param y 
     */
    public PointImage(BufferedImage image, LookupOp lookup, float x, float y) {
        this.image     = image;
        this.lookup    = lookup;
        this.x         = x;
        this.y         = y;
        this.labelArea = new Rectangle2D.Float(x, y, image.getWidth(), image.getHeight());
    }
    
    /**
     * Constructor using an ImageIcon
     * 
     * @param imageIcon
     * @param x
     * @param y 
     */
    public PointImage(ImageIcon imageIcon, float x, float y) {
        this.image     = null;
        this.imageIcon = imageIcon;
        this.x         = x;
        this.y         = y;
        this.labelArea = new Rectangle2D.Float(x, y, imageIcon.getIconWidth(), imageIcon.getIconHeight());
    }    
    
    /**
     * Method for drawing the the PointImage.
     * 
     * @param g2 
     */
    @Override
    public void drawLabel(Graphics2D g2) {
        //Buffered Image
        if (image != null) {
            if (lookup == null) {
                g2.drawImage(image, (int) x, (int) y, null);
            } else {
                //Image modification, usualy to show image is selected.
                g2.drawImage(image, lookup, (int) x, (int) y);
            }
        }
        if (imageIcon != null)
            g2.drawImage(imageIcon.getImage(), (int) x, (int) y, imageIcon.getImageObserver());
    }

    /**
     * Returns a Rectangle2D covering the area of the image.
     * 
     * @return 
     */
    @Override
    public Rectangle2D getLabelArea() {
        return this.labelArea;
    }

    /**
     * Returns if the area of this Image overlaps another given area.
     * 
     * @param label
     * @return 
     */
    @Override
    public boolean overlapsLabel(MapLabel label) {
        boolean     value    = false;
        Rectangle2D testArea = label.getLabelArea();
        
        if (labelArea != null) {
            if (labelArea.equals(testArea))
                value = true;

            if (testArea != null) 
                value = labelArea.intersects(testArea);    
        }
        
        return value;
    }
    
}
