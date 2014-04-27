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

import co.foldingmap.Logger;
import co.foldingmap.ResourceHelper;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

/**
 * This is the default panel for displaying extended options of a VectorObject.
 * This default panel will show data variables and their value.
 * 
 * @author Alec
 */
public class DefaultExtendedOptionsPanel extends ExtendedOptionsPanel implements ActionListener {
    
    protected boolean                         editSingleObject;
    protected DefaultTableModel               modelExtendedOptions;
    protected JButton                         buttonAddRow, buttonDeleteRow;
    protected JPanel                          panelTableRowButtons;
    protected JScrollPane                     scrollExtendedOptionsTable;
    protected JTable                          tableExtendedOptions;
    protected VectorObject                    mapObject;
    protected VectorObjectList<VectorObject>  objects;        
    private   ResourceHelper                  resourceHelper;
    
    /**
     * Constructor for when only one object is being changed.
     */
    public DefaultExtendedOptionsPanel(VectorObject mapObject) {
        this.mapObject        = mapObject;
        this.editSingleObject = true;

        setupTable();
        init();
        setupPanel();
        setComponentProperties();
        addActionListeners();
    }
    
    /**
     * Constructor for when a group of objects are being changed.
     *
     * @param objects
     */
    public DefaultExtendedOptionsPanel(VectorObjectList<VectorObject> objects) {
        if (objects.size() == 1) {
            this.mapObject        = objects.get(0);
            this.editSingleObject = true;
        } else {
            this.objects          = objects;
            this.editSingleObject = false;
        }

        setupTable();
        init();
        setupPanel();
        setComponentProperties();
        addActionListeners();
    }    
    
    /**
     * Handles actions related to this object.
     * 
     * @param ae 
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        AbstractButton  initiatingObject;
        String          objectText, objectActionCommand;

        try {
            initiatingObject    = (AbstractButton) ae.getSource();
            objectText          = initiatingObject.getText();
            objectActionCommand = initiatingObject.getActionCommand();

            if (objectActionCommand.equalsIgnoreCase("Add Row")) {
                addNewRow();
            } else if (objectActionCommand.equalsIgnoreCase("Delete Row")) {
                deleteSelectedRow();
            } else if (objectActionCommand.equalsIgnoreCase("Ok")) {
                save();
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in DefaultExtendedOptionsPanel.actionPerformed(ActionEvent) - " + e);
        }
    }    
    
    /**
     * Adds action listeners to the appropriate objects.
     */    
    private void addActionListeners() {
        buttonAddRow.addActionListener(this);
        buttonDeleteRow.addActionListener(this);
    }    
    
    /**
     * Adds a row with a property name and it's value
     * 
     * @param name  The property name
     * @param value The property value
     */
    public void addRow(String name, String value) {
        String[]  newRow  = {name, value};

        if (modelExtendedOptions != null) {
            modelExtendedOptions.addRow(newRow);
        }
    }    
    
    /**
     * Adds a new blank row
     */
    public void addNewRow() {
        String[] blankRow = {"", ""};

        if (tableExtendedOptions != null) {
            if (tableExtendedOptions.getSelectedRow() != (tableExtendedOptions.getRowCount() - 1) && tableExtendedOptions.getSelectedRow() >= 0) {
                modelExtendedOptions.insertRow(tableExtendedOptions.getSelectedRow(), blankRow);
            } else {
                modelExtendedOptions.addRow(blankRow);
            }
        } else {
            modelExtendedOptions.addRow(blankRow);
        }
    }    
    
    /**
     * Deletes the first row.
     */
    public void deleteFirstRow() {
        if (modelExtendedOptions.getRowCount() > 0)
            modelExtendedOptions.removeRow(0);       
    }    
    
