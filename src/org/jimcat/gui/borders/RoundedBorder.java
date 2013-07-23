/*
 *  This file is part of JimCat.
 *
 *  JimCat is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation version 2.
 *
 *  JimCat is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with JimCat; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.jimcat.gui.borders;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.border.Border;

/**
 * An implementation of the Border interface drawing a rounded border defined by a given radius.
 * 
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class RoundedBorder implements Border {

	/**
	 * the radius of the border edges
	 */
	private int radius;

	/**
	 * create a new rounded border using given radius in edges
	 * 
	 * @param radius
	 */
	public RoundedBorder(int radius) {
		this.radius = radius;
	}

	/**
	 * get the size of this border
	 * 
	 * @see javax.swing.border.Border#getBorderInsets(java.awt.Component)
	 */
	@SuppressWarnings("unused")
	public Insets getBorderInsets(Component c) {
		return new Insets(radius, radius, radius, radius);
	}

	/**
	 * this border is not opaque
	 * 
	 * @see javax.swing.border.Border#isBorderOpaque()
	 */
	public boolean isBorderOpaque() {
		return false;
	}

	/**
	 * this methode will paint the border
	 * 
	 * @see javax.swing.border.Border#paintBorder(java.awt.Component,
	 *      java.awt.Graphics, int, int, int, int)
	 */
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		int diameter = radius << 1;
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Color color = c.getBackground();
		g2.setColor(color);
		g2.fillRoundRect(x, y, width - 1, height - 1, diameter, diameter);
		// strengthen the boarder
		g2.drawRoundRect(x, y, width - 1, height - 1, diameter, diameter);
	}

}
