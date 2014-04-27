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

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 *
 * @author Alec
 */
public class LineGraph extends GraphPanel {
    protected BasicStroke    thickLine, thinLine;
    protected GeneralPath    line, fill;
    protected GraphData      data;
    protected int            xStart, yStart;
    protected int            insetBottom, insetLeft, insetRight, insetTop;
    protected Line2D         xAxis, yAxis;
    protected Line2D[]       xTicks, yTicks;
    protected RenderingHints renderAntialiasing;
    protected String[]       xLabels, yLabels;
    
    public LineGraph(GraphData data) {
        super();
        
        setGraphData(data);
        
        insetBottom = 50;
        insetLeft   = 50;
        insetRight  = 50;
        insetTop    = 50;
        
        renderAntialiasing = new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        renderAntialiasing.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);   
        
        thinLine  = new BasicStroke(1,  BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL); 
        thickLine = new BasicStroke(8,  BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL); 
    }
    
    /*
     * Creates the Shapes used for the graph axes
     */
    private void createAxis() {       
        boolean useDecimal;
        float   intMod, labelValX, labelValY;
        int     numberOfLabelsX, numberOfLabelsY, tickX, tickY;
        
        numberOfLabelsY = ((getHeight() - 100) / 22) + 1;
        numberOfLabelsX = ((getWidth() - (insetLeft + insetRight)) / 40) + 1;
        xLabels         = new String[numberOfLabelsX]; 
        yLabels         = new String[numberOfLabelsY];
        xTicks          = new Line2D[numberOfLabelsX];
        yTicks          = new Line2D[numberOfLabelsY];
        
        float range     = (data.getMaxX() - data.getMinX()); 
        
        if (range >= 10) {
            float logVal   = (float) Math.log10(range); 
            float exponent = (logVal - 1);        
            float interval = (float) Math.pow(10, exponent);
            int   floor    = (int) Math.floor(exponent);
            int   mod      = (int) Math.pow(10, floor);
            
            intMod     = (int) Math.floor(interval / mod) * mod;
            useDecimal = false;
        } else {
            intMod     = range / numberOfLabelsX;
            intMod     = (float) (Math.floor(intMod * 100) * 0.01);
            useDecimal = true;
        }
        
        for (int i = 0; i < numberOfLabelsX; i++) {
            tickX      = (xStart + (i * 40));            
            labelValX  = ((intMod) * i) + data.getMinX();
            xTicks[i]  = new Line2D.Float(tickX, yStart - 4, tickX, yStart + 4);
            
            if (useDecimal) {
                NumberFormat formatter = new DecimalFormat("#0.0");
                xLabels[i] = formatter.format(labelValX);                                  
            } else {
                xLabels[i] = Integer.toString((int) labelValX);         
            }
        }
        
        for (int i = 0; i < numberOfLabelsY; i++) {
            tickY      = (yStart - (i * 22));
            labelValY  = (((data.getMaxY() - data.getMinY()) / numberOfLabelsY) * i) + data.getMinY();
            yTicks[i]  = new Line2D.Float((insetLeft - 8), tickY, insetLeft, tickY);
            
            //In case there is no data to show for Y-Axis, just increment the labels equal to i
            if ((data.getMaxY() - data.getMinY()) <= Float.MIN_VALUE)
                labelValY = i;
            
            yLabels[i] = Integer.toString((int) labelValY);
        }
        
        xAxis = new Line2D.Float(insetLeft, yStart, (getWidth() - insetRight), yStart);
        yAxis = new Line2D.Float(insetLeft, insetTop, insetLeft, yStart);
    }    
    
    /*
     * Creates the Line graph and the fill under the graph.
     */
    private void createGraph() {
        float   adjustedValueX, valueRatioX, valueRatioY;
        float   minX, maxX, maxY;
        float   x, y;
        float   yArea;
        
        line = new GeneralPath();
        fill = new GeneralPath();
        minX = data.getMinX();
        maxX = data.getMaxX();
        maxY = data.getMaxY();            
        
        yArea       = yStart - insetTop;
        valueRatioY = yArea / (maxY * 1.1f);        
        
        //move to origin
        fill.moveTo(xStart + 1, yStart - 1);
        
        for (int i = 0; i < data.size(); i++) {                                
            adjustedValueX = data.getX(i) - minX;
            valueRatioX    = adjustedValueX / maxX;
            
            x = (valueRatioX * (getWidth()  - 100)) + xStart + 1;            
            y = yStart - valueRatioY * data.getY(i);            
            
            if (i == 0) {
                line.moveTo(x, y);
            } else {
                line.lineTo(x, y);
            }
            
            fill.lineTo(x, y + 1);
        }
        
        //Move to x = 0, y = maxY
        fill.lineTo((getWidth() - insetRight), yStart);
    }    
        
    @Override
    public void mouseClicked(MouseEvent me) {

    }

    @Override
    public void mousePressed(MouseEvent me) {

    }

    @Override
    public void mouseReleased(MouseEvent me) {

    }

    @Override
    public void mouseEntered(MouseEvent me) {

    }

    @Override
    public void mouseExited(MouseEvent me) {

    }
    
    @Override
    public void paint(Graphics g) {
        float          labelPosX, labelPosY;
        FontMetrics    fontMetrics;
        Graphics2D     g2;
        int            textHeight;
        
        //setup graphics
        g2 = (Graphics2D) g;
        g2.setRenderingHints(renderAntialiasing);
        
        //get text height
        fontMetrics = g2.getFontMetrics(font);   
        textHeight  = fontMetrics.getHeight();
        
        xStart = insetLeft;
        yStart = getHeight() - insetBottom;
        
        createGraph();      
        createAxis();
        
        g2.setColor(new Color(187, 203, 216));
        //g2.setStroke(thickLine);
        g2.draw(yAxis);
        
        //g2.setStroke(thinLine);
        g2.draw(xAxis);
              
        //draw Y axis ticks
        g2.setColor(new Color(187, 203, 216));
        
        //Draw x-axis ticks and labels               
        for (int i = 0; i < xTicks.length; i++) {                     
            labelPosX = (float) (xTicks[i].getX1() - (fontMetrics.stringWidth(xLabels[i]) / 2));
            labelPosY = (float) (xTicks[i].getY2() + 15);
            
            g2.setColor(new Color(187, 203, 216));
            g2.draw(xTicks[i]); 
            g2.setColor(Color.BLACK);
            g2.drawString(xLabels[i], labelPosX, labelPosY);
        }        
        
        //Draw y-axis ticks and labels               
        for (int i = 0; i < yTicks.length; i++) {                     
            labelPosX = (float) (yTicks[i].getX1() - fontMetrics.stringWidth(yLabels[i]) - 5);
            labelPosY = (float) (yTicks[i].getY1() + (textHeight / 2f) - 2);
            
            g2.setColor(new Color(187, 203, 216));
            g2.draw(yTicks[i]); 
            g2.setColor(Color.BLACK);
            g2.drawString(yLabels[i], labelPosX, labelPosY);
        }
        
        //draw the top line
        g2.setColor(new Color(56, 98, 24));
        g2.draw(line);
        
        //fill line down to x-axis
        g2.setColor(new Color(207, 232, 202));
        g2.fill(fill);        
        
        //Draw X Axis Label
        Rectangle2D xAxisLabelBounds = fontMetrics.getStringBounds(data.getXAxisLabel(), g);
        float insideWidth = this.getWidth() - insetLeft;
        float xAxisLabelX = (float) ((insideWidth / 2.0) - (xAxisLabelBounds.getWidth() / 2.0));
        labelPosY = (float) xTicks[0].getY2() + textHeight + 20;
        g2.setColor(Color.BLACK);
        g2.drawString(data.getXAxisLabel(), xAxisLabelX, labelPosY);
    }
    
    public final void setGraphData(GraphData data) {
        this.data = data;      
    }    
}
