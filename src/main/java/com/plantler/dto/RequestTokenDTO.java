package com.plantler.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestTokenDTO {

	private String type;
	private String client_token;
	private String id;
	
}
