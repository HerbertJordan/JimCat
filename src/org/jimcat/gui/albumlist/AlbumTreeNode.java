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

import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.jimcat.gui.ViewControl;


/**
 * represents a node within the album tree visual.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public abstract class AlbumTreeNode {

	/**
	 * a reference to the containing album tree model
	 */
	private AlbumTreeModel treeModel;

	/**
	 * the parent of this component
	 */
	private AlbumTreeNode parent;

	/**
	 * the string representation
	 */
	private String titel;

	/**
	 * is this node a leaf
	 */
	private boolean leaf;

	/**
	 * small constructor prepairing some values
	 * 
	 * @param model -
	 *            the model containing this node
	 * @param parent -
	 *            the parent of this node or null, if node is root
	 * @param leaf -
	 *            is this node a leaf of this tree
	 */
	public AlbumTreeNode(AlbumTreeModel model, AlbumTreeNode parent, boolean leaf) {
		this.treeModel = model;
		this.parent = parent;
		this.leaf = leaf;
	}

	/**
	 * this methode will be called when this node is selected. Implement
	 * required procedure here
	 * 
	 * @param control -
	 *            the viewcontrol to use for action
	 */
	public abstract void performSelection(ViewControl control);

	/**
	 * this methode should return the popup menu for the this node.
	 * 
	 * @return - a PopupMenu for this node ready to use, null if there shouldn't
	 *         ba a Popup menu
	 */
	public abstract JPopupMenu getPopupMenu();

	/**
	 * this should return the children of this node owning the specified index.
	 * 
	 * @param index -
	 *            an index, 0 <= index < getChildrenCount()-1
	 * @return the requested child
	 */
	public abstract AlbumTreeNode getChildrenAt(int index);

	/**
	 * count the number of children of this node
	 * 
	 * @return the number of children
	 */
	public abstract int getChildrenCount();

	/**
	 * return the index of a given child
	 * 
	 * @param child
	 * @return - index of the child or -1 if there is no such child
	 */
	public abstract int getIndexOfChild(AlbumTreeNode child);

	/**
	 * return ob the titel-representation is editable
	 * 
	 * @return - true if editable, false otherwhise
	 */
	public abstract boolean isEditable();

	/**
	 * this should be overriden to write changes through to wrapped component
	 * @param value 
	 */
	public abstract void setValue(String value);

	/**
	 * this method should recursively build up path of this component
	 * 
	 * @return the tree path
	 */
	public TreePath getPath() {
		// build up path list
		if (parent != null) {
			// build up list
			return parent.getPath().pathByAddingChild(this);
		}
		// this node is a root
		return new TreePath(this);
	}

	/**
	 * @return the icon
	 */
	public abstract ImageIcon getIcon();


	/**
	 * @return the titel
	 */
	public String getTitel() {
		return titel;
	}

	/**
	 * @param titel
	 *            the titel to set
	 */
	protected void setTitel(String titel) {
		this.titel = titel;
	}

	/**
	 * @return the treeModel
	 */
	protected AlbumTreeModel getTreeModel() {
		return treeModel;
	}

	/**
	 * @return the parent
	 */
	public AlbumTreeNode getParent() {
		return parent;
	}

	/**
	 * returnes the tree which containes this node
	 * 
	 * @return the tree to which this node belongs
	 */
	public JTree getTree() {
		return getTreeModel().getTree();
	}

	/**
	 * @return the leaf
	 */
	public boolean isLeaf() {
		return leaf;
	}

}
