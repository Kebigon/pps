package xyz.kebigon.pps.filedrop;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;
import xyz.kebigon.pps.database.PublicDatabaseService;

@Controller
@RequestMapping("/file-drop")
@Slf4j
public class FileDropController implements InitializingBean
{
	@Value("${filedrop.directory}")
	private File directory;

	@Autowired
	private PublicDatabaseService publicDatabase;

	@GetMapping
	public ModelAndView dropFilePage() throws SQLException
	{
		final Map<String, Object> model = new HashMap<String, Object>();

		try (Connection connection = publicDatabase.getConnection())
		{
			final JdbcTemplate template = new JdbcTemplate(new SingleConnectionDataSource(connection, true));
			model.put("files", template.query("SELECT * FROM drop_file", DroppedFile::mapToDroppedFile));
		}

		return new ModelAndView("file-drop", model);
	}

	@PostMapping
	public String dropFile(@RequestParam("file") MultipartFile file, ModelMap modelMap) throws SQLException, IllegalStateException, IOException
	{
		try (Connection connection = publicDatabase.getConnection())
		{
			final String id = UUID.randomUUID().toString();

			final File savedFile = new File(directory, id);
			file.transferTo(savedFile);

			final Map<String, Object> args = new HashMap<String, Object>();
			args.put("id", id);
			args.put("name", file.getOriginalFilename());
			args.put("content_type", file.getContentType());
			args.put("path", savedFile.getCanonicalPath());

			final SimpleJdbcInsert insert = new SimpleJdbcInsert(new SingleConnectionDataSource(connection, true)).withTableName("drop_file");
			insert.execute(args);
		}

		modelMap.addAttribute("file", file);
		return "fileUploadView";
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		// Create directory if necessary
		if (!directory.exists())
			directory.mkdirs();
	}

	@GetMapping("/download/{id}")
	public void downloadPDFResource(HttpServletResponse response, @PathVariable String id) throws SQLException, IOException
	{
		try (Connection connection = publicDatabase.getConnection())
		{
			final JdbcTemplate template = new JdbcTemplate(new SingleConnectionDataSource(connection, true));
			final DroppedFile droppedFile = template.queryForObject("SELECT * FROM drop_file WHERE id = ? LIMIT 1", new Object[] { id },
					DroppedFile::mapToDroppedFile);

			final Path file = Paths.get(directory.getAbsolutePath(), id);
			if (Files.exists(file))
			{
				response.setContentType(droppedFile.getContentType());
				response.addHeader("Content-Disposition", "attachment; filename=" + droppedFile.getName());

				Files.copy(file, response.getOutputStream());
				response.getOutputStream().flush();

				// TODO: secure delete file
				log.info("Deleting file {}...", file);
				Files.delete(file);

				template.update("UPDATE drop_file SET downloaded = true WHERE id = ?", id);
			}
		} catch (final IncorrectResultSizeDataAccessException ex)
		{
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}
}
