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

package org.jimcat.gui.wheellist;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.Scrollable;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang.ObjectUtils;

/**
 * A Swing component feigning a long list of visible items.
 * 
 * T ... Typ of represented items
 * 
 * $Id$
 * 
 * @author csaf7445
 * @param <T>
 */
public class WheelList<T> extends JComponent implements Scrollable {

	/**
	 * factory used to generate new Items
	 */
	private WheelListItemFactory<T> factory;

	/**
	 * a map of currently used cards
	 */
	private Map<Integer, WheelListItem<T>> map;

	/**
	 * the model to represent
	 */
	private WheelListModel<T> model;

	/**
	 * the selection model to use
	 */
	private ListSelectionModel selectionModel;

	/**
	 * the size of an item
	 */
	private Dimension itemSize = new Dimension(100, 100);

	/**
	 * the currently displayed elements
	 */
	private Rectangle currentArea;

	/**
	 * the last ViewRect layout was done for
	 */
	private Rectangle lastViewArea;

	/**
	 * number of current rows
	 */
	private int currentColumnCount;

	/**
	 * current vertical offset
	 */
	private int currentSideOffset;

	/**
	 * the index of the item owning focus (last selected element)
	 */
	private int focusIndex = 0;

	/**
	 * creates a new WheelList using given factory.
	 * 
	 * @param factory
	 * @param model
	 * @param selectionModel
	 */
	public WheelList(WheelListItemFactory<T> factory, WheelListModel<T> model, ListSelectionModel selectionModel) {
		this.factory = factory;
		this.model = model;
		this.selectionModel = selectionModel;

		// install observers
		model.addListDataListener(new ModelObserver());
		selectionModel.addListSelectionListener(new SelectionObserver());
		addMouseListener(new WheelListMouseHandler(this, selectionModel));
		addKeyListener(new WheelListKeyHandler(this, selectionModel));

		// init other variables
		map = new HashMap<Integer, WheelListItem<T>>();

		// gui settup
		setDoubleBuffered(true);
		this.setLayout(null);
		currentArea = new Rectangle(0, 0, 0, 0);
		currentColumnCount = 1;
	}

	/**
	 * @return the itemSize
	 */
	public Dimension getItemSize() {
		return itemSize;
	}

	/**
	 * @param itemSize
	 *            the itemSize to set
	 */
	public void setItemSize(Dimension itemSize) {
		if (!ObjectUtils.equals(this.itemSize, itemSize)) {
			this.itemSize = itemSize;
			updateView();
			setFocusIndex(focusIndex);
		}
	}

	/**
	 * does layout before printing
	 * 
	 * @see javax.swing.JComponent#paintChildren(java.awt.Graphics)
	 */
	@Override
	protected void paintChildren(Graphics g) {
		// 1) do layout
		Rectangle area = null;
		if (getParent() instanceof JViewport) {
			JViewport port = (JViewport) getParent();
			area = port.getViewRect();
		} else {
			Dimension size = getSize();
			area = new Rectangle(0, 0, size.width, size.height);
		}
			
		// do layout
		currentColumnCount = getWidth() / itemSize.width;
		currentSideOffset = (getWidth() - itemSize.width * currentColumnCount) / 2;

		int startRow = area.y / itemSize.height;
		int endRow = (area.y + area.height) / itemSize.height;

		int startCol = (area.x - currentSideOffset) / itemSize.width;
		int endCol = (area.x - currentSideOffset + area.width) / itemSize.width - 1;

		// update current rectangle
		currentArea = new Rectangle(startCol, startRow, endCol - startCol, endRow - startRow);

		// only do layout if necessary
		if (!ObjectUtils.equals(currentArea, lastViewArea)) {

			// remove unnecessary items
			for (Integer key : new HashSet<Integer>(map.keySet())) {
				int keyValue = key.intValue();
				if (keyValue>=0 && !containesIndex(currentArea, keyValue)) {
					WheelListItem<T> item = map.get(key);
					remove(item);
					map.remove(key);
					map.put((-1)*key.intValue()-1, item);
				}
			}
			
			// add new items
			for (int i = startRow; i <= endRow; i++) {
				for (int j = startCol; j <= endCol; j++) {
					// place item
					int index = i * currentColumnCount + j;
					if (index < model.getSize()) {

						// get item
						WheelListItem<T> item = getItemForIndex(index);

						// prepaire item
						item.setElement(model.getElementAt(index));
						item.setSelected(selectionModel.isSelectedIndex(index));
						
						item.setSize(itemSize);
						item.setLocation(currentSideOffset + j * itemSize.width, i * itemSize.height);
						
						// add item
						if (item.getParent()!=this) {
							add(item);
						}
					}
				}
			}

			validate();

			lastViewArea = currentArea;
		}

		// 2) paint
		super.paintChildren(g);
	}

