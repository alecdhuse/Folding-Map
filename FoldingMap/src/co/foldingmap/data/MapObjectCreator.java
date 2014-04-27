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
package co.foldingmap.data;

import co.foldingmap.map.vector.VectorObjectList;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.LineString;
import co.foldingmap.map.vector.MapPoint;
import co.foldingmap.map.vector.Polygon;
import co.foldingmap.Logger;
import co.foldingmap.dataStructures.PropertyValuePair;
import co.foldingmap.map.DigitalMap;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Alec
 */
public class MapObjectCreator {
    protected DigitalMap                mapData;
    protected VectorObjectList<VectorObject>  errorObjects;
    protected StringBuffer              results;
    protected TabularData               dataFile;
            
    public MapObjectCreator(TabularData dataFile, DigitalMap mapData) {
        this.mapData  = mapData;
        this.dataFile = dataFile;
        this.results  = new StringBuffer();
    }
    
    /**
     * Adds information from the TabularData class in to MapObjects where 
     * the importDataVariable matches the mapObjectVariable.
     * 
     * @param mapData
     * @param importDataVariable
     * @param mapObjectVariable 
     */
    public void addData(DigitalMap mapData, String importDataVariable, String mapObjectVariable) {
        ArrayList<PropertyValuePair>    customDataFields;
        ArrayList<DataCell>             uv;
        ArrayList<DataCell>             currentRow, uniqueColumnItems;
        double                          average;
        int                             affectedObjects, trueValues, totalValues, unaffectedObjects;
        VectorObjectList<VectorObject>        mapObjects;
        PropertyValuePair               currentPair;
        String                          columnItemString, mapObjectVariableValue;
        TabularData                     subSet;

        affectedObjects   = 0;
        mapObjects        = new VectorObjectList<VectorObject>(mapData.getAllMapObjects());
        unaffectedObjects = 0;
        uniqueColumnItems = dataFile.getUniqueColumnItems(importDataVariable);

        for (DataCell currentItem: uniqueColumnItems) {
            customDataFields = new ArrayList<PropertyValuePair>();
            columnItemString = currentItem.toString().trim();
            columnItemString = columnItemString.replaceAll("[\\u00A0]", "");
            subSet           = dataFile.getSubSet(importDataVariable, columnItemString);

            //get data into a managable form
            for (int i = 0; i < dataFile.getNumberOfHeaders(); i++) {
                if (!(dataFile.getHeaderName(i).equals(importDataVariable))) {
                    //only parse the data for other field than the one to match to a VectorObject
                    uv = subSet.getUniqueColumnItems(i);

                    if (uv.size() == 1) {
                        //One Unique Value, use that value.
                        currentPair = new PropertyValuePair(dataFile.getHeaderName(i), uv.get(0).toString());
                        customDataFields.add(currentPair);                        
                    } else if (uv.size() == 2) {
                        /*
                         * There are only two options for choice in this column,
                         * take and average for the data point
                         */
                        if (uv.get(0).trim().equals("")) {
                            trueValues = subSet.getNumberOfInstances(uv.get(1).toString(), i);
                        } else {
                            trueValues = subSet.getNumberOfInstances(uv.get(0).toString(), i);
                        }

                        average     = ( ((double) trueValues) / ((double) dataFile.getColumn(i).size()));
                        average    *= 100;
                        currentPair = new PropertyValuePair(dataFile.getHeaderName(i), Double.toString(average));
                        customDataFields.add(currentPair);
                    } else if (uv.size() == dataFile.getColumn(i).size()) {
                        //Every cell has a unique data point, use count;
                        currentPair = new PropertyValuePair(dataFile.getHeaderName(i), Integer.toString(uv.size()));
                        customDataFields.add(currentPair);
                    } else if (TabularData.areOnlyNumbers(subSet.getColumn(i))) {
                        //average numbers together
                        average     = 0;
                        totalValues = 0;

                        for (DataCell currentCell: subSet.getColumn(i)) {
                            if (!currentCell.equals("")) {
                                average += currentCell.getFloatValue();
                                totalValues++;
                            }
                        }

                        results.append("Multiple matches for \"");
                        results.append(subSet.getHeaderName(i));
                        results.append("\", values were averaged.\n");

                        average     = average / ((double) totalValues);
                        currentPair = new PropertyValuePair(dataFile.getHeaderName(i), Double.toString(average));
                        customDataFields.add(currentPair);
                    } else {

                        Logger.log(Logger.ERR, "Data Import - Could not find a data solution for: " + dataFile.getHeaderName(i));
                    }
                }
            }

            //add data to object
            for (int i = 0; i < mapObjects.size(); i++) {
                VectorObject currentMapObject = mapObjects.get(i);

                if (mapObjectVariable.equalsIgnoreCase("Name")) {
                    if (currentMapObject.getName().trim().equalsIgnoreCase(currentItem.trim())) {
                        affectedObjects++;
                        
                        for (PropertyValuePair currentPropPair: customDataFields)
                            currentMapObject.setCustomDataField(currentPropPair.getProperty(), currentPropPair.getValue());
                        //break;
                    } else {
                        unaffectedObjects++;
                    }
                } else {
                    mapObjectVariableValue = currentMapObject.getCustomDataFieldValue(mapObjectVariable);
                    
                    if (mapObjectVariableValue != null) {
                        columnItemString = currentItem.toString().trim();
                        columnItemString = columnItemString.replaceAll("[\\u00A0]", "");
            
                        if (mapObjectVariableValue.equalsIgnoreCase(columnItemString)) {
                            affectedObjects++;

                            for (PropertyValuePair currentPropPair: customDataFields)
                                currentMapObject.setCustomDataField(currentPropPair.getProperty(), currentPropPair.getValue());
                            //break;
                        } else {
                            unaffectedObjects++;
                        }
                    }
                }
            }
        } // end uniqueColumnItems for loop


        //write results
        results.append("Added data to: ");
        results.append(Integer.toString(affectedObjects));
        results.append(" objects.\n");
        results.append("Could not match: ");
        results.append(Integer.toString(unaffectedObjects));
        results.append(" objects.\n");
    }    
    
