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

import co.foldingmap.GUISupport.components.RangeSlider;
import co.foldingmap.GUISupport.panels.BoundaryPanel;
import co.foldingmap.GUISupport.panels.FileChoicePanel;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.LatLonAltBox;
import co.foldingmap.mapImportExport.TileExporter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The dialog box for exporting to a map to tile format.
 * 
 * @author Alec
 */
public class TileExportDialog extends JDialog implements ActionListener, ChangeListener {
    private BoundaryPanel       boundaryPanel;
    private ButtonGroup         boundaryOptionButtons;
    private Coordinate          mapCenter;
    private DigitalMap          mapData;
    private FileChoicePanel     fileChoicePanel;
    private JButton             buttonOk, buttonCancel;
    private JComboBox           comboTileFormat;
    private JLabel              labelNumberOfTiles, labelTileFormat;
    private JPanel              panelViewBounds, panelZoomLevel;
    private JPanel              panelButtons, panelCenter, panelMapCenter, panelTileFormat;
    private JRadioButton        rbuttonMapBounds, rbuttonViewBounds;
    private JTextField          textMapCenter;
    private LatLonAltBox        mapBoundary;
    private MainWindow          mainWindow;
    private RangeSlider         zoomLevelRange;
    private String              mapCenterString;
    private String[]            tileFormats = {"MBTiles", "Nested Folders"};
    
    public TileExportDialog(MainWindow mainWindow, DigitalMap mapData) {
        super(mainWindow, "Export Tiles");
        
        this.mapData    = mapData;
        this.mainWindow = mainWindow;
        
        init();
        addObjectsToFrame();
        setupLocation();
        
        this.setVisible(true);              
    }    
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        File    selectedFile;
        String  comboSelectedItem;                     
        
