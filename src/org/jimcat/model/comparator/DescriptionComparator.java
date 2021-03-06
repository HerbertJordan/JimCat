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
 * This Comparator uses the description of the images to distinguish between
 * them.
 * 
 * The DescriptionComparator is used to compare two images concerning their
 * description.
 * 
 * 
 * $Id$
 * 
 * @author Michael Handler
 */
public class DescriptionComparator implements Comparator<Image> {

	/**
	 * The compare method uses the compareTo method of the String class to
	 * compare two images concerning their description.
	 * @param o1 
	 * @param o2 
	 * @return the result of the compare mehtod as specified in java.util.Comparator
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Image o1, Image o2) {
		if (o1 == null || o1.getDescription() == null) {
			if (o2 == null || o2.getDescription() == null)
				return 0;
			return -1;
		}
		if (o2 == null || o2.getDescription() == null)
			return 1;
		return o1.getDescription().compareTo(o2.getDescription());
	}

}
