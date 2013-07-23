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

/**
 * The orientation of the image within the card
 *
 * $Id$
 * @author Herbert
 */
public enum CardOrientation {
	EAST(BorderLayout.EAST), WEST(BorderLayout.WEST);
	
	/**
	 * the assoziated borderlayout constant
	 */
	private String borderLayoutConstant;
	
	/**
     * private constructor
	 * @param constant 
     */
    private CardOrientation(String constant) {
	    this.borderLayoutConstant = constant;
    }
	
	/**
     * @return the borderLayoutConstant
     */
    public String getBorderLayoutConstant() {
	    return borderLayoutConstant;
    }
}
