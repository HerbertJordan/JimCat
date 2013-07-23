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

package org.jimcat.gui.imagepopup;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.jimcat.gui.SwingClient;
import org.jimcat.model.Image;
import org.jimcat.model.notification.BeanChangeEvent;
import org.jimcat.model.notification.BeanListener;
import org.jimcat.model.notification.BeanProperty;
import org.jimcat.model.notification.CollectionListener;
import org.jimcat.model.tag.Tag;
import org.jimcat.model.tag.TagGroup;

/**
 * the part of the Image popup menu concerning tags.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public abstract class ImagePopupTagMenu extends JMenu implements CollectionListener<TagGroup, TagGroup>,
        BeanListener<TagGroup>, ActionListener {

	/**
	 * a map used to associate taggroups with menu items used to perform updates
	 * when a TagGroup is changing
	 */
	private Map<TagGroup, JMenuItem> tag2Menu;

	/**
	 * a map used to associate menu items with taggroups used to resolve actions
	 * to tags
	 */
	private Map<JMenuItem, TagGroup> menu2Tag;

	/**
	 * the root element of the tagtree
	 */
	private TagGroup tagTreeRoot;

	/**
	 * If the popup menu is customized this is set to the specific image
	 */
	private Image image;

	/**
	 * determines if the entry has to be shown if the image has the tag or not
	 */
	private boolean hasToHaveTag;

	/**
	 * default constructor
	 */
	public ImagePopupTagMenu() {
		// load tag group root
		tag2Menu = new LinkedHashMap<TagGroup, JMenuItem>();
		menu2Tag = new LinkedHashMap<JMenuItem, TagGroup>();
		tagTreeRoot = SwingClient.getInstance().getTagControl().getTagTreeRoot();

		// build swing components
		initComponents();
	}

	/**
	 * build up swing components
	 */
	private void initComponents() {
		// build root component
		tag2Menu.put(tagTreeRoot, this);
		menu2Tag.put(this, tagTreeRoot);

		// register listeners
		tagTreeRoot.addListener((CollectionListener<TagGroup, TagGroup>) this);
		tagTreeRoot.addListener((BeanListener<TagGroup>) this);

		// build up submenu
		buildSubTree(this, tagTreeRoot);
	}

	/**
	 * this will clean up a subtree of a tag tree
	 * 
	 * @param root
	 */
	private void clearSubTree(JMenu root) {
		// recursive call to subtrees
		for (Component comp : root.getMenuComponents()) {
			if (comp instanceof JMenuItem) {
				unregisterItem((JMenuItem) comp);
			}
		}
		// remove all subcomponents
		root.removeAll();
	}

	/**
	 * this will recusivelly clear all register entries for the given item
	 * 
	 * @param item
	 */
	protected void unregisterItem(JMenuItem item) {
		// make recursive call
		if (item instanceof JMenu) {
			clearSubTree((JMenu) item);
		}
		// remove listener
		item.removeActionListener(this);
		// remove from maps
		TagGroup group = menu2Tag.get(item);

		tag2Menu.remove(group);
		menu2Tag.remove(item);

		// unregister from model elements
		if (!(group instanceof Tag)) {
			group.removeListener((CollectionListener<TagGroup, TagGroup>) this);
		}
		group.removeListener((BeanListener<TagGroup>) this);
	}

	/**
	 * Customize popup menu for a specific image, i.e. if it's in album X then
	 * don't create an entry to add it to X.
	 * 
	 * @param img
	 * @param b
	 */
	public void customizeForImage(Image img, boolean b) {
		this.image = img;
		this.hasToHaveTag = b;

		clearSubTree(this);
		buildSubTree(this, tagTreeRoot);
	}

	/**
	 * Resets to a general popup menu
	 */
	public void resetCustomize() {
		this.image = null;
	}

	/**
	 * a recursive algorithm to build up the subtree
	 * 
	 * @param root
	 * @param group
	 * @return int
	 */
	protected int buildSubTree(JMenu root, TagGroup group) {
		// add new nodes
		int i = 0;
		for (TagGroup tagGroup : group.getSubTags()) {
			if (tagGroup instanceof Tag) {
				if (image != null && image.hasTag((Tag) tagGroup) != hasToHaveTag) {
					continue;
				}
			}
			root.add(createItem(tagGroup));
			i++;
		}
		return i;
	}

	/**
	 * create a new menu item according to the typ of Tag handed in if it is a
	 * group, the whole subtree will be resolved
	 * 
	 * @param group
	 * @return the JMenuItem for the given TagGroup
	 */
	private JMenuItem createItem(TagGroup group) {
		// check if it is a group
		JMenuItem result = null;
		if (group instanceof Tag) {
			// create a leaf
			JMenuItem leaf = new JMenuItem(group.getName());
			leaf.addActionListener(this);
			result = leaf;
		} else {
			// add listener to group
			group.addListener((CollectionListener<TagGroup, TagGroup>) this);
			// create a node - recusive
			JMenu node = new JMenu(group.getName());
			int nr = buildSubTree(node, group);
			node.setEnabled(nr > 0);
			result = node;
		}
		menu2Tag.put(result, group);
		tag2Menu.put(group, result);

		// register bean listener
		group.addListener((BeanListener<TagGroup>) this);
		return result;
	}

	/**
	 * subclasses should implement this methode to react on a item selection
	 * 
	 * @param tag
	 */
	public abstract void elementSelected(Tag tag);

	/**
	 * a big update in a TagGroup => recreate subtree
	 * 
	 * @param collection
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#basementChanged(org.jimcat.model.notification.ObservableCollection)
	 */
	public void basementChanged(TagGroup collection) {
		// get subtree root
		JMenu subRoot = (JMenu) tag2Menu.get(collection);

		// clear subtree
		clearSubTree(subRoot);

		// rebuild subtree
		buildSubTree(subRoot, collection);
	}

	/**
	 * a new element was added to a subtree
	 * 
	 * @param collection
	 * @param elements
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#elementsAdded(org.jimcat.model.notification.ObservableCollection,
	 *      java.util.Set)
	 */
	public void elementsAdded(TagGroup collection, Set<TagGroup> elements) {
		for (TagGroup element : elements) {
			// get parent node
			JMenu parent = (JMenu) tag2Menu.get(collection);

			// create new element
			JMenuItem item = createItem(element);

			// add to submenu
			parent.add(item, collection.indexOf(element));
		}
	}

	/**
	 * an element was removed from a group => remove it from menu
	 * 
	 * @param collection
	 * @param elements
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#elementsRemoved(org.jimcat.model.notification.ObservableCollection,
	 *      java.util.Set)
	 */
	public void elementsRemoved(TagGroup collection, Set<TagGroup> elements) {
		for (TagGroup element : elements) {
			// get parent node
			JMenu parent = (JMenu) tag2Menu.get(collection);

			// get menu for element
			JMenuItem item = tag2Menu.get(element);
			if(item == null) {
				return; //TODO Check why the item can be null here
			}
			unregisterItem(item);

			// remove item from parent
			parent.remove(item);
		}
	}

	/**
	 * react on a changed name of a taggroup
	 * 
	 * @param collection
	 * @param events
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#elementsUpdated(org.jimcat.model.notification.ObservableCollection,
	 *      java.util.List)
	 */
	@SuppressWarnings("unused")
	public void elementsUpdated(TagGroup collection, List<BeanChangeEvent<TagGroup>> events) {
		// ignore - will be done through bean listener
	}

	/**
	 * react on a bean change (just name)
	 * 
	 * @see org.jimcat.model.notification.BeanListener#beanPropertyChanged(org.jimcat.model.notification.BeanChangeEvent)
	 */
	public void beanPropertyChanged(BeanChangeEvent<TagGroup> event) {
		if (event.getProperty() == BeanProperty.TAG_NAME) {
			// update name
			TagGroup tag = event.getSource();
			JMenuItem item = tag2Menu.get(tag);
			if (item != null) {
				item.setText(tag.getName());
			}
		}
	}

	/**
	 * react on an event (menu selection)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		TagGroup group = menu2Tag.get(e.getSource());
		if (group instanceof Tag) {
			elementSelected((Tag) group);
		} else {
			System.out
			        .println("ImagePopupTagMenu.actionPerformed() - there shouldn't be a TagGroup fireing an event - "
			                + e.getSource());
		}
	}
}
