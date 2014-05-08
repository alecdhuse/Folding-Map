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
package co.foldingmap.mapImportExport;

import co.foldingmap.GUISupport.ProgressIndicator;
import co.foldingmap.Logger;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.Layer;
import co.foldingmap.map.MercatorProjection;
import co.foldingmap.map.tile.MbTileSource;
import co.foldingmap.map.tile.TileLayer;
import co.foldingmap.map.tile.TileMath;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.LatLonAltBox;
import co.foldingmap.map.vector.NodeMap;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Alec
 */
public class MbTilesImporter implements FormatImporter {

    public MbTilesImporter() {
        
    }
    
    /**
     * Adds a new the MapSource from mapFile to the given TileLayer.
     * 
     * @param mapFile           The file containing the map to import.
     * @param nodeMap           The NodeMap to add new Coordinates to.
     * @param layer             The TileLayer to add TileSource to.
     * @param progressIndicator Optional, to display the progress of the import.
     */
    @Override
    public void importToLayer(File mapFile, NodeMap nodeMap, Layer layer, ProgressIndicator progressIndicator) throws IOException {
        if (layer instanceof TileLayer) {
            TileLayer    tileLayer = (TileLayer) layer;
            MbTileSource mbTiles   = new MbTileSource(mapFile.getPath()); 
            
            tileLayer.setTileSource(mbTiles);
        } else {
            Logger.log(Logger.ERR, "Error in MbTilesImporter.importToVectorLayer(File, NodeMap, Layer, ProgressIndicator) - Supplied Layer must be a TileLayer.");
        }
    }

    /**
     * Creates a new map with the given file as the TileSource in a new TileLayer.
     * 
     * @param mapFile
     * @param progressIndicator
     * @return
     * @throws IOException 
     */
    @Override
    public DigitalMap importAsMap(File mapFile, ProgressIndicator progressIndicator) throws IOException {
        DigitalMap         newMap;
        MercatorProjection proj;
        MbTileSource       mbTiles   = new MbTileSource(mapFile.getPath());                    
        TileLayer          tileLayer = new TileLayer(mbTiles);
        LatLonAltBox       bounds    = mbTiles.getBoundingBox();           
        float              zoomLevel = TileMath.getVectorMapZoom(mbTiles.getMinZoom());

        if (bounds != null) {
            proj = new MercatorProjection(bounds.getNorth(), bounds.getWest(), zoomLevel);
        } else {
            proj = new MercatorProjection();
        }

        if (mbTiles.getZoom() > 0) {
            proj.setZoomLevel(TileMath.getVectorMapZoom(mbTiles.getZoom())); 
        } else {
            proj.setZoomLevel(TileMath.getVectorMapZoom(mbTiles.getMinZoom())); 
        }
        
        newMap = new DigitalMap(mbTiles.getName(), proj);

        //Set map description from the layer description.
        newMap.setMapDescription(tileLayer.getDescription());

        if (mbTiles.getCenter() != null) {
            newMap.setLookAtCoordinate(mbTiles.getCenter());
        } else {
            if (bounds.getNorth() >= 90) {
                newMap.setLookAtCoordinate(new Coordinate(0, 85.0511f, bounds.getWest()));  
            } else {
                newMap.setLookAtCoordinate(new Coordinate(0, bounds.getNorth(), bounds.getWest()));  
            }     
        }

        newMap.addLayer(tileLayer);
        
        return newMap;
    }
    
}
