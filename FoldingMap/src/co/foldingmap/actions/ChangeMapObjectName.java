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

import co.foldingmap.map.vector.VectorObject;

/**
 * Changes a VectorObject's name/.
 * 
 * @author Alec
 */
public class ChangeMapObjectName extends ChangeNameAction {    
    protected VectorObject object;
    protected String    oldName;
    
    public ChangeMapObjectName(VectorObject object, String newName) {
        this.commandDescription = "Change Object Name";
        this.executed           = false;
        this.newName            = new StringBuilder(newName);
        this.object             = object;
        this.oldName            = object.getName();
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
        object.setName(newName.toString());
        this.executed = true;
    }

    @Override
    public void undo() {
        object.setName(oldName);
    }
    
}
