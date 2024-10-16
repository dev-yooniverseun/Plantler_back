package com.plantler.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import com.plantler.dto.CommentDTO;
import com.plantler.dto.FileDTO;
import com.plantler.dto.KhBoardDTO;

@Mapper
public interface KhBoardMapper {
	
	// 전체 게시글 리스트
	@Select("  SELECT pb.*, pu.user_nick, pc.category_name, "
			+ "       DATE_FORMAT(pb.board_regdate, '%Y-%m-%d %H:%i:%s') AS board_regdate2 "
			+ "  FROM pl_board AS pb "
			+ " INNER JOIN pl_user AS pu "
			+ "    ON (pb.user_no = pu.user_no) "
			+ " INNER JOIN pl_category AS pc "
			+ "    ON (pb.category_id = pc.category_id) "
			+ " ORDER BY 1 DESC")
	public List<KhBoardDTO> findAll();
	

//	// 게시글 상세
//	@Select("SELECT pb.*, pu.user_nick, pc.category_name, "
//			+ " DATE_FORMAT(pb.board_regdate, '%Y-%m-%d %H:%i:%s') AS board_regdate2 "
//			+ "	FROM pl_board AS pb "
//			+ "	INNER JOIN pl_user AS pu "
//			+ "	    ON (pb.user_no = pu.user_no) "
//			+ "	 INNER JOIN pl_category AS pc "
//			+ "	   ON (pb.category_id = pc.category_id) WHERE board_no = #{board_no}")
//	public KhBoardDTO findOne(int board_no);
	
	
	@Select("SELECT * FROM pl_comment WHERE comment_no = #{comment_no}")
	public CommentDTO findByNo(int comment_no);
	
	// 아래의 코드는 추후에 회원쪽으로 가야하는 코드 아닌가???
	@Select("SELECT user_no FROM pl_user WHERE user_id = #{user_id}")
	public int findByUserId(String user_id);
	
	@SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "board_no", before = false, resultType = Integer.class)
	@Insert("INSERT INTO pl_board (board_title, board_content, category_id, user_no) VALUE (#{board_title}, #{board_content}, #{category_id}, #{user_no})")
	public int saveBoard(KhBoardDTO khBoardDTO);
	
	// pl_file테이블에는 user_no이 없어서 뺌
	@SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "file_no", before = false, resultType = Integer.class)
	@Insert("INSERT INTO pl_file (board_no, file_server_name, file_name, file_type, file_url, file_extension, file_sort) VALUE (#{board_no}, #{file_server_name}, #{file_name}, #{file_type}, #{file_url}, #{file_extension}, #{file_sort})")
	public int saveFile(FileDTO fileDTO);
	
	// 게시글 상세
	@Select("SELECT * FROM pl_file WHERE file_no = #{file_no}")
	public FileDTO findByFileNo(int file_no);

	// 게시글 상세
	@Select("SELECT pb.*, pu.user_nick, pc.category_name,"
			+ "		DATE_FORMAT(pb.board_regdate, '%Y-%m-%d %H:%i:%s') AS board_regdate2"
			+ "	FROM pl_board AS pb "
			+ "	INNER JOIN pl_user AS pu"
			+ "		ON (pb.user_no = pu.user_no)"
			+ "	INNER JOIN pl_category AS pc"
			+ "		ON (pb.category_id = pc.category_id) WHERE board_no = #{board_no}")
	public KhBoardDTO findByBoardNo(int board_no);
	
	// 게시글 상세 + 파일
	@Select("SELECT * FROM pl_file WHERE board_no = #{board_no}")
	public FileDTO findByFileBoardNO(int board_no);

	//게시판 상단 랭킹 1 to 10
	@Select("SELECT pb.*, pf.file_no "
			+ "		FROM pl_board AS pb "
			+ "	LEFT JOIN pl_file AS pf "
			+ "		ON (pb.board_no = pf.board_no) "
			+ "ORDER BY board_like DESC LIMIT 10")
	public List<KhBoardDTO> KhTop10ByBoardLikes(int limit);
	
	//랭킹 게시글에 이미지가 없는 경우, file_no = 1 가져오기
//	@Select("SELECT * FROM pl_file WHERE file_no = 1")
//	public FileDTO findByFileNoOne(int file_no);
	
	// 게시글 수정
	@Update("UPDATE pl_board SET board_title = #{board_title}, board_content = #{board_content}, category_id = #{category_id} WHERE board_no = #{board_no}")
	public int updateBoard(KhBoardDTO khBoardDTO);

	// 게시글 파일 수정
	@Update("UPDATE pl_file SET file_server_name = #{file_server_name}, file_name = #{file_name}, file_type = #{file_type}, file_url = #{file_url}, file_extension = #{file_extension}, file_sort = #{file_sort} WHERE file_no = #{file_no}")
	public int updateFile(FileDTO fileDTO);
	
	// 게시글 삭제
	@Delete("DELETE FROM pl_board WHERE board_no = #{board_no}")
	public int deleteBoard(int board_no);
	
	// 게시글 파일 삭제
	@Delete("DELETE FROM pl_file WHERE file_no = #{file_no}")
	public int deleteFile(int file_no);
	
	// 게시글 조회수
	@Update("UPDATE pl_board SET board_count = board_count + 1 WHERE board_no = #{board_no}")
	public int boardCount(int board_no);
	

}

