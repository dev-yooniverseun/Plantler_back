package com.plantler.auth;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.plantler.dto.RequestTokenDTO;
import com.plantler.dto.UserDTO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtToken {

	private MacAlgorithm ALGORITHM = Jwts.SIG.HS256;
	
	@Value("${access.auth.jwt}") 
	private String jwtSecretKey;
	
	@Value("${access.auth.interval}")
	private int interval;
	
	@Autowired
	private KeyUtils keyUtils;
	
	
	/********************************
	 * iss : 토큰 발급자	(issuer)
	 * sub : 토큰 제목		(subject)
	 * aud : 토큰 대상자	(audience)
	 * exp : 토큰 만료 시간	(expiration)
	 * iat : 토큰 발급 시간	(issuedAt)
	 * nbf : 토큰 활성 날짜	(not before)
	 *********************************/
	
	private Map<String, String> getHeader() {
		Map<String, String> header = new HashMap<>();
		header.put("alg", "HS256");
		header.put("typ", "JWT");
		return header;
	}
	
	private SecretKey getKey() {
	    return Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(jwtSecretKey));
	}
	
	private Map<String, ?> setClaims(RequestTokenDTO tokenDTO) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("issuer", "JsonAPI");
		claims.put("subject", "User");
		claims.put("audience", keyUtils.encodeContent(new Gson().toJson(tokenDTO)));
		return claims;
	}
	
	private Date getDate() {
		Calendar date = Calendar.getInstance();
		date.add(Calendar.MINUTE, interval);
		return date.getTime();
	}
	
	public String setToken(RequestTokenDTO tokenDTO) {
		return Jwts.builder()
				.header().add(getHeader()).and()
				.claims(setClaims(tokenDTO))
				.expiration(getDate())
				.issuedAt(Calendar.getInstance().getTime())
				.signWith(getKey(), ALGORITHM)
				.compact();
	}
	
	public Claims getToken(String token) {
		Claims claims = Jwts.parser()
				.verifyWith(getKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
		return claims;
	}
	
	public RequestTokenDTO getUser(String token) {
		String userJson = getToken(token).get("audience", String.class);
		return new Gson().fromJson(keyUtils.decodeContent(userJson), RequestTokenDTO.class);
	}
	
	public boolean isValidToken(String token) {
	    try {
	        Claims claims = getToken(token);
	        log.info("===============================================");
	        log.info("|ExpireTime\t: {}|", claims.getExpiration());
	        log.info("|IIssuedTime\t: {}|", claims.getIssuedAt());
	        log.info("|RealTime\t: {}|", Calendar.getInstance().getTime());
	        log.info("===============================================");
	        return true;
	    } catch (ExpiredJwtException exception) {
	    	log.info("==============");
	    	log.error("Token Expired");
	    } catch (JwtException exception) {
	    	log.info("==============");
	    	log.error("Token Tampered");
	    } catch (NullPointerException exception) {
	    	log.info("==============");
	    	log.error("Token is null");
	    }
	    log.info("==============");
	    return false;
	}
	
}
