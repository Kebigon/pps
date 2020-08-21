package xyz.kebigon.pps.feed;

import java.util.Comparator;

import com.rometools.rome.feed.synd.SyndEntry;

public class SyndEntryComparator implements Comparator<SyndEntry>
{
	private static final SyndEntryComparator instance = new SyndEntryComparator();

	public static SyndEntryComparator get()
	{
		return instance;
	}

	@Override
	public int compare(SyndEntry o1, SyndEntry o2)
	{
		// Sort by publication date in anti-chronological order (most recent
		// first)
		return o2.getPublishedDate().compareTo(o1.getPublishedDate());
	}
}
