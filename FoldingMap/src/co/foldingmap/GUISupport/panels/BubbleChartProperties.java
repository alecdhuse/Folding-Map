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
package co.foldingmap.GUISupport.panels;

import co.foldingmap.GUISupport.CellEditorRenderer;
import co.foldingmap.GUISupport.SpringUtilities;
import co.foldingmap.GUISupport.components.ColorGradientComboBox;
import co.foldingmap.GUISupport.components.HeatMapValueTableModel;
import co.foldingmap.Logger;
import co.foldingmap.actions.Actions;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.MapObject;
import co.foldingmap.map.MapObjectList;
import co.foldingmap.map.themes.ColorRamp;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Alec
 */
public class BubbleChartProperties extends    ActionPanel 
                                   implements ItemListener, 
                                              ListSelectionListener {
    
    private Actions                         actions;
    private ArrayList<String>               customDataFields;
    private boolean                         areNumbers;
    private ButtonGroup                     objectsToUseButtonGroup, keyOrientationButtonGroup;
    private ColorGradientComboBox           comboGradientStyle;
    private DigitalMap                      mapData;
    private HeatMapValueTableModel          heatMapValueTableModel;
    private JComboBox                       comboKeyPosition; 
    private JComboBox                       comboColorVariable, comboSizeVariable;    
    private JLabel                          labelKeyOrientation;
    private JPanel                          panelGradientStyle;
    private JPanel                          panelKeyOptions, panelKeyOrientation, panelKeyPosition;
    private JPanel                          panelLayerName;
    private JPanel                          panelObjectsToUse, panelObjectsToUseButtons, panelOptions;        
    private JPanel                          panelColorVariable, panelSizeVariable;
    private JPanel                          panelTransparency;
    private JRadioButton                    radioUseAllObject, radioUseSelectedObjects;
    private JRadioButton                    radioHorizontal, radioVertical;
    private JSlider                         sliderTransparency;
    private JTable                          tableObjectValues;
    private JTextField                      textName;
    private JScrollPane                     spaneOptions, spaneStringValues;
    private String[]                        keyPositions = {"No Key", "Top Left", "Top Right", "Bottom Left", "Bottom Right"};
    private ValueRangePanel                 colorValueRange, sizeValueRange;
    private VectorObjectList<VectorObject>  selectedVectorObjects;    
    private VectorLayer                     vectorLayer;
    
    public BubbleChartProperties(Actions actions, DigitalMap mapData, VectorLayer vectorLayer) {
        this.mapData     = mapData;
        this.vectorLayer = vectorLayer;
        
        //temp
        this.selectedVectorObjects = new VectorObjectList<VectorObject>();
        this.actions               = actions;
        
        init();
        updateColorMinMax();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        ColorRamp ramp = getColorRamp();
        MapObjectList<MapObject> mapObjects;
        
        mapData.getTheme().addColorRamp(ramp);
        selectedVectorObjects = new VectorObjectList<VectorObject>();
        
        if (radioUseAllObject.isSelected()) {
            mapObjects = mapData.getAllMapObjects();
        } else {
            mapObjects = mapData.getSelectedObjects();
        }
        
        for (MapObject object: mapObjects) {
            if (object instanceof VectorObject) {
                selectedVectorObjects.add((VectorObject) object);
            }
        } 
        
        actions.CreateBubbleChart(mapData, selectedVectorObjects, ramp.getID(), (String) comboColorVariable.getSelectedItem(), (String) comboSizeVariable.getSelectedItem());
    }

    private void addObjectsToFrame(boolean numericalValues) {    
        panelOptions.removeAll();
        
        //center panel
        this.setLayout(new BorderLayout());
        this.add(spaneOptions, BorderLayout.CENTER);

        //options panel
        panelOptions.add(panelLayerName);
        panelOptions.add(panelObjectsToUse);
        panelOptions.add(panelColorVariable);
        panelOptions.add(panelSizeVariable);
        //panelOptions.add(panelKeyOptions);        
        
        //objects to use
        panelObjectsToUseButtons.add(radioUseAllObject);
        panelObjectsToUseButtons.add(radioUseSelectedObjects);        
        objectsToUseButtonGroup.add(radioUseAllObject);
        objectsToUseButtonGroup.add(radioUseSelectedObjects);
        
        //Layer Name Panel
        panelLayerName.setBorder(new TitledBorder("Layer Name"));
        panelLayerName.add(textName);    
        
        //Objects to Use Panel
        panelObjectsToUse.add(panelObjectsToUseButtons);        
        panelObjectsToUse.setBorder(new TitledBorder("Objects To Use"));
        
        //Color Variable 
        panelColorVariable.removeAll();
        panelColorVariable.setBorder(new TitledBorder("Bubble Color Variable"));
        panelColorVariable.add(comboColorVariable);
        
        //Value Range Panel
        colorValueRange.setBorder(new TitledBorder("Value Range"));
                
        if (numericalValues) {
            panelGradientStyle.setBorder(new TitledBorder("Style"));
            panelGradientStyle.add(comboGradientStyle);
            
            panelColorVariable.add(colorValueRange);
            panelColorVariable.add(panelGradientStyle);             
        } else {
            panelColorVariable.add(spaneStringValues);
        }       
                                
        panelColorVariable.add(panelTransparency);
        panelTransparency.add(sliderTransparency);        
        panelTransparency.setBorder(new TitledBorder("Transparency"));
                
        //Size Variable Options Panel
        panelSizeVariable.setBorder(new TitledBorder("Bubble Size Variable"));
        panelSizeVariable.add(comboSizeVariable);
        panelSizeVariable.add(sizeValueRange);
        sizeValueRange.setBorder(new TitledBorder("Value Range"));
        SpringUtilities.makeCompactGrid(panelSizeVariable, 2,  1, 3, 3, 10, 10);  
        
        //Key Orientation
        keyOrientationButtonGroup.add(radioHorizontal);
        keyOrientationButtonGroup.add(radioVertical);
        panelKeyPosition.add(comboKeyPosition);  
        panelKeyPosition.setBorder(new TitledBorder("Key Position"));
        panelKeyOrientation.add(radioHorizontal);
        panelKeyOrientation.add(radioVertical);
        panelKeyOrientation.setBorder(new TitledBorder("Key Orientation"));
        panelKeyOptions.add(panelKeyPosition);   
        panelKeyOptions.add(panelKeyOrientation); 
        
        SpringUtilities.makeCompactGrid(panelOptions, 4,  1, 3, 3, 10, 10);
        
        if (numericalValues) {
            SpringUtilities.makeCompactGrid(panelColorVariable, 4,  1, 3, 3, 10, 10);   
        } else {
            SpringUtilities.makeCompactGrid(panelColorVariable, 3,  1, 3, 3, 10, 10);   
        }
        
        this.validate();
        this.repaint();        
    }
    
    /**
     * Returns the color from the Gradient associated with the given value.
     * The min an max from the textFields will be used to determine this color.
     * 
     * @param value
     * @return 
     */
    public Color getColorFromGradient(float value) {
        Color           color;
        float           adjustedValue, max, min, valueRatio;
        int             intColorInteger, colorPosistion;     
        int             red, blue, green, alpha;
        int[]           pixelData;
        
        color = Color.BLACK;
        
        try {
            max            = (float) colorValueRange.getMaximum();
            min            = (float) colorValueRange.getMinimum();
            adjustedValue  = value - min;
            valueRatio     = adjustedValue / max;
            colorPosistion = (int) (valueRatio * 255);                       
            pixelData      = comboGradientStyle.getSelectedGradientPixelData();
                    
            //adjust for custom min and max
            if (colorPosistion > 255) {
                colorPosistion = 255;
            } else if (colorPosistion < 0) {
                colorPosistion = 0;
            }   
            
            if (pixelData != null) {
                intColorInteger = pixelData[colorPosistion];
                
                red   = (intColorInteger & 0x00ff0000) >> 16;
                green = (intColorInteger & 0x0000ff00) >> 8;
                blue  = (intColorInteger & 0x000000ff);
                alpha = sliderTransparency.getValue();
                color = new Color(red, green, blue, alpha);                
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in BubbleChartProperties.getColorFromGradient(float) - " + e);            
        }                
        
        return color;
    }    
    
    public ColorRamp getColorRamp() {
        ArrayList<String>       fieldValues;
        boolean                 addPair, areNumericalValues;
        Color                   color;
        ColorRamp               colorRamp;
        int                     numberOfValues;                    
       
        fieldValues = getColorFieldValues();   
               
        //test to see if field value are numbers or not
        if (getValuesAsNumbers(fieldValues) != null) {
            areNumericalValues = true;
        } else {
            areNumericalValues = false;
        }
                    
        //Create the HashMap with the initial capacity equal to the number of field values
        numberOfValues = fieldValues.size();
        colorRamp      = new ColorRamp(textName.getText() + "-ramp", numberOfValues);
     
        //Load the HashMap
        for (String value: fieldValues) {
            if (areNumericalValues) {
                if (!value.equals("")) {
                    color   = getColorFromGradient(Float.parseFloat(value));  
                    addPair = true;
                } else {
                    //make blanks transparent
                    color   = new Color(0,0,0,0);
                    addPair = false;
                }
            } else {
                color   = heatMapValueTableModel.getColorForValue(value);  
                addPair = true;
            }
            
            if (addPair == true)
                colorRamp.addEntry(value, color);
        }
        
        return colorRamp;        
    }
    
    /**
     * Initializes all displayable objects
     */
    private void init() {
        try {
            customDataFields = mapData.getAllCustomDataFields();
            customDataFields.add("Altitude");
            Collections.sort(customDataFields);
            
            colorValueRange              = new ValueRangePanel();
            comboGradientStyle           = new ColorGradientComboBox();
            comboKeyPosition             = new JComboBox(keyPositions);
            comboColorVariable           = new JComboBox(customDataFields.toArray());
            comboSizeVariable            = new JComboBox(customDataFields.toArray());       
            heatMapValueTableModel       = new HeatMapValueTableModel(this, customDataFields);    
            keyOrientationButtonGroup    = new ButtonGroup(); 
            labelKeyOrientation          = new JLabel("Key Orientation");
            tableObjectValues            = new JTable(heatMapValueTableModel);
            panelColorVariable           = new JPanel(new SpringLayout());
            panelGradientStyle           = new JPanel(new FlowLayout(FlowLayout.CENTER));
            panelLayerName               = new JPanel(new GridLayout(1, 2));
            panelObjectsToUse            = new JPanel(new GridLayout(1, 2));
            panelObjectsToUseButtons     = new JPanel(new GridLayout(1, 2));
            panelOptions                 = new JPanel(new SpringLayout());
            panelKeyOptions              = new JPanel(new GridLayout(2, 1));
            panelKeyPosition             = new JPanel(new GridLayout(1, 1));
            panelKeyOrientation          = new JPanel(new GridLayout(1, 2));
            panelSizeVariable            = new JPanel(new SpringLayout());
            panelTransparency            = new JPanel(new GridLayout(1, 1));
            objectsToUseButtonGroup      = new ButtonGroup();      
            radioHorizontal              = new JRadioButton("Horizontal", true);
            radioVertical                = new JRadioButton("Vertical");
            radioUseAllObject            = new JRadioButton("All Objects");
            radioUseSelectedObjects      = new JRadioButton("Selected objects");
            sizeValueRange               = new ValueRangePanel();
            sliderTransparency           = new JSlider(0, 255, 180);
            spaneOptions                 = new JScrollPane(panelOptions);
            spaneStringValues            = new JScrollPane(tableObjectValues);
            textName                     = new JTextField("Bubble Chart");
            
            //Set default range values
            colorValueRange.setRange(mapData.getMinimumFieldValue(customDataFields.get(0)), mapData.getMaximumFieldValue(customDataFields.get(0)));
            
            this.setLayout(new SpringLayout());
            radioUseAllObject.addActionListener(this);
            radioUseSelectedObjects.addActionListener(this);
            
            tableObjectValues.setDefaultRenderer(Component.class, new CellEditorRenderer());
            tableObjectValues.setDefaultEditor(Component.class,   new CellEditorRenderer());
            tableObjectValues.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            tableObjectValues.getColumnModel().getColumn(0).setPreferredWidth(220);
            tableObjectValues.getColumnModel().getColumn(1).setPreferredWidth(80);
            tableObjectValues.getColumnModel().getColumn(2).setPreferredWidth(70);            
                        
            spaneStringValues.setPreferredSize( new Dimension(245, 180));

            textName.setMaximumSize(new Dimension(250, 25));
            panelObjectsToUseButtons.setMaximumSize(new Dimension(250, 30));
            sliderTransparency.setMaximumSize(new Dimension(250, 30));
            panelGradientStyle.setMaximumSize(new Dimension(250, 30));        
            
            comboKeyPosition.addItemListener(this);
            labelKeyOrientation.setEnabled(false);
            radioHorizontal.setEnabled(false);
            radioVertical.setEnabled(false);
            
            comboColorVariable.addItemListener(this);
            comboSizeVariable.addItemListener(this);
            
            //Set the option buttons of what objects to use, baised on selected objects.
            if (selectedVectorObjects.size() > 1) {
                radioUseAllObject.setSelected(false);
                radioUseSelectedObjects.setSelected(true);
            } else {
                radioUseAllObject.setSelected(true);
                radioUseSelectedObjects.setSelected(false);
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in BubbleChartProperties.init() - " + e);
        }
    }        
    
    @Override
    public void itemStateChanged(ItemEvent ie) {
        if (ie.getSource() == comboKeyPosition) {
            if (comboKeyPosition.getSelectedIndex() == 0) {
                labelKeyOrientation.setEnabled(false);
                radioHorizontal.setEnabled(false);
                radioVertical.setEnabled(false);
            } else {
                labelKeyOrientation.setEnabled(true);     
                radioHorizontal.setEnabled(true);
                radioVertical.setEnabled(true);                
            }
        } else if (ie.getSource() == comboColorVariable) {
            updateColorMinMax();
        } else if (ie.getSource() == comboSizeVariable) {
            updateSizeMinMax();
        } 
    }

    /**
     * Gets the field values to be used when making the Bubble Chart.
     * 
     * @return 
     */
    public ArrayList<String> getColorFieldValues() {
        ArrayList<String>   currentValues, fieldValues;
        Object              colorObject;
        
        fieldValues  = new ArrayList<String>();
        colorObject  = comboColorVariable.getSelectedItem();
        
        if (radioUseSelectedObjects.isSelected()) {
            currentValues = selectedVectorObjects.getCustomDataFieldValue((String) colorObject);
            for (String s: currentValues) {
                if (!fieldValues.contains(s))
                    fieldValues.add(s); 
            }                
        } else {
            currentValues = mapData.getCustomDataFieldValue((String) colorObject);
            for (String s: currentValues) {
                if (!fieldValues.contains(s))
                    fieldValues.add(s); 
            } 
        }   
        
        return fieldValues;
    }    
    
    /**
     * Gets the field values to be used when making the Bubble Chart.
     * 
     * @return 
     */
    public ArrayList<String> getSizeFieldValues() {
        ArrayList<String>   currentValues, fieldValues;
        Object              colorObject;
        
        fieldValues  = new ArrayList<String>();
        colorObject  = comboSizeVariable.getSelectedItem();
        
        if (radioUseSelectedObjects.isSelected()) {
            currentValues = selectedVectorObjects.getCustomDataFieldValue((String) colorObject);
            for (String s: currentValues) {
                if (!fieldValues.contains(s))
                    fieldValues.add(s); 
            }                
        } else {
            currentValues = mapData.getCustomDataFieldValue((String) colorObject);
            for (String s: currentValues) {
                if (!fieldValues.contains(s))
                    fieldValues.add(s); 
            } 
        }   
        
        return fieldValues;
    }        
    
    /**
     * Returns the String values as numbers, if they can be converted.
     * Returns null if they cannot, meaning they are not all numbers.
     * 
     * @param values
     * @return 
     */
    public ArrayList<Float> getValuesAsNumbers(ArrayList<String> values) {
        ArrayList<Float>    numbers;
        float               number;
        
        numbers = new ArrayList<Float>();
        
        try {
            for (String s: values) {
                if (!s.equals("")) {
                    number = Float.parseFloat(s);   
                    numbers.add(number);
                } else {
                    //ignore blanks
                }
            }
            
            return numbers;
        } catch (Exception e) {
            //Formatting Error, not a number
            return null;
        }        
    }      
    
    /**
     * Updates the min and max color text fields.
     */
    private void updateColorMinMax() {
        ArrayList<Float>    numbers;
        ArrayList<String>   currentValues, fieldValues;
        Object              colorObject, sizeObject;
        String              selectedColorField, selcectedSizeField;
        
        try {
            colorObject = comboColorVariable.getSelectedItem();
            sizeObject  = comboSizeVariable.getSelectedItem();
                    
            selectedColorField = (String) comboColorVariable.getSelectedItem();    

            fieldValues = getColorFieldValues();                   
            numbers     = getValuesAsNumbers(fieldValues);
         

            if (numbers != null && numbers.size() > 0) {          
                areNumbers = true;
                Collections.sort(numbers);       
                colorValueRange.setMinimum(numbers.get(0));
                
                if (numbers.size() > 1) {
                    colorValueRange.setMaximum(numbers.get(numbers.size() - 1));
                } else {
                    colorValueRange.setMinimum(numbers.get(0));
                }
                
                addObjectsToFrame(true);
            } else {
                areNumbers = false;
                Collections.sort(fieldValues);            
                heatMapValueTableModel.setTableData(fieldValues); 
                addObjectsToFrame(false);
            }       
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in BubbleChartProperties.updateColorMinMax() - " + e);
        }
    }    
    
    /**
     * Updates the min and max color text fields.
     */
    private void updateSizeMinMax() {
        ArrayList<Float>    numbers;
        ArrayList<String>   currentValues, fieldValues;
        Object              colorObject, sizeObject;
        String              selectedSizeField;
        
        try {
            sizeObject  = comboSizeVariable.getSelectedItem();
                    
            selectedSizeField = (String) comboColorVariable.getSelectedItem();    

            fieldValues = getSizeFieldValues();                   
            numbers     = getValuesAsNumbers(fieldValues);
         

            if (numbers != null && numbers.size() > 0) {          
                areNumbers = true;
                Collections.sort(numbers);       
                sizeValueRange.setMinimum(numbers.get(0));
                
                if (numbers.size() > 1) {
                    sizeValueRange.setMaximum(numbers.get(numbers.size() - 1));
                } else {
                    sizeValueRange.setMinimum(numbers.get(0));
                }
                
                addObjectsToFrame(true);
            } else {
                areNumbers = false;
                Collections.sort(fieldValues);            
                heatMapValueTableModel.setTableData(fieldValues); 
                addObjectsToFrame(false);
            }       
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in BubbleChartProperties.updateSizeMinMax() - " + e);
        }
    }        
    
    @Override
    public void valueChanged(ListSelectionEvent lse) {
        updateColorMinMax();
    }
}
