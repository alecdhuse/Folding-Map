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

import co.foldingmap.GUISupport.panels.OsmLocationSearchPanel;
import co.foldingmap.actions.Actions;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.MercatorProjection;
import co.foldingmap.map.themes.Web;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.LatLonBox;
import co.foldingmap.mapImportExport.BoundsSearchResult;
import co.foldingmap.mapImportExport.FmXmlImporter;
import co.foldingmap.mapImportExport.MapImporter;
import co.foldingmap.mapImportExport.OsmOverpassDownloader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Alec
 */
public class WelcomeDialog extends    JDialog 
                           implements ActionListener, 
                                      ListSelectionListener,
                                      MouseListener {    
    
    private boolean                 ignoreNextSelection;
    private JButton                 buttonOK, buttonOpenFromDisk;
    private JList                   listNewMaps, listRecentMaps;
    private JPanel                  panelButtons, panelCenter, panelLeft, panelRight;
    private JScrollPane             recentFilesScroll;
    private MainWindow              mainWindow;
    private OsmLocationSearchPanel  searchPanel;
    private String[]                newMapOptions = {"OpenStreetMap.org", "World Map", "Blank Map"};
    private UserConfig              userConfig;
    
    public WelcomeDialog(MainWindow mainWindow, UserConfig userConfig) {
        super(mainWindow);
        
        this.mainWindow = mainWindow;
        
        init();
        setupLocation();
        addObjectsToFrame();
        setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {         
        DigitalMap mapData;
        File       mapFile; 
        
        if (ae.getSource() == buttonOK) {            
            if (listNewMaps.getSelectedValue() != null) {
                openNewMap();
            } else if (listRecentMaps.getSelectedValue() != null) {
                openRecentMap();
            } else {
                //OSM Map
                try {
                    OsmOverpassDownloader osmDownloader;
                    BoundsSearchResult    result  = searchPanel.getSelectedResult();
                    LatLonBox             bounds  = result.getBounds();
                    MercatorProjection    proj    = new MercatorProjection(bounds.getNorth(), bounds.getWest(), 11); 
                    
                    mapData = new DigitalMap(result.getName(), proj);
                    mapData.setLookAtCoordinate(new Coordinate(0, bounds.getNorth(), bounds.getWest())); 
                    mapData.setTheme(new Web(), null, null);
                    mainWindow.setMap(mapData);
                    osmDownloader = new OsmOverpassDownloader(mainWindow, mapData, bounds);
                    osmDownloader.start();
                } catch (Exception e) {
                    Logger.log(Logger.ERR, "Error in WelcomeDialog.actionPerformed(ActionEvent) - Cannot open OSM location.");
                }                    
            }
        } else if (ae.getSource() == buttonOpenFromDisk) {
            Actions actions = new Actions(new DigitalMap(), mainWindow.getMapPanel());
            actions.openMap(mainWindow, mainWindow.getMapPanel());    
        }
        
        this.dispose();
    }    
    
    private void addObjectsToFrame() {
        this.setLayout(new BorderLayout());
        this.add(panelCenter,  BorderLayout.CENTER);
        this.add(panelButtons, BorderLayout.SOUTH);
        
        panelButtons.add(buttonOpenFromDisk);
        panelButtons.add(buttonOK);        
        
        panelCenter.add(panelLeft);
        panelCenter.add(panelRight);
        
        panelLeft.add(listNewMaps);
        panelLeft.add(recentFilesScroll);
        panelRight.add(searchPanel);
        
        searchPanel.setBorder(new TitledBorder("Search Location For New Map"));
        listNewMaps.setBorder(new TitledBorder("New Maps"));
        recentFilesScroll.setBorder(new TitledBorder("Recent Maps"));
        
        JViewport viewport = recentFilesScroll.getViewport();
        viewport.setViewPosition(new Point(300, 0));
    }
    
    private void init() {
        String configFilePath = System.getProperty("user.home") + File.separator + ".foldingmap";
        
        userConfig          = new UserConfig(new File(configFilePath));
        buttonOK            = new JButton("OK");
        buttonOpenFromDisk  = new JButton("Open From Disk");
        listNewMaps         = new JList(newMapOptions);
        listRecentMaps      = new JList(userConfig.getRecentFiles());
        panelButtons        = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelCenter         = new JPanel(new GridLayout(1,2));
        panelLeft           = new JPanel(new GridLayout(2,1));
        panelRight          = new JPanel(new GridLayout(1,1));
        recentFilesScroll   = new JScrollPane(listRecentMaps);
        searchPanel         = new OsmLocationSearchPanel();
        
        listNewMaps.setSelectedIndex(0);
        listNewMaps.addListSelectionListener(this);
        listNewMaps.addMouseListener(this);
        listRecentMaps.addListSelectionListener(this);
        listRecentMaps.addMouseListener(this);
        searchPanel.addListSelectionListener(this);
        buttonOK.addActionListener(this);
        buttonOpenFromDisk.addActionListener(this);
    }
    
    @Override
    public void mouseClicked(MouseEvent me) {
        //Open selection on double click
        if (me.getClickCount() >= 2) {
            if (me.getSource() == listNewMaps) {
                openNewMap();
                this.dispose();
            } else if (me.getSource() == listRecentMaps) {
                openRecentMap();
                this.dispose();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {}

    @Override
    public void mouseReleased(MouseEvent me) {}

    @Override
    public void mouseEntered(MouseEvent me) {}

    @Override
    public void mouseExited(MouseEvent me) {}    
    
    private void openNewMap() {
        DigitalMap  mapData;
        File        mapFile;
        
        String selectedValue = (String) listNewMaps.getSelectedValue();

        if (selectedValue.equals("World Map")) {
            mapFile = new File("default.fmxml");
            mapData = FmXmlImporter.openFile(mapFile, mainWindow.getProgressBarPanel());
            mainWindow. setMap(mapData);  
        } else if (selectedValue.equals("Blank Map")) {
            mapFile = new File("blankmap.fmxml");
            mapData = FmXmlImporter.openFile(mapFile, mainWindow.getProgressBarPanel());
            mainWindow.setMap(mapData);  
        }        
    }
    
    private void openRecentMap() {
        File   mapFile;
        String selectedValue = (String) listRecentMaps.getSelectedValue();

        try {
            mapFile = new File(selectedValue);
            
            userConfig.addRecentFile(selectedValue);
            MapImporter.importMap(mapFile, mainWindow, mainWindow, mainWindow.getProgressBarPanel());
            //mainWindow.setMap(mainWindow.getMapPanel().getMap());  
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in WelcomeDialog.openRecentMap() - Cannot open recent file " + selectedValue);
        }        
    }
    
    /**
     * Sets up the location of the dialog box.
     */
    private void setupLocation() {        
        Toolkit   tk           = Toolkit.getDefaultToolkit();
        Dimension screenSize   = tk.getScreenSize();
        int       width        = 600;
        int       height       = 300;        
        int       screenHeight = screenSize.height;
        int       screenWidth  = screenSize.width;
        int       x            = (screenWidth  - width)  / 2;
        int       y            = (screenHeight - height) / 2;

        this.setSize(width, height);
        this.setLocation(x, y);
    }

    /**
     * Handle the value changed event for the various JLists.
     * 
     * @param lse 
     */
    @Override
    public void valueChanged(ListSelectionEvent lse) {      
        if (ignoreNextSelection == true) {
            //When clearing the selection this is method is called,
            //it should be ignored when clearing.
            ignoreNextSelection = false;
        } else if (lse.getSource() == listNewMaps) {            
            ignoreNextSelection = true;
            listRecentMaps.clearSelection();
            ignoreNextSelection = true;
            searchPanel.clearSelection();
            
            //If the OSM option is selected, move the focus to the search box.
            String selected = (String) listNewMaps.getSelectedValue();
            if (selected.equalsIgnoreCase("OpenStreetMap.org")) 
                searchPanel.setSearchBoxAsFocus();
        } else if (lse.getSource() == listRecentMaps) {
            ignoreNextSelection = true;
            listNewMaps.clearSelection();    
            ignoreNextSelection = true;
            searchPanel.clearSelection();            
        } else if (lse.getSource() == searchPanel) {
            ignoreNextSelection = true;
            listNewMaps.clearSelection();
            ignoreNextSelection = true;
            listRecentMaps.clearSelection();            
        }
    }


}
