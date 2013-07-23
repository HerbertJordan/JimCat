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

import static org.jimcat.model.filter.metadata.PictureTakenFilter.Type.AFTER;
import static org.jimcat.model.filter.metadata.PictureTakenFilter.Type.BEFORE;

import org.apache.commons.lang.ObjectUtils;
import org.jimcat.model.filter.Filter;
import org.jimcat.model.filter.metadata.PictureTakenFilter;
import org.jimcat.model.filter.metadata.PictureTakenFilter.Type;
import org.joda.time.DateTime;

/**
 * a representation of a PictureTaken filter within the filter tree (smartlist
 * editor)
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class PictureTakenFilterNode extends FilterTreeNode {

	/**
	 * the format used to display the date taken TODO: make configable by
	 * Configuration property
	 */
	private static final String DATE_FORMAT = "dd.MM.yyyy";

	/**
	 * the type of this filter
	 */
	private Type type;

	/**
	 * the limiting date
	 */
	private DateTime date;

	/**
	 * create a node using given values
	 * 
	 * @param parent
	 * @param filter
	 */
	public PictureTakenFilterNode(GroupFilterTreeNode parent, PictureTakenFilter filter) {
		super(parent, true);

		// setup members
		this.type = filter.getDateCompareMode();
		this.date = filter.getLimitDate();
	}

	/**
	 * generate a titel for this node
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#generateTitle()
	 */
	@Override
	public String generateTitle() {
		String linkingWord = "";
		switch (type) {
		case BEFORE:
			linkingWord = "before";
			break;
		case AFTER:
			linkingWord = "after";
			break;
		}
		return "must be taken " + linkingWord + " " + date.toString(DATE_FORMAT);
	}

	/**
	 * reassembling filter from current state
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#getFilter()
	 */
	@Override
	public Filter getFilter() {
		// just create new filter with current setup
		return new PictureTakenFilter(type, date);
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
			if (type == BEFORE) {
				type = AFTER;
			} else {
				type = BEFORE;
			}
		}
		// rest is done by super implementation
		super.setNegate(negate);
	}

	/**
	 * @return the date
	 */
	public DateTime getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(DateTime date) {
		DateTime oldValue = this.date;
		this.date = date;
		
		// inform listeners
		if (!ObjectUtils.equals(oldValue, date)) {
			fireTreeNodeChange(this);
		}
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(Type type) {
		Type oldValue = this.type;
		this.type = type;
		
		// inform listeners
		if (!ObjectUtils.equals(oldValue, type)) {
			fireTreeNodeChange(this);
		}
	}

}
