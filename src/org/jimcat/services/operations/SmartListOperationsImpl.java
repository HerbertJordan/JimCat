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

package org.jimcat.services.operations;

import org.jimcat.model.SmartList;
import org.jimcat.model.libraries.SmartListLibrary;
import org.jimcat.services.SmartListOperations;

/**
 * Provide a list of operations requested by the SmartListOperations interface.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class SmartListOperationsImpl implements SmartListOperations {

	/**
	 * SmartListLibrary used to perform operations.
	 */
	private SmartListLibrary library = SmartListLibrary.getInstance();

	/**
	 * use this operation to create a new smartlist
	 * 
	 * @see org.jimcat.services.SmartListOperations#addSmartList(org.jimcat.model.SmartList)
	 */
	public void addSmartList(SmartList list) {
		// just add list
		library.add(list);
	}

	/**
	 * use this operation to delete a smartlist (remove from persistency
	 * 
	 * @see org.jimcat.services.SmartListOperations#deleteSmartList(org.jimcat.model.SmartList)
	 */
	public void deleteSmartList(SmartList list) {
		// just remove from library
		library.remove(list);
	}

	/**
	 * get the up and running SmartListLibrary
	 * 
	 * @see org.jimcat.services.SmartListOperations#getSmartListLibrary()
	 */
	public SmartListLibrary getSmartListLibrary() {
		return library;
	}

}
