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

package org.jimcat.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jimcat.model.Image;
import org.jimcat.services.rename.Renamer;

/**
 * The rename panel is used to display rename resultes both for the renamer and
 * the export dialog.
 * 
 * 
 * $Id$
 * 
 * @author Christoph, Michael
 */
public class RenamePanel extends JPanel {

	/**
	 * the string which defines the new name
	 */
	private JTextField configString;

	/**
	 * defines how many digits numbers have
	 */
	private JSpinner digitsNumber;

	/**
	 * selector for the date that shall be used
	 */
	private JComboBox date;

	/**
	 * the tabel to show the results
	 */
	private JTable table;

	/**
	 * the renamer used to evaluate the input and build the new names
	 */
	private Renamer renamer;

	/**
	 * the picture taken date
	 */
	private static final String DATE_PICTURE_TAKEN = "Picture Taken";

	/**
	 * the picture added date
	 */
	private static final String DATE_PICTURE_ADDED = "Picture Addded";

	/**
	 * the picture modified date
	 */
	private static final String DATE_PICTURE_MODIFIED = "Picture Modified";

	/**
	 * the list of images that are renamed
	 */
	private List<Image> selectedImages = new ArrayList<Image>(0);

	/**
	 * the new names of the renamed images
	 */
	private List<String> newNames = new ArrayList<String>(0);

	/**
	 * the table model for the table
	 */
	private RenameTableModel defaultTableModel;

	/**
	 * the list of options
	 */
	private List<Option> options;

	/**
	 * the comboBox used to display the options
	 */
	private JComboBox optionsBox;

	/**
	 * the character used to mark options
	 */
	private String escapeCharacter;

	/**
	 * default constructor
	 */
	public RenamePanel() {
		initComponents();
	}

