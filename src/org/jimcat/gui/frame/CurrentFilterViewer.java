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

package org.jimcat.gui.frame;

import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.jimcat.gui.SwingClient;
import org.jimcat.gui.TagCombineMode;
import org.jimcat.gui.ViewControl;
import org.jimcat.gui.ViewFilterListener;
import org.jimcat.model.filter.AlbumFilter;
import org.jimcat.model.filter.TagFilter;
import org.jimcat.model.filter.metadata.TextFilter;

/**
 * TODO: Short description in one sentence.
 * 
 * TODO: Long description (example?)
 * 
 * 
 * $Id$
 * 
 * @author Christoph
 */
public class CurrentFilterViewer extends JComponent implements ViewFilterListener {

	private JLabel text = new JLabel();

	public CurrentFilterViewer() {
		initComponents();
	}

	private void initComponents() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(text);

		setBorder(BorderFactory.createEtchedBorder());

		SwingClient.getInstance().getViewControl().addViewFilterListener(this);
	}

	public void filterChanges(ViewControl control) {

		TextFilter textFilter = control.getTextFilter();
		AlbumFilter albumFilter = control.getAlbumFilter();
		List<TagFilter> tagFilterList = control.getTagFilterList();

		List<String> l = new ArrayList<String>();

		if (textFilter != null) {
			l.add(String.format("Text '%s'", textFilter.getPattern()));
		}

		if (albumFilter != null) {
			l.add("Album '" + albumFilter.getAlbum().getName() + "'");
		}

		if (tagFilterList.size() > 0) {
			boolean negate = control.isNegateTagFilter();

			TagCombineMode mode = control.getCombineMode();

			String tags = "";

			for (int i = 0; i < tagFilterList.size(); i++) {
				tags += tagFilterList.get(i).getTag().getName();

				if (i < tagFilterList.size() - 1) {
					tags += ", ";
				}
			}

			String s;

			if (tagFilterList.size() > 1) {

				if (negate) {
					s = "Doesn't have ";
				} else {
					s = "Has ";
				}

				if (mode == TagCombineMode.ALL) {
					s += "these tags: ";
				} else {
					s += "one of these tags: ";
				}
			} else {
				if (negate) {
					s = "Doesn't have tag ";
				} else {
					s = "Has tag ";
				}
			}

			l.add(s + tags);
		}

		if (l.isEmpty()) {
			text.setText("Showing all images");
		} else {
			String s = "Showing: ";

			for (int i = 0; i < l.size(); i++) {
				s += l.get(i);

				if (i != l.size() - 1) {
					s += ", ";
				}
			}

			text.setText(s);
		}
	}
}
