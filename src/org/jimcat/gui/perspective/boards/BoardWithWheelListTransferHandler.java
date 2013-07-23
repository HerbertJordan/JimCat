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

package org.jimcat.gui.perspective.boards;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;

import org.jimcat.gui.SwingClient;
import org.jimcat.gui.ViewControl;
import org.jimcat.gui.dndutil.ExceptionCatchingTransferHandler;
import org.jimcat.gui.dndutil.ImageSetTransferable;
import org.jimcat.gui.dndutil.ImageSetWrapper;
import org.jimcat.gui.dndutil.TagSetWrapper;
import org.jimcat.gui.wheellist.WheelList;
import org.jimcat.model.Image;
import org.jimcat.model.comparator.AlbumOrderComparator;
import org.jimcat.model.tag.Tag;
import org.jimcat.services.imageimport.ImportJob;

/**
 * The BoardWithWheelListTransferHandler is used to transfer data from the board
 * view that uses a wheel list for printing the images.
 * 
 * 
 * $Id$
 * 
 * @author Michael
 */
public class BoardWithWheelListTransferHandler extends ExceptionCatchingTransferHandler {
	/**
	 * the tag flavor
	 */
	private DataFlavor tagFlavor = new DataFlavor(TagSetWrapper.class, "TagSetWrapper");

	/**
	 * the image flavor
	 */
	private DataFlavor imageFlavor = new DataFlavor(ImageSetWrapper.class, "ImageSetWrapper");

	/**
	 * the supported dataFlavors
	 */
	private DataFlavor supportedFlavours[] = { tagFlavor, imageFlavor, DataFlavor.javaFileListFlavor };

	/**
	 * the board to which this transfer handler belongs
	 */
	private Board board;

	/**
	 * The wheel list to which this transfer handler belongs
	 */
	private WheelList<Image> wheelList;

	/**
	 * The constructor needs the Board to which this TransferHandler belongs.
	 * 
	 * @param board
	 * @param wheelList 
	 */
	public BoardWithWheelListTransferHandler(Board board, WheelList<Image> wheelList) {
		this.board = board;
		this.wheelList = wheelList;
	}

	/**
	 * The canImport method checks wheter the target component is the wheellist
	 * to which this transferhandler belongs and if this is the case compares
	 * the supported data-flavours to those brought by the drag.
	 * 
	 * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent,
	 *      java.awt.datatransfer.DataFlavor[])
	 */
	@Override
	public boolean safeCanImport(JComponent comp, DataFlavor[] transferFlavors) {
		if (comp.equals(wheelList)) {
			for (int i = 0; i < transferFlavors.length; i++) {
				for (DataFlavor supported : supportedFlavours)
					if (supported.equals(transferFlavors[i])) {
						if (supported.equals(imageFlavor)) {
							if (board.getControl().isReorderingPossible())
								return true;
							return false;
						}
						return true;
					}
			}
		}
		return false;
	}

	/**
	 * The importData method calls the canImport method an if possible extracts
	 * the data from the drag and imports it to the current selection of the
	 * board.
	 * 
	 * @see javax.swing.TransferHandler#importData(javax.swing.JComponent,
	 *      java.awt.datatransfer.Transferable)
	 */
	@Override
	public boolean safeImportData(JComponent comp, Transferable t) {
		if (canImport(comp, t.getTransferDataFlavors())) {
			for (DataFlavor supported : t.getTransferDataFlavors()) {
				if (supported.equals(tagFlavor)) {
					return importTagData(t);
				} else if (supported.equals(imageFlavor)) {
					return importImageData(t);
				} else if (supported.equals(DataFlavor.javaFileListFlavor)) {
					importFileListData(t);
				}
			}
		}
		return false;
	}

	/**
	 * This method is called when tags are dropped on the board. It will add the
	 * tags brought by the drag to the selected images in the board.
	 * 
	 * @param t
	 * @return true if successful
	 */
	private boolean importTagData(Transferable t) {
		TagSetWrapper tagWrapper;
		try {
			tagWrapper = (TagSetWrapper) t.getTransferData(tagFlavor);
			// note that this only works if the selection is following the mouse
			// while dragging over the target component
			for (Tag tag : tagWrapper.getTags()) {
				SwingClient.getInstance().getImageControl().addTagToImages(board.getControl().getSelectedImages(), tag);
			}
			return true;
		} catch (UnsupportedFlavorException e) {
			throw new RuntimeException("Error when importing Tags to Thumbnailboard", e);
		} catch (IOException e) {
			throw new RuntimeException("Error when importing Tags to Thumbnailboard", e);
		}
	}

	/**
	 * 
	 * This method is called when images are dropped in the board. This is only
	 * possible when an album is shown in the current view and will reorder the
	 * images in the album.
	 * 
	 * @param t
	 * @return successful
	 */
	private boolean importImageData(Transferable t) {
		ImageSetWrapper imageWrapper;
		try {
			imageWrapper = (ImageSetWrapper) t.getTransferData(imageFlavor);
			List<Image> images = new LinkedList<Image>(imageWrapper.getImages());
			// get the mouse position within the list - note that this could be
			// different than the mouse position within the board
			Point mousePos = wheelList.getMousePosition();
			int pos = board.getIndexAt(mousePos);
			if (pos == -1) {
				pos = board.getComponentCount() - 1;
			}
			board.getControl().getAlbumFilter().getAlbum().moveToIndex(images, pos);
			board.getControl().setSorting(new AlbumOrderComparator(board.getControl().getAlbumFilter().getAlbum()));
			return true;
		} catch (UnsupportedFlavorException e) {
			throw new RuntimeException("Error when importing Images to Thumbnailboard", e);
		} catch (IOException e) {
			throw new RuntimeException("Error when importing Images to Thumbnailboard", e);
		}
	}

	/**
	 * 
	 * This method is called when a list of file objects is dropped on the
	 * board. Then an import job for this files and if they are directories,
	 * this directories is started. The job is set recursive.
	 * 
	 * @param t
	 * @return successful
	 */
	@SuppressWarnings("unchecked")
	private boolean importFileListData(Transferable t) {
		List<File> toImport;
		try {
			toImport = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
			ImportJob importJob = new ImportJob();
			importJob.setFiles(toImport);
			importJob.setRecursive(true);
			ViewControl control = SwingClient.getInstance().getViewControl();
			control.clearFilter();
			control.setImportIdFilter(importJob.getImportId());
			SwingClient.getInstance().startJob(importJob);
			return true;
		} catch (UnsupportedFlavorException e) {
			throw new RuntimeException("Exception when trying to import files in board.", e);
		} catch (IOException e) {
			throw new RuntimeException("Exception when trying to import files in board.", e);
		}
	}

	/**
	 * 
	 * The createTransferable method takes the currently selected images and
	 * puts them into an ImageSetWrapper which is added to a corresponding
	 * Transferable.
	 * 
	 * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
	 */
	@Override
	protected Transferable safeCreateTransferable(JComponent c) {
		if (!(c.equals(wheelList))) {
			return null;
		}
		List<Image> imagesToTransfer = board.getControl().getSelectedImages();
		return new ImageSetTransferable(new ImageSetWrapper(new HashSet<Image>(imagesToTransfer)));
	}

	/**
	 * Currently only move is possible
	 * 
	 * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
	 */
	@Override
	public int safeGetSourceActions(@SuppressWarnings("unused")
	JComponent c) {
		return MOVE;
	}

}
