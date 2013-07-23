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
 * The PathComparator compares two images according to their path.
 * 
 * Two images are compared by comparing the path stored in the metadata of the
 * image.
 * 
 * 
 * $Id$
 * 
 * @author Michael Handler
 */
public class PathComparator implements Comparator<Image> {

	/**
	 * The compare method of the PathComparator class is used to compare two
	 * images according to their path.
	 * @param o1 
	 * @param o2 
	 * @return the result of the compare mehtod as specified in java.util.Comparator
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Image o1, Image o2) {
		if (o1 == null || o1.getMetadata() == null || o1.getMetadata().getPath() == null) {
			if (o2 == null || o2.getMetadata() == null || o2.getMetadata().getPath() == null)
				return 0;
			return -1;
		}
		if (o2 == null || o2.getMetadata() == null || o2.getMetadata().getPath() == null)
			return 1;
		return o1.getMetadata().getPath().compareTo(o2.getMetadata().getPath());
	}

}
