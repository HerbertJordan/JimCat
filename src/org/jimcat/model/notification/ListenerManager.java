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

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * A class handling listeners. Adding - Removing - Notfication.
 * 
 * 
 * $Id: ListenerManager.java 999 2007-09-14 20:02:58Z cleiter $
 * 
 * @author csag1760
 * @param <T>
 */
public final class ListenerManager<T extends ObservableBean<T>> {

	private Set<BeanListener<T>> listeners = new CopyOnWriteArraySet<BeanListener<T>>();

	private T source;

	/**
	 * create a new Listener manager managing all BeanListeners for a Observable
	 * Bean
	 * 
	 * @param source
	 *            the bean supporting
	 */
	public ListenerManager(T source) {
		this.source = source;
	}

	/**
	 * add a new listener to manage
	 * 
	 * @param listener
	 *            the new listener
	 */
	public void addListener(BeanListener<T> listener) {
		listeners.add(listener);
	}

	/**
	 * remove a listener from managed list of listeners
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeListener(BeanListener<T> listener) {
		listeners.remove(listener);
	}

	/**
	 * inform all registered listeners about a changed porperty
	 * 
	 * @param property
	 *            the property changed
	 * @param newValue
	 *            the new value of changed property
	 * 
	 * @see BeanListener#beanPropertyChanged(BeanChangeEvent)
	 */
	public void notifyListeners(BeanProperty property, Object newValue) {
		notifyListeners(property, newValue, null);
	}

	/**
	 * inform all registered listeners about a changed porperty
	 * 
	 * @param property
	 *            the property changed
	 * @param newValue
	 *            the new value of changed property
	 * @param oldValue
	 *            the old value of changed property
	 * 
	 * @see BeanListener#beanPropertyChanged(BeanChangeEvent)
	 */
	public void notifyListeners(BeanProperty property, Object newValue, Object oldValue) {
		BeanChangeEvent<T> event = new BeanChangeEvent<T>(source, property, newValue, oldValue);

		for (BeanListener<T> listener : listeners) {
			listener.beanPropertyChanged(event);
		}
	}
}
