package xyz.kebigon.pps;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter
{
	@Value("${spring.security.user.name}")
	private String username;
	@Value("${spring.security.user.password}")
	private String password;

	@Override
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception
	{
		auth.inMemoryAuthentication().withUser(username).password(password).roles("USER");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception
	{
		http //
				.csrf().disable() //
				.authorizeRequests() //
				.mvcMatchers("/file-drop/download").anonymous() //
				.anyRequest().fullyAuthenticated() //
				// .anyRequest().permitAll() //
				.and() //
				.formLogin().and() //
				.httpBasic();
	}

	@Bean
	public PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
}
