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

import co.foldingmap.map.Layer;
import co.foldingmap.map.vector.MultiGeometry;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.VectorObject;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * This is the tree node to be displayed in the layers tree.
 * 
 * @author Alec
 */
    public class CheckedTreeNode extends DefaultMutableTreeNode {
    private boolean                 opened, selected;
    private int                     layerNumber;
    private Layer                   layer;    
    
    public CheckedTreeNode() {
        super();
        this.layerNumber    = -1;
        this.opened         = false;
        this.selected       = false;
        
        this.setAllowsChildren(true);
    }

    public CheckedTreeNode(Object obj) {
        super(obj);
        this.layerNumber    = -1;
        this.opened         = false;
        this.selected       = false;
        
        this.setAllowsChildren(true);
        
        if (obj instanceof Layer) {
            layer = (Layer) obj;
            
            if (layer instanceof VectorLayer) {
                VectorLayer vl = (VectorLayer) layer;
                    
                for (VectorObject vo: vl.getObjectList()) {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(vo);                    
                    this.add(node);
                    
                    if (vo instanceof MultiGeometry) {
                        MultiGeometry mg = (MultiGeometry) vo;
                        
                        for (VectorObject comVO: mg.getComponentObjects()) 
                            node.add(new DefaultMutableTreeNode(comVO));                        
                    }                    
                }
            }
        }
    }

    public CheckedTreeNode(Object obj, boolean allowChildren) {
        super(obj, allowChildren);
        this.layerNumber    = -1;
        this.opened         = false;
        this.selected       = false;        
        
        if (obj instanceof Layer) {
            layer = (Layer) obj;
            
            if (layer instanceof VectorLayer) {
                VectorLayer vl = (VectorLayer) layer;
                    
                for (VectorObject vo: vl.getObjectList()) {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(vo);                    
                    this.add(node);
                    
                    if (vo instanceof MultiGeometry) {
                        MultiGeometry mg = (MultiGeometry) vo;
                        
                        for (VectorObject comVO: mg.getComponentObjects()) 
                            node.add(new DefaultMutableTreeNode(comVO));                        
                    }
                }
            }
        }
    }    
    
//    @Override
//    public Enumeration children() {
//        if (layer != null) {
//            if (layer instanceof VectorLayer) {
//                VectorLayer vl = (VectorLayer) layer;
//                
//                return new MapObjectEnumeration(vl.getObjectList());
//            } else {
//                return new MapObjectEnumeration();
//            }
//            
//            
//        } else {
//            return new MapObjectEnumeration();
//        }
//    }
    
    public boolean isSelected() {
        return selected;
    }

    /**
     * Returns if the node is opened or not.
     * 
     * @return 
     */
    public boolean isOpened() {
        return opened;
    }
    
//    @Override
//    public TreeNode getChildAt(int i) {
//        if (layer instanceof VectorLayer) {
//            VectorLayer  vl = (VectorLayer) layer;
//            VectorObject vo = vl.getObjectList().get(i);
//            DefaultMutableTreeNode node = new DefaultMutableTreeNode(vo);
//                    
//            if (vo instanceof MultiGeometry) {
//                MultiGeometry mg = (MultiGeometry) vo;
//
//                for (VectorObject comVO: mg.getComponentObjects()) 
//                    node.add(new DefaultMutableTreeNode(comVO));                        
//            }                    
//                     
//            return node;
//        } else {
//            return new DefaultMutableTreeNode();
//        }
//    }
//    
    /**
     * Returns the Layer associated with this CheckedTreeNode.
     * 
     * @return 
     */
    public Layer getLayer() {
        return this.layer;
    }
    
    @Deprecated
    public int getLayerNumber() {
        return layerNumber;
    }

    public String getText() {
        return  toString();
    }

    @Deprecated
    public void setLayerNumber(int n) {
        this.layerNumber = n;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Sets if the node is opened or not.
     * 
     * @param opened 
     */
    public void setOpened(boolean opened) {
        this.opened = opened;
    }
    
    @Override
    public String toString() {
        return getUserObject().toString();
    }    
    
  
}
