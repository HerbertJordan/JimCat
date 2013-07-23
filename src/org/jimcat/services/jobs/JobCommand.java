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

/**
 * A finite list of commands for jobhandling.
 * 
 * <ul>
 * <li>START - starting a job, external</li>
 * <li>ROLLBACK - changing to rollback mode, external or internal</li>
 * <li>SUSPEND - suspend job, external or internal</li>
 * <li>RESUME - bring job back to work, external</li>
 * <li>FAILURE - brings job to a failer state, internal</li>
 * <li>CANCEL - abort job imediatly, external or internal</li>
 * <li>FINISH_JOB - job is finished, internal</li>
 * <li>FINISH_ROLLBACK - rollback finished, internal</li>
 * </ul>
 * 
 * external / internal means source of command. External: other thread, e.g.
 * user, internal: this job
 * 
 * $Id: JobCommand.java 329 2007-04-18 13:01:15Z 07g1t1u1 $
 * 
 * @author Herbert
 */
public enum JobCommand {
	START, ROLLBACK, SUSPEND, RESUME, FAILURE, CANCEL, FINISHJOB, FINISHROLLBACK;
}
