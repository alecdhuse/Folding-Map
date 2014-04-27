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

import javax.swing.Popup;

/**
 *
 * @author Alec
 */
public class SearchResultsLayer extends VectorLayer {
    protected boolean objectPopupIsShown;
    protected int     currentResult;
    protected Popup   objectPopup;
       
    /**
     * Constructor with objects to include in this layer.
     * 
     * @param objects 
     */
    public SearchResultsLayer(VectorObjectList<VectorObject> objects) {
        super("Search Results");

        this.objects       = objects;
        this.currentResult = 0;

        if (objects.size() > 0) {
            objects.get(currentResult).setHighlighted(true);
            objectPopupIsShown = true;
        }
    }    
}
