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


/**
 * this node represents the "show all images" element within the tree.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class AllImagesNode extends AlbumTreeNode {

	/**
	 * simple constructor to provide values to super-constructor
	 * 
	 * @param model -
	 *            the containing model
	 * @param parent 
	 */
	public AllImagesNode(AlbumTreeModel model, AlbumTreeNode parent) {
		super(model, parent, true);

		// set titel
		setTitel("All Images");
	}

	/**
	 * There are no children => return null
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#getChildrenAt(int)
	 */
	@Override
	@SuppressWarnings("unused")
	public AlbumTreeNode getChildrenAt(int index) {
		return null;
	}

	/**
	 * There are no children => return 0
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#getChildrenCount()
	 */
	@Override
	public int getChildrenCount() {
		return 0;
	}

	/**
	 * There shouldn't be a popup => returns null
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#getPopupMenu()
	 */
	@Override
	public JPopupMenu getPopupMenu() {
		return null;
	}

	/**
	 * this node isn't editable
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#isEditable()
	 */
	@Override
	public boolean isEditable() {
		return false;
	}

	/**
	 * Selecting this node will clear all filtercriterias from the given view
	 * control
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#performSelection(org.jimcat.gui.ViewControl)
	 */
	@Override
	public void performSelection(ViewControl control) {
		control.clearFilter();
	}

	/**
	 * Value can't be edited => do nothing
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#setValue(java.lang.String)
	 */
	@Override
	@SuppressWarnings("unused")
	public void setValue(String value) {
		// no nothing
	}

	/**
	 * there are no children => just returnes -1
	 * 
	 * @see org.jimcat.gui.albumlist.AlbumTreeNode#getIndexOfChild(org.jimcat.gui.albumlist.AlbumTreeNode)
	 */
	@Override
	@SuppressWarnings("unused")
	public int getIndexOfChild(AlbumTreeNode child) {
		return -1;
	}

	/**
     * The Icon of the allImagesNode
     * @see org.jimcat.gui.albumlist.AlbumTreeNode#getIcon()
     */
    @Override
    public ImageIcon getIcon() {
	    return Icons.ALL_IMAGES;
    }
}
