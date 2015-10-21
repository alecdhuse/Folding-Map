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

import co.foldingmap.map.vector.LatLonBox;
import co.foldingmap.map.vector.CoordinateList;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.ClipboardOperations;
import co.foldingmap.GUISupport.ProgressBarPanel;
import co.foldingmap.GUISupport.ProgressIndicator;
import co.foldingmap.GUISupport.Updateable;
import co.foldingmap.MainWindow;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.Layer;
import co.foldingmap.map.MapPanel;
import co.foldingmap.map.themes.ColorRamp;
import co.foldingmap.map.themes.ColorStyle;
import co.foldingmap.map.themes.MapTheme;
import co.foldingmap.map.visualization.HeatMapKey;
import java.util.ArrayList;

/**
 *
 * @author Alec
 */
public class Actions {
    private ArrayList<Action>  preformedActions, undoneActions;
    private DigitalMap         mapData;
    private int                maxActionsStored;
    private MapPanel           mainMapPanel;
    
    /**
     * Constructor for objects of class Actions
     */
    public Actions(DigitalMap mapData, MapPanel mainMapPanel) {
        this.mapData           = mapData;
        this.mainMapPanel      = mainMapPanel;
        this.maxActionsStored  = 10;
        this.preformedActions  = new ArrayList<Action>();
        this.undoneActions     = new ArrayList<Action>();
    }    
    
    /**
     * Adds a new MapPoint to the map at the last clicked location.
     * 
     * @param mapData 
     */
    public void addMapPoint(DigitalMap mapData) {
        Action action = new AddMapPoint(mapData);
        performAction(action); 
    }
    
    /**
     * Adds the coordinate the selected object.
     * 
     * @param mapData
     * @param coordinateToAdd 
     */
    public void addPointToObject(DigitalMap mapData, Coordinate coordinateToAdd) {
        Action action = new AddPointToObject(mapData, coordinateToAdd);
        performAction(action);        
    }
    
    /**
     * Changes a Map's name.
     * 
     * @param mapData
     * @param newName 
     */
    public void changeMapName(DigitalMap mapData, String newName) {
        if(!mapData.getName().equals(newName)) {
            ChangeMapName action = new ChangeMapName(mapData, newName);
            performAction(action);     
        }
    }
    
    /**
     * Changes an Objects name.
     * 
     * @param object
     * @param newName 
     */
    public void changeObjectName(VectorObject object, String newName) {
        if (!object.getName().equals(newName)) {
            ChangeMapObjectName action = new ChangeMapObjectName(object, newName);
            performAction(action);         
        }
    }
    
    /**
     * Changes a list of VectorObject's style to a new style.
     * 
     * @param objects
     * @param mapTheme
     * @param newStyle 
     */
    public void changeObjectStyle(VectorObjectList<VectorObject> objects, MapTheme mapTheme, ColorStyle newStyle) {
        ChangeObjectStyle action = new ChangeObjectStyle(objects, mapTheme, newStyle);
        performAction(action);
    }
    
    /**
     * Changes an Object's Z order within its parent layer.
     * 
     * @param mapData
     * @param operation 
     */
    public void changeObjectZOrder(DigitalMap mapData, int operation) {
        Action action = new  ChangeObjectZOrder(mapData, operation);
        performAction(action);        
    }
    
    /**
     * Changes a map's theme.
     * 
     * @param mapData               The DigitalMap to which the new MapTheme is being applied.
     * @param newTheme              The new MapTheme being applied.
     * @param updateable            Class to update when this Action is finished
     * @param progressIndicator     Class to indicate current progress in this Action.
     */
    public void changeTheme(DigitalMap        mapData, 
                            MapTheme          newTheme, 
                            Updateable        updateable,
                            ProgressIndicator progressIndicator) {
        
        ChangeTheme action = new ChangeTheme(mapData, newTheme, updateable, progressIndicator);
        performAction(action);
    }
           
    /**
     * Connects a LineString to another map object.
     * 
     * @param mapData
     * @param lineToExtend
     * @param objectToConectTo 
     */
    public void connectLinearObject(DigitalMap mapData) {
        Action action = new ConnectLinearObject(mapData);
        performAction(action);        
    }
    
    /**
     * Creates a bubble chart
     * 
     * @param mapData
     * @param objects
     * @param rampID
     * @param colorVariable
     * @param sizeVariable 
     */
    public void CreateBubbleChart(DigitalMap mapData,
                                  VectorObjectList<VectorObject> objects, 
                                  String    rampID, 
                                  String    colorVariable, 
                                  String    sizeVariable) {                
        
        Action action = new CreateBubbleChart(mapData, objects, rampID, colorVariable, sizeVariable);
        performAction(action);          
    }
    
