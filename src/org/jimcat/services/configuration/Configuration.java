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

package org.jimcat.services.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads and stores settings from a config file.
 * 
 * 
 * $Id: Configuration.java 943 2007-06-16 10:33:00Z 07g1t1u2 $
 * 
 * @author Christoph
 */
public class Configuration {

	/**
	 * the filename of the configuration File
	 */

	private static String userHome = System.getProperty("user.home", ".");

	private static String configDir = userHome + "/.jimcat/";

	private static String config = configDir + "config";

	private static Properties properties = new Properties();

	private static File file = new File(config);

	static {
		try {
			if (!file.exists()) {
				createConfigFile();
			}

			InputStream in = new FileInputStream(file);
			properties.load(in);
			in.close();
		} catch (IOException e) {
			throw new IllegalStateException("Unable to open config");
		}
	}

	private static void createConfigFile() throws IOException {
		File dir = new File(configDir);

		if (!dir.exists()) {
			dir.mkdirs();
		}

		file.createNewFile();
	}

	/**
	 * 
	 * get the directory of the configuration
	 * 
	 * @return the directory of the configuration as String
	 */
	public static String getConfigBaseDirectory() {
		return configDir;
	}

	/**
	 * 
	 * Get a property as a String
	 * 
	 * @param key
	 * @return the property specified by key in a String representation
	 */
	public static String getString(String key) {
		String value = (String) properties.get(key);

		if (value == null) {
			throw new ConfigurationValueNotFoundException(key);
		}

		return value;
	}

	/**
	 * 
	 * Get a property as a String, if result would be null, result is
	 * defaultValue
	 * 
	 * @param key
	 * @param defaultValue
	 * @return the property specified by key in a String representation
	 */
	public static String getString(String key, String defaultValue) {
		String value = (String) properties.get(key);
		return value != null ? value : defaultValue;
	}

	/**
	 * 
	 * get the property specified by key as an int
	 * 
	 * @param key
	 * @return the property specified by key as an int
	 */
	public static int getInt(String key) {
		try {
			return Integer.parseInt(getString(key));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * 
	 * get the property specified by key as an int if key has no corresponding
	 * value this method returns the defaultValue.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return get the property specified by key as an int
	 */
	public static int getInt(String key, int defaultValue) {
		try {
			return Integer.parseInt(getString(key));
		} catch (ConfigurationValueNotFoundException e) {
			return defaultValue;
		} catch (NumberFormatException e) {
			throw new IllegalStateException();
		}
	}

	/**
	 * 
	 * set a property with given key and value
	 * @param key
	 * @param value
	 */
	public static void set(String key, String value) {
		properties.put(key, value);
		save();
	}

	private static void save() {
		try {
			FileOutputStream os = new FileOutputStream(file);
			properties.store(os, null);
			os.close();
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("Couldn't find config file");
		} catch (IOException e) {
			throw new IllegalStateException("Couldn't write to config file");
		}
	}

	/**
	 * 
	 * set a property with given key and value
	 * @param key
	 * @param value
	 */
	public static void set(String key, int value) {
		set(key, Integer.toString(value));
	}

	/**
	 * 
	 * remove a property with given key
	 * @param key
	 */
	public static void remove(String key) {
		properties.remove(key);
		save();
	}

	/**
	 * The Exception that is thrown when the key is not found in the
	 * configuration.
	 * 
	 * 
	 * $Id: Configuration.java 943 2007-06-16 10:33:00Z 07g1t1u2 $
	 * 
	 * @author Christoph
	 */
	public static class ConfigurationValueNotFoundException extends RuntimeException {

		/**
		 * 
		 * construct a new exception with given key as parameter
		 * @param key
		 */
		public ConfigurationValueNotFoundException(String key) {
			super("Configuration value '" + key + "' not found.");
		}
	}
}
