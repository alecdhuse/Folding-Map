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
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.MapObject;
import co.foldingmap.map.MapObjectList;
import co.foldingmap.map.vector.VectorObject;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 *
 * @author Alec
 */
public class DataSheetPanel extends JPanel implements ActionListener, 
                                                      ListSelectionListener, 
                                                      MouseListener, 
                                                      TableModelListener,
                                                      Updateable {
    
    private ArrayList<String>           fields;
    private ArrayList<Updateable>       updates;
    private boolean                     updating;
    private DefaultTableModel           tableModel;
    private DigitalMap                  mapData;
    private int                         selectedColumnIndex;
    private JMenuItem                   deleteColumn;
    private JPopupMenu                  columnMenu;
    private JScrollPane                 mainPane;
    private DataTable                   table;
    private JTableHeader                tableHeader;
    private MapObjectList<MapObject>    objects;
    
    public DataSheetPanel(DigitalMap mapData) {
        this.mapData  = mapData;
        this.updates  = new ArrayList<Updateable>();
        this.updating = false;
        
        init();                
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == deleteColumn) {
            deleteColumn();
        }
    }    
    
    public void addUpdateable(Updateable u) {
        updates.add(u);
    }
    
    private void deleteColumn() {
        String       columnLabel;
        VectorObject vectorObject;
        
        if (selectedColumnIndex >= 0) {
            columnLabel = tableModel.getColumnName(selectedColumnIndex);
            
            for (MapObject object: objects) {
                vectorObject = (VectorObject) object;
                vectorObject.removeCustomDataField(columnLabel);
            }
            
            table.removeColumn(table.getColumnModel().getColumn(selectedColumnIndex));
        }
    }
    
    private void init() {
        try {
            this.setLayout(new BorderLayout());

            fields       = mapData.getAllCustomDataFields();
            objects      = mapData.getAllMapObjects();        

            columnMenu   = new JPopupMenu();
            deleteColumn = new JMenuItem("Delete Column");
            tableModel   = new DefaultTableModel();
            table        = new DataTable(tableModel);
            mainPane     = new JScrollPane(table);
            tableHeader  = table.getTableHeader();
                    
            this.add(mainPane, BorderLayout.CENTER);

            //Add Column Names
            
            tableModel.addColumn("Name");                    
            for (String field: fields) 
                tableModel.addColumn(field);
            
            table.getSelectionModel().addListSelectionListener(this);                               
            columnMenu.add(deleteColumn);
            
            tableModel.addTableModelListener(this);
            tableHeader.addMouseListener(this);
            deleteColumn.addActionListener(this);            
            
            update();
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in DataSheetPanel.init() - " + e);
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent me) {
        selectedColumnIndex = -1;
        
        if (me.getButton() == 3) {
            if (me.getSource() == tableHeader) {
                selectedColumnIndex = tableHeader.columnAtPoint(me.getPoint());  
                columnMenu.show(this, me.getX(), me.getY());
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        
    }

    @Override
    public void mouseExited(MouseEvent me) {
        
    }    
    
    @Override
    public void mousePressed(MouseEvent me) {
        
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        
    }    
    
    @Override
    public void setPreferredSize(Dimension d) {
        mainPane.setPreferredSize(d);
        mainPane.setMaximumSize(d);
        this.setMaximumSize(d);
    }
    
    @Override
    public void tableChanged(TableModelEvent tme) {
        int         editedRow, editedColumn;
        VectorObject   object;
        
        if (!updating) {
            editedRow    = tme.getFirstRow();
            editedColumn = tme.getColumn();

            if (editedColumn > 0) {
                object = (VectorObject) objects.get(editedRow);
                object.setCustomDataField(fields.get(editedColumn - 1), (String) tableModel.getValueAt(editedRow, editedColumn));
            }
        }

    }        
    
    @Override
    public void update() {
        try {
            VectorObject   object;
            String      newRow[]   = new String[fields.size() + 1];
            String      field, value;

            updating = true;
            
            for (int row = 0; row < objects.size(); row++) {
                object    = (VectorObject) objects.get(row);
                newRow[0] = object.getName();                       

                for (int i = 0; i < fields.size(); i++) {
                    field = fields.get(i);
                    value = object.getCustomDataFieldValue(field);

                    if (object.getCustomDataFieldValue(field) != null) {
                        newRow[i+1] = value;
                        //tableModel.setValueAt(value, row, i);   
                    } else {
                        newRow[i+1] = "";
                        //tableModel.setValueAt("", row, i);  
                    }

                }        

                tableModel.addRow(newRow);
            }

            tableModel.fireTableDataChanged();
            table.validate();
            updating = false;
        } catch (Exception e) {
            Logger.log(Logger.ERR, "DataSheetPanel.update() - " + e);
            updating = false;
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent lse) {
        int         selectedIndex;
        VectorObject   object;

        mapData.deselectObjects();
        
        selectedIndex = table.getSelectedRow();
        object        = (VectorObject) this.objects.get(selectedIndex);
        
        mapData.setSelected(object);  
        
        for (Updateable u: updates) 
            u.update();
        
    }

}
