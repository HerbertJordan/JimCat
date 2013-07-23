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

package org.jimcat.gui.imageviewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import org.jdesktop.swingx.graphics.ShadowRenderer;
import org.jimcat.model.Image;

/**
 * An image viewer drawing a frame around the shown image.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class FramedImageViewer extends ImageViewer {

	/**
	 * the default shadow width
	 */
	private static final int DEFAULT_SHADOW_WIDTH = 3;

	/**
	 * the default amoung of shadow displacement
	 */
	private static final Point DEFAULT_SHADOW_DISPLACEMENT = new Point(3, 3);

	/**
	 * shadow color used by default
	 */
	private static final Color DEFAULT_SHADOW_COLOR = Color.DARK_GRAY;

	/**
	 * the default shadow opacity
	 */
	private static final float DEFAULT_SHADOW_OPACITY = 0.25f;

	/**
	 * the shadow renderer for shadow effect
	 */
	private ShadowRenderer shadowRenderer;

	/**
	 * the frame width
	 */
	private int frameWidth = 0;

	/**
	 * the color of the Frame
	 */
	private Color frameColor = Color.WHITE;

	/**
	 * the shadow color to use
	 */
	private Color shadowColor = DEFAULT_SHADOW_COLOR;

	/**
	 * the shadow displacement
	 */
	private Point shadowDisplacement = DEFAULT_SHADOW_DISPLACEMENT;

	/**
	 * the shadow width
	 */
	private int shadowWidth = DEFAULT_SHADOW_WIDTH;

	/**
	 * the shadow opacity
	 */
	private float shadowOpacity = DEFAULT_SHADOW_OPACITY;

	/**
	 * the double buffer image
	 */
	private java.awt.Image bufferedImage;

	/**
	 * default constructor
	 */
	public FramedImageViewer() {
		updateShadowRenderer();
		
		// add listener for resize events
		addComponentListener(new ComponentAdapter() {
			/**
			 * react on a resize
			 * @see java.awt.event.ComponentAdapter#componentResized(java.awt.event.ComponentEvent)
			 */
			@Override
			@SuppressWarnings("unused")
			public void componentResized(ComponentEvent e) {
			    bufferedImage = null;
			}
		});
	}

	/**
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		if (bufferedImage == null) {
			
			// get image from super class
			Image image = getImage();

			// check if this component has an image to show
			if (image == null) {
				return;
			}

			// required size
			BufferedImage img = getImageControl().getImageGraphic(image, getRequiredGraphicSize(), getQuality());

			// check if there is anything to draw 
			if (img==null) {
				// replace with unknown image ...
				return;
			}
			
			// draw frame
			int width = getFrameWidth();
			Dimension size = getSize();
			int x = (size.width - img.getWidth() - width) / 2 - 1;
			int y = (size.height - img.getHeight() - width) / 2 - 1;
			int frameWidht = img.getWidth() + width * 2;
			int frameHeight = img.getHeight() + width * 2;

			BufferedImage frame = new BufferedImage(frameWidht, frameHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = frame.createGraphics();
			g2.setColor(frameColor);
			g2.fillRect(0, 0, frameWidht, frameHeight);
			g2.dispose();


			// assemble image
			bufferedImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
			
			g2 = (Graphics2D)bufferedImage.getGraphics();
			
			// draw shadow
			g2.translate(shadowDisplacement.x, shadowDisplacement.y);
			drawImageToCenter(shadowRenderer.createShadow(frame), g2);
			g2.translate(-shadowDisplacement.x, -shadowDisplacement.y);

			// draw frame
			g2.setColor(frameColor);
			g2.fillRect(x, y, frameWidht, frameHeight);

			// draw image
			drawImageToCenter(img, g2);
		}
		g.drawImage(bufferedImage, 0, 0, this);
	}

	/**
	 * invalidate buffered image
	 * 
	 * @see org.jimcat.gui.imageviewer.ImageViewer#updateImage()
	 */
	@Override
	protected void updateImage() {
	    bufferedImage = null;
	    repaint();
	}
	
	/**
	 * reduces required graphic size by frame width
	 * 
	 * @see org.jimcat.gui.imageviewer.ImageViewer#getRequiredGraphicSize()
	 */
	@Override
	public Dimension getRequiredGraphicSize() {
		Dimension size = getSize();
		int height = size.height - (frameWidth + shadowWidth + Math.abs(shadowDisplacement.y)) * 2;
		int width = size.width - (frameWidth + shadowWidth + Math.abs(shadowDisplacement.x)) * 2;
		return new Dimension(width, height);
	}
	
	/**
	 * use given size + frame width as new preferred size
	 * 
	 * @see org.jimcat.gui.imageviewer.ImageViewer#setGraphicSize(java.awt.Dimension)
	 */
	@Override
	public void setGraphicSize(Dimension size) {
		int height = size.height + (frameWidth + shadowWidth + Math.abs(shadowDisplacement.y)) * 2;
		int widht = size.width + (frameWidth + shadowWidth + Math.abs(shadowDisplacement.x)) * 2;
		setPreferredSize(new Dimension(widht, height));
		bufferedImage = null;
	}
	
	/**
	 * Exchange image
	 * 
	 * @see org.jimcat.gui.imageviewer.ImageViewer#setImage(org.jimcat.model.Image)
	 */
	@Override
	public void setImage(Image img) {
	    if (img!=getImage()) {
	    	super.setImage(img);
	    	bufferedImage = null;
	    }
	}

	/**
	 * creates new shadowrendere from current setup
	 */
	private void updateShadowRenderer() {
		shadowRenderer = new ShadowRenderer(shadowWidth, shadowOpacity, shadowColor);
		if (isVisible()) {
			repaint();
		}
	}

	/**
	 * @return the frameWidth
	 */
	public int getFrameWidth() {
		return frameWidth;
	}

	/**
	 * @param frameWidth
	 *            the frameWidth to set
	 */
	public void setFrameWidth(int frameWidth) {
		this.frameWidth = frameWidth;
	}

	/**
	 * @return the frameColor
	 */
	public Color getFrameColor() {
		return frameColor;
	}

	/**
	 * @param frameColor
	 *            the frameColor to set
	 */
	public void setFrameColor(Color frameColor) {
		this.frameColor = frameColor;
	}

	/**
	 * @return the shadowColor
	 */
	public Color getShadowColor() {
		return shadowColor;
	}

	/**
	 * @param shadowColor
	 *            the shadowColor to set
	 */
	public void setShadowColor(Color shadowColor) {
		this.shadowColor = shadowColor;
		updateShadowRenderer();
	}

	/**
	 * @return the shadowDisplacement
	 */
	public Point getShadowDisplacement() {
		return shadowDisplacement;
	}

	/**
	 * @param shadowDisplacement
	 *            the shadowDisplacement to set
	 */
	public void setShadowDisplacement(Point shadowDisplacement) {
		this.shadowDisplacement = shadowDisplacement;
		updateShadowRenderer();
	}

	/**
	 * @return the shadowWidth
	 */
	public int getShadowWidth() {
		return shadowWidth;
	}

	/**
	 * @param shadowWidth
	 *            the shadowWidth to set
	 */
	public void setShadowWidth(int shadowWidth) {
		this.shadowWidth = shadowWidth;
		updateShadowRenderer();
	}

}
