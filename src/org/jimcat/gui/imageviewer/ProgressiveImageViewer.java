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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.ObjectUtils;
import org.jimcat.model.Image;
import org.jimcat.services.imagemanager.ImageQuality;

/**
 * This image viewer is showing trying to improve image quality by showing fast
 * rendered images followed by images of higher quality.
 * 
 * This viewer can't be used for printing purposes.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class ProgressiveImageViewer extends ImageViewer {

	/**
	 * used to determine which quality should be used for painting
	 */
	private int drawCount = 0;

	/**
	 * the rendered image shown
	 */
	private BufferedImage img = null;

	/**
	 * the executor used for job updates
	 */
	private ExecutorService executor = Executors.newSingleThreadExecutor();

	/**
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {

		// get image
		Image image = getImage();

		// check if this component has an image to show
		if (image == null) {
			return;
		}

		// required size
		Dimension size = getRequiredGraphicSize();

		if (drawCount == 0) {
			// test if image is availabel
			img = control.getImageGraphicIfAvailable(image, size, ImageQuality.getFastest());
			if (img == null) {
				// just show loading image
				img = control.getImageGraphic(image, size, ImageQuality.THUMBNAIL);
				repaintAsynchron(ImageQuality.getFastest());
			} else {
				drawCount++;
				repaintAsynchron(ImageQuality.getBest());
			}
		} else if (drawCount == 1) {
			// draw image in high quality
			repaintAsynchron(ImageQuality.getBest());
		}

		drawImageToCenter(img, g);

		drawCount++;
	}

	/**
	 * Overriden to notice image changes
	 * 
	 * @see org.jimcat.gui.imageviewer.ImageViewer#setImage(org.jimcat.model.Image)
	 */
	@Override
	public void setImage(Image img) {
		// the next draw will be the first draw
		if (!ObjectUtils.equals(img, getImage())) {
			drawCount = 0;
		}
		super.setImage(img);
	}

	/**
	 * setting up a certain quality isn't supported by this type of viewer
	 * 
	 * @see org.jimcat.gui.imageviewer.ImageViewer#setQuality(org.jimcat.services.imagemanager.ImageQuality)
	 */
	@Override
	@SuppressWarnings("unused")
	public void setQuality(ImageQuality quality) {
		throw new UnsupportedOperationException("no exact quality is supported by progressive Image viewer");
	}

	/**
	 * overridden to support image redraw after certain changes
	 * 
	 * @see org.jimcat.gui.imageviewer.ImageViewer#updateImage()
	 */
	@Override
	protected void updateImage() {
	    drawCount = 0;
	    repaint();
	}
	
	/**
	 * This will load the image to show asynchron and call repaint after
	 * rendering has finished
	 * 
	 * @param quality
	 */
	private void repaintAsynchron(ImageQuality quality) {
		executor.execute(new UpdateJob(getImage(), quality));
	}
	
	/**
	 * a Job used for asynchron repainting
	 * 
	 * @author Herbert
	 */
	private class UpdateJob implements Runnable {

		/**
		 * the quality looking for
		 */
		private ImageQuality quality = null;

		private Image image = null;

		/**
		 * create a new update job for given quality
		 * 
		 * @param image
		 *            the image to load
		 * @param quality
		 *            the quality to use
		 */
		public UpdateJob(Image image, ImageQuality quality) {
			this.image = image;
			this.quality = quality;
		}

		/**
		 * does the work
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			if (image != getImage()) {
				return;
			}

			// required size
			Dimension size = getRequiredGraphicSize();
			BufferedImage res = control.getImageGraphic(image, size, quality);

			if (image == getImage()) {
				img = res;
				repaint();
			}
		}
	}

}
