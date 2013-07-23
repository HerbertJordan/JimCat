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

package org.jimcat.gui.histogram;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

/**
 * The two sliders used inside of the histogram diagram
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class HistogramSlider extends JComponent {

	/**
	 * the dimension of this component
	 */
	public static final Dimension SIZE = new Dimension(9, 22);

	/**
	 * color triangles are filled with
	 */
	private static final Color FILL_COLOR = new Color(200, 200, 200);

	/**
	 * the border color of the triangles
	 */
	private static final Color BORDER_COLOR = Color.GRAY;

	/**
	 * the buffered image containing visualisation
	 */
	private static final BufferedImage buffer;

	static {
		// create triangle polygones
		int heigh = SIZE.height - 1;
		int width = SIZE.width - 1;

		Polygon TOP = new Polygon(new int[] { 0, width / 2, width }, new int[] { 0, width / 2, 0 }, 3);
		Polygon BUTTON = new Polygon(new int[] { 0, width, width / 2 },
		        new int[] { heigh, heigh, heigh - (width / 2) }, 3);

		// draw visualization
		buffer = new BufferedImage(9, 22, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = buffer.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(FILL_COLOR);
		g.fillPolygon(TOP);
		g.fillPolygon(BUTTON);

		g.setColor(BORDER_COLOR);
		g.drawPolygon(TOP);
		g.drawPolygon(BUTTON);
		g.dispose();
	}

	/**
	 * Two triangles marking a Position
	 */
	public HistogramSlider() {
		setOpaque(false);
		setVisible(true);

		setMinimumSize(SIZE);
		setMaximumSize(SIZE);
		setPreferredSize(SIZE);
		setSize(SIZE);
	}

	/**
	 * draws required Triangles
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(buffer, 0, 0, null);
	}

}
