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
package co.foldingmap.map.labeling;

import co.foldingmap.Logger;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * This class handles the drawing and placement of all the labels on the map.
 * 
 * @author Alec
 */
public class LabelManager {
    private ArrayList<MapLabel> labels;
    
    /**
     * Constructor for LabelManager.
     * 
     */
    public LabelManager() {
        labels = new ArrayList<MapLabel>();
    }    
    
    /**
     * Adds a LineStringLAbel to the list of labels to be drawn.
     * 
     * @param label 
     */
    public boolean addLabel(Graphics2D g2, LineStringLabel label) {
        boolean addLabel = false;
        
        try {
            label.generateLabelArea(g2); 

            if (labels.isEmpty()) {
                addLabel = true;
            }

            for (MapLabel currentLabel: labels) {
                if (!currentLabel.overlapsLabel(label)) {
                    addLabel = true;
                } else {
                    addLabel = false;
                    break;                    
                }
            }

            if (addLabel) {
                labels.add(label); 
                label.complete = true;
            }            
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in LabelManager.addLabel(Graphics2D, LineStringLabel) - " + e);
        }
              
        return addLabel;  
    }    
    
    public void addLabels(Graphics2D g2, ArrayList labels) {
        for (int i = 0; i < labels.size(); i++) {
            Object ml = labels.get(i);
            
            if (ml instanceof LineStringLabel) {
                addLabel(g2, (LineStringLabel) ml);
            } else if (ml instanceof PointLabel) {
                addLabel(g2, (PointLabel) ml);
            }
        }
    }
    
    /**
     * Adds a PointLabel to the list of labels to be drawn.
     * 
     * @param g2
     * @param label
     * @return      If the label could be added to the list of labels to be drawn.
     */
    public boolean addLabel(Graphics2D g2, PointLabel label) {
        boolean addLabel = false;
        
        try {
            label.generateLabelArea(g2, PointLabel.SOUTH); 

            if (labels.isEmpty()) {
                addLabel = true;
            }

            for (MapLabel currentLabel: labels) {
                if (!currentLabel.overlapsLabel(label)) {
                    addLabel = true;
                } else {
                    label.generateLabelArea(g2, PointLabel.EAST); 

                    if (!currentLabel.overlapsLabel(label)) {
                        addLabel = true;
                    } else {
                        addLabel = false;
                        break;
                    }
                }
            }
            
            if (addLabel) {
                labels.add(label); 
                label.complete = true;
            }
        } catch (Exception e) {
            System.err.println("Error in LabelManager.addLabel(Graphics2D, PointLabel) - " + e);
        }
              
        return addLabel;
    }    
    
    /**
     * Adds a PolygonLabel to the list of labels to be drawn.
     * 
     * @param g2
     * @param label
     * @return      If the label could be added to the list of labels to be drawn.
     */
    public boolean addLabel(Graphics2D g2, PolygonLabel label) {
        boolean addLabel = false;
        
        try {
            label.generateLabelArea(g2);            
            
            Rectangle2D bounds = label.getLabelArea();

            if (labels.isEmpty()) {
                addLabel = true;            
            } else {
                for (MapLabel currentLabel: labels) {
                    if (label.overlapsLabel(currentLabel)) {
                        addLabel = false;
                        break;                        
                    } else {
                        addLabel = true;                        
                    }
                }
            }
            
            if (addLabel) {
                labels.add(label); 
                label.complete = true;
            }            
            
            return addLabel;
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in LabelManager.addLabel(Graphics2D, MapLabel - " + e);
            return false;
        }
    }        
    
    /**
     * Adds a MApLabel to the list of labels to be drawn.
     * 
     * @param g2
     * @param label
     * @return      If the label could be added to the list of labels to be drawn.
     */
    public boolean addLabel(Graphics2D g2, MapLabel label) {
        boolean addLabel = false;
        
        try {                    
            Rectangle2D bounds = label.getLabelArea();

            if (labels.isEmpty()) {
                addLabel = true;            
            } else {
                for (MapLabel currentLabel: labels) {
                    if (label.overlapsLabel(currentLabel)) {
                        addLabel = false;
                        break;                        
                    } else {
                        addLabel = true;                        
                    }
                }
            }
            
            if (addLabel) {
                labels.add(label); 
                label.complete = true;
            }            
            
            return addLabel;
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in LabelManager.addLabel(Graphics2D, MapLabel - " + e);
            return false;
        }
    }     
    
    /**
     * Remove all labels from the list.
     * 
     */
    public void clear() {
        labels.clear();
    }    
    
    /**
     * Draws all the labels in the Managers list.
     * 
     * @param g2 
     */
    public void drawLabels(Graphics2D g2) {      
        try {                    
            for (MapLabel cl: this.labels) {              
                if (cl.isComplete())
                    cl.drawLabel(g2);         
            }
        } catch (Exception e) {
            System.err.println("Error in LabelManager.drawLabels(Graphics2D) - " + e);
        }
    }      
    
    /**
     * Returns all the labels for this LabelManager.
     * 
     * @return An ArrayList of MapLabels contained in this LabelManager.
     */
    public ArrayList<MapLabel> getLabels() {
        return this.labels;
    }
}
