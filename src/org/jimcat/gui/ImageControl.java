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

package org.jimcat.gui;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.swing.JOptionPane;

import org.jimcat.model.Album;
import org.jimcat.model.Image;
import org.jimcat.model.ImageRating;
import org.jimcat.model.libraries.ImageLibrary;
import org.jimcat.model.notification.BeanModificationManager;
import org.jimcat.model.tag.Tag;
import org.jimcat.services.ImageOperations;
import org.jimcat.services.OperationsLocator;
import org.jimcat.services.imagedelete.DeleteJob;
import org.jimcat.services.imagemanager.ImageQuality;
import org.jimcat.services.imageupdate.UpdateJob;

/**
 * Central element containing image contolling routines.
 * 
 * $Id: ImageControl.java 998 2007-08-29 20:36:25Z cleiter $
 * 
 * @author Herbert
 */
public class ImageControl {

	/**
	 * a reference to the image control
	 */
	private ImageOperations control;

	/**
	 * the library this instance is working on
	 */
	private ImageLibrary library;

	/**
	 * default constructor, setting up default values
	 */
	public ImageControl() {
		control = OperationsLocator.getImageOperations();
		library = control.getLibrary();
	}

	/**
	 * @return the library
	 */
	public ImageLibrary getLibrary() {
		return library;
	}

	/**
	 * Use this methode to get an image-representation of the given dimension
	 * 
	 * @param img -
	 *            the image
	 * @param dim -
	 *            the dimension
	 * @return - the scalled image specified or null
	 */
	public BufferedImage getImageGraphic(Image img, Dimension dim) {
		return control.getImageGraphic(img, dim);
	}

	/**
	 * Use this methode to get an image-representation of the given dimension an
	 * quality
	 * 
	 * @param img -
	 *            the image
	 * @param dim -
	 *            the dimension
	 * @param quality -
	 *            a minimum quality for the requested image
	 * @return - the scalled image specified or null
	 */
	public BufferedImage getImageGraphic(Image img, Dimension dim, ImageQuality quality) {
		return control.getImageGraphic(img, dim, quality);
	}

	/**
	 * Use this methode to get an image-representation of the given dimension
	 * and quality if available in cache
	 * 
	 * @param img -
	 *            the image
	 * @param dim -
	 *            the dimension
	 * @param quality -
	 *            a minimum quality for the requested image
	 * @return - the scalled image specified or null if not available
	 */
	public BufferedImage getImageGraphicIfAvailable(Image img, Dimension dim, ImageQuality quality) {
		return control.getImageGraphicIfAvailable(img, dim, quality);
	}

	/**
	 * this will add a new preload order to the image manager queue
	 * 
	 * @param img
	 * @param dim
	 * @see ImageOperations#preloadImage(Image, Dimension)
	 */
	public void preloadImage(Image img, Dimension dim) {
		control.preloadImage(img, dim);
	}

	/**
	 * rate given images with given rating
	 * 
	 * @param images
	 * @param rating
	 */
	public void rateImages(Collection<Image> images, ImageRating rating) {
		// set rating
		try {
			BeanModificationManager.startTransaction();
			for (Image img : images) {
				img.setRating(rating);
			}
		} catch (RuntimeException e) {
			throw e;
		} finally {
			BeanModificationManager.commitTransaction();
		}
	}

	/**
	 * assign the given rating to currently selected images
	 * 
	 * @param rating
	 */
	public void rateSelected(ImageRating rating) {
		rateImages(getSelectedImages(), rating);
	}

	/**
	 * add a singel tag to the given images
	 * 
	 * @param images
	 * @param tag
	 */
	public void addTagToImages(Collection<Image> images, Tag tag) {
		// add tag
		try {
			BeanModificationManager.startTransaction();
			for (Image img : images) {
				img.addTag(tag);
			}
		} catch (RuntimeException e) {
			throw e;
		} finally {
			BeanModificationManager.commitTransaction();
		}
	}

	/**
	 * add a singel tag to the currently selected images.
	 * 
	 * @param tag
	 */
	public void addTagToSelection(Tag tag) {
		addTagToImages(getSelectedImages(), tag);
	}

	/**
	 * removes the given tag from the given images
	 * 
	 * @param images
	 * @param tag
	 */
	public void removeTagFromImages(Collection<Image> images, Tag tag) {
		// remove tag
		try {
			BeanModificationManager.startTransaction();
			for (Image img : images) {
				img.removeTag(tag);
			}
		} catch (RuntimeException e) {
			throw e;
		} finally {
			BeanModificationManager.commitTransaction();
		}
	}

	/**
	 * remove a singel tag from the currently selected images.
	 * 
	 * @param tag
	 */
	public void removeTagFromSelection(Tag tag) {
		removeTagFromImages(getSelectedImages(), tag);
	}

	/**
	 * add given images to the given album
	 * 
	 * @param images
	 * @param album
	 */
	public void addImagesToAlbum(Collection<Image> images, Album album) {
		// add images to album
		try {
			BeanModificationManager.startTransaction();
			for (Image img : images) {
				album.addImage(img);
			}
		} catch (RuntimeException e) {
			throw e;
		} finally {
			BeanModificationManager.commitTransaction();
		}
	}

	/**
	 * add the currently selected images to the given album
	 * 
	 * @param album
	 */
	public void addSelectionToAlbum(Album album) {
		addImagesToAlbum(getSelectedImages(), album);
	}

	/**
	 * removes the given images from the given album
	 * 
	 * @param images
	 * @param album
	 */
	public void removeImagesFromAlbum(Collection<Image> images, Album album) {
		// remove images from album
		try {
			BeanModificationManager.startTransaction();
			for (Image img : images) {
				album.removeImage(img);
			}
		} catch (RuntimeException e) {
			throw e;
		} finally {
			BeanModificationManager.commitTransaction();
		}
	}