	/**
	 * get a WheelList item for given index
	 * 
	 * @param index
	 * @return the WheelList item for given index
	 */
	private WheelListItem<T> getItemForIndex(int index) {
		// fastest -> use item from cache
		WheelListItem<T> res = map.get(index);
		if (res != null) {
			return res;
		}

		// else find free item
		for (Integer key : map.keySet()) {
			int itemIndex = key.intValue();

			if (!containesIndex(currentArea, itemIndex)) {

				WheelListItem<T> item = map.get(key);
				map.remove(itemIndex);
				map.put(index, item);
				return item;
			}

		}

		// create new item
		res = factory.getNewItem();
		res.setSize(itemSize);
		map.put(index, res);
		return res;

	}
	
	/**
	 * tests if the given index is within the given area
	 * @param area the area to test
	 * @param index the index to test
	 * @return if given index is part of shown rect
	 */
	private boolean containesIndex(Rectangle area, int index) {
		if (area==null) {
			return false;
		}
		int x = index % currentColumnCount;
		int y = index / currentColumnCount;
		return (area.x <= x && x <= area.x + area.width && area.y <= y && y <= area.y
		        + area.height);
	}

	/**
	 * force an update
	 */
	private void updateView() {
		lastViewArea = null;
		revalidate();
		repaint();
	}

	/**
	 * gets the index of the item at the given point
	 * 
	 * @param point
	 * @return the index of the item at the given point or -1 of there is no
	 *         such item
	 */
	public int getIndexAt(Point point) {
		// calculate index from point
		int res = (point.y / itemSize.height) * currentColumnCount + (point.x - currentSideOffset) / itemSize.width;
		if (res < 0 || res >= model.getSize()) {
			return -1;
		}
		return res;
	}

	/**
	 * @return the currentColumnCount
	 */
	public int getCurrentColumnCount() {
		return currentColumnCount;
	}

	/**
	 * @return the focusIndex
	 */
	public int getFocusIndex() {
		return focusIndex;
	}

	/**
	 * @param focusIndex
	 *            the focusIndex to set
	 */
	public void setFocusIndex(int focusIndex) {
		this.focusIndex = focusIndex;
		// scroll to item
		if (focusIndex >= 0 && focusIndex < model.getSize()) {
			// get Rect for given index
			int x = focusIndex % currentColumnCount;
			int y = focusIndex / currentColumnCount;

			// get Rect
			Rectangle rect = new Rectangle(x * itemSize.width, y * itemSize.height, itemSize.width, itemSize.height);
			scrollRectToVisible(rect);
		}
	}

	/**
	 * @return the model
	 */
	public WheelListModel<T> getModel() {
		return model;
	}

	// ////////////////
	// Scrollable
	// ////////////////

