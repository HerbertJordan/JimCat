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
import org.jimcat.model.filter.Filter;
import org.jimcat.model.filter.logical.NotFilter;
import org.jimcat.model.filter.metadata.TextFilter;

/**
 * Represents a text filter
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class TextFilterNode extends FilterTreeNode {

	/**
	 * the pattern checked by this filter
	 */
	private String pattern;

	/**
	 * creates a new node using values from the given filter
	 * 
	 * @param parent
	 * @param filter
	 */
	public TextFilterNode(GroupFilterTreeNode parent, TextFilter filter) {
		super(parent, true);

		// setup members
		pattern = filter.getPattern();
	}

	/**
	 * generate titel
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#generateTitle()
	 */
	@Override
	public String generateTitle() {
		return getPrefix() + "match text \"" + pattern + "\"";
	}

	/**
	 * generate filter from pattern
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#getFilter()
	 */
	@Override
	public Filter getFilter() {
		Filter result = new TextFilter(pattern);
		// negate if necessary
		if (isNegate()) {
			result = new NotFilter(result);
		}
		return result;
	}

	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * @param pattern
	 *            the pattern to set
	 */
	public void setPattern(String pattern) {
		String oldValue = this.pattern;
		this.pattern = pattern;
		
		// inform listeners
		if (!ObjectUtils.equals(oldValue, pattern)) {
			fireTreeNodeChange(this);
		}
	}

}
