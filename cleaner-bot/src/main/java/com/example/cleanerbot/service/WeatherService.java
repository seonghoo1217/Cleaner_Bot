package com.example.cleanerbot.service;

import com.example.cleanerbot.domain.response.DateInfo;
import com.example.cleanerbot.domain.response.LocationRes;
import com.example.cleanerbot.domain.response.WeatherInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WeatherService {
	public static final String API_KEY="hNOLl8iuZBrQCk9vxGEf3CFCpravu7FA3uKPBhHPOzsVkfIa8GjCR4AY547yvE9brNtTw2RUv%2BRxXiEiAke%2Fqg%3D%3D";

	public static final String ERROR ="ERROR";

	public static final String ROUTING_ERROR="<returnReasonCode>04</returnReasonCode>";


	public String getWeatherInfoUsedToAPI(LocationRes locationRes) throws IOException {
		System.out.println("X="+locationRes.getX());
		System.out.println("Y="+locationRes.getY());

		try {
			URL url = new URL(getTargetUrlUsedToKMAApi(locationRes).toString());
			System.out.println("request url:" + url);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-type", "application/json");

			BufferedReader rd;
			if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} else {
				rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			conn.disconnect();
			String data = sb.toString();
			if (data.charAt(2)!='r'){
				System.out.println("ERROR DATA="+data);
				throw new IllegalStateException("기상청 API 오류");
			}
			System.out.println("DATA="+data);

			WeatherInfo parsingWeatherInfo = getParsingToInfoObject(data);

			return getWeatherInfoToString(parsingWeatherInfo,locationRes.getInputArea());

		}catch (IllegalStateException e){
			System.out.println("에러!");
		}
		return "";
	}

	private StringBuilder getTargetUrlUsedToKMAApi(LocationRes locationRes) throws UnsupportedEncodingException {
		DateInfo di = getDateInfoToObject();
		StringBuilder target_url=new StringBuilder(	"http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst");
		target_url.append("?" + URLEncoder.encode("serviceKey","UTF-8") +"="+ API_KEY);
		target_url.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
		target_url.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("500", "UTF-8")); /*한 페이지 결과 수*/
		target_url.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
		target_url.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(di.getDate(), "UTF-8")); /*‘21년 6월 28일 발표*/
		target_url.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode(di.getTime(), "UTF-8")); /*06시 발표(정시단위) */
		target_url.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode(locationRes.getX(), "UTF-8")); /*예보지점의 X 좌표값*/
		target_url.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode(locationRes.getY(), "UTF-8"));
		return target_url;
	}

	private WeatherInfo getParsingToInfoObject(String data) {
		WeatherInfo wf=new WeatherInfo();
		Integer cnt=0;
		JsonParser jsonParser = new JsonParser();
		JsonElement parseResult = jsonParser.parse(data);
		JsonArray fetchResult = parseResult.getAsJsonObject().get("response")
				.getAsJsonObject().get("body")
				.getAsJsonObject().get("items")
				.getAsJsonObject().get("item").getAsJsonArray();
		for (JsonElement je:fetchResult){
			if (wf.getInfoValueNotLoss()){
				break;
			}
			String category = je.getAsJsonObject().get("category").toString();
			String fcstValue = je.getAsJsonObject().get("fcstValue").toString().replaceAll("\"", "");
			switch (category){
				case "\"TMX\"" :
					System.out.println("TMX VALUE "+fcstValue);
					wf.setTMX(getParsingFcstValue(fcstValue));
					break;
				case "\"TMP\"" :
					wf.setTMP(getParsingFcstValue(fcstValue));
					break;
				case "\"REH\"" :
					wf.setREH(getParsingFcstValue(fcstValue));
					break;
				case "\"TMN\"" :
					System.out.println("TMN VALUE "+fcstValue);
					wf.setTMN(getParsingFcstValue(fcstValue));
					break;
				case "\"POP\"" :
					wf.setPOP(getParsingFcstValue(fcstValue));
					break;
			}
		}

		return wf;
	}

	private String getWeatherInfoToString(WeatherInfo weatherInfo,String userArea){
		StringBuilder sb=new StringBuilder();
		DateInfo di = getDateInfoToObject();
		sb.append("**날짜 :**").append(di.getDate()).append(" **시간 :**").append(di.getTime()).append("를 기준으로 기상청 API를 통해 알려드립니다.");
		sb.append("\n");
		sb.append("\n");
		sb.append("**").append(userArea+"** 지역의 정보를 알려드리겠습니다.").append("\n \n");
		sb.append("**평균기온**은 ").append(weatherInfo.getTMP()).append("°C이며, ")
						.append("**최저 기온**은 ").append(weatherInfo.getTMN())
								.append("**최고 기온**은 ").append(weatherInfo.getTMX()).append("°C입니다.\n");
		sb.append("**강수확률**은 ").append(weatherInfo.getPOP()).append("%이며, ")
						.append("**습도**는 ").append(weatherInfo.getREH()).append("%입니다.");
		return sb.toString();
	}

	private DateInfo getDateInfoToObject(){
		int H = LocalDateTime.now().getHour();
		int M = LocalDateTime.now().getMinute();
		LocalDate now = LocalDate.now();
		System.out.println("H="+H);
		System.out.println("M="+M);

		if (H <= 2 && M < 10) {
			LocalDate nowTime = now.minusDays(1);
			System.out.println("Log=" + nowTime);
			return getFormationToDateInfo(getDateInfoToString(nowTime), "2300");
		} else if (H >= 2 && H <= 5 && M < 10) {
			return getFormationToDateInfo(getDateInfoToString(now), "0200");
		} else if (H >= 5 && H <= 8 && M < 10) {
			return getFormationToDateInfo(getDateInfoToString(now), "0500");
		} else if (H >= 8 && H <= 11 && M < 10) {
			return getFormationToDateInfo(getDateInfoToString(now), "0800");
		} else if (H >= 11 && H <= 14 && M < 10) {
			return getFormationToDateInfo(getDateInfoToString(now), "1100");
		} else if (H >= 14 && H <= 17 && M < 10) {
			return getFormationToDateInfo(getDateInfoToString(now), "1400");
		} else if (H >= 17 && H <= 20 && M < 10) {
			return getFormationToDateInfo(getDateInfoToString(now), "1700");
		} else if (H >= 20 && M < 10) {
			return getFormationToDateInfo(getDateInfoToString(now), "2000");
		} else {
			return getFormationToDateInfo(getDateInfoToString(now), "2300");
		}
	}

	private String getDateInfoToString(LocalDate nowTime){
		return nowTime.toString().replaceAll("-","");
	}

	private DateInfo getFormationToDateInfo(String date,String time){
		System.out.println("Date="+date);
		System.out.println("Time="+time);

		return DateInfo.builder()
				.date(date)
				.time(time)
				.build();
	}

	private Integer getParsingFcstValue(String fcstValue){
		System.out.println("!!="+fcstValue);
		return (int) Double.parseDouble(fcstValue);
	}
}
