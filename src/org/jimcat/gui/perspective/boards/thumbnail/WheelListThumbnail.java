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

package org.jimcat.gui.perspective.boards.thumbnail;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import org.jimcat.gui.borders.RoundedBorder;
import org.jimcat.gui.imageviewer.FramedImageViewer;
import org.jimcat.gui.imageviewer.ImageViewer;
import org.jimcat.gui.wheellist.WheelListItem;
import org.jimcat.model.Image;

/**
 * An WheelList item showing a single Thumbnail
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class WheelListThumbnail extends WheelListItem<Image> {

	/**
	 * the background color of a selected card
	 */
	private static final Color COLOR_SELECTED = new Color(255, 140, 0, 100);

	/**
	 * the background color of an unselected card
	 */
	private static final Color COLOR_UNSELECTED = new Color(255, 140, 0, 0);

	/**
	 * the viewer used to display a image
	 */
	private ImageViewer viewer;

	/**
	 * build up content
	 */
	public WheelListThumbnail() {
		// build up gui components
		initComponents();
	}

	/**
	 * build up swing componets
	 */
	private void initComponents() {
		setLayout(new BorderLayout());
		setBorder(new RoundedBorder(3));
		setOpaque(false);

		// viewer = new ImageViewer();
		viewer = new FramedImageViewer();
		add(viewer, BorderLayout.CENTER);
	}

	/**
	 * @param selected
	 *            the selected to set
	 */
	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		setBackground(selected ? COLOR_SELECTED : COLOR_UNSELECTED);
	}

	/**
	 * exchange shown element
	 * 
	 * @see org.jimcat.gui.wheellist.WheelListItem#setElement(java.lang.Object)
	 */
	@Override
	public void setElement(Image element) {
		viewer.setImage(element);
	}

	/**
	 * get currently shown element
	 * 
	 * @see org.jimcat.gui.wheellist.WheelListItem#getElement()
	 */
	@Override
	public Image getElement() {
		return viewer.getImage();
	}

	/**
	 * @param size
	 * @see org.jimcat.gui.imageviewer.ImageViewer#setGraphicSize(java.awt.Dimension)
	 */
	public void setGraphicSize(Dimension size) {
		viewer.setGraphicSize(size);
	}

}
