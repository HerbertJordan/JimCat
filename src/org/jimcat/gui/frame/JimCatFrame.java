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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.ImageIcon;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.JXFrame;
import org.jimcat.gui.SwingClient;
import org.jimcat.gui.perspective.AbstractPerspective;
import org.jimcat.gui.perspective.Perspectives;
import org.jimcat.gui.perspective.Perspectives.Perspective;
import org.jimcat.gui.sidebar.SideBar;
import org.jimcat.gui.toolbar.ToolBar;

/**
 * MainFrame for the JimCat Swing Client.
 * 
 * This container includes all elements visible within the main JimCat Window.
 * It also manges the perspective exchange.
 * 
 * $Id: JimCatFrame.java 981 2007-06-21 19:47:58Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public class JimCatFrame extends JXFrame {

	/**
	 * list of listeners
	 */
	private List<JimCatFrameListener> listeners = new CopyOnWriteArrayList<JimCatFrameListener>();

	/**
	 * main layout element to access by changing perspectiv
	 */
	private JSplitPane mainSplitPane;

	private Container rightContainer;

	private AbstractPerspective currentPerspective;

	/**
	 * default constructor - only constructor
	 */
	public JimCatFrame() {
		super("JimCat");
		initComponents();
	}

	/**
	 * build up JimCatFrame GUI sturucture
	 */
	private void initComponents() {

		// Basic Settings
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setIconImage(new ImageIcon(JimCatFrame.class.getResource("jimcat.png")).getImage());

		// Register WindowEvent Handler
		addWindowListener(new WindowAdapter() {
			@Override
			@SuppressWarnings("unused")
			public void windowClosing(WindowEvent e) {
				SwingClient.getInstance().initateShutdown();
			}
		});

		// Menu
		setJMenuBar(new JimCatMenu(this));

		// Main Panel
		mainSplitPane = new JSplitPane();
		rightContainer = new Container();
		rightContainer.setLayout(new BorderLayout());
		// mainSplitPane.setDividerLocation(250);
		// mainSplitPane.setOneTouchExpandable(false);
		mainSplitPane.setRightComponent(rightContainer);

		rightContainer.add(new CurrentFilterViewer(), BorderLayout.NORTH);

		// ToolBar
		SwingClient client = SwingClient.getInstance();
		ToolBar toolbar = new ToolBar(this, client.getViewControl(), client.getImageControl());

		// SideBar
		mainSplitPane.setLeftComponent(new SideBar());
		setPerspective(Perspectives.getThumbnailPerspective());

		// assemble
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(toolbar, BorderLayout.NORTH);
		contentPane.add(mainSplitPane, BorderLayout.CENTER);

		// place ...
		Dimension size = new Dimension(1024, 595);
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(size);
		setLocation((screensize.width - size.width) / 2, (screensize.height - size.height) / 2);
	}

	/**
	 * this will exchange the current perspective
	 * 
	 * @param perspective
	 */
	public void setPerspective(Perspective perspective) {
		AbstractPerspective newPerspective = perspective.getPerspective();

		if (currentPerspective == newPerspective) {
			return;
		}

		// disable old perspective
		if (currentPerspective != null) {
			currentPerspective.setActive(false);
			rightContainer.remove(currentPerspective);
		}

		// enable new perspective
		newPerspective.setActive(true);
		rightContainer.add(newPerspective, BorderLayout.CENTER);

		currentPerspective = newPerspective;

		mainSplitPane.setRightComponent(rightContainer);

		// inform listener
		for (JimCatFrameListener listener : listeners) {
			listener.perspectiveExchanged(perspective);
		}
	}

	/**
	 * adds a new JimCatFrameListener
	 * 
	 * @param listener
	 */
	public void addJimCatFrameListener(JimCatFrameListener listener) {
		listeners.add(listener);
	}

	/**
	 * removes a JimCatFrameListener
	 * 
	 * @param listener
	 */
	public void removeJimCatFrameListener(JimCatFrameListener listener) {
		listeners.remove(listener);
	}
}
