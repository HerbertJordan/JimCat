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

package org.jimcat.model.filter.metadata;

import org.jimcat.model.ExifMetadata;
import org.jimcat.model.Image;
import org.jimcat.model.filter.Filter;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Weeks;

/**
 * A filter used for relative date (relative to now) filtering.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class RelativeDateFilter extends Filter {

	/**
	 * a list of possible reference dates
	 */
	public enum ReferenceDate {
		DateAdded, DateTaken, DateModified;
	}

	/**
	 * a list of possible time units
	 */
	public enum TimeUnit {
		DAYS, WEEKS, MONTHS;
	}

	/**
	 * the reverence date used by this filter
	 */
	private ReferenceDate referenceDate;

	/**
	 * the time unit the value is given in
	 */
	private TimeUnit timeUnit;

	/**
	 * the value to check
	 */
	private int value;

	/**
	 * create a new filter using given values
	 * 
	 * @param referenceDate
	 * @param timeUnit
	 * @param value
	 */
	public RelativeDateFilter(ReferenceDate referenceDate, TimeUnit timeUnit, int value) {
		super();
		this.referenceDate = referenceDate;
		this.timeUnit = timeUnit;
		this.value = value;
	}

	/**
	 * checks if the given image matches this filter
	 * 
	 * @see org.jimcat.model.filter.Filter#matches(org.jimcat.model.Image)
	 */
	@Override
	public boolean matches(Image image) {
		// get reverence date
		DateTime reference = getReferenceDate(image);

		// check if reference exists
		if (reference == null) {
			return false;
		}

		int compareValue = getCompareValue(reference);
		if (compareValue == -1) {
			return false;
		}

		// compare values
		return compareValue >= 0 && compareValue < value;
	}

	/**
	 * extract reference date from image
	 * 
	 * @param image
	 * @return the reference date to use - null if not available
	 */
	private DateTime getReferenceDate(Image image) {
		switch (referenceDate) {
		case DateAdded:
			return image.getMetadata().getDateAdded();
		case DateModified:
			return image.getMetadata().getModificationDate();
		case DateTaken:
			ExifMetadata metadata = image.getExifMetadata();
			if (metadata != null) {
				return metadata.getDateTaken();
			}
			return null;
		}
		return null;
	}

	/**
	 * 
	 * @param time
	 * @return the value to compare depending on time unit set
	 */
	private int getCompareValue(DateTime time) {
		DateTime now = new DateTime();
		switch (timeUnit) {
		case DAYS:
			return Days.daysBetween(time, now).getDays();
		case WEEKS:
			return Weeks.weeksBetween(time, now).getWeeks();
		case MONTHS:
			return Months.monthsBetween(time, now).getMonths();
		}
		return -1;
	}

	/**
	 * @return the referenceDate
	 */
	public ReferenceDate getReferenceDate() {
		return referenceDate;
	}

	/**
	 * @return the timeUnit
	 */
	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Get a new version of this filter which must be a new reference.
	 */
	@Override
	public Filter getCleanVersion() {
		return new RelativeDateFilter(referenceDate, timeUnit, value);
	}
}
