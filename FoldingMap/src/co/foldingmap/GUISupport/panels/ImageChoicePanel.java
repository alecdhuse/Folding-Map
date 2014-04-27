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
import co.foldingmap.map.themes.IconStyle;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

/**
 *
 * @author Alec
 */
public class ImageChoicePanel extends JPanel {
    private Dialog          parentDialog;
    private JLabel          labelImage;
    private FileChoicePanel fileChoicePanel;
    private IconStyle       iconStyle;
    
    public ImageChoicePanel(Dialog parentDialog) {
        this.parentDialog = parentDialog;
        
        init();
    }
    
    private void init() {
        labelImage      = new JLabel();
        fileChoicePanel = new FileChoicePanel(parentDialog);
        
        labelImage.setBackground(Color.WHITE);
        labelImage.setBorder(LineBorder.createBlackLineBorder());
        labelImage.setPreferredSize(new Dimension(40,40));
        labelImage.setHorizontalAlignment(JLabel.CENTER);
        
        fileChoicePanel.setPreferredSize(new Dimension(500, 35));
        fileChoicePanel.setMinimumSize(  new Dimension(500, 35));
                
        this.setLayout(new BorderLayout());
        this.add(labelImage,      BorderLayout.WEST);
        this.add(fileChoicePanel, BorderLayout.CENTER);       
    }   
    
    public void setIconStyle(IconStyle iconStyle) {
        try {
            this.iconStyle = iconStyle;
            labelImage.setIcon(iconStyle.getObjectImage());            
                    
            if (iconStyle.getImageFile() != null) {
                fileChoicePanel.setFile(iconStyle.getImageFile().getCanonicalPath());
            } else {
                fileChoicePanel.setFile("");
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error int ImageChoicePanel.setIconStyle(IconStyle) - " + e);
        }
    }
}
