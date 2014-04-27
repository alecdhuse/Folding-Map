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

/**
 * The Parent class for KMLBuffer and KMLWritter.
 * 
 * @author Alec
 */
public abstract class XmlOutput {
    protected int   tabIndentCount;
    
    public abstract void closeTag(String tag);
    public abstract void openTag(String tagName);
    public abstract void writePairTag(String key, String value);
    public abstract void writeTag(String tagName, String content);
    public abstract void writeText(String text);
    public abstract void writeTextLine(String text);
    
    /**
     *  Decreases the indent for the XML Output
     */
    public void decreaseIndent() {
        if (tabIndentCount > 0)
            tabIndentCount--;
    }    
    
    /**
     * Returns a String with the amount of current indent being used.
     * 
     * @return 
     */
    public String getIndent() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < tabIndentCount; i++)
            sb.append("\t");

        return sb.toString();
    }    
    
    /**
     *  Increases the indent for the XML Output
     */
    public void increaseIndent() {
        tabIndentCount++;
    }
}
