package xyz.kebigon.pps.feed;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import xyz.kebigon.pps.database.DatabaseService;
import xyz.kebigon.pps.database.Feed;
import xyz.kebigon.pps.database.FeedTag;
import xyz.kebigon.pps.database.FeedsRepository;
import xyz.kebigon.pps.database.TagsRepository;

@Controller
@RequestMapping("/rss/edit")
public class EditFeedsController
{
	@Autowired
	private DatabaseService database;
	@Autowired
	private FeedsRepository feedsRepository;
	@Autowired
	private TagsRepository tagsRepository;
	@Autowired
	private FeedClient feedClient;

	@GetMapping
	public ModelAndView editFeeds() throws SQLException
	{
		final Map<String, Object> model = new HashMap<String, Object>();

		try (Connection connection = database.getConnection())
		{
			final List<Feed> feeds = feedsRepository.findAll(connection);
			for (final Feed feed : feeds)
				feed.setTags(tagsRepository.findByFeedId(connection, feed.getId()));
			model.put("feeds", feeds);
		}

		return new ModelAndView("edit-feeds", model);
	}

	@PostMapping("/add")
	public RedirectView addRSSFeed(@RequestParam String url, @RequestParam String tags) throws SQLException
	{
		final String name = feedClient.getFeed(url, null).getTitle();

		try (Connection connection = database.getConnection())
		{
			final int feedId = feedsRepository.save(connection, name, url);

			for (final String tag : tags.split(","))
			{
				final int tagId = tagsRepository.findOrCreate(connection, tag.trim());

				feedsRepository.associateFeedAndTag(connection, feedId, tagId);
			}
		}

		return new RedirectView("/", true);
	}

	@DeleteMapping
	public RedirectView deleteAll() throws SQLException
	{
		try (Connection connection = database.getConnection())
		{
			feedsRepository.deleteAll(connection);
		}

		return new RedirectView("/", true);
	}

	@DeleteMapping("/{id}")
	public RedirectView deleteById(@PathVariable int id) throws SQLException
	{
		try (Connection connection = database.getConnection())
		{
			feedsRepository.deleteById(connection, id);
		}

		return new RedirectView("/", true);
	}

	@GetMapping("/export")
	public RedirectView export(HttpServletResponse response) throws SQLException, IOException
	{
		try (Connection connection = database.getConnection())
		{
			response.setContentType("text/tab-separated-values");
			response.addHeader("Content-Disposition", "attachment; filename=feeds.tsv");

			try (OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream()))
			{
				final List<Feed> feeds = feedsRepository.findAll(connection);
				for (final Feed feed : feeds)
				{
					writer.write(feed.getName() + '\t' + feed.getUrl());

					final List<FeedTag> tags = tagsRepository.findByFeedId(connection, feed.getId());
					for (final FeedTag tag : tags)
						writer.write(tag.getName() + ',');
				}
			}
		}

		return new RedirectView("/", true);
	}

}
