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

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * A helper class which can be used by collections to support Collection
 * listeners.
 * 
 * T - The element stored within observed collection U - the type of collection
 * observed
 * 
 * $Id: CollectionListenerManager.java 999 2007-09-14 20:02:58Z cleiter $
 * 
 * @author csag1760
 * @param <T>
 * @param <U>
 */
public final class CollectionListenerManager<T extends ObservableBean<T>, U extends ObservableCollection<T, U>> {

	/**
	 * the list of registered listeners
	 */
	private Set<CollectionListener<T, U>> listeners = new CopyOnWriteArraySet<CollectionListener<T, U>>();

	/**
	 * the list supported
	 */
	private U source;

	/**
	 * create a new manager for the given collection
	 * 
	 * @param source
	 */
	public CollectionListenerManager(U source) {
		this.source = source;
	}

	/**
	 * register a new listener
	 * 
	 * @param listener
	 */
	public void addListener(CollectionListener<T, U> listener) {
		listeners.add(listener);
	}

	/**
	 * remove a registered listener
	 * 
	 * @param listener
	 */
	public void removeListener(CollectionListener<T, U> listener) {
		listeners.remove(listener);
	}

	/**
	 * notify listener about newly added elements
	 * 
	 * @param elements
	 */
	public void notifyAdded(Set<T> elements) {
		if (elements.size() == 0) {
			// message isn't necessary
			return;
		}
		for (CollectionListener<T, U> listener : listeners) {
			listener.elementsAdded(source, elements);
		}
	}

	/**
	 * notify listeners about newly removed elements
	 * 
	 * @param elements
	 */
	public void notifyRemoved(Set<T> elements) {
		if (elements.size() == 0) {
			// message isn't necessary
			return;
		}
		for (CollectionListener<T, U> listener : listeners) {
			listener.elementsRemoved(source, elements);
		}
	}

	/**
	 * notify listeners about updated elements
	 * 
	 * @param events -
	 *            the change events in cronological order
	 */
	public void notifyUpdated(List<BeanChangeEvent<T>> events) {
		if (events.size() == 0) {
			// message isn'T necessary
			return;
		}
		for (CollectionListener<T, U> listener : listeners) {
			listener.elementsUpdated(source, events);
		}
	}

	/**
	 * notify listeners about a fundamental change in data base.
	 */
	public void notifyExchange() {
		for (CollectionListener<T, U> listener : listeners) {
			listener.basementChanged(source);
		}
	}

}
