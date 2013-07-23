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

package org.jimcat.model.comparator;

import java.util.Comparator;

import org.jimcat.model.Image;

/**
 * The IdComparator compares two images concerning their IDs.
 * 
 * The IdComparator is used to distinguish between two images concerning their
 * ID.
 * 
 * 
 * $Id$
 * 
 * @author Michael Handler
 */
public class IdComparator implements Comparator<Image> {

	/**
	 * The compare method of the IdComparator distinguishes between Images by
	 * comparing their ids
	 * 
	 * @param o1
	 * @param o2
	 * @return the result of the compare mehtod as specified in
	 *         java.util.Comparator
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Image o1, Image o2) {
		if (o1 == null && o2 == null) {
			return 0;
		}

		if (o1 == null) {
			return -1;
		}

		if (o2 == null) {
			return 1;
		}

		// FIXME cleiter we don't have any id's anymore
		// return o1.getId().compareTo(o2.getId());
		return 0;
	}
}
