/*
 *  This file is part of jimcat.
 *
 *  jimcat is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation version 2.
 *
 *  jimcat is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with jimcat; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.jimcat.model.comparator;

import java.util.Comparator;

import org.jimcat.model.Image;

/**
 * The NullComparator is used to return constant zero.
 * 
 * 
 * $Id: NullComparator.java 935 2007-06-15 09:21:09Z 07g1t1u2 $
 * 
 * @author Christoph
 */
public class NullComparator implements Comparator<Image> {

	/**
	 * the NullComparator always returns zero.
	 * @param o1 
	 * @param o2 
	 * @return always zero
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@SuppressWarnings("unused")
	public int compare(Image o1, Image o2) {
		return 0;
	}
}
