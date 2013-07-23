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

package org.jimcat.persistence.db4o;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.jimcat.model.Album;
import org.jimcat.model.Image;
import org.jimcat.model.tag.TagGroup;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.config.Configuration;
import com.db4o.config.ObjectClass;
import com.db4o.config.ObjectField;
import com.db4o.config.TSerializable;

/**
 * DB4O Utility Class
 * 
 * 
 * $Id$
 * 
 * @author Christoph
 */
public class Db4oUtil {

	private static ExecutorService executor = Executors.newSingleThreadExecutor(new InternalThreadFactory());

	private static ObjectContainer db;

	private static String userHome = System.getProperty("user.home", ".");

	private static String configDir = userHome + "/.jimcat/";

	static {
		Configuration config = Db4o.configure();

		configJodaTime(config);

		config.objectClass(TagGroup.class).cascadeOnUpdate(true);
		config.objectClass(Album.class).cascadeOnUpdate(true);
		config.objectClass(Image.class).cascadeOnUpdate(true);

		File configDirFile = new File(configDir);
		if (!configDirFile.exists()) {
			configDirFile.mkdir();
		}

		db = Db4o.openFile(configDirFile + "/jimcat.yap");
	}

	private static void configJodaTime(Configuration config) {
		ObjectClass oc;

		Class clazz = org.joda.time.base.BaseDateTime.class;
		oc = config.objectClass(clazz);
		oc.updateDepth(0);
		oc.maximumActivationDepth(Integer.MAX_VALUE);
		oc.minimumActivationDepth(Integer.MAX_VALUE);
		oc.cascadeOnActivate(true);
		oc.cascadeOnDelete(false);
		oc.cascadeOnUpdate(false);

		ObjectField of = oc.objectField("iMillis");
		of.indexed(true);

		of = oc.objectField("iChronology");
		of.indexed(false);
		of.queryEvaluation(false);
		of.cascadeOnDelete(false);

		clazz = org.joda.time.DateTime.class;
		oc = config.objectClass(clazz);
		oc.updateDepth(0);
		oc.maximumActivationDepth(Integer.MAX_VALUE);
		oc.minimumActivationDepth(Integer.MAX_VALUE);
		oc.cascadeOnActivate(true);
		oc.cascadeOnDelete(false);
		oc.cascadeOnUpdate(false);

		clazz = org.joda.time.chrono.ISOChronology.class;
		oc = config.objectClass(clazz);
		oc.updateDepth(0);
		oc.translate(new TSerializable());
		oc.maximumActivationDepth(Integer.MAX_VALUE);
		oc.minimumActivationDepth(Integer.MAX_VALUE);
		oc.cascadeOnActivate(true);
		oc.cascadeOnDelete(false);
		oc.cascadeOnUpdate(false);

		clazz = org.joda.time.chrono.ZonedChronology.class;
		oc = config.objectClass(clazz);
		oc.updateDepth(0);
		oc.translate(new TSerializable());
		oc.maximumActivationDepth(Integer.MAX_VALUE);
		oc.minimumActivationDepth(Integer.MAX_VALUE);
		oc.cascadeOnActivate(true);
		oc.cascadeOnDelete(false);
		oc.cascadeOnUpdate(false);

		clazz = org.joda.time.chrono.GregorianChronology.class;
		oc = config.objectClass(clazz);
		oc.updateDepth(0);
		oc.translate(new TSerializable());
		oc.maximumActivationDepth(Integer.MAX_VALUE);
		oc.minimumActivationDepth(Integer.MAX_VALUE);
		oc.cascadeOnActivate(true);
		oc.cascadeOnDelete(false);
		oc.cascadeOnUpdate(false);

		clazz = org.joda.time.tz.CachedDateTimeZone.class;
		oc = config.objectClass(clazz);
		oc.translate(new TSerializable());
	}

	/**
	 * 
	 * @return the database as ObjectContainer
	 */
	public static ObjectContainer getDatabase() {
		return db;
	}

	/**
	 * 
	 * shutdown the database
	 */
	public static void shutdown() {
		executor.shutdown();
		db.close();
	}

	/**
	 * Execute a task in the executor
	 * 
	 * @param r
	 */
	public static void execute(Runnable r) {
		executor.execute(r);
	}

	private static class InternalThreadFactory implements ThreadFactory {

		/**
		 * Create a new thread with the MIN priority
		 * 
		 * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
		 */
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r, "db4o executor");
			t.setPriority(Thread.MIN_PRIORITY);
			return t;
		}

	}
}
