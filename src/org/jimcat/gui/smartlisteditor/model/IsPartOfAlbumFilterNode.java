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

package org.jimcat.gui.smartlisteditor.model;

import org.jimcat.model.filter.Filter;
import org.jimcat.model.filter.IsPartOfAlbumFilter;
import org.jimcat.model.filter.logical.NotFilter;

/**
 * Node within the filter tree representing a is part of any albums filter.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class IsPartOfAlbumFilterNode extends FilterTreeNode {
	/**
	 * a direct constructor
	 * 
	 * @param parent
	 */
	public IsPartOfAlbumFilterNode(GroupFilterTreeNode parent) {
		super(parent, true);
	}

	/**
	 * get a titel for this filter
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#generateTitle()
	 */
	@Override
	public String generateTitle() {
		return getPrefix() + "be part of any albums";
	}

	/**
	 * generate the represented filter
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#getFilter()
	 */
	@Override
	public Filter getFilter() {
		Filter result = new IsPartOfAlbumFilter();
		if (isNegate()) {
			result = new NotFilter(result);
		}
		return result;
	}

}
