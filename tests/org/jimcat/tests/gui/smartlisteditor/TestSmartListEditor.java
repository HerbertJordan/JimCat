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

package org.jimcat.tests.gui.smartlisteditor;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.jimcat.gui.smartlisteditor.SmartListEditor;
import org.jimcat.model.Album;
import org.jimcat.model.SmartList;
import org.jimcat.model.filter.AlbumFilter;
import org.jimcat.model.filter.DuplicateFilter;
import org.jimcat.model.filter.Filter;
import org.jimcat.model.filter.HasTagsFilter;
import org.jimcat.model.filter.ImportFilter;
import org.jimcat.model.filter.IsPartOfAlbumFilter;
import org.jimcat.model.filter.SmartListFilter;
import org.jimcat.model.filter.TagFilter;
import org.jimcat.model.filter.ImportFilter.Type;
import org.jimcat.model.filter.logical.AndFilter;
import org.jimcat.model.filter.logical.NotFilter;
import org.jimcat.model.filter.logical.OrFilter;
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
import org.jimcat.model.tag.Tag;
import org.jimcat.persistence.RepositoryLocator;
import org.jimcat.persistence.RepositoryLocator.ConfigType;
import org.joda.time.DateTime;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.OfficeSilver2007Skin;

/**
 * A small test case to run smartlisteditor isolated
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class TestSmartListEditor {
	/**
	 * for developing / testing
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// set dependencies
		RepositoryLocator.setConfigType(ConfigType.XSTREAM);

		try {
			UIManager.setLookAndFeel(new SubstanceLookAndFeel());
			SubstanceLookAndFeel.setSkin(new OfficeSilver2007Skin());
			// comment this out if you like to choose themes
			UIManager.put(SubstanceLookAndFeel.NO_EXTRA_ELEMENTS, Boolean.TRUE);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		SmartList list = new SmartList();
		list.setName("SmartListEditorTestList");

		Tag tag1 = new Tag();
		tag1.setName("test1");
		Tag tag2 = new Tag();
		tag2.setName("test2");

		Filter orFilter = new OrFilter(new NotFilter(new DuplicateFilter()), new ImportFilter(Type.AT_LEAST, 20));

		// OR + constant true
		Filter filter = new AndFilter(orFilter, null);

		filter = new AndFilter(filter, new NotFilter(orFilter));

		// constant false
		filter = new AndFilter(filter, new NotFilter(null));

		// Duplicatefilter
		filter = new AndFilter(filter, new DuplicateFilter());

		// no duplicatefilter
		filter = new AndFilter(filter, new NotFilter(new DuplicateFilter()));

		// importiflter
		filter = new AndFilter(filter, new ImportFilter(Type.UP_TO, 30));

		// tagfilter
		filter = new AndFilter(filter, new TagFilter(tag1));

		// no tag filter
		filter = new AndFilter(filter, new NotFilter(new TagFilter(tag2)));

		// max bytesize
		filter = new AndFilter(filter, new FileSizeFilter(FileSizeFilter.Type.BIGGER_THEN, 200000));

		// min bytesize
		filter = new AndFilter(filter, new FileSizeFilter(FileSizeFilter.Type.SMALLER_THEN, 200000));

		filter = new AndFilter(filter, new NotFilter(new FileSizeFilter(FileSizeFilter.Type.SMALLER_THEN, 200000)));

		// max image size
		filter = new AndFilter(filter, new ImageSizeFilter(ImageSizeFilter.Type.SMALLER_THAN, 800, 600));

		// min image size
		filter = new AndFilter(filter, new ImageSizeFilter(ImageSizeFilter.Type.BIGGER_THAN, 800, 600));

		// min image width
		filter = new AndFilter(filter, new ImageSizeFilter(ImageSizeFilter.Type.WIDER_THAN, 800, 600));

		// min image heigh
		filter = new AndFilter(filter, new ImageSizeFilter(ImageSizeFilter.Type.THINER_THAN, 800, 600));

		// taken after
		filter = new AndFilter(filter, new PictureTakenFilter(PictureTakenFilter.Type.AFTER, new DateTime()));

		// taken before
		filter = new AndFilter(filter, new PictureTakenFilter(PictureTakenFilter.Type.BEFORE, new DateTime()));

		// text filter
		filter = new AndFilter(filter, new TextFilter("test"));

		// smartlist filter
		filter = new AndFilter(filter, new SmartListFilter(list));

		// smartlist filter
		Album album = new Album();
		album.setName("SmartListEditorTestAlbum");
		filter = new AndFilter(filter, new AlbumFilter(album));

		// MegaPixel filter
		filter = new AndFilter(filter, new MegaPixelFilter(org.jimcat.model.filter.metadata.MegaPixelFilter.Type.BIGGER_OR_EQUAL, 2.0f));

		// hastags filter
		filter = new AndFilter(filter, new HasTagsFilter());	
		
		// is part of any albums filter
		filter = new AndFilter(filter, new IsPartOfAlbumFilter());
		
		// relative date filter
		filter = new AndFilter(filter, new RelativeDateFilter(ReferenceDate.DateAdded,TimeUnit.DAYS,7));
		
		// exif data filter
		filter = new AndFilter(filter, new ExifMetadataFilter(ExifMetadataProperty.MODEL, "canon"));
		list.setFilter(filter);

		SmartListEditor edit = new SmartListEditor(null);
		edit.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		edit.addWindowListener(new WindowAdapter() {
			/**
			 * close window
			 * 
			 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
			 */
			@Override
			@SuppressWarnings("unused")
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		edit.setSmartList(list);
		edit.setVisible(true);
	}
}
