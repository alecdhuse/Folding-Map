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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.LineBorder;

/**
 *
 * @author Alec
 */
public class HyperlinkPopupDialog extends JPanel implements ActionListener {
    public static final String  COMMAND_OK     = "Ok";
    public static final String  COMMAND_CANCEL = "Cancel";
    public static final int     OK             = 1;
    public static final int     CANCEL         = 0;
    public static final int     NOT_SELECTED   = 3;
    
    protected ActionListener objectListener;
    protected Component      owner;
    protected int            userAction;
    protected JButton        buttonOK,      buttonCancel;
    protected JLabel         labelHyperlinkTitle, labelLinkText, labelLink;
    protected JPanel         panelButtons,  panelCenter, panelHyperlinkTitle;
    protected JPanel         panelLinkText, panelLink;
    protected JTextField     textLinkText,  textLink;
    protected Popup          hyperlinkPopup;    
    
    /**
     * Constructor for objects of class NewHyperlinkPanel
     */
    public HyperlinkPopupDialog(Component owner)
    {
        this.owner      = owner;
        this.userAction = NOT_SELECTED;

        init();
        setupPanel();
    }

    public HyperlinkPopupDialog(Component owner, String textToHyperlink) {
        this.owner      = owner;
        this.userAction = NOT_SELECTED;

        init();
        setupPanel();
        textLinkText.setText(textToHyperlink);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Object  initiatingObject = ae.getSource();

        if (initiatingObject == buttonCancel) {
            this.userAction = CANCEL;

            if (objectListener != null)
                objectListener.actionPerformed(new ActionEvent(this, userAction, COMMAND_CANCEL));
        } else if (initiatingObject == buttonOK) {
            this.userAction = OK;

            if (objectListener != null)
                objectListener.actionPerformed(new ActionEvent(this, userAction, COMMAND_OK));
        }

        hyperlinkPopup.hide();
    }

    public void addActionListener(ActionListener l) {
        this.objectListener = l;
    }

    public String getLinkText() {
        return textLinkText.getText();
    }

    public String getLinkURL() {
        return textLink.getText();
    }

    public void hideDialog() {
        hyperlinkPopup.hide();
    }

    private void init() {
        buttonOK            = new JButton("OK");
        buttonCancel        = new JButton("Cancel");
        labelHyperlinkTitle = new JLabel("Hyperlink");
        labelLinkText       = new JLabel("Link Text");
        labelLink           = new JLabel("Link URL ");
        panelButtons        = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelCenter         = new JPanel(new GridLayout(2,2));
        panelHyperlinkTitle = new JPanel();
        panelLinkText       = new JPanel();
        panelLink           = new JPanel();
        textLinkText        = new JTextField(" ", 25);
        textLink            = new JTextField(" ", 25);

        buttonOK.addActionListener(this);
        buttonCancel.addActionListener(this);
    }

    private void setupPanel() {
        panelLinkText.add(labelLinkText);
        panelLinkText.add(textLinkText);

        panelLink.add(labelLink);
        panelLink.add(textLink);

        panelCenter.add(panelLinkText);
        panelCenter.add(panelLink);

        panelButtons.add(buttonOK);
        panelButtons.add(buttonCancel);

        panelHyperlinkTitle.add(labelHyperlinkTitle);
        panelHyperlinkTitle.setBackground(new Color(221, 231, 238));

        this.setLayout(new BorderLayout());
        this.add(panelHyperlinkTitle, BorderLayout.NORTH);
        this.add(panelCenter,         BorderLayout.CENTER);
        this.add(panelButtons,        BorderLayout.SOUTH);

        this.setBorder(LineBorder.createBlackLineBorder());
    }

    public void showHyperlinkDialog(int x, int y) {
        hyperlinkPopup = PopupFactory.getSharedInstance().getPopup(owner, this, x, y);
        hyperlinkPopup.show();
    }

    @Override
    public String toString() {
        return "HyperlinkPopupDialog";
    }
    
}
