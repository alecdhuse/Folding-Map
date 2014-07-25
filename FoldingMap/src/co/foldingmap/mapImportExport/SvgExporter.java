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

import co.foldingmap.Logger;
import co.foldingmap.map.vector.MultiGeometry;
import co.foldingmap.map.vector.LatLonAltBox;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.CoordinateList;
import co.foldingmap.map.vector.VectorObjectList;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.MapPoint;
import co.foldingmap.map.vector.LineString;
import co.foldingmap.map.vector.Polygon;
import co.foldingmap.map.themes.PolygonStyle;
import co.foldingmap.map.themes.MapTheme;
import co.foldingmap.map.themes.ColorStyle;
import co.foldingmap.map.themes.IconStyle;
import co.foldingmap.map.themes.LineStyle;
import co.foldingmap.map.labeling.PointLabel;
import co.foldingmap.map.labeling.MapLabel;
import co.foldingmap.map.labeling.LineStringLabel;
import co.foldingmap.map.labeling.PolygonLabel;
import co.foldingmap.map.labeling.LabelManager;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.Layer;
import co.foldingmap.map.MapView;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author Alec
 */
public class SvgExporter {

    private int  indentCount;
       
    public SvgExporter() {
        indentCount = 0;
    }
    
    /** Adds an Indent to the indent counter.  
     * This counter is used to properly indent XML tags for easy reading in
     * the exported SVG file.
     */    
    private void addIndent() {
        indentCount++;
    }
    
    private void exportLayer(BufferedWriter outputStream, 
                             Layer layer, 
                             MapTheme theme, 
                             float maxY) {
        
        if (layer instanceof VectorLayer) {
            exportVectorLayer(outputStream, (VectorLayer) layer, theme, maxY);
        } else {
            Logger.log(Logger.ERR, "Export of layers other than VectorLayer not supported at this time.");
        }
    }
    
    private void exportVectorLayer(BufferedWriter outputStream, 
                                   VectorLayer layer, 
                                   MapTheme theme, 
                                   float maxY) {
        
        VectorObjectList<VectorObject> lineStrings;
        
        try {
            outputStream.write(getIndent());
            outputStream.write("<g\n");
            addIndent();
            outputStream.write(getIndent());
            outputStream.write("id=\"");
            outputStream.write(layer.getName());
            outputStream.write("\">\n\n");
            removeIndent();
            
            //Get a list of all LineStrings including those in MultiGeometries
            lineStrings = new VectorObjectList<VectorObject>();
            lineStrings.addAll(layer.getObjectList().getLineStrings());
            
            for (VectorObject object: layer.getObjectList().getMultiGeometries()) 
                lineStrings.addAll(((MultiGeometry) object).getComponentObjects().getLineStrings());            
            
            //Draw LineString outlines first          
            for (VectorObject object: lineStrings) {
                ColorStyle style = theme.getStyle(object.getObjectClass());
                writeSvgLine(outputStream, object.getName(), object.getCoordinateList(), style, true);
            }
            
            for (VectorObject object: layer.getObjectList()) 
                exportVectorObject(outputStream, object, theme, maxY);               
            
            outputStream.write("</g>\n");
            removeIndent();
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in SvgExporter.exportVectorLayer(ObjectOutputStream, VectorLayer, MapTheme) - " + e);
        }
    }
        
