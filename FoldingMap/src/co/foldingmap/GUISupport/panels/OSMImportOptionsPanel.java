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

import co.foldingmap.GUISupport.CellEditorRenderer;
import co.foldingmap.GUISupport.OptionsPanel;
import co.foldingmap.GUISupport.OsmConditionsTableModel;
import co.foldingmap.Logger;
import co.foldingmap.MainWindow;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.vector.LatLonBox;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;
import co.foldingmap.mapImportExport.OsmImportCondition;
import co.foldingmap.mapImportExport.OsmImporter;
import co.foldingmap.mapImportExport.OsmOverpassDownloader;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Alec
 */
public class OSMImportOptionsPanel extends OptionsPanel  {    
    protected BoundaryPanel           regionBoundaryPanel;
    protected LatLonBox               bounds;
    protected DigitalMap              mapData;
    protected JRadioButton            optionAllTags;
    protected JPanel                  panelImportOptions, panelImportNorth;
    protected JPanel                  panelDataTypes, panelLower, panelSpacer, panelTags;
    protected JScrollPane             scrollPaneOptions;
    protected JTable                  tableImportOptions;
    protected JTextField              textSelected, textTagKey, textTagValue;
    protected MainWindow              mainWindow;
    protected OsmImporter             osmImporter;
    protected OsmConditionsTableModel tableModelOptions;
    protected OsmImportCondition      defaultRow;
    protected OsmOverpassDownloader   osmDownloader;  
    
    protected final String[]          objectType  = {"Any", "Nodes", "Ways", "Areas", "Relations"};
    protected final String[]          columnNames = {"Object Type", "Key", "Value", ""};

    public OSMImportOptionsPanel(MainWindow mainWindow, DigitalMap mapData) {
        this.mapData      = mapData;
        this.mainWindow   = mainWindow;
                
        init();
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {        
        VectorObjectList<VectorObject> osmObjects;        

        try {      
            //Action is called externaly do the import!
            bounds        = regionBoundaryPanel.getLatLonAltBox();    
            osmObjects    = new VectorObjectList<VectorObject>();
            osmDownloader = new OsmOverpassDownloader(mainWindow, mapData, bounds); 
            
            tableModelOptions.hidePopups();
            osmDownloader.setImportConditions(tableModelOptions.getRows());
            osmDownloader.start();                     
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in OsmImportOptionsPanel.actionPerformed() - " + e);
        }
    } 
    
    private void init() {
        try {
            bounds              = mapData.getLastMapView().getViewBounds();
            regionBoundaryPanel = new BoundaryPanel(bounds, BoundaryPanel.HORIZONTAL);
            panelImportOptions  = new JPanel(new BorderLayout());
            panelImportNorth    = new JPanel(new GridLayout(2,4));
            panelSpacer         = new JPanel();
            panelTags           = new JPanel(new GridLayout(3,2));
            tableModelOptions   = new OsmConditionsTableModel(mainWindow, setupConditions());
            tableImportOptions  = new JTable(tableModelOptions);
            scrollPaneOptions   = new JScrollPane(tableImportOptions);
            textTagKey          = new JTextField();
            textTagValue        = new JTextField();                        

            this.setLayout(new BorderLayout());
            this.add(regionBoundaryPanel, BorderLayout.NORTH);
            this.add(panelImportOptions,  BorderLayout.CENTER);      

            panelImportOptions.add(panelImportNorth,  BorderLayout.NORTH);
            panelImportOptions.add(scrollPaneOptions, BorderLayout.CENTER);

            regionBoundaryPanel.setBorder(new TitledBorder("Bounds"));

            tableImportOptions.setDefaultRenderer(OsmImportCondition.class, new CellEditorRenderer());
            tableImportOptions.setDefaultEditor(OsmImportCondition.class,   new CellEditorRenderer());
            tableImportOptions.setRowHeight(25);
            tableImportOptions.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            tableImportOptions.getColumnModel().getColumn(0).setPreferredWidth(120);
            tableImportOptions.getColumnModel().getColumn(1).setPreferredWidth(135);
            tableImportOptions.getColumnModel().getColumn(2).setPreferredWidth(135);
            tableImportOptions.getColumnModel().getColumn(3).setPreferredWidth(60);
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in OsmImportOptionsPanel.init() - " + e);
        }            
    }

    private List<OsmImportCondition> setupConditions() {
        ArrayList<OsmImportCondition> conditions;
        
        conditions = new ArrayList<OsmImportCondition>();
        
        if (bounds.getWidth() > 3.4) {
            conditions.add(new OsmImportCondition("Nodes", "place",    "city"));
            conditions.add(new OsmImportCondition("Any",   "boundary", "administrative"));
            conditions.add(new OsmImportCondition("Ways",  "highway",  "primary"));
        } else {
            conditions.add(new OsmImportCondition("Any", "Any", "Any"));
        }
     
        return conditions;
    }
}
