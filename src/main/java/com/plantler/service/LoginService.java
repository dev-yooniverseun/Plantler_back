package com.plantler.service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.plantler.auth.JwtToken;
import com.plantler.dto.RequestTokenDTO;
import com.plantler.dto.ResponseTokenDTO;
import com.plantler.dto.UserDTO;
import com.plantler.mapper.LoginMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LoginService {

	@Autowired
    private LoginMapper loginMapper;
	
	@Autowired
	private JwtToken jwttoken;

    public Map<String, Object> editUser(Map<String, Object> map) {
        map.put("status", false);
        if (loginMapper.edit(map) == 1) {
            Object noValue = map.get("user_no");
            if (noValue != null) {
                try {
                	int no = Integer.parseInt(noValue.toString());
                    map.put("user", loginMapper.findOne(no));
                    map.put("status", true);
                } catch (NumberFormatException e) {
                    map.put("status", false);
                }
            }
        }
        return map;
    }
    
    
    public Map<String, Object> saveUser(Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        String user_id = params.get("user_id");
		String user_pwd = params.get("user_pwd");
		String user_email = params.get("user_email");
		String user_nick = params.get("user_nick");
		response.put("status", "failure");
        
        boolean check = true;
        String message = null;
		
		if(user_id == null || user_id.equals("")) {
			check = false;
			message = "사용자 아이디가 유효하지 않아서 ";
		}
		
		if(user_pwd == null || user_pwd.equals("")) {
			check = false;
			message = "사용자 비밀번호가 유효하지 않아서 ";
		}
		
		if(user_email == null || user_email.equals("")) {
			check = false;
			message = "사용자 이메일이 유효하지 않아서 ";
		}
		
		if(user_nick == null || user_nick.equals("")) {
			check = false;
			message = "사용자 닉네임이 유효하지 않아서 ";
		}
		response.put("message", message + "회원가입에 실패했습니다. 다시 시도해주세요.");
		
		if(check) {
	        UserDTO user = UserDTO.builder()
	                .user_id(user_id)
	                .user_pwd(user_pwd)
	                .user_email(user_email)
	                .user_nick(user_nick)
	                .user_provider("plantler")
	                .build();
	        if (loginMapper.save(user) == 1) {
	            response.put("status", "success");
	            response.put("message", user_nick + "님, 회원가입을 축하합니다!");
	            response.put("user", user);
	        }
		}
        
        return response;
    }

    
    
    // 사용자 정보 조회
    public UserDTO getUserByNo(Map<String, String> request) {
    	String token = request.get("token");
    	if(jwttoken.isValidToken(token)) {
    		RequestTokenDTO requestTokenDTO = jwttoken.getUser(token);
    		String user_id = requestTokenDTO.getId();
    		return loginMapper.findById(user_id);
    	}
    	return null;
    }

    public boolean checkUserNick(String userNick) {
        Map<String, String> params = new HashMap<>();
        params.put("user_nick", userNick);
        return loginMapper.isNickExists(params);
    }

    public boolean checkUserId(String userId) {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", userId);
        return loginMapper.isIdExists(params);
    }

    public UserDTO findIdByEmail(String email) {
        UserDTO user = UserDTO.builder().user_email(email).build();
        return loginMapper.findUserByEmail(user);
    }
    
    public UserDTO findPwdByEmail(String id, String email) {
        String tempPwd = generateTempPwd();
        
//	        // 임시 비밀번호를 해시 처리
//	        String hashedPwd = passwordEncoder.encode(tempPwd);
        
        UserDTO user = UserDTO.builder()
                               .user_email(email)
                               .user_id(id)
                               .user_pwd(tempPwd)
                               .build();
        
        int updateCount = loginMapper.updatePwd(user);
        if (updateCount > 0) {
            // 임시 비밀번호를 반환 (필요시 다른 방법으로 전달 가능)
            user.setUser_pwd(tempPwd);
            log.info("user_pwd:{}", user.getUser_pwd());
            return user;
        } else {
            return null; // 비밀번호 업데이트 실패 시 null 반환
        }
    }
    

    private String generateTempPwd() {
    	SecureRandom random = new SecureRandom();
    	byte[] bytes = new byte[8]; // 8바이트 = 64비트, 더 긴 비밀번호를 생성
    	random.nextBytes(bytes);
    	return bytes.toString(); // 바이트 배열을 문자열로 변환하여 사용
    }
    
    
    public ResponseTokenDTO login(String userId, String password) {
        UserDTO user = UserDTO.builder().user_id(userId).user_pwd(password).build();
        UserDTO loginUser = loginMapper.login(user);
        if (loginUser != null) {
            String jwt = jwttoken.setToken(RequestTokenDTO.builder().type("local").id(loginUser.getUser_id()).build());
            ResponseTokenDTO tok = ResponseTokenDTO.builder().state(true).token(jwt).userDTO(loginUser).build();
            log.info("TokenDTO : {}", tok);
            return tok;
        }
        return ResponseTokenDTO.builder().state(false).build();
    }

    public Map<String, Object> getUserDetail(int userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", false);
        map.put("user", loginMapper.findOne(userId));
        map.put("status", true);
        return map;
    }
    
    public boolean userdelete(String nick) {
        int result = loginMapper.deleteUserByNick(nick);
        return result > 0;  
    }
}