    public void exportVectorObject(BufferedWriter outputStream, 
                                   VectorObject object, 
                                   MapTheme theme, 
                                   float maxY) {
        ColorStyle  style;
        
        style = theme.getStyle(object.getObjectClass());
        
        if (object instanceof Polygon) {
            writePath(outputStream, object.getName(), object.getCoordinateList(), style, true);
        } else if (object instanceof MapPoint) {
            writeImage(outputStream, (IconStyle) style, object.getCoordinateList().get(0), object.getName());            
        } else if (object instanceof LineString) {
            writeSvgLine(outputStream, object.getName(), object.getCoordinateList(), style, false);                        
        } else if (object instanceof MultiGeometry) {
            try {
                outputStream.write(getIndent());
                outputStream.write("<g id=\"");                    
                outputStream.write(object.getName());
                outputStream.write("\">\n\n");                    

                addIndent();

                for (VectorObject subObject: ((MultiGeometry) object).getComponentObjects()) 
                    exportVectorObject(outputStream, subObject, theme, maxY);                                                                                                            

                removeIndent(); 
                outputStream.write(getIndent());
                outputStream.write("</g>\n");
            } catch (Exception e) {
                Logger.log(Logger.ERR, "Error in SvgExporter.exportVectorObject() while exporting MultiGeometry: " + e);
            }                   
        } else {
            writePath(outputStream, object.getName(), object.getCoordinateList(), style, false);
        }            
    }
    
    private void exportMapLabel(BufferedWriter outputStream,
                                MapLabel label) {
        
        Color   outlineColor, fillColor;
        float   x, y;
        Font    labelFont;
        String  style, fontStyle;
        
        try {
            labelFont = label.getFont();
            
            //construct style
            if (label.getFillColor() != null) {
                fillColor = label.getFillColor();
            } else {
                fillColor = Color.BLACK;
            }
            
            if (label.getOutlineColor() != null) {
                outlineColor = label.getOutlineColor();
            } else {
                outlineColor = Color.WHITE;
            }
            
            if (labelFont.getStyle() == Font.BOLD) {
                fontStyle = "font-weight=\"bold\"";
            } else if (labelFont.getStyle() == Font.PLAIN) {
                fontStyle = "font-style=\"normal\"";
            } else if (labelFont.getStyle() == Font.ITALIC) {
                fontStyle = "font-style=\"italic\"";
            } else {
                fontStyle = "";
            }
            
            style  = "font-family=\"" + labelFont.getFamily() + "\" font-size=\"" + labelFont.getSize() + "\" " + fontStyle + " ";
            
            outputStream.write(getIndent());
            outputStream.write("<g " + style + ">\n"); 
            addIndent();
            
            style = "fill:#" + getHexColor(fillColor) + ";stroke:#" + getHexColor(outlineColor);    
                
            if (label instanceof PointLabel) {
                PointLabel pointLabel = (PointLabel) label;
                
                x = pointLabel.getLine1StartPoint().x;
                y = pointLabel.getLine1StartPoint().y;
                                        
                outputStream.write(getIndent());
                outputStream.write("<text x=\"");     
                outputStream.write(Float.toString(x));
                outputStream.write("\" y=\"");  
                outputStream.write(Float.toString(y));
                outputStream.write("\" style=\""); 
                outputStream.write(style); 
                outputStream.write("\">\n");
                
                addIndent();
                outputStream.write(getIndent());
                outputStream.write("<tspan x=\"");
                outputStream.write(Float.toString(x));
                outputStream.write("\" y=\"");  
                outputStream.write(Float.toString(y));                
                outputStream.write("\">"); 
                outputStream.write(pointLabel.getLine1Text());
                outputStream.write("</tspan>\n"); 
                
                if (pointLabel.getLine2Text() != null && pointLabel.getLine2Text().length() > 0) { 
                    x = pointLabel.getLine2StartPoint().x;
                    y = pointLabel.getLine2StartPoint().y;
                    
                    if (x != 0 && y != 0) {
                        outputStream.write(getIndent());
                        outputStream.write("<tspan x=\"");
                        outputStream.write(Float.toString(x));
                        outputStream.write("\" y=\"");  
                        outputStream.write(Float.toString(y));                
                        outputStream.write("\">"); 
                        outputStream.write(pointLabel.getLine2Text());
                        outputStream.write("</tspan>\n");      
                    }
                }
                
                removeIndent();
                outputStream.write(getIndent());
                outputStream.write("</text>\n");      
                
                removeIndent();
                outputStream.write(getIndent());
                outputStream.write("</g>\n");                 
            } else if (label instanceof LineStringLabel) {
                LineStringLabel lineLabel = (LineStringLabel) label;
                
            } else if (label instanceof PolygonLabel) {
                PolygonLabel polyLabel = (PolygonLabel) label;
                
            }
            

        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in SvgExporter.exportMapLabel(BufferedWriter, MapLabel) - " + e);
        }
    }
    
