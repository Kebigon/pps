package xyz.kebigon.pps.feed;

import com.rometools.rome.feed.synd.SyndCategoryImpl;

import xyz.kebigon.pps.database.FeedTag;

public class FeedTagSyndCategory extends SyndCategoryImpl
{
	private static final long serialVersionUID = 1L;

	private final int backgroundColor;
	private final int foregroundColor;

	public FeedTagSyndCategory(FeedTag tag)
	{
		setName(tag.getName());
		backgroundColor = tag.getBackgroundColor();
		foregroundColor = tag.getForegroundColor();
	}

	public String getHexBackgroundColor()
	{
		return Integer.toHexString(backgroundColor);
	}

	public String getHexForegroundColor()
	{
		return Integer.toHexString(foregroundColor);
	}
}
