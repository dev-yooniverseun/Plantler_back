package com.plantler.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.plantler.auth.JwtToken;
import com.plantler.dto.RequestTokenDTO;
import com.plantler.dto.UserDTO;
import com.plantler.mapper.LoginMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OAuth2Service {
	
	@Autowired
	private JwtToken jwtToken;
	
	@Autowired
    private LoginMapper loginMapper;

	@Value("${naver.client.id}") 		private String NAVER_CLIENT_ID;
    @Value("${naver.client.secret}") 	private String NAVER_CLIENT_SECRET;
    @Value("${naver.authorization.uri}") private String NAVER_AUTHORIZATION_URI;
    @Value("${naver.redirect.uri}") 	private String NAVER_REDIRECT_URI;
    @Value("${naver.token.url}") 		private String NAVER_TOKEN_URL;
    @Value("${naver.user.info.url}") 	private String NAVER_USER_INFO_URL;

    @Value("${kakao.client.id}") 		private String KAKAO_CLIENT_ID;
    @Value("${kakao.authorization.uri}") private String KAKAO_AUTHORIZATION_URI;
    @Value("${kakao.redirect.uri}") 	private String KAKAO_REDIRECT_URI;
    @Value("${kakao.token.url}") 		private String KAKAO_TOKEN_URL;
    @Value("${kakao.user.info.url}") 	private String KAKAO_USER_INFO_URL;
    @Value("${kakao.logout.uri}") 		private String KAKAO_logout_URI;
    
    private String host = "http://localhost:3000";
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public String getOAuth2LoginUrl(String provider) {
    	String uri = null;
    	String client_id = null;
    	String redirect_uri = null;
    	if(provider.equals("naver")) {
    		uri = NAVER_AUTHORIZATION_URI;
    		client_id = NAVER_CLIENT_ID;
    		redirect_uri = NAVER_REDIRECT_URI;
    	}
    	if(provider.equals("kakao")) {
    		uri = KAKAO_AUTHORIZATION_URI;
    		client_id = KAKAO_CLIENT_ID;
    		redirect_uri = KAKAO_REDIRECT_URI;
    	}
    	return UriComponentsBuilder.fromHttpUrl(uri)
		    	.queryParam("response_type", "code")
		    	.queryParam("client_id", client_id)
		    	.queryParam("redirect_uri", redirect_uri)
		    	.queryParam("state", "1").toUriString();
    }
    
    public String handleOAuth2Callback(String provider, String code, String state) {
    	String accessToken = getAccessToken(provider, code, state);
    	if (accessToken == null) {
    		return host + "/login";
        }
    	String userInfoResponseBody = getUserInfo(provider, accessToken);
    	if(userInfoResponseBody == null) {
    		return host + "/login";
    	}
    	Map<String, String> tokenMap = processUserInfo(userInfoResponseBody, provider, accessToken);
    	String user_nick = null;
    	try {
			user_nick = URLEncoder.encode(tokenMap.get("user_nick"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	return host + "/auth/" + tokenMap.get("token") + "/" + provider + "/" + user_nick;
    }
        
    private String getAccessToken(String provider, String code, String state) {
    	String tokenUrl = null;
    	String clientId = null;
    	String clientSecret = null;
    	if(provider.equals("naver")) {
    		tokenUrl = NAVER_TOKEN_URL;
    		clientId = NAVER_CLIENT_ID;
    		clientSecret = NAVER_CLIENT_SECRET;
    	}
    	if(provider.equals("kakao")) {
    		tokenUrl = KAKAO_TOKEN_URL;
    		clientId = KAKAO_CLIENT_ID;
    	}
        String url = UriComponentsBuilder.fromHttpUrl(tokenUrl)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("code", code)
                .queryParam("state", state)
                .toUriString();
        ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            return null;
        }
        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("access_token").asText(null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
	
    private String getUserInfo(String provider, String accessToken) {
    	String userInfoUrl = null;
    	if(provider.equals("naver")) {
    		userInfoUrl = NAVER_USER_INFO_URL;
    	}
    	if(provider.equals("kakao")) {
    		userInfoUrl = KAKAO_USER_INFO_URL;
    	}
    	if(userInfoUrl == null) {
    		return null;
    	}
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, String.class);
        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }
    
    private Map<String, String>  processUserInfo(String userInfoResponseBody, String provider, String accessToken) {
    	Map<String, String> resultMap = new HashMap<>();
    	try {
            JsonNode root = objectMapper.readTree(userInfoResponseBody);
            String id = null;
            String email = null;
            String user_nick = null;
            String token = null;
            boolean check = true;
            switch (provider) {
                case "naver":
                    id = root.path("response").path("id").asText();
                    email = root.path("response").path("email").asText();
                    user_nick = root.path("response").path("nickname").asText();
                    log.info("naver id:{}", id); 
                    log.info("naver email:{}", email);
                    log.info("naver user_nick:{}", user_nick);
                    break;
                case "kakao":
                	log.info("kakao : {}", root);
                    id = root.path("id").asText();
                    email = root.path("kakao_account").path("email").asText();
                    user_nick = root.path("properties").path("nickname").asText();
                    log.info("kakao id:{}", id);
                    log.info("kakao email:{}", email);
                    log.info("kakao user_nick:{}", user_nick);
                    break;
            }
            
            if(loginMapper.findById(id) == null) {
	            UserDTO user = UserDTO.builder()
		                .user_id(id)
		                .user_email(email)
		                .user_nick(user_nick)
		                .user_provider(provider)
		                .build();
	            if (loginMapper.save(user) == 0) {
	            	check = false;
	            }
            }
            
            if(check) {
            	token = jwtToken.setToken(RequestTokenDTO.builder().type(provider).client_token(accessToken).id(id).build());
            	resultMap.put("token", token);
            	resultMap.put("user_nick", user_nick);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultMap;
    }
    
    public String kakaoLogout(String token) {
    	String url = host + "/oauth2";
    	if( kakaoRequsetLogout(token) ) {
    		url += "/success";
    	} else {
    		url += "/failure";
    	}
    	return url;
    }
    
    private boolean kakaoRequsetLogout(String token) {
    	
    	RequestTokenDTO requestTokenDTO = jwtToken.getUser(token);
    	if(requestTokenDTO == null) {
    		return false;
    	}
    	
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");
        headers.set("Authorization", "Bearer " + requestTokenDTO.getClient_token());
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<String> response = restTemplate.exchange(KAKAO_logout_URI, HttpMethod.POST, entity, String.class);

        return response.getStatusCode() == HttpStatus.OK ? true : false;
    }
    
}
