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

package org.jimcat.gui.perspective.boards;

import java.awt.Point;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.TooManyListenersException;

import javax.swing.ListSelectionModel;

/**
 * The RestoreComponentStateDropTargetListener is used to save the selection of
 * a perspective on dragEnter and restore it on dragExit. This class is used by
 * a board so it needs the board as parameter.
 * 
 * It stores the selection on dragEnter in an list of integers and restores it
 * on dragExit.
 * 
 * $Id$
 * 
 * @author Michael
 */
public class RestoreComponentStateDropTargetListener extends DropTargetAdapter {
	/**
	 * a list of the selected Images
	 */
	private List<Integer> selectedImages;

	/**
	 * the board (needed to handle selections)
	 */
	private Board board;

	/**
	 * 
	 * private Constructor called by addRestoreComponentStateDropTargetListener
	 * 
	 * @param board
	 */
	private RestoreComponentStateDropTargetListener(Board board) {
		this.board = board;
	}

	/**
	 * 
	 * To add a RestoreComponentStateDropTargetListener to a dropTarget
	 * 
	 * @param board
	 * @param dropTarget
	 */
	public static void addRestoreComponentStateDropTargetListener(Board board, DropTarget dropTarget) {
		try {
			dropTarget.addDropTargetListener(new RestoreComponentStateDropTargetListener(board));
		} catch (TooManyListenersException tmle) {
			// should not happen
			throw new RuntimeException("Too many drop target listeners", tmle);
		}
	}

	/**
	 * Needed by adapter, no use for us here.
	 * 
	 * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
	 */
	public void drop(@SuppressWarnings("unused")
	DropTargetDropEvent dtde) {
		// no use for us here
	}

	/**
	 * on dragEnter the current selection is stored
	 * 
	 * @see java.awt.dnd.DropTargetAdapter#dragEnter(java.awt.dnd.DropTargetDragEvent)
	 */
	@Override
	public void dragEnter(@SuppressWarnings("unused")
	DropTargetDragEvent dtde) {
		// store selection
		this.selectedImages = new LinkedList<Integer>();
		ListSelectionModel selectionModel = board.getControl().getSelectionModel();
		for (int i = 0; i <= selectionModel.getMaxSelectionIndex(); i++) {
			if (selectionModel.isSelectedIndex(i))
				selectedImages.add(new Integer(i));
		}
	}

	/**
	 * on dragExit the selection before dragEnter is restored
	 * 
	 * @see java.awt.dnd.DropTargetAdapter#dragExit(java.awt.dnd.DropTargetEvent)
	 */
	@Override
	public void dragExit(@SuppressWarnings("unused")
	DropTargetEvent dte) {
		// reset the selection
		ListSelectionModel selectionModel = board.getControl().getSelectionModel();
		selectionModel.clearSelection();
		if (selectedImages != null) {
			for (int x : selectedImages) {
				selectionModel.addSelectionInterval(x, x);
			}
		}
		this.selectedImages = null;
	}

	/**
	 * Change selection to current mousePosition
	 * 
	 * @see java.awt.dnd.DropTargetAdapter#dragOver(java.awt.dnd.DropTargetDragEvent)
	 */
	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		ListSelectionModel selectionModel = board.getControl().getSelectionModel();
		Point mousePos = dtde.getLocation();
		Integer curSel = new Integer(board.getIndexAt(mousePos));
		if (curSel.intValue() >= 0 && !selectedImages.contains(curSel)) {
			selectionModel.setSelectionInterval(curSel.intValue(), curSel.intValue());
		} else {
			selectionModel.clearSelection();
			for (int x : selectedImages) {
				selectionModel.addSelectionInterval(x, x);
			}
		}

	}

}
