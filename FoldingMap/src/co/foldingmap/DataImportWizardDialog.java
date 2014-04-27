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

import co.foldingmap.GUISupport.AnalyzeDataPanel;
import co.foldingmap.GUISupport.DataImportResultPanel;
import co.foldingmap.GUISupport.ImportFilePanel;
import co.foldingmap.GUISupport.components.WizardPane;
import co.foldingmap.data.TabularData;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JDialog;

/**
 *
 * @author Alec
 */
public class DataImportWizardDialog extends JDialog implements ActionListener {
    protected AnalyzeDataPanel                     analyzeDataPanel;
    protected ArrayList<String>                    customDataFields;
    protected DataImportResultPanel                dataImportResultPanel;
    protected DigitalMap                           mapData;
    protected WizardPane                           mainPane;
    protected Window                               owner;
    
    public DataImportWizardDialog(Window owner, DigitalMap mapData) {
        super(owner, "Import Data");

        try {
            this.customDataFields = mapData.getAllCustomDataFields();
            this.mapData          = mapData;
            this.owner            = owner;
            
            init();
            createPanels();
            addObjectsToFrame();
            setupLocation();
            
            this.setVisible(true);
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in DataImportWizardDialog constructor - " + e);
        }        
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        int    selectedPanel;
        String actionCommand;

        actionCommand = ae.getActionCommand();
        selectedPanel = this.getSelectedPanelIndex();

        if (actionCommand.equalsIgnoreCase("Finish")) {
            //Add the objects to a new layer and add that Layer to the map
            VectorLayer newObjects = new VectorLayer("Imported Data");
            newObjects.addAllObjects(getResults());
            mapData.addLayer(newObjects);

            dispose();
            owner.repaint();
        } else if (actionCommand.equalsIgnoreCase("Next")) {
            if (selectedPanel == 2)
                createObjects();
        }
    }    
    
    /**
     * Setup the frame layout.
     */
    private void addObjectsToFrame() {
        this.getContentPane().setLayout(new GridLayout());
        this.add(mainPane);        
    }
    
    public void createObjects() {
        AnalyzeDataPanel        analyzeDataPanel;
        DataImportResultPanel   dataImportResultPanel;
        String                  objectStyle, objectToCreate;

        analyzeDataPanel      = getAnalyzeDataPanel();
        dataImportResultPanel = getDataImportResultPanel();        
        objectToCreate        = analyzeDataPanel.getObjectToCreate();

        if (objectToCreate.equalsIgnoreCase("(None)")) {
            dataImportResultPanel.addData(mapData, analyzeDataPanel.getSelectedImportDataVariable(), analyzeDataPanel.getSelectedMapObjectVariable());
        } else if (objectToCreate.equalsIgnoreCase("Point")) {
            objectStyle   = analyzeDataPanel.getObjectStyleID();
            String[] vars = analyzeDataPanel.getObjectVariables();
            dataImportResultPanel.createPoints(vars[1], vars[2], vars[0], objectStyle);
        } else if (objectToCreate.equalsIgnoreCase("Line")) {
            objectStyle   = analyzeDataPanel.getObjectStyleID();
            String[] vars = analyzeDataPanel.getObjectVariables();
            dataImportResultPanel.createLineStrings(vars[0], vars[1], objectStyle);
        } else if (objectToCreate.equalsIgnoreCase("Polygon")) {
            objectStyle   = analyzeDataPanel.getObjectStyleID();
            String[] vars = analyzeDataPanel.getObjectVariables();
            dataImportResultPanel.createPolygons(vars[0], vars[1], objectStyle);
        }
    }    
    
    /**
     * Create the Panels to be used in this Wizard.
     */
    private void createPanels() {
        analyzeDataPanel      = new AnalyzeDataPanel(mainPane, mapData, customDataFields);
        dataImportResultPanel = new DataImportResultPanel(mapData);

        mainPane.addPanel("Select File To Import",      new ImportFilePanel(this, mainPane));
        mainPane.addPanel("Decide How To Use The Data", analyzeDataPanel);
        mainPane.addPanel("View Import Results",        dataImportResultPanel);

        mainPane.allowAdvance(false);        
    }
    
    /**
     * Initiate the components. 
     */
    private void init() {
        mainPane = new WizardPane(this);
        mainPane.addActionListener(this);        
    }
    
    /**
     * Returns the AnalyzeDataPanel.
     * 
     * @return 
     */
    public AnalyzeDataPanel getAnalyzeDataPanel() {
        return analyzeDataPanel;
    }

    /**
     * Returns the DataImportResultPanel.
     * 
     * @return 
     */
    public DataImportResultPanel getDataImportResultPanel() {
        return dataImportResultPanel;
    }

    /**
     * Returns the Results from the Import
     * 
     * @return 
     */
    public VectorObjectList<VectorObject> getResults() {
        return dataImportResultPanel.getMapObjects();
    }

    /**
     * Returns the index of the selected Panel.
     * 
     * @return 
     */
    public int getSelectedPanelIndex() {
        return mainPane.getSelectedPanelIndex();
    }    
    
    /**
     * Sets the TabularData to be used in the Data Import.
     * 
     * @param dataFile 
     */
    public void passDataFile(TabularData dataFile) {
        analyzeDataPanel.setDataFile(dataFile);
        dataImportResultPanel.setDataFile(dataFile);
    }    
    
    private void setupLocation() {
        Toolkit   tk           = Toolkit.getDefaultToolkit();
        Dimension screenSize   = tk.getScreenSize();
        int       screenHeight = screenSize.height;
        int       screenWidth  = screenSize.width;
        int       height       = 375;
        int       width        = 600;
        int       x            = (screenWidth  - width)  / 2;
        int       y            = (screenHeight - height) / 2;

        this.setSize(width, height);
        this.setLocation(x, y);
    }

}
