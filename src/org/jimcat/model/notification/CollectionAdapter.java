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

/**
 * An adapter for collection listener interface
 *
 * $Id$
 * @author Herbert
 * @param <T> 
 * @param <U> 
 */
public abstract class CollectionAdapter<T extends ObservableBean<T>, U extends ObservableCollection<T, ? super U>> implements CollectionListener<T,U> {

	/**
     * empty implementation for basement changed
     * @see org.jimcat.model.notification.CollectionListener#basementChanged(org.jimcat.model.notification.ObservableCollection)
     */
	@SuppressWarnings("unused")
    public void basementChanged(U collection) {
	    // does nothing
    }

	/**
     * empty implementation for elements added
     * @see org.jimcat.model.notification.CollectionListener#elementsAdded(org.jimcat.model.notification.ObservableCollection, java.util.Set)
     */
	@SuppressWarnings("unused")
    public void elementsAdded(U collection, Set<T> elements) {
	    // does nothing
    }

	/**
     * empty implementation for elements removed
     * @see org.jimcat.model.notification.CollectionListener#elementsRemoved(org.jimcat.model.notification.ObservableCollection, java.util.Set)
     */
	@SuppressWarnings("unused")
    public void elementsRemoved(U collection, Set<T> elements) {
	    // does nothing
    }

	/**
     * empty implementation for elements updated
     * @see org.jimcat.model.notification.CollectionListener#elementsUpdated(org.jimcat.model.notification.ObservableCollection, java.util.List)
     */
	@SuppressWarnings("unused")
    public void elementsUpdated(U collection, List<BeanChangeEvent<T>> events) {
	    // does nothing
    }

}
