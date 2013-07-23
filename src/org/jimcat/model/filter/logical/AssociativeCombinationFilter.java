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

package org.jimcat.model.filter.logical;

import org.jimcat.model.filter.Filter;

/**
 * An interface to identify and access combination filter like AND and OR.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public interface AssociativeCombinationFilter {

	/**
	 * return first filter expression
	 * 
	 * @return the first part of the combination filter
	 */
	public Filter getFirst();

	/**
	 * return second filter expression
	 * 
	 * @return the second part of the combination filter
	 */
	public Filter getSecond();

}
