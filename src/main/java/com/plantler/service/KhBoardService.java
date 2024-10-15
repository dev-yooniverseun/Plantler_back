package com.plantler.service;

import org.springframework.stereotype.Service;

import com.plantler.dto.KhBoardDTO;
import com.plantler.mapper.KhBoardMapper;

@Service
public class KhBoardService {

	private final KhBoardMapper khBoardMapper;
	
	public KhBoardService(KhBoardMapper khBoardMapper) {
		this.khBoardMapper = khBoardMapper;
	}
	
	public KhBoardDTO khBoardDetail(int board_no) {
		return khBoardMapper.findByBoardNo(board_no);
	}
}
