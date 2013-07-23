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
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jimcat.gui.SwingClient;
import org.jimcat.model.Image;
import org.jimcat.model.libraries.ImageLibrary;
import org.jimcat.model.libraries.LibraryView;
import org.jimcat.model.notification.BeanChangeEvent;
import org.jimcat.model.notification.CollectionListener;
import org.jimcat.model.notification.ObservableCollection;

/**
 * The status info - just printing number of shown images.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class SelectionInfo extends JPanel {

	/**
	 * the Font used for labeling bars
	 */
	private static final Font LABEL_FONT = new JLabel().getFont().deriveFont(5);

	/**
	 * the color for the scale
	 */
	private static final Color LABEL_COLOR = Color.GRAY;

	/**
	 * a refernce to the running ImageLibrary
	 */
	private ImageLibrary library;

	/**
	 * the used library view
	 */
	private LibraryView view;

	/**
	 * the label used to show information
	 */
	private JLabel label;

	/**
	 * simple constructor
	 * 
	 * @param client
	 */
	public SelectionInfo(SwingClient client) {
		// extract information
		library = client.getImageControl().getLibrary();
		view = client.getViewControl().getLibraryView();

		// register listeners
		library.addListener(new ListObserver<ImageLibrary>());
		view.addListener(new ListObserver<LibraryView>());

		// init components
		initComponents();
	}

	/**
	 * build up content
	 */
	private void initComponents() {
		setLayout(new BorderLayout());
		setOpaque(false);

		label = new JLabel();
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(LABEL_FONT);
		label.setForeground(LABEL_COLOR);
		add(label, BorderLayout.CENTER);

		update();
	}

	/**
	 * update text
	 */
	private void update() {
		int count = library.size();
		int cur = view.size();
		label.setText("Showing " + cur + " of " + count + " images");
	}

	/**
	 * a generic listener useable for both, ImageLibrary and ImageView
	 * 
	 * @param <U>
	 */
	private class ListObserver<U extends ObservableCollection<Image, U>> implements CollectionListener<Image, U> {

		/**
		 * react on events
		 * 
		 * @see org.jimcat.model.notification.CollectionListener#basementChanged(org.jimcat.model.notification.ObservableCollection)
		 */
		@SuppressWarnings("unused")
		public void basementChanged(U collection) {
			update();
		}

		/**
		 * react on events
		 * 
		 * @see org.jimcat.model.notification.CollectionListener#elementsAdded(org.jimcat.model.notification.ObservableCollection,
		 *      java.util.Set)
		 */
		@SuppressWarnings("unused")
		public void elementsAdded(U collection, Set<Image> elements) {
			update();
		}

		/**
		 * react on events
		 * 
		 * @see org.jimcat.model.notification.CollectionListener#elementsRemoved(org.jimcat.model.notification.ObservableCollection,
		 *      java.util.Set)
		 */
		@SuppressWarnings("unused")
		public void elementsRemoved(U collection, Set<Image> elements) {
			update();
		}

		/**
		 * react on events
		 * 
		 * @see org.jimcat.model.notification.CollectionListener#elementsUpdated(org.jimcat.model.notification.ObservableCollection,
		 *      java.util.List)
		 */
		@SuppressWarnings("unused")
		public void elementsUpdated(U collection, List<BeanChangeEvent<Image>> events) {
			update();
		}
	}
}