    /**
     * Creates a HeatMap of the selected objects with a given field value.
     * 
     * @param mapData
     * @param objects
     * @param fieldName
     * @param hashMap 
     */
    public void createHeatMap(DigitalMap mapData, 
                             String     layerName,
                             VectorObjectList<VectorObject> objects, 
                             String[]   fieldName,
                             ColorRamp  colorRamp,
                             int        displayInterval,
                             HeatMapKey heatMapKey) {
        
        Action action = new CreateHeatMap(mapData, layerName, objects, fieldName, colorRamp, displayInterval, heatMapKey);
        performAction(action);          
    }
    
    /**
     * Crops the current map to a given bounds.
     * 
     * @param mapData
     * @param bounds 
     */
    public void cropMap(DigitalMap mapData, LatLonBox bounds) {
        Action action = new CropMap(mapData, bounds);
        performAction(action);
    }
    
    /**
     * Copies selected objects to the clipboard then deletes them from the map.
     * 
     * @param mapData 
     */
    public void cutSelectedObjects(DigitalMap mapData) {
        Action action = new CutSelectedObjects(mapData);
        performAction(action);
    }
    
    /**
     * Removes the selected point from a selected object.
     * 
     * @param mapData 
     */
    public void deletePointFromObject(DigitalMap mapData) {
        Action action = new DeletePointFromObject(mapData);
        performAction(action);        
    }
    
    /**
     * Removes the selected layer form the map.
     * 
     * @param mapData 
     */
    public void deleteSelectedLayer(MainWindow mainWindow, DigitalMap mapData) {
        Action action = new DeleteSelectedLayer(mainWindow, mapData);
        performAction(action);  
    }
    
    /**
     * Deletes all selected objects in the given map.
     * 
     * @param mapData 
     */
    public void deleteSelectedObjects(DigitalMap mapData) {
        Action action = new DeleteSelectedObjects(mapData);
        performAction(action);
    }
    
    /**
     * Returns the action for the next action to be redone.
     * Returns null if there is no next action.
     * 
     * @return 
     */
    public Action getRedoAction() {
        if (undoneActions.size() > 0) {
            Action a = undoneActions.get(undoneActions.size() - 1);        
            return a;
        } else {
            return null;
        }
    }
    
    /**
     * Returns the action for the next action to be undone.
     * Returns null if there is no next action.
     * 
     * @return 
     */
    public Action getUndoAction() {
        if (preformedActions.size() > 0) {
            Action a = preformedActions.get(preformedActions.size() - 1);        
            return a;
        } else {
            return null;
        }
    }
        
    /**
     * Opens a FileDialog for the user to select which photo(s) to import.
     * 
     * @param mainWindow 
     */
    public void importGeotaggedPhotos(MainWindow mainWindow, DigitalMap mapData)  {
        Action action = new ImportGeotaggedPhotos(mainWindow, mapData);
        performAction(action);        
    } 
    
    /**
     * Imports a map by creating new layers in the current map.
     * 
     * @param mainWindow
     * @param mapData 
     */
    public void importMap(MainWindow mainWindow, DigitalMap mapData) {
        Action action = new ImportMap(mainWindow, mapData);
        performAction(action);
    }
    
    /**
     * Merges the original Coordinate into the second Coordinate.
     * 
     * @param mapData
     * @param original
     * @param mergeTo 
     */
    public void mergeCoordinates(DigitalMap mapData, Coordinate original, Coordinate mergeTo) {    
        Action action = new MergeCoordinates(mapData, original, mergeTo);
        performAction(action);            
    }
    
    /**
     * Merges two layers together into one.
     * 
     * @param mapData
     * @param layerToMerge
     * @param layerAcceptingMerge 
     */
    public void mergeLayer(DigitalMap mapData, Layer layerToMerge, Layer layerAcceptingMerge) {
        Action action = new MergeLayer(mapData, layerToMerge, layerAcceptingMerge);
        performAction(action);        
    }
    
    /**
     * The action for moving coordinates.
     * 
     * @param originalPosition
     * @param newPosition 
     */
    public void moveCoordinate(Coordinate originalPosition, Coordinate newPosition) {
        Action action = new MoveCoordinate(originalPosition, newPosition);
        performAction(action);         
    }    
    
    /**
     * Move the selected layer in the map down in order.
     * 
     * @param layers
     * @param selectedLayer 
     */
    public void moveLayerDown(DigitalMap mapData, Updateable updateable) {
        Action action = new MoveSelectedLayerDown(mapData);
        performAction(action);   
        updateable.update();
    }
    
