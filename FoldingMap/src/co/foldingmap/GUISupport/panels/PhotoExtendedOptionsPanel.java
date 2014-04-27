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
package co.foldingmap.GUISupport.panels;

import co.foldingmap.Logger;
import co.foldingmap.ResourceHelper;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;
import java.io.File;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 *
 * @author Alec
 */
public class PhotoExtendedOptionsPanel extends    ExtendedOptionsPanel 
                                       implements MouseListener, MouseMotionListener, MouseWheelListener {
    protected boolean   firstPaint;
    protected float     photoZoom;      
    protected ImageIcon objectImage;
    protected int       currentX, currentY;
    protected int       dragStartX, dragStartY;
    protected int       drawX, drawY;
    protected int       drawHeight, drawWidth;
    
    public PhotoExtendedOptionsPanel(String photoFile) {
        try {
            ResourceHelper helper = ResourceHelper.getInstance();           
            this.objectImage = helper.getImage(photoFile);
            init();
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in PhotoExtendedOptionsPanel constructor(File) - " + e);
        }
    }   

    public PhotoExtendedOptionsPanel(URL photoURL) {
        try {
            this.objectImage = new ImageIcon(photoURL);
            init();
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in PhotoExtendedOptionsPanel constructor(URL) - " + e);
        }
    }       
    
    @Override
    public void actionPerformed(ActionEvent ae) {}
    
    private void init() {
        this.setBackground(new Color(68,68,68));
        this.photoZoom  = 1;
        this.drawX      = 0;
        this.drawY      = 0;      
        this.firstPaint = true;
        
        this.addMouseListener(this);   
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);                                        
        updateDrawProportions();
        
        if (firstPaint == true) {
            firstPaint = false;
            centerImage();
        }
        
        g.drawImage(objectImage.getImage(), drawX, drawY, drawWidth, drawHeight,  null);
    }
    
    @Override
    public void save() {}

    @Override
    public void mouseClicked(MouseEvent me) {
        if (me.getButton() == MouseEvent.BUTTON1) {
            //Left, Zoom In
            photoZoom += 1;
            drawX += (int) (drawX * (1f / (float) photoZoom));
            drawY += (int) (drawY * (1f / (float) photoZoom));             
        } else if (me.getButton() == MouseEvent.BUTTON2) {
            //Center, do nothing
        } else if (me.getButton() == MouseEvent.BUTTON3) {
            //Right, Zoom Out
            photoZoom -= 1;
            drawX -= (int) (drawX * (1f / (float) photoZoom));
            drawY -= (int) (drawY * (1f / (float) photoZoom));              
        }
        
        if (photoZoom <= 0) {
            photoZoom  = 1;
            this.drawX = 0;
            this.drawY = 0;             
        } 
        
        this.repaint();
    }

    @Override
    public void mousePressed(MouseEvent me) {
        dragStartX = me.getX() - drawX;
        dragStartY = me.getY() - drawY;       
    }

    @Override
    public void mouseReleased(MouseEvent me) {}

    @Override
    public void mouseEntered(MouseEvent me) {}

    @Override
    public void mouseExited(MouseEvent me) {}

    @Override
    public void mouseDragged(MouseEvent me) {
        this.drawX = (me.getX() - dragStartX);
        this.drawY = (me.getY() - dragStartY);
        
        //Horizontal boundaries 
        if (drawX > this.getWidth())
            drawX = this.getWidth()  - 3;
        
        if (drawX + drawWidth < 0)
            drawX = (drawWidth * -1) + 3;        
        
        //Vertical boundaries
        if (drawY > this.getHeight())
            drawY = this.getHeight() - 3;
        
        if (drawY + drawHeight < 0)
            drawY = (drawHeight * -1) + 3;
        
        this.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        currentX = me.getX();
        currentY = me.getY();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
        if (mwe.getWheelRotation() > 0) {
            //Zoom Out
            photoZoom -= 1;
            drawX -= (int) (drawX * (1f / (float) photoZoom));
            drawY -= (int) (drawY * (1f / (float) photoZoom));               
        } else if (mwe.getWheelRotation() < 0) {
            //Zoom In
            photoZoom += 1;
            drawX += (int) (drawX * (1f / (float) photoZoom));
            drawY += (int) (drawY * (1f / (float) photoZoom));               
        }        
        
        if (photoZoom <= 0) {
            photoZoom  = 1;
            centerImage();            
        } 
        
        this.repaint();
    }
    
    public void centerImage() {
        int panelCenterX = (int) (this.getWidth()  / 2.0);
        int panelCenterY = (int) (this.getHeight() / 2.0);
        int imageCenterX = (int) (drawWidth  / 2.0);
        int imageCenterY = (int) (drawHeight / 2.0);    

        drawX = panelCenterX - imageCenterX;
        drawY = panelCenterY - imageCenterY;            
    }
    
    private void updateDrawProportions() {
        int imageHeight = objectImage.getIconHeight();
        int imageWidth  = objectImage.getIconWidth();        
        int frameHeight = this.getHeight();
        int frameWidth  = this.getWidth();
        
        if (frameHeight > frameWidth) {
            drawHeight = frameHeight;
            drawWidth  = (int) ((double) frameWidth * ((double) imageHeight / (double) imageWidth));
        } else {
            drawWidth  = frameWidth;
            drawHeight = (int) ((double) frameHeight * ((double) imageHeight / (double) imageWidth));
        }
        
        drawHeight *= photoZoom;
        drawWidth  *= photoZoom;        
    }
}