	/**
	 * remove the currently selected images from an album
	 * 
	 * @param album
	 */
	public void removeSelectionFromAlbum(Album album) {
		removeImagesFromAlbum(getSelectedImages(), album);
	}

	/**
	 * removes the given images from library
	 * 
	 * @param images
	 */
	public void removeFromLibrary(Collection<Image> images) {
		// conforme task
		String msg = "Should the selected " + images.size() + " images really be deleted from library?";
		String titel = "Attention";
		int options = JOptionPane.YES_NO_OPTION;
		int typ = JOptionPane.WARNING_MESSAGE;

		int result = SwingClient.getInstance().showConfirmDialog(msg, titel, options, typ);

		// if not confirmed return
		if (result != JOptionPane.YES_OPTION) {
			return;
		}

		// remove images
		library.remove(new HashSet<Image>(images));
	}

	/**
	 * remove images from library (disc version remain) => done synchron
	 */
	public void removeSelectionFromLibrary() {
		removeFromLibrary(getSelectedImages());
	}

	/**
	 * remove given images from disc
	 * 
	 * @param images
	 */
	public void removeImagesFromDisc(Collection<Image> images) {
		// conforme task
		String msg = "Should the selected " + images.size() + " images really be deleted from disc?";
		String titel = "Attention";
		int options = JOptionPane.YES_NO_OPTION;
		int typ = JOptionPane.WARNING_MESSAGE;

		int result = SwingClient.getInstance().showConfirmDialog(msg, titel, options, typ);

		// if not confirmed return
		if (result != JOptionPane.YES_OPTION) {
			return;
		}

		// build delete job
		DeleteJob job = new DeleteJob();
		job.setImageSet(new HashSet<Image>(images));

		SwingClient.getInstance().startJob(job);
	}

	/**
	 * remove images from disc => asynchron job
	 */
	public void removeSelectionFromDisc() {
		removeImagesFromDisc(getSelectedImages());
	}

	/**
	 * rotate given images clock wise
	 * 
	 * @param images
	 */
	public void rotateImagesCW(Collection<Image> images) {
		// rotate images clockwise
		try {
			BeanModificationManager.startTransaction();
			for (Image img : images) {
				img.setRotation(img.getRotation().getClockwiseNeighbor());
			}
		} catch (RuntimeException e) {
			throw e;
		} finally {
			BeanModificationManager.commitTransaction();
		}
	}

	/**
	 * rotate selected elements clock wise
	 */
	public void rotateSelectionCW() {
		rotateImagesCW(getSelectedImages());
	}

	/**
	 * rotate given images counter clock wise
	 * 
	 * @param images
	 */
	public void rotateImagesCCW(Collection<Image> images) {
		// rotate images clockwise
		try {
			BeanModificationManager.startTransaction();
			for (Image img : images) {
				img.setRotation(img.getRotation().getCounterClockwiseNeighbor());
			}
		} catch (RuntimeException e) {
			throw e;
		} finally {
			BeanModificationManager.commitTransaction();
		}
	}

	/**
	 * rotate selected elements counter clock wise
	 */
	public void rotateSelectionCCW() {
		rotateImagesCCW(getSelectedImages());
	}

	/**
	 * 
	 * Rename a list of images
	 * 
	 * @param images
	 *            The images to rename
	 * @param newNames
	 *            The corresponding list of the new names in the right order
	 * 
	 */
	public void rename(List<Image> images, List<String> newNames) {
		try {
			BeanModificationManager.startTransaction();

			for (int i = 0; i < images.size(); i++) {
				Image image = images.get(i);
				String newName = newNames.get(i);
				image.setTitle(newName);
			}
		} catch (RuntimeException e) {
			throw e;
		} finally {
			BeanModificationManager.commitTransaction();
		}
	}

	/**
	 * update given images using a new job
	 * 
	 * @param images
	 */
	public void updateImages(Collection<Image> images) {
		UpdateJob job = new UpdateJob();
		job.setUpdateList(new ArrayList<Image>(images));
		SwingClient.getInstance().startJob(job);
	}

	/**
	 * update current selection
	 */
	public void updateSelection() {
		updateImages(getSelectedImages());
	}

	/**
	 * test if the quick edit function is useable on this system
	 * 
	 * @return true if it is useable
	 */
	public boolean quickEditPossible() {
		return false; // FIXME cleiter
//		return !System.getProperty("java.version").matches("1.[0-5].*")
//		        && Desktop.getDesktop().isSupported(Action.EDIT);
	}

	/**
	 * this will start systems default image editor for given images.
	 * 
	 * it will do nothing if quickEditPossible returns false.
	 * 
	 * @param images
	 *            the images to edit
	 */
	public void editImages(Collection<Image> images) {
		// test if editing is supported
		if (!quickEditPossible()) {
			return;
		}
		
		/*
		// start editing
		Desktop desktop = Desktop.getDesktop();
		for (Image img : images) {
			try {
				desktop.edit(img.getMetadata().getPath());
			} catch (Exception e) {
				// do not react - there is nothing you can do
			}
		}
		*/
	}

	/**
	 * start editing current selection
	 * 
	 * @see ImageControl#editImages(Collection)
	 */
	public void editSelection() {
		editImages(getSelectedImages());
	}

	/**
	 * get current selection
	 * 
	 * @return - currently selected images
	 */
	private List<Image> getSelectedImages() {
		ViewControl view = SwingClient.getInstance().getViewControl();
		// get current selection
		return view.getSelectedImages();
	}
}
