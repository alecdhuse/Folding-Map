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
package co.foldingmap.graphicsSupport;

import java.awt.RenderingHints.Key;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.junit.Ignore;

/**
 * This class is used when testing Objects that require a Graphics2D class.
 * 
 * @author Alec
 */
@Ignore
public class Graphics2DTest extends Graphics2D {
    public AffineTransform   affineTransform;
    public Color             backgroundColor, drawColor;
    public FontMetrics       fontMetrics;
    public RenderingHints    renderingHints;
    public Stroke            stroke;
    
    public Graphics2DTest() {
        super();
        
        affineTransform = new AffineTransform();
        renderingHints  = new RenderingHints(new HashMap<RenderingHints.Key,Object>());
    } 
    
    @Override
    public void draw(Shape shape) {
        
    }

    @Override
    public boolean drawImage(Image image, AffineTransform at, ImageObserver io) {

        return true;
    }

    @Override
    public void drawImage(BufferedImage bi, BufferedImageOp bio, int i, int i1) {

    }

    @Override
    public void drawRenderedImage(RenderedImage ri, AffineTransform at) {

    }

    @Override
    public void drawRenderableImage(RenderableImage ri, AffineTransform at) {

    }

    @Override
    public void drawString(String string, int i, int i1) {

    }

    @Override
    public void drawString(String string, float f, float f1) {

    }

    @Override
    public void drawString(AttributedCharacterIterator aci, int i, int i1) {

    }

    @Override
    public void drawString(AttributedCharacterIterator aci, float f, float f1) {

    }

    @Override
    public void drawGlyphVector(GlyphVector gv, float f, float f1) {

    }

    @Override
    public void fill(Shape shape) {

    }

    @Override
    public boolean hit(Rectangle rctngl, Shape shape, boolean bln) {

        return true;
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        System.out.println("Grapgics2DTest.getDeviceConfiguration() called");
        return null;
    }

    @Override
    public void setComposite(Composite cmpst) {

    }

    @Override
    public void setPaint(Paint paint) {

    }

    @Override
    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    @Override
    public void setRenderingHint(Key key, Object o) {
        renderingHints.put(key, o);
    }

    @Override
    public Object getRenderingHint(Key key) {        
        return renderingHints.get(key);
    }

    @Override
    public void setRenderingHints(Map<?, ?> map) {
        HashMap hashMap = new HashMap(map);
        renderingHints = new RenderingHints(hashMap);
    }

    @Override
    public void addRenderingHints(Map<?, ?> map) {
        HashMap hashMap = new HashMap(map);
        
        Set      set = hashMap.entrySet();
        Iterator it  = set.iterator();

        while (it.hasNext()) {
          Map.Entry entry = (Map.Entry) it.next();
          renderingHints.put(entry.getKey(), entry.getValue());
        }        
    }

    @Override
    public RenderingHints getRenderingHints() {
        return renderingHints;
    }

    @Override
    public void translate(int i, int i1) {
        
    }

    @Override
    public void translate(double d, double d1) {

    }

    @Override
    public void rotate(double d) {

    }

    @Override
    public void rotate(double d, double d1, double d2) {

    }

    @Override
    public void scale(double d, double d1) {

    }

    @Override
    public void shear(double d, double d1) {

    }

    @Override
    public void transform(AffineTransform at) {

    }

    @Override
    public void setTransform(AffineTransform at) {

    }

    @Override
    public AffineTransform getTransform() {
        return null;
    }

    @Override
    public Paint getPaint() {
        System.out.println("Grpahics2DText.getPaint() called");
        return null;
    }

    @Override
    public Composite getComposite() {
        System.out.println("Grpahics2DText.getComposite() called");
        return null;
    }

    @Override
    public void setBackground(Color color) {
        this.backgroundColor = color;
    }

    @Override
    public Color getBackground() {
        return this.backgroundColor;
    }

    @Override
    public Stroke getStroke() {
        return this.stroke;
    }

