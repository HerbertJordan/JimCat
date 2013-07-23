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

package org.jimcat.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

/**
 * A small collection of usefull collection utilities.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public final class SetUtils {

	/**
	 * private constructor for util class
	 */
	private SetUtils() {
		// to avoid instantiation
	}

	/**
	 * This methode is creating an intersection set for the given sets.
	 * 
	 * @param <T> -
	 *            generic type of the sets
	 * @param a -
	 *            set a
	 * @param b -
	 *            set b
	 * @return returns a set containing the intersection of set a and b
	 */
	@SuppressWarnings("unchecked")
	public static <T> Set<T> intersection(Set<T> a, Set<T> b) {
		return new HashSet<T>(CollectionUtils.intersection(a, b));
	}

}
