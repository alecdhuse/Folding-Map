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
package co.foldingmap.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;

/**
 * Simple XML parser for parsing KML.
 * 
 * @author Alec
 */
public class XMLParser {
    Reader  readerDocument;

    /**
     * Constructor for objects of class XMLParser
     */
    public XMLParser(File documentFile)
    {
        try {
            this.readerDocument = new FileReader(documentFile);
        } catch (Exception e) {
            System.err.println("Error in XMLParser constructor(file) - " + e);
        }
    }

    /**
     * Constructor with Reader.
     * 
     * @param r 
     */
    public XMLParser(Reader r) {
        this.readerDocument = r;
    }

    /**
     * Does the actual parsing of the document.
     * 
     * @return 
     */
    public XMLTag parseDocument() {
        boolean         stopDocumentRead;
        BufferedReader  br;
        int             docTagStart, docTagStartClose, docTagEnd;
        String          currentLine, docTagContents;
        StringBuilder   documentText;
        XMLTag          documentTag;

        documentTag      = new XMLTag("", "");
        stopDocumentRead = false;

        try {
            br           = new BufferedReader(readerDocument);
            documentText = new StringBuilder();

            //read in document
            while (br.ready() && !stopDocumentRead)
            {
                currentLine = br.readLine();

                if (currentLine != null) {
                    documentText.append(currentLine);
                } else {
                    //emergency stop
                    stopDocumentRead = true;
                }
            }

            docTagStart      = documentText.indexOf("<Document");
            docTagEnd        = documentText.indexOf("</Document>");

            //can't find document tag try lower case
            if (docTagStart == -1)
                docTagStart = documentText.indexOf("<document");

            if (docTagEnd == -1)
                docTagEnd = documentText.indexOf("</document>");
            
            //no document tag try reding the whole document
            if ((docTagEnd < 0) && (docTagStart < 0)) {
                docTagStart      = documentText.indexOf("<kml");
                docTagEnd        = documentText.indexOf("</kml>");
                
                if (docTagStart < 0)
                    docTagStart = 0;
                
                if (docTagEnd < 0)
                    docTagEnd = documentText.length();
                
                docTagStartClose = documentText.indexOf(">", docTagStart) + 1;

                if ((docTagStartClose > 0) && (docTagEnd > docTagStartClose)) {
                    docTagContents   = documentText.substring((docTagStartClose), docTagEnd);
                    documentTag      = new XMLTag("Document", docTagContents);
                }
            } else {
                //docTagStartClose = docTagStart + 10;
                docTagStartClose = documentText.indexOf(">", docTagStart) + 1;
                
                if (docTagStart == -1) {
                    docTagStart      = documentText.indexOf("<osm");
                    docTagStartClose = documentText.indexOf(">", docTagStart) + 1;
                }

                if (docTagEnd == -1) 
                    docTagEnd = documentText.indexOf("</osm>");
                
                if (docTagEnd == -1) 
                    docTagEnd = documentText.length();              
                
                if (docTagStart > -1 && docTagEnd > -1) {
                    docTagContents = documentText.substring((docTagStartClose), docTagEnd);
                    documentTag    = new XMLTag("Document", docTagContents);                    
                } else {
                    System.out.println("Could not find Document Tag");
                }
            } //end document tag start check
        } catch (Exception e) {
            System.err.println("Error in XMLParser.parseDocument() - " + e);
        }

        return documentTag;
    }
    
}
