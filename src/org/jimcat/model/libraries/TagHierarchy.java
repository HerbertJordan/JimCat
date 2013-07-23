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
import org.jimcat.model.notification.BeanListener;
import org.jimcat.model.notification.BeanModification;
import org.jimcat.model.notification.BeanModificationListener;
import org.jimcat.model.notification.BeanModificationManager;
import org.jimcat.model.notification.CollectionListener;
import org.jimcat.model.notification.CollectionListenerManager;
import org.jimcat.model.notification.ObservableCollection;
import org.jimcat.model.tag.Tag;
import org.jimcat.model.tag.TagGroup;
import org.jimcat.persistence.RepositoryLocator;
import org.jimcat.persistence.TagRepository;

/**
 * A central, library like container to manage the system wide used tag
 * hierarchy
 * 
 * $Id$
 * 
 * @author michael & herbert
 */
public final class TagHierarchy implements CollectionListener<TagGroup, TagGroup>, BeanListener<TagGroup>,
        ObservableCollection<TagGroup, TagHierarchy> {

	/**
	 * the singelton instance
	 */
	private static TagHierarchy INSTANCE;

	/**
	 * a reference to the repository used for backup content
	 */
	private TagRepository repository;

	/**
	 * the listener manager used to manage observers
	 */
	private CollectionListenerManager<TagGroup, TagHierarchy> manager;

	/**
	 * the root of the managed tree
	 */
	private TagGroup root;

	/**
	 * a set of contained tags
	 */
	private Set<TagGroup> content;

	/**
	 * the listener used to observe transactions
	 */
	private ModificationListener modificationListener = new ModificationListener();

	/**
	 * private constructor prepairing tag hierarchy
	 */
	private TagHierarchy() {
		// get repository
		repository = RepositoryLocator.getTagRepository();

		// init members
		manager = new CollectionListenerManager<TagGroup, TagHierarchy>(this);
		content = new HashSet<TagGroup>();

		// load tree
		root = repository.getTagTree();

		// FIXME - move to something like "default config manager"
		if (root == null) {
			// create a new root entry
			TagGroup newRoot = new TagGroup(true);
			newRoot.setName("ROOT");

			TagGroup people = new TagGroup();
			people.setName("People");

			TagGroup events = new TagGroup();
			events.setName("Events");

			TagGroup places = new TagGroup();
			places.setName("Places");

			TagGroup misc = new TagGroup();
			misc.setName("Misc");

			newRoot.addSubTag(people);
			newRoot.addSubTag(events);
			newRoot.addSubTag(places);
			newRoot.addSubTag(misc);

			root = newRoot;
			backupTree();
		}

		// register to groups and build up content
		updateContentFromTree();
	}

	/**
	 * the singelton factory
	 * 
	 * @return an instance of the TagHierarchy
	 */
	public synchronized static TagHierarchy getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new TagHierarchy();
		}
		return INSTANCE;
	}

	/**
	 * get tag hierarchy root
	 * 
	 * @return the root of the tag hierarchy
	 */
	public TagGroup getTagHierarchyRoot() {
		return root;
	}

	/**
	 * check if a given tag is within this hierarchy
	 * 
	 * @param tag
	 * @return true if the given tag is part of the hierarchy
	 */
	public boolean contains(Tag tag) {
		return content.contains(tag);
	}

	/**
	 * add listener to this hierarchy
	 * 
	 * @see org.jimcat.model.notification.ObservableCollection#addListener(org.jimcat.model.notification.CollectionListener)
	 */
	public void addListener(CollectionListener<TagGroup, TagHierarchy> listener) {
		manager.addListener(listener);
	}

	/**
	 * remove listener from this hierarchy
	 * 
	 * @see org.jimcat.model.notification.ObservableCollection#removeListener(org.jimcat.model.notification.CollectionListener)
	 */
	public void removeListener(CollectionListener<TagGroup, TagHierarchy> listener) {
		manager.removeListener(listener);
	}

	/**
	 * just saves changes to repository
	 * 
	 * try to avoid this operation through it might be very expensive
	 * 
	 * @param collection
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#basementChanged(org.jimcat.model.notification.ObservableCollection)
	 */
	@SuppressWarnings("unused")
	public void basementChanged(TagGroup collection) {
		synchroniceStructure();
	}

	/**
	 * just save changes to repository
	 * 
	 * @param collection
	 * @param elements
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#elementsAdded(org.jimcat.model.notification.ObservableCollection,
	 *      java.util.Set)
	 */
	@SuppressWarnings("unused")
	public void elementsAdded(TagGroup collection, Set<TagGroup> elements) {
		synchroniceStructure();
	}

	/**
	 * just save changes to repository
	 * 
	 * @param collection
	 * @param elements
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#elementsRemoved(org.jimcat.model.notification.ObservableCollection,
	 *      java.util.Set)
	 */
	@SuppressWarnings("unused")
	public void elementsRemoved(TagGroup collection, Set<TagGroup> elements) {
		synchroniceStructure();
	}

	/**
	 * just save changes to library
	 * 
	 * @param collection
	 * @param events
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#elementsUpdated(org.jimcat.model.notification.ObservableCollection,
	 *      java.util.List)
	 */
	@SuppressWarnings("unused")
	public void elementsUpdated(TagGroup collection, List<BeanChangeEvent<TagGroup>> events) {
		// do nothing - events captured through bean listener interface
	}

	/**
	 * react on TagGroup changes => save changes
	 * 
	 * @see org.jimcat.model.notification.BeanListener#beanPropertyChanged(org.jimcat.model.notification.BeanChangeEvent)
	 */
	@SuppressWarnings("unused")
	public void beanPropertyChanged(BeanChangeEvent<TagGroup> event) {
		// if it is part of a transaction => do nothing
		if (event.isPartOfTransaction()) {
			BeanModification transaction = event.getTransaction();
			transaction.addListener(modificationListener);
			transaction.appendEvent(modificationListener, event);
			return;
		}
		// else dispatch event

		// get changed element
		TagGroup element = event.getSource();

		// check if it is part of this collection
		if (content.contains(element)) {
			manager.notifyUpdated(Collections.singletonList(event));

			// save changes
			backupTree();
		} else {
			// Failure, remove listener -> should never happen
			element.removeListener((BeanListener<TagGroup>) TagHierarchy.this);
			element.removeListener((CollectionListener<TagGroup, TagGroup>) TagHierarchy.this);
		}
	}

	/**
	 * this methode will update current lists with the current state of the
	 * tagtree.
	 * 
	 * It will also save the current tree to the installed repository
	 */
	private void synchroniceStructure() {
		// update content and backup
		updateContentFromTree();
		backupTree();
	}

	/**
	 * used this methode to get a clean set of contained tags
	 * 
	 * It will unregister from all tags this TagHierarchy is registered to. Then
	 * it will reregister to all TagGroups within the current TagTree.
	 * 
	 */
	private void updateContentFromTree() {
		// get current nodes
		Set<TagGroup> current = new HashSet<TagGroup>();
		toSet(root, current);

		// get removed nodes
		Set<TagGroup> removed = new HashSet<TagGroup>(content);
		removed.removeAll(current);

		// get added nodes
		Set<TagGroup> added = new HashSet<TagGroup>(current);
		added.removeAll(content);

		// unregister from removed nodes
		try {
			// encapsulate throwen events
			BeanModificationManager.startTransaction();
			for (TagGroup group : removed) {
				group.removeListener((BeanListener<TagGroup>) this);
				group.removeListener((CollectionListener<TagGroup, TagGroup>) this);
				group.prepaireDelete();
			}
		} finally {
			BeanModificationManager.commitTransaction();
		}

		// register to new nodes
		for (TagGroup group : added) {
			group.addListener((BeanListener<TagGroup>) this);
			group.addListener((CollectionListener<TagGroup, TagGroup>) this);
		}

		// exchange sets
		content = current;

		// notify listeners
		manager.notifyAdded(added);
		manager.notifyRemoved(removed);
	}

	/**
	 * store the Tag Tree given by its root node int the given set
	 * 
	 * @param subTree -
	 *            the root of the subtree to be stored
	 * @param elements -
	 *            the set to witch it should be stored
	 */
	private void toSet(TagGroup subTree, Set<TagGroup> elements) {
		elements.add(subTree);
		for (TagGroup group : subTree.getSubTags()) {
			toSet(group, elements);
		}
	}

	/**
	 * perform synchronisation with data base
	 */
	private synchronized void backupTree() {
		// printTree(root,"");
		repository.save(root);
	}

	/**
	 * print a tree to the console
	 * 
	 * @param subRoot
	 * @param prefix
	 */
	@SuppressWarnings("unused")
	private void printTree(TagGroup subRoot, String prefix) {
		for (TagGroup group : subRoot.getSubTags()) {
			printTree(group, prefix + " - ");
		}
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
			List<BeanChangeEvent<TagGroup>> events = modification.getEventListFor(this);

			// extract information
			List<BeanChangeEvent<TagGroup>> filteredEvents = new LinkedList<BeanChangeEvent<TagGroup>>();
			Set<TagGroup> beansToSave = new HashSet<TagGroup>();

			for (BeanChangeEvent<TagGroup> event : events) {
				// the changed element
				TagGroup element = event.getSource();
				// check if it is part of this collection
				if (content.contains(element)) {
					// register to lists
					filteredEvents.add(event);
					beansToSave.add(element);
				} else {
					// Failure, remove listener -> should never happen
					element.removeListener((BeanListener<TagGroup>) TagHierarchy.this);
					element.removeListener((CollectionListener<TagGroup, TagGroup>) TagHierarchy.this);
				}
			}

			// send notification
			manager.notifyUpdated(filteredEvents);

			// save changes
			backupTree();
		}

	}

}
