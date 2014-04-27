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

import co.foldingmap.map.themes.LabelStyle;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * This class handles the drawing of labels for Polygons.
 * 
 * @author Alec
 */
public class PolygonLabel extends MapLabel {
    protected int         x, y;
    protected LabelStyle  labelStyle;
    protected String      labelText;
    
    public PolygonLabel(String labelText, LabelStyle labelStyle, int x, int y) {
        this.labelText  = labelText;
        this.labelStyle = labelStyle;
        
        this.x = x;
        this.y = y;
    }
    
    @Override
    public void drawLabel(Graphics2D g2) {
        g2.drawString(labelText, x, y);
    }

    /**
     * Creates the Rectangle2D that represents this Label's area.
     * Used to help prevent overlapping of labels.
     * 
     * @param g2
     */
    public void generateLabelArea(Graphics2D g2) {
        fontMetrics = g2.getFontMetrics();
        labelArea   = fontMetrics.getStringBounds(labelText, g2);
    }    
    
    @Override
    public Rectangle2D getLabelArea() {
        return labelArea;
    }

    @Override
    public boolean overlapsLabel(MapLabel label) {
        boolean     value    = false;
        Rectangle2D testArea = label.getLabelArea();
        
        if (labelArea != null) {
            if (labelArea.equals(testArea))
                value = true;

            if (testArea != null) 
                value = labelArea.intersects(testArea);    
        }
        
        return value;
    }
    
}
