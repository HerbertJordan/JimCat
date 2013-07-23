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

package org.jimcat.gui.smartlisteditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.jimcat.gui.smartlisteditor.model.AlbumFilterNode;
import org.jimcat.gui.smartlisteditor.model.DuplicateFilterNode;
import org.jimcat.gui.smartlisteditor.model.ExifMetadataFilterNode;
import org.jimcat.gui.smartlisteditor.model.FileSizeFilterNode;
import org.jimcat.gui.smartlisteditor.model.FilterTreeNode;
import org.jimcat.gui.smartlisteditor.model.GroupFilterTreeNode;
import org.jimcat.gui.smartlisteditor.model.HasTagsFilterNode;
import org.jimcat.gui.smartlisteditor.model.ImageSizeFilterNode;
import org.jimcat.gui.smartlisteditor.model.ImportFilterNode;
import org.jimcat.gui.smartlisteditor.model.IsPartOfAlbumFilterNode;
import org.jimcat.gui.smartlisteditor.model.MegaPixelFilterNode;
import org.jimcat.gui.smartlisteditor.model.PictureTakenFilterNode;
import org.jimcat.gui.smartlisteditor.model.RatingFilterNode;
import org.jimcat.gui.smartlisteditor.model.RelativeDateFilterNode;
import org.jimcat.gui.smartlisteditor.model.SmartListFilterNode;
import org.jimcat.gui.smartlisteditor.model.TagFilterNode;
import org.jimcat.gui.smartlisteditor.model.TextFilterNode;
import org.jimcat.model.Album;
import org.jimcat.model.ImageRating;
import org.jimcat.model.SmartList;
import org.jimcat.model.filter.ImportFilter;
import org.jimcat.model.filter.RatingFilter;
import org.jimcat.model.filter.ImportFilter.Type;
import org.jimcat.model.filter.metadata.ExifMetadataFilter;
import org.jimcat.model.filter.metadata.FileSizeFilter;
import org.jimcat.model.filter.metadata.ImageSizeFilter;
import org.jimcat.model.filter.metadata.MegaPixelFilter;
import org.jimcat.model.filter.metadata.PictureTakenFilter;
import org.jimcat.model.filter.metadata.RelativeDateFilter;
import org.jimcat.model.filter.metadata.TextFilter;
import org.jimcat.model.filter.metadata.ExifMetadataFilter.ExifMetadataProperty;
import org.jimcat.model.filter.metadata.RelativeDateFilter.ReferenceDate;
import org.jimcat.model.filter.metadata.RelativeDateFilter.TimeUnit;
import org.joda.time.DateTime;

