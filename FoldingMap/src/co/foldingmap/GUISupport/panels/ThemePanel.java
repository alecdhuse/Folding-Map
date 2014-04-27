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
package co.foldingmap.GUISupport.panels;

import co.foldingmap.GUISupport.SpringUtilities;
import co.foldingmap.GUISupport.Updateable;
import co.foldingmap.ResourceHelper;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.themes.ColorStyle;
import co.foldingmap.map.themes.IconStyle;
import co.foldingmap.map.themes.MapTheme;
import co.foldingmap.map.themes.MapThemeManager;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Alec
 */
public class ThemePanel extends ActionPanel implements ChangeListener, ListSelectionListener {
    private ArrayList<Updateable>  updateables;
    private ColorStylePanel        colorStylePanel;
    private DefaultComboBoxModel   themeComboModel;
    private Dialog                 parentDialog;
    private DigitalMap             mapData;
    private ImageChoicePanel       imageChoicePanel;
    private JButton                buttonAddTheme, buttonRemoveTheme;
    private JComboBox              comboTheme;
    private JList                  listPoints, listLines, listPolys;
    private JPanel                 panelAddRemoveTheme, panelCenter, panelNorth;
    private JPanel                 panelIconStyle, panelLineStyle, panelPolyStyle;
    private JScrollPane            scrollPoint, scrollLine, scrollPoly;
    private JTabbedPane            tabThemeElement;
    private MapTheme               theme;
    private MapThemeManager        themeManager;
    private ResourceHelper         helper;
    
