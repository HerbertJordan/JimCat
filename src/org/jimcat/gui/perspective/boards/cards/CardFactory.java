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

package org.jimcat.gui.perspective.boards.cards;

import org.jimcat.gui.wheellist.WheelListItem;
import org.jimcat.gui.wheellist.WheelListItemFactory;
import org.jimcat.model.Image;

/**
 * A Card factory producing WheelList Items for a wheel list.
 * 
 * $Id$
 * 
 * @author csaf7445
 */
public class CardFactory extends WheelListItemFactory<Image> {

	/**
	 * create a new useable instance
	 * 
	 * @see org.jimcat.gui.wheellist.WheelListItemFactory#getNewItem()
	 */
	@Override
	public WheelListItem<Image> getNewItem() {
		return new WheelListCard();
	}

}
