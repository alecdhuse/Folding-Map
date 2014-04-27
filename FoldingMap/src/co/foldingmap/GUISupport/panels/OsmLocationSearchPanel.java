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

import co.foldingmap.mapImportExport.BoundsSearchResult;
import co.foldingmap.mapImportExport.OsmImporter;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Alec
 */
public class OsmLocationSearchPanel extends    JPanel 
                                    implements ActionListener, 
                                               ListSelectionListener,
                                               KeyListener {
    
    private ArrayList<ListSelectionListener> listSelectionListeners;
    
    private boolean          searchCompleted;
    private DefaultListModel listModel;
    private JButton          searchButton;
    private JList            resultsList;
    private JPanel           searchPanel;
    private JScrollPane      scrollResults;
    private JTextField       searchBox;
    
    public OsmLocationSearchPanel() {
        init();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == searchButton) {
            doSearch();
        }
    }
    
    /**
     * Adds a List selection listener to this search panel.
     * 
     * @param lsl 
     */
    public void addListSelectionListener(ListSelectionListener lsl) {
        listSelectionListeners.add(lsl);
    }
    
    /**
     * Clears all selections from the result list.
     */
    public void clearSelection() {
        resultsList.clearSelection();
    }
    
    private void doSearch() {
        if (searchCompleted == true) 
            listModel.removeAllElements();        
        
        if (searchBox.getText().length() > 1) {
            BoundsSearchResult[] results = OsmImporter.search(searchBox.getText());

            for (BoundsSearchResult result: results) 
                listModel.addElement(result);
            
            searchCompleted = true;
        }        
    }
    
    /**
     * Returns the selected search result.
     * 
     * @return 
     */
    public BoundsSearchResult getSelectedResult() {
        return (BoundsSearchResult) resultsList.getSelectedValue();
    }
    
    private void init() {
        listModel               = new DefaultListModel();
        listSelectionListeners  = new ArrayList<ListSelectionListener>();
        resultsList             = new JList(listModel);
        searchCompleted         = false;
        searchBox               = new JTextField();
        searchButton            = new JButton("Search");
        searchPanel             = new JPanel(new BorderLayout());
        scrollResults           = new JScrollPane(resultsList);        
                        
        searchPanel.add(searchBox,    BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        
        this.setLayout(new BorderLayout());
        this.add(searchPanel,   BorderLayout.NORTH);
        this.add(scrollResults, BorderLayout.CENTER);
                
        searchButton.addActionListener(this);   
        searchBox.addKeyListener(this);
        resultsList.addListSelectionListener(this);
        
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
            doSearch();
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }

    /**
     * Sets the focus windows to the search box
     */
    public void setSearchBoxAsFocus() {
        this.searchBox.requestFocus();
    }
    
    /**
     * Triggered when the selection of a search result changes.
     * 
     * @param lse 
     */
    @Override
    public void valueChanged(ListSelectionEvent lse) {
        //Change the source to this panel
        ListSelectionEvent newLse = new ListSelectionEvent(this, lse.getFirstIndex(), lse.getLastIndex(), lse.getValueIsAdjusting());
        
        for (ListSelectionListener lsl: listSelectionListeners) 
            lsl.valueChanged(newLse);        
    }

}
