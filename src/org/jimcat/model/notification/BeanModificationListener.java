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

package org.jimcat.model.notification;


/**
 * A listener interface to observe BeanModification Transactions.
 *
 * Up to now, only commit events are supported
 *
 * $Id$
 * @author Herbert
 */
public interface BeanModificationListener {

	/**
	 * informs listener that the given changes have been commited
	 * 
	 * @param modification
	 */
	public void changesCommited(BeanModification modification);
	
}
