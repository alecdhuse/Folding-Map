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

import co.foldingmap.ResourceHelper;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import javax.swing.*;

/**
 * A panel that is used for displaying the progress of events in the program.
 * 
 * @author Alec
 */
public class ProgressBarPanel extends JPanel implements ActionListener, ProgressIndicator {
    protected boolean        paused, stopped;
    protected JButton        buttonPause, buttonStop;
    protected JLabel         labelMessage;
    protected JPanel         buttonPanel, leftPanel;
    protected JProgressBar   progressBar;
    protected ResourceHelper resourceHelper;
    
    public ProgressBarPanel() {
        init();
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == buttonPause) {
            paused = !paused;
            
            if (paused) {
                buttonPause.setIcon(resourceHelper.getImage("play_button.png"));
            } else {
                buttonPause.setIcon(resourceHelper.getImage("pause_button.png"));
            }
            
            buttonPause.setBorder(null);
        } else if (ae.getSource() == buttonStop) {
            stopped = true;
            
            if (progressBar.getValue() == 100) 
                this.setVisible(false);            
        }
    }    
    
    /**
     * Sets the progress to finished.
     * 
     */
    @Override
    public void finish() {
        setPauseVisible(false);
        this.progressBar.setValue(100);
        this.stopped = true;
    }    
    
    private void init() {
        resourceHelper  = ResourceHelper.getInstance();
        buttonPanel     = new JPanel();
        buttonPause     = new JButton(resourceHelper.getImage("pause_button.png"));
        buttonStop      = new JButton(resourceHelper.getImage("stop_button.png"));    
        labelMessage    = new JLabel("", SwingConstants.LEFT);
        leftPanel       = new JPanel(new FlowLayout(FlowLayout.LEFT));
        progressBar     = new JProgressBar(0, 100);
        
        buttonPause.setBorder(null);
        buttonPause.setFocusable(false);
        buttonPause.addActionListener(this);
        buttonPause.setPreferredSize(new Dimension(15, 15));
                
        buttonStop.setBorder(null);
        buttonStop.setFocusable(false);
        buttonStop.addActionListener(this);
        buttonStop.setPreferredSize(new Dimension(15, 15));
        
        buttonPanel.add(buttonPause);
        buttonPanel.add(buttonStop);        
        buttonPanel.setPreferredSize(new Dimension(40, 19));
                
        progressBar.setPreferredSize( new Dimension(110, 19));
        labelMessage.setPreferredSize(new Dimension(215, 19));
                
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.add(buttonPanel);
        this.add(progressBar);        
        this.add(labelMessage);                
    }
    
    /**
     * Returns if the user has clicked the pause button.
     * 
     * @return 
     */
    public boolean isPaused() {
        return paused;
    }
    
    /**
     * Returns if the user has hit the stop button.
     * 
     * @return 
     */
    public boolean isStopped() {
        return stopped;
    }
    
    /**
     * Gets an image from the jar's main resource folder.
     * 
     * @param fileName
     * @return 
     */
    public ImageIcon getImage(String fileName) {
        try {
            String filePath = "resources" + File.separator + fileName;
            URL    url      = getClass().getResource(filePath);

            return new ImageIcon(url);
        } catch (Exception e) {
            return new ImageIcon();
        }
    }       
    
    /**
     * Resets the progress of this indicator
     */
    public void reset() {
        paused  = false;
        stopped = false;
        progressBar.setValue(0);
        labelMessage.setForeground(Color.BLACK);
        labelMessage.setText("");   
        setPauseVisible(true);
    }    
    
    /**
     * Displays and error message in the ProgressBarPanel
     * 
     * @param errMessage 
     */
    public void setError(String errMessage) {
        labelMessage.setForeground(Color.RED);
        labelMessage.setText(errMessage);    
        this.setVisible(true);        
        this.repaint();
    }
    
    /**
     * Sets the message to be displayed.
     * 
     * @param message 
     */
    @Override
    public void setMessage(String message) {
        labelMessage.setForeground(Color.BLACK);
        labelMessage.setText(message);     
        this.setVisible(true);        
        this.repaint();
    }
    
    private void setPauseVisible(boolean v) {
        if (v) {
            buttonPause.setIcon(resourceHelper.getImage("stop_button.png"));
        } else {
            buttonPause.setIcon(resourceHelper.getImage("blank_button.png"));
        }
        
        buttonPause.setBorder(null);
    }
    
    /**
     * Set the value of the progress bar.
     * 
     * @param value 
     */
    @Override
    public void setValue(int value) {        
        if ((value >= 0) && (value <= 100)) {
            progressBar.setValue(value);
        }

        this.setVisible(true);
        this.repaint();
    }    
    
    /**
     * Update the progress and message.
     * 
     * @param detail
     * @param value 
     */
    @Override
    public void updateProgress(String detail, int value) {
        labelMessage.setForeground(Color.BLACK);
        labelMessage.setText(detail);
        setValue(value);
    }

}
