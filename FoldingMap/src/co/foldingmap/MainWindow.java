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

import co.foldingmap.map.MapObject;
import co.foldingmap.map.MapPanel;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.MapAccepter;
import co.foldingmap.map.Layer;
import co.foldingmap.map.vector.NetworkLayer;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.PhotoPoint;
import co.foldingmap.map.vector.VectorObjectList;
import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.actions.ExportLayer;
import co.foldingmap.actions.Actions;
import co.foldingmap.actions.AddGroundOverlay;
import co.foldingmap.actions.AddNetworkLayer;
import co.foldingmap.actions.MergeToPolygon;
import co.foldingmap.actions.MergeToMultiGeometry;
import co.foldingmap.actions.ChangeObjectZOrder;
import co.foldingmap.actions.MergeToLineString;
import co.foldingmap.actions.MergeToLinearRing;
import co.foldingmap.actions.AddVectorLayer;
import co.foldingmap.GUISupport.HeatMapProperties;
import co.foldingmap.GUISupport.DataSheetPanel;
import co.foldingmap.GUISupport.TileLayerProperties;
import co.foldingmap.GUISupport.Updateable;
import co.foldingmap.GUISupport.ProgressBarPanel;
import co.foldingmap.GUISupport.LayerMenuItem;
import co.foldingmap.GUISupport.ObjectDetailsToolBar;
import co.foldingmap.GUISupport.panels.MapPropertiesPanel;
import co.foldingmap.GUISupport.panels.VectorLayerPropertiesPanel;
import co.foldingmap.GUISupport.panels.PhotoExtendedOptionsPanel;
import co.foldingmap.GUISupport.panels.DefaultExtendedOptionsPanel;
import co.foldingmap.GUISupport.panels.ExtendedOptionsPanel;
import co.foldingmap.GUISupport.panels.NetworkLayerPropertiesPanel;
import co.foldingmap.GUISupport.panels.BubbleChartProperties;
import co.foldingmap.GUISupport.components.PopupMenuButton;
import co.foldingmap.GUISupport.components.checkBoxTree.CheckedTreeNode;
import co.foldingmap.GUISupport.components.checkBoxTree.LayersTree;
import co.foldingmap.map.tile.TileLayer;
import co.foldingmap.map.tile.TileMath;
import co.foldingmap.map.visualization.HeatMap;
import co.foldingmap.mapImportExport.FmXmlImporter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * This class is the main program window the user interacts with.
 * 
 * @author Alec
 */
