CREATE TABLE feed (
	name VARCHAR NOT NULL,
	url VARCHAR NOT NULL
);

CREATE TABLE tag (
	name VARCHAR NOT NULL,
	background_color INTEGER  NOT NULL,
	foreground_color INTEGER  NOT NULL
);

CREATE TABLE feed_tag (
	feed_id INTEGER,
	tag_id INTEGER,
	PRIMARY KEY(feed_id, tag_id),
	FOREIGN KEY(feed_id) REFERENCES feed(rowid) ON DELETE CASCADE,
	FOREIGN KEY(tag_id) REFERENCES tag(rowid) ON DELETE CASCADE
);
