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
package co.foldingmap.map;

import co.foldingmap.map.tile.TileMath;
import co.foldingmap.xml.XmlOutput;

/**
 *
 * @author Alec
 */
public class Visibility {
    private float maxTileLevel = 23;
    private float minTileLevel =  0;    
    
    /**
     * Default constructor with minimum visibility = 0 and maximum = 23;
     */
    public Visibility() {
        this.maxTileLevel  = 23;
        this.minTileLevel  = 0;        
    }
    
    /**
     * Basic constructor for Visibility.
     * 
     * @param maxTileLevel
     * @param minTileLevel 
     */
    public Visibility(float maxTileLevel, float minTileLevel) {
        this.maxTileLevel  = maxTileLevel;
        this.minTileLevel  = minTileLevel;
    }            
    
    /**
     * Returns a cloned object of this Visibility object.
     * 
     * @return 
     */
    @Override
    public Visibility clone() {
        return new Visibility(maxTileLevel, minTileLevel);
    }
    
    /**
     * Returns if this object is equal to another object.
     * 
     * @param obj
     * @return 
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Visibility) {
            Visibility v = (Visibility) obj;
            
            return (this.hashCode() == v.hashCode());
        } else {
            return false;
        }
    }

    /**
     * Generates a hash code for this object,
     * 
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Float.floatToIntBits(this.maxTileLevel);
        hash = 37 * hash + Float.floatToIntBits(this.minTileLevel);
        return hash;
    }
    
    /**
     * Returns the max tile zoom level for this Visibility object.
     * 
     * @return 
     */
    public float getMaxTileZoomLevel() {
        return this.maxTileLevel;
    }
    
    /**
     * Returns the min tile zoom level for this Visibility object.
     * 
     * @return 
     */
    public float getMinTileZoomLevel() {
        return this.minTileLevel;
    }     
    
    /**
     * Given a vector zoom level, returns if the owner of this Visibility object should be shown.
     * 
     * @param vectorZoomLevel
     * @return 
     */
    public boolean isVisible(float vectorZoomLevel) {
        boolean maxOk, minOk;
        float   tileZoom;

        tileZoom      = TileMath.getTileMapZoom(vectorZoomLevel);
        maxOk         = ((this.maxTileLevel == -1) ? true : (tileZoom <= this.maxTileLevel));
        minOk         = ((this.minTileLevel == -1) ? true : (tileZoom >= this.minTileLevel));
        
        return (maxOk && minOk);
    }    
    
    /**
     * Sets the max tile zoom level for this Visibility object.
     * 
     */
    public void setMaxTileZoomLevel(float maxTile) {
        this.maxTileLevel = maxTile;
    }
    
    /**
     * Sets the min tile zoom level for this Visibility object.
     * 
     */
    public void setMinTileZoomLevel(float minTile) {
        this.minTileLevel = minTile;
    }        
    
    /**
     * Write FmXml for Lod.
     * 
     * @param kmlWriter 
     */
    public void toXML(XmlOutput xmlWriter) {
        xmlWriter.openTag ("Visibility");
        
        xmlWriter.writeTag("maxTileZoom",  Float.toString(maxTileLevel));
        xmlWriter.writeTag("minTileZoom",  Float.toString(minTileLevel));
              
        xmlWriter.closeTag("Visibility");
    }      
}
