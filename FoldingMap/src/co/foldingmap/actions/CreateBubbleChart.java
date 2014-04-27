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
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;
import co.foldingmap.map.visualization.BubblePoint;

/**
 *
 * @author Alec
 */
public class CreateBubbleChart extends Action {
    private DigitalMap                     mapData;
    private String                         colorVariable, rampID, sizeVariable;
    private VectorObjectList<VectorObject> objects;
            
    public CreateBubbleChart(DigitalMap mapData,
                             VectorObjectList<VectorObject> objects, 
                             String    rampID, 
                             String    colorVariable, 
                             String    sizeVariable) {
        
        this.mapData       = mapData;
        this.colorVariable = colorVariable;
        this.sizeVariable  = sizeVariable;
        this.rampID        = rampID;
        this.objects       = objects;
    }
    
    /**
     * Returns if this Action can be undone.
     * 
     * @return 
     */
    @Override
    public boolean canUndo() {
        return false;
    }    
    
    @Override
    public void execute() {
        BubblePoint bubblePoint;
        VectorLayer layer;
        
        layer = new VectorLayer("Bubble Chart");
        mapData.addLayer(layer, 0);
        
        for (VectorObject object: objects) {
            if (object.getCustomDataFieldValue(colorVariable) != null || 
                object.getCustomDataFieldValue(sizeVariable)  != null) {
                            
                bubblePoint = new BubblePoint(object, rampID, colorVariable, sizeVariable);
                layer.addObject(bubblePoint);
            }
        }                
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
