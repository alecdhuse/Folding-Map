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
package co.foldingmap.GUISupport;

import co.foldingmap.GUISupport.components.WizardPane;
import co.foldingmap.GUISupport.components.WizardPanePanel;
import co.foldingmap.Logger;
import co.foldingmap.data.TabularData;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.themes.ColorStyle;
import co.foldingmap.map.themes.MapTheme;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.*;

/**
 *
 * @author Alec
 */
public class AnalyzeDataPanel extends WizardPanePanel implements ActionListener {
    protected ArrayList<String>     customDataFields;
    protected AttachDataPanel       attachDataPanel;
    protected CreateLineStringPanel createLineStringPanel;
    protected CreateMapPointsPanel  createMapPointsPanel;
    protected CreatePolygonPanel    createPolygonPanel;
    protected DefaultComboBoxModel  comboModelObjectStyles;
    protected DefaultListModel      listModelVariables;
    protected DigitalMap            mapData;
    protected JComboBox             comboObjectStyle, comboObjectToCreate;
    protected JLabel                labelCoordinateDataInfo, labelObjectStyle, labelObjectToCreate;
    protected JList                 listVariables;
    protected JPanel                panelInternal, panelObjectToCreate;
    protected JScrollPane           spaneVariables;
    protected JTextArea             textArea;
    protected String[]              availableObjectsToCreate = {"(None)", "Point", "Line", "Polygon"};
    protected TabularData           dataFile;
    protected WizardPane            wizardPane;

    public AnalyzeDataPanel(WizardPane wizardPane, DigitalMap mapData, ArrayList<String> customDataFields) {
        this.customDataFields = customDataFields;
        this.mapData          = mapData;
        this.wizardPane       = wizardPane;

        init();
    }    
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        String  actionCommand, selectedObjectType;

        actionCommand = ae.getActionCommand();

