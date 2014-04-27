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

import co.foldingmap.DataImportWizardDialog;
import co.foldingmap.GUISupport.components.WizardPane;
import co.foldingmap.GUISupport.components.WizardPanePanel;
import co.foldingmap.GUISupport.panels.FileChoicePanel;
import co.foldingmap.Logger;
import co.foldingmap.data.CsvDataConnector;
import co.foldingmap.data.ExcelDataConnector;
import co.foldingmap.data.TabularData;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Alec
 */
public class ImportFilePanel extends WizardPanePanel implements ActionListener, TableModelListener {
    protected ArrayList<ActionListener>            actionListeners;
    protected DataImportWizardDialog               parentDialog;
    protected DefaultTableModel                    tableModelVariables;
    protected FileChoicePanel                      fileChoice;
    protected JLabel                               fileLabelText;
    protected JPanel                               panelVariables;
    protected JScrollPane                          spaneVariables;
    protected JTable                               tableVariables;
    protected TabularData                          dataFile;
    protected WizardPane                           wizardPane;
    
    public ImportFilePanel(DataImportWizardDialog parentDialog, WizardPane wizardPane) {
        try {
            this.parentDialog = parentDialog;
            this.wizardPane   = wizardPane;

            //initiate componets
            actionListeners         = new ArrayList<ActionListener>();
            fileChoice              = new FileChoicePanel(parentDialog);
            fileLabelText           = new JLabel("Please select the file containing the data you wish to import.");
            panelVariables          = new JPanel();
            tableModelVariables     = new DefaultTableModel();
            tableVariables          = new JTable(tableModelVariables);
            spaneVariables          = new JScrollPane(tableVariables);            

            tableModelVariables.addColumn("Found the followning Column Headers:");
            tableVariables.setMaximumSize(new Dimension(400, 200));
            spaneVariables.setMaximumSize(new Dimension(400, 200));
            panelVariables.setMaximumSize(new Dimension(400, 200));
            tableVariables.setPreferredSize(new Dimension(400, 200));
            spaneVariables.setPreferredSize(new Dimension(400, 200));
            panelVariables.setPreferredSize(new Dimension(400, 250));
            tableModelVariables.addTableModelListener(this);

            panelVariables.add(spaneVariables);

            this.setLayout(new SpringLayout());
            this.add(fileLabelText);
            this.add(fileChoice);
            this.add(panelVariables);
            SpringUtilities.makeCompactGrid(this, 3, 1, 3, 3, 4, 10);

            panelVariables.setVisible(false);

            //add action listeners
            fileChoice.addActionListener(this);
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in ImportFilePanel constructor - " + e);
        }         
    }
    
    /**
     * Handles actions for this panel.
     * 
     * @param ae 
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        String  actionCommand, fileEx;
        
        actionCommand = ae.getActionCommand();
        
        
        
        if (actionCommand.equalsIgnoreCase("File Selected")) {
            fileEx = fileChoice.getSelectedFileExtension();
            
            if (fileEx.equalsIgnoreCase("xls")) {
                loadExcelFile(fileChoice.getSelectedFile());
            } else if (fileEx.equalsIgnoreCase("xlsx")) {
                loadExcelFile(fileChoice.getSelectedFile());
            } else if (fileEx.equalsIgnoreCase("csv")) {
                loadCsvFile(fileChoice.getSelectedFile());
            }
        }
    }    
    
    /**
     * Adds an action listener to this Panel.
     * 
     * @param l 
     */
    public void addActionListener(ActionListener l) {
        actionListeners.add(l);
    }    
    
    @Override
    public void displayPanel() {

    }

    public void loadCsvFile(File csvFile) {
        ArrayList<String>   headers;
        CsvDataConnector    dataConnector;
        String[]            newRow;
        
        dataConnector = new CsvDataConnector(csvFile);
        dataFile      = dataConnector.getTabularData();
        headers       = dataFile.getHeaderNames();
        
        for (int i = 0; i < tableModelVariables.getRowCount(); i++) {
            tableModelVariables.removeRow(i);
        }

        for (String currentHeader: headers) {
            newRow = new String[1];
            newRow[0] = currentHeader;
            tableModelVariables.addRow(newRow);
        }

        panelVariables.setVisible(true);
        this.validate();
        this.repaint();

        this.permitAdvance = true;
        wizardPane.allowAdvance(true);

        parentDialog.passDataFile(dataFile);      
    }
    
    public void loadExcelFile(File workBookFile) {
        ArrayList<String>   headers;
        ExcelDataConnector  dataConnector;
        String[]            newRow;

        dataConnector = new ExcelDataConnector(workBookFile);
        dataFile      = dataConnector.getDataFile();
        headers       = dataFile.getHeaderNames();

        for (int i = 0; i < tableModelVariables.getRowCount(); i++) {
            tableModelVariables.removeRow(i);
        }

        for (String currentHeader: headers) {
            newRow = new String[1];
            newRow[0] = currentHeader;
            tableModelVariables.addRow(newRow);
        }

        panelVariables.setVisible(true);
        this.validate();
        this.repaint();

        this.permitAdvance = true;
        wizardPane.allowAdvance(true);

        parentDialog.passDataFile(dataFile);
    }    
    
    @Override
    public void tableChanged(TableModelEvent e) {
        int     changeType = e.getType();
        int     firstRow, lastRow;
        String  changedCellText;

        if (changeType == TableModelEvent.UPDATE) {
            firstRow = e.getFirstRow();
            lastRow  = e.getLastRow();

            if (firstRow == lastRow) {
                //assume name has been changed
                if (dataFile != null) {
                    changedCellText = (String) tableModelVariables.getValueAt(firstRow, 0);
                    dataFile.renameHeader(firstRow, changedCellText);
                }
            }
        } // if changeType = Update
    }
    
}
