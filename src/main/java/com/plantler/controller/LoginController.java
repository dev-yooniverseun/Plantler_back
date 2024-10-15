package com.plantler.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import com.plantler.dto.ResponseTokenDTO;
import com.plantler.dto.UserDTO;
import com.plantler.service.LoginService;  // 변경된 이름
import com.plantler.service.OAuth2Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
public class LoginController {

	  @Autowired
	  private LoginService loginService;  // 변경된 이름
	  
	  @Autowired
	  private OAuth2Service oAuth2Service;
	  
	  @GetMapping("/login/{type}")
	  public RedirectView oauth2Login(@PathVariable("type") String type) {
		  return new RedirectView( oAuth2Service.getOAuth2LoginUrl(type) );
	  }
	  @GetMapping("/login/{type}/callback")
	  public RedirectView oauth2Callback(@PathVariable("type") String type, @RequestParam("code") String code, @RequestParam(name="state", required=false) String state) {
		  return new RedirectView(oAuth2Service.handleOAuth2Callback(type, code, state));
	  }
	  
	  @GetMapping("/kakao/logout")
	  public RedirectView kakaoLogout(@RequestParam("token") String token) {
		  return new RedirectView(oAuth2Service.kakaoLogout(token));
	  }
	  
//////////////////////////////////////////////////////////////////////////////////////////////////////
	  @PostMapping("/forgot-pwd")
	    public ResponseEntity<?> forgotpwd(@RequestBody Map<String, String> params) {
	        String id = params.get("user_id");
	        String email = params.get("user_email");
	        log.info("params:{}", params);
	        // 유효성 검사 후 서비스 호출
	        if (email != null && !email.isEmpty() && id != null && !id.isEmpty()) {
	         UserDTO userDTO =loginService.findPwdByEmail(id, email);
	         if(userDTO != null) return ResponseEntity.ok(userDTO);
	         else 
	        	  log.info("message:{}", "여기!안갔습니다.");
	         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	        }
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 잘못된 요청 처리
	    }
	
	  @PostMapping("/save")
	  public ResponseEntity<Map<String, Object>> save(@RequestBody Map<String, String> params) {
		  Map<String, Object> response = loginService.saveUser(params);
		  if ("success".equals(response.get("status"))) {
			  return ResponseEntity.status(HttpStatus.CREATED).body(response);
		  } else {
			  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		  }
	  }
	  
	  @PostMapping("/getUserByNo")
	    public ResponseEntity<UserDTO> getUserByNo(@RequestBody Map<String, String> request) {
		  UserDTO user = loginService.getUserByNo(request);
	        if (user != null) {
	            return ResponseEntity.ok(user);
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	        }
	    }

	  
	    @PostMapping("/edit")
	    public Map edit(@RequestBody Map<String, Object> params) {
	    	Map<String, Object> map = new HashMap<>();
	    	map.put("user_id", params.get("user_id"));
	    	map.put("user_nick", params.get("user_nick"));
	    	map.put("user_pwd", params.get("user_pwd"));
	    	map.put("user_email", params.get("user_email"));
	    	  Object userNoValue = params.get("user_no");
	    	    if (userNoValue != null) {
	    	        try {
	    	            int userNo = Integer.parseInt(userNoValue.toString()); // 변환
	    	            map.put("user_no", userNo);
	    	        } catch (NumberFormatException e) {
	    	            log.error("user_no 변환 오류: {}", e.getMessage());
	    	            map.put("user_no", 0); // 기본값 설정
	    	        }
	    	    } else {
	    	        map.put("user_no", 0); // user_no가 null일 경우 기본값 설정
	    	    }

	    	    log.info("map:{}", map);
	    	    return loginService.editUser(map);  // 변경된 이름
	
	    }

	 
	    
	    @PostMapping("/checkUserNick")
	    public Map<String, Object> checkUserNick(@RequestBody Map<String, String> request) {
	        String userNick = request.get("nick");
	        Map<String, Object> response = new HashMap<>();
	        response.put("exists", loginService.checkUserNick(userNick));
	        return response;
	    }

	    @PostMapping("/checkUserID")
	    public Map<String, Object> checkUserID(@RequestBody Map<String, String> request) {
	        String userId = request.get("id");
	        Map<String, Object> response = new HashMap<>();
	        response.put("exists", loginService.checkUserId(userId));
	        return response;
	    }


	    @PostMapping("/findID")
	    public UserDTO findId(@RequestBody Map<String, String> params) {
	        String email = params.get("user_email");
	        return email != null && !email.isEmpty() ? loginService.findIdByEmail(email) : null;  // 변경된 이름
	    }

	    @PostMapping("/login")
	    public ResponseTokenDTO login(@RequestBody Map<String, String> params) {
	        return loginService.login(params.get("id"), params.get("pwd"));  // 변경된 이름
	    }

	    @PostMapping("/detail")
	    public Map detail(@RequestBody Map<String, Object> map) {
	        return loginService.getUserDetail(Integer.parseInt(map.get("no").toString()));  // 변경된 이름
	    }
	    
	    @PostMapping("/userdelete")
	    public ResponseEntity<String> userDelete(@RequestBody Map<String, String> params) {
	        String nick = params.get("user_nick");

	        try {
	            boolean isDeleted = loginService.userdelete(nick);  // Assuming this returns a boolean
	            if (isDeleted) {
	                // Return a 200 OK with a success message
	                return ResponseEntity.ok("User deleted successfully.");
	            } else {
	                // Return a 404 NOT FOUND if user was not found or not deleted
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
	            }
	        } catch (Exception e) {
	            // Return a 500 INTERNAL SERVER ERROR in case of an exception
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while deleting user.");
	        }
	    }
	    
//////////////////////////////////////////////////////////////////////////////////////////////////////


}