    public ThemePanel(Dialog parentDialog, DigitalMap mapData) {
        this.parentDialog = parentDialog;
        this.mapData      = mapData;
        this.theme        = mapData.getTheme();
        this.themeManager = mapData.getMapThemeManager();
        this.updateables  = new ArrayList<Updateable>();
                
        init();
        setupLayout();
        updateLists();
        
        listPoints.setSelectedIndex(0);
    }    

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == comboTheme) {
            theme = (MapTheme) comboTheme.getSelectedItem();
            mapData.setTheme(theme, null, null); 
            updateLists();
            fireUpdates();
        }
    }    
    
    public void addUpdateable(Updateable u) {
        this.updateables.add(u);
    }
    
    private void fireUpdates() {
        for (Updateable u: updateables) {
            u.update();
        }
    }
    
    private void init() {
        themeComboModel     = new DefaultComboBoxModel(); 
        helper              = ResourceHelper.getInstance();
        
        buttonAddTheme      = new JButton(helper.getImage("add.png"));
        buttonRemoveTheme   = new JButton(helper.getImage("delete.png"));
        colorStylePanel     = new ColorStylePanel(theme);
        comboTheme          = new JComboBox(themeComboModel);
        imageChoicePanel    = new ImageChoicePanel(parentDialog);
        listPoints          = new JList();
        listLines           = new JList();
        listPolys           = new JList();
        panelAddRemoveTheme = new JPanel();
        panelCenter         = new JPanel(new BorderLayout());
        panelIconStyle      = new JPanel(new SpringLayout());
        panelLineStyle      = new JPanel();
        panelNorth          = new JPanel(new BorderLayout());
        panelPolyStyle      = new JPanel();
        scrollPoint         = new JScrollPane(listPoints);
        scrollLine          = new JScrollPane(listLines);
        scrollPoly          = new JScrollPane(listPolys);
        tabThemeElement     = new JTabbedPane();
                
        //Add available themes to the combobox
        for (MapTheme t: themeManager.getAllThemes()) {
            themeComboModel.addElement(t);
            
            if (t == theme) 
                themeComboModel.setSelectedItem(t);            
        }
        
        //setup listeners
        comboTheme.addActionListener(this);
        tabThemeElement.addChangeListener(this);
        listPoints.addListSelectionListener(this);
        listLines.addListSelectionListener(this);
        listPolys.addListSelectionListener(this);                  
    }
    
    private void setupLayout() {
        this.setLayout(new BorderLayout());
        this.add(panelNorth,  BorderLayout.NORTH);
        this.add(panelCenter, BorderLayout.CENTER);
        
        panelNorth.add(comboTheme,          BorderLayout.CENTER);
        panelNorth.add(panelAddRemoveTheme, BorderLayout.EAST);
        
        panelCenter.add(tabThemeElement,    BorderLayout.NORTH);
        
        panelAddRemoveTheme.add(buttonAddTheme);
        panelAddRemoveTheme.add(buttonRemoveTheme);
        
        tabThemeElement.add("Point",   scrollPoint);
        tabThemeElement.add("Line",    scrollLine);
        tabThemeElement.add("Polygon", scrollPoly);                
    }

    @Override
    public void stateChanged(ChangeEvent ce) {
        if (ce.getSource() == listPoints) {
            imageChoicePanel.setIconStyle((IconStyle) listPoints.getSelectedValue());
            
            panelIconStyle.removeAll();
            panelIconStyle.add(imageChoicePanel);
            panelIconStyle.add(colorStylePanel);            
            
            SpringUtilities.makeCompactGrid(panelIconStyle, 2, 1, 2, 2, 2, 2);
            
            panelCenter.removeAll();
            panelCenter.add(tabThemeElement, BorderLayout.NORTH);
            panelCenter.add(panelIconStyle,  BorderLayout.CENTER);                        
            
            panelIconStyle.revalidate();
            panelCenter.revalidate();
        }
    }

    private void updateLists() {
        ArrayList   icons, lines, polys;
        
        listPoints.removeAll();
        listLines.removeAll();
        listPolys.removeAll();        
  
        icons = theme.getAllIconStyles();
        lines = theme.getAllLineStyles();
        polys = theme.getAllPolygonStyles();
        
        Collections.sort(icons);
        Collections.sort(lines);
        Collections.sort(polys);
                                
        listPoints.setListData(icons.toArray());        
        listLines.setListData(lines.toArray());
        listPolys.setListData(polys.toArray());
    }

    @Override
    public void valueChanged(ListSelectionEvent lse) {
        if (lse.getSource() == listPoints) {
            imageChoicePanel.setIconStyle((IconStyle)  listPoints.getSelectedValue());
            colorStylePanel.setColorStyle((ColorStyle) listPoints.getSelectedValue());
            
            panelIconStyle.removeAll();
            panelIconStyle.add(imageChoicePanel);   
            panelIconStyle.add(colorStylePanel);            
            
            SpringUtilities.makeCompactGrid(panelIconStyle, 2, 1, 2, 2, 2, 2);
            
            panelCenter.removeAll();
            panelCenter.add(tabThemeElement, BorderLayout.NORTH);
            panelCenter.add(panelIconStyle,  BorderLayout.CENTER);
            
            panelIconStyle.revalidate();
            panelCenter.revalidate();        
        } else if (lse.getSource() == listLines) {   
            colorStylePanel.setColorStyle((ColorStyle) listLines.getSelectedValue());
            
            panelLineStyle.removeAll();
            panelLineStyle.add(colorStylePanel);            
            
            panelCenter.removeAll();
            panelCenter.add(tabThemeElement, BorderLayout.NORTH);
            panelCenter.add(panelLineStyle,  BorderLayout.CENTER);  
            
            panelLineStyle.revalidate(); 
            panelCenter.revalidate(); 
        } else if (lse.getSource() == listPolys) {   
            colorStylePanel.setColorStyle((ColorStyle) listPolys.getSelectedValue());
            
            panelPolyStyle.removeAll();
            panelPolyStyle.add(colorStylePanel);               
            
            panelCenter.removeAll();
            panelCenter.add(tabThemeElement, BorderLayout.NORTH);
            panelCenter.add(panelPolyStyle,  BorderLayout.CENTER);    
            
            panelPolyStyle.revalidate(); 
            panelCenter.revalidate(); 
        }
    }
}
