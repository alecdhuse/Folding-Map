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

import co.foldingmap.map.themes.PolygonStyle;
import co.foldingmap.map.themes.ColorHelper;
import co.foldingmap.map.themes.MapTheme;
import co.foldingmap.map.themes.ThemeConstants;
import co.foldingmap.map.themes.ColorStyle;
import co.foldingmap.map.themes.LabelStyle;
import co.foldingmap.map.themes.OutlineStyle;
import co.foldingmap.map.themes.IconStyle;
import co.foldingmap.map.themes.LineStyle;
import co.foldingmap.GUISupport.SpringUtilities;
import co.foldingmap.GUISupport.components.LabelStylePreviewPanel;
import co.foldingmap.GUISupport.components.StyleColorPreviewPanel;
import co.foldingmap.Logger;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;

/**
 *
 * @author Alec
 */
public class ColorStylePanel extends JPanel implements ActionListener, ItemListener {
    private ColorStyle             colorStyle;
    private DefaultComboBoxModel   comboBoxModelFont, comboBoxModelStroke;    
    private DefaultListModel       listModel;
    private JButton                buttonAddOutline;
    private JCheckBox              checkFill, checkLabel, checkOutline;
    private JComboBox              comboFont, comboStrokeStyle;
    private JLabel                 labelFillColor, labelFont, labelFontType, labelFontSize;
    private JLabel                 labelSelectedColors, labelStandardColors, labelStrokeStyle;
    private JLabel                 labelOutlineColor, labelOutlineCondition, labelOutlineSize, labelStyle;
    private JList                  outlineStyleList;
    private JPanel                 styleColorsPanel;
    private JPanel                 fillPanel, labelPanel, outlinePanel, outlineInnerPanel, outlineStylePanel;
    private JSpinner               spinnerFontSize, spinnerWidth;
    private JTabbedPane            styleTabs;
    private JTextField             textLabelColor, textLabelOutlineColor;
    private JTextField             textFillColor, textFont, textOutlineColor, textOutlineCondition;
    private LabelStylePreviewPanel labelStylePanel;
    private MapTheme               theme;
    private StyleColorPreviewPanel styleColor, selectedStyleColor;
    
