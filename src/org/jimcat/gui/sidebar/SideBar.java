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

package org.jimcat.gui.sidebar;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jimcat.gui.albumlist.AlbumList;
import org.jimcat.gui.tagtree.TagPanelPopupHandler;
import org.jvnet.substance.SubstanceLookAndFeel;

/**
 * A simple JPanel encapsulationg the SideBar
 * 
 * $Id: SideBar.java 894 2007-06-09 19:07:31Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public class SideBar extends JPanel {

	/**
	 * default constructor
	 */
	public SideBar() {
		initComponents();
	}

	/**
	 * build up content hierarchy
	 */
	private void initComponents() {
		this.setLayout(new BorderLayout());

		// general Scrolling
		JScrollPane pane = new JScrollPane();
		pane.setBorder(null);

		// Task Pane Container
		JXTaskPaneContainer container = new JXTaskPaneContainer();
		container.setOpaque(true);

		// TagFilter task
		JXTaskPane filter = new JXTaskPane();
		filter.setTitle("Tags");
		filter.add(new TagFilterTree());
		filter.setExpanded(true);
		filter.setOpaque(true);
		filter.setFocusable(false);
		filter.addMouseListener(new TagPanelPopupHandler(filter));
		container.add(filter);

		// add Tasks to Scroll pane
		pane.setViewportView(container);

		// TagFilter task
		JXTaskPane albums = new JXTaskPane();
		albums.setTitle("Image Lists");
		albums.add(new AlbumList());
		albums.setExpanded(true);
		albums.setOpaque(true);
		albums.setFocusable(false);
		container.add(albums);

		// add Tasks to Scroll pane
		pane.setViewportView(container);

		// enable watermark
		pane.putClientProperty(SubstanceLookAndFeel.WATERMARK_TO_BLEED, Boolean.TRUE);

		// add scroll pane to this component
		add(pane, BorderLayout.CENTER);

	}
}
