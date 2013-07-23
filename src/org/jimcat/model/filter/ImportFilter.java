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

package org.jimcat.model.filter;

import org.jimcat.model.Image;

/**
 * A small filter aiming at the import id of an image.
 * 
 * $Id: ImportFilter.java 998 2007-08-29 20:36:25Z cleiter $
 * 
 * @author Herbert
 */
public class ImportFilter extends Filter {

	/**
	 * an enumeration of supported types
	 */
	public enum Type {
		AT_LEAST, UP_TO, EXACT;
	}

	/**
	 * the type of this filter
	 */
	private Type importCompareMode;

	/**
	 * the import id to have
	 */
	private long importID;

	/**
	 * constructor requireing an importID
	 * 
	 * @param type -
	 *            of comparison
	 * @param importID
	 */
	public ImportFilter(Type type, long importID) {
		this.importID = importID;
		this.importCompareMode = type;
	}

	/**
	 * @see org.jimcat.model.filter.Filter#matches(org.jimcat.model.Image)
	 */
	@Override
	public boolean matches(Image image) {
		// extract id
		long id = image.getMetadata().getImportId();

		// compare, depending on type
		switch (importCompareMode) {
		case AT_LEAST:
			return id >= importID;
		case EXACT:
			return id == importID;
		case UP_TO:
			return id < importID;
		}
		// if type is null
		return false;
	}

	/**
	 * get import id this filter filters for
	 * 
	 * @return the import id of the import
	 */
	public long getImportID() {
		return importID;
	}

	/**
	 * @return the importCompareMode
	 */
	public Type getImportCompareMode() {
		return importCompareMode;
	}

	/**
	 * Get a new version of this filter which must be a new reference.
	 */
	@Override
	public Filter getCleanVersion() {
		return new ImportFilter(importCompareMode, importID);
	}

}
