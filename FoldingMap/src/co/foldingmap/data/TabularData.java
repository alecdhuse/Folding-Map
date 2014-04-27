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
package co.foldingmap.data;

import co.foldingmap.Logger;
import java.util.ArrayList;

/**
 *
 * @author Alec
 */
public class TabularData {
    private ArrayList<ArrayList<DataCell>> columns;
    private ArrayList<String>              headerCells, headerNames;
    
    public TabularData() {
        columns     = new ArrayList<ArrayList<DataCell>>();
        headerCells = new ArrayList<String>();
        headerNames = new ArrayList<String>();        
    }
    
    public TabularData(ArrayList<ArrayList<DataCell>> columns, ArrayList<String> headerNames, ArrayList<String> headerCells) {
        this.columns     = columns;
        this.headerCells = headerCells;
        this.headerNames = headerNames;
    }    
    
    /**
     * Add a new cell to the end of a column.
     * 
     * @param row
     * @param cell 
     */
    public void addCell(int row, DataCell cell) {
        ArrayList<DataCell> col;
        
        try {
            col = columns.get(row);
            col.add(cell);        
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in TabularData.addCell - " + e);
        }
     }
    
    /**
     * Adds a column with a given name at a given index.
     * 
     * @param index
     * @param headerName 
     */
    public void addColumn(int index, String headerName) {    
        columns.add(index, new ArrayList<DataCell>());
        
        if (!headerNames.contains(headerName))
            headerNames.add(index, headerName);        
    }
    
    /**
     * Adds a new header name, if the name dose not already exist as a header.
     * If the name already exists, it will not be added.
     * 
     * @param headerName 
     */
    public void addHeader(String headerName) {
        if (!headerNames.contains(headerName)) {
            headerNames.add(headerName);
        }
    }
    
    /**
     * Returns if a given ArrayList of strings are all numbers.
     * 
     * @param list
     * @return 
     */
    public static boolean areOnlyNumbers(ArrayList<DataCell> list) {
        boolean onlyNumbers;
        float   number;
        
        onlyNumbers = false;

        for (DataCell currentElement: list) {
            try {
                if (!currentElement.toString().equals("")) {
                    number = currentElement.getFloatValue();

                    if (number != Float.NaN)
                        onlyNumbers = true;
                } else {
                    //skip blank cells
                }
            } catch (Exception e) {
                return false;
            }
        }

        return onlyNumbers;
    }    
    
    /**
     * Checks to see if a given column contains any coordinate information.
     * 
     * @param column
     * @return 
     */
    public boolean columnContainsCoordinates(int column) {
        ArrayList<DataCell> cells;
        boolean             hasCS;
        double              trueFalseRatio;
        int                 falseCount, trueCount;
        
        cells       = columns.get(column);
        falseCount  = 1;
        hasCS       = false;
        trueCount   = 0;
        
        for (DataCell cell: cells) {
            if (cell.trim().indexOf(" ") > 0) {
                //check to see if cell contains a coordinate string
                if (cell.getCoordinate() != null) {
                    trueCount++;
                } else {
                    falseCount++;
                }
            }
        }

        trueFalseRatio = (trueCount / falseCount);
        if (trueFalseRatio > 0.75) {
            hasCS = true;
        } else {
            hasCS = false;
        }

        return hasCS;
    }
    
    /**
     * Checks to see if a given column contains a Coordinate String.
     * 
     * @param column
     * @return 
     */
    public boolean containsCoordinateString(int column) {
        ArrayList<DataCell> cells;
        boolean             hasCS;
        double              trueFalseRatio;
        int                 falseCount, trueCount;
        
        cells       = columns.get(column);
        falseCount  = 1;
        hasCS       = false;
        trueCount   = 0;
        
        for (DataCell cell: cells) {
            if (cell.trim().indexOf(" ") > 0) {
                //check to see if cell contains a coordinate string
                if (cell.getCoordinate() != null) {
                    trueCount++;
                } else {
                    falseCount++;
                }
            }
        }

        trueFalseRatio = (trueCount / falseCount);
        if (trueFalseRatio > 0.75) {
            hasCS = true;
        } else {
            hasCS = false;
        }

        return hasCS;
    }    
        
