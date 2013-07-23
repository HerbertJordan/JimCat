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

/**
 * common picuture taken filter replacing privous PictureTaken After/Before date
 * filters.
 * 
 * Matches all pictures which are taken before/after a specific date, depending
 * on type.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class PictureTakenFilter extends Filter {

	/**
	 * an enumeration of supported types
	 */
	public enum Type {
		BEFORE, AFTER;
	}

	/**
	 * the type of this filter
	 */
	private Type dateCompareMode;

	/**
	 * the limit date
	 */
	private DateTime limitDate;

	/**
	 * create a new filter using given values
	 * 
	 * @param type
	 * @param date
	 */
	public PictureTakenFilter(Type type, DateTime date) {
		super();
		this.dateCompareMode = type;
		this.limitDate = date;
	}

	/**
	 * match images - determined by type
	 * 
	 * @see org.jimcat.model.filter.Filter#matches(org.jimcat.model.Image)
	 */
	@Override
	public boolean matches(Image image) {

		// gather information
		ExifMetadata exifMetadata = image.getExifMetadata();

		if (exifMetadata == null) {
			return false;
		}

		DateTime takenAt = exifMetadata.getDateTaken();

		if (takenAt == null) {
			return false;
		}

		// compare
		switch (dateCompareMode) {
		case BEFORE:
			return takenAt.isBefore(limitDate);
		case AFTER:
			return takenAt.isAfter(limitDate) || takenAt.isEqual(limitDate);
		}

		// happens when type is null
		return false;
	}

	/**
	 * @return the date
	 */
	public DateTime getLimitDate() {
		return limitDate;
	}

	/**
	 * @return the type
	 */
	public Type getDateCompareMode() {
		return dateCompareMode;
	}

	/**
	 * Get a new version of this filter which must be a new reference.
	 */
	@Override
	public Filter getCleanVersion() {
		return new PictureTakenFilter(dateCompareMode, limitDate);
	}

}