    public void exportMap(DigitalMap mapData, File outputFile) {
        BufferedWriter      outputStream;
        float               height, width, maxX;
        LabelManager        labelManager;
        LatLonAltBox        mapBounds;
        Layer               currentLayer;
        MapView             mapView;
        
        
        try {
            height       = 600;
            width        = 1200;
            mapView      = mapData.getLastMapView();
            mapBounds    = mapData.getBoundary();            
            outputStream = new BufferedWriter(new FileWriter(outputFile));
            labelManager = mapView.getLabelManager();
            
            //mapView.getMapProjection().setReference(new Coordinate(0, 90, -180));
            mapData.calculateCoordinateLocations(mapView);
            height = mapView.getY(mapBounds.getSouthWestCoordinate());
            maxX   = mapView.getX(mapBounds.getNorthEastCoordinate(), MapView.NO_WRAP);
                    
            outputStream.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
            outputStream.write("<!-- Created with FoldingMap (http://www.foldingmap.co/) -->\n\n");
            
            outputStream.write("<svg\n");
            addIndent();
            outputStream.write(getIndent());
            outputStream.write("xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n");
            outputStream.write(getIndent());
            outputStream.write("xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n");
            outputStream.write(getIndent());
            outputStream.write("xmlns:svg=\"http://www.w3.org/2000/svg\"\n");
            outputStream.write(getIndent());
            outputStream.write("xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n");
            outputStream.write(getIndent());
            outputStream.write("xmlns=\"http://www.w3.org/2000/svg\"\n");
            
            //height and width
            outputStream.write(getIndent());
            outputStream.write("width=\"");
            outputStream.write(Float.toString(width));
            outputStream.write("\"\n");
            outputStream.write(getIndent());
            outputStream.write("height=\"");
            outputStream.write(Float.toString(height));
            outputStream.write("\"\n");            
            outputStream.write(getIndent());
            outputStream.write("id=\"");
            outputStream.write(mapData.getName());
            outputStream.write("\">\n\n");  
            
            //loop backwards through the layers, since the first layer is on the bottom.
            for (int i = mapData.getLayers().size() - 1; i >= 0; i--) {
                currentLayer = mapData.getLayer(i);
                exportLayer(outputStream, currentLayer, mapData.getTheme(), height);
            }
            
            //write Labels
            for (MapLabel label: labelManager.getLabels()) {
                exportMapLabel(outputStream, label);
            }
            
            removeIndent();
            outputStream.write("</svg>");
            outputStream.close();
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in SvgExporter.exportMap(DigitalMap) - " + e);
        }
    }
    
    /**
     * Generates the SVG style string from a given ColorStyle
     * 
     * @param style
     * @return 
     */
    private String generateLineStyleString(ColorStyle style, boolean isOutline) { 
        Color         strokeColor;
        float         width;
        LineStyle     lineStyle;
        StringBuilder sb;
        
        lineStyle = (LineStyle) style;
        sb        = new StringBuilder();
        width     = lineStyle.getLineWidth();
                
        if (isOutline) {
            width       += 1.2f;
            strokeColor  = lineStyle.getOutlineColor();
        } else {
            strokeColor  = lineStyle.getFillColor();
        }
             
        sb.append("fill-opacity:0;");
        
        sb.append("stroke:#");
        sb.append(getHexColor(strokeColor)); 
        sb.append(";");       
        
        sb.append("stroke-width:");
        sb.append(Float.toString(width)); 
        sb.append(";");           
        
        sb.append("stroke-linecap:round;");
        
        return sb.toString();
    }   
    
