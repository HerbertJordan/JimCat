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
import org.jimcat.model.filter.TagFilter;
import org.jimcat.model.filter.logical.NotFilter;
import org.jimcat.model.tag.Tag;

/**
 * Represents a TagFilter within the filter tree model.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class TagFilterNode extends FilterTreeNode {

	/**
	 * the tag to filter for
	 */
	private Tag tag = null;

	/**
	 * create a new TagFilter Node
	 * 
	 * @param parent
	 */
	public TagFilterNode(GroupFilterTreeNode parent) {
		super(parent, true);
	}

	/**
	 * build up filter
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#getFilter()
	 */
	@Override
	public Filter getFilter() {
		// check state
		if (tag == null) {
			return null;
		}

		// build tag filter
		Filter result = new TagFilter(tag);

		// neagate if necessary
		if (isNegate()) {
			result = new NotFilter(result);
		}

		// done
		return result;
	}

	/**
	 * @return the tag
	 */
	public Tag getTag() {
		return tag;
	}

	/**
	 * @param tag
	 *            the tag to set
	 */
	public void setTag(Tag tag) {
		if (tag == null) {
			return;
		}
		
		Tag oldValue = this.tag;
		this.tag = tag;
		
		// inform listeners
		if (!ObjectUtils.equals(oldValue, tag)) {
			fireStructureChanged(this);
		}
	}

	/**
	 * generate a new titel
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#generateTitle()
	 */
	@Override
	public String generateTitle() {
		if (tag != null) {
			return getPrefix() + "have tag " + tag.getName();
		}
		return "no tag selected";
	}

}
