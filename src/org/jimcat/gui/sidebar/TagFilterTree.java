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

package org.jimcat.gui.sidebar;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jimcat.gui.SwingClient;
import org.jimcat.gui.TagCombineMode;
import org.jimcat.gui.ViewControl;
import org.jimcat.gui.ViewFilterListener;
import org.jimcat.gui.tagtree.TagTree;
import org.jimcat.gui.tagtree.TagTreeListener;
import org.jimcat.model.filter.TagFilter;
import org.jimcat.model.tag.Tag;

/**
 * This class represents the tagFilter Tree within the Sitebar
 * 
 * $Id: TagFilterTree.java 552 2007-05-09 16:25:15Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public class TagFilterTree extends JPanel implements TagTreeListener, ViewFilterListener, ActionListener, ChangeListener {

	private static final String COMMAND_ALL = "all";

	private static final String COMMAND_ANY = "any";

	private static final String COMMAND_NEGATE = "negate";

	/**
	 * reference to the ViewControl
	 */
	private ViewControl control;

	/**
	 * the radio button for all-combine mode
	 */
	private JRadioButton all;

	/**
	 * the radio button for any-combine mode
	 */
	private JRadioButton any;

	/**
	 * the checkbox to select inverse search
	 */
	private JCheckBox negate;

	/**
	 * the contained tagtree
	 */
	private TagTree tree;

	/**
	 * used to determine if this component was the source of a filter change.
	 */
	private boolean filterChangeTriggered = false;

	/**
	 * simple Constructor
	 */
	public TagFilterTree() {
		// initate members
		control = SwingClient.getInstance().getViewControl();

		// build components
		initComponents();

		// register to view
		control.addViewFilterListener(this);
	}

	/**
	 * build up component
	 */
	private void initComponents() {
		setLayout(new BorderLayout());
		setOpaque(false);

		// Options for option pane content
		Font buttonFont = new JRadioButton().getFont().deriveFont(Font.BOLD);

		int left = 5;
		int right = 5;

		// panel containing optional setup items
		JPanel options = new JPanel();
		options.setBorder(new EmptyBorder(5, 0, 0, 0));
		options.setLayout(new GridLayout(1, 1));
		options.setOpaque(false);

		// panel containing combine mode chooser
		JPanel chooser = new JPanel();
		chooser.setBorder(new EmptyBorder(0, 0, 5, 0));
		chooser.setLayout(new GridLayout(1, 3));
		chooser.setOpaque(false);

		// combine mode buttons
		all = new JRadioButton("All");
		all.setToolTipText("Show Images Which Have All Selected Tags");
		all.setFocusable(false);
		all.setOpaque(false);
		all.setSelected(TagCombineMode.ALL == control.getCombineMode());
		all.setBorder(new EmptyBorder(0, left, 0, right));
		all.setFont(buttonFont);
		all.setActionCommand(COMMAND_ALL);
		all.addActionListener(this);
		chooser.add(all);

		any = new JRadioButton("Any");
		any.setToolTipText("Show Images Which Have At Least One Of The Tags");
		any.setFocusable(false);
		any.setOpaque(false);
		any.setSelected(TagCombineMode.ANY == control.getCombineMode());
		any.setBorder(new EmptyBorder(0, left, 0, right));
		any.setFont(buttonFont);
		any.setActionCommand(COMMAND_ANY);
		any.addActionListener(this);
		chooser.add(any);

		// not checkbox to invert tag selection
		negate = new JCheckBox("Not");
		negate.setToolTipText("Negate Tag Selection");
		negate.setFocusable(false);
		negate.setOpaque(false);
		negate.setBorder(new EmptyBorder(0, left, 0, right));
		negate.setFont(buttonFont);
		negate.setActionCommand(COMMAND_NEGATE);
		negate.addChangeListener(this);
		chooser.add(negate);

		options.add(chooser);
		add(options, BorderLayout.SOUTH);

		// the tree
		tree = new TagTree();
		tree.addTagTreeListener(this);

		add(tree, BorderLayout.CENTER);
	}

	/**
	 * If a tag is selected, the filter should be modified
	 * 
	 * @see org.jimcat.gui.tagtree.TagTreeListener#tagSelected(org.jimcat.model.tag.Tag)
	 */
	public synchronized void tagSelected(Tag tag) {
		filterChangeTriggered = true;
		control.addTagFilter(new TagFilter(tag));
		filterChangeTriggered = false;
	}

	/**
	 * If a tag is unselected, the filter should be modified
	 * 
	 * @see org.jimcat.gui.tagtree.TagTreeListener#tagUnSelected(org.jimcat.model.tag.Tag)
	 */
	public synchronized void tagUnSelected(Tag tag) {
		filterChangeTriggered = true;
		control.removeTagFilter(new TagFilter(tag));
		filterChangeTriggered = false;
	}

	/**
	 * if a filter has changed, update
	 * 
	 * @see org.jimcat.gui.ViewFilterListener#filterChanges(org.jimcat.gui.ViewControl)
	 */
	public synchronized void filterChanges(ViewControl viewControl) {
		// check if this component was source of change
		if (!filterChangeTriggered) {
			// get Tagfilter list
			List<TagFilter> tagFilterList = viewControl.getTagFilterList();

			// extract tages
			Set<Tag> tags = new HashSet<Tag>();
			for (TagFilter filter : tagFilterList) {
				tags.add(filter.getTag());
			}

			// update tree
			tree.selectTags(tags);
		}

		// check mode
		boolean modeAny = control.getCombineMode() == TagCombineMode.ALL;
		all.setSelected(modeAny);
		any.setSelected(!modeAny);

		// negate mode
		negate.setSelected(control.isNegateTagFilter());
	}

	/**
	 * change tag combine mode
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source != any && source != all) {
			return;
		}

		JRadioButton button = (JRadioButton) e.getSource();
		button.setSelected(!button.isSelected());

		// change combine mode
		String command = e.getActionCommand();
		filterChangeTriggered = true;
		if (COMMAND_ALL.equals(command)) {
			control.setCombineMode(TagCombineMode.ALL);
		} else if (COMMAND_ANY.equals(command)) {
			control.setCombineMode(TagCombineMode.ANY);
		}
		filterChangeTriggered = false;
	}

	/**
	 * react on negate changes
	 * 
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		// is right source
		if (e.getSource() == negate) {
			// change negation setup
			control.setNegateTagFilter(negate.isSelected());
		}
	}

}
