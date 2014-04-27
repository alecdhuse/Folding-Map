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

import co.foldingmap.map.DigitalMap;

/**
 * Changes the Map's name.
 * 
 * @author Alec
 */
public class ChangeMapName extends ChangeNameAction {
    protected DigitalMap mapData;
    protected String     oldName;
    
    public ChangeMapName(DigitalMap mapData, String newName) {
        this.commandDescription = "Change Map Name";
        this.executed           = false;
        this.mapData            = mapData;
        this.newName            = new StringBuilder(newName);
        this.oldName            = mapData.getName();
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
        this.mapData.setName(newName.toString());
        this.executed = true;
    }

    @Override
    public void undo() {
        this.mapData.setName(oldName);
    }
    
}
