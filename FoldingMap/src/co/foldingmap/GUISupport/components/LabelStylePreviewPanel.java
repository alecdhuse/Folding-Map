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

import co.foldingmap.map.themes.LabelStyle;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import javax.swing.JColorChooser;

/**
 *
 * @author Alec
 */
public class LabelStylePreviewPanel extends StyleColorPreviewPanel {
    protected LabelStyle labelStyle;
    protected String     labelText;
    
    public LabelStylePreviewPanel() {
        init();
        addActionListeners2();
        
        labelText    = "Main St";
        labelStyle   = new LabelStyle(Color.BLACK);        
    }
    
    protected final void addActionListeners2() {
        this.addMouseListener(this);
        innerPanel.addMouseListener(this);        
    }        
    
    @Override
    public void mouseClicked(MouseEvent me) {
        if (me.getSource() == this) {
            labelStyle.setOutlineColor(JColorChooser.showDialog(this, "Outline Color", outlineColor));
            this.setBackground(labelStyle.getOutlineColor());
        } else if (me.getSource() == innerPanel) {  
            labelStyle.setFillColor(JColorChooser.showDialog(this, "Fill Color", fillColor));
        }
        
        fireActionListeners(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Color Change"));
    }    
    
    /**
     * Overrides the paint method to draw the label preview text in this JPanel.
     * 
     * @param g 
     */
    @Override
    public void paint(Graphics g) {
        Graphics2D  g2;
        int         x, y;
        
        super.paintComponent(g); // clears the panel  
        
        if (labelStyle != null && this.isEnabled()) {
            g2 = (Graphics2D) g;

            if (labelStyle.getFont() != null) 
                g2.setFont(labelStyle.getFont());

            //Get text bounds
            FontMetrics fontMetrics = g2.getFontMetrics();        
            Rectangle2D textBounds  = fontMetrics.getStringBounds(labelText, g);

            //Find the x and y to center the text
            y = (int) ((this.getHeight() / 2.0) - (textBounds.getHeight() / 2.0));
            x = (int) ((this.getWidth()  / 2.0) - (textBounds.getWidth()  / 2.0));                        

            //draw outline
            if (labelStyle.getOutlineColor() != null) {
                g2.setColor(labelStyle.getOutlineColor());   
                g2.drawString(labelText, (x - 1), (y + 0));  
                g2.drawString(labelText, (x + 1), (y - 0));  
                g2.drawString(labelText, (x - 0), (y + 1));  
                g2.drawString(labelText, (x + 0), (y - 1));  
            }        

            g2.setColor(labelStyle.getFillColor());  
            g2.drawString(labelText, x ,y);
        }
    }    
    
    /**
     * Sets the labelStyle used to show the label preview
     * @param ls 
     */
    public void setLabelStyle(LabelStyle ls) {
        if (ls != null) {
            this.labelStyle   = ls;
            this.outlineColor = ls.getOutlineColor();
            this.setBackground(ls.getOutlineColor());
            this.setEnabled(true);
        } else {
            this.setEnabled(false);
        }
    }
}
