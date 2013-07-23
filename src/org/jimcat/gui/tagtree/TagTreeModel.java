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

package org.jimcat.gui.tagtree;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.jimcat.gui.SwingClient;
import org.jimcat.gui.TagControl;
import org.jimcat.model.notification.BeanChangeEvent;
import org.jimcat.model.notification.BeanListener;
import org.jimcat.model.notification.CollectionListener;
import org.jimcat.model.tag.Tag;
import org.jimcat.model.tag.TagGroup;

/**
 * An adapter between the an TreeModel and the TagTree used by the System.
 * 
 * This model allways contain all Tags within the system.
 * 
 * $Id: TagTreeModel.java 935 2007-06-15 09:21:09Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public class TagTreeModel implements TreeModel, CollectionListener<TagGroup, TagGroup>, BeanListener<TagGroup> {

	/**
	 * reference to central TagControler
	 */
	private TagControl control;

	/**
	 * listeners to this TreeModel
	 */
	private List<TreeModelListener> listeners = new CopyOnWriteArrayList<TreeModelListener>();

	/**
	 * TagTree this Model belonges to
	 */
	private TagTree tree;

	/**
	 * the root element of the represented TagTree
	 */
	private TagTreeNode root;

	/**
	 * a HashMap to find nodes associated to tags
	 */
	private HashMap<TagGroup, TagTreeNode> tag2NodeMap = new HashMap<TagGroup, TagTreeNode>();

	/**
	 * creates a new TagTreeModel for the given tree - linked to the system Tag
	 * tree
	 * @param tree 
	 */
	public TagTreeModel(TagTree tree) {
		this.tree = tree;

		// get Controller reference
		control = SwingClient.getInstance().getTagControl();

		// get TagTree
		TagGroup tagTreeRoot = control.getTagTreeRoot();

		// build up TagNodeTree
		root = getTagNodeTreeFromTree(tagTreeRoot);
	}

	/**
	 * this methode wrapps a TagNodeTree around the TagTree it also registers
	 * this class as Collection and BeanListener
	 * 
	 * @param group
	 * @return the tagTreeNode for this group
	 */
	private TagTreeNode getTagNodeTreeFromTree(TagGroup group) {

		if (group == null) {
			return null;
		}

		// step
		TagTreeNode result = new TagTreeNode(tree, group, CheckBoxState.UNSET);
		tag2NodeMap.put(group, result);

		// register listeners
		group.addListener((BeanListener<TagGroup>) this);
		if (!(group instanceof Tag)) {
			group.addListener((CollectionListener<TagGroup, TagGroup>) this);
		}

		// recursive call
		for (TagGroup subgroup : group.getSubTags()) {
			result.add(getTagNodeTreeFromTree(subgroup));
		}

		return result;
	}

	/**
	 * this will remove all TagTreeNodes associated to the given node (subtree).
	 * it will break connections and unregister listeners.
	 * 
	 * @param node
	 */
	private void removeTagNodeSubTree(TagTreeNode node) {
		// remove listener for this node
		TagGroup tag = node.getTag();
		if (!(tag instanceof Tag)) {
			tag.removeListener((CollectionListener<TagGroup, TagGroup>) this);
		}
		tag.removeListener((BeanListener<TagGroup>) this);

		// first, clear subelements recursively
		Enumeration children = node.children();
		while (children.hasMoreElements()) {
			// get child
			TagTreeNode child = (TagTreeNode) children.nextElement();

			// recursive step
			removeTagNodeSubTree(child);
		}

		// remove children
		node.removeAllChildren();
	}

	/**
	 * Adds a new Listener to this Tree
	 * 
	 * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	public void addTreeModelListener(TreeModelListener listener) {
		listeners.add(listener);
	}

	/**
	 * The structure is stored inside the TagTree
	 * 
	 * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
	 */
	public Object getChild(Object parent, int index) {
		TagTreeNode node = (TagTreeNode) parent;
		return node.getChildAt(index);
	}

	/**
	 * This information is stored inside the TagNodeTree
	 * 
	 * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
	 */
	public int getChildCount(Object parent) {
		TagTreeNode node = (TagTreeNode) parent;
		return node.getChildCount();
	}

	/**
	 * This information has to be extracted from the TagNodeTree
	 * 
	 * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object,
	 *      java.lang.Object)
	 */
	public int getIndexOfChild(Object parent, Object child) {
		if (parent == null || child == null)
			return -1;
		TagTreeNode tag = (TagTreeNode) parent;
		return tag.getIndex((TagTreeNode) child);
	}

	/**
	 * Returns the root element
	 * 
	 * @see javax.swing.tree.TreeModel#getRoot()
	 */
	public Object getRoot() {
		return root;
	}

	/**
	 * determines if this Node is a leafe
	 * 
	 * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
	 */
	public boolean isLeaf(Object node) {
		TagTreeNode tagNode = (TagTreeNode) node;
		TagGroup group = tagNode.getTag();
		if (group instanceof Tag) {
			return true;
		}
		return false;
	}

	/**
	 * Removes the given listener from this model
	 * 
	 * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	public void removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
	}

	/**
	 * Invoked when a tag is renamed
	 * 
	 * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath,
	 *      java.lang.Object)
	 */
	public void valueForPathChanged(TreePath path, Object newValue) {
		TagTreeNode node = (TagTreeNode) path.getLastPathComponent();
		TagGroup tag = node.getTag();

		// delegate renaming to central coordination
		control.renameTag(tag, (String) newValue);
	}

	/**
	 * Updates the TagTree if there are some new Tags
	 * @param collection 
	 * @param elements 
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#elementsAdded(org.jimcat.model.notification.ObservableCollection,
	 *      java.util.Set)
	 */
	public void elementsAdded(TagGroup collection, Set<TagGroup> elements) {
		// get node within
		TagTreeNode node = tag2NodeMap.get(collection);

		// a list of new nodes
		List<TagTreeNode> newNodes = new LinkedList<TagTreeNode>();

		for (TagGroup element : elements) {

			// create new note
			TagTreeNode newNode = getTagNodeTreeFromTree(element);
			tag2NodeMap.put(element, newNode);
			newNodes.add(newNode);

			// register listeners
			element.addListener((BeanListener<TagGroup>) this);
			if (!(element instanceof Tag)) {
				element.addListener((CollectionListener<TagGroup, TagGroup>) this);
			}

			// integrate new node and sort
			node.insert(newNode, collection.indexOf(element));

			CheckBoxTreeActionHandler.updateParent(newNode);
		}

		// create change event
		TreePath path = new TreePath(node.getPath());

		Object nodes[] = newNodes.toArray();

		int indizes[] = new int[nodes.length];

		for (int i = 0; i < nodes.length; i++) {
			indizes[i] = node.getIndex(newNodes.get(i));
		}

		TreeModelEvent event = new TreeModelEvent(tree, path, indizes, nodes);

		// deliver change event
		for (TreeModelListener listener : listeners) {
			listener.treeNodesInserted(event);
		}

		// now, the tree knows the new nodes => make optical changes
		// to remove plus before tagGroup and to open
		// the parent tagGroup if something has been added

		// expand parent
		tree.getTree().expandPath(path);
		// expand new nodes
		for (TagTreeNode newNode : newNodes) {
			if (!(newNode.isLeaf()) && newNode.getChildCount() == 0) {
				tree.getTree().expandPath(new TreePath(newNode.getPath()));
			}
		}
	}

	/**
	 * Updates the TagTree if there are some Tags deleted
	 * @param collection 
	 * @param elements 
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#elementsRemoved(org.jimcat.model.notification.ObservableCollection,
	 *      java.util.Set)
	 */
	public void elementsRemoved(TagGroup collection, Set<TagGroup> elements) {

		// get node within
		TagTreeNode node = tag2NodeMap.get(collection);

		// fields for notification
		List<TagTreeNode> nodes = new LinkedList<TagTreeNode>();
		List<Integer> indizes = new LinkedList<Integer>();

		for (TagGroup element : elements) {

			// find old node
			TagTreeNode oldNode = tag2NodeMap.get(element);

			// remove from register
			tag2NodeMap.remove(element);

			// unregister listeners
			element.removeListener((BeanListener<TagGroup>) this);
			if (!(element instanceof Tag)) {
				element.removeListener((CollectionListener<TagGroup, TagGroup>) this);
			}

			// add to change lists
			indizes.add(new Integer(node.getIndex(oldNode)));
			nodes.add(oldNode);

			// remove from parent
			node.remove(oldNode);
			oldNode.setParent(node);
			CheckBoxTreeActionHandler.updateParent(oldNode);

			// remove listeners for subtree
			removeTagNodeSubTree(oldNode);
		}

		// create change event
		TreePath path = new TreePath(node.getPath());

		// confert indizes to index array
		int index[] = new int[indizes.size()];
		for (int i = 0; i < index.length; i++) {
			index[i] = indizes.get(i).intValue();
		}

		TreeModelEvent event = new TreeModelEvent(tree, path, index, nodes.toArray());

		// deliver change event
		for (TreeModelListener listener : listeners) {
			listener.treeNodesRemoved(event);
		}

	}

	/**
	 * receive update infos about elements
	 * @param collection 
	 * @param events 
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#elementsUpdated(org.jimcat.model.notification.ObservableCollection,
	 *      java.util.List)
	 */
	@SuppressWarnings("unused")
	public void elementsUpdated(TagGroup collection, List<BeanChangeEvent<TagGroup>> events) {
		// ignore. Nodes will handle changes on its own
	}

	/**
	 * build up compleate subtree if a group is drastically changed
	 * @param group 
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#basementChanged(org.jimcat.model.notification.ObservableCollection)
	 */
	public void basementChanged(TagGroup group) {
		// get parent node
		TagTreeNode node = tag2NodeMap.get(group);

		// destroy subtree
		removeTagNodeSubTree(node);

		// create new subtree
		TagTreeNode newNode = getTagNodeTreeFromTree(group);

		// remove node itself and reinsert into tree
		TagTreeNode parent = (TagTreeNode) node.getParent();
		node.removeFromParent(); // FIXME how to redraw tagtree in a clean
		// way
		parent.add(newNode);

		// create event
		TreeModelEvent event = new TreeModelEvent(tree, parent.getPath());

		// deliver change event
		for (TreeModelListener listener : listeners) {
			listener.treeStructureChanged(event);
		}
	}

	/**
	 * Updates a Tag
	 * 
	 * @see org.jimcat.model.notification.BeanListener#beanPropertyChanged(org.jimcat.model.notification.BeanChangeEvent)
	 */
	public void beanPropertyChanged(BeanChangeEvent<TagGroup> event) {
		// find corresponding tag
		TagGroup tag = event.getSource();
		TagTreeNode node = tag2NodeMap.get(tag);

		// Build up event
		TagTreeNode parent = (TagTreeNode) node.getParent();
		TreePath path = new TreePath(parent.getPath());

		int indizes[] = { parent.getIndex(node) };

		Object nodes[] = { node };

		TreeModelEvent modelEvent = new TreeModelEvent(tree, path, indizes, nodes);

		for (TreeModelListener listener : listeners) {
			listener.treeNodesChanged(modelEvent);
		}
	}

}
