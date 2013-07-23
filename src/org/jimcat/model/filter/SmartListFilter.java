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

import org.jimcat.model.Image;
import org.jimcat.model.SmartList;
import org.jimcat.model.libraries.SmartListLibrary;

/**
 * This filter will use a given SmartList to decide if image is matched.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class SmartListFilter extends Filter {

	/**
	 * the SmartList used by this filter
	 */
	private SmartList smartList;

	/**
	 * create a new instance using given smartlist
	 * 
	 * @param smartList
	 * @throws IllegalArgumentException
	 *             if list is null
	 */
	public SmartListFilter(SmartList smartList) throws IllegalArgumentException {

		setSmartList(smartList);
	}

	/**
	 * check if the given image is matched by the smartlist
	 * 
	 * @see org.jimcat.model.filter.Filter#matches(org.jimcat.model.Image)
	 */
	@Override
	public boolean matches(Image image) {
		// just delegate
		if (smartList != null && smartList.getFilter() != null) {
			return smartList.getFilter().matches(image);
		}
		// default => true
		return true;
	}

	/**
	 * @return the smartList
	 */
	public SmartList getSmartList() {
		return smartList;
	}

	/**
	 * @param smartList
	 *            the smartList to set
	 */
	private void setSmartList(SmartList smartList) {
		this.smartList = smartList;
	}

	/**
	 * @see org.jimcat.model.filter.Filter#contains(org.jimcat.model.filter.Filter)
	 */
	@Override
	public boolean contains(Filter filter) {
		// if smartlist is null => no loop
		if (smartList == null) {
			return false;
		}

		Filter root = smartList.getFilter();

		// if root is null => no loop
		if (root == null) {
			return false;
		}

		return root == filter || root.contains(filter);
	}

	/**
	 * checks if the stored album still exists
	 * 
	 * @see org.jimcat.model.filter.Filter#getCleanVersion()
	 */
	@Override
	public Filter getCleanVersion() {
		if (SmartListLibrary.getInstance().contains(smartList)) {
			// filter is still ok
			return new SmartListFilter(smartList);
		}
		// filter must be deleted
		return null;
	}
}
