package com.plantler.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.plantler.auth.JwtToken;
import com.plantler.dto.CommentDTO;
import com.plantler.dto.FileDTO;
import com.plantler.dto.KhBoardDTO;
import com.plantler.dto.RequestTokenDTO;
import com.plantler.dto.ResultDTO;
import com.plantler.mapper.KhBoardMapper;
import com.plantler.service.KhBoardService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@Slf4j
//@RequestMapping("/kh")
public class KhBoardController {
	
	private final KhBoardMapper khbMapper;
	private final JwtToken jwtToken;
	
	@GetMapping("/")
	public String home() {
		return "서버 스타트~~~~~~~~~~";
	}
	
	@GetMapping("/view")
    public ResponseEntity<?> view(@RequestParam("file_no") int file_no) {
		try {
			  FileDTO fileDTO = khbMapper.findByFileNo(file_no);
			  String url = fileDTO.getFile_server_name(); // 데이터베이스에서 file_no로 파일 정보 가져와서 아래 로직 적용
		      String path = getRootPath().concat("\\upload\\").concat(url);
		      File file = new File(path);
		      return ResponseEntity.ok()
		        .contentLength(file.length())
		        .contentType(MediaType.parseMediaType(fileDTO.getFile_type()))
		        .body(new InputStreamResource(new FileInputStream(file)));
		    } catch (Exception e) {
		      e.printStackTrace();
		      return ResponseEntity.notFound().build();
		    }
    }
	

	
	
