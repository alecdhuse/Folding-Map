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

import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.tile.TileLayer;
import co.foldingmap.map.tile.TileServerTileSource;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Alec
 */
public class TileServerOptionsPanel extends OptionsPanel {
    private DigitalMap  mapData;
    private JPanel      panelAddress;
    private JTextField  textServerAddress;
    private String      serverAddress;
    
    public TileServerOptionsPanel(DigitalMap mapData) {
        this.mapData    = mapData;
        
        init();
        addObjectsTo();
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        TileLayer            newLayer;
        TileServerTileSource tileSource;
        
        serverAddress = textServerAddress.getText();
        tileSource    = new TileServerTileSource(serverAddress, "OpenAreal");
        newLayer      = new TileLayer(tileSource);
        
        mapData.addLayer(newLayer);
    }
    
    private void addObjectsTo() {
        this.setLayout(new BorderLayout());
        this.add(panelAddress, BorderLayout.NORTH);
        
        panelAddress.add(textServerAddress);
    }
    
    private void init() {
        panelAddress      = new JPanel();
        serverAddress     = "oatile1.mqcdn.com/tiles/1.0.0/sat";
        textServerAddress = new JTextField(serverAddress, 30);
        
        panelAddress.setBorder(new TitledBorder("Server Address"));
    }
}
