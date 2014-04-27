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
package co.foldingmap.map.vector;

import co.foldingmap.map.MapProjection;
import co.foldingmap.map.MapView;
import co.foldingmap.map.MercatorProjection;
import co.foldingmap.map.tile.TileMath;
import co.foldingmap.xml.XmlOutput;

/**
 * This class is the implementation of the KML Region object.  It is used to 
 * determine if object should be shown at current view levels.
 * 
 * Fading is not fully implemented at this time.
 * 
 * @author Alec
 */
public class Region {
    public  static final int        MIN_VIEW  = 0;
    public  static final int        MAX_VIEW  = 1;
    public  static final int        BOTH_VIEW = 2;
    public  static final int        NONE_VIEW = 3;

    private LatLonAltBox            latLonAltBox;
    private LevelOfDetail           levelOfDetail;
    private String                  regionName;    
    
    /**
     * Constructor for objects of class Region
     */
    public Region(String regionName, LatLonAltBox latLonAltBox, float maxLevelOfDetailPixels, float minLevelOfDetailPixels) {
        this.regionName             = regionName;
        this.latLonAltBox           = latLonAltBox;        
        this.levelOfDetail          = new LevelOfDetail(maxLevelOfDetailPixels, minLevelOfDetailPixels);
        
        this.setMaxLevelOfDetailPixels(maxLevelOfDetailPixels);
        this.setMinLevelOfDetailPixels(minLevelOfDetailPixels);
    }    
    
    /**
     * Constructor for objects of class Region
     */
    public Region(String regionName, LatLonAltBox latLonAltBox, LevelOfDetail lod) {
        this.regionName             = regionName;
        this.latLonAltBox           = latLonAltBox;        
        this.levelOfDetail          = lod;
        
        this.setMaxLevelOfDetailPixels(lod.getMaxLodPixels());
        this.setMinLevelOfDetailPixels(lod.getMinLodPixels());        
    }        
    
    /**
     * Constructor for objects of class Region
     */
    public Region(String regionName) {
        this.regionName     = regionName;
        this.levelOfDetail  = new LevelOfDetail(-1, 0);
    }    
    
    /**
     * Calculates the diagonal distance of the LatLonAltBox on the screen.
     * 
     * @param mapView
     * @return 
     */
    public float calculateLevelOfDetailPixels(MapView mapView) {
        float  maxLOD;
        double x1, x2, y1, y2;        
        
        y1 = mapView.getY(new Coordinate(0, latLonAltBox.getNorth(), latLonAltBox.getWest()));
        y2 = mapView.getY(new Coordinate(0, latLonAltBox.getSouth(), latLonAltBox.getEast()));        
        x1 = mapView.getX(new Coordinate(0, latLonAltBox.getNorth(), latLonAltBox.getWest()), MapView.NO_WRAP);
        x2 = mapView.getX(new Coordinate(0, latLonAltBox.getSouth(), latLonAltBox.getEast()), MapView.NO_WRAP);       
        
        maxLOD = (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));        

