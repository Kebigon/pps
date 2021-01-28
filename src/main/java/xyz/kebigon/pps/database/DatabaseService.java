package xyz.kebigon.pps.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Service;

import xyz.kebigon.pps.InvalidEncryptionKeyException;

@Service
public class DatabaseService
{
	@Value("${database.private.url}")
	private String databaseUrl;

	private String databaseKey = null;
	private boolean migrate = true;

	public Connection getConnection() throws SQLException
	{
		if (databaseKey == null)
			throw new InvalidEncryptionKeyException();
		return getConnection(databaseKey);
	}

	private Connection getConnection(String key)
	{
		Connection connection;
		try
		{
			connection = DriverManager.getConnection(databaseUrl + key);
		}
		catch (final SQLException e)
		{
			throw new InvalidEncryptionKeyException();
		}

		// On first connection, migrate the database using flyway if necessary
		if (migrate)
		{
			final SingleConnectionDataSource dataSource = new SingleConnectionDataSource(connection, true);

			final Flyway flyway = Flyway.configure().dataSource(dataSource).load();
			flyway.migrate();
			migrate = false;
		}

		return connection;
	}

	public void setDatabaseKey(String databaseKey)
	{
		this.databaseKey = databaseKey;
	}

	public boolean checkDatabaseKey(String key)
	{
		getConnection(key);
		return true;
	}
}
