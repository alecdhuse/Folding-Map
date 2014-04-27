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

import co.foldingmap.GUISupport.ProgressBarPanel;
import co.foldingmap.Logger;
import co.foldingmap.MainWindow;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.MapView;
import co.foldingmap.map.tile.TileMath;
import co.foldingmap.map.tile.TileReference;
import co.foldingmap.map.vector.LatLonAltBox;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import javax.imageio.ImageIO;

/**
 * Class to export map as map tiles.  Supports nested directories and MbTiles format.
 * 
 * @author Alec Dhuse
 */
public class TileExporter extends Thread {
    public static final int     TILESIZE      = 256;       
    public static final int     NESTEDFOLDERS = 1;
    public static final int     MBTILES       = 2;
           
    private DigitalMap          mapData;
    private File                export;
    private int                 maxZoom, minZoom, exportMethod;
    private LatLonAltBox        bounds;
    private MainWindow          mainWindow;
    
    public TileExporter(DigitalMap mapData, 
                        LatLonAltBox bounds, 
                        int minZoom, 
                        int maxZoom, 
                        int exportMethod, 
                        File export,
                        MainWindow  mainWindow) {
        
        this.mapData      = mapData;
        this.bounds       = bounds;
        this.minZoom      = minZoom;
        this.maxZoom      = maxZoom;
        this.exportMethod = exportMethod;
        this.export       = export;
        this.mainWindow   = mainWindow;
    }
    
    /**
     * Calculates the number of tiles that would be generated from the bounds 
     * zoom level specified.
     * 
     * @param bounds
     * @param zoomMin
     * @param zoomMax
     * @return 
     */
    public static long calculateNumberOfMapTiles(LatLonAltBox bounds, int zoomMin, int zoomMax) {
        long            numberOfTiles, boundsWidth, boundsHeight;
        TileReference   northWest, southEast;
        
        numberOfTiles = 0;
        
        for (int z = zoomMin; z <= zoomMax; z++) {
            northWest = TileReference.getTileReference(bounds.getNorth(), bounds.getWest(), z);
            southEast = TileReference.getTileReference(bounds.getSouth(), bounds.getEast(), z);
            
            boundsWidth  = (southEast.getX() - northWest.getX());
            boundsHeight = (southEast.getY() - northWest.getY());
                    
            numberOfTiles += (boundsWidth * boundsHeight);
        }
        
        return numberOfTiles;
    }
     
    /**
     * Static method for creating a BufferedImage of a map with a given MapView.
     * 
     * @param mapData
     * @param mapView
     * @param renderAntialiasing
     * @return 
     */
    public static BufferedImage createTileImage(DigitalMap mapData, MapView mapView, RenderingHints renderAntialiasing, int height, int width) {
        BufferedImage   exportBufferedImage;
        Graphics2D      exportGraphics2D;
        
        //setup image buffer
        exportBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        exportGraphics2D    = exportBufferedImage.createGraphics();
        exportGraphics2D.setRenderingHints(renderAntialiasing); 
        
        //draw background
        exportGraphics2D.setColor(mapData.getTheme().getBackgroundColor());
        exportGraphics2D.fill(new Rectangle2D.Float(-1, -1, width + 1, height + 1));          
        
        //draw the map
        mapData.drawMap(exportGraphics2D, mapView);        
        
        return exportBufferedImage;                
    }        
    
