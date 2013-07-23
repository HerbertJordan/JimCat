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

import java.util.HashSet;
import java.util.Set;

import org.jimcat.model.Image;
import org.jimcat.model.libraries.ImageLibrary;

/**
 * This filter will only accept images having a duplicate within the library.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class DuplicateFilter extends Filter {

	/**
	 * Just asking library if there is a duplicate of the given image.
	 * 
	 * @see org.jimcat.model.filter.Filter#matches(org.jimcat.model.Image)
	 */
	@Override
	public boolean matches(Image image) {
		ImageLibrary library = ImageLibrary.getInstance();
		return library.hasDuplicate(image);
	}

	/**
	 * Get a new version of this filter which must be a new reference.
	 */
	@Override
	public Filter getCleanVersion() {
		return new DuplicateFilter();
	}

	/**
	 * get a list of all known duplicates
	 * 
	 * @see org.jimcat.model.filter.Filter#possibleMembers()
	 */
	@Override
	public Set<Image> possibleMembers() {
		return new HashSet<Image>(ImageLibrary.getInstance().getDuplicates());
	}

}