	/**
	 * build up content
	 */
	private void initComponents() {
		setLayout(new BorderLayout());

		renamer = new Renamer();
		escapeCharacter = renamer.getEscapeCharacter();
		renamer.setConfigString(escapeCharacter + "t " + escapeCharacter + "n");
		renamer.setDigits(4);

		configString = new JTextField();
		configString.addKeyListener(new StringKeyListener());
		configString.setPreferredSize(new Dimension(120, configString.getHeight()));

		date = new JComboBox(new String[] { DATE_PICTURE_TAKEN, DATE_PICTURE_MODIFIED, DATE_PICTURE_ADDED });
		date.addActionListener(new DateActionListener());

		digitsNumber = new JSpinner(new SpinnerNumberModel(4, 1, 10, 1));
		digitsNumber.addChangeListener(new SpinnerListener());

		defaultTableModel = new RenameTableModel();
		table = new JTable(defaultTableModel);
		JScrollPane scrollPane = new JScrollPane(table);
		table.setEnabled(false);

		JPanel leftPanel = new JPanel(new GridLayout(4, 2));

		leftPanel.add(new JLabel("New Name"));
		leftPanel.add(configString);
		leftPanel.add(new JLabel("Use Date"));
		leftPanel.add(date);
		leftPanel.add(new JLabel("Digits for Number"));
		leftPanel.add(digitsNumber);

		leftPanel.add(new JLabel("Insert..."));

		options = new ArrayList<Option>();
		options.add(new Option("", "---"));
		options.add(new Option(escapeCharacter + "t", " Current Name"));
		options.add(new Option(escapeCharacter + "f", " Current File Name"));
		options.add(new Option(escapeCharacter + "n", " Number"));
		options.add(new Option(escapeCharacter + "w", " Width"));
		options.add(new Option(escapeCharacter + "h", " Height"));
		options.add(new Option(escapeCharacter + "r", " Rating"));
		options.add(new Option("", "---"));
		options.add(new Option(escapeCharacter + "d." + escapeCharacter + "m." + escapeCharacter + "y", " Date"));
		options.add(new Option(escapeCharacter + "d", " Day"));
		options.add(new Option(escapeCharacter + "m", " Month"));
		options.add(new Option(escapeCharacter + "y", " Year"));
		options.add(new Option(escapeCharacter + "H:" + escapeCharacter + "M:" + escapeCharacter + "S", " Time"));
		options.add(new Option(escapeCharacter + "H", " Hour"));
		options.add(new Option(escapeCharacter + "M", " Minute"));
		options.add(new Option(escapeCharacter + "S", " Second"));
		options.add(new Option("", "---"));
		options.add(new Option(escapeCharacter + escapeCharacter, " " + escapeCharacter));

		optionsBox = new JComboBox(options.toArray());
		optionsBox.addActionListener(new OptionsBoxListener());
		optionsBox.setMaximumRowCount(options.size());

		leftPanel.add(optionsBox);

		JPanel leftPanelContainer = new JPanel();
		leftPanelContainer.add(leftPanel);

		add(leftPanelContainer, BorderLayout.WEST);
		add(scrollPane, BorderLayout.CENTER);

		Dimension size = getSize();
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screensize.width - size.width) / 2, (screensize.height - size.height) / 2);
	}

	/**
	 * 
	 * set the images that shall be renamed
	 * 
	 * @param images
	 */
	public void setSelectedImages(List<Image> images) {
		this.selectedImages = images;
		updateTable();
	}

	/**
	 * 
	 * set the table model
	 * 
	 * @param model
	 */
	public void setTableModel(TableModel model) {
		table.setModel(model);
	}

	/**
	 * 
	 * set the text shown in config string
	 * 
	 * @param newText
	 */
	public void setConfigStringText(String newText) {
		configString.setText(newText);
	}

	/**
	 * 
	 * get the config string text
	 * 
	 * @return the config string text
	 */
	public String getConfigStringText() {
		return configString.getText();
	}

	/**
	 * 
	 * update the table - calculate new Names
	 */
	public void updateTable() {
		renamer.setConfigString(configString.getText());
		renamer.setDigits(((Integer) digitsNumber.getValue()).intValue());
		newNames = renamer.getNewNames(selectedImages);
		table.tableChanged(new TableModelEvent(table.getModel()));
	}

	private class RenameTableModel extends DefaultTableModel {

		/**
		 * 
		 * the default constructor for the table model for the rename table
		 */
		public RenameTableModel() {
			setColumnIdentifiers(new String[] { "Old Name", "New Name" });
		}

		@Override
		@SuppressWarnings("unused")
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		@Override
		public int getRowCount() {
			return selectedImages.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return selectedImages.get(rowIndex).getTitle();
			}
			return newNames.get(rowIndex);
		}

		@Override
		@SuppressWarnings("unused")
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}
	}

	private class SpinnerListener implements ChangeListener {
		@SuppressWarnings("unused")
		public void stateChanged(ChangeEvent e) {
			updateTable();
		}
	}

	private class StringKeyListener extends KeyAdapter {
		@Override
		@SuppressWarnings("unused")
		public void keyReleased(KeyEvent e) {
			updateTable();
		}
	}

	private class DateActionListener implements ActionListener {
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent e) {
			String selected = (String) date.getSelectedItem();

			if (selected.equals(DATE_PICTURE_ADDED)) {
				renamer.useDateAdded();
			} else if (selected.equals(DATE_PICTURE_MODIFIED)) {
				renamer.useModificationDate();
			} else if (selected.equals(DATE_PICTURE_TAKEN)) {
				renamer.useDateTaken();
			} else {
				throw new IllegalStateException("Unknown date");
			}

			updateTable();
		}
	}

	private class Option {
		private String characters;

		private String name;

		/**
		 * 
		 * construct a new Option
		 * 
		 * @param characters
		 * @param name
		 */
		public Option(String characters, String name) {
			this.characters = characters;
			this.name = name;
		}

		/**
		 * 
		 * @return the string that define the option
		 */
		public String getCharacters() {
			return characters;
		}

		/**
		 * 
		 * @return the name of the option
		 */
		public String getName() {
			return name;
		}

		/**
		 * 
		 * gets the name of the option
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return name;
		}
	}

	private class OptionsBoxListener implements ActionListener {
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent e) {
			Option option = (Option) optionsBox.getSelectedItem();
			String characters = option.getCharacters();

			configString.requestFocus();

			if (characters.length() == 0) {
				return;
			}

			String text = configString.getText();
			int caretPosition = configString.getCaretPosition();
			int length = characters.length();

			String before = text.substring(0, caretPosition);
			String after = text.substring(caretPosition, text.length());

			String newText = before + characters + after;
			configString.setText(newText);
			configString.setCaretPosition(caretPosition + length);
			updateTable();
		}
	}

	/**
	 * @return the newNames
	 */
	public List<String> getNewNames() {
		return newNames;
	}

	/**
	 * @return the selectedImages
	 */
	public List<Image> getSelectedImages() {
		return selectedImages;
	}

	/**
	 * @return the renamer
	 */
	public Renamer getRenamer() {
		return renamer;
	}

	/**
	 * @param newNames
	 *            the newNames to set
	 */
	public void setNewNames(List<String> newNames) {
		this.newNames = newNames;
	}
}
