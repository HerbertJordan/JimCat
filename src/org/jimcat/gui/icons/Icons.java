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

package org.jimcat.gui.icons;

import javax.swing.ImageIcon;

/**
 * This enumeration contains all Icons used in the GUI.
 * 
 * All icons used in the GUI are listed here, so that the loading of them is
 * centralized. This means that only here the concrete position of the icons in
 * the file system has to be known, whereas everywhere else the abstraction
 * level rises.
 * 
 * $Id$
 * 
 * @author Michael Handler
 */
public final class Icons {

	public static final ImageIcon JOB_CANCEL = load("cancel");

	public static final ImageIcon JOB_ROLLBACK = load("rollback");

	public static final ImageIcon JOB_RUN = load("run");

	public static final ImageIcon JOB_SUSPEND = load("suspend");

	public static final ImageIcon VIEW_LIST = load("application_view_list");

	public static final ImageIcon VIEW_THUMBNAILS = load("application_view_tile");
	
	public static final ImageIcon VIEW_CARDS = load("application_view_cards");

	public static final ImageIcon ALL_IMAGES = load("images");
	
	public static final ImageIcon IMAGE_EDIT = load("picture_edit");

	public static final ImageIcon ALBUM = load("image");

	public static final ImageIcon ALBUM_ADD = load("image_add");

	public static final ImageIcon ALBUM_REMOVE = load("image_delete");

	public static final ImageIcon ALBUM_EDIT = load("image_edit");

	public static final ImageIcon SMARTLIST = load("smartlist");

	public static final ImageIcon SMARTLIST_ADD = load("smartlist_add");

	public static final ImageIcon SMARTLIST_REMOVE = load("smartlist_delete");

	public static final ImageIcon SMARTLIST_EDIT = load("smartlist_edit");

	public static final ImageIcon FULLSCREEN = load("monitor");
	
	public static final ImageIcon CLOSE_FULLSCREEN = load("monitor_cross");

	public static final ImageIcon TAG = load("tag_blue");

	public static final ImageIcon TAG_ADD = load("tag_blue_add");

	public static final ImageIcon TAG_REMOVE = load("tag_blue_delete");

	public static final ImageIcon TAG_OR_TAGGROUP_REMOVE = load("delete");

	public static final ImageIcon TAG_REMOVE_ASSOCIATED = load("bin_closed");

	public static final ImageIcon TAG_EDIT =  load("text_replace");

	public static final ImageIcon TAG_GROUP_ADD = load("book_add");

	public static final ImageIcon IMPORT = load("folder_go_right");

	public static final ImageIcon EXPORT = load("folder_go_left");

	public static final ImageIcon PRINT = load("printer");

	public static final ImageIcon DELETE_FROM_LIBRARY = load("delete");

	public static final ImageIcon DELETE_FROM_DISK = load("delete");

	public static final ImageIcon ROTATE_LEFT = load("shape_rotate_anticlockwise");

	public static final ImageIcon ROTATE_RIGHT = load("shape_rotate_clockwise");

	public static final ImageIcon RENAME = load("text_replace");

	public static final ImageIcon UPDATE_IMAGE = load("page_refresh");

	public static final ImageIcon VIEW_LOCKED = load("lock");
	
	public static final ImageIcon VIEW_UNLOCK = load("lock_open");
	

	private Icons() {
		/* hide */
	}

	private static ImageIcon load(String icon) {
		return new ImageIcon(Icons.class.getResource(icon + ".png"));
	}

}
