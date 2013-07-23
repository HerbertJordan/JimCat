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
import org.jimcat.model.filter.logical.NotFilter;

/**
 * represents a constant value within the filter tree model.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class ConstantFilterNode extends FilterTreeNode {

	/**
	 * simple constructor (creats a constant for TRUE)
	 * 
	 * @param parent
	 */
	public ConstantFilterNode(GroupFilterTreeNode parent) {
		super(parent, true);
	}

	/**
	 * build represented filter
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#getFilter()
	 */
	@Override
	public Filter getFilter() {
		Filter result = null;
		if (isNegate()) {
			result = new NotFilter(result);
		}
		return result;
	}

	/**
	 * generate a titel for this node
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#generateTitle()
	 */
	@Override
	public String generateTitle() {
		return "constant " + !isNegate();
	}
}
