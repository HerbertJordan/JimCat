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

import javax.swing.JComponent;

/**
 * An item used within the WheelList.
 *
 * $Id$
 * @author csaf7445
 * @param <T> 
 */
public abstract class WheelListItem<T> extends JComponent {
	
	/**
	 * the represented element
	 */
	private T element;

	/**
	 * is this item selected
	 */
	private boolean selected;
	
	/**
     * @return the element
     */
    public T getElement() {
    	return element;
    }

	/**
     * @param element the element to set
     */
    public void setElement(T element) {
    	this.element = element;
    }

	/**
     * @return the selected
     */
    public boolean isSelected() {
    	return selected;
    }

	/**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
    	this.selected = selected;
    }
	
}
