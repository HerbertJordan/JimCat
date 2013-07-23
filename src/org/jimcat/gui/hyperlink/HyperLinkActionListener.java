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

package org.jimcat.gui.hyperlink;

import java.awt.event.ActionEvent;

import org.jdesktop.swingx.action.LinkAction;

/**
 * A Action Listener for a HyperLink
 * 
 * 
 * $Id$
 * 
 * @author csaf8653
 */
public class HyperLinkActionListener extends LinkAction<String> {

	private String url;

	/**
	 * @param url url to open when event occures
	 */
	public HyperLinkActionListener(String url) {
		this.url = url;
	}

	/**
	 * create on action event
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@SuppressWarnings("unused")
	public void actionPerformed(ActionEvent e) {
		BrowserStartup.invokeBrowser(url);
	}
}
