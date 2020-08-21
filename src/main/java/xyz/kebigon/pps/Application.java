package xyz.kebigon.pps;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@PropertySource("classpath:database.cfg")
@EnableCaching
@Slf4j
public class Application
{
	public static void main(String[] args)
	{
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CaffeineCacheManager cacheManager()
	{
		log.info("cacheManager");

		final CaffeineCacheManager cacheManager = new CaffeineCacheManager();
		cacheManager.setCacheNames(Arrays.asList("stampSessions", "test"));
		cacheManager.setCacheSpecification("expireAfterWrite=10m");
		return cacheManager;
	}
}
