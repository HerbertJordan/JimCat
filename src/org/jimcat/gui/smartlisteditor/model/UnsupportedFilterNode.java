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

/**
 * This node is placed within the filter tree if the accourding filter is
 * unknown.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class UnsupportedFilterNode extends FilterTreeNode {

	/**
	 * the unknown filter type
	 */
	private Filter unknownFilter;

	/**
	 * simple constructor for this type of filter node
	 * 
	 * @param parent
	 * @param unknownFilter
	 */
	public UnsupportedFilterNode(GroupFilterTreeNode parent, Filter unknownFilter) {
		super(parent, true);
		this.unknownFilter = unknownFilter;
	}

	/**
	 * just return the unknown filter
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#getFilter()
	 */
	@Override
	public Filter getFilter() {
		// just return unknown filter
		return unknownFilter;
	}

	/**
	 * generate titel for node
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#generateTitle()
	 */
	@Override
	public String generateTitle() {
		return getPrefix() + "match unknown filter type: " + unknownFilter.getClass().getSimpleName();
	}

	/**
	 * this type of filter node is not editable
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#isEditable()
	 */
	@Override
	public boolean isEditable() {
		// its never editable
		return false;
	}

}
