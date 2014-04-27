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
package co.foldingmap.GUISupport.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * A class used to display a preview of the fill and outline colors for a map style
 * @author Alec
 */
public class StyleColorPreviewPanel extends JPanel implements MouseListener {
    protected ArrayList<ActionListener> actionListenres;
    protected Border                    outerBorder;
    protected Color                     fillColor, outlineColor;
    protected JPanel                    innerPanel;
    
    public StyleColorPreviewPanel() {
        init();
        addActionListeners();
    }
    
    /**
     * Adds an ActionListener to this Class.
     * 
     * @param al 
     */
    public void addActionListener(ActionListener al) {
        this.actionListenres.add(al);
    }
    
    protected final void addActionListeners() {
        this.addMouseListener(this);
        innerPanel.addMouseListener(this);        
    }
    
    protected final void init() {
        innerPanel      = new JPanel();
        outerBorder     = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        actionListenres = new ArrayList<ActionListener>(1);
                
        this.setBorder(outerBorder);
        this.setPreferredSize(new Dimension(50, 50));
        this.setMaximumSize(new Dimension(50, 50));
        this.setLayout(new BorderLayout());
        
        this.add(innerPanel, BorderLayout.CENTER);        
    }       
    
    protected void fireActionListeners(ActionEvent ae) {
        for (ActionListener al: actionListenres) {
            al.actionPerformed(ae);
        }
    }
    
    /**
     * Returns the Fill Color currently being displayed.
     * 
     * @return 
     */
    public Color getFillColor() {
        return fillColor;
    }
    
    /**
     * Returns the Outline Color currently being displayed. 
     * 
     * @return 
     */
    public Color getOutlineColor() {
        return outlineColor;
    }
    
    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
        innerPanel.setBackground(fillColor); 
    }
    
    public void setOutlineColor(Color outlineColor) {
        this.outlineColor = outlineColor;
        this.setBackground(outlineColor);
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        if (me.getSource() == this) {
            setOutlineColor(JColorChooser.showDialog(this, "Outline Color", outlineColor));
        } else if (me.getSource() == innerPanel) {
            setFillColor(JColorChooser.showDialog(this, "Fill Color", fillColor));            
        }
        
        fireActionListeners(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Color Change"));
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }
}
