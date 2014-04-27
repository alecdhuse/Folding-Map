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
import co.foldingmap.GUISupport.components.RangeSlider;
import co.foldingmap.MainWindow;
import co.foldingmap.map.vector.NetworkLayer;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Alec
 */
public class NetworkLayerPropertiesPanel extends ActionPanel {
    private Dialog          parentDialog;
    private FileChoicePanel textNetworkAddress;
    private JLabel          labelRefreshRate, labelSecondsCaption;
    private JPanel          panelAddress, panelBlank, panelLayerName, panelRefreshRate, panelVisibility;
    private JTextField      textLayerName, textRefreshRate;
    private MainWindow      mainWindow;
    private NetworkLayer    networkLayer;
    private RangeSlider     rangeVisibility;
    
    public NetworkLayerPropertiesPanel(MainWindow   mainWindow, 
                                       Dialog       parentDialog, 
                                       NetworkLayer networkLayer) {
        
        this.mainWindow   = mainWindow;
        this.parentDialog = parentDialog;
        this.networkLayer = networkLayer;
        
        init();
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        String actionEvent = ae.getActionCommand();
        
        if (actionEvent.equalsIgnoreCase("Ok")) {
            //Save field info to the NetworkLayer.
            networkLayer.setAddress(textNetworkAddress.getText());
            networkLayer.setName(textLayerName.getText());
            networkLayer.setRefreshInterval(Float.parseFloat(textRefreshRate.getText()));                        
            networkLayer.setVisibility(rangeVisibility.getLowValue(), rangeVisibility.getHighValue());
            networkLayer.setParentMap(mainWindow.getMapPanel().getMap());
            
            networkLayer.updateData();
            mainWindow.updateLayersTree();            
        }
        
        if (secondaryActionListener != null)
            secondaryActionListener.actionPerformed(ae);        
    }
 
    private void init() {        
        labelRefreshRate    = new JLabel("Refresh Rate");
        labelSecondsCaption = new JLabel("Seconds");
        panelAddress        = new JPanel(new BorderLayout());
        panelBlank          = new JPanel();
        panelLayerName      = new JPanel(new BorderLayout());
        panelRefreshRate    = new JPanel();
        panelVisibility     = new JPanel(new BorderLayout());
        rangeVisibility     = new RangeSlider(0, 23);
        textLayerName       = new JTextField("", 35);
        textNetworkAddress  = new FileChoicePanel(parentDialog);
        textRefreshRate     = new JTextField("300", 6);
        
        textRefreshRate.setMaximumSize(new Dimension(500, 28));
        panelAddress.setMaximumSize(new Dimension(520, 40));
        panelRefreshRate.setMaximumSize(new Dimension(520, 40));
        
        this.setLayout(new SpringLayout());
        this.add(panelLayerName);
        this.add(panelAddress);
        this.add(panelVisibility);
        this.add(panelRefreshRate);
        this.add(panelBlank);
        
        panelLayerName.add(textLayerName,    BorderLayout.NORTH);
        panelVisibility.add(rangeVisibility, BorderLayout.CENTER);
        
        panelAddress.setBorder(new TitledBorder("File or Address"));
        panelLayerName.setBorder(new TitledBorder("Name"));
        panelRefreshRate.setBorder(new TitledBorder("Refresh Options"));
        panelRefreshRate.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelVisibility.setBorder(new TitledBorder("Visibility Options"));
                
        panelAddress.add(textNetworkAddress, BorderLayout.CENTER);
        panelRefreshRate.add(labelRefreshRate );
        panelRefreshRate.add(textRefreshRate);
        panelRefreshRate.add(labelSecondsCaption);
        textRefreshRate.setHorizontalAlignment(JTextField.RIGHT);
        
        SpringUtilities.makeCompactGrid(this, 5, 1, 2, 2, 5, 5); 
        
        //Load Network Layer Info
        if (networkLayer != null) {
            textNetworkAddress.setText(networkLayer.getAddress());
            textLayerName.setText(networkLayer.getName());
            textRefreshRate.setText(Float.toString(networkLayer.getRefreshInterval()));    
            rangeVisibility.setLowValue( (int) networkLayer.getVisibility().getMinTileZoomLevel());
            rangeVisibility.setHighValue((int) networkLayer.getVisibility().getMaxTileZoomLevel());
        }
    }
}
