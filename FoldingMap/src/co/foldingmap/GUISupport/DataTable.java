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
package co.foldingmap.GUISupport;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * An extended version of JTable to allow for some minor changes,
 * such as alternating color rows and a different header color.
 * 
 * @author Alec
 */
public class DataTable extends JTable {
    
    public DataTable(TableModel dm) {
        super(dm);
        
        JTableHeader header = this.getTableHeader();
        final TableCellRenderer tcrOs = this.getTableHeader().getDefaultRenderer();               
        
        header.setDefaultRenderer(new TableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, 
                                                           Object value, 
                                                           boolean isSelected, 
                                                           boolean hasFocus, 
                                                           int row, int column) {
                JLabel lbl;
                
                lbl = (JLabel) tcrOs.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setForeground(Color.WHITE);
                lbl.setBorder(new LineBorder(Color.WHITE, 1, true));
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                lbl.setText(" " + lbl.getText());
                
                if (isSelected) {
                    lbl.setForeground(new Color(153, 153, 153));
                    lbl.setBackground(Color.WHITE);
                } else {
                    lbl.setForeground(Color.WHITE);
                    lbl.setBackground(new Color(153, 153, 153));
                }
                
                return lbl;
            }
        });        
    }
    
    @Override
    public Component prepareRenderer (TableCellRenderer renderer,int Index_row, int Index_col) {
        Component comp = super.prepareRenderer(renderer, Index_row, Index_col);
        
        //even index, selected or not selected
        if (Index_row % 2 == 0) {
            comp.setBackground(new Color(241, 241, 254));
            comp.setForeground(Color.BLACK);
        } else {
            comp.setBackground(Color.WHITE);
            comp.setForeground(Color.BLACK);
        }
        
        return comp;
    }

}
