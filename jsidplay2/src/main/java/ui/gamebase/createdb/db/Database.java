package ui.gamebase.createdb.db;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.hibernate.Session;

import ui.entities.PersistenceProperties;

public abstract class Database {
	private EntityManager em;

	public abstract String processType(String type, int i);

	public abstract String getCreateStmtLayout();

	public abstract boolean isPrimaryKey(String columnName);

	public abstract boolean isAutoIncrementSupported();

	public void connect(String driver, String jdbcURL) {
		em = Persistence.createEntityManagerFactory(
				PersistenceProperties.GAMEBASE_DS,
				new PersistenceProperties(driver, jdbcURL))
				.createEntityManager();
	}

	public boolean isNumeric(int type) {
		if (type == java.sql.Types.BIGINT || type == java.sql.Types.DECIMAL
				|| type == java.sql.Types.DOUBLE
				|| type == java.sql.Types.FLOAT
				|| type == java.sql.Types.INTEGER
				|| type == java.sql.Types.NUMERIC
				|| type == java.sql.Types.SMALLINT
				|| type == java.sql.Types.TINYINT)
			return true;
		else
			return false;

	}

	public String generateDrop(String table) {
		StringBuffer result = new StringBuffer();
		result.append("DROP TABLE ");
		result.append(table);
		return result.toString();
	}

	public String generateCreate(String table, Database target)
			throws SQLException {
		StringBuffer result = new StringBuffer();

		StringBuffer sql = new StringBuffer();

		sql.append("SELECT * FROM ");
		sql.append(table);
		ResultSet rs = executeQuery(sql.toString());
		ResultSetMetaData md = rs.getMetaData();

		result.append(target.getCreateStmtLayout());
		result.append(table);
		result.append(" ( ");

		for (int i = 1; i <= md.getColumnCount(); i++) {
			if (i != 1)
				result.append(',');
			result.append(md.getColumnName(i));
			result.append(' ');

			String type = processType(md.getColumnTypeName(i),
					md.getPrecision(i));
			result.append(type);

			if (type.indexOf("INT") == -1) {
				result.append('(');
				result.append(md.getPrecision(i));
				if (md.getScale(i) > 0) {
					result.append(',');
					result.append(md.getScale(i));
				}
				result.append(") ");
			} else
				result.append(' ');

			if (this.isNumeric(md.getColumnType(i))) {
				if (!md.isSigned(i))
					result.append("UNSIGNED ");
			}

			if (md.isNullable(i) == ResultSetMetaData.columnNoNulls)
				result.append("NOT NULL ");
			else
				result.append("NULL ");
			if (target.isAutoIncrementSupported() && md.isAutoIncrement(i))
				result.append(" auto_increment");
		}

		if (isPrimaryKey(md.getColumnName(1))) {
			result.append(',');
			result.append("PRIMARY KEY(");
			result.append(md.getColumnName(1));
			result.append(')');
		}

		result.append(" )");

		return result.toString();
	}

	public ResultSet executeQuery(final String sql) throws SQLException {
		Session session = em.unwrap(Session.class);
		return session.doReturningWork((connection) -> {
			Statement stmt = connection.createStatement();
			return stmt.executeQuery(sql);
		});
	}

	public void execute(final String sql) throws SQLException {
		Session session = em.unwrap(Session.class);
		session.doWork((connection) -> {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate(sql);
		});
	}

	public Collection<String> listTables() throws SQLException {
		Session session = em.unwrap(Session.class);
		return session.doReturningWork((connection) -> {
			Collection<String> result = new ArrayList<String>();
			DatabaseMetaData dbm = connection.getMetaData();
			ResultSet rs = dbm.getTables(null, null, "%",
					new String[] { "TABLE" });
			while (rs.next()) {
				String str = rs.getString("TABLE_NAME");
				result.add(str);
			}
			return result;
		});
	}

	public Collection<String> listColumns(final String table)
			throws SQLException {
		Session session = em.unwrap(Session.class);
		return session.doReturningWork((connection) -> {
			Collection<String> result = new ArrayList<String>();
			DatabaseMetaData dbm = connection.getMetaData();
			ResultSet rs = dbm.getColumns(null, null, table, null);
			while (rs.next()) {
				result.add(rs.getString("COLUMN_NAME"));
			}
			return result;
		});
	}

	public void copy(final Database source, final String table) {
		Session session = em.unwrap(Session.class);
		session.doWork((connection) -> {
			try {
				StringBuffer selectSQL = new StringBuffer();
				StringBuffer insertSQL = new StringBuffer();
				StringBuffer values = new StringBuffer();

				Collection<String> columns = source.listColumns(table);

				selectSQL.append("SELECT ");
				insertSQL.append("INSERT INTO ");
				insertSQL.append(table);
				insertSQL.append("(");

				boolean first = true;
				for (String column : columns) {
					if (!first) {
						selectSQL.append(",");
						insertSQL.append(",");
						values.append(",");
					} else
						first = false;

					selectSQL.append(column);
					insertSQL.append(column);
					values.append("?");
				}
				selectSQL.append(" FROM ");
				selectSQL.append(table);

				insertSQL.append(") VALUES (");
				insertSQL.append(values);
				insertSQL.append(")");

				PreparedStatement statement = connection
						.prepareStatement(insertSQL.toString());
				ResultSet rs = source.executeQuery(selectSQL.toString());

				int rows = 0;

				while (rs.next()) {
					rows++;
					for (int i = 1; i <= columns.size(); i++) {
						int type = rs.getMetaData().getColumnType(i);
						if (type == Types.INTEGER) {
							try {
								statement.setInt(i, rs.getInt(i));
							} catch (Exception e2) {
								System.err.println(e2.getMessage());
							}
						} else {
							try {
								statement.setString(i, rs.getString(i));
							} catch (Exception e) {
								// System.err.println(e.getMessage());
							}
						}
					}
					statement.executeUpdate();
					if (statement.getWarnings() != null) {
						System.err.println(statement.getWarnings());
					}
				}

				System.out.println("Copied " + rows + " rows.");
			} catch (SQLException e) {
				e.printStackTrace();
			}

		});
	}

	public void flush() {
		em.getTransaction().begin();
		em.getTransaction().commit();
	}

	public void close() {
		em.close();
		em.getEntityManagerFactory().close();
		// Workaround a bug, when tha database contents is not flushed until
		// System.exit()
		System.gc();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}