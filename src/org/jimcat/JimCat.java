package org.jimcat;

import org.jimcat.gui.SwingClient;
import org.jimcat.gui.splashscreen.SplashScreen;
import org.jimcat.persistence.RepositoryLocator;
import org.jimcat.persistence.RepositoryLocator.ConfigType;
import org.jimcat.services.failurefeedback.FailureFeedbackService;
import org.jimcat.services.instancecontrol.InstanceControl;

/**
 * This class only contains the Main Method and therefore performs the startup
 * process.
 * 
 * $Id: JimCat.java 970 2007-06-19 18:39:26Z 07g1t1u3 $
 * 
 * @author Herbert
 */
public class JimCat {

	/**
	 * The main method containing startup sequence
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// startup failure feedback service
		FailureFeedbackService.startUp();

		// start instance control
		InstanceControl control = InstanceControl.getInstance();
		if (!control.isOnlyInstance()) {
			System.err.println("There is another instance running");
			return;
		}

		// show Splash Screen
		SplashScreen.showSplash();
		SplashScreen.setProgressText("Starting up");
		// SplashScreen.setProgressInterminate(true);

		// set dependencies
		RepositoryLocator.setConfigType(ConfigType.CONFIG);

		// start gui
		SwingClient client = SwingClient.getInstance();
		client.startup();
	}
}
