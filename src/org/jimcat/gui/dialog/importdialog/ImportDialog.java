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

package org.jimcat.gui.dialog.importdialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.jimcat.gui.SwingClient;
import org.jimcat.gui.ViewControl;
import org.jimcat.gui.frame.JimCatDialog;
import org.jimcat.gui.frame.JimCatFrame;
import org.jimcat.gui.imagepopup.AddToAlbumMenu;
import org.jimcat.gui.tagtree.TagTree;
import org.jimcat.model.Album;
import org.jimcat.services.imageimport.ImportFileNameFilter;
import org.jimcat.services.imageimport.ImportJob;

/**
 * This class is forming a dialog for an import.
 * 
 * It controlles the setup of an importJob and initate it when done.
 * 
 * $Id: ImportDialog.java 990 2007-06-24 17:12:20Z 07g1t1u3 $
 * 
 * @author Herbert
 */
public class ImportDialog extends JimCatDialog implements ActionListener {

	/**
	 * constant used to identify select action
	 */
	private final static String SELECT_COMMAND = "select";

	/**
	 * constant used to identify start action
	 */
	private final static String START_COMMAND = "start";

	/**
	 * constant used to identify cancel action
	 */
	private final static String CANCEL_COMMAND = "cancel";

	/**
	 * constant used to identify album selection action
	 */
	private final static String ALBUM_SELECT = "album";

	/**
	 * the file chooser used for this import dialog
	 */
	private JFileChooser chooser;

	/**
	 * used to validate choosen filename
	 */
	private ImportFileNameFilter fileNameFilter;

	/**
	 * the swingclient used for executing commands
	 */
	private SwingClient client;

	// configuration points
	/**
	 * the source file / directory for import process
	 */
	private JTextField source;

	/**
	 * the album name field
	 */
	private JTextField album;

	/**
	 * the album picker used for selection
	 */
	private AlbumPicker albumPicker;

	/**
	 * should the source be read recursive
	 */
	private JCheckBox recursive;

	/**
	 * should found images be copied to a "save" place
	 */
	private JCheckBox copyFiles;

	/**
	 * a list of tags which should be added to new images
	 */
	private TagTree tagTree;

	/**
	 * direct constructor
	 * 
	 * @param client
	 * @param mainframe
	 */
	public ImportDialog(SwingClient client, JimCatFrame mainframe) {
		super(mainframe, true);

		// init members
		this.client = client;
		this.fileNameFilter = new ImportFileNameFilter();

		initComponents();
	}

	/**
	 * build up content
	 */
	private void initComponents() {
		// generell setup
		setTitle("Image Import Setup");
		setLayout(new BorderLayout());

		// build up layout
		// header config
		JPanel header = new JPanel();
		header.setLayout(new GridLayout(3, 1));

		// headline
		JLabel headline = new JLabel("Please config import: ");
		headline.setBorder(new EmptyBorder(0, 5, 0, 5));
		headline.setFont(header.getFont().deriveFont(14).deriveFont(Font.BOLD));
		header.add(headline);

		// file selector
		JPanel fileSelection = new JPanel();
		fileSelection.setLayout(new BorderLayout());
		JLabel sourceLabel = new JLabel("Source: ");
		sourceLabel.setBorder(new EmptyBorder(0, 5, 0, 5));
		fileSelection.add(sourceLabel, BorderLayout.WEST);

		source = new JTextField();
		fileSelection.add(source, BorderLayout.CENTER);

		JButton select = new JButton("Select...");
		select.setActionCommand(SELECT_COMMAND);
		select.addActionListener(this);
		select.setMnemonic('s');
		fileSelection.add(select, BorderLayout.EAST);
		header.add(fileSelection);

		// Option panel
		JPanel options = new JPanel();
		options.setLayout(new GridLayout(1, 2));

		recursive = new JCheckBox("Include Subfolders");
		recursive.setBorder(new EmptyBorder(0, 20, 0, 20));
		recursive.setMnemonic('i');
		copyFiles = new JCheckBox("Copy Files");
		copyFiles.setBorder(new EmptyBorder(0, 20, 0, 20));
		copyFiles.setMnemonic('p');

		options.add(recursive);
		options.add(copyFiles);
		header.add(options);

		add(header, BorderLayout.NORTH);

		// main panel
		JPanel taglistPanel = new JPanel();
		taglistPanel.setLayout(new BorderLayout());

		// top label
		JLabel tagListLabel = new JLabel();
		tagListLabel.setBorder(new EmptyBorder(10, 10, 5, 10));
		tagListLabel.setText("Default associated tags: ");
		taglistPanel.add(tagListLabel, BorderLayout.NORTH);

		// tagtree conten
		JScrollPane taglist = new JScrollPane();
		taglist.setBorder(new EmptyBorder(0, 10, 0, 10));
		taglist.setOpaque(false);

		tagTree = new TagTree();
		tagTree.setBackground(Color.WHITE);
		taglist.setViewportView(tagTree);

		taglist.getViewport().setBackground(Color.WHITE);

		taglistPanel.add(taglist, BorderLayout.CENTER);

		add(taglistPanel, BorderLayout.CENTER);

		// album selector
		JPanel albumSelection = new JPanel();
		albumSelection.setLayout(new BorderLayout());
		albumSelection.setBorder(new EmptyBorder(5, 5, 5, 5));
		JLabel albumLabel = new JLabel("Default Album: ");
		albumLabel.setBorder(new EmptyBorder(0, 5, 0, 5));
		albumSelection.add(albumLabel, BorderLayout.WEST);

		album = new JTextField();
		albumSelection.add(album, BorderLayout.CENTER);

		albumPicker = new AlbumPicker();

		JButton albumSelect = new JButton("Select...");
		albumSelect.setActionCommand(ALBUM_SELECT);
		albumSelect.addActionListener(this);
		albumSelect.setMnemonic('e');
		albumSelection.add(albumSelect, BorderLayout.EAST);
		header.add(albumSelection);

		// Bottom line
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

		// combine album and button selection
		JPanel south = new JPanel();
		south.setLayout(new BorderLayout());
		south.add(albumSelection, BorderLayout.NORTH);
		south.add(bottonPanel, BorderLayout.SOUTH);

		add(south, BorderLayout.SOUTH);

		// resize and place ...
		Dimension size = new Dimension(310, 350);
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(size);
		setLocation((screensize.width - size.width) / 2, (screensize.height - size.height) / 2);

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
	 * (re)init file chooser
	 */
	private void initChooser() {

		File currentDirectory = null;

		if (chooser != null) {
			currentDirectory = chooser.getCurrentDirectory();
		}

		chooser = new JFileChooser();
		ImportDialogFileFilter filter = new ImportDialogFileFilter();
		chooser.setFileFilter(filter);
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setMultiSelectionEnabled(true);
		chooser.setDialogTitle("Select path to import");
		chooser.setCurrentDirectory(currentDirectory);
	}

	/**
	 * This is used to handle button events
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
		} else if (ALBUM_SELECT.equals(command)) {
			performAlbumSelection(e);
		}
	}

	/**
	 * this is just getting a path from a FileChooser
	 */
	private void performSelection() {
		int result = chooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			// bild up seperated file list
			File files[] = chooser.getSelectedFiles();
			StringBuffer list = new StringBuffer();
			for (File file : files) {
				list.append(file.getAbsolutePath());
				// split by path seperator
				list.append(File.pathSeparatorChar);
			}

			// create string of filelsit
			String fileList = "";

			if (list.length() > 0) {
				// remove last seperator
				fileList = list.substring(0, list.length() - 1);
			}

			// set string
			source.setText(fileList);
		}
	}

