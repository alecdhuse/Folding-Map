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

import java.util.ArrayList;

/**
 * This class is used to write KML to a String buffer.  Used with clipboard
 * functions and others.
 * 
 * @author Alec
 */
public class XmlBuffer extends XmlOutput {
    private ArrayList<String> openTags;
    private int               tabIndentCount;
    private StringBuilder     kml;
    
    /**
     * Creates a new buffer
     */
    public XmlBuffer() {
        this.kml      = new StringBuilder();
        this.openTags = new ArrayList<String>();
    }
    
    /**
     * Closes the currently open tag.  If the tag is not open nothing happens.
     * 
     * @param tag 
     */
    @Override
    public void closeTag(String tag) {
        String currentTag;

        try {
            for (int i = (openTags.size() - 1); i >= 0; i--) {
                currentTag = openTags.get(i);

                if (currentTag.equalsIgnoreCase(tag)) {
                    openTags.remove(i);
                    tabIndentCount--;

                    kml.append(getIndent());
                    kml.append("</");
                    kml.append(tag);
                    kml.append(">");
                    kml.append("\n");                    
                    
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error in KmlBuffer.closeTag() - " + e);
        }
    }           
    
    /**
     * Open a new tag with a given name.
     * 
     * @param tagName 
     */
    @Override
    public void openTag(String tagName) {
        int indexOfSpace = tagName.indexOf(" ");
        
        try {
            if (indexOfSpace > 0) {
                openTags.add(tagName.substring(0, indexOfSpace));
            } else {
                openTags.add(tagName);
            }            
            
            kml.append(getIndent());
            kml.append("<");
            kml.append(tagName);
            kml.append(">");
            kml.append("\n");

            tabIndentCount++;
        } catch (Exception e) {
            System.err.println("Error in KmlBuffer.OpenTag(String) - " + e);
        }
    }    
    
    /**
     * Write a new key value pair tag of the form:
     * 
     *      <pair key="KEY">VALUE</pair>
     * 
     * @param key
     * @param Value 
     */
    @Override
    public void writePairTag(String key, String value) {
        kml.append(getIndent());
        kml.append("<pair key=\"");
        kml.append(key);
        kml.append("\">");
        kml.append(value);
        kml.append("</pair>\n");
    }
    
    /**
     * Write a new tag with a given name and content.
     * 
     * @param tagName
     * @param content 
     */
    @Override
    public void writeTag(String tagName, String content) {
        try {
            kml.append(getIndent());
            kml.append("<"  + tagName + ">");
            kml.append(content);
            kml.append("</" + tagName + ">");
            kml.append("\n");
        } catch (Exception e) {
            System.err.println("Error in KmlBuffer.writeTag(String, String) - " + e);
        }
    }    
    
    /**
     * Write text to the XML buffer.
     * 
     * @param text 
     */
    @Override
    public void writeText(String text) {
        try {
            kml.append(text);
        } catch (Exception e) {
            System.err.println("Error in KmlWriter.writeTextLine(String) - " + e);
        }
    }    
    
    /**
     * Writes a line of text and a \n to the KML buffer.
     * 
     * @param text 
     */
    @Override
    public void writeTextLine(String text) {
        try {
            kml.append(getIndent());
            kml.append(text);
            kml.append("\n");
        } catch (Exception e) {
            System.err.println("Error in KmlBuffer.writeTextLine(String) - " + e);
        }
    }    
    
    /**
     * Returns a string of all the KML in this buffer.
     * 
     * @return 
     */
    @Override
    public String toString() {
        return kml.toString().trim();
    }
}
