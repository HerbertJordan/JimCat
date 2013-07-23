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

package org.jimcat.model.filter;

import org.jimcat.model.Image;
import org.jimcat.model.ImageRating;

/**
 * A filter for to filter images by thire rating.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class RatingFilter extends Filter {

	/**
	 * an enumeration of supported types
	 */
	public enum Type {
		AT_LEAST, UP_TO, EXACT;
	}

	/**
	 * the type of this filter
	 */
	private Type ratingCompareMode;

	/**
	 * the determining rating
	 */
	private ImageRating rating;

	/**
	 * create a new filter using given values
	 * 
	 * @param type
	 * @param rating
	 */
	public RatingFilter(Type type, ImageRating rating) {
		this.ratingCompareMode = type;
		this.rating = rating;
	}

	/**
	 * match images - determined by rating
	 * 
	 * @see org.jimcat.model.filter.Filter#matches(org.jimcat.model.Image)
	 */
	@Override
	public boolean matches(Image image) {

		// gather information
		ImageRating comp = image.getRating();

		// rating must be not null
		if (comp == null) {
			return false;
		}

		// compare
		switch (ratingCompareMode) {
		case AT_LEAST:
			return rating.ordinal() <= comp.ordinal();
		case UP_TO:
			return rating.ordinal() > comp.ordinal();
		case EXACT:
			return rating.ordinal() == comp.ordinal();
		}

		// default
		return false;
	}

	/**
	 * @return the rating
	 */
	public ImageRating getRating() {
		return rating;
	}

	/**
	 * @return the ratingCompareMode
	 */
	public Type getRatingCompareMode() {
		return ratingCompareMode;
	}

	/**
	 * Get a new version of this filter which must be a new reference.
	 */
	@Override
	public Filter getCleanVersion() {
		return new RatingFilter(ratingCompareMode, rating);
	}

}
