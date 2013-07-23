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

package org.jimcat.model.filter.logical;

import java.util.HashSet;
import java.util.Set;

import org.jimcat.model.Image;
import org.jimcat.model.filter.Filter;
import org.jimcat.model.libraries.ImageLibrary;

/**
 * 
 * Logical NOT filter
 * 
 * 
 * $Id: NotFilter.java 998 2007-08-29 20:36:25Z cleiter $
 * 
 * @author Christoph
 */
public class NotFilter extends Filter {

	private Filter first;

	/**
	 * 
	 * Construct a new NotFilter negating the given filter.
	 * 
	 * @param first
	 */
	public NotFilter(Filter first) {
		this.first = first;
	}

	/**
	 * test if given image is matching this subfilter
	 * 
	 * @see org.jimcat.model.filter.Filter#matches(org.jimcat.model.Image)
	 */
	@Override
	public boolean matches(Image image) {
		return first == null || !first.matches(image);
	}

	/**
	 * get access to the negated filter
	 * 
	 * @return the filter that is negated by this not filter
	 */
	public Filter getSubFilter() {
		return first;
	}

	/**
	 * check if the given filter is within the subtree base on this filter
	 * 
	 * @see org.jimcat.model.filter.Filter#contains(org.jimcat.model.filter.Filter)
	 */
	@Override
	public boolean contains(Filter filter) {
		if (this.first == filter) {
			return true;
		}

		if (this.first == null) {
			return false;
		}

		return this.first.contains(filter);
	}

	/**
	 * Get a new version of this filter which must be a new reference.
	 */
	@Override
	public Filter getCleanVersion() {
		return new NotFilter(first);
	}

	/**
	 * calculate possible member elements
	 * 
	 * @see org.jimcat.model.filter.Filter#possibleMembers()
	 */
	@Override
	public Set<Image> possibleMembers() {
		if (first != null) {
			Set<Image> possible = first.possibleMembers();
			if (possible != null) {
				Set<Image> res = new HashSet<Image>(ImageLibrary.getInstance().getAll());
				res.removeAll(possible);
				return res;
			}
		}
		return null;
	}
}
