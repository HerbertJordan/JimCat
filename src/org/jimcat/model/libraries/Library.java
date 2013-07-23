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

package org.jimcat.model.libraries;

import java.util.Set;

import org.jimcat.model.notification.BeanListener;
import org.jimcat.model.notification.ObservableBean;
import org.jimcat.model.notification.ObservableCollection;

/**
 * a interface describing a general Library for any kind of observable bean
 * 
 * A library is the basic storage of information used by the system. It should
 * be the only element accessing the according repository.
 * 
 * It will know all "stored" Beans and it is observing them. If there are any
 * changes it will perform updates to the database.
 * 
 * T - typ of elements U - typ of the library itself
 * 
 * $Id$
 * 
 * @author Herbert
 * @param <T> 
 * @param <U> 
 */
public interface Library<T extends ObservableBean<T>, U extends ObservableCollection<T, U>> extends
        ObservableCollection<T, U>, BeanListener<T> {

	/**
	 * this methode returns a set of all currently contained images
	 * 
	 * @return a set of all contained images
	 */
	public Set<T> getAll();

	/**
	 * this methode adds the given element to this library
	 * 
	 * this methode will fire an elementsAdded event if the element hasn't been
	 * allready within this library
	 * 
	 * @param element
	 * @return true if this call has changed the content
	 */
	public boolean add(T element);

	/**
	 * this methode adds the given elements to the library
	 * 
	 * this methode will fire an elementsAdded event if any element hasn't been
	 * allready within this library
	 * 
	 * @param elements
	 * @return true if this call has changed the content
	 */
	public boolean add(Set<T> elements);

	/**
	 * this methode removes the given element from the library
	 * 
	 * this methode will fire an elementsRemoved event if the elment has been
	 * within this library
	 * 
	 * @param element
	 * @return true if this call has changed the content
	 */
	public boolean remove(T element);

	/**
	 * this methode removes the given elements from the library
	 * 
	 * this methode will fire an elementsRemoved event if any elment has been
	 * within this library
	 * 
	 * @param elements
	 * @return true if this call has changed the content
	 */
	public boolean remove(Set<T> elements);

	/**
	 * this cleares the library
	 * 
	 * this methode will fire an element basementChanged event there will be no
	 * removeElement events
	 * 
	 * @return true if this call has changed the content
	 */
	public boolean removeAll();

	/**
	 * this methode returnes the number of elements stored within this library
	 * 
	 * @return - number of elements stored
	 */
	public int size();

	/**
	 * this methode checkes if a given element is stored within this library
	 * 
	 * @param element -
	 *            an element to be tested
	 * @return true if it is contained, false otherwise
	 */
	public boolean contains(T element);
}
