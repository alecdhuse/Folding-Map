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

/**
 *
 * @author Alec
 */
public interface ProgressIndicator {
    
    /**
     * Sets the progress to finished.
     * 
     */
    public void finish();    
    
    /**
     * Resets the Indicator so it can be reused.
     */
    public void reset();
    
    /**
     * Sets the message to be displayed on the Progress Indicator.
     * 
     * @param message 
     */
    public void setMessage(String message);
    
    /**
     * Sets the percent complete of the current operation.
     * 
     * @param value A percent of completeness from 0 to 100
     */
    public void setValue(int value);
    
    /**
     * Sets if the indicator is visible or not.
     * 
     * @param visible 
     */
    public void setVisible(boolean visible);
    
    /**
     * Update the progress with a message and a percent complete.
     * 
     * @param detail
     * @param value 
     */
    public void updateProgress(String detail, int value);
}