/**
 * A submenu to add new Filter to a group
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class FilterTreeAddPopup extends JMenu {

	/**
	 * string action commands ...
	 */
	private static final String GROUP = "group";

	private static final String ALBUM = "album";

	private static final String SMART = "smart";

	private static final String TAG = "tag";

	private static final String IMPORT = "import";

	private static final String DUPLICATE = "duplicat";

	private static final String IMAGESIZE = "imagesize";

	private static final String FILESIZE = "filesize";

	private static final String PICTURE_TAKEN = "picturetaken";

	private static final String TEXT = "text";

	private static final String RATING = "rating";

	private static final String MEGAPIXEL = "megapixel";

	private static final String HAS_TAGS = "has tags";

	private static final String IS_PART_OF_ALBUM = "is part of album";

	private static final String RELATIVE_DATE = "relative date";

	private static final String EXIF_METADATA = "exif metadata";

	/**
	 * a reference to the containing menu
	 */
	private FilterTreePopupMenu menu;

	/**
	 * a constructor requesting the containing menu
	 * 
	 * @param menu -
	 *            the menu containing this menu
	 */
	public FilterTreeAddPopup(FilterTreePopupMenu menu) {
		this.menu = menu;

		// build up menu
		initComponents();
	}

	/**
	 * build up menu
	 */
	private void initComponents() {
		JMenuItem tmp = null;
		EventDispatcher listener = new EventDispatcher();

		// logical filter
		tmp = new JMenuItem("Group");
		tmp.setActionCommand(GROUP);
		tmp.addActionListener(listener);
		add(tmp);

		// Album filter
		tmp = new JMenuItem("Album Filter");
		tmp.setActionCommand(ALBUM);
		tmp.addActionListener(listener);
		add(tmp);

		// SmartList filter
		tmp = new JMenuItem("Smart List Filter");
		tmp.setActionCommand(SMART);
		tmp.addActionListener(listener);
		add(tmp);

		// tag filter
		tmp = new JMenuItem("Tag Filter");
		tmp.setActionCommand(TAG);
		tmp.addActionListener(listener);
		add(tmp);

		// has tag filter
		tmp = new JMenuItem("Any Tag Filter");
		tmp.setActionCommand(HAS_TAGS);
		tmp.addActionListener(listener);
		add(tmp);

		// is part of album filter
		tmp = new JMenuItem("Part of Album Filter");
		tmp.setActionCommand(IS_PART_OF_ALBUM);
		tmp.addActionListener(listener);
		add(tmp);

		// import filter
		tmp = new JMenuItem("Import Filter");
		tmp.setActionCommand(IMPORT);
		tmp.addActionListener(listener);
		add(tmp);

		// duplicate
		tmp = new JMenuItem("Duplicate Filter");
		tmp.setActionCommand(DUPLICATE);
		tmp.addActionListener(listener);
		add(tmp);

		// rating
		tmp = new JMenuItem("Rating Filter");
		tmp.setActionCommand(RATING);
		tmp.addActionListener(listener);
		add(tmp);

		// megapixel filter
		tmp = new JMenuItem("MegaPixel Filter");
		tmp.setActionCommand(MEGAPIXEL);
		tmp.addActionListener(listener);
		add(tmp);

		// image size
		tmp = new JMenuItem("Image Size Filter");
		tmp.setActionCommand(IMAGESIZE);
		tmp.addActionListener(listener);
		add(tmp);

		// file size
		tmp = new JMenuItem("Filesize Filter");
		tmp.setActionCommand(FILESIZE);
		tmp.addActionListener(listener);
		add(tmp);

		// relative date filter
		tmp = new JMenuItem("Relative Date Filter");
		tmp.setActionCommand(RELATIVE_DATE);
		tmp.addActionListener(listener);
		add(tmp);

		// picture taken filter
		tmp = new JMenuItem("Picture Taken Filter");
		tmp.setActionCommand(PICTURE_TAKEN);
		tmp.addActionListener(listener);
		add(tmp);

		// text filter
		tmp = new JMenuItem("Text Filter");
		tmp.setActionCommand(TEXT);
		tmp.addActionListener(listener);
		add(tmp);

		// exif filter
		tmp = new JMenuItem("Exif Filter");
		tmp.setActionCommand(EXIF_METADATA);
		tmp.addActionListener(listener);
		add(tmp);
	}

	/**
	 * get the node you should add new filter nodes
	 * @param node 
	 */
	private void addNode(FilterTreeNode node) {
		// insert to current node
		GroupFilterTreeNode dest = (GroupFilterTreeNode) menu.getCurrentNode();
		dest.addChild(node);

		menu.getTree().startEditingAtPath(node.getPath());
	}

	/**
	 * add a new Group filter tree node
	 */
	private void addGroup() {
		addNode(new GroupFilterTreeNode(null, GroupFilterTreeNode.Type.ALL));
	}

	/**
	 * add a new Album filter
	 */
	private void addAlbumFilter() {
		addNode(new AlbumFilterNode(null, (Album) null));
	}

	/**
	 * add a new SmartList filter
	 */
	private void addSmartListFilter() {
		addNode(new SmartListFilterNode(null, (SmartList) null));
	}

	/**
	 * add a new TagFilter
	 */
	private void addTagFilter() {
		addNode(new TagFilterNode(null));
	}

	/**
	 * add new Import Filter
	 */
	private void addImportFilter() {
		ImportFilter filter = new ImportFilter(Type.EXACT, 0);
		addNode(new ImportFilterNode(null, filter));
	}

	/**
	 * add new Duplciate filter
	 */
	private void addDuplicateFilter() {
		addNode(new DuplicateFilterNode(null));
	}

	/**
	 * add new image size filter
	 */
	private void addImageSizeFilter() {
		ImageSizeFilter filter = new ImageSizeFilter(ImageSizeFilter.Type.BIGGER_THAN, 800, 600);
		addNode(new ImageSizeFilterNode(null, filter));
	}

	/**
	 * add new file size filter
	 */
	private void addFileSizeFilter() {
		FileSizeFilter filter = new FileSizeFilter(FileSizeFilter.Type.BIGGER_THEN, 200000);
		addNode(new FileSizeFilterNode(null, filter));
	}

	/**
	 * add new picture taken filter
	 */
	private void addPictureTakenFilter() {
		PictureTakenFilter filter = new PictureTakenFilter(PictureTakenFilter.Type.BEFORE, new DateTime());
		addNode(new PictureTakenFilterNode(null, filter));
	}

	/**
	 * add new rating filter
	 */
	private void addRatingFilter() {
		RatingFilter filter = new RatingFilter(RatingFilter.Type.EXACT, ImageRating.NONE);
		addNode(new RatingFilterNode(null, filter));
	}

	/**
	 * add new text filter node
	 */
	private void addTextFilter() {
		TextFilter filter = new TextFilter("");
		addNode(new TextFilterNode(null, filter));
	}

	/**
	 * add new megapixel filter
	 */
	private void addMegaPixelFilter() {
		MegaPixelFilter filter = new MegaPixelFilter(
		        org.jimcat.model.filter.metadata.MegaPixelFilter.Type.BIGGER_OR_EQUAL, 1.0f);
		addNode(new MegaPixelFilterNode(null, filter));
	}

	/**
	 * add new has tags filter
	 */
	private void addHasTagsFilter() {
		addNode(new HasTagsFilterNode(null));
	}

	private void addIsPartOfAlbumFilter() {
		addNode(new IsPartOfAlbumFilterNode(null));
	}

	/**
	 * add new relative date filter
	 */
	private void addRelativeDateFilter() {
		RelativeDateFilter filter = new RelativeDateFilter(ReferenceDate.DateAdded, TimeUnit.DAYS, 7);
		addNode(new RelativeDateFilterNode(null, filter));
	}

	/**
	 * add new exif metadata filter
	 */
	private void addExifFilter() {
		ExifMetadataFilter filter = new ExifMetadataFilter(ExifMetadataProperty.MANUFACTURER, "");
		addNode(new ExifMetadataFilterNode(null, filter));
	}

	/**
	 * listen to menu items and dispatch events
	 */
	private class EventDispatcher implements ActionListener {
		/**
		 * react on a menu selection
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if (GROUP.equals(command)) {
				addGroup();
			} else if (ALBUM.equals(command)) {
				addAlbumFilter();
			} else if (SMART.equals(command)) {
				addSmartListFilter();
			} else if (TAG.equals(command)) {
				addTagFilter();
			} else if (IMPORT.equals(command)) {
				addImportFilter();
			} else if (DUPLICATE.equals(command)) {
				addDuplicateFilter();
			} else if (IMAGESIZE.equals(command)) {
				addImageSizeFilter();
			} else if (FILESIZE.equals(command)) {
				addFileSizeFilter();
			} else if (PICTURE_TAKEN.equals(command)) {
				addPictureTakenFilter();
			} else if (TEXT.equals(command)) {
				addTextFilter();
			} else if (RATING.equals(command)) {
				addRatingFilter();
			} else if (MEGAPIXEL.equals(command)) {
				addMegaPixelFilter();
			} else if (HAS_TAGS.equals(command)) {
				addHasTagsFilter();
			} else if (IS_PART_OF_ALBUM.equals(command)) {
				addIsPartOfAlbumFilter();
			} else if (RELATIVE_DATE.equals(command)) {
				addRelativeDateFilter();
			} else if (EXIF_METADATA.equals(command)) {
				addExifFilter();
			}
		}
	}

}
