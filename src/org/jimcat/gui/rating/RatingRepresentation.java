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

package org.jimcat.gui.rating;

import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import org.jimcat.model.ImageRating;


/**
 * A helper class providing images for ratings
 * 
 * $Id: RatingRepresentation.java 459 2007-05-01 22:26:46Z 07g1t1u3 $
 * 
 * @author Herbert
 */
public final class RatingRepresentation {

	/**
	 * configures shown images
	 */
	private static final Map<ImageRating, ImageIcon> REPRESENTATION_MAP = new HashMap<ImageRating, ImageIcon>();

	static {
		Class myClass = RatingRepresentation.class;

		ImageIcon img = null;
		img = new ImageIcon(myClass.getResource("rating_star00t.gif"));
		REPRESENTATION_MAP.put(ImageRating.NONE, img);

		img = new ImageIcon(myClass.getResource("rating_star01t.gif"));
		REPRESENTATION_MAP.put(ImageRating.ONE, img);

		img = new ImageIcon(myClass.getResource("rating_star02t.gif"));
		REPRESENTATION_MAP.put(ImageRating.TWO, img);

		img = new ImageIcon(myClass.getResource("rating_star03t.gif"));
		REPRESENTATION_MAP.put(ImageRating.THREE, img);

		img = new ImageIcon(myClass.getResource("rating_star04t.gif"));
		REPRESENTATION_MAP.put(ImageRating.FOUR, img);

		img = new ImageIcon(myClass.getResource("rating_star05t.gif"));
		REPRESENTATION_MAP.put(ImageRating.FIVE, img);
	}

	/**
	 * private constructor to seal Klass
	 */
	private RatingRepresentation() {
		// to seal
	}

	/**
	 * get an icon for this rating or null if there is none
	 * 
	 * @param rating -
	 *            a rating
	 * @return - an according icon
	 */
	public static ImageIcon getIcon(ImageRating rating) {
		return REPRESENTATION_MAP.get(rating);
	}
}
