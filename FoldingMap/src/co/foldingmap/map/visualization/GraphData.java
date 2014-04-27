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

/**
 *
 * @author Alec
 */
public class GraphData {
    private float[] xValues,    yValues; //These are parallel arrays
    private String  xAxisLabel, yAxisLabel;
    
    public GraphData(float[] xValues, float[] yValues) {
        this.xValues = xValues;
        this.yValues = yValues;
    }
    
    /**
     * Multiplies each X value by the given adjValue.
     * 
     * @param adjValue 
     */
    public void adjustXValues(float adjValue) {
        for (int i = 0; i < xValues.length; i++) 
             xValues[i] = xValues[i] * adjValue;                
    }
    
    public float getMaxX() {
        float currentMax = Float.MIN_VALUE;
        
        for (int i = 0; i < xValues.length; i++) {
             if (xValues[i] > currentMax)
                 currentMax = xValues[i];
        }
        
        return currentMax;
    }
    
    public float getMaxY() {
        float currentMax = Float.MIN_VALUE;
        
        for (int i = 0; i < yValues.length; i++) {
             if (yValues[i] > currentMax)
                 currentMax = yValues[i];
        }
        
        return currentMax;
    }
    
    public float getMinX() {
        float currentMin = Float.MAX_VALUE;
        
        for (int i = 0; i < xValues.length; i++) {
             if (xValues[i] < currentMin)
                 currentMin = xValues[i];
        }
        
        return currentMin;
    }
    
    public float getMinY() {
        float currentMin = Float.MAX_VALUE;
        
        for (int i = 0; i < yValues.length; i++) {
             if (yValues[i] < currentMin)
                 currentMin = yValues[i];
        }
        
        return currentMin;
    }
    
    public float getX(int index) {
        return xValues[index];
    }
    
    public float getY(int index) {
        return yValues[index];
    }    
    
    public String getXAxisLabel() {
        return xAxisLabel;
    }
    
    public String getYAxisLabel() {
        return yAxisLabel;
    }
    
    /**
     * Sets the label for the graph's axis.
     * 
     * @param xAxisLabel
     * @param yAxisLabel 
     */
    public void setAxisLabels(String xAxisLabel, String yAxisLabel) {
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
    }
    
    public int size() {
        return xValues.length;
    }
}
