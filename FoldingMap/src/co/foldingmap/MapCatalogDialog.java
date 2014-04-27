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

import co.foldingmap.GUISupport.OptionsPanel;
import co.foldingmap.GUISupport.TileServerOptionsPanel;
import co.foldingmap.GUISupport.panels.LiveFeedsPanel;
import co.foldingmap.GUISupport.panels.OSMImportOptionsPanel;
import co.foldingmap.map.DigitalMap;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Alec
 */
public class MapCatalogDialog extends JDialog implements ActionListener, ListSelectionListener {
    private   DefaultListModel       listModelDataSources;
    private   DigitalMap             mapData;
    private   ResourceHelper         helper; 
    private   JButton                buttonDownload, buttonCancel;
    private   JLabel                 labelOSM;
    private   JLabel[]               labelDataSources;
    private   JList                  listDataSources;
    private   JPanel                 panelButtons, panelDataSourceDetails;
    private   JScrollPane            spaneDataSources;
    private   LiveFeedsPanel         liveFeedsPanel;
    private   MainWindow             mainWindow;
    private   OptionsPanel           optionsPanel;
    private   OSMImportOptionsPanel  osmImportOptionsPanel;
    private   String                 osm, liveFeeds, mapQuest;        
    private   TileServerOptionsPanel tileServerOptions;
            
    public MapCatalogDialog(MainWindow mainWindow, DigitalMap mapData) {
        super(mainWindow, "Map Catalog");
        this.mainWindow = mainWindow;
        this.mapData    = mapData;
        
        init();        
        setComponentProperties();   
        setupDataSources();
        addObjectsToFrame();
        setupLocation();  
        
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {        
        if (ae.getSource() == buttonDownload) {
            this.setVisible(false);
            optionsPanel.actionPerformed(ae);
            mainWindow.update();
            this.dispose();
        } else if (ae.getSource() == buttonCancel) {
            this.dispose();
        }
       
    }

    /**
     * Adds objects to the main window and sets up the layout
     */
    private void addObjectsToFrame() {
        this.getContentPane().setLayout(new BorderLayout());
        this.add(spaneDataSources,       BorderLayout.WEST);
        this.add(panelDataSourceDetails, BorderLayout.CENTER);        
        this.add(panelButtons,           BorderLayout.SOUTH);
        
        panelDataSourceDetails.add(optionsPanel);
        panelButtons.add(buttonDownload);
        panelButtons.add(buttonCancel);
    }    
    
    private void init() {
        helper = ResourceHelper.getInstance();
        
        buttonCancel           = new JButton("Cancel");
        buttonDownload         = new JButton("Download");
        labelOSM               = new JLabel("Open Street Map");
        labelDataSources       = new JLabel[1];
        listModelDataSources   = new DefaultListModel();        
        listDataSources        = new JList(listModelDataSources);  
        liveFeedsPanel         = new LiveFeedsPanel(mainWindow, mapData);
        osmImportOptionsPanel  = new OSMImportOptionsPanel(mainWindow, mapData);
        panelButtons           = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelDataSourceDetails = new JPanel(new BorderLayout());
        spaneDataSources       = new JScrollPane(listDataSources);
        tileServerOptions      = new TileServerOptionsPanel(mapData);
                
        optionsPanel           = osmImportOptionsPanel;         
    }    
    
     /**
     * Set all the action commands, tool tips and others for objects used in this class.
     */
    private void setComponentProperties() {        
        labelOSM.setVerticalTextPosition(JLabel.BOTTOM);
        labelOSM.setHorizontalTextPosition(JLabel.CENTER);
        
        listDataSources.addListSelectionListener(this);
        listDataSources.setMinimumSize(new Dimension(300, 275));
        spaneDataSources.setMinimumSize(new Dimension(300, 275));
        
        buttonDownload.addActionListener(this);
        buttonCancel.addActionListener(this);      
    }

    private void setupDataSources() {
        osm       = "Open Street Map";
        liveFeeds = "Live Feeds";
        mapQuest  = "MapQuest Open Aerial";
                
        listModelDataSources.addElement(osm);
        listModelDataSources.addElement(mapQuest);
        listModelDataSources.addElement(liveFeeds);
        listDataSources.setSelectedIndex(0);
    }    
    
    /**
     * Sets up the location of the dialog box.
     */
    private void setupLocation() {        
        Toolkit   tk           = Toolkit.getDefaultToolkit();
        Dimension screenSize   = tk.getScreenSize();
        int       width        = 600;
        int       height       = 400;        
        int       screenHeight = screenSize.height;
        int       screenWidth  = screenSize.width;
        int       x            = (screenWidth  - width)  / 2;
        int       y            = (screenHeight - height) / 2;

        this.setSize(width, height);
        this.setLocation(x, y);
    }    
    
    @Override
    public void valueChanged(ListSelectionEvent lse) {
        String  selectedItem;
        
        selectedItem = (String) listDataSources.getSelectedValue();
        panelDataSourceDetails.removeAll();
        
        if (selectedItem.equals(liveFeeds)) {
            panelDataSourceDetails.add(liveFeedsPanel);
            optionsPanel = liveFeedsPanel;
        } else if (selectedItem.equals(mapQuest)) {    
            panelDataSourceDetails.add(tileServerOptions);  
            optionsPanel = tileServerOptions;
        } else if (selectedItem.equals(osm)) {
            panelDataSourceDetails.add(osmImportOptionsPanel);  
            optionsPanel = osmImportOptionsPanel;
        }                
                
        panelDataSourceDetails.repaint();
        panelDataSourceDetails.validate();
    }    
}