package com.example.cleanerbot.domain;

import com.example.cleanerbot.domain.request.LocationReq;
import com.example.cleanerbot.service.LocationService;
import com.example.cleanerbot.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Log4j2
@RequiredArgsConstructor

public class BotListener extends ListenerAdapter{

	public static final String ADMIN_ID="344483656125120523";

	private final LocationService locationService;

	private final WeatherService weatherService;

	@Override
	@SneakyThrows
	public void onMessageReceived(MessageReceivedEvent event) {
		User user = event.getAuthor();
		TextChannel textChannel = null;
		VoiceChannel voiceChannel = null;
		if (isVoiceChannel(event)){
			voiceChannel= event.getChannel().asVoiceChannel();
		}else {
			textChannel = event.getChannel().asTextChannel();
		}
		Message message = event.getMessage();

		log.info("get Message : "+ message.getContentDisplay());

		if(user.isBot()) return;
		else if (message.getContentDisplay().equals("")){
			log.info("디스코드 Message 문자열 값 공백");
		}

		String[] messageArray = message.getContentDisplay().split(" ");

		if (messageArray[0].equalsIgnoreCase("시리야")){
			String[] messageArgs = Arrays.copyOfRange(messageArray, 1, messageArray.length);

//			for (String msg:messageArgs){
//				String s = sendMessage(event, msg);
//				textChannel.sendMessage(s).queue();
////				deleteChannelMessage(event,msg);
//			}
			String responseMsg = sendMessage(event, messageArgs);
			if (responseMsg.length()>0){
				if (isVoiceChannel(event)){
					voiceChannel.sendMessage(responseMsg).queue();
				}else {
					textChannel.sendMessage(responseMsg).queue();
				}
			}
		}
	}

	private String sendMessage(MessageReceivedEvent event,String[] msgArgs) throws IOException {
		User user = event.getAuthor();
		String returnedMessage="";
		System.out.println("logging="+msgArgs[0]);
		switch (msgArgs[0]){
			case "안녕" : returnedMessage=user.getName() + "님 안녕하세요! "+ getQuarterTimeInfo();
			break;
			case "시간" : returnedMessage="현재시간은 "+getLocalDateTime()+"입니다";
			break;
			case "클리너" : deleteChannelMessage(event,msgArgs,user);
			break;
			case "날씨" :
				String weatherAPIResult = weatherService.getWeatherInfoUsedToAPI(locationService.readLocationValueOnFile(formationToObject(msgArgs)));
				if(weatherAPIResult.length()!=0) returnedMessage=weatherAPIResult;
				break;
		}

		return returnedMessage;
	}

	private void deleteChannelMessage(MessageReceivedEvent event,String[] msgArgs,User user){
		MessageChannelUnion channel = event.getChannel();
		TextChannel textChannel = event.getChannel().asTextChannel();
		if (textChannel == null) {
			System.out.println("Not Found Channel");
			return;
		}

		System.out.println("채널 이름 :" +textChannel.getName());

		if(user.getId().equals(ADMIN_ID)){
			//forEachAsync :JDA에서 Loop를 통해 비동기적으로 메시지를 처리하는 방법
			channel.getIterableHistory().forEachAsync(msg->{
				msg.delete().queueAfter(1,TimeUnit.NANOSECONDS);
				return true;
			}).thenRun(()->{
				System.out.println("채널의 모든 메시지 삭제가 완료되었습니다.");
			});
		}
	}

	private String getLocalDateTime(){
		LocalDateTime now = LocalDateTime.now();
		return now.getYear()+"년 "+now.getMonthValue()+"월 "+now.getDayOfMonth()+"일 "+now.getHour()+"시 "+now.getMinute()+"분";
	}

	private String getQuarterTimeInfo(){
		int Hours = LocalDateTime.now().getHour();
		if (Hours>=0&&Hours<8) return "새벽까지 공부하시다니 대단하네요! 😆";
		else if (Hours>=8&&Hours<12) return "아침 일찍 공부하는 당시는 갓생러! 🤩";
		else if (Hours>=12&&Hours<18) return "나른한 점심시간! 졸려도 공부는 해야죠!  \uD83D\uDD25";
		else return "저녁이에요! 개발자들의 시간입니다 🥱";
	}

	private LocationReq formationToObject(String[] msgArgs){
		return LocationReq.builder()
				.area1(msgArgs[1])
				.area2(msgArgs[2])
				.area3(msgArgs[3])
				.build();
	}

	private boolean isVoiceChannel(MessageReceivedEvent event){
		return event.getChannel().toString().contains("Voice");
	}
}
