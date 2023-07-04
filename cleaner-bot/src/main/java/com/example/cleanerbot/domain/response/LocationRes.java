package com.example.cleanerbot.domain.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LocationRes {
	private String x;

	private String y;
}
