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
package co.foldingmap.map.vector;

import co.foldingmap.GUISupport.SpringUtilities;
import co.foldingmap.map.LayerPropertiesPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

/**
 *
 * @author Alec
 */
public class VecorLayerPropertiesPanel extends LayerPropertiesPanel implements ActionListener {
    private JCheckBox   checkUseTimeSpan;
    private JLabel      labelSpacer, labelTimeSpanBegin, labelTimeSpanEnd;
    private JTextField  textTimeSpanBegin, textTimeSpanEnd;
    private VectorLayer vectorLayer;
    
    /**
     * Constructor
     * 
     * @param vectorLayer 
     */
    public VecorLayerPropertiesPanel(VectorLayer vectorLayer) {
        this.vectorLayer = vectorLayer;
        init();
    }    
    
    /**
     * Handles actions for this Panel.
     * 
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(checkUseTimeSpan)) {
            if (checkUseTimeSpan.isSelected()) {
                textTimeSpanBegin.setEnabled(true);
                textTimeSpanEnd.setEnabled(true);
                textTimeSpanBegin.setText(vectorLayer.getEarliestDate().toString());
                textTimeSpanEnd.setText(vectorLayer.getLatestDate().toString());
            } else {
                textTimeSpanBegin.setEnabled(false);
                textTimeSpanEnd.setEnabled(false);
            }
        }
    }  
    
    /**
     * Initiates objects for this Panel.
     * 
     */
    private void init() {
        checkUseTimeSpan    = new JCheckBox("Use a Time Span");
        labelSpacer         = new JLabel(" ");
        labelTimeSpanBegin  = new JLabel("Time Span Begin:");
        labelTimeSpanEnd    = new JLabel("Time Span End:");
        textTimeSpanBegin   = new JTextField(25);
        textTimeSpanEnd     = new JTextField(25);

        this.setLayout(new SpringLayout());
        this.add(checkUseTimeSpan);
        this.add(labelSpacer);
        this.add(labelTimeSpanBegin);
        this.add(textTimeSpanBegin);
        this.add(labelTimeSpanEnd);        
        this.add(textTimeSpanEnd);

        SpringUtilities.makeCompactGrid(this, 3, 2, 2, 2, 5, 5);

        checkUseTimeSpan.addActionListener(this);
        
        if (vectorLayer.hasTimeSpan()) {
            checkUseTimeSpan.setSelected(true);
            textTimeSpanBegin.setEnabled(true);
            textTimeSpanEnd.setEnabled(true);
            textTimeSpanBegin.setText(vectorLayer.getTimeSpanBeginString());
            textTimeSpanEnd.setText(vectorLayer.getTimeSpanEndString());
        } else {
            checkUseTimeSpan.setSelected(false);
            textTimeSpanBegin.setEnabled(false);
            textTimeSpanEnd.setEnabled(false);
        }
    }    
}
