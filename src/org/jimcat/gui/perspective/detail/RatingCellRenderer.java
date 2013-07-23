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

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.jimcat.gui.rating.RatingRepresentation;
import org.jimcat.model.ImageRating;


/**
 * A class capable to display a rating within a table.
 * 
 * $Id: RatingCellRenderer.java 944 2007-06-16 17:15:22Z 07g1t1u3 $
 * 
 * @author Herbert
 */
public class RatingCellRenderer implements TableCellRenderer {

	/**
	 * teh default presentation
	 */
	public static final ImageIcon DEFAULT_ICON = RatingRepresentation.getIcon(ImageRating.NONE);

	/**
	 * the component used to display rating
	 */
	private JComponent renderer;

	/**
	 * the label helping displaying the image
	 */
	private JLabel image;

	/**
	 * constructor, prepairing render
	 */
	public RatingCellRenderer() {
		// Build up renderer
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.CENTER);
		layout.setHgap(0);
		layout.setVgap(0);
		panel.setLayout(layout);
		image = new JLabel();
		image.setOpaque(false);
		image.setText(null);
		image.setIcon(DEFAULT_ICON);
		panel.add(image);
		renderer = panel;
	}

	/**
	 * get useable rating cell editor for given cell
	 * 
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
	 *      java.lang.Object, boolean, boolean, int, int)
	 */
	@SuppressWarnings("unused")
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column) {

		// prepair renderer
		if (value instanceof ImageRating) {
			// if it is a Rating, show appropriate image
			ImageRating rating = (ImageRating) value;
			image.setIcon(RatingRepresentation.getIcon(rating));
		} else {
			// else show no starts
			image.setIcon(DEFAULT_ICON);
		}
		return renderer;
	}
}