        if (actionCommand.equalsIgnoreCase("Change Object")) {
            //The type of object to create has changed
            setupInternalPanel();
        }
    }    
        
    @Override
    public void displayPanel() {
        setDataFile(dataFile);
    }

    /**
     * Returns the ID of the style to be used when creating objects.
     * 
     * @return 
     */
    public String getObjectStyleID() {
        ColorStyle objectStyle;

        try {
            objectStyle = (ColorStyle) comboObjectStyle.getSelectedItem();
            return objectStyle.getID();
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in AbalyseDataPanel.getObjectStyle() - " + e);
            return null;
        }        
    }    
    
    /**
     * Returns the type of object to be used when creating objects.
     * 
     * @return 
     */
    public String getObjectToCreate() {
        return (String) comboObjectToCreate.getSelectedItem();
    }    
    
    /**
     * Returns the headers or columns names to be used when creating or 
     * adding data to objects.
     * 
     * @return 
     */
    public String[] getObjectVariables() {
        String   objectToCreate = getObjectToCreate();
        String[] variables;

        if (objectToCreate.equalsIgnoreCase("(None)")) {
            variables = new String[0];
        } else if (objectToCreate.equalsIgnoreCase("Point")) {
            variables    = new String[3];
            variables[0] = createMapPointsPanel.getAltitudeVariable();
            variables[1] = createMapPointsPanel.getLatitudeVariable();
            variables[2] = createMapPointsPanel.getLongitudeVariable();
        } else if (objectToCreate.equalsIgnoreCase("Line")) {
            variables    = new String[2];
            variables[0] = createLineStringPanel.getNameVariable();
            variables[1] = createLineStringPanel.getCoordinatesVariable();
        } else if (objectToCreate.equalsIgnoreCase("Polygon")) {
            variables    = new String[2];
            variables[0] = createPolygonPanel.getNameVariable();
            variables[1] = createPolygonPanel.getCoordinatesVariable();
        } else {
            variables = new String[0];
        }

        return variables;
    }    
    
    /**
     * Returns the import data variable to be used.
     * 
     * @return 
     */
    public String getSelectedImportDataVariable() {
        return attachDataPanel.getSelectedImportDataVariable();
    }    
    
    /**
     * Return the map variable to match with the imported data.
     * 
     * @return 
     */
    public String getSelectedMapObjectVariable() {
        return attachDataPanel.getSelectedMapObjectVariable();
    }    
    
    private void init() {
        attachDataPanel          = new AttachDataPanel(customDataFields);
        comboModelObjectStyles   = new DefaultComboBoxModel();
        comboObjectStyle         = new JComboBox(comboModelObjectStyles);
        comboObjectToCreate      = new JComboBox(availableObjectsToCreate);
        createLineStringPanel    = new CreateLineStringPanel();
        createMapPointsPanel     = new CreateMapPointsPanel();
        createPolygonPanel       = new CreatePolygonPanel();
        labelCoordinateDataInfo  = new JLabel();
        labelObjectStyle         = new JLabel("Object Style");
        labelObjectToCreate      = new JLabel("Object To Create");
        listModelVariables       = new DefaultListModel();
        listVariables            = new JList(listModelVariables);
        panelInternal            = new JPanel(new SpringLayout());
        panelObjectToCreate      = new JPanel(new SpringLayout());
        spaneVariables           = new JScrollPane(listVariables);
        textArea                 = new JTextArea(3, 30);
        
        comboObjectToCreate.setActionCommand("Change Object");
        comboObjectToCreate.addActionListener(this);
        
        textArea.setBorder(null);
        
        listVariables.setMaximumSize(   new Dimension(350, 80));
        spaneVariables.setMaximumSize(  new Dimension(350, 90));
        listVariables.setPreferredSize( new Dimension(350, 80));
        spaneVariables.setPreferredSize(new Dimension(350, 90));

        panelObjectToCreate.add(labelObjectToCreate);
        panelObjectToCreate.add(comboObjectToCreate);
        panelObjectToCreate.add(labelObjectStyle);
        panelObjectToCreate.add(comboObjectStyle);

        setupInternalPanel();

        this.add(panelInternal);        
    }
    
    /**
     * Sets the TabularData to be used by this class.
     * 
     * @param dataFile 
     */
    public void setDataFile(TabularData dataFile) {
        this.dataFile = dataFile;
        setupCoodinateDataLabel();
        attachDataPanel.updateHeaderNames(dataFile.getHeaderNames());
    }    
    
    public void setupCoodinateDataLabel() {
        ArrayList<Boolean>  columnCoordinateData;
        ArrayList<String>   columnsContainingCoordinateData;
        Boolean             currentBoolean, objectTypeSet;
        int                 numberOfHeaders;
        String              labelText;

        columnsContainingCoordinateData = new ArrayList<String>();
        columnCoordinateData            = dataFile.findLocationData();
        objectTypeSet                   = false;
        listModelVariables.clear();

        for (int i = 0; i < columnCoordinateData.size(); i++) {
            currentBoolean = columnCoordinateData.get(i);
            if (currentBoolean == true) {
                columnsContainingCoordinateData.add(dataFile.getHeaderName(i));
            } 
        }

        if (columnsContainingCoordinateData.isEmpty()) {
            labelText = "No coordinate information could be read. \nData will be loaded and can be assigned \nto a Map Object at another time.";
        } else {
            labelText = "Coordinate data was found in colums:";
            for (String s: columnsContainingCoordinateData) {
                listModelVariables.addElement(s);
            }

            numberOfHeaders = columnsContainingCoordinateData.size();
            createMapPointsPanel.setHeaders(dataFile.getHeaderNames());
            createLineStringPanel.setHeaders(dataFile.getHeaderNames());
            createPolygonPanel.setHeaders(dataFile.getHeaderNames());
            if (objectTypeSet == false) {
                //check to see if any of the columns contain a coordinate string
                for (int i = 0; i < dataFile.getNumberOfColumns(); i++) {
                    if (dataFile.containsCoordinateString(i)) {
                        comboObjectToCreate.setSelectedIndex(2);
                        objectTypeSet = true;
                        break;
                    }
                }
            }

            if (objectTypeSet == false) {
                if ((numberOfHeaders == 2) || (numberOfHeaders == 3)) {
                    //There is only enough data to make a point
                    comboObjectToCreate.setSelectedIndex(1);
                    objectTypeSet = true;
                }
            }

        }

        textArea.setText(labelText);
        permitAdvance = true;
        wizardPane.allowAdvance(true);
    }    
    
    private void setupInternalPanel() {
        ArrayList availableStyles;
        MapTheme  currentTheme;
        String    selectedObjectType;

        selectedObjectType = (String) comboObjectToCreate.getSelectedItem();
        currentTheme       = mapData.getTheme();

        labelObjectStyle.setVisible(true);
        comboObjectStyle.setVisible(true);
        comboModelObjectStyles.removeAllElements();

        labelCoordinateDataInfo.setMaximumSize(new Dimension(350, 90));
        panelInternal.setMaximumSize(new Dimension(500, 100));
        panelInternal.removeAll();
        panelInternal.add(textArea);
        panelInternal.add(spaneVariables);
        panelInternal.add(panelObjectToCreate);
        
        if (selectedObjectType.equals("(None)")) {
            panelInternal.add(attachDataPanel);
            labelObjectStyle.setVisible(false);
            comboObjectStyle.setVisible(false);
            availableStyles = new ArrayList();
        } else if (selectedObjectType.equals("Point")) {
            availableStyles = currentTheme.getAllIconStyles();
            panelInternal.add(createMapPointsPanel);            
        } else if (selectedObjectType.equals("Line")) {
            availableStyles = currentTheme.getAllLineStyles();
            panelInternal.add(createLineStringPanel);
        } else if (selectedObjectType.equals("Polygon")) {
            availableStyles = currentTheme.getAllPolygonStyles();
            panelInternal.add(createPolygonPanel);
        } else {
            availableStyles = new ArrayList();
        }

        Collections.sort(availableStyles);
        comboModelObjectStyles = new DefaultComboBoxModel(availableStyles.toArray());
        comboObjectStyle.setModel(comboModelObjectStyles);

        SpringUtilities.makeCompactGrid(panelObjectToCreate, 2, 2, 1, 1, 3, 3);
        SpringUtilities.makeCompactGrid(panelInternal,       4, 1, 1, 1, 3, 3);

        this.revalidate();
    }    
}
