package com.exchange.portal.exchangeportal.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class JwtTokenUtils {
	private static final String CLAIM_KEY_USERNAME = "sub";
	private static final String CLAIM_KEY_CREATED = "created";

	public static String generateToken(String key, String signingKey, Long expiration) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(CLAIM_KEY_USERNAME, key);
		claims.put(CLAIM_KEY_CREATED, new Date());
		return generateToken(claims, signingKey, expiration);
	}

	private static String generateToken(Map<String, Object> claims, String singingKey, Long expiration) {
		return Jwts.builder()
				.setClaims(claims)                                                                        // 自定义属性
				.setExpiration(new Date(Instant.now().toEpochMilli() + expiration * 1000))                // 过期时间
				.signWith(SignatureAlgorithm.HS512, singingKey)                                           // 签名算法以及密匙
				.compact();
	}


	public static String refreshToken(Claims claims, String singingKey, Long expiration) {
		claims.put(CLAIM_KEY_CREATED, new Date());
		return generateToken(claims, singingKey, expiration);
	}
}
