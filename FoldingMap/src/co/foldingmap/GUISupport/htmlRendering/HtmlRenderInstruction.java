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
package co.foldingmap.GUISupport.htmlRendering;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * Interface for a render instruction
 * 
 * @author Alec
 */
public interface HtmlRenderInstruction {
    
    public void        draw(Graphics2D g2);
    public Rectangle2D getBounds(Graphics2D g2);
}