	/**
	 * this will start the import process
	 */
	private void performStart() {
		// test configuration
		String[] sources = source.getText().trim().split(File.pathSeparator);
		if (sources.length == 0) {
			JOptionPane.showMessageDialog(this, "please select a valid source for images", "Error",
			        JOptionPane.ERROR_MESSAGE);
			return;
		}

		// check files
		List<File> files = new LinkedList<File>();
		for (String sourceFile : sources) {
			File file = new File(sourceFile);
			files.add(file);
			// test file

			boolean valid = file.exists() && fileNameFilter.accept(file.getParentFile(), file.getName());

			if (!valid) {
				JOptionPane.showMessageDialog(this, "selected source element isn't valid: " + sourceFile, "Error",
				        JOptionPane.ERROR_MESSAGE);
				source.requestFocusInWindow();
				return;
			}
		}

		// check albums
		String albumName = album.getText();
		Album defaultAlbum = null;

		// check if nothing was entered
		if (!(albumName.equals(""))) {
			boolean found = false;
			Set<Album> albums = client.getAlbumControl().getAllAlbums();
			Iterator<Album> iter = albums.iterator();
			while (!found && iter.hasNext()) {
				defaultAlbum = iter.next();
				found = defaultAlbum.getName().equals(albumName);
			}

			if (!found) {
				// should it be created?
				int res = JOptionPane.showConfirmDialog(this, "Selected Album does not exists. Should it be created?",
				        "Start Import", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (res == JOptionPane.NO_OPTION) {
					defaultAlbum = null;
				} else {
					defaultAlbum = client.getAlbumControl().createNewAlbum(albumName);
				}
			}

		}
		// build up Import Job
		ImportJob job = new ImportJob();

		// set source file
		job.setFiles(files);

		// rekursive
		job.setRecursive(recursive.isSelected());

		// enable image copy
		job.setCopyImages(copyFiles.isSelected());

		// Tags
		job.setDefaultTags(tagTree.getSelectedTags());

		// Album
		job.setDefaultAlbum(defaultAlbum);

		// clear setting for next import job
		setVisible(false);
		source.setText("");
		album.setText("");

		// update view
		ViewControl control = client.getViewControl();
		control.clearFilter();
		control.setImportIdFilter(job.getImportId());

		// start import job
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
	 * show album picker
	 * 
	 * @param e
	 */
	private void performAlbumSelection(ActionEvent e) {
		// just show popup menue
		JComponent button = (JComponent) e.getSource();
		albumPicker.getPopupMenu().show(button, button.getWidth(), 0);
	}

	/**
	 * Popup menu used to select album
	 */
	private class AlbumPicker extends AddToAlbumMenu {
		/**
		 * overriden to react in special behavior
		 * 
		 * @see org.jimcat.gui.imagepopup.AddToAlbumMenu#elementSelected(org.jimcat.model.Album)
		 */
		@Override
		public void elementSelected(Album selected) {
			album.setText(selected.getName());
		}
	}
}
