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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashSet;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.commons.io.FileUtils;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jimcat.gui.SwingClient;
import org.jimcat.gui.imageviewer.ImageViewer;
import org.jimcat.gui.rating.RatingEditor;
import org.jimcat.gui.tagtree.TagPanelPopupHandler;
import org.jimcat.gui.tagtree.TagTree;
import org.jimcat.gui.tagtree.TagTreeListener;
import org.jimcat.model.Image;
import org.jimcat.model.ImageMetadata;
import org.jimcat.model.Thumbnail;
import org.jimcat.model.notification.BeanChangeEvent;
import org.jimcat.model.notification.BeanListener;
import org.jimcat.model.tag.Tag;
import org.jvnet.substance.SubstanceLookAndFeel;

/**
 * Represents the SideBar of the Detail Perspective.
 * 
 * $Id: DetailSideBar.java 935 2007-06-15 09:21:09Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public class DetailSideBar extends JPanel implements BeanListener<Image>, ListSelectionListener, TagTreeListener {

	/**
	 * dimension of the preview image
	 */
	private static final Dimension PREVIEW_DIMENSION = new Dimension(Thumbnail.MAX_THUMBNAIL_SIZE, Thumbnail.MAX_THUMBNAIL_SIZE);

	/**
	 * the Font used for descriptions
	 */
	private static Font labelFont = new Font("Tahoma", Font.BOLD, 11);

	/**
	 * image to be displayed
	 */
	private ImageViewer image;

	/**
	 * the label containing titel
	 */
	private JLabel title;

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
	 * the tagtree to modify image tags
	 */
	private TagTree tagTree;

	/**
	 * the component showing associated tags
	 */
	private DetailTagList detailTagList;

	/**
	 * the component showing exif infos
	 */
	private ExifList exifList;

	/**
	 * the image currently displayed
	 */
	private Image currentImage;

	/**
	 * the table next to this detail panel
	 */
	private JXTable table;

	/**
	 * the index of the last selected element within the table
	 */
	private int lastSelected = 0;

	/**
	 * is the selection up to date
	 */
	private boolean dirty = true;

	/**
	 * only constructor
	 * @param table 
	 */
	public DetailSideBar(JXTable table) {
		this.table = table;
		initComponents();

		// register as a listener
		table.getModel().addTableModelListener(new ModelListener());
		table.getSelectionModel().addListSelectionListener(this);
	}

	/**
	 * build up Component hierarchy
	 */
	private void initComponents() {
		
		title = new JLabel();
		
		// general settings
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(0, 15, 0, 15));

		// add preview
		add(createPreviewPanel(), BorderLayout.NORTH);

		// add details
		add(createImageDetail(), BorderLayout.CENTER);

		// load first image
		updateImage();
	}

	/**
	 * creates the preview head
	 * 
	 * @return the JPanel for preview purposes
	 */
	private JPanel createPreviewPanel() {
		// preview panel
		JPanel preview = new JPanel();
		BorderLayout previewLayout = new BorderLayout();
		previewLayout.setHgap(5);
		previewLayout.setVgap(5);
		preview.setLayout(new BorderLayout());
		preview.setOpaque(true);

		image = new ImageViewer();
		image.setMinimumSize(PREVIEW_DIMENSION);
		image.setPreferredSize(PREVIEW_DIMENSION);
		image.setMaximumSize(PREVIEW_DIMENSION);
		image.setBorder(new EmptyBorder(5, 5, 5, 5));
		image.setOpaque(false);
		image.addMouseListener(new PreviewImageDoubleClickListener());
		preview.add(image, BorderLayout.CENTER);

		return preview;
	}

	/**
	 * creates image Detail - Section
	 * 
	 * @return the Component used to show details
	 */
	private JComponent createImageDetail() {
		JLabel tmp = null;

		// Task Panel
		JXTaskPaneContainer container = new JXTaskPaneContainer();
		container.setOpaque(false);

		// General Infos
		JXTaskPane general = new JXTaskPane();
		general.setOpaque(true);
		general.setTitle("General Info");
		general.setExpanded(true);

		// General info list
		JPanel info = new JPanel();
		GridLayout infoLayout = new GridLayout(0, 2);
		infoLayout.setHgap(5);
		info.setLayout(infoLayout);
		info.setOpaque(false);


		tmp = new JLabel("Title");
		tmp.setFont(labelFont);
		info.add(tmp);
		info.add(title);

		tmp = new JLabel("Rating");
		tmp.setFont(labelFont);
		info.add(tmp);
		rating = new RatingEditor();
		info.add(rating);

		tmp = new JLabel("Dimension");
		tmp.setFont(labelFont);
		info.add(tmp);
		dimension = new JLabel("");
		info.add(dimension);

		tmp = new JLabel("Size");
		tmp.setFont(labelFont);
		info.add(tmp);
		size = new JLabel("");
		info.add(size);

		tmp = new JLabel("Location");
		tmp.setFont(labelFont);
		info.add(tmp);
		path = new JLabel("");
		path.setToolTipText("");
		info.add(path);

		general.add(info);
		container.add(general);

		// Exif data
		JXTaskPane exifs = new JXTaskPane();
		exifs.setOpaque(true);
		exifs.setTitle("Exif Infos");
		exifs.setExpanded(true);
		exifList = new ExifList();
		exifs.add(exifList);
		container.add(exifs);

		// Tags
		JXTaskPane tags = new JXTaskPane();
		tags.setOpaque(true);
		tags.setTitle("Associated Tags");
		tags.setExpanded(true);

		// associated tags-list
		detailTagList = new DetailTagList();
		tags.add(detailTagList);
		container.add(tags);

		// Modify Tags
		JXTaskPane filter = new JXTaskPane();
		filter.setTitle("Modify Tags");
		filter.setExpanded(false);
		filter.setOpaque(true);

		tagTree = new TagTree();
		tagTree.addTagTreeListener(this);

		filter.setLayout(new BorderLayout());
		filter.addMouseListener(new TagPanelPopupHandler(filter));
		filter.add(tagTree, BorderLayout.CENTER);
		container.add(filter);

		JScrollPane pane = new JScrollPane();
		pane.setOpaque(true);
		pane.setBorder(null);
		pane.setViewportView(container);
		pane.putClientProperty(SubstanceLookAndFeel.WATERMARK_TO_BLEED, Boolean.TRUE);
		return pane;
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
			exifList.setExifData(img.getExifMetadata());
			break;
		}
		case IMAGE_TAGS: {
			tagTree.selectTags(img.getTags());
			break;
		}
			// case thumbnail is handeld by component ImageViewer
			// case rating is handeld by component RatingEditor on its own
		default:
			break;
		}
	}

	/**
	 * reacts on table-selection changes
	 * 
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			// change current image
			ListSelectionModel model = (ListSelectionModel) e.getSource();
			lastSelected = Math.max(model.getMinSelectionIndex(), 0);
			dirty = true;
			updateImage();
		}
	}

	/**
	 * exchanges the Image with the last selected image
	 */
	private void updateImage() {
		if (dirty) {
			// get Image for index
			DetailTableModel dataModel = (DetailTableModel) table.getModel();
			// update
			setCurrentImage(dataModel.getImageAtRow(lastSelected));
			// reset dirty
			dirty = false;
		}
	}

	/**
	 * replaces the current image
	 * 
	 * @param img
	 */
	private void setCurrentImage(Image img) {
		// only if there is a change
		if (img == currentImage) {
			return;
		}

		// unregister
		if (currentImage != null) {
			currentImage.removeListener(this);
		}

		// remove data if img == null
		if (img == null) {
			currentImage = null;
			title.setText("");
			image.setImage(null);
			rating.setImage(null);
			dimension.setText("");
			size.setText("");
			path.setText("");
			path.setToolTipText("");
			exifList.setExifData(null);
			detailTagList.setImage(null);
			tagTree.selectTags(new HashSet<Tag>());
			return;
		}

		// exchange
		currentImage = img;
		// register
		currentImage.addListener(this);

		// update
		title.setText(img.getTitle());
		image.setImage(img);

		// inform rating editor
		rating.setImage(img);

		// update metadata
		updateMetadata(img.getMetadata());

		// update exif infos
		exifList.setExifData(img.getExifMetadata());

		// update taglists
		detailTagList.setImage(img);
		tagTree.selectTags(img.getTags());

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
	 * this methode will format the path of an image to be displayed
	 * 
	 * @param file
	 * @return the path formated as String in a readable representation.
	 */
	private String formatPath(File file) {
		// check for null
		if (file == null) {
			return "";
		}

		// just return the name of the parent folder
		String dir = file.getParentFile().getName();

		// if it is still to long, cut it of
		if (dir.length() > 15) {
			dir = dir.substring(0, 12) + "...";
		}

		// remove filename
		return dir;
	}

	/**
	 * to fix width
	 * 
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		Dimension dim = super.getPreferredSize();
		// the Thumbnail dimension + some space
		dim.width = Thumbnail.MAX_THUMBNAIL_SIZE + 20;
		return dim;
	}

	// //////////////////////
	// TAG TREE INTERFACE
	// //////////////////////

	/**
	 * Add a new Tag to an image
	 * 
	 * @see org.jimcat.gui.tagtree.TagTreeListener#tagSelected(org.jimcat.model.tag.Tag)
	 */
	public void tagSelected(Tag tag) {
		if (currentImage != null) {
			currentImage.addTag(tag);
		}
	}

	/**
	 * Remove a Tag from an image
	 * 
	 * @see org.jimcat.gui.tagtree.TagTreeListener#tagUnSelected(org.jimcat.model.tag.Tag)
	 */
	public void tagUnSelected(Tag tag) {
		if (currentImage != null) {
			currentImage.removeTag(tag);
		}
	}

	/**
	 * 
	 * The PreviewImageDobleClickListener is used to open the fullscreen on
	 * double click on the observed component.
	 * 
	 * 
	 * $Id: DetailSideBar.java 935 2007-06-15 09:21:09Z 07g1t1u2 $
	 * 
	 * @author Michael
	 */
	private class PreviewImageDoubleClickListener extends MouseAdapter {

		/**
		 * Open fullscreen on left double click event
		 * 
		 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
			// if it was a left double click open fullscreen perspective
			if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
				SwingClient.getInstance().showFullScreen();
			}
		}

	}
	
	/**
	 * used to be informed about table changes
	 */
	private class ModelListener implements TableModelListener {
		/**
		 * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
		 */
		@SuppressWarnings("unused")
		public void tableChanged(TableModelEvent e) {
			ListSelectionModel model = table.getSelectionModel();
			lastSelected = Math.max(model.getMinSelectionIndex(), 0);
			dirty = true;
			updateImage();
		}
	}

}