    protected void findHeaders(ArrayList<ArrayList<DataCell>> rows) {
        ArrayList<DataCell> firstRow, SecondRow;
        int                 cellNumber, filledCellCount;
        String              cellText;

        try {
            cellNumber      = 0;
            filledCellCount = 0;
            firstRow        = rows.get(0);
            SecondRow       = rows.get(1);

            /*
             * Check to see if only one cell in the first row has text.
             * This should indicate that the first row is only a label.
             */
            for (DataCell cell: firstRow) {
                if (!cell.equals(""))
                    filledCellCount++;
            }
            
            if (filledCellCount > 1) {
                for (DataCell cell: firstRow) {
                    if (!cell.equals("")) {
                        if (!headerNames.contains(cell.toString())) {
                            headerNames.add(cell.toString());
                            columns.add(new ArrayList<DataCell>());
                        }
                    } else {
                        /* The cell is blank, check the next row.
                        * But only if the first cell is blank,
                        * otherwise assume it is data and not header.
                        */

                        cellText = SecondRow.get(0).toString();
                        if (cellText.equals("")) {
                            //first cell of the second row is empty, assume more header info
                            if (cellNumber < SecondRow.size()) {
                                cellText = SecondRow.get(cellNumber).toString();
                            } else {
                                cellText = "";
                            }

                            if (!headerNames.contains(cell.toString())) {
                                headerNames.add(cellText);
                                columns.add(new ArrayList<DataCell>());
                            }
                        } else {
                            if (!headerNames.contains(cell.toString())) {
                                headerNames.add(" ");
                                columns.add(new ArrayList<DataCell>());
                            }
                        }
                    }

                    cellNumber++;
                }
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in TabularDataFile.findHeaders(ArrayList<ArrayList>) - " + e);
        }
    }    
    
    /**
     * This function attempts to locate any coordinate information
     * 
     * @return 
     */
    public ArrayList<Boolean> findLocationData() {
        ArrayList<Boolean>  columnContainsCoordinate;
        boolean             containsCoordinate;
        double              falseCount, trueCount, trueFalseRatio;
        String              headerName;

        columnContainsCoordinate = new ArrayList<Boolean>();

        for (int i = 0; i < columns.size(); i++) {
            ArrayList<DataCell> c = columns.get(i);

            containsCoordinate = false;
            falseCount = 1;
            trueCount  = 0;

            if (i < headerNames.size()) {
                headerName = headerNames.get(i);
            } else {
                headerName = "unknown";
            }

            if (headerName.equalsIgnoreCase("Altitude") || headerName.equalsIgnoreCase("Alt")) {
                for (DataCell cell: c) {
                    if (cell.getFloatValue() != Float.NaN) {
                        trueCount++;
                    } else {
                        falseCount++;
                    }
                }

                trueFalseRatio = (trueCount / falseCount);
                
                if (trueFalseRatio > 0.75) {
                    containsCoordinate = true;
                } else {
                    containsCoordinate = false;
                }
            } else {
                for (DataCell cell: c) {

                    /** check to see if cell has spaces, if it does perhaps
                     *  it is a coordinate String
                     */
                    if (cell.trim().indexOf(" ") > 0) {
                        //check to see if cell contains a coordinate string
                        if (cell.getCoordinate() != null) {
                            trueCount++;
                        } else {
                            falseCount++;
                        }
                    } else {
                        //does not contain spaces, check to see if it is a coordinate
                        if (cell.getCoordinate() != null) {
                            trueCount++;
                        } else {
                            if (cell.containsCoordinateInformation()) {
                                trueCount++;
                            } else {
                                falseCount++;
                            }
                        }
                    }
                }

                trueFalseRatio = (trueCount / falseCount);
                
                if (trueFalseRatio > 0.75) {
                    containsCoordinate = true;
                } else {
                    containsCoordinate = false;
                }


            } //end altitude check

            columnContainsCoordinate.add(containsCoordinate);
        }

        return columnContainsCoordinate;
    }    
    
    /**
     * Returns a the column at the given index.
     * 
     * @param index
     * @return 
     */
    public ArrayList<DataCell> getColumn(int index) {    
        return columns.get(index);
    }
    
    /**
     * Returns the Header Name at a given index.
     * 
     * @param index
     * @return 
     */
    public String getHeaderName(int index) {
        return headerNames.get(index);
    }    
    
    /**
     * Returns the Header names of the data.
     * 
     * @return 
     */
    public ArrayList<String> getHeaderNames() {
        return headerNames;
    }    
    
    /**
     * Returns the row at the given index.
     * 
     * @param index
     * @return 
     */
    public ArrayList<DataCell> getRow(int index) {
        ArrayList<DataCell> row;

        row = new ArrayList<DataCell>();

        for (ArrayList<DataCell> c: columns) {
            if (index < c.size()) {
                row.add(c.get(index));
            } else {
                row.add(new DataCell(""));
            }
        }

        return row;
    }
        
    /**
     * Returns a count of the number of times a given string appears in a 
     * column of this data table.
     * 
     * @param instance
     * @param column
     * @return 
     */
    public int getNumberOfInstances(String instance, int column) {
        ArrayList<DataCell> currentColumn;
        int                 count = 0;

        currentColumn = columns.get(column);

        for (DataCell cell: currentColumn) {
            if (cell.toString().equalsIgnoreCase(instance))
                count++;
        }

        return count;
    }
    
    /**
     * Returns the column count.
     * 
     * @return 
     */
    public int getNumberOfColumns() {
        return this.columns.size();
    }
    
    /**
     * Returns the number of headers in this TabularData.
     * 
     * @return 
     */
    public int getNumberOfHeaders() {
        return this.headerNames.size();
    }    
    
    /**
     * Returns the row count.
     * Columns may contain different number of rows, this method will return
     * the the row count for the column with the most rows.
     * 
     * @return 
     */
    public int getNumberOfRows() {
        int numberOfRows = 0;

        for (ArrayList<DataCell> c: columns) {
            if (numberOfRows < c.size())
                numberOfRows = c.size();
        }

        return numberOfRows;
    }
    
    /**
     * Returns a TabularData class that is a subset where a column 
     * contains a given value.
     * 
     * @param compareColumnHeader
     * @param columnValue
     * @return 
     */
    public TabularData getSubSet(String compareColumnHeader, String columnValue) {
        ArrayList<ArrayList<DataCell>> newColumns;
        ArrayList<DataCell>            currentRow;
        int                            compareColumn;
        String                         cellString;
        
        newColumns    = new ArrayList<ArrayList<DataCell>>();
        compareColumn = -1;

        for (int i = 0; i < this.headerNames.size(); i++) {
            newColumns.add(new ArrayList<DataCell>());
        }

        //find the column the that matches the compair column
        for (int i = 0; i < this.headerNames.size(); i++) {
            String currentHeader = headerNames.get(i);
            if (currentHeader.equalsIgnoreCase(compareColumnHeader)) {
                compareColumn = i;
                break;
            }
        }

        if (compareColumn >= 0) {
            for (int i = 0; i < getNumberOfRows(); i++) {
                currentRow = getRow(i);
                cellString = currentRow.get(compareColumn).toString().trim();
                cellString = cellString.replaceAll("[\\u00A0]", "");
                
                if (cellString.equalsIgnoreCase(columnValue)) {
                    /**
                     * If the column we are matching, in fact matches the value
                     * we are looking for then add this row to the subset.
                     */
                    for (int j = 0; j < this.headerNames.size(); j++) {
                        newColumns.get(j).add(currentRow.get(j));
                    }
                }
            }
        }

        return new TabularData(newColumns, this.headerNames, this.headerCells);
    }
    
    /**
     * Returns one instance of each data value in this column.
     * 
     * @return
     */
    public ArrayList<DataCell> getUniqueColumnItems(int columnIndex) {
        ArrayList<DataCell> searchColumn, uniqueColumnItems;

        uniqueColumnItems = new ArrayList<DataCell>();
        searchColumn      = columns.get(columnIndex);

        for (DataCell currentCell: searchColumn) {
            if (!uniqueColumnItems.contains(currentCell))
                uniqueColumnItems.add(currentCell);
        }

        return uniqueColumnItems;
    }    
    
    /**
     * Returns one instance of each data value in this column.
     * 
     * @param headerName
     * @return 
     */
    public ArrayList<DataCell> getUniqueColumnItems(String headerName) {
        int columnIndex = -1;

        for (int i = 0; i < this.headerNames.size(); i++) {
            String currentHeader = headerNames.get(i);
            if (currentHeader.equalsIgnoreCase(headerName)) {
                columnIndex = i;
                break;
            }
        }

        if (columnIndex >= 0) {
            return getUniqueColumnItems(columnIndex);
        } else {
            return new ArrayList<DataCell>();
        }
    }    
    
    /**
     * Loads this file with DataCells.
     * 
     * @param rows 
     */
    public void loadData(ArrayList<ArrayList<DataCell>> rows) {
        ArrayList<DataCell>  currentColumn, currentRow;
        int                  rowDataStartIndex;
        DataCell             currentCell;

        findHeaders(rows);
        currentRow = rows.get(1);

        //find where the headers end, and the data starts.
        if (currentRow.get(0).equals("")) {
            rowDataStartIndex = 2;
        } else {
            rowDataStartIndex = 1;
        }

        for (int currentRowIndex = rowDataStartIndex; currentRowIndex < rows.size(); currentRowIndex++) {
            currentRow = rows.get(currentRowIndex);

            for (int currentCellIndex = 0; currentCellIndex < currentRow.size(); currentCellIndex++) {
                currentCell   = currentRow.get(currentCellIndex);

                if (currentCellIndex < columns.size()) {
                    currentColumn = columns.get(currentCellIndex);
                    currentColumn.add(currentCell);
                } else {
                    currentColumn = new ArrayList<DataCell>();
                    currentColumn.add(currentCell);
                    columns.add(currentColumn);
                }
            }
        }        
    }    
    
    /**
     * Rename a specific header in the data.
     * 
     * @param index
     * @param newName 
     */
    public void renameHeader(int index, String newName) {
        this.headerNames.remove(index);
        this.headerNames.add(index, newName);
    }    
}
