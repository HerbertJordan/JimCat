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
 * A generic class that encapsulates a bean change event.
 * 
 * 
 * $Id: BeanChangeEvent.java 999 2007-09-14 20:02:58Z cleiter $
 * 
 * @author csag1760
 * @param <T>
 *            the generic type
 */
public class BeanChangeEvent<T extends ObservableBean<T>> {

	/**
	 * the transaction this event belonges to
	 */
	private BeanModification transaction;

	/**
	 * the source of the event
	 */
	private T source;

	/**
	 * the property that has changed
	 */
	private BeanProperty property;

	/**
	 * the new value of the bean
	 */
	private Object newValue;

	/**
	 * the old value of the bean
	 */
	private Object oldValue;

	/**
	 * 
	 * Construct a new BeanChangeEvent
	 * 
	 * @param source
	 * @param property
	 * @param newValue
	 */
	public BeanChangeEvent(T source, BeanProperty property, Object newValue) {
		this(source, property, newValue, null);
	}

	/**
	 * Construct a new bean change event.
	 * 
	 * @param source
	 * @param property
	 * @param newValue
	 * @param oldValue
	 */
	public BeanChangeEvent(T source, BeanProperty property, Object newValue, Object oldValue) {
		transaction = BeanModificationManager.getRunningTransaction();
		this.source = source;
		this.property = property;
		this.newValue = newValue;
		this.oldValue = oldValue;
	}

	/**
	 * 
	 * getNewValue
	 * 
	 * @return the new value of the bean
	 */
	public Object getNewValue() {
		return newValue;
	}

	/**
	 * 
	 * getOldValue
	 * 
	 * @return the old value of the bean
	 */
	public Object getOldValue() {
		return oldValue;
	}

	/**
	 * 
	 * getProperty
	 * 
	 * @return the property that caused the cange event
	 */
	public BeanProperty getProperty() {
		return property;
	}

	/**
	 * 
	 * getSource
	 * 
	 * @return the source that caused the change event
	 */
	public T getSource() {
		return source;
	}

	/**
	 * is this event part of a transaction?
	 * 
	 * @return true only if this evnet is part of a transaction
	 */
	public boolean isPartOfTransaction() {
		return transaction != null;
	}

	/**
	 * get transaction this event belonges to - may be null
	 * 
	 * @return the transaction this event belongs to, or null
	 */
	public BeanModification getTransaction() {
		return transaction;
	}
}
