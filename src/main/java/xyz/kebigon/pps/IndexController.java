package xyz.kebigon.pps;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController
{
	@GetMapping
	public ModelAndView RSSFeeds()
	{
		return new ModelAndView("index");
	}
}
