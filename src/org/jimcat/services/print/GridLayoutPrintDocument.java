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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jimcat.gui.imageviewer.ImageViewer;
import org.jimcat.model.Image;
import org.jvnet.substance.SubstanceLookAndFeel;

/**
 * The GridLayoutPrintDocument uses the GridLayout to place a given list of
 * images on a JPanel.
 * 
 * This class can be used to print a list of images or a substitute on a jpanel
 * with a given size using grid layout. Furthermore the title of the image can
 * also be placed near each picture.
 * 
 * $Id$
 * 
 * @author Christoph, Michael
 */
public class GridLayoutPrintDocument extends PrintDocument {
	/**
	 * the layout used
	 */
	private GridLayout layout = new GridLayout();

	/**
	 * Horizontal gap in pixels
	 */
	private int hgap;

	/**
	 * Vertical gap in pixels
	 */
	private int vgap;

	/**
	 * boolean to indicate if images shall be really drawn or substitute used
	 */
	private boolean showImages;

	/**
	 * 
	 * construct a new document
	 * 
	 * @param showImages
	 */
	public GridLayoutPrintDocument(boolean showImages) {
		this.showImages = showImages;
	}

	/**
	 * 
	 * set the horizontal gap
	 * 
	 * @param hgap
	 *            in percent
	 */
	public void setHgap(int hgap) {
		double factor = hgap / 100.0;

		this.hgap = (int) (pageDimension.width * factor);
	}

	/**
	 * 
	 * set the vertical gap
	 * 
	 * @param vgap
	 *            in percent
	 */
	public void setVgap(int vgap) {
		double factor = vgap / 100.0;

		this.vgap = (int) (pageDimension.height * factor);
	}

	/**
	 * 
	 * draw the page on a jpanel and return it
	 * 
	 * @see org.jimcat.services.print.PrintDocument#drawPage(int)
	 */
	@Override
	public JPanel drawPage(int page) {

		if (page > getPageCount()) {
			throw new IllegalArgumentException("Page #" + page + " doesn't exist, there are just " + getPageCount()
			        + " pages");
		}

		JPanel panel = new JPanel();
		panel.setLayout(layout);
		panel.setSize(pageDimension);
		panel.setOpaque(true);
		panel.setBackground(Color.WHITE);
		panel.putClientProperty(SubstanceLookAndFeel.WATERMARK_IGNORE, Boolean.TRUE);

		layout.setColumns(getColumnCount());
		layout.setRows(getRowCount());

		List<Image> currentImages = getImagesForPage(page);

		int top = vgap / 2;
		int left = hgap / 2;
		int bottom = vgap - top;
		int right = hgap - left;

		for (Image img : currentImages) {
			Item item = new Item(img);

			item.setGaps(top, left, bottom, right);

			panel.add(item);

		}
		/*
		 * if last side and not enough images to fill it add empty containers
		 * into grid-layout
		 */
		int difference = getImagesPerPage() - currentImages.size();
		while (difference != 0) {
			JPanel dummyPanel = new JPanel();
			dummyPanel.setOpaque(false);
			panel.add(dummyPanel);
			difference--;
		}

		doLayout(panel);

		panel.setPreferredSize(pageDimension);

		return panel;
	}

	/**
	 * 
	 * returns the items for the given page
	 * 
	 * @param page
	 * @return a list of images part of the given page
	 */
	private List<Image> getImagesForPage(int page) {

		int imagesPerPage = getImagesPerPage();

		int from = (page - 1) * imagesPerPage;
		int to = from + imagesPerPage;
		to = Math.min(to, images.size());

		return images.subList(from, to);
	}

	/**
	 * 
	 * return how many images fit on a page
	 * 
	 * @return the number of images per page
	 */
	private int getImagesPerPage() {
		return getColumnCount() * getRowCount();
	}

	/**
	 * returns the number of rows that fit on the page
	 * 
	 * 
	 * @return the number of rows
	 */
	private int getRowCount() {
		if (calculateImageSize().height > 0) {
			return (pageDimension.height) / calculateImageSize().height;
		}
		return 1;
	}

	/**
	 * returns the number of columns that fit on the page
	 * 
	 * 
	 * @return the number of columns
	 */
	private int getColumnCount() {
		if (calculateImageSize().width>0) {
			return (pageDimension.width) / calculateImageSize().width;
		}
		return 1;
	}

	/**
	 * 
	 * Calculates image size
	 * 
	 * @return The calculated image size
	 */
	private Dimension calculateImageSize() {
		double factor = size / 100.0;

		int width = (int) (pageDimension.width * factor);
		int height = (int) (pageDimension.height * factor);

		return new Dimension(width, height);
	}

	/**
	 * get the number of pages needed to print all the images
	 * 
	 * @see org.jimcat.services.print.PrintDocument#getPageCount()
	 */
	@Override
	public int getPageCount() {
		return (int) (Math.ceil(images.size() / (float) getImagesPerPage()));
	}

	/**
	 * set wheter the images shall be drawn or not
	 * 
	 * @param b
	 */
	public void setShowImages(boolean b) {
		this.showImages = b;
	}

	/**
	 * 
	 * An item is a encapsulation of an image that shall be placed in the
	 * document.
	 * 
	 * The item can draw an image or a grey rectangle as substitute and print
	 * the title as an optional feature.
	 * 
	 * 
	 * $Id$
	 * 
	 * @author Christoph, Michael
	 */
	private class Item extends JPanel {
		/**
		 * the image that shall be drawn into this panel
		 */
		private Image image;

		/**
		 * the imageViewer used to draw the image
		 */
		private ImageViewer imageViewer;

		/**
		 * 
		 * construct an item, the image is set and initComponent is called
		 * 
		 * @param image
		 */
		public Item(Image image) {
			this.image = image;

			initComponent();
		}

		/**
		 * initialize an item of the PrintDocument
		 */
		private void initComponent() {
			BorderLayout borderLayout = new BorderLayout();
			setLayout(borderLayout);
			setOpaque(false);
			setBorder(BorderFactory.createEmptyBorder());

			if (showImages) {
				imageViewer = new ImageViewer();
				imageViewer.setQuality(renderingQuality);
				imageViewer.setImage(image);
				add(imageViewer, BorderLayout.CENTER);
			} else {
				JPanel imageSubstitute = new JPanel();
				imageSubstitute.setSize(calculateImageSize());
				imageSubstitute.setBackground(Color.DARK_GRAY);
				imageSubstitute.setOpaque(true);
				add(imageSubstitute);
			}

			if (showTitle) {
				JLabel label = new JLabel(image.getTitle());
				label.setForeground(Color.BLACK);
				label.setHorizontalAlignment(SwingConstants.CENTER);
				add(label, BorderLayout.SOUTH);
			}
		}

		/**
		 * Set gaps for this image.
		 * 
		 * @param top
		 * @param left
		 * @param bottom
		 * @param right
		 */
		public void setGaps(int top, int left, int bottom, int right) {
			setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
		}
	}
}
