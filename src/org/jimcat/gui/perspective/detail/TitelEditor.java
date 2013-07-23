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

package org.jimcat.gui.perspective.detail;

import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * A simple editor for editing image titels
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class TitelEditor extends DefaultCellEditor {

	/**
	 * the delay between two clicks there must be to activate editor
	 */
	private static final int MIN_EDIT_DELAY = 700;

	/**
	 * table working for
	 */
	private JTable table;
	
	/**
	 * the time when der wars a last selection change
	 */
	private long lastSelectionChange = 0;

	/**
	 * create new Titel edior for given DetailTable
	 * 
	 * @param table
	 */
	public TitelEditor(JTable table) {
		super(new JTextField());

		// init members
		this.table = table;
		table.getSelectionModel().addListSelectionListener(new SelectionObserver());
	}

	/**
	 * Start cell editing only under certain circumstances. (Time since last
	 * edit must be long enough.
	 * 
	 * @see javax.swing.DefaultCellEditor#isCellEditable(java.util.EventObject)
	 */
	@Override
	public boolean isCellEditable(EventObject event) {
		// check if event is edit-starting event
		if (event instanceof MouseEvent) {
			MouseEvent mouseEvent = (MouseEvent) event;
			
			// check if row is selected
			int index = table.rowAtPoint(mouseEvent.getPoint());
			if (!table.getSelectionModel().isSelectedIndex(index)) {
				return false;
			}
			
			// check if its wasn't a dubble click
			long time = mouseEvent.getWhen();
			int clicks = mouseEvent.getClickCount();
			if (time - lastSelectionChange >= MIN_EDIT_DELAY && clicks == 1) {
				return true;
			}
			return false;
		}
		// delegate to parent
		return super.isCellEditable(event);
	}
	
	/**
	 * private class observing selection models
	 */
	private class SelectionObserver implements ListSelectionListener {
		/**
		 * listening to selection model and remember change
		 * 
		 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
		 */
		@SuppressWarnings("unused")
		public void valueChanged(ListSelectionEvent e) {
			// remember change
			lastSelectionChange = System.currentTimeMillis();
		}
	}
}
