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
package co.foldingmap.GUISupport.components;

import co.foldingmap.GUISupport.FileExtensionFilter;
import co.foldingmap.GUISupport.panels.FileChoicePanel;
import co.foldingmap.Logger;
import co.foldingmap.ResourceHelper;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 *
 * @author Alec
 */
public class HtmlTextArea extends JPanel implements ActionListener, KeyListener {
    
    public static int APPLE_COMMAND = 4;
    public static int CONTROL       = 2;

    protected ArrayList<String>         includedImages;
    protected boolean                   addingImage, openParagraphTag;
    protected ColorChooserPopupDialog   colorChooserPopupDialog;
    protected Dialog                    parentWindow;
    protected FileChoicePanel           fileChoicePanel;    
    protected HTMLDocument              htmlDocument;
    protected HTMLEditorKit             defaultHTMLEditorKit;
    protected HyperlinkPopupDialog      hyperlinkPopupDialog;
    protected JButton                   buttonAddImageOk, buttonAddImageCancel;
    protected JButton                   buttonColors, buttonImage, buttonLink;
    protected JPanel                    pannelAddImage, pannelAddImageButtons, panelButtons, panelCenter;
    protected JScrollPane               mainScroll;
    protected JTextPane                 mainText;
    protected JToggleButton             buttonBold, buttonItalics, buttonUnderLine;    
    protected Popup                     colorPopup, imagePopup, newLinkPopup;
    protected PopupFactory              mainPopupFactory;
    protected SimpleAttributeSet        style;    

