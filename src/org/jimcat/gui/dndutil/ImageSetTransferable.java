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

/**
 * A SetTransferable is used to transfer a set of data.
 * 
 * The SetTransferable implements the Transferable interface is used to transfer
 * data stored in a set.
 * 
 * 
 * $Id$
 * 
 * @author Michael
 */
public class ImageSetTransferable implements Transferable {
	/**
	 * the data of this transferable
	 */
	private ImageSetWrapper data;

	/**
	 * the supported data flavour
	 */
	private DataFlavor supported = new DataFlavor(ImageSetWrapper.class, "ImageSetWrapper");

	/**
	 * construct a new transferable
	 * 
	 * @param images
	 */
	public ImageSetTransferable(ImageSetWrapper images) {
		data = images;
	}

	/**
	 * 
	 * Returns the transferData stored in the transferable if the data flavor
	 * that is wished is supported.
	 * 
	 * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
	 */
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
		if (!isDataFlavorSupported(flavor)) {
			throw new UnsupportedFlavorException(flavor);
		}
		if (supported.equals(flavor)) {
			return data;
		}
		return null;
	}

	/**
	 * 
	 * Returns the DataFlavors that are supported.
	 * 
	 * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
	 */
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { supported };
	}

	/**
	 * 
	 * The method is dataFlavorSupported tests here if the given DataFlavor is
	 * the supported dataFlavor.
	 * 
	 * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
	 */
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return supported.equals(flavor);
	}

}
