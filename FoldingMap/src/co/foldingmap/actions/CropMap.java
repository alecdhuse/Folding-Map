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

import co.foldingmap.map.vector.LatLonBox;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.VectorObjectList;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.Logger;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.Layer;
import java.util.ArrayList;

/**
 * Crops the map to a given bounds.
 * 
 * @author Alec
 */
public class CropMap extends Action {
    protected ArrayList<Layer>  newLayers, oldLayers;
    protected DigitalMap        mapData; 
    protected LatLonBox         bounds;
    
    public CropMap(DigitalMap mapData, LatLonBox bounds) {
        this.bounds             = bounds;
        this.commandDescription = "Crop Map";
        this.mapData            = mapData;
        this.oldLayers          = new ArrayList<Layer>();
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
        VectorObjectList<VectorObject> objects;
        VectorLayer              currentVectorLayer;
        
        try {
            bounds    = mapData.getLastMapView().getViewBounds();
            newLayers = mapData.getLayers();

            for (Layer l: newLayers) {
                oldLayers.add(l.copy());

                if (l instanceof VectorLayer) {
                    currentVectorLayer = (VectorLayer) l;
                    objects = currentVectorLayer.getObjectList();
                    objects = objects.fitObjectsToBoundary(bounds);
                    currentVectorLayer.setObjectList(objects);
                }
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in CropMap.execute() - " + e);
        }
    }

    @Override
    public void undo() {
        mapData.removeAllLayers();
        
        for (Layer l: oldLayers)
            mapData.addLayer(l);
    }
    
}
