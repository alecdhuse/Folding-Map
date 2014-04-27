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
package co.foldingmap.map.vector;

import co.foldingmap.xml.XmlOutput;

/**
 * A convenience class for Level Of Detail.
 * 
 * @author Alec
 */
public class LevelOfDetail {
    private float maxLodPixels,  minLodPixels;
    private float maxFadeExtent, minFadeExtent;
    
    /**
     * Basic constructor for LevelOfDetail.
     * 
     * @param maxLodPixels
     * @param minLodPixels 
     */
    public LevelOfDetail(float maxLodPixels, float minLodPixels) {
        this.maxLodPixels  = maxLodPixels;
        this.minLodPixels  = minLodPixels;
        this.maxFadeExtent = 0;
        this.minFadeExtent = 0;
    }
            
    /**
     * Full constructor for LevelOfDetail.
     * 
     * @param maxLodPixels
     * @param minLodPixels 
     */
    public LevelOfDetail(float maxLodPixels, float minLodPixels, 
                         float maxFadeExtent, float minFadeExtent) {
        
        this.maxLodPixels  = maxLodPixels;
        this.minLodPixels  = minLodPixels;
        this.maxFadeExtent = maxFadeExtent;
        this.minFadeExtent = minFadeExtent;
    }    
    
    /**
     * Returns if this LevelOfDetail is equal to another object.
     * 
     * @param obj
     * @return 
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LevelOfDetail) {
            return (this.hashCode() == obj.hashCode());
        } else {
            return false;
        }
    }

    /**
     * Generates a hash code for this object.
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 7;
        
        hash = 11 * hash + Float.floatToIntBits(this.maxLodPixels);
        hash = 11 * hash + Float.floatToIntBits(this.minLodPixels);
        hash = 11 * hash + Float.floatToIntBits(this.maxFadeExtent);
        hash = 11 * hash + Float.floatToIntBits(this.minFadeExtent);
        
        return hash;
    }
    
    /**
     * Returns the maxLodPixels for this LevelOfDetail.
     * 
     * @return 
     */
    public float getMaxLodPixels() {
        return maxLodPixels;
    }
    
    /**
     * Returns the minLodPixels for this LevelOfDetail.
     * 
     * @return 
     */
    public float getMinLodPixels() {
        return minLodPixels;
    }
    
    /**
     * Returns the maxFadeExtent for this LevelOfDetail.
     * 
     * @return 
     */
    public float getMaxFadeExtent() {
        return maxFadeExtent;
    }
    
    /**
     * Returns the minFadeExtent for this LEvelOfDetail.
     * 
     * @return 
     */
    public float getMinFadeExtent() {
        return minFadeExtent;
    }
    
    /**
     * Sets the maxLodPixels for this LevelOfDetail.
     * 
     * @param maxLodPixels 
     */
    public void setMaxLodPixels(float maxLodPixels) {
        this.maxLodPixels = maxLodPixels;
    }
    
    /**
     * Sets the minLodPixels for this LevelOfDetail.
     * 
     * @param minLodPixels 
     */
    public void setMinLodPixels(float minLodPixels) {
        this.minLodPixels = minLodPixels;
    }    
    
    /**
     * Write KML for Lod.
     * 
     * @param xmlWriter
     */
    public void toXML(XmlOutput xmlWriter) {
        xmlWriter.openTag ("Lod");
        
        xmlWriter.writeTag("minLodPixels",  Float.toString(minLodPixels));
        xmlWriter.writeTag("maxLodPixels",  Float.toString(maxLodPixels));
        xmlWriter.writeTag("minFadeExtent", Float.toString(minFadeExtent));
        xmlWriter.writeTag("maxFadeExtent", Float.toString(maxFadeExtent));
        
        xmlWriter.closeTag("Lod");
    }    
}
