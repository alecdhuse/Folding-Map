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

import co.foldingmap.GUISupport.components.ColorGradientComboBox;
import co.foldingmap.GUISupport.components.HeatMapValueTableModel;
import co.foldingmap.GUISupport.panels.ActionPanel;
import co.foldingmap.Logger;
import co.foldingmap.actions.Actions;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.themes.ColorRamp;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;
import co.foldingmap.map.visualization.HeatMap;
import co.foldingmap.map.visualization.HeatMapKey;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Alec
 */
public class HeatMapProperties extends    ActionPanel 
                               implements ItemListener, 
                                          ListSelectionListener {
    
    private Actions                         actions;
    private ArrayList<String>               customDataFields;
    private boolean                         areNumbers;
    private ButtonGroup                     objectsToUseButtonGroup, keyOrientationButtonGroup;
    private ColorGradientComboBox           comboGradientStyle;
    private DigitalMap                      mapData;
    private HeatMap                         heatMap;
    private HeatMapValueTableModel          heatMapValueTableModel;
    private JComboBox                       comboKeyPosition;    
    private JLabel                          labelDisplayInterval, labelGradientStyle;
    private JLabel                          labelKeyPosition, labelKeyOrientation;
    private JLabel                          labelMaxValue, labelMinValue, labelName;
    private JLabel                          labelObjectsToUse, labelTransparency, labelVariable;
    private JList                           listVariable;
    private JPanel                          panelGradientStyle, panelKeyOrientation;
    private JPanel                          panelObjectsToUseButtons, panelOptions;        
    private JRadioButton                    radioUseAllObject, radioUseSelectedObjects;
    private JRadioButton                    radioHorizontal, radioVertical;
    private JSlider                         sliderTransparency;
    private JTable                          tableObjectValues;
    private JTextField                      textDisplayInterval, textMaxValue, textMinValue, textName;
    private JScrollPane                     spaneListVariables, spaneStringValues;
    private String[]                        keyPositions = {"No Key", "Top Left", "Top Right", "Bottom Left", "Bottom Right"};
    private VectorObjectList<VectorObject>  selectedVectorObjects;
    
    /**
     * Used for editing the properties of an existing HeatMap.
     * 
     * @param heatMap 
     */
    public HeatMapProperties(DigitalMap mapData, HeatMap heatMap) {
        this.heatMap                = heatMap;
        this.mapData                = mapData;
        this.selectedVectorObjects  = heatMap.getMapObjects();
                
        init();
        
        //set layer name
        textName.setText(heatMap.getName());
                
        //Set the used variables as selected
        int[] indices = new int[heatMap.getVariables().length];
        
        for (int i = 0; i < heatMap.getVariables().length; i++) 
            indices[i] = customDataFields.indexOf(heatMap.getVariables()[i]);        
        
        listVariable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listVariable.setSelectedIndices(indices);
                       
        updateMinMax();
        
        //update the display interval
        textDisplayInterval.setText(Integer.toString(heatMap.getDisplayInterval()));
        
        //update values
        ColorRamp         colorRamp = mapData.getTheme().getColorRamp(heatMap.getColorRampID());
        ArrayList<String> keys      = new ArrayList<String>(colorRamp.getKeySet());
        ArrayList<Color>  colors    = new ArrayList<Color>(colorRamp.getColors());
        
        //set transparency
        sliderTransparency.setValue(colors.get(1).getAlpha());
        
        if (areNumbers) {
            //update min and max values
            Collections.sort(keys);
            this.textMaxValue.setText(keys.get(keys.size() - 1));
            
            if (!keys.get(0).equals("")) {
                this.textMinValue.setText(keys.get(0));
            } else {
                this.textMinValue.setText(keys.get(1));
            }
        } else {
            //set each color and value in the list
            for (int i = 0; i < colors.size(); i++) {
                Color c = colors.get(i);
                heatMapValueTableModel.setColorValue(i, c);
            }
        }
        
        if (heatMap.getHeatMapKey().getPositionReference() == HeatMapKey.NONE) {
            comboKeyPosition.setSelectedIndex(0);
        } else if (heatMap.getHeatMapKey().getPositionReference() == HeatMapKey.TOP_LEFT) {
            comboKeyPosition.setSelectedIndex(1);
        } else if (heatMap.getHeatMapKey().getPositionReference() == HeatMapKey.TOP_RIGHT) {
            comboKeyPosition.setSelectedIndex(2);
        } else if (heatMap.getHeatMapKey().getPositionReference() == HeatMapKey.BOTTOM_LEFT) {
            comboKeyPosition.setSelectedIndex(3);
        } else if (heatMap.getHeatMapKey().getPositionReference() == HeatMapKey.BOTTOM_RIGHT) {
            comboKeyPosition.setSelectedIndex(4);
        }
        
        if (heatMap.getHeatMapKey().hasHorizontalOrientation()) {
            radioHorizontal.setEnabled(true);
            radioVertical.setEnabled(true);
            radioHorizontal.setSelected(true);
        } else {
            radioHorizontal.setEnabled(true);
            radioVertical.setEnabled(true);   
            radioVertical.setSelected(true);
        }
    }
    
    /**
     * Used for creating a new HeatMap.
     * 
     * @param mapData
     * @param actions 
     */
    public HeatMapProperties(DigitalMap mapData, Actions actions) {
        this.actions         = actions;
        this.mapData         = mapData;
        this.selectedVectorObjects = new VectorObjectList<VectorObject>(mapData.getSelectedObjects());        

        init();
        updateMinMax();        
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        HeatMapKey                      heatMapKey;
        JButton                         clickedButton;
        String                          actionEvent;
        VectorObjectList<VectorObject>  mapObjects;
        
        actionEvent = ae.getActionCommand();
        mapObjects  = new VectorObjectList<VectorObject>(mapData.getAllMapObjects());
                
        if (ae.getSource() instanceof JButton) {
            clickedButton = (JButton) ae.getSource();
        } else {
            clickedButton = null;
        }
        
        if (ae.getSource() == radioUseAllObject) {
            updateMinMax();
        } else if (ae.getSource() == radioUseSelectedObjects) {
            updateMinMax();
        } else if (actionEvent.equals("Change Color")) {
            Color newColor = JColorChooser.showDialog(null, "Change Color", clickedButton.getBackground()); 
            clickedButton.setBackground(newColor);
            clickedButton.setForeground(newColor);
            heatMapValueTableModel.setColorValue(tableObjectValues.getSelectedRow(), newColor);
        } else if (actionEvent.equalsIgnoreCase("Ok")) {
            heatMapKey = new HeatMapKey(getKeyLabels(), getColors(), getKeyPosition(), radioHorizontal.isSelected());
            
            //Action called when the ok button on the windows property dialog is clicked
            if (heatMap == null) {
                //create a new heatmap
                Object[] objects = this.listVariable.getSelectedValues();
                String[] fields = new String[objects.length];

                for (int i = 0; i < objects.length; i++) 
                    fields[i] = (String) objects[i];                

                ColorRamp              colorRamp       = this.getColorRamp();
                int                    displayInterval = Integer.parseInt(textDisplayInterval.getText());
                                                        
                if (radioUseSelectedObjects.isSelected()) {
                    actions.createHeatMap(mapData, textName.getText(), selectedVectorObjects, fields, colorRamp, displayInterval, heatMapKey);
                } else {
                    actions.createHeatMap(mapData, textName.getText(), mapObjects, fields, colorRamp, displayInterval, heatMapKey);
                }    
                
                
            } else {
                //update values in an existing heatmap
                heatMap.setName(textName.getText());
                
                //update selected fields
                Object[] objects = this.listVariable.getSelectedValues();
                String[] fields = new String[objects.length];       
                
                for (int i = 0; i < objects.length; i++) 
                    fields[i] = (String) objects[i];                
                
                //Add Color Ramp to Theme
                mapData.getTheme().addColorRamp(getColorRamp());

                //Set the ColorRamp Info on the HeatMap Layer
                heatMap.setColorRampID(getColorRamp().getID());
                heatMap.setVariables(fields);
                
                //update display interval
                heatMap.setDisplayInterval(Integer.parseInt(textDisplayInterval.getText()));
                
                //update objects
                if (radioUseAllObject.isSelected()) {
                    //User updates the objects to be used
                    heatMap.setMapObjects(mapObjects);
                }
                
                heatMap.getHeatMapKey().setHorizontal(radioHorizontal.isSelected());
                heatMap.getHeatMapKey().setPositionReference(getKeyPosition());
            }
        } else if (actionEvent.equals("Remove")) {
            heatMapValueTableModel.removeRow(tableObjectValues.getSelectedRow());
        }
    }    
    
    /**
     * Adds objects to the main window and sets up the layout
     */
    private void addObjectsToFrame(boolean numericalValues) {
        this.removeAll();
        panelOptions.removeAll();
        
        //center panel
        this.add(panelOptions);

        //objects to use
        panelObjectsToUseButtons.add(radioUseAllObject);
        panelObjectsToUseButtons.add(radioUseSelectedObjects);
        objectsToUseButtonGroup.add(radioUseAllObject);
        objectsToUseButtonGroup.add(radioUseSelectedObjects);

        //options panel
        panelOptions.add(labelName);
        panelOptions.add(textName);     
        panelOptions.add(labelObjectsToUse);
        panelOptions.add(panelObjectsToUseButtons);  
        panelOptions.add(labelKeyPosition); 
        panelOptions.add(comboKeyPosition);        
        panelOptions.add(labelKeyOrientation);
        panelOptions.add(panelKeyOrientation);        
        panelOptions.add(labelVariable);
        panelOptions.add(spaneListVariables);
        panelOptions.add(labelDisplayInterval);
        panelOptions.add(textDisplayInterval);        
        
        if (numericalValues) {
            panelGradientStyle.add(comboGradientStyle);
            panelOptions.add(labelMaxValue);
            panelOptions.add(textMaxValue);
            panelOptions.add(labelMinValue);
            panelOptions.add(textMinValue);
            panelOptions.add(labelGradientStyle);
            panelOptions.add(panelGradientStyle);   
        }
        
        panelOptions.add(labelTransparency);
        panelOptions.add(sliderTransparency);

                       
        //Heatmap Key Orientation
        keyOrientationButtonGroup.add(radioHorizontal);
        keyOrientationButtonGroup.add(radioVertical);
        panelKeyOrientation.add(radioHorizontal);
        panelKeyOrientation.add(radioVertical);
        
        if (numericalValues) {
            SpringUtilities.makeCompactGrid(panelOptions, 10, 2, 3, 3, 10, 10);   
            SpringUtilities.makeCompactGrid(this,         1,  1, 3, 3, 10, 10);
        } else {            
            this.add(spaneStringValues);
            SpringUtilities.makeCompactGrid(panelOptions, 7, 2, 3, 3, 10, 10);            
            SpringUtilities.makeCompactGrid(this,         2, 1, 3, 3, 10, 10);
        }
        
        this.validate();
        this.repaint();
    }    
        
    /**
     * Initializes all displayable objects
     */
    private void init() {
        try {
            customDataFields = mapData.getAllCustomDataFields();
            customDataFields.add("Altitude");
            Collections.sort(customDataFields);
            
            comboGradientStyle           = new ColorGradientComboBox();
            comboKeyPosition             = new JComboBox(keyPositions);
            labelGradientStyle           = new JLabel("Style");
            heatMapValueTableModel       = new HeatMapValueTableModel(this, customDataFields);    
            keyOrientationButtonGroup    = new ButtonGroup(); 
            labelDisplayInterval         = new JLabel("Display Interval (ms)");
            labelKeyPosition             = new JLabel("Key Position");
            labelKeyOrientation          = new JLabel("Key Orientation");
            labelMaxValue                = new JLabel("Max Value");
            labelMinValue                = new JLabel("Min Value");
            labelName                    = new JLabel("Name");
            labelObjectsToUse            = new JLabel("Objects To Use");
            labelTransparency            = new JLabel("Transparency");
            labelVariable                = new JLabel("Variable");   
            listVariable                 = new JList(customDataFields.toArray());
            tableObjectValues            = new JTable(heatMapValueTableModel);
            panelGradientStyle           = new JPanel(new FlowLayout(FlowLayout.CENTER));
            panelObjectsToUseButtons     = new JPanel(new GridLayout(1, 2));
            panelOptions                 = new JPanel(new SpringLayout());
            panelKeyOrientation          = new JPanel(new GridLayout(1, 2));
            objectsToUseButtonGroup      = new ButtonGroup();      
            radioHorizontal              = new JRadioButton("Horizontal", true);
            radioVertical                = new JRadioButton("Vertical");
            radioUseAllObject            = new JRadioButton("All Objects");
            radioUseSelectedObjects      = new JRadioButton("Selected objects");
            sliderTransparency           = new JSlider(0, 255, 180);
            spaneStringValues            = new JScrollPane(tableObjectValues);
            spaneListVariables           = new JScrollPane(listVariable);
            textDisplayInterval          = new JTextField("500");
            textMaxValue                 = new JTextField(Double.toString(mapData.getMaximumFieldValue(customDataFields.get(0))));
            textMinValue                 = new JTextField(Double.toString(mapData.getMinimumFieldValue(customDataFields.get(0))));
            textName                     = new JTextField("HeatMap");
            
            this.setLayout(new SpringLayout());
            radioUseAllObject.addActionListener(this);
            radioUseSelectedObjects.addActionListener(this);
            listVariable.addListSelectionListener(this);
            listVariable.setSelectedIndex(0);
            
            labelDisplayInterval.setEnabled(false);
            textDisplayInterval.setEnabled(false);
            
            tableObjectValues.setDefaultRenderer(Component.class, new CellEditorRenderer());
            tableObjectValues.setDefaultEditor(Component.class,   new CellEditorRenderer());
            tableObjectValues.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            tableObjectValues.getColumnModel().getColumn(0).setPreferredWidth(220);
            tableObjectValues.getColumnModel().getColumn(1).setPreferredWidth(80);
            tableObjectValues.getColumnModel().getColumn(2).setPreferredWidth(70);            
                        
            spaneStringValues.setPreferredSize( new Dimension(245, 180));
            spaneListVariables.setMinimumSize(  new Dimension(245, 90));
            spaneListVariables.setMaximumSize(  new Dimension(245, 90));
            spaneListVariables.setPreferredSize(new Dimension(245, 90));
            textDisplayInterval.setMaximumSize( new Dimension(250, 25));
            textMaxValue.setMaximumSize(new Dimension(250, 25));
            textMinValue.setMaximumSize(new Dimension(250, 25));
            textName.setMaximumSize(new Dimension(250, 25));
            panelObjectsToUseButtons.setMaximumSize(new Dimension(250, 30));
            sliderTransparency.setMaximumSize(new Dimension(250, 30));
            panelGradientStyle.setMaximumSize(new Dimension(250, 30));        
            
            comboKeyPosition.addItemListener(this);
            labelKeyOrientation.setEnabled(false);
            radioHorizontal.setEnabled(false);
            radioVertical.setEnabled(false);
                
            //Set the option buttons of what objects to use, baised on selected objects.
            if (selectedVectorObjects.size() > 1) {
                radioUseAllObject.setSelected(false);
                radioUseSelectedObjects.setSelected(true);
            } else {
                radioUseAllObject.setSelected(true);
                radioUseSelectedObjects.setSelected(false);
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in HeatMapProperties.init() - " + e);
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
        } else {
            updateMinMax();
        }
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
            max            = Float.parseFloat(textMaxValue.getText());
            min            = Float.parseFloat(textMinValue.getText());
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
            Logger.log(Logger.ERR, "Error in HeatMapProperties.getColorFromGradient(float) - " + e);            
        }                
        
        return color;
    }
    
    public ArrayList<Color> getColors() {
        ArrayList<Color> colors;
        boolean          areNumericalValues;
        Color            color;
        int              red, blue, green;
                
        colors = new ArrayList<Color>();
        
        //test to see if field value are numbers or not
        if (getValuesAsNumbers(getFieldValues()) != null) {
            areNumericalValues = true;
        } else {
            areNumericalValues = false;
        }        
        
        if (areNumericalValues) {
            for (int i: comboGradientStyle.getSelectedGradientPixelData()) {
                red   = (i & 0x00ff0000) >> 16;
                green = (i & 0x0000ff00) >> 8;
                blue  = (i & 0x000000ff);;
                color = new Color(red, green, blue);  
                colors.add(color);
            }                
        } else {
            for (int i = 0; i < getFieldValues().size(); i++) {
                colors.add(heatMapValueTableModel.getColorValues().get(i));
            }           
        }        
        
        return colors;
    }
    
    /**
     * Gets the field values to be used when making the HeatMap.
     * 
     * @return 
     */
    public ArrayList<String> getFieldValues() {
        ArrayList<String>   currentValues, fieldValues;
        Object[]            objects;
        
        fieldValues = new ArrayList<String>();
        objects     = listVariable.getSelectedValues(); 
        
        if (radioUseSelectedObjects.isSelected()) {
            for (Object o: objects) {
                currentValues = selectedVectorObjects.getCustomDataFieldValue((String) o);
                for (String s: currentValues) {
                    if (!fieldValues.contains(s))
                        fieldValues.add(s); 
                }                
            }
        } else {
            for (Object o: objects) {
                currentValues = mapData.getCustomDataFieldValue((String) o);
                for (String s: currentValues) {
                    if (!fieldValues.contains(s))
                        fieldValues.add(s); 
                } 
            }
        }   
        
        return fieldValues;
    }
    
    /**
     * Returns the Labels to be used in the HeatMapKey.
     * 
     * @return 
     */
    public ArrayList<String> getKeyLabels() {
        ArrayList<String> labels;
        ArrayList<Float>  values;
                
        values = getValuesAsNumbers(getFieldValues());
        
        if (values != null) {
            labels = new ArrayList<String>();
            Collections.sort(values);
            
            labels.add(values.get(0).toString());   
            labels.add(values.get(values.size() / 2).toString()); 
            labels.add(values.get(values.size() - 1).toString());                       
        } else {
            labels = getFieldValues();
        }
        
        return labels;
    }    
    
    /**
     * Returns the value from the comboKeyPosition as an int representing
     * the chosen position.
     * 
     * @return 
     */
    public int getKeyPosition() {
        if (comboKeyPosition.getSelectedItem().equals("No Key")) {
            return HeatMapKey.NONE;
        } else if (comboKeyPosition.getSelectedItem().equals("Top Left")) {
            return HeatMapKey.TOP_LEFT;
        } else if (comboKeyPosition.getSelectedItem().equals("Top Right")) {
            return HeatMapKey.TOP_RIGHT;
        } else if (comboKeyPosition.getSelectedItem().equals("Bottom Left")) {
            return HeatMapKey.BOTTOM_LEFT;
        } else if (comboKeyPosition.getSelectedItem().equals("Bottom Right")) {
            return HeatMapKey.BOTTOM_RIGHT;
        } else {
            return -1;
        }
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
    
    public ColorRamp getColorRamp() {
        ArrayList<String>       fieldValues;
        boolean                 addPair, areNumericalValues;
        Color                   color;
        ColorRamp               colorRamp;
        int                     numberOfValues;                    
       
        fieldValues = getFieldValues();   
               
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
     * Generates and returns a HashMap representing values and the color
     * that should be assigned to that value.  This is for use in the 
     * CreateHeatMap action.
     * 
     * @return 
     */
    public HashMap<String, Color> getValueColorHashMap() {
        ArrayList<String>       fieldValues;
        boolean                 areNumericalValues;
        Color                   color;
        HashMap<String, Color>  valueColorSet;
        int                     numberOfValues;                    
       
        fieldValues   = getFieldValues();   
               
        //test to see if field value are numbers or not
        if (getValuesAsNumbers(fieldValues) != null) {
            areNumericalValues = true;
        } else {
            areNumericalValues = false;
        }
                    
        //Create the HashMap with the initial capacity equal to the number of field values
        numberOfValues = fieldValues.size();
        valueColorSet  = new HashMap<String, Color>(numberOfValues);
                
        //Load the HashMap
        for (String value: fieldValues) {
            if (areNumericalValues) {
                if (!value.equals("")) {
                    color = getColorFromGradient(Float.parseFloat(value));                
                } else {
                    //make blanks transparent
                    color = new Color(0,0,0,0);
                }
            } else {
                color = heatMapValueTableModel.getColorForValue(value);                
            }
            
            valueColorSet.put(value, color);
        }
        
        return valueColorSet;
    }

    /**
     * Updates the min and max text fields.
     */
    private void updateMinMax() {
        ArrayList<Float>    numbers;
        ArrayList<String>   currentValues, fieldValues;
        Object[]            objects;
        String              selectedField;
        
        try {
            objects = listVariable.getSelectedValues();

            if (objects.length == 1) {
                selectedField = (String) listVariable.getSelectedValue();        

                fieldValues = getFieldValues();                   
                numbers     = getValuesAsNumbers(fieldValues);

                //turn off display interval
                labelDisplayInterval.setEnabled(false);
                textDisplayInterval.setEnabled(false);            
            } else {
                fieldValues = new ArrayList<String>();

                for (Object o: objects) {
                    selectedField = (String) o;
                    currentValues = getFieldValues();

                    for (String s: currentValues) {
                        if (!fieldValues.contains(s))
                            fieldValues.add(s);
                    }
                }    

                numbers = getValuesAsNumbers(fieldValues);

                //turn on display interval
                labelDisplayInterval.setEnabled(true);
                textDisplayInterval.setEnabled(true);            
            }

            if (numbers != null && numbers.size() > 0) {          
                areNumbers = true;
                Collections.sort(numbers);                
                textMinValue.setText(numbers.get(0).toString());
                
                if (numbers.size() > 1) {
                    textMaxValue.setText(numbers.get(numbers.size() - 1).toString());
                } else {
                    textMinValue.setText(numbers.get(0).toString());
                }
                
                addObjectsToFrame(true);
            } else {
                areNumbers = false;
                Collections.sort(fieldValues);            
                heatMapValueTableModel.setTableData(fieldValues); 
                addObjectsToFrame(false);
            }       
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in HeatMapProperties.updateMinMax() - " + e);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent lse) {                
        updateMinMax();
    }
}
