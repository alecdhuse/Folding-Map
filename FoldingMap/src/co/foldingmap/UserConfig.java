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

import co.foldingmap.actions.ExportMapToImage;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *  This file reads in configuration information from the user's config file.
 * @author Alec
 */
public class UserConfig {
    public static final int NUMBER_OF_RECENT_FILES = 4;
    
    protected HashMap<String, String> properties;
    protected File              configFile;
    
    public UserConfig() {
        init();
    }

    public UserConfig(File configFile) {
        properties = new HashMap<String, String>();
        
        this.configFile = configFile;
        openConfigFile(configFile);
    }

    /**
     * Adds a new recent file to this UserConfig
     * 
     * @param newFile 
     */
    public void addRecentFile(String newFile) {
        
        //bump down the old list of recent files
        for (int i = (NUMBER_OF_RECENT_FILES - 2); i >= 0; i--) {
            String recentFile = properties.get("recentFile" + i);
            
            if (recentFile != null) {
                //Check to see if this recent file is the same as the new file.
                if (!recentFile.equals(newFile)) {
                    //If not the same as the now file add it in
                    properties.put("recentFile" + (i + 1), recentFile);
                } else {
                    //File is the same, proceed to the next recent file
                    properties.put("recentFile" + (i + 2), recentFile);
                }
            }
        }
        
        //add the new entry
        properties.put("recentFile0", newFile);
        
        this.writeFile(this.configFile);
    }
    
    private String getProperty(String line) {
        int    start = line.indexOf(".") + 1;
        int    end   = line.indexOf("=");
        String prop  = line.substring(start, end).trim();
        
        return prop;
    }
    
    /**
     * Returns a list of the recently used files.
     * 
     * @return 
     */
    public String[] getRecentFiles() {
        String[] files = new String[NUMBER_OF_RECENT_FILES];
    
        for (int i = 0; i < NUMBER_OF_RECENT_FILES; i++) 
            files[i] = properties.get("recentFile" + i);   
        
        return files;
    }
    
    private String getValue(String line) {
        int    start;
        String value = "";

        start = line.indexOf("=");

        if (start > 0) {
            start++;
            value = line.substring(start).trim();
        }

        return value;
    }

    /**
     * Returns the directory path to use for exporting.
     * 
     * @return 
     */
    public String getExportDIR() {
        return properties.get("exportDIR");
    }
    
    /**
     * Returns the file type to use for exporting.
     * 
     * @return 
     */
    public String getExportFileType() {
        return properties.get("exportFileType");
    }    
    
    /**
     * Returns the directory path to use for importing.
     * 
     * @return 
     */
    public String getImportDIR() {
        return properties.get("importDIR");
    }    
    
    /**
     * Returns the directory path to use for the working directory.
     * 
     * @return 
     */
    public String getWorkingDIR() {
        return properties.get("workingDIR");
    }

    private void init() {
        properties = new HashMap<String, String>();
        
        //Set default values
        properties.put("exportDIR",      System.getProperty("user.home"));
        properties.put("exportFileType", ExportMapToImage.PNG);
        properties.put("importDIR",      System.getProperty("user.home"));
        properties.put("logToFile",      "true");
        properties.put("useOpenGL",      "false");
        properties.put("workingDIR",     System.getProperty("user.home"));    
    }

    /**
     * Returns if Logger should log to a file.
     * 
     * @return 
     */
    public boolean logToFile() {
        return Boolean.getBoolean(properties.get("logToFile"));
    }
    
    private void openConfigFile(File configFile) {
        BufferedReader  br;
        FileReader      fr;
        
        try {
            fr = new FileReader(configFile);
            br = new BufferedReader(fr);

            if (!br.ready()) {
                //file exists but has no info
                init();
                writeFile(configFile);
            }

            while (br.ready()) {
                readProperty(br.readLine());
            }
        } catch (FileNotFoundException fe) {
            //file does not exist
            init();
            writeFile(configFile);
        } catch (Exception e) {

        }
    }

    private void readProperty(String line) {
        try {            
            String prop = getProperty(line);
            String val  = getValue(line); 
            
            properties.put(prop, val);
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Cannot read line: " + line);
        }
    }

    public void setExportDIR(String exportDIR) {
        setProperty("exportDIR", exportDIR);
        this.writeFile(this.configFile);
    }     
    
    public void setExportFileType(String exportFileType) {
        setProperty("exportFileTyp", exportFileType);
        this.writeFile(this.configFile);
    }         
    
    public void setImportDIR(String importDIR) {
        setProperty("importDIR", importDIR);
        this.writeFile(this.configFile);
    }    
    
    /**
     * Sets a given UserConfig property to a given value.
     * 
     * @param property
     * @param value 
     */
    public void setProperty(String property, String value) {
        properties.put(property, value);
    }
    
    public void setWorkingDIR(String workingDIR) {
//        this.workingDIR = workingDIR;
        setProperty("workingDIR", workingDIR);
        this.writeFile(this.configFile);
    }

    @Override
    public String toString() {
        StringBuilder out   = new StringBuilder();

        Iterator it = properties.entrySet().iterator();
        
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            
            out.append(".");
            out.append(pairs.getKey());
            out.append(" = ");
            out.append(pairs.getValue());
            out.append("\n");
        }          
        
        return out.toString();
    }

    /**
     * Writes the config to a files.
     * 
     * @param  configFile
     * @return If writing the file was successful.
     */
    private boolean writeFile(File configFile) {
        BufferedWriter br;
        FileWriter     fw;

        try {
            fw = new FileWriter(configFile);
            br = new BufferedWriter(fw);

            br.write(this.toString());
            br.close();
            fw.close();

            return true;
        } catch (Exception e) {
            return false;
        }        
    }    
    
    /** Returns if OpenGL should be used
     * 
     * @return 
     */
    public boolean useOpenGL() {
//        return useOpenGL;
        return Boolean.getBoolean(properties.get("useOpenGL"));
    }
}
