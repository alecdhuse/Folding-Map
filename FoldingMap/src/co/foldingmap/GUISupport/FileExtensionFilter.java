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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Custom made file filter.
 * 
 * @author Alec
 */
public class FileExtensionFilter implements FilenameFilter {
    ArrayList<String>   acceptedExtensions;
    boolean             acceptDirectories;
    
    /**
     * Constructor without arguments.
     * 
     */
    public FileExtensionFilter() {
        this.acceptedExtensions = new ArrayList<String>();
        this.acceptDirectories  = false;
    }

    /**
     * Constructor with a file extention.
     * 
     * @param extension 
     */
    public FileExtensionFilter(String extension) {
        this.acceptedExtensions = new ArrayList<String>();
        this.addExtension(extension);
    }

    /**
     * Returns if the file is acceptable for the filter.
     * 
     * @param dir
     * @param name
     * @return 
     */
    @Override
    public boolean accept(File dir, String name) {
        boolean accepted;
        String  extension;
        
        accepted = false;
        
        if (dir.isDirectory() && acceptDirectories)
            accepted = true;
        
        for (String ex: acceptedExtensions) {
            if (ex.length() < name.length()) {
                extension = name.substring(name.length() - ex.length());

                if (extension.equalsIgnoreCase(ex)) {
                    accepted = true;
                    break;
                }
            }
        }

        return accepted;
    }
    
    /**
     * Sets if directories are acceptable.
     * 
     * @param acceptDirectories 
     */
    public void acceptDirectories(boolean acceptDirectories) {
        this.acceptDirectories = acceptDirectories;
    }
    
    /**
     * Add an extention to this filter.
     * 
     * @param extension 
     */
    public final void addExtension(String extension) {
        acceptedExtensions.add(extension);
    }

}
