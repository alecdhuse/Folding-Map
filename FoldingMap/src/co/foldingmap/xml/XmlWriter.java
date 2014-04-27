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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * This Class is used to write KML tags to a file.
 * 
 * @author Alec
 */
public class XmlWriter extends XmlOutput {
    private ArrayList<String> openTags;
    private BufferedWriter    bw;
    private File              fileOut;    

    /**
     * Constructor with a file to write to,
     * 
     * @param fileOut 
     */
    public XmlWriter(File fileOut) {
        try {
            this.fileOut        = fileOut;
            this.bw             = new BufferedWriter(new FileWriter(fileOut));
            this.tabIndentCount = 0;
            this.openTags       = new ArrayList<String>();
        } catch (Exception e) {
            System.err.println("Error in KmlWriter Constructor(File) - " + e);
        }
    }

    /**
     * Close the KML file being written to.
     */
    public void closeFile() {
        try {
            bw.close();
        } catch (Exception e) {
            System.err.println("Error in KmlWriter.closeFile() - " + e);
        }
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

                    bw.write(getIndent());
                    bw.write("</");
                    bw.write(tag);
                    bw.write(">");
                    bw.write("\n");                    
                    
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error in KmlWriter.closeTag() - " + e);
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
            
            bw.write(getIndent());
            bw.write("<");
            bw.write(tagName);
            bw.write(">");
            bw.write("\n");

            tabIndentCount++;
        } catch (Exception e) {
            System.err.println("Error in KmlWriter.OpenTag(String) - " + e);
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
        try {
            bw.write(getIndent());
            bw.write("<pair key=\"");
            bw.write(key);
            bw.write("\">");
            bw.write(value);
            bw.write("</pair>\n");   
        } catch (Exception e) {
            System.err.println("Error in KmlWriter.writePairTag(String, String) - " + e);
        }            
    }    
    
    /**
     * Write a new tag with a given name and content.
     * 
     * @param tagName
     * @param content 
     */
    @Override
    public void writeTag(String tagName, String content) {
        String  tagClose;
        
        try {
            if (content == null)
                content = "";
            
            if (tagName.indexOf(" ") > 0) {
                tagClose = tagName.substring(0, tagName.indexOf(" "));
            } else {
                tagClose = tagName;
            }
            
            bw.write(getIndent());
            bw.write("<"  + tagName + ">");
            bw.write(content);
            bw.write("</" + tagClose + ">");
            bw.write("\n");
        } catch (Exception e) {
            System.err.println("Error in KmlWriter.writeTag(String, String) - " + e);
        }
    }

    /**
     * Write text to the KML file.
     * 
     * @param text 
     */
    @Override
    public void writeText(String text) {
        try {
            bw.write(text);
        } catch (Exception e) {
            System.err.println("Error in KmlWriter.writeTextLine(String) - " + e);
        }
    }

    /**
     * Writes a line of text and a \n to the KML file.
     * 
     * @param text 
     */
    @Override
    public void writeTextLine(String text) {
        try {
            bw.write(getIndent());
            bw.write(text);
            bw.write("\n");
        } catch (Exception e) {
            System.err.println("Error in KmlWriter.writeTextLine(String) - " + e);
        }
    }
    
}
