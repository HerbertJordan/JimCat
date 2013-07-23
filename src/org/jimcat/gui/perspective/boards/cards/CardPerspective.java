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
import java.awt.Dimension;

import org.jimcat.gui.ViewControl;
import org.jimcat.gui.perspective.AbstractPerspective;
import org.jimcat.gui.perspective.boards.Board;

/**
 * Card Perspective new
 * 
 * $Id$
 * 
 * @author Michael & Herbert
 */
public class CardPerspective extends AbstractPerspective {

	/**
	 * dimension of each card
	 */
	private static final Dimension CARD_SIZE = new Dimension(390,230);
	
	/**
	 * the board used to display items
	 */
	private Board board;
	
	/**
	 * @param control
	 */
	public CardPerspective(ViewControl control) {
		super(control);
		initComponents();
	}

	/**
	 * build up component hierarchie
	 */
	private void initComponents() {
		setLayout(new BorderLayout());

		CardFactory factory = new CardFactory();
		board = new Board(getViewControl(), factory);
		board.setItemSize(CARD_SIZE);
		add(board, BorderLayout.CENTER);
	}

	/**
     * disalbe library view observing
     * @see org.jimcat.gui.perspective.AbstractPerspective#disablePerspective()
     */
    @Override
    protected void disablePerspective() {
	    board.setActive(false);
    }

	/**
     * enalbe library view observing
     * @see org.jimcat.gui.perspective.AbstractPerspective#enablePerspective()
     */
    @Override
    protected void enablePerspective() {
    	board.setActive(true);
    }
}
