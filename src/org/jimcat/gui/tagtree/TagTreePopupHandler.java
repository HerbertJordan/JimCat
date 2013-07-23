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

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.jimcat.gui.SwingClient;
import org.jimcat.gui.TagControl;
import org.jimcat.gui.icons.Icons;
import org.jimcat.model.tag.Tag;
import org.jimcat.model.tag.TagGroup;

/**
 * Creates the Popupmenu for the TagTree
 * 
 * $Id: TagTreePopupHandler.java 935 2007-06-15 09:21:09Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public class TagTreePopupHandler extends MouseAdapter implements KeyListener {

	/**
	 * a reference to a central TagControl
	 */
	private TagControl control;

	/**
	 * tree this handler belongs to
	 */
	private JTree tree;

	/**
	 * popup menu
	 */
	private JPopupMenu menu;

	/**
	 * the TagTree this popup is called on
	 */
	private TagTreeNode victim;

	/**
	 * Creates a new PopupHandler
	 * @param tree 
	 */
	public TagTreePopupHandler(JTree tree) {
		this.tree = tree;

		// get TagControl
		control = SwingClient.getInstance().getTagControl();

		// create menu
		menu = new JPopupMenu();
		JMenuItem rename = new JMenuItem("Rename...");
		rename.setIcon(Icons.TAG_EDIT);
		rename.addActionListener(new ActionListener() {
			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {
				renameTag();
			}
		});
		menu.add(rename);

		JMenuItem delete = new JMenuItem("Delete...");
		delete.setIcon(Icons.TAG_OR_TAGGROUP_REMOVE);
		delete.addActionListener(new ActionListener() {
			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {
				deleteTag();
			}
		});
		menu.add(delete);

		menu.addSeparator();

		JMenuItem addNewTag = new JMenuItem("Add Tag...");
		addNewTag.setIcon(Icons.TAG_ADD);
		addNewTag.setToolTipText("Add a new tag on same level");
		addNewTag.addActionListener(new ActionListener() {
			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {
				addNewTag();
			}
		});
		menu.add(addNewTag);

		JMenuItem addNewTagGroup = new JMenuItem("Add Category...");
		addNewTagGroup.setIcon(Icons.TAG_GROUP_ADD);
		addNewTagGroup.setToolTipText("Add a new category on same level");
		addNewTagGroup.addActionListener(new ActionListener() {
			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {
				addNewTagGroup();
			}
		});
		menu.add(addNewTagGroup);
	}

	/**
	 * showes a popup for the specified node
	 * 
	 * @param node
	 * @param x 
	 * @param y 
	 */
	private void showPopupForNode(TagTreeNode node, int x, int y) {
		// set victem and show Popup
		victim = node;
		menu.show(tree, x, y);
	}

	/**
	 * 
	 * call checkForPopup
	 * 
	 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		checkForPopup(e);
	}

	/**
	 * call checkForPopup
	 * 
	 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		checkForPopup(e);
	}

	/**
	 * 
	 * check if popup is triggered and show it
	 * 
	 * @param e
	 */
	private void checkForPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			// Show popup
			TreePath path = tree.getClosestPathForLocation(e.getX(), e.getY());
			TagTreeNode node = (TagTreeNode) path.getLastPathComponent();
			showPopupForNode(node, e.getX(), e.getY());
		}
	}

	/**
	 * To react on the context_menu key
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_CONTEXT_MENU) {
			TreePath path = tree.getSelectionPath();
			TagTreeNode node = (TagTreeNode) path.getLastPathComponent();
			Rectangle rectangle = tree.getPathBounds(path);
			showPopupForNode(node, rectangle.x + 40, rectangle.y + 20);
		}
	}

	/**
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@SuppressWarnings("unused")
	public void keyReleased(KeyEvent e) {
		// nothing - just for interface
	}

	/**
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@SuppressWarnings("unused")
	public void keyTyped(KeyEvent e) {
		// nothing - just for interface
	}

	/**
	 * rename menu entry
	 */
	private void renameTag() {
		tree.startEditingAtPath(new TreePath(victim.getPath()));
	}

	/**
	 * delete through parent
	 */
	private void deleteTag() {
		// delegate to central control
		TagGroup tag = victim.getTag();

		TagTreeNode node = (TagTreeNode) victim.getParent();
		TagGroup parent = node.getTag();

		// delegate delete
		control.deleteTag(parent, tag);
	}

	/**
	 * adds a new Tag
	 */
	private void addNewTag() {
		// get Parent tag
		// if user selected a TagGroup, child should be added
		TagGroup parent = victim.getTag();
		if (parent instanceof Tag) {
			// if user selected a Tag, the new tag should be on the same level
			TagTreeNode node = (TagTreeNode) victim.getParent();
			parent = node.getTag();
		}

		// the rest is done by central control
		control.addNewTag(parent);
	}

	/**
	 * adds a new TagGroup
	 */
	private void addNewTagGroup() {
		// get Parent Tag
		TagGroup parent;
		if (victim.getTag() instanceof Tag) {
			parent = ((TagTreeNode) victim.getParent()).getTag();
		} else {
			parent = victim.getTag();
		}

		// the rest is done by central control
		control.addNewTagGroup(parent);
	}

}
