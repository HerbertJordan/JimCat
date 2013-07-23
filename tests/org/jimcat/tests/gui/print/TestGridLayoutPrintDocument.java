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

package org.jimcat.tests.gui.print;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.JFrame;

import org.jimcat.model.Image;
import org.jimcat.model.libraries.ImageLibrary;
import org.jimcat.persistence.RepositoryLocator;
import org.jimcat.persistence.RepositoryLocator.ConfigType;
import org.jimcat.services.print.GridLayoutPrintDocument;

/**
 * 
 * 
 * $Id$
 * 
 * @author csag1760
 */
public class TestGridLayoutPrintDocument extends JFrame {

	private GridLayoutPrintDocument doc = new GridLayoutPrintDocument(true);

	public TestGridLayoutPrintDocument() {

		RepositoryLocator.setConfigType(ConfigType.XSTREAM);

		ImageLibrary imageLibrary = ImageLibrary.getInstance();
		Set<Image> images = imageLibrary.getAll();

		ArrayList<Image> list = new ArrayList<Image>(images);

		doc.setImages(list);

		doc.setPageDimension(new Dimension(800, 600));
		doc.setSize(20);
		doc.setHgap(30);
		doc.setVgap(30);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		validate();
		add(doc.drawPage(1));
		setSize(800, 600);

		setVisible(true);
		setBackground(Color.WHITE);

	}

	public static void main(String[] args) {
		new TestGridLayoutPrintDocument();
	}
}
