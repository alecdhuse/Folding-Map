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

import co.foldingmap.GUISupport.components.WizardPanePanel;
import co.foldingmap.data.MapObjectCreator;
import co.foldingmap.data.TabularData;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author Alec
 */
public class DataImportResultPanel extends WizardPanePanel {
    protected DigitalMap                mapData;
    protected JScrollPane               spaneResults;
    protected JTextArea                 textResults;
    protected VectorObjectList<VectorObject>  objects;
    protected MapObjectCreator          mapObjectCreator;
    protected TabularData               dataFile;    
    
    public DataImportResultPanel(DigitalMap mapData) {
        this.mapData        = mapData;
        textResults         = new JTextArea("Created 0 objects.");
        spaneResults        = new JScrollPane(textResults, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        objects             = new VectorObjectList<VectorObject>();
        permitAdvance       = true;

        spaneResults.setMaximumSize(new Dimension(400, 300));
        spaneResults.setPreferredSize(new Dimension(400, 290));
        
        this.setLayout(new BorderLayout());
        this.add(spaneResults, BorderLayout.CENTER);
    }    
            
    public void addData(DigitalMap mapData, String importDataVariable, String mapObjectVariable) {
        mapObjectCreator.addData(mapData, importDataVariable, mapObjectVariable);
        textResults.setText(mapObjectCreator.getResults());
        textResults.setCaretPosition(0);
    }    
    
    public void createLineStrings(String nameColumn, String coordinatesColumn, String objectType) {
        VectorObjectList<VectorObject> errorObjects;

        if (mapObjectCreator != null) {
            objects      = mapObjectCreator.createLineStrings(nameColumn, coordinatesColumn, objectType );
            errorObjects = mapObjectCreator.getObjectsWithErrors();

            textResults.setText(mapObjectCreator.getResults());
            textResults.setCaretPosition(0);
        }
    }    
    
    public void createPoints(String latitudeColumn, String longitudeColumn, String altitudeColumn,  String objectType) {
        VectorObjectList<VectorObject> errorObjects;

        if (mapObjectCreator != null) {
            objects      = mapObjectCreator.createPoints(latitudeColumn, longitudeColumn, altitudeColumn, objectType);
            errorObjects = mapObjectCreator.getObjectsWithErrors();

            textResults.setText(mapObjectCreator.getResults());
            textResults.setCaretPosition(0);
        }
    }    
    
    public void createPolygons(String nameColumn, String coordinatesColumn, String objectType) {
        VectorObjectList<VectorObject> errorObjects;

        if (mapObjectCreator != null) {
            objects      = mapObjectCreator.createPolygons(nameColumn, coordinatesColumn, objectType);
            errorObjects = mapObjectCreator.getObjectsWithErrors();

            textResults.setText(mapObjectCreator.getResults());
            textResults.setCaretPosition(0);
        }
    }    
    
    public VectorObjectList<VectorObject> getMapObjects() {
        return objects;
    }
    
    @Override
    public void displayPanel() {
        this.revalidate();
    }
    
    public void setDataFile(TabularData dataFile) {
        this.dataFile    = dataFile;
        mapObjectCreator = new MapObjectCreator(dataFile, mapData);        
    }
}
