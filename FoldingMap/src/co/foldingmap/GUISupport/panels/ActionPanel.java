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

import java.awt.event.ActionListener;
import javax.swing.JPanel;

/**
 * A panel that implements ActionListener, used for hybrid objects. 
 * 
 * @author Alec
 */
public abstract class ActionPanel extends JPanel implements ActionListener {
    protected ActionListener secondaryActionListener = null;
    
    /**
     * Sets the single secondary ActionListener.  
     * If there is an existing reference, it will be overwritten.
     * 
     * @param al 
     */
    public void setSecondaryActionListener(ActionListener al) {
        secondaryActionListener = al;
    }
}
