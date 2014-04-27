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

import co.foldingmap.dataStructures.SmartTokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Represents an XML tag.
 * 
 * @author Alec
 */
public class XMLTag {
    ArrayList<XMLTag>       subTags;
    HashMap<String, String> properties;
    String                  tagName, tagContent;
    
    /**
     * Constructor for objects of class XMLTag
     * 
     * @param tagName
     * @param subTag
     */
    public XMLTag(String tagName, XMLTag subTag) {
        this.tagName    = (tagName);
        this.subTags    = new ArrayList<XMLTag>();
        
        subTags.add(subTag);             
    }    
    
    /**
     * Constructor for objects of class XMLTag
     * 
     * @param tagName
     * @param tagContent
     * 
     */
    public XMLTag(String tagName, String tagContent) {
        this.tagName    = (tagName);
        this.tagContent = (tagContent);

        subTags = new ArrayList<XMLTag>();

        //Tag has Properties
        if (tagName.contains("=")) {            
            parseProperties(tagName);            
        }           
        
        if (tagName.equalsIgnoreCase("description")) {
            /**
             * Do nothing, because it could contain other tags, but we want
             * to leave it in the description tag and not break it up.
             */         
        } else if (tagContent.contains("<")) {
            //there are subtags
            parseSubTags();
        }
    }

    /**
     * Adds a subtag to this XML Tag.
     * 
     * @param newSubtag 
     */
    public void addSubtag(XMLTag newSubtag) {
        subTags.add(newSubtag);
    }

    /**
     * Adds a list of XML Tags to this XML Tag.
     * 
     * @param newSubtags 
     */
    public void addSubtags(ArrayList<XMLTag> newSubtags) {
        subTags.addAll(newSubtags);
    }

    /**
     * Returns if this tag has a subtag with a given name.
     * 
     * @param subtagName
     * @return 
     */
    public boolean containsSubTag(String subtagName) {
        boolean         subtagExists = false;
        String          token;
        StringTokenizer st;
        
        for (XMLTag currentTag: subTags) {        
            if (!currentTag.getTagName().contains(" ")) {
                if (currentTag.getTagName().equalsIgnoreCase(subtagName)) {
                    subtagExists = true;
                    break;
                }
            } else {
                st    = new StringTokenizer(currentTag.getTagName());
                token = st.nextToken();
                
                if (token.equalsIgnoreCase(subtagName)) {
                    subtagExists = true;
                    break;                    
                }
            }
        }
        
        return subtagExists;
    }
    
    /**
     * Converts a String with HTML safe text to normal text.
     * 
     * @param safeText
     * @return 
     */
    public static String convertSafeText(String safeText) {
        String text;
        
        text = safeText.replaceAll("&apos;", "'");
        text = text.replaceAll("&amp;" , "&");      
        
        return text;
    }
    
    /**
     * Creates an XML tag object and calls the toString method, 
     * returning the result.
     * 
     * @param tagName
     * @param tagContent
     * @return 
     */
    public static String createTagText(String tagName, String tagContent) {
        XMLTag  newTag;

        newTag = new XMLTag(tagName, tagContent);

        return newTag.toString();
    }

    /**
     * Returns a list of the gx subtags contained in this XML tag.
     * 
     * @return 
     */
    public ArrayList<XMLTag> getGxSubtags() {
        ArrayList<XMLTag>  gxSubtags   = new ArrayList<XMLTag>();

        for (XMLTag currentTag: subTags) {
            if (currentTag.getTagName().startsWith("gx:")) {
                gxSubtags.add(currentTag);
            }
        }

        return gxSubtags;
    }

    /**
     * Returns the value of the given tag property.  
     * If the tag does not exist a blank String is returned.
     * 
     * @param property
     * @return 
     */
    public String getPropertyValue(String property) {
        if (properties == null) {
            return "";
        } else {
            String value = properties.get(property);
            
            if (value == null) {
                return "";
            } else {
                return value;
            }
        }
    }
    
