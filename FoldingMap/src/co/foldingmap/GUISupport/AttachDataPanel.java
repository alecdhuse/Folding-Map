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

import co.foldingmap.Logger;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Alec
 */
public class AttachDataPanel extends javax.swing.JPanel {
    private ArrayList<String> customDataFields, headerNames;
    private JComboBox         comboImportDataVariable, comboMapObjectVariable;
    private JLabel            labelImportDataVariable, labalMapObjectVariable;
    
    public AttachDataPanel(ArrayList<String> customDataFields) {
        this.customDataFields = customDataFields;
        this.headerNames      = new ArrayList<String>();
        
        //add name to the list
        customDataFields.add(0, "Name");        
        
        init();    
        setupPanel();
    }
    
    /**
     * Get the user selected value from the imported data file.
     * 
     * @return 
     */
    public String getSelectedImportDataVariable() {
        return (String) comboImportDataVariable.getSelectedItem();
    }

    /**
     * Get the user selected value for the Map file.
     * 
     * @return 
     */
    public String getSelectedMapObjectVariable() {
        return (String) comboMapObjectVariable.getSelectedItem();
    }  
    
    /**
     * Initiate Components
     */
    private void init() { 
        try {
            comboImportDataVariable     = new JComboBox(headerNames.toArray());
            comboMapObjectVariable      = new JComboBox(customDataFields.toArray());
            labelImportDataVariable     = new JLabel("Match with data field:");
            labalMapObjectVariable      = new JLabel("Map objects with:");

            this.setBorder(new TitledBorder("Attach Data To Existing Objects"));

            //add name to the list
            customDataFields.add(0, "Name");     

            //clear the items in the combobox and add objects from customDataFields
            comboMapObjectVariable.removeAllItems();
            for (String currentDataField: customDataFields)
                comboMapObjectVariable.addItem(currentDataField);        
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error on AttachDataPanel.init() - " + e);
        }
    }
    
    /**
     * Sets up the Components in the Panel.
     */
    private void setupPanel() {
        this.setLayout(new GridLayout(2,2));
        
        this.add(labalMapObjectVariable);
        this.add(comboMapObjectVariable);
        this.add(labelImportDataVariable);
        this.add(comboImportDataVariable);
    }
    
    /**
     * Update the headers from the data file to the combo box.
     * 
     * @param headerNames 
     */
    public void updateHeaderNames(ArrayList<String> headerNames) {
        this.headerNames      = headerNames;

        comboImportDataVariable.removeAllItems();
        for (String currentDataVariable: headerNames) {
            comboImportDataVariable.addItem(currentDataVariable);
        }
    }      
}
