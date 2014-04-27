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
package co.foldingmap.map.raster;

import co.foldingmap.map.Layer;
import co.foldingmap.map.MapObjectList;
import co.foldingmap.map.MapView;
import co.foldingmap.map.Overlay;
import co.foldingmap.map.vector.LatLonAltBox;
import co.foldingmap.xml.XmlOutput;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.JMenuItem;

/**
 *
 * @author Alec
 */
public class RasterLayer extends Layer {
    protected ArrayList<Overlay>        overlays;
    
    public RasterLayer(String layerName) {
        this.layerName = layerName;
        this.visible   = true;
    }
    
    /**
     * Adds an Overlay to this layer.
     * 
     * @param overlay 
     */
    public void addOverlay(Overlay overlay) {
        if (overlay != null) {
            //see if the arraylist has been initialied or not.
            if (overlays == null)
                overlays = new ArrayList<Overlay>();

            overlays.add(overlay);
        }
    }    
    
    /**
     * Closes this Layer gracefully.
     */
    @Override
    public void closeLayer() {
        
    }    
    
    @Override
    public Layer copy() {
        RasterLayer layer;
        
        layer = new RasterLayer(this.layerName);
        
        for (Overlay ol: this.getOverlays()) {
            layer.addOverlay(ol);
        }
        
        return layer;
    }

    @Override
    public void drawLayer(Graphics2D g2, MapView mView) {
        if (this.visible) {
            for (Overlay ol: overlays) {
                ol.drawObject(g2, mView);
            }
        }
    }

    @Override
    public LatLonAltBox getBoundary() {
        ImageOverlay   groundOverlay;
        LatLonAltBox    bounds;
        
        bounds = null;
        
        for (Overlay ol: overlays) {
            if (ol instanceof ImageOverlay) {
                groundOverlay = (ImageOverlay) ol;
                
                if (bounds == null) {
                    bounds = groundOverlay.getBounds();
                } else {
                    bounds = LatLonAltBox.combine(bounds, groundOverlay.getBounds());
                }
            }
        }

        
        return bounds;
    }

    @Override
    public float getCenterLongitude() {
        LatLonAltBox    bounds =  getBoundary();
        
        if (bounds != null) {
            return (float) bounds.getCenter().getLongitude();
        } else {
            return 0;
        }        
    }

    @Override
    public float getCenterLatitude() {
        LatLonAltBox    bounds =  getBoundary();
        
        if (bounds != null) {
            return (float) bounds.getCenter().getLatitude();
        } else {
            return 0;
        }   
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
    
    public ArrayList<Overlay> getOverlays() {
        return this.overlays;
    }
    
    public boolean remove(Overlay l) {
        return overlays.remove(l);
    }
    
    @Override
    public MapObjectList selectObjects(Rectangle2D range) {
        MapObjectList   selectedObjects = new MapObjectList();
        
        for (Overlay o: overlays) {
            if (o.isObjectWithinRectangle(range))
                selectedObjects.add(o);
        }
        
        return selectedObjects;
    }

    @Override
    public void toXML(XmlOutput kmlWriter) {
        //TODO: add XML out for raster layer
    }
    
}
    