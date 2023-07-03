package com.example.cleanerbot.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Log4j2
@RequiredArgsConstructor
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
//				String s = sendMessage(event, msg);
//				textChannel.sendMessage(s).queue();
				deleteChannelMessage(event,msg);
			}
		}
	}

	private String sendMessage(MessageReceivedEvent event,String message){
		User user = event.getAuthor();
		String returnedMessage="";

		switch (message){
			case "안녕" : returnedMessage=user.getName() + "님 안녕하세요! 좋은 저녁입니다";
			break;
			case "문어" : returnedMessage="나는 문어 꿈을 꾸는 문어~";
			break;
		}

		return returnedMessage;
	}

	private void deleteChannelMessage(MessageReceivedEvent event,String message){
		MessageChannelUnion channel = event.getChannel();
		String channelId = channel.getId();
		TextChannel textChannel = event.getChannel().asTextChannel();
		if (textChannel == null) {
			System.out.println("Not Found Channel");
			return;
		}
		System.out.println("message="+message);
		System.out.println("채널 이름 :" +textChannel.getName());
		if(message.equals("클리너")){
			//forEachAsync :JDA에서 Loop를 통해 비동기적으로 메시지를 처리하는 방법
			channel.getIterableHistory().forEachAsync(msg->{
				msg.delete().queueAfter(1,TimeUnit.NANOSECONDS);
				return true;
			}).thenRun(()->{
				System.out.println("채널의 모든 메시지 삭제가 완료되었습니다.");
			});
		}
	}
}
