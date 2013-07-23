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

package org.jimcat.services.failurefeedback;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

/**
 * A encapsulation of information concerning an unsuspected problem within the
 * system.
 * 
 * This is the unit of information send to the central debugging center.
 * 
 * $Id$
 * 
 * @author Michael/Herbert
 */
public class FailureDescription {

	/**
	 * the emerged, unsuspected problem
	 */
	private Throwable cause;

	/**
	 * if this message is caused by an logging event
	 */
	private LoggingEvent logEvent;

	/**
	 * the name of the thread causing it - null if unknown
	 */
	private String threadName;

	/**
	 * a short message if context is known - null if unknown
	 */
	private String message;

	/**
	 * the version of the users vm
	 */
	private String vmVersion;

	/**
	 * the users operating system
	 */
	private String os;

	/**
	 * number of available processors
	 */
	private int availableProcessors;

	/**
	 * amoung of free memory
	 */
	private long freeMemory;

	/**
	 * amoung of total memory
	 */
	private long totalMemory;

	/**
	 * amoung of max memory
	 */
	private long maxMemory;

	/**
	 * the version of jimcat
	 */
	private String jimcatVersion;

	/**
	 * direct constructor requesting fields.
	 * 
	 * use this if you would like to describe a failure base on an exception
	 * 
	 * @param cause
	 * @param threadName
	 * @param message
	 */
	public FailureDescription(Throwable cause, String threadName, String message) {
		this(threadName, message);

		// set members
		this.cause = cause;
	}

	/**
	 * direct constructor requesting fields.
	 * 
	 * use this if you would like to report an error recived through log4j
	 * 
	 * @param logEvent
	 * @param message
	 */
	public FailureDescription(LoggingEvent logEvent, String message) {
		this(logEvent.getThreadName(), message);

		// set members
		this.logEvent = logEvent;
	}

	/**
	 * private default constructor gathering general infos
	 * @param threadName the thread causing the failure
	 * @param message the message that shall be displayed
	 */
	private FailureDescription(String threadName, String message) {
		// init members
		this.threadName = threadName;
		this.message = message;

		// gather general infos
		// vm and os (from system)
		vmVersion = System.getProperty("java.vm.version");
		os = System.getProperty("os.name") + " - " + System.getProperty("os.version");

		// runtime infos
		Runtime runtime = Runtime.getRuntime();
		availableProcessors = runtime.availableProcessors();
		freeMemory = runtime.freeMemory();
		totalMemory = runtime.totalMemory();
		maxMemory = runtime.maxMemory();

		// jimcat infos
		jimcatVersion = getClass().getPackage().getImplementationVersion();

	}

	/**
	 * @return the availableProcessors
	 */
	public int getAvailableProcessors() {
		return availableProcessors;
	}

	/**
	 * @return the cause
	 */
	public Throwable getCause() {
		return cause;
	}

	/**
	 * @return the freeMemory
	 */
	public long getFreeMemory() {
		return freeMemory;
	}

	/**
	 * @return the maxMemory
	 */
	public long getMaxMemory() {
		return maxMemory;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the os
	 */
	public String getOs() {
		return os;
	}

	/**
	 * @return the threadName
	 */
	public String getThreadName() {
		return threadName;
	}

	/**
	 * @return the totalMemory
	 */
	public long getTotalMemory() {
		return totalMemory;
	}

	/**
	 * @return the vmVersion
	 */
	public String getVmVersion() {
		return vmVersion;
	}

	/**
	 * @return the jimcatVersion
	 */
	public String getJimcatVersion() {
		return jimcatVersion;
	}

	/**
	 * generates a formated failure report
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// build up report
		StringBuffer result = new StringBuffer();

		// Header:
		String newLine = System.getProperty("line.separator");
		String space = "    ";
		result.append(newLine);
		result.append("##########").append(newLine);
		result.append(space).append("Error-Message:  ").append(message).append(newLine);
		result.append(space).append("VM-Version:     ").append(vmVersion).append(newLine);
		result.append(space).append("OS Version:     ").append(os).append(newLine);
		result.append(space).append("JimCat Version: ").append(jimcatVersion).append(newLine);
		result.append(space).append("Source-Thread:  ").append(threadName).append(newLine);
		result.append(space).append("CPU-count:      ").append(availableProcessors).append(newLine);
		result.append(space).append("Free Memory:    ").append(freeMemory).append(" Bytes").append(newLine);
		result.append(space).append("Total Memory:   ").append(totalMemory).append(" Bytes").append(newLine);
		result.append(space).append("Max Memory:     ").append(maxMemory).append(" Bytes").append(newLine);
		result.append(newLine);

		// add cause
		StringWriter buffer = new StringWriter();
		PrintWriter sink = new PrintWriter(buffer);
		if (cause != null) {
			cause.printStackTrace(sink);
			result.append(" Exception:").append(newLine);
		} else {
			result.append(" LogEntry: ").append(logEvent.getMessage()).append(newLine);
			ThrowableInformation throwableInformation = logEvent.getThrowableInformation();
			if (throwableInformation != null) {
				Throwable throwable = throwableInformation.getThrowable();
				throwable.printStackTrace(sink);
			}
		}
		result.append(buffer.toString());
		result.append(newLine);

		result.append("##########").append(newLine);

		return result.toString();
	}
}