    /**
     * Exports map as tiles to nested directories 
     * 
     * @param mapData
     * @param bounds
     * @param minZoom
     * @param maxZoom
     * @param dir
     * @param mainWindow 
     */
    public static void exportTilesToDIR(DigitalMap mapData, LatLonAltBox bounds, int minZoom, int maxZoom, File dir, MainWindow mainWindow) {
        BufferedImage       exportBufferedImage;
        File                currentTileFile;
        double              tileLatitude, tileLongitude;
        float               numberOfTiles, tilesCreated, vectorMapZoom;
        int                 percent;
        MapView             mapView;
        ProgressBarPanel    progressBar;
        RenderingHints      renderAntialiasing;
        String              progressText;
        TileReference       maxRef, minRef;
                
        try {            
            //Prime the MapView
            mapView = new MapView();     
            mapView.setDisplayAll(true);                       
            mapView.getMapProjection().setDisplaySize(TILESIZE, TILESIZE);
            
            //setup rendering
            renderAntialiasing  = new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            renderAntialiasing.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);        
            
            progressBar = mainWindow.getProgressBarPanel();
            numberOfTiles = calculateNumberOfMapTiles(bounds, minZoom, maxZoom);
            tilesCreated  = 0;
            
            for (int z = minZoom; z <= maxZoom; z++) {
                //This will take a lot of memory clean up what we can first
                System.gc();
        
                //setup the zoom
                vectorMapZoom = TileMath.getVectorMapZoom(z);
                mapView.getMapProjection().setZoomLevel(vectorMapZoom);
                
                //find the tile bounds
                minRef = TileReference.getTileReference(bounds.getNorth(), bounds.getWest(), z);
                maxRef = TileReference.getTileReference(bounds.getSouth(), bounds.getEast(), z);                
                maxRef.incrementY();
                
                //Prime the MapView
                mapView = new MapView();     
                mapView.setDisplayAll(true);                       
                mapView.getMapProjection().setDisplaySize(TILESIZE, TILESIZE);          

                vectorMapZoom = TileMath.getVectorMapZoom(z);        
                mapView.getMapProjection().setZoomLevel(vectorMapZoom);                   
                        
                //create tiles
                for (int x = minRef.getX(); x < maxRef.getX(); x++) {
                    for (int y = minRef.getY(); y < maxRef.getY(); y++) {                                                
                        if (progressBar != null) {
                            progressText = "Exporting - Z: " + z + " X: " + x + " Y: " + y;
                            percent      = (int) ((tilesCreated / numberOfTiles) * 100);
                            progressBar.updateProgress(progressText, percent);
                        }
                        
                        tileLatitude  = getTileLatitude(x, y, z);
                        tileLongitude = getTileLongitude(x, y, z);                          
                        mapView.getMapProjection().setReference(tileLatitude, tileLongitude);
                
                        exportBufferedImage = createTileImage(mapData, mapView, renderAntialiasing, TILESIZE, TILESIZE);
                        
                        currentTileFile = new File(dir,  z + "/" + x + "/" + y + ".png");
                        currentTileFile.mkdirs();
                        
                        try {
                            ImageIO.write(exportBufferedImage, "PNG",  currentTileFile);    
                        } catch (Exception e) {
                            Logger.log(Logger.ERR, "Counld not create tile z: " + z + " x: " + x  + " y: " + y + " Error: " + e);
                        }
                        
                        tilesCreated++;
                    }
                }
                
            }
            
            progressBar.finish();            
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in TileExporter.exportTilesToDIR(DigitalMap, LatLonAltBox, int, int, File, MainWindow) - " + e);
        }           
    }
    
    /**
     * Exports as map tiles to MbTiles format.
     * 
     * @param mapData
     * @param bounds
     * @param minZoom
     * @param maxZoom
     * @param dbFile
     * @param mainWindow 
     * @see <a href="https://github.com/mapbox/mbtiles-spec">MbTiles Spec</a>
     */
    public static void exportTilesToMbTiles(DigitalMap mapData, LatLonAltBox bounds, int minZoom, int maxZoom, File dbFile, MainWindow mainWindow) {
        BufferedImage           exportBufferedImage;
        ByteArrayOutputStream   baos;
        byte[]                  tileImageBytes;
        Connection              conn;
        double                  tileLatitude, tileLongitude;
        float                   numberOfTiles, tilesCreated, vectorMapZoom;
        int                     percent;
        MapView                 mapView;        
        PreparedStatement       prep; 
        ProgressBarPanel        progressBar;
        RenderingHints          renderAntialiasing;
        Statement               stat;
        String                  boundsString, filePathName, imageHash;
        String                  progressText;
        TileReference           maxRef, minRef; 
                                               
        try {            
            //Prime the MapView
            mapView = new MapView();     
            mapView.setDisplayAll(true);                       
            mapView.getMapProjection().setDisplaySize(TILESIZE, TILESIZE);
            
            //setup rendering
            renderAntialiasing  = new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            renderAntialiasing.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);                
            
            progressBar = mainWindow.getProgressBarPanel();
            numberOfTiles = calculateNumberOfMapTiles(bounds, minZoom, maxZoom);
            tilesCreated  = 0;            
            
            filePathName = dbFile.getCanonicalPath();
            boundsString = bounds.getWest() + "," + bounds.getSouth() + "," + bounds.getEast() + "," + bounds.getNorth();
                    
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + filePathName);
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
            
            //update metadata
            updateMetadata(mapData, conn, boundsString, minZoom, maxZoom);
            
            //create the tiles and add them into the DB
            for (int z = minZoom; z <= maxZoom; z++) {
                //This will take a lot of memory clean up what we can first
                System.gc();
        
                //setup the zoom
                vectorMapZoom = TileMath.getVectorMapZoom(z);                
                mapView.getMapProjection().setZoomLevel(vectorMapZoom);
                
                //find the tile bounds
                minRef = TileReference.getTileReference(bounds.getNorth(), bounds.getWest(), z);
                maxRef = TileReference.getTileReference(bounds.getSouth(), bounds.getEast(), z);
                                
                //Make sure we get at least one tile from each requested zoom level.
                maxRef.incrementY();
                if (minRef.getX() == maxRef.getX()) maxRef.incrementX();
                
                //create tiles
                for (int x = minRef.getX(); x < maxRef.getX(); x++) {
                    for (int y = minRef.getY(); y < maxRef.getY(); y++) {                                                
                        //            
                        if (progressBar != null) {
                            progressText = "Exporting - Zoom: " + z + " X: " + x + " Y: " + y;
                            percent      = (int) ((tilesCreated / numberOfTiles) * 100);
                            progressBar.updateProgress(progressText, percent);
                        }                      
                        
                        tileLatitude  = getTileLatitude(x, y, z);
                        tileLongitude = getTileLongitude(x, y, z);                          
                        mapView.getMapProjection().setReference(tileLatitude, tileLongitude);
                                        
                        //get the tile image and reate an input stream to add it to the db;
                        exportBufferedImage = createTileImage(mapData, mapView, renderAntialiasing, TILESIZE, TILESIZE);
                        baos = new ByteArrayOutputStream();
                        ImageIO.write(exportBufferedImage, "png", baos);
                        tileImageBytes = baos.toByteArray();                        
                        
                        //hash the image
                        imageHash = hashBytes(tileImageBytes);
                        
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
                        prep.setInt(1, z);
                        prep.setInt(2, x);
                        prep.setInt(3, (maxRef.getY() - y) - 1);  //MBTiles y is reversed
                        prep.setString(4, imageHash);
                        prep.setString(5, " ");
                        prep.addBatch();                                 
                        
                        conn.setAutoCommit(false);
                        prep.executeBatch();
                        conn.setAutoCommit(true);   
                        
                        tilesCreated++;
                    }
                                       
                }
                
            }
                  
            progressBar.updateProgress("Export Complete", 100);
            progressBar.finish(); 
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in TileExporter.exportTilesToMbTiles(DigitalMap, LatLonAltBox, int, int, File, MainWindow) - " + e);
        }        
    }
    
    /**
     * Finds the zoom level that would result in the whole map being displayed
     * in one tile.
     * 
     * @param bounds
     * @return 
     */
    public static int findMinZoom(LatLonAltBox bounds) {
        int  minZoom;
        long tileNumbers;
        
        minZoom = 1;
        
        for (int i = 23; i > 0; i--) {
            tileNumbers = calculateNumberOfMapTiles(bounds, i, i);
            
            if (tileNumbers == 1) {
                minZoom = i;
                break;
            }
        }
        
        return minZoom;
    }
        
    /**
     * Returns the top Latitude of the specified tile;
     * 
     * @param x
     * @param y
     * @param z
     * @return 
     */
    public static double getTileLatitude(int x, int y, int z) {
        double latDeg, latRad, n;
        
        n = Math.pow(2, z);
        latRad = Math.atan(Math.sinh(Math.PI * (1 - 2 * y / n)));
        latDeg = latRad * 180.0 / Math.PI;   
        
        return latDeg;
    }
    
    /**
     * Returns the Left Longitude of the specified tile.
     * 
     * @param x
     * @param y
     * @param z
     * @return 
     */
    public static double getTileLongitude(int x, int y, int z) {
        double lonDeg, n;
        
        n = Math.pow(2, z);
        lonDeg = x / n * 360.0 - 180.0;
        
        return lonDeg;
    }    
        
    /**
     * Creates a MD5 hash of an array of bytes.
     * 
     * @param bytes
     * @return 
     */
    public static String hashBytes(byte[] bytes) {
        byte[]          digestedBytes;
        MessageDigest   messageDigest;
        StringBuffer    hexString;        
        
        hexString = new StringBuffer();
        
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(bytes);
            digestedBytes = messageDigest.digest();
            
            for (int i=0; i < digestedBytes.length; i++) 
                    hexString.append(Integer.toHexString(0xFF & digestedBytes[i]));
                        
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in TileExporter.hashBytes(Byte[] bytes) - e");
        }
        
        return hexString.toString();
        
    }
    
    /**
     * Splits a large map rendering in to a series of smaller tiles.
     * 
     * @param bi
     * @param tileSize
     * @return 
     */
    public static BufferedImage[][] splitImage(BufferedImage bi, int tileSize) {
        int rows = bi.getHeight() / tileSize;
        int cols = bi.getWidth()  / tileSize;
        
        BufferedImage[][] images = new BufferedImage[cols][rows];
        
        for (int x = 0; x < cols; x++) {
            int xOffset = x * tileSize;
            
            for (int y = 0; y < rows; y++) {
                int yOffset = y * tileSize;
                
                images[x][y] = new BufferedImage(tileSize, tileSize, bi.getType());  
                Graphics2D gr = images[x][y].createGraphics();  
                
                gr.drawImage(bi, 0, 0, tileSize, tileSize, yOffset, xOffset, yOffset + tileSize, xOffset + tileSize, null);  
                gr.dispose();                  
            }
        }
        
        return images;
    }
    
    /**
     * Starts the thread to export the map as tiles.
     * 
     */
    @Override
    public void run() {
        if (exportMethod == TileExporter.NESTEDFOLDERS) {            
            exportTilesToDIR(mapData, bounds, minZoom, maxZoom, export, mainWindow);
        } else if (exportMethod == TileExporter.MBTILES) {
            exportTilesToMbTiles(mapData, bounds, minZoom, maxZoom, export, mainWindow);
        }               
    }
    
    /**
     * Updates the Metadata table in the mbtiles SQL light database.
     * 
     * @param mapData
     * @param conn
     * @param boundsString
     * @param minZoom
     * @param maxZoom 
     */
    private static void updateMetadata(DigitalMap mapData, Connection conn, String boundsString, int minZoom, int maxZoom) {
        try {
            PreparedStatement prep = conn.prepareStatement("insert into metadata values (?, ?);");
            prep.setString(1, "name");
            prep.setString(2, mapData.getName());
            prep.addBatch();   
            
            prep.setString(1, "type");
            prep.setString(2, "baselayer");
            prep.addBatch();                           
            
            prep.setString(1, "version");
            prep.setString(2, mapData.getVersionNumber());
            prep.addBatch();             
            
            prep.setString(1, "minzoom");
            prep.setString(2, Integer.toString(minZoom));
            prep.addBatch();                
            
            prep.setString(1, "maxzoom");
            prep.setString(2, Integer.toString(maxZoom));
            prep.addBatch();            
            
            prep.setString(1, "center");
            prep.setString(2, "0,0,2");
            prep.addBatch();            
            
            prep.setString(1, "description");
            prep.setString(2, mapData.getMapDescription());
            prep.addBatch();              
            
            prep.setString(1, "format");
            prep.setString(2, "png");
            prep.addBatch();              
            
            prep.setString(1, "bounds");
            prep.setString(2, boundsString);
            prep.addBatch();              
            
            conn.setAutoCommit(false);
            prep.executeBatch();
            conn.setAutoCommit(true);         
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in TileExporter.updateMetadata(DigitalMap, Connection, String, int, int) - " + e);
        }    
    }    
}
