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

package org.jimcat.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.RejectedExecutionException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.jimcat.gui.dialog.exportdialog.ExportDialog;
import org.jimcat.gui.dialog.failurefeedbackdialog.FailureFeedbackDialog;
import org.jimcat.gui.dialog.importdialog.ImportDialog;
import org.jimcat.gui.dialog.printdialog.PrintDialog;
import org.jimcat.gui.dialog.renamedialog.RenameDialog;
import org.jimcat.gui.frame.AboutDialog;
import org.jimcat.gui.frame.JimCatFrame;
import org.jimcat.gui.fullscreen.FullScreenView;
import org.jimcat.gui.imagepopup.ImagePopupMenu;
import org.jimcat.gui.jobmanager.JobFace;
import org.jimcat.gui.jobmanager.JobManagerDialog;
import org.jimcat.gui.smartlisteditor.SmartListEditor;
import org.jimcat.gui.splashscreen.SplashScreen;
import org.jimcat.model.Image;
import org.jimcat.model.SmartList;
import org.jimcat.services.JobOperations;
import org.jimcat.services.OperationsLocator;
import org.jimcat.services.SystemOperations;
import org.jimcat.services.configuration.Configuration;
import org.jimcat.services.instancecontrol.InstanceListener;
import org.jimcat.services.jobs.Job;
import org.jimcat.services.jobs.JobFailureDescription;
import org.jimcat.services.jobs.JobManager;
import org.jvnet.lafwidget.LafWidget;
import org.jvnet.lafwidget.utils.LafConstants.AnimationKind;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.OfficeSilver2007Skin;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.jgoodies.looks.plastic.theme.SkyBlue;

/**
 * This is the main class for the jimcat swing client.
 * 
 * It is the root of the compleate Component tree and proviedes some basic IO
 * methodes.
 * 
 * $Id: SwingClient.java 970 2007-06-19 18:39:26Z 07g1t1u3 $
 * 
 * @author Herbert
 */
public final class SwingClient {

	/**
	 * singelton Instance
	 */
	private static final SwingClient INSTANCE = new SwingClient();

	/**
	 * the default theme to be used
	 */
	private static final String DEFAULT_THEME = "substance";
	
	/**
	 * to prevent multible instanziation
	 */
	private boolean started = false;

	/**
	 * a reference to the main element
	 */
	private JimCatFrame mainFrame;

	/**
	 * a reference to the jobManager Dialog used by this GUI
	 */
	private JobManagerDialog jobManagerDialog;

	/**
	 * a reference to the about frame
	 */
	private JDialog aboutFrame;

	/**
	 * a reference to the fullscreen view;
	 */
	private FullScreenView fullScreenView;

	/**
	 * the importdialog used for import-setup
	 */
	private ImportDialog importDialog;

	/**
	 * the exportDialog used for export-setup
	 */
	private ExportDialog exportDialog;

	/**
	 * The Print Dialog
	 */
	private PrintDialog printDialog;

	/**
	 * The Rename Dialog
	 */
	private RenameDialog renameDialog;

	/**
	 * the smartList editor used for editing
	 */
	private SmartListEditor smartListEditor;

	/**
	 * the central filter control instance
	 */
	private ViewControl viewControl;

	/**
	 * a collection of extracted tag operations
	 */
	private TagControl tagControl;

	/**
	 * a collection of more complex album operations
	 */
	private AlbumControl albumControl;

	/**
	 * a collection of more complex smartlist operations
	 */
	private SmartListControl smartListControl;

	/**
	 * a collection of extracted image operations
	 */
	private ImageControl imageControl;

	/**
	 * set to true if the background thread has finished preloading the
	 * components
	 */
	private boolean startupCompleted = false;

	/**
	 * if startupCompleted == false a method has to wait until it gets a notify
	 * on this object
	 */
	private Object startupLock = new Object();

	/**
	 * initializes windows for later use
	 */
	private final BackgroundGuiLoader guiLoader = new BackgroundGuiLoader();

	/**
	 * singelton - getInstance()
	 * 
	 * @return an instance of the SwingClient
	 */
	public static SwingClient getInstance() {
		return INSTANCE;
	}

