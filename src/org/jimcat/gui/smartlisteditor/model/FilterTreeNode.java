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

package org.jimcat.gui.smartlisteditor.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.ImageIcon;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.jimcat.gui.icons.Icons;
import org.jimcat.model.filter.Filter;

/**
 * this is an abstract base class for describing a node within the FilterTree.
 * 
 * every node itself is implementing the TreeModel interface. So you can use any
 * Filter node as tree model within a JTree.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public abstract class FilterTreeNode implements TreeModel, TreeModelListener {

	/**
	 * a list of tree model listeners
	 */
	private List<TreeModelListener> listeners;

	/**
	 * the parent of this component
	 */
	private GroupFilterTreeNode parent;

	/**
	 * the list of subfilters
	 */
	private List<FilterTreeNode> children;

	/**
	 * the icon to display in front of the titel
	 */
	private ImageIcon icon = Icons.JOB_RUN;

	/**
	 * is this node a leaf
	 */
	private boolean leaf;

	/**
	 * is this element negated
	 */
	private boolean negate;

	/**
	 * small constructor prpairing some values
	 * 
	 * @param parent -
	 *            the parent of this node
	 * @param leaf -
	 *            is this node a leaf
	 */
	public FilterTreeNode(GroupFilterTreeNode parent, boolean leaf) {
		// init listener list
		listeners = new CopyOnWriteArrayList<TreeModelListener>();

		// init members
		setParent(parent);
		this.leaf = leaf;
		this.children = new LinkedList<FilterTreeNode>();

	}

	/**
	 * get a list of all children. list is made unmodefiable.
	 * 
	 * @return  list of all children
	 */
	public List<FilterTreeNode> getChildren() {
		return Collections.unmodifiableList(children);
	}

	/**
	 * this should return the children of this node owning the specified index.
	 * 
	 * @param index -
	 *            an index, 0 <= index < getChildrenCount()-1
	 * @return the requested child
	 */
	public FilterTreeNode getChildrenAt(int index) {
		return children.get(index);
	}

	/**
	 * count the number of childs of this node
	 * 
	 * @return the number of children
	 */
	public int getChildrenCount() {
		return children.size();
	}

	/**
	 * return the index of a given child
	 * 
	 * @param child
	 * @return - index of the child or -1 if there is no such child
	 */
	public int getIndexOfChild(FilterTreeNode child) {
		return children.indexOf(child);
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(GroupFilterTreeNode parent) {
		// is there a real change?
		if (this.parent == parent) {
			return;
		}
		// remove from old parent
		if (this.parent != null && this.parent.getIndexOfChild(this) != -1) {
			this.parent.removeChild(this);
		}
		// change parent
		this.parent = parent;

		// add to new parent
		if (parent != null && parent.getIndexOfChild(this) == -1) {
			parent.addChild(this);
		}
	}

	/**
	 * add a new child
	 * 
	 * @param node
	 */
	protected void addChild(FilterTreeNode node) {
		if (!(this instanceof GroupFilterTreeNode)) {
			throw new UnsupportedOperationException("this node does not support children");
		}
		// add to childrenlist
		children.add(node);

		// setup parent
		node.setParent((GroupFilterTreeNode) this);

		// add listener to relay massages
		node.addTreeModelListener(this);

		// fire node inserted event
		fireTreeNodeInsered(node);
	}

	/**
	 * remove a child
	 * 
	 * @param node
	 */
	protected void removeChild(FilterTreeNode node) {
		// remove listener - stop relaying messages
		node.removeTreeModelListener(this);

		// remove from child list
		int index = children.indexOf(node);
		children.remove(node);
		// update parent
		node.setParent(null);

		// fire event
		fireTreeNodeRemoved(node, this, index);
	}

	/**
	 * this method should recursively build up path of this component
	 * 
	 * @return the path
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
	 * this methode should return the currently modified filter.
	 * 
	 * @return the currently modified filter
	 */
	public abstract Filter getFilter();

	/**
	 * get title for this node
	 * 
	 * @return the title of this node
	 */
	public abstract String generateTitle();

	/**
	 * by default, all nodes are editable
	 * 
	 * override this methode if you would like to change this behaviour
	 * 
	 * @return true by default
	 */
	public boolean isEditable() {
		return true;
	}

	/**
	 * @return the icon
	 */
	public ImageIcon getIcon() {
		return icon;
	}

	/**
	 * @param icon
	 *            the icon to set
	 */
	protected void setIcon(ImageIcon icon) {
		this.icon = icon;
	}

	/**
	 * @return the titel
	 */
	public String getTitel() {
		return generateTitle();
	}

	/**
	 * @return the parent
	 */
	public GroupFilterTreeNode getParent() {
		return parent;
	}

	/**
	 * @return the leaf
	 */
	public boolean isLeaf() {
		return leaf;
	}

	/**
	 * @return the negate
	 */
	public boolean isNegate() {
		return negate;
	}

	/**
	 * @param negate
	 *            the negate to set
	 */
	public void setNegate(boolean negate) {
		boolean oldValue = this.negate;
		this.negate = negate;
		if (oldValue != negate) {
			fireTreeNodeChange(this);
		}
	}

	/**
	 * get a prefix depending on the negation state
	 * 
	 * @return the normal or negation prefix
	 */
	protected String getPrefix() {
		if (negate) {
			return "must not ";
		}
		return "must ";
	}

	/**
	 * register a new TreeListener
	 * 
	 * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	public void addTreeModelListener(TreeModelListener l) {
		listeners.add(l);
	}

	/**
	 * get child of the given node at the given index
	 * 
	 * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
	 */
	public Object getChild(Object parentNode, int index) {
		FilterTreeNode node = (FilterTreeNode) parentNode;
		return node.getChildrenAt(index);
	}

	/**
	 * get number of childs beneath this the given node
	 * 
	 * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
	 */
	public int getChildCount(Object parentNode) {
		FilterTreeNode node = (FilterTreeNode) parentNode;
		return node.getChildrenCount();
	}

	/**
	 * get index of a given child
	 * 
	 * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object,
	 *      java.lang.Object)
	 */
	public int getIndexOfChild(Object parentNode, Object child) {
		FilterTreeNode node = (FilterTreeNode) parentNode;
		return node.getIndexOfChild((FilterTreeNode) child);
	}

	/**
	 * return the root node
	 * 
	 * @see javax.swing.tree.TreeModel#getRoot()
	 */
	public Object getRoot() {
		return this;
	}

	/**
	 * decide if the given node is a leaf
	 * 
	 * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
	 */
	public boolean isLeaf(Object node) {
		FilterTreeNode treeNode = (FilterTreeNode) node;
		return treeNode.isLeaf();
	}

	/**
	 * 
	 * Return if this node is the root of the tree which is the case if it has
	 * no parent
	 * 
	 * @return true if node is root
	 */
	public boolean isRoot() {
		if (this.parent == null)
			return true;
		return false;
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
	 * this operation isn't supported by this model
	 * 
	 * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath,
	 *      java.lang.Object)
	 */
	@SuppressWarnings("unused")
	public void valueForPathChanged(TreePath path, Object newValue) {
		// do nothing, all is done by editors
	}

	/**
	 * notify all registered listenes about a node value change.
	 * 
	 * @param node
	 */
	protected void fireTreeNodeChange(FilterTreeNode node) {
		// get information
		FilterTreeNode nodeParent = node.getParent();
		TreePath path = null;
		TreeModelEvent event = null;

		// build up event
		if (nodeParent == null) {
			// root must not have any indices or nodes
			path = new TreePath(node);
			event = new TreeModelEvent(node, path);
		} else {
			// build up normal informations
			path = nodeParent.getPath();
			int indices[] = { nodeParent.getIndexOfChild(node) };
			Object nodes[] = { node };
			event = new TreeModelEvent(node, path, indices, nodes);
		}

		// send event
		for (TreeModelListener listener : listeners) {
			listener.treeNodesChanged(event);
		}
	}

	/**
	 * notify all listeners about a newly inserted node.
	 * 
	 * @param newNode -
	 *            the new node
	 */
	protected void fireTreeNodeInsered(FilterTreeNode newNode) {
		// get information
		TreePath path = newNode.getParent().getPath();
		int indices[] = { newNode.getParent().getIndexOfChild(newNode) };
		Object nodes[] = { newNode };

		// build up event
		TreeModelEvent event = new TreeModelEvent(newNode, path, indices, nodes);

		// send event
		for (TreeModelListener listener : listeners) {
			listener.treeNodesInserted(event);
		}
	}

	/**
	 * notify all listeners about a newly inserted node.
	 * 
	 * @param node -
	 *            the node deleted
	 * @param formerParent -
	 *            the former parent
	 * @param formerIndex -
	 *            the former index of the deleted node
	 */
	protected void fireTreeNodeRemoved(FilterTreeNode node, FilterTreeNode formerParent, int formerIndex) {
		// get information
		TreePath path = formerParent.getPath();
		int indices[] = { formerIndex };
		Object nodes[] = { node };

		// build up event
		TreeModelEvent event = new TreeModelEvent(node, path, indices, nodes);

		// send event
		for (TreeModelListener listener : listeners) {
			listener.treeNodesRemoved(event);
		}
	}

	/**
	 * notify all listeners about a restructuring
	 * 
	 * @param subtreeRoot -
	 *            the root of the subtree where changes occure
	 */
	protected void fireStructureChanged(FilterTreeNode subtreeRoot) {
		// build up event
		TreeModelEvent event = new TreeModelEvent(subtreeRoot, subtreeRoot.getPath());

		// send event
		for (TreeModelListener listener : listeners) {
			listener.treeStructureChanged(event);
		}
	}

	/**
	 * relaying messages from subnodes
	 * 
	 * @see javax.swing.event.TreeModelListener#treeNodesChanged(javax.swing.event.TreeModelEvent)
	 */
	public void treeNodesChanged(TreeModelEvent e) {
		for (TreeModelListener listener : listeners) {
			listener.treeNodesChanged(e);
		}
	}

	/**
	 * relaying messages from subnodes
	 * 
	 * @see javax.swing.event.TreeModelListener#treeNodesInserted(javax.swing.event.TreeModelEvent)
	 */
	public void treeNodesInserted(TreeModelEvent e) {
		for (TreeModelListener listener : listeners) {
			listener.treeNodesInserted(e);
		}
	}

	/**
	 * relaying messages from subnodes
	 * 
	 * @see javax.swing.event.TreeModelListener#treeNodesRemoved(javax.swing.event.TreeModelEvent)
	 */
	public void treeNodesRemoved(TreeModelEvent e) {
		for (TreeModelListener listener : listeners) {
			listener.treeNodesRemoved(e);
		}
	}

	/**
	 * relaying messages from subnotes
	 * 
	 * @see javax.swing.event.TreeModelListener#treeStructureChanged(javax.swing.event.TreeModelEvent)
	 */
	public void treeStructureChanged(TreeModelEvent e) {
		for (TreeModelListener listener : listeners) {
			listener.treeStructureChanged(e);
		}
	}

}
