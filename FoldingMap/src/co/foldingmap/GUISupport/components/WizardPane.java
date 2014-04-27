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
package co.foldingmap.GUISupport.components;

import co.foldingmap.GUISupport.SpringUtilities;
import co.foldingmap.Logger;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Use actionListener to determine when the wizard is finished.  The action 
 * command changes from "Next" to "Finish" when the wizard is on the last page.
 * 
 * @author Alec
 */
public class WizardPane extends JPanel implements ActionListener, ListSelectionListener {
    protected ActionListener                actionListener;
    protected ArrayList<String>             names;
    protected ArrayList<WizardPanePanel>    panels;
    protected DefaultListModel              listModel;
    protected Dialog                        parentDialog;
    protected int                           panelIndex;
    protected JButton                       buttonBack, buttonCancel, buttonNext;
    protected JLabel                        labelSteps;
    protected JList                         panelList;
    protected JPanel                        panelButtons, panelCenter, panelDisplayedStep, panelSteps;
    protected String                        actionCommand;
    
    /**
     * Constructor for WizardPane.
     * 
     * @param parentDialog 
     */
    public WizardPane(Dialog parentDialog) {
        this.actionCommand = "Next";
        this.parentDialog  = parentDialog;
        names              = new ArrayList<String>();
        panels             = new ArrayList<WizardPanePanel>();

        init();
        setupPanel();
    }    
    
    /**
     * 
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        AbstractButton  initiatingButton;
        Object          initiatingObject;
        String          actionCommand;

        initiatingObject = e.getSource();

        //preform internal functions
        if (initiatingObject instanceof AbstractButton) {
            initiatingButton = (AbstractButton) initiatingObject;
            actionCommand    = initiatingButton.getActionCommand();

            if (actionCommand.equalsIgnoreCase("Back")) {
                back();
            } else if(actionCommand.equalsIgnoreCase("Cancel")) {
                cancel();
            } else if (actionCommand.equalsIgnoreCase("Next")) {
                nextPage();
            }
        }

        //triger the action listener
        ActionEvent ae = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, this.actionCommand);

        if (actionListener != null)
            actionListener.actionPerformed(ae);

        //update action command
        if (buttonNext.getText().equalsIgnoreCase("Finish")) {
            this.actionCommand = "Finish";
        } else if (buttonNext.getText().equalsIgnoreCase("Next")) {
            this.actionCommand = "Next";
        }
    }    
    
    /**
     * Adds an action listener to this pane.
     * 
     * @param a 
     */
    public void addActionListener(ActionListener a) {
        this.actionListener = a;
    }    
    
    /**
     * Adds a WizardPanePanel to the Wizard.
     *
     * @param name  The name to be displayed in the steps list.
     * @param panel The actual panel to be added.
     */
    public void addPanel(String name, WizardPanePanel panel) {
        names.add(name + " ");
        panels.add(panel);
        listModel.addElement(name);

        if (panels.size() == 1) {
            panelList.setSelectedIndex(0);
            buttonNext.setText("Finish");
            panelDisplayedStep.removeAll();
            panelDisplayedStep.add(name, panel);
        } else if (panels.size() > 1) {
            buttonNext.setText("Next");
        }
    }   
    
    /**
     * Add a WizardPanePanel to this WizardPane.
     * 
     * @param name
     * @param panel
     * @param index 
     */
    public void addPanel(String name, WizardPanePanel panel, int index) {
        names.add(name);
        panels.add(panel);
        listModel.add(index, name);
    }    
    
    /**
     * Allow the advance to the next WizardPanePanel
     * 
     * @param advance 
     */
    public void allowAdvance(boolean advance) {
        buttonNext.setEnabled(advance);
    }    
    
