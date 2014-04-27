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

import co.foldingmap.GUISupport.Updateable;
import co.foldingmap.actions.Action;
import co.foldingmap.actions.Actions;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;



/**
 * A JButton that when clicked a popup menu appears.
 * 
 * @author Alec
 */
public class PopupMenuButton extends JButton implements ActionListener, MouseListener {
    public static final int    LEFT_CLICK      = MouseEvent.BUTTON1;
    public static final int    RIGHT_CLICK     = MouseEvent.BUTTON3;
    public static final String MENU_ACTIVATION = "Menu Activation";
    
    protected Actions                   actions;
    private   ArrayList<MenuActionPair> menuActions;
    protected ArrayList<Updateable>     objectUpdates;
    protected int                       menuActivation;
    protected long                      lastActionTime;
    protected JPopupMenu                popupMenu;
    
    /**
     * 
     * @param icon
     *          The Icon to be displayed in the button.
     * @param actions
     *          The actions class, used to execute actions for the menu items.
     * @param menuActivation 
     *          Specifies which mouse button will activate the menu.
     */
    public PopupMenuButton(Icon icon, Actions actions, int menuActivation) {
        super(icon);
        
        this.actions        = actions;
        this.menuActivation = menuActivation;
        this.objectUpdates  = new ArrayList<Updateable>();
        init();
    }
    
    /**
     * Used to trigger the actions for each MenuItem.
     * 
     * @param ae 
     */
    @Override
    public void actionPerformed(ActionEvent ae) {

        //Prevent duplicate clicks
        if (!(lastActionTime == ae.getWhen())) {
            for (MenuActionPair map: menuActions) {
                if (map.menuItem == ae.getSource()) {                    
                    actions.performAction(map.action);
                    lastActionTime = ae.getWhen();
                    break;
                }
            }
        } 
        
        //call updates
        for (Updateable u: objectUpdates) u.update();
    }    
    
    /**
     * Adds a JMenuItem to the popup menu.
     * 
     * @param menuItem
     * @param action 
     */
    public void add(JMenuItem menuItem, Action action) {
        menuActions.add(new MenuActionPair(menuItem, action));
        popupMenu.add(menuItem);
        menuItem.addActionListener(this);
    }
    
    /**
     * Override for the parent Keylistener, adds the listener to sub objects 
     * as well.
     * 
     * @param kl 
     */
    @Override
    public void addKeyListener(KeyListener kl) {
        super.addKeyListener(kl);
        popupMenu.addKeyListener(kl);
    }
    
    /**
     * Adds an object to call an update on after a MenuItem has been selected 
     * and its action run.
     * 
     * @param update 
     */
    public void addUpdate(Updateable update) {
        this.objectUpdates.add(update);
    }
    
    public void hidePopup() {
        popupMenu.setVisible(false);
    }
    
    private void init() {
        menuActions    = new ArrayList<MenuActionPair>();
        popupMenu      = new JPopupMenu();
        lastActionTime = 0;
        
        this.addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        int x = 0;
        int y = 0;
        
        hidePopup();
        
        if (me.getButton() == this.menuActivation) {
            this.fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, MENU_ACTIVATION));
             
            //show popup
            if (this.getParent() instanceof JToolBar) {
                JToolBar parent = (JToolBar) this.getParent();
                
                if (parent.getOrientation() == SwingConstants.HORIZONTAL) {
                    x = 0;
                    y = parent.getY() + parent.getHeight() - 10;
                } else if (parent.getOrientation() == SwingConstants.VERTICAL) {
                    x = this.getX() + parent.getWidth();
                    y = me.getY() - 16;
                }                                               
            } else {
                x = this.getX() - 150;
                y = this.getY() + 15;          
            }
            
            popupMenu.show(this, x, y);
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {
        
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        
    }

    @Override
    public void mouseExited(MouseEvent me) {
        
    }
    
    public void removeAllMenuItems() {
        menuActions.clear();
        popupMenu.removeAll();
    }
    
    public void setActions(Actions actions) {
        this.actions = actions;
    }
    
}
class MenuActionPair {
    JMenuItem menuItem;
    Action    action;
    
    public MenuActionPair(JMenuItem menuItem, Action action) {
        this.menuItem = menuItem;
        this.action   = action;
    }
}
