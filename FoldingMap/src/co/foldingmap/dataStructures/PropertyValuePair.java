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
package co.foldingmap.dataStructures;

/**
 * Used to store a property name and it's value
 * @author alecdhuse
 */
public class PropertyValuePair {    
    protected String property, value;

    /** 
     * Constructor given the property and the value.
     * 
     * @param property
     * @param value 
     */
    public PropertyValuePair(String property, String value) {
        this.property = property;
        this.value    = value;
    }

    /**
     * Returns id this PVP is equal to another object.
     * 
     * @param o
     * @return 
     */
    @Override
    public boolean equals(Object o) {
        
        if (o instanceof PropertyValuePair) {
            PropertyValuePair pvp = (PropertyValuePair) o;
            
            return (this.hashCode() == pvp.hashCode());
        } else {
            return false;
        }
    }

    /**
     * Generates the hash code for this PVP.
     * 
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.property != null ? this.property.hashCode() : 0);
        hash = 47 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }
    
    /**
     * Returns the Property.
     * 
     * @return 
     */
    public String getProperty() {
        return property;
    }

    /**
     * Returns the Value
     * 
     * @return 
     */
    public String getValue() {
        return value;
    }

    /**
     * Updates the value of this Property
     * @param value 
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Returns this Object as a String.
     * 
     * @return 
     */
    @Override
    public String toString() {
        return property + " - " + value;
    }
}
