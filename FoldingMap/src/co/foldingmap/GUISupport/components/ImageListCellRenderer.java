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

import java.awt.Color;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * List Cell Renderer for displaying images.
 * 
 * @author Alec
 */
public class ImageListCellRenderer extends JLabel implements ListCellRenderer {
    protected ImageIcon[]     displayImages;
    
    public ImageListCellRenderer(ImageIcon[] displayImages) {
        this.displayImages = displayImages;

        setOpaque(true);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
    }   
    
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        int selectedIndex = ((Integer)value).intValue();

        if (isSelected) {
            this.setBackground(new Color(75, 75, 75));
            this.setForeground(list.getSelectionForeground());
        } else {
            this.setBackground(new Color(75, 75, 75));
            this.setForeground(list.getForeground());
        }

        ImageIcon icon = displayImages[selectedIndex];

        if (icon != null) {
            this.setIcon(icon);
        }

        this.setHorizontalAlignment(ImageListCellRenderer.LEFT);        
        return this;
    }    
}
