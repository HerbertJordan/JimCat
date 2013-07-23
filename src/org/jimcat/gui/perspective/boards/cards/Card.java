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

package org.jimcat.gui.perspective.boards.cards;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.FileUtils;
import org.jimcat.gui.borders.RoundedShadowBorder;
import org.jimcat.gui.imageviewer.FramedImageViewer;
import org.jimcat.gui.imageviewer.ImageViewer;
import org.jimcat.gui.rating.RatingEditor;
import org.jimcat.model.Album;
import org.jimcat.model.ExifMetadata;
import org.jimcat.model.Image;
import org.jimcat.model.ImageMetadata;
import org.jimcat.model.comparator.AlbumComparator;
import org.jimcat.model.comparator.TagComparator;
import org.jimcat.model.notification.BeanChangeEvent;
import org.jimcat.model.notification.BeanListener;
import org.jimcat.model.tag.Tag;
import org.joda.time.DateTime;

/**
 * A single card within the card view.
 * 
 * $Id$
 * 
 * @author Michael
 */
public class Card extends JPanel implements BeanListener<Image> {

	/**
	 * the background color of a selected card
	 */
	private static final Color COLOR_SELECTED = new Color(255, 140, 0, 100);

	/**
	 * the background color of an unselected card
	 */
	private static final Color COLOR_UNSELECTED = new Color(255, 140, 0, 30);

	/**
	 * the date format used to print dates
	 */
	private static final String DATE_FORMAT = "yyyy/MM/dd";

	/**
	 * the album comparator used to print albums in alphabetical order
	 */
	private static final AlbumComparator ALBUM_COMPARATOR = new AlbumComparator();

	/**
	 * the tag comparator used to print albums in alphabetical order
	 */
	private static final TagComparator TAG_COMPARATOR = new TagComparator();

	/**
	 * the image this card represents
	 */
	private Image image;

	/**
	 * the viewer used to display a image
	 */
	private ImageViewer viewer;

	/**
	 * the orientation of this card (image position)
	 */
	private CardOrientation orientation = CardOrientation.WEST;

	/**
	 * should this item be marked as selected?
	 */
	private boolean selected = false;

	/**
	 * the Font used for descriptions
	 */
	private static Font labelFont = new Font("Tahoma", Font.BOLD, 11);

	/**
	 * the label containing titel
	 */
	private JLabel title = new JLabel();

	/**
	 * used to display rating
	 */
	private RatingEditor rating;

	/**
	 * used to display image dimension
	 */
	private JLabel dimension;

	/**
	 * used to display size
	 */
	private JLabel size;

	/**
	 * used to display path
	 */
	private JLabel path;

	/**
	 * used to display date taken
	 */
	private JLabel taken;

	/**
	 * used to list associated tags
	 */
	private JLabel tags;

	/**
	 * used to list albums containing image
	 */
	private JLabel albums;

	/**
	 * creates a new Card
	 */
	public Card() {
		// build content
		initComponents();
	}

