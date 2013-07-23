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

package org.jimcat.gui.dialog.printdialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jimcat.gui.SwingClient;
import org.jimcat.gui.frame.JimCatDialog;
import org.jimcat.gui.frame.JimCatFrame;
import org.jimcat.model.Image;
import org.jimcat.services.OperationsLocator;
import org.jimcat.services.imagemanager.ImageQuality;
import org.jimcat.services.print.GridLayoutPrintDocument;
import org.jimcat.services.print.PrintJob;

/**
 * The printDialog is used to configure a printJob
 * 
 * 
 * $Id$
 * 
 * @author Christoph, Michael
 */
public class PrintDialog extends JimCatDialog implements ActionListener {

	/**
	 * the command strings for print action
	 */
	private final static String PRINT_COMMAND = "print";

	/**
	 * the command strings for select printer action
	 */
	private final static String SELECT_PRINTER_COMMAND = "select";

	/**
	 * the command strings for cancel action
	 */
	private final static String CANCEL_COMMAND = "cancel";

	/**
	 * the swingclient used for executing commands
	 */
	private SwingClient client;

	/**
	 * the document used for preview and the printJob
	 */
	private GridLayoutPrintDocument document;

	/**
	 * for configuration of print only selection
	 */
	private JCheckBox onlySelection;

	/**
	 * the horizontal gap configuration slider
	 */
	private JSlider hgap;

	/**
	 * the vertical gap configuration slider
	 */
	private JSlider vgap;

	/**
	 * the image size configuration slider
	 */
	private JSlider size;

	/**
	 * the current page slider
	 */
	private JSlider page = new JSlider(1, 1);

	/**
	 * checkbox to configure if title shall be printed
	 */
	private JCheckBox showTitle;

	/**
	 * if the real images shall be shown in preview or only substitutes
	 */
	private JCheckBox showImages;

	/**
	 * the change listener for the slider and checkboxes
	 */
	private PrintChangeListener changeListener;

	/**
	 * the panel into which the preview document is drawn
	 */
	private PrintPreview preview;

	/**
	 * the page format
	 */
	private PageFormat pageFormat;

	/**
	 * label to anounce current selected page
	 */
	private JLabel pageLabel;

	/**
	 * 
	 * The construcor calls the method initComponents to initialize the gui.
	 * 
	 * @param client
	 * @param mainframe
	 */
	public PrintDialog(SwingClient client, JimCatFrame mainframe) {
		super(mainframe, true);
		// init members
		this.client = client;
		initComponents();
	}

