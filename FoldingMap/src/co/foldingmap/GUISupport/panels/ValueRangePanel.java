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

import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Alec
 */
public class ValueRangePanel extends JPanel {
    private JLabel                          labelMaxValue, labelMinValue;
    private JTextField                      textMaxValue, textMinValue;
    
    public ValueRangePanel() {
        init();
    }
    
    public double getMaximum() {
        return Double.parseDouble(textMaxValue.getText());
    }
    
    public double getMinimum() {
        return Double.parseDouble(textMinValue.getText());
    }    
    
    private void init() {
        labelMaxValue                = new JLabel("Max Value");
        labelMinValue                = new JLabel("Min Value");         
        textMaxValue                 = new JTextField();
        textMinValue                 = new JTextField();      
        
        textMaxValue.setMaximumSize(new Dimension(100, 25));
        textMinValue.setMaximumSize(new Dimension(100, 25));        
        
        this.setLayout(new GridLayout(2,2));
        this.add(labelMinValue);
        this.add(labelMaxValue);
        this.add(textMinValue);        
        this.add(textMaxValue);        
    }
    
    public void setMaximum(double max) {
        textMaxValue.setText(Double.toString(max));
    }
    
    public void setMinimum(double min) {
        textMinValue.setText(Double.toString(min));
    }
    
    public void setRange(double min, double max) {       
        textMinValue.setText(Double.toString(min));
        textMaxValue.setText(Double.toString(max));
    }    
    
    public void setRange(String min, String max) {       
        textMinValue.setText(min);
        textMaxValue.setText(max);
    }
}
