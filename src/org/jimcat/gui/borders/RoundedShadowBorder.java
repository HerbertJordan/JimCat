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
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.image.BufferedImage;

import org.apache.commons.lang.ObjectUtils;
import org.jdesktop.swingx.graphics.ShadowRenderer;

/**
 * A common border with rounded edges and shadow
 * 
 * $Id$
 * 
 * @author csaf7445
 */
public class RoundedShadowBorder extends RoundedBorder {

	/**
	 * the default shadow width
	 */
	private static final int SHADOW_WIDTH = 3;

	/**
	 * the default amoung of shadow displacement
	 */
	private static final Point SHADOW_DISPLACEMENT = new Point(3, 3);

	/**
	 * shadow color used by default
	 */
	private static final Color SHADOW_COLOR = Color.DARK_GRAY;

	/**
	 * the default shadow opacity
	 */
	private static final float SHADOW_OPACITY = 0.25f;

	/**
	 * the shadow renderer used to generate shadows
	 */
	private ShadowRenderer shadowRenderer;

	/**
	 * image used for double buffering
	 */
	private Image imageBuffer;

	/**
	 * the last width of rendered border
	 */
	private int lastWidth;

	/**
	 * the last height of rendered border
	 */
	private int lastHeight;
	
	/**
	 * the last color used for the border
	 */
	private Color lastColor;

	/**
	 * create new Border with given parameter
	 * 
	 * @param radius
	 */
	public RoundedShadowBorder(int radius) {
		super(radius);

		shadowRenderer = new ShadowRenderer(SHADOW_WIDTH, SHADOW_OPACITY, SHADOW_COLOR);
	}

	/**
	 * 
	 * get border dimension depending on shadow configuration
	 * 
	 * @see org.jimcat.gui.borders.RoundedBorder#getBorderInsets(java.awt.Component)
	 */
	@Override
	public Insets getBorderInsets(Component c) {
		Insets res = super.getBorderInsets(c);

		res.top += Math.max(SHADOW_WIDTH - SHADOW_DISPLACEMENT.y, 0);
		res.bottom += Math.max(SHADOW_WIDTH + SHADOW_DISPLACEMENT.y, 0);

		res.left += Math.max(SHADOW_WIDTH - SHADOW_DISPLACEMENT.x, 0);
		res.right += Math.max(SHADOW_WIDTH + SHADOW_DISPLACEMENT.x, 0);

		return res;
	}

	/**
	 * 
	 * paint this type of border
	 * 
	 * @see org.jimcat.gui.borders.RoundedBorder#paintBorder(java.awt.Component,
	 *      java.awt.Graphics, int, int, int, int)
	 */
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		
		Color borderColor = c.getBackground();
		
		// check if old image is still useable
		if (imageBuffer == null || width != lastWidth || height != lastHeight || !ObjectUtils.equals(borderColor, lastColor)) {
			// update image buffer
			imageBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			lastHeight = height;
			lastWidth = width;
			lastColor = borderColor;
			
			int shadow_width = SHADOW_WIDTH * 2;

			int xb = Math.max(x + SHADOW_WIDTH - SHADOW_DISPLACEMENT.x, x);
			int yb = Math.max(y + SHADOW_WIDTH - SHADOW_DISPLACEMENT.y, y);

			int xs = Math.max(x, x + SHADOW_DISPLACEMENT.x);
			int ys = Math.max(y, y + SHADOW_DISPLACEMENT.y);

			BufferedImage border = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics imgG = border.createGraphics();
			super.paintBorder(c, imgG, xb, yb, width - shadow_width - Math.abs(SHADOW_DISPLACEMENT.x), height
			        - shadow_width - Math.abs(SHADOW_DISPLACEMENT.y));
			imgG.dispose();

			BufferedImage shadow = shadowRenderer.createShadow(border);
			
			Graphics2D g2 = (Graphics2D)imageBuffer.getGraphics();
			g2.drawImage(shadow, xs, ys, null);
			g2.drawImage(border, xb, yb, null);
			g2.dispose();
		}
		g.drawImage(imageBuffer, x, y, null);
	}
}