    /**
     * Deletes the selected row from the table. 
     */
    public void deleteSelectedRow() {
        int     selectedRow;
        String  fieldName;
        
        selectedRow = tableExtendedOptions.getSelectedRow();

        if (selectedRow >= 0) {
            fieldName = (String) tableExtendedOptions.getValueAt(selectedRow, 0);
            modelExtendedOptions.removeRow(tableExtendedOptions.getSelectedRow() );
                        
            if (mapObject != null) 
                mapObject.removeCustomDataField(fieldName);
                        
            for (VectorObject object: objects) 
                object.removeCustomDataField((String) tableExtendedOptions.getValueAt(0, selectedRow));
                        
        }
    }    
    
    /**
     * Enable the delete row button.
     * 
     * @param enabled 
     */
    public void enableDeleteRow(boolean enabled) {
        buttonDeleteRow.setEnabled(enabled);
    }   
    
    /**
     * Initiates the objects for this class.
     * 
     */
    private void init() {
        resourceHelper = ResourceHelper.getInstance();
        
        ImageIcon   addIcon, deleteIcon;
        
        try {
            addIcon    = (resourceHelper.getImage("add.png"));
            deleteIcon = (resourceHelper.getImage("delete.png"));
                                    
            buttonAddRow                 = new JButton(addIcon);
            buttonDeleteRow              = new JButton(deleteIcon);
            panelTableRowButtons         = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            scrollExtendedOptionsTable   = new JScrollPane(tableExtendedOptions);
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in DefaultExtendedOptionPanel.init() - " + e);
        }
    }    
    
    /**
     * Save the data in the panel to the VectorObject.
     */
    @Override
    public void save() {
        int             numberOfRows;
        String          editValue, field, value;
        TableCellEditor cellEditor;
        
        numberOfRows = tableExtendedOptions.getRowCount();
        cellEditor   = tableExtendedOptions.getCellEditor();
        
        if (cellEditor != null) 
            cellEditor.stopCellEditing();        
        
        for (int i = 0; i < numberOfRows; i++) {
            field = ((String) tableExtendedOptions.getValueAt(i, 0)).trim();
            value = ((String) tableExtendedOptions.getValueAt(i, 1)).trim();

            if ( (!field.equals("")) && (!value.equals("")) )
                mapObject.setCustomDataField(field, value);
        } //end for loop
    }    
    
    /**
     * Set all the action commands, tool tips and others for objects used in this class.
     */
    private void setComponentProperties() {
        buttonAddRow.setActionCommand("Add Row");
        buttonDeleteRow.setActionCommand("Delete Row");
    }    
    
    /**
     * Sets up the visual layout of the panel.
     */
    private void setupPanel() {
        this.setLayout(new BorderLayout());
        this.add(scrollExtendedOptionsTable, BorderLayout.CENTER);
        this.add(panelTableRowButtons,       BorderLayout.SOUTH);

        panelTableRowButtons.add(buttonAddRow);
        panelTableRowButtons.add(buttonDeleteRow);
    }    
    
    private void setupTable() {
        HashMap<String, String>     dataFields;
        Iterator                    it;
        Set                         set;

        try {
            modelExtendedOptions = new DefaultTableModel();
            modelExtendedOptions.addColumn("Property");
            modelExtendedOptions.addColumn("Value");

            if (this.editSingleObject) {
                dataFields = mapObject.getCustomDataFields();
                set        = dataFields.entrySet();
                it         = set.iterator();

                if (!it.hasNext())
                    addNewRow();

                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    addRow(entry.getKey().toString(), entry.getValue().toString());
                }
            } else {
                //get all custom data fields
                ArrayList<String> dataFieldsList = this.objects.getAllCustomDataFields();

                for (String currentField: dataFieldsList)
                    addRow(currentField, "");
            }

            tableExtendedOptions = new JTable(modelExtendedOptions);
            tableExtendedOptions.setBackground(new Color(225, 225, 225));
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in DefaultExtendedOptionsPanel.setupTable() - " + e);
        }
    }    
}
