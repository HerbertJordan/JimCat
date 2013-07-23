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

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.jimcat.gui.dndutil.ExceptionCatchingTransferHandler;
import org.jimcat.gui.dndutil.FilterTreeNodeSetWrapper;
import org.jimcat.gui.dndutil.FilterTreeTransferable;
import org.jimcat.gui.smartlisteditor.model.FilterTreeNode;
import org.jimcat.gui.smartlisteditor.model.GroupFilterTreeNode;

/**
 * The transfer handler for the filter tree, who is responsible for coordinating
 * drag & drop operations.
 * 
 * 
 * $Id$
 * 
 * @author Michael
 */
public class FilterTreeTransferHandler extends ExceptionCatchingTransferHandler {

	/**
	 * the supported dataFlavor
	 */
	private DataFlavor supported = new DataFlavor(FilterTreeNodeSetWrapper.class, "FilterTreeNodeSetWrapper");

	/**
	 * This method checks wheter the data brought by the drag can be imported.
	 * 
	 * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent,
	 *      java.awt.datatransfer.DataFlavor[])
	 */
	@Override
	public boolean safeCanImport(JComponent comp, DataFlavor[] transferFlavors) {
		if (comp instanceof JTree) {
			for (int i = 0; i < transferFlavors.length; i++) {
				if (supported.equals(transferFlavors[i])) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * This method is used to create transferable with the dragged data.
	 * 
	 * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
	 */
	@Override
	protected Transferable safeCreateTransferable(JComponent c) {
		Set<FilterTreeNode> nodesToTransfer = new HashSet<FilterTreeNode>();
		if (c instanceof JTree) {
			JTree tree = (JTree) c;
			TreePath[] paths = tree.getSelectionPaths();
			for (TreePath path : paths) {
				// for each selected node
				FilterTreeNode selectedNode = (FilterTreeNode) path.getLastPathComponent();
				// check if it is root - then it can't be dragged
				if (!selectedNode.isRoot()) {
					nodesToTransfer.add(selectedNode);
				}
			}
			if (!nodesToTransfer.isEmpty()) {
				return new FilterTreeTransferable(new FilterTreeNodeSetWrapper(nodesToTransfer));
			}
		}
		return null;
	}

	/**
	 * moving of filters is supported
	 * 
	 * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
	 */
	@Override
	@SuppressWarnings("unused")
	public int safeGetSourceActions(JComponent c) {
		return MOVE;
	}

	/**
	 * This method is called on drop and is responsible for importing the data
	 * brought by the drag, it calls canImport first to check if the data can be
	 * imported or not.
	 * 
	 * @see javax.swing.TransferHandler#importData(javax.swing.JComponent,
	 *      java.awt.datatransfer.Transferable)
	 */
	@Override
	public boolean safeImportData(JComponent comp, Transferable t) {
		FilterTreeNodeSetWrapper dataToImport;
		try {
			if (canImport(comp, t.getTransferDataFlavors())) {
				dataToImport = (FilterTreeNodeSetWrapper) t.getTransferData(supported);
				JTree tree = (JTree) comp;
				Point mousePosition = tree.getMousePosition();
				if (mousePosition == null) {
					return false;
				}
				TreePath targetPath = tree.getClosestPathForLocation(mousePosition.x, mousePosition.y);
				// TreePath targetPath = tree.getSelectionPath();
				FilterTreeNode targetNode = (FilterTreeNode) targetPath.getLastPathComponent();
				// when the target node is a group node just add nodes here
				if (targetNode instanceof GroupFilterTreeNode) {
					return addNodes((GroupFilterTreeNode) targetNode, dataToImport.getFilterTreeNodes(), -1);
				}
				// target node is a normal node
				int index = targetNode.getParent().getIndexOfChild(targetNode);
				targetNode = targetNode.getParent();
				return addNodes((GroupFilterTreeNode) targetNode, dataToImport.getFilterTreeNodes(), index);

			}
		} catch (UnsupportedFlavorException ufe) {
			// should not happen
			throw new RuntimeException("Failure when importing data in filter tree.", ufe);
		} catch (IOException ioe) {
			// should not happen
			throw new RuntimeException("Failure when importing data in filter tree.", ioe);
		}
		return false;
	}

	/**
	 * 
	 * This method is used to add the nodes specified in nodesToAdd to the
	 * target at the specified index. if index is -1 the nodes are added at the
	 * end
	 * 
	 * @param target
	 * @param nodesToAdd
	 * @param index
	 * @return true if adding was successfull
	 */
	private boolean addNodes(GroupFilterTreeNode target, Set<FilterTreeNode> nodesToAdd, int index) {
		int pos = index;
		if (pos == -1) {
			pos = target.getChildrenCount();
		}

		// check for circular dependencies
		for (FilterTreeNode nodeToAdd : nodesToAdd) {
			if (isPartOfPath(target, nodeToAdd)) {
				nodesToAdd.remove(nodeToAdd);
			}
		}

		if (nodesToAdd.isEmpty()) {
			return false;
		}

		target.addChildrenAtIndex(nodesToAdd, pos);
		return true;
	}

	/**
	 * 
	 * This method returns true if nodeToAdd is part of the upwards path of
	 * target, else false.
	 * 
	 * @param target
	 * @param nodeToAdd
	 * @return true if the nodeToAdd as a ancestor of traget
	 */
	private boolean isPartOfPath(FilterTreeNode target, FilterTreeNode nodeToAdd) {
		FilterTreeNode recursiveCheck = target;
		while (recursiveCheck != null) {
			if (recursiveCheck.equals(nodeToAdd)) {
				return true;
			}
			recursiveCheck = recursiveCheck.getParent();
		}
		return false;
	}

}
