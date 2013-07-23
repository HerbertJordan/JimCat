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

package org.jimcat.gui.frame;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.jimcat.gui.SwingClient;
import org.jimcat.gui.ViewControl;
import org.jimcat.gui.ViewFilterListener;
import org.jimcat.gui.icons.Icons;
import org.jimcat.gui.perspective.Perspectives;
import org.jimcat.gui.perspective.Perspectives.Perspective;

/**
 * This class is forming the menu of the main window.
 * 
 * $Id: JimCatMenu.java 951 2007-06-18 20:47:40Z 07g1t1u3 $
 * 
 * @author Herbert
 */
public class JimCatMenu extends JMenuBar {

	/**
	 * a reference to the JimCate main Frame
	 */
	private JimCatFrame mainFrame;

	/**
	 * a reference to the installed SwingClient
	 */
	private final SwingClient client = SwingClient.getInstance();

	/**
	 * the menu item representing the view duplicate option
	 */
	protected JMenuItem viewDuplicates;

	/**
	 * simple constructor
	 * 
	 * @param frame
	 *            JimCatFrame
	 */
	public JimCatMenu(JimCatFrame frame) {
		mainFrame = frame;

		// shortcuts
		KeyStroke ctrlI = KeyStroke.getKeyStroke(KeyEvent.VK_I, Event.CTRL_MASK);
		KeyStroke ctrlE = KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK);
		KeyStroke ctrlP = KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK);
		KeyStroke ctrlQ = KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK);

		KeyStroke ctrlL = KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.CTRL_MASK);
		KeyStroke ctrlU = KeyStroke.getKeyStroke(KeyEvent.VK_U, Event.CTRL_MASK);
		KeyStroke ctrlD = KeyStroke.getKeyStroke(KeyEvent.VK_D, Event.CTRL_MASK);
		KeyStroke ctrlR = KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK);
		KeyStroke ctrlT = KeyStroke.getKeyStroke(KeyEvent.VK_T, Event.CTRL_MASK);
		KeyStroke ctrlJ = KeyStroke.getKeyStroke(KeyEvent.VK_J, Event.CTRL_MASK);
		KeyStroke altEnter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, Event.ALT_MASK);

		// Main Menus
		JMenu file = new JMenu("File");
		file.setMnemonic('f');

		JMenu view = new JMenu("View");
		view.setMnemonic('v');

		JMenu help = new JMenu("Help");
		help.setMnemonic('h');

		// File menu

		JMenuItem fileImport = new JMenuItem("Import...");
		fileImport.setMnemonic('i');
		fileImport.setAccelerator(ctrlI);
		fileImport.setIcon(Icons.IMPORT);
		fileImport.addActionListener(new ImageImportAction());

		JMenuItem fileExport = new JMenuItem("Export...");
		fileExport.setMnemonic('e');
		fileExport.setAccelerator(ctrlE);
		fileExport.setIcon(Icons.EXPORT);
		fileExport.addActionListener(new ImageExportAction());

		JMenuItem filePrint = new JMenuItem("Print...");
		filePrint.setMnemonic('p');
		filePrint.setAccelerator(ctrlP);
		filePrint.setIcon(Icons.PRINT);
		filePrint.addActionListener(new PrintAction());

		JMenuItem fileExit = new JMenuItem("Exit");
		fileExit.setAccelerator(ctrlQ);
		fileExit.setMnemonic('x');
		fileExit.addActionListener(new ImageExitAction());

		// View menu

		JMenuItem viewShowAll = new JMenuItem("Show All");
		viewShowAll.setMnemonic('a');
		viewShowAll.setAccelerator(ctrlL);
		viewShowAll.setIcon(Icons.ALL_IMAGES);
		viewShowAll.addActionListener(new ShowAllAction());

		viewDuplicates = new JCheckBoxMenuItem("Show Duplicates");
		viewDuplicates.setMnemonic('u');
		viewDuplicates.setAccelerator(ctrlU);
		viewDuplicates.addActionListener(new DuplicatesAction());

		Perspective detailPerspective = Perspectives.getDetailPerspective();
		JMenuItem viewDetail = new JMenuItem(detailPerspective.getName());
		viewDetail.setMnemonic('d');
		viewDetail.setAccelerator(ctrlD);
		viewDetail.setIcon(Icons.VIEW_LIST);
		viewDetail.setToolTipText("Details");
		viewDetail.addActionListener(new PerspectiveSwitcher(detailPerspective, mainFrame));

		Perspective cardsPerspective = Perspectives.getCardPerspective();
		JMenuItem viewCards = new JMenuItem(cardsPerspective.getName());
		viewCards.setMnemonic('c');
		viewCards.setAccelerator(ctrlR);
		viewCards.setIcon(Icons.VIEW_CARDS);
		viewCards.setToolTipText("Cards");
		viewCards.addActionListener(new PerspectiveSwitcher(cardsPerspective, mainFrame));

		Perspective thumbnailPerspective = Perspectives.getThumbnailPerspective();
		JMenuItem viewThumbnails = new JMenuItem(thumbnailPerspective.getName());
		viewThumbnails.setMnemonic('t');
		viewThumbnails.setAccelerator(ctrlT);
		viewThumbnails.setIcon(Icons.VIEW_THUMBNAILS);
		viewThumbnails.setToolTipText("Thumbnails");
		viewThumbnails.addActionListener(new PerspectiveSwitcher(thumbnailPerspective, mainFrame));

		JMenuItem viewFullscreen = new JMenuItem("Fullscreen");
		viewFullscreen.setMnemonic('f');
		viewFullscreen.setAccelerator(altEnter);
		viewFullscreen.setIcon(Icons.FULLSCREEN);
		viewFullscreen.addActionListener(new FullScreenAction());

		JMenuItem viewJobManager = new JMenuItem("Show Job Manager");
		viewJobManager.setMnemonic('j');
		viewJobManager.setAccelerator(ctrlJ);
		viewJobManager.addActionListener(new ShowJobManagerAction());

		// Help menu
		JMenuItem helpAbout = new JMenuItem("About...");
		helpAbout.setMnemonic('a');
		helpAbout.addActionListener(new HelpAboutAction());

		// File menu
		file.add(fileImport);
		file.add(fileExport);
		file.add(filePrint);
		file.addSeparator();
		file.add(fileExit);

		// View menu
		view.add(viewShowAll);
		view.add(viewDuplicates);
		view.addSeparator();
		view.add(viewDetail);
		view.add(viewCards);
		view.add(viewThumbnails);
		view.add(viewFullscreen);
		view.addSeparator();
		view.add(viewJobManager);

		// Help menu

		// add create error - only development build
		if (getClass().getPackage().getImplementationVersion() == null) {
			JMenuItem helpError = new JMenuItem("Create Error");
			helpError.setMnemonic('c');
			helpError.addActionListener(new HelpErrorAction());
			help.add(helpError);
		}
		
		help.add(helpAbout);

		add(file);
		add(view);
		add(help);

		client.getViewControl().addViewFilterListener(new MenuViewListener());
	}

	private class ImageExitAction implements ActionListener {
		/**
		 * react on event
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent e) {
			client.initateShutdown();
		}
	}

	private class ImageImportAction implements ActionListener {
		/**
		 * react on event
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent e) {
			client.displayImportDialog();
		}
	}

	private class ImageExportAction implements ActionListener {
		/**
		 * react on event
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent e) {
			client.displayExportDialog(false);
		}
	}

	private class ShowJobManagerAction implements ActionListener {
		/**
		 * react on event
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent e) {
			client.displayJobManager();
		}
	}

	private class HelpAboutAction implements ActionListener {
		/**
		 * react on event
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent e) {
			client.displayAboutFrame();
		}
	}

	private class HelpErrorAction implements ActionListener {
		/**
		 * react on event
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent e) {
			throw new RuntimeException("This is a user generated exception");
		}
	}

	private class PrintAction implements ActionListener {
		/**
		 * react on event
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent e) {
			client.displayPrintDialog();
		}
	}

	private class ShowAllAction implements ActionListener {
		/**
		 * react on event
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent e) {
			client.getViewControl().clearFilter();
		}
	}

	private class DuplicatesAction implements ActionListener {
		/**
		 * react on event
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent e) {
			client.getViewControl().setFilterDuplicates(viewDuplicates.isSelected());
		}
	}

	private class FullScreenAction implements ActionListener {
		/**
		 * react on event
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent e) {
			client.showFullScreen();
		}
	}

	/**
	 * Small private class to encapsulate Perspective change action.
	 * 
	 * $Id: JimCatMenu.java 951 2007-06-18 20:47:40Z 07g1t1u3 $
	 * 
	 * @author Herbert
	 */
	private class PerspectiveSwitcher implements ActionListener {

		/**
		 * perspective to set
		 */
		private Perspective perspective;

		/**
		 * destination JimCatFrame
		 */
		private JimCatFrame frame;

		/**
		 * create a new PerspectiveSwitcher using those values
		 * 
		 * @param perspective
		 * @param frame
		 */
		public PerspectiveSwitcher(Perspective perspective, JimCatFrame frame) {
			this.perspective = perspective;
			this.frame = frame;
		}

		/**
		 * performes perspective exchange
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent e) {
			frame.setPerspective(perspective);
		}
	}

	private class MenuViewListener implements ViewFilterListener {
		/**
		 * react on filter changes - update duplicate filter status information hook
		 * 
		 * @see org.jimcat.gui.ViewFilterListener#filterChanges(org.jimcat.gui.ViewControl)
		 */
		public void filterChanges(ViewControl control) {
			viewDuplicates.setSelected(control.isFilterDuplicates());
		}
	}
}
