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
package co.foldingmap.GUISupport.components;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Alec
 */
public class HeatMapValueTableModel extends AbstractTableModel {
    private ArrayList<Color>   colorValues;
    private ArrayList<JButton> colorButtons, removeButtons;
    private ArrayList<JLabel>  labels;    
    private ArrayList<String>  customDataValues;    
    private ActionListener     actionListener;
    
    public HeatMapValueTableModel(ActionListener actionListener, ArrayList<String> customDataValues) {
        this.colorButtons      = new ArrayList<JButton>();
        this.colorValues       = new ArrayList<Color>();
        this.customDataValues  = customDataValues;
        this.actionListener = actionListener;
        this.labels            = new ArrayList<JLabel>();
        this.removeButtons     = new ArrayList<JButton>();
                
        colorValues.add(new Color(141, 211, 199));
        colorValues.add(new Color(255, 255, 179));
        colorValues.add(new Color(190, 186, 218));
        colorValues.add(new Color(251, 128, 114));
        colorValues.add(new Color(128, 177, 211));
        colorValues.add(new Color(253, 180,  98));
        colorValues.add(new Color(179, 222, 105));
        colorValues.add(new Color(252, 205, 229));
        colorValues.add(new Color(217, 217, 217));
        colorValues.add(new Color(188, 128, 189));
        colorValues.add(new Color(204, 235, 197));
        colorValues.add(new Color(255, 237, 111));
        
        updateRowItems();
    }
    
    /**
     * Returns the assigned color for a given value.
     * 
     * @param value
     * @return 
     */
    public Color getColorForValue(String value) {
        Color color;
        int   index;
        
        index = customDataValues.indexOf(value);
        
        if (index < colorValues.size()) {
            color = colorValues.get(index);
        } else {
            color = Color.BLACK;
        }
                
        return color;
    }
    
    /**
     * Returns all the color values for this TableModel.
     * 
     * @return 
     */
    public ArrayList<Color> getColorValues() {
        return this.colorValues;
    }
    
    @Override
    public int getRowCount() {
        return customDataValues.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Class getColumnClass(int columnIndex) { 
        if (columnIndex == 0) {
            return JLabel.class;
        } else  {
            return JButton.class;
        } 
    }    
    
    @Override
    public String getColumnName(int columnIndex) { 
        if (columnIndex == 0) {
            return "Object Value";
        } else if (columnIndex == 1) {
            return "Color";
        } else  {
            return "";
        }
    }    
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object      object;
        
        if (columnIndex == 0) {
            object = labels.get(rowIndex);
        } else if (columnIndex == 1) {            
            object = colorButtons.get(rowIndex);
        } else {
            object = removeButtons.get(rowIndex);
        } 
        
        return object;
    }
    
    @Override
    public boolean isCellEditable(int columnIndex, int rowIndex) { 
        return true;
    }     
    
    /**
     * Removes a given row index from this table model.
     * 
     * @param index 
     */
    public void removeRow(int index) {
        Color   color;
        
        //make sure color values shift when an object is deleted.
        color =  colorValues.remove(index);        
        colorValues.add(color);
        
        customDataValues.remove(index);        
        
        updateRowItems();
        fireTableDataChanged();
    }    
    
    /**
     * Sets the color for a given index.
     * 
     * @param index
     * @param newColor 
     */
    public void setColorValue(int index, Color newColor) {
        colorValues.remove(index);
        colorValues.add(index, newColor);
    }
    
    /**
     * Sets the data to be used in this table model
     * @param customDataValues 
     */
    public void setTableData(ArrayList<String> customDataValues) {
        this.customDataValues = customDataValues;
        updateRowItems();
        fireTableDataChanged();
    }
    
    /**
     * Creates the objects used to display in the rows of the table.
     */
    private void updateRowItems() {
        Color   color;
        JButton colorButton, removeButton;
        JLabel  label;
        String  values;
        
        //clear out old valiues
        colorButtons.clear();
        removeButtons.clear();
        labels.clear();            
        
        for (int i = 0; i < customDataValues.size(); i++) {
            values       = customDataValues.get(i);
            label        = new JLabel(values);
            colorButton  = new JButton(" ");
            removeButton = new JButton("Remove");
            
            colorButton.setOpaque(true);
            colorButton.setBorder(null);
            colorButton.setActionCommand("Change Color");
            colorButton.addActionListener(actionListener);
            
            if (i < colorValues.size()) {
                color = colorValues.get(i);
            } else {
                color = Color.BLACK;
            }         
            
            colorButton.setBackground(color);   
            colorButton.setForeground(color);
            
            removeButton.setActionCommand("Remove");
            removeButton.addActionListener(actionListener); 
            
            colorButtons.add(colorButton);
            removeButtons.add(removeButton);
            labels.add(label);         
        }
    }
}
