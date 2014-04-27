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
import co.foldingmap.map.themes.ColorRamp;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;
import co.foldingmap.map.visualization.HeatMap;
import co.foldingmap.map.visualization.HeatMapKey;

/**
 * Creates a HeatMap of the selected objects with a given field value.
 * 
 * @author Alec
 */
public class CreateHeatMap extends Action {
    private ColorRamp                       colorRamp;
    private DigitalMap                      mapData;    
    private HeatMapKey                      heatMapKey;
    private int                             displayInterval;
    private VectorObjectList<VectorObject>  objects;
    private String                          layerName;
    private String[]                        fieldName;   
    private Layer                           newLayer;
    
    public CreateHeatMap(DigitalMap mapData, 
                         String     layerName,
                         VectorObjectList<VectorObject> objects, 
                         String[]   fieldName,
                         ColorRamp  colorRamp,
                         int        displayInterval,
                         HeatMapKey heatMapKey) {
        
        this.layerName          = layerName;
        this.mapData            = mapData;
        this.colorRamp          = colorRamp;
        this.objects            = objects;
        this.fieldName          = fieldName;
        this.commandDescription = "Create Heat Map";
        this.displayInterval    = displayInterval;        
        this.heatMapKey         = heatMapKey;
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
        newLayer = new HeatMap(layerName, objects, colorRamp.getID(), fieldName, displayInterval, heatMapKey);
        
        mapData.getTheme().addColorRamp(colorRamp);
        mapData.addLayer(newLayer, 0);
    }

    @Override
    public void undo() {
        mapData.removeLayer(newLayer);
        mapData.getTheme().removeColorRamp(colorRamp);
    }
}
