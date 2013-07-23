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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;

/**
 * Common base class for all DB4O DAOs
 * 
 * $Id$
 * 
 * @author Christoph
 */
public class Db4oDao {

	protected <T> Set<T> get(T template) {
		ObjectSet<T> objectSet = db().get(template);
		return toSet(objectSet);
	}

	protected <T> Set<T> get(Predicate<T> predicate) {
		ObjectSet<T> objectSet = db().query(predicate);
		return toSet(objectSet);
	}

	private <T> Set<T> toSet(ObjectSet<T> objectSet) {
		return new HashSet<T>(objectSet);
	}

	private ObjectContainer db() {
		return Db4oUtil.getDatabase();
	}

	protected <T> Set<T> getAll(final Class<T> t) {
		return toSet(db().query(t));
	}

	protected void set(final Object o) {
		Db4oUtil.execute(new Runnable() {
			public void run() {
				db().set(o);
			}
		});
	}

	protected void delete(final Object o) {
		Db4oUtil.execute(new Runnable() {
			public void run() {
				db().delete(o);
			}
		});
	}

	protected void set(final Collection<?> c) {
		Db4oUtil.execute(new Runnable() {
			public void run() {
				for (Object o : c) {
					db().set(o);
				}
			}
		});
	}

	protected void delete(final Collection<?> c) {
		Db4oUtil.execute(new Runnable() {
			public void run() {
				for (Object o : c) {
					db().delete(o);
				}
			}
		});
	}
}
