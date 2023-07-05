package com.example.cleanerbot.domain.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DateInfo {
	private String date;

	private String time;
}
