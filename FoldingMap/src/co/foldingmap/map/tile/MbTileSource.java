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
import co.foldingmap.ResourceHelper;
import co.foldingmap.map.vector.LatLonAltBox;
import co.foldingmap.mapImportExport.TileExporter;
import co.foldingmap.xml.XmlOutput;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.*;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;

/**
 *
 * @author Alec
 */
public class MbTileSource extends TileSource {
    private boolean         tilesTable;  
    private Connection      conn;      
    private String          mapBounds, mapVersion, filePathName;
    
    /**
     * Creates a connection to a SQLite database in the MBTiles format.  
     * If no file exists a new one is created.
     * 
     * @param filePathName 
     */
    public MbTileSource(String filePathName) {
        this.filePathName = filePathName;
        
        try {
            File f = new File(filePathName);
            
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + filePathName);      

            if (f.exists()) {
                createTables();
            }
            
            loadMetaData();
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in MbTileSource.constructor(String) - " + e);
        }
    }
    
    /**
     * Closes the connection to the Tile Source.
     */
    @Override
    public void closeSource() {
        try {
            conn.close(); 
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in MbTilsSource.closeSource() - " + e);
        }
    }
    
    /**
     * Creates the tables needed for the MBTiles database format.
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
            System.err.println("Error in MbTileSource.createTables() - " + e);
        }
    }      
    
    /**
     * Returns the bounds for a give MbTiles bounds string formated:
     *  West, South, East, North;
     * 
     * @param bounds
     * @return 
     */
    public static LatLonAltBox getBounds(String bounds) {
        LatLonAltBox    boundingBox;
        String          north, south, east, west;
        StringTokenizer st;
                
        st = new StringTokenizer(bounds, ",");
        
        west  = st.nextToken();
        south = st.nextToken();
        east  = st.nextToken();
        north = st.nextToken();
        
        boundingBox = new LatLonAltBox(north, south, east, west);
        
        return boundingBox;
    }    
    
    /**
     * Returns a BufferedImage from a SQLite database matching the given 
     * TileReference.  If no tile is present in the database then null is 
     * returned.
     * 
     * @param tr
     * @return 
     */
    @Override
    public BufferedImage getTileImage(TileReference tr) {
        byte[]                  tileImage;
        BufferedImage           bi;     
        int                     numberOfTiles, x, y;
        ResultSet               rs;
        Statement               stat;
        String                  sql, tileID;
        
        bi = null;
        
        try {           
            numberOfTiles = (int) Math.pow(2, tr.getZoom());
            
            //allow for repeating of tiles
            if (tr.getX() >= numberOfTiles) {
                x = (tr.getX() - numberOfTiles);
            } else if (tr.getX() < 0) {
                x = numberOfTiles + tr.getX();
            } else {
                x = tr.getX();
            }            
            
            //MBTiles y is reversed
            y = (numberOfTiles - tr.getY()) - 1;
            
            if (tilesTable) {
                //uses tile table instead of map
                sql  = "SELECT tile_data FROM tiles WHERE zoom_level =" + tr.getZoom() + " AND tile_column =" + x + " AND tile_row =" + y + ";";  
            } else {
                sql  = "SELECT tile_id FROM map WHERE zoom_level =" + tr.getZoom() + " AND tile_column =" + x + " AND tile_row =" + y + ";";  
            }
            
            stat = conn.createStatement();                             
            rs   = stat.executeQuery(sql);
            
            if (rs.next()) {
                if (tilesTable) {
                    tileImage = rs.getBytes("tile_data");
                    bi = ImageIO.read(new ByteArrayInputStream(tileImage));                    
                } else {
                    tileID = rs.getString("tile_id");
                    sql    = "SELECT tile_data FROM images WHERE tile_id = '" + tileID + "';";
                    rs     = stat.executeQuery(sql);

                    if (rs.next()) {
                        tileImage = rs.getBytes("tile_data");
                        bi = ImageIO.read(new ByteArrayInputStream(tileImage));                                        
                    }       
                }
            } else {
                //Tile not found, return null.
                bi = null;             
            }                                   
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in MbTileSource.getTileImage(TileReference) - " + e);
        }
        
        return bi;
    }
 
    /**
     * Returns the path and file name of the MbTiles file used as the source for this Tile Source.
     * 
     * @return 
     */
    @Override
    public String getSource() {
        return this.filePathName;
    }    
    
    /**
     * Loads the tile map's meta data from the MbTile file.
     */
    private void loadMetaData() {
        boolean   hasMax, hasMin;
        ResultSet rs;
        Statement stat;
        String    property, value;
        
        try {   
            hasMax = false;
            hasMin = false;
            stat   = conn.createStatement(); 
            rs     = stat.executeQuery("SELECT * FROM metadata;");
            
            while (rs.next()) {
                property = rs.getString("name");
                value    = rs.getString("value");
                
                if (property.equalsIgnoreCase("bounds")) {
                    this.mapBounds   = value;
                    this.boundingBox = getBounds(mapBounds);
                }
                
                if (property.equalsIgnoreCase("minzoom")) {
                    this.minZoom = Integer.parseInt(value);
                    hasMin = true;
                }
                
                if (property.equalsIgnoreCase("maxzoom")) {
                    this.maxZoom = Integer.parseInt(value);
                    hasMax = true;
                }                
                
                if (property.equalsIgnoreCase("description")) this.description   = value;
                if (property.equalsIgnoreCase("name"))        this.name          = value;
                if (property.equalsIgnoreCase("version"))     this.mapVersion    = value;

            }
            
            //check to see if this mbTile db has a tiles table
            stat = conn.createStatement();                   
            rs   = stat.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='tiles';");
            if (rs.next()) {
                tilesTable = true;
            } else {
                tilesTable = false;
            }
            
            if (tilesTable) {
                if (hasMax == false) {
                    rs = stat.executeQuery("SELECT Max(zoom_level) FROM tiles;");

                    if (rs.next()) {
                        this.maxZoom = rs.getInt(1);
                    }
                }

                if (hasMin == false) {
                    rs = stat.executeQuery("SELECT Min(zoom_level) FROM tiles;");

                    if (rs.next()) {
                        this.minZoom = rs.getInt(1);
                    }
                }                
            } else {            
                if (hasMax == false) {
                    rs = stat.executeQuery("SELECT Max(zoom_level) FROM map;");

                    if (rs.next()) {
                        this.maxZoom = rs.getInt(1);
                    }
                }

                if (hasMin == false) {
                    rs = stat.executeQuery("SELECT Min(zoom_level) FROM map;");

                    if (rs.next()) {
                        this.minZoom = rs.getInt(1);
                    }
                }   
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in MbTileSource.loadMetaData() - " + e);
        }

    }
        
    /**
     * Puts a tile into the tile MbTiles database.
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
            Logger.log(Logger.ERR, "Error in MbTileSource.putTileImage(TileReference, BufferedImage) - " + e);
        }
    }  
    
    /**
     * Writes this TileSource to FmXML.
     * 
     * @param xmlWriter 
     */
    @Override
    public void toXML(XmlOutput xmlWriter) {
        xmlWriter.openTag("TileSource");
        xmlWriter.writeTag("href", filePathName);
        xmlWriter.closeTag("TileSource");        
    }    
}
