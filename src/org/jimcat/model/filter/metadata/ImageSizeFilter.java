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

package org.jimcat.model.filter.metadata;

import org.jimcat.model.Image;
import org.jimcat.model.filter.Filter;

/**
 * A common filter for images sizes replacing privous Minimum / Maximimum Image
 * Size Filter.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class ImageSizeFilter extends Filter {

	/**
	 * types of image size filter
	 * 
	 * $Id$
	 * 
	 * @author Herbert
	 */
	public enum Type {
		SMALLER_THAN, BIGGER_THAN, HEIGHER_THAN, LOWER_THAN, WIDER_THAN, THINER_THAN;
	}

	/**
	 * the type of this image
	 */
	private Type imageSizeCompareMode;

	/**
	 * the width limit
	 */
	private int width;

	/**
	 * the height limit
	 */
	private int height;

	/**
	 * direct constructor for this filter, requesting all information
	 * 
	 * @param type
	 * @param width
	 * @param height
	 */
	public ImageSizeFilter(Type type, int width, int height) {
		super();
		this.imageSizeCompareMode = type;
		this.width = width;
		this.height = height;
	}

	/**
	 * check if the given image matches this filter
	 * 
	 * @see org.jimcat.model.filter.Filter#matches(org.jimcat.model.Image)
	 */
	@Override
	public boolean matches(Image image) {

		// gather information
		int imageHeight = image.getMetadata().getHeight();
		int imageWidth = image.getMetadata().getWidth();

		// check matching depending on type
		switch (imageSizeCompareMode) {
		case SMALLER_THAN:
			return imageHeight <= height && imageWidth <= width;
		case BIGGER_THAN:
			return imageHeight >= height && imageWidth >= width;
		case HEIGHER_THAN:
			return imageHeight >= height;
		case LOWER_THAN:
			return imageHeight <= height;
		case WIDER_THAN:
			return imageWidth >= width;
		case THINER_THAN:
			return imageWidth <= width;
		}

		// happens when type is null
		return false;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the type
	 */
	public Type getImageSizeCompareMode() {
		return imageSizeCompareMode;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Get a new version of this filter which must be a new reference.
	 */
	@Override
	public Filter getCleanVersion() {
		return new ImageSizeFilter(imageSizeCompareMode, width, height);
	}

}
