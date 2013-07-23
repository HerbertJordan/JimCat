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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;

import org.jimcat.gui.imagepopup.ImagePopupMenu;
import org.jimcat.gui.rating.RatingRepresentation;
import org.jimcat.model.Image;
import org.jimcat.model.ImageRating;

/**
 * An editor supportin in-place editing of Ratings
 * 
 * $Id: RatingCellEditor.java 928 2007-06-14 14:53:06Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public class RatingCellEditor extends AbstractCellEditor implements TableCellEditor, MouseListener,
        MouseMotionListener, TableModelListener {

	/**
	 * the component used to edit values
	 */
	private JComponent editor;

	/**
	 * used to display current value
	 */
	private JLabel image;

	/**
	 * the current edited image (containing the rating)
	 */
	private Image img;

	/**
	 * to avoid editing on first click
	 */
	private boolean first = true;

	/**
	 * used to determine if a change is caused by this component
	 */
	private boolean changing = false;

	/**
	 * the model this element is registered to
	 */
	private DetailTableModel model;

	/**
	 * constructor, assembling editor
	 */
	public RatingCellEditor() {
		// Build up editor
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
		image.setIcon(RatingCellRenderer.DEFAULT_ICON);

		// a Mouse listener for dircet clicks
		image.addMouseListener(this);

		// a mouse listener for dragging
		image.addMouseMotionListener(this);

		panel.add(image);
		editor = panel;
	}

	/**
	 * Prepair renderer for editing
	 * 
	 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable,
	 *      java.lang.Object, boolean, int, int)
	 */
	@SuppressWarnings("unused")
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		// register to table
		setTableModel((DetailTableModel) table.getModel());
		img = model.getImageAtRow(row);
		// prepair editor
		if (value instanceof ImageRating) {
			ImageRating rating = (ImageRating) value;
			image.setIcon(RatingRepresentation.getIcon(rating));
			img.setRating(rating);
		}
		first = true;
		return editor;
	}

	/**
	 * returns value after editing
	 * 
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	public Object getCellEditorValue() {
		return img.getRating();
	}

	/**
	 * used to exchange the observed model
	 * 
	 * @param newModel
	 */
	private void setTableModel(DetailTableModel newModel) {
		// is there a change
		if (model == newModel) {
			return;
		}
		// unregister from old
		if (model != null) {
			model.removeTableModelListener(this);
		}
		// exchange
		model = newModel;
		// register to new
		if (model != null) {
			model.addTableModelListener(this);
		}
	}

	/**
	 * does nothing, just to implement interface
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@SuppressWarnings("unused")
	public void mouseClicked(MouseEvent e) {
		// do nothing
	}

	/**
	 * does nothing, just to implement interface
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@SuppressWarnings("unused")
	public void mouseEntered(MouseEvent e) {
		// do nothing
	}

	/**
	 * does nothing, just to implement interface
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@SuppressWarnings("unused")
	public void mouseExited(MouseEvent e) {
		// do nothing
	}

	/**
	 * react on a mouse pressed event
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {

		if (e.getButton() == MouseEvent.BUTTON1) {
			if (!first) {
				updateValue(e);
			} else {
				first = false;
			}
		}
		checkForPopup(e);
	}

	/**
	 * check for popup on mouse released
	 * 
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@SuppressWarnings("unused")
	public void mouseReleased(MouseEvent e) {
		checkForPopup(e);
	}
	
	/**
	 * 
	 * check if a popup was triggered and show it
	 * @param e
	 */
	private void checkForPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			ImagePopupMenu.getInstance().show(editor, e.getX(), e.getY());
		}
	}

	/**
	 * allow dragging of rating
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent e) {
		updateValue(e);
	}

	/**
	 * does nothing, just to implement interface
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@SuppressWarnings("unused")
	public void mouseMoved(MouseEvent e) {
		// do nothing
	}

	/**
	 * react on a table change
	 * 
	 * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
	 */
	@SuppressWarnings("unused")
	public void tableChanged(TableModelEvent e) {
		if (e.getType() != TableModelEvent.UPDATE || !changing) {
			// react by stopping editing
			fireEditingStopped();
		}
	}

	/**
	 * this will update the current rating value by using a mouse event
	 * 
	 * @param e
	 */
	private void updateValue(MouseEvent e) {
		int range = image.getWidth();
		ImageRating newRating;
		if (e.getX() <= 0) {
			newRating = ImageRating.NONE;
		} else {
			int anz = ImageRating.values().length - 1;
			newRating = ImageRating.values()[Math.min((e.getX() * 5 / range) + 1, anz)];
		}
		image.setIcon(RatingRepresentation.getIcon(newRating));
		changing = true;
		img.setRating(newRating);
		changing = false;
	}
}
