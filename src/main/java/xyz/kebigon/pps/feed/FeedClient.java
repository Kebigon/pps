package xyz.kebigon.pps.feed;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import lombok.extern.slf4j.Slf4j;
import xyz.kebigon.pps.database.FeedTag;

@Service
@Slf4j
public class FeedClient
{
	private final RestTemplate restTemplate = new RestTemplate();
	private final SyndFeedInput input = new SyndFeedInput();

	@Value("${feeds.youtube.replaceByInvidious}")
	private boolean replaceYoutubeByInvidious;
	@Value("${invidious.url}")
	private String invidiousUrl;

	@Cacheable("test")
	public SyndFeed getFeed(String url, List<FeedTag> tags)
	{
		log.info("Getting {}", url);

		final SyndFeed feed = restTemplate.execute(url, HttpMethod.GET, null, response -> {
			try
			{
				return input.build(new XmlReader(response.getBody()));
			} catch (IllegalArgumentException | FeedException e)
			{
				throw new IOException(e);
			}
		});

		if (replaceYoutubeByInvidious)
			feed.getEntries().stream().filter(entry -> entry.getLink().contains("https://www.youtube.com"))
					.forEach(entry -> entry.setLink(entry.getLink().replace("https://www.youtube.com", invidiousUrl)));

		if (tags != null)
		{
			final List<SyndCategory> categories = tags.stream().map(tag -> new FeedTagSyndCategory(tag)).collect(Collectors.toList());

			feed.getEntries().stream().forEach(entry -> entry.setCategories(categories));
		}

		return feed;
	}
}
