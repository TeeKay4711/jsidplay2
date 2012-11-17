package applet.entities;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;

public class PersistenceUtil extends HashMap<String, String> {

	public static final String CONFIG_DS = "jsidplay2-ds";
	public static final String COLLECTION_DS = CONFIG_DS;
	public static final String GAMEBASE_DS = CONFIG_DS;

	public PersistenceUtil(File databaseFile) {
		put("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
		put("hibernate.connection.url",
				"jdbc:hsqldb:file:" + databaseFile.getAbsolutePath()
						+ ";shutdown=true");
		common();
	}

	public PersistenceUtil(String driver, String jdbcURL) {
		put("hibernate.connection.driver_class", driver);
		put("hibernate.connection.url", jdbcURL);
		common();
	}

	private void common() {
		put("hibernate.connection.username", "");
		put("hibernate.connection.password", "");
		put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
		put("hibernate.hbm2ddl.auto", "update");
		// put("hibernate.show_sql", "true");
		// put("hibernate.format_sql", "true");
	}

	public static void databaseDeleteOnExit(final File dbFile) {
		File parent = dbFile.getParentFile();
		File[] dbFiles = parent.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				if (file.isFile()
						&& file.getName().startsWith(dbFile.getName() + ".")) {
					return true;
				}
				return false;
			}
		});
		for (File file : dbFiles) {
			file.deleteOnExit();
		}
	}

}
