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

package org.jimcat.services.operations;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import org.jimcat.model.Image;
import org.jimcat.model.libraries.ImageLibrary;
import org.jimcat.services.ImageOperations;
import org.jimcat.services.ServiceLocator;
import org.jimcat.services.imagemanager.ImageManager;
import org.jimcat.services.imagemanager.ImageQuality;

/**
 * A facade for all kind of image based operations.
 * 
 * $Id: ImageOperationsImpl.java 934 2007-06-15 08:40:58Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public class ImageOperationsImpl implements ImageOperations {

	/**
	 * used imageManager
	 */
	private ImageManager manager = ServiceLocator.getImageManager();

	/**
	 * 
	 * gets a graphic to display a given image as fast as possible
	 * 
	 * @see org.jimcat.services.ImageOperations#getImageGraphic(org.jimcat.model.Image,
	 *      java.awt.Dimension)
	 */
	public BufferedImage getImageGraphic(Image img, Dimension dim) {
		return manager.getImage(img, dim);
	}

	/**
	 * gets a graphic to display a given image with a minimum quality
	 * 
	 * @see org.jimcat.services.ImageOperations#getImageGraphic(org.jimcat.model.Image,
	 *      java.awt.Dimension, org.jimcat.services.imagemanager.ImageQuality)
	 */
	public BufferedImage getImageGraphic(Image img, Dimension dim, ImageQuality quality) {
		return manager.getImage(img, dim, quality);
	}

	/**
	 * get a graphic to display a given image with a minimum quality if
	 * available in cache
	 * 
	 * @param img -
	 *            the image to show
	 * @param dim -
	 *            the dimension requested
	 * @param quality -
	 *            the image quality requested
	 * @return - a BufferedImage containing the image or null if not stored in
	 *         cache
	 */
	public BufferedImage getImageGraphicIfAvailable(Image img, Dimension dim, ImageQuality quality) {
		return manager.getImageIfAvailable(img, dim, quality);
	}

	/**
	 * preload the given image
	 * 
	 * @see org.jimcat.services.ImageOperations#preloadImage(org.jimcat.model.Image,
	 *      java.awt.Dimension)
	 */
	public void preloadImage(Image img, Dimension dim) {
		manager.preLoadImage(img, dim);
	}

	/**
	 * get singelton instance of image library
	 * 
	 * @see org.jimcat.services.ImageOperations#getLibrary()
	 */
	public ImageLibrary getLibrary() {
		return ImageLibrary.getInstance();
	}

}
