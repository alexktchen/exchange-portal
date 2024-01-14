package com.exchange.portal.exchangeportal.common.auth;

import com.exchange.portal.exchangeportal.util.JwtTokenUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;


public class RawAccessJwtToken implements JwtToken {

    private String token;
    private String secret;

    public RawAccessJwtToken(String token, String secret) {
        this.token = token;
        this.secret = secret;
    }

    public String getTokenValue() throws Exception {
        return parseClaims().getSubject();
    }

    private Claims parseClaims() {
        return Jwts.parser().setSigningKey(this.secret).parseClaimsJws(this.token).getBody();
    }

    public String refreshToken(Long expiration) throws Exception {
        Claims claims = parseClaims();
        return JwtTokenUtils.refreshToken(claims, secret, expiration);
    }

    @Override
    public String getToken() {
        return token;
    }
}