	// 게시판 목록 + 랭킹 포함
	@GetMapping("/khboardlist")
	public ResultDTO list() {
		// 1. Map
		// 2. DTO
		
		boolean state = false;
		
		//게시판 목록
		List<KhBoardDTO> khBoardList = khbMapper.findAll();

		//노하우 게시판 상단 랭킹
		List<KhBoardDTO> khBoardRanks = khbMapper.KhTop10ByBoardLikes(10); 
		
		//파일 정보 추가 (게시판 랭킹용)
		if(khBoardRanks != null && !khBoardRanks.isEmpty()) {
			for (KhBoardDTO board : khBoardRanks) {
				try {
					FileDTO fileDTO = khbMapper.findByFileNo(board.getBoard_no());
					if (fileDTO != null) {
						//board.setFile_no(fileDTO.findByFileNoOne(1));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		
		boolean rankstate = (khBoardRanks != null && !khBoardRanks.isEmpty());
		state = rankstate || (khBoardList != null && !khBoardList.isEmpty());
		
	return ResultDTO.builder()
			.state(state)
			.result(Map.of("ranks", khBoardRanks, "list", khBoardList))
			.build();
		
	}
	
	
	// 게시글 상세페이지
	
	private final KhBoardService khBoardService;
	
	@GetMapping("/khboarddetail/{board_no}")
	public ResultDTO KhBoardDetail(@PathVariable("board_no") int board_no) {
		 // 조회수 증가
	    int countResult = khbMapper.boardCount(board_no);
	    log.info("게시글 번호: {} 조회수 증가 결과: {}", board_no, countResult);
		
		KhBoardDTO khBoardDTO = khbMapper.findByBoardNo(board_no);
		if(khBoardDTO != null) {
			FileDTO fileDTO = khbMapper.findByFileBoardNO(khBoardDTO.getBoard_no());
			if(fileDTO != null) {				
				khBoardDTO.setFile_no(fileDTO.getFile_no());
			}
			return ResultDTO.builder().state(true).result(khBoardDTO).build();
		}
		return ResultDTO.builder().state(false).result("❌❌❌ 게시글을 찾을 수 없습니다. ❌❌❌").build();
	}
	

	
	@GetMapping("/khTest")
	public ResultDTO test(@RequestParam("comment_no") int comment_no) {
		
		boolean state = true;
		
		CommentDTO commentDTO = khbMapper.findByNo(comment_no);
		if(commentDTO == null) { // 값이 없을때 
			state = false;
			//commentDTO = new CommentDTO();
		}
		
		return ResultDTO.builder()
				.state(state)
				.result(commentDTO)
				.build();
	}
	
//	@Value("/upload + ${plantler.file.path}") String dir;
	private String getRootPath() {return new File("").getAbsolutePath();}
	
	private String getFileExtension(String originalFilename) {
        // 파일 확장자 추출 로직 수정
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        } else {
            return ""; // 확장자 없음
        }
    }
	
	// 게시판 글 등록 (파일 포함) -----------------------------------------------------------------	
	@PostMapping("/khboardregister")
	public ResultDTO khboardRegister(@RequestParam(name="file", required = false) MultipartFile multipartFile, 
			@RequestParam("board_title") String board_title, 
			@RequestParam("board_content") String board_content, 
			@RequestParam("category_id") int category_id,
			HttpServletRequest request) throws IOException {
		// 서비스 이동하는 메소드 호출 >> 
	
		boolean state = false;
		Object result = null;
	
		try {
			// Token 가져오기
			String token =  request.getHeader("Authorization");
			System.out.println(token);
			if(jwtToken.isValidToken(token)) {
				RequestTokenDTO requestTokenDTO = jwtToken.getUser(token);
				String user_id = requestTokenDTO.getId();
				System.out.println("user id: " + user_id);
				int user_no = khbMapper.findByUserId(user_id);
				System.out.println("user no: " + user_no);
				
				// 1단 게시판 등록 도전!! >> 게시판 번호를 이용하여 파일 업로드쪽으로 가봅시다..
				KhBoardDTO khBoardDTO = KhBoardDTO.builder()
						.board_title(board_title)
						.board_content(board_content)
						.category_id(category_id)
						.user_no(user_no)
						.build();
				System.out.println("게시판 등록 되니?" + khBoardDTO);
				if(khbMapper.saveBoard(khBoardDTO) == 1) {
					
					// 2단계 파일 업로드 도전!!
					int board_no = khBoardDTO.getBoard_no();
					if(multipartFile == null || multipartFile.isEmpty()) {
						// 파일이 없다..
						System.out.println("NO FILE UPLOADED");
					} else {
						String file_server_name = UUID.randomUUID().toString(); 
						String file_name = multipartFile.getOriginalFilename();
						String file_type = multipartFile.getContentType();
						String file_url = file_server_name;
						String file_extension = getFileExtension(multipartFile.getOriginalFilename());
						int file_sort = 0;
						
						String dir = getRootPath();
						log.info("DIR : {}", dir);
						String path = dir + "\\upload";
						log.info("path : {}", path);
						
						File directory = new File(path);
		                if (!directory.exists()) {
		                    System.out.println("Directory does not exist. Creating directory..." + path);
		                    directory.mkdirs();
		                }
						
		                File file = new File(path + "\\" + file_server_name);
						multipartFile.transferTo(file);
						// 파일 저장 완료!!
						System.out.println("FDILE UPLOADED SUCCESSFULLY");
						
						FileDTO fileDTO = FileDTO.builder()
							.board_no(board_no)
							.file_server_name(file_server_name)
							.file_name(file_name)
							.file_type(file_type)
							.file_url(file_url)
							.file_extension(file_extension)
							.file_sort(file_sort)
							.build();
						
						if( khbMapper.saveFile(fileDTO) == 1) {
							// 파일 테이블 정상 입력 완료!!
							System.out.println("FileDTO SAVED SUCCESSFULLY");
							state = true;
							result = board_no;
						}
						
					}
				}
			
			} 
			
		}catch (Exception e) {
				e.printStackTrace();
				return ResultDTO.builder().state(false).msg("ERRROR").build();
		}
		
		return ResultDTO.builder().state(state).result(result).build();
	
	}
		
// 여기서 부터 하기............
		// 게시판 글 수정(지선생님이 도와주심.....................)
		@PostMapping("/khboardupdate")
		public ResultDTO KhBoardUpdate(
				@RequestParam("board_no") int board_no,
		        @RequestParam("board_title") String board_title,
		        @RequestParam("board_content") String board_content,
		        @RequestParam("category_id") int category_id,
		        @RequestParam(name="file", required = false) MultipartFile multipartFile,
		        HttpServletRequest request) {

		    boolean state = false;
		    Object result = null;

		    try {
		        // Token 가져오기
		        String token = request.getHeader("Authorization");
		        if (jwtToken.isValidToken(token)) {
		            RequestTokenDTO requestTokenDTO = jwtToken.getUser(token);
		            int user_no = khbMapper.findByUserId(requestTokenDTO.getId());

		            // 게시글 수정 DTO 생성
		            KhBoardDTO khBoardDTO = KhBoardDTO.builder()
		                    .board_no(board_no)
		                    .board_title(board_title)
		                    .board_content(board_content)
		                    .category_id(category_id)
		                    .user_no(user_no)
		                    .build();

		            // 게시글 업데이트
		            if (khbMapper.updateBoard(khBoardDTO) == 1) {
		                state = true;

		                // 파일 수정 로직
		                if (multipartFile != null && !multipartFile.isEmpty()) {
		                    // 파일 정보 생성
		                    FileDTO fileDTO = new FileDTO();
		                    String file_server_name = UUID.randomUUID().toString();
		                    String file_name = multipartFile.getOriginalFilename();
		                    String file_type = multipartFile.getContentType();
		                    String file_extension = getFileExtension(file_name);
		                    String file_url = file_server_name;
		                    fileDTO.setFile_server_name(file_server_name);
		                    fileDTO.setFile_name(file_name);
		                    fileDTO.setFile_type(file_type);
		                    fileDTO.setFile_url(file_url);
		                    fileDTO.setFile_extension(file_extension);
		                    fileDTO.setFile_sort(0);
		                    fileDTO.setFile_no(khbMapper.findByFileBoardNO(board_no).getFile_no()); // 기존 파일 번호 가져오기

		                    // 파일 저장
		                    String dir = getRootPath() + "\\upload";
		                    File directory = new File(dir);
		                    if (!directory.exists()) {
		                        directory.mkdirs();
		                    }
		                    File file = new File(dir + "\\" + file_server_name);
		                    multipartFile.transferTo(file);

		                    // 파일 업데이트
		                    if (khbMapper.updateFile(fileDTO) == 1) {
		                        state = true;
		                    }
		                }
		                result = board_no; // 수정된 게시글 번호
		            }
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		        return ResultDTO.builder().state(false).msg("ERROR").build();
		    }

		    return ResultDTO.builder().state(state).result(result).build();
		}

		// 게시글 삭제
		@PostMapping("/khboarddelete/{board_no}")
		public ResultDTO deleteBoard(@PathVariable("board_no") int board_no, HttpServletRequest request) {
		    boolean state = false;
		    try {
		        // Token 가져오기
		        String token = request.getHeader("Authorization");
		        if (jwtToken.isValidToken(token)) {
		        	
		        	// 파일 정보 조회
		            FileDTO fileDTO = khbMapper.findByFileBoardNO(board_no);
		            if (fileDTO != null) {
		                // 파일 삭제 (서버에서)
		                String filePath = getRootPath() + "\\upload\\" + fileDTO.getFile_server_name();
		                File file = new File(filePath);
		                if (file.exists()) {
		                    file.delete();
		                }

		                // 데이터베이스에서 파일 정보 삭제
		                khbMapper.deleteFile(fileDTO.getFile_no());
		            }
		        	
		            // 권한 검증 후 게시글 삭제 수행
		            int result = khbMapper.deleteBoard(board_no);
		            if (result == 1) {
		                state = true; // 삭제 성공
		            }
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		        return ResultDTO.builder().state(false).msg("ERROR").build();
		    }
		    return ResultDTO.builder().state(state).build();
		}
		
		
		
		
//				// 게시글 조회수(보류)
//				@GetMapping("/khboarddetail/{board_no}")
//				public void boardCount(int board_no) {
//				    int result = khbMapper.boardCount(board_no);
//				    log.info("게시글 번호: {} 조회수 증가", board_no);
//				    if (result == 0) {
//				        // 해당 게시물이 존재하지 않거나 업데이트 실패
//				        throw new RuntimeException("게시글 조회수 업데이트 실패");
//				    }
//				}
		
	}


