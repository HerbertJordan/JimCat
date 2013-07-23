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

package org.jimcat.gui.histogram;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * A context menu used within Histogram to switch between dimensions and
 * resolutions
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class HistogramPopup extends JPopupMenu {

	/**
	 * used to spearte dimension / resolution
	 */
	private String NUMBER_SPLIT = "-";

	/**
	 * the histogram this menu is supporting
	 */
	private Histogram histogram;

	/**
	 * create a new popup menu for the given histogram using given model
	 * 
	 * @param histogram -
	 *            the histogram to work for
	 * @param model -
	 *            the model
	 */
	public HistogramPopup(Histogram histogram, HistogramModel model) {
		this.histogram = histogram;

		// init menu
		initComponents(model);
	}

	/**
	 * build up this component using given model
	 * 
	 * @param histModel
	 */
	private void initComponents(HistogramModel histModel) {
		ItemListener listener = new ItemListener();
		// iterate through dimensions
		for (int i = 0; i < histModel.getCountDimension(); i++) {
			// if there is only one resolutions, use JMenuItem
			if (histModel.getCountResolutions(i) == 1) {
				JMenuItem item = new JMenuItem(histModel.getNameFor(i));
				item.setActionCommand(i + NUMBER_SPLIT + 0);
				item.addActionListener(listener);
				add(item);
			} else {
				// use menu group
				JMenu subMenu = new JMenu(histModel.getNameFor(i));
				// add resolutions
				for (int j = 0; j < histModel.getCountResolutions(i); j++) {
					JMenuItem item = new JMenuItem(histModel.getNameFor(i, j));
					item.setActionCommand(i + NUMBER_SPLIT + j);
					item.addActionListener(listener);
					subMenu.add(item);
				}
				add(subMenu);
			}
		}
	}

	/**
	 * private class reacting on item selection
	 */
	private class ItemListener implements ActionListener {
		/**
		 * so someone clicked on an item ...
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			// get target
			String cords[] = e.getActionCommand().split(NUMBER_SPLIT);
			if (cords.length != 2) {
				return;
			}

			// extract values
			int dim = Integer.parseInt(cords[0]);
			int res = Integer.parseInt(cords[1]);

			// update histogram
			histogram.showResolution(dim, res);
		}
	}

}