    /**
     * Creates LineStrings from the TabularData class.
     * 
     * @param nameColumn
     * @param coordinatesColumn
     * @param objectType 
     */
    public VectorObjectList<VectorObject> createLineStrings(String nameColumn, String coordinatesColumn, String objectType) {
        ArrayList<String>        headers;
        ArrayList<DataCell>      row;
        HashMap<String,String>   customFields;
        int                      coordinatesColumnIndex, nameColumnIndex, numberOfRows;
        LineString               newLine;
        VectorObjectList<VectorObject> newLines;
        String                   coordinateString, name;
        
        coordinatesColumnIndex = -1;
        coordinateString       = "";
        headers                = dataFile.getHeaderNames();
        name                   = "";
        nameColumnIndex        = -1;
        newLines               = new VectorObjectList<VectorObject>();
        numberOfRows           = dataFile.getNumberOfRows();
        this.errorObjects      = new VectorObjectList<VectorObject>();

        //find the columns for name, coordinates
        for (int i = 0; i < headers.size(); i++) {
            String header = headers.get(i);

            if (header.equalsIgnoreCase(nameColumn)) {
                nameColumnIndex        = i;
            } else if (header.equalsIgnoreCase(coordinatesColumn)) {
                coordinatesColumnIndex = i;
            }
        }

        for (int i = 0; i < numberOfRows; i++) {
            customFields = new HashMap<String,String>();
            row          = dataFile.getRow(i);
            
            if (coordinatesColumnIndex >= 0) {
                //coordinates exist

                //get name and coordinate string fields
                coordinateString = row.get(coordinatesColumnIndex).toString();
                name             = row.get(nameColumnIndex).toString();

                //create custom fields
                for (int headerIndex = 0; headerIndex < headers.size(); headerIndex++) {
                    if ( (headerIndex != nameColumnIndex) && (headerIndex != coordinatesColumnIndex) ) {
                        customFields.put(headers.get(headerIndex).toString(), row.get(headerIndex).toString());
                    }
                } //end custom fields for loop

                //create objects
                try {
                    if (!coordinateString.equals("")) {
                        newLine = new LineString(name, objectType, coordinateString);
                        newLines.add(newLine);
                    } else {
                        errorObjects.add(new MapPoint(name, Coordinate.UNKNOWN_COORDINATE, customFields));
                    }
                } catch (Exception e) {
                    errorObjects.add(new MapPoint(name, Coordinate.UNKNOWN_COORDINATE, customFields));
                }
            }
        } //end for loop

        //write results
        results.append("Created ");
        results.append(newLines.size());
        results.append(" objects.");

        if (errorObjects.size() > 0) {
            results.append("\nCould not create ");
            results.append(errorObjects.size());
            results.append(" objects because of errors.");
            results.append("\n\nObjects With Errors:\n");
            results.append("--------------------");

            for (VectorObject currentErrorObject: errorObjects) {
                results.append("\n");
                results.append(currentErrorObject.toString());
            }
        }

        return newLines;
    }
    
