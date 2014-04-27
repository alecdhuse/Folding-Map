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
package co.foldingmap.GUISupport;

import co.foldingmap.map.Layer;
import javax.swing.JMenuItem;

/**
 * A class that handles menu items for the Layers Tree context menu.
 * 
 * @author Alec
 */
public class LayerMenuItem extends JMenuItem {
    Layer   itemLayer;
    String  actionString;

    /**
     * Constructor for objects of class JLayerMenuItem
     */
    public LayerMenuItem(Layer itemLayer, String actionString) {
        super(itemLayer.toString());
        this.itemLayer    = itemLayer;
        this.actionString = actionString;
    }

    /**
     * Gets the Action string for this menu item.
     * 
     * @return 
     */
    public String getActionString() {
        return actionString;
    }

    /**
     * Gets the layer for this menu item
     * @return 
     */
    public Layer getLayer() {
        return itemLayer;
    }

    /**
     * Sets the layer for this menu item.
     * 
     * @param newLayer 
     */
    public void setLayer(Layer newLayer) {
        this.itemLayer = newLayer;
    }
    
}
