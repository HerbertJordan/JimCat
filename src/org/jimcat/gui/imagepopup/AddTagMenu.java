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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.jimcat.gui.ImageControl;
import org.jimcat.gui.SwingClient;
import org.jimcat.gui.TagControl;
import org.jimcat.model.notification.BeanListener;
import org.jimcat.model.tag.Tag;
import org.jimcat.model.tag.TagGroup;

/**
 * This menu can be used to add new tag to the currently selected images.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class AddTagMenu extends ImagePopupTagMenu {

	/**
	 * a map to resolve -new- buttons to parent group
	 */
	private Map<JMenuItem, TagGroup> item2group;

	/**
	 * the creator used to create new tag and add images to new tag
	 */
	private TagCreator creator = new TagCreator();

	/**
	 * a reference to the installed ImageControl
	 */
	private ImageControl control = SwingClient.getInstance().getImageControl();

	/**
	 * a reference to the installed TagControl
	 */
	private TagControl tagControl = SwingClient.getInstance().getTagControl();

	/**
	 * this methode is adding the selected tag to all selected images.
	 * 
	 * @see org.jimcat.gui.imagepopup.ImagePopupTagMenu#elementSelected(org.jimcat.model.tag.Tag)
	 */
	@Override
	public void elementSelected(Tag tag) {
		// delegate operation
		control.addTagToSelection(tag);
	}

	/**
	 * Overriden to add "add new" category
	 * 
	 * @see org.jimcat.gui.imagepopup.ImagePopupTagMenu#buildSubTree(javax.swing.JMenu,
	 *      org.jimcat.model.tag.TagGroup)
	 */
	@Override
	protected int buildSubTree(JMenu root, TagGroup group) {
		// does the same as parent does
		int i = super.buildSubTree(root, group);

		// and adds the new item
		root.addSeparator();
		JMenuItem item = new JMenuItem("<html><i>New Tag...");
		item.addActionListener(getCreator());
		getItemMap().put(item, group);
		root.add(item);

		// register bean listener
		group.addListener((BeanListener<TagGroup>) this);

		return i;
	}

	/**
	 * unregister creator if it is the new item
	 * 
	 * @see org.jimcat.gui.imagepopup.ImagePopupTagMenu#unregisterItem(javax.swing.JMenuItem)
	 */
	@Override
	protected void unregisterItem(JMenuItem item) {
		if (item2group.containsKey(item)) {
			item.removeActionListener(creator);
		} else {
			super.unregisterItem(item);
		}
	}

	/**
	 * get item2group map
	 * 
	 * @return the Map which links items and groups
	 */
	private Map<JMenuItem, TagGroup> getItemMap() {
		if (item2group == null) {
			item2group = new HashMap<JMenuItem, TagGroup>();
		}
		return item2group;
	}

	/**
	 * get tag creation listener
	 * 
	 * @return the TagCreator
	 */
	private TagCreator getCreator() {
		if (creator == null) {
			creator = new TagCreator();
		}
		return creator;
	}

	/**
	 * used to generate and assign new tags
	 */
	private class TagCreator implements ActionListener {
		/**
		 * react on a click
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			// first create new Tag
			TagGroup source = getItemMap().get(e.getSource());
			Tag newOne = tagControl.addNewTag(source);

			if (newOne == null) {
				// action aborted
				return;
			}
			// assign values
			elementSelected(newOne);
		}
	}

}