	/**
	 * 
	 * initialize the gui-components of the print-dialog
	 */
	private void initComponents() {
		// generell setup
		setTitle("Print Images Setup");
		setLayout(new BorderLayout());

		changeListener = new PrintChangeListener();

		JPanel previewSide = initPreview(); // left side
		JPanel config = initConfig(); // right side
		JPanel buttons = initButtons(); // bottom

		add(previewSide, BorderLayout.CENTER);
		add(config, BorderLayout.EAST);
		add(buttons, BorderLayout.SOUTH);

		Dimension frameSize = new Dimension(650, 500);
		setSize(frameSize);
		// setResizable(false);
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screensize.width - frameSize.width) / 2, (screensize.height - frameSize.height) / 2);

		updateDocument();
	}

	/**
	 * 
	 * initialize the preview part of the dialog
	 * 
	 * @return the jpanel that is created
	 */
	private JPanel initPreview() {
		// creat result Panel
		JPanel result = new JPanel();
		result.setBorder(new EmptyBorder(12, 12, 0, 0));
		result.setLayout(new BorderLayout());

		// preview in the north
		document = new GridLayoutPrintDocument(false);
		document.setRenderingQuality(ImageQuality.FASTEST);

		// set the size of the print document
		PrinterJob job = PrinterJob.getPrinterJob();
		pageFormat = job.defaultPage();

		int imageableWidth = (int) pageFormat.getImageableWidth();
		int imageableHeigh = (int) pageFormat.getImageableHeight();
		Dimension pageSize = new Dimension(imageableWidth, imageableHeigh);
		document.setPageDimension(pageSize);

		preview = new PrintPreview(document, pageFormat);
		result.add(preview, BorderLayout.CENTER);

		// slider / text combination
		JPanel sliderText = new JPanel(new GridLayout(2, 1));
		page.addChangeListener(changeListener);
		page.setMajorTickSpacing(1);
		page.setPaintTicks(true);
		page.setSnapToTicks(true);
		page.setFocusable(false);
		sliderText.add(page);

		pageLabel = new JLabel();
		pageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		sliderText.add(pageLabel);

		// page slider
		JPanel pagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		pagePanel.add(sliderText);
		result.add(pagePanel, BorderLayout.SOUTH);

		return result;
	}

	/**
	 * 
	 * initialize the configuration part of the dialog with the sliders
	 * 
	 * @return the configuration panel
	 */
	private JPanel initConfig() {
		JPanel configPanel = new JPanel(new BorderLayout());
		configPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

		JButton sysPrintDialog = new JButton("Printer Setup...");
		sysPrintDialog.setActionCommand(SELECT_PRINTER_COMMAND);
		sysPrintDialog.addActionListener(this);
		sysPrintDialog.setMnemonic('s');

		JPanel sliderPanel = new JPanel();

		sliderPanel.setSize(200, 100);

		hgap = new JSlider(0, 20, 2);
		vgap = new JSlider(0, 20, 2);
		size = new JSlider(10, 51, 22);
		showTitle = new JCheckBox("Print Titles");
		showImages = new JCheckBox("Display Images", true);
		onlySelection = new JCheckBox("Only Selection");

		hgap.addChangeListener(changeListener);
		vgap.addChangeListener(changeListener);
		size.addChangeListener(changeListener);
		showTitle.addItemListener(changeListener);
		showImages.addItemListener(changeListener);
		onlySelection.addItemListener(new ShowOnlySelectedChangeListener());

		hgap.setFocusable(false);
		vgap.setFocusable(false);
		size.setFocusable(false);
		showTitle.setFocusable(false);
		onlySelection.setFocusable(false);
		showImages.setFocusable(false);

		showTitle.setMnemonic('t');
		onlySelection.setMnemonic('o');
		showImages.setMnemonic('d');

		GridLayout gridLayout = new GridLayout(12, 1);
		sliderPanel.setLayout(gridLayout);

		sliderPanel.add(new JLabel("Size"));
		sliderPanel.add(size);

		// sliderPanel.add(new JLabel(""));

		sliderPanel.add(new JLabel("Horizontal Gap"));
		sliderPanel.add(hgap);

		sliderPanel.add(new JLabel("Vertical Gap"));
		sliderPanel.add(vgap);

		sliderPanel.add(new JPanel());

		sliderPanel.add(showTitle);
		sliderPanel.add(onlySelection);
		sliderPanel.add(showImages);

		sliderPanel.add(new JPanel());
		sliderPanel.add(sysPrintDialog);

		configPanel.add(sliderPanel, BorderLayout.NORTH);

		return configPanel;
	}

	/**
	 * 
	 * initialize the bottom part of the dialog with the buttons
	 * 
	 * @return the buttons pannel
	 */
	private JPanel initButtons() {
		JPanel panel = new JPanel();

		panel.setBorder(new EmptyBorder(10, 0, 0, 0));
		FlowLayout flowLayout = new FlowLayout();
		panel.setLayout(flowLayout);
		flowLayout.setAlignment(SwingConstants.RIGHT);

		JButton print = new JButton("Print");
		JButton cancel = new JButton("Cancel");

		print.setActionCommand(PRINT_COMMAND);
		cancel.setActionCommand(CANCEL_COMMAND);

		print.addActionListener(this);
		cancel.addActionListener(this);

		print.setMnemonic('p');
		cancel.setMnemonic('c');

		panel.add(print);
		panel.add(cancel);

		return panel;
	}

	/**
	 * update the size of the preview panel
	 */
	private void updateSizeOfPreviewPanel() {
		// just update preview panel
		preview.update();
	}

	/**
	 * 
	 * update the images that shall be printed in the preview
	 */
	public void updatePreviewImages() {
		List<Image> images;
		if (onlySelection.isSelected()) {
			// copy the selected images
			images = new LinkedList<Image>();
			for (Image img : client.getViewControl().getSelectedImages()) {
				images.add(img);
			}
		} else {
			// take a snapshot
			images = client.getViewControl().getLibraryView().getSnapshot();
		}

		document.setImages(images);
		updateDocument();
	}

	/**
	 * 
	 * update the preview document
	 */
	private void updateDocument() {
		document.setHgap(hgap.getValue());
		document.setVgap(vgap.getValue());
		document.setShowImages(showImages.isSelected());
		document.setShowTitle(showTitle.isSelected());
		document.setSize(size.getValue());

		page.setMaximum(Math.max(1, document.getPageCount()));

		preview.setDocument(document);
		preview.setPage(page.getValue());

		pageLabel.setText("Page " + page.getValue() + "/" + Math.max(1, document.getPageCount()));

		preview.setFormat(pageFormat);
		preview.update();
	}

	/**
	 * 
	 * set the checkbox onlySelection available or not
	 * 
	 * @param selectedImagesAvailable
	 */
	public void setSelectedImagesAvailable(boolean selectedImagesAvailable) {
		onlySelection.setEnabled(selectedImagesAvailable);
	}

	/**
	 * handle the button-actions
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (CANCEL_COMMAND.equals(e.getActionCommand())) {
			setVisible(false);
		} else if (PRINT_COMMAND.equals(e.getActionCommand())) {
			startPrintJob();
		} else if (SELECT_PRINTER_COMMAND.equals(e.getActionCommand())) {
			handleSysPrintDialog();
		}
	}

	/**
	 * show the system - printer dialog and read the data that it returns
	 */
	private void handleSysPrintDialog() {
		// show the dialog
		PrinterJob job = PrinterJob.getPrinterJob();
		pageFormat = job.pageDialog(pageFormat);
		// get the new pageFormat
		int width = (int) pageFormat.getImageableWidth();
		int height = (int) pageFormat.getImageableHeight();
		document.setPageDimension(new Dimension(width, height));
		updateSizeOfPreviewPanel();
		preview.update();
		updateDocument();
	}

	/**
	 * configure and start the print job according to the parameters that were
	 * set in the dialog
	 */
	private void startPrintJob() {
		// check if any images are marked for printing
		if (document.getPageCount() == 0) {
			client.showMessage("No images have been selected for printing.", "Printing aborted", JOptionPane.OK_OPTION);
			setVisible(false);
			return;
		}
		// start the print job
		document.setShowImages(true);
		document.setRenderingQuality(ImageQuality.getBest());
		PrintJob pj = new PrintJob(OperationsLocator.getJobOperations().getJobManager(), document, pageFormat);
		client.startJob(pj);
		setVisible(false);
	}

	/**
	 * a printe listener observing all control elements of this dialog
	 */
	private class PrintChangeListener implements ChangeListener, ItemListener {

		/**
		 * calls updateDocument from PrintDialog
		 * 
		 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
		 */
		@SuppressWarnings("unused")
		public void stateChanged(ChangeEvent e) {
			updateDocument();
		}

		/**
		 * calls updateDocument from PrintDialog
		 * 
		 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
		 */
		@SuppressWarnings("unused")
		public void itemStateChanged(ItemEvent e) {
			updateDocument();
		}
	}

	/**
	 * listener observing the show only selected checkbox
	 */
	private class ShowOnlySelectedChangeListener implements ItemListener {

		/**
		 * calls updatePreviewImages from PrintDialog
		 * 
		 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
		 */
		@SuppressWarnings("unused")
		public void itemStateChanged(ItemEvent e) {
			updatePreviewImages();
		}

	}

}
