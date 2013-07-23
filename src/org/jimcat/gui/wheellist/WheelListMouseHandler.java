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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ListSelectionModel;

import org.jimcat.gui.SwingClient;
import org.jimcat.gui.imagepopup.ImagePopupMenu;

/**
 * This class is introducing mouse options to the WheelList.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class WheelListMouseHandler extends MouseAdapter {

	/**
	 * the board this handler is installed on
	 */
	private WheelList board;

	/**
	 * the selection model the board is usings
	 */
	private ListSelectionModel selectionModel;

	/**
	 * the last index user has clicked on
	 */
	private int lastIndex = 0;

	/**
	 * a dircet constructor getting all required references
	 * 
	 * @param board
	 * @param selectionModel
	 */
	public WheelListMouseHandler(WheelList board, ListSelectionModel selectionModel) {
		this.board = board;
		this.selectionModel = selectionModel;
	}

	/**
	 * support selection by mouse
	 * 
	 * @see java.awt.event.MouseAdapter#mousePressed(MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {

		// get focus on component
		if (!board.hasFocus()) {
			board.requestFocusInWindow();
		}

		// react on selection
		if (e.getButton() == MouseEvent.BUTTON1) {

			int index = board.getIndexAt(e.getPoint());
			if (index<0) {
				return;
			}
			
			board.setFocusIndex(index);

			if (e.isControlDown()) {
				// if control is down, add interval to current selection
				if (selectionModel.isSelectedIndex(index)) {
					selectionModel.removeSelectionInterval(index, index);
				} else {
					selectionModel.addSelectionInterval(index, index);
				}
				lastIndex = index;
			} else if (e.isShiftDown()) {
				// if shift is down, select interval from last first click on
				selectionModel.setSelectionInterval(lastIndex, index);
			} else {
				// default, replace current selection
				if (!selectionModel.isSelectedIndex(index)) {
					selectionModel.setSelectionInterval(index, index);
				}
				lastIndex = index;
			}
		}

		checkForPopup(e);
	}

	/**
	 * check for Popup trigger
	 * 
	 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// react on selection
		if (e.getButton() == MouseEvent.BUTTON1) {

			int index = board.getIndexAt(e.getPoint());
			if (index<0) {
				return;
			}
			
			board.setFocusIndex(index);

			if (e.isControlDown()) {
				//do nothing
			} else if (e.isShiftDown()) {
				// do nothing
			} else {
				// default, replace current selection
				if (selectionModel.isSelectedIndex(index)) {
					selectionModel.setSelectionInterval(index, index);
				}
				lastIndex = index;
			}

			board.setFocusIndex(index);
		}
		checkForPopup(e);
	}

	/**
	 * react on double click
	 * 
	 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// if it was a left double click => open fullscreen view
		if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
			SwingClient.getInstance().showFullScreen();
		}
	}

	/**
	 * internal methode to open popup
	 * 
	 * @param e
	 */
	private void checkForPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			// show popup
			ImagePopupMenu menu = ImagePopupMenu.getInstance();
			int index = board.getIndexAt(e.getPoint());
			menu.show(board, index, e.getPoint());
		}
	}
}
