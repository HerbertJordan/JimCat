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
 * 
 * A TextFilter is a filter that is searching for a given text in image titles
 * or other fields of the image.
 * 
 * 
 * $Id: TextFilter.java 998 2007-08-29 20:36:25Z cleiter $
 * 
 * @author Herbert
 */
public class TextFilter extends Filter {

	private String pattern;

	/**
	 * creates a new TextFilter using the given pattern
	 * 
	 * @param pattern
	 */
	public TextFilter(String pattern) {
		this.pattern = pattern.toLowerCase();
	}

	/**
	 * test if given image is matching local filter configuration
	 * 
	 * @see org.jimcat.model.filter.Filter#matches(org.jimcat.model.Image)
	 */
	@Override
	public boolean matches(Image image) {
		// up to now, just support titel search
		// TODO support search in description
		String title = image.getTitle();

		if (title == null) {
			return false;
		}

		title = title.toLowerCase();
		return title.contains(pattern);
	}

	/**
	 * returns the search pattern
	 * 
	 * @return the pattern used for the text filtering
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * Get a new version of this filter which must be a new reference.
	 */
	@Override
	public Filter getCleanVersion() {
		return new TextFilter(pattern);
	}

}
