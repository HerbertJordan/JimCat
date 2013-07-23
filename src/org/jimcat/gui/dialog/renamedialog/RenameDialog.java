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

package org.jimcat.gui.dialog.renamedialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.jimcat.gui.SwingClient;
import org.jimcat.gui.dialog.RenamePanel;
import org.jimcat.gui.frame.JimCatDialog;
import org.jimcat.gui.frame.JimCatFrame;
import org.jimcat.model.Image;

/**
 * Dialog to rename a bunch of images.
 * 
 * 
 * @author Christoph
 */
public class RenameDialog extends JimCatDialog implements ActionListener {

	/**
	 * command used to identify rename action
	 */
	private final static String RENAME_COMMAND = "rename";

	/**
	 * command used to identify cancel action
	 */
	private final static String CANCEL_COMMAND = "cancel";

	/**
	 * the cancel button
	 */
	private JButton cancel;

	/**
	 * the rename button
	 */
	private JButton rename;

	/**
	 * the panel encapsulating the rename configuration
	 */
	private RenamePanel renamePanel;

	/**
	 * the swingclient used for executing commands
	 */
	private SwingClient client;

	/**
	 * direct constructor
	 * 
	 * @param client
	 * @param mainframe
	 */
	public RenameDialog(SwingClient client, JimCatFrame mainframe) {
		super(mainframe, true);

		this.client = client;

		initComponents();
	}

	/**
	 * build up content
	 */
	private void initComponents() {
		// generell setup
		setTitle("Rename Images");
		setLayout(new BorderLayout());

		renamePanel = new RenamePanel();

		cancel = new JButton("Cancel");
		cancel.setActionCommand(CANCEL_COMMAND);
		cancel.addActionListener(this);
		cancel.setMnemonic('c');

		rename = new JButton("Rename");
		rename.setActionCommand(RENAME_COMMAND);
		rename.addActionListener(this);
		rename.setMnemonic('r');

		getRootPane().setDefaultButton(rename);

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bottomPanel.add(rename);
		bottomPanel.add(cancel);

		add(renamePanel, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);

		pack();
		setResizable(false);

		Dimension size = getSize();
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screensize.width - size.width) / 2, (screensize.height - size.height) / 2);
	}

	/**
	 * change list of selected images
	 * 
	 * @param images
	 */
	public void setSelectedImages(List<Image> images) {
		renamePanel.setSelectedImages(images);
		renamePanel.updateTable();
	}

	/**
	 * react on actions emerged from contained control elements
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if (RENAME_COMMAND.equals(command)) {
			client.getImageControl().rename(renamePanel.getSelectedImages(), renamePanel.getNewNames());
			setVisible(false);
		} else if (CANCEL_COMMAND.equals(command)) {
			setVisible(false);
		}
	}

}
