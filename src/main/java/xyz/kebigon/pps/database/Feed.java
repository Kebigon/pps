package xyz.kebigon.pps.database;

import java.util.List;

import lombok.Data;

@Data
public class Feed
{
	private int id;
	private String name;
	private String url;
	private List<FeedTag> tags;
}