	/**
	 * a call to this Methode will cause the Swing GUI to create a new Frame and
	 * show it.
	 */
	public void startup() {
		// check if it hasn't been started yet
		if (started) {
			return;
		}

		SplashScreen.setProgressText("Loading Library");
		// create controles
		tagControl = new TagControl(this);
		imageControl = new ImageControl();
		albumControl = new AlbumControl(this);
		smartListControl = new SmartListControl(this);
		// create a new ViewControl
		viewControl = new ViewControl();

		started = true;

		SplashScreen.setProgressText("Creating GUI");
		// SplashScreen.fadeProgressBar();
		// Install LookAndFeel
		try {

			String theme = Configuration.getString("theme", DEFAULT_THEME);

			if (theme.equals("substance")) {
				UIManager.setLookAndFeel(new SubstanceLookAndFeel());
				SubstanceLookAndFeel.setSkin(new OfficeSilver2007Skin());
				UIManager.put(SubstanceLookAndFeel.NO_EXTRA_ELEMENTS, Boolean.TRUE);
				UIManager.put(LafWidget.ANIMATION_KIND, AnimationKind.NONE);
			} else if (theme.equals("jgoodies")) {
				PlasticLookAndFeel.setPlasticTheme(new SkyBlue());
				UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Build up Frame - in right Thread
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				startupProcess();
			}
		});
	}

	/**
	 * the actual startup process
	 */
	private void startupProcess() {
		// set default local for all components
		Locale.setDefault(Locale.ENGLISH);

		// build fullscreen - shouldn't be decorated
		fullScreenView = new FullScreenView();

		// Install Decorations
		if (Configuration.getString("theme", DEFAULT_THEME).equals("substance")) {
			String osName = System.getProperty("os.name").toLowerCase();
			if (!osName.startsWith("linux")) {
				JFrame.setDefaultLookAndFeelDecorated(true);
				JDialog.setDefaultLookAndFeelDecorated(true);
			}
		}

		// build mainFrame
		mainFrame = new JimCatFrame();

		// preload Image popup
		ImagePopupMenu.getInstance();

		// build JobManager
		JobOperations jobOps = OperationsLocator.getJobOperations();
		JobManager jobManager = jobOps.getJobManager();
		jobManagerDialog = new JobManagerDialog(jobManager);

		new Thread(guiLoader).start();

		// add InstanceListener
		SystemOperations sysOp = OperationsLocator.getSystemOperations();
		sysOp.addInstanceListener(new InstanceListener() {
			/**
			 * React on surpressed instance
			 * 
			 * @see org.jimcat.services.instancecontrol.InstanceListener#otherInstanceSurpressed()
			 */
			public void otherInstanceSurpressed() {
				// This will bring this Window to the front
				mainFrame.toFront();
			}
		});

		// add failure listener
		sysOp.addFailureFeedbackListener(new FailureFeedbackDialog(this));

		mainFrame.setVisible(true);
		SplashScreen.hideSplashScreen();
	}

	/**
	 * this will check the status and try to shutdown the system
	 */
	public void initateShutdown() {
		// check if there are still running jobs
		JobOperations jobOps = OperationsLocator.getJobOperations();
		List<Job> jobs = jobOps.getJobManager().getActiveJobs();
		if (jobs.size() > 0) {
			JOptionPane.showMessageDialog(mainFrame, "There are still jobs running.", "Can't shut down",
			        JOptionPane.ERROR_MESSAGE);
			displayJobManager();
			return;
		}

		guiLoader.kill();

		// TODO: integrate other shutdown checks

		// Performe shutdown
		// Shutdown gui
		shutdown();

		// Shutdown backend
		SystemOperations sysOp = OperationsLocator.getSystemOperations();
		sysOp.shutdown();
	}

	/**
	 * this will cause the SwingClient to shutdown
	 */
	private void shutdown() {
		// if not started, do nothing
		if (!started) {
			return;
		}
		started = false;

		if (EventQueue.isDispatchThread()) {
			// its the right thread
			shutdownProcess();
		} else {
			// enqueue in right Thread
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					shutdownProcess();
				}
			});
		}
	}

	/**
	 * This method blocks as long as the startup isn't complete
	 */
	private void blockWhileInitializing() {
		if (!startupCompleted) {
			synchronized (startupLock) {
				try {
					while (!startupCompleted) {
						startupLock.wait();
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	/**
	 * the actual gui-shutdown process
	 */
	private void shutdownProcess() {

		for (Window w : new Window[] { mainFrame, importDialog, exportDialog, smartListEditor, jobManagerDialog,
		        fullScreenView, aboutFrame }) {

			if (w == null) {
				continue;
			}

			w.setVisible(false);
			w.dispose();
		}
	}

	/**
	 * This methode provides access to the associated ViewControl
	 * 
	 * @return the associated ViewControl
	 */
	public ViewControl getViewControl() {
		if (viewControl == null) {
			viewControl = new ViewControl();
		}
		return viewControl;
	}

	/**
	 * Returns the central element containing TagOperations. All operations on
	 * tags should be performed through this Control.
	 * 
	 * @return the TagControl
	 */
	public TagControl getTagControl() {
		if (tagControl == null) {
			tagControl = new TagControl(this);
		}
		return tagControl;
	}

	/**
	 * Returns a reference to the up and running album control.
	 * 
	 * @return the AlbumControl
	 */
	public AlbumControl getAlbumControl() {
		return albumControl;
	}

	/**
	 * Returns a reference to the up and running smartlist control.
	 * 
	 * @return the SmartListControl
	 */
	public SmartListControl getSmartListControl() {
		return smartListControl;
	}

	/**
	 * @return the imageControl
	 */
	public ImageControl getImageControl() {
		if (imageControl == null) {
			imageControl = new ImageControl();
		}
		return imageControl;
	}

	/**
	 * return current top element visible
	 * 
	 * @return the current top Component
	 */
	public Component getCurrentTopWindow() {
		if (fullScreenView.isVisible()) {
			return fullScreenView;
		}
		return mainFrame;
	}

	/**
	 * the main frame of this GUI
	 * 
	 * @return the MainFrame
	 */
	public JimCatFrame getMainFrame() {
		return mainFrame;
	}

	/**
	 * this will display the jobmanger
	 */
	public void displayJobManager() {
		if (!jobManagerDialog.isVisible()) {
			// move to current screen
			Rectangle screen = mainFrame.getGraphicsConfiguration().getBounds();

			Dimension size = jobManagerDialog.getSize();

			int x = (screen.width - size.width) / 10 * 9 + screen.x;
			int y = (screen.height - size.height) / 4 * 3 + screen.y;
			jobManagerDialog.setLocation(x, y);

			// show job manager
			jobManagerDialog.setVisible(true);
		} else {
			jobManagerDialog.toFront();
		}
	}

	/**
	 * this will display the about frame
	 */
	public void displayAboutFrame() {
		blockWhileInitializing();

		if (!aboutFrame.isVisible()) {
			centerWindow(aboutFrame);
			aboutFrame.setVisible(true);
		}
	}

	/**
	 * call this to display import dialog
	 */
	public void displayImportDialog() {
		blockWhileInitializing();
		if (!importDialog.isVisible()) {
			centerWindow(importDialog);
			importDialog.clearFileList();
			importDialog.setVisible(true);
		}
	}

	/**
	 * call this to display the print dialog
	 */
	public void displayPrintDialog() {
		blockWhileInitializing();
		if (!printDialog.isVisible()) {
			centerWindow(printDialog);
			printDialog.updatePreviewImages();
			boolean selectedImages = getViewControl().getSelectedImages().size() != 0;
			printDialog.setSelectedImagesAvailable(selectedImages);
			printDialog.setVisible(true);
		}
	}

	/**
	 * call this to display export dialog
	 * 
	 * @param onlySelected
	 */
	public void displayExportDialog(boolean onlySelected) {
		blockWhileInitializing();
		if (!exportDialog.isVisible()) {
			centerWindow(exportDialog);
			exportDialog.clearFileList();
			exportDialog.setOnlySelection(onlySelected);
			exportDialog.setVisible(true);
		}
	}

	/**
	 * call this to display export dialog
	 */
	public void displayRenameDialog() {
		blockWhileInitializing();
		if (!renameDialog.isVisible()) {
			centerWindow(renameDialog);
			renameDialog.setSelectedImages(new ArrayList<Image>(getViewControl().getSelectedImages()));
			renameDialog.setVisible(true);
		}
	}

	/**
	 * call this to display the smartlist editor
	 * 
	 * @param list -
	 *            the smartlist to edit
	 */
	public void displaySmartListEditor(SmartList list) {
		blockWhileInitializing();
		if (!smartListEditor.isVisible()) {
			centerWindow(smartListEditor);
			smartListEditor.setSmartList(list);
			smartListEditor.setVisible(true);
		}
	}

	/**
	 * use this to start a new job.
	 * 
	 * it will display a new JobFace to control the running job.
	 * 
	 * @param job
	 */
	public void startJob(Job job) {
		// build up face
		JobFace face = new JobFace();
		face.setJob(job);

		// show jobmanager
		displayJobManager();

		// add job to jobmanager and start it
		JobOperations jobOps = OperationsLocator.getJobOperations();
		JobManager jobManager = jobOps.getJobManager();
		jobManager.excecuteJob(job);
	}

	/**
	 * Shows a Confirmation Dialog using JOptionPane
	 * 
	 * @param message
	 * @param title
	 * @param options
	 * @param typ
	 * @return the result of the dialog
	 * 
	 * @see JOptionPane#showConfirmDialog(java.awt.Component, Object, String,
	 *      int, int)
	 */
	public int showConfirmDialog(String message, String title, int options, int typ) {
		return JOptionPane.showConfirmDialog(getCurrentTopWindow(), message, title, options, typ);
	}

	/**
	 * Shows a Input Dialog using JOptionPane
	 * 
	 * @param message
	 * @param title
	 * @param typ
	 * @return the result of the input dialog
	 * 
	 * @see JOptionPane#showInputDialog(java.awt.Component, Object, String, int)
	 */
	public String showInputDialog(String message, String title, int typ) {
		return JOptionPane.showInputDialog(getCurrentTopWindow(), message, title, typ);
	}

	/**
	 * shows a simple message with a ok button
	 * 
	 * @param message
	 * @param title
	 * @param typ
	 * 
	 * @see JOptionPane#showMessageDialog(java.awt.Component, Object, String,
	 *      int)
	 */
	public void showMessage(String message, String title, int typ) {
		JOptionPane.showMessageDialog(getCurrentTopWindow(), message, title, typ);
	}

	/**
	 * this will show a JobFailure description and allowes the user to choose an
	 * option
	 * 
	 * @param description
	 */
	public void showJobFailure(JobFailureDescription description) {
		// show Failure description
		int result = JOptionPane.showOptionDialog(getCurrentTopWindow(), description.getDescription(),
		        "Failure during job execution", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null,
		        description.getOptions().toArray(), description.getRespond());

		// react on result
		if (result != JOptionPane.CLOSED_OPTION) {
			description.setRespond(description.getOptions().get(result));
		}
	}

	/**
	 * switch to fullscreen
	 */
	public void showFullScreen() {
		if (fullScreenView.isVisible()) {
			return;
		}
		centerWindow(fullScreenView);
		fullScreenView.display();
	}

	/**
	 * close fullscreen
	 */
	public void hideFullScreen() {
		fullScreenView.closeFullScreenView();
	}

	/**
	 * this will center the given window on the screen currently occupied by the
	 * main frame
	 * 
	 * @param window
	 */
	private void centerWindow(Window window) {
		Rectangle screen = mainFrame.getGraphicsConfiguration().getBounds();

		Dimension size = window.getSize();

		int x = (screen.width - size.width) / 2 + screen.x;
		int y = (screen.height - size.height) / 2 + screen.y;
		window.setLocation(x, y);
	}

	/**
	 * initialize components for faster access in the future done in a
	 * background thread so the startup feels faster
	 */
	private class BackgroundGuiLoader implements Runnable {

		private boolean kill = false;

		private RuntimeException ex = new RuntimeException();

		public void run() {
			synchronized (startupLock) {

				try {
					// build ImportDiaolg
					importDialog = new ImportDialog(SwingClient.this, mainFrame);
					checkKill();

					// build ExportDialog
					exportDialog = new ExportDialog(SwingClient.this, mainFrame);
					checkKill();

					// build RenameDialog
					renameDialog = new RenameDialog(SwingClient.this, mainFrame);
					checkKill();

					// build PrintDialog
					printDialog = new PrintDialog(SwingClient.this, mainFrame);
					checkKill();

					// build SmartList editor
					smartListEditor = new SmartListEditor(mainFrame);
					checkKill();

					// build about dialog
					aboutFrame = new AboutDialog(mainFrame);

					startupCompleted = true;
					startupLock.notifyAll();
				} catch (RejectedExecutionException e) {
					// sometimes thrown in internal swing classes if there's
					// a shutdown in progress. we ignore it.
				} catch (RuntimeException e) {
					if (e == ex) {
						// aborted
					} else {
						throw e; // unexpected exception, rethrow it
					}
				}
			}
		}

		private void checkKill() {
			if (!kill) {
				return;
			}

			startupCompleted = true;
			startupLock.notifyAll();
			throw ex;
		}

		public void kill() {
			this.kill = true;
		}
	}

}