    /**
     * Generates the SVG style string from a given ColorStyle
     * 
     * @param style
     * @return 
     */
    private String generateStyleString(ColorStyle style) {
        float         width;
        StringBuilder sb     = new StringBuilder();
        
        if (style instanceof PolygonStyle) {
            width = ((PolygonStyle) style).getLineWidth();                
        } else if (style instanceof LineStyle) {
            width = ((LineStyle) style).getLineWidth();        
        } else {
            width = 1;            
        }
        
        //Fill Color
        if (style.isFilled()) {
            sb.append("fill:#");
            sb.append(getHexColor(style.getFillColor())); 
            sb.append(";");
            sb.append("fill-opacity:1;");
        } else {
            sb.append("fill:none;");
        }
        
        //Outline Color
        if (style.getOutlineStyles().size() > 0) {
            //no stroke, outlines will be seperate objects
            sb.append("stroke:none");     
            sb.append("stroke-opacity:0");
        } else {
            sb.append("stroke:#");
            sb.append(getHexColor(style.getOutlineColor()));
            sb.append(";");            
            sb.append("stroke-opacity:1;");
        }
        
        //Stroke Width
        if (style.getOutlineStyles().size() > 0) {
            //no stroke, outlines will be seperate objects
            sb.append("stroke-width:0px");            
        } else {
            sb.append("stroke:");
            sb.append(width);
            sb.append("px;");             
        }        
        
        sb.append("stroke-linecap:butt;");
        sb.append("stroke-linejoin:miter");        
        
        return sb.toString();
    }
    
   /**
     * Returns the hex version of a Color object.  
     * Output is red, green, blue.
     * 
     * @param c
     * @return 
     */
    public static String getHexColor(Color c) {
        String hexColor, b, g, r;

        b = Integer.toHexString(c.getBlue());
        g = Integer.toHexString(c.getGreen());
        r = Integer.toHexString(c.getRed());

        if (b.length() == 1)
            b = "0" + b;

        if (g.length() == 1)
            g = "0" + g;

        if (r.length() == 1)
            r = "0" + r;

        hexColor =  r + g + b;

        return hexColor;
    }    
    
    private String getIndent() {
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < indentCount; i++) 
            sb.append("\t");
            
