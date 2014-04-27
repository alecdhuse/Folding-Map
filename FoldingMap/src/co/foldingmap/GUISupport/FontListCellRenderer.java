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
package co.foldingmap.GUISupport;

import co.foldingmap.map.themes.FontLoader;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * A ListModel that displays font names written using that font.
 * 
 * @author Alec
 */
public class FontListCellRenderer implements ListCellRenderer {
    protected Font[]    fonts;
    protected JLabel[]  listLabels;
    
    /**
     * Default Constructor for FontListCellRenderer.
     */
    public FontListCellRenderer() {
        fonts      = FontLoader.getFonts();
        listLabels = new JLabel[fonts.length];
        
        for (int i = 0; i < fonts.length; i++) {
            listLabels[i] = new JLabel(fonts[i].getName());
            listLabels[i].setFont(fonts[i]);
            listLabels[i].setOpaque(true);
        }
    }    

    @Override
    public Component getListCellRendererComponent(JList jlist, Object o, int i, boolean isSelected, boolean bln1) {
        if (o instanceof Font && i >= 0) {
            JLabel label = listLabels[i];
            
            if (isSelected) {
                label.setBackground(new Color(112, 192, 208));                
            } else {
                label.setBackground(Color.WHITE);
            }
            
            return label;
        } else {
            return new JLabel();
        }
    }
    
}
