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

import java.util.Comparator;

import org.jimcat.model.Image;
import org.jimcat.model.ImageRating;
import org.jimcat.model.comparator.ModificationDateComparator;
import org.jimcat.model.comparator.DateAddedComparator;
import org.jimcat.model.comparator.DescriptionComparator;
import org.jimcat.model.comparator.FileSizeComparator;
import org.jimcat.model.comparator.HeightComparator;
import org.jimcat.model.comparator.ImportIdComparator;
import org.jimcat.model.comparator.LastExportPathComparator;
import org.jimcat.model.comparator.NullComparator;
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
import org.jimcat.model.libraries.LibraryView;
import org.jimcat.model.notification.BeanProperty;
import org.joda.time.DateTime;

/**
 * An enumeration of detail Table columnes
 * 
 * $Id: DetailTableColumn.java 935 2007-06-15 09:21:09Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public enum DetailTableColumn {
	COUNT("#", Integer.class, false, null, new NullComparator()) {
		@Override
		public Object getValue(LibraryView view, Image img) {
			return new Integer(view.indexOf(img) + 1);
		}
	},
	TITEL("Image Name", String.class, true, BeanProperty.IMAGE_TITEL, new TitleComparator()) {
		@Override
		@SuppressWarnings("unused")
		public Object getValue(LibraryView view, Image img) {
			return img.getTitle();
		}

		@Override
		public void setValue(Image img, Object value) {
			img.setTitle((String) value);
		}
	},
	RATING("Rating", ImageRating.class, true, BeanProperty.IMAGE_RATING, new RatingComparator()) {
		@Override
		@SuppressWarnings("unused")
		public Object getValue(LibraryView view, Image img) {
			return img.getRating();
		}
	},
	DESCRIPTION("Description", String.class, true, BeanProperty.IMAGE_DESCRIPTION, new DescriptionComparator()) {
		@Override
		@SuppressWarnings("unused")
		public Object getValue(LibraryView view, Image img) {
			return img.getDescription();
		}

		@Override
		public void setValue(Image img, Object value) {
			img.setDescription((String) value);
		}
	},
	MODIFICATION_DATE("Modification Date", DateTime.class, false, BeanProperty.IMAGE_METADATA, new ModificationDateComparator()) {
		@Override
		@SuppressWarnings("unused")
		public Object getValue(LibraryView view, Image img) {
			return img.getMetadata().getModificationDate();
		}
	},
	DATE_ADDED("Date Added", DateTime.class, false, BeanProperty.IMAGE_METADATA, new DateAddedComparator()) {
		@Override
		@SuppressWarnings("unused")
		public Object getValue(LibraryView view, Image img) {
			return img.getMetadata().getDateAdded();
		}
	},
	PATH("Path", String.class, false, BeanProperty.IMAGE_METADATA, new PathComparator()) {
		@Override
		@SuppressWarnings("unused")
		public Object getValue(LibraryView view, Image img) {
			return img.getMetadata().getPath();
		}
	},
	LAST_EXPORT_PATH("Last Export Path", String.class, false, BeanProperty.IMAGE_LAST_EXPORT_PATH,
	        new LastExportPathComparator()) {
		@Override
		@SuppressWarnings("unused")
		public Object getValue(LibraryView view, Image img) {
			return img.getLastExportPath();
		}
	},
	IMPORT_ID("Import Id", Long.class, false, BeanProperty.IMAGE_METADATA, new ImportIdComparator()) {
		@Override
		@SuppressWarnings("unused")
		public Object getValue(LibraryView view, Image img) {
			return new Long(img.getMetadata().getImportId());
		}
	},
	FILE_SIZE("Filesize", Long.class, false, BeanProperty.IMAGE_METADATA, new FileSizeComparator()) {
		@Override
		@SuppressWarnings("unused")
		public Object getValue(LibraryView view, Image img) {
			return new Long(img.getMetadata().getSize());
		}
	},
	IMAGE_HEIGHT("Height", Integer.class, false, BeanProperty.IMAGE_METADATA, new HeightComparator()) {
		@Override
		@SuppressWarnings("unused")
		public Object getValue(LibraryView view, Image img) {
			return new Integer(img.getMetadata().getHeight());
		}
	},
	IMAGE_WIDTH("Width", Integer.class, false, BeanProperty.IMAGE_METADATA, new WidthComparator()) {
		@Override
		@SuppressWarnings("unused")
		public Object getValue(LibraryView view, Image img) {
			return new Integer(img.getMetadata().getWidth());
		}
	},
	EXIF_APERTURE("Aperture", String.class, false, BeanProperty.IMAGE_EXIF_META, new ExifApertureComparator()) {
		@Override
		@SuppressWarnings("unused")
		public Object getValue(LibraryView view, Image img) {
			if (img.getExifMetadata() != null)
				return img.getExifMetadata().getAperture();
			return null;
		}
	},
	EXIF_DATE_TAKEN("Date Taken", DateTime.class, false, BeanProperty.IMAGE_EXIF_META, new ExifDateTakenComparator()) {
		@Override
		@SuppressWarnings("unused")
		public Object getValue(LibraryView view, Image img) {
			if (img.getExifMetadata() != null)
				return img.getExifMetadata().getDateTaken();
			return null;
		}
	},
	EXIF_EXPOSURE("Exposure", String.class, false, BeanProperty.IMAGE_EXIF_META, new ExifExposureComparator()) {
		@Override
		@SuppressWarnings("unused")
		public Object getValue(LibraryView view, Image img) {
			if (img.getExifMetadata() != null)
				return img.getExifMetadata().getExposure();
			return null;
		}
	},
	EXIF_FLASH("Flash", String.class, false, BeanProperty.IMAGE_EXIF_META, new ExifFlashComparator()) {
		@Override
		@SuppressWarnings("unused")
		public Object getValue(LibraryView view, Image img) {
			if (img.getExifMetadata() != null)
				return img.getExifMetadata().getFlash();
			return null;
		}
	},
	EXIF_FOCAL("Focal Length", String.class, false, BeanProperty.IMAGE_EXIF_META, new ExifFocalComparator()) {
		@Override
		@SuppressWarnings("unused")
		public Object getValue(LibraryView view, Image img) {
			if (img.getExifMetadata() != null)
				return img.getExifMetadata().getFocal();
			return null;
		}
	},
	EXIF_ISO("ISO", String.class, false, BeanProperty.IMAGE_EXIF_META, new ExifIsoComparator()) {
		@Override
		@SuppressWarnings("unused")
		public Object getValue(LibraryView view, Image img) {
			if (img.getExifMetadata() != null)
				return img.getExifMetadata().getIso();
			return null;
		}
	},
	EXIF_MANUFACTURER("Manufacturer", String.class, false, BeanProperty.IMAGE_EXIF_META, new ExifManufacturerComparator()) {
		@Override
		@SuppressWarnings("unused")
		public Object getValue(LibraryView view, Image img) {
			if (img.getExifMetadata() != null)
				return img.getExifMetadata().getManufacturer();
			return null;
		}
	},
	EXIF_MODEL("Model", String.class, false, BeanProperty.IMAGE_EXIF_META, new ExifModelComparator()) {
		@Override
		@SuppressWarnings("unused")
		public Object getValue(LibraryView view, Image img) {
			if (img.getExifMetadata() != null)
				return img.getExifMetadata().getModel();
			return null;
		}
	};

	/**
	 * the column name
	 */
	private String name;

	/**
	 * typ of element
	 */
	private Class typ;

	/**
	 * is this colum editable
	 */
	private boolean editable;

	/**
	 * the associated bean property of an Image if there is no according
	 * property or it cant change its value this field should be null
	 */
	private BeanProperty property;

	/**
	 * a sorter for this column
	 */
	private Comparator<Image> sorter;

	/**
	 * direct constructor
	 * 
	 * @param name
	 * @param typ
	 * @param editable
	 * @param property -
	 *            the bean property belonging to this column
	 * @param sorter -
	 *            a comparator for this column
	 */
	private DetailTableColumn(String name, Class typ, boolean editable, BeanProperty property, Comparator<Image> sorter) {
		this.name = name;
		this.typ = typ;
		this.editable = editable;
		this.property = property;
		this.sorter = sorter;
	}

	/**
	 * extracts the value for this column out of an image
	 * @param view 
	 * 
	 * @param img
	 * @return the value of this column
	 */
	public abstract Object getValue(LibraryView view, Image img);

	/**
	 * stores the new value to the appropriate field
	 * 
	 * @param img
	 * @param value 
	 */
	@SuppressWarnings("unused")
	public void setValue(Image img, Object value) {
		// default not editable column
		return;
	}

	/**
	 * @return the editable
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the typ
	 */
	public Class getTyp() {
		return typ;
	}

	/**
	 * @return a sorter for this column
	 */
	public Comparator<Image> getSorter() {
		return sorter;
	}

	/**
	 * return the table representing the according property or null
	 * 
	 * @param property -
	 *            a Bean Property
	 * @return a DetailTableColumn or null if there is no
	 */
	public static DetailTableColumn getColumnForProperty(BeanProperty property) {
		if (property == null) {
			return null;
		}
		for (DetailTableColumn column : values()) {
			if (property == column.property) {
				return column;
			}
		}
		return null;
	}
}
