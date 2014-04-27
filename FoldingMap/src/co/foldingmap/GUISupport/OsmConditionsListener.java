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
package co.foldingmap.GUISupport;

import co.foldingmap.Logger;
import co.foldingmap.mapImportExport.OsmImportCondition;
import co.foldingmap.mapImportExport.OsmTags;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 * Listener for OSM import conditions, handles updates for all components. 
 * @author Alec
 */
public class OsmConditionsListener implements ActionListener, CaretListener, ItemListener, KeyListener, MouseListener {
    protected ArrayList<OsmImportCondition>   rows;
    protected JList                           listHints;
    protected JTextField                      textSelected;
    protected OsmConditionsTableModel         tableModel;
    protected OsmTags                         osmTags;
    protected Popup                           hintPopup;  
    protected Window                          parentWindow;
    
    public OsmConditionsListener(Window parent, OsmConditionsTableModel tableModel, ArrayList<OsmImportCondition> rows) {
        this.rows         = rows;
        this.listHints    = new JList();
        this.osmTags      = new OsmTags();
        this.parentWindow = parent;
        this.tableModel   = tableModel;
        
        listHints.addMouseListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        int     selectedRow;
        JButton button;
        JPanel  panel;
        JTable  table;    
        String  actionCommand;
        
        try {
            actionCommand = ae.getActionCommand();
            button = (JButton) ae.getSource();   
            panel  = (JPanel) button.getParent();     

            if (panel.getParent() instanceof JTable) {
                table       = (JTable) panel.getParent();        
                selectedRow = table.getSelectedRow();

                if (actionCommand.equals("Add")) {
                    tableModel.addRow(new OsmImportCondition("Any", "Any", "Any"), selectedRow + 1);                
                } else if (actionCommand.equals("Remove") && table.getRowCount() > 1) {
                    if (selectedRow < 0) {
                        tableModel.removeRow(tableModel.getRowCount() - 1);
                    } else {
                        tableModel.removeRow(selectedRow);
                    }
                }            
            }       
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in OsmConditionsListener.actionPerformed(ActionoEvent) - " + e);
        }
    }    
    
    @Override
    public void caretUpdate(CaretEvent ce) {
        ArrayList<String>   textHint;
        boolean             showPopup;        
        int                 selectedColumn, selectedRow;
        int                 x, y;             
        JTable              table;
        JTextField          textField;
        OsmImportCondition  row;               
        Point               onScreenPoint;
        String              objectType;        
                
        textHint   = new ArrayList<String>();
        showPopup  = false;
        x          = 0;
        y          = 0;              
        
        if (ce.getSource() instanceof JTextField) {
            textField    = (JTextField) ce.getSource();
            textSelected = textField;
            
            if (textField.getParent() instanceof JTable) {
                table          = (JTable) textField.getParent();
                selectedRow    = table.getSelectedRow();
                selectedColumn = table.getSelectedColumn();
                row            = rows.get(selectedRow);
                objectType     = row.getObjectType();
                
                if (selectedColumn == 1) {
                    row.setKey(textField.getText());
                    
                    if (textField.getText().length() > 0) {
                        onScreenPoint = textField.getLocationOnScreen();
                        textHint      = osmTags.getKeys(objectType, textField.getText());
                        x             = onScreenPoint.x + 5;
                        y             = onScreenPoint.y + textField.getHeight();
                        showPopup     = true;
                    }                    
                } else if (selectedColumn == 2) {
                    row.setValue(textField.getText());
                    
                    if (textField.getText().length() > 0) {
                        onScreenPoint = textField.getLocationOnScreen();
                        textHint      = osmTags.getValues(objectType, row.getKey(), textField.getText());
                        x             = onScreenPoint.x + 5;
                        y             = onScreenPoint.y + textField.getHeight();   
                        showPopup     = true;
                    }                    
                }
                
                //Hide the old popup if it exists
                if (hintPopup != null) 
                    hintPopup.hide();        

                if (showPopup) {
                    //show popup                    
                    listHints.removeAll();
                    listHints.setListData(textHint.toArray());
                    listHints.setSelectedIndex(0);            

                    hintPopup = PopupFactory.getSharedInstance().getPopup(parentWindow, listHints, x, y);
                    hintPopup.show();
                }                
            }
        } 
    }

    @Override
    public void keyTyped(KeyEvent ke) {

    }

    @Override
    public void keyPressed(KeyEvent ke) {
        JTextField  textSource;
        String      selectedHint;
        
        if (ke.getKeyCode() == KeyEvent.VK_ENTER && hintPopup != null) {
            /* If enter is pressed change the text of the textfield  
             * to the selected value in the hints popup
             */            
            textSource   = (JTextField) ke.getSource();
            selectedHint = (String) listHints.getSelectedValue();
            
            if (selectedHint != null) {
                textSource.setText(selectedHint);
                hintPopup.hide();
                hintPopup = null;
            }            
        } 
    }
    
    /**
     * Hides the hint popup.
     */
    public void hideHintPopup() {
        if (hintPopup != null) {
            hintPopup.hide();
            hintPopup = null;
        }
    }
    
    @Override
    public void itemStateChanged(ItemEvent ie) {
        int                 selectedRow;
        JComboBox           combo;
        JTable              table;
        OsmImportCondition  row;  
        
        if (ie.getSource() instanceof JComboBox) {
            combo = (JComboBox) ie.getSource();
            
            if (combo.getParent() instanceof JTable) {
                table          = (JTable) combo.getParent();
                selectedRow    = table.getSelectedRow();
                row            = rows.get(selectedRow);    
                
                row.setObjectType((String) combo.getSelectedItem());
            }
        }
    }    
    
    @Override
    public void keyReleased(KeyEvent ke) {

    }

    @Override
    public void mouseClicked(MouseEvent me) {
        String      selectedHint;
        
        if (hintPopup != null) {
            selectedHint = (String) listHints.getSelectedValue();
            textSelected.setText(selectedHint);
            hintPopup.hide();
            hintPopup = null;            
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {
        JTextField          textField;
        String              text;
        
        if (me.getSource() instanceof JTextField) {
            textField = (JTextField) me.getSource();
            text      = textField.getText();
            
            if (text.equalsIgnoreCase("Any")) {
                textField.setText("");
            } else if (text.equalsIgnoreCase("*")) {
                textField.setText("");
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {

    }

    @Override
    public void mouseEntered(MouseEvent me) {

    }

    @Override
    public void mouseExited(MouseEvent me) {

    }
    
}
