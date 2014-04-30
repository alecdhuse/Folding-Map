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
import co.foldingmap.MainWindow;
import co.foldingmap.MapObjectPropertiesWindow;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.Layer;
import co.foldingmap.map.MapView;
import co.foldingmap.map.raster.ImageOverlay;
import co.foldingmap.map.raster.RasterLayer;
import co.foldingmap.map.vector.LatLonAltBox;
import co.foldingmap.map.vector.LatLonBox;
import co.foldingmap.map.vector.MapIcon;

/**
 *
 * @author Alec
 */
public class AddGroundOverlay extends Action {
    private DigitalMap      mapData;
    private ImageOverlay    newOverlay;
    private LatLonBox       overlayBounds;
    private MapView         mapView;
    private MainWindow      mainWindow;
    private RasterLayer     parentLayer;
        
    public AddGroundOverlay(MainWindow mainWindow, DigitalMap mapData, MapView mapView) {
        this.mainWindow     = mainWindow;
        this.mapData        = mapData;
        this.mapView        = mapView;
        this.overlayBounds  = mapView.getViewBounds();       
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
            newOverlay = new ImageOverlay("New Overlay", new MapIcon("New Map Icon", ""), overlayBounds);
            newOverlay.setLastMapView(mapView);
            
            if (mapData.containsRasterLayer()) {
                if (mapData.getSelectedLayer() instanceof RasterLayer) {
                    parentLayer = (RasterLayer) mapData.getSelectedLayer();
                } else {
                    for (Layer l: mapData.getLayers()) {
                        if (l instanceof RasterLayer) {
                            parentLayer = (RasterLayer) l;
                            break;
                        }
                    }
                }
            } else {
                //The map dose not contain a RasterLayer, create a new one.
                parentLayer = new RasterLayer("New Raster Layer");
                mapData.addLayer(parentLayer, 0);
                mainWindow.updateLayersTree();
            }            

            new MapObjectPropertiesWindow(mainWindow, newOverlay);
            parentLayer.addOverlay(newOverlay);
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in AddGroundOverlay.execute() - " + e);
        }
    }

    @Override
    public void undo() {
        if (parentLayer != null && newOverlay != null) {
            parentLayer.remove(newOverlay);
        }
    }
    
}
