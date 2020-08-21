package xyz.kebigon.pps.filedrop;

import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.Data;

@Data
public class DroppedFile
{
	private String id;
	private String name;
	private String contentType;
	private String path;
	private boolean downloaded;

	public static DroppedFile mapToDroppedFile(ResultSet rs, int rowNum) throws SQLException
	{
		final DroppedFile droppedFile = new DroppedFile();
		droppedFile.setId(rs.getString("id"));
		droppedFile.setName(rs.getString("name"));
		droppedFile.setContentType(rs.getString("content_type"));
		droppedFile.setPath(rs.getString("path"));
		droppedFile.setDownloaded(rs.getBoolean("downloaded"));
		return droppedFile;
	}
}
