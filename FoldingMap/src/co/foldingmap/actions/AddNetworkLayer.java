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
package co.foldingmap.actions;

import co.foldingmap.GUISupport.panels.NetworkLayerPropertiesPanel;
import co.foldingmap.LayerProperties;
import co.foldingmap.MainWindow;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.vector.NetworkLayer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Alec
 */
public class AddNetworkLayer extends Action implements ActionListener {

    private DigitalMap   mapData;
    private MainWindow   mainWindow;
    private NetworkLayer newLayer;    
    
    public AddNetworkLayer(MainWindow mainWindow, DigitalMap mapData) {
        this.mainWindow = mainWindow;
        this.mapData    = mapData;        
    }
    
    /**
     * If the OK button is pushed the new layer is added to the map.
     * 
     * @param ae 
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        String actionEvent = ae.getActionCommand();
         
        if (actionEvent.equalsIgnoreCase("Ok")) {
            newLayer.setParentMap(mapData);
            mapData.addLayer(newLayer, 0);
            mainWindow.updateLayersTree();            
        }
    }    
        
    
    /**
     * Returns if this Action can be undone.
     * 
     * @return 
     */
    @Override
    public boolean canUndo() {
        return true;
    }    
    
    @Override
    public void execute() {
        LayerProperties             layerPropertiesDialog;
        NetworkLayerPropertiesPanel nlPanel;
        
        layerPropertiesDialog = new LayerProperties(mainWindow);
        newLayer = new NetworkLayer("New Network Layer", "");
        
        nlPanel  = new NetworkLayerPropertiesPanel(mainWindow, layerPropertiesDialog, newLayer);
        nlPanel.setSecondaryActionListener(this);
        
        layerPropertiesDialog.setLayerPanel(nlPanel);
        layerPropertiesDialog.setVisible(true);
    }

    @Override
    public void undo() {
        mapData.removeLayer(newLayer);
    }
    
}
