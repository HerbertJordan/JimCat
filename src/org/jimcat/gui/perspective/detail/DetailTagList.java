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
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jimcat.gui.SwingClient;
import org.jimcat.gui.TagControl;
import org.jimcat.gui.icons.Icons;
import org.jimcat.model.Image;
import org.jimcat.model.notification.BeanChangeEvent;
import org.jimcat.model.notification.BeanListener;
import org.jimcat.model.notification.BeanProperty;
import org.jimcat.model.tag.Tag;
import org.jimcat.model.tag.TagGroup;

/**
 * This component forms the associated Tagslist within the DetailPerspective
 * Side Bar.
 * 
 * $Id: DetailTagList.java 941 2007-06-16 08:55:06Z 07g1t1u3 $
 * 
 * @author Herbert
 */
public class DetailTagList extends JPanel {

	/**
	 * prefix used to format tree
	 */
	private static final String PREFIX = "    ";

	/**
	 * the Font used for taggroups
	 */
	private static Font TAG_GROUP_FONT = new Font("Tahoma", Font.BOLD, 11);

	/**
	 * the font used for tags
	 */
	private static Font TAG_FONT = new JLabel().getFont();

	/**
	 * the root of the TagTree
	 */
	private TagGroup tagroot;

	/**
	 * a list of useable labels, forming a label cache
	 */
	private List<TagLabel> labels = new LinkedList<TagLabel>();

	/**
	 * this pointer helps reusing labels it pointes to the next useable label
	 * within the lables - list
	 */
	private int labelCachePointer = 0;

	/**
	 * a map between the tag and the label, also used as generell register
	 */
	private Map<TagGroup, TagListEntry> tag2label = new HashMap<TagGroup, TagListEntry>();

	/**
	 * the image this list is based on
	 */
	private Image image;

	/**
	 * a listener to react on image changes
	 */
	private BeanListener<Image> myImageListener;

	/**
	 * a listener to react on tagchanges
	 */
	private BeanListener<TagGroup> myTagListener;

	/**
	 * small constructor
	 */
	public DetailTagList() {
		// initate members
		TagControl control = SwingClient.getInstance().getTagControl();
		tagroot = control.getTagTreeRoot();

		myImageListener = new ImageListener();
		myTagListener = new TagGroupListener();

		// init components
		setLayout(new BorderLayout());
		setOpaque(false);
	}

	/**
	 * to exchange image this list is based on
	 * 
	 * @param img
	 */
	public void setImage(Image img) {
		// if there is no change, do nothing
		if (img == image) {
			return;
		}
		// unregister from old
		if (image != null) {
			image.removeListener(myImageListener);
		}
		// exchange
		image = img;
		// register to new one
		if (image != null) {
			image.addListener(myImageListener);
		}
		// update list
		updateList();
	}

	/**
	 * this will flush the list an recreate a new one
	 */
	private void updateList() {
		// remove TagGroupListener from old
		for (TagGroup tag : tag2label.keySet()) {
			tag.removeListener(myTagListener);
		}

		// clear current config
		tag2label.clear();
		clearLabelCache();

		// build up new list
		if (image != null) {
			generateSubTree(tagroot, image.getTags(), "", 0);
		}

		// generate new Content from generated list
		JPanel content = new JPanel();
		content.setLayout(new GridLayout(0, 1));
		content.setOpaque(false);

		// create content from current label cache
		for (int i = 0; i < labelCachePointer; i++) {
			content.add(labels.get(i));
		}

		// display
		removeAll();
		add(content, BorderLayout.CENTER);

		// revalidate
		revalidate();
	}

	/**
	 * helps building up the labeles recursivelly
	 * 
	 * @param group
	 * @param selected
	 * @param prefix
	 * @param level
	 */
	private void generateSubTree(TagGroup group, Set<Tag> selected, String prefix, int level) {

		if (group == null) {
			return;
		}
		// added tags to list
		for (TagGroup tag : group.getSubTags()) {
			if (tag.containsAnyOf(selected)) {
				// hit
				// register listener
				tag.addListener(myTagListener);

				// retrieve label
				TagLabel label = getNextLabel();
				label.setTag(tag, prefix);

				// add to tag2label map
				TagListEntry entry = new TagListEntry();
				entry.label = label;
				entry.level = level;
				tag2label.put(tag, entry);

				// recursive step
				generateSubTree(tag, selected, prefix + PREFIX, level + 1);

				// spaces between main level groups
				if (level == 0) {
					TagLabel space = getNextLabel();
					space.setTag(null, "");
				}
			}
		}

		// remove last SPACER
		if (level == 0) {
			removeLastFromCache();
		}
	}

	/**
	 * this will return a free JLabel free to use
	 * 
	 * @return the next free Label to use
	 */
	private TagLabel getNextLabel() {
		// if there are not enought labels, create a new one
		if (labelCachePointer >= labels.size()) {
			TagLabel newOne = new TagLabel();
			newOne.setOpaque(false);
			labels.add(newOne);
		}
		return labels.get(labelCachePointer++);
	}

	/**
	 * this will reset the current Label Cache and free all binded labels
	 */
	private void clearLabelCache() {
		// just reset cache pointer
		labelCachePointer = 0;
	}

	/**
	 * this will remove the last element from the cache
	 */
	private void removeLastFromCache() {
		labelCachePointer--;
	}

	/**
	 * a list entry
	 */
	private class TagListEntry {
		TagLabel label;

		int level;
	}

