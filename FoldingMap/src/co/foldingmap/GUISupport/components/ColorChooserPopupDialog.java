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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;
import javax.swing.border.LineBorder;

/**
 *
 * @author Alec
 */
public class ColorChooserPopupDialog extends JPanel implements MouseListener {
    public static final String COMMAND_COLOR_SELECTED = "Color Selected";

    protected ActionListener objectListener;
    protected Color          selectedColor;
    protected JLabel         labelColorTitle, labelMoreColors, labelSpacer1, labelSpacer2;
    protected JPanel         panelColorsPopup, panelChoosableColors;
    protected JPanel         panelColorTitle, panelMoreColors;
    protected JPanel         panelBlack, panelBlue, panelGreen, panelRed, panelWhite;
    protected JPanel         panelGray, panelLightBlue, panelLightGreen, panellightRed, panelWhiteGray;
    protected Component      owner;
    protected Popup          ColorChooserPopup;
    protected PopupFactory   mainPopupFactory;

    /**
     * Constructor for objects of class ColorChooserPopupDialog
     */
    public ColorChooserPopupDialog(Component owner)
    {
        this.owner         = owner;
        this.selectedColor = Color.BLACK;

        init();
        setup();
        addListeners();
    }

    public void addActionListener(ActionListener l) {
        this.objectListener = l;
    }