    /**
     * Returns XML safe text, for writing to a file.
     * 
     * @param text
     * @return 
     */
    public static String getSafeText(String text) {
        
        if (text != null && text.length() > 0) {
            text = text.replaceAll("'", "&apos;");
            text = text.replaceAll("&", "&amp;");
        }
        
        return text;
    }

    /**
     * Returns the content of this tag.
     * 
     * @return 
     */
    public String getTagContent() {    
        return convertSafeText(tagContent);
    }

    /**
     * Returns the name of this tag.
     * 
     * @return 
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * Returns the content of a subtag with a given name.
     * 
     * @param name
     * @return 
     */
    public String getSubtagContent(String name) {
        try {
            String tagContent  = new String();
            XMLTag currentTag;

            for (int i = 0; i < subTags.size(); i++) {
                currentTag = (XMLTag) subTags.get(i);
                if (currentTag.getTagName().equalsIgnoreCase(name)) {
                    tagContent = currentTag.getTagContent();
                    break;
                }
            }

            return tagContent;
        } catch (Exception e) {
            System.err.println("Error Getting Subtag Content(String) - " + e);
            return "";
        }
    }

    /**
     * Returns an ArrayList of all subtags in this tag.
     * 
     * @return 
     */
    public ArrayList<XMLTag> getSubtags() {
        return subTags;
    }

    /**
     * Returns the subtag as an XML String.
     * 
     * @return 
     */
    public String getSubtagsAsString() {
        StringBuilder string = new StringBuilder();

        for (XMLTag currentSubTag: subTags) {
            string.append(currentSubTag.toString());
        }

        return string.toString();
    }

    /**
     * Returns the first occurance of a XMLTag with the supplied name
     * @param name
     * @return 
     */
    public XMLTag getSubtag(String name) {
        String          token;
        StringTokenizer st;
        XMLTag          returnTag = null;

        for (XMLTag currentSubTag: subTags) {
            if (!currentSubTag.getTagName().contains(" ")) {
                if (currentSubTag.getTagName().equalsIgnoreCase(name)) {
                    returnTag = currentSubTag;
                    break;
                }
            } else {
                st    = new StringTokenizer(currentSubTag.getTagName());
                token = st.nextToken();
                
                if (token.equalsIgnoreCase(name)) {
                    returnTag = currentSubTag;
                    break;
                }                
            }
        }

        return returnTag;
    }

    /**
     * Returns all subtags that match the name supplied.
     * 
     * @param name
     * @return 
     * @deprecated
     */    
    public ArrayList<XMLTag> getSubtags(String name) {
        ArrayList<XMLTag> returnSubtags     = new ArrayList<XMLTag>();
        XMLTag            currentTag;
                
        for (int i = 0; i < subTags.size(); i++) {
            currentTag = (XMLTag) subTags.get(i);
            
            if (currentTag.getTagName().equalsIgnoreCase(name))
                returnSubtags.add(currentTag);
        }

        return returnSubtags;
    }

    /**
     * Returns all subtags that match the name supplied.
     * This version works better on tags with properties.      
     * 
     * @param name
     * @return 
     */
    public ArrayList<XMLTag> getSubtagsByName(String name) {
        ArrayList<XMLTag> returnSubtags     = new ArrayList<XMLTag>();
        int               tagNameStop;
        String            tagName;
        XMLTag            currentTag;
                
        for (int i = 0; i < subTags.size(); i++) {
            currentTag  = (XMLTag) subTags.get(i);
            tagNameStop = currentTag.getTagName().indexOf(" ");
            
            if (tagNameStop < 0) tagNameStop = currentTag.getTagName().length();
            
            tagName = currentTag.getTagName().substring(0, tagNameStop);
                    
            if (tagName.equalsIgnoreCase(name))
                returnSubtags.add(currentTag);
        }

        return returnSubtags;
    }    
    
