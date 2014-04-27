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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.StringTokenizer;

/**
 *
 * @author Alec
 */
public class CsvDataConnector {
    private File        csvFile;
    private TabularData tabularData;
    
    public CsvDataConnector(File csvFile) {
        this.csvFile = csvFile;
        parse();
    }
    
    public TabularData getTabularData() {
        return tabularData; 
    }
    
    /**
     * Parse each line of the CSV file.
     */
    private void parse() {
        BufferedReader  br;
        int             columnNumber;
        long            lineNumber;
        String          line;
        StringTokenizer st;                
        
        try {
            lineNumber  = 0;            
            tabularData = new TabularData();            
            br = new BufferedReader(new FileReader(csvFile));
            
            while (br.ready()) {
                lineNumber++;
                line = br.readLine();      
                st   = new StringTokenizer(line, ",");
                columnNumber = 0;
                
                while (st.hasMoreTokens()) {
                    if (lineNumber == 1) {
                        tabularData.addColumn(columnNumber, st.nextToken());
                    } else {
                        tabularData.addCell(columnNumber, new DataCell(st.nextToken() ));
                    }           
                    
                    columnNumber++;
                }
            }
            
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in CsvDataConnector.parse() - " + e);
        }
    }
}
