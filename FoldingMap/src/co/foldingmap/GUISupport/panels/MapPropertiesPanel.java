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

import co.foldingmap.map.DigitalMap;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Alec
 */
public class MapPropertiesPanel extends ActionPanel {
    private DigitalMap  mapData;
    private JPanel      panelName;
    private JScrollPane spaneDescription;
    private JTextArea   textDescription;
    private JTextField  textName;
    
    public MapPropertiesPanel(DigitalMap mapData) {
        this.mapData = mapData;
        
        init();
        setupPanel();
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("cancel")) {
                    
        } else if (ae.getActionCommand().equals("ok")) {
            mapData.setName(textName.getText());
            mapData.setMapDescription(textDescription.getText());
        }
    }    
    
    private void init() {
        panelName        = new JPanel(new BorderLayout());
        textName         = new JTextField(mapData.getName());
        textDescription  = new JTextArea(mapData.getMapDescription());
        spaneDescription = new JScrollPane(textDescription);
                
        panelName.setBorder(new TitledBorder("Name"));
        spaneDescription.setBorder(new TitledBorder("Description"));
        
        textDescription.setLineWrap(true);        
    }
    
    public void setMap(DigitalMap mapData) {
        textName.setText(mapData.getName());
        textDescription.setText(mapData.getMapDescription());
    }
    
    private void setupPanel() {
        this.setLayout(new BorderLayout());
        this.add(panelName,        BorderLayout.NORTH);
        this.add(spaneDescription, BorderLayout.CENTER);
        
        panelName.add(textName, BorderLayout.CENTER);
    }

}
