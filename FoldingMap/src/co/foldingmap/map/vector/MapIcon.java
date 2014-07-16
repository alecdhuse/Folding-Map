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
package co.foldingmap.map.vector;

import co.foldingmap.ResourceHelper;
import co.foldingmap.xml.XmlOutput;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 * Component element for Overlays.
 * 
 * @author Alec
 */
public class MapIcon {
    public static final int   ON_CHANGE   = 1;
    public static final int   ON_INTERVAL = 2;
    public static final int   ON_EXPIRE   = 3;
    public static final int   NEVER       = 5;
    public static final int   ON_STOP     = 6;
    public static final int   ON_REQUEST  = 7;    
    public static final int   ON_REGION   = 8; 
    
    protected BufferedImage   iconImage;
    protected Float           refreshInterval; //in seconds
    protected ImageIcon       imageIcon;
    protected int             height, width;
    protected int             refreshMode, viewRefreshMode;
    protected long            lastUpdate; // in ms
    protected ResourceHelper  helper;
    protected String          fileAddress, id;
    
    public MapIcon(String id, String fileAddress) {
        this.id              = id;
        this.fileAddress     = fileAddress;
        this.helper          = ResourceHelper.getInstance();
        this.lastUpdate      = 0;
        this.refreshInterval = 240f;
        this.refreshMode     = ON_INTERVAL;
        this.viewRefreshMode = ON_REQUEST;
    }
    
    private MapIcon(String id, String fileAddress, Float refreshInterval, long lastUpdate) {
        this.id              = id;
        this.fileAddress     = fileAddress;
        this.helper          = ResourceHelper.getInstance();
        this.refreshInterval = refreshInterval;
        this.lastUpdate      = lastUpdate;
    }    
    
    /**
     * Creates a copy of this MapIcon.
     * 
     * @return 
     */
    public MapIcon copy() {
        MapIcon newIcon;
        
        newIcon = new MapIcon(id, fileAddress, refreshInterval, lastUpdate);
        
        newIcon.setRefreshInterval(refreshInterval);
        newIcon.setViewRefreshMode(viewRefreshMode);
        
        return newIcon;
    }
    
    /**
     * Returns if this MapIcon is equal to another object.
     * 
     * @param o
     * @return 
     */
    @Override
    public boolean equals(Object o) {
        
        if (o instanceof MapIcon) {
            MapIcon icon = (MapIcon) o;         
            return (icon.hashCode() == this.hashCode());
        } else {        
            return false;
        }
    }

    /**
     * Generate a hash code for this MapIcon.
     * 
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.refreshInterval != null ? this.refreshInterval.hashCode() : 0);
        hash = 17 * hash + this.refreshMode;
        hash = 17 * hash + this.viewRefreshMode;
        hash = 17 * hash + (this.fileAddress != null ? this.fileAddress.hashCode() : 0);
        hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
    
    /**
     * Returns the BufferedImage for this MapIcon class.
     * If the update has expired a new image will be fetched.
     * 
     * @return 
     */
    public BufferedImage getBufferedImage() {
        if (fileAddress != null || !fileAddress.equals("")) {
            if (lastUpdate + (refreshInterval * 1000) < System.currentTimeMillis()) {            
                iconImage  = helper.getBufferedImage(fileAddress);

                if (iconImage != null) {
                    lastUpdate = System.currentTimeMillis();                
                    height     = iconImage.getHeight();
                    width      = iconImage.getWidth();                
                }
            }
            
            return iconImage;
        } else {
            return null;
        }
    }    
    
    /**
     * Returns the image file location used in this MapIcon.
     * This can be a local file or URL.
     * 
     * @return 
     */
    public String getFileAddress() {
        return this.fileAddress;
    }
    
    /**
     * Returns the height of this image, but only if the image has been 
     * retrieved by using the getImageIcon() or the getBufferedImage() method.
     * Otherwise 0 is returned.
     * 
     * @return 
     */
    public int getHeight() {
        return this.height;
    }
    
    /**
     * Returns the ratio of height to width.
     * Returns 0 if the image has ret to be retrieved by the getImageIcon() 
     * or the getBufferedImage() methods.
     * 
     * @return 
     */
    public float getHeightWidthRatio() {
        if (height > 0 && width > 0) {
            return (height / (float) width);
        } else {
            return 0;
        }
    }
    
    /**
     * Returns the ID of this MapIcon.
     * 
     * @return 
     */
    public String getID() {
        return this.id;
    }
    
    /**
     * Returns the ImageIcon for this MapIcon class.
     * If the update has expired a new image will be fetched.
     * 
     * @return 
     */
    public ImageIcon getImageIcon() {
        if (fileAddress != null && (!fileAddress.equals(""))) {
            if (lastUpdate + (refreshInterval * 1000) < System.currentTimeMillis()) {                                               
                imageIcon  = helper.getImage(fileAddress);
                
                if (imageIcon != null) {
                    lastUpdate = System.currentTimeMillis(); 
                    height     = imageIcon.getIconHeight();
                    width      = imageIcon.getIconWidth();              
                }
            }
        }
        
        return imageIcon;
    }
    
    /**
     * Returns the interval in seconds that this image is refreshed.
     * 
     * @return 
     */
    public float getRefreshInterval() {
        return this.refreshInterval;
    }
    
    /**
     * Returns the refresh mode for this image.
     * Possible values are: ON_CHANGE, ON_INTERVAL, ON_EXPIRE.
     * 
     * @return 
     */
    public int getRefreshMode() {
        return this.refreshMode;
    }
    
    /**
     * Returns the View Refresh Mode for this image.
     * Possible values are: NEVER, ON_STOP, ON_REQUEST, ON_REGION.
     * 
     * @return 
     */
    public int getViewRefreshMode() {
        return this.viewRefreshMode;
    }
    
    /**
     * Returns the width of this image, but only if the image has been 
     * retrieved by using the getImageIcon() or the getBufferedImage() method.
     * Otherwise 0 is returned.
     * 
     * @return 
     */
    public int getWidth() {
        return this.width;
    }
    
    /**
     * Sets the Image file address, this can be a local file or a URL.
     * 
     * @param fileAddress 
     */
    public void setAddress(String fileAddress) {
        this.fileAddress = fileAddress;
    }
    
    public void setRefreshInterval(float refreshInterval) {
        this.refreshInterval = refreshInterval;
    }
    
    /**
     * Sets the Refresh Mode.
     * Possible values are: ON_CHANGE, ON_INTERVAL, ON_EXPIRE.
     * 
     * @param refreshMode 
     */
    public void setRefreshMode(int refreshMode) {
        this.refreshMode = refreshMode;
    }
    
    /**
     * Sets the View Refresh Mode.
     * Possible values are: NEVER, ON_STOP, ON_REQUEST, ON_REGION.
     * @param viewRefreshMode 
     */
    public void setViewRefreshMode(int viewRefreshMode) {
        this.viewRefreshMode = viewRefreshMode;
    }
    
    /**
     * Writes this MapIcon to XML.
     * 
     * @param xmlWriter 
     */
    public void toXML(XmlOutput xmlWriter) {
        xmlWriter.openTag("Icon id=\"" + id + "\"");
        
        xmlWriter.writeTag("href",            fileAddress);
        xmlWriter.writeTag("refreshMode",     Integer.toString(refreshMode));
        xmlWriter.writeTag("refreshInterval", Float.toString(refreshInterval));
        xmlWriter.writeTag("viewRefreshMode", Integer.toString(viewRefreshMode));
        
        xmlWriter.closeTag("Icon");        
    }
}
