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

import co.foldingmap.Logger;
import co.foldingmap.ResourceHelper;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.PixelGrabber;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;

/**
 *
 * @author Alec
 */
public class ColorGradientComboBox  extends JComboBox {
    private DefaultComboBoxModel            comboBoxModel;
    private ImageIcon[]                     gradientImages;
    private ResourceHelper                  resourceHelper;
    
    public ColorGradientComboBox() {
        super();
        setupGradientStyle();
    }
    
    /**
     * Returns the Selected GradientImage's pixel data in an integer array.
     * 
     * @return 
     */
    public int[] getSelectedGradientPixelData() {
        Image           selectedGradient;
        int             selectedIndex;
        int[]           pixelData;
        PixelGrabber    grabber;
        
        try {
            selectedIndex    = getSelectedIndex();
            selectedGradient = gradientImages[selectedIndex].getImage();
            pixelData        = new int[256];   
            grabber          = new PixelGrabber(selectedGradient, 0, 0, 256, 1, pixelData, 0, 255);

            grabber.grabPixels();
            
            return pixelData;
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in ColorGradientComboBox.getSelectedGradientPixelData() - " + e);
            return null;
        }                
    }     
    
    /**
     * Sets up the GradientStyles
     */
    private void setupGradientStyle() {
        ImageListCellRenderer   renderer;
        Integer[]               comboBoxValues;
                
        comboBoxModel   = (DefaultComboBoxModel) this.getModel();
        comboBoxValues  = new Integer[7];
        gradientImages  = new ImageIcon[7];
        resourceHelper  = ResourceHelper.getInstance();
        
        for (int i = 0; i < gradientImages.length; i++) {
            comboBoxValues[i] = i; 
            comboBoxModel.addElement(i);      
        }
        
        try {
            gradientImages[0]  = resourceHelper.getImage("heatmap_gyr.png");
            gradientImages[1]  = resourceHelper.getImage("heatmap_classic.png");
            gradientImages[2]  = resourceHelper.getImage("heatmap_ta.png");
            gradientImages[3]  = resourceHelper.getImage("heatmap_fire.png");
            gradientImages[4]  = resourceHelper.getImage("heatmap_green.png");    
            gradientImages[5]  = resourceHelper.getImage("heatmap_blue.png");
            gradientImages[6]  = resourceHelper.getImage("heatmap_blue-red.png");            
            renderer           = new ImageListCellRenderer(gradientImages);
            
            renderer.setPreferredSize(new Dimension(256, 24));
            setRenderer(renderer);          
            setBackground(new Color(75, 75, 75));
            setMaximumSize(new Dimension(300, 30));
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in ColorGradientComboBox.setupGradientStyle() - " + e);
        }            
    }    
}
