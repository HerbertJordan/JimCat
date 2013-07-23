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

package org.jimcat.gui.perspective.boards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.jimcat.gui.wheellist.WheelListModel;
import org.jimcat.model.Image;
import org.jimcat.model.libraries.LibraryView;
import org.jimcat.model.notification.BeanChangeEvent;
import org.jimcat.model.notification.CollectionListener;

/**
 * The list model used to wrap the library viewer - used by any Board
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class BoardModel implements WheelListModel<Image> {

	/**
	 * a list of observers
	 */
	private List<ListDataListener> listener = new CopyOnWriteArrayList<ListDataListener>();

	/**
	 * the view wrapped
	 */
	private LibraryView view;

	/**
	 * the element observing the library view
	 */
	private ViewObserver observer;
	
	/**
	 * is this model actively listening
	 */
	private boolean active = true;
	
	/**
	 * was there a change since set to passive
	 */
	private boolean dirty = false;
	
	/**
	 * to create a new list wrapping given Library View
	 * 
	 * @param view
	 */
	public BoardModel(LibraryView view) {
		this.view = view;
		observer = new ViewObserver();
		view.addListener(observer);
	}
	
	/**
	 * change active state
	 * 
	 * @param active
	 */
	public void setActive(boolean active) {
		if (this.active != active) {
			this.active = active;
			// if reenabled - check changes
			if (active && dirty) {
				// no exact info tracked =>
				// simulate total exchange
				observer.basementChanged(view);
				dirty = false;
			}
		}
	}
	
	/**
	 * add a new listener
	 * @param l 
	 * 
	 * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
	 */
	public void addListDataListener(ListDataListener l) {
		listener.add(l);
	}

	/**
	 * retrieve an element at given positon
	 * @param index 
	 * @return the image at the given index
	 * 
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public Image getElementAt(int index) {
		return view.getImage(index);
	}

	/**
	 * get number of images
	 * @return the number of images
	 * 
	 * @see javax.swing.ListModel#getSize()
	 */
	public int getSize() {
		return view.size();
	}

	/**
	 * remove a listener
	 * @param l 
	 * 
	 * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
	 */
	public void removeListDataListener(ListDataListener l) {
		listener.remove(l);
	}

	/**
	 * a private class used to observe the library view
	 */
	private class ViewObserver implements CollectionListener<Image, LibraryView> {

		/**
		 * react on big changes
		 * @param collection 
		 * 
		 * @see org.jimcat.model.notification.CollectionListener#basementChanged(org.jimcat.model.notification.ObservableCollection)
		 */
		public void basementChanged(LibraryView collection) {
			if (active) {
				// send message to observers
				ListDataEvent event = new ListDataEvent(BoardModel.this, ListDataEvent.CONTENTS_CHANGED, 0, collection
				        .size());
	
				// inform listeners
				for (ListDataListener l : listener) {
					l.contentsChanged(event);
				}
			} else {
				dirty = true;
			}
		}

		/**
		 * react on added elements
		 * @param collection 
		 * @param elements 
		 * 
		 * @see org.jimcat.model.notification.CollectionListener#elementsAdded(org.jimcat.model.notification.ObservableCollection,
		 *      java.util.Set)
		 */
		public void elementsAdded(LibraryView collection, Set<Image> elements) {
			if (active) {
				// aggregate index ranges
				int index[] = new int[elements.size()];
	
				// get list of indizes
				List<Image> imgList = new ArrayList<Image>(elements);
				for (int i = 0; i < index.length; i++) {
					index[i] = collection.indexOf(imgList.get(i));
				}
	
				// sort indizes
				Arrays.sort(index);
	
				// aggregate events
				int start = 0;
				int end = start;
				while (start < index.length) {
					while (end < index.length - 1 && index[end] + 1 == index[end + 1]) {
						end++;
					}
	
					ListDataEvent event = new ListDataEvent(BoardModel.this, ListDataEvent.INTERVAL_ADDED, index[start],
					        index[end]);
	
					// inform listener
					for (ListDataListener l : listener) {
						l.intervalAdded(event);
					}
	
					// move pointer
					start = end + 1;
					end = start;
				}
			} else {
				dirty = true;
			}
		}

		/**
		 * react on deleted elements
		 * @param collection 
		 * @param elements 
		 * 
		 * @see org.jimcat.model.notification.CollectionListener#elementsRemoved(org.jimcat.model.notification.ObservableCollection,
		 *      java.util.Set)
		 */
		@SuppressWarnings("unused")
		public void elementsRemoved(LibraryView collection, Set<Image> elements) {
			if (active) {
				// its to expensive to give excat information => cheaper to exchange all
				basementChanged(collection);
			} else {
				dirty = true;
			}
		}

		/**
		 * react on updated elements
		 * @param collection 
		 * @param events 
		 * 
		 * @see org.jimcat.model.notification.CollectionListener#elementsUpdated(org.jimcat.model.notification.ObservableCollection,
		 *      java.util.List)
		 */
		@SuppressWarnings("unused")
		public void elementsUpdated(LibraryView collection, List<BeanChangeEvent<Image>> events) {
			// handeld by WheelListItems
		}

	}

}