	/**
	 * build up swing componets
	 */
	private void initComponents() {

		setOpaque(false);
		setBorder(new RoundedShadowBorder(15));
		setLayout(new BorderLayout());
		setBackground(new Color(255, 140, 0, 100));

		viewer = new FramedImageViewer();
		viewer.setGraphicSize(new Dimension(140, 140));

		add(viewer, orientation.getBorderLayoutConstant());

		JLabel tmp = null;

		// General info list
		JPanel labelPanel = new JPanel();
		labelPanel.setOpaque(false);
		labelPanel.setBorder(new EmptyBorder(0, 0, 0, 10));
		labelPanel.setLayout(new GridLayout(0, 1));

		JPanel contentPanel = new JPanel();
		contentPanel.setOpaque(false);
		contentPanel.setLayout(new GridLayout(0, 1));

		tmp = new JLabel("Title");
		tmp.setFont(labelFont);
		labelPanel.add(tmp);
		tmp.setOpaque(false);
		contentPanel.add(title);

		tmp = new JLabel("Date Taken");
		tmp.setFont(labelFont);
		labelPanel.add(tmp);
		tmp.setOpaque(false);
		taken = new JLabel();
		contentPanel.add(taken);

		tmp = new JLabel("Dimension");
		tmp.setFont(labelFont);
		tmp.setOpaque(false);
		labelPanel.add(tmp);
		dimension = new JLabel("");
		contentPanel.add(dimension);

		tmp = new JLabel("Size");
		tmp.setFont(labelFont);
		tmp.setOpaque(false);
		labelPanel.add(tmp);
		size = new JLabel("");
		contentPanel.add(size);

		tmp = new JLabel("File path");
		tmp.setFont(labelFont);
		tmp.setOpaque(false);
		labelPanel.add(tmp);
		path = new JLabel("");
		path.setToolTipText("");
		path.addMouseListener(new MouseEventsToCardGrandParentShifter());
		contentPanel.add(path);

		tmp = new JLabel("Rating");
		tmp.setFont(labelFont);
		tmp.setOpaque(false);
		labelPanel.add(tmp);

		rating = new RatingEditor();

		JPanel ratingWrapper = new JPanel(new BorderLayout());
		ratingWrapper.setOpaque(false);
		ratingWrapper.add(rating, BorderLayout.WEST);
		contentPanel.add(ratingWrapper);

		// free line
		tmp = new JLabel();
		tmp.setOpaque(false);
		labelPanel.add(tmp);
		tmp = new JLabel();
		contentPanel.add(tmp);

		// tags
		tmp = new JLabel("Tags");
		tmp.setFont(labelFont);
		tmp.setOpaque(false);
		labelPanel.add(tmp);
		tags = new JLabel("");
		tags.setToolTipText("");
		tags.addMouseListener(new MouseEventsToCardGrandParentShifter());
		contentPanel.add(tags);

		// albums
		tmp = new JLabel("Albums");
		tmp.setFont(labelFont);
		tmp.setOpaque(false);
		labelPanel.add(tmp);
		albums = new JLabel("");
		albums.setToolTipText("");
		albums.addMouseListener(new MouseEventsToCardGrandParentShifter());
		contentPanel.add(albums);

		JPanel info = new JPanel();
		info.setOpaque(false);
		info.setLayout(new BorderLayout());
		info.setBorder(new EmptyBorder(20, 10, 10, 0));

		info.add(labelPanel, BorderLayout.WEST);
		info.add(contentPanel, BorderLayout.CENTER);

		JPanel infoHolder = new JPanel();
		infoHolder.setOpaque(false);
		infoHolder.setLayout(new BorderLayout());
		infoHolder.add(info, BorderLayout.NORTH);

		add(infoHolder, BorderLayout.CENTER);
	}

	/**
	 * 
	 * get the grand parent of a card
	 * 
	 * @return the grandParent Container
	 */
	private Container getCardGrandParent() {
		Container parent = this.getParent();
		if (parent != null) {
			return parent.getParent();
		}
		return null;
	}

	/**
	 * this will resize the shown thumbnail
	 * 
	 * @param size
	 */
	public void setThumbnailSize(Dimension size) {
		// update viewer size and revalidate layout and content
		viewer.setGraphicSize(size);
		revalidate();
	}

	/**
	 * @return the image
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * @param image
	 *            the image to set
	 */
	public void setImage(Image image) {
		if (image != this.image) {
			if (this.image != null) {
				this.image.removeListener(this);
			}
			this.image = image;
			viewer.setImage(image);
			rating.setImage(image);
			if (image != null) {
				image.addListener(this);
				title.setText(image.getTitle());
				updateMetadata(image.getMetadata());
				updateExif(image.getExifMetadata());
				updateTagList();
				updateAlbumList();
			}
		}
	}

	/**
	 * update shown metadata
	 * 
	 * @param metadata
	 */
	private void updateMetadata(ImageMetadata metadata) {
		if (metadata != null) {
			// Dimension
			dimension.setText(metadata.getWidth() + "x" + metadata.getHeight());
			// image size
			size.setText(FileUtils.byteCountToDisplaySize(metadata.getSize()));
			// location
			File location = metadata.getPath();
			String strLocation = "";
			if (location != null) {
				strLocation = location.getParent();
			}
			path.setText(formatPath(location));
			path.setToolTipText(strLocation);
		} else {
			dimension.setText("");
			size.setText("");
			path.setText("");
			path.setToolTipText("");
		}
	}

	/**
	 * update shown tag list
	 */
	private void updateTagList() {
		List<Tag> tagList = new ArrayList<Tag>(image.getTags());
		StringBuffer result = new StringBuffer();
		Collections.sort(tagList, TAG_COMPARATOR);

		for (Tag t : tagList) {
			result.append(t.getName()).append(", ");
		}

		String list = " -none- ";
		if (tagList.size() > 0) {
			list = result.substring(0, result.length() - 2);
		}
		tags.setText(list);
		tags.setToolTipText(list);
	}

