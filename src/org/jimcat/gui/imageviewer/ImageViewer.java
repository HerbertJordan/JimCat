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

import javax.swing.JComponent;

import org.jimcat.gui.ImageControl;
import org.jimcat.gui.SwingClient;
import org.jimcat.model.Image;
import org.jimcat.model.notification.BeanChangeEvent;
import org.jimcat.model.notification.BeanListener;
import org.jimcat.model.notification.BeanProperty;
import org.jimcat.services.imagemanager.ImageQuality;

/**
 * Used to display images.
 * 
 * $Id$
 * 
 * @author csaf7445
 */
public class ImageViewer extends JComponent implements BeanListener<Image> {

	/**
	 * a reference to the installed image control
	 */
	protected static ImageControl control = SwingClient.getInstance().getImageControl();

	/**
	 * currently displayed image
	 */
	private Image image;

	/**
	 * the quality of images this viewer should use
	 */
	private ImageQuality quality;
	
	/**
	 * default constructor - using fastes ImageQuality
	 */
	public ImageViewer() {
		this(ImageQuality.getFastest());
	}

	/**
	 * a constructor to setup image quality to use
	 * @param quality
	 */
	public ImageViewer(ImageQuality quality) {
		// init members
		this.quality = quality;
		
		// enable buffering
		setDoubleBuffered(true);
	}
	
	/**
	 * @return the image
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * @param img
	 *            the image to set
	 */
	public void setImage(Image img) {
		// is there a change?
		if (img == image) {
			return;
		}
		// remove old listener
		if (image != null) {
			image.removeListener(this);
		}
		// change element
		this.image = img;
		// register to image
		if (image != null) {
			image.addListener(this);
		}

		// update image
		repaint();
	}

	/**
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {

		// check if this component has an image to show
		if (image == null) {
			return;
		}

		// required size
		Dimension size = getRequiredGraphicSize();

		BufferedImage img = control.getImageGraphic(image, size, quality);

		drawImageToCenter(img, g);
	}

	/**
	 * used to determine the size of the image required by this component.
	 * 
	 * @return the required size as Dimension
	 */
	public Dimension getRequiredGraphicSize() {
		// the graphic should fill the whole component
		return getSize();
	}

	/**
	 * this will set the size of the dsiplayed image
	 * 
	 * a request to getPreferredSize will give you a dimension witch will be
	 * supporting the given size. => use a Layoutmanager which is supporting
	 * preferred sizes. (e.g. FlowLayout)
	 * 
	 * @param size
	 */
	public void setGraphicSize(Dimension size) {
		setPreferredSize(size);
	}

	/**
	 * repaints on thumbnail changes
	 * 
	 * @see org.jimcat.model.notification.BeanListener#beanPropertyChanged(org.jimcat.model.notification.BeanChangeEvent)
	 */
	public void beanPropertyChanged(BeanChangeEvent<Image> event) {
		// check source
		Image img = event.getSource();
		if (img != image) {
			img.removeListener(this);
			return;
		}

		// update image if there was a change
		if (event.getProperty() == BeanProperty.IMAGE_THUMBNAIL) {
			updateImage();
		}
		
		if (event.getProperty() == BeanProperty.IMAGE_ROTATION) {
			updateImage();
		}
	}

	/**
	 * draws the image to the center of this component
	 * 
	 * @param img
	 * @param g
	 */
	protected void drawImageToCenter(BufferedImage img, Graphics g) {

		if (img == null) {
			return; // FIXME
		}

		// center image
		Dimension size = getSize();
		int x = (size.width - img.getWidth()) / 2;
		int y = (size.height - img.getHeight()) / 2;

		g.drawImage(img, x, y, this);
	}

	/**
	 * private method used by the basic image viewer to update the shown
	 * image. It may be overridden by advanced image viewers.
	 */
	protected void updateImage() {
		repaint();
	}
	
	/**
	 * get a link to the image control of the service layer
	 * 
	 * @return the ImageControl
	 */
	protected static ImageControl getImageControl() {
		return control;
	}

	/**
     * @return the quality
     */
    public ImageQuality getQuality() {
    	return quality;
    }

	/**
     * @param quality the quality to set
     */
    public void setQuality(ImageQuality quality) {
    	this.quality = quality;
    }
	
	
}
