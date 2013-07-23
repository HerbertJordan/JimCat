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

package org.jimcat.gui.dndutil;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * This is a wrapper class for a set of tags.
 * 
 * 
 * $Id$
 * 
 * @author Michael
 */
public class TagTransferable implements Transferable {
	/**
	 * the tagSet to be stored in this transferable
	 */
	private TagSetWrapper tagSet;

	/**
	 * the treeNode to be stored in this transferable
	 */
	private TagTreeNodeSetWrapper tagTreeNodeSet;

	/**
	 * the supported data flavours
	 */
	private DataFlavor tagSetFlavor = new DataFlavor(TagSetWrapper.class, "TagSetWrapper");

	/**
	 * the flavour representing a set of TagTreeNodes 
	 */
	private DataFlavor tagNodeSetFlavor = new DataFlavor(TagTreeNodeSetWrapper.class, "TagTreeNodeSetWrapper");

	/**
	 * Construct a new TagTransferable with a set of tags
	 * 
	 * @param tags
	 */
	public TagTransferable(TagSetWrapper tags) {
		this(null, tags);
	}

	/**
	 * 
	 * Construct a new TagTransferable with a tagTreeNode
	 * 
	 * @param nodes
	 */
	public TagTransferable(TagTreeNodeSetWrapper nodes) {
		this(nodes, null);
	}

	/**
	 * 
	 * Construct a new TagTransferable with a tagTreeNode and a tagSet
	 * 
	 * @param nodes
	 * @param tags
	 */
	public TagTransferable(TagTreeNodeSetWrapper nodes, TagSetWrapper tags) {
		this.tagTreeNodeSet = nodes;
		this.tagSet = tags;
	}

	/**
	 * Returns the transferData stored in the transferable if the data flavor
	 * that is wished is supported.
	 * 
	 * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
	 */
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (!isDataFlavorSupported(flavor)) {
			throw new UnsupportedFlavorException(flavor);
		}
		if (tagSetFlavor.equals(flavor)) {
			return tagSet;
		} else if (tagNodeSetFlavor.equals(flavor)) {
			return tagTreeNodeSet;
		}
		return null;
	}

	/**
	 * Returns the DataFlavors that are supported.
	 * 
	 * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
	 */
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { tagNodeSetFlavor, tagSetFlavor };
	}

	/**
	 * The method is dataFlavorSupported tests here if the given DataFlavor is
	 * one of the supported dataFlavors.
	 * 
	 * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
	 */
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		if (tagNodeSetFlavor.equals(flavor) || tagSetFlavor.equals(flavor)) {
			return true;
		}
		return false;
	}

}