    /**
     * Returns the first subtag that starts with the given text.
     * 
     * @param prefix
     * @return 
     */
    public XMLTag getSubtagStartsWith(String prefix) {
        XMLTag returnTag = null;

        for (XMLTag currentSubTag: subTags) {
            if (currentSubTag.getTagName().startsWith(prefix)) {
                returnTag = currentSubTag;
                break;
            }
        }

        return returnTag;        
    }
    
    /**
     * Returns subtags that match the name supplied.
     * 
     * @param name
     * @return 
     */
    public ArrayList<XMLTag> getTagSubtags(String name) {
        ArrayList<XMLTag> returnSubtags     = new ArrayList<XMLTag>();
        XMLTag            currentTag;

        for (int i = 0; i < subTags.size(); i++) {
            currentTag = (XMLTag) subTags.get(i);
            if (currentTag.getTagName().equalsIgnoreCase(name)) {
                returnSubtags = currentTag.getSubtags();
                break;
            }
        }

        return returnSubtags;
    }

    /**
     * Returns all subtags with a given name.
     * 
     * @param name
     * @return 
     */
    public ArrayList<XMLTag> getTags(String name) {
        String            nameToSearchFor = name;
        StringTokenizer   st;
        ArrayList<XMLTag> tags = new ArrayList<XMLTag>();
        XMLTag            currentTag;

        try {
            for (int i = 0; i < subTags.size(); i++) {
                currentTag = (XMLTag) subTags.get(i);
                st         = new StringTokenizer(currentTag.getTagName());

                if (st.hasMoreTokens())
                    nameToSearchFor = st.nextToken();

                if (nameToSearchFor.equalsIgnoreCase(name)) {
                    tags.add(currentTag);
                }
            }
        } catch (Exception e) {
            System.err.println("Error in XMLTag.getTags(" + name + ") - " + e);
        }
        
        return tags;
    }

    /**
     * For tags that have the form <object id="something">
     * Returns the value of a tag, the value being the text between the 
     * quotation marks of a tag's name.
     * 
     * If no value is present, an empty string is returned.
     * 
     * @return 
     */
    public String getTagValue() {
        int     start, end;
        String  value;
        
        start = tagName.indexOf("\"");
        
        if (start > 0) {
            end = tagName.lastIndexOf("\"");
            value = tagName.substring(start + 1, end);
            
            return value;
        } else {
            return "";
        }
    }
    
