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

package org.jimcat.model;

import org.jimcat.model.filter.Filter;
import org.jimcat.model.filter.FilterCircleException;
import org.jimcat.model.notification.BeanListener;
import org.jimcat.model.notification.BeanProperty;
import org.jimcat.model.notification.ListenerManager;
import org.jimcat.model.notification.ObservableBean;

/**
 * Representing a SmartList within the system. The SmartList is like most other
 * model elements, this bean is an Observable Bean.
 * 
 * 
 * $Id: SmartList.java 999 2007-09-14 20:02:58Z cleiter $
 * 
 * @author Herbert
 */
public class SmartList implements ObservableBean<SmartList> {

	/**
	 * a Listener Manager for this bean
	 */
	private transient ListenerManager<SmartList> manager;

	private String name;

	private Filter filter;

	/**
	 * get the name of this SmartList
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * set the name of this SmartList to a new value. If name is unlike current
	 * name, a bean property change event will be fired.
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		String oldValue = this.name;
		if (this.name != name) {
			this.name = name;
			getManager().notifyListeners(BeanProperty.SMARTLIST_NAME, name, oldValue);
		}
	}

	/**
	 * @return the filter
	 */
	public Filter getFilter() {
		return filter;
	}

	/**
	 * @param filter
	 *            the filter to set
	 */
	public void setFilter(Filter filter) {
		Filter oldValue = this.filter;

		// prefent to establish unclean filter trees
		Filter newValue = filter;
		if (filter != null) {
			filter.getCleanVersion();
		}

		if (this.filter == newValue) {
			return;
		}
		this.filter = newValue;

		if (newValue != null && newValue.contains(newValue)) {
			this.filter = oldValue; // revert
			throw new FilterCircleException();
		}

		getManager().notifyListeners(BeanProperty.SMARTLIST_FILTER, newValue, oldValue);
	}

	/**
	 * add a lsitener to this bean
	 * 
	 * @see org.jimcat.model.notification.ObservableBean#addListener(org.jimcat.model.notification.BeanListener)
	 */
	public void addListener(BeanListener<SmartList> listener) {
		getManager().addListener(listener);
	}

	/**
	 * remove a listener from this bean
	 * 
	 * @see org.jimcat.model.notification.ObservableBean#removeListener(org.jimcat.model.notification.BeanListener)
	 */
	public void removeListener(BeanListener<SmartList> listener) {
		getManager().removeListener(listener);
	}

	/**
	 * to implement interface, no work required
	 * 
	 * @see org.jimcat.model.notification.ObservableBean#prepaireDelete()
	 */
	public void prepaireDelete() {
		// nothing to do up to now
	}

	/**
	 * creates an ListenerManager if necessary
	 * 
	 * @return the listener manager of this smart list
	 */
	private ListenerManager<SmartList> getManager() {
		if (manager == null) {
			manager = new ListenerManager<SmartList>(this);
		}
		return manager;
	}
}
