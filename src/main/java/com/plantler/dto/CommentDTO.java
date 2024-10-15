package com.plantler.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {

	private int comment_no;
	private int board_no;
	private String comment_content;
	private LocalDateTime comment_regdate;
	private LocalDateTime comment_moddate;
	
}
