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

import co.foldingmap.map.vector.LatLonBox;

/**
 *
 * @author Alec
 */
public class BoundsSearchResult {
    protected String    name;
    protected LatLonBox bounds;
    
    public BoundsSearchResult(String name, LatLonBox bounds) {
        this.name   = name;
        this.bounds = bounds;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof BoundsSearchResult) {
            BoundsSearchResult bsr = (BoundsSearchResult) o;
            
            return (this.hashCode() == bsr.hashCode());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 97 * hash + (this.bounds != null ? this.bounds.hashCode() : 0);
        return hash;
    }
    
    public LatLonBox getBounds() {
        return bounds;
    }
    
    public String getName() {
        return name;        
    }    
    
    /**
     * Returns the name in this search result.
     */
    @Override
    public String toString() {
        return name;
    }
}
