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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.print.PageFormat;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.jimcat.gui.borders.RoundedShadowBorder;
import org.jimcat.services.print.PrintDocument;
import org.jvnet.substance.SubstanceLookAndFeel;

/**
 * A component used for print previews
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class PrintPreview extends JComponent {

	/**
	 * the document used for the preview
	 */
	private PrintDocument document;

	/**
	 * the page currently shown
	 */
	private int page = 1;

	/**
	 * the page format used for the layout
	 */
	private PageFormat format;

	/**
	 * the only child of this component
	 */
	private JPanel content;

	/**
	 * the panel simulating the sheet
	 */
	private JPanel sheet;

	/**
	 * the panel containing the document
	 */
	private JPanel printArea;

	/**
	 * create a new print preview for the given document
	 * 
	 * @param document
	 *            the document to show
	 * @param format
	 *            the format to use
	 */
	public PrintPreview(PrintDocument document, PageFormat format) {
		this.document = document;
		this.format = format;

		// build up gui components
		initComponents();
	}

	/**
	 * build up gui components
	 */
	private void initComponents() {
		setLayout(null);

		// the white sheet + shadow
		content = new JPanel();
		content.setOpaque(false);
		content.setLayout(new BorderLayout());
		content.setBackground(Color.WHITE);
		content.setBorder(new RoundedShadowBorder(0));
		content.putClientProperty(SubstanceLookAndFeel.WATERMARK_IGNORE, Boolean.TRUE);
		add(content);

		// insert sheet
		sheet = new JPanel();
		sheet.setOpaque(false);
		sheet.setBorder(null);
		sheet.setLayout(new BorderLayout());
		content.add(sheet, BorderLayout.CENTER);

		// insert document
		printArea = new JPanel();
		printArea.setOpaque(false);
		printArea.setBorder(new LineBorder(new Color(240, 240, 240), 1));
		printArea.setLayout(new BorderLayout());
		sheet.add(printArea, BorderLayout.CENTER);
	}

	/**
	 * update layout
	 * 
	 * @see java.awt.Container#doLayout()
	 */
	@Override
	public void doLayout() {
		// get available size
		Dimension size = getSize();

		// get relative Page format - page size
		double aspectWidth = size.getWidth() / format.getWidth();
		double aspectHeigh = size.getHeight() / format.getHeight();
		double scale = Math.min(aspectWidth, aspectHeigh);

		// resize
		int width = (int) (format.getWidth() * scale);
		int height = (int) (format.getHeight() * scale);
		content.setSize(new Dimension(width, height));

		// place
		int x = (size.width - width) / 2;
		int y = (size.height - height) / 2;
		content.setLocation(x, y);

		// empty border - non printable area
		// calculate the size of the left and right boder
		int borderWest = (int) (format.getImageableX() * scale) - 1;
		int borderEast = (int) ((format.getWidth() - format.getImageableWidth() - format.getImageableX()) * scale) - 1;

		// calculate the size of the top and bottom boder
		int borderNorth = (int) (format.getImageableY() * scale) - 1;
		int borderSouth = (int) ((format.getHeight() - format.getImageableHeight() - format.getImageableY()) * scale) - 1;

		sheet.setBorder(BorderFactory.createEmptyBorder(borderNorth, borderWest, borderSouth, borderEast));

		// content
		int areaWidth = (int) (format.getImageableWidth() * scale);
		int areaHeigh = (int) (format.getImageableHeight() * scale);
		Dimension area = new Dimension(areaWidth, areaHeigh);

		document.setPageDimension(area);
		if (document.getPageCount() > 0) {
			JPanel doc = document.drawPage(page);
			printArea.removeAll();
			printArea.add(doc, BorderLayout.CENTER);
			printArea.revalidate();
		} else {
			printArea.removeAll();
			printArea.revalidate();
		}
	}

	/**
	 * this will update the shown preview
	 */
	public void update() {
		revalidate();
	}

	/**
	 * @param document
	 *            the document to set
	 */
	public void setDocument(PrintDocument document) {
		this.document = document;
		update();
	}

	/**
	 * @param format
	 *            the format to set
	 */
	public void setFormat(PageFormat format) {
		this.format = format;
		update();
	}

	/**
	 * @param page
	 *            the page to set
	 */
	public void setPage(int page) {
		this.page = Math.max(Math.min(page, document.getPageCount()), 1);
		update();
	}

}
