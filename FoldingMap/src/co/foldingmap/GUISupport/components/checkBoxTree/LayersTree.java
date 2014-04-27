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

import co.foldingmap.GUISupport.Updateable;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.VectorObject;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * Used to Display layers in the MainWindow.
 * 
 * @author Alec
 */
public class LayersTree extends    JTree 
                          implements MouseListener, 
                                     TreeSelectionListener {
    
    private ArrayList<Updateable> updates;
    private boolean               changedSelection;
    private CheckBoxNodeRenderer  renderer;

    public LayersTree(TreeModel newModel)   {
        super(newModel);

        renderer         = new CheckBoxNodeRenderer();
        changedSelection = false;
        updates          = new ArrayList<Updateable>();
        
        this.setCellRenderer(renderer);
        super.addTreeSelectionListener(this);
        this.addMouseListener(this);
    }

    /**
     * Constructor for objects of class JCheckboxTree
     */
    public LayersTree(TreeNode root) {
        super(root);

        renderer         = new CheckBoxNodeRenderer();
        changedSelection = false;
        updates          = new ArrayList<Updateable>();
        
        this.setCellRenderer(renderer);
        super.addTreeSelectionListener(this);
        this.addMouseListener(this);
    }

    /**
     * Adds an Updateable to this object.
     * These objects will be updated upon a selection change.
     * 
     * @param updateable 
     */
    public void addUpdateable(Updateable updateable) {
        updates.add(updateable);
    }    
    
    /**
     * Fires off the update method of all the Updatables for htis object.
     */
    private void fireUpdates() {
        for (Updateable u: updates) {
            u.update();
        }
    }
    
    /**
     * Returns the UserObject for the selected Tree Node.
     * Returns null is no object is selected or there is another error.
     * 
     * @return 
     */
    public Object getSelectedNodeObject() {
        Object    returnObject, selectedObject;
        Object[]  pathObjects;
        TreePath  tPath;        
    
        tPath = this.getSelectionPath();
        returnObject = null;
        
        if (tPath != null) {
            pathObjects = tPath.getPath();

            if (pathObjects != null) {
                selectedObject = pathObjects[pathObjects.length - 1];

                if (selectedObject instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) selectedObject;
                    returnObject = treeNode.getUserObject();                    
                }
            }        
        }
        
        return returnObject;
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        Object    selectedObject;
        Object[]  pathObjects;
        TreePath  tPath;

        if (e.getButton() == MouseEvent.BUTTON1) {
            tPath = this.getSelectionPath();

            if (tPath != null) {
                pathObjects = tPath.getPath();

                if (pathObjects != null) {
                    selectedObject = pathObjects[pathObjects.length - 1];

                    if (selectedObject instanceof CheckedTreeNode) {
                        CheckedTreeNode cTreeNode = (CheckedTreeNode) selectedObject;
                                               
                        if (e.getX() < 16) {
                            cTreeNode.setOpened(!cTreeNode.isOpened());
                            
                            if (cTreeNode.isOpened()) {
                                this.expandPath(tPath);
                            } else {
                                this.collapsePath(tPath);
                            }
                        } else {
                            if (e.getClickCount() == 1) {
                                if (changedSelection == false) {
                                    cTreeNode.setSelected(!cTreeNode.isSelected());
                                }
                            } else if (e.getClickCount() == 2) {
                                cTreeNode.setSelected(!cTreeNode.isSelected());
                            }
                        }
                    } else  if (selectedObject instanceof DefaultMutableTreeNode) {
                        if (this.getSelectedNodeObject() instanceof VectorObject) {
                            VectorObject vo = (VectorObject) this.getSelectedNodeObject();
                            VectorLayer  vl = (VectorLayer) vo.getParentLayer();
                            vl.getParentMap().deselectObjects();
                            vl.getParentMap().setSelected(vo);
                        }
                    }
                }
                
                fireUpdates();
            }

            this.changedSelection = false;
        }

        this.repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        changedSelection = true;
    }    
}
