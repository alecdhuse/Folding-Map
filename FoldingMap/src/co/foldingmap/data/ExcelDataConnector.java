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
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Alec
 */
public class ExcelDataConnector {
    protected int                   numberOfRows, numberOfCollums;
    protected Sheet                 workingSheet;
    protected TabularData           dataFile;
    protected Workbook              workbook;

    /**
     * Opens a new ExcelDataConnector using a given file.
     * 
     * @param workBookFile  The file containing the workbook.
     */
    public ExcelDataConnector(File workBookFile) {
        workbook     = this.openWorkBook(workBookFile);
        workingSheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
        dataFile     = parseWorkbook();
        
        workbook.setMissingCellPolicy(Row.CREATE_NULL_AS_BLANK);        
    }    
    
    /**
     * Returns a cell value as a DataCell object.
     * 
     * @param cell
     * @return 
     */
    public DataCell getCellText(Cell cell) {
        DataCell  cellText;

        switch(cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                cellText = new DataCell(cell.getRichStringCellValue().getString());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if(DateUtil.isCellDateFormatted(cell)) {
                    cellText = new DataCell(cell.getDateCellValue().toString());
                } else {
                    cellText = new DataCell(Double.toString(cell.getNumericCellValue()));
                }

                break;
            case Cell.CELL_TYPE_BOOLEAN:
                cellText = new DataCell(Boolean.toString(cell.getBooleanCellValue()));
                break;
            case Cell.CELL_TYPE_FORMULA:
                cellText = new DataCell(cell.getCellFormula());
                break;
            default:
                cellText = new DataCell("");
        }

        return cellText;
    }    
    
    /**
     * Returns the file used by the ExcelDataConnector.
     * 
     * @return 
     */
    public TabularData getDataFile() {
        dataFile = parseWorkbook();
        return dataFile;
    }    
    
    /**
     * Opens the Excel Workbook to be used by this class.
     * 
     * @param workBookFile
     * @return 
     */
    private Workbook openWorkBook(File workBookFile) {
        FileInputStream openedStream;
        Workbook        openedWorkbook = new HSSFWorkbook();

        try {
            openedStream = new FileInputStream(workBookFile);

            if (workBookFile.getName().endsWith(".xls")) {
                //old file type
                openedWorkbook = new HSSFWorkbook(openedStream);
            } else if (workBookFile.getName().endsWith(".xlsx")) {
                //new file type
                openedWorkbook = new XSSFWorkbook(openedStream);
            } else {
                //unknown file type
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error ExcelDataConnector.openWorkBook(File) - " + e);
        }

        return openedWorkbook;
    }    
    
    /**
     * Parses the cells from the workbook into a TabulaData class.
     * 
     * @param workingSheet
     * @return 
     */
    private TabularData parseWorkbook() {
        ArrayList<ArrayList<DataCell>> rows;
        ArrayList<DataCell>            cells;
        int                            columnIndex, currentCellColumnIndex, lastCellcolumnIndex;
        int                            numberOfCells, previousRowLength, rowIndex;
        TabularData                    dataFile;

        dataFile          = new TabularData();
        previousRowLength = 0;
        rows              = new ArrayList<ArrayList<DataCell>>();
        rowIndex          = -1;
        
        try {
            for (Row row : workingSheet) {
                cells               = new ArrayList<DataCell>();
                columnIndex         = row.getFirstCellNum();
                lastCellcolumnIndex = -1;
                numberOfCells       = row.getPhysicalNumberOfCells();
                rowIndex++;
                
                //add blank cells
                for (int i = 0; i < columnIndex; i++) 
                    cells.add(new DataCell(""));                
                
                for (Cell cell : row) {
                    currentCellColumnIndex = cell.getColumnIndex();                                

                    if ((lastCellcolumnIndex + 1) == currentCellColumnIndex) {
                        cells.add(getCellText(cell));
                        lastCellcolumnIndex = currentCellColumnIndex;
                    } else {
                        for (int i = (lastCellcolumnIndex + 1); i < currentCellColumnIndex; i++) {
                            cells.add(new DataCell(""));
                        }
                        
                        cells.add(getCellText(cell));
                        lastCellcolumnIndex = currentCellColumnIndex;
                    }
                }

                //if this row does not match the length of the last one, add blank cells
                for (int i = (lastCellcolumnIndex + 1) ; i <= previousRowLength; i++) {
                    cells.add(new DataCell(""));
                    lastCellcolumnIndex = i;
                }

                previousRowLength = lastCellcolumnIndex;
                rows.add(cells);
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in ExcelDataConnector.parseWorkbook(Sheet) - " + e);
        }

        dataFile.loadData(rows);

        return dataFile;
    }
    
}
