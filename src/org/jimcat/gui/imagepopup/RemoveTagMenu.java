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

package org.jimcat.gui.imagepopup;

import org.jimcat.gui.ImageControl;
import org.jimcat.gui.SwingClient;
import org.jimcat.model.tag.Tag;

/**
 * This menu can be used to remove a tag from the currently selected images.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class RemoveTagMenu extends ImagePopupTagMenu {

	/**
	 * a reference to the installed ImageControl
	 */
	private ImageControl control = SwingClient.getInstance().getImageControl();

	/**
	 * this methode is removing the selected tag from all selected images.
	 * 
	 * @see org.jimcat.gui.imagepopup.ImagePopupTagMenu#elementSelected(org.jimcat.model.tag.Tag)
	 */
	@Override
	public void elementSelected(Tag tag) {
		// delegate command
		control.removeTagFromSelection(tag);
	}

}
