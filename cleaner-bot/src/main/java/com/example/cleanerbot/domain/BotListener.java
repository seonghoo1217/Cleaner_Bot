package com.example.cleanerbot.domain;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;

@Log4j2
public class BotListener extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		User user = event.getAuthor();
		TextChannel textChannel = event.getChannel().asTextChannel();
		Message message = event.getMessage();

		log.info("get Message : "+ message.getContentDisplay());

		if(user.isBot()) return;
		else if (message.getContentDisplay().equals("")){
			log.info("디스코드 Message 문자열 값 공백");
		}

		String[] messageArray = message.getContentDisplay().split(" ");

		if (messageArray[0].equalsIgnoreCase("시리야")){
			String[] messageArgs = Arrays.copyOfRange(messageArray, 1, messageArray.length);

			for (String msg:messageArgs){
				String s = sendMessage(event, msg);
				textChannel.sendMessage(s).queue();
			}
		}
	}

	private String sendMessage(MessageReceivedEvent event,String message){
		User user = event.getAuthor();
		String returnedMessage="";

		switch (message){
			case "안녕" : returnedMessage=user.getName() + "님 안녕하세요! 좋은 저녁입니다";
			break;
		}

		return returnedMessage;
	}
}
