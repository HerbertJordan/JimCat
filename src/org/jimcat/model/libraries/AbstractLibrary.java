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

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jimcat.model.notification.BeanChangeEvent;
import org.jimcat.model.notification.BeanModification;
import org.jimcat.model.notification.BeanModificationListener;
import org.jimcat.model.notification.BeanModificationManager;
import org.jimcat.model.notification.CollectionListener;
import org.jimcat.model.notification.CollectionListenerManager;
import org.jimcat.model.notification.ObservableBean;
import org.jimcat.persistence.Repository;
import org.jimcat.util.SetUtils;

/**
 * An abstract library as base class for all ragular libraries.
 * 
 * $Id$
 * 
 * @author Herbert
 * @param <T> 
 * @param <U> 
 */
public class AbstractLibrary<T extends ObservableBean<T>, U extends AbstractLibrary<T, U>> implements Library<T, U> {

	/**
	 * the manager used by this library
	 */
	private CollectionListenerManager<T, U> manager;

	/**
	 * a reference to the installed album repository
	 */
	private Repository<T> repository;

	/**
	 * the content of elements internally stored
	 */
	private Set<T> content;

	/**
	 * defines whether the library is just getting constructed
	 */
	protected boolean initStep = true;

	/**
	 * the listener used to observe transactions
	 */
	private ModificationListener modificationListener = new ModificationListener();

	/**
	 * default constructor requesting repository to use
	 * 
	 * @param repository
	 */
	@SuppressWarnings("unchecked")
	public AbstractLibrary(Repository<T> repository) {
		this.repository = repository;

		manager = new CollectionListenerManager<T, U>((U) this);
		content = Collections.synchronizedSet(new HashSet<T>());

		// load albums
		add(repository.getAll());

		// init done
		initStep = false;
	}

	/**
	 * add the given element to this library
	 * 
	 * @see org.jimcat.model.libraries.Library#add(org.jimcat.model.notification.ObservableBean)
	 */
	public boolean add(T element) {
		return add(Collections.singleton(element));
	}

	/**
	 * add a set of new elements to this library
	 * 
	 * @see org.jimcat.model.libraries.Library#add(Set)
	 */
	public boolean add(Set<T> elements) {
		// get new elements
		Set<T> newbies = new HashSet<T>(elements);
		newbies.removeAll(content);

		// check if empty
		if (newbies.isEmpty()) {
			// done -> no changes
			return false;
		}

		// add listener
		for (T element : newbies) {
			element.addListener(this);
		}

		// add elements
		content.addAll(newbies);

		// inform listeners
		getManager().notifyAdded(newbies);

		// save changes
		if (!initStep) {
			repository.save(elements);
		}

		// something has changed
		return true;
	}

	/**
	 * check if a certain element is within this library
	 * 
	 * @see org.jimcat.model.libraries.Library#contains(org.jimcat.model.notification.ObservableBean)
	 */
	public boolean contains(T element) {
		return content.contains(element);
	}

	/**
	 * returns a set of elements included in this library
	 * 
	 * @see org.jimcat.model.libraries.Library#getAll()
	 */
	public Set<T> getAll() {
		return Collections.unmodifiableSet(content);
	}

	/**
	 * removes the given element from the library
	 * 
	 * @see org.jimcat.model.libraries.Library#remove(org.jimcat.model.notification.ObservableBean)
	 */
	public boolean remove(T element) {
		return remove(Collections.singleton(element));
	}

	/**
	 * remove a set of elements from this library
	 * 
	 * @see org.jimcat.model.libraries.Library#remove(Set)
	 */
	public boolean remove(Set<T> elements) {
		// get elements to remove
		Set<T> victems = SetUtils.intersection(content, elements);

		// check if empty
		if (victems.isEmpty()) {
			// done - no changes
			return false;
		}

		// remove listener and inform beans about comming up deletion
		try {
			// there may be some update events -> encapsulate them within one
			// transaction
			BeanModificationManager.startTransaction();
			for (T element : victems) {
				element.removeListener(this);
				element.prepaireDelete();
			}
		} finally {
			// commit transaction
			BeanModificationManager.commitTransaction();
		}

		// remove elements
		content.removeAll(victems);

		// inform listeners
		getManager().notifyRemoved(victems);

		// save changes
		repository.remove(victems);

		// changes have been made
		return true;
	}

