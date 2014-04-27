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

import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.Layer;
import java.util.ArrayList;

/**
 * Move the selected layer in the map down in order.
 * 
 * @author Alec
 */
public class MoveSelectedLayerUp extends Action {
    private ArrayList<Layer>    layers;
    private int                 originalIndex;
    private Layer               selectedLayer;
    
    public MoveSelectedLayerUp(DigitalMap mapData) {
        this.commandDescription = "Move Selected Layer Up";
        this.layers             = mapData.getLayers();
        this.selectedLayer      = mapData.getSelectedLayer();
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
        Layer currentLayer;

        for (int l = 0; l < layers.size(); l++) {
            currentLayer = layers.get(l);

            if (this.selectedLayer == currentLayer) {
                layers.remove(l);

                if (l > 0) {
                    layers.add((l-1), this.selectedLayer);
                    originalIndex = l-1;
                }
                
                break;
            }
        }
    }

    @Override
    public void undo() {
        layers.remove(selectedLayer);
        layers.add(originalIndex, selectedLayer);
    }
    
}
