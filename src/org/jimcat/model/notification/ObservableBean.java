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
 * An observable bean must implement the methods addListener and removeListener
 * and will notify the registered listeners about changes of the state of the
 * bean.
 * 
 * 
 * $Id: ObservableBean.java 999 2007-09-14 20:02:58Z cleiter $
 * 
 * @author csag1760
 * @param <T>
 */
public interface ObservableBean<T extends ObservableBean<T>> {

	/**
	 * 
	 * add a listener for this bean
	 * 
	 * @param listener
	 */
	public void addListener(BeanListener<T> listener);

	/**
	 * 
	 * remove a listener from this bean
	 * 
	 * @param listener
	 */
	public void removeListener(BeanListener<T> listener);

	/**
	 * this methode is called if the bean is going to be deleted. All
	 * bidirectional links should be removed.
	 * 
	 * This methode should not throw any event changes
	 */
	public void prepaireDelete();
}
