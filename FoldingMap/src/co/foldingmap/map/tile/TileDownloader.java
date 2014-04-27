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
import co.foldingmap.mapImportExport.TileExporter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 *
 * @author Alec
 */
public class TileDownloader extends Thread {
    private ArrayList<TileReference> tilesToDownload;
    private boolean                  urlReplace;
    private Connection               conn;  
    private String                   tileServerAddress;
    
    public TileDownloader(String tileServerAddress, String sourceTitle) {
        this.tileServerAddress = tileServerAddress;
        this.tilesToDownload   = new ArrayList<TileReference>();    
        
        if (tileServerAddress.contains("{x}")) {
            urlReplace = true;
        } else {
            urlReplace = false;
        }
        
        //load database
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + sourceTitle + ".mbtiles");       
            createTables();
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in TileDownloader(String) when loading database - " + e);
        }        
    }
    
    public void closeConnection() {
        try {
            conn.close();           
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in TileDownloader.closeConnection() - " + e);
        }                
    }
    
    /**
     * Create tables for the cache database
     */
    private void createTables() {
        PreparedStatement       prep; 
        Statement               stat;
        
        try {
            stat = conn.createStatement();                        
            prep = conn.prepareStatement("");
            
            //create the tables
            stat.executeUpdate("create table if not exists metadata (name text, value text);");
            stat.executeUpdate("create table if not exists map (zoom_level integer, tile_column integer, tile_row integer, tile_id text, grid_id text);");
            stat.executeUpdate("create table if not exists grid_key (grid_id text, key_name text);");
            stat.executeUpdate("create table if not exists keymap (key_name text, key_json text);");
            stat.executeUpdate("create table if not exists grid_uftgrid (grid_id text, grid_uftgrid blob);");
            stat.executeUpdate("create table if not exists images (tile_data blob, tile_id text);");        
            
            //create indexes
            stat.executeUpdate("CREATE UNIQUE INDEX if not exists map_index ON map (zoom_level, tile_column, tile_row);");
            stat.executeUpdate("CREATE UNIQUE INDEX if not exists grid_key_lookup ON grid_key (grid_id, key_name);");
            stat.executeUpdate("CREATE UNIQUE INDEX if not exists keymap_lookup ON keymap (key_name);");
            stat.executeUpdate("CREATE UNIQUE INDEX if not exists grid_uftgrid_lookup ON grid_uftgrid (grid_id);");
            stat.executeUpdate("CREATE UNIQUE INDEX if not exists images_id ON images (tile_id);");
            stat.executeUpdate("CREATE UNIQUE INDEX if not exists name ON metadata (name);");
            
            //create view to mimic the tiles table that appears in the spec
            stat.executeUpdate("CREATE VIEW if not exists tiles AS SELECT m.zoom_Level zoom_level, m.tile_column tile_column, m.tile_row tile_row, i.tile_data tile_data FROM map m, images i WHERE m.tile_id = i.tile_id;");            
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in TileDownloader.createTables() - " + e);
        }
    }    
    
    /**
     * Downloads a tile from the tile server.
     * @param  tileRef The TileReference of the tile to download.
     * @return The downloaded tile as a BufferedImage
     */
    public BufferedImage downloadTile(TileReference tileRef) {     
        BufferedImage   bufferedImage;
        int             x, y, z;
        String          constructedURL, lastMod;
        URL             url;
        URLConnection   urlConn;
        
        try {
            x = tileRef.getX();
            y = tileRef.getY();
            z = tileRef.getZoom();
            
            if (y < 0) y = 0;
            
            if (urlReplace) {
                constructedURL = "http://" + tileServerAddress;
                constructedURL = constructedURL.replace("{x}", Integer.toString(x));
                constructedURL = constructedURL.replace("{y}", Integer.toString(y));
                constructedURL = constructedURL.replace("{z}", Integer.toString(z));
            } else {
                constructedURL = "http://" + tileServerAddress + "/" + z + "/" + x + "/" + y + ".png";
            }
            
            url       = new URL(constructedURL);            
            urlConn   = url.openConnection();
            //lastMod  = urlConn.getHeaderField("Expires");
            
            bufferedImage = ImageIO.read(url);     
        } catch (Exception e) {            
            Logger.log(Logger.ERR, "Error TileDownloader.downloadTile(" + tileRef.toString() + ") - " + e);            
            bufferedImage   = null;
        }      
        
        return bufferedImage;
    }         
    
    /**
     * Get a tile from the cache database.
     * 
     * @param tileRef
     * @return 
     */
    public BufferedImage getTileFromDB(TileReference tileRef) {
        byte[]                  tileImage;
        BufferedImage           bi;     
        int                     numberOfTiles, x, y, zoom;
        ResultSet               rs;
        Statement               stat;
        String                  sql, tileID;
        
        bi = null;
        
        try {           
            numberOfTiles = (int) Math.pow(2, tileRef.getZoom());
            
            //allow for repeating of tiles
            if (tileRef.getX() >= numberOfTiles) {
                x = (tileRef.getX() - numberOfTiles);
            } else if (tileRef.getX() < 0) {
                x = numberOfTiles + tileRef.getX();
            } else {
                x = tileRef.getX();
            }            
            
            //MBTiles y is reversed
            y    = (numberOfTiles - tileRef.getY()) - 1;            
            
            sql  = "SELECT tile_id FROM map WHERE zoom_level =" + tileRef.getZoom() + " AND tile_column =" + x + " AND tile_row =" + y + ";";              
            stat = conn.createStatement();                             
            rs   = stat.executeQuery(sql);
            
            if (rs.next()) {
                tileID = rs.getString("tile_id");
                sql    = "SELECT tile_data FROM images WHERE tile_id = '" + tileID + "';";
                rs     = stat.executeQuery(sql);

                if (rs.next()) {
                    tileImage = rs.getBytes("tile_data");
                    bi = ImageIO.read(new ByteArrayInputStream(tileImage));                                        
                }       
            }                            
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in TileDownloader.getTileFromDB(TileReference) - " + e);
        }
        
        return bi;        
    }    
    
    /**
     * Gets a tile by retrieving it from the cache or downloading it from the 
     * server if there is no cached version available.
     * 
     * @param tileRef
     * @return 
     */
    public BufferedImage getTileImage(TileReference tileRef) {
        BufferedImage   tileBI;
        
        if (tileRef.getY() >= 0) {
            tileBI = getTileFromDB(tileRef);

            if (tileBI == null) {
                tilesToDownload.add(tileRef);
                if (!this.isAlive()) this.start();
            }
        } else {             
            tileBI = null;  
        }
        
        return tileBI;
    }      
    
    /**
     * Puts a tile into the tile cache database.
     * 
     * @param tileRef
     * @param bi 
     */
    public void putTileImage(TileReference tileRef, BufferedImage bi) {
        byte[]                  tileImageBytes;
        ByteArrayOutputStream   baos;      
        int                     numberOfTiles;
        PreparedStatement       prep; 
        Statement               stat;
        String                  imageHash;
        
        try {
            if (bi != null) {
                numberOfTiles = (int) Math.pow(2, tileRef.getZoom());
                baos = new ByteArrayOutputStream();
                ImageIO.write(bi, "png", baos);
                tileImageBytes = baos.toByteArray();

                //hash the image
                imageHash = TileExporter.hashBytes(tileImageBytes);  

                /* The actual Images are stored here, referenced by the
                * hash.  If an image is just empty ocean there will be 
                * a hash colision and thus saving space in the db.
                */
                prep = conn.prepareStatement("INSERT OR IGNORE into images values (?, ?);");
                prep.setBytes(1, tileImageBytes);
                prep.setString(2, imageHash);
                prep.addBatch();                         

                conn.setAutoCommit(false);
                prep.executeBatch();
                conn.setAutoCommit(true);                         

                prep = conn.prepareStatement("INSERT OR REPLACE into map values (?, ?, ?, ?, ?);");
                prep.setInt(1, tileRef.getZoom());
                prep.setInt(2, tileRef.getX());
                prep.setInt(3, (numberOfTiles - tileRef.getY()) - 1);  //MBTiles y is reversed
                prep.setString(4, imageHash);
                prep.setString(5, " ");
                prep.addBatch();                                 

                conn.setAutoCommit(false);
                prep.executeBatch();
                conn.setAutoCommit(true);     
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in TileDownloader.putTileImage(TileReference, BufferedImage) - " + e);
        }
    }    
    
    @Override
    public void run() {
        BufferedImage   bi;
        TileReference   tileRef;
        
        try {
            while (!this.isInterrupted()) {
                try {
                    while (tilesToDownload.size() > 0) {
                        tileRef = tilesToDownload.remove(0);
                        bi      = downloadTile(tileRef);      
                        
                        if (bi != null)
                            putTileImage(tileRef, bi);
                    }
                } catch (Exception e) {}

                sleep(250);
            }
        } catch (Exception e) {}
    }
}
