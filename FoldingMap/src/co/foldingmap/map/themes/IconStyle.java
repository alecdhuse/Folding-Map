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
package co.foldingmap.map.themes;

import co.foldingmap.ResourceHelper;
import co.foldingmap.map.Visibility;
import co.foldingmap.xml.XmlOutput;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * The KML object for IconStyle
 * 
 * @author Alec
 */
public class IconStyle extends ColorStyle {

    protected ImageIcon      objectImage;
    protected float          heading, scale;        
    protected ResourceHelper resourceHelper;
    protected String         imageFileName;
    
    /**
     * This is for a default IconStyle.
     * 
     */
    public IconStyle() {
        super();
        
        this.id = "Default IconStyle";
    }
    
    /**
     * Basic Constructor for IconStyle.
     * 
     * @param id
     * @param fillColor
     * @param label 
     */
    public IconStyle(String id, Color fillColor) {
        super();

        this.heading            = 0;
        this.scale              = 1;
        this.imageFileName      = "";
        this.fillColor          = fillColor;
        this.id                 = id;
        this.outline            = true;
        this.label              = null;
    }    
    
    /**
     * Basic Constructor for IconStyle.
     * 
     * @param id
     * @param fillColor
     * @param label 
     */
    public IconStyle(String id, Color fillColor, LabelStyle label, Visibility visibility) {
        super();

        this.heading            = 0;
        this.scale              = 1;
        this.imageFileName      = "";
        this.fillColor          = fillColor;
        this.id                 = id;
        this.outline            = true;
        this.label              = label;
        this.visibility         = visibility;
    }      
    
    public IconStyle(String id, String objectImage, Visibility visibility) {
        this.id            = id;
        this.imageFileName = objectImage;
        this.objectImage   = getImageFromResources(objectImage);        
        this.visibility    = visibility;
    }
         
    /**
     * Returns if two IconStyles are equal.
     * 
     * @param o
     * @return 
     */
    @Override
    public boolean equals(Object o) {
        boolean isEqual = false;
        
        if (o instanceof IconStyle) {
            IconStyle is = (IconStyle) o;
            
            if (is.getFillColor().equals(this.fillColor) &&                    
                is.getHeading() == this.heading &&
                is.getID().equals(this.id) &&                                              
                is.getOutlineColor().equals(this.getOutlineColor()) &&
                is.getOutlineStyles().equals(this.getOutlineStyles()) &&
                is.getScale() == this.getScale() &&
                is.getSelectedFillColor().equals(this.getSelectedFillColor()) &&
                is.getImageFileName().equals(this.getImageFileName()) &&
                is.getSelectedOutlineColor().equals(this.getSelectedOutlineColor())) {
                
                if (is.getLabel() == null && this.getLabel() == null) {
                    isEqual = true;
                } else if (is.getLabel().equals(this.getLabel())) {
                    isEqual = true;
                }
                
                if (isEqual) {
                    int testHash = (is.getVisibility()   != null ? is.getVisibility().hashCode()   : 0);
                    int thisHash = (this.getVisibility() != null ? this.getVisibility().hashCode() : 0);
                    
                    isEqual = (testHash == thisHash);
                }
            }
        }
        
        return isEqual;
    }
    
