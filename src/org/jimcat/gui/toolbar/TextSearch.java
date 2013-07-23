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

package org.jimcat.gui.toolbar;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jimcat.gui.SwingClient;
import org.jimcat.gui.ViewControl;
import org.jimcat.gui.ViewFilterListener;
import org.jimcat.model.filter.metadata.TextFilter;

/**
 * The textserach field in the uper right corner
 * 
 * $Id: TextSearch.java 935 2007-06-15 09:21:09Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public class TextSearch extends JPanel implements DocumentListener, ViewFilterListener {

	/**
	 * internal viewcontrol events should be send to
	 */
	private ViewControl control;

	/**
	 * searchfield
	 */
	private JTextField searchField;

	/**
	 * determines if the last change was triggert by this component
	 */
	private boolean changeTriggered = false;

	/**
	 * a direct constructor
	 * 
	 * @param control
	 */
	public TextSearch(ViewControl control) {
		this.control = control;

		// build up component
		initComponents();

		// register to viewControl
		control.addViewFilterListener(this);
	}

	private void initComponents() {
		setLayout(new BorderLayout());
		setOpaque(false);

		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 10, 3, 10));
		panel.setLayout(new BorderLayout());

		searchField = new JTextField(20);
		searchField.getDocument().addDocumentListener(this);
		panel.add(searchField, BorderLayout.CENTER);

		add(panel, BorderLayout.NORTH);

		// status bar
		SelectionInfo info = new SelectionInfo(SwingClient.getInstance());
		add(info, BorderLayout.SOUTH);
	}

	/**
	 * update TextFilter
	 */
	private synchronized void updateTextFilter() {
		changeTriggered = true;
		TextFilter filter = null;
		if (!searchField.getText().equals("")) {
			filter = new TextFilter(searchField.getText());
		}
		control.setTextFilter(filter);
		changeTriggered = false;
	}

	/**
	 * Use event to update textfilter
	 * 
	 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
	 */
	@SuppressWarnings("unused")
	public void changedUpdate(DocumentEvent e) {
		updateTextFilter();
	}

	/**
	 * Use event to update textfilter
	 * 
	 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
	 */
	@SuppressWarnings("unused")
	public void insertUpdate(DocumentEvent e) {
		updateTextFilter();
	}

	/**
	 * Use event to update textfilter
	 * 
	 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
	 */
	@SuppressWarnings("unused")
	public void removeUpdate(DocumentEvent e) {
		updateTextFilter();
	}

	/**
	 * update the text if other call changed current filter view
	 * 
	 * @see org.jimcat.gui.ViewFilterListener#filterChanges(org.jimcat.gui.ViewControl)
	 */
	public synchronized void filterChanges(ViewControl viewControl) {
		if (!changeTriggered) {
			TextFilter filter = viewControl.getTextFilter();
			if (filter == null) {
				searchField.setText("");
			} else {
				searchField.setText(filter.getPattern());
			}
		}
	}
}
