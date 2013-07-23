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

package org.jimcat.gui.smartlisteditor;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTree;
import org.jimcat.gui.smartlisteditor.model.FilterTreeNode;

/**
 * Creates the Popupmenu for the FilterTree
 * 
 * $Id: TagTreePopupHandler.java 202 2007-04-10 06:14:13Z 07g1t1u1 $
 * 
 * @author Herbert
 */
public class FilterTreePopupHandler extends MouseAdapter implements KeyListener {

	/**
	 * the filter tree popup menu
	 */
	private FilterTreePopupMenu menu;

	/**
	 * the tree this hanlder is installed to
	 */
	private JXTree tree;

	/**
	 * create a new handler for the given tree
	 * 
	 * @param editor
	 * @param tree
	 */
	public FilterTreePopupHandler(SmartListEditor editor, JXTree tree) {
		this.tree = tree;

		menu = new FilterTreePopupMenu(editor, tree);
	}

	/**
	 * 
	 * shows a popup
	 * 
	 * @param popupMenu
	 * @param x
	 * @param y
	 */
	private void showPopup(JPopupMenu popupMenu, int x, int y) {
		// just show Popup
		if (popupMenu != null) {
			popupMenu.show(tree, x, y);
		}
	}

	/**
	 * call check for popup
	 * 
	 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		checkForPopup(e);
	}

	/**
	 * call check for popup
	 * 
	 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		checkForPopup(e);
	}

	/**
	 * 
	 * check if event is a popup trigger and show popup
	 * 
	 * @param e
	 */
	private void checkForPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			// Show popup
			TreePath path = tree.getClosestPathForLocation(e.getX(), e.getY());
			tree.setSelectionPath(path);
			FilterTreeNode node = (FilterTreeNode) path.getLastPathComponent();
			menu.setCurrentFilterNode(node);
			showPopup(menu, e.getX(), e.getY());
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
			FilterTreeNode node = (FilterTreeNode) path.getLastPathComponent();
			menu.setCurrentFilterNode(node);
			Rectangle rectangle = tree.getPathBounds(path);
			showPopup(menu, rectangle.x + 40, rectangle.y + 10);
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

}
