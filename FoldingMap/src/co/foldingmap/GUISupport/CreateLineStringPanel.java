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

import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

/**
 *
 * @author Alec
 */
public class CreateLineStringPanel extends javax.swing.JPanel {
    private DefaultComboBoxModel comboModelCoordinates, comboModelName;   
    private JComboBox            comboCoordinates, comboName;
    private JLabel               labelCoordinates, labelName;
    
    public CreateLineStringPanel() {
        init();
        setupPanel();
    }
    
    /**
     * Initiate Components
     */
    private void init() { 
        comboModelName        = new DefaultComboBoxModel();
        comboModelCoordinates = new DefaultComboBoxModel();        
        comboCoordinates      = new JComboBox(comboModelCoordinates);        
        comboName             = new JComboBox(comboModelName);
        labelCoordinates      = new JLabel("Coordinates:");
        labelName             = new JLabel("Name:");   
    }    
    
    /**
     * Returns the variable to use for creating coordinates.
     * 
     * @return 
     */
    public String getCoordinatesVariable() {
        return (String) comboModelCoordinates.getSelectedItem();
    }    

    /**
     * Returns the variable to be used for the name.
     * 
     * @return 
     */
    public String getNameVariable() {
        return (String) comboModelName.getSelectedItem();
    }

    /**
     * Sets the header to be placed in the ComboBoxes.
     * 
     * @param headerNames 
     */
    public void setHeaders(ArrayList<String> headerNames) {
        comboModelCoordinates.removeAllElements();
        comboModelName.removeAllElements();
        
        for (String s: headerNames) {
            comboModelCoordinates.addElement(s);
            comboModelName.addElement(s);
            
            if (s.equalsIgnoreCase("Coordinates")) {
                comboModelCoordinates.setSelectedItem(s);
            } else if (s.equalsIgnoreCase("Name")) {
                comboModelName.setSelectedItem(s);
            }
        }        
    }    
    
    /**
     * Sets up the Components in the Panel.
     */
    private void setupPanel() {
        this.setLayout(new GridLayout(2,2));
        
        this.add(labelName);
        this.add(comboName);
        this.add(labelCoordinates);
        this.add(comboCoordinates);
    }    
}
