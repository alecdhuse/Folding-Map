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

import co.foldingmap.xml.XmlOutput;
import java.awt.image.BufferedImage;

/**
 * Class for managing tiles from a tile server.  
 * Tiles are cached locally, and updated after the cache time has been exceeded.
 * 
 * @author Alec
 */
public class TileServerTileSource extends TileSource { 
    private long            cacheTime;
    private String          tileServerAddress;
    private TileDownloader  tileDownloader;
            
    public TileServerTileSource(String tileServerAddress, String sourceTitle) {
        this.cacheTime         = 14400000; //Set default time to 10 days
        this.tileDownloader    = new TileDownloader(tileServerAddress, sourceTitle);
        this.tileServerAddress = tileServerAddress;
        this.name              = "Tile Server";        
        this.maxZoom           = 20;
    }
    
    @Override
    public void closeSource() {
        tileDownloader.closeConnection();            
    }        
    
    /**
     * Returns the tile cache time in milliseconds.
     * 
     * @return 
     */
    public long getCacheTime() {
        return cacheTime;
    }    
    
    /**
     * Gets a tile by retrieving it from the cache or downloading it from the 
     * server if there is no cached version available.
     * 
     * @param tileRef
     * @return 
     */
    @Override
    public BufferedImage getTileImage(TileReference tileRef) {
        BufferedImage   tileBI;
        
        if (tileRef.getY() >= 0) {
            tileBI = tileDownloader.getTileImage(tileRef);
        } else {
            tileBI = null;
        }
        
        return tileBI;
    }    
    
    /**
     * Returns the URL of the tile server used for this Tile Source.
     * 
     * @return 
     */
    @Override
    public String getSource() {
        return this.tileServerAddress;
    }       
    
    /**
     * Sets the tile cache time in milliseconds.
     * 
     * @param t 
     */
    public void setCacheTime(long t) {
        this.cacheTime = t;
    }

    /**
     * Sets the server address for this tile source.
     * 
     * @param tileServerAddress With or without the http:// 
     */
    public void setTileServerAddress(String tileServerAddress) {
        this.tileServerAddress = tileServerAddress;
    }
    
    /**
     * Writes this TileSource to FmXML.
     * 
     * @param xmlWriter 
     */
    @Override
    public void toXML(XmlOutput xmlWriter) {
        xmlWriter.openTag("TileSource");
        xmlWriter.writeTag("href", tileServerAddress);
        xmlWriter.closeTag("TileSource");        
    }
}
