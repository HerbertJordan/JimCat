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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jimcat.gui.SwingClient;
import org.jimcat.gui.ViewControl;
import org.jimcat.gui.wheellist.WheelList;
import org.jimcat.gui.wheellist.WheelListItemFactory;
import org.jimcat.model.Image;

/**
 * The container for a set of items.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class Board extends JPanel {

	/**
	 * a reference to the underlaying view control
	 */
	private ViewControl control;

	/**
	 * the list used to display items
	 */
	private WheelList<Image> list;

	/**
	 * the board model for the wheelList
	 */
	private BoardModel model;

	/**
	 * the renderer to use
	 */
	private WheelListItemFactory<Image> factory;

	/**
	 * default constructor, using maximum Thumbnail size
	 * @param control 
	 * @param factory 
	 */
	public Board(ViewControl control, WheelListItemFactory<Image> factory) {
		// int members
		this.control = control;
		this.factory = factory;

		// build up content
		initComponents();
	}

	/**
	 * build up content
	 */
	private void initComponents() {
		setDoubleBuffered(true);
		setLayout(new BorderLayout());
		setBorder(null);

		// init list
		model = new BoardModel(control.getLibraryView());
		list = new WheelList<Image>(factory, model, control.getSelectionModel());
		list.setOpaque(false);
		// install event handler
		list.addMouseMotionListener(new WheelListMouseMotionListener());
		list.addKeyListener(new BoardKeyAdapter());
		list.setTransferHandler(new BoardWithWheelListTransferHandler(this, this.list));
		RestoreComponentStateDropTargetListener.addRestoreComponentStateDropTargetListener(this, list.getDropTarget());
		// list.setSelectionModel(control.getSelectionModel());

		// put into scroll pane
		JScrollPane pane = new JScrollPane();
		pane.setViewportView(list);
		pane.setOpaque(false);
		pane.setBorder(null);
		pane.getViewport().setOpaque(false);
		add(pane, BorderLayout.CENTER);
	}

	/**
	 * change item size
	 * 
	 * @param itemSize
	 */
	public void setItemSize(Dimension itemSize) {
		list.setItemSize(itemSize);
	}

	/**
	 * get used view control
	 * 
	 * @return the ViewControl
	 */
	public ViewControl getControl() {
		return control;
	}

	/**
	 * @param point
	 * @return the index at the specified point
	 * @see org.jimcat.gui.wheellist.WheelList#getIndexAt(java.awt.Point)
	 */
	public int getIndexAt(Point point) {
		return list.getIndexAt(point);
	}

	/**
	 * @param active
	 * @see org.jimcat.gui.perspective.boards.BoardModel#setActive(boolean)
	 */
	public void setActive(boolean active) {
		model.setActive(active);

		// update window viewport after this event is handeld
		EventQueue.invokeLater(new Runnable() {
			/**
			 * move visible window to right position
			 * 
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				list.setFocusIndex(list.getFocusIndex());
			}
		});
	}

	/**
	 * a mouse motion listener to initiate drag n drop
	 */
	private class WheelListMouseMotionListener extends MouseMotionAdapter {

		/**
		 * initiate drag'n'drop
		 * 
		 * @see java.awt.event.MouseMotionAdapter#mouseDragged(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseDragged(MouseEvent e) {
			if (list.getComponentAt(e.getPoint()) == null || list.getComponentAt(e.getPoint()).equals(list)) {
				// if there is no image at that point return
				return;
			}
			list.getTransferHandler().exportAsDrag(list, e, list.getTransferHandler().getSourceActions(list));
		}
	}

	/**
	 * To react on Enter events in a similar way as the other perpectives do, a
	 * key listener has to be installed who listens for enter events and opens
	 * fullscreen.
	 * 
	 * 
	 * $Id: DetailTable.java 885 2007-06-09 14:33:06Z 07g1t1u2 $
	 * 
	 * @author Michael
	 */
	private final class BoardKeyAdapter extends KeyAdapter {
		/**
		 * Open fullscreen on enter event.
		 * 
		 * @see java.awt.event.KeyAdapter#keyPressed(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				SwingClient.getInstance().showFullScreen();
			}
		}
	}

}
