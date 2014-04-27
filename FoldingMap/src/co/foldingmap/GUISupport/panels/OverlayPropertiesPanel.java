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

import co.foldingmap.GUISupport.SpringUtilities;
import co.foldingmap.Logger;
import co.foldingmap.map.Overlay;
import co.foldingmap.map.raster.ImageOverlay;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Alec
 */
public class OverlayPropertiesPanel extends ActionPanel {

    protected BoundaryPanel     boundaryPanel;
    protected Dialog            parentDialog;
    protected FileChoicePanel   fileChoicePanel;
    protected Overlay           overlay;
    protected JPanel            panelOverlayID;
    protected JTextField        textOverlayID;
    
    public OverlayPropertiesPanel(Dialog parentDialog, Overlay overlay) {
        this.parentDialog = parentDialog;
        this.overlay      = overlay;
        
        init();
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        ImageOverlay   groundOverlay = null;
        
        if (overlay instanceof ImageOverlay) {
            groundOverlay = (ImageOverlay) overlay;            
            boundaryPanel.setLatLonAltBox(groundOverlay.getBounds());
        }           
        
        if (ae.getActionCommand().equalsIgnoreCase("Ok")) {
            if (groundOverlay != null) {
                groundOverlay.setBoundary(boundaryPanel.getLatLonAltBox());
                groundOverlay.getMapIcon().setAddress(fileChoicePanel.getText());
            }
            
            overlay.setID(textOverlayID.getText());            
        } else if (ae.getSource() == fileChoicePanel) {
            groundOverlay.setDimension(getImageDimension(fileChoicePanel.getText()));            
        }
    }
    
    private Dimension getImageDimension(String fileAddress) {
        BufferedImage bi ;
        Dimension     d;
        
        try {
            bi = ImageIO.read(new File(fileAddress));     
            d  = new Dimension(bi.getWidth(), bi.getHeight());
            
        } catch (Exception e) {
            d  = new Dimension(0,0);
            Logger.log(Logger.ERR, "Error in OverlayPropertiesPanel.getImageDimension(String) - " + e);
        }
        
        return d;
    }
    
    private void init() {            
        boundaryPanel   = new BoundaryPanel(BoundaryPanel.HORIZONTAL);
        fileChoicePanel = new FileChoicePanel(parentDialog, FileChoicePanel.OPEN);
        panelOverlayID  = new JPanel();
        textOverlayID   = new JTextField(overlay.getID(), 30);
        
        textOverlayID.setMaximumSize(new Dimension(500, 28));
        this.setLayout(new SpringLayout());
        
        boundaryPanel.setBorder(new TitledBorder("Overlay Bounds"));
        fileChoicePanel.setBorder(new TitledBorder("Overlay Image"));
        panelOverlayID.setBorder(new TitledBorder("Overlay ID"));
        panelOverlayID.add(textOverlayID);
        
        this.add(panelOverlayID);
        this.add(fileChoicePanel);
        this.add(boundaryPanel);
        
        SpringUtilities.makeCompactGrid(this, 3, 1, 2, 2, 5, 5);  
        
        if (overlay instanceof ImageOverlay) {
            ImageOverlay   groundOverlay = (ImageOverlay) overlay;            
            boundaryPanel.setLatLonAltBox(groundOverlay.getBounds());
        }        
        
        fileChoicePanel.addActionListener(this);
    }
}
