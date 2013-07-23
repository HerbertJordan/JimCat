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
 * A filter used to filter images due to their being part of any albums.
 * 
 * $Id$
 * 
 * @author Michael
 */
public class IsPartOfAlbumFilter extends Filter {
	/**
	 * Checks if the given image is part of any albums.
	 * 
	 * @see org.jimcat.model.filter.Filter#matches(org.jimcat.model.Image)
	 */
	@Override
	public boolean matches(Image image) {
		// just check number of albums
		return image.getAlbums().size() > 0;
	}

	/**
	 * Get a new version of this filter which must be a new reference.
	 */
	@Override
	public Filter getCleanVersion() {
		return new IsPartOfAlbumFilter();
	}
}
