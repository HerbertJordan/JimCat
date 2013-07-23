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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jimcat.gui.SwingClient;
import org.jimcat.gui.frame.JimCatFrame;
import org.jimcat.gui.frame.JimCatFrameListener;
import org.jimcat.gui.icons.Icons;
import org.jimcat.gui.perspective.Perspectives;
import org.jimcat.gui.perspective.Perspectives.Perspective;

/**
 * The shortcut perspective switch in the upper left corner.
 * 
 * $Id: PerspectiveSwitch.java 942 2007-06-16 09:07:47Z 07g1t1u3 $
 * 
 * @author Herbert
 */
public class PerspectiveSwitch extends JPanel implements JimCatFrameListener, ActionListener {

	/**
	 * a reference to the main Frame
	 */
	private JimCatFrame mainFrame;

	/**
	 * stores the currently selected button
	 */
	private Perspective currentPerspective;

	private Perspective thumbnailPerspective;

	private Perspective detailPerspective;

	private Perspective cardPerspective;

	private JButton thumbnailButton;

	private JButton detailButton;

	private JButton cardButton;

	private Color defaultBackgroundColor;

	private final Color selectedBackgroundColor = Color.GRAY;

	/**
	 * direct constructor
	 * 
	 * @param frame -
	 *            a reference to the including JimCatMainFrame
	 */
	public PerspectiveSwitch(JimCatFrame frame) {
		mainFrame = frame;
		initComponents();
	}

	/**
	 * build up component
	 */
	private void initComponents() {
		// Build Layout + Border
		setLayout(new FlowLayout(FlowLayout.CENTER));
		setBorder(new EmptyBorder(0, 10, 0, 10));

		// detail perspective and button
		detailPerspective = Perspectives.getDetailPerspective();
		JMenuItem detailPerspectiveItem = new JMenuItem(detailPerspective.getName());
		detailPerspectiveItem.setToolTipText("Detail View");
		detailPerspectiveItem.addActionListener(this);

		detailButton = new JButton(Icons.VIEW_LIST);

		defaultBackgroundColor = detailButton.getBackground();

		// detailButton.setBorder(BorderFactory.createEmptyBorder());
		detailButton.setFocusable(false);

		detailButton.setActionCommand(detailPerspective.getName());
		detailButton.setPreferredSize(new Dimension(40, 20));
		detailButton.addActionListener(this);
		add(detailButton);

		// card perspective and button
		cardPerspective = Perspectives.getCardPerspective();
		JMenuItem cardPerspectiveItem = new JMenuItem(cardPerspective.getName());
		cardPerspectiveItem.setToolTipText("Card View");
		cardPerspectiveItem.addActionListener(this);

		cardButton = new JButton(Icons.VIEW_CARDS);
		// cardButton.setBorder(BorderFactory.createEmptyBorder());
		cardButton.setFocusable(false);

		cardButton.setActionCommand(cardPerspective.getName());
		cardButton.setPreferredSize(new Dimension(40, 20));
		cardButton.addActionListener(this);
		add(cardButton);

		// thumbnail perspective
		thumbnailPerspective = Perspectives.getThumbnailPerspective();
		JMenuItem thumbnailPerspectiveItem = new JMenuItem(thumbnailPerspective.getName());
		thumbnailPerspectiveItem.setToolTipText("Thumbnail View");
		thumbnailPerspectiveItem.addActionListener(this);

		thumbnailButton = new JButton(Icons.VIEW_THUMBNAILS);
		// thumbnailButton.setBorder(BorderFactory.createEmptyBorder());
		thumbnailButton.setFocusable(false);

		thumbnailButton.setActionCommand(thumbnailPerspective.getName());
		thumbnailButton.setPreferredSize(new Dimension(40, 20));
		thumbnailButton.addActionListener(this);
		add(thumbnailButton);

		// fullscreen
		JButton fullscreenButton = new JButton(Icons.FULLSCREEN);
		// fullscreenButton.setBorder(BorderFactory.createEmptyBorder());
		fullscreenButton.setFocusable(false);

		fullscreenButton.setPreferredSize(new Dimension(40, 20));
		fullscreenButton.addActionListener(new ActionListener() {
			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {
				SwingClient.getInstance().showFullScreen();

			}
		});
		add(fullscreenButton);

		// register as listener by JimCatFrame
		mainFrame.addJimCatFrameListener(this);

	}

	/**
	 * Update selecte Button on perspective exchange
	 * 
	 * @param newPerspective
	 *            the new perspective shown
	 */
	public void perspectiveExchanged(final Perspective newPerspective) {
		if (currentPerspective != newPerspective) {

			detailButton.setSelected(false);
			thumbnailButton.setSelected(false);
			cardButton.setSelected(false);

			if (currentPerspective == detailPerspective) {
				detailButton.setBackground(defaultBackgroundColor);
			} else if (currentPerspective == thumbnailPerspective) {
				thumbnailButton.setBackground(defaultBackgroundColor);
			} else if (currentPerspective == cardPerspective) {
				cardButton.setBackground(defaultBackgroundColor);
			}

			if (newPerspective == detailPerspective) {
				detailButton.setSelected(true);
				detailButton.setBackground(selectedBackgroundColor);
			} else if (newPerspective == thumbnailPerspective) {
				thumbnailButton.setSelected(true);
				thumbnailButton.setBackground(selectedBackgroundColor);
			} else if (newPerspective == cardPerspective) {
				cardButton.setSelected(true);
				cardButton.setBackground(selectedBackgroundColor);
			}

			currentPerspective = newPerspective;

		}
	}

	/**
	 * Exchanges the perspective by clicking a button
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {

		String cmd = e.getActionCommand();

		detailButton.setSelected(false);
		thumbnailButton.setSelected(false);
		cardButton.setSelected(false);

		if (cmd.equals(detailPerspective.getName())) {
			mainFrame.setPerspective(detailPerspective);
			detailButton.setSelected(true);
		} else if (cmd.equals(thumbnailPerspective.getName())) {
			mainFrame.setPerspective(thumbnailPerspective);
			thumbnailButton.setSelected(true);
		} else if (cmd.equals(cardPerspective.getName())) {
			mainFrame.setPerspective(cardPerspective);
			cardButton.setSelected(true);
		} else {
			throw new IllegalStateException("Unknown Perspective: " + cmd);
		}
	}

}
