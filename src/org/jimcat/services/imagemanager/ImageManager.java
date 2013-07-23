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

package org.jimcat.services.imagemanager;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import org.jimcat.model.Image;

/**
 * this interfaces describes generally a usefull ImagaManager.
 * 
 * An implementation should support the describte features in a performant way.
 * (e.g. build in image cache)
 * 
 * $Id: ImageManager.java 955 2007-06-19 11:54:02Z 07g1t1u3 $
 * 
 * @author Herbert
 */
public interface ImageManager {

	/**
	 * get the image according to the Image DataBean.
	 * 
	 * This will return the image the fastes possible way. Nevertheless, if the
	 * image is in cache with good quality, it will return it.
	 * 
	 * @param img
	 * @param dimension
	 * @return a buffered image containing the image specified by the image
	 *         object
	 */
	public BufferedImage getImage(Image img, Dimension dimension);

	/**
	 * get the image according to the Image DataBean with at least the given
	 * quality.
	 * 
	 * if there is a better image quality available you will get it.
	 * 
	 * @param img
	 * @param dimension
	 * @param quality
	 * @return a buffered image containing the image specified by the image
	 *         object
	 */
	public BufferedImage getImage(Image img, Dimension dimension, ImageQuality quality);

	/**
	 * get the image according to the Image DataBean with at least the given
	 * quality if it is available. If image is not stored in the cache, it will
	 * not be loaded and null will be returned
	 * 
	 * if there is a better image quality available you will get it.
	 * 
	 * @param img
	 * @param dimension
	 * @param quality
	 * @return - a buffered image if available, null if not
	 */
	public BufferedImage getImageIfAvailable(Image img, Dimension dimension, ImageQuality quality);

	/**
	 * this will initiat an asynchron loading of an image.
	 * 
	 * @param img
	 * @param dimension
	 */
	public void preLoadImage(Image img, Dimension dimension);

	/**
	 * flush given image from cache - replace source image by given image
	 * 
	 * if sourece image is null, every value should be reloaded on access after
	 * flush
	 * 
	 * @param img -
	 *            the image to flush
	 * @param sourceImage
	 *            the new source image
	 */
	public void flushImage(Image img, BufferedImage sourceImage);

	/**
	 * this will shutdown the ImageManager
	 */
	public void shutdown();
}
