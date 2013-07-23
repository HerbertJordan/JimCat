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

package org.jimcat.model.comparator;

import java.util.Comparator;

import org.jimcat.model.SmartList;


/**
 * A comparator used to display smartlists in an alphabetical order.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class SmartListComparator implements Comparator<SmartList> {

	/**
	 * Compare to smartlists using thire name.
	 * 
	 * null values are always the smallest possible value and are considerte
	 * equal.
	 * @param o1 
	 * @param o2 
	 * @return the result of the compare mehtod as specified in java.util.Comparator
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(SmartList o1, SmartList o2) {

		// both null => equal
		if (o1 == null && o2 == null) {
			return 0;
		}
		// first argument null => o1 smaller
		if (o1 == null) {
			return -1;
		}
		// second argument null => o2 smaller
		if (o2 == null) {
			return 1;
		}
		// if they are equal
		if (o1.equals(o2)) {
			return 0;
		}

		return o1.getName().compareToIgnoreCase(o2.getName());
	}
}