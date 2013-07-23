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

package org.jimcat.gui.perspective.detail;

import java.awt.BorderLayout;

import org.jimcat.gui.ViewControl;
import org.jimcat.gui.perspective.AbstractPerspective;

/**
 * Objects of this class form the detail perspective of JimCat.
 * 
 * It includes a table representing the current LibraryView. The table alsow
 * allowes you to edit image properties.
 * 
 * $Id: DetailPerspective.java 935 2007-06-15 09:21:09Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public class DetailPerspective extends AbstractPerspective {

	/**
	 * the table (main element)
	 */
	private DetailTable table;

	/**
	 * direct constructor
	 * @param control 
	 */
	public DetailPerspective(ViewControl control) {
		super(control);
		initComponents();
	}

	/**
	 * build up component structure
	 */
	private void initComponents() {
		// general
		setLayout(new BorderLayout());

		// table
		table = new DetailTable(getViewControl());
		add(table, BorderLayout.CENTER);

		// detail side bar
		add(new DetailSideBar(table.getTable()), BorderLayout.EAST);
	}

	/**
	 * @see org.jimcat.gui.perspective.AbstractPerspective#disablePerspective()
	 */
	@Override
	protected void disablePerspective() {
		table.setActive(false);
	}

	/**
	 * @see org.jimcat.gui.perspective.AbstractPerspective#enablePerspective()
	 */
	@Override
	protected void enablePerspective() {
		table.setActive(true);
	}

}
