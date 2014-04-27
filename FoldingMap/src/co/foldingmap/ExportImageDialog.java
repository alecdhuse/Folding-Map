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

import co.foldingmap.GUISupport.SpringUtilities;
import co.foldingmap.GUISupport.panels.FileChoicePanel;
import co.foldingmap.actions.ExportMapToImage;
import co.foldingmap.map.DigitalMap;
import co.foldingmap.map.MapPanel;
import co.foldingmap.map.MapView;
import co.foldingmap.map.vector.Coordinate;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Alec
 */
public class ExportImageDialog extends JDialog 
                               implements ActionListener, FocusListener {
    
    private ButtonGroup         buttonGroupRange;
    private ExportMapToImage    exportMapToImage;
    private FileChoicePanel     fileChoicePanel;
    private float               zoomLevel;
    private JButton             buttonOk, buttonCancel;
    private JComboBox           comboImageType;              
    private JLabel              labelImageHeight, labelImageWidth;
    private JPanel              panelButtons, panelCenter, panelImageOptions;
    private JPanel              panelImageType, panelImagePreview;
    private JPanel              panelImageRange, panelImageSize, panelImageRangeSize;
    private JPanel              rightCenter;
    private JRadioButton        radioAll, radioSelectedRange;
    private JTextField          textImageHeight, textImageWidth;
    private DigitalMap          mapData;
    private MainWindow          mainWindow;
    private MapPanel            mapPanel;    
    private String[]            imageType = {ExportMapToImage.JPEG, ExportMapToImage.PNG, ExportMapToImage.SVG};
    
    /**
     * Constructor for objects of class ExportImageDialog
     */
    public ExportImageDialog(MainWindow mainWindow, DigitalMap mapData) {
        super(mainWindow, "Export Image");
        
        this.mapData    = mapData;
        this.mainWindow = mainWindow;
        this.zoomLevel  = 0;                        

        init();
        addActionListeners();
        addObjectsToFrame();
        setComponentProperties();
        setupLocation();
        
        this.setVisible(rootPaneCheckingEnabled);
        
        //hack to get preview to show full sized
        Dimension imageDimension = getMapDimensions();
        updateOutputImageDimensions((int) imageDimension.getHeight(), (int) imageDimension.getWidth());
        
        //update preview reference point
        float lat = mapData.getBoundary().getNorth();
        float lon = mapData.getBoundary().getWest();
       
        if (lat > 83.8f) lat = 83.71f;       
        
        mapPanel.getMapView().getMapProjection().setReference(new Coordinate(0f, lat, lon));        
    }    
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        String objectActionCommand;

        try {
            objectActionCommand = ae.getActionCommand();

            if (objectActionCommand.equals("Cancel")) {
                dispose();
            } else if (objectActionCommand.equals("Ok")) {
                exportMapToImage = new ExportMapToImage(mapData, 
                                                        mapPanel.getMapView(), 
                                                        getOutputDimensions(), 
                                                        (String) comboImageType.getSelectedItem(), 
                                                        fileChoicePanel.getSelectedFile());
                exportMapToImage.execute();
                
                
                mainWindow.getUserConfig().setExportDIR(fileChoicePanel.getSelectedFile().getParentFile().toString());
                mainWindow.getUserConfig().setExportFileType((String) comboImageType.getSelectedItem());
                        
                dispose();
            }

        } catch (Exception e) {
            System.out.println("Error in ExportImageDialogListener.actionPerformed(ActionEvent) - " + e);
        }
    }    
    
    /**
     * Add action listeners to all components that have actions
     */
    private void addActionListeners() {
        buttonOk.addActionListener(this);
        buttonCancel.addActionListener(this);
        textImageHeight.addFocusListener(this);
        textImageWidth.addFocusListener(this);

        mapPanel.addMouseListener(mapPanel);
        mapPanel.addMouseMotionListener(mapPanel);
    }    
    
    /**
     * Adds objects to the main window and sets up the layout
     */
    private void addObjectsToFrame() {
        this.getContentPane().setLayout(new BorderLayout());
        this.add(panelCenter,  BorderLayout.CENTER);
        this.add(panelButtons, BorderLayout.SOUTH);

        //image preview
        panelImagePreview.add(mapPanel);

        //image type panel
        panelImageType.add(comboImageType, BorderLayout.NORTH);
        
        //center panel
        panelCenter.add(panelImagePreview);
        panelCenter.add(rightCenter);

        //Image Options
        panelImageRangeSize.add(panelImageSize);
        panelImageRangeSize.add(panelImageRange);
        
        panelImageOptions.add(panelImageRangeSize);
        panelImageOptions.add(panelImageType);        
        
        rightCenter.add(panelImageOptions);        
        rightCenter.add(fileChoicePanel);

        //Image Range
        buttonGroupRange.add(radioAll);
        buttonGroupRange.add(radioSelectedRange);
        panelImageRange.add(radioAll);
        panelImageRange.add(radioSelectedRange);

        //Ok and Cancel Buttons
        panelButtons.add(buttonOk);
        panelButtons.add(buttonCancel);

        //Image Size
        panelImageSize.add(labelImageHeight);
        panelImageSize.add(textImageHeight);
        panelImageSize.add(labelImageWidth);
        panelImageSize.add(textImageWidth);

        SpringUtilities.makeCompactGrid(panelImageSize, 2, 2, 3, 3, 4, 10);
    }        
    
    @Override
    public void focusGained(FocusEvent e) {

    }

    @Override
    public void focusLost(FocusEvent e) {
        int height, width;

        height = getOutputHeight();
        width  = getOutputWidth();

        updateOutputImageDimensions(height, width);
    }    
    
    /**
     * Initializes all displayable objects
     */
    private void init() {
        buttonOk                     = new JButton("Ok");
        buttonCancel                 = new JButton("Cancel");
        buttonGroupRange             = new ButtonGroup();
        comboImageType               = new JComboBox(imageType);
        fileChoicePanel              = new FileChoicePanel(this, FileChoicePanel.SAVE, mainWindow.getUserConfig().getExportDIR());
        labelImageHeight             = new JLabel("Height: ");
        labelImageWidth              = new JLabel("Width: ");
        mapPanel                     = new MapPanel();
        panelButtons                 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelCenter                  = new JPanel(new GridLayout(1, 2, 5, 5));
        panelImageOptions            = new JPanel(new GridLayout(2, 1));
        panelImagePreview            = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelImageRange              = new JPanel(new GridLayout(2,1));
        panelImageRangeSize          = new JPanel(new GridLayout(1,2));
        panelImageSize               = new JPanel(new SpringLayout());
        panelImageType               = new JPanel(new BorderLayout());
        radioAll                     = new JRadioButton("All", true);
        radioSelectedRange           = new JRadioButton("Selected Range");
        rightCenter                  = new JPanel();
        textImageHeight              = new JTextField(6);
        textImageWidth               = new JTextField(6);
    }
    
    /**
     * Gets the Dimensions of the DigitalMap
     *
     * @return  Dimension  The dimensions of the map.
     */
    public final Dimension getMapDimensions() {
        float height, width, ratio;
        
        width  = mapData.getBoundary().getWidth();
        height = mapData.getBoundary().getHeight();        
                
        if (width > height) {
            ratio  = width  / height;
            height = 500;
            width  = 500 * ratio;
        } else {
            ratio  = height / width;
            height = 500 * ratio;
            width  = 500;
        }              
        
        return new Dimension((int) width, (int) height);
    }    
    
    /**
     * Returns the output dimensions of the image.
     * 
     * @return 
     */
    public Dimension getOutputDimensions() {
        Dimension d;
        
        d = new Dimension(getOutputWidth(), getOutputHeight());
        
        return d;
    }
    
    public int getOutputHeight() {
        int height = 250;

        try {
            height = Integer.parseInt(textImageHeight.getText());
        } catch (Exception e) {
            System.out.println("Error in ExportImageDialog.getOutputWidth() - " + e);
        }

        return height;
    }

    public int getOutputWidth() {
        int width = 250;

        try {
            width = Integer.parseInt(textImageWidth.getText());
        } catch (Exception e) {
            System.out.println("Error in ExportImageDialog.getOutputWidth() - " + e);        
        }

        return width;
    }    
    
    /**
     * Set all the action commands, tool tips and others for objects used in this class.
     */
    private void setComponentProperties() {
        try {
            buttonOk.setActionCommand("Ok");
            buttonCancel.setActionCommand("Cancel");

            panelImagePreview.setBackground(new Color(43, 46, 55));

            textImageHeight.setHorizontalAlignment(JTextField.RIGHT);
            textImageWidth.setHorizontalAlignment(JTextField.RIGHT);

            panelImageSize.setBorder   (new TitledBorder("Image Size"));
            panelImageType.setBorder   (new TitledBorder("Image Type"));
            panelImageRange.setBorder  (new TitledBorder("Range"));
            fileChoicePanel.setBorder  (new TitledBorder("Output File"));
            panelCenter.setBorder(LineBorder.createGrayLineBorder());

            Dimension imageDimension = getMapDimensions();
            updateOutputImageDimensions((int) imageDimension.getHeight(), (int) imageDimension.getWidth());

            comboImageType.setMinimumSize(  new Dimension(344, 28));
            comboImageType.setPreferredSize(new Dimension(344, 28));

            //Set the export file type to that in the UserConfig
            for (int i = 0; i < comboImageType.getItemCount(); i++) {
                String item = (String) comboImageType.getItemAt(i);

                if (item.equalsIgnoreCase(mainWindow.getUserConfig().getExportFileType())) {
                    comboImageType.setSelectedIndex(i);
                    break;
                }
            }

            mapPanel.showScale(false);
            mapPanel.showZoomControls(false);        
            mapPanel.setMap(mapData);     
            mapPanel.getMapView().setDisplayAll(true);
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in ExportImageDialog.setComponentProperties() - " + e);
        }
    }    
    
    /**
     * Sets up the screen location and size of the dialog
     */
    private void setupLocation() {
        Toolkit   tk           = Toolkit.getDefaultToolkit();
        Dimension screenSize   = tk.getScreenSize();
        int       screenHeight = screenSize.height;
        int       screenWidth  = screenSize.width;
        int       x            = (screenWidth  - 720) / 2;
        int       y            = (screenHeight - 350) / 2;

        this.setSize(720, 350);
        this.setLocation(x, y);
    }    
    
    /**
     * Updates the preview and text boxes of the output image dimensions.
     */
    public final void updateOutputImageDimensions(int height, int width) {
       Coordinate   minCoordinate;
       float        zoomOffset;
       float        dimensionRatio;
       float        scaleX,  scaleY;
       float        lat, lon;
       int          outputHeight, outputWidth, x, y;
       int          panelHeight, panelWidth;
       MapView      mapView;

       textImageHeight.setText(Integer.toString(height));
       textImageWidth.setText( Integer.toString(width));

       mapView       = mapPanel.getMapView();
       minCoordinate = mapData.getBoundary().getNorthWestCoordinate();
       panelHeight   = panelImagePreview.getHeight();
       panelWidth    = panelImagePreview.getWidth();
               
       if (panelHeight == 0) {
           panelHeight = 287;
           panelWidth  = 306;
       } 
       
       if (height < panelHeight) {
           outputHeight = height;
       } else {
           if (height > width) {
                //set the height to the height of the parent container
                outputHeight = panelHeight - 5;
           } else {
                //calculate the display ratio
                dimensionRatio = (panelWidth / (float) width);
                outputHeight   = (int) (dimensionRatio * height) - 5;
           }
       }

       if (width < panelWidth) {
           outputWidth = width;
       } else {
           if (width > height) {
               //set the width to the whole width of the container
               outputWidth = panelWidth - 5;
           } else {
                //calculate the display ratio
                dimensionRatio = (panelHeight / (float) height);
                outputWidth    = (int) (dimensionRatio * width) - 5;
           }
       }

       x = ((panelWidth  - outputWidth)  / 2);
       y = ((panelHeight - outputHeight) / 2);       

       scaleX = (outputWidth  / (float) width);
       scaleY = (outputHeight / (float) height);       

       //reset map reference
       mapView.getMapProjection().setReference(minCoordinate);
       
       if (zoomLevel == 0 || zoomLevel == 1) {
           zoomLevel  = (float) mapView.getZoomLevel();             
           zoomOffset = (float) (zoomLevel * (1.0f / scaleX) / 4.141f);
           
           mapView.getMapProjection().setZoomLevel(zoomOffset);
       }
              
       mapPanel.setScale(scaleX, scaleY);
       mapPanel.setBounds(x, y, outputWidth, outputHeight);       
       
       this.repaint();
    }
    
}
