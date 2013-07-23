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

import org.apache.commons.collections.comparators.ComparatorChain;

/**
 * A Proxy that ensures type safety for the comparator chain.
 * 
 * @see org.apache.commons.collections.comparators.ComparatorChain
 * 
 * $Id$
 * 
 * @author csag1760
 * @param <T> 
 */
public class ComparatorChainProxy<T> implements Comparator<T> {

	private ComparatorChain chain = new ComparatorChain();

	/**
	 * @param comp
	 * @param b
	 * @see org.apache.commons.collections.comparators.ComparatorChain#addComparator(java.util.Comparator,
	 *      boolean)
	 */
	public void addComparator(Comparator<T> comp, boolean b) {
		chain.addComparator(comp, b);
	}

	/**
	 * @return the size of the comparator chain
	 * @see org.apache.commons.collections.comparators.ComparatorChain#size()
	 */
	public int size() {
		return chain.size();
	}

	/**
	 * @param comp
	 * @see org.apache.commons.collections.comparators.ComparatorChain#addComparator(java.util.Comparator)
	 */
	public void addComparator(Comparator<T> comp) {
		chain.addComparator(comp);
	}

	/**
	 * @param o1
	 * @param o2
	 * @return zero if equal, a value lower than zero if o1 is smaller than o2,
	 *         else a value greater than zero.
	 * @throws UnsupportedOperationException
	 * @see org.apache.commons.collections.comparators.ComparatorChain#compare(java.lang.Object,
	 *      java.lang.Object)
	 */
	public int compare(T o1, T o2) throws UnsupportedOperationException {
		return chain.compare(o1, o2);
	}

}
