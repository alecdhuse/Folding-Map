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
     * @param kmlWriter 
     */
    @Override
    public void toXML(XmlOutput kmlWriter) {
        try {            
            kmlWriter.openTag ("LinearRing id=\"" + getObjectClass() + "\"");
            kmlWriter.writeTag("name", getName());
            
            if (hasDisplayableText(getDescription()) && !getDescription().equalsIgnoreCase("null"))
                kmlWriter.writeTag("description", "<![CDATA[" + getDescription() + "]]>");
 
            
            kmlWriter.writeTag("Ref", Long.toString(getReference()));
            kmlWriter.writeTag("coordinates",  getCoordinateString());

            if (visibility != null)
                visibility.toXML(kmlWriter);
            
            if (this.parentLayer.hasTimeSpan()) {
                kmlWriter.openTag ("gx:Timestamps");
                    for (Coordinate currentCoordinate: coordinates)
                        kmlWriter.writeText(currentCoordinate.getTimestamp() + " ");
                kmlWriter.closeTag("gx:Timestamps");
            }

            writeCustomDataFieldsAsXML(kmlWriter);

            kmlWriter.closeTag("LinearRing");
        } catch (Exception e) {
            System.err.println("Error in LinearRing.toXML(KmlWriter) Object: " + this.objectName + " - " + e);
        }
    }    
}
