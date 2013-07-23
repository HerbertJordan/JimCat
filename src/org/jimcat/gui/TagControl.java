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

package org.jimcat.gui;

import javax.swing.JOptionPane;

import org.jimcat.model.tag.Tag;
import org.jimcat.model.tag.TagGroup;
import org.jimcat.services.OperationsLocator;
import org.jimcat.services.TagOperations;

/**
 * A list of extracted Tag Operations
 * 
 * $Id: TagControl.java 935 2007-06-15 09:21:09Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public final class TagControl {

	/**
	 * a reference to the containing SwingClient
	 */
	private SwingClient client;

	/**
	 * a reference to the Tag control beneath
	 */
	private TagOperations control;

	/**
	 * root element of the used TagTree
	 */
	private TagGroup root;

	/**
	 * constructing this TagControl
	 * @param client 
	 */
	public TagControl(SwingClient client) {
		this.client = client;

		// establish a conection
		control = OperationsLocator.getTagOperations();
		root = control.getTagTree();
	}

	/**
	 * returns the tagTree root
	 * 
	 * @return the root of the tagTree
	 */
	public final TagGroup getTagTreeRoot() {
		return root;
	}

	/**
	 * changes the name of a Tag
	 * 
	 * @param tag -
	 *            the tag
	 * @param newName -
	 *            the new name
	 */
	public final void renameTag(TagGroup tag, String newName) {
		// change value
		tag.setName(newName);
	}

	/**
	 * Deletes a Tag.
	 * 
	 * @param parent -
	 *            the Parent of this tag
	 * @param tag -
	 *            the tag to delete
	 */
	public final void deleteTag(TagGroup parent, TagGroup tag) {
		// Ask user
		String msg = null;
		if (tag instanceof Tag) {
			msg = "Should the tag \"" + tag.getName() + "\" really be deleted?";
		} else {
			msg = "Should the category \"" + tag.getName() + "\" really be deleted?";
		}
		String titel = "Attention";
		int options = JOptionPane.YES_NO_OPTION;
		int typ = JOptionPane.QUESTION_MESSAGE;

		int result = client.showConfirmDialog(msg, titel, options, typ);

		if (result != JOptionPane.YES_OPTION) {
			return;
		}

		// delete
		parent.removeSubTag(tag);
	}

	/**
	 * startes an assistent to add a new tag
	 * 
	 * @param parent -
	 *            the parent beneath this tag should be
	 * @return the new created tag or null if abourted
	 */
	public final Tag addNewTag(TagGroup parent) {
		// ask for new name
		String msg = "Please enter the name of the new Tag: ";
		String title = "Create new Tag";
		int typ = JOptionPane.QUESTION_MESSAGE;
		String tagName = client.showInputDialog(msg, title, typ);

		if (tagName == null || tagName.equals("")) {
			// aborted
			return null;
		}

		// create and integrate tag
		Tag tag = new Tag();
		tag.setName(tagName);
		parent.addSubTag(tag);

		return tag;
	}

	/**
	 * startes an assistent to add a new tagGroup
	 * 
	 * @param parent -
	 *            the parent beneath this tag should be
	 */
	public final void addNewTagGroup(TagGroup parent) {
		// ask for new name
		String msg = "Please enter the name of the new category: ";
		String title = "Create new category";
		int typ = JOptionPane.QUESTION_MESSAGE;
		String tagName = client.showInputDialog(msg, title, typ);

		if (tagName == null || tagName.equals("")) {
			// aborted
			return;
		}

		// create and integrate tag
		TagGroup tag = new TagGroup();
		tag.setName(tagName);
		parent.addSubTag(tag);
	}

}
