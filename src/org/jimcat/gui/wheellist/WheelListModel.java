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

import javax.swing.event.ListDataListener;

/**
 * Describing a WheelListModel.
 *
 * $Id$
 * @author csaf7445
 * @param <T> 
 */
public interface WheelListModel<T> {

	/** 
	   * Returns the length of the list.
	   * @return the length of the list
	   */
	  int getSize();

	  /**
	   * Returns the value at the specified index.  
	   * @param index the requested index
	   * @return the value at <code>index</code>
	   */
	  T getElementAt(int index);
	  
	  /**
	   * Adds a listener to the list that's notified each time a change
	   * to the data model occurs.
	   * @param l the <code>ListDataListener</code> to be added
	   */  
	  void addListDataListener(ListDataListener l);

	  /**
	   * Removes a listener from the list that's notified each time a 
	   * change to the data model occurs.
	   * @param l the <code>ListDataListener</code> to be removed
	   */  
	  void removeListDataListener(ListDataListener l);
	
}
