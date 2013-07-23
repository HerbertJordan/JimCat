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
 * This interface should be used to get informed about filter changes.
 * 
 * $Id: ViewFilterListener.java 458 2007-05-01 22:01:29Z 07g1t1u3 $
 * 
 * @author Herbert
 */
public interface ViewFilterListener {

	/**
	 * this methode will be called if there are any changes to the current
	 * filter setup.
	 * 
	 * @param control
	 */
	public void filterChanges(ViewControl control);
	
}
