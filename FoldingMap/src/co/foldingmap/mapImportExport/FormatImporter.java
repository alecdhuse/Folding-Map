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
package co.foldingmap.mapImportExport;

import co.foldingmap.GUISupport.ProgressIndicator;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.Layer;
import co.foldingmap.map.vector.NodeMap;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Alec
 */
public interface FormatImporter {
    
    /**
     * Imports objects from a given map file and adds objects from the map to
     * the given VectorLayer.
     * 
     * @param mapFile           The file containing the map to import.
     * @param nodeMap           The NodeMap to add new Coordinates to.
     * @param layer             The Layer to add imported objects to.
     * @param progressIndicator Optional, to display the progress of the import.
     * 
     * @throws java.io.IOException
     */
    public abstract void importToLayer(File mapFile, NodeMap nodeMap, Layer layer, ProgressIndicator progressIndicator) throws IOException;
    
    /**
     * Creates a new DigitalMap with all the data imported from the given
     * map file.  
     * 
     * @param mapFile           The file containing the map to import.
     * @param progressIndicator Optional, to display the progress of the import.
     * @return                  The DigitalMap containing data from the imported file.
     * 
     * @throws java.io.IOException
     */
    public abstract DigitalMap importAsMap(File mapFile, ProgressIndicator progressIndicator) throws IOException;
   
}
