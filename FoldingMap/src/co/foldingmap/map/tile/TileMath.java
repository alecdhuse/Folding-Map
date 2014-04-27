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

import co.foldingmap.map.MercatorProjection;
import co.foldingmap.map.vector.Coordinate;

/**
 * Has some operations to assist in drawing and using tiles.
 * 
 * @author Alec Dhuse
 */
public class TileMath {
    public static final int     TILESIZE      = 256;      
    
    /**
     * Returns the Tile Zoom level equal to the vector zoom level given.
     * 
     * @param vectorZoomLevel
     * @return 
     */
    public static float getTileMapZoom(float vectorZoomLevel) {
        float               tileWidth, tileZoom, widthMax;
        MercatorProjection  projection;
        
        projection = new MercatorProjection();
        projection.setZoomLevel(vectorZoomLevel);   
        
        widthMax  = (float) projection.getX(new Coordinate(0, 0, 180)); 
        tileWidth = widthMax / TILESIZE;                
        tileZoom  = log2(tileWidth);
                
        return tileZoom;
    }
    
    /**
     * Gets the Vector Map zoom level equal to a given Tile Map Zoom Level.
     * 
     * @param tileZoomLevel
     * @return 
     */
    public static float getVectorMapZoom(float tileZoomLevel) {
        float               newZoom, widthMax;
        MercatorProjection  projection;
        
        projection = new MercatorProjection();
        projection.setZoomLevel(1);
        projection.setReference(new Coordinate(0, 85.0511f, -180));
        
        widthMax = (float) projection.getX(new Coordinate(0, 0, 180));                        
        newZoom  = (float) ((TILESIZE / widthMax) * (Math.pow(2, tileZoomLevel)));
        
        return newZoom;
    }    
    
    /**
     * Returns the log base 2 of a number.
     * 
     * @param n
     * @return 
     */
    public static float log2(float n) {
        return (float) (Math.log(n) / Math.log(2));
    }
}
