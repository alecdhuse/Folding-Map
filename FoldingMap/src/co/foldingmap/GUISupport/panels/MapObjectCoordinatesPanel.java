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

import co.foldingmap.MainWindow;
import co.foldingmap.ResourceHelper;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.CoordinateList;
import co.foldingmap.map.vector.VectorObject;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Alec
 */
public class MapObjectCoordinatesPanel extends JPanel 
                                       implements ActionListener, 
                                                  ListSelectionListener {
    
    private CoordinateList<Coordinate>  coordinateList;
    private CoordinateTableModel        tableModel;
    private JButton                     buttonAddRow, buttonChangeAltitude, buttonDeleteRow;
    private JButton                     buttonReverseCoordinates;
    private JButton                     buttonSetAltitude;
    private JPanel                      panelButtons, panelButtonsCenter;
    private JPanel                      panelButtonsLeft, panelButtonsRight;
    private JScrollPane                 spaneTable;
    private JTable                      tableCoordinates;
    private JTextField                  textChangeAltitude;
    private MainWindow                  mainWindow;
    private VectorObject                object;
    private ResourceHelper              resourceHelper;
    
    public MapObjectCoordinatesPanel(MainWindow mainWindow, CoordinateList<Coordinate> list) {
        this.mainWindow     = mainWindow;
        this.coordinateList = object.getCoordinateList();
        this.object         = null;
        
        setupTable();
        init();        
        setupPanel();              
        
        tableCoordinates.getSelectionModel().addListSelectionListener(this);         
    }
    
    public MapObjectCoordinatesPanel(MainWindow mainWindow, VectorObject object) {
        this.mainWindow     = mainWindow;
        this.coordinateList = object.getCoordinateList();
        this.object         = object;
        
        setupTable();
        init();        
        setupPanel();
        
        tableCoordinates.getSelectionModel().addListSelectionListener(this);         
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == buttonAddRow) {
            addNewRow();
        } else if (ae.getSource() == buttonChangeAltitude) {
            textChangeAltitude.setBackground(new Color(211, 225, 240));
            panelButtonsCenter.setVisible(true);
        } else if (ae.getSource() == buttonDeleteRow) {
            removeSelectedrow();
        } else if (ae.getSource() == buttonReverseCoordinates) {
            coordinateList.reverse();
            populateTable();
        } else if (ae.getSource() == buttonSetAltitude) {
            try {
                float newAlt = Float.parseFloat(textChangeAltitude.getText());
                
                for (Coordinate c: coordinateList)                 
                    c.setAltitude(newAlt);                
                
                panelButtonsCenter.setVisible(false);
                populateTable();                
            } catch (Exception e) {
                textChangeAltitude.setBackground(new Color(244, 152, 151));
            }               
        }
    }    
    
    /**
     * Adds a new, blank row to the end of the table
     */
    public void addNewRow() {
        if (tableCoordinates != null) {
            if (tableCoordinates.getSelectedRow() != (tableCoordinates.getRowCount() - 1) && tableCoordinates.getSelectedRow() >= 0) {
                tableModel.insertNewBlankRow(tableCoordinates.getSelectedRow());
            } else {
                tableModel.addNewBlankRow();
            }
        } else {
            tableModel.addNewBlankRow();
        }
    }    
    
    /**
     * Adds a new row to the end of the table containing the given coordinate
     * information.
     * 
     * @param c 
     */
    public void addNewRow(Coordinate c) {
        tableModel.addRow(c);
    }    
    
    /**
     * Changes the altitudes of each coordinate to the specified value.
     * 
     * @param altitude 
     */
    public void changeAllAltitudes(float altitude) {
        for (Coordinate c: coordinateList) {
            c.setAltitude(altitude);
        }
    }
    
    private void init() {
        resourceHelper = ResourceHelper.getInstance();
        
        buttonAddRow                = new JButton(resourceHelper.getImage("add.png"));
        buttonChangeAltitude        = new JButton(resourceHelper.getImage("world.png"));
        buttonDeleteRow             = new JButton(resourceHelper.getImage("delete.png"));
        buttonReverseCoordinates    = new JButton(resourceHelper.getImage("reverse.png"));      
        buttonSetAltitude           = new JButton("Set Altitude");
        panelButtons                = new JPanel(new BorderLayout());
        panelButtonsCenter          = new JPanel();
        panelButtonsLeft            = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelButtonsRight           = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        spaneTable                  = new JScrollPane(tableCoordinates);
        textChangeAltitude          = new JTextField(8);
                
        buttonAddRow.setToolTipText("Add Row");
        buttonChangeAltitude.setToolTipText("Change All Altitudes");
        buttonDeleteRow.setToolTipText("Remove Row");
        buttonReverseCoordinates.setToolTipText("Reverse Coordinates");
        
        buttonAddRow.addActionListener(this);
        buttonChangeAltitude.addActionListener(this);
        buttonDeleteRow.addActionListener(this);
        buttonReverseCoordinates.addActionListener(this);
        buttonSetAltitude.addActionListener(this);
                                 
        panelButtonsCenter.setVisible(false);
        
        populateTable();
    }
    
    /**
     * Populate Coordinate Table with the VectorObject's Coordinate information
     */
    private void populateTable() {
        //remove any existing rows
        while (tableModel.getRowCount() > 0) 
            tableModel.removeRow(0);
        
        if (coordinateList.size() > 0) {            
            for (Coordinate c: coordinateList) {
                addNewRow(c);
            }
        }  
    }
    
    /**
     * Removes the selected row from the Coordinate Table and removes that
     * Coordinate from the VectorObject.
     */
    private void removeSelectedrow() {
        int selectedRow = tableCoordinates.getSelectedRow();

        if (selectedRow >= 0) {
            tableModel.removeRow(tableCoordinates.getSelectedRow());
            coordinateList.remove(selectedRow);
        }
    }    
    
    private void setupPanel() {
        this.setLayout(new BorderLayout());
        this.add(spaneTable,   BorderLayout.CENTER);
        this.add(panelButtons, BorderLayout.SOUTH);
        
        panelButtons.add(panelButtonsLeft,   BorderLayout.WEST);
        panelButtons.add(panelButtonsCenter, BorderLayout.CENTER);
        panelButtons.add(panelButtonsRight,  BorderLayout.EAST);
        
        panelButtonsCenter.add(textChangeAltitude);
        panelButtonsCenter.add(buttonSetAltitude);
        
        panelButtonsLeft.add(buttonReverseCoordinates);
        panelButtonsLeft.add(buttonChangeAltitude);
        
        panelButtonsRight.add(buttonAddRow);
        panelButtonsRight.add(buttonDeleteRow);        
    }
    
    private void setupTable() {
        tableModel = new CoordinateTableModel();        

        addNewRow();

        tableCoordinates = new JTable(tableModel);
        
        //set column width size
        tableCoordinates.getColumnModel().getColumn(0).setMaxWidth(62);  
        tableCoordinates.getColumnModel().getColumn(1).setPreferredWidth(65); 
        tableCoordinates.getColumnModel().getColumn(2).setPreferredWidth(65); 
        tableCoordinates.getColumnModel().getColumn(3).setPreferredWidth(65); 
        tableCoordinates.getColumnModel().getColumn(4).setMinWidth(120); 
    }

    @Override
    public void valueChanged(ListSelectionEvent lse) {
        Coordinate selectedCoordinate;
        int        selectedRow;
        
        selectedRow        = tableCoordinates.getSelectedRow();
        selectedCoordinate = coordinateList.get(selectedRow);
        
        if (object != null)
            object.setSelectedCoordinate(selectedCoordinate);
        
        mainWindow.getMapPanel().update();
    }

}
