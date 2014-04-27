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
package co.foldingmap;

import co.foldingmap.GUISupport.Updateable;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Alec
 */
public class Logger {
    public static final int INFO = 0;
    public static final int WARN = 2;
    public static final int ERR  = 4;
       
    private static final ArrayList<Updateable> updateables = new ArrayList<Updateable>();
    private static final StringBuilder         log         = new StringBuilder();    
    private static final SimpleDateFormat      dateFormat  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    
    /**
     * Adds an Updateable to the list.  
     * These are notified when a new log entry is made.
     * 
     * @param u 
     */
    public static void addUpdateable(Updateable u) {
        Logger.updateables.add(u);
    }
    
    private static void fireUpdates() {
        for (Updateable u: updateables) 
            u.update();           
    }
            
    
    public static void log(int level, String message) {
        boolean doUpdates = true;
        
        if (level == INFO) {
            log.append("[INFO] ");
            System.out.println(message);
            doUpdates = false;
        } else if (level == WARN) { 
            log.append("[WARN] ");
            System.out.println(message);
            doUpdates = false;
        } else if (level == ERR) { 
            log.append("[ERR]  ");
            System.err.println(message);
            doUpdates = true;
        }
        
        log.append(message);
        log.append("\n");    
        
        if (doUpdates) fireUpdates();
        writeToFile(message);
    }
    
    private static void writeToFile(String message) {
        try {
            FileWriter  fw   = new FileWriter("foldingMap-err.log", true);
            PrintWriter out  = new PrintWriter(new BufferedWriter(fw));
            String      date = dateFormat.format(new Date()) + "\t";
                    
            out.println(date + message);
            out.close();
        } catch (IOException e) {
            System.err.println("Error in Logger.writeToFile(String) - " + e);
        }        
    }
}
