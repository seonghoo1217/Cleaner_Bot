package com.example.cleanerbot;

import com.example.cleanerbot.domain.BotListener;
import com.example.cleanerbot.service.LocationService;
import com.example.cleanerbot.service.WeatherService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;

@SpringBootApplication
@RequiredArgsConstructor
public class CleanerBotApplication {

	private final ResourceLoader resourceLoader;

	public static LocationService locationService;

	public static WeatherService weatherService;

	@PostConstruct
	public void init(){
		locationService=new LocationService(resourceLoader);
		weatherService=new WeatherService();
	}

	public static void main(String[] args) throws LoginException {
		ApplicationContext context = SpringApplication.run(CleanerBotApplication.class, args);
		DiscordBotToken discordBotEntity = context.getBean(DiscordBotToken.class);
		String discordBotToken = discordBotEntity.getDiscordBotToken();


		JDA jda = JDABuilder.createDefault(discordBotToken)
				.setActivity(Activity.playing("명령을 기다리는중!"))
				.enableIntents(GatewayIntent.MESSAGE_CONTENT)
				.addEventListeners(new BotListener(locationService,weatherService))
				.build();
	}
	@Component
	class DiscordBotToken {
		@Value("${discord.bot.token}")
		private String discordBotToken;

		public String getDiscordBotToken() {
			return discordBotToken;
		}
	}
}
