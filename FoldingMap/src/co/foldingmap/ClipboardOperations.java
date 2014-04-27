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
package co.foldingmap;

import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.themes.ColorStyle;
import co.foldingmap.map.themes.IconStyle;
import co.foldingmap.map.themes.LineStyle;
import co.foldingmap.map.themes.PolygonStyle;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;
import co.foldingmap.mapImportExport.KmlExporter;
import co.foldingmap.xml.XmlBuffer;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

/**
 * This class has methods for dealing with getting and putting information on
 * the clipboard.
 * 
 * @author Alec
 */
public class ClipboardOperations {
    public static final int UNKNOWN = 0;
    public static final int KML     = 1;
    public static final int GPS     = 4;
    public static final int GPX     = 2;
    public static final int OSM     = 3;
    
    public static void copyMapObjects(DigitalMap mapData, VectorObjectList<VectorObject> objects) {
        ColorStyle          cs;        
        StringSelection     stringSelection;
        XmlBuffer           kml;
        
        kml = new XmlBuffer();
        
        kml.writeTextLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        kml.writeTextLine("<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n");
        kml.openTag("Document");
        kml.writeTag("name", "KmlFile");
               
        //write out styles
        for (VectorObject object: objects) {
            cs = mapData.getTheme().getStyle(object.getObjectClass());
            
            if (cs instanceof IconStyle) {
                KmlExporter.exportIconStyle(kml, (IconStyle) cs);
            } else if (cs instanceof LineStyle) {
                KmlExporter.exportLineStyle(kml, (LineStyle) cs);
            } else if (cs instanceof PolygonStyle) {
                KmlExporter.exportPolyStyle(kml, (PolygonStyle) cs);
            }
        }
        
        //write out objects
        for (VectorObject object: objects) 
            KmlExporter.exportMapObject(kml, object);
       
        kml.closeTag("Document");
        kml.writeTextLine("</kml>");
        
        stringSelection = new StringSelection(kml.toString());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
    }
    
    /**
     * Returns the text on the clipboard.
     * 
     * @return 
     */
    public static String getClipboardText() {
        String                    clipboardText;
        Transferable              clipboardTransferable;
        
        try {
            clipboardTransferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            clipboardText         = (String) clipboardTransferable.getTransferData(DataFlavor.stringFlavor);        
            
            return clipboardText;
        } catch (Exception e) {
            return "";
        }
    }       
    
    /**
     * Returns the type of data on the clipboard.
     * 
     * @return 
     */
    public static int getPasteDataType() {
        int                 dataType;
        String              clipboardText;
        Transferable        clipboardTransferable; 
        
        //init
        dataType = UNKNOWN;
        
        try {
            clipboardTransferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            clipboardText         = (String) clipboardTransferable.getTransferData(DataFlavor.stringFlavor);   
            
            if (clipboardText.contains("<kml")) {
                dataType = KML;
            } else if (clipboardText.contains("<gpx")) {
                dataType = GPX;
            } else if (clipboardText.contains("<osm")) {
                dataType = OSM;   
            } else if (clipboardText.contains("lat") && clipboardText.contains("lng")) {
                dataType = GPS;
            } else if (clipboardText.contains("°")) {    
                dataType = GPS;
            }
        } catch (Exception e) {
            System.err.println("Error in ClipboardOperations.getPasteDataType() - " + e);
        }
        
        return dataType;
    }
}