    /**
     * Constructor for objects of class HtmlTextArea
     */
    public HtmlTextArea(Dialog  parentWindow) {
        init();

        this.parentWindow   = parentWindow;
        this.includedImages = new ArrayList<String>();

        this.setLayout(new BorderLayout());
        this.add(panelButtons,      BorderLayout.NORTH);      
        this.add(panelCenter,       BorderLayout.CENTER);
        panelCenter.add(mainScroll, BorderLayout.CENTER);

        pannelAddImage.add(fileChoicePanel);
        pannelAddImage.add(pannelAddImageButtons);
        
        pannelAddImageButtons.add(buttonAddImageOk);
        pannelAddImageButtons.add(buttonAddImageCancel);
        
        panelButtons.add(buttonImage);
        panelButtons.add(buttonLink);
        panelButtons.add(buttonColors);
        panelButtons.add(buttonBold);
        panelButtons.add(buttonItalics);
        panelButtons.add(buttonUnderLine);

        buttonBold.addActionListener(this);
        buttonColors.addActionListener(this);
        buttonImage.addActionListener(this);
        buttonItalics.addActionListener(this);
        buttonLink.addActionListener(this);
        buttonUnderLine.addActionListener(this);
        buttonAddImageOk.addActionListener(this);
        buttonAddImageCancel.addActionListener(this);
                
//        buttonBold.setBorder(new MetalBorders.ButtonBorder());
//        buttonColors.setBorder(new MetalBorders.ButtonBorder());
//        buttonImage.setBorder(new MetalBorders.ButtonBorder());
//        buttonItalics.setBorder(new MetalBorders.ButtonBorder());
//        buttonLink.setBorder(new MetalBorders.ButtonBorder());
//        buttonUnderLine.setBorder(new MetalBorders.ButtonBorder());
        
        buttonBold.setActionCommand("Bold");
        buttonColors.setActionCommand("Colors");
        buttonImage.setActionCommand("Image");
        buttonItalics.setActionCommand("Italics");
        buttonLink.setActionCommand("Link");
        buttonUnderLine.setActionCommand("Underline");

        mainText.addKeyListener(this);

        createNewParagraph();
        openParagraphTag = true;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        AbstractButton  initiatingAbstractButton;
        Object          initiatingObject;
        String          objectText, objectActionCommand;

        try {
            initiatingObject    = ae.getSource();

            if (initiatingObject instanceof AbstractButton) {
                initiatingAbstractButton = (AbstractButton) initiatingObject;
                objectText               = initiatingAbstractButton.getText();
                objectActionCommand      = initiatingAbstractButton.getActionCommand();
            } else {
                objectText               = initiatingObject.toString();
                objectActionCommand      = ae.getActionCommand();
            }

            if (objectActionCommand.equals("Bold")) {
                changeBold(buttonBold.isSelected());
            } else if (initiatingObject == buttonAddImageCancel) {
                addImage();
            } else if (initiatingObject == buttonAddImageOk) {
                addImage(fileChoicePanel.getText());
                addImage();
            } else if (objectActionCommand.equals("Colors")) {
                if (colorChooserPopupDialog == null) {
                    openColorChooserPopupDialog();
                } else {
                    colorChooserPopupDialog.hideDialog();
                    colorChooserPopupDialog = null;
                }
            } else if (objectText.equals("ColorChooserPopupDialog")) {
                if (objectActionCommand.equals(ColorChooserPopupDialog.COMMAND_COLOR_SELECTED))
                    changeColor(colorChooserPopupDialog.getSelectedColor());

                colorChooserPopupDialog.hideDialog();
                colorChooserPopupDialog = null;            
            } else if (objectText.equals("HyperlinkPopupDialog")) {
                if (objectActionCommand.equals(HyperlinkPopupDialog.COMMAND_OK))
                    addLink(hyperlinkPopupDialog.getLinkText(), hyperlinkPopupDialog.getLinkURL());

                hyperlinkPopupDialog = null;
            } else if (objectActionCommand.equals("Image")) {
                addImage();
            } else if (objectActionCommand.equals("Italics")) {
                changeItalics(buttonItalics.isSelected());
            } else if (objectActionCommand.equals("Link")) {
                if (hyperlinkPopupDialog == null) {
                    openHyperlinkPopupDialog();
                } else {
                    hyperlinkPopupDialog.hideDialog();
                    hyperlinkPopupDialog = null;
                }
            } else if (objectActionCommand.equals("Underline")) {
                changeUnderline(buttonUnderLine.isSelected());
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in HtmlTextArea.actionPerformed - " + e);
        }
    }

    public void addImage2() {                
        if (addingImage == false) {
            fileChoicePanel.clear();
            panelCenter.add(pannelAddImage, BorderLayout.NORTH);      
            addingImage = true;
        } else {
            panelCenter.remove(pannelAddImage);
            addingImage = false;
        }
        
        panelCenter.revalidate();
    }
    
    public void addImage(String file) {   
        int    caretPos;
        String imageHtml;
        
        try {
            caretPos   = mainText.getCaretPosition();
            imageHtml  = "<img src=\"" + "file:" + file + "\" align=\"top\" />";

            defaultHTMLEditorKit.insertHTML(htmlDocument, caretPos, imageHtml, 0, 0, HTML.Tag.IMG);
            mainText.setStyledDocument(htmlDocument);        
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in HtmlTextArea.addImage(String) - " + e);
        }            
    }
    
    public void addImage() {
        FileDialog              fileDialog;
        FileExtensionFilter     fileExtensionFilter;
        
        try {
            fileDialog = new FileDialog(parentWindow, "Open File", FileDialog.LOAD);
            fileExtensionFilter = new FileExtensionFilter();
            fileExtensionFilter.addExtension("gif");
            fileExtensionFilter.addExtension("jpg");
            fileExtensionFilter.addExtension("png");
            fileDialog.setFilenameFilter(fileExtensionFilter);
            
            fileDialog.setVisible(true); 
            String fileName = fileDialog.getDirectory() + fileDialog.getFile();
            includedImages.add(fileName);
            addImage(fileName);                         
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in HtmlTextArea.addImage() - " + e);
        }
    }

    public void addLink(String linkText, String linkURL) {
        try {
            int    caretPos  = mainText.getCaretPosition();
            String linkHtml  = "<a href=\"" + linkURL + "\">" + linkText + "</a>";

            if ( (!linkText.equals("")) && (linkURL.equals(""))) {
                defaultHTMLEditorKit.insertHTML(htmlDocument, caretPos, linkHtml, 0, 0, HTML.Tag.A);
                mainText.setStyledDocument(htmlDocument);
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in HtmlTextArea.addLink() - " + e);
        }
    }

    public void changeBold(boolean bold) {
        if (bold) {
            StyleConstants.setBold(style, true);
        } else {
            StyleConstants.setBold(style, false);
        }

        mainText.setCharacterAttributes(style, false);
        mainText.grabFocus();
    }

    public void changeColor(Color c) {
        StyleConstants.setForeground(style, c);
        mainText.setCharacterAttributes(style, false);
        buttonColors.setForeground(c);
        mainText.grabFocus();
    }

    public void changeItalics(boolean italic) {
        if (italic) {
            StyleConstants.setItalic(style, true);
        } else {
            StyleConstants.setItalic(style, false);
        }

        mainText.setCharacterAttributes(style, false);
        mainText.grabFocus();
    }

    public void changeUnderline(boolean underline) {
        if (underline) {
            StyleConstants.setUnderline(style, true);
        } else {
            StyleConstants.setUnderline(style, false);
        }

        mainText.setCharacterAttributes(style, false);
        mainText.grabFocus();
    }

    public final void createNewParagraph() {
        try {
            int    caretPos      = mainText.getCaretPosition();
            String paragraphHtml = "<p>";

            defaultHTMLEditorKit.insertHTML(htmlDocument, caretPos, paragraphHtml, 0, 0, HTML.Tag.P);
            mainText.setStyledDocument(htmlDocument);
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in HtmlTextArea.keyPressed - " + e);
        }
    }

    public void endParagraph() {
        try {
            int    caretPos      = mainText.getCaretPosition();
            String paragraphHtml = "</p>";

            defaultHTMLEditorKit.insertHTML(htmlDocument, caretPos, paragraphHtml, 0, 0, HTML.Tag.P);
            mainText.setStyledDocument(htmlDocument);
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in HtmlTextArea.keyPressed - " + e);
        }
    }

    public String getHtml() {
        if (openParagraphTag)
            endParagraph();

        return mainText.getText();
    }

    public ArrayList<String> getIncludedImages() {
        return includedImages;
    }

    private void init() {
        try {
            ResourceHelper  resourceHelper = ResourceHelper.getInstance();
            
            addingImage           = false;
            buttonAddImageCancel  = new JButton("Cancel");
            buttonAddImageOk      = new JButton("Ok");            
            buttonBold            = new JToggleButton(resourceHelper.getImage("text_bold.png"));
            buttonColors          = new JButton("A");
            buttonImage           = new JButton(resourceHelper.getImage("image.png"));
            buttonItalics         = new JToggleButton(resourceHelper.getImage("text_italic.png"));
            buttonLink            = new JButton(resourceHelper.getImage("link.png"));
            buttonUnderLine       = new JToggleButton(resourceHelper.getImage("text_underline.png"));
            fileChoicePanel       = new FileChoicePanel(parentWindow, FileChoicePanel.OPEN);            
            htmlDocument          = new HTMLDocument();
            defaultHTMLEditorKit  = new HTMLEditorKit();
            mainPopupFactory      = new PopupFactory();
            mainText              = new JTextPane();
            mainScroll            = new JScrollPane(mainText);
            pannelAddImage        = new JPanel(new GridLayout(2, 1, 2, 2));
            pannelAddImageButtons = new JPanel();
            panelButtons          = new JPanel();
            panelCenter           = new JPanel(new BorderLayout());
            style                 = new SimpleAttributeSet();

            buttonColors.setForeground(Color.BLACK);
            buttonColors.setFont(buttonColors.getFont().deriveFont(Font.BOLD));
            buttonBold.setMargin(     new Insets(4, 4, 4, 4));
            buttonColors.setMargin(   new Insets(4, 5, 4, 5));
            buttonImage.setMargin(    new Insets(4, 4, 4, 4));
            buttonItalics.setMargin(  new Insets(4, 4, 4, 4));
            buttonLink.setMargin(     new Insets(4, 4, 4, 4));
            buttonUnderLine.setMargin(new Insets(4, 4, 4, 4));

            buttonColors.setMaximumSize(new Dimension(30, 30));
            buttonColors.setPreferredSize(new Dimension(30, 30));
            
            mainText.setStyledDocument(htmlDocument);
            mainText.setEditorKit(defaultHTMLEditorKit);

            this.setPreferredSize(new Dimension(375, 280));

            //this.setBorder(LineBorder.createGrayLineBorder());
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in HTMLTextArea.init - " + e);
        }
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        try {
            //special formating, so when the user presses enter a <br/> tag is inserted.
            if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                int    caretPos      = mainText.getCaretPosition();
                String lineBreakHtml = "<br />";

                defaultHTMLEditorKit.insertHTML(htmlDocument, caretPos, lineBreakHtml, 0, 0, HTML.Tag.BR);
                mainText.setStyledDocument(htmlDocument);
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in HtmlTextArea.keyPressed - " + e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e)    {
        char keyChar;
        int  modifiers;

        keyChar   = e.getKeyChar();
        modifiers = e.getModifiers();
        
        if ((modifiers == CONTROL) || (modifiers == APPLE_COMMAND)) {
            switch (keyChar) {
                case 'c':
                    mainText.copy();
                    break;
                case 'p':
                    mainText.paste();
                    break;
                case 'x':
                    mainText.cut();
                    break;
            }
        }
    }

    public void openColorChooserPopupDialog() {
        int x, y;

        try {
            x = parentWindow.getX() + buttonColors.getX();
            y = parentWindow.getY() + (buttonColors.getY() + buttonColors.getHeight() + 125);

            colorChooserPopupDialog = new ColorChooserPopupDialog(this);
            colorChooserPopupDialog.addActionListener(this);
            colorChooserPopupDialog.showColorChooserDialog(x, y);
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in HtmlTextArea.openColorChooserPopupDialog() - " + e);
        }
    }

    public void openHyperlinkPopupDialog() {
        int x, y;

        try {
            x = parentWindow.getX() + buttonLink.getX();
            y = parentWindow.getY() + (buttonLink.getY() + buttonLink.getHeight() + 125);

            hyperlinkPopupDialog = new HyperlinkPopupDialog(this);
            hyperlinkPopupDialog.addActionListener(this);
            hyperlinkPopupDialog.showHyperlinkDialog(x, y);
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in HtmlTextArea.openHyperlinkPopupDialog() - " + e);
        }
    }

    @Override
    public void paint(Graphics g) {
        Container parent;
        int height, width, x, y;

        x      = this.getX();
        y      = this.getY();
        parent = this.getParent();
        height = parent.getHeight() - y - 10;
        width  = parent.getWidth()      - 10;

        this.setPreferredSize(new Dimension(width, height));

        super.paint(g);
    }

    public void setHtml(String html) {
        mainText.setText(html);
        openParagraphTag = false;
    }
    
}
