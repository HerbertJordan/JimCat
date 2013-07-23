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

import java.util.Collection;
import java.util.Set;

/**
 * a generic interface for kind of data beans
 * 
 * T - type this repository responsible for
 * 
 * $Id$
 * 
 * @author Herbert * 
 * @param <T> the generic type
 */
public interface Repository<T> {

	/**
	 * Get all elements stored by this repository
	 * 
	 * @return all elements in the repository
	 */
	Set<T> getAll();

	/**
	 * Save or update a collection of elements within this repository
	 * 
	 * @param elements
	 */
	void save(Collection<T> elements);

	/**
	 * Delete a collection of elements within this repository
	 * 
	 * @param elements
	 */
	void remove(Collection<T> elements);
}