	/**
	 * update album list
	 */
	private void updateAlbumList() {
		List<Album> albumList = new ArrayList<Album>(image.getAlbums());
		StringBuffer result = new StringBuffer();
		Collections.sort(albumList, ALBUM_COMPARATOR);

		for (Album a : albumList) {
			result.append(a.getName()).append(", ");
		}

		String list = " -none- ";
		if (albumList.size() > 0) {
			list = result.substring(0, result.length() - 2);
		}
		albums.setText(list);
		albums.setToolTipText(list);
	}

	/**
	 * update shown exif values
	 * 
	 * @param metadata
	 */
	private void updateExif(ExifMetadata metadata) {
		if (metadata == null || metadata.getDateTaken() == null) {
			taken.setText("unknown");
		} else {
			DateTime date = metadata.getDateTaken();
			taken.setText(date.toString(DATE_FORMAT));
		}
	}

	/**
	 * this method will format the path of an image to be displayed
	 * 
	 * @param file
	 * @return the formatted Path
	 */
	private String formatPath(File file) {
		// check for null
		if (file == null) {
			return "";
		}

		// just return the name of the parent folder
		String dir = file.getParentFile().getAbsolutePath();
		if (dir.length() > 15) {
			dir = file.getParentFile().getName();
		}

		// remove filename
		return dir;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected
	 *            the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
		setBackground(selected ? COLOR_SELECTED : COLOR_UNSELECTED);
	}

	/**
	 * update card orientation.
	 * 
	 * @param orientation
	 *            the orientation to set
	 */
	public void setOrientation(CardOrientation orientation) {
		if (this.orientation == orientation) {
			return;
		}
		this.orientation = orientation;

		// update layout
		remove(viewer);
		add(viewer, orientation.getBorderLayoutConstant());
		revalidate();
	}

	/**
	 * handels Images property changes
	 * 
	 * @see org.jimcat.model.notification.BeanListener#beanPropertyChanged(org.jimcat.model.notification.BeanChangeEvent)
	 */
	@SuppressWarnings("unused")
	public void beanPropertyChanged(BeanChangeEvent<Image> event) {
		// get Image
		Image img = event.getSource();

		// update depending on Property
		switch (event.getProperty()) {
		case IMAGE_TITEL: {
			title.setText(img.getTitle());
			break;
		}
		case IMAGE_METADATA: {
			updateMetadata(img.getMetadata());
			break;
		}
		case IMAGE_EXIF_META: {
			updateExif(img.getExifMetadata());
			break;
		}
		case IMAGE_TAGS: {
			updateTagList();
			break;
		}
		case IMAGE_ALBUMS: {
			updateAlbumList();
			break;
		}
			// case rating is handeld by component RatingEditor on its own
		default:
			break;
		}
	}

	/**
	 * 
	 * This class is used to shift mouse events to the grand parent of a card.
	 * 
	 * This is necessary because the grand parent is responsible for reacting to
	 * mouse clicks.
	 * 
	 * 
	 * $Id$
	 * 
	 * @author Michael
	 */
	private class MouseEventsToCardGrandParentShifter extends MouseAdapter {

		/**
		 * on mouse pressed shift the event to grand parent of a card
		 * 
		 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			if (e != null) {
				shiftEvent(e);
			}
		}

		/**
		 * on mouse released shift the event to grand parent of a card
		 * 
		 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseReleased(MouseEvent e) {
			if (e != null) {
				shiftEvent(e);
			}
		}

		/**
		 * on mouse clicked shift the event to grand parent of a card
		 * 
		 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e != null) {
				shiftEvent(e);
			}
		}

		private void shiftEvent(MouseEvent e) {
			Container target = getCardGrandParent();
			if (target == null) {
				return;
			}
			// construct a new mouse event - needed to set positions in a
			// correct way
			MouseEvent event;
			if (target.getMousePosition() != null) {
				event = new MouseEvent((Component) e.getSource(), e.getID(), e.getWhen(), e.getModifiers(), target
				        .getMousePosition().x, target.getMousePosition().y, e.getClickCount(), e.isPopupTrigger(), e
				        .getButton());
			} else {
				event = new MouseEvent((Component) e.getSource(), e.getID(), e.getWhen(), e.getModifiers(), e.getX(), e
				        .getY(), e.getClickCount(), e.isPopupTrigger(), e.getButton());
			}
			// dispatch event in grand parent
			target.dispatchEvent(event);
		}
	}

}
