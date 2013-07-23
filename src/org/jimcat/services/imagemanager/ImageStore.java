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

import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;

/**
 * This is a container for several images stored in various ImageQualities.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class ImageStore {

	/**
	 * the amoung of quality steps
	 */
	private static final int ANZ_QUALITY_STEPS = ImageQuality.values().length;

	/**
	 * the internal images store structure
	 */
	private SoftReference<BufferedImage> images[];

	/**
	 * creates a new, empty image store
	 */
	@SuppressWarnings("unchecked")
	public ImageStore() {
		images = new SoftReference[ANZ_QUALITY_STEPS];
	}

	/**
	 * add a new image with the given quality to this store
	 * 
	 * @param image -
	 *            the image to store
	 * @param quality -
	 *            the quality it has
	 */
	public void addImage(BufferedImage image, ImageQuality quality) {
		// get index within internal list
		int index = quality.ordinal();
		// create new soft reference
		images[index] = new SoftReference<BufferedImage>(image);

		// removed stored images with "weaker" quality
		for (int i = index + 1; i < images.length; i++) {
			images[i] = null;
		}
	}

	/**
	 * Get an imager out of this container with at least the given Quality. It
	 * may be better. If such an image isn't available, null will be returned.
	 * 
	 * @param quality -
	 *            requested quality.
	 * @return a buffered image with at least the given Quality
	 */
	public BufferedImage getImage(ImageQuality quality) {
		// check if there is a better quality (from best to worst)
		for (int i = 0; i <= quality.ordinal(); i++) {
			// get image
			SoftReference<BufferedImage> current = images[i];
			// if it's not empty, we have a hit
			if (current != null && current.get() != null) {
				return current.get();
			}
		}
		// so, there was no entry for this
		return null;
	}

	/**
	 * check if this store still containes some images
	 * 
	 * @return true if the store is empty
	 */
	public boolean isEmpty() {
		// check the list if its empty
		for (int i = 0; i < images.length; i++) {
			// if there is an image somewhere
			if (images[i] != null && images[i].get() != null) {
				// we have found one, its not empty
				return false;
			}
			// internal cleanup
			images[i] = null;
		}
		// so, the store is empty
		return true;
	}

}