    /**
     * Changes the displayed Panel to the previous panel.
     */
    public void back() {
        String          currentName;
        WizardPanePanel currentPanel;

        try {
            if (panelIndex > 0) {
                panelDisplayedStep.removeAll();
                panelIndex--;

                currentPanel = panels.get(panelIndex);
                currentName  = names.get(panelIndex);

                currentPanel.displayPanel();
                panelDisplayedStep.add(currentName, currentPanel);
                panelList.setSelectedIndex(panelIndex);
            }

            if ((panels.size() - 1) > (panelIndex)) {
                buttonNext.setText("Next");
            } else {
                buttonNext.setText("Finish");
            }

            if (panelIndex > 0) {
                buttonBack.setEnabled(true);
            } else {
                buttonBack.setEnabled(false);
            }

            panelDisplayedStep.revalidate();
            this.revalidate();
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in WizardPane.back() - " + e);
        }
    }
    
    /**
     * Cancel the wizard and close the parentDialog.
     */
    public void cancel() {
        parentDialog.dispose();
    }    
    
    /**
     * Returns the index of selected and displayed panel.
     * 
     * @return 
     */
    public int getSelectedPanelIndex() {
        return panelIndex;
    }    
    
    /**
     * Initiate the objects for this pane.
     */
    private void init() {
        buttonBack         = new JButton("Back");
        buttonCancel       = new JButton("Cancel");
        buttonNext         = new JButton("Next");
        labelSteps         = new JLabel("Steps", JLabel.CENTER);
        listModel          = new DefaultListModel();
        panelCenter        = new JPanel(new SpringLayout());
        panelDisplayedStep = new JPanel();
        panelList          = new JList(listModel);
        panelButtons       = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelIndex         = 0;
        panelSteps         = new JPanel(new SpringLayout());
        
        buttonBack.addActionListener(this);
        buttonBack.setActionCommand("Back");
        buttonCancel.addActionListener(this);
        buttonCancel.setActionCommand("Cancel");
        buttonNext.addActionListener(this);
        buttonNext.setActionCommand("Next");
        panelList.addListSelectionListener(this);
        buttonBack.setEnabled(false);
    }    
    
    /**
     * Advance to the Wizard next page.
     */
    private void nextPage() {
        String           currentName;
        WizardPanePanel  currentPanel;

        try {
            if (panelIndex < (panels.size() - 1)) {
                panelDisplayedStep.removeAll();
                panelIndex++;

                currentPanel = panels.get(panelIndex);
                currentName  = names.get(panelIndex);

                currentPanel.displayPanel();
                panelList.setSelectedIndex(panelIndex);
                panelDisplayedStep.add(currentName, currentPanel);
                allowAdvance(currentPanel.canAdvance());
                currentPanel.revalidate();
            }

            if (panels.size() > (panelIndex + 1)) {
                buttonNext.setText("Next");
            } else {
                buttonNext.setText("Finish");
            }

            if (panelIndex > 0) {
                buttonBack.setEnabled(true);
            } else {
                buttonBack.setEnabled(false);
            }

            panelDisplayedStep.revalidate();
            this.revalidate();
            this.repaint();
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in WizardPane.nextPage() - " + e);
        }
    }    
    
    /**
     * @param g
     */
    @Override
    public void paintAll(Graphics g) {
        int height, width;
        
        height = panelCenter.getHeight();
        width  = 220;

        setPreferredSize(new Dimension(width, height));
        super.paintAll(g);
    }    
    
    /**
     * Setup the location of the pane's components. 
     */
    private void setupPanel() {
        this.setLayout(new BorderLayout());

        panelButtons.add(buttonCancel);
        panelButtons.add(buttonBack);
        panelButtons.add(buttonNext);

        panelSteps.setBorder(LineBorder.createBlackLineBorder());
        panelSteps.setBackground(Color.WHITE);
        panelSteps.add(labelSteps);
        panelSteps.add(panelList);
        SpringUtilities.makeCompactGrid(panelSteps, 2, 1, 3, 3, 4, 10);

        panelCenter.add(panelSteps);
        panelCenter.add(panelDisplayedStep);
        SpringUtilities.makeCompactGrid(panelCenter, 1, 2, 3, 3, 4, 10);

        this.add(panelButtons, BorderLayout.SOUTH);
        this.add(panelCenter,  BorderLayout.CENTER);
    }    
    
    public void valueChanged(ListSelectionEvent e) {
        //reset the selection to the current panel
        panelList.setSelectedIndex(panelIndex);
    }    
}
