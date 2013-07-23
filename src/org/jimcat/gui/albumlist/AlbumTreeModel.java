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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.jimcat.model.Album;
import org.jimcat.model.SmartList;


/**
 * The treemodel behind the album list.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class AlbumTreeModel implements TreeModel {

	/**
	 * list of treemodel listeners
	 */
	private List<TreeModelListener> listeners = new CopyOnWriteArrayList<TreeModelListener>();

	/**
	 * containing tree
	 */
	private JTree tree;

	/**
	 * the root element of this tree
	 */
	private AlbumTreeRoot root = new AlbumTreeRoot(this);

	/**
	 * a constructor requesting containing tree
	 * 
	 * @param tree
	 */
	public AlbumTreeModel(JTree tree) {
		this.tree = tree;
	}

	/**
	 * add a new TreeModelListner
	 * 
	 * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	public void addTreeModelListener(TreeModelListener l) {
		// just add to list
		listeners.add(l);
	}

	/**
	 * returns the child at the specified position
	 * 
	 * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
	 */
	public Object getChild(Object parent, int index) {
		AlbumTreeNode node = (AlbumTreeNode) parent;
		return node.getChildrenAt(index);
	}

	/**
	 * counts the children of a given node
	 * 
	 * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
	 */
	public int getChildCount(Object parent) {
		AlbumTreeNode node = (AlbumTreeNode) parent;
		return node.getChildrenCount();
	}

	/**
	 * get index of an element within a parent
	 * 
	 * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object,
	 *      java.lang.Object)
	 */
	public int getIndexOfChild(Object parent, Object child) {
		// check for null
		if (parent == null || child == null) {
			return -1;
		}

		// get index
		AlbumTreeNode node = (AlbumTreeNode) parent;
		return node.getIndexOfChild((AlbumTreeNode) child);
	}

	/**
	 * returns root element
	 * 
	 * @see javax.swing.tree.TreeModel#getRoot()
	 */
	public AlbumTreeRoot getRoot() {
		return root;
	}

	/**
	 * check if the given node is a leaf
	 * 
	 * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
	 */
	public boolean isLeaf(Object node) {
		// it is a leaf if there are no children
		AlbumTreeNode element = (AlbumTreeNode) node;
		return element.isLeaf();
	}

	/**
	 * remove a tree listener
	 * 
	 * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	public void removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
	}

	/**
	 * update a value for the given path
	 * 
	 * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath,
	 *      java.lang.Object)
	 */
	public void valueForPathChanged(TreePath path, Object newValue) {
		if (path != null) {
			AlbumTreeNode node = (AlbumTreeNode) path.getLastPathComponent();
			node.setValue((String) newValue);
		}
	}

	/**
	 * get the path for the all images item
	 * 
	 * @return teh path for the all images item
	 */
	public TreePath getPathForAllImages() {
		return root.getPathForAllImages();
	}

	/**
	 * get the path for a given album
	 * 
	 * @param album
	 * @return - the path or null if there is no such element
	 */
	public TreePath getPathForAlbum(Album album) {
		return root.getPathForAlbum(album);
	}

	/**
	 * get the path for a given smartlist
	 * 
	 * @param list
	 * @return - the path or null if there is no such element
	 */
	public TreePath getPathForSmartList(SmartList list) {
		return root.getPathForSmartList(list);
	}

	/**
	 * get the tree element using this model
	 * 
	 * @return the tree
	 */
	public JTree getTree() {
		return tree;
	}

	/**
	 * this methode will inform all registered listeners about a new inserted
	 * node.
	 * 
	 * @param event -
	 *            a appropriate event
	 * @see TreeModelListener#treeNodesInserted(TreeModelEvent)
	 */
	protected void fireNodeInserted(TreeModelEvent event) {
		// inform listeners
		for (TreeModelListener listener : listeners) {
			listener.treeNodesInserted(event);
		}
	}

	/**
	 * this methode will inform all registered listeners about a structure
	 * change event.
	 * 
	 * @param event -
	 *            a appropriate event
	 * @see TreeModelListener#treeStructureChanged(TreeModelEvent)
	 */
	protected void fireStructureChanged(TreeModelEvent event) {
		// inform listeners
		for (TreeModelListener listener : listeners) {
			listener.treeStructureChanged(event);
		}
	}

	/**
	 * this methode will inform all listeners about a node update
	 * 
	 * @param event -
	 *            a appropriate event
	 * @see TreeModelListener#treeNodesChanged(TreeModelEvent)
	 */
	protected void fireNodeChanged(TreeModelEvent event) {
		// inform listeners
		for (TreeModelListener listener : listeners) {
			listener.treeNodesChanged(event);
		}
	}

	/**
	 * this methode will inform all registered listeners about a removed node
	 * 
	 * @param event -
	 *            a appropriate event
	 * @see TreeModelListener#treeNodesRemoved(TreeModelEvent)
	 */
	protected void fireNodeRemoved(TreeModelEvent event) {
		// inform listeners
		for (TreeModelListener listener : listeners) {
			listener.treeNodesRemoved(event);
		}
	}

}
