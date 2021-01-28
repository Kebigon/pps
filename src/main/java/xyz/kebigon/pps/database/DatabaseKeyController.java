package xyz.kebigon.pps.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import xyz.kebigon.pps.InvalidEncryptionKeyException;

@Controller
@RequestMapping("/key")
public class DatabaseKeyController
{
	@Autowired
	private DatabaseService database;

	@GetMapping
	public String displayArticle()
	{
		return "key";
	}

	@PostMapping
	public RedirectView configureDatabaseKey(@RequestParam String key)
	{
		if (key == null || !database.checkDatabaseKey(key))
			throw new InvalidEncryptionKeyException();

		database.setDatabaseKey(key);
		return new RedirectView("/");
	}
}
