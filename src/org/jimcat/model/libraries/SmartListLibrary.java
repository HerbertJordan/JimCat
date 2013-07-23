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

import org.jimcat.model.Album;
import org.jimcat.model.SmartList;
import org.jimcat.model.filter.Filter;
import org.jimcat.model.notification.BeanModificationManager;
import org.jimcat.model.notification.CollectionAdapter;
import org.jimcat.model.tag.Tag;
import org.jimcat.model.tag.TagGroup;
import org.jimcat.persistence.RepositoryLocator;

/**
 * this class is forming a central container for all persistently stored
 * smartlists of this system.
 * 
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class SmartListLibrary extends AbstractLibrary<SmartList, SmartListLibrary> {

	/**
	 * the singelton instance
	 */
	private static SmartListLibrary INSTANCE;

	/**
	 * create a new smartlistlibrary.
	 * 
	 * This constructor is using informations proviede by the installed
	 * SmartListRepository.
	 */
	private SmartListLibrary() {
		super(RepositoryLocator.getSmartListRepository());

		// register listeners
		AlbumLibrary.getInstance().addListener(new AlbumLibraryObserver());
		TagHierarchy.getInstance().addListener(new TagHierarchyObserver());
		this.addListener(new SmartListLibraryObserver());
	}

	/**
	 * returnes the only instance of this class (singelton)
	 * 
	 * @return an instance of SmartListLibrary
	 */
	public static SmartListLibrary getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SmartListLibrary();
		}
		return INSTANCE;
	}

	/**
	 * adds the given smartlists to this library after they have been cleaned
	 * 
	 * @see org.jimcat.model.libraries.AbstractLibrary#add(java.util.Set)
	 */
	@Override
	public boolean add(Set<SmartList> elements) {
		if (!initStep) {
			cleanLists(elements);
		}
		return super.add(elements);
	}

	/**
	 * this methode will clean all contained smartlists
	 */
	private void cleanAll() {
		try {
			BeanModificationManager.startTransaction();
			cleanLists(getAll());
		} catch (RuntimeException e) {
			throw e;
		} finally {
			BeanModificationManager.commitTransaction();
		}
	}

	/**
	 * this methode is cleaning up contained smartlists (broken links will be
	 * removed)
	 * 
	 * @param lists
	 */
	private void cleanLists(Set<SmartList> lists) {
		for (SmartList list : lists) {
			Filter filter = list.getFilter();
			if (filter != null) {
				list.setFilter(filter.getCleanVersion());
			}
		}
	}

	/**
	 * this class is observing the album library and is reacting on removed
	 * albums
	 */
	private class AlbumLibraryObserver extends CollectionAdapter<Album, AlbumLibrary> {
		/**
		 * react on removed albums
		 * 
		 * @see org.jimcat.model.notification.CollectionAdapter#elementsRemoved(org.jimcat.model.notification.ObservableCollection,
		 *      java.util.Set)
		 */
		@Override
		@SuppressWarnings("unused")
		public void elementsRemoved(AlbumLibrary collection, Set<Album> elements) {
			cleanAll();
		}

		/**
		 * react on massive changes
		 * 
		 * @see org.jimcat.model.notification.CollectionAdapter#basementChanged(org.jimcat.model.notification.ObservableCollection)
		 */
		@Override
		@SuppressWarnings("unused")
		public void basementChanged(AlbumLibrary collection) {
			cleanAll();
		}
	}

	/**
	 * this class is observing the smartList library and is reacting on removed
	 * smartlists
	 */
	private class SmartListLibraryObserver extends CollectionAdapter<SmartList, SmartListLibrary> {
		/**
		 * react on removed smartlists
		 * 
		 * @see org.jimcat.model.notification.CollectionAdapter#elementsRemoved(org.jimcat.model.notification.ObservableCollection,
		 *      java.util.Set)
		 */
		@Override
		@SuppressWarnings("unused")
		public void elementsRemoved(SmartListLibrary collection, Set<SmartList> elements) {
			cleanAll();
		}

		/**
		 * react on massive changes
		 * 
		 * @see org.jimcat.model.notification.CollectionAdapter#basementChanged(org.jimcat.model.notification.ObservableCollection)
		 */
		@Override
		@SuppressWarnings("unused")
		public void basementChanged(SmartListLibrary collection) {
			cleanAll();
		}
	}

	/**
	 * this class is observing the album library and is reacting on removed
	 * albums
	 */
	private class TagHierarchyObserver extends CollectionAdapter<TagGroup, TagHierarchy> {
		/**
		 * react on removed albums
		 * 
		 * @see org.jimcat.model.notification.CollectionAdapter#elementsRemoved(org.jimcat.model.notification.ObservableCollection,
		 *      java.util.Set)
		 */
		@Override
		@SuppressWarnings("unused")
		public void elementsRemoved(TagHierarchy collection, Set<TagGroup> elements) {
			for (TagGroup group : elements) {
				if (group instanceof Tag) {
					cleanAll();
					return;
				}
			}
		}

		/**
		 * react on massive changes
		 * 
		 * @see org.jimcat.model.notification.CollectionAdapter#basementChanged(org.jimcat.model.notification.ObservableCollection)
		 */
		@Override
		@SuppressWarnings("unused")
		public void basementChanged(TagHierarchy collection) {
			cleanAll();
		}
	}

}
