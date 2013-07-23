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

package org.jimcat.model.notification;

/**
 * An observable collection must implement the addListener and removeListener
 * methods and will notify the listeners about changes in it's structure,
 * meaning elements added, removed or reorganized.
 * 
 * 
 * T = element type in collection, must be Observable itself U = type of
 * collection $Id: ObservableCollection.java 999 2007-09-14 20:02:58Z cleiter $
 * 
 * @author csag1760
 * @param <T>
 * @param <U>
 */
public interface ObservableCollection<T extends ObservableBean<T>, U extends ObservableCollection<T, U>> {

	/**
	 * 
	 * add a listener to this collecton
	 * 
	 * @param listener
	 */
	void addListener(CollectionListener<T, U> listener);

	/**
	 * 
	 * remove a listener from this collection
	 * 
	 * @param listener
	 */
	void removeListener(CollectionListener<T, U> listener);

}
