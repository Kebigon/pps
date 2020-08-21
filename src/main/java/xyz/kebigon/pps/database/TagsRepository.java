package xyz.kebigon.pps.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TagsRepository extends AbstractRepository<FeedTag>
{
	private static final String FIND_BY_NAME_QUERY = "SELECT rowid, * FROM tag WHERE name = ? LIMIT 1";
	private static final String FIND_BY_FEED_ID_QUERY = "SELECT t.rowid, t.* FROM tag t, feed_tag ft WHERE ft.feed_id = ? AND ft.tag_id = t.rowid";

	@Autowired
	private ColorPicker colorPicker;

	public TagsRepository()
	{
		super("tag");
	}

	public List<FeedTag> findByFeedId(Connection connection, int feedId)
	{
		return createTemplate(connection).query(FIND_BY_FEED_ID_QUERY, this::mapToObject, feedId);

	}

	public FeedTag findByName(Connection connection, String name) throws SQLException
	{
		return createTemplate(connection).queryForObject(FIND_BY_NAME_QUERY, this::mapToObject, name);
	}

	public int findOrCreate(Connection connection, String name) throws SQLException
	{
		try
		{
			return findByName(connection, name).getId();
		}
		catch (final IncorrectResultSizeDataAccessException e)
		{
			final int bgColor = colorPicker.pickBackgroundColor();
			final int fgColor = colorPicker.pickForegroundColor(bgColor);

			return save(connection, name, bgColor, fgColor);
		}
	}

	public int save(Connection connection, String name, int bgColor, int fgColor)
	{
		log.info("save  tag {} {} {}", name, bgColor, fgColor);

		final Map<String, Object> inParameters = new HashMap<String, Object>();
		inParameters.put("name", name);
		inParameters.put("background_color", bgColor);
		inParameters.put("foreground_color", fgColor);
		return super.save(connection, inParameters);
	}

	@Override
	protected FeedTag mapToObject(ResultSet rs, int rowNum) throws SQLException
	{
		return new FeedTag(rs.getInt("rowid"), rs.getString("name"), rs.getInt("background_color"), rs.getInt("foreground_color"));
	}
}
