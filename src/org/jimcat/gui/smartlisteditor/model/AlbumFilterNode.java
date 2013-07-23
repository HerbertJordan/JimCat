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

package org.jimcat.gui.smartlisteditor.model;

import org.apache.commons.lang.ObjectUtils;
import org.jimcat.model.Album;
import org.jimcat.model.filter.AlbumFilter;
import org.jimcat.model.filter.Filter;
import org.jimcat.model.filter.logical.NotFilter;

/**
 * FilterTree representation of an AlbumFilter
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class AlbumFilterNode extends FilterTreeNode {

	/**
	 * the album to filter for
	 */
	private Album album;

	/**
	 * generate a node from a given AlbumFilter
	 * 
	 * @param parent
	 * @param filter
	 */
	public AlbumFilterNode(GroupFilterTreeNode parent, AlbumFilter filter) {
		super(parent, true);

		// setup members
		this.album = filter.getAlbum();
	}

	/**
	 * generate a node from a given Album
	 * 
	 * @param parent
	 * @param album
	 */
	public AlbumFilterNode(GroupFilterTreeNode parent, Album album) {
		super(parent, true);

		// setup members
		this.album = album;
	}

	/**
	 * generate a string represenation
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#generateTitle()
	 */
	@Override
	public String generateTitle() {
		if (album != null) {
			return getPrefix() + "be element of album \"" + album.getName() + "\"";
		}
		return "no album selected";
	}

	/**
	 * generate a filter of this node
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#getFilter()
	 */
	@Override
	public Filter getFilter() {
		Filter result = new AlbumFilter(album);
		if (album == null) {
			result = null;
		}
		if (isNegate()) {
			result = new NotFilter(result);
		}
		return result;
	}

	/**
	 * @return the album
	 */
	public Album getAlbum() {
		return album;
	}

	/**
	 * @param album
	 *            the album to set
	 */
	public void setAlbum(Album album) {
		Album oldValue = this.album;
		this.album = album;
		
		// inform listeners
		if (!ObjectUtils.equals(oldValue, album)) {
			fireTreeNodeChange(this);
		}
	}

}
