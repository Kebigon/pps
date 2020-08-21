package xyz.kebigon.pps.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public abstract class AbstractRepository<T>
{
	private static final String FIND_ALL_QUERY_PATTERN = "SELECT rowid, * FROM %1$s";
	private static final String FIND_BY_ID_QUERY_PATTERN = "SELECT rowid, * FROM %1$s WHERE rowid = ? LIMIT 1";
	private static final String DELETE_BY_ID_QUERY_PATTERN = "DELETE FROM %1$s WHERE rowid = ?";

	private final String tableName;
	private final String findAllQuery;
	private final String findByIdQuery;
	private final String deleteByIdQuery;

	protected AbstractRepository(String tableName)
	{
		this.tableName = tableName;
		this.findAllQuery = String.format(FIND_ALL_QUERY_PATTERN, tableName);
		this.findByIdQuery = String.format(FIND_BY_ID_QUERY_PATTERN, tableName);
		this.deleteByIdQuery = String.format(DELETE_BY_ID_QUERY_PATTERN, tableName);
	}

	/*
	 * JdbcTemplate & SimpleJdbcInsert creation utils
	 */

	protected JdbcTemplate createTemplate(Connection connection)
	{
		return new JdbcTemplate(new SingleConnectionDataSource(connection, true));
	}

	protected SimpleJdbcInsert createInsert(Connection connection)
	{
		return new SimpleJdbcInsert(new SingleConnectionDataSource(connection, true)).withTableName(tableName).usingGeneratedKeyColumns("rowid");
	}

	/*
	 * Queries
	 */

	public List<T> findAll(Connection connection) throws SQLException
	{
		return createTemplate(connection).query(findAllQuery, this::mapToObject);
	}

	public T findById(Connection connection, int id) throws SQLException
	{
		return createTemplate(connection).queryForObject(findByIdQuery, this::mapToObject, id);
	}

	public void deleteById(Connection connection, int id) throws SQLException
	{
		createTemplate(connection).update(deleteByIdQuery, id);
	}

	protected int save(Connection connection, Map<String, ?> inParameters)
	{
		return (int) createInsert(connection).executeAndReturnKey(inParameters);
	}

	protected abstract T mapToObject(ResultSet rs, int rowNum) throws SQLException;
}
