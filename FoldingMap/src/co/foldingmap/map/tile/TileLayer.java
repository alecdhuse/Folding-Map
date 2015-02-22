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
package co.foldingmap.map.tile;

import co.foldingmap.Logger;
import co.foldingmap.map.Layer;
import co.foldingmap.map.MapObject;
import co.foldingmap.map.MapObjectList;
import co.foldingmap.map.MapView;
import co.foldingmap.map.vector.LatLonAltBox;
import co.foldingmap.map.vector.LatLonBox;
import co.foldingmap.xml.XmlOutput;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JMenuItem;

/**
 *
 * @author Alec
 */
public class TileLayer extends Layer {
    private TileSource  tileSource;
    
    public TileLayer(TileSource tileSource) {
        this.layerDescription   = tileSource.getDescription();
        this.layerName          = tileSource.getName();
        this.tileSource         = tileSource;
        this.visible            = true;        
    }
    
    public TileLayer(String name, String description, boolean visible, TileSource tileSource) {
        this.layerName        = name;
        this.layerDescription = description;
        this.tileSource       = tileSource;
        this.visible          = visible;        
    }    
    
    /**
     * Closes this Layer gracefully.
     */
    @Override
    public void closeLayer() {
        this.tileSource.closeSource();
    }    
    
    @Override
    public Layer copy() {
        Layer layerCopy = new TileLayer(getTileSource());
        
        layerCopy.setName(layerName);
        layerCopy.setVisible(visible);
        layerCopy.setParentMap(parentMap);
        layerCopy.setLayerDescription(layerDescription);
        
        return layerCopy;
    }

    @Override
    public void drawLayer(Graphics2D g2, MapView mapView) {
        BufferedImage   image;
        float           longitudeW, longitudeE, tileZoom;
        float           tileX, tileX2, tileY;
        int             size;
        LatLonBox       viewBounds;
        TileReference   currentTileRef, tileRef0, tileRef1;
        
        try {
            if (this.visible) {     
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
                
                viewBounds = mapView.getViewBounds();
                longitudeE = viewBounds.getEast();
                longitudeW = viewBounds.getWest();
                tileZoom   = Math.round(TileMath.getTileMapZoom(mapView.getZoomLevel()));
                
                if (tileZoom > this.tileSource.maxZoom) {
                    tileZoom = tileSource.maxZoom;
                } else if (tileZoom < this.tileSource.minZoom) {
                    tileZoom = tileSource.minZoom;
                }

                //The west bounds is over the IDL
                if (longitudeW > longitudeE) {
                    longitudeW = -180;
                }

                if ((longitudeE < longitudeW) || tileZoom <= 4) {
                    longitudeE = 180;        
                }

                tileRef0 = TileReference.getTileReference(viewBounds.getNorth(), longitudeW, (int) tileZoom);
                tileRef1 = TileReference.getTileReference(viewBounds.getSouth(), longitudeE, (int) tileZoom);    

                //determine scale
                tileX  = mapView.getX(tileRef0.getCoordinate(), MapView.NO_WRAP);
                tileX2 = mapView.getX(tileRef0.getTileOffset(1, 0).getCoordinate(), MapView.NO_WRAP);            
                size   = (int) (tileX2 - tileX);                                         

                tileX  = mapView.getX(tileRef1.getCoordinate(), MapView.NO_WRAP);                
                int x  = tileRef1.getX(); 
                
                if (size > 0) {
                    while (tileX > (size * -1)) {                        
                        tileY = mapView.getY(tileRef1.getCoordinate());

                        for (int y = tileRef1.getY(); y >= tileRef0.getY(); y--) {
                            currentTileRef = new TileReference(x, y, (int) tileZoom);
                            image = tileSource.getTileImage(currentTileRef);

                            if (image != null) {                                          
                                g2.drawImage(image, (int) tileX, (int) tileY, (int) size, (int) size, null);   
                                tileY -= size;
                            }                                        
                        }

                        tileX -= size;
                        x = (x - 1) % ((int) Math.pow(2, tileZoom));
                    }              
                }
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in TileLayer.drawLayer(Graphics2D, MapView) - " + e);
        }
    }

    /**
     * Returns the boundary that contains all the tiles in this layer.
     * 
     * @return 
     */
    @Override
    public LatLonAltBox getBoundary() {
        if (this.getTileSource().getBoundingBox() != null) {
            return this.getTileSource().getBoundingBox();
        } else {
            return new LatLonAltBox(85.05f, -85.05f, 180f, -180f, 0f, 0f);
        }
    }    
    
    /**
     * Returns the center longitude for this TileLayer.
     * 
     * @return 
     */
    @Override
    public float getCenterLongitude() {
        return (float) this.getTileSource().getBoundingBox().getCenter().getLongitude();
    }

    /**
     * Returns the center latitude for this TileLAyer.
     * 
     * @return 
     */
    @Override
    public float getCenterLatitude() {
        return (float) this.getTileSource().getBoundingBox().getCenter().getLatitude();
    }

    /**
     * Returns JMenuItems that should be used in the context menu for this Layer
     * 
     * @return 
     */
    @Override
    public JMenuItem[] getContextMenuItems() {        
        return new JMenuItem[0];
    }    
    
    /**
     * Returns the TileSource for this TileLayer;
     * 
     * @return 
     */
    public TileSource getTileSource() {
        return this.tileSource;
    }
    
    @Override
    public MapObjectList selectObjects(Rectangle2D range) {
        //TODO: Have the ability to select tiles as MapObjects
        return new MapObjectList<MapObject>();
    }

    /**
     * Sets the TileSource for this TileLayer.
     * 
     * @param tileSource    The TileSource used to fetch tiles for this layer.
     */
    public void setTileSource(TileSource tileSource) {
        this.tileSource = tileSource;
    }
    
    /**
     * Writes out this TileLayer to FmXML.
     * 
     * @param xmlWriter 
     */
    @Override
    public void toXML(XmlOutput xmlWriter) {
        xmlWriter.openTag("TileLayer");
        
        xmlWriter.writeTag("name",        layerName);
        xmlWriter.writeTag("description", layerDescription);
        xmlWriter.writeTag("visible",     Boolean.toString(visible));
        
        //Write out Tile Source
        tileSource.toXML(xmlWriter);
        
        xmlWriter.closeTag("TileLayer");
    }
    
}