        return sb.toString();
    }    
    
    private void removeIndent() {
        indentCount--;
    }    
    
    private void writeImage(BufferedWriter outputStream, 
                           IconStyle style, 
                           Coordinate c, 
                           String id) {
        
        BufferedImage         bi;
        byte[]                imageBytes;
        ByteArrayOutputStream baos;
        float                 x, y, height, width;
        ImageIcon             image;
        OutputStream          os64;
        Point2D.Float        point;
        String                imageEnc;        
        
        try {
            point = c.getCenterPoint();
            
            if (style.getImageFile() == null) {
                //No image
                outputStream.write(getIndent());
                outputStream.write("<circle cx=\"");
                outputStream.write(Float.toString(point.x - 2));
                outputStream.write("\" cy=\"");
                outputStream.write(Float.toString(point.y - 2));
                outputStream.write("\" r=\"2\" stroke=\"#");                
                outputStream.write(getHexColor(style.getOutlineColor()));
                outputStream.write("\" stroke-width=\"1\"  fill=\"");
                outputStream.write(getHexColor(style.getFillColor()));
                outputStream.write("\"/>\n");
            } else {            
                //Has image
                //first encode image into a string
                baos = new ByteArrayOutputStream();
                bi   = ImageIO.read(style.getImageFile());            

                ImageIO.write(bi, "PNG", baos);        
                imageBytes = baos.toByteArray();               
                imageEnc   = Base64.encodeBase64String(imageBytes);

                baos.close();            

                image    = style.getObjectImage();  
                height   = image.getIconHeight();
                width    = image.getIconWidth();
                x        = point.x - (width  / 2.0f);
                y        = point.y - (height / 2.0f);            

                outputStream.write(getIndent());
                outputStream.write("<image\n");            
                addIndent();

                //y coordiante
                outputStream.write(getIndent());
                outputStream.write("y=\""); 
                outputStream.write(Float.toString(y)); 
                outputStream.write("\"\n"); 

                //x coordiante
                outputStream.write(getIndent());
                outputStream.write("x=\""); 
                outputStream.write(Float.toString(x)); 
                outputStream.write("\"\n");  

                //id
                outputStream.write(getIndent());
                outputStream.write("id=\""); 
                outputStream.write(id); 
                outputStream.write("\"\n");             

                //xlink
                outputStream.write(getIndent());
                outputStream.write("xlink:href=\"data:image/png;base64,"); 
                outputStream.write(imageEnc);
                outputStream.write("\"\n"); 

                //x coordiante
                outputStream.write(getIndent());
                outputStream.write("height=\""); 
                outputStream.write(Float.toString(height)); 
                outputStream.write("\"\n");        

                //y coordiante
                outputStream.write(getIndent());
                outputStream.write("width=\""); 
                outputStream.write(Float.toString(width)); 
                outputStream.write("\" />\n");           
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in SvgExporter.writeImage() - " + e);
        }
    }
    
    private void writePath(BufferedWriter outputStream, 
                          String id,
                          CoordinateList<Coordinate> coordinates, 
                          ColorStyle style,
                          boolean closePath) {
        
        Coordinate  c;
        String      styleString;
        
        try {
            styleString = generateStyleString(style);
        
            outputStream.write(getIndent());
            outputStream.write("<path\n");
            addIndent();
            
            outputStream.write(getIndent());
            outputStream.write("style=\"");
            outputStream.write(styleString);
            outputStream.write("\"\n");
            
            //write out points
            outputStream.write(getIndent());
            outputStream.write("d=\"M ");
            
            for (int i = 0; i < coordinates.size(); i++) {
                c = coordinates.get(i);
                
                if (i > 0) {
                    outputStream.write("L ");
                }
                
                outputStream.write(Float.toString(c.getCenterPoint().x));
                outputStream.write(" ");
                outputStream.write(Float.toString(c.getCenterPoint().y ));
                
                if (i+1 < coordinates.size())
                    outputStream.write(" ");
            }
            
            if (closePath) outputStream.write(" Z");
            
            outputStream.write("\"\n");
            
            //write out id
            outputStream.write(getIndent());
            outputStream.write("id=\"");
            outputStream.write(id);
            outputStream.write("\"");
            
            //close tag
            outputStream.write(" />\n");
            removeIndent();             
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in SvgExporter.writePath(BufferedWriter, String, CoordinateList, ColorStyle - " + e);
        }             
    }
    
    private void writeSvgLine(BufferedWriter outputStream, 
                              String id,
                              CoordinateList<Coordinate> coordinates, 
                              ColorStyle style,
                              boolean isOutline) {
        Coordinate  c;        
        String      styleString;
        
        try {                        
            styleString = generateLineStyleString(style, isOutline);
            
            addIndent();    
            outputStream.write(getIndent());
            outputStream.write("<polyline points=\"");

            for (int i = 0; i < coordinates.size(); i++) {
                c = coordinates.get(i);
                
                outputStream.write(Float.toString(c.getCenterPoint().x));
                outputStream.write(",");
                outputStream.write(Float.toString(c.getCenterPoint().y )); 
                
                if (i+1 < coordinates.size()) outputStream.write(" ");
            }
                        
            outputStream.write("\"\n");
            outputStream.write(getIndent());
            outputStream.write("style=\"");
            outputStream.write(styleString);
            outputStream.write("\" />\n");
            removeIndent();  
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in SvgExporter.writePolyLine(BufferedWriter, String, CoordinateList, ColorStyle - " + e);
        }     
    }    
}
