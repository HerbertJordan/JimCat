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

package org.jimcat.gui.hyperlink;

import java.io.IOException;

import org.jimcat.services.ServiceLocator;
import org.jimcat.services.failurefeedback.FailureDescription;

/**
 * This is a class, that has methods to invoke a browser.
 * 
 * 
 * $Id$
 * 
 * @author Michael
 */
public class BrowserStartup {

	/**
	 * 
	 * To invoke a Browser
	 * 
	 * @param url
	 * @return true if the browser could be invoked
	 */
	public static final boolean invokeBrowser(String url) {
		String osname = getOS();
		String s1 = null;
		try {
			if (osname != null && osname.startsWith("Windows")) {
				s1 = "rundll32 url.dll,FileProtocolHandler " + url;
				Runtime.getRuntime().exec(s1);
				return true;
			} else if (osname != null && osname.startsWith("Linux")) {
				s1 = "netscape -remote openURL(" + url + ")";
				Process process1 = Runtime.getRuntime().exec(s1);
				try {
					int i = process1.waitFor();
					if (i != 0) {
						s1 = "netscape " + url;
						Runtime.getRuntime().exec(s1);
					}
					return true;
				} catch (InterruptedException ex) {
					FailureDescription description = new FailureDescription(ex, "about dialog",
					        "Error bringing up browser, cmd='" + s1 + "'" + "\nCaught: " + ex);
					ServiceLocator.getFailureFeedbackService().reportFailure(description);
				}
			} else if (osname != null && osname.startsWith("Mac")) {
				s1 = "open " + url;
				Runtime.getRuntime().exec(s1);
				return true;
			}
		} catch (IOException ex) {
			FailureDescription description = new FailureDescription(ex, "about dialog",
			        "Could not invoke browser, command=" + s1 + "\nCaught: " + ex);
			ServiceLocator.getFailureFeedbackService().reportFailure(description);
		}
		return false;
	}

	/**
	 * 
	 * get the os name
	 * 
	 * @return the name of the operationg system as String
	 */
	private static String getOS() {
		String name = System.getProperty("os.name");
		return name;
	}

}
