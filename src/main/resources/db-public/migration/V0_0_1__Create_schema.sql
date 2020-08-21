CREATE TABLE drop_file (
	id VARCHAR(11) PRIMARY KEY,
	name VARCHAR(255) NOT NULL,
	content_type VARCHAR(255) NOT NULL,
	path VARCHAR(255) NOT NULL,
	downloaded BOOLEAN DEFAULT 'FALSE'
);
