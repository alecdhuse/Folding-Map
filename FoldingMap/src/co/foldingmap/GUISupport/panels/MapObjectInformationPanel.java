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

import co.foldingmap.GUISupport.SpringUtilities;
import co.foldingmap.GUISupport.components.HtmlTextArea;
import co.foldingmap.map.MapObject;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Alec
 */
public class MapObjectInformationPanel extends JPanel implements ActionListener {
    private HtmlTextArea    htmlTextArea;
    private JLabel          objectNameLabel;
    private JPanel          objectNamePanel;
    private JTextField      objectNameText;
    private MapObject       object;
    private Dialog          parentWindow;
    
    public MapObjectInformationPanel(Dialog parentWindow, MapObject object) {
        this.parentWindow = parentWindow;
        this.object       = object;
        
        init();
        setupPanel();
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equalsIgnoreCase("Ok")) {
            //update object information
            object.setDescription(htmlTextArea.getHtml());
            object.setName(objectNameText.getText());
        }
    }    
    
    private void init() {
        htmlTextArea    = new HtmlTextArea(parentWindow);
        objectNameLabel = new JLabel("Name");
        objectNamePanel = new JPanel(new SpringLayout());
        objectNameText  = new JTextField(object.getName());
        
        htmlTextArea.setHtml(object.getDescription());
        htmlTextArea.setBorder(new TitledBorder("Description"));
    }
    
    private void setupPanel() {
        this.setLayout(new BorderLayout());
        
        this.add(objectNamePanel, BorderLayout.NORTH);
        this.add(htmlTextArea,    BorderLayout.CENTER);
        
        objectNamePanel.add(objectNameLabel);
        objectNamePanel.add(objectNameText);
        SpringUtilities.makeCompactGrid(objectNamePanel, 1, 2, 3, 3, 4, 10);

    }


}
