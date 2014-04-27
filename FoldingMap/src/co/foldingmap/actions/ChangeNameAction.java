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

/**
 * An ancestor class for changing Map/Object name actions.
 * 
 * @author Alec
 */
public abstract class ChangeNameAction extends Action {
    protected boolean       executed;
    protected StringBuilder newName;
    
    public ChangeNameAction() {
        newName = new StringBuilder();
    }
    
    /**
     * Appends text to the name change before it is executed.
     * 
     * @param text 
     */
    public void appendNameText(String text) {
        if (!executed)
            newName.append(text);
    }        
    
    /**
     * Returns the text for the name change.
     * 
     * @return 
     */
    public String getNameText() {
        return newName.toString();
    }
    
    public boolean hasExecuted() {
        return this.executed;
    }
    
    /**
     * Changes the name change text before it is executed.
     * 
     * @param text 
     */
    public void setNameText(String text) {
        if (!executed)
            newName = new StringBuilder(text);
    }
}
