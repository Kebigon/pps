package xyz.kebigon.pps.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Service;

@Service
public class PublicDatabaseService
{
	@Value("${database.public.url}")
	private String databaseUrl;

	private boolean migrate = true;

	public Connection getConnection() throws SQLException
	{
		final Connection connection = DriverManager.getConnection(databaseUrl);

		// On first connection, migrate the database using flyway if necessary
		if (migrate)
		{
			final SingleConnectionDataSource dataSource = new SingleConnectionDataSource(connection, true);

			final Flyway flyway = Flyway.configure().locations("db-public/migration").dataSource(dataSource).load();
			flyway.migrate();
			migrate = false;
		}

		return connection;
	}
}
