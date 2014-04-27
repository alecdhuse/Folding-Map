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
import co.foldingmap.xml.XmlOutput;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 *
 * @author Alec
 */
public class DirectoriesTileSource extends TileSource {
    private BufferedImage   blankTile;
    private String          baseDirectory;
    
    public DirectoriesTileSource(String baseDirectory) {
        this.baseDirectory = baseDirectory;
        this.blankTile     = ResourceHelper.getInstance().getBufferedImage("transparent_tile.png");
        this.name          = "Tile Layer";
    }
    
    /**
     * Closes the connection to the Tile Source.
     */
    @Override
    public void closeSource() {

    }    
    
    /**
     * Returns the base directory on the tiles used in this tile source.
     * 
     * @return 
     */
    @Override
    public String getSource() {
        return this.baseDirectory;
    }    
    
    @Override
    public BufferedImage getTileImage(TileReference tr) {
        BufferedImage bi;
        int           numberOfTiles, x, y, zoom;
        String        filePath, dirs;
        
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
                    
            if (tr.getY() < numberOfTiles) {
                dirs     = tr.getZoom() + File.separator + x + File.separator + tr.getY() + ".png";
                filePath = baseDirectory + dirs;
                bi       = ImageIO.read(new File(filePath));

                return bi;
            } else {
                return blankTile;
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in DirectoriesTileSource.getTileImage(TileReference) - " + e);
        }
        
        return null;
    }
    
    /**
     * Writes this TileSource to FmXML.
     * 
     * @param xmlWriter 
     */
    @Override
    public void toXML(XmlOutput xmlWriter) {
        xmlWriter.openTag("TileSource");
        xmlWriter.writeTag("href", baseDirectory);
        xmlWriter.closeTag("TileSource");
    }


}
