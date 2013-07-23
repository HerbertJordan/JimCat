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

import java.util.Set;

import org.jimcat.model.Image;

/**
 * 
 * The abstract class Filter is the superclass of all filter in the system.
 * 
 * A filter has to implement the method matches. Binary filter should be
 * subclasses of the class Binary Filter
 * 
 * @see org.jimcat.model.filter.logical.BinaryFilter
 * 
 * $Id: Filter.java 311 2007-04-16 22:24:23Z 07g1t1u1 $
 * 
 * @author Christoph
 */
public abstract class Filter {

	/**
	 * this methode should do the actual filtering.
	 * 
	 * return true, if the given images is passing the filter, false otherwise.
	 * 
	 * @param image -
	 *            the image to test
	 * @return true if the filter matches
	 */
	public abstract boolean matches(Image image);

	/**
	 * Checks if a subtree of this filter contains the given filter
	 * 
	 * @param filter
	 * @return true if the filter contains the given filter
	 */

	@SuppressWarnings("unused")
	public boolean contains(Filter filter) {
		return false;
	}

	/**
	 * get a clean version which only refers existing objects
	 * 
	 * removes deleted albums, tags and smartlists
	 * 
	 * @return a clean version of the given filter
	 */
	public abstract Filter getCleanVersion();

	/**
	 * this methode schould return a list of possible postives for this filter.
	 * try to make this list as small as possible. return null if you cann't
	 * provide such a list
	 * 
	 * the resulting list must be modifieable
	 * 
	 * @return the images for which the filter that could possibly match if it
	 *         is known
	 */
	public Set<Image> possibleMembers() {
		return null;
	}
}
