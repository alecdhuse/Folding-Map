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
package co.foldingmap.actions;

import co.foldingmap.Logger;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.MapView;
import co.foldingmap.map.themes.MapTheme;
import co.foldingmap.mapImportExport.SvgExporter;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Exports the map to an image based on the given parameters. 
 * 
 * @author Alec
 */
public class ExportMapToImage extends Action {
    public static final String  JPEG = "JPEG";
    public static final String  PNG  = "PNG";
    public static final String  SVG  = "SVG";
    
    private DigitalMap  mapData;
    private Dimension   outputDimensions;
    private File        exportFile;
    private MapView     imageMapView;
    private String      exportType;
    
    public ExportMapToImage(DigitalMap  mapData, 
                            MapView     imageMapView, 
                            Dimension   outputDimensions, 
                            String      exportType,
                            File        exportFile) {
        
        this.commandDescription = "Export Map to Image";
        this.exportFile         = exportFile;
        this.exportType         = exportType;
        this.imageMapView       = imageMapView;
        this.mapData            = mapData;
        this.outputDimensions   = outputDimensions;
    }
    
    /**
     * Returns if this Action can be undone.
     * 
     * @return 
     */
    @Override
    public boolean canUndo() {
        return false;
    }    
    
    @Override
    public void execute() {
        BufferedImage           exportBufferedImage;
        Graphics2D              exportGraphics2D;
        MapTheme                mapTheme;
        RenderingHints          renderAntialiasing;

        try {
            if (exportType.equals(JPEG) || exportType.equals(PNG)) {
                exportBufferedImage = new BufferedImage(outputDimensions.width, outputDimensions.height, BufferedImage.TYPE_INT_BGR);
                exportGraphics2D    = exportBufferedImage.createGraphics();
                mapTheme            = mapData.getTheme();

                renderAntialiasing  = new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                renderAntialiasing.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                //draw background
                exportGraphics2D.setColor(mapTheme.getBackgroundColor());
                exportGraphics2D.fill(new Rectangle2D.Double(-1, -1, outputDimensions.width + 1, outputDimensions.height + 1));

                exportGraphics2D.setRenderingHints(renderAntialiasing);
                mapData.drawMap(exportGraphics2D, imageMapView);
                
                if (exportType.equals(JPEG)) {
                    ImageIO.write(exportBufferedImage, "JPEG", exportFile);
                } else if (exportType.equals(PNG)) {
                    ImageIO.write(exportBufferedImage, "PNG",  exportFile);
                }                 
            } else if (exportType.equals(SVG)) {
                SvgExporter svgExporter = new SvgExporter();
                svgExporter.exportMap(mapData, exportFile);
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in ExportMapToImage.execute() - " + e);
        } 
    }

    @Override
    public void undo() {
        //no undo
    }
    
}
