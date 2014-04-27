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
 * Used to keep track of the inner boundary of objects.
 * 
 * @author Alec
 */
public class InnerBoundary {
    protected CoordinateList<Coordinate>    coordinateList;
    
    public InnerBoundary(CoordinateList<Coordinate> coordinateList) {
        this.coordinateList = coordinateList;
    }
    
    /**
     * Returns if two InnerBoundary objects are equal.
     * Two InnerBoundaries are equal if they contain the same coordinates in the same order.
     * 
     * @param o
     * @return 
     */
    @Override
    public boolean equals(Object o) {
        boolean isEqual = false;
        
        if (o instanceof InnerBoundary) {
            InnerBoundary ib = (InnerBoundary) o;
            
            return (this.hashCode() == ib.hashCode());
//            CoordinateList<Coordinate> testList = ib.getCoordinateList();
//            
//            if (testList.size() == coordinateList.size()) {
//                for (int i = 0; i < coordinateList.size(); i++) {
//                    if (coordinateList.get(i).equals(testList.get(i))) {
//                        isEqual = true;
//                    } else {
//                        isEqual = false;
//                        break;
//                    }
//                }
//            } 
        } 
        
        return isEqual;
    }

    /**
     * Generates the hashCode for this InnerBoundary.
     * 
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.coordinateList != null ? this.coordinateList.hashCode() : 0);
        return hash;
    }
    
    /**
     * Returns the CoordinateList for this InnerBoundary.
     * 
     * @return 
     */
    public CoordinateList<Coordinate> getCoordinateList() {
        return this.coordinateList;
    }
    
    /**
     * Outputs this InnerBoundary to FmXML.
     * 
     * @param kmlWriter 
     */
    public void toXML(XmlOutput kmlWriter) {
        try {
            kmlWriter.openTag ("innerBoundaryIs");
            kmlWriter.openTag ("LinearRing");
            kmlWriter.writeTag("coordinates",  coordinateList.getCoordinateString());
            kmlWriter.closeTag("LinearRing");
            kmlWriter.closeTag("innerBoundaryIs");        
        } catch (Exception e) {
            System.err.println("Error in InnerBoundary.toXML(KmlOutput) - " + e);
        }
    }
}
