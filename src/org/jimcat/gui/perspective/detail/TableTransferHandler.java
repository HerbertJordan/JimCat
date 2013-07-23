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

package org.jimcat.gui.perspective.detail;

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

import org.jdesktop.swingx.JXTable;
import org.jimcat.gui.SwingClient;
import org.jimcat.gui.ViewControl;
import org.jimcat.gui.dndutil.ExceptionCatchingTransferHandler;
import org.jimcat.gui.dndutil.ImageSetTransferable;
import org.jimcat.gui.dndutil.ImageSetWrapper;
import org.jimcat.gui.dndutil.TagSetWrapper;
import org.jimcat.model.Image;
import org.jimcat.model.comparator.AlbumOrderComparator;
import org.jimcat.model.tag.Tag;
import org.jimcat.services.imageimport.ImportJob;

/**
 * The TableTransferHandler is used to transfer data from the table.
 * 
 * The TableTransferHandler takes the selected rows of this DetailTable and
 * stores them in a SetTransferable.
 * 
 * 
 * $Id$
 * 
 * @author Michael
 */
public class TableTransferHandler extends ExceptionCatchingTransferHandler {

	/**
	 * the tag flavor
	 */
	private DataFlavor tagFlavor = new DataFlavor(TagSetWrapper.class, "TagSetWrapper");

	/**
	 * the image flavor
	 */
	private DataFlavor imageFlavor = new DataFlavor(ImageSetWrapper.class, "ImageSetWrapper");

	/**
	 * the supported dataFlavours
	 */
	private DataFlavor supportedFlavours[] = { tagFlavor, imageFlavor, DataFlavor.javaFileListFlavor };

	/**
	 * the detailtable to which this transferHandler belongs
	 */
	private DetailTable table;

	/**
	 * 
	 * The constructor needs the detailTable to which this transferHandler
	 * belongs
	 * 
	 * @param table
	 */
	public TableTransferHandler(DetailTable table) {
		this.table = table;
	}

	/**
	 * 
	 * The createTransferable method puts the currentry selected images into an
	 * ImageSetWrapper which is added to a corresponding Transferable.
	 * 
	 * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
	 */
	@Override
	protected Transferable safeCreateTransferable(JComponent c) {
		if (!(c instanceof JXTable)) {
			return null;
		}

		List<Image> imagesToTransfer = table.getControl().getSelectedImages();
		return new ImageSetTransferable(new ImageSetWrapper(new HashSet<Image>(imagesToTransfer)));
	}

	/**
	 * 
	 * Currently only move is possible.
	 * 
	 * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
	 */
	@Override
	public int safeGetSourceActions(@SuppressWarnings("unused")
	JComponent c) {
		return MOVE;
	}

	/**
	 * The canImport method checks if the target component is a JXTabel and if
	 * yes it compares the supported dataFlavors to the dataFlavors of the drag.
	 * 
	 * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent,
	 *      java.awt.datatransfer.DataFlavor[])
	 */
	@Override
	public boolean safeCanImport(JComponent comp, DataFlavor[] transferFlavors) {
		if (comp instanceof JXTable) {
			for (int i = 0; i < transferFlavors.length; i++) {
				for (DataFlavor supported : supportedFlavours) {
					if (supported.equals(transferFlavors[i])) {
						if (supported.equals(imageFlavor)) {
							if (table.getControl().isReorderingPossible())
								return true;
							return false;
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * The importData method calls the canImport method an if possible extracts
	 * the data from the drag and uses it on the table.
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
					return importFileListData(t);
				}
			}
		}
		return false;
	}

	/**
	 * This method is called when tags are dropped on the table. It will add the
	 * tags brought by the drag to the selected images in the table.
	 * 
	 * @param t
	 * @return true if successfull
	 */
	private boolean importTagData(Transferable t) {
		TagSetWrapper tagWrapper;
		try {
			tagWrapper = (TagSetWrapper) t.getTransferData(tagFlavor);
			for (Tag tag : tagWrapper.getTags()) {
				SwingClient.getInstance().getImageControl().addTagToImages(table.getControl().getSelectedImages(), tag);
			}
			return true;
		} catch (UnsupportedFlavorException e) {
			throw new RuntimeException("Exception when trying to import tags to table.", e);
		} catch (IOException e) {
			throw new RuntimeException("Exception when trying to import tags to table.", e);
		}
	}

	/**
	 * 
	 * This method is called when images are dropped on the table. This is only
	 * possible when showing an album and will reorder the images.
	 * 
	 * @param t
	 * @return true if successfull
	 */
	private boolean importImageData(Transferable t) {
		ImageSetWrapper imageWrapper;
		try {
			imageWrapper = (ImageSetWrapper) t.getTransferData(imageFlavor);
			List<Image> images = new LinkedList<Image>(imageWrapper.getImages());
			Point mousePos = table.getTable().getMousePosition();
			if (mousePos == null) {
				return false;
			}
			int pos = table.getTable().rowAtPoint(mousePos);
			// int pos = table.getTable().getSelectedRow();
			if (pos == -1) {
				pos = table.getTable().getRowCount() - 1;
			}
			table.getControl().getAlbumFilter().getAlbum().moveToIndex(images, pos);
			table.getControl().setSorting(new AlbumOrderComparator(table.getControl().getAlbumFilter().getAlbum()));
			return true;
		} catch (UnsupportedFlavorException e) {
			throw new RuntimeException("Exception when trying to shift images in table.", e);
		} catch (IOException e) {
			throw new RuntimeException("Exception when trying to shift images in table.", e);
		}
	}

	/**
	 * 
	 * This method is called when a list of file objects is dropped on the
	 * table. Then an import job for this files and if they are directories,
	 * this directories is started. The job is set recursive.
	 * 
	 * @param t
	 * @return true if successfull
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
			throw new RuntimeException("Exception when trying to import files in table.", e);
		} catch (IOException e) {
			throw new RuntimeException("Exception when trying to import files in table.", e);
		}
	}

}