	/**
	 * clear this library
	 * 
	 * @see org.jimcat.model.libraries.Library#removeAll()
	 */
	public boolean removeAll() {
		// check if there is something to do
		if (content.isEmpty()) {
			return false;
		}
		// synchronization required because of complex operation
		synchronized (content) {
			// unsuscribe from all contained albums and delete them
			for (T element : content) {
				element.removeListener(this);
			}

			// save changes
			repository.remove(content);

			// clear album set
			content.clear();

			// inform listeners - fire basementChanged event
			getManager().notifyExchange();

			// changes have been made
			return true;
		}
	}

	/**
	 * get the number of elements within this library
	 * 
	 * @see org.jimcat.model.libraries.Library#size()
	 */
	public int size() {
		synchronized (content) {
			return content.size();
		}
	}

	/**
	 * add a new CollectionListener to this library
	 * 
	 * @see org.jimcat.model.notification.ObservableCollection#addListener(org.jimcat.model.notification.CollectionListener)
	 */
	public void addListener(CollectionListener<T, U> listener) {
		getManager().addListener(listener);
	}

	/**
	 * remove a collectionListener from this library
	 * 
	 * @see org.jimcat.model.notification.ObservableCollection#removeListener(org.jimcat.model.notification.CollectionListener)
	 */
	public void removeListener(CollectionListener<T, U> listener) {
		getManager().removeListener(listener);
	}

	/**
	 * handle changed bean properties - just distribute it to all listeners
	 * 
	 * @see org.jimcat.model.notification.BeanListener#beanPropertyChanged(org.jimcat.model.notification.BeanChangeEvent)
	 */
	public void beanPropertyChanged(BeanChangeEvent<T> event) {
		// if it is part of a transaction => do nothing
		if (event.isPartOfTransaction()) {
			BeanModification transaction = event.getTransaction();
			transaction.addListener(modificationListener);
			transaction.appendEvent(modificationListener, event);
			return;
		}
		// else dispatch event

		// get changed element
		T element = event.getSource();

		// check if it is part of this collection
		if (content.contains(element)) {
			manager.notifyUpdated(Collections.singletonList(event));

			// save changes
			repository.save(Collections.singleton(element));
		} else {
			// Failure, remove listener -> should never happen
			element.removeListener(this);
		}
	}

	/**
	 * @return the content
	 */
	protected Set<T> getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	protected void setContent(Set<T> content) {
		this.content = content;
	}

	/**
	 * 
	 * @return the listener manager for this library
	 */
	@SuppressWarnings("unchecked")
	protected synchronized CollectionListenerManager<T, U> getManager() {
		if (manager == null) {
			manager = new CollectionListenerManager<T, U>((U) this);
		}
		return manager;
	}

	/**
	 * a private class to observe bean modification transactions
	 */
	private class ModificationListener implements BeanModificationListener {

		/**
		 * react on finished transactions (multible update)
		 * 
		 * @see org.jimcat.model.notification.BeanModificationListener#changesCommited(org.jimcat.model.notification.BeanModification)
		 */
		public void changesCommited(BeanModification modification) {
			List<BeanChangeEvent<T>> events = modification.getEventListFor(this);

			// extract information
			List<BeanChangeEvent<T>> filteredEvents = new LinkedList<BeanChangeEvent<T>>();
			Set<T> beansToSave = new HashSet<T>();

			for (BeanChangeEvent<T> event : events) {
				// the changed element
				T element = event.getSource();
				// check if it is part of this collection
				if (content.contains(element)) {
					// register to lists
					filteredEvents.add(event);
					beansToSave.add(element);
				} else {
					// Failure, remove listener -> should never happen
					element.removeListener(AbstractLibrary.this);
				}
			}

			// send notification
			getManager().notifyUpdated(filteredEvents);

			// save changes
			repository.save(beansToSave);
		}

	}

}
