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
 * This comparator is used to generate decide if two images are duplicates.
 * 
 * To images are duplicates of each other if
 * 
 * <ul>
 * <li>their checksums are equal</li>
 * <li>their filesize is equal</li>
 * <li>their with / heigh are equal</li>
 * </ul>
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class DuplicateComparator implements Comparator<Image> {

	/**
	 * the chain of attributes used to compare two images
	 */
	private static ComparatorChainProxy<Image> comparator = new ComparatorChainProxy<Image>();

	static {
		// the list of elements to check
		comparator.addComparator(new ChecksumComparator());
		comparator.addComparator(new FileSizeComparator());
		comparator.addComparator(new WidthComparator());
		comparator.addComparator(new HeightComparator());
	}

	/**
	 * Compare to images if they are duplicates of each other.
	 * 
	 * If they are, 0 is returnd. Otherwise a natural order is is provided by
	 * this method.
	 * @param o1 
	 * @param o2 
	 * @return the result of the compare mehtod as specified in java.util.Comparator
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Image o1, Image o2) {
		// check if both are the same object
		if (o1 == o2) {
			return 0;
		}
		return comparator.compare(o1, o2);
	}

}
