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

package org.jimcat.gui.dialog.exportdialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.jimcat.gui.SwingClient;
import org.jimcat.gui.dialog.RenamePanel;
import org.jimcat.gui.dialog.importdialog.ImportDialogFileFilter;
import org.jimcat.gui.frame.JimCatDialog;
import org.jimcat.gui.frame.JimCatFrame;
import org.jimcat.model.Image;
import org.jimcat.services.imageexport.ExportDirectoryFilter;
import org.jimcat.services.imageexport.ExportJob;

/**
 * The export dialog is used to configure an export of images and to start a
 * corresponding job.
 * 
 * 
 * 
 * $Id$
 * 
 * @author Michael
 */
public class ExportDialog extends JimCatDialog implements ActionListener {

	/**
	 * commands used for actionListener
	 */
	private final static String SELECT_COMMAND = "select";

	/**
	 * constant to identify start action
	 */
	private final static String START_COMMAND = "start";

	/**
	 * constant to identify cancel action
	 */
	private final static String CANCEL_COMMAND = "cancel";

	/**
	 * the file chooser used for this import dialog
	 */
	private JFileChooser chooser;

	/**
	 * this is set if only the selection shall be exported
	 */
	private JCheckBox onlySelection;

	/**
	 * used to validate choosen directory
	 */
	private ExportDirectoryFilter directoryFilter;

	/**
	 * the swingclient used for executing commands
	 */
	private SwingClient client;

	/**
	 * the target directory for export process
	 */
	private JTextField target;

	/**
	 * the renamePanel used to show the output file name
	 */
	private RenamePanel renamePanel;

	/**
	 * 
	 * The construcor calls the method initComponents to initialize the gui.
	 * 
	 * @param client
	 * @param mainframe
	 */
	public ExportDialog(SwingClient client, JimCatFrame mainframe) {
		super(mainframe, true);
		this.directoryFilter = new ExportDirectoryFilter();
		// init members
		this.client = client;
		initComponents();
	}

	/**
	 * 
	 * initialize the gui-components of the export-dialog
	 */
	private void initComponents() {
		// generell setup
		setTitle("Image Export Setup");
		setLayout(new BorderLayout());

		JPanel header = new JPanel();
		header.setLayout(new BorderLayout());

		// headline
		JLabel headline = new JLabel("Please config export: ");
		headline.setBorder(new EmptyBorder(0, 5, 0, 5));
		headline.setFont(header.getFont().deriveFont(14).deriveFont(Font.BOLD));
		header.add(headline, BorderLayout.NORTH);

		// file selector
		JPanel fileSelection = new JPanel();
		fileSelection.setLayout(new BorderLayout());
		JLabel targetLabel = new JLabel("Target: ");
		targetLabel.setBorder(new EmptyBorder(0, 5, 0, 5));
		fileSelection.add(targetLabel, BorderLayout.WEST);

		target = new JTextField();
		fileSelection.add(target, BorderLayout.CENTER);

		JButton select = new JButton("Select...");
		select.setActionCommand(SELECT_COMMAND);
		select.addActionListener(this);
		select.setMnemonic('s');
		fileSelection.add(select, BorderLayout.EAST);
		header.add(fileSelection, BorderLayout.CENTER);

		// Option panel
		JPanel exportOptions = new JPanel();
		exportOptions.setLayout(new BorderLayout());

		onlySelection = new JCheckBox("Export only Selection");
		onlySelection.setBorder(new EmptyBorder(10, 20, 0, 20));
		onlySelection.setFocusable(false);
		onlySelection.addActionListener(this);

		renamePanel = new ExportRenamePanel();
		renamePanel.setBorder(new EmptyBorder(20, 20, 0, 20));
		renamePanel.setConfigStringText(renamePanel.getRenamer().getEscapeCharacter()+"t");
		
		// to update images
		setOnlySelection(onlySelection.isSelected());
		renamePanel.updateTable();

		exportOptions.add(onlySelection, BorderLayout.NORTH);
		exportOptions.add(renamePanel, BorderLayout.SOUTH);

		header.add(exportOptions, BorderLayout.SOUTH);

		add(header, BorderLayout.NORTH);

		// Botton line
		JPanel bottonPanel = new JPanel();
		bottonPanel.setLayout(new BorderLayout());

		// Button panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2));
		buttonPanel.setBorder(new EmptyBorder(5, 10, 5, 10));

		JButton ok = new JButton();
		ok.setText("OK");
		ok.setActionCommand(START_COMMAND);
		ok.addActionListener(this);
		ok.setMnemonic('o');
		buttonPanel.add(ok);

		JButton cancel = new JButton();
		cancel.setText("Cancel");
		cancel.setActionCommand(CANCEL_COMMAND);
		cancel.addActionListener(this);
		cancel.setMnemonic('c');
		buttonPanel.add(cancel);

		bottonPanel.add(buttonPanel, BorderLayout.EAST);

		add(bottonPanel, BorderLayout.SOUTH);
		// resize and place ...
		pack();
		Dimension size = getSize();
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(size);
		setLocation((screensize.width - size.width) / 2, (screensize.height - size.height) / 2);
		setResizable(false);

		// configure filechooser
		initChooser();
	}
	
