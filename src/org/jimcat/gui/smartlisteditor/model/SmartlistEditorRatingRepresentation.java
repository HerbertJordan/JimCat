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

package org.jimcat.gui.smartlisteditor.model;

import org.jimcat.model.ImageRating;

/**
 * A rating representaion manager used by RatingFilterEditor.
 * 
 * $Id$
 * 
 * @author Christoph
 */
public class SmartlistEditorRatingRepresentation {

	private ImageRating rating;

	private String name;

	/**
	 * create a new Instance representing given ImageRating
	 * 
	 * @param rating
	 */
	public SmartlistEditorRatingRepresentation(ImageRating rating) {
		this.rating = rating;

		switch (rating) {
		case NONE:
			name = "no";
			break;
		case ONE:
			name = "one";
			break;
		case TWO:
			name = "two";
			break;
		case THREE:
			name = "three";
			break;
		case FOUR:
			name = "four";
			break;
		case FIVE:
			name = "five";
			break;
		default:
			throw new RuntimeException("Extend me for Rating " + rating);
		}
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the rating
	 */
	public ImageRating getRating() {
		return rating;
	}

	/**
	 * get representing string representation
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}
}
