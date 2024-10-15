package com.plantler.dto;

import java.sql.Date;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KhBoardDTO {

	private int board_no;
	private int category_id;
	private int user_no;
	private String board_title;
	private String board_content;
	private int board_count;
	private int board_like;
	private String board_regdate2;
	private Date board_regdate;
	private Timestamp board_moddate;
	private String user_nick;
	private String category_name;
	
	private int file_no;
	private String file_server_name;
	

}