    public ColorStylePanel(MapTheme theme) {
        this.theme = theme;
        
        init();
    }    
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == checkFill) {
            colorStyle.setFill(checkFill.isSelected());        
        } else if (ae.getSource() == checkLabel) {            
            if (checkLabel.isSelected()) {
                //Add default Label Style
                colorStyle.setLabel(MapTheme.DEFAULT_LABEL);
            } else {
                //remove Label Style
                colorStyle.setLabel(null);
            }
            
            updateLabelControles(colorStyle.getLabel());
        } else if (ae.getSource() == styleColor) {
            colorStyle.setFillColor(styleColor.getFillColor());
            colorStyle.setOutlineColor(styleColor.getOutlineColor());
        } else if (ae.getSource() == selectedStyleColor) {
            colorStyle.setSelectedFillColor(selectedStyleColor.getFillColor());
            colorStyle.setSelectedOutlineColor(selectedStyleColor.getOutlineColor());            
        }
    }    
    
    private void buildLineStylePanel() {
        styleTabs.removeAll();
        styleTabs.add(outlineInnerPanel, "Style");
        styleTabs.add(labelPanel,        "Label");  
        styleTabs.revalidate();        
        
        outlineInnerPanel.removeAll();                
        outlineInnerPanel.add(checkOutline);
        outlineInnerPanel.add(textOutlineColor);
        outlineInnerPanel.add(labelFillColor);
        outlineInnerPanel.add(textFillColor);        
        outlineInnerPanel.add(labelOutlineSize);
        outlineInnerPanel.add(spinnerWidth);
        outlineInnerPanel.add(labelStrokeStyle);
        outlineInnerPanel.add(comboStrokeStyle);       
        
        SpringUtilities.makeCompactGrid(outlineInnerPanel, 4, 2, 2, 2, 2, 2);                       
    }
    
    private void buildPointStylePanel() {
        styleTabs.removeAll();
        styleTabs.add(outlineInnerPanel, "Style");
        styleTabs.add(labelPanel,        "Label");  
        styleTabs.revalidate();        
        
        outlineInnerPanel.removeAll();                
        outlineInnerPanel.add(checkOutline);
        outlineInnerPanel.add(textOutlineColor);
        outlineInnerPanel.add(labelFillColor);
        outlineInnerPanel.add(textFillColor);           
        
        SpringUtilities.makeCompactGrid(outlineInnerPanel, 2, 2, 2, 2, 2, 2);                       
    }    
    
    private void buildPoylStylePanel() {
        styleTabs.removeAll();
        styleTabs.add(fillPanel,    "Fill");
        styleTabs.add(outlinePanel, "Outline");
        styleTabs.add(labelPanel,   "Label");         
        styleTabs.revalidate();
        
        outlineStylePanel.removeAll();
        outlineStyleList.setMinimumSize(new Dimension(50, 30));
        outlineStylePanel.add(outlineStyleList, BorderLayout.CENTER);
        outlineStylePanel.add(buttonAddOutline, BorderLayout.SOUTH);
        
        outlinePanel.add(outlineStylePanel, BorderLayout.WEST);
        outlinePanel.add(outlineInnerPanel, BorderLayout.CENTER);        
        
        outlineInnerPanel.removeAll();        
        outlineInnerPanel.add(labelOutlineCondition);
        outlineInnerPanel.add(textOutlineCondition);
        outlineInnerPanel.add(checkOutline);
        outlineInnerPanel.add(textOutlineColor);
        outlineInnerPanel.add(labelOutlineSize);
        outlineInnerPanel.add(spinnerWidth);
        outlineInnerPanel.add(labelStrokeStyle);
        outlineInnerPanel.add(comboStrokeStyle);       
        
        SpringUtilities.makeCompactGrid(outlineInnerPanel, 4, 2, 2, 2, 4, 4);                
    }    
    
    private void init() {
        buttonAddOutline        = new JButton("Add");
        checkFill               = new JCheckBox("Fill");
        checkLabel              = new JCheckBox("Label");
        checkOutline            = new JCheckBox("Outline");
//        comboBoxModelFont       = new DefaultComboBoxModel(FontLoader.getFonts());
        comboBoxModelStroke     = new DefaultComboBoxModel(ThemeConstants.STROKE_STYLES);
//        comboFont               = new JComboBox(comboBoxModelFont);
        comboStrokeStyle        = new JComboBox(comboBoxModelStroke);        
        fillPanel               = new JPanel();
        labelFillColor          = new JLabel("Fill Color");
        labelFont               = new JLabel("Font");
        labelFontSize           = new JLabel("Size");
        labelFontType           = new JLabel("Font");
        labelOutlineColor       = new JLabel("Outline Color");
        labelOutlineCondition   = new JLabel("Condition");
        labelOutlineSize        = new JLabel("Outline Size");
        labelPanel              = new JPanel(new GridLayout(4, 2));
        labelSelectedColors     = new JLabel("Selected Colors");
        labelStandardColors     = new JLabel("Standard Colors");
        labelStrokeStyle        = new JLabel("Stroke Style"); 
        labelStyle              = new JLabel("Label");
        labelStylePanel         = new LabelStylePreviewPanel();
        listModel               = new DefaultListModel();
        outlinePanel            = new JPanel(new BorderLayout());
        outlineInnerPanel       = new JPanel(new SpringLayout());
        outlineStyleList        = new JList(listModel);
        outlineStylePanel       = new JPanel(new BorderLayout());
        styleColor              = new StyleColorPreviewPanel();
        styleColorsPanel        = new JPanel(new SpringLayout());
        selectedStyleColor      = new StyleColorPreviewPanel();
        spinnerFontSize         = new JSpinner();
        spinnerWidth            = new JSpinner();
        styleTabs               = new JTabbedPane();
        textFillColor           = new JTextField();
        textFont                = new JTextField();
        textLabelColor          = new JTextField();
        textLabelOutlineColor   = new JTextField();
        textOutlineColor        = new JTextField();
        textOutlineCondition    = new JTextField();        
        
//        comboFont.setRenderer(new FontListCellRenderer());
//        comboFont.setSelectedIndex(1);
//        comboFont.setEditable(false);
        
        //Add Listeners
        styleColor.addActionListener(this);
        selectedStyleColor.addActionListener(this);
        checkFill.addActionListener(this);
        checkLabel.addActionListener(this);
        comboStrokeStyle.addItemListener(this);
        
        //Add Objects to Panel
        this.setLayout(new BorderLayout());
               
        this.add(styleColorsPanel, BorderLayout.WEST);
        this.add(styleTabs,        BorderLayout.CENTER);
        
        labelPanel.add(checkLabel);
        labelPanel.add(textLabelColor);
        labelPanel.add(labelOutlineColor);
        labelPanel.add(textLabelOutlineColor);
        labelPanel.add(labelFontType);
        labelPanel.add(textFont);        
        labelPanel.add(labelFontSize);
        labelPanel.add(spinnerFontSize);        
        
        styleColorsPanel.add(labelStandardColors);                             
        styleColorsPanel.add(styleColor);
        styleColorsPanel.add(labelSelectedColors);  
        styleColorsPanel.add(selectedStyleColor);
        styleColorsPanel.add(labelStyle);
        styleColorsPanel.add(labelStylePanel);    
        
        SpringUtilities.makeCompactGrid(styleColorsPanel, 3, 2, 2, 2, 5, 5);
    }
    
    @Override
    public void itemStateChanged(ItemEvent ie) {
        //Update Stroke
        if (ie.getSource() == this.comboStrokeStyle) {
            if (colorStyle instanceof PolygonStyle) {
                OutlineStyle ots = ((PolygonStyle) colorStyle).getOutlineStyles().get(outlineStyleList.getSelectedIndex());
                ots.setStrokeStyle((String) comboBoxModelStroke.getSelectedItem());
            } else if (colorStyle instanceof LineStyle) {
                LineStyle ls = ((LineStyle) colorStyle);
                ls.setLineStroke((String) comboBoxModelStroke.getSelectedItem());
            }
        }
    }    
    
    /**
     * Set the Color Style to be displayed and edited in this panel.
     * 
     * @param cs 
     */
    public void setColorStyle(ColorStyle cs) {
        try {
            this.colorStyle = cs;

            String fillString     = (colorStyle.getFillColor()    != null ? ("#" + ColorHelper.getColorHexStandard(colorStyle.getFillColor())) : "");
            String labelString    = (colorStyle.getLabel()        != null ? ("#" + ColorHelper.getColorHexStandard(colorStyle.getLabel().getFillColor())) : "");
            String labelOutString = (colorStyle.getLabel()        != null ? ("#" + ColorHelper.getColorHexStandard(colorStyle.getLabel().getOutlineColor())) : "");
            String outlineString  = (colorStyle.getOutlineColor() != null ? ("#" + ColorHelper.getColorHexStandard(colorStyle.getOutlineColor())) : "");

            if (colorStyle instanceof IconStyle) {
                buildPointStylePanel();
            } else if (colorStyle instanceof PolygonStyle) {
                //Update Outline Styles List
                listModel.removeAllElements();

                for (OutlineStyle ots: colorStyle.getOutlineStyles()) 
                    listModel.addElement(ots); 
                
                if (colorStyle.getOutlineStyles().size() > 0) {
                    OutlineStyle ots = colorStyle.getOutlineStyles().get(0);                
                    spinnerWidth.setValue(ots.getWidth());
                    outlineStyleList.setSelectedIndex(0);
                    textOutlineCondition.setText(ots.getBorderCondition());
                    
                    for (int i = 0; i < listModel.size(); i++) {
                        OutlineStyle iOts = (OutlineStyle) listModel.get(i);
                    
                        if (ots.getStrokeStyle().equalsIgnoreCase(iOts.getStrokeStyle())) {
                            outlineStyleList.setSelectedIndex(i);
                            break;
                        }
                    }
                }
                
                this.buildPoylStylePanel();
                outlineStylePanel.setVisible(true);        
            } else if (colorStyle instanceof LineStyle) {
                LineStyle ls = (LineStyle) colorStyle;
                
                spinnerWidth.setValue(ls.getLineWidth());
                
                //display what the current stroke style is
                for (int i = 0; i < comboBoxModelStroke.getSize(); i++) {
                    String s = (String) comboBoxModelStroke.getElementAt(i);

                    if (ls.getLineStroke().equalsIgnoreCase(s)) {
                        comboBoxModelStroke.setSelectedItem(s);
                        break;
                    }
                }                
                
                this.buildLineStylePanel();
                
            } else {
                outlineStylePanel.setVisible(false);
                this.buildLineStylePanel();
            }

            styleColor.setOutlineColor(colorStyle.getOutlineColor());                       
            styleColor.setFillColor(colorStyle.getFillColor());

            selectedStyleColor.setOutlineColor(colorStyle.getSelectedOutlineColor());
            selectedStyleColor.setFillColor(colorStyle.getSelectedFillColor());                

            textFillColor.setText(fillString);
            textLabelColor.setText(labelString);
            textLabelOutlineColor.setText(labelOutString);
            textOutlineColor.setText(outlineString);

            checkFill.setSelected(colorStyle.isFilled());
            checkOutline.setSelected(colorStyle.isOutlined());

            //Update Label Controls 
            LabelStyle ls = colorStyle.getLabel();

            if (colorStyle instanceof LineStyle || colorStyle instanceof PolygonStyle) {
                labelStylePanel.setBackground(colorStyle.getFillColor());
                updateLabelControles(ls);
            } else {
                labelStylePanel.setBackground(Color.WHITE);
            }  

            labelStylePanel.setLabelStyle(ls);
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in ColorStylePanel.setColorStyle(ColorStyle) - " + e);
        }
    }

    protected final void updateLabelControles(LabelStyle ls) {
        if (ls != null) {                                
            checkLabel.setSelected(true);
            labelFont.setEnabled(true);
            textFont.setEnabled(true);
            labelFontSize.setEnabled(true);
            spinnerFontSize.setEnabled(true);
            spinnerFontSize.setValue(Float.valueOf(ls.getFont().getSize2D()));
            textFont.setText(ls.getFont().getName());
        } else {
            checkLabel.setSelected(false);
            labelFont.setEnabled(false);
            textFont.setEnabled(false);
            labelFontSize.setEnabled(false);
            spinnerFontSize.setEnabled(false);    
            spinnerFontSize.setValue(Float.valueOf(0));
            textFont.setText("");
        }       
                
        labelStylePanel.setLabelStyle(ls);
        
        if (colorStyle instanceof LineStyle || colorStyle instanceof PolygonStyle) {
            labelStylePanel.setBackground(colorStyle.getFillColor());
        } else {
            labelStylePanel.setBackground(Color.WHITE);
        }       
    }
}
