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
package co.foldingmap.GUISupport;

import co.foldingmap.map.MapObjectList;
import co.foldingmap.map.MapObject;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.Visibility;
import co.foldingmap.map.MapUtilities;
import co.foldingmap.map.vector.LinearRing;
import co.foldingmap.map.vector.MultiGeometry;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;
import co.foldingmap.map.vector.MapPoint;
import co.foldingmap.map.vector.LineString;
import co.foldingmap.map.vector.Polygon;
import co.foldingmap.actions.Actions;
import co.foldingmap.actions.ChangeMapObjectName;
import co.foldingmap.actions.SelectObjects;
import co.foldingmap.actions.ChangeNameAction;
import co.foldingmap.actions.ChangeMapName;
import co.foldingmap.GUISupport.components.PopupMenuButton;
import co.foldingmap.GUISupport.components.RangeSlider;
import co.foldingmap.Logger;
import co.foldingmap.MainWindow;
import co.foldingmap.ResourceHelper;
import co.foldingmap.map.themes.ColorStyle;
import co.foldingmap.map.themes.MapTheme;
import co.foldingmap.map.themes.MapThemeManager;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This class bundles the setup and actions of theObjectDetailsToolBar.
 * 
 * @author Alec
 */
public class ObjectDetailsToolBar extends    JToolBar 
                                  implements ActionListener, 
                                             CaretListener, 
                                             ChangeListener, 
                                             Updateable {
    
    private Actions                 actions;
//    private ArrayList<Updateable>   updates;
    private boolean                 mapSelected;
    private ChangeNameAction        changeNameAction;
    private DefaultComboBoxModel    comboModel;
    private DigitalMap              mapData;
    private ImageIcon               iconBlank, iconLinearRing, iconLineString, iconMap;
    private ImageIcon               iconMapPoint, iconMultiGeometry, iconPolygon;
//    private JButton                 buttonObjectVisibility;
    private JComboBox               comboObjectClassType;
    private JLabel                  labelObjectDetailSpacer, labelObjectType;
    private JTextField              textObjectName;
    private RangeSlider             visibilityRange;
    private VectorObject            mapObject;
    private MainWindow              parentWindow;
    private PopupMenuButton         buttonFilterPoints, buttonFilterLines, buttonFilterPolygons, buttonFilterMulti;
    private ResourceHelper          helper;
    private SelectObjects           selectPoints, selectLines, selectPolygons, selectMulti;    
    
    public ObjectDetailsToolBar(MainWindow  parentWindow) {
        super("Object Details",JToolBar.HORIZONTAL);
        
        this.parentWindow = parentWindow;
//        c
        
        init();
    }
    
    /**
     * Handles the action preformed event for objects on this tool bar.
     * 
     * @param ae 
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == comboObjectClassType) {
            if (comboModel.getSelectedItem() instanceof MapTheme) {
                changeTheme();
                parentWindow.update();
            } else {
                changeStyle();
            }
//        } else if (ae.getSource() == buttonObjectVisibility) {
//            int popupX = this.buttonObjectVisibility.getX() - (200);
//            int popupY = (int) this.buttonObjectVisibility.getY() + this.buttonObjectVisibility.getHeight() + 60;
//            parentWindow.showVisibilityPopup(popupX, popupY);
        } else if (ae.getSource() instanceof PopupMenuButton) {
            if (ae.getActionCommand().equalsIgnoreCase(PopupMenuButton.MENU_ACTIVATION)) {
                //load the currently used clesses as menu options for these buttons
                updateFilterButton((PopupMenuButton) ae.getSource());           
            } else {
                if (ae.getSource() == buttonFilterPoints) {
                    selectPoints.execute();
                } else if (ae.getSource() == buttonFilterLines) {
                    selectLines.execute();
                } else if (ae.getSource() == buttonFilterPolygons) {
                    selectPolygons.execute();
                } else if (ae.getSource() == buttonFilterMulti) {
                    selectMulti.execute();
                }
                
                this.update();
                this.parentWindow.update();
            }
        } 
    }    
    
    /**
     * Adds a KeyListener to objects on this ToolBar.
     * 
     * @param kl 
     */
    @Override
    public void addKeyListener(KeyListener kl) {
        comboObjectClassType.addKeyListener(kl);
        textObjectName.addKeyListener(kl);
    }
    
//    public void addUpdateable(Updateable updateable) {
//        updates.add(updateable);
//    }
    
    @Override
    public void caretUpdate(CaretEvent ce) {
        if (ce.getSource() == textObjectName && changeNameAction != null) {
            if (changeNameAction.hasExecuted()) {
                changeNameAction = new ChangeMapName(mapData, textObjectName.getText());
            } else {
                changeNameAction.setNameText(textObjectName.getText());
            }
        }
    }    
    
    /**
     * Changes the style of the selected object(s).
     * 
     */
    private void changeStyle() {
        ColorStyle  newStyle;
        VectorObjectList<VectorObject> vectorObjects;
        
        vectorObjects = new VectorObjectList<VectorObject>(mapData.getSelectedObjects());
        newStyle      = (ColorStyle) comboModel.getSelectedItem();
        actions.changeObjectStyle(vectorObjects, mapData.getTheme(), newStyle);
    }
    
    /**
     * Changes the map's theme.
     */
    private void changeTheme() {
        MapTheme    newTheme;
        
        newTheme = (MapTheme) comboModel.getSelectedItem();
        actions.changeTheme(mapData, newTheme, parentWindow, parentWindow.getProgressBarPanel());
    }  
    
    /**
     * Returns the VisibilityObject for an MapObject's style, if there is one.
     * 
     * @param object
     * @return 
     */
    private Visibility getObjectStyleVisibility(MapObject object) {
        ColorStyle cs  = null;
        Visibility vis = null;
        
        if (object instanceof MapPoint) {
            cs = mapData.getTheme().getIconStyle(((VectorObject)object).getObjectClass());
        } else if (object instanceof LineString) {
            cs = mapData.getTheme().getLineStyle(((VectorObject)object).getObjectClass());                            
        } else if (object instanceof Polygon) {
            cs = mapData.getTheme().getPolygonStyle(((VectorObject)object).getObjectClass());                                         
        }  
        
        if (cs != null) vis = cs.getVisibility();
        
        return vis;
    }
    
    private void init() {
        try {
            initFilters();
            helper = ResourceHelper.getInstance();
        
            //Object Icons           
            iconBlank               = helper.getImage("blank.png");
            iconLinearRing          = helper.getImage("linear_ring.png");
            iconLineString          = helper.getImage("polyline.png");
            iconMap                 = helper.getImage("new_map.png");
            iconMapPoint            = helper.getImage("marker.png");
            iconMultiGeometry       = helper.getImage("multi-geometry.png");
            iconPolygon             = helper.getImage("polygon.png");   
            
            buttonFilterPoints      = new PopupMenuButton(iconMapPoint,      actions, PopupMenuButton.RIGHT_CLICK);
            buttonFilterLines       = new PopupMenuButton(iconLineString,    actions, PopupMenuButton.RIGHT_CLICK);
            buttonFilterPolygons    = new PopupMenuButton(iconPolygon,       actions, PopupMenuButton.RIGHT_CLICK);
            buttonFilterMulti       = new PopupMenuButton(iconMultiGeometry, actions, PopupMenuButton.RIGHT_CLICK);             
            comboModel              = new DefaultComboBoxModel();
            comboObjectClassType    = new JComboBox(comboModel);
            labelObjectDetailSpacer = new JLabel("");
            labelObjectType         = new JLabel(" ", iconMap, SwingConstants.LEFT);
            visibilityRange         = new RangeSlider(0, 23);
            textObjectName          = new JTextField(16);                                                
                    
            visibilityRange.setMaximumSize(new Dimension(150, 20));
            visibilityRange.setPreferredSize(new Dimension(150, 20));
            visibilityRange.setToolTipText("Object's Zoom Level Visibility");
            
            this.add(labelObjectDetailSpacer);
            this.add(labelObjectType);
            this.add(textObjectName);
            this.add(comboObjectClassType);
            //this.add(buttonObjectVisibility);
            this.add(visibilityRange);
            this.addSeparator();
            this.add(buttonFilterPoints);
            this.add(buttonFilterLines);
            this.add(buttonFilterPolygons);
            this.add(buttonFilterMulti);         
            
            //Setup Display properties
//            buttonObjectVisibility.setEnabled(false);
            visibilityRange.setEnabled(false);
            comboObjectClassType.setEnabled(false);
            comboObjectClassType.setMaximumSize(new Dimension(250, 50));            
            textObjectName.setMaximumSize(new Dimension(250, 50));       
            
            //Add Listeners
//            buttonObjectVisibility.addActionListener(this);
            visibilityRange.addChangeListener(this);
            comboObjectClassType.addActionListener(this);
            textObjectName.addCaretListener(this);
            textObjectName.addKeyListener(parentWindow.getMapPanel());  
            
            //filter buttons
            buttonFilterPoints.addActionListener(this);
            buttonFilterLines.addActionListener(this);
            buttonFilterPolygons.addActionListener(this);
            buttonFilterMulti.addActionListener(this);         
            buttonFilterPoints.addUpdate(parentWindow);
            buttonFilterLines.addUpdate(parentWindow);
            buttonFilterPolygons.addUpdate(parentWindow);
            buttonFilterMulti.addUpdate(parentWindow);      
            
            buttonFilterPoints.addUpdate(this);
            buttonFilterLines.addUpdate(this);
            buttonFilterPolygons.addUpdate(this);
            buttonFilterMulti.addUpdate(this);              
            
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in ObjectDetailsToolBar.init() - " + e);
        }
    }
    
    /**
     * Initiates filter objects for the quick filter buttons
     */
    private void initFilters() {
        selectPoints   = new SelectObjects(mapData, SelectObjects.MAP_POINT,     SelectObjects.ALL_CLASSES, false);
        selectLines    = new SelectObjects(mapData, SelectObjects.LINE_STRING,   SelectObjects.ALL_CLASSES, false);
        selectPolygons = new SelectObjects(mapData, SelectObjects.POLYGON,       SelectObjects.ALL_CLASSES, false);
        selectMulti    = new SelectObjects(mapData, SelectObjects.MULTIGEOMETRY, SelectObjects.ALL_CLASSES, false);                                       
    }
    
    /**
     * Sets the map being used.
     * 
     * @param mapData 
     */
    public void setMap(DigitalMap mapData) {
        this.mapData = mapData;
        this.actions = mapData.getActions();
        
        buttonFilterPoints.setActions(actions);  
        buttonFilterLines.setActions(actions);  
        buttonFilterPolygons.setActions(actions);  
        buttonFilterMulti.setActions(actions);         
        
        selectPoints.setMap(mapData); 
        selectLines.setMap(mapData); 
        selectPolygons.setMap(mapData); 
        selectMulti.setMap(mapData);     
        
        textObjectName.setText(mapData.getName());
    }
    
    /**
     * Catches state changed events for objects on the listen list.
     * 
     * @param ce 
     */
    @Override
    public void stateChanged(ChangeEvent ce) {
        if (ce.getSource() == this.visibilityRange) {
            int min = visibilityRange.getLowValue();
            int max = visibilityRange.getHighValue();
            
            for (MapObject object: mapData.getSelectedObjects()) {
                Visibility v = object.getVisibility();

                if (v == null && (min > 0 || max < 23)) {
                    v = new Visibility(max, min);
                    object.setVisibility(v);
                } else if (v != null) {
                    v = new Visibility(max, min);
                    object.setVisibility(v);
                } else {
                    object.setVisibility(null);
                }                
            }
            
            parentWindow.getMapPanel().update();
        }
    }    
    
    /**
     * Updates the selection of objects.
     */
    @Override
    public void update() {   
        ArrayList                   availableStyles;
        VectorObject                selectedObject;
        MapObjectList<MapObject>    selectedObjects;
        MapTheme                    mapTheme;
        MapThemeManager             themeManager;        
        
        try {
            //Update name, if it has changed before we update the toolbar.
            updateName();

            if (mapData != null) {
                mapSelected     = false;
                mapObject       = null;
                selectedObjects = (mapData.getSelectedObjects());  
                mapTheme        = mapData.getTheme();
                themeManager    = mapData.getMapThemeManager();

                if (selectedObjects.size() == 0) {
                    //No objects selected, display info about the map.
                    mapSelected = true;
                    textObjectName.setText(mapData.getName());
                    labelObjectType.setIcon(iconMap);
                    textObjectName.setEnabled(true);

                    comboModel = new DefaultComboBoxModel(themeManager.getAllThemes().toArray());
                    comboModel.setSelectedItem(mapTheme);
                    comboObjectClassType.setModel(comboModel);
                    comboObjectClassType.setEnabled(true);
                    visibilityRange.setEnabled(false);
                    visibilityRange.setValues(0, 23);
                    
                    changeNameAction = new ChangeMapName(mapData, textObjectName.getText());
                } else if (selectedObjects.size() == 1) {   
                    if (selectedObjects.get(0) instanceof VectorObject) {
                        selectedObject = (VectorObject) selectedObjects.get(0);
                        mapObject      = selectedObject;

                        textObjectName.setText(selectedObject.getName());
                        textObjectName.setEnabled(true);
//                        buttonObjectVisibility.setEnabled(true);
                        visibilityRange.setEnabled(true);
                                                
                        Visibility vis    = selectedObjects.get(0).getVisibility();
                        Visibility objVis = getObjectStyleVisibility(selectedObjects.get(0));
                                
                        if (vis != null) {       
                            visibilityRange.setValues((int) vis.getMinTileZoomLevel(), (int) vis.getMaxTileZoomLevel());
                        } else {
                            if (objVis != null) {
                                visibilityRange.setValues((int) objVis.getMinTileZoomLevel(), (int) objVis.getMaxTileZoomLevel());                                
                            } else {
                                visibilityRange.setValues(0, 23);
                            }
                        }                        
                    
                        if (selectedObject instanceof MapPoint) {
                            labelObjectType.setIcon(this.iconMapPoint); 

                            availableStyles = mapTheme.getAllIconStyles();
                            Collections.sort(availableStyles);

                            comboModel = new DefaultComboBoxModel(availableStyles.toArray());
                            comboModel.setSelectedItem(mapTheme.getIconStyle(selectedObject.getObjectClass()));
                            comboObjectClassType.setModel(comboModel);
                            comboObjectClassType.setEnabled(true);    
                        } else if (selectedObject instanceof LinearRing) {
                            labelObjectType.setIcon(this.iconLinearRing);

                            availableStyles = mapTheme.getAllLineStyles();
                            Collections.sort(availableStyles);                    

                            comboModel = new DefaultComboBoxModel(availableStyles.toArray());
                            comboModel.setSelectedItem(mapTheme.getLineStyle(selectedObject.getObjectClass()));
                            comboObjectClassType.setModel(comboModel);
                            comboObjectClassType.setEnabled(true);                                
                        } else if (selectedObject instanceof LineString) {  
                            labelObjectType.setIcon(this.iconLineString);

                            availableStyles = mapTheme.getAllLineStyles();
                            Collections.sort(availableStyles);                    

                            comboModel = new DefaultComboBoxModel(availableStyles.toArray());
                            comboModel.setSelectedItem(mapTheme.getLineStyle(selectedObject.getObjectClass()));
                            comboObjectClassType.setModel(comboModel);
                            comboObjectClassType.setEnabled(true);                                         
                        } else if (selectedObject instanceof Polygon) {
                            labelObjectType.setIcon(this.iconPolygon);

                            availableStyles = mapTheme.getAllPolygonStyles();
                            Collections.sort(availableStyles);                    

                            comboModel = new DefaultComboBoxModel(availableStyles.toArray());
                            comboModel.setSelectedItem(mapTheme.getPolygonStyle(selectedObject.getObjectClass()));
                            comboObjectClassType.setModel(comboModel);
                            comboObjectClassType.setEnabled(true);                     
                        } else if (selectedObject instanceof MultiGeometry) {
                            labelObjectType.setIcon(this.iconMultiGeometry);
                            comboObjectClassType.setEnabled(false);  
                        }

                        changeNameAction = new ChangeMapObjectName(selectedObject, textObjectName.getText());
                    }
                } else {
                    //more than one object selected
                    textObjectName.setText("");
                    
                    //Find the biggest max and smallest min and set the range slider to those vals.
                    int max = 23;
                    int min = 0;
                    
                    for (MapObject obj: selectedObjects) {                                                
                        Visibility v = obj.getVisibility();
                        
                        if (v != null) {
                            max = (v.getMaxTileZoomLevel() > max ? (int) v.getMaxTileZoomLevel() : max);
                            min = (v.getMinTileZoomLevel() < min ? (int) v.getMinTileZoomLevel() : min);                            
                        }                       
                    }
                    
                    visibilityRange.setValues(min, max);
                    
                    if (selectedObjects.areAllVectorObjects()) {
                        VectorObjectList<VectorObject> vectorObjects;
                        
                        vectorObjects = new VectorObjectList<VectorObject>(selectedObjects);
//                        buttonObjectVisibility.setEnabled(true);
                        visibilityRange.setEnabled(true);
                                
                        if (vectorObjects.getMapPoints().size() == selectedObjects.size()) {
                            labelObjectType.setIcon(this.iconMapPoint);

                            availableStyles = mapTheme.getAllIconStyles();
                            Collections.sort(availableStyles);                    

                            comboModel = new DefaultComboBoxModel(availableStyles.toArray());
                            comboModel.setSelectedItem(mapTheme.getIconStyle(null));
                            comboObjectClassType.setModel(comboModel);
                            comboObjectClassType.setEnabled(true);                      
                            textObjectName.setEnabled(false);
                        } else if (vectorObjects.getLineStrings().size() == selectedObjects.size()) {
                            labelObjectType.setIcon(this.iconLineString);

                            availableStyles = mapTheme.getAllLineStyles();
                            Collections.sort(availableStyles);                      

                            comboModel = new DefaultComboBoxModel(availableStyles.toArray());
                            comboModel.setSelectedItem(mapTheme.getLineStyle(null));
                            comboObjectClassType.setModel(comboModel);
                            comboObjectClassType.setEnabled(true);                      
                            textObjectName.setEnabled(false);
                        } else if (vectorObjects.getPolygons().size() == selectedObjects.size()) {    
                            labelObjectType.setIcon(this.iconPolygon);

                            availableStyles = mapTheme.getAllPolygonStyles();
                            Collections.sort(availableStyles);                     

                            comboModel = new DefaultComboBoxModel(availableStyles.toArray());
                            comboModel.setSelectedItem(mapTheme.getPolygonStyle(null));
                            comboObjectClassType.setModel(comboModel);
                            comboObjectClassType.setEnabled(true);                     
                            textObjectName.setEnabled(false);
                        } else if (vectorObjects.getMultiGeometries().size() == selectedObjects.size()) {  
                            labelObjectType.setIcon(this.iconMultiGeometry);
                            comboObjectClassType.setEnabled(false); 
                            textObjectName.setEnabled(false);
                        } else {
                            //Multiple Tyles of objects
                            labelObjectType.setIcon(this.iconBlank);
                            textObjectName.setText("Multiple Object Types");
                            textObjectName.setEnabled(false);
                            comboObjectClassType.setEnabled(false);
                        }

                        changeNameAction = null;
                    }//end VectorObject test
                }                               
            } //end mapData != null check

//            for (Updateable update: updates)
//                update.update();
            
            parentWindow.repaint();
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in ObjectDetailsToolBar.update() - " + e);
        }
    }
    
    /**
     * Updates the quick filter button with all used object classes for that
     * object type.
     * 
     * @param buttonToUpdate 
     */
    private void updateFilterButton(PopupMenuButton buttonToUpdate) {
        ArrayList<String>               classNames;
        int                             objectType;
        JMenuItem                       menuItem;
        SelectObjects                   selectObjects;
        VectorObjectList<VectorObject>  vectorObjects;
                
        buttonToUpdate.removeAllMenuItems();
        
        vectorObjects = new VectorObjectList<VectorObject>(mapData.getAllMapObjects());
        
        if (buttonToUpdate == buttonFilterPoints) {
            classNames = MapUtilities.getObjectClasses(vectorObjects.getMapPoints());   
            objectType = SelectObjects.MAP_POINT;
        } else if (buttonToUpdate == buttonFilterLines) {      
            classNames = MapUtilities.getObjectClasses(vectorObjects.getLineStrings());     
            objectType = SelectObjects.LINE_STRING;
        } else if (buttonToUpdate == buttonFilterPolygons) {  
            classNames = MapUtilities.getObjectClasses(vectorObjects.getPolygons());  
            objectType = SelectObjects.POLYGON;
        } else if (buttonToUpdate == buttonFilterMulti) {  
            classNames = MapUtilities.getObjectClasses(vectorObjects.getMultiGeometries());                    
            objectType = SelectObjects.MULTIGEOMETRY;
        } else {
            classNames = new ArrayList<String>();
            objectType = 0;
        }
        
        for (String name: classNames) {
            menuItem      = new JMenuItem(name);
            selectObjects = new SelectObjects(mapData, objectType, name, false);
            buttonToUpdate.add(menuItem, selectObjects);
        }
    }
    
    /**
     * Forces an update of the Map or the Selected Object's name.
     */
    public void updateName() {
        if (changeNameAction != null) {
            if (mapSelected) {
                //Change Map Name, if it has changed.
                if (!changeNameAction.getNameText().equals(mapData.getName()))
                    actions.performAction(changeNameAction);
            } else if (mapObject != null) {
                //Change Object Name
                if (!changeNameAction.getNameText().equals(mapObject.getName()))
                    actions.performAction(changeNameAction);
            }    
        } //end changeNameAction != null check
    }

}