	/**
	 * a small listener for TagGroup Property changes
	 * 
	 * $Id: DetailTagList.java 941 2007-06-16 08:55:06Z 07g1t1u3 $
	 * 
	 * @author Herbert
	 */
	private class TagGroupListener implements BeanListener<TagGroup> {

		/**
		 * update labels if those are shown
		 * 
		 * @see org.jimcat.model.notification.BeanListener#beanPropertyChanged(org.jimcat.model.notification.BeanChangeEvent)
		 */
		public void beanPropertyChanged(BeanChangeEvent<TagGroup> event) {
			// only listen to Image_Tags
			if (event.getProperty() != BeanProperty.TAG_NAME) {
				return;
			}

			// update label
			TagGroup tag = event.getSource();

			// get Label
			TagListEntry entry = tag2label.get(tag);
			if (entry != null) {
				// create new text for label
				StringBuffer prefix = new StringBuffer("");
				for (int i = 0; i < entry.level; i++) {
					prefix.append(PREFIX);
				}

				// set text
				TagLabel label = entry.label;
				label.setTag(tag, prefix.toString());
			}
		}
	}

	/**
	 * A small listener to handle Image changes
	 * 
	 * $Id: DetailTagList.java 941 2007-06-16 08:55:06Z 07g1t1u3 $
	 * 
	 * @author Herbert
	 */
	private class ImageListener implements BeanListener<Image> {

		/**
		 * to react on Tagchanges of an image
		 * 
		 * @see org.jimcat.model.notification.BeanListener#beanPropertyChanged(org.jimcat.model.notification.BeanChangeEvent)
		 */
		public void beanPropertyChanged(BeanChangeEvent<Image> event) {
			// check if ths event is usefull
			if (event.getSource() != image) {
				return;
			}
			if (event.getProperty() != BeanProperty.IMAGE_TAGS) {
				return;
			}

			// we have to update the tag list
			updateList();
		}
	}

	/**
	 * the component forming one label
	 */
	private class TagLabel extends JPanel {

		/**
		 * the remove button
		 */
		private JLabel removeTag;

		/**
		 * the label to show name
		 */
		private JLabel label;

		/**
		 * the tag remove listener
		 */
		private RemoveTagListener listener;

		/**
		 * create a new TagLabel representing the given taggroup
		 * 
		 */
		public TagLabel() {
			this.listener = new RemoveTagListener(null);
			initComponents();
		}

		/**
		 * build up component
		 */
		private void initComponents() {
			setLayout(new BorderLayout());
			setOpaque(false);

			removeTag = new JLabel();
			removeTag.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
			removeTag.setToolTipText(null);
			removeTag.setFocusable(false);
			removeTag.addMouseListener(listener);
			add(removeTag, BorderLayout.EAST);

			label = new JLabel();
			label.setOpaque(false);
			add(label, BorderLayout.CENTER);
		}

		/**
		 * exchange shown tag
		 * 
		 * @param tag
		 * @param prefix
		 */
		public void setTag(TagGroup tag, String prefix) {
			if (tag == null) {
				removeTag.setToolTipText("");
				removeTag.setIcon(null);
				label.setText("");
			} else {
				removeTag.setToolTipText("Remove Tag " + tag.getName());
				removeTag.setIcon(Icons.TAG_REMOVE_ASSOCIATED);

				label.setText(prefix + tag.getName());
				if (!(tag instanceof Tag)) {
					label.setFont(TAG_GROUP_FONT);
				} else {
					label.setFont(TAG_FONT);
				}

				listener.setTag(tag);
			}
		}

	}

	/**
	 * a listener capable to delete a given tag
	 */
	private class RemoveTagListener extends MouseAdapter {

		/**
		 * the tag targeted
		 */
		private TagGroup tag;

		/**
		 * create a new Listener which will delete given tag
		 * 
		 * @param tag
		 */
		public RemoveTagListener(TagGroup tag) {
			this.tag = tag;
		}

		/**
		 * initate tag deletion on click
		 * 
		 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
		 */
		@Override
		@SuppressWarnings("unused")
		public void mouseReleased(MouseEvent e) {
			if (e.getButton() != MouseEvent.BUTTON1) {
				return;
			}

			JLabel source = (JLabel) e.getSource();
			int width = source.getWidth();
			int height = source.getHeight();

			int x = e.getX();
			int y = e.getY();

			if (x < 0 || x > width || y < 0 || y > height) {
				// released outside => abort
				return;
			}

			SwingClient client = SwingClient.getInstance();
			String msg;
			String name = tag.getName();

			if (tag instanceof Tag) {
				msg = "Should the tag \"" + name + "\" be removed from this image?";
			} else {
				msg = "Should the category \"" + name + "\" and all subtags be removed from this image?";
			}

			int confirm = client.showConfirmDialog(msg, "Confirm Remove", 0, JOptionPane.QUESTION_MESSAGE);

			if (confirm == JOptionPane.OK_OPTION) {
				// remove tags
				removeTag(tag, image);
			}
		}

		/**
		 * recursive methode to remove compleat tag groups
		 * 
		 * @param group
		 * @param img
		 */
		private void removeTag(TagGroup group, Image img) {
			if (group instanceof Tag) {
				img.removeTag((Tag) group);
			} else {
				for (TagGroup grp : group.getSubTags()) {
					removeTag(grp, img);
				}
			}
		}

		/**
		 * exchange target tag
		 * 
		 * @param tag
		 */
		public void setTag(TagGroup tag) {
			this.tag = tag;
		}
	}
}
