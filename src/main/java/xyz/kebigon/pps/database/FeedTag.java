package xyz.kebigon.pps.database;

import lombok.Data;

@Data
public class FeedTag
{
	private final int id;
	private final String name;
	private final int backgroundColor;
	private final int foregroundColor;

	public String getHexBackgroundColor()
	{
		return Integer.toHexString(backgroundColor);
	}

	public String getHexForegroundColor()
	{
		return Integer.toHexString(foregroundColor);
	}
}
