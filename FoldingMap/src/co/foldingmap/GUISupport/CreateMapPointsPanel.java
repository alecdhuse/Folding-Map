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
public class CreateMapPointsPanel extends javax.swing.JPanel {
    protected ArrayList<String>     headerNames;
    protected DefaultComboBoxModel  comboModelLatitude, comboModelLongitude, comboModelAltitude, comboModelName;
    protected JComboBox             comboLatitude, comboLongitude, comboAltitude, comboName;
    protected JLabel                labelLatitude, labelLongitude, labelAltitude, labelName;
    
    public CreateMapPointsPanel() {
        init();
        setupPanel();
    }
    
    /**
     * Initiate Components
     */
    private void init() { 
        comboModelLatitude  = new DefaultComboBoxModel();
        comboModelLongitude = new DefaultComboBoxModel();
        comboModelAltitude  = new DefaultComboBoxModel();
        comboModelName      = new DefaultComboBoxModel();
        comboLatitude       = new JComboBox(comboModelLatitude);
        comboLongitude      = new JComboBox(comboModelLongitude);
        comboAltitude       = new JComboBox(comboModelAltitude);
        comboName           = new JComboBox(comboModelName);
        labelLatitude       = new JLabel("Latitude");
        labelLongitude      = new JLabel("Longitude");
        labelAltitude       = new JLabel("Altitude");
        labelName           = new JLabel("Name");
    }    
    
    /**
     * Returns the variable to be used for the Altitude.
     * 
     * @return 
     */       
    public String getAltitudeVariable() {
        return (String) comboModelAltitude.getSelectedItem();
    }     
    
    /**
     * Returns the variable to be used for the Latitude.
     * 
     * @return 
     */       
    public String getLatitudeVariable() {
        return (String) comboModelLatitude.getSelectedItem();
    }

    /**
     * Returns the variable to be used for the Longitude.
     * 
     * @return 
     */       
    public String getLongitudeVariable() {
        return (String) comboModelLongitude.getSelectedItem();
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
        this.headerNames = headerNames;

        setupAltitude(); 
        setupLatitude();
        setupLongitude();
        setupName();              
    }    
    
    /**
     * Setup the Altitude combo box, with the selected value being something
     * related to Altitude.
     */
    private void setupAltitude() {
        comboModelAltitude.removeAllElements();
        comboModelAltitude.addElement("(None)");

        for (String s: headerNames) {
            comboModelAltitude.addElement(s);

            if (s.equalsIgnoreCase("Altitude") || s.equalsIgnoreCase("Alt"))
                comboModelAltitude.setSelectedItem(s);
        }        
    }     
    
    /**
     * Setup the Latitude combo box, with the selected value being something
     * related to Latitude.
     */    
    private void setupLatitude() {
        comboModelLatitude.removeAllElements();

        for (String s: headerNames) {
            comboModelLatitude.addElement(s);

            if (s.equalsIgnoreCase("Latitude") || s.equalsIgnoreCase("Lat"))
                comboModelLatitude.setSelectedItem(s);
        }
    }

    /**
     * Setup the Longitude combo box, with the selected value being something
     * related to Longitude.
     */    
    private void setupLongitude() {
        comboModelLongitude.removeAllElements();

        for (String s: headerNames) {
            comboModelLongitude.addElement(s);

            if (s.equalsIgnoreCase("Longitude") || s.equalsIgnoreCase("Lon"))
                comboModelLongitude.setSelectedItem(s);
        }
    }
       
   /**
     * Setup the Name combo box, with the selected value being something
     * related to Name.
     */     
    private void setupName() {
        comboModelName.removeAllElements();

        for (String s: headerNames) {
            comboModelName.addElement(s);

            if (s.equalsIgnoreCase("Name") || s.equalsIgnoreCase("Label"))
                comboModelName.setSelectedItem(s);
        }
    }
    
    /**
     * Sets up the Components in the Panel.
     */
    private void setupPanel() {
        this.setLayout(new GridLayout(4, 2, 3, 3));
        
        this.add(labelName);
        this.add(comboName);
        this.add(labelLatitude);
        this.add(comboLatitude);
        this.add(labelLongitude);
        this.add(comboLongitude);
        this.add(labelAltitude);
        this.add(comboAltitude);
    }    
}
