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
import co.foldingmap.map.vector.LatLonAltBox;
import co.foldingmap.map.vector.LatLonBox;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;

/**
 * A Panel for entering and displaying the boundaries of a map or portion 
 * of a map.
 * 
 * @author Alec
 */
public class BoundaryPanel extends JPanel implements KeyListener {
    public static final int HORIZONTAL  = 0;
    public static final int VERTICAL    = 1;
    public static final int BORDERS     = 2;
    
    protected GridBagLayout gridBagLayout;
    protected LatLonBox     boundaries;
    protected JLabel        northLabel, southLabel, eastLabel, westLabel;
    protected JTextField    northText, southText, eastText, westText;    
    
    public BoundaryPanel(int orientation) {
        this.boundaries = new LatLonAltBox(90, -90, 180, -180, 0, 0);
        init();
        
        if (orientation == HORIZONTAL)  {
            setupHorizontal();
        } else if (orientation == VERTICAL)  {
            setupVertical();
        } else {
            setupBorders();
        }
    }    
    
    public BoundaryPanel(LatLonBox boundaries, int orientation) {
        this.boundaries = boundaries;
        init();
                        
        if (orientation == HORIZONTAL)  {
            setupHorizontal();
        } else if (orientation == VERTICAL)  {
            setupVertical();
        } else {
            setupBorders();
        }
    }     
    
    /**
     * Returns a LatLonAltBox set to the boundaries from this panel.
     * 
     * @return 
     */
    public LatLonBox getLatLonBox() {
        return this.boundaries;
    }    
    
    /**
     * Returns a LatLonAltBox set to the boundaries from this panel.
     * 
     * @return 
     */
    public LatLonAltBox getLatLonAltBox() {
        return new LatLonAltBox(this.boundaries);
    }        
    
    /**
     * Initiate the components for this panel.
     */
    protected final void init() {
        gridBagLayout = new GridBagLayout();
                
        northLabel    = new JLabel("North", SwingConstants.CENTER);
        southLabel    = new JLabel("South", SwingConstants.CENTER);
        eastLabel     = new JLabel("East",  SwingConstants.CENTER);
        westLabel     = new JLabel("West",  SwingConstants.CENTER);
        
        northText     = new JTextField(Float.toString(boundaries.getNorth()));
        southText     = new JTextField(Float.toString(boundaries.getSouth()));
        eastText      = new JTextField(Float.toString(boundaries.getEast()));
        westText      = new JTextField(Float.toString(boundaries.getWest()));
        
        northText.setPreferredSize(new Dimension(20, 15));
        southText.setPreferredSize(new Dimension(20, 15));
        eastText.setPreferredSize( new Dimension(20, 15));
        westText.setPreferredSize( new Dimension(20, 15));
        
        northText.addKeyListener(this);
        southText.addKeyListener(this);
        eastText.addKeyListener(this);
        westText.addKeyListener(this); 
    }    
    
    /**
     * Handel keyTyped event.
     * 
     * @param ke 
     */
    @Override
    public void keyTyped(KeyEvent ke) {
        try {
            if (ke.getSource() == northText) {
                this.boundaries.setNorth(Float.parseFloat(northText.getText()));
            } else if (ke.getSource() == southText) {
                this.boundaries.setSouth(Float.parseFloat(southText.getText()));
            } else if (ke.getSource() == eastText) {
                this.boundaries.setEast(Float.parseFloat(eastText.getText()));
            } else if (ke.getSource() == westText) {
                this.boundaries.setWest(Float.parseFloat(westText.getText()));
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in BoundaryPanel.keyTyped(KeyEvent) - " + e);
        }
    }    
    
    @Override
    public void keyPressed(KeyEvent ke) {}

    @Override
    public void keyReleased(KeyEvent ke) {}      
    
    /**
     * Enable or disable this panel.
     * 
     * @param enabled 
     */
    @Override
    public void setEnabled(boolean enabled) {
        northText.setEnabled(enabled);
        southText.setEnabled(enabled);
        eastText.setEnabled(enabled);
        westText.setEnabled(enabled);
    }
    
    /**
     * Set the boundary text fields to the values of the passed LatLonAltBox.
     * 
     * @param bounds 
     */
    public void setLatLonAltBox(LatLonAltBox bounds) {
        this.boundaries = bounds;
        updateBoundaryTexts();
    }   
    
    /**
     * Sets up the panel in a Borders Orientation
     */
    protected final void setupBorders() {
        JPanel  north, south, east, west;
        
        north = new JPanel(new GridLayout(2, 1));
        south = new JPanel(new GridLayout(2, 1));
        east  = new JPanel(new GridLayout(2, 1));
        west  = new JPanel(new GridLayout(2, 1));

        north.add(northLabel);
        north.add(northText);
        south.add(southLabel);
        south.add(southText);
        east.add(eastLabel);
        east.add(eastText);        
        west.add(westLabel);
        west.add(westText);
    }
    
    /**
     * Sets up the panel in a Horizontal Orientation
     */
    protected final void setupHorizontal() {
        GridBagConstraints  gridBagConstraints = new GridBagConstraints();
                        
        this.setLayout(gridBagLayout);        
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;  
        gridBagConstraints.ipadx = 70;
        gridBagConstraints.ipady = 10;
        this.add(northLabel, gridBagConstraints);
        
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;          
        this.add(southLabel, gridBagConstraints);
        
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;          
        this.add(eastLabel, gridBagConstraints);
        
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;          
        this.add(westLabel, gridBagConstraints);
                
        
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;    
        gridBagConstraints.ipady = 18;
        this.add(northText, gridBagConstraints);
        
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;         
        this.add(southText, gridBagConstraints);
        
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;         
        this.add(eastText, gridBagConstraints);
        
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;         
        this.add(westText, gridBagConstraints);      
    }
    
    /**
     * Setup this panel in a vertical Orientation
     */
    protected final void setupVertical() {
        this.setLayout(new SpringLayout());
        
        this.add(northLabel);
        this.add(northText);
        
        this.add(southLabel);
        this.add(southText);
        
        this.add(eastLabel);
        this.add(eastText);
        
        this.add(westLabel);                                
        this.add(westText);
        
        SpringUtilities.makeCompactGrid(this, 4, 2, 3, 3, 4, 10);        
    } 
    
    /**
     * Updates the text fields on this panel with this classes field values.
     */
    private void updateBoundaryTexts() {
        northText.setText(Float.toString(boundaries.getNorth()));
        southText.setText(Float.toString(boundaries.getSouth()));
        eastText.setText(Float.toString(boundaries.getEast()));
        westText.setText(Float.toString(boundaries.getWest()));
    }    
}
