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

import co.foldingmap.GUISupport.panels.ActionPanel;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.tile.TileLayer;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Alec
 */
public class TileLayerProperties extends ActionPanel {
    private JLabel      labelName, labelSource;
    private JPanel      panelCenter, panelName, panelSource;
    private JTextField  textName, textSource;
    private TileLayer   tileLayer;
    
    public TileLayerProperties(DigitalMap mapData, TileLayer tileLayer) {
        this.tileLayer  = tileLayer;
        
        init();
        addObjectsToFrame();
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {

    }
    
    /**
     * Adds objects to the main window and sets up the layout
     */
    private void addObjectsToFrame() {
        this.setLayout(new BorderLayout());
        this.add(panelName,   BorderLayout.NORTH);
        this.add(panelCenter, BorderLayout.CENTER);
        
        panelName.add(labelName, BorderLayout.WEST);
        panelName.add(textName,  BorderLayout.CENTER);        
        
        panelCenter.add(panelSource);
        
        panelSource.add(labelSource);
        panelSource.add(textSource);
    }    
    
    private void init() {
        labelName   = new JLabel("Name");
        labelSource = new JLabel("Source");
        panelCenter = new JPanel();
        panelName   = new JPanel(new BorderLayout());
        panelSource = new JPanel();
        textName    = new JTextField(tileLayer.getName());
        textSource  = new JTextField(tileLayer.getTileSource().getSource());
    }
}
