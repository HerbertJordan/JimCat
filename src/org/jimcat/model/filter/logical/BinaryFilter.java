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

import org.jimcat.model.Image;
import org.jimcat.model.filter.Filter;

/**
 * A binary Filter is a Filter that combines two other Filter.
 * 
 * 
 * $Id$
 * 
 * @author csag1760
 */
public abstract class BinaryFilter extends Filter {

	protected Filter first;

	protected Filter second;

	/**
	 * 
	 * construct a new BinaryFilter with the two given filter.
	 * 
	 * @param first
	 * @param second
	 */
	public BinaryFilter(Filter first, Filter second) {
		this.first = first;
		this.second = second;
	}

	protected BinaryFilter() {
		/* for hibernate */
	}

	/**
	 * @see org.jimcat.model.filter.Filter#matches(org.jimcat.model.Image)
	 */
	@Override
	public abstract boolean matches(Image image);

	/**
	 * this methode should build an efficient version of the filter
	 * implementation
	 * 
	 * @param left
	 * @param right
	 * @return an efficient version of the filter implementation
	 */
	protected abstract Filter build(Filter left, Filter right);

	/**
	 * @return the first
	 */
	public final Filter getFirst() {
		return first;
	}

	/**
	 * @return the second
	 */
	public final Filter getSecond() {
		return second;
	}

	/**
	 * Returns true if the given filter is part of this binary filter, else
	 * false. Works recursively through the filter of this binary filter.
	 * 
	 * @param filter
	 * 
	 * @see org.jimcat.model.filter.Filter#contains(org.jimcat.model.filter.Filter)
	 */
	@Override
	public final boolean contains(Filter filter) {
		if (first == filter || second == filter) {
			return true;
		}

		if (first != null && first.contains(filter)) {
			return true;
		}

		if (second != null && second.contains(filter)) {
			return true;
		}

		return false;
	}

	/**
	 * get a clean version of this filter only referencing existing objects
	 * 
	 * @see org.jimcat.model.filter.Filter#getCleanVersion()
	 */
	@Override
	public final Filter getCleanVersion() {

		return build(first.getCleanVersion(), second.getCleanVersion());

		/*
		 * // clean left side Filter leftHand = null; if (first!=null) {
		 * leftHand = first.getCleanVersion(); } // clean right side Filter
		 * rightHand = null; if (second!=null) { rightHand =
		 * second.getCleanVersion(); } // if there was no change => return this
		 * if (leftHand == first && rightHand == second) { return this; } // if
		 * there was a change, build new return build(leftHand, rightHand);
		 */
	}
}
