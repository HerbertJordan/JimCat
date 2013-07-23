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

import org.apache.commons.lang.ObjectUtils;
import org.jimcat.model.SmartList;
import org.jimcat.model.filter.Filter;
import org.jimcat.model.filter.SmartListFilter;
import org.jimcat.model.filter.logical.NotFilter;

/**
 * represents a SmartListFilter within the filter tree.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class SmartListFilterNode extends FilterTreeNode {

	/**
	 * the smartlist to filter for
	 */
	private SmartList smartList;

	/**
	 * create a new representation for the given smartlist
	 * 
	 * @param parent
	 * @param filter
	 */
	public SmartListFilterNode(GroupFilterTreeNode parent, SmartListFilter filter) {
		super(parent, true);

		// setup members
		smartList = filter.getSmartList();
	}

	/**
	 * create a new representation for the given smartlist
	 * 
	 * @param parent
	 * @param list
	 */
	public SmartListFilterNode(GroupFilterTreeNode parent, SmartList list) {
		super(parent, true);

		// setup members
		smartList = list;
	}

	/**
	 * generate a titel
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#generateTitle()
	 */
	@Override
	public String generateTitle() {
		if (smartList != null) {
			return getPrefix() + "match SmartList \"" + smartList.getName() + "\"";
		}
		if (isNegate()) {
			return "No smartlist selected: Matches nothing";
		}
		return "No smartlist selected: Matches everything";
	}

	/**
	 * regenerate filter
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#getFilter()
	 */
	@Override
	public Filter getFilter() {
		Filter result = new SmartListFilter(smartList);
		if (isNegate()) {
			result = new NotFilter(result);
		}
		return result;
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
	public void setSmartList(SmartList smartList) {
		SmartList oldList = this.smartList;
		this.smartList = smartList;
		
		// inform listener
		if (!ObjectUtils.equals(oldList, smartList)) {
			fireTreeNodeChange(this);
		}
	}

}
