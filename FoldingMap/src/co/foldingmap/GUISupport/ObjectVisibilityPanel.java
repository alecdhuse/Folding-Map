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

import co.foldingmap.Logger;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.MapView;
import co.foldingmap.map.Visibility;
import co.foldingmap.map.tile.TileMath;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Alec
 */
public class ObjectVisibilityPanel extends JPanel implements ActionListener {
    protected DigitalMap                mapData;
    protected JCheckBox                 checkMinView, checkMaxView;
    protected JLabel                    labelVisibleSpacer;
    protected JPanel                    panelLevelOfDetail;
    protected JRadioButton              radioAlwaysVisible;
    protected JTextField                textVisibleMaxView, textVisibleMinView;
    protected VectorObjectList<VectorObject>  mapObjects;

    public ObjectVisibilityPanel() {        
        init();
    }

    public ObjectVisibilityPanel(DigitalMap mapData, VectorObjectList<VectorObject> mapObjects) {
        this.mapData    = mapData;
        this.mapObjects = mapObjects;
        init();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object sourceObject = e.getSource();

        if (sourceObject == checkMaxView) {
            radioAlwaysVisible.setSelected(false);
            textVisibleMaxView.setEnabled(true);
            updateVisibility();
        } else if (sourceObject == checkMinView) {
            radioAlwaysVisible.setSelected(false);
            textVisibleMinView.setEnabled(true);
            updateVisibility();
        } else if (sourceObject == radioAlwaysVisible) {
            checkMaxView.setSelected(false);
            checkMinView.setSelected(false);
            textVisibleMaxView.setEnabled(false);
            textVisibleMinView.setEnabled(false);
            updateVisibility();
        }
    }

    private void init() {
        try {
            checkMaxView        = new JCheckBox("Max This Zoom");
            checkMinView        = new JCheckBox("Min This Zoom");
            labelVisibleSpacer  = new JLabel("");
            panelLevelOfDetail  = new JPanel(new GridLayout(2, 3, 5, 5));
            radioAlwaysVisible  = new JRadioButton("Always Visible");
            textVisibleMaxView  = new JTextField();
            textVisibleMinView  = new JTextField();

            panelLevelOfDetail.setBorder(new TitledBorder("Level Of Detail"));
            
            this.setLayout(new GridLayout(1, 1, 5, 5));
            this.setBorder(new TitledBorder(""));
            this.add(panelLevelOfDetail);
            
            panelLevelOfDetail.add(radioAlwaysVisible);
            panelLevelOfDetail.add(checkMaxView);
            panelLevelOfDetail.add(checkMinView);
            panelLevelOfDetail.add(labelVisibleSpacer);
            panelLevelOfDetail.add(textVisibleMaxView);
            panelLevelOfDetail.add(textVisibleMinView);

            checkMaxView.addActionListener(this);
            checkMinView.addActionListener(this);
            radioAlwaysVisible.addActionListener(this);

            if (this.mapObjects == null) {
                textVisibleMaxView.setEnabled(false);
                textVisibleMinView.setEnabled(false);
                radioAlwaysVisible.setSelected(true);
                checkMaxView.setSelected(false);
                checkMinView.setSelected(false);
            } else {
                if (this.mapObjects.size() == 1) {
                    Visibility visibility = mapObjects.get(0).getVisibility();
                    
                    if (visibility != null) {
                        float  maxTile = visibility.getMaxTileZoomLevel();
                        float  minTile = visibility.getMinTileZoomLevel();

                        if (maxTile >= 25 && minTile <= 0) {
                            radioAlwaysVisible.setSelected(true);
                        } else if (maxTile < 25 && minTile <= 0) {
                            radioAlwaysVisible.setSelected(false);
                            checkMaxView.setSelected(true);
                            checkMinView.setSelected(false);
                            textVisibleMaxView.setText(Float.toString(maxTile));
                            textVisibleMinView.setText("");
                        } else if (minTile >= 0) {
                            radioAlwaysVisible.setSelected(false);
                            checkMaxView.setSelected(false);
                            checkMinView.setSelected(true);
                            textVisibleMaxView.setText("");
                            textVisibleMinView.setText(Float.toString(minTile));
                        } else {
                            radioAlwaysVisible.setSelected(true);
                        }
                    } else {
                        //no region
                        radioAlwaysVisible.setSelected(true);
                    }
                } else {
                   //size > 1
                }
            }

        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in ObjectVisibilityPanel.init() - " + e);
        }
    }

    /**
     * Saves the visibility values to the given mapObjects.
     */
    public void save() {
        VectorObject currentObject;
        
        try {
            for (int i = 0; i < mapObjects.size(); i++) {
                currentObject = mapObjects.get(i);

                currentObject.getVisibility().setMaxTileZoomLevel(Float.parseFloat(textVisibleMaxView.getText()));
                currentObject.getVisibility().setMinTileZoomLevel(Float.parseFloat(textVisibleMinView.getText()));         
            } //end for loop        
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in ObjectVisibilityPanel.save() - " + e);
        }            
    }
                
    private void updateVisibility() { 
        float        tileZoom;
        MapView      mapView;        
        VectorObject currentObject;
        
        try {
            mapView  = mapData.getLastMapView();
            tileZoom = TileMath.getTileMapZoom(mapView.getZoomLevel());
                    
            for (int i = 0; i < mapObjects.size(); i++) {
                currentObject = mapObjects.get(i);
                
                if (radioAlwaysVisible.isSelected()) {
                    this.textVisibleMaxView.setText("");
                    this.textVisibleMinView.setText("");   
                    currentObject.setVisibility(null);
                } else if (checkMaxView.isSelected() && !checkMinView.isSelected()) {
                    if (currentObject.getVisibility() == null) {
                        currentObject.setVisibility(new Visibility(tileZoom, 0));
                    } else {
                        currentObject.getVisibility().setMaxTileZoomLevel(tileZoom);
                    }
                    
                    this.textVisibleMaxView.setText(Float.toString(tileZoom));
                    this.textVisibleMinView.setText("");          
                } else if (!checkMaxView.isSelected() && checkMinView.isSelected()) {  
                    if (currentObject.getVisibility() == null) {
                        currentObject.setVisibility(new Visibility(25, tileZoom));
                    } else {                    
                        currentObject.getVisibility().setMinTileZoomLevel(tileZoom);
                    }
                     
                    this.textVisibleMaxView.setText("");
                    this.textVisibleMinView.setText(Float.toString(tileZoom));                    
                } else if (checkMaxView.isSelected() && checkMinView.isSelected()) {                    
                    if (currentObject.getVisibility() == null) {
                        currentObject.setVisibility(new Visibility(tileZoom, tileZoom));
                    } else {       
                        currentObject.getVisibility().setMaxTileZoomLevel(tileZoom);
                        currentObject.getVisibility().setMinTileZoomLevel(tileZoom);
                    }                    
                                        
                    currentObject.getVisibility().setMaxTileZoomLevel(tileZoom);
                    currentObject.getVisibility().setMinTileZoomLevel(tileZoom);
                    this.textVisibleMaxView.setText(Float.toString(tileZoom));
                    this.textVisibleMinView.setText(Float.toString(tileZoom));
                } //end if
            } //end for loop
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in ObjectVisibilityPanel.updateVisibility(): " + e);
        }
    }

    
}
