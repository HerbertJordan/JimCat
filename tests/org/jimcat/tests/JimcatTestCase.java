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

package org.jimcat.tests;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import junit.framework.TestCase;

import org.jimcat.model.Image;
import org.jimcat.model.ImageMetadata;
import org.jimcat.persistence.RepositoryLocator;
import org.jimcat.persistence.RepositoryLocator.ConfigType;
import org.joda.time.DateTime;

/**
 * A common base class for jimcat tests.
 * 
 * 
 * Adds the feature of loading config values from a properties file.
 * 
 * 
 * $Id$
 * 
 * @author Christoph
 */
public class JimcatTestCase extends TestCase {

	private Properties properties;

	private static String prefix = "" + Math.random();

	public JimcatTestCase() {
		// for each new test file set new (clean) repositories
		resetRepositories();
	}

	protected void resetRepositories() {
		if (!RepositoryLocator.hasDependencies()) {
			RepositoryLocator.setConfigType(getConfig());
		} else {
			RepositoryLocator.resetDependencies();
		}
	}

	protected ConfigType getConfig() {
		return ConfigType.MOCK;
	}

	protected String getProperty(String key) {

		if (properties == null) {
			loadConfig();
		}

		String property = properties.getProperty(key);

		if (property == null) {
			throw new IllegalStateException("There is no property called '" + key + "'.");
		}

		return property;
	}

	protected Image createImage(String name) {
		Image image = new Image();
		image.setDescription(name);
		image.setTitle(name);

		DateTime creationDate = new DateTime();
		DateTime lastModifiedDate = new DateTime();
		File path = new File(prefix + "/" + name);
		ImageMetadata metadata = new ImageMetadata(path, 1, 1, 1, null, 1, creationDate, lastModifiedDate);

		image.setMetadata(metadata);

		return image;
	}

	private void loadConfig() {
		properties = new Properties();

		try {
			Class clazz = JimcatTestCase.class;
			InputStream is = clazz.getResourceAsStream("tests.properties");

			properties.load(is);
		} catch (NullPointerException e) {
			String msg = "Please copy the file 'tests.properties.template' to "
			        + "a file called 'tests.properties' and adjust it to your needs";

			throw new RuntimeException(msg, e);
		} catch (IOException e) {
			throw new RuntimeException("Error reading test config file", e);
		}
	}
}
