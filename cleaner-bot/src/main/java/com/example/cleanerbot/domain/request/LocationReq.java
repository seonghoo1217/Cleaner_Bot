package com.example.cleanerbot.domain.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LocationReq {
	private String area1;

	private String area2;

	private String area3;
}
