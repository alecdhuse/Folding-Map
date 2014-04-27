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
package co.foldingmap;

import co.foldingmap.GUISupport.panels.ActionPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 *
 * @author Alec
 */
public class LayerProperties extends JDialog implements ActionListener {
    private ActionPanel panelLayerOptions;
    private JButton     buttonCancel, buttonOk;
    private JPanel      panelButtons;
    private MainWindow  mainWindow;
    
    public LayerProperties(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        
        init();
        setupLocation();
    }
    
    public LayerProperties(MainWindow mainWindow, ActionPanel panelLayerOptions) {
        this.panelLayerOptions = panelLayerOptions;
        this.mainWindow        = mainWindow;
        
        init();
        addObjectsToFrame();
        setupLocation();
        
        this.setVisible(true);
    }  
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == buttonCancel) {
            this.dispose();
        } else if (ae.getSource() == buttonOk) {
            panelLayerOptions.actionPerformed(ae);
            mainWindow.updateLayersTree();
            this.dispose();
        }
    }    
    
    private void addObjectsToFrame() {        
        this.add(panelLayerOptions, BorderLayout.CENTER);
        this.add(panelButtons,      BorderLayout.SOUTH);
        
        this.validate();
    }
    
    private void init() {                
        buttonCancel    = new JButton("Cancel");
        buttonOk        = new JButton("Ok");
        panelButtons    = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        buttonCancel.addActionListener(this);
        buttonCancel.setActionCommand("Cancel");
        
        buttonOk.addActionListener(this);
        buttonOk.setActionCommand("Ok");
        
        this.setLayout(new BorderLayout());
        this.panelButtons.add(buttonOk);
        this.panelButtons.add(buttonCancel);
    }
    
    /**
     * Sets the Layer Options Panel to be used in this Dialog.
     * 
     * @param layerPanel 
     */
    public void setLayerPanel(ActionPanel layerPanel) {
        panelLayerOptions = layerPanel;                
        addObjectsToFrame();
    }
    
    /**
     * Sets up the location of the dialog box.
     */
    private void setupLocation() {        
        Toolkit   tk           = Toolkit.getDefaultToolkit();
        Dimension screenSize   = tk.getScreenSize();
        int       width        = 460;
        int       height       = 500;        
        int       screenHeight = screenSize.height;
        int       screenWidth  = screenSize.width;
        int       x            = (screenWidth  - width)  / 2;
        int       y            = (screenHeight - height) / 2;

        this.setSize(width, height);
        this.setLocation(x, y);
    }


}