        return maxLOD;
    }    

    /**
     * Finds the Level of Detail for a bounds at a given tile zoom level.
     * 
     * @param tileZoom
     * @param bounds
     * @return 
     */
    public static float calculateLodFromTileZoom(float tileZoom, LatLonAltBox bounds) {
        float         vectorZoom = TileMath.getVectorMapZoom(tileZoom);
        MapProjection projection = new MercatorProjection(); 
        
        projection.setZoomLevel(vectorZoom);
        
        float y1 = (float) projection.getY(bounds.getNorthWestCoordinate());
        float y2 = (float) projection.getY(bounds.getSouthEastCoordinate());
        float x1 = (float) projection.getX(bounds.getNorthWestCoordinate());
        float x2 = (float) projection.getX(bounds.getSouthEastCoordinate());      
        
        float lod = (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));  
        
        return lod;
    }    
    
    /**
     * Calculates a vector zoom level that will meet the given Level of Detail.
     * 
     * @param lod
     * @return 
     */
    public float calculateVectorZoomLevelForLOD(Float lod) {
        MapProjection projection = new MercatorProjection(); 
        float         zoomLevel  = 0.001f;
        float         testLod    = 0;
        float         increment  = 100;
        
        while (increment > 0.001f) {
            projection.setZoomLevel(zoomLevel);

            float y1 = (float) projection.getY(latLonAltBox.getNorthWestCoordinate());
            float y2 = (float) projection.getY(latLonAltBox.getSouthEastCoordinate());
            float x1 = (float) projection.getX(latLonAltBox.getNorthWestCoordinate());
            float x2 = (float) projection.getX(latLonAltBox.getSouthEastCoordinate());

            testLod = (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)); 

            if (testLod < lod) {
                zoomLevel += increment;
            } else {
                zoomLevel -= increment;
                increment *= 0.1f;
            }
                
        }
        
        return zoomLevel;
    }
    
    /**
     * Returns is this Region is equal to a given Object.
     * 
     * @param o
     * @return 
     */
    @Override
    public boolean equals(Object o) {
        boolean isEqual = false;
        
        if (o instanceof Region) {
            Region r = (Region) o;
            
            if (r.getName().equals(this.getName()) &&
                r.getLatLonAltBox().equals(this.getLatLonAltBox()) &&
                r.getLevelOfDetail().equals(this.getLevelOfDetail())) {
                
                isEqual = true;
            }
        }
        
        return isEqual;
    }
    
    /**
     * Returns this Region's LAtLonAltBox
     * 
     * @return 
     */
    public LatLonAltBox getLatLonAltBox() {
        return latLonAltBox;
    }    
    
    /**
     * Returns the LevelOfDetail object for this Region.
     * 
     * @return 
     */
    public LevelOfDetail getLevelOfDetail() {
        return levelOfDetail;
    }      

    public boolean isVisible(MapView mapView) {
        boolean regionVisible;
        double  distance;
        
        distance      = this.calculateLevelOfDetailPixels(mapView);
        regionVisible = false;

        //-1 indecates the there is no value
        if (levelOfDetail.getMaxLodPixels() == -1) {
            //check to see if the pixels are within range
            if ((levelOfDetail.getMinLodPixels() < distance))
                     regionVisible = true;
        } else if (levelOfDetail.getMaxLodPixels() == -1) {
            //check to see if the pixels are within range
            if ( (levelOfDetail.getMaxLodPixels() > distance))
                     regionVisible = true;
        } else {
            //check to see if the pixels are within range
            if ( (levelOfDetail.getMaxLodPixels() > distance) &&
                 (levelOfDetail.getMinLodPixels() < distance)) {
                     regionVisible = true;
            }
        }

        return regionVisible;
    }
  
    /**
     * Sets this Region's Max Level of Detail Pixels.
     * 
     * @param lodMax 
     */
    public final void setMaxLevelOfDetailPixels(float lodMax) {
        levelOfDetail.setMaxLodPixels(lodMax);
    }    
          
    /**
     * Sets this Region's Min Level of Detail Pixels.
     * 
     * @param lodMin 
     */
    public final void setMinLevelOfDetailPixels(float lodMin) {
        levelOfDetail.setMinLodPixels(lodMin);     
    }              
    
    /**
     * Returns the regionName of this Region.
     * 
     * @return 
     */
    public String getName() {
        return regionName;
    }
    
    /**
     * Writes this Region as is KML representation.
     * 
     * @param kmlWriter 
     */
    public void toXML(XmlOutput kmlWriter) {
        kmlWriter.openTag ("Region");

        kmlWriter.writeTag("name", regionName);
        
        latLonAltBox.toXML(kmlWriter);        
        levelOfDetail.toXML(kmlWriter);

        kmlWriter.closeTag("Region");
    }    
    
//    public void updateRegion(DigitalMap mapData, VectorObject mapObject, int viewType) {
//        MapView mapView;
//        Region  objectRegion;
//
//        mapView      = mapData.getLastMapView();
//        objectRegion = this;
//
//        switch (viewType) {
//            case MIN_VIEW:
//                objectRegion.setMaxLevelOfDetailPixels(-1);
//                objectRegion.setMinLevelOfDetailPixels(calculateLevelOfDetailPixels(mapView));
//                break;
//            case MAX_VIEW:
//                objectRegion.setMaxLevelOfDetailPixels(calculateLevelOfDetailPixels(mapView));
//                objectRegion.setMinLevelOfDetailPixels(-1);
//                break;
//            case BOTH_VIEW:
//                objectRegion.setMaxLevelOfDetailPixels(calculateLevelOfDetailPixels(mapView));
//                objectRegion.setMinLevelOfDetailPixels(calculateLevelOfDetailPixels(mapView));
//                break;
//            case NONE_VIEW:
//                objectRegion.setMaxLevelOfDetailPixels(-1);
//                objectRegion.setMinLevelOfDetailPixels(-1);
//                break;
//        }
//
//        //mapObject.setRegion(this);
//    }    
}