public class MainWindow extends    JFrame
                        implements ActionListener, 
                                   MapAccepter, 
                                   MenuListener, 
                                   MouseListener, 
                                   MouseMotionListener, 
                                   MouseWheelListener,
                                   Updateable, 
                                   WindowListener {
    
    private Actions                     actions;
    private ButtonGroup                 buttonGroupMapTools; 
    private LayersTree                  treeLayers;
    private CheckedTreeNode             rootLayerNode;
    private DataSheetPanel              dataSheetPanel;
    private DecimalFormat               decimalFormat, zoomFormat;
    private DefaultTreeModel            layersDefaultTreeModel;
    private ExtendedOptionsPanel        extendedOptionsPanel;
    private JButton                     buttonCloseLayerToolbar, buttonCloseOptionToolbar, buttonRemoveLayer;
    private JButton                     buttonConnectObject, buttonUnmerge;    
    private JCheckBoxMenuItem           menuItemHelpLanguageEnglish, menuItemHelpLanguageFrench;
    private JCheckBoxMenuItem           menuItemViewMapTools, menuItemViewLayers, menuItemViewMapOverview;
    private JCheckBoxMenuItem           menuItemViewObjectData, menuItemViewObjectDetails, menuItemViewStatusBar;
    private JCheckBoxMenuItem           menuItemLayersLockLayer;      
    private JLabel                      labelLayersCaption, labelMouseCoordinates, labelExtendedOptions;    
    private JMenu                       menuEdit, menuFile, menuHelp, menuMap, menuTools, menuView;
    private JMenu                       menuFileImport, menuFileExport, menuHelpLanguage;
    private JMenu                       menuLayersMergeLayer;
    private JMenu                       menuMapAddObject, menuMapOrder;
    private JMenu                       menuMapModifyOrder,  menuMapModifyMoveToLayer;    
    private JMenuBar                    mainMenuBar;
    private JMenuItem                   menuItemAddNetworkLayer, menuItemAddVectorLayer;
    private JMenuItem                   menuItemFileOpen, menuItemFileNew, menuItemFileQuit, menuItemFileSave, menuItemFileSaveAs, menuItemFilePrint;
    private JMenuItem                   menuItemFileImportData, menuItemFileImportMap, menuItemFileImportGeotaggedPhotos;
    private JMenuItem                   menuItemFileExportImage, menuItemFileExportTiles, menuItemFileExportMap;    
    private JMenuItem                   menuItemEditRedo, menuItemEditUndo, menuItemEditCopy, menuItemEditCut, menuItemEditPaste, menuItemEditDelete;
    private JMenuItem                   menuItemEditFind, menuItemEditDeselect;
    private JMenuItem                   menuItemMapAddImageOverlay, menuItemMapAddLinearObject, menuItemMapAddPoint, menuItemMapAddPolygon;
    private JMenuItem                   menuItemMapCrop, menuItemMapProperties;
    private JMenuItem                   menuItemMapOrderMoveToFront, menuItemMapOrderMoveToBack, menuItemMapOrderMoveForward, menuItemMapOrderMoveBackward;    
    private JMenuItem                   menuItemToolsCreateBubbleChart, menuItemToolsCreateHeatMap, menuItemToolsDataCatalog, menuItemToolsGPS;
    private JMenuItem                   menuItemHelpAbout;    
    private JMenuItem                   menuItemMapModifyAddMapPoint, menuItemMapModifyAddObjectPoint, menuItemMapModifyDeleteObjectPoint, menuItemMapModifySplitObject;
    private JMenuItem                   menuItemMapModifyCopy, menuItemMapModifyPaste, menuItemMapModifyDelete, menuItemMapModifyProperties;
    private JMenuItem                   menuItemMapModifyOrderToFront, menuItemMapModifyOrderToBack, menuItemMapModifyOrderForward, menuItemMapModifyOrderBackward;
    private JMenuItem                   menuItemMapNormalCut, menuItemMapNormalCopy, menuItemMapNormalPaste, menuItemMapNormalDelete, menuItemMapNormalNewPoint, menuItemMapNormalProperties;
    private JMenuItem                   menuItemMergeLineString, menuItemMergeLinearRing, menuItemMergePolygon, menuItemMergeLogicalGroup;
    private JMenuItem                   menuItemLayersDeleteLayers, menuItemLayersLayerProperties, menuItemMoveToLayerNewLayer, menuItemLayersMoveUp, menuItemLayersMoveDown, menuItemLayersSaveLayer;
    private JMenuItem                   menuItemLayerItemCopy, menuItemLayerItemCut, menuItemLayerDelete, menuItemLayerProperties;
    private JPanel                      panelMeasure, panelMouseCoordinates, panelRightDoc, panelStatusBar;
    private JPanel                      panelLayerButtons, panelLayerCenter, panelLayerNorth;
    private JPanel                      panelMainCenter, panelExtendedOptionsNorth;        
    private JPopupMenu                  popupMenuLayers, popupMenuLayerItem, popupMenuMapNormal, popupMenuMapModifyPoints;   
    private JScrollPane                 spaneLayers;
    private JToggleButton               tbuttonArrowTool, tbuttonModifyPoints, tbuttonSelectTool, tbuttonTrace;        
    private JToolBar                    mapToolsBar, toolbarExtendedOptions, toolbarLayers;
    private MapPanel                    mapPanel;
    private MapPropertiesPanel          mapPropertiesPanel;
    private ObjectDetailsToolBar        objectDetailsToolBar;
    private Popup                       visibilityPopup;
    private PopupMenuButton             buttonAddLayer, buttonMergeObjects;    
    private ProgressBarPanel            panelProgressBar;    
    private ResourceHelper              helper;           
    private Toolkit                     awtToolKit;
    private UserConfig                  config;
    
    public MainWindow(UserConfig config) {
        super("Folding Map");
        
        try {
            init();
            setupToolbar();
            addListeners();
                        
            this.setIconImage(helper.getBufferedImage("folding_map-32.png"));
            this.config = config;
            
            if (System.getProperty("os.name").equalsIgnoreCase("Mac OS X")) {
//                com.apple.eawt.Application macApp = com.apple.eawt.Application.getApplication();
//                macApp.setDockIconImage(helper.getBufferedImage("folding_map-32.png"));                 
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }                

            this.setDefaultCloseOperation(EXIT_ON_CLOSE);
            this.setExtendedState(this.getExtendedState()|JFrame.MAXIMIZED_BOTH);        
            this.setVisible(true);
            tbuttonArrowTool.requestFocus();
            this.repaint();                        
            
            openDefaulMap();
            WelcomeDialog welcomeDialog = new WelcomeDialog(this, config);
            updateExtendedOptions();
        } catch (Exception e) {
            System.err.println("Error in MainWindow Constructor(UserConfig) - " + e);
        }
    }    
    
    @Override
    public void actionPerformed(ActionEvent ae) {      
        try {
            if (ae.getSource() == null) {        

            } else if (ae.getSource() == buttonAddLayer) {

            } else if (ae.getSource() == buttonCloseLayerToolbar) {    

            } else if (ae.getSource() == buttonCloseOptionToolbar) {    

            } else if (ae.getSource() == buttonConnectObject) {     
                actions.connectLinearObject(mapPanel.getMap());
            } else if (ae.getSource() == buttonRemoveLayer) { 

            } else if (ae.getSource() == buttonUnmerge) {
                actions.Unmerge(mapPanel.getMap());
            } else if (ae.getSource() == menuItemEditCopy) {    
                ClipboardOperations.copyMapObjects(mapPanel.getMap(), new VectorObjectList<VectorObject>(mapPanel.getMap().getSelectedObjects()));
            } else if (ae.getSource() == menuItemEditCut) {   
                actions.cutSelectedObjects(mapPanel.getMap());
            } else if (ae.getSource() == menuItemEditDelete) {  
                actions.deleteSelectedObjects(mapPanel.getMap());
                updateObjectDetailsToolBar();
                updateExtendedOptions();
            } else if (ae.getSource() == menuItemEditDeselect) {   
                mapPanel.getMap().deselectObjects();
                objectDetailsToolBar.update();
                updateExtendedOptions();
            } else if (ae.getSource() == menuItemEditFind) {  

            } else if (ae.getSource() == menuItemEditPaste) {  
                actions.paste(this, mapPanel.getMap());
            } else if (ae.getSource() == menuItemEditRedo) {  
                actions.redo();
            } else if (ae.getSource() == menuItemEditUndo) {  
                actions.undo();
            } else if (ae.getSource() == menuItemFileExportImage) {      
                ExportImageDialog eid = new ExportImageDialog(this,mapPanel.getMap());
            } else if (ae.getSource() == menuItemFileExportMap) {             
                actions.saveMapAs(this, mapPanel.getMap(), this.panelProgressBar);
            } else if (ae.getSource() == menuItemFileExportTiles) { 
                TileExportDialog tileDialog = new TileExportDialog(this, mapPanel.getMap());   
            } else if (ae.getSource() == menuItemFileImportGeotaggedPhotos) {   
                actions.importGeotaggedPhotos(this, mapPanel.getMap());
            } else if (ae.getSource() == menuItemFileImportData) {             
                DataImportWizardDialog dWiz = new DataImportWizardDialog(this, mapPanel.getMap());
            } else if (ae.getSource() == menuItemFileImportMap) {            
                actions.importMap(this, mapPanel.getMap());
                this.update();
            } else if (ae.getSource() == menuItemFileNew) {
                openDefaulMap();
            } else if (ae.getSource() == menuItemFileOpen) {  
                actions.openMap(this, mapPanel);       
            } else if (ae.getSource() == menuItemFileQuit) {  
                //TODO: Add code to see if the map has changes and ask ot save
                this.dispose();
            } else if (ae.getSource() == menuItemFilePrint) {    

            } else if (ae.getSource() == menuItemFileSave) {  
                actions.saveMap(this, mapPanel.getMap(), this.panelProgressBar);
            } else if (ae.getSource() == menuItemFileSaveAs) { 
                actions.saveMapAs(this, mapPanel.getMap(), this.panelProgressBar);
            } else if (ae.getSource() == menuItemHelpLanguageEnglish) {    

            } else if (ae.getSource() == menuItemHelpLanguageFrench) {    

            } else if (ae.getSource() == menuItemLayerProperties) {        
                //Get the selected object and show correct properties for it
                Object o = treeLayers.getSelectedNodeObject();
                
                if (o instanceof VectorObject)
                    new MapObjectPropertiesWindow(this, (VectorObject) o);            
            } else if (ae.getSource() == menuItemLayersDeleteLayers) {     
                actions.deleteSelectedLayer(this, mapPanel.getMap());
                this.updateLayersTree();
            } else if (ae.getSource() == menuItemLayersLayerProperties) { 
                Layer selectedLayer = mapPanel.getMap().getSelectedLayer();

                if (selectedLayer instanceof HeatMap) {
                    LayerProperties lp = new LayerProperties(this, new HeatMapProperties(mapPanel.getMap(), (HeatMap) selectedLayer));
                } else if (selectedLayer instanceof NetworkLayer) {   
                    LayerProperties layerPropDialog = new LayerProperties(this);
                    NetworkLayerPropertiesPanel netLayerProps = new NetworkLayerPropertiesPanel(this, layerPropDialog, (NetworkLayer) selectedLayer);
                    
                    layerPropDialog.setLayerPanel(netLayerProps);
                    layerPropDialog.setVisible(true);                    
                } else if (selectedLayer instanceof TileLayer) {
                    LayerProperties lp = new LayerProperties(this, new TileLayerProperties(mapPanel.getMap(), (TileLayer) selectedLayer));
                } else if (selectedLayer instanceof VectorLayer) {
                    LayerProperties lp = new LayerProperties(this, new VectorLayerPropertiesPanel((VectorLayer) selectedLayer));
                }
            } else if (ae.getSource() == menuItemLayersLockLayer) {      
                Layer layer = mapPanel.getMap().getSelectedLayer();
                layer.setLocked(!layer.isLocked());
            } else if (ae.getSource() == menuItemMapModifyProperties) {      
                VectorObjectList<VectorObject> objects = new VectorObjectList<VectorObject>(mapPanel.getMap().getSelectedObjects());

                if (objects.size() == 0) {
                    MapPropertiesWindow pw = new MapPropertiesWindow(this, mapPanel.getMap());
                } else if (objects.size() == 1) {
                    MapObjectPropertiesWindow mopw = new MapObjectPropertiesWindow(this, objects.get(0));            
                } 
            } else if (ae.getSource() == menuItemMoveToLayerNewLayer) {      

            } else if (ae.getSource() instanceof LayerMenuItem) {           
                LayerMenuItem layerMenuItem = (LayerMenuItem) ae.getSource();

                if (layerMenuItem.getActionString().equals("Merge")) {
                    //Merging one layer to another.
                    actions.mergeLayer(mapPanel.getMap(), mapPanel.getMap().getSelectedLayer(), layerMenuItem.getLayer());
                } else if (layerMenuItem.getActionString().equals("Change Layer")) {
                    //TODO: Finish this part
                }      

                this.update();
            } else if (ae.getSource() == menuItemLayersMoveUp) {  
                /* Move the selected layer in the map up a level, and call and 
                * call an upadateable (this) when finished. */             
                actions.moveLayerUp(mapPanel.getMap(), this);                
            } else if (ae.getSource() == menuItemLayersMoveDown) {  
                /* Move the selected layer in the map down a level, and call and 
                * call an upadateable (this) when finished. */             
                actions.moveLayerDown(mapPanel.getMap(), this);
            } else if (ae.getSource() == menuItemLayersSaveLayer) {            
                Layer      layer   = mapPanel.getMap().getSelectedLayer();
                ExportLayer.export(this, mapPanel.getMapView().getMapProjection(), layer);
            } else if (ae.getSource() == menuItemMapAddImageOverlay) {     
                AddGroundOverlay addGroundOverlay;
                addGroundOverlay = new AddGroundOverlay(this, mapPanel.getMap(), mapPanel.getMapView());           
                actions.performAction(addGroundOverlay);
            } else if (ae.getSource() == menuItemMapAddLinearObject) { 

            } else if (ae.getSource() == menuItemMapAddPoint) {             

            } else if (ae.getSource() == menuItemMapAddPolygon) {            

            } else if (ae.getSource() == menuItemMapCrop) {
                actions.cropMap(mapPanel.getMap(), (mapPanel.getMap().getLastMapView().getViewBounds()));  
            } else if (ae.getSource() == menuItemMapModifyAddMapPoint) {      
                actions.addMapPoint(mapPanel.getMap());
            } else if (ae.getSource() == menuItemMapModifyAddObjectPoint) {     
                actions.addPointToObject(mapPanel.getMap(), mapPanel.getCoordinateAtMouseLocation());
            } else if (ae.getSource() == menuItemMapModifyCopy) {  
                ClipboardOperations.copyMapObjects(mapPanel.getMap(), new VectorObjectList<VectorObject>(mapPanel.getMap().getSelectedObjects()));            
            } else if (ae.getSource() == menuItemMapModifyDelete) {     
                actions.deleteSelectedObjects(mapPanel.getMap());
                updateObjectDetailsToolBar();
                updateExtendedOptions();            
            } else if (ae.getSource() == menuItemMapModifyDeleteObjectPoint) {     
                actions.deletePointFromObject(mapPanel.getMap());          
            } else if (ae.getSource() == menuItemMapModifyOrderBackward) {  
                actions.changeObjectZOrder(mapPanel.getMap(), ChangeObjectZOrder.BACKWARD);
            } else if (ae.getSource() == menuItemMapModifyOrderForward) {       
                actions.changeObjectZOrder(mapPanel.getMap(), ChangeObjectZOrder.FORWARD);
            } else if (ae.getSource() == menuItemMapModifyOrderToBack)  { 
                actions.changeObjectZOrder(mapPanel.getMap(), ChangeObjectZOrder.TO_BACK);
            } else if (ae.getSource() == menuItemMapModifyOrderToFront) {     
                actions.changeObjectZOrder(mapPanel.getMap(), ChangeObjectZOrder.TO_FRONT);            
            } else if (ae.getSource() == menuItemMapModifyPaste) {    
                actions.paste(this, mapPanel.getMap());       
            } else if (ae.getSource() == menuItemMapModifySplitObject) {
                VectorObject object = (VectorObject) mapPanel.getMap().getSelectedObjects().get(0);
                actions.splitObject(mapPanel.getMap(), object, object.getSelectedCoordinate());
            } else if (ae.getSource() == menuItemMapNormalCopy) {  
                ClipboardOperations.copyMapObjects(mapPanel.getMap(), new VectorObjectList<VectorObject>(mapPanel.getMap().getSelectedObjects()));
            } else if (ae.getSource() == menuItemMapNormalDelete) {    
                actions.deleteSelectedObjects(mapPanel.getMap());   
                updateObjectDetailsToolBar();
                updateExtendedOptions();            
            } else if (ae.getSource() == menuItemMapNormalPaste) {    
                actions.paste(this, mapPanel.getMap());
            } else if (ae.getSource() == menuItemMapOrderMoveBackward) {  
                actions.changeObjectZOrder(mapPanel.getMap(), ChangeObjectZOrder.BACKWARD);
            } else if (ae.getSource() == menuItemMapOrderMoveForward) {  
                actions.changeObjectZOrder(mapPanel.getMap(), ChangeObjectZOrder.FORWARD);
            } else if (ae.getSource() == menuItemMapProperties) { 
                new MapPropertiesWindow(this, mapPanel.getMap());
            } else if (ae.getSource() == menuItemMapOrderMoveToBack) {  
                actions.changeObjectZOrder(mapPanel.getMap(), ChangeObjectZOrder.TO_BACK);
            } else if (ae.getSource() == menuItemMapOrderMoveToFront) {                         
                actions.changeObjectZOrder(mapPanel.getMap(), ChangeObjectZOrder.TO_FRONT);
            } else if (ae.getSource() == menuItemMapNormalNewPoint) {        
                actions.addMapPoint(mapPanel.getMap());
            } else if (ae.getSource() == menuItemMapNormalProperties) {      
                VectorObjectList<VectorObject> objects = new VectorObjectList<VectorObject>(mapPanel.getMap().getSelectedObjects());

                if (objects.size() == 0) {
                    new MapPropertiesWindow(this, mapPanel.getMap());
                } else if (objects.size() == 1) {
                    new MapObjectPropertiesWindow(this, objects.get(0));            
                }               
            } else if (ae.getSource() == menuItemToolsCreateBubbleChart) {
                VectorLayer vectorLayer = new VectorLayer("Bubble Chart");
                new LayerProperties(this, new BubbleChartProperties(actions, mapPanel.getMap(), vectorLayer));
            } else if (ae.getSource() == menuItemToolsCreateHeatMap) {    
                new LayerProperties(this, new HeatMapProperties(mapPanel.getMap(), actions));
            } else if (ae.getSource() == menuItemToolsDataCatalog) {  
                MapCatalogDialog mapCatalog = new MapCatalogDialog(this, mapPanel.getMap());
            } else if (ae.getSource() == menuItemToolsGPS) {    

            } else if (ae.getSource() == menuItemViewLayers) {  

            } else if (ae.getSource() == menuItemViewMapOverview) {                         

            } else if (ae.getSource() == menuItemViewMapTools) {    

            } else if (ae.getSource() == menuItemViewObjectData) {     
                if (menuItemViewObjectData.isSelected()) {                
                    dataSheetPanel = new DataSheetPanel(mapPanel.getMap());
                    dataSheetPanel.setPreferredSize(new Dimension(this.getWidth(), (this.getHeight() / 5)));
                    dataSheetPanel.addUpdateable(objectDetailsToolBar); 
                    dataSheetPanel.addUpdateable(mapPanel); 
                    panelMainCenter.add(dataSheetPanel, BorderLayout.SOUTH);
                } else {
                    panelMainCenter.remove(dataSheetPanel);
                }

                this.repaint();
            } else if (ae.getSource() == menuItemViewObjectDetails) {  

            } else if (ae.getSource() == menuItemViewStatusBar) {              

            } else if (ae.getSource() == tbuttonArrowTool) {  
                mapPanel.setDragMode(MapPanel.DRAG_PAN);            
            } else if (ae.getSource() == tbuttonModifyPoints) {  
                mapPanel.setDragMode(MapPanel.MODIFY);
            } else if (ae.getSource() == tbuttonSelectTool) {  
                mapPanel.setDragMode(MapPanel.DRAG_SELECT);
            } else if (ae.getSource() == tbuttonTrace) {      
                mapPanel.setDragMode(MapPanel.TRACE);
            } else if (ae.getActionCommand().equals("Move To Layer")) {
                JMenuItem menuItem = (JMenuItem) ae.getSource();
                actions.moveObjectsToLayer(mapPanel.getMap(), menuItem.getText(), new VectorObjectList<VectorObject>(mapPanel.getMap().getSelectedObjects()));
            }
            
            mapPanel.repaint();
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in MainWindow.actionPerformed(ActionEvent) - " + e);
        }
    }            
    
    /**
     * Adds a new layer to the layer sidebar
     *
     * @param newLayer       The new layer to be added.
     * @param newLayerNumber A reference to the layer's position in the digital map's layer vector.
     */
    public void addLayer(Layer newLayer, int newLayerNumber) {
        CheckedTreeNode newNode;

        newNode = new CheckedTreeNode(newLayer);
        newNode.setSelected(newLayer.isVisible());
        newNode.setLayerNumber(newLayerNumber);
        rootLayerNode.add(newNode);
        layersDefaultTreeModel.reload(rootLayerNode);
        this.repaint();
    }    
    
    /**
     * Adds all layers contained in the supplied vector to the layer sidebar
     *
     * @param layers    A Vector containing one or more Layer objects.
     */
    public void addLayers(ArrayList<Layer> layers) {
        CheckedTreeNode newNode;
        Layer           currentLayer;

        if (layers.size() > 0) {
            for (int i = 0; i < layers.size(); i++) {
                currentLayer = layers.get(i);
                newNode = new CheckedTreeNode(currentLayer);
                newNode.setSelected(currentLayer.isVisible());
                newNode.setLayerNumber(i);
                rootLayerNode.add(newNode);
            }

            treeLayers.expandRow(0);
            treeLayers.setSelectionRow(1);
            treeLayers.setRootVisible(false);
            
            layersDefaultTreeModel.reload(rootLayerNode);
            this.repaint();            
        } else {
            ArrayList<Layer> defaultLayers = new ArrayList<Layer>();
            defaultLayers.add(new VectorLayer("Default"));
            addLayers(defaultLayers);
        }
    }    
    
    /**
     * Add action listeners to all components that have actions
     *
     */
    private void addListeners() {
        try {
            this.addWindowListener(this);
            
            mapPanel.addMouseWheelListener(this);            
            mapPanel.addMouseListener(this);   
            mapPanel.addMouseMotionListener(this);            
            
            //
            objectDetailsToolBar.addKeyListener(mapPanel);
            //objectDetailsToolBar.addUpdateable(dataSheetPanel);
            
            //File Menu
            menuItemFileNew.addActionListener(this);            
            menuItemFileOpen.addActionListener(this);
            menuItemFilePrint.addActionListener(this);
            menuItemFileQuit.addActionListener(this);
            menuItemFileSave.addActionListener(this);
            menuItemFileSaveAs.addActionListener(this);
            menuItemFileExportImage.addActionListener(this);
            menuItemFileExportTiles.addActionListener(this);
            menuItemFileImportData.addActionListener(this); 
            menuItemFileImportGeotaggedPhotos.addActionListener(this); 
            menuItemFileImportMap.addActionListener(this);
            menuItemFileExportMap.addActionListener(this);
            
            //Edit Menu
            menuEdit.addMenuListener(this);
            menuItemEditUndo.addActionListener(this);
            menuItemEditRedo.addActionListener(this);
            menuItemEditCopy.addActionListener(this);
            menuItemEditCut.addActionListener(this);
            menuItemEditPaste.addActionListener(this);
            menuItemEditDelete.addActionListener(this);
            menuItemEditFind.addActionListener(this);
            menuItemEditDeselect.addActionListener(this);

            //Map Menu
            menuItemMapAddImageOverlay.addActionListener(this);
            menuItemMapAddPoint.addActionListener(this);
            menuItemMapAddLinearObject.addActionListener(this);
            menuItemMapAddPolygon.addActionListener(this);
            menuItemMapProperties.addActionListener(this);
            menuItemMapCrop.addActionListener(this);
            menuItemMapOrderMoveToFront.addActionListener(this);
            menuItemMapOrderMoveToBack.addActionListener(this);
            menuItemMapOrderMoveForward.addActionListener(this);
            menuItemMapOrderMoveBackward.addActionListener(this);

            //Tools Menu
            menuItemToolsDataCatalog.addActionListener(this);
            menuItemToolsCreateBubbleChart.addActionListener(this);
            menuItemToolsCreateHeatMap.addActionListener(this);
            menuItemToolsGPS.addActionListener(this);

            //View Menu
            menuItemViewMapTools.addActionListener(this);
            menuItemViewLayers.addActionListener(this);
            menuItemViewMapOverview.addActionListener(this);
            menuItemViewObjectData.addActionListener(this);
            menuItemViewObjectDetails.addActionListener(this);
            menuItemViewStatusBar.addActionListener(this);

            //help menu
            menuItemHelpLanguageEnglish.addActionListener(this);
            menuItemHelpLanguageFrench.addActionListener(this);
            menuItemHelpAbout.addActionListener(this);        
            
            //Toolbar
            tbuttonArrowTool.addActionListener(this);
            tbuttonModifyPoints.addActionListener(this);
            tbuttonSelectTool.addActionListener(this);
            tbuttonTrace.addActionListener(this);
            buttonConnectObject.addActionListener(this);
            buttonUnmerge.addActionListener(this);
            tbuttonArrowTool.addKeyListener(mapPanel);
            tbuttonModifyPoints.addKeyListener(mapPanel);
            tbuttonSelectTool.addKeyListener(mapPanel);            
            buttonMergeObjects.addKeyListener(mapPanel);
            buttonConnectObject.addKeyListener(mapPanel);
            buttonMergeObjects.addUpdate(mapPanel);
            buttonMergeObjects.addUpdate(objectDetailsToolBar);                        
            
            //Modify Points Popup Menu
            menuItemMapModifyAddMapPoint.addActionListener(this);
            menuItemMapModifyOrderToFront.addActionListener(this);              
            menuItemMapModifyOrderForward.addActionListener(this);
            menuItemMapModifyOrderBackward.addActionListener(this);
            menuItemMapModifyOrderToBack.addActionListener(this);            
            menuItemMapModifyAddObjectPoint.addActionListener(this);
            menuItemMapModifyDeleteObjectPoint.addActionListener(this);
            menuItemMapModifySplitObject.addActionListener(this);
            menuItemMapModifyCopy.addActionListener(this);
            menuItemMapModifyPaste.addActionListener(this);
            menuItemMapModifyDelete.addActionListener(this);
            menuItemMapModifyProperties.addActionListener(this);
            
            //Normal Map Popup Menu
            menuItemMapNormalCut.addActionListener(this);
            menuItemMapNormalCopy.addActionListener(this);
            menuItemMapNormalPaste.addActionListener(this);
            menuItemMapNormalDelete.addActionListener(this);
            menuItemMapNormalNewPoint.addActionListener(this);
            menuItemMapNormalProperties.addActionListener(this);   
            
            //Layers Tree
            buttonAddLayer.addActionListener(this);
            buttonRemoveLayer.addActionListener(this);
            
            //Layers Popup Menu
            treeLayers.addMouseListener(this);
            menuItemLayersDeleteLayers.addActionListener(this);   
            menuItemLayersLayerProperties.addActionListener(this);   
            menuItemMoveToLayerNewLayer.addActionListener(this);   
            menuItemLayersMoveUp.addActionListener(this);   
            menuItemLayersMoveDown.addActionListener(this);   
            menuItemLayersSaveLayer.addActionListener(this);               
            menuItemLayersLockLayer.addActionListener(this); 
            
            //Layers Vector Object
            menuItemLayerItemCopy.addActionListener(this); 
            menuItemLayerItemCut.addActionListener(this); 
            menuItemLayerDelete.addActionListener(this); 
            menuItemLayerProperties.addActionListener(this); 
            
            //Misc
            buttonCloseLayerToolbar.addActionListener(this);   
            buttonCloseOptionToolbar.addActionListener(this); 
            mapPanel.addUpdateable(objectDetailsToolBar); 
            mapPanel.addUpdateable(dataSheetPanel);
        } catch (Exception e) {
            System.err.println("Error in MainWindow.addActionListener() - " + e);
        }
    }         
    
    /**
     * Returns the MapPanel Used by this Window.
     * 
     * @return 
     */
    public MapPanel getMapPanel() {
        return this.mapPanel;
    }
    
    /**
     * Returns the ProgressBarPanel, this is to be used for any task that takes
     * more than a second to execute.
     * 
     * @return 
     */
    public ProgressBarPanel getProgressBarPanel() {
        return this.panelProgressBar;
    }
    
    /**
     * Returns the user config object.
     * 
     */
    public UserConfig getUserConfig() {
        return config;
    }
    
    /**
     * Hides all popups for this window.
     */
    public void hideAllPopups() {
        popupMenuMapNormal.setVisible(false);
        popupMenuMapModifyPoints.setVisible(false);
        popupMenuLayers.setVisible(false);
        popupMenuLayerItem.setVisible(false);
        buttonMergeObjects.hidePopup();
        hideVisibilityPopup();
    }
    
    /**
     * Hide the object visibility options popup panel.
     */
    public void hideVisibilityPopup() {
        if (visibilityPopup != null) {
            visibilityPopup.hide();
            visibilityPopup = null;
        }
    }    
    
    private void init() {
        try {
            helper = ResourceHelper.getInstance();                                 
            
            //init objects
            rootLayerNode                       = new CheckedTreeNode("Layers");
            awtToolKit                          = Toolkit.getDefaultToolkit();
            buttonAddLayer                      = new PopupMenuButton(helper.getImage("add.png"), this.actions, PopupMenuButton.LEFT_CLICK); 
            buttonCloseLayerToolbar             = new JButton(helper.getImage("x.png"));             
            buttonCloseOptionToolbar            = new JButton(helper.getImage("x.png")); 
            buttonConnectObject                 = new JButton(helper.getImage("connect.png"));
            buttonGroupMapTools                 = new ButtonGroup();
            buttonMergeObjects                  = new PopupMenuButton(helper.getImage("shape_group.png"), this.actions, PopupMenuButton.LEFT_CLICK); 
            buttonRemoveLayer                   = new JButton(helper.getImage("delete.png"));
            buttonUnmerge                       = new JButton(helper.getImage("shape_ungroup.png"));
            decimalFormat                       = new DecimalFormat("#0.00000");   
            labelExtendedOptions                = new JLabel("Options", SwingConstants.CENTER);
            labelLayersCaption                  = new JLabel("Layers", SwingConstants.CENTER);
            labelMouseCoordinates               = new JLabel("Latitude: 0.0000 - Longitude: 0.0000");
            layersDefaultTreeModel              = new DefaultTreeModel(rootLayerNode);
            mainMenuBar                         = new JMenuBar();
            menuEdit                            = new JMenu("Edit");
            menuFile                            = new JMenu("File");
            menuFileImport                      = new JMenu("Import");
            menuFileExport                      = new JMenu("Export");            
            menuHelp                            = new JMenu("Help");
            menuHelpLanguage                    = new JMenu("Language");
            menuItemAddNetworkLayer             = new JMenuItem("Add Network Layer");
            menuItemAddVectorLayer              = new JMenuItem("Add Vector Layer");
            menuItemEditRedo                    = new JMenuItem("Redo",    helper.getImage("redo.png"));
            menuItemEditUndo                    = new JMenuItem("Undo",    helper.getImage("undo.png"));
            menuItemEditCopy                    = new JMenuItem("Copy");
            menuItemEditCut                     = new JMenuItem("Cut");
            menuItemEditPaste                   = new JMenuItem("Paste");
            menuItemEditFind                    = new JMenuItem("Find",    helper.getImage("find.png"));
            menuItemEditDelete                  = new JMenuItem("Delete");
            menuItemEditDeselect                = new JMenuItem("Deselect");
            menuItemFileImportData              = new JMenuItem("Data");
            menuItemFileImportGeotaggedPhotos   = new JMenuItem("Photos");
            menuItemFileImportMap               = new JMenuItem("Map");
            menuItemFileExportImage             = new JMenuItem("Image");
            menuItemFileExportMap               = new JMenuItem("Map");
            menuItemFileExportTiles             = new JMenuItem("Tiles");
            menuItemFileNew                     = new JMenuItem("New",     helper.getImage("new_map.png"));
            menuItemFileOpen                    = new JMenuItem("Open",    helper.getImage("open_map.png"));
            menuItemFileQuit                    = new JMenuItem("Exit",    helper.getImage("exit.png"));
            menuItemFilePrint                   = new JMenuItem("Print",   helper.getImage("print.png"));
            menuItemFileSave                    = new JMenuItem("Save",    helper.getImage("save.png"));
            menuItemFileSaveAs                  = new JMenuItem("Save As", helper.getImage("save_as.png"));
            menuItemHelpAbout                   = new JMenuItem("About");
            menuItemHelpLanguageEnglish         = new JCheckBoxMenuItem("English");
            menuItemHelpLanguageFrench          = new JCheckBoxMenuItem("Français");   
            menuItemMapCrop                     = new JMenuItem("Crop Map");
            menuLayersMergeLayer                = new JMenu("Merge Layer To");
            menuMap                             = new JMenu("Map");            
            menuMapAddObject                    = new JMenu("Add Object");
            menuMapModifyOrder                  = new JMenu("Order");
            menuMapModifyMoveToLayer            = new JMenu("Move To Layer");
            menuItemMapAddLinearObject          = new JMenuItem("Add Linear Object", helper.getImage("polyline.png"));            
            menuMapOrder                        = new JMenu("Object Order");  
            menuItemLayersDeleteLayers          = new JMenuItem("Delete Layer"); 
            menuItemLayersMoveDown              = new JMenuItem("Move Down");
            menuItemLayersMoveUp                = new JMenuItem("Move Up"); 
            menuItemMoveToLayerNewLayer         = new JMenuItem("New Layer"); 
            menuItemLayersLockLayer             = new JCheckBoxMenuItem("Lock Layer");
            menuItemLayersLayerProperties       = new JMenuItem("Properties");
            menuItemLayersSaveLayer             = new JMenuItem("Save As");
            menuItemLayerItemCopy               = new JMenuItem("Copy");
            menuItemLayerItemCut                = new JMenuItem("Cut");
            menuItemLayerDelete                 = new JMenuItem("Delete");
            menuItemLayerProperties             = new JMenuItem("Properties");
            menuItemMapAddImageOverlay          = new JMenuItem("Add Image Overlay", helper.getImage("image.png"));
            menuItemMapAddPoint                 = new JMenuItem("Add Point",         helper.getImage("marker.png"));
            menuItemMapAddPolygon               = new JMenuItem("Add Polygon",       helper.getImage("polygon.png"));             
            menuItemMapOrderMoveToFront         = new JMenuItem("Move To Front");
            menuItemMapOrderMoveToBack          = new JMenuItem("Move To Back");
            menuItemMapOrderMoveForward         = new JMenuItem("Move Forward");
            menuItemMapOrderMoveBackward        = new JMenuItem("Move Backward"); 
            menuItemMapProperties               = new JMenuItem("Map Properties",    helper.getImage("map_edit.png"));           
            menuItemMapModifyAddMapPoint        = new JMenuItem("Add New Point To Map");
            menuItemMapModifyAddObjectPoint     = new JMenuItem("Add New Point To Object");
            menuItemMapModifyDeleteObjectPoint  = new JMenuItem("Delete Point From Object");
            menuItemMapModifySplitObject        = new JMenuItem("Split Object At Point");        
            menuItemMapModifyCopy               = new JMenuItem("Copy");
            menuItemMapModifyPaste              = new JMenuItem("Paste");
            menuItemMapModifyDelete             = new JMenuItem("Delete");
            menuItemMapModifyProperties         = new JMenuItem("Properties");
            menuItemMapModifyOrderToFront       = new JMenuItem("Move To Front");
            menuItemMapModifyOrderToBack        = new JMenuItem("Move To Back");
            menuItemMapModifyOrderForward       = new JMenuItem("Move Forward");
            menuItemMapModifyOrderBackward      = new JMenuItem("Move Backward");
            menuItemMapNormalCut                = new JMenuItem("Cut");
            menuItemMapNormalCopy               = new JMenuItem("Copy");
            menuItemMapNormalPaste              = new JMenuItem("Paste");
            menuItemMapNormalDelete             = new JMenuItem("Delete");
            menuItemMapNormalNewPoint           = new JMenuItem("New Point");
            menuItemMapNormalProperties         = new JMenuItem("Properties");
            menuItemMergeLineString             = new JMenuItem("Merge into Linear Object", helper.getImage("polyline.png"));
            menuItemMergeLinearRing             = new JMenuItem("Merge into Linear Ring",   helper.getImage("linear_ring.png"));
            menuItemMergePolygon                = new JMenuItem("Merge into Polygon",       helper.getImage("polygon.png"));
            menuItemMergeLogicalGroup           = new JMenuItem("Merge into Logical Group", helper.getImage("multi-geometry.png"));  
            menuItemToolsCreateBubbleChart      = new JMenuItem("Create Bubble Chart");
            menuItemToolsCreateHeatMap          = new JMenuItem("Create Heat Map");
            menuItemToolsDataCatalog            = new JMenuItem("Map Catalog");                        
            menuItemToolsGPS                    = new JMenuItem("GPS");   
            menuItemViewMapTools                = new JCheckBoxMenuItem("Map Tools");
            menuItemViewLayers                  = new JCheckBoxMenuItem("Layers");
            menuItemViewMapOverview             = new JCheckBoxMenuItem("Map Overview");
            menuItemViewObjectData              = new JCheckBoxMenuItem("Object Data");
            menuItemViewObjectDetails           = new JCheckBoxMenuItem("Object Details");
            menuItemViewStatusBar               = new JCheckBoxMenuItem("Status Bar");        
            menuLayersMergeLayer                = new JMenu("Merge Layer To");            
            menuTools                           = new JMenu("Tools");
            menuView                            = new JMenu("View");
            mapPanel                            = new MapPanel();
            mapPropertiesPanel                  = new MapPropertiesPanel(mapPanel.getMap());
            mapToolsBar                         = new JToolBar("Map Tools", JToolBar.VERTICAL);  
            panelExtendedOptionsNorth           = new JPanel(new BorderLayout());
            panelLayerButtons                   = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            panelLayerCenter                    = new JPanel(new BorderLayout());
            panelLayerNorth                     = new JPanel(new BorderLayout());
            panelMainCenter                     = new JPanel(new BorderLayout());
            panelMeasure                        = new JPanel();
            panelMouseCoordinates               = new JPanel(new FlowLayout(FlowLayout.CENTER));
            panelProgressBar                    = new ProgressBarPanel();
            panelRightDoc                       = new JPanel(new GridLayout(2,1));
            panelStatusBar                      = new JPanel(new GridLayout(1,3));
            popupMenuLayers                     = new JPopupMenu();
            popupMenuLayerItem                  = new JPopupMenu();
            popupMenuMapNormal                  = new JPopupMenu(); 
            popupMenuMapModifyPoints            = new JPopupMenu();
            objectDetailsToolBar                = new ObjectDetailsToolBar(this);                    
            tbuttonArrowTool                    = new JToggleButton(helper.getImage("cursor.png"), true);            
            tbuttonModifyPoints                 = new JToggleButton(helper.getImage("mod_points.png"));
            tbuttonSelectTool                   = new JToggleButton(helper.getImage("select.png"));
            tbuttonTrace                        = new JToggleButton(helper.getImage("trace_merge.png"));
            toolbarExtendedOptions              = new JToolBar("Extended Options", JToolBar.VERTICAL);
            toolbarLayers                       = new JToolBar("Layers",           JToolBar.VERTICAL);        
            treeLayers                          = new LayersTree(layersDefaultTreeModel);
            spaneLayers                         = new JScrollPane(treeLayers);                                       
            zoomFormat                          = new DecimalFormat("#0.0"); 
            
            buttonCloseLayerToolbar.setBorder(null);
            buttonCloseOptionToolbar.setBorder(null);
                    
            //Setup Menu Bar
            this.setJMenuBar(mainMenuBar);                                    
            
            //Object Layout
            this.getContentPane().setLayout(new BorderLayout());
            this.add(objectDetailsToolBar,  BorderLayout.NORTH);
            this.add(panelStatusBar,        BorderLayout.SOUTH);
            this.add(panelRightDoc,         BorderLayout.EAST);
            this.add(mapToolsBar,           BorderLayout.WEST);            
            this.add(panelMainCenter,       BorderLayout.CENTER);
            
            //Main Center Panel
            panelMainCenter.add(mapPanel,   BorderLayout.CENTER);
            
            //Layers Tree
            panelRightDoc.add(toolbarLayers);                               
            panelRightDoc.add(toolbarExtendedOptions);  
            treeLayers.setRootVisible(false);
            buttonAddLayer.setBorder(null);
            buttonRemoveLayer.setBorder(null);                     
            treeLayers.addUpdateable(objectDetailsToolBar);
            
            //Satatus Bar
            panelMouseCoordinates.add(labelMouseCoordinates);
            panelStatusBar.add(panelProgressBar);
            panelStatusBar.add(panelMouseCoordinates);
            panelStatusBar.add(panelMeasure);
            
            //MapTools toolbar 
            mapToolsBar.add(tbuttonArrowTool);
            mapToolsBar.add(tbuttonModifyPoints);
            mapToolsBar.add(tbuttonSelectTool);
            mapToolsBar.add(tbuttonTrace);
            mapToolsBar.addSeparator();
            mapToolsBar.add(buttonConnectObject);
            mapToolsBar.addSeparator();
            mapToolsBar.add(buttonMergeObjects);  
            mapToolsBar.addSeparator();
            mapToolsBar.add(buttonUnmerge);
            buttonGroupMapTools.add(tbuttonArrowTool);
            buttonGroupMapTools.add(tbuttonModifyPoints);
            buttonGroupMapTools.add(tbuttonSelectTool);
            buttonGroupMapTools.add(tbuttonTrace);                                   
            
            //layers toolbar
            toolbarLayers.setLayout(new BorderLayout());
            toolbarLayers.add(panelLayerNorth,           BorderLayout.NORTH);
            toolbarLayers.add(panelLayerCenter,          BorderLayout.CENTER);
            panelLayerNorth.add(labelLayersCaption,      BorderLayout.CENTER);
            panelLayerNorth.add(buttonCloseLayerToolbar, BorderLayout.EAST);
            panelLayerCenter.add(spaneLayers,            BorderLayout.CENTER);
            panelLayerCenter.add(panelLayerButtons,      BorderLayout.SOUTH);
            panelLayerButtons.add(buttonAddLayer);
            panelLayerButtons.add(buttonRemoveLayer);  
            
            JPanel tempPanel = new JPanel();
            toolbarExtendedOptions.setLayout(new BorderLayout());            
            panelExtendedOptionsNorth.add(labelExtendedOptions,     BorderLayout.CENTER);
            panelExtendedOptionsNorth.add(buttonCloseOptionToolbar, BorderLayout.EAST);
            toolbarExtendedOptions.add(panelExtendedOptionsNorth,   BorderLayout.NORTH);
            toolbarExtendedOptions.add(tempPanel,                   BorderLayout.CENTER);                
            tempPanel.setPreferredSize(new Dimension(200, 400));              
        } catch (Exception e) {
            System.err.println("Error in MainWindow.init() - " + e);
        }
    }    
    
    /**
     * Operations for when the Layers Tree is clicked.
     * 
     * @param me 
     */
    public void layersTreeClicked(MouseEvent me) {
        CheckedTreeNode selectedCheckedTreeNode;
        Layer               selectedLayer;
        Object              selectedObject;
        Object[]            pathObjects;
        TreePath            tPath;

        try {
            tPath                   = treeLayers.getSelectionPath();
            pathObjects             = tPath.getPath();
            selectedObject          = pathObjects[pathObjects.length - 1];

            //right click menu
            if ((me.getButton() == MouseEvent.BUTTON3)) {
                if (treeLayers.getSelectedNodeObject() instanceof Layer) {
                    showLayersPopupMenu(mapPanel, me.getX(), me.getY());
                } else if (treeLayers.getSelectedNodeObject() instanceof VectorObject) {
                    popupMenuLayerItem.show(treeLayers, me.getX(), me.getY());
                }
            }

            if (selectedObject instanceof CheckedTreeNode) {
                selectedCheckedTreeNode = (CheckedTreeNode) pathObjects[pathObjects.length - 1];
                selectedLayer           = (Layer) selectedCheckedTreeNode.getUserObject();

                if (selectedLayer != null) {
                    selectedLayer.setVisible(selectedCheckedTreeNode.isSelected());
                    mapPanel.getMap().setSelectedLayer(selectedLayer);
                }
            }
        } catch (Exception e) {
            System.err.println("Error in MainWindow.layersTreeClicked(MouseEvent) - " + e);
        }

        mapPanel.repaint();
    }    
    
    @Override
    public void menuSelected(MenuEvent me) {
        if (me.getSource() == menuEdit) {    
            //Update Undo and Redu MenuItems to include the action description.
            updateEditMenu();
        }
    }

    @Override
    public void menuDeselected(MenuEvent me) {
        
    }

    @Override
    public void menuCanceled(MenuEvent me) {

    }    
    
    @Override
    public void mouseClicked(MouseEvent me) {  
        hideAllPopups();
         
        if (me.getSource() == mapPanel) {
            objectDetailsToolBar.update();
            updateExtendedOptions();            
        } else if (me.getSource() == this.treeLayers) {
            this.layersTreeClicked(me);
            return;
        }

        if (me.getButton() == MouseEvent.BUTTON3) {                          
            if (ClipboardOperations.getPasteDataType() != ClipboardOperations.UNKNOWN) {
                menuItemMapModifyPaste.setEnabled(true);
                menuItemMapNormalPaste.setEnabled(true);      
            } else {
                menuItemMapModifyPaste.setEnabled(false);
                menuItemMapNormalPaste.setEnabled(false);               
            }

            if (mapPanel.getMap().getSelectedObjects().size() == 0) {
                menuItemMapModifyDelete.setEnabled(false);
                menuItemMapNormalDelete.setEnabled(false);
                menuItemMapNormalCut.setEnabled(false);
                menuItemMapModifyCopy.setEnabled(false);
                menuMapModifyOrder.setEnabled(false);
                menuMapModifyMoveToLayer.setEnabled(false);
                menuItemMapModifyAddObjectPoint.setEnabled(false);
                menuItemMapModifyDeleteObjectPoint.setEnabled(false);
                menuItemMapModifySplitObject.setEnabled(false);                  
            } else {
                menuItemMapModifyDelete.setEnabled(true);
                menuItemMapModifyCopy.setEnabled(true);
                menuItemMapNormalDelete.setEnabled(true);    
                menuItemMapNormalCut.setEnabled(true);
                menuMapModifyOrder.setEnabled(true);
                menuMapModifyMoveToLayer.setEnabled(true);
                menuItemMapModifyAddObjectPoint.setEnabled(true);
                menuItemMapModifyDeleteObjectPoint.setEnabled(true);
                menuItemMapModifySplitObject.setEnabled(true);  

                //Update menuMapModifyMoveToLayer to have current layers  
                menuMapModifyMoveToLayer.removeAll();

                for (Layer l: mapPanel.getMap().getLayers()) {
                    if (l instanceof VectorLayer) {                    
                        JMenuItem menuItem = new JMenuItem(l.getName());                    
                        menuItem.setActionCommand("Move To Layer");
                        menuItem.addActionListener(this);
                        menuMapModifyMoveToLayer.add(menuItem);
                    }
                }
            }

            if (mapPanel.getDragMode() == MapPanel.DRAG_PAN) {
                popupMenuMapNormal.show(mapPanel, me.getX(), me.getY());
            } else if (mapPanel.getDragMode() == MapPanel.MODIFY) {
                popupMenuMapModifyPoints.show(mapPanel, me.getX(), me.getY());
            } else if (mapPanel.getDragMode() == MapPanel.DRAG_SELECT) {
                popupMenuMapNormal.show(mapPanel, me.getX(), me.getY());
            }            
        }             
        
        updateMouseLocationBar(me);
    }

    @Override
    public void mouseDragged(MouseEvent me) {
    }    
    
    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }      
    
    @Override
    public void mouseMoved(MouseEvent me) {
        updateMouseLocationBar(me);
    }    
    
    @Override
    public void mousePressed(MouseEvent me) {}

    @Override
    public void mouseReleased(MouseEvent me) {}
      
    /**
     * Listens for mouse wheel moment in over this window.
     * 
     * @param mwe 
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
        updateMouseLocationBar(mwe);
    }    
    
    /**
     * Opens the default map, used when first starting the program.
     * 
     */
    private void openDefaulMap() {
        File defaultMapFile = new File("blankmap.fmxml");
        setMap(FmXmlImporter.openFile(defaultMapFile, panelProgressBar));        
        objectDetailsToolBar.update();        
    }
       
    private void populateLayersPopupMenu() {
        Layer selectedLayer;
        
        popupMenuLayers.removeAll();
                
        selectedLayer = mapPanel.getMap().getSelectedLayer();
        
        //Add MenuItems specific to this layer
        for (JMenuItem menuItem: selectedLayer.getContextMenuItems())
            popupMenuLayers.add(menuItem);
        
        //if we actualy added some, put a seperator in.
        if (selectedLayer.getContextMenuItems().length > 0)
            popupMenuLayers.addSeparator();
                
        //Layers popup menu
        popupMenuLayers.add(menuItemLayersSaveLayer);
        popupMenuLayers.add(menuLayersMergeLayer);
        popupMenuLayers.addSeparator();
        popupMenuLayers.add(menuItemLayersMoveUp);
        popupMenuLayers.add(menuItemLayersMoveDown);
        popupMenuLayers.addSeparator();
        popupMenuLayers.add(menuItemLayersDeleteLayers);
        popupMenuLayers.add(menuItemLayersLockLayer);
        popupMenuLayers.addSeparator();
        popupMenuLayers.add(menuItemLayersLayerProperties);
        popupMenuLayers.setInvoker(treeLayers);          
    }
    
    /**
     * Sets the map to be displayed in the MapPanel.
     * 
     * @param mapData 
     */
    @Override
    public void setMap(DigitalMap mapData) {    
        getMapPanel().getMap().closeMap();
        
        mapPanel.setMap(mapData);
        objectDetailsToolBar.setMap(mapData);
        this.actions = mapData.getActions();
        
        buttonMergeObjects.setActions(actions);
        buttonMergeObjects.removeAllMenuItems();
        buttonMergeObjects.add(menuItemMergeLineString,   new MergeToLineString(mapPanel.getMap(),    objectDetailsToolBar));
        buttonMergeObjects.add(menuItemMergeLinearRing,   new MergeToLinearRing(mapPanel.getMap(),    objectDetailsToolBar));
        buttonMergeObjects.add(menuItemMergePolygon,      new MergeToPolygon(mapPanel.getMap(),       objectDetailsToolBar));
        buttonMergeObjects.add(menuItemMergeLogicalGroup, new MergeToMultiGeometry(mapPanel.getMap(), objectDetailsToolBar));
        
        buttonAddLayer.setActions(actions);
        buttonAddLayer.removeAllMenuItems();
        buttonAddLayer.add(menuItemAddNetworkLayer, new AddNetworkLayer(this, mapPanel.getMap()));       
        buttonAddLayer.add(menuItemAddVectorLayer,  new AddVectorLayer(this,  mapPanel.getMap()));
               
        mapPropertiesPanel.setMap(mapPanel.getMap());
        mapData.addUpdateable(this);
        
        update();
    }
    
    /**
     * Sets up main tool bar.
     */ 
   private void setupToolbar() {
        try {
            //File Menu
            menuFile.setMnemonic(KeyEvent.VK_F);
            menuItemFileSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, awtToolKit.getMenuShortcutKeyMask()));
            mainMenuBar.add(menuFile);
            menuFile.add(menuItemFileNew);
            menuFile.add(menuItemFileOpen);
            menuFile.add(menuItemFileSave);
            menuFile.add(menuItemFileSaveAs);
            menuFile.addSeparator();
            menuFile.add(menuItemFilePrint);
            menuFile.addSeparator();
            menuFile.add(menuFileImport);
                menuFileImport.add(menuItemFileImportMap);
                menuFileImport.add(menuItemFileImportGeotaggedPhotos);
                menuFileImport.add(menuItemFileImportData);
            menuFile.add(menuFileExport);
                menuFileExport.add(menuItemFileExportMap);
                menuFileExport.add(menuItemFileExportImage);
                menuFileExport.add(menuItemFileExportTiles);
            menuFile.addSeparator();
            menuFile.add(menuItemFileQuit);
            menuItemFilePrint.setEnabled(false);
            
            //Edit menu
            menuEdit.setMnemonic(KeyEvent.VK_E);
            menuItemEditUndo.setAccelerator    (KeyStroke.getKeyStroke(KeyEvent.VK_Z, awtToolKit.getMenuShortcutKeyMask()));
            menuItemEditCut.setAccelerator     (KeyStroke.getKeyStroke(KeyEvent.VK_X, awtToolKit.getMenuShortcutKeyMask()));
            menuItemEditCopy.setAccelerator    (KeyStroke.getKeyStroke(KeyEvent.VK_C, awtToolKit.getMenuShortcutKeyMask()));
            menuItemEditPaste.setAccelerator   (KeyStroke.getKeyStroke(KeyEvent.VK_V, awtToolKit.getMenuShortcutKeyMask()));
            menuItemEditFind.setAccelerator    (KeyStroke.getKeyStroke(KeyEvent.VK_F, awtToolKit.getMenuShortcutKeyMask()));
            menuItemEditDelete.setAccelerator  (KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
            menuItemEditDeselect.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, awtToolKit.getMenuShortcutKeyMask()));
            menuItemEditUndo.setEnabled(false);
            menuItemEditRedo.setEnabled(false);
            mainMenuBar.add(menuEdit);
            menuEdit.add(menuItemEditUndo);
            menuEdit.add(menuItemEditRedo);
            menuEdit.addSeparator();
            menuEdit.add(menuItemEditCut);
            menuEdit.add(menuItemEditCopy);
            menuEdit.add(menuItemEditPaste);
            menuEdit.add(menuItemEditDelete);
            menuEdit.addSeparator();
            menuEdit.add(menuItemEditFind);
            menuEdit.addSeparator();
            menuEdit.add(menuItemEditDeselect);
        
            //View menu
            menuView.setMnemonic(KeyEvent.VK_V);
            menuItemViewMapTools.setState(true);
            menuItemViewObjectDetails.setState(true);
            menuItemViewStatusBar.setState(true);            
            menuItemViewLayers.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, awtToolKit.getMenuShortcutKeyMask()));
            mainMenuBar.add(menuView);
            menuView.add(menuItemViewMapTools);
            menuView.add(menuItemViewLayers);
            menuView.add(menuItemViewObjectData);
            menuView.add(menuItemViewObjectDetails);
            menuView.add(menuItemViewStatusBar);        
            
            //Map menu
            menuItemMapOrderMoveToFront.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_CLOSE_BRACKET, awtToolKit.getMenuShortcutKeyMask() + InputEvent.SHIFT_MASK));
            menuItemMapOrderMoveToBack.setAccelerator(KeyStroke.getKeyStroke(  KeyEvent.VK_OPEN_BRACKET,  awtToolKit.getMenuShortcutKeyMask() + InputEvent.SHIFT_MASK));
            menuItemMapOrderMoveForward.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_CLOSE_BRACKET, awtToolKit.getMenuShortcutKeyMask()));
            menuItemMapOrderMoveBackward.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET,  awtToolKit.getMenuShortcutKeyMask()));
            menuMap.setMnemonic(KeyEvent.VK_M);
            mainMenuBar.add(menuMap);
            menuMap.add(menuMapAddObject);
                menuMapAddObject.add(menuItemMapAddPoint);
                menuMapAddObject.add(menuItemMapAddLinearObject);
                menuMapAddObject.add(menuItemMapAddPolygon);
                menuMapAddObject.add(menuItemMapAddImageOverlay);
            menuMap.add(menuMapOrder);
                menuMapOrder.add(menuItemMapOrderMoveToFront);
                menuMapOrder.add(menuItemMapOrderMoveForward);
                menuMapOrder.add(menuItemMapOrderMoveBackward);
                menuMapOrder.add(menuItemMapOrderMoveToBack);
            menuMap.add(menuItemMapCrop);    
            menuMap.addSeparator();
            menuMap.add(menuItemMapProperties);
        
            //tools Menu
            menuItemToolsGPS.setEnabled(false); //just until the functionality is created
            mainMenuBar.add(menuTools);
            menuTools.add(menuItemToolsCreateBubbleChart);
            menuTools.add(menuItemToolsCreateHeatMap);
            menuTools.addSeparator();
            menuTools.add(menuItemToolsDataCatalog);
        
            //help menu
            menuItemHelpLanguageEnglish.setSelected(true); //this eventualy needs to determined from the settings file
            menuHelp.setMnemonic(KeyEvent.VK_H);
            mainMenuBar.add(menuHelp);
            menuHelp.add(menuItemHelpAbout);
            menuHelp.add(menuHelpLanguage);
            menuHelpLanguage.add(menuItemHelpLanguageEnglish);
            menuHelpLanguage.add(menuItemHelpLanguageFrench);   

            //Modify Points Popup Menu
            popupMenuMapModifyPoints.setInvoker(mapPanel);
            popupMenuMapModifyPoints.add(menuItemMapModifyAddMapPoint);
            popupMenuMapModifyPoints.addSeparator();
            popupMenuMapModifyPoints.add(menuMapModifyOrder);
                menuMapModifyOrder.add(menuItemMapModifyOrderToFront);                
                menuMapModifyOrder.add(menuItemMapModifyOrderForward);
                menuMapModifyOrder.add(menuItemMapModifyOrderBackward);
                menuMapModifyOrder.add(menuItemMapModifyOrderToBack);
            popupMenuMapModifyPoints.add(menuMapModifyMoveToLayer);
            popupMenuMapModifyPoints.addSeparator();
            popupMenuMapModifyPoints.add(menuItemMapModifyAddObjectPoint);
            popupMenuMapModifyPoints.add(menuItemMapModifyDeleteObjectPoint);
            popupMenuMapModifyPoints.add(menuItemMapModifySplitObject);
            popupMenuMapModifyPoints.addSeparator();
            popupMenuMapModifyPoints.add(menuItemMapModifyCopy);
            popupMenuMapModifyPoints.add(menuItemMapModifyPaste);
            popupMenuMapModifyPoints.add(menuItemMapModifyDelete);
            popupMenuMapModifyPoints.addSeparator();
            popupMenuMapModifyPoints.add(menuItemMapModifyProperties);
            
            //Normal Map Popup Menu
            popupMenuMapNormal.setInvoker(mapPanel);
            popupMenuMapNormal.add(menuItemMapNormalCut);
            popupMenuMapNormal.add(menuItemMapNormalCopy);
            popupMenuMapNormal.add(menuItemMapNormalPaste);
            popupMenuMapNormal.add(menuItemMapNormalDelete);
            popupMenuMapNormal.addSeparator();
            popupMenuMapNormal.add(menuItemMapNormalNewPoint);
            popupMenuMapNormal.addSeparator();
            popupMenuMapNormal.add(menuItemMapNormalProperties); 
            
            //Layers Item Popup Menu
            popupMenuLayerItem.add(menuItemLayerItemCopy);
            popupMenuLayerItem.add(menuItemLayerItemCut);
            popupMenuLayerItem.add(menuItemLayerDelete);
            popupMenuLayerItem.addSeparator();
            popupMenuLayerItem.add(menuItemLayerProperties);

        } catch (Exception e) {
            System.err.print("Error in MainWindow.setupToolbar() - " + e);
        }
    }

   /**
    * Shows the context menu for the LAyers Tree.
    * 
    * @param invoker
    * @param x
    * @param y 
    */
    public void showLayersPopupMenu(Component invoker, int x, int y)  {
        DigitalMap          mapData         = mapPanel.getMap();
        Layer               selectedLayer   = mapData.getSelectedLayer();
        ArrayList<Layer>    layers          = mapData.getLayers();

        //remove current menu Items
        menuLayersMergeLayer.removeAll();

        //add layers to the merge sub-menu
        for (Layer currentLayer: layers) {
            //make sure layers are of the same type and not the same layer
            if ((currentLayer != selectedLayer) && (selectedLayer.getClass() == currentLayer.getClass()))  {
                LayerMenuItem currentLayerMenuItem = new LayerMenuItem(currentLayer, "Merge");
                menuLayersMergeLayer.add(currentLayerMenuItem);
                currentLayerMenuItem.addActionListener(this);
            }
        }

        if (menuLayersMergeLayer.getItemCount() == 0) {
            menuLayersMergeLayer.setEnabled(false);
        } else {
            menuLayersMergeLayer.setEnabled(true);
        }

        if (layers.get(0) == selectedLayer) {
            menuItemLayersMoveUp.setEnabled(false);
            menuItemLayersMoveDown.setEnabled(true);
        } else if (layers.get(layers.size() - 1) == selectedLayer) {
            menuItemLayersMoveUp.setEnabled(true);
            menuItemLayersMoveDown.setEnabled(false);
        } else {
            menuItemLayersMoveUp.setEnabled(true);
            menuItemLayersMoveDown.setEnabled(true);
        }

        //set Layer Locked value
        menuItemLayersLockLayer.setState(mapData.getSelectedLayer().isLocked());

        populateLayersPopupMenu();
        popupMenuLayers.setLocation(y, y);
        popupMenuLayers.show(treeLayers, x, y);
    }   
   
    /**
     * Runs update commands for the MainWindow.
     * 
     */
    @Override
    public void update() {
        if (dataSheetPanel != null) {
            panelMainCenter.remove(dataSheetPanel);
            dataSheetPanel = new DataSheetPanel(mapPanel.getMap());
            dataSheetPanel.setPreferredSize(new Dimension(this.getWidth(), (this.getHeight() / 5)));
            panelMainCenter.add(dataSheetPanel, BorderLayout.SOUTH);
        }
        
        updateLayersTree();
        this.objectDetailsToolBar.update();
        
        this.repaint();
    }      
    
    /**
     * Updates the undo and redo menu descriptions to include the actions that
     * would be undone or redone.
     * 
     */
    private void updateEditMenu() {
        co.foldingmap.actions.Action undo;
        co.foldingmap.actions.Action redo;
        
        try {
            undo = actions.getUndoAction();
            redo = actions.getRedoAction();

            if (undo != null) {
                menuItemEditUndo.setEnabled(true);
                menuItemEditUndo.setText("Undo " + undo.getCommandDescription());
            } else {
                menuItemEditUndo.setEnabled(false);
                menuItemEditUndo.setText("Undo");
            }

            if (redo != null) {
                menuItemEditRedo.setEnabled(true);
                menuItemEditRedo.setText("Redo " + redo.getCommandDescription());
            } else {
                menuItemEditRedo.setEnabled(false);
                menuItemEditRedo.setText("Redo");
            }

            if (ClipboardOperations.getPasteDataType() != ClipboardOperations.UNKNOWN) {
                menuItemEditPaste.setEnabled(true);
            } else {
                menuItemEditPaste.setEnabled(false);
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in MainWindow.updateEditMenu() - " + e);
        }
    }  
    
    /**
     * Updates the panel showing object's extended options.
     */
    public final void updateExtendedOptions() {
        MapObject    selectedObject;
        VectorObject object;

        toolbarExtendedOptions.removeAll();
        
        //save changes to the extended options panel
        if (extendedOptionsPanel != null) extendedOptionsPanel.save();

        if (mapPanel.getMap().getSelectedObjects().size() == 1) {
            selectedObject  = mapPanel.getMap().getSelectedObjects().get(0);
            
            if (selectedObject instanceof PhotoPoint) {                
                PhotoPoint  photoPoint = (PhotoPoint) selectedObject;
                extendedOptionsPanel = new PhotoExtendedOptionsPanel(photoPoint.getPhotoFile());
                
                toolbarExtendedOptions.add(panelExtendedOptionsNorth, BorderLayout.NORTH);
                toolbarExtendedOptions.add(extendedOptionsPanel,      BorderLayout.CENTER);                
                extendedOptionsPanel.setPreferredSize(new Dimension(200, 400));                
            } else if (selectedObject instanceof VectorObject) {
                object               = (VectorObject) mapPanel.getMap().getSelectedObjects().get(0);
                extendedOptionsPanel = new DefaultExtendedOptionsPanel(object);
                
                toolbarExtendedOptions.add(panelExtendedOptionsNorth, BorderLayout.NORTH);
                toolbarExtendedOptions.add(extendedOptionsPanel,      BorderLayout.CENTER);                
                extendedOptionsPanel.setPreferredSize(new Dimension(200, 400));
            } else {
                
            }
        } else if (mapPanel.getMap().getSelectedObjects().size() == 0) { 
            mapPropertiesPanel.setMap(mapPanel.getMap());
            toolbarExtendedOptions.add(panelExtendedOptionsNorth, BorderLayout.NORTH);
            toolbarExtendedOptions.add(mapPropertiesPanel,        BorderLayout.CENTER);                
            mapPropertiesPanel.setPreferredSize(new Dimension(200, 400));    
        } else {
            JPanel tempPanel = new JPanel();
            toolbarExtendedOptions.setLayout(new BorderLayout());            
            panelExtendedOptionsNorth.add(labelExtendedOptions,     BorderLayout.CENTER);
            panelExtendedOptionsNorth.add(buttonCloseOptionToolbar, BorderLayout.EAST);
            toolbarExtendedOptions.add(panelExtendedOptionsNorth,   BorderLayout.NORTH);
            toolbarExtendedOptions.add(tempPanel,                   BorderLayout.CENTER);                
            tempPanel.setPreferredSize(new Dimension(200, 400));              
        }  
        
        toolbarExtendedOptions.revalidate(); 
    }
    
    /**
     * Updates the Layers Tree to all the layers in this map.
     */
    public void updateLayersTree() {
        DigitalMap mapData      = mapPanel.getMap();

        try {
            //remove current tree nodes
            layersDefaultTreeModel.setRoot(null);
            rootLayerNode = new CheckedTreeNode("Layers");
            layersDefaultTreeModel.setRoot(rootLayerNode);
            this.addLayers(mapData.getLayers());
            layersDefaultTreeModel.reload();
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in MainWindow.updateLayersTree() - " + e);
        }
    }    
    
    /**
     * Updates the text in the mouse location bar.
     * 
     * @param me 
     */
    private void updateMouseLocationBar(MouseEvent me) {        
        if (me.getSource() == mapPanel) {
            float   latitude, longitude, zoom;
            String  mouseLocationText;
        
            latitude  = mapPanel.getMapView().getLatitude(me.getX(), me.getY());
            longitude = mapPanel.getMapView().getLongitude(me.getX(), me.getY());
            zoom      = TileMath.getTileMapZoom(mapPanel.getMapView().getZoomLevel());
            mouseLocationText = "Latitude: " + decimalFormat.format(latitude) + " - Longitude: " + decimalFormat.format(longitude) + " Zoom: " + zoomFormat.format(zoom);  
            this.labelMouseCoordinates.setText(mouseLocationText);
        }
    }        
    
    /**
     * Method for updating the object details toolbar.
     * 
     */
    public void updateObjectDetailsToolBar() {
        objectDetailsToolBar.update();
    }
    
    @Override
    public void windowOpened(WindowEvent we) {

    }

    @Override
    public void windowClosing(WindowEvent we) {
        
    }

    @Override
    public void windowClosed(WindowEvent we) {

    }

    @Override
    public void windowIconified(WindowEvent we) {
        this.hideAllPopups();
    }

    @Override
    public void windowDeiconified(WindowEvent we) {

    }

    @Override
    public void windowActivated(WindowEvent we) {

    }

    @Override
    public void windowDeactivated(WindowEvent we) {
        this.hideAllPopups();
    }
}
