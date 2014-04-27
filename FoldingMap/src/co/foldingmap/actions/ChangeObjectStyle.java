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

import co.foldingmap.Logger;
import co.foldingmap.map.themes.ColorStyle;
import co.foldingmap.map.themes.MapTheme;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;
import java.util.ArrayList;

/**
 * Changes a VectorObject(s)' Style.
 * 
 * @author Alec
 */
public class ChangeObjectStyle extends Action {
    protected ArrayList<ColorStyle>     oldStyles;
    protected ColorStyle                newStyle;  
    protected VectorObjectList<VectorObject>  objects;
    protected MapTheme                  mapTheme;
    
    public ChangeObjectStyle(VectorObjectList<VectorObject> objects, MapTheme mapTheme, ColorStyle newStyle) {
        this.commandDescription = "Change Object's Style";
        this.mapTheme           = mapTheme;
        this.newStyle           = newStyle;
        this.objects            = objects;
        this.oldStyles          = new ArrayList<ColorStyle>();
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
        ColorStyle  oldStyle;

        try {
            for (VectorObject obj: objects) {
                oldStyle = mapTheme.getStyle(obj.getObjectClass());
                oldStyles.add(oldStyle);
                obj.setClass(newStyle.getID());
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in ChangeObjectStyle.execute() - " + e);
        }
    }

    @Override
    public void undo() {
        ColorStyle  oldStyle;
        VectorObject   obj;
        
        for (int i = 0; i < objects.size(); i++) {
            obj         = objects.get(i);
            oldStyle    = oldStyles.get(i);
            obj.setClass(oldStyle.getID());
        }
    }
    
}
