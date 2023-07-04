package com.example.cleanerbot.service;

import com.example.cleanerbot.domain.response.LocationRes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class WeatherService {
	public static final String API_KEY="hNOLl8iuZBrQCk9vxGEf3CFCpravu7FA3uKPBhHPOzsVkfIa8GjCR4AY547yvE9brNtTw2RUv%2BRxXiEiAke%2Fqg%3D%3D";

	public void getWeatherInfoUsedToAPI(LocationRes locationRes) throws IOException {
		RestTemplate restTemplate=new RestTemplate();
		System.out.println("X="+locationRes.getX());
		System.out.println("Y="+locationRes.getY());

		/*MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("serviceKey", API_KEY);
		params.add("dataType", "JSON");
		params.add("numOfRows","10");
		params.add("pageNo","1");
		params.add("base_date", LocalDate.now().toString().replaceAll("-",""));
		params.add("base_time","0800");
		params.add("nx",locationRes.getX());
		params.add("ny",locationRes.getY());*/

/*

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
*/

		StringBuilder target_url=new StringBuilder(	"http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst");
		target_url.append("?" + URLEncoder.encode("serviceKey","UTF-8") +"="+ API_KEY);
		target_url.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
		target_url.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8")); /*한 페이지 결과 수*/
		target_url.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
		target_url.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(LocalDate.now().toString().replaceAll("-",""), "UTF-8")); /*‘21년 6월 28일 발표*/
		target_url.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode("0900", "UTF-8")); /*06시 발표(정시단위) */
		target_url.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode(locationRes.getX(), "UTF-8")); /*예보지점의 X 좌표값*/
		target_url.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode(locationRes.getY(), "UTF-8"));
		System.out.println("Url="+target_url);
//		String response = restTemplate.getForObject(target_url.toString(), String.class);

		URL url = new URL(target_url.toString());
		System.out.println("request url:"+ url);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-type", "application/json");

		BufferedReader rd;
		if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
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
		System.out.println(data);
	}
}