    /**
     * Creates MapPoints from the columns containing latitude, longitude and
     * altitude.
     * 
     * @param latitudeColumn
     * @param longitudeColumn
     * @param altitudeColumn
     * @param objectType 
     */
    public VectorObjectList<VectorObject> createPoints(String latitudeColumn, String longitudeColumn, String altitudeColumn,  String objectType) {
        ArrayList<String>        headers;
        ArrayList<DataCell>      row;
        float                    latitude, longitude, altitude;
        HashMap<String,String>   customFields;
        int                      numberOfRows;
        int                      latitudeColumnIndex, longitudeColumnIndex, altitudeColumnIndex;
        MapPoint                 currentMapPoint;
        VectorObjectList<VectorObject> newPoints;
        String                   name;

        this.errorObjects    = new  VectorObjectList<VectorObject>();
        headers              = dataFile.getHeaderNames();
        newPoints            = new  VectorObjectList<VectorObject>();
        numberOfRows         = dataFile.getNumberOfRows();
        latitudeColumnIndex  = -1;
        longitudeColumnIndex = -1;
        altitudeColumnIndex  = -1;

        //find the columns used for alt, lat, lon
        for (int i = 0; i < headers.size(); i++) {
            String header = headers.get(i);

            if (header.equalsIgnoreCase(latitudeColumn)) {
                latitudeColumnIndex  = i;
            } else if (header.equalsIgnoreCase(longitudeColumn)) {
                longitudeColumnIndex = i;
            } else if (header.equalsIgnoreCase(altitudeColumn)) {
                altitudeColumnIndex  = i;
            }
        }

        for (int i = 0; i < numberOfRows; i++) {
            customFields = new HashMap<String,String>();
            name         = "";
            row          = dataFile.getRow(i);

            if (latitudeColumnIndex >= 0) {
                latitude  = row.get(latitudeColumnIndex).getFloatValue();
            } else {
                latitude  = (float) Coordinate.UNKNOWN_COORDINATE.getLatitude();
            }

            if (longitudeColumnIndex >= 0) {
                longitude = row.get(longitudeColumnIndex).getFloatValue();
            } else {
                longitude = (float) Coordinate.UNKNOWN_COORDINATE.getLongitude();
            }

            if ((altitudeColumnIndex >= 0)) {
                altitude  = row.get(altitudeColumnIndex).getFloatValue();
            } else {
                altitude  = 0.0f;
            }

            //create custom fields
            for (int headerIndex = 0; headerIndex < headers.size(); headerIndex++) {
                if (headers.get(headerIndex).equalsIgnoreCase("Name")) {
                    name = row.get(headerIndex).trim();
                } else if ( (headerIndex != altitudeColumnIndex) && (headerIndex != longitudeColumnIndex) && (headerIndex != latitudeColumnIndex) ) {
                    customFields.put(headers.get(headerIndex).toString(), row.get(headerIndex).toString());
                }
            }

            if (Coordinate.isLongitudeValid(longitude) && Coordinate.isLatitudeValid(latitude)) {
                float      alt, lat, lon;
                
                Coordinate coord = new Coordinate(altitude, latitude, longitude);
                currentMapPoint  = new MapPoint(name, coord, customFields);
                currentMapPoint.setClass(objectType);
                newPoints.add(currentMapPoint);
            } else {
                //log errors
                errorObjects.add(new MapPoint(name, Coordinate.UNKNOWN_COORDINATE, customFields));
            }
        }

        //write results
        results.append("Created ");
        results.append(newPoints.size());
        results.append(" objects.");

        if (errorObjects.size() > 0) {
            results.append("\nCould not create ");
            results.append(errorObjects.size());
            results.append(" objects because of errors.");
            results.append("\n\nObjects With Errors:\n");
            results.append("--------------------");

            for (VectorObject currentErrorObject: errorObjects) {
                results.append("\n");
                results.append(currentErrorObject.toString());
            }
        }

        return newPoints;        
    }    
    
