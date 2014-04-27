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

import co.foldingmap.GUISupport.panels.MapPropertiesPanel;
import co.foldingmap.GUISupport.panels.ThemePanel;
import co.foldingmap.map.DigitalMap;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author Alec
 */
public class MapPropertiesWindow extends JDialog implements ActionListener {
    private DigitalMap          mapData;
    private JButton             okButton, cancelButton;
    private JPanel              buttonPanel;
    private JTabbedPane         mainTabs;
    private MainWindow          mainWindow;
    private MapPropertiesPanel  mapPropertiesPanel;
    private ThemePanel          themePanel;
        
    public MapPropertiesWindow(MainWindow mainWindow, DigitalMap mapData) {
        //super("Map Properties");
        
        this.mainWindow = mainWindow;
        this.mapData    = mapData;
        
        init();
        setupFrame();
        setupLocation();        
        setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == cancelButton) {
            this.dispose();
        } else if (ae.getSource() == okButton) {
            this.dispose();
        }
    }    
    
    private void init() {
        buttonPanel         = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cancelButton        = new JButton("Cancel");
        mainTabs            = new JTabbedPane();
        mapPropertiesPanel  = new MapPropertiesPanel(mapData);
        okButton            = new JButton("Ok");        
        themePanel          = new ThemePanel(this, mapData);
        
        cancelButton.addActionListener(this);
        okButton.addActionListener(this);
        themePanel.addUpdateable(mainWindow);
    }
    
    private void setupFrame() {
        this.setLayout(new BorderLayout());
        this.add(buttonPanel, BorderLayout.SOUTH);
        this.add(mainTabs,    BorderLayout.CENTER);
        
        buttonPanel.add(okButton);
        //buttonPanel.add(cancelButton);        
        
        mainTabs.add("Information", mapPropertiesPanel);
        mainTabs.add("Theme",       themePanel);
    }
    
    /**
     * Sets up the location of the dialog box.
     */
    private void setupLocation() {        
        Toolkit   tk           = Toolkit.getDefaultToolkit();
        Dimension screenSize   = tk.getScreenSize();
        int       width        = 570;
        int       height       = 500;        
        int       screenHeight = screenSize.height;
        int       screenWidth  = screenSize.width;
        int       x            = (screenWidth  - width)  / 2;
        int       y            = (screenHeight - height) / 2;

        this.setSize(width, height);
        this.setLocation(x, y);
    }


}
