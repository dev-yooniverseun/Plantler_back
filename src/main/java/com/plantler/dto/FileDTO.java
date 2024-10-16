package com.plantler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileDTO {

	private int file_no;
	private int board_no;
	private String file_server_name;
	private String file_name;
	private String file_type;
	private String file_url;
	private String file_extension;
	private int file_sort;
//	file_regdate;
//	file_moddate;
//	private boolean file_delYN;
}
