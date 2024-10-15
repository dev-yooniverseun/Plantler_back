package com.plantler.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OAuthResponseDTO {

	private String token;
	private String nick;

}
