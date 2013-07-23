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
 * A transferable for a Set of FilterTreeNodes
 * 
 * 
 * $Id$
 * 
 * @author Michael
 */
public class FilterTreeTransferable implements Transferable {
	/**
	 * the data stored in this transferable
	 */
	private FilterTreeNodeSetWrapper data;

	/**
	 * the supported data flavor
	 */
	private DataFlavor supported = new DataFlavor(FilterTreeNodeSetWrapper.class, "FilterTreeNodeSetWrapper");

	/**
	 * construct a new transferable
	 * 
	 * @param data
	 */
	public FilterTreeTransferable(FilterTreeNodeSetWrapper data) {
		this.data = data;
	}

	/**
	 * Return the transferable
	 * 
	 * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
	 */
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (supported.equals(flavor)) {
			return data;
		}
		return null;
	}

	/**
	 * Returns the supported data flavors.
	 * 
	 * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
	 */
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { supported };
	}

	/**
	 * returns true if the current data flavor is supported.
	 * 
	 * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
	 */
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		if (supported.equals(flavor)) {
			return true;
		}
		return false;
	}

}
