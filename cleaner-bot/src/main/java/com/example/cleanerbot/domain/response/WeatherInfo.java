package com.example.cleanerbot.domain.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherInfo {
	private Integer TMX;
	private Integer TMN;
	private Integer TMP;
	private Integer REH;
	private Integer POP;


	public boolean getInfoValueNotLoss(){
		return this.getTMP()!=null&& this.getPOP()!=null
						&&this.getREH()!=null && this.getTMN() != null
								&& this.getTMX() != null;
	}
}
