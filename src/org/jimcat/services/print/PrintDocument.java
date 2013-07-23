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

package org.jimcat.services.print;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jimcat.model.Image;
import org.jimcat.services.imagemanager.ImageQuality;

/**
 * A print document is a document that layouts a list of images on a panel.
 * 
 * 
 * $Id$
 * 
 * @author Christoph, Michael
 */
public abstract class PrintDocument {

	/**
	 * the width and height of the page
	 */
	protected Dimension pageDimension;

	/**
	 * the images that shall be layoutet
	 */
	protected List<Image> images = new ArrayList<Image>();

	/**
	 * if the title shall be added
	 */
	protected boolean showTitle;

	/**
	 * Size percentage
	 */
	protected int size = 20;

	/**
	 * enumeration setting the quality of the rendering
	 */
	protected ImageQuality renderingQuality = ImageQuality.BEST;

	/**
	 * @param renderingQuality
	 *            the renderingQuality to set
	 */
	public void setRenderingQuality(ImageQuality renderingQuality) {
		this.renderingQuality = renderingQuality;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * 
	 * set the images
	 * 
	 * @param imageList
	 */
	public void setImages(List<Image> imageList) {
		this.images = new ArrayList<Image>(imageList);
	}

	/**
	 * 
	 * remove images from the imageList
	 * 
	 * @param imageList
	 */
	public void removeImages(List<Image> imageList) {
		this.images.removeAll(imageList);
	}

	/**
	 * 
	 * get the number of pages that are needed to layout the given images with
	 * the given size and the given border
	 * 
	 * @return the number of pages
	 */
	public abstract int getPageCount();

	/**
	 * 
	 * draw the page specified by the parameter into a JPanel
	 * 
	 * @param page
	 * @return a panel which contains the images that shall be printed on the
	 *         given page
	 */
	public abstract JPanel drawPage(int page);

	/**
	 * 
	 * set the width and height of the page
	 * 
	 * @param pageDimension
	 */
	public void setPageDimension(Dimension pageDimension) {
		this.pageDimension = pageDimension;
	}

	/**
	 * get the width and height of the page
	 * @return the page dimension
	 */
	public Dimension getPageDimension() {
		return pageDimension;
	}

	/**
	 * 
	 * do layout recursive for all subcomponents
	 * 
	 * @param c
	 */
	public static void doLayout(JComponent c) {
		if (c.getLayout() != null) {
			c.getLayout().layoutContainer(c);
		}
		for (Component c1 : c.getComponents()) {
			if (c1 instanceof JComponent) {
				doLayout((JComponent) c1);
			}
		}
	}

	/**
	 * @param showTitle
	 *            the showTitle to set
	 */
	public void setShowTitle(boolean showTitle) {
		this.showTitle = showTitle;
	}
}
