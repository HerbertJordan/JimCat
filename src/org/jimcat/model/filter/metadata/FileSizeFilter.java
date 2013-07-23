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

import org.jimcat.model.Image;
import org.jimcat.model.filter.Filter;

/**
 * A common file size filter replacing privious maximum/minimum ByteSizeFilter
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class FileSizeFilter extends Filter {

	/**
	 * an enumeration of filter size types
	 */
	public enum Type {
		BIGGER_THEN, SMALLER_THEN;
	}

	/**
	 * the type of this filter
	 */
	private Type fileSizeCompareMode;

	/**
	 * the limiting size
	 */
	private long byteSize;

	/**
	 * directo constructor
	 * 
	 * @param type -
	 *            the type (BIGGER_THEN, SMALLER_THEN)
	 * @param size -
	 *            the size to check
	 * @throws IllegalArgumentException
	 *             if type is nul
	 */
	public FileSizeFilter(Type type, long size) throws IllegalArgumentException {
		if (type == null) {
			throw new IllegalArgumentException("type must not be null");
		}
		this.fileSizeCompareMode = type;
		this.byteSize = size;
	}

	/**
	 * apply filter
	 * 
	 * @see org.jimcat.model.filter.Filter#matches(org.jimcat.model.Image)
	 */
	@Override
	public boolean matches(Image image) {
		// get image size
		long imageSize = image.getMetadata().getSize();

		// compare
		switch (fileSizeCompareMode) {
		case BIGGER_THEN:
			return imageSize >= byteSize;
		case SMALLER_THEN:
			return imageSize <= byteSize;
		}
		// can only happen if type is null
		return false;
	}

	/**
	 * @return the size
	 */
	public long getSize() {
		return byteSize;
	}

	/**
	 * @return the type
	 */
	public Type getFileSizeCompareMode() {
		return fileSizeCompareMode;
	}

	/**
	 * Get a new version of this filter which must be a new reference.
	 */
	@Override
	public Filter getCleanVersion() {
		return new FileSizeFilter(fileSizeCompareMode, byteSize);
	}

}
