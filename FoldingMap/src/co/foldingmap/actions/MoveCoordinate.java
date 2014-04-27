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
package co.foldingmap.actions;

import co.foldingmap.map.vector.Coordinate;

/**
 * Action to move a component coordinate of a MapObject.
 * 
 * @author Alec
 */
public class MoveCoordinate extends Action {
    private Coordinate originalPosition, newPosition;
    
    /**
     * Constructor for objects of class CommandMoveCoordinate
     *
     * Coordinate newPosition should be a reference to the point moved.
     * When undone the latitude and longitude from the originalPosition will
     * be used to update the Coordinate newPosition.  The reference will continue to be
     * with newPosition.  originalPosition should be a copy of the coordinate before it is moved.
     */    
    public MoveCoordinate(Coordinate originalPosition, Coordinate newPosition) {
        this.commandDescription = "Move Object Point";
        this.originalPosition   = originalPosition;
        this.newPosition        = newPosition;
    }
    
    /**
     * Returns if this Action can be undone.
     * 
     * @return 
     */
    @Override
    public boolean canUndo() {
        return true;
    }      
    
    @Override
    public void execute() {
        //this is unused at the moment.  The movment of the point is done within the MapPanel.
    }

    @Override
    public void undo() {
        newPosition.setLatitude(originalPosition.getLatitude());
        newPosition.setLongitude(originalPosition.getLongitude());
    }
    
}
