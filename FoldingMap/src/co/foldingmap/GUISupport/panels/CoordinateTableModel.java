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

import co.foldingmap.map.vector.Coordinate;
import java.util.ArrayList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * This is a custom Table Model for editing coordinate information.  It is setup
 * to output 5 columns: Reference, Latitude, Longitude, Altitude and timestamp.
 * Coordinate information is directly edited, changes made to the tables are
 * automatically made to the coordinates represented here.
 * 
 * @author Alec
 */
public class CoordinateTableModel implements TableModel {
    ArrayList<Coordinate>         coordinateList;
    ArrayList<TableModelListener> listeners;
    
    public CoordinateTableModel() {
        coordinateList = new ArrayList<Coordinate>();
        listeners      = new ArrayList<TableModelListener>();
    }
    
    /**
     * Adds a new black row containing Coordinate.UNKNOWN_COORDINATE.
     */
    public void addNewBlankRow() {
        coordinateList.add(Coordinate.UNKNOWN_COORDINATE);
        
        fireUpdate(coordinateList.size() - 1, coordinateList.size() - 1, -1, TableModelEvent.INSERT);
    }    
    
    /**
     * Adds a given Coordinate to the table model.
     * 
     * @param c 
     */
    public void addRow(Coordinate c) {
        coordinateList.add(c);
        
        fireUpdate(coordinateList.size() - 1, coordinateList.size() - 1, -1, TableModelEvent.INSERT);
    }
    
    /**
     * Adds a TableModelListener to receive changes in the data.
     * 
     * @param tl 
     */
    @Override
    public void addTableModelListener(TableModelListener tl) {
        this.listeners.add(tl);
    }    
    
    /**
     * Internal method to send a tableChanged event to all the listeners.
     * 
     * @param firstRow
     * @param lastRow
     * @param column
     * @param type 
     */
    private void fireUpdate(int firstRow, int lastRow, int column, int type) {
        for (TableModelListener tl: this.listeners) {
            if (column >= 0) { 
                tl.tableChanged(new TableModelEvent(this, firstRow, lastRow, column, type));
            } else {
                tl.tableChanged(new TableModelEvent(this, firstRow, lastRow, type));
            }
                    
        }
    }
    
    /**
     * Returns the number of Coordinates in this table model.
     * 
     * @return 
     */
    @Override
    public int getRowCount() {
        return coordinateList.size();
    }

    /**
     * Returns the number of displayable columns, which equals five.
     * 
     * @return 
     */
    @Override
    public int getColumnCount() {
        return 5;
    }

    /**
     * Returns the name for a given column.
     * 
     * @param i
     * @return 
     */
    @Override
    public String getColumnName(int i) {
        if (i == 0) {
            return "Reference";
        } else if (i == 1) {
            return "Latitude";
        } else if (i == 2) {
            return "Longitude";
        } else if (i == 3) {
            return "Altitude";
        } else if (i == 4) {
            return "Time";
        } else {
            return "";
        }
    }

    /**
     * Returns the object class for the requested column.
     * 
     * @param i
     * @return 
     */
    @Override
    public Class<?> getColumnClass(int i) {
        if (i == 0) {
            return Long.class;
        } else if (i == 1) {
            return Float.class;
        } else if (i == 2) {
            return Float.class;
        } else if (i == 3) {
            return Float.class;
        } else if (i == 4) {
            return String.class;
        } else {
            return String.class;
        }
    }

    /**
     * Returns if a cell is editable.
     * 
     * @param i
     * @param i1
     * @return 
     */
    @Override
    public boolean isCellEditable(int i, int i1) {
        if (i1 > 0) {
            return true;
        } else {
            return false;            
        }
    }

    /**
     * Returns the value of a given Coordinate's parameter.
     * 
     * @param i
     * @param i1
     * @return 
     */
    @Override
    public Object getValueAt(int i, int i1) {
        if (i1 == 0) {
            return new Long(coordinateList.get(i).getID());
        } else if (i1 == 1) {
            return new Float(coordinateList.get(i).getLatitude());
        } else if (i1 == 2) {
            return new Float(coordinateList.get(i).getLongitude());
        } else if (i1 == 3) {
            return new Float(coordinateList.get(i).getAltitude());
        } else if (i1 == 4) {
            return coordinateList.get(i).getTimestamp();
        } else {
            return "";
        }
    }

    /**
     * Inserts a blank row containing Coordinate.UNKNOWN_COORDINATE at the 
     * given index.
     * 
     * @param index 
     */
    public void insertNewBlankRow(int index) {
        coordinateList.add(index, Coordinate.UNKNOWN_COORDINATE);
        
        fireUpdate(coordinateList.size() - 1, coordinateList.size() - 1, -1, TableModelEvent.INSERT);
    }       
    
    /**
     * Sets the value for a given Coordinate.
     * 
     * @param o
     * @param i
     * @param i1 
     */
    @Override
    public void setValueAt(Object o, int i, int i1) {
        Coordinate c = coordinateList.get(i);
        
        if (i1 == 0) {    
            if (o instanceof Long) {
                c.setId((Long) o);
            } else if (o instanceof String) {
                c.setId(Long.parseLong((String) o));
            }
            
        } else if (i1 == 1) {
            if (o instanceof Float) {
                c.setLatitude((Float) o);
            } else if (o instanceof String) {
                c.setLatitude(Float.parseFloat((String) o));
            }                        
        } else if (i1 == 2) {
            if (o instanceof Float) {
                c.setLongitude((Float) o);
            } else if (o instanceof String) {
                c.setLongitude(Float.parseFloat((String) o));
            }                        
        } else if (i1 == 3) {
            if (o instanceof Float) {
                c.setAltitude((Float) o);
            } else if (o instanceof String) {
                c.setAltitude(Float.parseFloat((String) o));
            }                        
        } else if (i1 == 4) {
            if (o instanceof String) {
                c.setTimestamp((String) o);
            }                        
        }
        
        fireUpdate(i, i, i1, TableModelEvent.UPDATE);
    }

    /**
     * Removes all Coordinates from this TableModel.
     * 
     */
    public void removeAll() {
        coordinateList.clear();
        
        fireUpdate(0, coordinateList.size() - 1, -1, TableModelEvent.DELETE);
    }
    
    /**
     * Removes a specific row given its index.
     * 
     * @param index 
     */
    public void removeRow(int index) {
        coordinateList.remove(index);
        
        fireUpdate(index, index, -1, TableModelEvent.DELETE);        
    }
    
    /**
     * Removes a specific TableModelListener.
     * 
     * @param tl 
     */
    @Override
    public void removeTableModelListener(TableModelListener tl) {
        this.listeners.remove(tl);
    }
    
}
