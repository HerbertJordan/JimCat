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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A class keeping track of bean modifications encapsulated in a bean
 * transaction.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public final class BeanModification {

	/**
	 * a list of listeners
	 */
	private Set<BeanModificationListener> listener = new HashSet<BeanModificationListener>();

	/**
	 * to remember initiator of this BeanModification
	 */
	private Exception transactionInitator;

	/**
	 * list of changes mapped by type
	 */
	private Map<BeanModificationListener, ChangeList> changes;

	/**
	 * constructor to create new BeanModification object
	 */
	protected BeanModification() {
		transactionInitator = new Exception();
		changes = new HashMap<BeanModificationListener, ChangeList>();
	}

	/**
	 * append new event to the list for the given type
	 * @param <T> 
	 * 
	 * @param l -
	 *            the listener interrested in this event
	 * @param event
	 */
	@SuppressWarnings("unchecked")
    public <T extends ObservableBean<T>> void appendEvent(BeanModificationListener l, BeanChangeEvent<T> event) {
		ChangeList<T> current = changes.get(l);

		// if list doesn't existest -> create new
		if (current == null) {
			current = new ChangeList<T>();
			changes.put(l, current);
		}

		// append change event
		current.add(event);
	}

	/**
	 * get list of changes from a certain type
	 * 
	 * may be null
	 * 
	 * @param <T> -
	 *            type of observed Bean
	 * @param l -
	 *            the listener requesting events
	 * @return list of changes from a certain type
	 */
	@SuppressWarnings("unchecked")
    public <T extends ObservableBean<T>> List<BeanChangeEvent<T>> getEventListFor(BeanModificationListener l) {
		ChangeList<T> beanChanges = changes.get(l);

		// if it is null => return null
		if (beanChanges == null) {
			return null;
		}

		return beanChanges.events;
	}

	/**
	 * commit this BeanModification
	 */
	protected void commit() {
		// send notifications
		for (BeanModificationListener l : listener) {
			l.changesCommited(this);
		}
	}

	/**
	 * add a new listener
	 * 
	 * unlike other observable elements, each listener could only be registered
	 * once
	 * 
	 * @param l
	 */
	public void addListener(BeanModificationListener l) {
		listener.add(l);
	}

	/**
	 * remove a registered listener
	 * 
	 * @param l
	 */
	public void removeListener(BeanModificationListener l) {
		listener.remove(l);
	}

	/**
	 * @return the transactionInitator
	 */
	protected Exception getTransactionInitator() {
		return transactionInitator;
	}

	/**
	 * a container for bean changes of a certain kind
	 * @param <T> 
	 */
	private class ChangeList<T extends ObservableBean<T>> {

		/**
		 * the list of changes
		 */
		private List<BeanChangeEvent<T>> events;

		/**
		 * default constructor
		 */
		public ChangeList() {
			events = new LinkedList<BeanChangeEvent<T>>();
		}

		/**
		 * to append a new event
		 * 
		 * @param event
		 */
		public void add(BeanChangeEvent<T> event) {
			events.add(event);
		}
	}

}
