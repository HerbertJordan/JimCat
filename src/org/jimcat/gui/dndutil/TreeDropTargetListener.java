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

package org.jimcat.gui.dndutil;

import java.awt.Point;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TooManyListenersException;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * This class is used to recheck drop ability on mouse movement inside the
 * current component and to expand a tree-node after a given amount of time.
 * 
 * 
 * 
 * $Id$
 * 
 * @author Herbert, Michael
 */
public class TreeDropTargetListener extends DropTargetAdapter {

	/**
	 * The time to wait until expanding a tree node in milliseconds
	 */
	private static long waitFor = 600;

	/**
	 * The path over which the mouse is currently positioned
	 */
	private TreePath currentPath;

	/**
	 * the tree to which this listener is registered
	 */
	private JTree tree;

	/**
	 * the timer used for expanding paths
	 */
	private Timer timer;

	/**
	 * a instance of the class expandNode.
	 */
	private ExpandNode expander;

	/**
	 * 
	 * private default constructor, constructing should be done by
	 * addNewRecheckListener method
	 * 
	 * @param tree
	 */
	private TreeDropTargetListener(JTree tree) {
		this.tree = tree;
		tree.addMouseListener(new TreeMouseListener());
		this.timer = new Timer();
		this.currentPath = null;
		// private constructor setting the tree
	}

	/**
	 * 
	 * Add a new treeListener to a tree
	 * 
	 * @param tree
	 */
	public static void addNewTreeDropTargetListener(JTree tree) {
		try {
			DropTarget dropTarget = tree.getDropTarget();
			dropTarget.addDropTargetListener(new TreeDropTargetListener(tree));
		} catch (TooManyListenersException e) {
			// should not happen
			new RuntimeException("Too many listeners for drop target.", e);
		}
	}

	/**
	 * On drag Enter check if the current position is a new one, if yes. Stop a
	 * running timer, if there is one and start a new one for the new current
	 * node.
	 * 
	 * @see java.awt.dnd.DropTargetAdapter#dragEnter(java.awt.dnd.DropTargetDragEvent)
	 */
	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		// get the mouse position
		Point mousePos = dtde.getLocation();
		// if null do nothing
		if (mousePos == null) {
			return;
		}
		// test if curentPath is one of those that shall not be expanded
		TreePath newPath = tree.getPathForLocation(mousePos.x, mousePos.y);

		// if there is no currentPath
		if (currentPath == null) {
			expander = new ExpandNode();
			currentPath = newPath;
			timer.schedule(expander, waitFor);
		} else if (currentPath.equals(newPath)) {
			// do nothing
		} else {
			expander.cancel();
			expander = new ExpandNode();
			currentPath = newPath;
			timer.schedule(expander, waitFor);
		}
	}

	/**
	 * This method is called when the mouse leaves the drop target. When that
	 * happens the timer is cancled.
	 * 
	 * @see java.awt.dnd.DropTargetAdapter#dragExit(java.awt.dnd.DropTargetEvent)
	 */
	@Override
	@SuppressWarnings("unused")
	public void dragExit(DropTargetEvent dte) {
		expander.cancel();
		timer.purge();
	}

	/**
	 * When drop is called the timer is cancled.
	 * 
	 * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
	 */
	@SuppressWarnings("unused")
	public void drop(DropTargetDropEvent dtde) {
		expander.cancel();
		timer.purge();
	}

	/**
	 * The dragOver method is overwritten to make sure that revalidation of drop
	 * ability is checked also if mouse is moved within the current component.
	 * 
	 * @see java.awt.dnd.DropTargetAdapter#dragOver(java.awt.dnd.DropTargetDragEvent)
	 */
	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		DropTarget target = dtde.getDropTargetContext().getDropTarget();
		target.dragEnter(dtde);
	}

	/**
	 * 
	 * The ExpandNode class has a run mehtod which expands the path specified by
	 * currentPath.
	 * 
	 * 
	 * $Id$
	 * 
	 * @author Michael
	 */
	private class ExpandNode extends TimerTask {

		/**
		 * is used to expand the currentPath.
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			tree.expandPath(currentPath);
		}

	}

	/**
	 * a private listener waiting for events witch allow to cancel the expander
	 * timer
	 */
	private class TreeMouseListener extends MouseAdapter {

		/**
		 * Cancle timer on mouse released
		 * 
		 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
		 */
		@Override
		@SuppressWarnings("unused")
		public void mouseReleased(MouseEvent e) {
			if (expander != null) {
				expander.cancel();
			}
			timer.purge();
		}

	}

}