    /**
     * Returns if this tag has subtags.
     * 
     * @return 
     */
    public boolean hasSubTags() {
        if (subTags.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns true if the text matches the start of this tags name.  
     * Matches are case insensitive.
     * 
     * @param text
     * @return 
     */
    public boolean nameStartsWith(String text) {
        String start;
        
        start = this.tagName.substring(0, text.length());
        
        return (text.equalsIgnoreCase(start));
    }
    
    /**
     * Parses out tag properties.
     * 
     * @param tagName 
     */
    private void parseProperties(String tagName) {
            //Tag has Properties
            if (properties == null)
                properties = new HashMap<String, String>();
            
            SmartTokenizer st = new SmartTokenizer(tagName);
            
            while (st.hasMore()) {
                st.jumpAfterChar(' ');
                String propName  = st.getTextTo('=');

                st.jumpAfterChar('"');
                String propValue = st.getTextTo('"');
                
                properties.put(propName, propValue);   
            }
    }
    
    /**
     * Parses subtag text ad creates XMLTag objects from it.
     * 
     */
    private void parseSubTags() {
        int             equalsIndex, tagStart, tagEnd, contentStart, offset;
        String          currentToken, tagName, subTagContent, subTagName, closingTag;
        StringTokenizer st, stEquals;
        XMLTag          newTag, newSubtag;

        offset     = 0;
        closingTag = "";

        try {
            while (offset < tagContent.length()) {
                tagStart      = tagContent.indexOf("<", offset);
                tagEnd        = tagContent.indexOf(">", offset);
                contentStart  = (tagEnd + 1);

                if ((tagStart >= 0) && (tagEnd >= 0) && (tagEnd > tagStart)) {
                    tagName = tagContent.substring(tagStart + 1, tagEnd);
                    st      = new StringTokenizer(tagName);

                    //if ((tagName.endsWith("/")) || (tagName.indexOf("=") >= 0)) {
                    if ((tagName.endsWith("/"))) {
                        //for single tags like <br />
                        newTag = new XMLTag(st.nextToken(), "");

                        if (tagName.indexOf("=") >= 0) {
                            //tags with values in them like <node id="619207332"
                            stEquals = new StringTokenizer(tagName);

                            while (stEquals.hasMoreElements()) {
                                currentToken = stEquals.nextToken();
                                equalsIndex  = currentToken.indexOf("=");

                                if (equalsIndex >= 0) {
                                    subTagName    = currentToken.substring(0, equalsIndex);
                                    subTagContent = currentToken.substring(equalsIndex + 1);

                                    if (subTagContent.startsWith("\"")) {
                                        subTagContent = subTagContent.substring(1);
                                    }

                                    if (subTagContent.endsWith("\"")) {
                                        subTagContent = subTagContent.substring(0, subTagContent.length() - 1);
                                    }

                                    if (subTagContent.endsWith("\"/")) {
                                        subTagContent = subTagContent.substring(0, subTagContent.length() - 2);
                                    }

                                    newSubtag = new XMLTag(subTagName, subTagContent);
                                    newTag.addSubtag(newSubtag);
                                }
                            } //end while loop

                            offset = tagEnd + 1;
                        } else {
                            offset     = tagContent.length();
                            tagContent = "";
                        }

                        addSubtag(newTag);
                        //tagContent = "";
                    } else {
                        if (tagName.endsWith("/")) {
                            //for closing tags
                            tagStart      = tagName.indexOf(" ");
                            tagEnd        = tagName.indexOf("/");
                            subTagContent = tagName.substring(tagStart, tagEnd);
                            subTagContent = subTagContent.trim();
                            offset        = contentStart + 1;
                            tagName       = st.nextToken();
                            newTag        = new XMLTag(tagName, subTagContent);
                            subTags.add(newTag);
                        } else {
                            if (st.countTokens() == 1) {
                                closingTag = "</" + tagName + ">";
                                tagStart = tagContent.indexOf(closingTag, offset);
                            } else if (st.countTokens() > 1) {
                                //tags of the format: <Polygon id="University">
                                closingTag = "</" + st.nextToken() + ">";
                                tagStart = tagContent.indexOf(closingTag, offset);
                            }

                            if (tagStart >= 0) {
                                subTagContent = tagContent.substring((tagEnd + 1) , tagStart);
                                subTagContent = subTagContent.trim();                               
                                newTag        = new XMLTag(tagName, subTagContent);
                                offset        = (tagStart + closingTag.length());                                
                                
                                subTags.add(newTag);
                            } else {
                                offset = contentStart;
                            }
                        }
                    }
                } else {
                    offset = tagContent.length();
                }
            }

            if (subTags.size() > 0) {
                tagContent = "";
            } else {
                //no subtags, preserve memory
                subTags.trimToSize();
            }
        } catch (Exception e) {
            System.err.println("Error Parsing Subtags of " + this.tagName + ": " + e);
        }
    } // end of parseSubTags

    /**
     * Outputs a String in XML of this tag.
     * 
     * @return 
     */
    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();

        string.append("<");
        string.append(tagName);
        string.append(">");
        string.append(getSafeText(tagContent));
        
        for (XMLTag currentSubTag: subTags) {
            string.append(currentSubTag.toString());
        }

        string.append("</");
        string.append(tagName);
        string.append(">");        

        return string.toString();
    }
    
}
