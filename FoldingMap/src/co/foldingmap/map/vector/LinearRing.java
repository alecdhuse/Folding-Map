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

import co.foldingmap.Logger;
import co.foldingmap.map.MapView;
import co.foldingmap.xml.XmlOutput;

/**
 * Handles the FmXML object LinearRing, very similar to LineString, however this 
 * object is closed into a loop.
 * 
 * @author Alec
 */
public class LinearRing extends LineString {
    
    public LinearRing(String name, String objectClass, CoordinateList<Coordinate> coordinates) {    
        super(name, objectClass, coordinates);
    }
    
    /**
     * Converts the objects latitude and longitude coordinates screen x,y points.
     * Calls the same method from the SuperClass LineString, but then closes the path.
     * 
     * @param mapView 
     */
    @Override
    protected void convertCoordinatesToLines(MapView mapView) {
        super.convertCoordinatesToLines(mapView);
        
        if (lineLeftInit)
            lineLeft.closePath();

        if (lineCenterInit)
            lineCenter.closePath();                  

        if (lineRightInit)
            lineRight.closePath();      
    }    
    
    /**
     * Writes out the KML for LinearRing.
     * 
     * @param xmlWriter 
     */
    @Override
    public void toXML(XmlOutput xmlWriter) {
        try {            
            xmlWriter.openTag ("LinearRing class=\"" + getObjectClass() + "\" id=\"" + getName() + "\"");
            
            if (hasDisplayableText(getDescription()) && !getDescription().equalsIgnoreCase("null"))
                xmlWriter.writeTag("description", "<![CDATA[" + getDescription() + "]]>");
 
            
            xmlWriter.writeTag("Ref", Long.toString(getReference()));
            xmlWriter.writeTag("coordinates",  getCoordinateString());

            if (visibility != null)
                visibility.toXML(xmlWriter);
            
            //Timestanps are now in nodes and this isn't needed - 2014-07-16 ASD
//            if (this.parentLayer.hasTimeSpan()) {
//                xmlWriter.openTag ("gx:Timestamps");
//                    for (Coordinate currentCoordinate: coordinates)
//                        xmlWriter.writeText(currentCoordinate.getTimestamp() + " ");
//                xmlWriter.closeTag("gx:Timestamps");
//            }

            writeCustomDataFieldsAsXML(xmlWriter);

            xmlWriter.closeTag("LinearRing");
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in LinearRing.toXML(KmlWriter) Object: " + this.objectName + " - " + e);
        }
    }    
}
