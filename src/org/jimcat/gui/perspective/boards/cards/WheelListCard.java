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

import java.awt.BorderLayout;

import org.jimcat.gui.wheellist.WheelListItem;
import org.jimcat.model.Image;

/**
 * An item useable within a wheel list
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class WheelListCard extends WheelListItem<Image> {

	/**
	 * the contained card
	 */
	private Card card;

	/**
	 * default constructor
	 */
	public WheelListCard() {
		initComponents();
	}

	/**
	 * init components
	 */
	private void initComponents() {
		setOpaque(false);
		setLayout(new BorderLayout());

		card = new Card();
		add(card, BorderLayout.CENTER);
	}

	/**
	 * exchange shown image
	 * 
	 * @see org.jimcat.gui.wheellist.WheelListItem#setElement(java.lang.Object)
	 */
	@Override
	public void setElement(Image element) {
		card.setImage(element);
	}

	/**
	 * get shown image
	 * 
	 * @see org.jimcat.gui.wheellist.WheelListItem#getElement()
	 */
	@Override
	public Image getElement() {
		return card.getImage();
	}

	/**
	 * @return true if this card is selected
	 * @see org.jimcat.gui.perspective.boards.cards.Card#isSelected()
	 */
	@Override
	public boolean isSelected() {
		return card.isSelected();
	}

	/**
	 * @param selected
	 * @see org.jimcat.gui.perspective.boards.cards.Card#setSelected(boolean)
	 */
	@Override
	public void setSelected(boolean selected) {
		card.setSelected(selected);
	}

}
