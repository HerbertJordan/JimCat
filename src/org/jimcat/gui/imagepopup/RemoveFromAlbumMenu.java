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
import org.jimcat.model.Album;

/**
 * The submenu used to remove the currently selected images from a selectable
 * album.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class RemoveFromAlbumMenu extends ImagePopupAlbumMenu {

	/**
	 * a reference to the installed ImageControl
	 */
	private ImageControl control = SwingClient.getInstance().getImageControl();

	/**
	 * do the magic - remove images from album
	 * 
	 * @see org.jimcat.gui.imagepopup.ImagePopupAlbumMenu#elementSelected(org.jimcat.model.Album)
	 */
	@Override
	public void elementSelected(Album album) {
		// delegate
		control.removeSelectionFromAlbum(album);
	}

}
