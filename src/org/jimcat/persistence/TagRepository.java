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

package org.jimcat.persistence;

import org.jimcat.model.tag.TagGroup;

/**
 * 
 * This is an interface defining the methods a tag repository has to implement.
 * 
 * 
 * $Id: TagRepository.java 999 2007-09-14 20:02:58Z cleiter $
 * 
 * @author Christoph
 */
public interface TagRepository {

	/**
	 * 
	 * @return the root of the tagTree
	 */
	TagGroup getTagTree();

	/**
	 * 
	 * save the tree under given tagGroup
	 * 
	 * @param tagGroup
	 */
	void save(TagGroup tagGroup);
}
