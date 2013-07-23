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

import static org.jimcat.model.filter.metadata.FileSizeFilter.Type.BIGGER_THEN;
import static org.jimcat.model.filter.metadata.FileSizeFilter.Type.SMALLER_THEN;

import org.apache.commons.lang.ObjectUtils;
import org.jimcat.model.filter.Filter;
import org.jimcat.model.filter.metadata.FileSizeFilter;

/**
 * A filter node representing file size filters.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class FileSizeFilterNode extends FilterTreeNode {

	/**
	 * the type of the represented filter
	 */
	private FileSizeFilter.Type type;

	/**
	 * the size limit in byte
	 */
	private long size;

	/**
	 * a direct constructor
	 * 
	 * @param parent
	 * @param filter 
	 */
	public FileSizeFilterNode(GroupFilterTreeNode parent, FileSizeFilter filter) {
		super(parent, true);

		// setup members
		type = filter.getFileSizeCompareMode();
		size = filter.getSize();
	}

	/**
	 * generate a string representation of this node
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#generateTitle()
	 */
	@Override
	public String generateTitle() {
		String linkingWord = "";
		switch (type) {
		case BIGGER_THEN:
			linkingWord = "bigger";
			break;
		case SMALLER_THEN:
			linkingWord = "smaller";
			break;
		}
		return "must be " + linkingWord + " than " + size + " byte";
	}

	/**
	 * build filter from component
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#getFilter()
	 */
	@Override
	public Filter getFilter() {
		return new FileSizeFilter(type, size);
	}

	/**
	 * negating means actually switching type
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#setNegate(boolean)
	 */
	@Override
	public void setNegate(boolean negate) {
		if (isNegate() != negate) {
			// switch type
			if (type == BIGGER_THEN) {
				type = SMALLER_THEN;
			} else {
				type = BIGGER_THEN;
			}
		}
		// rest is done by super implementation
		super.setNegate(negate);
	}

	/**
	 * @return the size
	 */
	public long getSize() {
		return size;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(long size) {
		long oldValue = this.size;
		this.size = size;
		if (oldValue != size) {
			fireTreeNodeChange(this);
		}
	}

	/**
	 * @return the type
	 */
	public FileSizeFilter.Type getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(FileSizeFilter.Type type) {
		FileSizeFilter.Type oldValue = this.type;
		this.type = type;

		// inform listeners
		if (!ObjectUtils.equals(oldValue, type)) {
			fireTreeNodeChange(this);
		}
	}

}
