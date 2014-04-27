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

import co.foldingmap.MainWindow;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.Layer;

/**
 * Removes the selected layer form the map.
 * 
 * @author Alec
 */
public class DeleteSelectedLayer extends Action {
    private DigitalMap  mapData;
    private Layer       layer;    
    private MainWindow  mainWindow;
    
    public DeleteSelectedLayer(MainWindow mainWindow, DigitalMap mapData) {
        this.mapData    = mapData;
        this.layer      = mapData.getSelectedLayer();
        this.commandDescription = "Delete Layer";
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
        mapData.removeLayer(layer);
    }

    @Override
    public void undo() {
        mapData.addLayer(layer);
        mainWindow.updateLayersTree();
    }
    
}