    private void addListeners() {
        labelMoreColors.addMouseListener(this);

        panelBlack.addMouseListener(this);
        panelBlue.addMouseListener(this);
        panelGreen.addMouseListener(this);
        panelRed.addMouseListener(this);
        panelWhite.addMouseListener(this);
        panelGray.addMouseListener(this);
        panelLightBlue.addMouseListener(this);
        panelLightGreen.addMouseListener(this);
        panellightRed.addMouseListener(this);
        panelWhiteGray.addMouseListener(this);
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public void hideDialog() {
        ColorChooserPopup.hide();
    }

    private void init() {
        labelColorTitle      = new JLabel("Colors");
        labelMoreColors      = new JLabel("More Colors", new ImageIcon("color_wheel.png"), SwingConstants.LEFT);
        labelSpacer1         = new JLabel(" ");
        labelSpacer2         = new JLabel(" ");
        mainPopupFactory     = new PopupFactory();
        panelColorsPopup     = new JPanel(new BorderLayout(5, 5));
        panelColorTitle      = new JPanel();
        panelChoosableColors = new JPanel(new GridLayout(2, 5, 5, 5));
        panelBlack           = new JPanel();
        panelBlue            = new JPanel();
        panelGreen           = new JPanel();
        panelRed             = new JPanel();
        panelWhite           = new JPanel();
        panelGray            = new JPanel();
        panelLightBlue       = new JPanel();
        panelLightGreen      = new JPanel();
        panellightRed        = new JPanel();
        panelMoreColors      = new JPanel();
        panelWhiteGray       = new JPanel();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Object  sourceObject;
        JPanel  sourcePanel;

        sourceObject = e.getSource();

        if (sourceObject instanceof JPanel) {
            sourcePanel   = (JPanel) sourceObject;
            selectedColor = sourcePanel.getBackground();

            if (objectListener != null)
                objectListener.actionPerformed(new ActionEvent(this, 1, COMMAND_COLOR_SELECTED));
        } else if (sourceObject == labelMoreColors) {
            selectedColor = JColorChooser.showDialog(this, "More Colors", selectedColor);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e)  {
        JPanel sourceJPanel;
        Object sourceObject = e.getSource();

        if (sourceObject == labelMoreColors) {
            panelMoreColors.setBackground(new Color(255, 220, 123));
        } else if (sourceObject instanceof JPanel) {
            sourceJPanel = (JPanel) sourceObject;
            sourceJPanel.setBorder(new LineBorder(Color.RED));
        }
    }

    @Override
    public void mouseExited(MouseEvent e)   {
        JPanel sourceJPanel;
        Object sourceObject = e.getSource();

        if (sourceObject == labelMoreColors) {
            panelMoreColors.setBackground(new Color(221, 231, 238));
        } else if (sourceObject instanceof JPanel) {
            sourceJPanel = (JPanel) sourceObject;
            sourceJPanel.setBorder(new LineBorder(Color.BLACK));
        }
    }

    @Override
    public void mousePressed(MouseEvent e)  {}
    
    @Override
    public void mouseReleased(MouseEvent e) {}

    private void setup() {
        panelBlack.setPreferredSize(new Dimension(15, 15));
        panelBlue.setPreferredSize( new Dimension(15, 15));
        panelGreen.setPreferredSize(new Dimension(15, 15));
        panelRed.setPreferredSize(  new Dimension(15, 15));
        panelWhite.setPreferredSize(new Dimension(15, 15));
        panelGray.setPreferredSize(new Dimension(15, 15));
        panelLightBlue.setPreferredSize(new Dimension(15, 15));
        panelLightGreen.setPreferredSize(new Dimension(15, 15));
        panellightRed.setPreferredSize(new Dimension(15, 15));
        panelWhiteGray.setPreferredSize(new Dimension(15, 15));

        panelBlack.setBackground(Color.BLACK);
        panelBlue.setBackground( new Color( 79,  81, 189));
        panelGreen.setBackground(new Color(155, 187,  59));
        panelRed.setBackground(  new Color(192,  50,  77));
        panelWhite.setBackground(Color.WHITE);
        panelGray.setBackground(new Color(127, 127,  127));
        panelLightBlue.setBackground(new Color(141, 179, 226));
        panelLightGreen.setBackground(new Color(194, 214, 155));
        panellightRed.setBackground(new Color(217, 95,  94));
        panelWhiteGray.setBackground(new Color(191, 191,  191));

        panelBlack.setBorder(LineBorder.createGrayLineBorder());
        panelBlue.setBorder(LineBorder.createGrayLineBorder());
        panelGreen.setBorder(LineBorder.createGrayLineBorder());
        panelRed.setBorder(LineBorder.createGrayLineBorder());
        panelWhite.setBorder(LineBorder.createGrayLineBorder());
        panelGray.setBorder(LineBorder.createGrayLineBorder());
        panelLightBlue.setBorder(LineBorder.createGrayLineBorder());
        panelLightGreen.setBorder(LineBorder.createGrayLineBorder());
        panellightRed.setBorder(LineBorder.createGrayLineBorder());
        panelWhiteGray.setBorder(LineBorder.createGrayLineBorder());

        panelChoosableColors.add(panelBlack);
        panelChoosableColors.add(panelBlue);
        panelChoosableColors.add(panelGreen);
        panelChoosableColors.add(panelRed);
        panelChoosableColors.add(panelWhite);
        panelChoosableColors.add(panelGray);
        panelChoosableColors.add(panelLightBlue);
        panelChoosableColors.add(panelLightGreen);
        panelChoosableColors.add(panellightRed);
        panelChoosableColors.add(panelWhiteGray);

        panelColorTitle.add(labelColorTitle);
        panelMoreColors.add(labelMoreColors);

        panelColorsPopup.add(panelColorTitle,      BorderLayout.NORTH);
        panelColorsPopup.add(panelChoosableColors, BorderLayout.CENTER);
        panelColorsPopup.add(panelMoreColors,      BorderLayout.SOUTH);
        panelColorsPopup.add(labelSpacer1,         BorderLayout.EAST);
        panelColorsPopup.add(labelSpacer2,         BorderLayout.WEST);

        panelColorsPopup.setBorder(LineBorder.createBlackLineBorder());

        labelMoreColors.addMouseListener(this);

        panelColorTitle.setBackground(new Color(221, 231, 238));
        panelMoreColors.setBackground(new Color(221, 231, 238));
    }

    public void showColorChooserDialog(int x, int y) {
        ColorChooserPopup = PopupFactory.getSharedInstance().getPopup(owner, panelColorsPopup, x, y);
        ColorChooserPopup.show();
    }

    @Override
    public String toString() {
        return "ColorChooserPopupDialog";
    }
    
}
