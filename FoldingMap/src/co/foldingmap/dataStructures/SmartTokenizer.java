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
package co.foldingmap.dataStructures;

/**
 *
 * @author Alec
 */
public class SmartTokenizer {
    private int     currentIndex;
    private String  string;
    
    public SmartTokenizer(String string) {
        this.string       = string;
        this.currentIndex = 0;
    }
    
    /**
     * Returns the text between a delimiter of the same character, starting at
     * the current index pointer.  Handles if that character is escaped by '\'
     * 
     * @param delim
     * @return 
     */
    public String getContent(char delim) {
        boolean continueLoop;
        char    currentChar, prevChar;
        int     startIndex;
        String  content;
        
        startIndex   = string.indexOf(delim, currentIndex);
        
        if (startIndex >= 0) {
            currentIndex = startIndex + 1;
            continueLoop = true;
            prevChar     = ' ' ;
            content      = "";

            while (continueLoop && currentIndex < string.length()) {
                currentChar = string.charAt(currentIndex);

                if (currentIndex > 0)
                    prevChar = string.charAt(currentIndex - 1);

                if (currentChar == delim && prevChar != '\\') {
                    continueLoop = false;
                    content      = string.substring(startIndex + 1, currentIndex);
                    currentIndex++;
                } else {
                    currentIndex++;
                }
            }

            return content;
        } else {
            currentIndex = string.length();
            return "";
        }
    }
    
    /**
     * Returns the text between the two delimiting characters.  
     * Deals with nested characters.
     * 
     * @param startDelim
     * @param endDelim
     * @return 
     */
    public String getContent(char startDelim, char endDelim) {
        char currentChar;
        int  openDelims, startIndex;
        
        startIndex   = string.indexOf(startDelim, currentIndex);
        openDelims   = 1;
        currentIndex = startIndex + 1;
        
        while (openDelims > 0 && currentIndex < string.length()) {
            currentChar = string.charAt(currentIndex);
            
            if (currentChar == startDelim) {
                openDelims++;
            } else if (currentChar == endDelim) {
                openDelims--;
            }
            
            currentIndex++;
        }
                
        return string.substring(startIndex + 1, currentIndex - 1);        
    }
    
    /** 
     * Returns the next char in the tokenizer, without advancing the index 
     * pointer.  Spaces are ignored.
     * 
     * @return 
     */
    public char getNextChar() {        
        if (currentIndex < string.length()) {
            //Skip over whitespace 
            moveToNextNonWhiteSpace();

            return string.charAt(currentIndex);
        } else {
            //end of string, return blank char.
            return 0;
        }
    }
    
    /**
     * Returns a String consisting of the whitespace starting at the current index ending at the 
     * first occurrence of non-whitespace.
     * 
     * @return 
     */
    public String getNextWhiteSpace() {
        int startIndex = currentIndex;
        
        moveToNextNonWhiteSpace();
        
        int endIndex = currentIndex;
        
        return string.substring(startIndex, endIndex);
    }
    
    /**
     * Returns text from the current pointer to the first instance of a given
     * char.  The index pointer is set to one past the stop point.
     * 
     * If the end of the String is reach without reaching that character, 
     * the substring from the index pointer to the String end will be returned.
     * 
     * @param character
     * @return 
     */
    public String getTextTo(char character) {
        String text;    
        int    charIndex = string.indexOf(character, currentIndex);
        
        if (charIndex > 0) {
            text = string.substring(currentIndex, charIndex);
            currentIndex = charIndex + 1;
        } else {
            text = string.substring(currentIndex);
            currentIndex = string.length();
        }        
        
        return text;
    }
    
    /**
     * Returns if there is more of the string after the index pointer.
     * 
     * @return 
     */
    public boolean hasMore() {
        if (currentIndex < string.length()) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Returns the index of the first occurrence of a given String.
     * Returns -1 if the string is not found.
     * 
     * @param searchString
     * @return 
     */
    public int indexOf(String searchString) {
        return string.indexOf(searchString);     
    }    
    
    private boolean isWhitespace(char c) {
        if (c == ' '  || c == '\n' || c == '\t') {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Jumps the current pointer index to after the next occurrence of a given 
     * char.
     * 
     * @param character 
     */
    public void jumpAfterChar(char character) {
        int newIndex = string.indexOf(character, currentIndex); 
        
        if (newIndex >= 0) {
            currentIndex = newIndex + 1;
        } else {
            currentIndex = string.length();
        }
    }    
    
    /**
     * Increments the pointer to the next non whitespace character.
     * 
     */
    public void moveToNextNonWhiteSpace() {
        while ((currentIndex < string.length())     &&
               (string.charAt(currentIndex) == ' '  ||
                string.charAt(currentIndex) == '\n' ||
                string.charAt(currentIndex) == '\t')) {

            currentIndex++;        
        }       
    }    
    
    /**
     * Increments the pointer to the next whitespace character.
     * 
     */
    public void moveToNextWhiteSpace() {
        while ((currentIndex < string.length())     &&
                string.charAt(currentIndex) != ' '  &&
                string.charAt(currentIndex) != '\n' &&
                string.charAt(currentIndex) != '\t') {

                currentIndex++;        
        }    
    }
    
    /**
     * Returns the next white space delimited token.  If index pointer is in the
     * middle of a token, the pointer is incremented to the next whitespace 
     * character to start the token.
     * 
     * @return 
     */
    public String nextToken() {
        int startIndex;
        
        if (currentIndex > 0) {
            if (!isWhitespace(string.charAt(currentIndex - 1))) 
                moveToNextWhiteSpace();            
        }
        
        startIndex = currentIndex;                
        moveToNextNonWhiteSpace();
        
        //find the next whitespace
        moveToNextWhiteSpace();
        
        return (string.substring(startIndex, currentIndex)).trim();
    }
    
    /**
     * Returns the text after the index pointer.
     * 
     * @return 
     */
    @Override
    public String toString() {
        return string.substring(currentIndex);
    }
}
