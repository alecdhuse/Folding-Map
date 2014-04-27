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
package co.foldingmap.GUISupport.components.checkBoxTree;

import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;
import java.util.Enumeration;
import javax.swing.JLabel;

/**
 * Enumeration for MapObjects used by CheckedTreeNode.
 * 
 * @author Alec
 */
public class MapObjectEnumeration implements Enumeration {
    private int                            currentElement;
    private VectorObjectList<VectorObject> mapObjects;

    public MapObjectEnumeration() {
        this.currentElement = 0;
        this.mapObjects     = null;
    }

    public MapObjectEnumeration(VectorObjectList<VectorObject> mapObjects) {
        this.currentElement = 0;
        this.mapObjects     = mapObjects;
    }

    @Override
    public boolean hasMoreElements() {
        if (mapObjects == null) {
            return false;
        } else {
            if (currentElement < mapObjects.size()) {
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public JLabel nextElement() {
        VectorObject mapObject;

        if (mapObjects != null) {
            mapObject = mapObjects.get(currentElement);
            currentElement++;

            return new JLabel(mapObject.getName());
        } else {
            return null;
        }
    }
}
