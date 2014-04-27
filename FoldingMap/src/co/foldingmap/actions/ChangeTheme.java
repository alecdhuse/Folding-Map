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

import co.foldingmap.GUISupport.ProgressIndicator;
import co.foldingmap.GUISupport.Updateable;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.themes.MapTheme;

/**
 * Action for changing the map's theme.
 * 
 * @author Alec
 */
public class ChangeTheme extends Action {
    protected   DigitalMap        mapData;
    protected   MapTheme          newTheme, oldTheme;
    protected   ProgressIndicator progressIndicator;
    protected   Updateable        updateable;
    
    /**
     * 
     * @param mapData               The DigitalMap to which the new MapTheme is being applied.
     * @param newTheme              The new MapTheme being applied.
     * @param updateable            Class to update when this Action is finished
     * @param progressIndicator     Class to indicate current progress in this Action.
     * 
     */
    public ChangeTheme(DigitalMap        mapData, 
                       MapTheme          newTheme, 
                       Updateable        updateable,
                       ProgressIndicator progressIndicator) {
        
        this.commandDescription = "Change Map Theme";
        this.mapData            = mapData;
        this.newTheme           = newTheme;
        this.oldTheme           = mapData.getTheme();
        this.progressIndicator  = progressIndicator;
        this.updateable         = updateable;
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
        mapData.setTheme(newTheme, updateable, progressIndicator);
    }

    @Override
    public void undo() {
        mapData.setTheme(oldTheme, updateable, progressIndicator);
    }
    
}
