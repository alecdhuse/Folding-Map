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

import co.foldingmap.xml.XmlOutput;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Alec
 */
public abstract class Overlay {
    protected String overlayID;
    
    public abstract Overlay copyOverlay();
    public abstract void    drawObject(Graphics2D g2, MapView mapView);
    public abstract boolean isObjectWithinRectangle(Rectangle2D range);
    public abstract void    toXML(XmlOutput kmlWriter);
    
    public String getID() {
        return this.overlayID;
    }    
    
    public void setID(String id) {
        this.overlayID = id;
    }
}
