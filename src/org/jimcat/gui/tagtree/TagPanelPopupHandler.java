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

package org.jimcat.gui.tagtree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.jimcat.gui.SwingClient;
import org.jimcat.gui.TagControl;
import org.jimcat.gui.icons.Icons;

/**
 * this handler manages the creation of a pop menu to create tags anywhere.
 * 
 * Install this to a JPanel / JTaskPane containing a TagTree.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class TagPanelPopupHandler extends MouseAdapter implements ActionListener {

	/**
	 * commands used to distinguish between menu entries
	 */
	private static final String COMMAND_ADD_GROUP = "addgroup";

	private static final String COMMAND_ADD_TAG = "addtag";

	/**
	 * a reference to a central TagControl
	 */
	private TagControl control;

	/**
	 * component this handler belongs to
	 */
	private JComponent component;

	/**
	 * popup menu
	 */
	private JPopupMenu menu;

	/**
	 * Creates a new PopupHandler
	 * @param component 
	 */
	public TagPanelPopupHandler(JComponent component) {
		this.component = component;
		// get TagControl
		control = SwingClient.getInstance().getTagControl();

		// create menu
		menu = new JPopupMenu();

		JMenuItem addTag = new JMenuItem("Add Root Tag...");
		addTag.setIcon(Icons.TAG_ADD);
		addTag.setActionCommand(COMMAND_ADD_TAG);
		addTag.addActionListener(this);
		menu.add(addTag);

		JMenuItem addGroup = new JMenuItem("Add Root Category...");
		addGroup.setIcon(Icons.TAG_GROUP_ADD);
		addGroup.setActionCommand(COMMAND_ADD_GROUP);
		addGroup.addActionListener(this);
		menu.add(addGroup);
	}

	/**
	 * call checkForPopup
	 * 
	 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		checkForPopup(e);
	}

	/**
	 * call checkForPopup
	 * 
	 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		checkForPopup(e);
	}

	/**
	 * 
	 * check if event is a popup trigger and show popup
	 * 
	 * @param e
	 */
	private void checkForPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			// Show popup
			menu.show(component, e.getX(), e.getY());
		}
	}

	/**
	 * this is reciving any selected option
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (COMMAND_ADD_TAG.equals(command)) {
			addNewTag();
		} else if (COMMAND_ADD_GROUP.equals(command)) {
			addNewTagGroup();
		}
	}

	/**
	 * adds a new Tag
	 */
	private void addNewTag() {
		// add a new node beneath the Tag root
		control.addNewTag(control.getTagTreeRoot());
	}

	/**
	 * adds a new TagGroup
	 */
	private void addNewTagGroup() {
		// add a new group beneath the Tag root
		control.addNewTagGroup(control.getTagTreeRoot());
	}

}
