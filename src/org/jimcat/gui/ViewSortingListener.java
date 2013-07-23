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

package org.jimcat.gui;

/**
 * an interface describing an listener to the current View.
 * 
 * This interface should be used to get informed about sorting changes.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public interface ViewSortingListener {

	/**
	 * this methode will be called if there are any changes to the current
	 * sorting setup.
	 * 
	 * @param control
	 */
	public void sortingChanged(ViewControl control);
}
