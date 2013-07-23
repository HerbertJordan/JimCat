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

package org.jimcat.services.jobs;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * This class is used to describe a problem requiring user interaction during
 * the execution of a job.
 * 
 * $Id: JobFailureDescription.java 935 2007-06-15 09:21:09Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public class JobFailureDescription {

	/**
	 * an associated Exception - might be null
	 */
	private Exception cause;

	/**
	 * a short description
	 */
	private String description;

	/**
	 * An array of possible reactions
	 */
	private List<JobFailureOption> options;

	/**
	 * the option choosen by the user
	 */
	private JobFailureOption respond;

	/**
	 * empty constructor. Setup failure through setter.
	 */
	public JobFailureDescription() {
		this(null,null,null,null);
	}
	
	/**
	 * constructor giving all fields
	 * 
	 * @param cause -
	 *            the cause or null
	 * @param description -
	 *            a short description
	 * @param options -
	 *            a list of supported options
	 * @param defaultRespond the default respond
	 */
	public JobFailureDescription(Exception cause, String description, List<JobFailureOption> options,
	        JobFailureOption defaultRespond) {
		this.cause = cause;
		this.description = description;
		setOptions(options);
		setRespond(defaultRespond);
	}

	/**
	 * @return the cause
	 */
	public Exception getCause() {
		return cause;
	}

	/**
	 * @param cause
	 *            the cause to set
	 */
	public void setCause(Exception cause) {
		this.cause = cause;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the list of supported options
	 */
	public List<JobFailureOption> getOptions() {
		return options;
	}

	/**
     * @param options the options to set
     */
    public void setOptions(List<JobFailureOption> options) {
    	if (options!=null) {
    		this.options = Collections.unmodifiableList(options);
    	} else {
    		this.options = null;
    	}
    }

	/**
	 * the selected responde or null if there hasn't been a selection.
	 * 
	 * @return the respond
	 */
	public JobFailureOption getRespond() {
		return respond;
	}

	/**
	 * set the selected response
	 * 
	 * @param respond
	 *            the respond to set
	 * 
	 * @throws IllegalArgumentException -
	 *             if the response isn't within the supported options
	 */
	public void setRespond(JobFailureOption respond) throws IllegalArgumentException {
		//if new respond is null, just set it
		if (respond==null) {
			this.respond = null;
			return;
		}
		//else check if this options is supported
		if (!options.contains(respond)) {
			throw new IllegalArgumentException("option not suppored by JobFailure");
		}
		this.respond = respond;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof JobFailureDescription)) {
			return false;
		}
		JobFailureDescription other = (JobFailureDescription) obj;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(other.cause, this.cause);
		eb.append(other.description, this.description);
		eb.append(other.options, this.options);
		eb.append(other.respond, this.respond);
		return eb.isEquals();
	}
}
