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

import javax.swing.JPanel;

/**
 *
 * @author Alec
 */
public abstract class WizardPanePanel extends JPanel {
    protected boolean   permitAdvance; 
    
    /*
     * Called when the panel is to be displayed
     */
    public abstract void displayPanel();

    /**
     * Returns if the WizardPane using this panel can advance.
     * @return 
     */
    public boolean canAdvance() {
        return permitAdvance;
    }    
}
