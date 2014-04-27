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
package co.foldingmap.GUISupport.components.checkBoxTree;

import co.foldingmap.Logger;
import co.foldingmap.ResourceHelper;
import co.foldingmap.map.tile.TileLayer;
import co.foldingmap.map.vector.MultiGeometry;
import co.foldingmap.map.vector.VectorObject;
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author Alec
 */
public class CheckBoxNodeRenderer implements TreeCellRenderer {
    
  private JCheckBox      checkBoxRenderer;
  private Color          selectionBorderColor, selectionForeground, selectionBackground;
  private Color          textForeground, textBackground;
  private Boolean        booleanValue;
  private Font           fontValue;
  private ImageIcon      iconBlank, iconCollapsed, iconExpanded;
  private ImageIcon      iconPoint, iconLine, iconRing, iconPoly, iconMulti, iconTile;
  private ResourceHelper helper;
  
  protected JCheckBox getLeafRenderer() {
    return checkBoxRenderer;
  }

  public CheckBoxNodeRenderer() {
      try {
        helper         = ResourceHelper.getInstance();
        booleanValue   = (Boolean) UIManager.get("Tree.drawsFocusBorderAroundIcon");
        fontValue      = UIManager.getFont("Tree.font");
        textForeground = UIManager.getColor("Tree.textForeground");
        textBackground = UIManager.getColor("Tree.textBackground");

        selectionBorderColor = UIManager.getColor("Tree.selectionBorderColor");
        selectionForeground  = UIManager.getColor("Tree.selectionForeground");
        selectionBackground  = UIManager.getColor("Tree.selectionBackground");

        //load images
        iconCollapsed   = helper.getImage("bullet_arrow_right.png");
        iconExpanded    = helper.getImage("bullet_arrow_down.png");
        iconPoint       = helper.getImage("marker.png");
        iconLine        = helper.getImage("polyline.png");
        iconRing        = helper.getImage("linear_ring.png");
        iconPoly        = helper.getImage("polygon.png");
        iconMulti       = helper.getImage("multi-geometry.png");
        iconBlank       = helper.getImage("blank.png");
        iconTile        = helper.getImage("tile-map.png");
        
      } catch (Exception e) {
        Logger.log(Logger.ERR, "Error in CheckBoxNodeRenderer() - " + e);
      }
  }

  @Override
  public Component getTreeCellRendererComponent(JTree   tree, 
                                                Object  value,
                                                boolean selected, 
                                                boolean expanded, 
                                                boolean leaf, 
                                                int     row, 
                                                boolean hasFocus) {

      Dimension panelDimension;
      JLabel    imageLabel, textLabel;
      JPanel    panel;      
      
      panel = new JPanel(new BorderLayout(0,0));
      
      if (value instanceof CheckedTreeNode) {
        CheckedTreeNode   node;                            
        Dimension         imageDimension;
        JCheckBox         returnCheckbox;

        node = (CheckedTreeNode) value;

        //Set Icons for expanded and not expanded
        if (node.getChildCount() > 0) {
            if (node.isOpened()) {
                imageLabel = new JLabel(iconExpanded);
            } else {
                imageLabel = new JLabel(iconCollapsed);
            }
        } else {
            if (node.getUserObject() instanceof TileLayer) {
                imageLabel = new JLabel(iconTile);
            } else {
                imageLabel = new JLabel(iconBlank);
            }
        }

        returnCheckbox = new JCheckBox(value.toString());      

        returnCheckbox.setEnabled(tree.isEnabled());
        returnCheckbox.setSelected(node.isSelected());
        returnCheckbox.setFocusPainted((booleanValue != null) && (booleanValue.booleanValue()));            
        
        panelDimension = new Dimension(tree.getWidth() - 1, 20);
        panel.setPreferredSize(panelDimension);
        panel.setMaximumSize(panelDimension);

        panel.add(imageLabel,     BorderLayout.WEST);
        panel.add(returnCheckbox, BorderLayout.CENTER);      

        if (fontValue != null) {
            returnCheckbox.setFont(fontValue);
        }
        
        if (selected) {
            returnCheckbox.setForeground(selectionForeground);
            returnCheckbox.setBackground(selectionBackground);
            panel.setBackground(selectionBackground);
        } else {
            returnCheckbox.setForeground(textForeground);
            returnCheckbox.setBackground(textBackground);
            panel.setBackground(textBackground);
        }

        imageDimension = new Dimension(16, 20);
        imageLabel.setPreferredSize(imageDimension);
        imageLabel.setMaximumSize(imageDimension);     
      } else if (value instanceof DefaultMutableTreeNode) {
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;          
        String                 displayText;
        VectorObject           vo = null;
        
        panelDimension = new Dimension(tree.getWidth() - 50, 20);
        panel.setPreferredSize(panelDimension);
        panel.setMaximumSize(panelDimension);                
        
        if (treeNode.getUserObject() instanceof String) {
            displayText = (String) treeNode.getUserObject(); 
        } else if (treeNode.getUserObject() instanceof VectorObject) {
            vo = (VectorObject) treeNode.getUserObject();  
            displayText = vo.getName();
        } else {
            displayText = "";
        }

        if (vo instanceof MultiGeometry) {
            textLabel  = new JLabel(displayText, iconMulti, SwingConstants.LEFT);  
        } else if (vo instanceof co.foldingmap.map.vector.Polygon) {
            textLabel  = new JLabel(displayText, iconPoly, SwingConstants.LEFT); 
        } else if (vo instanceof co.foldingmap.map.vector.LinearRing) {   
            textLabel  = new JLabel(displayText, iconRing, SwingConstants.LEFT); 
        } else if (vo instanceof co.foldingmap.map.vector.LineString) {
            textLabel  = new JLabel(displayText, iconLine, SwingConstants.LEFT); 
        } else if (vo instanceof co.foldingmap.map.vector.MapPoint) { 
            textLabel  = new JLabel(displayText, iconPoint, SwingConstants.LEFT); 
        } else {
            textLabel  = new JLabel(displayText, iconBlank, SwingConstants.LEFT); 
        }
        
        if (selected) {
            textLabel.setForeground(selectionForeground);
            textLabel.setBackground(selectionBackground);
            panel.setBackground(selectionBackground);
        } else {
            textLabel.setForeground(textForeground);
            textLabel.setBackground(textBackground);
            panel.setBackground(textBackground);
        }  
        
        panel.add(textLabel,  BorderLayout.CENTER);              
      }
      
      return panel;
  }    
}
