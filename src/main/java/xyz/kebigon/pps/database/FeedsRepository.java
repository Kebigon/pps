package xyz.kebigon.pps.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FeedsRepository extends AbstractRepository<Feed>
{
	private static final String FIND_ALL_BY_TAG_NAME_QUERY = "SELECT f.rowid, f.* FROM feed f, feed_tag ft, tag t WHERE t.name = ? AND t.rowid = ft.tag_id AND ft.feed_id = f.rowid";

	public FeedsRepository()
	{
		super("feed");
	}

	public List<Feed> findAllByTagName(Connection connection, String tagName)
	{
		return createTemplate(connection).query(FIND_ALL_BY_TAG_NAME_QUERY, this::mapToObject, tagName);
	}

	public int save(Connection connection, String name, String url)
	{
		final Map<String, String> inParameters = new HashMap<String, String>();
		inParameters.put("name", name);
		inParameters.put("url", url);
		return super.save(connection, inParameters);
	}

	public void associateFeedAndTag(Connection connection, int feedId, int tagId)
	{
		log.info("associateFeedAndTag {} {}", feedId, tagId);

		final Map<String, Object> inParameters = new HashMap<String, Object>();
		inParameters.put("feed_id", feedId);
		inParameters.put("tag_id", tagId);
		new SimpleJdbcInsert(new SingleConnectionDataSource(connection, true)).withTableName("feed_tag") //
				.execute(inParameters);
	}

	@Override
	public void deleteById(Connection connection, int id) throws SQLException
	{
		super.deleteById(connection, id);
		createTemplate(connection).update("DELETE FROM feed_tag WHERE feed_id = ?", id);
	}

	@Override
	protected Feed mapToObject(ResultSet rs, int rowNum) throws SQLException
	{
		final Feed feed = new Feed();
		feed.setId(rs.getInt("rowid"));
		feed.setName(rs.getString("name"));
		feed.setUrl(rs.getString("url"));
		return feed;
	}

	public void deleteAll(Connection connection)
	{
		createTemplate(connection).update("DELETE FROM feed_tag");
		createTemplate(connection).update("DELETE FROM feed");
	}
}
