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

import co.foldingmap.map.themes.ColorStyle;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.CoordinateList;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Alec
 */
public interface MapObject {
    
    public abstract MapObject   copy();
    public abstract void        drawObject(Graphics2D g2, MapView mapView, ColorStyle colorStyle);
    public abstract void        drawPoints(Graphics2D g2, MapView mapView);
    public abstract CoordinateList<Coordinate> getCoordinateList();
    public abstract String      getDescription();
    public abstract String      getName();
    public abstract Coordinate  getSelectedCoordinate();
    public abstract Visibility  getVisibility();
    public abstract boolean     isHighlighted();
    public abstract boolean     isObjectWithinRectangle(Rectangle2D range);
    public abstract void        setDescription(String description);
    public abstract void        setHighlighted(boolean h);
    public abstract void        setName(String name);
    public abstract void        setParentLayer(Layer parentLayer);
    public abstract void        setSelectedCoordinate(Coordinate selectedCoordinate);
    public abstract void        setVisibility(Visibility v);
    public abstract void        unselectCoordinate();
    public abstract Layer       getParentLayer() ;
}
