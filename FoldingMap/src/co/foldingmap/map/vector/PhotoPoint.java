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

import co.foldingmap.xml.XmlOutput;

/**
 * A class used for holding geo tagged photos and adding them to a map.
 * 
 * @author Alec
 */
public class PhotoPoint extends MapPoint {
    protected String  photoFile;
    
    public PhotoPoint(String photoName, Coordinate c, String photoFile) {
        init();
        
        this.coordinates.add(c);
        this.objectName  = photoName;
        this.photoFile   = photoFile;
        this.objectClass = "Photo";
    }
    
    /**
     * Returns the File for this PhotoPoint.
     * 
     * @return 
     */
    public String getPhotoFile() {
        return this.photoFile;
    }
    
    @Override
    public void toXML(XmlOutput xmlWriter) {        
        xmlWriter.openTag ("PhotoPoint class=\"" + getObjectClass() + "\" id=\"" + getName() + "\"");
        xmlWriter.writeTag("Ref", Long.toString(getReference()));
        
        xmlWriter.openTag ("Icon");
        xmlWriter.writeTag("href", photoFile);
        xmlWriter.closeTag("Icon");        
        
        xmlWriter.writeTag("coordinates", getCoordinateString());

        if (hasDisplayableText(getDescription()) && !getDescription().equalsIgnoreCase("null"))
            xmlWriter.writeTag("description", "<![CDATA[" + getDescription() + "]]>");

        if (visibility != null)
            visibility.toXML(xmlWriter);                

        writeCustomDataFieldsAsXML(xmlWriter);

        xmlWriter.closeTag("PhotoPoint");
    }    
}
