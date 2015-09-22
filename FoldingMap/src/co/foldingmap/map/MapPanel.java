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
package co.foldingmap.map;

import co.foldingmap.map.vector.CoordinateList;
import co.foldingmap.map.vector.CoordinateMath;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorObjectList;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.MapPoint;
import co.foldingmap.GUISupport.Updateable;
import co.foldingmap.GUISupport.htmlRendering.HtmlRenderer;
import co.foldingmap.Logger;
import co.foldingmap.ResourceHelper;
import co.foldingmap.actions.Actions;
import co.foldingmap.map.themes.MapTheme;
import co.foldingmap.map.visualization.TimeSpanControl;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Popup;

/**
 * This class extends JPanel and handles the displaying of map,
 * represented by the DigitalMap Class.
 * 
 * @author Alec
 */
public class MapPanel extends JPanel implements KeyListener, 
                                                MapAccepter,
                                                MouseListener, 
                                                MouseMotionListener, 
                                                MouseWheelListener, 
                                                Updateable {
    
    //Map Modes for how the map acts and is displayed.
    public static final int DRAG_PAN           = 0;
    public static final int DRAG_SELECT        = 1;
    public static final int MODIFY             = 2;
    public static final int TRACE              = 3;

    public static final int SINGLE_CLICK_WIDTH = 8;
    
    private Actions                     actions;
    private ArrayList<Shape>            points;
    private ArrayList<Updateable>       updateables;
    private BasicStroke                 dashedStroke1, dashedStroke2;
    private boolean                     allowShiftZoom, controlPressed, shiftPressed;
    private boolean                     showObjectPopup, showScale, showSelectRectangle, showZoomControls;
    private boolean                     coordinateDrag, dragging, moveSelectedPoint, panelLocked;    
    private boolean                     zoomControlClicked;
    private Coordinate                  coordinateToModify, coordinateToModifyOriginal;
    private CoordinateList<Coordinate>  traceCoordinates;
    private DigitalMap                  mapData;
    private Ellipse2D                   ellipseZoomIn, ellipseZoomOut;
    private float                       currentX, currentY;
    private float                       xDragStart, yDragStart;
    private float                       scaleX, scaleY;
    private float                       xOrigin, yOrigin; 
    private ImageIcon                   iconPlus, iconMinus;
    private int                         dragMode, previousDragMode;
    private MapObjectList<MapObject>    traceMergeObjects;
    private MapView                     currentMapView;    
    private Path2D                      tracePath;
    private Popup                       objectPopup;
    private Rectangle2D                 rectangleSelection;
    private RenderingHints              renderAntialiasing;
    private ResourceHelper              helper;
    private TimeSpanControl             timeSpanControl; 
    
    /**
     * Constructor for objects of class MapPanel
     */
    public MapPanel() {
        init();                                           
    }    
    
    /**
     * Add an Updateable to this panel.  These are called when certain events
     * happen in the panel.
     * 
     * @param u 
     */
    public void addUpdateable(Updateable u) {
        this.updateables.add(u);
    }
    
    private void clearTracePath() {
        traceCoordinates.clear();
        traceMergeObjects.clear();
    }
    
    /**
     * Creates the visual trace path used when using the trace merge function.
     */
    private void createTracePath() {
       float      x, y;
       int        index;
       Shape      point;
               
       try {
           points      = new ArrayList<Shape>();
           tracePath   = new Path2D.Double();
           index       = -1;

           for (Coordinate currentCoordinate: traceCoordinates) {
               index++;
               
               point = VectorObject.getPointShape(currentCoordinate, MapView.NO_WRAP);
               x     = currentMapView.getX(currentCoordinate, MapView.NO_WRAP);
               y     = currentMapView.getY(currentCoordinate);
                
               if (index == 0) {
                   tracePath.moveTo(x, y);
               } else {
                   tracePath.lineTo(x, y);
               }
               
               points.add(point);
           }
        } catch (Exception e) {
            System.err.println("Error in MapPanel.createTracePath() - " + e);
        }
    }    
    
    /**
     * Performs the trace-merge operation
     * 
     * @param e
     * @param xDragDifference
     * @param yDragDifference
     */
    private void doTrace() {        
        try {
            mapData.deselectObjects();                                        
            mapData.getActions().traceMerge(mapData, new VectorObjectList(traceMergeObjects), traceCoordinates, updateables);
            
            tracePath = null;           
            this.clearTracePath();         
        } catch (Exception e) {
            System.err.println("Error in MapPanel.doTrace(MouseEvent, float, float) - " + e);
        }
    }    
    
    /**
     * Moves the View Port and updates dragging information.
     * 
     * @param e
     * @param xDragDifference
     * @param yDragDifference 
     */
    private void dragPanMap(MouseEvent e, double xDragDifference, double yDragDifference) {
        xDragStart = e.getX();
        yDragStart = e.getY();
        scroll(xDragDifference, yDragDifference);
    }    
    
    /**
     * Draws the object popup for a single selected object.
     * 
     * @param g2 
     */
    private void drawObjectPopup(Graphics2D g2) {
        BasicStroke              lineStroke;
        boolean                  hasDescription;
        float                    popupHeight, popupWidth, popupX, popupY;        
        FontMetrics              fm;
        HtmlRenderer             htmlRenderer;
        MapObject                selectedObject;
        MapObjectList<MapObject> selectedObjects;   
        Rectangle2D              descBounds, titleBounds;
        RoundRectangle2D         popup;
        String                   title;        
        
        try {
            htmlRenderer    = null;
            lineStroke      = new BasicStroke(2,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            selectedObjects = this.mapData.getSelectedObjects();

            g2.setFont(g2.getFont().deriveFont(Font.BOLD));        
            fm  = g2.getFontMetrics();

            if (selectedObjects.size() == 1 && dragging == false) {   

                selectedObject = selectedObjects.get(dragMode);
                title          = (selectedObject.getName().length() > 0 ?  selectedObject.getName() : "(Unnamed)");

                //If the selected object is a MapPoint use the point to anchor and not the click pos
                if (selectedObject instanceof MapPoint) {
                    Coordinate c = selectedObject.getCoordinateList().get(0);
                    popupX = (float) c.getCenterPoint().getX();
                    popupY = (float) c.getCenterPoint().getY() - 10;
                } else {
                    popupX = currentX;
                    popupY = currentY;                     
                }

                if (selectedObject.getDescription() != null && 
                    selectedObject.getDescription().length() > 0) {            

                    titleBounds    = fm.getStringBounds(title, g2);
                    hasDescription = true;
                    popupHeight    = 100;
                    popupWidth     = 200;      
                    popupX         = popupX - (popupWidth / 2);
                    popupY         = popupY - popupHeight - 10;                  
                    htmlRenderer   = new HtmlRenderer(selectedObject.getDescription(), (popupX + 5), (popupY + 30), (popupHeight - 35), (popupWidth - 10));                
                    descBounds     = htmlRenderer.getBounds(g2);                
                    popupHeight    = (float) (descBounds.getHeight() + 30); 
                    popupWidth     = (float) (descBounds.getWidth() + 10);     
                    popupWidth     = (float) Math.max(popupWidth, titleBounds.getWidth() + 10);              
                } else {
                    hasDescription = false;
                    popupHeight    = 22;                                
                    Rectangle2D r  = fm.getStringBounds(title, g2);
                    popupWidth     = (float) (r.getWidth() + 10);
                    popupX  = popupX - (popupWidth / 2);
                    popupY  = popupY - popupHeight;                  
                }

                popup = new RoundRectangle2D.Float(popupX, popupY, popupWidth, popupHeight, 6, 6);                 

                g2.setColor(new Color(68,68,68,180));
                g2.fill(popup);            

                g2.setColor(new Color(255,255,255,180));
                g2.setStroke(lineStroke);
                g2.draw(popup);

                g2.setFont(g2.getFont().deriveFont(Font.BOLD)); 
                g2.drawString(title, popupX + 5, popupY + 15);

                if (hasDescription == true && htmlRenderer != null) {
                    htmlRenderer.draw(g2);
                } //End Description Check
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error ing MapPanel.drawObjectPopup(Graphics2D) - " + e);
        }
    }
    
    /**
     * Draws the scale of the current view of the map .
     *
     * @param g2 - The graphics of the MapPanel
     */
    private void drawScale(Graphics2D g2) {
        BasicStroke lineStroke;
        Coordinate  c1, c2;
        float       lat, lon, scaleDistanceDouble;
        float       scaleDisplayX1, scaleDisplayX2, scaleDisplayY1, scaleDisplayY2;
        int         scaleDistance;
        Rectangle2D scaleRectangle;
        String      scaleLabel;

        //Set the left side of the scale
        scaleDisplayX1 = 15;
        scaleDisplayY1 = (this.getHeight() - 35);
        lat = currentMapView.getLatitude(scaleDisplayX1, scaleDisplayY1);
        lon = currentMapView.getLongitude(scaleDisplayX1, scaleDisplayY1);
        c1  = new Coordinate(0, lat, lon, false);

        scaleDistanceDouble =  getPanelWidthInKm() / 10;
        lineStroke = new BasicStroke(1,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

        g2.setStroke(lineStroke);

        if (scaleDistanceDouble < 1.0) {
            scaleDistance = (int) ((scaleDistanceDouble) * 10);
            scaleDistance = scaleDistance * 100;

            if (scaleDistance <= 0) {
                scaleDistance = (int) ((scaleDistanceDouble) * 100);
                scaleDistance = scaleDistance * 10;

                if (scaleDistance <= 0) 
                    scaleDistance = (int) ((scaleDistanceDouble) * 1000);
            }

            scaleLabel    = Integer.toString(scaleDistance) + " m";
            scaleDistanceDouble = (scaleDistance / 1000.0f);
        } else {
            scaleDistance = (int) scaleDistanceDouble;
            scaleLabel    = Integer.toString(scaleDistance) + " km";
            scaleDistanceDouble = scaleDistance;
        }

        c2 = c1.reckonCoordinate(scaleDistanceDouble, 90);        
        scaleDisplayX2 = (float) currentMapView.getMapProjection().getX(c2);
        scaleDisplayY2 = currentMapView.getY(c2);

        scaleRectangle = new Rectangle2D.Double(scaleDisplayX1, scaleDisplayY1, (scaleDisplayX2 - scaleDisplayX1), 5);
        g2.setColor(Color.BLACK);
        g2.draw(scaleRectangle);
        
        g2.drawString(scaleLabel, (float) scaleDisplayX1 - 1, (float) scaleDisplayY1 + 18);
        g2.drawString(scaleLabel, (float) scaleDisplayX1 + 1, (float) scaleDisplayY1 + 18);
        g2.drawString(scaleLabel, (float) scaleDisplayX1 + 0, (float) scaleDisplayY1 + 19);
        g2.drawString(scaleLabel, (float) scaleDisplayX1 + 0, (float) scaleDisplayY1 + 17);

        g2.setColor(Color.WHITE);
        g2.drawString(scaleLabel, (float) scaleDisplayX1, (float) scaleDisplayY1 + 18);

        g2.setColor(new Color(68,68,68,180));
        g2.fill(scaleRectangle);
    }    
        
    /**
     * Draws the zoom controls for the MapPanel
     *
     * @param g2 - The graphics used in this MapPanel
     */
    private void drawZoomControls(Graphics2D g2) {        
        int         zoomInX, zoomInY, zoomOutX, zoomOutY;
        
        zoomInX  = this.getWidth() - 30;
        zoomInY  = this.getHeight() - 35;
        zoomOutX = zoomInX - 25;
        zoomOutY = zoomInY;

        ellipseZoomIn  = new Ellipse2D.Float(zoomInX,  zoomInY,  20, 20);
        ellipseZoomOut = new Ellipse2D.Float(zoomOutX, zoomOutY, 20, 20);
        
        g2.drawImage(iconPlus.getImage(),  zoomInX,  zoomInY,  iconPlus.getImageObserver());
        g2.drawImage(iconMinus.getImage(), zoomOutX, zoomOutY, iconPlus.getImageObserver());
    }    
    
    /**
     * Runs update on all of this object's updatables.
     */
    private void fireUpdateables() {
        try {
            for (Updateable u: updateables) {
                if (u != null)
                    u.update();
            }
        } catch (Exception e) {
            System.err.println("Error in MapPanel.fireUpdateables() - " + e);
        }            
    }
    
    /**
     * Returns the Coordinate that represents the current mouse location.
     *
     * @return The Coordinate of the current mouse location.
     */
    public Coordinate getCoordinateAtMouseLocation() {
       float lat, lon;

       lat = currentMapView.getLatitude(currentX,  currentY);
       lon = currentMapView.getLongitude(currentX, currentY);
       
       return new Coordinate(0, lat, lon);
    }    
    
    /**
     * Returns the drag mode being used by this panel.
     * 
     * @return 
     */
    public int getDragMode() {
        return this.dragMode;
    }
    
    /**
     * Returns the DigitalMap being displayed in this panel.
     * 
     * @return 
     */
    public DigitalMap getMap() {
        return mapData;
    }
    
    /**
     * Returns the MapView being used by this panel.
     * 
     * @return 
     */
    public MapView getMapView() {
        return this.currentMapView;
    }
    
    /**
     * Returns the width of this MapPanel in Kilometers.  
     * 
     * @return 
     */
    public float getPanelWidthInKm() {
        Coordinate  c1, c2;
        float       widthInKm;
        float       panelLat1, panelLat2, panelLon1, panelLon2;

        panelLat1 = currentMapView.getLatitude(0,0);
        panelLat2 = currentMapView.getLatitude(this.getHeight(), this.getWidth());
        panelLon1 = currentMapView.getLongitude(0,0);
        panelLon2 = currentMapView.getLongitude(this.getHeight(), this.getWidth());

        c1 = new Coordinate(0, panelLat1, panelLon1, false);
        c2 = new Coordinate(0, panelLat2, panelLon2, false);

        widthInKm = CoordinateMath.getDistance(c1, c2)  / 1000.0f;

        return widthInKm;
    }    
    
    /**
     * Hides the Object Popup.
     * 
     */
    public void hideObjectPopup() {        
        if ((objectPopup != null) && showObjectPopup) {
            showObjectPopup = false;
            objectPopup.hide();
        }
    }    
    
    /**
     * Initiate objects used by the panel.
     * 
     */
    private void init() {
        this.helper            = ResourceHelper.getInstance();
        this.allowShiftZoom    = true;
        this.mapData           = new DigitalMap();
        this.showScale         = true;
        this.showZoomControls  = true;
        this.traceMergeObjects = new MapObjectList<MapObject>();                
        this.traceCoordinates  = new CoordinateList<Coordinate>();
        this.updateables       = new ArrayList<Updateable>();  
        
        this.xOrigin   = 0;
        this.yOrigin   = 0;
        this.dragMode  = DRAG_PAN;

        this.controlPressed      = false;
        this.panelLocked         = false;
        this.showSelectRectangle = false;
        this.coordinateToModify  = null;

        this.mapData        = new DigitalMap();
        this.currentMapView = new MapView(new MercatorProjection(0, 0, 1));

        this.renderAntialiasing = new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        this.renderAntialiasing.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);        
        
        this.iconPlus   = helper.getImage("add_button.png");
        this.iconMinus  = helper.getImage("minus_button.png");
        
        float DASH1[] = { 5.0f };
        dashedStroke1 = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 10.0f, DASH1, 0.0f);
        dashedStroke2 = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 10.0f, DASH1, 5.0f);           
        
        addKeyListener(this);        
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);        
    }     
    
    @Override
    public void keyPressed(KeyEvent ke) {

        if ((ke.getKeyCode() == KeyEvent.VK_KP_DOWN) || 
            (ke.getKeyCode() == KeyEvent.VK_DOWN)) {
            this.scroll(0, -10);
        } else if ((ke.getKeyCode() == KeyEvent.VK_KP_LEFT) || 
                   (ke.getKeyCode() == KeyEvent.VK_LEFT)) {
            this.scroll(10, 0);
        } else if ((ke.getKeyCode() == KeyEvent.VK_KP_RIGHT) || 
                   (ke.getKeyCode() == KeyEvent.VK_RIGHT)) {
            this.scroll(-10, 0);
        } else if ((ke.getKeyCode() == KeyEvent.VK_KP_UP) || 
                   (ke.getKeyCode() == KeyEvent.VK_UP)) {
            this.scroll(0, 10);
        } else if ((ke.getKeyCode() == KeyEvent.VK_ENTER)) {
            if (dragMode == TRACE) {
                this.doTrace();
            }
        } else if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
            clearTracePath();
        }        
        
        if (ke.getKeyCode() == KeyEvent.VK_CONTROL) {
            this.controlPressed = true;
        } else if (ke.getKeyCode()  == 157) {
            //apple command key
            this.controlPressed = true;
//        } else if (ke.getKeyCode()  == KeyEvent.VK_M) {
//            mapData.deselectObjects();
//            this.previousDragMode = this.dragMode;
//            this.dragMode         = DRAG_SELECT;
//            showSelectRectangle   = true;
        } else if (ke.getKeyCode()  == KeyEvent.VK_SHIFT) {
            //for shift zoom
            this.shiftPressed = true;
        }        
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        this.controlPressed = false;
        this.shiftPressed   = false;
    }
    
    @Override
    public void keyTyped(KeyEvent ke) {}    
    
    @Override
    public void mouseClicked(MouseEvent me) {
        Rectangle2D selection;
        
        currentX = me.getX();
        currentY = me.getY();        
        
        currentMapView.setLastMouseClickPosition(currentX, currentY);
        
        if (this.timeSpanControl != null && this.timeSpanControl.containsPoint(currentX, currentY)) {
            this.timeSpanControl.mouseClicked(currentX, currentY);
            timeSpanControl.addUpdateable(this);
        } else {        
            if (!panelLocked) {
                selection = new Rectangle2D.Float((me.getX() - (SINGLE_CLICK_WIDTH / 2.0f)), 
                                                  (me.getY() - (SINGLE_CLICK_WIDTH / 2.0f)), 
                                                   SINGLE_CLICK_WIDTH, SINGLE_CLICK_WIDTH); 

                if (me.getButton() == MouseEvent.BUTTON1) {
                    if (dragMode == DRAG_PAN || dragMode == MODIFY) {
                        if (!controlPressed) 
                            mapData.deselectObjects();

                        if (zoomControlClicked == false) {                        
                            mapData.highlightObject(selection, controlPressed);                    
                        }

                        if ((dragMode == MODIFY) && (mapData.getSelectedObjects().size() > 0)) {
                            MapObjectList<MapObject> selectedObjects;
                            
                            selectedObjects = (mapData.getSelectedObjects());

                            /** select the last element because that will be the newly selected VectorObject.  
                            * This will allow multiple component coordinates to be selected. */                        
                            MapObject   objectToModify  = selectedObjects.lastElement(); 
                            Coordinate  closeCoordinate = objectToModify.getCoordinateList().getCoordinateClosestTo(currentMapView.getLastMouseClickCoordinate());

                            objectToModify.setSelectedCoordinate(closeCoordinate);                        
                        }                                        
                    }

                    //show popup
                    if (dragMode == DRAG_PAN) {
                        if (mapData.getSelectedObjects().size() == 1) {
                            showObjectPopup(me.getXOnScreen(), me.getYOnScreen());
                        }
                    }          

                    if (dragMode == TRACE) {                        
                        boolean coordinateAdded = false;                        
                        
                        for (Coordinate c: currentMapView.getViewPortCoordinates()) {                            
                            //Check to see if alreay existing Coordinates have been clicked.
                            if (Math.abs(c.getCenterPoint().x - currentX) < 3 &&
                                Math.abs(c.getCenterPoint().y - currentY) < 3) {
                                
                                /* Check to see if the newly clicked point is the 
                                *  second to last added, if so remove it. */                                    
                                if (traceCoordinates.size() > 2 &&
                                    c.equals(traceCoordinates.get(traceCoordinates.size() - 2))) {

                                    traceCoordinates.remove(traceCoordinates.size() - 1);
                                    coordinateAdded = true;                             
                                } else {                                                                                   
                                    traceCoordinates.forceAdd(c);                                        
                                    coordinateAdded = true;    

                                    //If the coordinate used has only one parent, add it to the trace merge objects
                                    if (c.getParentVectorObjects().size() == 1)
                                        traceMergeObjects.add(c.getParentVectorObjects().get(0));

                                    //If the trace is closed, create a polygon.
                                    if (traceCoordinates.size() > 1 &&
                                        traceCoordinates.lastCoordinate().equals(traceCoordinates.get(0))) {

                                        this.doTrace();
                                    }
                                } //end check for undo                                
                            }
                        }
                        
                        if (coordinateAdded == false) {
                            //Add new Coordiante
                            Coordinate c = getCoordinateAtMouseLocation();
                            currentMapView.getViewPortCoordinates().add(c);
                            mapData.getCoordinateSet().put(c);
                            traceCoordinates.forceAdd(c);                                                              
                        }
                        
                        //mapData.highlightObject(selection, controlPressed);
                    }

                } else if (me.getButton() == MouseEvent.BUTTON2) {

                } else if (me.getButton() == MouseEvent.BUTTON3) {
                    if (dragMode == TRACE) { 
                        //Finish the trace route
                        doTrace();                    
                    }
                }        

                this.zoomControlClicked = false;                                        
            } //end panel locked check
        }
        
        this.repaint();
    }

    /**
     * Handles the dragging events for the MapPanel.
     *
     * @param me
     */    
    @Override
    public void mouseDragged(MouseEvent me) {
        float                    xDragDifference, yDragDifference;
        MapObject                objectToModify;
        MapObjectList<MapObject> selectedObjects;

        try {
            currentX = me.getX();
            currentY = me.getY();

            selectedObjects = (mapData.getSelectedObjects());

            if (!panelLocked) {                
                if (scaleX == 0 && scaleY == 0) {
                    xDragDifference = (me.getX() - xDragStart);
                    yDragDifference = (me.getY() - yDragStart);
                } else {
                    /* if using scaling we must adjust the drag by the 
                     * inverse of the scale
                     */
                    xDragDifference = (me.getX() - xDragStart) * (1.0f / scaleX);
                    yDragDifference = (me.getY() - yDragStart) * (1.0f / scaleY);                    
                }
                
                if (this.timeSpanControl != null && this.timeSpanControl.containsPoint(xDragStart, yDragStart)) {
                    this.timeSpanControl.mouseClicked(currentX, currentY);
                } else {
                    if (dragMode == DRAG_PAN) {
                        if (this.shiftPressed == true) {
                            //shift zoom!
                            showSelectRectangle = true;
                        } else if ((this.controlPressed == true) && 
                                ((xDragDifference > SINGLE_CLICK_WIDTH) || 
                                (yDragDifference > SINGLE_CLICK_WIDTH))) {

                            //change mode to Drag_Select
                            mapData.deselectObjects();
                            this.previousDragMode = DRAG_PAN;
                            this.dragMode         = DRAG_SELECT;
                            showSelectRectangle   = true;
                        } else {
                            //drag pan the map
                            xDragStart = me.getX();
                            yDragStart = me.getY();
                            scroll(xDragDifference, yDragDifference);
                        }
                    } else if (dragMode == DRAG_SELECT) {
                        showSelectRectangle = true;
                    } else if (dragMode == MODIFY) {
                        if (this.shiftPressed == true) {
                            //shift zoom!
                            showSelectRectangle = true;
                        } else if ((this.controlPressed == true) && (coordinateToModify == null)) {
    //                        //change mode to Drag_Select
    //                        mapData.deselectObjects();
    //                        this.previousDragMode = MODIFY;
    //                        this.dragMode         = DRAG_SELECT;
    //                        showSelectRectangle   = true;
                        } else {
                            if (selectedObjects.size() > 0) {
                                objectToModify     = mapData.getSelectedObjects().get(0);
                                coordinateToModify = objectToModify.getSelectedCoordinate();
                            } else {
                                coordinateToModify  = null;
                                objectToModify      = null; 
                            }

                            if (coordinateToModify == null) {
                                //pan map
                                dragPanMap(me, xDragDifference, yDragDifference);
                            } else {
                                if (moveSelectedPoint) {
                                    //move selected point
                                    coordinateToModify.setLongitude(currentMapView.getLongitude(currentX, currentY));
                                    coordinateToModify.setLatitude (currentMapView.getLatitude (currentX, currentY));                                                                        
                                    this.repaint();
                                } else {
                                    //pan map
                                    dragPanMap(me, xDragDifference, yDragDifference);
                                }
                            }
                        }
                    } else if (dragMode == TRACE) {
                        dragPanMap(me, xDragDifference, yDragDifference);
                    }

                    if (showSelectRectangle == true) {
                        //draw the selection rectange if the situation warents it.

                        if ( (currentY > yDragStart) && (currentX > xDragStart) ) {
                            rectangleSelection = new Rectangle2D.Float((xDragStart), (yDragStart), xDragDifference , yDragDifference);
                        } else if ( (currentY > yDragStart) && (currentX < xDragStart) ) {
                            rectangleSelection = new Rectangle2D.Float((currentX), (yDragStart), (xDragStart - currentX), yDragDifference);
                        } else if ( (currentY < yDragStart) && (currentX > xDragStart) ) {
                            rectangleSelection = new Rectangle2D.Float((xDragStart), (currentY) , xDragDifference , (yDragStart - currentY));
                        } else if ( (currentY < yDragStart) && (currentX < xDragStart) ) {
                            rectangleSelection = new Rectangle2D.Float(currentX, currentY, (xDragStart - currentX), (yDragStart - currentY));
                        }                        
                    }
                }
            }//end of locked check

            this.repaint();
            coordinateDrag = true;
        } catch (Exception e) {
            System.err.println("Error in MapPanel.mouseDragged(MouseEvent) - " + e);
        }
    }    
    
    @Override
    public void mouseEntered(MouseEvent me) {}    

    @Override
    public void mouseExited(MouseEvent me) {}        
    
    @Override
    public void mouseMoved(MouseEvent me) {
        currentX = me.getX();
        currentY = me.getY();
    }    
    
    @Override
    public void mousePressed(MouseEvent me) {
       float coordinateToModifyX, coordinateToModifyY;
              
       try {
           zoomControlClicked = false;
           hideObjectPopup();
           
           if (!panelLocked) {
               xDragStart = me.getX();
               yDragStart = me.getY();               
               dragging   = true;
               
               //check to see if user has clicked the zoom controls
               if (showZoomControls) {
                   if (ellipseZoomIn.contains(me.getX(),me.getY())) {
                       this.zoomIn(this.getWidth() / 2.0, this.getHeight() / 2.0);
                       zoomControlClicked = true;
                   } else if (ellipseZoomOut.contains(me.getX(),me.getY())) {
                       this.zoomOut(this.getWidth() / 2.0, this.getHeight() / 2.0);
                       zoomControlClicked = true;
                   }
               }
               
               if (zoomControlClicked == false) {
                   //move component points
                   if (dragMode == MODIFY) {
                       MapObject               objectToModify;
                       MapObjectList<MapObject> selectedObjects;

                       selectedObjects = (mapData.getSelectedObjects());

                       if (selectedObjects.size() > 0) {
                            objectToModify             = selectedObjects.get(0); //get the first selected object
                            coordinateToModifyOriginal = objectToModify.getSelectedCoordinate().copy();      //create a copy of the object so that the action can be undone
                            coordinateToModifyX        = currentMapView.getX(coordinateToModifyOriginal, MapView.NO_WRAP);
                            coordinateToModifyY        = currentMapView.getY(coordinateToModifyOriginal);

                            if ((Math.abs(me.getX() - coordinateToModifyX) < SINGLE_CLICK_WIDTH) && 
                                (Math.abs(me.getY() - coordinateToModifyY) < SINGLE_CLICK_WIDTH) ) {
                                //Coordinate is within single cick range, move point
                                moveSelectedPoint = true;
                            } else {
                                //pan map
                                moveSelectedPoint = false;
                            }
                        }
                   } else if (dragMode == TRACE) {

                   }                   
               } //end zoom clicked check
           } //end of locked check
        } catch (Exception e) {
            System.err.println("Error in MapPanel.mousePressed(MouseEvent) - " + e);
        }

        coordinateDrag = false;
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        boolean mergeSelectedPoint = false;
        
        if (!panelLocked) {
            this.dragging = false;
            
            if (this.shiftPressed == true) {
                //shift zoom
                //TODO: fix shift zooming
                //zoomTo(rectangleSelection);
            }

            if ((dragMode == DRAG_SELECT) && (me.getButton() == MouseEvent.BUTTON1)) {
                if (!controlPressed) {
                    mapData.deselectObjects();
                }

                mapData.highlightObject(rectangleSelection, controlPressed);

            } else if (dragMode == MODIFY) {
                if ((actions != null) && 
                    (coordinateToModifyOriginal != null) && 
                    (coordinateToModify         != null) && 
                    (coordinateDrag             == true)) {
                    
                    //This code is buggy
//                    if (!coordinateToModifyOriginal.equals(coordinateToModify)) {
//                        //Check to see if the point is overlapping an existing point
//                        for (Coordinate c: currentMapView.getViewPortCoordinates()) {
//                            if (Math.abs(c.getCenterPoint().x - coordinateToModify.getCenterPoint().x) < 3 &&
//                                Math.abs(c.getCenterPoint().y - coordinateToModify.getCenterPoint().y) < 3 &&
//                                !c.equals(coordinateToModify)) { 
//                                
//                                boolean sameObject = false;
//                                
//                                for (MapObject mapObject1: c.getParentVectorObjects()) {
//                                    for (MapObject mapObject2: coordinateToModify.getParentVectorObjects()) {
//                                        if (mapObject1 == mapObject2) {
//                                            sameObject = true;
//                                            break;
//                                        }                                            
//                                    }
//                                    
//                                    if (sameObject)
//                                        break;
//                                }
//                                
//                                if (!sameObject)
//                                    actions.mergeCoordinates(mapData, coordinateToModify, c);
//                                
//                                break;
//                            }                                      
//                        }                        
//                    }                        
//                    
//                    if (mergeSelectedPoint == false) {
//                        actions.moveCoordinate(coordinateToModifyOriginal, coordinateToModify);
//                        coordinateToModifyOriginal = null;
//                    }
//                    
//                    for (VectorObject vo: coordinateToModify.getParentVectorObjects()) 
//                        vo.generateBoundingBox();                    
                }

                coordinateToModify = null;
            } else if (dragMode == TRACE) {

            }

            //reset selection rectangle
            showSelectRectangle = false;
            coordinateDrag      = false;
            rectangleSelection  = new Rectangle2D.Double(0,0,0,0);

            this.repaint();
        }

    }

    /**
     * Process mouse wheel moved events.
     * 
     * @param mwe 
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
        hideObjectPopup();
        
        if (!panelLocked) {
            if (mwe.getWheelRotation() > 0) {
                zoomOut(currentX, currentY);
            } else if (mwe.getWheelRotation() < 0) {
                zoomIn(currentX, currentY);
            }
            
            if (dragMode == TRACE) {
                this.repaint();
            }         
        } //end panel lock check          
    }
    
    /**
     * Paints the DigitalMap on the Panel.
     * 
     * @param g 
     */
    @Override
    public void paint(Graphics g) {
        Graphics2D g2;
        MapTheme   mapTheme;

        super.paintComponent(g); // clears the panel        
        
        g2 = (Graphics2D) g;
        g2.setRenderingHints(renderAntialiasing);
          
        this.timeSpanControl = mapData.getTimeSpanControl();
        
        if ((scaleX != 0) && (scaleY != 0))
            g2.scale(scaleX, scaleY);

        //background color
        mapTheme = mapData.getTheme();
        this.setBackground(mapTheme.getBackgroundColor());

        currentMapView.getMapProjection().setDisplaySize(this.getHeight(), this.getWidth());
        currentMapView.setDragging(dragging);

        //draw map 
        mapData.drawMap(g2, currentMapView);
        
        //selection rectangle
        if (showSelectRectangle && rectangleSelection != null) {
            g2.setStroke(dashedStroke1); //Change draw mode to he first dashed stroke
            g2.setColor(Color.BLACK);
            g2.draw(rectangleSelection);

            g2.setStroke(dashedStroke2); //Change draw mode to he second dashed stroke
            g2.setColor(Color.WHITE);
            g2.draw(rectangleSelection);
        }

        if ((dragMode == TRACE)) {
            g2.setColor(new Color(68, 68, 68, 200));
            g2.setStroke(new BasicStroke(1,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            for (Coordinate c: currentMapView.getViewPortCoordinates()) {
                if (c.getParentVectorObjects().size() > 0)                     
                    g2.draw(VectorObject.getPointShape(c, MapView.NO_WRAP));                
            }
            
            if (traceCoordinates.size() > 0) {                
                g2.setStroke(new BasicStroke(1,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(new Color(68, 68, 68, 200));
                createTracePath();
                g2.draw(tracePath);

                for (Shape s: points) {
                    g2.setColor(Color.BLACK);
                    g2.draw(s);
                }
            }
        }        
        
        if (dragMode == DRAG_PAN)
            drawObjectPopup(g2);
            
        if (showScale)
            drawScale(g2);

        if (showZoomControls)
            drawZoomControls(g2);
        
        if (timeSpanControl != null)
            timeSpanControl.draw(g2, this.getHeight(), this.getWidth());
    }        
    
    /**
     * Scrolls the View Port.
     * 
     * @param horizontal    Horizontal scroll difference.
     * @param vertical      Vertical scroll difference.
     */
    public void scroll(double horizontal, double vertical) {
        xOrigin += (horizontal);
        yOrigin += (vertical);

        currentMapView.shiftMapReference(xOrigin, yOrigin);
        
        xOrigin = 0;
        yOrigin = 0;

        this.repaint();
    }    
    
    /**
     * Sets the Drag Mode for this map.  It the behavior of how dragging and 
     * other mouse operations are handled by the panel.
     * 
     * @param dragMode
     */
    public void setDragMode(int dragMode) {
        if (dragMode == TRACE && this.dragMode == TRACE) {
            doTrace();
        } else {
            clearTracePath();
        }
        
        this.dragMode = dragMode;        
                
        if (dragMode == MODIFY || dragMode == TRACE) {
            currentMapView.setShowPoints(true);
        } else {
            currentMapView.setShowPoints(false);
        }        
    }
    
    /**
     * Sets the map to be displayed in this MapPanel and updates the Map Reference.
     *
     * @param mapData
     */
    @Override
    public void setMap(DigitalMap mapData) {
        currentMapView = mapData.getLastMapView();
        currentMapView.setNodeMap(mapData.getCoordinateSet());
        
        updateMap(mapData);
    }
    
    /**
     * Sets the display scale for view in the map.
     * 
     * @param scaleX
     * @param scaleY 
     */
    public void setScale(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }    
        
    /**
     * Sets the TimeSpanControl to be used in the panel.
     * 
     * @param timeSpanControl 
     */
    public void setTimeSpanControl(TimeSpanControl timeSpanControl) {
        this.timeSpanControl = timeSpanControl;
    }
    
    /**
     * Displays the Object Popup.  This shows info about the selected object.
     * 
     * @param x
     * @param y 
     */
    public void showObjectPopup(int x, int y) {
        //TODO: finish codding ObjectPopup.
    }
    
    /**
     * Sets if the panel will display the map scale.
     * 
     * @param showScale 
     */
    public void showScale(boolean showScale) {
        this.showScale = showScale;
    }   
    
    public void showZoomControls(boolean showZoomControls) {
        this.showZoomControls = showZoomControls;
    }    
    
    /**
     * Does maintenence for the MapPanel
     */
    @Override
    public void update() {
        this.controlPressed = false;
        this.shiftPressed   = false;
        
        this.repaint();
    }    
    
    /**
     * Updates the Map used by the MapPanel but not the map reference
     * 
     * @param mapData The DigitalMap to display in the panel.
     */
    public void updateMap(DigitalMap mapData) {
//        this.zoomLevel = mapData.getLookAtCoordinate().getAltitude();
        this.mapData   = mapData;
        this.actions   = mapData.getActions();

        if (this.actions == null) {
            //The map does not have a commands object, create one.
            this.actions = new Actions(mapData, this);
            mapData.setActions(actions);
        }

        this.fireUpdateables();
        this.repaint();        
    }    
    
    /**
     * Zooms the view port in.
     * 
     * @param x
     * @param y 
     */
    public void zoomIn(double x, double y) {
        if (getPanelWidthInKm() > 0.01104) {            
            currentMapView.getMapProjection().zoomIn(x, y);
            currentMapView.update();
            
            this.repaint();
        }
    }

    /**
     * Zooms the view port out.
     * 
     * @param x
     * @param y 
     */
    public void zoomOut(double x, double y) {
        currentMapView.getMapProjection().zoomOut(x, y);
        currentMapView.update();
         
        this.repaint();
    }

}
