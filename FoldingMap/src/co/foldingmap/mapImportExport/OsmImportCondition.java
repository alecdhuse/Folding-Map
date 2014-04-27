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

/**
 *
 * @author Alec
 */
public class OsmImportCondition {    
    private String objectType;
    private String key;
    private String value;
    
    public OsmImportCondition(String objectType, String key, String value) {
        this.objectType = objectType;
        this.key        = key;
        this.value      = value;
    }
    
    public String getKey() {
        return key;
    }
    
    public String getObjectType() {        
        return objectType;
    }
    
    public String getValue() {
        return value;
    }
            
    public void setKey(String key) {
        this.key = key;
    }
    
    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
}