    /**
     * Creates a file object for this map icon.  This file is only in memory and
     * not on any disk.  It is to be put in a zip archive when creating kmz files.
     * 
     * If no image file is set, null will be returned.
     * 
     * @return  A file with the this map icon.
     */
    public File getImageFile() {
        boolean       drawResult;
        BufferedImage buffered;
        File          newImageFile, tempDIR;

        try {
            if (imageFileName != null) {
                if (!imageFileName.equals("")) {
                    newImageFile = new File(imageFileName);
                    buffered     = new BufferedImage(objectImage.getIconWidth(), objectImage.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = buffered.createGraphics();
                    drawResult   = g.drawImage(getObjectImage().getImage(), 0, 0, null);

                    ImageIO.write(buffered, "PNG", newImageFile); 
                    g.dispose();
                    newImageFile.deleteOnExit();
                            
                    return newImageFile;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println("Error in IconStyle.getImageFile() - " + e);
            return null;
        }
    }    
    
    /**
     * Returns the Image File name as a String.
     * 
     * @return 
     */
    public String getImageFileName() {
        return imageFileName;
    }
    
    /**
     * Returns the Heading for this IconStyle
     * 
     * @return 
     */
    public float getHeading() {
        return this.heading;
    }
    
   
    /**
     * Returns an ImageIcon of a given image file located in this program's 
     * .jar file.
     * 
     * @param fileName
     * @return 
     */
    public final ImageIcon getImageFromResources(String fileName) {
        ImageIcon imageIcon;

        try {
            if (this.resourceHelper != null) {
                imageIcon = resourceHelper.getImage(fileName);
            } else {
                imageIcon = (new ImageIcon(getClass().getResource("resources/" + fileName)));
            }
        } catch (Exception e) {
            imageIcon = null;
        }
        
        return imageIcon;
    }       
    
    /**
     * Returns the IconStyle icon as and ImageIcon.
     * 
     * @return 
     */
    public ImageIcon getObjectImage() {
        return objectImage;
    }    
    
    /**
     * Returns the Scale for this IconStyle
     * 
     * @return 
     */
    public float getScale() {
        return scale;
    }    
    
    /**
     * Sets the heading for this Icon Style.
     * 
     * @param heading 
     */
    public void setHeading(float heading) {
        this.heading = heading;
    }
    
    /**
     * Sets this object Icon from a file path.
     * 
     * @param fileName 
     */
    public void setImageFileName(String fileName) {
        try {
            if ((fileName != null) && (!fileName.equals(""))) {
                this.imageFileName = fileName;
                
                if (fileName.startsWith("http")) {
                    //Image file is on the web
                    if (this.resourceHelper == null)
                        resourceHelper = ResourceHelper.getInstance();
                    
                    this.objectImage = resourceHelper.getImage(fileName);
                } else {                    
                    this.objectImage = getImageFromResources(fileName);

                    if (this.objectImage == null)
                        this.objectImage = new ImageIcon(ImageIO.read(new File(fileName)));
                }
            } else {
                this.imageFileName = null;
                this.objectImage   = null;
            }
        } catch (Exception e) {
            System.err.println("Error in IconStyle.setImageFileName(" + fileName + ") - " + e);
        }
    }    
    
    /**
     * Sets the ResourceHelper that is used to load images.
     * 
     * @param resourceHelper 
     */
    public void setResourceHelper(ResourceHelper resourceHelper) {
        this.resourceHelper = resourceHelper;
    }
    
    /**
     * Sets the scale for this IconStyle.
     * 
     * @param scale 
     */
    public void setScale(float scale) {
        this.scale = scale;
    }
        
    /**
     * Writes this IconStyle to KML.
     * 
     * @param kmlWriter 
     */
    @Override
    public void toXML(XmlOutput kmlWriter) {
        try {
            kmlWriter.openTag ("Style id=\"" + id + "\"");
            kmlWriter.openTag ("IconStyle");

            kmlWriter.writeTag("color", ColorHelper.getColorHexStandard(fillColor));

            if (colorMode == NORMAL) {
                kmlWriter.writeTag("colorMode", "normal");
            } else if (colorMode == RANDOM) {
                kmlWriter.writeTag("colorMode", "random");
            }

            if (outline) {
                kmlWriter.writeTag("outline", "1");
                kmlWriter.writeTag("outlineColor", ColorHelper.getColorHexStandard(getOutlineColor()));
            } else {
                kmlWriter.writeTag("outline", "0");
            }        

            kmlWriter.writeTag("scale",   Float.toString(scale));
            kmlWriter.writeTag("heading", Float.toString(heading));

            if (imageFileName != null) {
                if ( (!imageFileName.equals("null")) && !(imageFileName == null) && (!imageFileName.equals(""))) {
                    kmlWriter.openTag ("Icon");
                    kmlWriter.writeTag("href", imageFileName);
                    kmlWriter.closeTag("Icon");
                }
            }

            if (this.visibility != null)
                this.visibility.toXML(kmlWriter);

            kmlWriter.closeTag("IconStyle");

            if (label != null)
                label.toXML(kmlWriter);

            kmlWriter.closeTag("Style");     
        } catch (Exception e) {
            System.err.println("Error in IconStyle.toXML(KmlOutput) - " + e);
        }
    }
}
