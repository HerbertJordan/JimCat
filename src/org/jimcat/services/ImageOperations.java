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

package org.jimcat.services;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import org.jimcat.model.Image;
import org.jimcat.model.libraries.ImageLibrary;
import org.jimcat.services.imagemanager.ImageQuality;

/**
 * A central point for all kind of imageoperations.
 * 
 * $Id: ImageOperations.java 934 2007-06-15 08:40:58Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public interface ImageOperations {

	/**
	 * get the up and running instance
	 * 
	 * @return an image library
	 */
	public ImageLibrary getLibrary();

	/**
	 * This methode will provide a visual representation for the given image
	 * (representation will be provided as fast as possible)
	 * 
	 * @param img -
	 *            the internal image representation
	 * @param dim -
	 *            the requested dimension
	 * @return a buffered image representation of the given image
	 */
	public BufferedImage getImageGraphic(Image img, Dimension dim);

	/**
	 * This methode will provide a visual representation for the requested image
	 * with at least given quality
	 * 
	 * @param img -
	 *            the internal image representation
	 * @param dim -
	 *            the requested dimension
	 * @param quality -
	 *            a minimum quality the rendered image should have
	 * @return a buffered image representation of the given image with at least
	 *         the given quality
	 */
	public BufferedImage getImageGraphic(Image img, Dimension dim, ImageQuality quality);

	/**
	 * This methode will provide a visual representation for the the requested
	 * image with at least given quality if it is currently stored in cache
	 * 
	 * @param img -
	 *            the internal image representation
	 * @param dim -
	 *            the requested dimension
	 * @param quality -
	 *            a minimum quality the rendered image should have
	 * @return a bufferd image representation of the image if available, else
	 *         null
	 */
	public BufferedImage getImageGraphicIfAvailable(Image img, Dimension dim, ImageQuality quality);

	/**
	 * cause the image manager to load an image in the background. this methode
	 * will return imediatly.
	 * 
	 * @param img -
	 *            the internal image representation
	 * @param dim -
	 *            the requested dimension
	 */
	public void preloadImage(Image img, Dimension dim);

}
