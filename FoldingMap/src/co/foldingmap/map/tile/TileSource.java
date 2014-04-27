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

import co.foldingmap.map.vector.LatLonAltBox;
import co.foldingmap.xml.XmlOutput;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

/**
 *
 * @author Alec
 */
public abstract class TileSource {
    protected int           maxZoom, minZoom;  
    protected LatLonAltBox  boundingBox;
    protected String        description, name;
    
    public abstract void          closeSource();    
    public abstract String        getSource();
    public abstract BufferedImage getTileImage(TileReference tr);
    public abstract void          toXML(XmlOutput xmlWriter);
            
    public static BufferedImage convertRenderedImage(RenderedImage img) {
        boolean         isAlphaPremultiplied;
        ColorModel      cm;
        Hashtable       properties;
        int             height, width;
        String[]        keys;
        WritableRaster  raster;
        
        try {
            if (img != null) {
                if (img instanceof BufferedImage) 
                        return (BufferedImage)img;	

                cm     = img.getColorModel();
                width  = img.getWidth();
                height = img.getHeight();
                raster = cm.createCompatibleWritableRaster(width, height);
                isAlphaPremultiplied = cm.isAlphaPremultiplied();
                properties = new Hashtable();
                keys = img.getPropertyNames();

                if (keys != null) {
                        for (int i = 0; i < keys.length; i++) {
                                properties.put(keys[i], img.getProperty(keys[i]));
                        }
                }

                BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, properties);
                img.copyData(raster);

                return result;
            } else {
                return new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB); 
            }
        } catch (Exception e) {
            return new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB); 
        }
    }      
    
    public LatLonAltBox getBoundingBox() {
        return boundingBox;
    }
    
    public int getMaxZoom() {
        return maxZoom;
    }
    
    public int getMinZoom() {
        return minZoom;
    }
    
    /**
     * Returns the description for this TileSource.
     * 
     * @return 
     */
    public String getDescription() {
        if (description == null) {
            return "";
        } else {
            return description;
        }
    }
    
    /**
     * Returns the name associated with the source, this name will probably be
     * used for a TileLayer name.
     * 
     * @return 
     */
    public String getName() {
        return this.name;
    }        
}
