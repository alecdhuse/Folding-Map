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

import java.awt.BorderLayout;
import javax.swing.JFrame;

/**
 *
 * @author Alec
 */
public class Test extends JFrame {
    PhotoExtendedOptionsPanel photoPanel;
    
    public Test() {
        try {
            //photoPanel = new PhotoExtendedOptionsPanel(new URL("http://www.theodora.com/maps/new9/time_zones_4.jpg"));
            photoPanel = new PhotoExtendedOptionsPanel("/Users/Alec/time_zones.jpg");
            this.setLayout(new BorderLayout());
            this.add(photoPanel, BorderLayout.CENTER);
            
            this.setSize(300, 300);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setVisible(true);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
    
    public static void main(String[] args) {
        Test test = new Test();
    }
}
