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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;

import org.jimcat.gui.ViewControl;
import org.jimcat.gui.ViewFreezeListener;
import org.jimcat.gui.icons.Icons;

/**
 * A small toggle button to freeze current view.
 *
 * $Id$
 * @author Herbert
 */
public class FreezeSwitch extends JPanel {

	/**
	 * a reference to the view control
	 */
	private ViewControl control;
	
	/**
	 * the button to switch between freeze modes
	 */
	private JToggleButton button;
	
	/**
	 * create a new freeze switch panel
	 * @param control 
	 */
	public FreezeSwitch(ViewControl control) {
		this.control = control;
		control.addViewFreezeListener(new LockListener());
		
		initComponents();
	}
	
	/**
	 * build up component
	 */
	private void initComponents() {
		// Build Layout + Border
		setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		setBorder(new EmptyBorder(5, 10, 10, 15));

		button = new JToggleButton();
		button.setToolTipText("freeze filtered view to ignore value changes");
		button.setPreferredSize(new Dimension(20, 20));
		button.addActionListener(new ButtonListener());
		button.setFocusable(false);
		button.setSelectedIcon(Icons.VIEW_LOCKED);
		button.setIcon(Icons.VIEW_UNLOCK);
		add(button);
	}

	/**
	 * react on toogle button clicks
	 */
	private class ButtonListener implements ActionListener {
		/**
		 * react on button click
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent event) {
		    control.setFreezeLock(button.isSelected());
		}
	}
	
	/**
	 * a listener to observe the freezelock state with the view control
	 */
	private class LockListener implements ViewFreezeListener {
		/**
		 * update button state
		 * @see org.jimcat.gui.ViewFreezeListener#freezeStateChanged(org.jimcat.gui.ViewControl)
		 */
		public void freezeStateChanged(ViewControl viewControl) {
		    button.setSelected(viewControl.isFreezed());
		}
	}
	
}