    /**
     * Move the selected layer in the map up in order.
     * 
     * @param layers
     * @param selectedLayer 
     */
    public void moveLayerUp(DigitalMap mapData, Updateable updateable) {
        Action action = new MoveSelectedLayerUp(mapData);
        performAction(action);  
        updateable.update();
    }
    
    public void moveObjectsToLayer(DigitalMap mapData, String layerName, VectorObjectList<VectorObject> objects) {
        Action action = new MoveObjectsToLayer(mapData, layerName, objects);
        performAction(action);          
    }
    
    /**
     * Action to open and load a map, replaces currently loaded map.
     * 
     * @param mainWindow 
     */
    public void openMap(MainWindow mainWindow, MapPanel mapPanel) {
        OpenMap openMap = new OpenMap(this, mainWindow);
        
        openMap.execute();

//        2013-04-24
//        if (openMap.getMap() != null)
//            mapPanel.setMap(openMap.getMap());
        
        //New map opened reset actions list
        this.preformedActions.clear();
        this.undoneActions.clear();
    }
    
    /**
     * Tries to paste what ever data it can from the map into the clipboard.
     * Currently Supports KML and GPX
     * 
     * @param mapData 
     */
    public void paste(MainWindow mainWindow, DigitalMap mapData) {
        Action action;
        int    dataType;
        
        action   = null;
        dataType = ClipboardOperations.getPasteDataType();
        
        if (dataType == ClipboardOperations.GPX) {
            action = new PasteGPX(mapData);
        } else if (dataType == ClipboardOperations.KML) {
            action = new PasteKML(mainWindow, mapData);
        } else {
            //Try to paste from plain text
            action = new PasteText(mapData);
        }
        
        if (action != null) performAction(action);
        
        mainWindow.update();
    }
    
    /**
     * Pastes KML as MapOBjects into the current DigitalMap.
     * 
     * @param mainWindow
     * @param mapData 
     */
    public void pasteKML(MainWindow mainWindow, DigitalMap mapData) {
        Action action = new PasteKML(mainWindow, mapData);
        performAction(action);
    }
    
    /**
     * Executes and actions and adds it to the performedActions list.
     * 
     * @param action 
     */
    public void performAction(Action action) {
        preformedActions.add(action);
        action.execute();        
    }
    
    /**
     * Re-executes the last undone action.
     * 
     */
    public void redo() {
        Action a;
        
        a = undoneActions.remove(undoneActions.size() - 1);
        
        a.execute();
        preformedActions.add(a);        
    }
    
    /**
     * Saves the map to it's map file.  If the map exists only in memory,
     * the SaveMapAs action will be called.
     * 
     * @param mapData 
     */
    public void saveMap(MainWindow mainWindow, DigitalMap mapData, ProgressBarPanel progressBarPanel) {
        Action  saveMap = new SaveMap(mainWindow, mapData, progressBarPanel);
        saveMap.execute();
    }
    
    /**
     * Saves the map to a new map file.  
     * 
     * @param mapData 
     */
    public void saveMapAs(MainWindow mainWindow, DigitalMap mapData, ProgressBarPanel progressBarPanel) {
        Action  saveMap = new SaveMapAs(mainWindow, mapData, progressBarPanel);
        saveMap.execute();
    }    
    
    /**
     * Splits an object into two objects at a given point
     */
    public void splitObject(DigitalMap mapData, VectorObject objectToSplit, Coordinate coordinateToSplitAt) {
        SplitObject action = new SplitObject(mapData, objectToSplit, coordinateToSplitAt);
        action.execute();
        preformedActions.add(action);          
    }
    
    /**
     * Does the trace merge
     * 
     * @param mapData
     */
    public void traceMerge(DigitalMap mapData, 
                           VectorObjectList<VectorObject> objectsToMerge, 
                           CoordinateList<Coordinate>     coordinatesToMerge,
                           ArrayList<Updateable>          updateables) {
        
        Action action = new TraceMerge(mapData, objectsToMerge, coordinatesToMerge, updateables);
        performAction(action);                 
    }    
    
    /**
     * Undoes the last preformed Action.
     * 
     */
    public void undo() {
        Action a;
        
        a = preformedActions.remove(preformedActions.size() - 1);
        
        a.undo();
        undoneActions.add(a);
    }
    
    /**
     * Unmerge actions, performs different operations depending on what is being unmerged.
     * 
     * @param mapData 
     */
    public void Unmerge(DigitalMap mapData) {
        Action action = new Unmerge(mapData);
        performAction(action);          
    }
}
