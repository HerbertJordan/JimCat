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

import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import org.jimcat.gui.ViewControl;
import org.jimcat.gui.icons.Icons;
import org.jimcat.model.Album;
import org.jimcat.model.SmartList;


/**
 * represents the none-visible root element of the album tree.
 * 
 * Nevertheless, it containes the elements of the first level and therefore
 * forms the base of the tree.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class AlbumTreeRoot extends AlbumTreeNode {

	/**
	 * a list of all children
	 */
	private List<AlbumTreeNode> children;

	/**
	 * the all images node
	 */
	private AlbumTreeNode allImagesNode;

	/**
	 * the album root node
	 */
	private AlbumTreeAlbumRoot albumRoot;

	/**
	 * the smartlist root node
	 */
	private AlbumTreeSmartListRoot smartListRoot;

	/**
	 * create a new Instance of this Node.
	 * 
	 * This will automatically build up the substructure.
	 * 
	 * @param model -
	 *            the containing TreeModel
	 */
	public AlbumTreeRoot(AlbumTreeModel model) {
		super(model, null, false);
		setTitel("root - should never be visible!");

		// create children
		children = new LinkedList<AlbumTreeNode>();

		// first child - all images
		allImagesNode = new AllImagesNode(model, this);
		children.add(allImagesNode);

		// second child - album list
		albumRoot = new AlbumTreeAlbumRoot(model, this);
		children.add(albumRoot);

		// third child - smartlist root
		smartListRoot = new AlbumTreeSmartListRoot(model, this);
		children.add(smartListRoot);
	}

	/**
	 * get the path for the all images node
	 * 
	 * @return the tree path
	 */
	public TreePath getPathForAllImages() {
		return allImagesNode.getPath();
	}

	/**
	 * get the path for a special album
	 * 
	 * @param album
	 * @return path or null if there is no such element
	 */
	public TreePath getPathForAlbum(Album album) {
		return albumRoot.getPathForAlbum(album);
	}

	/**
	 * get the path for a special smartlist
	 * 
	 * @param list
	 * @return path or null if there is no such element
	 */
	public TreePath getPathForSmartList(SmartList list) {
		return smartListRoot.getPathForSmartList(list);
	}

	/**
	 * returns the child at the specified position
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#getChildrenAt(int)
	 */
	@Override
	public AlbumTreeNode getChildrenAt(int index) {
		// get element without out of bound exceptions
		if (index >= 0 && index < children.size()) {
			return children.get(index);
		}
		return null;
	}

	/**
	 * returns the number of child
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#getChildrenCount()
	 */
	@Override
	public int getChildrenCount() {
		return children.size();
	}

	/**
	 * there is no popup menu for this node => return null
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#getPopupMenu()
	 */
	@Override
	public JPopupMenu getPopupMenu() {
		return null;
	}

	/**
	 * this node isn't editable => return false
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#isEditable()
	 */
	@Override
	public boolean isEditable() {
		return false;
	}

	/**
	 * this node should be never selected => do nothing
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#performSelection(org.jimcat.gui.ViewControl)
	 */
	@Override
	@SuppressWarnings("unused")
	public void performSelection(ViewControl control) {
		// do nothing
	}

	/**
	 * value can't be edited => does nothing
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#setTitel(java.lang.String)
	 */
	@Override
	@SuppressWarnings("unused")
	public void setValue(String value) {
		// do nothing
	}

	/**
	 * There are no children, returnes -1
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#getIndexOfChild(org.jimcat.gui.albumlist.AlbumTreeNode)
	 */
	@Override
	@SuppressWarnings("unused")
	public int getIndexOfChild(AlbumTreeNode child) {
		return -1;
	}
	
	/**
     * return the icon of the albumTreeRoot
     * @see org.jimcat.gui.albumlist.AlbumTreeNode#getIcon()
     */
    @Override
    public ImageIcon getIcon() {
	    return Icons.ALBUM;
    }

}
