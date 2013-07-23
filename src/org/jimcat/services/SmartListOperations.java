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

package org.jimcat.services;

import org.jimcat.model.SmartList;
import org.jimcat.model.libraries.SmartListLibrary;

/**
 * A list of common Smartlist Operations
 * 
 * $Id: SmartListOperations.java 934 2007-06-15 08:40:58Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public interface SmartListOperations {

	/**
	 * This methode should return the used SmartListLibrary
	 * 
	 * @return the smartlist library
	 */
	public SmartListLibrary getSmartListLibrary();

	/**
	 * adds a new Smartlist to the internal model
	 * 
	 * @param list
	 */
	public void addSmartList(SmartList list);

	/**
	 * deletes a SmartList from the internal model
	 * 
	 * @param list
	 */
	public void deleteSmartList(SmartList list);
}
