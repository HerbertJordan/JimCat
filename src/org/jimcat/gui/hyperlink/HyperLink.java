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

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jdesktop.swingx.JXHyperlink;

/**
 * An encapsulation of a JXHyperlink that installs a HyperLinkActionListener.
 * 
 * 
 * $Id$
 * 
 * @author csaf8653
 */
public class HyperLink extends JPanel {

	private JXHyperlink hyperlink;

	/**
	 * create a new hiperlink component with given text and link
	 * 
	 * @param url
	 * @param text
	 */
	public HyperLink(String url, String text) {
		this.setLayout(new BorderLayout());
		this.hyperlink = new JXHyperlink(new HyperLinkActionListener(url));
		this.hyperlink.setText(text);
		hyperlink.setHorizontalAlignment(SwingConstants.CENTER);
		hyperlink.setFocusable(false);
		this.add(hyperlink, BorderLayout.CENTER);
	}
}
