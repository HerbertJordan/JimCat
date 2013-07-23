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

/**
 * A Factory used by the WheelList to generate new WheelList items.
 * 
 * T .. type of represended objects
 * 
 * $Id$
 * 
 * @author csaf7445
 * @param <T> 
 */
public abstract class WheelListItemFactory<T> {

	/**
	 * produce a new item
	 * 
	 * @return - a new, useable item
	 */
	public abstract WheelListItem<T> getNewItem();

}