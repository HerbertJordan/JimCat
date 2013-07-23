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

package org.jimcat.persistence.xstream;

import org.jimcat.model.tag.TagGroup;
import org.jimcat.persistence.TagRepository;

/**
 * 
 * Tag repository for XStream backend.
 * 
 * 
 * $Id$
 * 
 * @author Christoph
 */
public class XStreamTagRepository implements TagRepository {

	/**
	 * Get the root element of the tag tree
	 * 
	 * @return the root tag group
	 */
	public TagGroup getTagTree() {
		return XStreamBackup.getInstance().tagRoot;
	}

	/**
	 * Save the tag tree
	 * 
	 * @param tagGroup
	 */
	@SuppressWarnings("unused")
	public void save(TagGroup tagGroup) {
		// do nothing
	}
}
