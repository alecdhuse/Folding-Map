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
package co.foldingmap.GUISupport.htmlRendering;

import co.foldingmap.ResourceHelper;
import co.foldingmap.dataStructures.SmartTokenizer;
import co.foldingmap.xml.XMLTag;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Class for rendering HTML and drawing it to a Graphics2D class.
 * 
 * @author Alec
 */
public class HtmlRenderer {
    protected ArrayList<HtmlRenderInstruction>  instructions;
    protected boolean                           isHtml, rendered;
    protected float                             x, y, height, width;
    protected ResourceHelper                    helper;
    protected String                            html;
    protected XMLTag                            docTag;
    
    public HtmlRenderer(String html, float x, float y, float height, float width) {
        this.html         = html;
        this.x            = x;
        this.y            = y;
        this.height       = height;
        this.width        = width;
        this.isHtml       = containsHTML(html);
        this.instructions = new ArrayList<HtmlRenderInstruction>();
        this.rendered     = false;        
        this.helper       = ResourceHelper.getInstance();
        
        if (isHtml)
            this.docTag = new XMLTag("Document", html);
    }         
    
    /**
     * Returns if the given text contains HTML.
     * 
     * @param text
     * @return 
     */
    private boolean containsHTML(String text) {
        String lText = text.toLowerCase();
        
        if (lText.contains("<a>")    ||
            lText.contains("<b>")    ||    
            lText.contains("<body>") ||    
            lText.contains("<html>") ||
            lText.contains("<i>")    ||
            lText.contains("<img")   ||    
            lText.contains("<p>")) {
            
            return true;
        } else {
            return false;
        }
    }
    
    public void draw(Graphics2D g2) {
        if (this.rendered == false)
            render(g2);
        
        for (HtmlRenderInstruction hri: instructions)
            hri.draw(g2);        
    }      
    
    public Rectangle2D getBounds(Graphics2D g2) {
        double      hriWidth;
        Rectangle2D hriBounds;
        Rectangle2D renderBounds = null;
        
        if (this.rendered == false)
            render(g2);
        
        for (HtmlRenderInstruction hri: instructions) {
            hriBounds = hri.getBounds(g2);
            hriWidth  = ((hriBounds.getX() - x) + hriBounds.getWidth());
            
            if (renderBounds == null) {
                renderBounds = hriBounds;
            } else {                
                double x = Math.min(renderBounds.getMinX(),   hriBounds.getMinX());
                double y = Math.min(renderBounds.getMinY(),   hriBounds.getMinY());
                double w = Math.max(renderBounds.getWidth(),  hriWidth);                
                double t = (hriBounds.getMinY() - y) + hriBounds.getHeight();
                double h = Math.max(renderBounds.getHeight(), t);
                renderBounds.setRect(x, y, w, h);
            }
        }
        
        return renderBounds;
    }
    
    public void render(Graphics2D g2) {
        if (isHtml) {
            renderHtml(g2);            
        } else {
            renderText(g2);
        }        
    }
    
    private ImageRenderInstruction processImageInstruction(SmartTokenizer st, 
                                                           String currentToken, 
                                                           String whitespace, 
                                                           float lineX, 
                                                           float lineY) {
        
        while(st.hasMore()) {
            currentToken = st.nextToken();      

            if (currentToken.substring(0, 3).equalsIgnoreCase("src")) {
                String imgSrc = currentToken.substring(5);

                if (imgSrc.endsWith("\">")) {
                    imgSrc = imgSrc.substring(0, imgSrc.length() - 2);
                } else if (!imgSrc.endsWith("\"")) {
                    imgSrc = imgSrc + whitespace + st.getTextTo('"');
                    st.jumpAfterChar('>');
                } else if (imgSrc.endsWith("\"")) {
                    imgSrc = imgSrc.substring(0, imgSrc.length() - 1);
                    st.jumpAfterChar('>');
                }

                return new ImageRenderInstruction(helper.getBufferedImage(imgSrc), lineX, lineY);
            }
        }     
        
        return null;
    }
    
