package org.jimcat.services.failurefeedback;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * A listener waiting for failure reports and storing them to a log file.
 * 
 * @author Michael Handler/Herbert Jordan
 */
public class LogFileFailureListener implements FailureFeedbackListener {

	/**
	 * the log file
	 */
	private File logFile;

	/**
	 * create a new log file failure listener
	 * 
	 * @param logFileName
	 *            the file to be used
	 */
	public LogFileFailureListener(String logFileName) {
		logFile = new File(logFileName);
	}

	/**
	 * @param failure
	 * @see org.jimcat.services.failurefeedback.FailureFeedbackListener#failureEmerged(org.jimcat.services.failurefeedback.FailureDescription)
	 */
	public void failureEmerged(FailureDescription failure) {
		// store failure to file
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(logFile, true));
			Writer writer = new OutputStreamWriter(out);
			writer.append("\n");
			writer.append(failure.toString());
			writer.flush();
		} catch (Exception e) {
			System.out.println("WARNING: Unable to log failure!");
		} finally {
			// close quietly
			try {
				if (out != null)
					out.close();
			} catch (Exception e) {
				// ignore
			}
		}
	}
}
