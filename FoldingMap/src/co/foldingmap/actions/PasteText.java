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
package co.foldingmap.actions;

import co.foldingmap.Logger;
import co.foldingmap.dataStructures.SmartTokenizer;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.Layer;
import co.foldingmap.map.MapObject;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.CoordinateMath;
import co.foldingmap.map.vector.MapPoint;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.VectorObject;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Attempts to create objects from Plain text clipboard data.
 * 
 * @author Alec
 */
public class PasteText extends Action {
    private ArrayList<MapObject> newObjects;
    private DigitalMap           mapData;
    
    public PasteText(DigitalMap mapData) {
        this.mapData    = mapData;
        this.newObjects = new ArrayList<MapObject>();
    }
    
    /**
     * Returns if this Action can be undone.
     * 
     * @return 
     */
    @Override
    public boolean canUndo() {
        return true;
    }      
    
    @Override
    public void execute() {
        String              clipboardText;
        Transferable        clipboardTransferable; 
        VectorLayer         vectorLayer;
        VectorObject        vObject;
        
        try {
            clipboardTransferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            clipboardText         = (String) clipboardTransferable.getTransferData(DataFlavor.stringFlavor);        
            vectorLayer           = null;
            
            StringTokenizer st = new StringTokenizer(clipboardText, "\n");
            
            while (st.hasMoreElements()) {
                String token = st.nextToken();
            
                vObject = parsePoint(token);

                if (vObject != null) {
                    newObjects.add(vObject);

                    //Find a VectorLayer to add the new MapPoint to.
                    if (mapData.getSelectedLayer() instanceof VectorLayer) {
                        vectorLayer = (VectorLayer) mapData.getSelectedLayer();
                    } else {
                        for (Layer l: mapData.getLayers()) {
                            if (l instanceof VectorLayer)
                                vectorLayer = (VectorLayer) l;
                        }
                    }

                    mapData.getCoordinateSet().put(vObject.getCoordinateList().get(0));
                    vectorLayer.addObject(vObject);                
                }
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in PasteText.execute() - " + e);
        }
    }

    /**
     * Tries to parse a coordinate from plain text.
     * Returns null if it fails.
     * 
     * @param text
     * @return 
     */
    public static MapPoint parsePoint(String text) {
        Coordinate      returnVal;
        float           alt, f1,  f2;
        double          lat, lon;
        String          l1,  l2, name, token;
        StringTokenizer tokenizer;
        
        //init
        returnVal = null;
        lat  = 0;
        lon  = 0;
        alt  = 0;
        name = "Pasted Point";
        
        if (text.contains("N") || text.contains("S") || text.contains("E") || text.contains("W") ) {
            //Try for format like: E: 121° 57.7" N: 24° 46' 27.9"
            SmartTokenizer st  = new SmartTokenizer(text);
            String         deg = "0°";
            String         min = "0'";
            String         sec = "0\"";
                    
            if (text.contains("E")) {
                st.jumpAfterChar('E');
            } else if (text.contains("e")) {
                st.jumpAfterChar('e');
            } else if (text.contains("W")) {
                st.jumpAfterChar('W');
            } else if (text.contains("w")) {
                st.jumpAfterChar('w');
            }
            
            token = st.nextToken();
            
            while (!token.contains("N") && !token.contains("n") &&
                   !token.contains("S") && !token.contains("s")) {
                            
                if (token.endsWith("°")) {
                    deg = token;
                } else if (token.endsWith("'")) {
                    min = token;
                } else if (token.endsWith("\"")) {
                    sec = token;
                }
                
                token = st.nextToken();
            }
            
            lon = CoordinateMath.convertHourToDecimal(deg + " " + min + " " + sec);
            lat = CoordinateMath.convertHourToDecimal(st.toString());
            
            if (Coordinate.isLongitudeValid(lon) && Coordinate.isLatitudeValid(lat)) {
                returnVal = new Coordinate(0, lat, lon);
                
                if (returnVal != null) 
                    return new MapPoint(name, "(Unspecified Point)", "", returnVal);
            }
        } else if (text.matches("[0-9a-z\\-, ]*lat\\:[0-9\\. ]*, lng\\:[0-9\\.\\- ]*")) {
            if (text.contains(",")) {
                StringTokenizer st = new StringTokenizer(text, ",");
                String s;                
                String latStr = "0";
                String lonStr = "0";
                
                try {
                    while (st.hasMoreElements()) {
                        s = st.nextToken().trim();

                        if (s.contains("lat")) {
                            latStr = s.substring(s.indexOf(" "));
                            lat = Double.parseDouble(latStr);
                        } else if (s.contains("lng")) {
                            lonStr = s.substring(s.indexOf(" "));
                            lon = Double.parseDouble(lonStr);
                        } else {
                            name = s;
                        }
                    }
                } catch (Exception e) {                   
                }
                
                returnVal = new Coordinate(0, lat, lon);
                
                if (returnVal != null) 
                    return new MapPoint(name, "(Unspecified Point)", "", returnVal);                
            }
        } else {        
            if (text.contains(",")) {
                //possibly comma delimited
                tokenizer = new StringTokenizer("text", ",");
            } else {
                //Try white space delimited 
                tokenizer = new StringTokenizer(text);
            }
        
            //Try to extract Lat and Lon
            if (tokenizer.countTokens() >= 2) {
                l1 = tokenizer.nextToken();
                l2 = tokenizer.nextToken();

                try {
                    f1 = Float.parseFloat(l1);
                    f2 = Float.parseFloat(l2);         

                    //Try to decide which is lat and lon
                    if (f1 > 90f) {
                        // try to use f1 as lon
                        lat = f2;
                        lon = f1;
                    } else {
                        lat = f1;
                        lon = f2;
                    }                
                
                } catch (Exception e) {
                    Logger.log(Logger.INFO, "Error in PasteText.parseCoordinate(String) - Cound Not Parse Coordinate");
                }           
            }
            
            //Check if they are valid
            if (Coordinate.isLongitudeValid(lon) && Coordinate.isLatitudeValid(lat)) 
                returnVal = new Coordinate(0, lat, lon);

            //try to extract altitude as third token
            if (tokenizer.hasMoreTokens()) {
                alt = Float.parseFloat(tokenizer.nextToken());
                returnVal.setAltitude(alt);
            }
          
        }                
        
        if (returnVal != null) 
            return new MapPoint(name, "(Unspecified Point)", "", returnVal);             
        
        return null;
    }
    
    @Override
    public void undo() {
        for (MapObject object: this.newObjects) {
            if (object instanceof VectorObject) {
                ((VectorLayer) object.getParentLayer()).removeObject((VectorObject) object);
            } else {
                Logger.log(Logger.ERR, "Error in PasteText.undo() - Can't Remove Object From Layer");
            }
        }
    }
    
}
