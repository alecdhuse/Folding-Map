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
import co.foldingmap.Logger;
import co.foldingmap.map.Layer;
import co.foldingmap.map.themes.MapTheme;
import co.foldingmap.map.vector.Polygon;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.VectorObject;
import java.util.ArrayList;

/**
 *
 * @author Alec
 */
public class UpdateObjectOutlines extends Thread {
    private ArrayList<Layer>             layers;
    private MapTheme                     mapTheme;
    private ProgressIndicator            progressIndicator;
    private Updateable                   updateable;
    
    public UpdateObjectOutlines(MapTheme          mapTheme, 
                                ArrayList<Layer>  layers, 
                                Updateable        updateable,
                                ProgressIndicator progressIndicator) {
        
        this.mapTheme          = mapTheme;
        this.layers            = layers;
        this.updateable        = updateable;
        this.progressIndicator = progressIndicator;
    }
    
    @Override
    public void run() {
        int         completedObjs   = 0;
        int         numberOfObjects = 0;
        int         percentComplete = 0;
        VectorLayer vectorLayer;
        
        try {
            if (progressIndicator != null)
                progressIndicator.setMessage("Updating Outlines");
            
            //count objects
            for (Layer l: this.layers) {
                if (l instanceof VectorLayer) {
                    vectorLayer = (VectorLayer) l;                
                    numberOfObjects += vectorLayer.getObjectList().getPolygons().size();
                }
            }
            
            //update outlines for objects
            for (Layer l: this.layers) {
                if (l instanceof VectorLayer) {
                    vectorLayer = (VectorLayer) l;

                    for (VectorObject vo: vectorLayer.getObjectList().getPolygons()) {
                        ((Polygon) vo).updateOutlines(mapTheme);                
                        completedObjs++;
                        percentComplete = (int) ((completedObjs / (float) numberOfObjects) * 100);
                        progressIndicator.updateProgress("Updating Polygon Outlines", percentComplete);
                    }
                }
            }      
            
            if (updateable != null)
                updateable.update();
            
            progressIndicator.setMessage("Outlines Updated");
            progressIndicator.finish();
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in UpdateObjectOutlines.run() - " + e);
        }
    }
    
}
