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

package org.jimcat.gui.wheellist;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ListSelectionModel;

/**
 * Handle Key-Events for the WheelList.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class WheelListKeyHandler extends KeyAdapter {

	/**
	 * the board this handler is installed on
	 */
	private WheelList board;

	/**
	 * the model altered
	 */
	private ListSelectionModel model;

	/**
	 * a constructor requesting all required fields
	 * 
	 * @param board
	 * @param selectionModel
	 */
	public WheelListKeyHandler(WheelList board, ListSelectionModel selectionModel) {
		super();
		this.board = board;
		this.model = selectionModel;
	}

	/**
	 * Allow user to navigate through keys.
	 * 
	 * @see java.awt.event.KeyAdapter#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {

		// the current index
		int max = board.getModel().getSize() - 1;
		int currentIndex = board.getFocusIndex();

		int columnCount = board.getCurrentColumnCount();

		int newStartIndex = currentIndex;
		int newEndIndex = currentIndex;

		switch (e.getKeyCode()) {
		case KeyEvent.VK_DOWN: {
			newStartIndex = Math.min(currentIndex + columnCount, max);
			newEndIndex = newStartIndex;
			break;
		}
		case KeyEvent.VK_UP: {
			newStartIndex = Math.max(currentIndex - columnCount, 0);
			newEndIndex = newStartIndex;
			break;
		}
		case KeyEvent.VK_RIGHT: {
			newStartIndex = Math.min(currentIndex + 1, max);
			newEndIndex = newStartIndex;
			break;
		}
		case KeyEvent.VK_LEFT: {
			newStartIndex = Math.max(currentIndex - 1, 0);
			newEndIndex = newStartIndex;
			break;
		}
		case KeyEvent.VK_A: {
			if (e.isControlDown()) {
				newStartIndex = 0;
				newEndIndex = max;
			} else {
				return;
			}
			break;
		}
		default:
			// do nothing
			return;
		}

		// update selection
		model.setSelectionInterval(newStartIndex, newEndIndex);
		if (newStartIndex == newEndIndex) {
			board.setFocusIndex(newStartIndex);
		}
	}

}