    public void renderHtml(Graphics2D g2) {
        float           currentLineWidth, currentLineX, currentLineY, maxLineHeight;
        float           lineSpacing;
        FontMetrics     fontMetrics;
        Rectangle2D     textBounds;
        SmartTokenizer  st;
        String          currentToken, whitespace;
        StringBuilder   currentLine;  
        XMLTag          startTag;
        
        if (docTag.containsSubTag("html")) {
            startTag = docTag.getSubtag("html"); 
        } else {
            startTag = docTag;
        }
        
        if (startTag.containsSubTag("body")) 
            startTag = startTag.getSubtag("body");                                
        
        //init
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN)); 
        fontMetrics      = g2.getFontMetrics();
        maxLineHeight    = 1;
        currentLineWidth = 0;
        currentLineX     = x;
        currentLineY     = y;
        lineSpacing      = 3;
        currentLine      = new StringBuilder();
        
        for (XMLTag currentTag: startTag.getSubtags()) {
            st = new SmartTokenizer(currentTag.getTagContent());
            
            while(st.hasMore()) {
                currentToken = st.nextToken();
                whitespace   = st.getNextWhiteSpace();
                
                if (currentToken.equalsIgnoreCase("<img")) {
                    if (currentLine.length() > 0) {
                        //need to write out text.
                        instructions.add(new TextRenderInstruction(currentLine.toString(), g2.getFont(), currentLineX, currentLineY));
                        textBounds    = fontMetrics.getStringBounds(currentLine.toString(), g2);
                        currentLineX += textBounds.getWidth();  
                        currentLine   = new StringBuilder();
                    }
                    
                    ImageRenderInstruction iri = processImageInstruction(st, currentToken, whitespace, currentLineX, currentLineY - 8);
                    
                    if (iri != null) {
                        instructions.add(iri);
                        currentLineX += (float) iri.getBounds(g2).getWidth();
                        maxLineHeight = (float) Math.max(maxLineHeight, iri.getBounds(g2).getHeight()); 
                    }
                } else if (currentToken.equalsIgnoreCase("<br>")) {
                    //new line
                    if (currentLine.length() > 0) {
                        instructions.add(new TextRenderInstruction(currentLine.toString(), g2.getFont(), currentLineX, currentLineY));
                        textBounds    = fontMetrics.getStringBounds(currentLine.toString(), g2);
                    }                  
                    
                    currentLineY += maxLineHeight + lineSpacing;
                    currentLineX  = x;
                } else {                    
                    textBounds = fontMetrics.getStringBounds(currentToken + whitespace, g2);

                    if ((currentLineWidth + textBounds.getWidth()) < width) {
                        currentLineWidth += textBounds.getWidth();
                        currentLine.append(currentToken);
                        currentLine.append(whitespace);
                        maxLineHeight = (float) Math.max(maxLineHeight, textBounds.getHeight());                    
                    } else {
                        //New Line
                        instructions.add(new TextRenderInstruction(currentLine.toString(), g2.getFont(), currentLineX, currentLineY));

                        currentLineY    += maxLineHeight + lineSpacing;
                        currentLine      = new StringBuilder();
                        maxLineHeight    = 1;
                        currentLineWidth = 0;
                        currentLineX     = x;
                        
                        currentLine.append(currentToken);
                        currentLine.append(whitespace);
                        maxLineHeight = (float) Math.max(maxLineHeight, textBounds.getHeight());                    
                    }
                }
            } // String Tokenizer has more tokens loop
            
            //draw final line
            if (currentLine.length() > 0) {
                instructions.add(new TextRenderInstruction(currentLine.toString(), g2.getFont(), currentLineX, currentLineY));
                textBounds    = fontMetrics.getStringBounds(currentLine.toString(), g2);
                currentLineX += textBounds.getWidth();
            }
        }
        
        rendered = true;
    }    
    
    public void renderText(Graphics2D g2) {
        float           currentLineWidth, currentLineY, maxLineHeight;
        float           lineSpacing;
        FontMetrics     fontMetrics;
        Rectangle2D     textBounds;
        SmartTokenizer  st;
        String          currentToken, whitespace;
        StringBuilder   currentLine;  
        
        //init
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN)); 
        fontMetrics      = g2.getFontMetrics();
        maxLineHeight    = 1;
        currentLineWidth = 0;
        currentLineY     = y;
        lineSpacing      = 3;
        currentLine      = new StringBuilder();
        
        st = new SmartTokenizer(html);

        while(st.hasMore()) {
            currentToken = st.nextToken();
            whitespace   = st.getNextWhiteSpace();
            textBounds   = fontMetrics.getStringBounds(currentToken + whitespace, g2);

            if ((currentLineWidth + textBounds.getWidth()) < width) {
                currentLineWidth += textBounds.getWidth();
                currentLine.append(currentToken);
                currentLine.append(whitespace);
                maxLineHeight = (float) Math.max(maxLineHeight, textBounds.getHeight());                    
            } else {
                //New Line
                instructions.add(new TextRenderInstruction(currentLine.toString(), g2.getFont(), x, currentLineY));
                
                currentLineY    += maxLineHeight + lineSpacing;
                currentLine      = new StringBuilder();
                maxLineHeight    = 1;
                currentLineWidth = 0;

                currentLine.append(currentToken);
                currentLine.append(whitespace);
                maxLineHeight = (float) Math.max(maxLineHeight, textBounds.getHeight());                    
            }
        } // String Tokenizer has more tokens loop

        //draw final line
        instructions.add(new TextRenderInstruction(currentLine.toString(), g2.getFont(), x, currentLineY));
        rendered = true;
    }        
}