    @Override
    public void clip(Shape shape) {
        
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        boolean            isAntiAliased, usesFractionalMetrics;
        FontRenderContext  fontRenderContext;
        
        if (renderingHints.containsKey(RenderingHints.KEY_ANTIALIASING)) {
            Object aaObject = renderingHints.get(RenderingHints.KEY_ANTIALIASING);
            
            if (aaObject.equals(RenderingHints.VALUE_ANTIALIAS_ON)) {
                isAntiAliased = true;
            } else {
                isAntiAliased = false;
            }
        } else {
            isAntiAliased = false;
        }
        
        if (renderingHints.containsKey(RenderingHints.KEY_FRACTIONALMETRICS)) {
            Object fractObj = renderingHints.get(RenderingHints.KEY_FRACTIONALMETRICS);
            
            if (fractObj.equals(RenderingHints.VALUE_FRACTIONALMETRICS_ON)) {
                usesFractionalMetrics = true;
            } else {
                usesFractionalMetrics = false;
            }
        } else {
            usesFractionalMetrics = false;
        }
        
        fontRenderContext = new FontRenderContext(affineTransform, isAntiAliased, usesFractionalMetrics);
        
        return fontRenderContext;
    }

    @Override
    public Graphics create() {
        return this;
    }

    @Override
    public Color getColor() {
        return this.drawColor;
    }

    @Override
    public void setColor(Color color) {
        this.drawColor = color;
    }

    @Override
    public void setPaintMode() {

    }

    @Override
    public void setXORMode(Color color) {

    }

    @Override
    public Font getFont() {
        return this.fontMetrics.getFont();
    }

    @Override
    public void setFont(Font font) {
        this.fontMetrics = new FontMetricsTest(font);
    }

    @Override
    public FontMetrics getFontMetrics(Font font) {
        this.fontMetrics = new FontMetricsTest(font);
        return fontMetrics;
    }

    @Override
    public Rectangle getClipBounds() {
        return null;
    }

    @Override
    public void clipRect(int i, int i1, int i2, int i3) {

    }

    @Override
    public void setClip(int i, int i1, int i2, int i3) {

    }

    @Override
    public Shape getClip() {
        return null;
    }

    @Override
    public void setClip(Shape shape) {

    }

    @Override
    public void copyArea(int i, int i1, int i2, int i3, int i4, int i5) {

    }

    @Override
    public void drawLine(int i, int i1, int i2, int i3) {

    }

    @Override
    public void fillRect(int i, int i1, int i2, int i3) {

    }

    @Override
    public void clearRect(int i, int i1, int i2, int i3) {

    }

    @Override
    public void drawRoundRect(int i, int i1, int i2, int i3, int i4, int i5) {

    }

    @Override
    public void fillRoundRect(int i, int i1, int i2, int i3, int i4, int i5) {

    }

    @Override
    public void drawOval(int i, int i1, int i2, int i3) {

    }

    @Override
    public void fillOval(int i, int i1, int i2, int i3) {

    }

    @Override
    public void drawArc(int i, int i1, int i2, int i3, int i4, int i5) {

    }

    @Override
    public void fillArc(int i, int i1, int i2, int i3, int i4, int i5) {

    }

    @Override
    public void drawPolyline(int[] ints, int[] ints1, int i) {

    }

    @Override
    public void drawPolygon(int[] ints, int[] ints1, int i) {

    }

    @Override
    public void fillPolygon(int[] ints, int[] ints1, int i) {

    }

    @Override
    public boolean drawImage(Image image, int i, int i1, ImageObserver io) {
        return true;
    }

    @Override
    public boolean drawImage(Image image, int i, int i1, int i2, int i3, ImageObserver io) {
        return true;
    }

    @Override
    public boolean drawImage(Image image, int i, int i1, Color color, ImageObserver io) {
        return true;
    }

    @Override
    public boolean drawImage(Image image, int i, int i1, int i2, int i3, Color color, ImageObserver io) {
        return true;
    }

    @Override
    public boolean drawImage(Image image, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7, ImageObserver io) {
        return true;
    }

    @Override
    public boolean drawImage(Image image, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7, Color color, ImageObserver io) {
        return true;
    }

    @Override
    public void dispose() {
        
    }
    
}
