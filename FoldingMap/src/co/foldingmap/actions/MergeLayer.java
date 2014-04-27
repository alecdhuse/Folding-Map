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

import co.foldingmap.Logger;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.Layer;
import co.foldingmap.map.vector.VectorLayer;
import java.util.Date;

/**
 * Merges two layers together into one.
 * 
 * @author Alec
 */
public class MergeLayer extends Action {
    DigitalMap  mapData;
    Layer       layerToMerge, layerAcceptingMerge;
    Layer       originalLayerToMerge, originalLayerAcceptingMerge;

    public MergeLayer(DigitalMap mapData, Layer layerToMerge, Layer layerAcceptingMerge) {
        this.commandDescription          = "Merge Layer";
        this.layerToMerge                = layerToMerge;
        this.layerAcceptingMerge         = layerAcceptingMerge;
        this.mapData                     = mapData;
        this.originalLayerToMerge        = layerToMerge.copy();
        this.originalLayerAcceptingMerge = layerAcceptingMerge.copy();        
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
        try {
            //Are layers the same type
            if (layerToMerge.getClass() == layerAcceptingMerge.getClass()) {
            
                if (layerToMerge instanceof VectorLayer) {
                    mergeVectorLayers();
                    mergeTimeSpans();
                } else {
                    Logger.log(Logger.WARN, "Error in MergeLayers.execute() - Layers type merge not supported.");
                }

            } else {
                Logger.log(Logger.WARN, "Error in MergeLayers.execute() - Layers are not of the same type.");
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in MergeLayers.execute() - " + e);
        }
    }

    /**
     * Merge the time spans from two different layers together
     */
    private void mergeTimeSpans() {
        Date layerToMergeBegin, layerToMergeEnd;
        Date layerAcceptingMergeBegin, layerAcceptingMergeEnd;
        try {
            layerToMergeBegin        = layerToMerge.getTimeSpanBegin();
            layerToMergeEnd          = layerToMerge.getTimeSpanEnd();
            layerAcceptingMergeBegin = layerAcceptingMerge.getTimeSpanBegin();
            layerAcceptingMergeEnd   = layerAcceptingMerge.getTimeSpanEnd();
                    
            if ((layerToMergeBegin        != null) &&
                (layerToMergeEnd          != null) &&
                (layerAcceptingMergeBegin != null) &&
                (layerAcceptingMergeEnd   != null)) {    
                                
                if (layerToMergeBegin.before(layerAcceptingMergeBegin)) 
                    layerAcceptingMerge.setTimeSpanBegin(layerToMergeBegin);                

                if (layerToMergeEnd.after(layerAcceptingMergeEnd)) 
                    layerAcceptingMerge.setTimeSpanEnd(layerToMergeEnd);                
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in MergeLayer.mergeTimeSpans() - " + e);
        }
    }
    
    /**
     * Merges two vector layers together.
     */
    private void mergeVectorLayers() {
        VectorLayer vectorLayerToMerge, vectorLayerAcceptingMerge;

        vectorLayerToMerge        = (VectorLayer) layerToMerge;
        vectorLayerAcceptingMerge = (VectorLayer) layerAcceptingMerge;

        vectorLayerAcceptingMerge.addAllObjects(vectorLayerToMerge.getObjectList());
        mapData.removeLayer(vectorLayerToMerge);
    }    
    
    @Override
    public void undo() {
        try {
            mapData.removeLayer(layerAcceptingMerge);
            mapData.addLayer(originalLayerToMerge);
            mapData.addLayer(originalLayerAcceptingMerge);
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in CommandMergeLayers.unExecute() - " + e);
        }
    }
}
