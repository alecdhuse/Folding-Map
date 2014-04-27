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

import co.foldingmap.GUISupport.SpringUtilities;
import co.foldingmap.Logger;
import co.foldingmap.map.MapUtilities;
import co.foldingmap.map.vector.VectorLayer;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Date;
import javax.swing.*;

/**
 *
 * @author Alec
 */
public class VectorLayerPropertiesPanel extends ActionPanel {
    private JCheckBox       checkUseTimeSpan;
    private JLabel          labelDescription, labelName;
    private JLabel          labelSpacer, labelTimeSpanBegin, labelTimeSpanEnd;
    private JScrollPane     spaneDescription;
    private JTextArea       textDescription;
    private JTextField      textName, textTimeSpanBegin, textTimeSpanEnd;
    private VectorLayer     layer;
            
    public VectorLayerPropertiesPanel(VectorLayer layer) {
        this.layer = layer;
        init();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String actionEvent = e.getActionCommand();           
            
            if (e.getSource().equals(checkUseTimeSpan)) {
                if (checkUseTimeSpan.isSelected()) {
                    textTimeSpanBegin.setEnabled(true);
                    textTimeSpanEnd.setEnabled(true);
                    textTimeSpanBegin.setText(layer.getEarliestDate().toString());
                    textTimeSpanEnd.setText(layer.getLatestDate().toString());
                } else {
                    textTimeSpanBegin.setEnabled(false);
                    textTimeSpanEnd.setEnabled(false);
                }
            } else if (actionEvent.equalsIgnoreCase("Ok")) {
                //Action called when the ok button on the windows property dialog is clicked
                layer.setLayerDescription(textDescription.getText());
                layer.setName(textName.getText());
                
                if (checkUseTimeSpan.isSelected()) {
                    layer.setTimeSpanBegin(MapUtilities.parseTimestamp(textTimeSpanBegin.getText()));
                    layer.setTimeSpanEnd(MapUtilities.parseTimestamp(textTimeSpanBegin.getText()));
                } else {
                    layer.setTimeSpanBegin((Date) null);
                    layer.setTimeSpanEnd((Date) null);
                }
            }
            
            if (secondaryActionListener != null)
                secondaryActionListener.actionPerformed(e);
            
        } catch (Exception ex) {
            Logger.log(Logger.ERR, "Error in VectorLayerPropertiesPanel.actionPerformed(ActionEvent) - " + ex);
        }
    }     
    
    private void init() {
        checkUseTimeSpan    = new JCheckBox("Use a Time Span");
        labelDescription    = new JLabel("Description");
        labelName           = new JLabel("Name");
        labelSpacer         = new JLabel(" ");
        labelTimeSpanBegin  = new JLabel("Time Span Begin:");
        labelTimeSpanEnd    = new JLabel("Time Span End:");
        textDescription     = new JTextArea(layer.getDescription());
        textName            = new JTextField(layer.getName(), 25);
        textTimeSpanBegin   = new JTextField(25);
        textTimeSpanEnd     = new JTextField(25); 
        spaneDescription    = new JScrollPane(textDescription);
        
        textDescription.setMaximumSize(new Dimension(250, 180));
        //textDescription.setPreferredSize(new Dimension(240, 180));
        spaneDescription.setMaximumSize(new Dimension(250, 180));    
        //spaneDescription.setPreferredSize(new Dimension(250, 180));  
        textName.setMaximumSize(new Dimension(250, 25));
        textTimeSpanBegin.setMaximumSize(new Dimension(250, 25));
        textTimeSpanEnd.setMaximumSize(new Dimension(250, 25));       
        
        //setup layout
        this.setLayout(new SpringLayout());
        this.add(labelName);
        this.add(textName);
        this.add(labelDescription);
        this.add(spaneDescription);
        this.add(checkUseTimeSpan);
        this.add(labelSpacer);
        this.add(labelTimeSpanBegin);
        this.add(textTimeSpanBegin);
        this.add(labelTimeSpanEnd);        
        this.add(textTimeSpanEnd);        
        
        SpringUtilities.makeCompactGrid(this, 5, 2, 2, 2, 5, 5);        
        
        if (layer.hasTimeSpan()) {
            checkUseTimeSpan.setSelected(true);
            textTimeSpanBegin.setEnabled(true);
            textTimeSpanEnd.setEnabled(true);            
            textTimeSpanBegin.setText(layer.getTimeSpanBeginString());
            textTimeSpanEnd.setText(layer.getTimeSpanEndString());
        } else {
            checkUseTimeSpan.setSelected(false);
            textTimeSpanBegin.setEnabled(false);
            textTimeSpanEnd.setEnabled(false);
        }        
    }
}