	/**
	 * this is used to format within a scrollpane
	 * 
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		// own calculation
		// 1) how many images
		int count = model.getSize();

		// if there are no items, they do not need any space
		if (count < 1) {
			// (0,0) produces an exception
			return new Dimension(10, 10);
		}

		// 2) how many are in a line
		int columCount = (getSize().width) / (itemSize.width);

		// 3) rowcount
		int rowCount = (int) Math.ceil(((float) count) / columCount);

		int width = columCount * itemSize.width;
		int heigh = rowCount * itemSize.height;
		return new Dimension(width, heigh);
	}

	/**
	 * @see javax.swing.Scrollable#getPreferredScrollableViewportSize()
	 */
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	/**
	 * @see javax.swing.Scrollable#getScrollableBlockIncrement(java.awt.Rectangle,
	 *      int, int)
	 */
	@SuppressWarnings("unused")
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return itemSize.height;
	}

	/**
	 * @see javax.swing.Scrollable#getScrollableTracksViewportHeight()
	 */
	public boolean getScrollableTracksViewportHeight() {
		if (getParent() instanceof JViewport) {
			return (((JViewport) getParent()).getHeight() > getPreferredSize().height);
		}
		return false;
	}

	/**
	 * @see javax.swing.Scrollable#getScrollableTracksViewportWidth()
	 */
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	/**
	 * @see javax.swing.Scrollable#getScrollableUnitIncrement(java.awt.Rectangle,
	 *      int, int)
	 */
	@SuppressWarnings("unused")
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		// half of item size
		return itemSize.height / 10;
	}

	/**
	 * signal parent components that the size of this component has changed
	 */
	private void fireResizeEvent() {
		// inform listeners about resize event
		ComponentListener listener[] = getComponentListeners();
		// test if there are listeners
		if (listener.length == 0) {
			return;
		}

		// create event and send
		ComponentEvent event = new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED);
		for (ComponentListener l : listener) {
			l.componentResized(event);
		}
	}

	/**
	 * a private class to observer list models
	 */
	private class ModelObserver implements ListDataListener {

		/**
		 * total exchange
		 * 
		 * @see javax.swing.event.ListDataListener#contentsChanged(javax.swing.event.ListDataEvent)
		 */
		public void contentsChanged(ListDataEvent e) {
			if (e.getType() != ListDataEvent.CONTENTS_CHANGED) {
				return;
			}

			// invalidate map index entries by moving elements to unuseable
			// index
			Set<WheelListItem<T>> entries = new HashSet<WheelListItem<T>>(map.values());
			map.clear();
			int i = -1;
			for (WheelListItem<T> item : entries) {
				map.put(i--, item);
			}
			
			removeAll();
			updateView();
		}

		/**
		 * react on added items
		 * 
		 * @see javax.swing.event.ListDataListener#intervalAdded(javax.swing.event.ListDataEvent)
		 */
		public void intervalAdded(ListDataEvent e) {
			if (e.getType() != ListDataEvent.INTERVAL_ADDED) {
				return;
			}

			// calculate teshold
			int treshold = currentArea.x + currentArea.width + (currentArea.y + currentArea.height)
			        * currentColumnCount;

			// update if added before given element
			if (e.getIndex0() <= treshold) {
				updateView();
			} else {
				// just inform listeners about possible resize
				fireResizeEvent();
			}
		}

		/**
		 * react on removed items
		 * 
		 * @see javax.swing.event.ListDataListener#intervalRemoved(javax.swing.event.ListDataEvent)
		 */
		public void intervalRemoved(ListDataEvent e) {
			if (e.getType() != ListDataEvent.INTERVAL_REMOVED) {
				return;
			}

			// calculate teshold
			int treshold = currentArea.x + currentArea.width + (currentArea.y + currentArea.height)
			        * currentColumnCount;

			// update if added before given element
			if (e.getIndex0() <= treshold) {
				updateView();
			} else {
				// just inform listeners about possible resize event
				fireResizeEvent();
			}
		}
	}

	/**
	 * private class to observe installed selection model
	 */
	private class SelectionObserver implements ListSelectionListener {

		/**
		 * selected values have changed
		 * 
		 * @param e
		 * 
		 * @see ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
		 */
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			for (int i = e.getFirstIndex(); i <= e.getLastIndex(); i++) {
				WheelListItem<T> item = map.get(i);
				if (item != null) {
					item.setSelected(selectionModel.isSelectedIndex(i));
				}
			}
			setFocusIndex(selectionModel.getMinSelectionIndex());
		}
	}

}
