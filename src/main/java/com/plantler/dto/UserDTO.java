package com.plantler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO {
	private int user_no;
	private String user_nick;
	private String user_id;
	private String user_pwd;
	private String user_email;
	private String user_provider;
	private String user_regdate;
	private String user_moddate;
	private boolean user_delYN;
}
