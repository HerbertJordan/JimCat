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

package org.jimcat.gui.albumlist;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jimcat.gui.ViewControl;
import org.jimcat.gui.icons.Icons;
import org.jimcat.model.SmartList;
import org.jimcat.model.comparator.SmartListComparator;
import org.jimcat.model.libraries.SmartListLibrary;
import org.jimcat.model.notification.BeanChangeEvent;
import org.jimcat.model.notification.BeanProperty;
import org.jimcat.model.notification.CollectionListener;
import org.jimcat.services.OperationsLocator;
import org.jimcat.services.SmartListOperations;

/**
 * the main node for the smartlist list.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class AlbumTreeSmartListRoot extends AlbumTreeNode implements CollectionListener<SmartList, SmartListLibrary> {

	/**
	 * the popupmenu used by this kind of nodes
	 */
	private static final SmartListRootPopupMenu popup = new SmartListRootPopupMenu();

	/**
	 * a reference to the observed library
	 */
	private SmartListLibrary library;

	/**
	 * a list of children
	 */
	private List<AlbumTreeSmartListNode> children;

	/**
	 * a comparator for alphabetical order
	 */
	private SmartListComparator comparator = new SmartListComparator();

	/**
	 * creates a new node of this kind
	 * 
	 * @param model -
	 *            the containing model
	 * @param parent -
	 *            the parent of this node
	 */
	public AlbumTreeSmartListRoot(AlbumTreeModel model, AlbumTreeNode parent) {
		super(model, parent, false);
		// update presentation
		setTitel("Smart Lists");

		// register to up and running AlbumLibrary
		SmartListOperations operations = OperationsLocator.getSmartListOperations();
		library = operations.getSmartListLibrary();
		library.addListener(this);

		// build up tree
		recreateChildren();
	}

	/**
	 * get the path for the given SmartList
	 * 
	 * @param list
	 * @return - the path or null if there is no such element
	 */
	public TreePath getPathForSmartList(SmartList list) {
		// search album
		for (AlbumTreeSmartListNode node : children) {
			if (node.getSmartList() == list) {
				return node.getPath();
			}
		}
		return null;
	}

	/**
	 * Return child at specified position or null, if there is no such child
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#getChildrenAt(int)
	 */
	@Override
	public AlbumTreeSmartListNode getChildrenAt(int index) {
		if (index < 0 || index >= children.size()) {
			return null;
		}
		return children.get(index);
	}

	/**
	 * number of children
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#getChildrenCount()
	 */
	@Override
	public int getChildrenCount() {
		return children.size();
	}

	/**
	 * index of child or -1, if there is no such element
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#getIndexOfChild(org.jimcat.gui.albumlist.AlbumTreeNode)
	 */
	@Override
	public int getIndexOfChild(AlbumTreeNode child) {
		return children.indexOf(child);
	}

	/**
	 * the popup menu for this node
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#getPopupMenu()
	 */
	@Override
	public JPopupMenu getPopupMenu() {
		return popup;
	}

	/**
	 * This node is not editable
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#isEditable()
	 */
	@Override
	public boolean isEditable() {
		return false;
	}

	/**
	 * nothing will happen if this elemt is selected
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#performSelection(org.jimcat.gui.ViewControl)
	 */
	@Override
	@SuppressWarnings("unused")
	public void performSelection(ViewControl control) {
		// do nothing
	}

	/**
	 * editing is not allowed, this methode does nothing
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#setValue(java.lang.String)
	 */
	@Override
	@SuppressWarnings("unused")
	public void setValue(String value) {
		// do nothing
	}

	/**
	 * if the content of the library has changed compleatly, regenerate children
	 * @param collection 
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#basementChanged(org.jimcat.model.notification.ObservableCollection)
	 */
	public void basementChanged(SmartListLibrary collection) {
		if (library != collection) {
			collection.removeListener(this);
			return;
		}
		// update list
		recreateChildren();

		// build event
		TreeModelEvent event = new TreeModelEvent(getTreeModel(), getPath());

		// inform listeners
		getTreeModel().fireStructureChanged(event);

	}

	/**
	 * react on added smartlist
	 * @param collection 
	 * @param elements 
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#elementsAdded(org.jimcat.model.notification.ObservableCollection,
	 *      java.util.Set)
	 */
	public void elementsAdded(SmartListLibrary collection, Set<SmartList> elements) {
		// check library
		if (library != collection) {
			collection.removeListener(this);
			return;
		}

		// list of inserted nodes
		List<AlbumTreeSmartListNode> newNodes = new LinkedList<AlbumTreeSmartListNode>();

		// process elements
		for (SmartList element : elements) {
			// add new Element to list
			AlbumTreeSmartListNode newNode = new AlbumTreeSmartListNode(getTreeModel(), this, element);
			newNodes.add(newNode);

			if (children.size() == 0) {
				children.add(newNode);
			} else {
				// insert through insert sort
				synchronized (children) {
					ListIterator<AlbumTreeSmartListNode> iter = children.listIterator();
					AlbumTreeSmartListNode current = iter.next();
					boolean lessThanOrEqual = comparator.compare(current.getSmartList(), element) <= 0;
					while (iter.hasNext() && lessThanOrEqual) {
						current = iter.next();
						lessThanOrEqual = comparator.compare(current.getSmartList(), element) <= 0;
					}

					// one step to far
					if (!lessThanOrEqual) {
						iter.previous();
					}
					// possition found
					iter.add(newNode);
				}
			}
		}

		// build up event
		// parentpath
		TreePath path = getPath();

		// generate index list
		int index[] = new int[newNodes.size()];
		for (int i = 0; i < newNodes.size(); i++) {
			index[i] = children.indexOf(newNodes.get(i));
		}

		// indices
		TreeModelEvent event = new TreeModelEvent(getTreeModel(), path, index, null);

		// inform TreeModelListener
		getTreeModel().fireNodeInserted(event);
	}

	/**
	 * react on added elements
	 * @param collection 
	 * @param elements 
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#elementsRemoved(org.jimcat.model.notification.ObservableCollection,
	 *      java.util.Set)
	 */
	public void elementsRemoved(SmartListLibrary collection, Set<SmartList> elements) {
		// check for right collection
		if (collection != library) {
			collection.removeListener(this);
			return;
		}

		// list of deleted indizes
		List<AlbumTreeSmartListNode> delNode = new LinkedList<AlbumTreeSmartListNode>();

		// process list
		for (SmartList element : elements) {
			// search matching node
			int i = -1;
			synchronized (children) {
				Iterator<AlbumTreeSmartListNode> iter = children.iterator();
				boolean found = false;
				while (iter.hasNext() && !found) {
					AlbumTreeSmartListNode node = iter.next();
					i++;
					if (node.getSmartList() == element) {
						// element found - remove
						delNode.add(node);
						found = true;
					}
				}
			}
		}

		// inform listeners
		TreePath path = getPath();

		// create index
		int index[] = new int[delNode.size()];
		for (int i = 0; i < delNode.size(); i++) {
			index[i] = children.indexOf(delNode.get(i));
		}

		// remove elements
		children.removeAll(delNode);

		// build event
		TreeModelEvent event = new TreeModelEvent(getTreeModel(), path, index, null);

		// fire event
		getTreeModel().fireNodeRemoved(event);
	}

	/**
	 * react on children changes.
	 * @param collection 
	 * @param events 
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#elementsUpdated(org.jimcat.model.notification.ObservableCollection,
	 *      java.util.List)
	 */
	@SuppressWarnings("unused")
	public void elementsUpdated(SmartListLibrary collection, List<BeanChangeEvent<SmartList>> events) {
		for (BeanChangeEvent<SmartList> event : events) {
			// check type of change
			if (event.getProperty() != BeanProperty.SMARTLIST_NAME) {
				// do not react on those changes
				continue;
			}

			// update list (sorting)
			recreateChildren();

			// build event
			TreeModelEvent treeEvent = new TreeModelEvent(getTreeModel(), getPath());

			// inform listeners
			getTreeModel().fireStructureChanged(treeEvent);

			// reestablish selection
			TreeSelectionModel model = getTreeModel().getTree().getSelectionModel();
			model.setSelectionPath(getPathForSmartList(event.getSource()));

			// no more iterations required
			return;
		}
	}

	/**
	 * this methode is (re-)creating the compleate subtree
	 */
	private void recreateChildren() {
		// dispose old list
		if (children != null) {
			for (AlbumTreeSmartListNode node : children) {
				node.dispose();
			}
		}

		// build up new list
		children = Collections.synchronizedList(new LinkedList<AlbumTreeSmartListNode>());
		List<SmartList> smartListList = new LinkedList<SmartList>(library.getAll());
		Collections.sort(smartListList, comparator);

		// add children
		for (SmartList list : smartListList) {
			children.add(new AlbumTreeSmartListNode(getTreeModel(), this, list));
		}
	}

	/**
	 * Return the icon for the AlbumTreeSmartListRoot
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#getIcon()
	 */
	@Override
	public ImageIcon getIcon() {
		return Icons.SMARTLIST;
	}
}
