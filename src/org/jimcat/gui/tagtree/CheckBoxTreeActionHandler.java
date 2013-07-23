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

import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTree;

/**
 * This class is responsible for appropriate selection.
 * 
 * Therefore, if you select a group, all subtags should be selected too.
 * 
 * $Id: CheckBoxTreeActionHandler.java 935 2007-06-15 09:21:09Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public class CheckBoxTreeActionHandler extends MouseAdapter implements KeyListener {

	/**
	 * performs the recursive selection
	 * 
	 * @param tree -
	 *            the tree
	 * @param node -
	 *            the node selected
	 */
	private void handleSelect(JXTree tree, TagTreeNode node) {
		// check if the tree is given
		if (tree == null) {
			return;
		}

		// check node
		if (node == null) {
			return;
		}

		// check if there are leafes in the subtree
		if (!hasTagLeafs(node)) {
			updateNode(node, CheckBoxState.UNSET);
		} else if (node.getChildCount() > 0) {
			// if node isn't a leaf, update Node and subnodes
			switch (node.getState()) {
			case SET: {
				updateNode(node, CheckBoxState.UNSET);
				break;
			}
			default: {
				updateNode(node, CheckBoxState.SET);
				break;
			}
			}
		} else {
			// if node is a leaf, just change state
			switch (node.getState()) {
			case SET: {
				node.setState(CheckBoxState.UNSET);
				break;
			}
			case UNSET: {
				node.setState(CheckBoxState.SET);
				break;
			}
			default: {
				break;
			}
			}
		}
		// update parent Node
		updateParent(node);

		// redraw
		EventQueue.invokeLater(new RedrawRunable(tree));

	}

	/**
	 * check if the given node has any childs containing a real tag
	 * 
	 * @param node
	 * @return true if the tag has children containing tags not tag groups
	 */
	private boolean hasTagLeafs(TagTreeNode node) {
		if (node.isLeaf()) {
			return true;
		}

		for (int i = 0; i < node.getChildCount(); i++) {
			TagTreeNode child = (TagTreeNode) node.getChildAt(i);
			if (hasTagLeafs(child)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * recursivelly updates a parent node state ...
	 * 
	 * @param node
	 */
	protected static void updateParent(TagTreeNode node) {
		if (node.getParent() != null) {
			TagTreeNode parent = (TagTreeNode) node.getParent();

			int set = 0;
			int not_unset = 0;
			for (int j = 0; j < parent.getChildCount(); j++) {

				TagTreeNode cur = (TagTreeNode) parent.getChildAt(j);

				if (!CheckBoxState.UNSET.equals(cur.getState())) {
					not_unset++;
				}
				if (CheckBoxState.SET.equals(cur.getState())) {
					set++;
				}
			}

			CheckBoxState res = CheckBoxState.PARTITAL;
			if (not_unset == 0) {
				res = CheckBoxState.UNSET;
			} else if (set == parent.getChildCount()) {
				res = CheckBoxState.SET;
			}

			if (parent.getState() != null && !parent.getState().equals(res)) {
				parent.setState(res);
				updateParent(parent);
			}
		}
	}

	/**
	 * update node and all children
	 * 
	 * @param node
	 * @param state
	 */
	private void updateNode(TagTreeNode node, CheckBoxState state) {
		node.setState(state);
		for (int i = 0; i < node.getChildCount(); i++) {
			TagTreeNode cur = (TagTreeNode) node.getChildAt(i);
			cur.setState(state);
			updateNode(cur, state);
		}

	}

	/**
	 * react on a MouseClick.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			// react on click just within bounds
			JXTree tree = (JXTree) e.getSource();
			TreePath path = tree.getClosestPathForLocation(e.getX(), e.getY());
			Rectangle bound = tree.getPathBounds(path);
			if (bound.contains(e.getPoint())) {
				handleSelect(tree, (TagTreeNode) path.getLastPathComponent());
			}
		}
	}

	/**
	 * just to implement interface, do nothing.
	 * @param e 
	 */
	public void keyPressed(KeyEvent e) {
		JTree tree = (JTree) e.getSource();
		if (e.getKeyCode() == KeyEvent.VK_F2) {
			TreePath selected = tree.getSelectionPath();
			if (selected != null) {
				tree.startEditingAtPath(selected);
			}
		} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			tree.stopEditing();
		}
	}

	/**
	 * just to implement interface, do nothing.
	 * @param e 
	 */
	@SuppressWarnings("unused")
	public void keyReleased(KeyEvent e) {
		// just to implement KeyListener
	}

	/**
	 * to reacto on space and enter
	 * 
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent e) {
		char key = e.getKeyChar();
		if (key == ' ' || key == '\n') {
			JXTree tree = (JXTree) e.getSource();
			TreePath path = tree.getSelectionPath();
			handleSelect(tree, (TagTreeNode) path.getLastPathComponent());
		}
	}

	/**
	 * a runable job for asynchronous paint within the swing dispatcher thread
	 * 
	 * @author Herbert
	 * 
	 */
	class RedrawRunable implements Runnable {

		private JComponent component = null;

		/**
		 * only constructor, requiring component to repaint
		 * 
		 * @param component
		 */
		public RedrawRunable(JComponent component) {
			this.component = component;
		}

		/**
		 * performing the job
		 */
		public void run() {
			if (component != null) {
				component.repaint();
			}
		}
	}
}
