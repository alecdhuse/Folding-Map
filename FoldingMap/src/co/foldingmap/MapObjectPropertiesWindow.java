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
package co.foldingmap;

import co.foldingmap.map.vector.CoordinateList;
import co.foldingmap.map.vector.CoordinateMath;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.LineString;
import co.foldingmap.GUISupport.panels.DefaultExtendedOptionsPanel;
import co.foldingmap.GUISupport.panels.MapObjectCoordinatesPanel;
import co.foldingmap.GUISupport.panels.MapObjectInformationPanel;
import co.foldingmap.GUISupport.panels.OverlayPropertiesPanel;
import co.foldingmap.map.MapObject;
import co.foldingmap.map.Overlay;
import co.foldingmap.map.visualization.GraphData;
import co.foldingmap.map.visualization.LineGraph;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author Alec
 */
public class MapObjectPropertiesWindow extends JDialog implements ActionListener {
    private CoordinateList<Coordinate>  coordinates;
    private DefaultExtendedOptionsPanel dataPanel;
    private JButton                     buttonCancel, buttonOk;
    private JPanel                      panelButtons, panelGraph;
    private JTabbedPane                 mainTabs;
    private LineGraph                   lineGraph;
    private MainWindow                  mainWindow;
    private MapObject                   object;
    private MapObjectCoordinatesPanel   coordinatesPanel;
    private MapObjectInformationPanel   infoPanel;    
    private OverlayPropertiesPanel      overlayPropertiesPanel;
    
    public MapObjectPropertiesWindow(MainWindow mainWindow, MapObject object) {
        super(mainWindow);
        
        this.mainWindow  = mainWindow;
        this.object      = object;
        this.coordinates = object.getCoordinateList();
        
        init();
        setupFrame();
        setupLocation();
        
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == buttonCancel) {
            this.dispose();
        } else if (ae.getSource() == buttonOk) {
            if (infoPanel != null)
                infoPanel.actionPerformed(ae);
            
            if (overlayPropertiesPanel != null)
                overlayPropertiesPanel.actionPerformed(ae);
            
            this.dispose();
        }
    }    
    
    private void init() {
        buttonCancel     = new JButton("Cancel");
        buttonOk         = new JButton("Ok");                        
        mainTabs         = new JTabbedPane();
        panelButtons     = new JPanel(new FlowLayout(FlowLayout.RIGHT));              
        
        if (object instanceof VectorObject) {
            coordinatesPanel = new MapObjectCoordinatesPanel(mainWindow, (VectorObject) object);
            dataPanel        = new DefaultExtendedOptionsPanel((VectorObject) object);
            infoPanel        = new MapObjectInformationPanel(this, object);
            panelGraph       = new JPanel(new BorderLayout());
        } else if (object instanceof Overlay) {
            overlayPropertiesPanel = new OverlayPropertiesPanel(this, (Overlay) object);
        }
    }
    
    private void setupFrame() {
        this.setLayout(new BorderLayout());
        this.add(mainTabs,     BorderLayout.CENTER);
        this.add(panelButtons, BorderLayout.SOUTH);
        
        if (object instanceof VectorObject) {
            mainTabs.add("Information", infoPanel);
            mainTabs.add("Coordinates", coordinatesPanel);
            mainTabs.add("Data",        dataPanel); 

            if (object instanceof LineString) {
                GraphData graphData;

                graphData = new GraphData(CoordinateMath.getDistancesM(coordinates), coordinates.getAltitudes());

                if (graphData.getMaxX() > 2500f) {
                    graphData.adjustXValues(0.001f);
                    graphData.setAxisLabels("Distance (KM)", "Altitude (M)");
                } else {
                    graphData.setAxisLabels("Distance (M)",  "Altitude (M)");
                }

                mainTabs.add("Elevation Graph", panelGraph);
                lineGraph = new LineGraph(graphData);
                panelGraph.add(lineGraph, BorderLayout.CENTER);
            }
        } else if (object instanceof Overlay) {
            mainTabs.add("Information", overlayPropertiesPanel);
        }
        
        this.panelButtons.add(buttonOk);
        this.panelButtons.add(buttonCancel);
        
        buttonCancel.addActionListener(this);
        buttonOk.addActionListener(this);
        buttonOk.setActionCommand("Ok");        
    }
    
    /**
     * Sets up the location of the dialog box.
     */
    private void setupLocation() {        
        Toolkit   tk           = Toolkit.getDefaultToolkit();
        Dimension screenSize   = tk.getScreenSize();
        int       width        = 550;
        int       height       = 500;        
        int       screenHeight = screenSize.height;
        int       screenWidth  = screenSize.width;
        int       x            = (screenWidth  - width)  / 2;
        int       y            = (screenHeight - height) / 2;

        this.setSize(width, height);
        this.setLocation(x, y);
    }

}
