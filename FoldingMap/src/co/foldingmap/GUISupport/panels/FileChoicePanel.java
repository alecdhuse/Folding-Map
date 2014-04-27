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

import co.foldingmap.GUISupport.FileExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Alec
 */
public class FileChoicePanel extends JPanel implements ActionListener {
    public static final int OPEN    = 1;
    public static final int SAVE    = 2;
    
    protected ArrayList<ActionListener> actionListeners;
    protected boolean                   acceptDIR;
    protected Dialog                    parentDialog;
    protected FileDialog                fileDialog;
    protected FileExtensionFilter       fileExtensionFilter;
    protected JButton                   buttonChooseFile;
    protected JTextField                textFilePath;
    protected String                    actionCommand, fileName, openToDirectory;    
    
    /**
     * Constructor with default OPEN action and directory.
     * 
     * @param parentDialog 
     */
    public FileChoicePanel(Dialog parentDialog) {                
        this.parentDialog = parentDialog;
        init(OPEN);
    }

    /**
     * Constructor with specified fileAction and default open directory.
     * 
     * @param parentDialog
     * @param fileAction 
     */
    public FileChoicePanel(Dialog parentDialog, int fileAction) {                
        this.parentDialog = parentDialog;
        init(fileAction);
    }     
    
    /**
     * Constructor with specified fileAction and open directory.
     * 
     * @param parentDialog
     * @param fileAction
     * @param openToDirectory 
     */
    public FileChoicePanel(Dialog parentDialog, int fileAction, String openToDirectory) {                
        this.parentDialog    = parentDialog;
        this.openToDirectory = openToDirectory;
        init(fileAction);
    }     
    
    /**
     * Set if this panel will accept directories as well as files.
     * 
     * @param acceptDIR 
     */
    public void acceptDIR(boolean acceptDIR) {
        //TODO: Make this work for windows and linux
        
        if (acceptDIR) {
            System.setProperty("apple.awt.fileDialogForDirectories", "true");
            this.fileExtensionFilter.acceptDirectories(true);
        } else {
            System.setProperty("apple.awt.fileDialogForDirectories", "false");
            this.fileExtensionFilter.acceptDirectories(false);
        }
    }    
    
    /**
     * Executes actions
     * 
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        AbstractButton  initiatingButton;
        ActionEvent     ae;
        Object          initiatingObject;
        String          objectActionCommand;

        initiatingObject = e.getSource();

        if (initiatingObject instanceof AbstractButton) {
            initiatingButton       = (AbstractButton) initiatingObject;
            objectActionCommand    = initiatingButton.getActionCommand();

            if (objectActionCommand.equalsIgnoreCase("Choose")) {
                showFileDialog();
            }
        }

        //change the action command to use the one specified
        ae = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, actionCommand);
        
        //tigger events for ActionListeners
        for (ActionListener al: actionListeners) {
            al.actionPerformed(ae);
        }
    }    
    
    /**
     * Adds an action listener to this panel.
     * 
     * @param l 
     */
    public void addActionListener(ActionListener l) {
        actionListeners.add(l);
    }    
    
    /**
     * Adds an extension to be accepted by the panel.
     * 
     * @param extension 
     */
    public void addFilenameFilter(String extension) {
        this.fileExtensionFilter.addExtension(extension);
        fileDialog.setFilenameFilter(this.fileExtensionFilter);
    }      
    
    /**
     * Clears the File selection from this object.
     * 
     */
    public void clear() {
        this.textFilePath.setText("");
    }
    
    /**
     * Returns the selected file from the panel.
     * 
     * @return 
     */
    public File getSelectedFile() {
        File    returnFile = null;

        //If the text field is different then use that vaule for the filename.
        if (!fileName.equals(textFilePath.getText())) 
            fileName = textFilePath.getText();        
        
        if (fileName != null)
            returnFile = new File(fileName);

        return returnFile;
    }    
    
    /**
     * Returns the extension for the selected file.
     * 
     * @return 
     */
    public String getSelectedFileExtension() {
        int    extensionStart;
        String fileExtension  = "";

        //If the text field is different then use that vaule for the filename.
        if (!fileName.equals(textFilePath.getText())) 
            fileName = textFilePath.getText();         
        
        if (fileName != null) {
            extensionStart = fileName.lastIndexOf('.') + 1;

            if (extensionStart > 0)
                fileExtension = fileName.substring(extensionStart);
        }

        return fileExtension;
    }
    
    /**
     * Returns the text of the panel, when a file is selected it will return
     * its full path and name.
     * 
     * @return 
     */
    public String getText() {
        return this.textFilePath.getText();
    }
     
    /**
     * Initiate the components of the panel.
     * 
     * @param fileAction 
     */
    private void init(int fileAction) {
        this.setLayout(new GridBagLayout());
        
        this.actionCommand       = "File Selected";
        this.actionListeners     = new ArrayList<ActionListener>();        
        this.fileExtensionFilter = new FileExtensionFilter();
        
        if (fileAction == OPEN) {
            fileDialog = new FileDialog(parentDialog, "Open File", FileDialog.LOAD);
        } else if (fileAction == SAVE) {
            fileDialog = new FileDialog(parentDialog, "Save File", FileDialog.SAVE);
        }
        
        buttonChooseFile = new JButton("Choose");        
        textFilePath     = new JTextField(20);

        fileDialog.setDirectory(this.openToDirectory);
        buttonChooseFile.addActionListener(this);
        buttonChooseFile.setActionCommand("Choose");
        
        GridBagConstraints  gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 4;
        this.add(textFilePath, gridBagConstraints);
        
        gridBagConstraints.gridx     = 5;
        gridBagConstraints.gridwidth = 1;        
        this.add(buttonChooseFile, gridBagConstraints);        
    }    
    
    /**
     * Sets the action command for this panel.
     * 
     * @param actionCommand 
     */
    public void setActionCommand(String actionCommand) {
        this.actionCommand = actionCommand;
    }

    /**
     * Sets the file address
     * 
     * @param textFilePath 
     */
    public void setFile(String textFilePath) {
        this.textFilePath.setText(textFilePath);
    }
    
    /**
     * Shows the file dialog to choose a file.
     */
    public void showFileDialog() {
        fileDialog.setVisible(true);
        fileName = fileDialog.getDirectory() + fileDialog.getFile();

        if (!fileName.equalsIgnoreCase("nullnull"))  
           textFilePath.setText(fileName);
    }        

    /**
     * Sets the minimum size for this panel
     * @param dmnsn 
     */
    @Override
    public void setMinimumSize(Dimension dmnsn) {
        super.setMinimumSize(dmnsn);
        
        Dimension dText = new Dimension((dmnsn.width - 75), dmnsn.height - 4);
        
        buttonChooseFile.setMinimumSize(new Dimension(75, dmnsn.height - 4));
        textFilePath.setMinimumSize(dText);  
        textFilePath.setSize(dText);
    }
    
    /**
     * Sets the preferred size for this panel.
     * 
     * @param d 
     */
    @Override
    public void setPreferredSize(Dimension dmnsn) {
        super.setPreferredSize(dmnsn);
        
        buttonChooseFile.setPreferredSize(new Dimension(75, dmnsn.height - 4));
        textFilePath.setPreferredSize(new Dimension((dmnsn.width - 75), dmnsn.height - 4));
    }
    
    /**
     * Sets the text for the text portion of this object.
     * 
     * @param text 
     */
    public void setText(String text) {
        this.textFilePath.setText(text);
    }
}
