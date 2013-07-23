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
import org.jimcat.model.filter.metadata.RelativeDateFilter;
import org.jimcat.model.filter.metadata.RelativeDateFilter.ReferenceDate;
import org.jimcat.model.filter.metadata.RelativeDateFilter.TimeUnit;

/**
 * A node representing a relative date filter within the filter tree.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class RelativeDateFilterNode extends FilterTreeNode {

	/**
	 * the reference date set
	 */
	private ReferenceDate referenceDate;

	/**
	 * the time unit set
	 */
	private TimeUnit timeUnit;

	/**
	 * the value defining the period
	 */
	private int value;

	/**
	 * create new node representing given filter
	 * 
	 * @param parent
	 * @param filter
	 */
	public RelativeDateFilterNode(GroupFilterTreeNode parent, RelativeDateFilter filter) {
		super(parent, true);

		// init members
		referenceDate = filter.getReferenceDate();
		timeUnit = filter.getTimeUnit();
		value = filter.getValue();
	}

	/**
	 * get titel for this node
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#generateTitle()
	 */
	@Override
	public String generateTitle() {
		// term defining reference date
		String date = null;
		switch (referenceDate) {
		case DateAdded:
			date = "date added ";
			break;
		case DateModified:
			date = "date modified ";
			break;
		case DateTaken:
			date = "date taken ";
			break;
		}

		// term defining time unit
		String unit = timeUnit.toString().toLowerCase();

		// build result string
		return date + getPrefix() + "be within last " + value + " " + unit;
	}

	/**
	 * generate a filter from this node
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#getFilter()
	 */
	@Override
	public Filter getFilter() {
		Filter result = new RelativeDateFilter(referenceDate, timeUnit, value);
		if (isNegate()) {
			result = new NotFilter(result);
		}
		return result;
	}

	/**
     * @return the referenceDate
     */
    public ReferenceDate getReferenceDate() {
    	return referenceDate;
    }

	/**
     * @param referenceDate the referenceDate to set
     */
    public void setReferenceDate(ReferenceDate referenceDate) {
    	ReferenceDate oldValue = this.referenceDate;
    	this.referenceDate = referenceDate;
    	
    	if (!ObjectUtils.equals(oldValue, referenceDate)) {
    		fireTreeNodeChange(this);
    	}
    }

	/**
     * @return the timeUnit
     */
    public TimeUnit getTimeUnit() {
    	return timeUnit;
    }

	/**
     * @param timeUnit the timeUnit to set
     */
    public void setTimeUnit(TimeUnit timeUnit) {
    	TimeUnit oldValue = this.timeUnit;
    	this.timeUnit = timeUnit;
    	
    	if (!ObjectUtils.equals(oldValue, timeUnit)) {
    		fireTreeNodeChange(this);
    	}
    }

	/**
     * @return the value
     */
    public int getValue() {
    	return value;
    }

	/**
     * @param value the value to set
     */
    public void setValue(int value) {
    	int oldValue = this.value;
    	this.value = value;
    	
    	if (oldValue != value) {
    		fireTreeNodeChange(this);
    	}
    }

}
