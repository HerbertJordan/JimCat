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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jimcat.model.SmartList;
import org.jimcat.persistence.SmartListRepository;

/**
 * 
 * XStream repository for XStream backend.
 * 
 * 
 * $Id$
 * 
 * @author Christoph
 */
public class XStreamSmartListRepository implements SmartListRepository {

	private Set<SmartList> smartLists = XStreamBackup.getInstance().smartLists;

	/**
	 * Load all smartlists from the persistence layer
	 * 
	 * @return a set of all smartlists
	 */
	public Set<SmartList> getAll() {
		return new HashSet<SmartList>(smartLists);
	}

	/**
	 * Remove a collection of smartlists
	 * 
	 * @param lists
	 */
	public void remove(Collection<SmartList> lists) {
		this.smartLists.removeAll(lists);
	}

	/**
	 * Save a collection of smartlists
	 * 
	 * @param lists
	 */
	public void save(Collection<SmartList> lists) {
		this.smartLists.addAll(lists);
	}
}