        if (ae.getSource() == this.buttonCancel) {
            this.dispose();
        } else if (ae.getSource() == this.buttonOk) {      
            comboSelectedItem = (String) comboTileFormat.getSelectedItem();   
            selectedFile      = fileChoicePanel.getSelectedFile();
            
            if (comboSelectedItem.equals("Nested Folders")) {                
                TileExporter exporter = new TileExporter(mapData, 
                                                         boundaryPanel.getLatLonAltBox(), 
                                                         zoomLevelRange.getLowValue(), 
                                                         zoomLevelRange.getHighValue(),
                                                         TileExporter.NESTEDFOLDERS,
                                                         selectedFile,
                                                         mainWindow);
                exporter.start();
            } else if (comboSelectedItem.equals("MBTiles")) {
                TileExporter exporter = new TileExporter(mapData, 
                                                         boundaryPanel.getLatLonAltBox(), 
                                                         zoomLevelRange.getLowValue(), 
                                                         zoomLevelRange.getHighValue(),
                                                         TileExporter.MBTILES,
                                                         selectedFile,
                                                         mainWindow);
                exporter.start();
            } else if (comboSelectedItem.equals("WeoGeo")) {
                
            }            
            
            dispose();
        } else if (ae.getSource() == comboTileFormat) {   
            comboSelectedItem = (String) comboTileFormat.getSelectedItem();   
            
            if (comboSelectedItem.equals("Nested Folders") || comboSelectedItem.equals("WeoGeo")) {
                fileChoicePanel.acceptDIR(true);
            } else if (comboSelectedItem.equals("MBTiles")) {
                fileChoicePanel.acceptDIR(false);
            }
        } else if (ae.getSource() == this.rbuttonMapBounds) {
            mapBoundary = mapData.getBoundary();
            boundaryPanel.setLatLonAltBox(mapBoundary);
            boundaryPanel.revalidate();
        } else if (ae.getSource() == this.rbuttonViewBounds) {    
            mapBoundary = new LatLonAltBox(mapData.getLastMapView().getViewBounds());
            boundaryPanel.setLatLonAltBox(mapBoundary);     
            boundaryPanel.revalidate();
        }
    }   
    
    private void addObjectsToFrame() {
        this.getContentPane().setLayout(new BorderLayout());
        this.add(panelTileFormat, BorderLayout.NORTH);
        this.add(panelCenter,     BorderLayout.CENTER);
        this.add(panelButtons,    BorderLayout.SOUTH);

        //TileFormat 
        panelTileFormat.add(labelTileFormat);
        panelTileFormat.add(comboTileFormat);        
        
        //Zoom Level Panel
        panelZoomLevel.add(zoomLevelRange);
        panelZoomLevel.add(labelNumberOfTiles);
                
        //Boundary options
        panelViewBounds.add(rbuttonMapBounds);
        panelViewBounds.add(rbuttonViewBounds);
        
        //Map Center
        panelMapCenter.add(textMapCenter);
        panelCenter.add(fileChoicePanel);
        panelCenter.add(panelViewBounds);
        panelCenter.add(boundaryPanel);
        panelCenter.add(panelMapCenter);
        panelCenter.add(panelZoomLevel);
        
        //Ok and Cancel Buttons
        panelButtons.add(buttonOk);
        panelButtons.add(buttonCancel);    
    }        
    
    private void init() {                
        boundaryOptionButtons       = new ButtonGroup();
        buttonOk                    = new JButton("Ok");
        buttonCancel                = new JButton("Cancel");
        comboTileFormat             = new JComboBox(tileFormats);
        fileChoicePanel             = new FileChoicePanel(this, FileChoicePanel.SAVE);
        labelNumberOfTiles          = new JLabel("Number of Tiles", SwingConstants.CENTER);
        labelTileFormat             = new JLabel("Tile Format");       
        mapBoundary                 = mapData.getBoundary();
        mapCenter                   = mapBoundary.getCenter();
        mapCenterString             = mapCenter.getLatitude() + ", " + mapCenter.getLongitude();
        panelButtons                = new JPanel(new FlowLayout(FlowLayout.RIGHT));   
        panelCenter                 = new JPanel(new GridLayout(5, 1));
        panelMapCenter              = new JPanel();
        panelTileFormat             = new JPanel();
        panelViewBounds             = new JPanel(new GridLayout(1,2));
        panelZoomLevel              = new JPanel(new GridLayout(2,1));
        rbuttonMapBounds            = new JRadioButton("Map Bounds");
        rbuttonViewBounds           = new JRadioButton("View Bounds");
        textMapCenter               = new JTextField(mapCenterString, 30);
        zoomLevelRange              = new RangeSlider(0, 23);
        boundaryPanel               = new BoundaryPanel(mapBoundary, BoundaryPanel.HORIZONTAL);
        
        boundaryPanel.setBorder(new TitledBorder("Map Bounds"));
        fileChoicePanel.setBorder(new TitledBorder("Save Location"));
        panelMapCenter.setBorder(new TitledBorder("Map Center"));
        panelZoomLevel.setBorder(new TitledBorder("Zoom Levels"));
        
        labelNumberOfTiles.setForeground(Color.GRAY);
        zoomLevelRange.addChangeListener(this);
        buttonOk.addActionListener(this);
        buttonCancel.addActionListener(this);
        
        zoomLevelRange.setLowValue(TileExporter.findMinZoom(mapBoundary));  
        comboTileFormat.addActionListener(this);
        
        rbuttonViewBounds.addActionListener(this);
        rbuttonMapBounds.addActionListener(this);
        rbuttonMapBounds.setSelected(true);
        boundaryOptionButtons.add(rbuttonMapBounds);
        boundaryOptionButtons.add(rbuttonViewBounds);
        panelViewBounds.setBorder(new TitledBorder("Boundary Presets"));
    }    
    
    /**
     * Sets up the on screen location and size of this dialog.
     */
    private void setupLocation() {
        Toolkit   tk           = Toolkit.getDefaultToolkit();
        Dimension screenSize   = tk.getScreenSize();
        int       screenHeight = screenSize.height;
        int       screenWidth  = screenSize.width;
        int       x            = (screenWidth  - 420) / 2;
        int       y            = (screenHeight - 600) / 2;

        this.setSize(426, 550);
        this.setLocation(x, y);
        this.setResizable(false);
    }    
    
    @Override
    public void stateChanged(ChangeEvent ce) {
        if (ce.getSource() == zoomLevelRange) {
            //update tile estimate
            updateNumberOfTilesLabel();
        }
    }  
    
    protected void updateNumberOfTilesLabel() {
        DecimalFormat formatter;
        double        fileSize;
        long          numberOfTiles;
        String        tilesFormatted;
        
        numberOfTiles = TileExporter.calculateNumberOfMapTiles(mapBoundary, 
                                                               zoomLevelRange.getLowValue(), 
                                                               zoomLevelRange.getHighValue());    
        formatter      = new DecimalFormat("#,###");
        tilesFormatted = formatter.format(numberOfTiles);
        fileSize       = (numberOfTiles * 20) / 1024;
                
        if (fileSize > 1024) {
            fileSize = (float) (fileSize / 1024);
            labelNumberOfTiles.setText(tilesFormatted + " Tiles, about " + fileSize + " GB");
        } else {
            labelNumberOfTiles.setText(tilesFormatted + " Tiles, about " + fileSize + " MB");
        }
    }    
}
