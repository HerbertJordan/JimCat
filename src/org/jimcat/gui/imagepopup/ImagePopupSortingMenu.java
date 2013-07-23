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

package org.jimcat.gui.imagepopup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.jimcat.gui.SwingClient;
import org.jimcat.gui.ViewControl;
import org.jimcat.model.Image;
import org.jimcat.model.comparator.AlbumOrderComparator;
import org.jimcat.model.comparator.ComparatorChainProxy;
import org.jimcat.model.comparator.ModificationDateComparator;
import org.jimcat.model.comparator.DateAddedComparator;
import org.jimcat.model.comparator.DescriptionComparator;
import org.jimcat.model.comparator.DuplicateComparator;
import org.jimcat.model.comparator.FileSizeComparator;
import org.jimcat.model.comparator.HeightComparator;
import org.jimcat.model.comparator.LastExportPathComparator;
import org.jimcat.model.comparator.PathComparator;
import org.jimcat.model.comparator.RatingComparator;
import org.jimcat.model.comparator.TitleComparator;
import org.jimcat.model.comparator.WidthComparator;
import org.jimcat.model.comparator.exifcomparator.ExifApertureComparator;
import org.jimcat.model.comparator.exifcomparator.ExifDateTakenComparator;
import org.jimcat.model.comparator.exifcomparator.ExifExposureComparator;
import org.jimcat.model.comparator.exifcomparator.ExifFlashComparator;
import org.jimcat.model.comparator.exifcomparator.ExifFocalComparator;
import org.jimcat.model.comparator.exifcomparator.ExifIsoComparator;
import org.jimcat.model.comparator.exifcomparator.ExifManufacturerComparator;
import org.jimcat.model.comparator.exifcomparator.ExifModelComparator;

/**
 * The submenu in the popup menu for sorting.
 * 
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class ImagePopupSortingMenu extends JMenu implements ActionListener {

	/**
	 * a list of displayed sorter
	 */
	private static List<Sorter> sorter;

	/**
	 * the view control to modify
	 */
	private ViewControl control;

	/**
	 * the last selected sorter
	 */
	private Comparator<Image> lastSorter;

	/**
	 * constructor requesting ViewControl
	 */
	public ImagePopupSortingMenu() {
		this.control = SwingClient.getInstance().getViewControl();

		// build up content
		initComponents();
	}

	/**
	 * build up menu items
	 */
	private void initComponents() {

		// build up items from list
		int i = 0;
		for (Sorter item : getSorter()) {
			// create item
			JMenuItem menuItem = new JMenuItem(item.titel);
			// config action mechanism
			menuItem.setActionCommand("" + i++);
			menuItem.addActionListener(this);
			// add to list
			add(menuItem);
		}
	}

	/**
	 * react on event
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		// get index clicked on
		int index = Integer.parseInt(e.getActionCommand());

		// setup sorter
		Comparator<Image> newSorter = getSorter().get(index).comparator;
		// if sorter is selected twice => revert sorting
		if (newSorter == lastSorter) {
			ComparatorChainProxy<Image> chain = new ComparatorChainProxy<Image>();
			chain.addComparator(newSorter, true);
			newSorter = chain;
		}
		// setup new sorter
		control.setSorting(newSorter);
		lastSorter = newSorter;
	}

	/**
	 * get access / generate list of sorting options
	 * 
	 * @return a list of sorting option
	 */
	private List<Sorter> getSorter() {
		if (sorter == null) {
			// build up sorter
			sorter = new LinkedList<Sorter>();

			// build list of sorting orders
			sorter.add(new Sorter("Album Order", new AlbumOrderComparator(null)));
			sorter.add(new Sorter("Title", new TitleComparator()));
			sorter.add(new Sorter("Rating", new RatingComparator()));
			sorter.add(new Sorter("Modification Date", new ModificationDateComparator()));
			sorter.add(new Sorter("Date Added", new DateAddedComparator()));
			sorter.add(new Sorter("Duplicate", new DuplicateComparator()));
			sorter.add(new Sorter("Description", new DescriptionComparator()));
			sorter.add(new Sorter("Filesize", new FileSizeComparator()));
			sorter.add(new Sorter("Height", new HeightComparator()));
			sorter.add(new Sorter("Width", new WidthComparator()));
			sorter.add(new Sorter("Path", new PathComparator()));
			sorter.add(new Sorter("Last Export Path", new LastExportPathComparator()));
			sorter.add(new Sorter("Aperture", new ExifApertureComparator()));
			sorter.add(new Sorter("Date Taken", new ExifDateTakenComparator()));
			sorter.add(new Sorter("Exposure", new ExifExposureComparator()));
			sorter.add(new Sorter("Flash", new ExifFlashComparator()));
			sorter.add(new Sorter("Focal", new ExifFocalComparator()));
			sorter.add(new Sorter("Iso", new ExifIsoComparator()));
			sorter.add(new Sorter("Manufacturer", new ExifManufacturerComparator()));
			sorter.add(new Sorter("Model", new ExifModelComparator()));
		}
		return sorter;
	}

	/**
	 * a item within this menu
	 * 
	 * $Id$
	 * 
	 * @author Herbert
	 */
	private class Sorter {
		/**
		 * the titel of this item
		 */
		private String titel;

		/**
		 * the comparator realizing the sorting
		 */
		private Comparator<Image> comparator;

		/**
		 * direct constructor
		 * 
		 * @param titel
		 * @param sorter
		 */
		public Sorter(String titel, Comparator<Image> sorter) {
			this.titel = titel;
			this.comparator = sorter;
		}
	}

}
