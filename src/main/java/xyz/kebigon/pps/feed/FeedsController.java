package xyz.kebigon.pps.feed;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.rometools.rome.feed.synd.SyndEntry;

import xyz.kebigon.pps.database.DatabaseService;
import xyz.kebigon.pps.database.Feed;
import xyz.kebigon.pps.database.FeedTag;
import xyz.kebigon.pps.database.FeedsRepository;
import xyz.kebigon.pps.database.TagsRepository;

@Controller
@RequestMapping("/rss")
public class FeedsController
{
	@Autowired
	private DatabaseService database;
	@Autowired
	private FeedsRepository feedsRepository;
	@Autowired
	private TagsRepository tagsRepository;

	@Autowired
	private FeedClient client;

	private Collection<Feed> getFeeds(Connection connection) throws SQLException
	{
		final List<Feed> feeds = feedsRepository.findAll(connection);
		for (final Feed feed : feeds)
			feed.setTags(tagsRepository.findByFeedId(connection, feed.getId()));
		return feeds;
	}

	private Collection<Feed> getFeeds(String tag, Connection connection) throws SQLException
	{
		final List<Feed> feeds = feedsRepository.findAllByTagName(connection, tag);
		for (final Feed feed : feeds)
			feed.setTags(tagsRepository.findByFeedId(connection, feed.getId()));
		return feeds;
	}

	@GetMapping
	public ModelAndView RSSFeeds(@RequestParam(required = false) String tag) throws SQLException
	{
		try (final Connection connection = database.getConnection())
		{
			final Collection<Feed> feeds = tag != null ? getFeeds(tag, connection) : getFeeds(connection);
			final SyndEntry[] entries = feeds.stream().flatMap(feed -> client.getFeed(feed.getUrl(), feed.getTags()).getEntries().stream())
					.sorted(SyndEntryComparator.get()).toArray(size -> new SyndEntry[size]);

			final Collection<FeedTag> tags = tagsRepository.findAll(connection);

			final Map<String, Object> model = new HashMap<String, Object>();
			model.put("entries", entries);
			model.put("tags", tags);
			return new ModelAndView("feeds", model);
		}
	}
}
