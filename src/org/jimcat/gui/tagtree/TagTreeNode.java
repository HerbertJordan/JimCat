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

import javax.swing.tree.DefaultMutableTreeNode;

import org.jimcat.model.tag.Tag;
import org.jimcat.model.tag.TagGroup;


/**
 * Represents a TagTree Node.
 * 
 * It is linked to a tag.
 * 
 * $Id: TagTreeNode.java 935 2007-06-15 09:21:09Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public class TagTreeNode extends DefaultMutableTreeNode {

	/**
	 * the tagtree this element belonges to
	 */
	private TagTree tree;

	/**
	 * represented tag
	 */
	private TagGroup myTag;

	/**
	 * the current state
	 */
	private CheckBoxState state;

	/**
	 * constructor using necessary filds
	 * @param tree 
	 * 
	 * @param tag -
	 *            the represented tag
	 * @param state -
	 *            the current state
	 */
	public TagTreeNode(TagTree tree, TagGroup tag, CheckBoxState state) {
		super();
		this.tree = tree;
		this.myTag = tag;
		this.state = state;
	}
	
	/**
	 * returnes true if it is representing a Tag
	 * @see javax.swing.tree.DefaultMutableTreeNode#isLeaf()
	 */
	@Override
	public boolean isLeaf() {
		return myTag instanceof Tag;
	}
	
	/**
	 * @return the state
	 */
	public CheckBoxState getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(CheckBoxState state) {
		// if state wouldn't change anything do nothing
		if (this.state != state) {
			// update state
			setStateQuietly(state);

			// informe listener
			if (myTag instanceof Tag) {
				Tag tag = (Tag) myTag;
				if (state == CheckBoxState.SET) {
					tree.notifyListenersAboutSelection(tag);
				} else if (state == CheckBoxState.UNSET) {
					tree.notifyListenersAboutUnSelection(tag);
				}
			}
		}
	}

	/**
	 * this will change the state without notifyien Listeners
	 * 
	 * @param state
	 */
	protected void setStateQuietly(CheckBoxState state) {
		if (this.state != state) {
			this.state = state;
		}

	}

	/**
	 * returns the represented tag
	 * 
	 * @return the tag in this node
	 */
	public TagGroup getTag() {
		return myTag;
	}

	/**
	 * Just print name of the tag.
	 * 
	 * This is required to mad "jump to element by key" is working
	 * 
	 * @see javax.swing.tree.DefaultMutableTreeNode#toString()
	 */
	@Override
	public String toString() {
		return myTag.getName();
	}

}
