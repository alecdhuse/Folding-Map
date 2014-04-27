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
package co.foldingmap.mapImportExport;

import co.foldingmap.GUISupport.ProgressBarPanel;
import co.foldingmap.Logger;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.MapPoint;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.VectorObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 *
 * @author Alec
 */
public class CsvImporter extends Thread {
    private ArrayList<String>   columnNames;
    private boolean             headersRead;
    private DigitalMap          mapData;
    private File                importFile;
    private int                 altIndex, latIndex, lonIndex;
    private int                 descriptionIndex, nameIndex;
    private ProgressBarPanel    progressPanel;
    private String              defaultPointClass;
    private VectorLayer         parentLayer;
    
    public CsvImporter(File        importFile,
                       DigitalMap  mapData,
                       VectorLayer parentLayer) {
        
        this.headersRead      = false;
        this.importFile       = importFile;
        this.mapData          = mapData;
        this.parentLayer      = parentLayer;
        this.columnNames      = new ArrayList<String>();
        
        this.altIndex  = -1;
        this.latIndex  = -1;
        this.lonIndex  = -1;
        this.nameIndex = -1;
        this.descriptionIndex  = -1;
        this.defaultPointClass = "Point";
    }
    
    public CsvImporter(File             importFile,
                       DigitalMap       mapData,
                       ProgressBarPanel progressPanel) {
        
        this.importFile    = importFile;
        this.mapData       = mapData;
        this.progressPanel = progressPanel;
        this.parentLayer   = new VectorLayer("CSV Data");
        this.columnNames   = new ArrayList<String>();
        
        this.altIndex      = -1;
        this.latIndex      = -1;
        this.lonIndex      = -1;
        this.nameIndex     = -1;
        
        this.descriptionIndex  = -1;
        this.defaultPointClass = "Point";       
        
        mapData.addLayer(parentLayer);
    }    
    
    private void importCSV() {
        BufferedReader          br;
        Coordinate              coordinate;
        double                  altitude, latitude, longitude; //Doubles for feed compatibility 
        HashMap<String,String>  extValues;
        int                     currentIndex;
        String                  line, token;
        String                  description, name;
        StringTokenizer         st;
        VectorObject            vObject;
        
        try {            
            br = new BufferedReader(new FileReader(importFile));
            
            //Read first lines      
            line = br.readLine();
            
            if (headersRead == false && !line.startsWith("<!"))
                parseColumnNames(line);
            
            while ((line = br.readLine()) != null) {
                latitude     = 0;
                longitude    = 0;
                altitude     = 0;
                currentIndex = 0;
                description  = "";
                name         = "CSV Point";
                extValues    = new HashMap<String,String>();
                st           = new StringTokenizer(line, ",");
                
                while (st.hasMoreTokens()) {
                    token = st.nextToken();
                    
                    if (currentIndex == this.altIndex) {
                        altitude = Double.parseDouble(token);
                    } else if (currentIndex == this.descriptionIndex) {
                        description = token;
                    } else if (currentIndex == this.latIndex) {
                        latitude = Double.parseDouble(token);
                    } else if (currentIndex == this.lonIndex) {
                        longitude = Double.parseDouble(token);
                    } else if (currentIndex == this.nameIndex) {
                        name = token;
                    } else {
                        extValues.put(columnNames.get(currentIndex), token);
                    }
                    
                    currentIndex++;
                }
                
                //create object
                try {
                    coordinate = new Coordinate((float) altitude, (float) latitude, (float) longitude);                    
                    vObject    = new MapPoint(name, coordinate, extValues);
                    vObject.setDescription(description);
                    vObject.setClass(defaultPointClass);
                    mapData.addCoordinateNode(coordinate);
                    parentLayer.addObject(vObject);
                } catch (Exception e2) {
                    Logger.log(Logger.ERR, "Error in CsvImporter.importCSV() Couldn't Create Object - " + e2);
                }
            }
            
            br.close();        
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in CsvImporter.importCSV() - " + e);
        }
    }
    
    private void parseColumnNames(String header) {
        int             currentIndex;
        String          token;
        StringTokenizer st;
        
        try {
            currentIndex = 0;
            st = new StringTokenizer(header, ",");
            
            while (st.hasMoreTokens()) {
                token = st.nextToken();
                
                //Add token to columnNames
                columnNames.add(token);
                
                if (token.equalsIgnoreCase("altitude")) {
                    altIndex = currentIndex;
                } else if (token.equalsIgnoreCase("elevation")) {
                    altIndex = currentIndex;
                } else if (token.equalsIgnoreCase("latitude")) {
                    latIndex = currentIndex;
                } else if (token.equalsIgnoreCase("longitude")) {
                    lonIndex = currentIndex;
                } else if (token.equalsIgnoreCase("description")) {
                    descriptionIndex = currentIndex;    
                } else if (token.equalsIgnoreCase("name")) {
                    nameIndex = currentIndex;  
                }                                        
                
                currentIndex++;
            }        
        } catch (Exception e) {
            Logger.log(Logger.ERR, "parseColumnNames(String) - " + e);
        }            
        
    }
    
    @Override
    public void run() {
        importCSV();
    }      
    
    /**
     * Sets the Default class for created MapPoints.  
     * The default is Point.
     * 
     * @param newClass 
     */
    public void setDefaultPointClass(String newClass) {
        this.defaultPointClass = newClass;
    }
}
