package xyz.kebigon.pps;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.view.RedirectView;

@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler
{
	@ExceptionHandler(InvalidEncryptionKeyException.class)
	public RedirectView handleNoKeyException(HttpServletRequest request)
	{
		return new RedirectView("/key?redirect=" + request.getServletPath());
	}
}
