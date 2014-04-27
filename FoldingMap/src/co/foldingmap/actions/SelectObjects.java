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
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;

/**
 * This class is used for selection filters
 * 
 * @author Alec
 */
public class SelectObjects extends Action {
    public static final int    MAP_POINT     = 1;
    public static final int    LINE_STRING   = 2;
    public static final int    LINEAR_RING   = 3;
    public static final int    POLYGON       = 4;
    public static final int    MULTIGEOMETRY = 5;
    public static final String ALL_CLASSES   = "all";
    
    private boolean     fromSelected;
    private DigitalMap  mapData;
    private int[]       objectType;
    private String      classType;
    
    public SelectObjects(DigitalMap mapData, int objectType, String classType, boolean fromSelected) {
        this.mapData            = mapData;        
        this.classType          = classType;
        this.objectType         = new int[1];        
        this.objectType[0]      = objectType;
        this.fromSelected       = fromSelected;
        this.commandDescription = "Selection Filter";
    }
    
    public SelectObjects(DigitalMap mapData, int[] objectType, String classType, boolean fromSelected) {
        this.mapData            = mapData;
        this.objectType         = objectType;
        this.classType          = classType;
        this.fromSelected       = fromSelected;
        this.commandDescription = "Selection Filter";
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
        VectorObjectList<VectorObject>  filterObjects, objects, returnObjects;
                
        if (fromSelected) {
            objects = new VectorObjectList<VectorObject>(mapData.getSelectedObjects());
        } else {
            objects = new VectorObjectList<VectorObject>(mapData.getAllMapObjects());
        }
        
        mapData.deselectObjects();
        filterObjects = new VectorObjectList<VectorObject>();
                
        for (int i = 0; i < objectType.length; i++) {
            switch (objectType[i]) {
                case MAP_POINT:                               
                    filterObjects.addAll(objects.getMapPoints());
                    break;        
                case LINE_STRING:
                    filterObjects.addAll(objects.getLineStrings());
                    break;        
                case LINEAR_RING:
                    
                    break;  
                case POLYGON:
                    filterObjects.addAll(objects.getPolygons());
                    break;  
                case MULTIGEOMETRY:
                    filterObjects.addAll(objects.getMultiGeometries());
                    break;                      
            }
        }
        
        if (classType.equalsIgnoreCase(ALL_CLASSES)) {
            for (VectorObject vo: filterObjects)
                mapData.setSelected(vo);
        } else {
            returnObjects = new VectorObjectList<VectorObject>();
            
            for (VectorObject object: filterObjects) {
                if (object.getObjectClass().equalsIgnoreCase(classType))
                    returnObjects.add(object);
            }
            
            for (VectorObject vo: returnObjects)
                mapData.setSelected(vo);            

        }                  
    }

    public void setMap(DigitalMap mapData) {
        this.mapData = mapData;
    }
    
    @Override
    public void undo() {
        mapData.deselectObjects();
    }
    
}
