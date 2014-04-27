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

import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.LatLonAltBox;

/**
 * This is an exception when testing if an object is within a given bounds.
 * This exception is thrown when the object is not within the bounds.
 * 
 * @author Alec
 */
public class ObjectNotWithinBoundsException extends Exception {
    LatLonAltBox    bounds;
    VectorObject       object;

    public ObjectNotWithinBoundsException(VectorObject object, LatLonAltBox bounds) {
        this.object = object;
        this.bounds = bounds;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(object.getName());
        sb.append(" bounds: ");
        sb.append(object.getBoundingBox().toString());
        sb.append(" is not within: ");
        sb.append(bounds.toString());

        return sb.toString();
    }
}
