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
package co.foldingmap.map.visualization;

import co.foldingmap.map.Layer;

/**
 *
 * @author Alec
 */
public abstract class VisualizationLayer extends Layer {   
    protected   boolean             showKey;
    protected   int                 displayInterval;
    protected   TimeSpanControl     timeControl;
    
    /**
     * Returns the time at which to display each visualization in the 
     * time series
     * 
     * @return 
     */
    public int getDisplayInterval() {
        return displayInterval;
    }        
    
    /**
     * Returns the number of elements in the Time Series.
     * 
     * @return 
     */
    public abstract int getNumberOfSeries();
    
    /**
     * Returns if this Visualization has a Time Series.
     * 
     * @return 
     */
    public abstract boolean hasTimeSeries();
    
    /**
     * Returns the TimeSpanControl, if it exists, otherwise returns null.
     * 
     * @return 
     */
    public TimeSpanControl getTimeSpanControl() {
        return timeControl;
    }
    
    /**
     * Sets the time in milliseconds for each visualization in the time series
     * to be displayed.
     */
    public void setDisplayInterval(int interval) {
        this.displayInterval = interval;
    }    
}
