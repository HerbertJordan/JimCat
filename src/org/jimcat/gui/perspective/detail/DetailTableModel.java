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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.jimcat.model.Image;
import org.jimcat.model.libraries.LibraryView;
import org.jimcat.model.notification.BeanChangeEvent;
import org.jimcat.model.notification.CollectionListener;

/**
 * Represents the DetailTabelModel behind the DetailTable within the Detail
 * perspective.
 * 
 * $Id: DetailTableModel.java 935 2007-06-15 09:21:09Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public class DetailTableModel implements TableModel, CollectionListener<Image, LibraryView> {

	/**
	 * a list of listeners
	 */
	private List<TableModelListener> listeners;

	/**
	 * a reference to the represented view
	 */
	private LibraryView view;

	/**
	 * is this model actively watching the LibraryView
	 */
	private boolean active = true;

	/**
	 * was there a change since set to passive
	 */
	private boolean dirty = false;

	/**
	 * a constructor for this model
	 * @param view 
	 */
	public DetailTableModel(LibraryView view) {
		this.view = view;
		listeners = new CopyOnWriteArrayList<TableModelListener>();
		this.view.addListener(this);
	}

	/**
	 * change active state
	 * 
	 * @param active
	 */
	public void setActive(boolean active) {
		if (this.active != active) {
			this.active = active;
			// if reenabled - check changes
			if (active && dirty) {
				// no exact info tracked =>
				// simulate total exchange
				basementChanged(view);
				dirty = false;
			}
		}
	}

	/**
	 * just adds a listener to this model
	 * 
	 * @see javax.swing.table.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
	 */
	public void addTableModelListener(TableModelListener l) {
		listeners.add(l);
	}

	/**
	 * return fixed types from field columnClasses
	 * 
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	public Class<?> getColumnClass(int columnIndex) {
		return getColumn(columnIndex).getTyp();
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return DetailTableColumn.values().length;
	}

	/**
	 * the name from the static field
	 * 
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int columnIndex) {
		return getColumn(columnIndex).getName();
	}

	/**
	 * the nummer of rowes
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return view.size();
	}

	/**
	 * retrieve a value for the table
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		// first get image
		if (rowIndex < 0 || rowIndex >= view.size()) {
			return null;
		}
		Image img = view.getImage(rowIndex);

		// get Value
		DetailTableColumn column = getColumn(columnIndex);
		return column.getValue(view, img);
	}

	/**
	 * Only column 2-4 are editable
	 * 
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	@SuppressWarnings("unused")
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		DetailTableColumn column = getColumn(columnIndex);
		return column.isEditable();
	}

	/**
	 * unregister a listener
	 * 
	 * @see javax.swing.table.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
	 */
	public void removeTableModelListener(TableModelListener l) {
		listeners.remove(l);
	}

	/**
	 * Update a value, only column 2 and 3 are allowed
	 * 
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// update field
		if (rowIndex < 0 || rowIndex >= view.size()) {
			return;
		}
		Image img = view.getImage(rowIndex);
		DetailTableColumn column = getColumn(columnIndex);
		column.setValue(img, aValue);
	}

	/**
	 * The comleate collection has changed dramatically, update hole table
	 * @param collection 
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#basementChanged(org.jimcat.model.notification.ObservableCollection)
	 */
	@SuppressWarnings("unused")
	public void basementChanged(LibraryView collection) {
		if (active) {
			// Trigger TableModelEvent
			TableModelEvent event = new TableModelEvent(this);

			// inform listeners
			notifyListener(event);
		} else {
			dirty = true;
		}
	}

	/**
	 * A single row was added
	 * @param collection 
	 * @param elements 
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#elementsAdded(org.jimcat.model.notification.ObservableCollection,
	 *      java.util.Set)
	 */
	public void elementsAdded(LibraryView collection, Set<Image> elements) {
		if (active) {
			// inform listener
			notifyListener(collection, elements, TableModelEvent.INSERT);
		} else {
			dirty = true;
		}
	}

	/**
	 * some elements has been removed, inform the listeners
	 * @param collection 
	 * @param elements 
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#elementsRemoved(org.jimcat.model.notification.ObservableCollection,
	 *      java.util.Set)
	 */
	@SuppressWarnings("unused")
	public void elementsRemoved(LibraryView collection, Set<Image> elements) {
		if (active) {
			// it's to expensive to calculate former indizes -> just exchange all
			// Trigger TableModelEvent
			TableModelEvent event = new TableModelEvent(this);

			// inform listeners
			notifyListener(event);
		} else {
			dirty = true;
		}
	}

	/**
	 * notify listeners about a change
	 * 
	 * @param collection -
	 *            a collection to get indizes from
	 * @param elements -
	 *            the list of images modified
	 * @param type -
	 *            TableModelEvent INSERT / UPDATE / DELETE
	 */
	private void notifyListener(LibraryView collection, Set<Image> elements, int type) {
		// aggregate index ranges
		int index[] = new int[elements.size()];

		// get list of indizes
		List<Image> imgList = new ArrayList<Image>(elements);
		for (int i = 0; i < index.length; i++) {
			index[i] = collection.indexOf(imgList.get(i));
		}

		// sort indizes
		Arrays.sort(index);

		// aggregate events
		int start = 0;
		int end = start;
		while (start < index.length) {
			while (end < index.length - 1 && index[end] + 1 == index[end + 1]) {
				end++;
			}

			TableModelEvent event = new TableModelEvent(this, index[start], index[end], TableModelEvent.ALL_COLUMNS,
			        type);

			// inform listener
			notifyListener(event);

			// move pointer
			start = end + 1;
			end = start;
		}
	}

	/**
	 * returns the image displayed within row
	 * 
	 * @param rowIndex
	 * @return the image or null if index is out of bound
	 */
	public Image getImageAtRow(int rowIndex) {
		if (rowIndex < 0 || rowIndex >= view.size()) {
			return null;
		}
		return view.getImage(rowIndex);
	}

	/**
	 * get a column by index
	 * 
	 * @param columnIndex
	 * @return the column with this index
	 */
	private DetailTableColumn getColumn(int columnIndex) {
		return DetailTableColumn.values()[columnIndex];
	}

	/**
	 * informs all listeners about event
	 * 
	 * @param event
	 */
	private void notifyListener(TableModelEvent event) {
		// inform listeners
		for (TableModelListener listener : listeners) {
			listener.tableChanged(event);
		}
	}

	/**
	 * A image value has changed, update
	 * @param collection 
	 * @param events 
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#elementsUpdated(org.jimcat.model.notification.ObservableCollection,
	 *      java.util.List)
	 */
	@SuppressWarnings("unused")
	public void elementsUpdated(LibraryView collection, List<BeanChangeEvent<Image>> events) {

		if (active) {

			int startLine = -1;
			int endLine = -1;
			int startCol = -1;
			int endCol = -1;

			for (BeanChangeEvent<Image> event : events) {
				// create event
				Image img = event.getSource();
				int index = view.indexOf(img);

				DetailTableColumn column = DetailTableColumn.getColumnForProperty(event.getProperty());
				if (column == null) {
					// there is not column for this property
					// finish
					continue;
				}
				int columnIndex = column.ordinal();

				// update ranges - lines
				if (startLine == -1 || startLine > index) {
					startLine = index;
				}
				if (endLine == -1 || endLine < index) {
					endLine = index;
				}

				// update ranges - columns
				if (startCol == -1 || startCol > columnIndex) {
					startCol = columnIndex;
				}
				if (endCol == -1 || endCol < columnIndex) {
					endCol = columnIndex;
				}
			}

			// was there a remarkable change?
			if (startLine == -1 || startCol == -1) {
				return;
			}

			// is there only one Columns updated?
			int columnIndex = TableModelEvent.ALL_COLUMNS;
			if (startCol == endCol) {
				columnIndex = startCol;
			}

			// create event
			TableModelEvent tableEvent = new TableModelEvent(this, startLine, endLine, columnIndex,
			        TableModelEvent.UPDATE);

			// inform listener
			notifyListener(tableEvent);

		} else {
			dirty = true;
		}
	}

}
