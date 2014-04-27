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
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.VectorObject;
import java.util.ArrayList;

/**
 *
 * @author Alec
 */
public class MergeCoordinates extends Action {    
    private Coordinate              original, mergeTo;
    private DigitalMap              mapData;
    private ArrayList<VectorObject> parentObjects;
    
    public MergeCoordinates(DigitalMap mapData, Coordinate original, Coordinate mergeTo) {
        this.original       = original;
        this.mapData        = mapData;
        this.mergeTo        = mergeTo;                
        this.parentObjects  = original.getParentVectorObjects();
    }
    
    /**
     * Returns if this Action can be undone.
     * 
     * @return 
     */
    @Override
    public boolean canUndo() {
        return false;
    }    
    
    @Override
    public void execute() {
        try {
            for (VectorObject vo: parentObjects) {
                int cIndex = vo.getCoordinateList().indexOf(original);

                if (cIndex > 0) {
                    vo.getCoordinateList().remove(cIndex);
                    vo.getCoordinateList().add(cIndex, mergeTo);
                    vo.updateOutlines(mapData.getTheme());
                }
            } // end for loop  
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in MergeCoordinates.execute() - " + e);
        }
    }

    @Override
    public void undo() {
        try {
            for (VectorObject vo: parentObjects) {
                    int cIndex = vo.getCoordinateList().indexOf(mergeTo);

                    if (cIndex > 0) {
                        vo.getCoordinateList().remove(cIndex);
                        vo.getCoordinateList().add(cIndex, original);
                        vo.updateOutlines(mapData.getTheme());
                    }            
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in MergeCoordinates.undo() - " + e);
        }
    }
    
}