	/**
	 * reset file chooser context
	 */
	public void clearFileList() {
		initChooser();
	}

	/**
	 * initialize the file-chooser. Set it to directories only.
	 */
	private void initChooser() {

		File currentDirectory = null;

		if (chooser != null) {
			currentDirectory = chooser.getCurrentDirectory();
		}

		chooser = new JFileChooser();
		ImportDialogFileFilter filter = new ImportDialogFileFilter();
		chooser.setFileFilter(filter);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		chooser.setDialogTitle("Select path to export");
		chooser.setCurrentDirectory(currentDirectory);
	}

	/**
	 * the action performed method reacts to the clicks on buttons within the
	 * export dialog. it also reacts to changing the selection of the
	 * haveCommonName checkBox.
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		// handle some button events
		String command = e.getActionCommand();
		if (SELECT_COMMAND.equals(command)) {
			performSelection();
		} else if (START_COMMAND.equals(command)) {
			performStart();
		} else if (CANCEL_COMMAND.equals(command)) {
			performCancel();
		}
		if (e.getSource() == onlySelection) {
			updateSelectedImages();
			renamePanel.updateTable();
		}
	}

	/**
	 * update the selected images
	 */
	private void updateSelectedImages() {
		List<Image> selectedImages = new LinkedList<Image>();
		if (onlySelection.isSelected()) {
			// copy the selected images
			for (Image img : client.getViewControl().getSelectedImages()) {
				selectedImages.add(img);
			}
		} else {
			// take a snapshot
			selectedImages = client.getViewControl().getLibraryView().getSnapshot();
		}
		renamePanel.setSelectedImages(selectedImages);
	}

	/**
	 * this is just getting a path from a FileChooser
	 */
	private void performSelection() {
		int result = chooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			// set the directory
			File dir = chooser.getSelectedFile();
			String directoryName = dir.getAbsolutePath();
			// set string
			target.setText(directoryName);
		}
	}

	/**
	 * start the export job using current configuration
	 */
	private void performStart() {
		// test configuration

		if (target.getText().equals("")) {
			JOptionPane.showMessageDialog(this, "please select a target directory for the images", "Error",
			        JOptionPane.ERROR_MESSAGE);
			return;
		}
		File targetDirectory = new File(target.getText());
		if (!directoryFilter.accept(targetDirectory.getParentFile(), targetDirectory.getName())) {
			JOptionPane.showMessageDialog(this, "please select a valid target directory for the images", "Error",
			        JOptionPane.ERROR_MESSAGE);
			return;
		}

		// create and configure export job
		ExportJob job = new ExportJob();
		job.setDestination(targetDirectory);

		// give the job the list of file names

		job.setImagesToExportNames(renamePanel.getNewNames());

		job.setImagesToExport(renamePanel.getSelectedImages());

		// clear fields for next export
		target.setText("");
		onlySelection.setSelected(false);
		// hide the export dialog
		setVisible(false);

		// start the job
		client.startJob(job);
	}

	/**
	 * this will just close the dialog
	 */
	private void performCancel() {
		// just close dialog
		setVisible(false);
	}

	/**
	 * on set visible set the selected images
	 * 
	 * @see java.awt.Dialog#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean b) {
		if (b) {
			updateSelectedImages();
			renamePanel.updateTable();
			onlySelection.setEnabled(!(client.getViewControl().getSelectedImages().isEmpty()));
		}
		super.setVisible(b);
	}

	/**
	 * 
	 * Set wheter OnlySelected is marked, or not
	 * 
	 * @param value
	 */
	public void setOnlySelection(boolean value) {
		this.onlySelection.setSelected(value);
		// update images
		updateSelectedImages();
		renamePanel.updateTable();
	}

}
