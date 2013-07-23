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

package org.jimcat.gui.albumlist;

import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;

import org.jimcat.gui.ViewControl;
import org.jimcat.gui.icons.Icons;
import org.jimcat.model.Album;
import org.jimcat.model.filter.AlbumFilter;
import org.jimcat.model.notification.BeanChangeEvent;
import org.jimcat.model.notification.BeanListener;
import org.jimcat.model.notification.BeanProperty;


/**
 * A singel node (leaf) of the album tree representing a album.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class AlbumTreeAlbumNode extends AlbumTreeNode implements BeanListener<Album> {

	/**
	 * the popupmenu used by this component
	 */
	private static AlbumPopupMenu popupMenu = new AlbumPopupMenu();

	/**
	 * the represented album
	 */
	private Album album;

	/**
	 * a direct constructor requesting all necessary fields
	 * 
	 * @param model -
	 *            the containing model
	 * @param parent -
	 *            the parent of this node
	 * @param album -
	 *            the represented list
	 */
	public AlbumTreeAlbumNode(AlbumTreeModel model, AlbumTreeNode parent, Album album) {
		super(model, parent, true);

		// init members
		this.album = album;
		album.addListener(this);

		setTitel(album.getName());
	}

	/**
	 * this will disconect this component from all observed elements call it if
	 * you do not require this node any more.
	 */
	public void dispose() {
		album.removeListener(this);
	}

	/**
	 * there are no children
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#getChildrenAt(int)
	 */
	@Override
	@SuppressWarnings("unused")
	public AlbumTreeNode getChildrenAt(int index) {
		return null;
	}

	/**
	 * there are no children => just returnes 0
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#getChildrenCount()
	 */
	@Override
	public int getChildrenCount() {
		return 0;
	}

	/**
	 * there are no children => returns -1
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#getIndexOfChild(org.jimcat.gui.albumlist.AlbumTreeNode)
	 */
	@Override
	@SuppressWarnings("unused")
	public int getIndexOfChild(AlbumTreeNode child) {
		return -1;
	}

	/**
	 * The popup menu for this component
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#getPopupMenu()
	 */
	@Override
	public JPopupMenu getPopupMenu() {
		popupMenu.setCurrentAlbumNode(this);
		return popupMenu;
	}

	/**
	 * This component is alwayes editable
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#isEditable()
	 */
	@Override
	public boolean isEditable() {
		return true;
	}

	/**
	 * If somebody select this component, the viewfilter should be updated.
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#performSelection(org.jimcat.gui.ViewControl)
	 */
	@Override
	public void performSelection(ViewControl control) {
		// create and set filter
		AlbumFilter albumFilter = new AlbumFilter(album);
		control.setAlbumFilter(albumFilter);
	}

	/**
	 * exchange the name of this component
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#setValue(java.lang.String)
	 */
	@Override
	public void setValue(String value) {
		// just set name
		album.setName(value);
	}

	/**
	 * if the album changes its name, update the titel of this node
	 * 
	 * @see org.jimcat.model.notification.BeanListener#beanPropertyChanged(org.jimcat.model.notification.BeanChangeEvent)
	 */
	public void beanPropertyChanged(BeanChangeEvent<Album> event) {
		// get source
		Album source = event.getSource();
		// to be save
		if (source != album) {
			source.removeListener(this);
		}

		// it is the right source
		if (event.getProperty() == BeanProperty.ALBUM_NAME) {
			setTitel(album.getName());
		}

		// inform TreeModel listener
		// done by root
	}

	/**
	 * @return the album
	 */
	public Album getAlbum() {
		return album;
	}

	/**
     * Return the icon for the albumTreeAlbumNode
     * @see org.jimcat.gui.albumlist.AlbumTreeNode#getIcon()
     */
    @Override
    public ImageIcon getIcon() {
	    return Icons.ALBUM;
    }
}
