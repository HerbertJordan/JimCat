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
 * Used to filter images by the total number of pixels.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class MegaPixelFilter extends Filter {

	/**
	 * types of size comparation
	 * 
	 * $Id$
	 * 
	 * @author Herbert
	 */
	public enum Type {
		BIGGER_OR_EQUAL, SMALLER_THEN;
	}

	/**
	 * the type of comparation
	 */
	private Type megaPixelCompareType;

	/**
	 * the total amoung of megapixes to compare with
	 */
	private float megaPixel;

	/**
	 * directo constructor
	 * 
	 * @param type -
	 *            the type (BIGGER_OR_EQUAL, SMALLER_THEN)
	 * @param megaPixel -
	 *            the size to check
	 * @throws IllegalArgumentException
	 *             if type is nul
	 */
	public MegaPixelFilter(Type type, float megaPixel) throws IllegalArgumentException {
		if (type == null) {
			throw new IllegalArgumentException("type must not be null");
		}
		this.megaPixelCompareType = type;
		this.megaPixel = megaPixel;
	}

	/**
	 * test if given image maches this filter
	 * 
	 * @see org.jimcat.model.filter.Filter#matches(org.jimcat.model.Image)
	 */
	@Override
	public boolean matches(Image image) {
		// get image size
		int width = image.getMetadata().getWidth();
		int height = image.getMetadata().getHeight();
		float mp = (width * height) / 1000000f;

		// compare
		switch (megaPixelCompareType) {
		case BIGGER_OR_EQUAL:
			return mp >= megaPixel;
		case SMALLER_THEN:
			return mp <= megaPixel;
		}
		// can only happen if type is null
		return false;
	}

	/**
	 * @return the megaPixel
	 */
	public float getMegaPixel() {
		return megaPixel;
	}

	/**
	 * @return the megaPixelCompareType
	 */
	public Type getMegaPixelCompareType() {
		return megaPixelCompareType;
	}

	/**
	 * Get a new version of this filter which must be a new reference.
	 */
	@Override
	public Filter getCleanVersion() {
		return new MegaPixelFilter(megaPixelCompareType, megaPixel);
	}

}
