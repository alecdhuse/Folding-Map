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
import co.foldingmap.map.MapView;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.MapPoint;
import co.foldingmap.map.vector.VectorLayer;

/**
 * Adds a new MapPoint to the map at the last clicked location.
 * 
 * @author Alec
 */
public class AddMapPoint extends Action {
    protected DigitalMap  mapData;
    protected MapPoint    newPoint;
    
    public AddMapPoint(DigitalMap mapData) {
        this.mapData = mapData;
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
        Coordinate  clickCoordinate;
        Layer       layer;
        MapView     mapView;
        VectorLayer vectorLayer;
        
        mapView         = mapData.getLastMapView();
        clickCoordinate = mapView.getLastMouseClickCoordinate();
        newPoint        = new MapPoint("New Point", "(Unspecified Point)", "", clickCoordinate);        
        layer           = mapData.getSelectedLayer();
        vectorLayer     = null;
        
        newPoint.setReference(mapData.getNewObjectReference());
        clickCoordinate.addParent(newPoint);
        
        //add point to the node map
        this.mapData.addCoordinateNode(clickCoordinate);          
        
        if (layer instanceof VectorLayer) {
            vectorLayer = (VectorLayer) layer;
        } else {
            for (Layer l: mapData.getLayers()) {
                if (l instanceof VectorLayer) {
                    vectorLayer = (VectorLayer) l;
                    break;
                }
            }
        }
        
        if (vectorLayer != null)
            vectorLayer.addObject(newPoint);        
    }

    @Override
    public void undo() {
        VectorLayer parentLayer;
        
        parentLayer = (VectorLayer) newPoint.getParentLayer();
        parentLayer.removeObject(newPoint);
        newPoint.getCoordinateList().get(0).removeParent(newPoint);
    }
    
}
