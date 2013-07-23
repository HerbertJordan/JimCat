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
import org.jimcat.model.Album;
import org.jimcat.model.comparator.AlbumComparator;
import org.jimcat.model.libraries.AlbumLibrary;
import org.jimcat.model.notification.BeanChangeEvent;
import org.jimcat.model.notification.BeanProperty;
import org.jimcat.model.notification.CollectionListener;
import org.jimcat.services.AlbumOperations;
import org.jimcat.services.OperationsLocator;

/**
 * the main node for the album list.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class AlbumTreeAlbumRoot extends AlbumTreeNode implements CollectionListener<Album, AlbumLibrary> {

	/**
	 * the popupmenu used by this kind of nodes
	 */
	private static final AlbumRootPopupMenu popup = new AlbumRootPopupMenu();

	/**
	 * a reference to the observed library
	 */
	private AlbumLibrary library;

	/**
	 * a list of children
	 */
	private List<AlbumTreeAlbumNode> children;

	/**
	 * a comparator for alphabetical order
	 */
	private AlbumComparator comparator = new AlbumComparator();

	/**
	 * creates a new node of this kind
	 * 
	 * @param model -
	 *            the containing model
	 * @param parent -
	 *            the parent of this node
	 */
	public AlbumTreeAlbumRoot(AlbumTreeModel model, AlbumTreeNode parent) {
		super(model, parent, false);
		// update presentation
		setTitel("Albums");

		// register to up and running AlbumLibrary
		AlbumOperations operations = OperationsLocator.getAlbumOperations();
		library = operations.getAlbumLibrary();
		library.addListener(this);

		// build up tree
		recreateChildren();
	}

	/**
	 * get the path for the given album
	 * 
	 * @param album
	 * @return - the path or null if there is no such element
	 */
	public TreePath getPathForAlbum(Album album) {
		// search album
		for (AlbumTreeAlbumNode node : children) {
			if (node.getAlbum() == album) {
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
	public AlbumTreeNode getChildrenAt(int index) {
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
	public void basementChanged(AlbumLibrary collection) {
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
	 * react on added elements
	 * @param collection 
	 * @param elements 
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#elementsAdded(org.jimcat.model.notification.ObservableCollection,
	 *      java.util.Set)
	 */
	public void elementsAdded(AlbumLibrary collection, Set<Album> elements) {
		// check library
		if (library != collection) {
			collection.removeListener(this);
			return;
		}

		// process changes

		// indices where nodes are inserted
		List<AlbumTreeAlbumNode> newNodes = new LinkedList<AlbumTreeAlbumNode>();

		for (Album element : elements) {
			// add new Element to list
			AlbumTreeAlbumNode newNode = new AlbumTreeAlbumNode(getTreeModel(), this, element);
			newNodes.add(newNode);
			
			if (children.size() == 0) {
				children.add(newNode);
			} else {
				// insert through insert sort
				synchronized (children) {
					ListIterator<AlbumTreeAlbumNode> iter = children.listIterator();
					AlbumTreeAlbumNode current = iter.next();
					boolean lessThanOrEqual = comparator.compare(current.getAlbum(), element) <= 0;
					while (iter.hasNext() && lessThanOrEqual) {
						current = iter.next();
						lessThanOrEqual = comparator.compare(current.getAlbum(), element) <= 0;
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

		// build up index list
		int index[] = new int[newNodes.size()];
		for (int i=0; i<newNodes.size(); i++) {
			index[i] = children.indexOf(newNodes.get(i));
		}
		
		TreeModelEvent event = new TreeModelEvent(getTreeModel(), path, index, null);

		// inform TreeModelListener
		getTreeModel().fireNodeInserted(event);

	}

	/**
	 * reacto on removed elements
	 * @param collection 
	 * @param elements 
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#elementsRemoved(org.jimcat.model.notification.ObservableCollection,
	 *      java.util.Set)
	 */
	public void elementsRemoved(AlbumLibrary collection, Set<Album> elements) {
		// check for right collection
		if (collection != library) {
			collection.removeListener(this);
			return;
		}

		// indizes where elements have been removed
		List<AlbumTreeAlbumNode> delNode = new LinkedList<AlbumTreeAlbumNode>();
		
		for (Album element : elements) {
			// search matching node
			int i = -1;
			synchronized (children) {
				Iterator<AlbumTreeAlbumNode> iter = children.iterator();
				boolean found = false;
				while (iter.hasNext() && !found) {
					AlbumTreeAlbumNode node = iter.next();
					i++;
					if (node.getAlbum() == element) {
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
		for (int i = 0; i<delNode.size(); i++) {
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
	public void elementsUpdated(AlbumLibrary collection, List<BeanChangeEvent<Album>> events) {
		for (BeanChangeEvent<Album> event : events) {
			// check type of change
			if (event.getProperty() != BeanProperty.ALBUM_NAME) {
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
			model.setSelectionPath(getPathForAlbum(event.getSource()));
			
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
			for (AlbumTreeAlbumNode node : children) {
				node.dispose();
			}
		}

		// build up new list
		children = Collections.synchronizedList(new LinkedList<AlbumTreeAlbumNode>());
		List<Album> albumList = new LinkedList<Album>(library.getAll());
		Collections.sort(albumList, comparator);

		// add children
		for (Album album : albumList) {
			children.add(new AlbumTreeAlbumNode(getTreeModel(), this, album));
		}
	}

	/**
	 * Return the Icon for the root of the albumTreeAlbums
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#getIcon()
	 */
	@Override
	public ImageIcon getIcon() {
		return Icons.ALBUM;
	}

}
