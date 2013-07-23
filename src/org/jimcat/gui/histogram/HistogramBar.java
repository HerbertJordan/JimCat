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

package org.jimcat.gui.histogram;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.button.StandardButtonShaper;
import org.jvnet.substance.utils.SubstanceConstants;

/**
 * A container assembling a completate histogram bar (diagram + buttons)
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class HistogramBar extends JPanel {

	/**
	 * action command for button priv
	 */
	private static final String PRIV = "priv";

	/**
	 * action command for button next
	 */
	private static final String NEXT = "next";

	/**
	 * the contained histogram
	 */
	private Histogram histogram;

	/**
	 * the privious button
	 */
	private JButton priv;

	/**
	 * the next button
	 */
	private JButton next;

	/**
	 * create new histogram bar using given model
	 * 
	 * @param model -
	 *            the model to show
	 */
	public HistogramBar(HistogramModel model) {
		// build histogram
		histogram = new Histogram(model);

		// build content
		initComponents();
	}

	/**
	 * build up component
	 */
	private void initComponents() {
		this.setLayout(new BorderLayout());

		// Helps getting rounded Buttons
		StandardButtonShaper buttonShaper = new StandardButtonShaper();

		// Button priv
		JPanel leftButtonPanel = new JPanel();
		FlowLayout leftButtonLayout = new FlowLayout();
		leftButtonLayout.setHgap(0);
		leftButtonLayout.setAlignment(FlowLayout.CENTER);
		leftButtonPanel.setLayout(leftButtonLayout);

		ButtonListener listener = new ButtonListener();

		priv = new JButton("<");
		priv.setFocusable(false);
		priv.setActionCommand("priv");
		priv.setPreferredSize(new Dimension(30, 20));
		priv.putClientProperty(SubstanceLookAndFeel.BUTTON_SHAPER_PROPERTY, buttonShaper);
		priv.putClientProperty(SubstanceLookAndFeel.BUTTON_SIDE_PROPERTY, SubstanceConstants.Side.RIGHT);
		priv.setActionCommand(PRIV);
		priv.addActionListener(listener);
		leftButtonPanel.add(priv);
		add(leftButtonPanel, BorderLayout.WEST);

		// Button next
		JPanel rightButtonPanel = new JPanel();
		FlowLayout rightButtonLayout = new FlowLayout();
		rightButtonLayout.setHgap(0);
		rightButtonLayout.setAlignment(FlowLayout.CENTER);
		rightButtonPanel.setLayout(rightButtonLayout);

		next = new JButton(">");
		next.setFocusable(false);
		next.setActionCommand("priv");
		next.setPreferredSize(new Dimension(30, 20));
		next.putClientProperty(SubstanceLookAndFeel.BUTTON_SHAPER_PROPERTY, buttonShaper);
		next.putClientProperty(SubstanceLookAndFeel.BUTTON_SIDE_PROPERTY, SubstanceConstants.Side.LEFT);
		next.setActionCommand(NEXT);
		next.addActionListener(listener);
		rightButtonPanel.add(next);
		add(rightButtonPanel, BorderLayout.EAST);

		// add histogram
		add(histogram, BorderLayout.CENTER);
	}

	/**
	 * @param path
	 * @throws IllegalArgumentException
	 * @see org.jimcat.gui.histogram.Histogram#centerElement(org.jimcat.gui.histogram.HistogramModelPath)
	 */
	public void centerElement(HistogramModelPath path) throws IllegalArgumentException {
		histogram.centerElement(path);
	}

	/**
	 * reacting on button actions
	 */
	private class ButtonListener implements ActionListener {
		/**
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if (PRIV.equals(command)) {
				histogram.moveCenter(-1);
			} else {
				histogram.moveCenter(1);
			}
		}
	}

}
