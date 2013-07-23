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

package org.jimcat.gui.toolbar;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JPanel;

import org.jimcat.gui.ImageControl;
import org.jimcat.gui.ViewControl;
import org.jimcat.gui.frame.JimCatFrame;
import org.jimcat.gui.histogram.HistogramBar;
import org.jimcat.gui.histogram.image.ImageHistogram;


/**
 * A class forming the JimCatFrame Toolbar containing Perspective selection,
 * DateSeeker and TextFilter.
 * 
 * $Id: ToolBar.java 935 2007-06-15 09:21:09Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public class ToolBar extends JPanel {

	/**
	 * a referenc to containing frame
	 */
	private JimCatFrame jimCatFrame;

	/**
	 * a reference to the viewcontrol of this application instance
	 */
	private ViewControl viewControl;

	/**
	 * a reference to the image control of this application instance
	 */
	private ImageControl imageControl;
	
	/**
	 * constructor requiring containing frame
	 * 
	 * @param frame
	 * @param viewControl 
	 * @param imageControl 
	 */
	public ToolBar(JimCatFrame frame, ViewControl viewControl, ImageControl imageControl) {
		this.jimCatFrame = frame;
		this.viewControl = viewControl;
		this.imageControl = imageControl;
		initComponents();
	}

	/**
	 * build up content
	 */
	private void initComponents() {
		setLayout(new BorderLayout());

		// View Selector + freeze
		JPanel west = new JPanel();
		west.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		west.add(new PerspectiveSwitch(jimCatFrame));
		west.add(new FreezeSwitch(viewControl));
		add(west, BorderLayout.WEST);

		// DateSlider
		add(new HistogramBar(new ImageHistogram(viewControl, imageControl)), BorderLayout.CENTER);

		// Textsuche
		add(new TextSearch(viewControl), BorderLayout.EAST);
	}

}
