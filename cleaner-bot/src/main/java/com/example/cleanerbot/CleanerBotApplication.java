package com.example.cleanerbot;

import com.example.cleanerbot.domain.BotListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javax.security.auth.login.LoginException;

@SpringBootApplication
public class CleanerBotApplication {

	public static void main(String[] args) throws LoginException {
		ApplicationContext context = SpringApplication.run(CleanerBotApplication.class, args);
		DiscordBotToken discordBotEntity = context.getBean(DiscordBotToken.class);
		String discordBotToken = discordBotEntity.getDiscordBotToken();

		JDA jda = JDABuilder.createDefault(discordBotToken)
				.setActivity(Activity.playing("명령을 기다리는중!"))
				.enableIntents(GatewayIntent.MESSAGE_CONTENT)
				.addEventListeners(new BotListener())
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
