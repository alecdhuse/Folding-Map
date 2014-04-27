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

import co.foldingmap.GUISupport.panels.VectorLayerPropertiesPanel;
import co.foldingmap.LayerProperties;
import co.foldingmap.MainWindow;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.vector.VectorLayer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Adds a new vector layer and pops up a dialog for entering the layer's info.
 * 
 * @author Alec
 */
public class AddVectorLayer extends Action implements ActionListener {

    private DigitalMap  mapData;
    private MainWindow  mainWindow;
    private VectorLayer newLayer;
    
    public AddVectorLayer(MainWindow mainWindow, DigitalMap mapData) {
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
            mapData.addLayer(newLayer);
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
        VectorLayerPropertiesPanel vPanel;
        
        newLayer = new VectorLayer("New Layer");
        
        vPanel = new VectorLayerPropertiesPanel(newLayer);
        vPanel.setSecondaryActionListener(this);
        new LayerProperties(mainWindow, vPanel);
    }

    @Override
    public void undo() {
        mapData.removeLayer(newLayer);
    }
    
}
