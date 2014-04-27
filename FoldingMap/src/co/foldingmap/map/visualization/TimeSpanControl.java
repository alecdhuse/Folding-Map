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

import co.foldingmap.GUISupport.Updateable;
import co.foldingmap.ResourceHelper;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import javax.swing.ImageIcon;

/**
 *
 * @author Alec
 */
public class TimeSpanControl extends Thread {
    protected ArrayList<Updateable> updateables;
    protected boolean               started;
    protected Color                 markerFill, markerOutline;
    protected float                 x, y, height, width;
    protected float                 stopInterval, trackX, trackY;
    protected GeneralPath           play;
    protected ImageIcon             configImage, pauseImage, playImage;
    protected int                   position, waitTime;
    protected ResourceHelper        resourceHelper;
    protected RoundRectangle2D      playTrack, rectangle;
    protected RoundRectangle2D      playButton, configButton;
    protected RoundRectangle2D      positionMarker;      
    protected VisualizationLayer    layer;
    
    public TimeSpanControl(VisualizationLayer layer) {
        this.layer          = layer;
        this.position       = 0;        
        this.markerFill     = Color.LIGHT_GRAY;
        this.markerOutline  = new Color(255, 255, 255, 200);    
        this.resourceHelper = ResourceHelper.getInstance();
        this.waitTime       = layer.getDisplayInterval();
        this.updateables    = new ArrayList<Updateable>();
                
        configImage = resourceHelper.getImage("gear.png");
        pauseImage  = resourceHelper.getImage("pause.png");
        playImage   = resourceHelper.getImage("play.png");
    }
    
    /**
     * Adds an updateable to be called when a setting of this control is changed.
     * 
     * @param updateable 
     */
    public void addUpdateable(Updateable updateable) {
        updateables.add(updateable);
    }
    
    /**
     * Returns if a given (x, y) coordinate is contained within the drawing of
     * this control.
     * 
     * @param px
     * @param py
     * @return 
     */
    public boolean containsPoint(float px, float py) {
        return rectangle.contains(px, py);
    }
    
    /**
     * Draws the Time Scale for the MapPanel.
     * 
     * @param g2
     * @param height
     * @param width 
     */
    public void draw(Graphics2D g2, float panelHeight, float panelWidth) {
        BasicStroke      stroke; 
        float            centerWidth, currentLineX, positionX;   
        int              numberOfStops, trackWidth;
        Line2D           stopLines[];  
        
        try {
            numberOfStops = layer.getNumberOfSeries();
            stopLines     = new Line2D[numberOfStops];
            stroke        = new BasicStroke(1f,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            centerWidth   = panelWidth / 2.0f;
            x             = centerWidth - 200;
            y             = panelHeight - 50;
            height        = 40;
            width         = 400;
            trackWidth    = 300;
            stopInterval  = (trackWidth / (numberOfStops - 1f)); //find the distance between stop lines.
            trackX        = (centerWidth - 150);
            trackY        = (y + 17);
            rectangle     = new RoundRectangle2D.Float(x, y, width, height, 20, 20);
            playTrack     = new RoundRectangle2D.Float(trackX, trackY, trackWidth, 5, 5, 5);
            playButton    = new RoundRectangle2D.Float((x + 10), (y + 10), 20, 20, 8, 8);            
            play          = new GeneralPath();
            configButton  = new RoundRectangle2D.Float((x + width - 30), (y + 10), 20, 20, 8, 8);
            
            //create play image
            play.moveTo((x + 14), (y + 14));   
            play.lineTo((x + 14), (y + 28));
            play.lineTo((x + 28), (y + 20));
            play.lineTo((x + 14), (y + 14));
            
            g2.setStroke(stroke);
            g2.setColor(new Color(68, 68, 68, 180));
            g2.fill(rectangle);      
            g2.setColor(Color.WHITE);
            g2.draw(playTrack);      
            g2.draw(playButton);             
            g2.draw(configButton); 
            g2.drawImage(configImage.getImage(), (int) (x + width - 30), (int) (y + 10), null);
            
            if (started) {
                g2.drawImage(pauseImage.getImage(), (int) (x + 11), (int) (y + 11), null);
            } else {
                g2.fill(play);
            }
            
            //create stop lines
            //g2.setColor(new Color(255, 255, 255, 200));

            for (int i = 1; i < (numberOfStops - 1); i++) {
                currentLineX = trackX + (i * stopInterval);
                stopLines[i] = new Line2D.Float(currentLineX, trackY, currentLineX, trackY + 5);   
                //g2.draw(stopLines[i]);  
            }        

            //draw posistion marker
            positionX      = (trackX + (position * stopInterval)) - 4f;
            positionMarker = new RoundRectangle2D.Float(positionX, trackY - 2, 9, 8, 2, 2);

            g2.setColor(markerFill);
            g2.fill(positionMarker);        
            g2.setColor(markerOutline);
            g2.draw(positionMarker);    
        } catch (Exception e) {
            System.err.println("Error in TimeSpanControl.draw(Graphics2D, float, float) - " + e);
        }
    }
       
    public int getPosition() {
        return position;
    }
    
    /**
     * Increments by one, and loops back to zero when the maximum is reached.
     */
    public void incrementPosition() {
        if (position < (layer.getNumberOfSeries() - 1)) {
            position++;
        } else {
            position = 0;
        }        
    }
    
    /**
     * Pass mouse clicks in to enable functionality.
     * 
     * @param range 
     */
    public void mouseClicked(double clickX, double clickY) {
        double trackPos;
        int    stopPos;
        
        if (playButton.contains(clickX, clickY)) {
            if (started) {
                started = false;                               
            } else {
                started = true;
                
                if (!this.isAlive()) 
                    this.start();
            }
        } else if (configButton.contains(clickX, clickY)) { 
            
        } else if (playTrack.contains(clickX, clickY)) { 
            trackPos = (clickX - trackX);
            stopPos  = (int) (trackPos / stopInterval);
            position = stopPos;
        }
    }    
    
    @Override
    public void run() {
        try {
            while (started) {
                incrementPosition();
                
                for (Updateable u: updateables) 
                    u.update();                
                
                this.sleep(waitTime);
            }
            
            this.interrupt();
        } catch (Exception e) {
            System.err.println("Error in TimeSpanControl.run() - " + e);
        }
    }
}
