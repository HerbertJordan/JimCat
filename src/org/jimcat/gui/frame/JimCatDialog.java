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

package org.jimcat.gui.frame;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

/**
 * Common JimCat Dialog Base
 * 
 * $Id$
 * 
 * @author csag1760
 */
public abstract class JimCatDialog extends JDialog {

	/**
	 * Construct a new JimCatDialog for given owner frame. You may also specify
	 * if this dialog should be modal.
	 * 
	 * @see JimCatDialog#JimCatDialog(Frame, String, boolean)
	 * 
	 * @param frame
	 * @param modal
	 */
	public JimCatDialog(JFrame frame, boolean modal) {
		this(frame, "", modal);

	}

	/**
	 * 
	 * Construct a new JimCatDialog for the given owner frame, with this title
	 * and the given modal. The constructor of the father class is called with
	 * those parameter, and the method initComponents is invoked.
	 * 
	 * @param owner
	 * @param title
	 * @param modal
	 * 
	 */
	public JimCatDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		initComponents();
	}

	/**
	 * Init components will install an EscapeKeyListener
	 */
	private void initComponents() {
		ActionMap am = getRootPane().getActionMap();
		InputMap im = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		Object windowCloseKey = new Object();
		KeyStroke windowCloseStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		Action windowCloseAction = new EscapeKeyListener();
		im.put(windowCloseStroke, windowCloseKey);
		am.put(windowCloseKey, windowCloseAction);
	}

	/**
	 * 
	 * A key listener for escape keys.
	 * 
	 * 
	 * $Id$
	 * 
	 * @author Christoph
	 */
	private class EscapeKeyListener extends AbstractAction {
		/**
		 * Hide the dialog when an escape key is pressed.
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
		}
	}
}
