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

import co.foldingmap.GUISupport.OptionsPanel;
import co.foldingmap.MainWindow;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.themes.ColorRamp;
import co.foldingmap.map.vector.NetworkLayer;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.JScrollPane;

/**
 *
 * @author Alec
 */
public class LiveFeedsPanel extends OptionsPanel  {
    private ColorRamp   earthquakeColorRamp;
    private DigitalMap  mapData;
    private JList       list;
    private JScrollPane spane;
    private MainWindow  mainWindow;
    
    private String[]    feedNames = {"USGS Earthquakes - Past Day",
                                     "MODIS Active Fire Hotspots",
                                     "WWLLN World Lightning Strikes"};
    
    private String[]    feedURLs  = {"http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson",
                                     "https://firms.modaps.eosdis.nasa.gov/active_fire/text/Global_24h.csv",
                                     "http://flash3.ess.washington.edu/lightning_src.kmz"};
    
    public LiveFeedsPanel(MainWindow mainWindow, DigitalMap mapData) {
        this.mainWindow = mainWindow;
        this.mapData    = mapData;
        
//        this.earthquakeColorRamp = new ColorRamp("Earthquake-ramp", 5);
//        this.earthquakeColorRamp.addEntry("pasthour", Color.RED);
//        this.earthquakeColorRamp.addEntry("pastday",  Color.ORANGE);
//        this.earthquakeColorRamp.addEntry("pastweek", Color.YELLOW);
//        this.earthquakeColorRamp.setDefaultColor(Color.LIGHT_GRAY);
//        mapData.getTheme().addColorRamp(earthquakeColorRamp);
        
        init();
        setupLayout();
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        int             selectedIndex;
        NetworkLayer    networkLayer;
                
        selectedIndex = list.getSelectedIndex();
        networkLayer  = new NetworkLayer(feedNames[selectedIndex], feedURLs[selectedIndex]);
        mapData.addLayer(networkLayer, 0);
        mainWindow.update();
    }
    
    private void init() {
        list  = new JList(feedNames);
        spane = new JScrollPane(list);
    }
    
    private void setupLayout() {
        this.setLayout(new BorderLayout());
        this.add(spane, BorderLayout.CENTER);
    }
}