    /**
     * Creates Polygons from the TabularData class.
     * @param nameColumn
     * @param coordinatesColumn
     * @param objectType 
     */
    public VectorObjectList<VectorObject> createPolygons(String nameColumn, String coordinatesColumn, String objectType) {
        ArrayList<String>        headers;
        ArrayList<DataCell>      row;
        HashMap<String,String>   customFields;
        int                      coordinatesColumnIndex, nameColumnIndex, numberOfRows;
        VectorObjectList<VectorObject> newPolygons;
        Polygon                  newPolygon;
        String                   coordinateString, name;

        coordinatesColumnIndex = -1;
        coordinateString       = "";
        headers                = dataFile.getHeaderNames();
        name                   = "";
        nameColumnIndex        = -1;
        newPolygons            = new VectorObjectList<VectorObject>();
        numberOfRows           = dataFile.getNumberOfRows();
        this.errorObjects      = new VectorObjectList<VectorObject>();

        //find the columns for name, coordinates
        for (int i = 0; i < headers.size(); i++) {
            String header = headers.get(i);

            if (header.equalsIgnoreCase(nameColumn)) {
                nameColumnIndex        = i;
            } else if (header.equalsIgnoreCase(coordinatesColumn)) {
                coordinatesColumnIndex = i;
            }
        }

        for (int i = 0; i < numberOfRows; i++) {
            customFields = new HashMap<String,String>();
            row          = dataFile.getRow(i);

            if (coordinatesColumnIndex >= 0) {
                //coordinates exist

                //get name and coordinate string fields
                coordinateString = row.get(coordinatesColumnIndex).toString();
                name             = row.get(nameColumnIndex).toString();

                //create custom fields
                for (int headerIndex = 0; headerIndex < headers.size(); headerIndex++) {
                    if ( (headerIndex != nameColumnIndex) && (headerIndex != coordinatesColumnIndex) ) {
                        customFields.put(headers.get(headerIndex).toString(), row.get(headerIndex).toString());
                    }
                } //end custom fields for loop

                //create objects
                try {
                    if (!coordinateString.equals("")) {
                        newPolygon = new Polygon(name, objectType, coordinateString);
                        newPolygons.add(newPolygon);
                    } else {
                        errorObjects.add(new MapPoint(name, Coordinate.UNKNOWN_COORDINATE, customFields));
                    }
                } catch (Exception e) {
                    errorObjects.add(new MapPoint(name, Coordinate.UNKNOWN_COORDINATE, customFields));
                }
            }
        } //end for loop

        //write results
        results.append("Created ");
        results.append(newPolygons.size());
        results.append(" objects.");

        if (errorObjects.size() > 0) {
            results.append("\nCould not create ");
            results.append(errorObjects.size());
            results.append(" objects because of errors.");
            results.append("\n\nObjects With Errors:\n");
            results.append("--------------------");

            for (VectorObject currentErrorObject: errorObjects) {
                results.append("\n");
                results.append(currentErrorObject.toString());
            }
        }

        return newPolygons;        
    }    
    
    /**
     * Return a list of objects that had error during the creating. 
     * @return 
     */
    public VectorObjectList<VectorObject> getObjectsWithErrors() {
        return errorObjects;
    }

    /**
     * Get the result of the Converstion/Creation.
     * @return 
     */
    public String getResults() {
        return results.toString();
    }    
}
