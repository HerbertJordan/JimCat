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

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.jimcat.gui.SwingClient;
import org.jimcat.gui.dndutil.ExceptionCatchingTransferHandler;
import org.jimcat.gui.dndutil.ImageSetWrapper;

/**
 * The AlbumTreeTransferHandler is used to import data brought to the tree by
 * drag & drop.
 * 
 * 
 * $Id$
 * 
 * @author Michael
 */
public class AlbumTreeTransferHandler extends ExceptionCatchingTransferHandler {

	/**
	 * the supported DataFlavor
	 */
	private DataFlavor supported = new DataFlavor(ImageSetWrapper.class, "ImageSetWrapper");

	/**
	 * 
	 * The can import tests wheter c is an instance of JTree and if true it
	 * iterates over the flavors contained in the drag & drop to test if the
	 * supported flavor is part of it.
	 * 
	 * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent,
	 *      java.awt.datatransfer.DataFlavor[])
	 */
	@Override
	public boolean safeCanImport(JComponent c, DataFlavor[] flavors) {
		if (c instanceof JTree) {
			JTree tree = (JTree) c;
			Point pos = tree.getMousePosition();
			if (pos != null) {
				TreePath path = tree.getClosestPathForLocation(pos.x, pos.y);
				AlbumTreeNode node = (AlbumTreeNode) path.getLastPathComponent();
				if (node instanceof AlbumTreeAlbumNode) {
					for (int i = 0; i < flavors.length; i++) {
						if (supported.equals(flavors[i])) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * the importData method here checks wheter c is a JTree and checks if the
	 * drag & drop would work (i.e. Contains a ImageSetWrapper) by using the can
	 * import method. If yes it tests if the current selection is an
	 * AlbumTreeAlbumNode and if this is also true it adds the images to the
	 * album of the AlbumTreeAlbumNode.
	 * 
	 * @see javax.swing.TransferHandler#importData(javax.swing.JComponent,
	 *      java.awt.datatransfer.Transferable)
	 */
	@Override
	public boolean safeImportData(JComponent c, Transferable t) {
		ImageSetWrapper wrapper;
		JTree tree;
		try {
			if (canImport(c, t.getTransferDataFlavors())) {
				tree = (JTree) c;
				Point mousePosition = tree.getMousePosition();
				if (mousePosition == null) {
					return false;
				}
				TreePath path = tree.getClosestPathForLocation(mousePosition.x, mousePosition.y);
				AlbumTreeNode node = (AlbumTreeNode) path.getLastPathComponent();

				if (!(node instanceof AlbumTreeAlbumNode)) {
					return false;
				}

				AlbumTreeAlbumNode mouseReleasedOver = (AlbumTreeAlbumNode) node;
				wrapper = (ImageSetWrapper) t.getTransferData(supported);

				SwingClient.getInstance().getImageControl().addImagesToAlbum(wrapper.getImages(),
				        mouseReleasedOver.getAlbum());
				return true;
			}
		} catch (UnsupportedFlavorException ufe) {
			// should not happen
			throw new RuntimeException("Failure when importing data in album tree.", ufe);
		} catch (IOException ioe) {
			// should not happen
			throw new RuntimeException("Failure when importing data in album tree.", ioe);
		}
		return false;
	}
}
